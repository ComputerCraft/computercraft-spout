package net.robbytu.computercraft.computer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

import org.getspout.spoutapi.block.SpoutBlock;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;
import org.luaj.vm2.lib.jse.JsePlatform;

import net.robbytu.computercraft.CCMain;
import net.robbytu.computercraft.computer.network.RednetHandler;
import net.robbytu.computercraft.database.ComputerData;
import net.robbytu.computercraft.gui.ComputerBlockGUI;
import net.robbytu.computercraft.material.block.ComputerBlock;
import net.robbytu.computercraft.util.BlockManager;
import net.robbytu.computercraft.util.ScriptHelper;

public class ComputerThread {
	public Thread thread;
	public boolean busy;
	public ComputerBlockGUI gui;
	
	private int id;
	private LinkedBlockingQueue<ComputerTask> tasks;
	private HashMap<String, List<String>> eventListeners = new HashMap<String, List<String>>();
	
	private SpoutBlock block;
	
	public ComputerThread(final int id, ComputerBlockGUI gui, final SpoutBlock block) {
		this.busy = false;
		this.gui = gui;
		
		this.id = id;
		this.tasks = new LinkedBlockingQueue<ComputerTask>(100);
		
		this.block = block;
		
		this.thread = new Thread(new Runnable() {
			public void run()  {
				try {
					LuaTable lua = initLua(id);
					
					while(true) {
						ComputerTask task = tasks.take();
						
						busy = true;
						task.execute(lua, Integer.toString(id));
						busy = false;
					}
				}
				catch(Exception ex) {
					busy = false;
				}
				finally {
					busy = false;
					
					if(CCMain.instance.ComputerThreads.containsKey(Integer.toString(id))) {
						CCMain.instance.ComputerThreads.remove(Integer.toString(id));
					}
					
					thread.interrupt();
				}
			}
		});
		
		thread.start();
	}
	
	public LuaTable initLua(final int CID) {
		final LuaTable lua = JsePlatform.debugGlobals();
		
		lua.set("collectgarbage", LuaValue.NIL);
		lua.set("dofile", LuaValue.NIL);
		lua.set("load", LuaValue.NIL);
		lua.set("loadfile", LuaValue.NIL);
		lua.set("module", LuaValue.NIL);
		lua.set("require", LuaValue.NIL);
		lua.set("package", LuaValue.NIL);
		lua.set("io", LuaValue.NIL);
		lua.set("os", LuaValue.NIL);
		lua.set("debug", LuaValue.NIL);
		lua.set("print", LuaValue.NIL);
		lua.set("luajava", LuaValue.NIL);
		lua.set("write", LuaValue.NIL);
		
		// Default functions
		lua.set("print", new OneArgFunction() {
            public LuaValue call(LuaValue val) {
            	String[] splitString = val.toString().split("/n");
            	for (String str : splitString)
            		gui.addEntry(str);

                return LuaValue.NIL;
            }
		});
		
		lua.set("write", new OneArgFunction() {
            public LuaValue call(LuaValue val) {
            	gui.addEntry(val.toString());

                return LuaValue.NIL;
            }
		});
		
		lua.set("writeline", new OneArgFunction() {
            public LuaValue call(LuaValue val) {
            	gui.addEntry(val.toString());

                return LuaValue.NIL;
            }
		});
		
		lua.set("run", new TwoArgFunction() {
			public LuaValue call(LuaValue val, LuaValue val2) {
				File scriptFile = FileManager.getFile(val.toString(), val2.toString(), CID);
				
				if (scriptFile != null) {
					try {
						final String script = ScriptHelper.getScript(scriptFile);
						
						addTask(new ComputerTask() {
							@Override
							public void execute(LuaTable lua, String ComputerID) {
								try {
									lua.get("loadstring").call(LuaValue.valueOf(script)).call();
								}
								catch(LuaError ex) {
									lua.get("print").call(LuaValue.valueOf("¤c" + ex.getMessage()));
									lua.get("print").call(LuaValue.valueOf("¤7Script Failed."));
								}
							}
						});
						
						addTask(ComputerBlock.getOSTask(CID));
						
						return LuaValue.TRUE;						
					} catch (IOException e) {
						lua.get("print").call(LuaValue.valueOf("¤7File unable to start!"));
					}

					return LuaValue.FALSE;
				}
				lua.get("print").call(LuaValue.valueOf("¤7File not found!"));
				return LuaValue.FALSE;
			}
		});
		
		// color.* functions
		LuaTable color = new LuaTable();
		color.set("byString", new OneArgFunction() {
			public LuaValue call(LuaValue val) {
				String color = val.toString().toUpperCase();
				LuaValue toReturn = LuaValue.valueOf("¤f");

				if(color.equals("BLACK")) toReturn = LuaValue.valueOf("¤0");
				if(color.equals("DARK_BLUE") || color.equals("DARKBLUE")) toReturn = LuaValue.valueOf("¤1");
				if(color.equals("DARK_GREEN") || color.equals("DARKGREEN")) toReturn = LuaValue.valueOf("¤2");
				if(color.equals("DARK_AQUA") || color.equals("DARKAQUA")) toReturn = LuaValue.valueOf("¤3");
				if(color.equals("DARK_RED") || color.equals("DARKRED")) toReturn = LuaValue.valueOf("¤4");
				if(color.equals("DARK_PURPLE") || color.equals("DARKPURPLE")) toReturn = LuaValue.valueOf("¤5");
				if(color.equals("GOLD")) toReturn = LuaValue.valueOf("¤6");
				if(color.equals("GRAY")) toReturn = LuaValue.valueOf("¤7");
				if(color.equals("DARK_GRAY") || color.equals("DARKGRAY")) toReturn = LuaValue.valueOf("¤8");
				if(color.equals("BLUE")) toReturn = LuaValue.valueOf("¤9");
				if(color.equals("GREEN")) toReturn = LuaValue.valueOf("¤a");
				if(color.equals("AQUA")) toReturn = LuaValue.valueOf("¤b");
				if(color.equals("RED")) toReturn = LuaValue.valueOf("¤c");
				if(color.equals("PURPLE")) toReturn = LuaValue.valueOf("¤d");
				if(color.equals("YELLOW")) toReturn = LuaValue.valueOf("¤e");
				
				return toReturn;
			}
		});
		lua.set("color", color);
		
		// term.* functions
		LuaTable term = new LuaTable();
		term.set("clear", new ZeroArgFunction() {
			public LuaValue call() {
				gui.clearConsole();
				
				return LuaValue.NIL;
			}
		});
		
		term.set("setInputTip", new OneArgFunction() {
			public LuaValue call(LuaValue val) {
				gui.input.setPlaceholder("¤8" + val.toString());
				
				return LuaValue.NIL;
			}
		});

		term.set("setInputPasswordField", new OneArgFunction() {
			public LuaValue call(LuaValue val) {
				gui.input.setPasswordField(val.toboolean());
				
				return LuaValue.NIL;
			}
		});
		
		term.set("getInput", new ZeroArgFunction() {
			public LuaValue call() {
				gui.input.setEnabled(true);
				
				String inp = "";
				while(inp.equals("")) {
					inp = gui.inputBuffer;
					try {
						Thread.sleep(100); // Don't remove. If you do, your CPU is not going to be happy with you
					} catch (InterruptedException e) {
						inp = "ERR_THREAD_INTERUPTION";
					}
				}
				
				gui.inputBuffer = "";
				gui.input.setEnabled(false);
				
				return LuaValue.valueOf(inp);
			}
		});
		lua.set("term", term);
		
		// IO functions
		LuaTable io = new LuaTable();
		io.set("isDir", new OneArgFunction() {
			public LuaValue call(LuaValue val) {
				return LuaValue.valueOf(FileManager.isDir(val.toString(), CID));
			}
		});
		
		io.set("mkdir", new TwoArgFunction() {
			public LuaValue call(LuaValue val, LuaValue val2) {
				return LuaValue.valueOf(FileManager.mkDir(val.toString(), val2.toString(), CID));
			}
		});
		
		io.set("printList", new OneArgFunction() {
			public LuaValue call(LuaValue val) {
				FileManager.printList(val.toString(), CID);
				return LuaValue.NIL;
			}
		});

		io.set("remove", new OneArgFunction() {
			public LuaValue call(LuaValue val) {
				return LuaValue.valueOf(FileManager.rm(val.toString(), CID));
			}
		});
		
		io.set("getDir", new OneArgFunction() {
			public LuaValue call(LuaValue val) {
				return LuaValue.valueOf(FileManager.getDir(val.toString(), CID));
			}
		});
		
		io.set("fileExists", new TwoArgFunction () {
			public LuaValue call(LuaValue val, LuaValue val2) {
				return LuaValue.valueOf(FileManager.fileExists(val.toString(), val2.toString(), CID));
			}
		});
		
		io.set("mkFile", new TwoArgFunction() {
			public LuaValue call(LuaValue val, LuaValue val2) {
				return LuaValue.valueOf(FileManager.mkFile(val.toString(), val2.toString(), CID));
			}
		});
		
		io.set("getFile", new TwoArgFunction() {
			public LuaValue call(LuaValue val, LuaValue val2) {
				String file = FileManager.getFileAsString(val.toString(), val2.toString(), CID);
				return LuaValue.valueOf(file);
			}
		});
		lua.set("io", io);
		
		// Events API
		LuaTable events = new LuaTable();
		events.set("registerListener", new TwoArgFunction() {
			public LuaValue call(LuaValue eventId, LuaValue functionName) {
				if(!eventListeners.containsKey(eventId.toString())) {
					eventListeners.put(eventId.toString(), new ArrayList<String>());
				}
				
				if(!eventListeners.get(eventId.toString()).contains(functionName.toString())) { // Prevent from adding a callback multiple times
					eventListeners.get(eventId.toString()).add(functionName.toString());
				}
				
				return LuaValue.NIL;
			}
		});

		events.set("unregisterListener", new TwoArgFunction() {
			public LuaValue call(LuaValue eventId, LuaValue functionName) {
				if(!eventListeners.containsKey(eventId.toString())) {
					return LuaValue.NIL; // EventId isn't even registered, so how are we supposed to delete anything?
				}
				
				if(eventListeners.get(eventId.toString()).contains(functionName.toString())) {
					eventListeners.get(eventId.toString()).remove(functionName.toString());
				}
				
				return LuaValue.NIL;
			}
		});

		events.set("isRegistered", new TwoArgFunction() {
			public LuaValue call(LuaValue eventId, LuaValue functionName) {
				if(!eventListeners.containsKey(eventId.toString())) {
					return LuaValue.FALSE; // EventId isn't even registered, so how can a callback be registered?
				}
				
				return LuaValue.valueOf(eventListeners.get(eventId.toString()).contains(functionName.toString()));
			}
		});
		
		events.set("triggerEvent", new TwoArgFunction() {
			public LuaValue call(LuaValue eventId, LuaValue message) {
				if(!eventListeners.containsKey(eventId.toString())) { // EventId isn't even registered, so there's nothing to be called
					return LuaValue.NIL;
				}
				
				for(int i = 0; i < eventListeners.get(eventId.toString()).size(); i++) {
					lua.get(eventListeners.get(eventId.toString()).get(i)).call(eventId, message);
				}
				
				return LuaValue.NIL;
			}
		});
		lua.set("event", events);
		
		// System API
		LuaTable sys = new LuaTable();
		sys.set("getComputerID", new ZeroArgFunction() {
			public LuaValue call() {
				return LuaValue.valueOf(id);
			}
		});
		
		sys.set("isWireless", new ZeroArgFunction() {
			public LuaValue call() {
				ComputerData data = CCMain.instance.getDatabase().find(ComputerData.class)
						.where()
							.eq("id", id)
						.findUnique();
				
				return LuaValue.valueOf(data.isWireless());
			}
		});
		
		sys.set("getComputerCoords", new ZeroArgFunction() {
			public LuaValue call() {
				ComputerData data = CCMain.instance.getDatabase().find(ComputerData.class)
				.where()
					.eq("id", id)
				.findUnique();
				
				String coords = data.getX() + "," + data.getY() + "," + data.getZ() + "," + data.getWorld();
				
				return LuaValue.valueOf(coords);
			}
		});
		
		lua.set("shutdown", new ZeroArgFunction() {
			public LuaValue call() {
				// Do shutdown events, so that scripts might save configs for example
				lua.get("triggerEvent").call(LuaValue.valueOf("shutdown"), LuaValue.NIL);
				
				thread.interrupt();
				
				return LuaValue.NIL;
			}
		});
		lua.set("sys", sys);
		
		// Redstone API
		LuaTable redstone = new LuaTable();
		redstone.set("setOutput", new TwoArgFunction() {
			public LuaValue call(LuaValue val1, LuaValue val2) {
				int side = val1.toint();
				boolean power = val2.toboolean();
				
				if (side > 3) return LuaValue.NIL;
				
				SpoutBlock target = BlockManager.blockAtSide(block, side);
				target.setBlockPowered(power);

				return LuaValue.NIL;
			}
		});
		
		redstone.set("isPowered", new OneArgFunction() {
			public LuaValue call(LuaValue val) {
				int side = val.toint();
				if (side > 3) return LuaValue.NIL;
				
				SpoutBlock target = BlockManager.blockAtSide(block, side);
				return LuaValue.valueOf(target.isBlockPowered());
			}
		});
		lua.set("redstone", redstone);
		
		// Network API - This is for both internal and world-wide networking
		LuaTable rednet = new LuaTable();
		rednet.set("scan", new OneArgFunction() {
			public LuaValue call(LuaValue val1) {
				
				return LuaValue.NIL;
			}
		});
		
		rednet.set("connect", new TwoArgFunction() {
			public LuaValue call(LuaValue val1, LuaValue val2) {
				String ret = RednetHandler.connect(val1.toString(), val2.toString(), CID);
				
				if (ret.equals("RN_CONNECTED")) {
					// Store the connection here!
				}
				
				return LuaValue.valueOf(ret);
			}
		});
		
		return lua;
	}
	
	public void addTask(ComputerTask task) {
		this.tasks.offer(task);
	}
	
	public void stop() {
		if(thread.isAlive()) thread.interrupt();
		
		if(CCMain.instance.ComputerThreads.containsKey(Integer.toString(this.id))) {
			CCMain.instance.ComputerThreads.remove(Integer.toString(this.id));
		}
	}
}
