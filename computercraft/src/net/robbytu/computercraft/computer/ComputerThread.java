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
	
	private boolean isWireless;
	private String SSID;
	
	private LuaTable lua;
	
	public ComputerThread(final int id, ComputerBlockGUI gui, final SpoutBlock block, boolean isWireless) {
		this.busy = false;
		this.gui = gui;
		
		this.id = id;
		this.tasks = new LinkedBlockingQueue<ComputerTask>(100);
		
		this.block = block;
		
		this.isWireless = isWireless;
		
		this.thread = new Thread(new Runnable() {
			public void run()  {
				try {
					lua = initLua(id);
					
					while(true) {
						ComputerTask task = tasks.take();
						
						busy = true;
						task.execute(lua, id);
						busy = false;
					}
				}
				catch(Exception ex) {
					busy = false;
				}
				finally {
					busy = false;
					
					if(CCMain.instance.ComputerThreads.containsKey(id)) {
						CCMain.instance.ComputerThreads.remove(id);
					}
					
					thread.interrupt();
				}
			}
		});
		
		thread.start();
	}
	
	public String connectedSSID() {
		return SSID;
	}
	
	public void triggerEvent(String eventId, String message) {
		if(!eventListeners.containsKey(eventId)) { // EventId isn't even registered, so there's nothing to be called
			return;
		}
		
		for(int i = 0; i < eventListeners.get(eventId).size(); i++) {
			lua.get(eventListeners.get(eventId).get(i)).call(LuaValue.valueOf(eventId), LuaValue.valueOf(message));
		}
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
							public void execute(LuaTable lua, int ComputerID) {
								try {
									lua.get("loadstring").call(LuaValue.valueOf(script)).call();
								}
								catch(LuaError ex) {
									lua.get("print").call(LuaValue.valueOf("\u00A7c" + ex.getMessage()));
									lua.get("print").call(LuaValue.valueOf("\u00A77Script Failed."));
								}
							}
						});
						
						addTask(ComputerBlock.getOSTask(CID)); //TODO bad place to do this, if the started script uses run(), the task queue is screwed up
						
						return LuaValue.TRUE;						
					} catch (IOException e) {
						lua.get("print").call(LuaValue.valueOf("\u00A77File unable to start!"));
					}

					return LuaValue.FALSE;
				}
				lua.get("print").call(LuaValue.valueOf("\u00A77File not found!"));
				return LuaValue.FALSE;
			}
		});
		
		// color.* functions
		LuaTable color = new LuaTable();
		color.set("byString", new OneArgFunction() {
			public LuaValue call(LuaValue val) {
				String color = val.toString().toUpperCase();
				LuaValue toReturn = LuaValue.valueOf("\u00A7f");

				if(color.equals("BLACK")) toReturn = LuaValue.valueOf("\u00A70");
				if(color.equals("DARK_BLUE") || color.equals("DARKBLUE")) toReturn = LuaValue.valueOf("\u00A71");
				if(color.equals("DARK_GREEN") || color.equals("DARKGREEN")) toReturn = LuaValue.valueOf("\u00A72");
				if(color.equals("DARK_AQUA") || color.equals("DARKAQUA")) toReturn = LuaValue.valueOf("\u00A73");
				if(color.equals("DARK_RED") || color.equals("DARKRED")) toReturn = LuaValue.valueOf("\u00A74");
				if(color.equals("DARK_PURPLE") || color.equals("DARKPURPLE")) toReturn = LuaValue.valueOf("\u00A75");
				if(color.equals("GOLD")) toReturn = LuaValue.valueOf("\u00A76");
				if(color.equals("GRAY")) toReturn = LuaValue.valueOf("\u00A77");
				if(color.equals("DARK_GRAY") || color.equals("DARKGRAY")) toReturn = LuaValue.valueOf("\u00A78");
				if(color.equals("BLUE")) toReturn = LuaValue.valueOf("\u00A79");
				if(color.equals("GREEN")) toReturn = LuaValue.valueOf("\u00A7a");
				if(color.equals("AQUA")) toReturn = LuaValue.valueOf("\u00A7b");
				if(color.equals("RED")) toReturn = LuaValue.valueOf("\u00A7c");
				if(color.equals("PURPLE")) toReturn = LuaValue.valueOf("\u00A7d");
				if(color.equals("YELLOW")) toReturn = LuaValue.valueOf("\u00A7e");
				
				return toReturn;
			}
		});
		
		color.set("BLACK", "\u00A70");
		color.set("DARKBLUE", "\u00A71");
		color.set("DARKGREEN", "\u00A72");
		color.set("DARKAQUA", "\u00A73");
		color.set("DARKRED", "\u00A74");
		color.set("DARKPURPLE", "\u00A75");
		color.set("GOLD", "\u00A76");
		color.set("GREY", "\u00A77");
		color.set("DARKGREY", "\u00A78");
		color.set("BLUE", "\u00A79");
		color.set("GREEN", "\u00A7a");
		color.set("AQUA", "\u00A7b");
		color.set("RED", "\u00A7c");
		color.set("PURPLE", "\u00A7d");
		color.set("YELLOW", "\u00A7e");
		
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
				gui.input.setPlaceholder("\u00A78" + val.toString());
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
				gui.buttonClicked = false;
				gui.input.setEnabled(true);
				
				String inp = "";
				//while(inp.equals("")) {
				while(!gui.buttonClicked) {
					//inp = gui.inputBuffer;
					try {
						Thread.sleep(100); // Don't remove. If you do, your CPU is not going to be happy with you
					} catch (InterruptedException e) {
						gui.inputBuffer = "";
						gui.input.setEnabled(false);
						return LuaValue.valueOf("ERR_THREAD_INTERUPTION");
					}
				}
		
				inp = gui.inputBuffer;
								
				gui.inputBuffer = "";
				gui.input.setEnabled(false);
				
				return LuaValue.valueOf(inp);
			}
		});
		lua.set("term", term);
		
		//Filesytem functions
		LuaTable fs = new LuaTable();
		fs.set("isDir", new OneArgFunction() {
			public LuaValue call(LuaValue val) {
				return LuaValue.valueOf(FileManager.isDir(val.toString(), CID));
			}
		});
		
		fs.set("separator", FileManager.separator);
		
		fs.set("makeDir", new OneArgFunction() {
			public LuaValue call(LuaValue val) {
				return LuaValue.valueOf(FileManager.mkDir(val.toString(), CID));
			}
		});
		
		fs.set("combine", new TwoArgFunction() {
			public LuaValue call(LuaValue basePath, LuaValue childPath) {
				return LuaValue.valueOf(FileManager.combine(basePath.toString(), childPath.toString()));
			}
		});
		
		fs.set("getDir", new OneArgFunction() {
			public LuaValue call(LuaValue val) {
				return LuaValue.valueOf(FileManager.getDir(val.toString(), CID));
			}
		});

		fs.set("delete", new OneArgFunction() {
			public LuaValue call(LuaValue val) {
				return LuaValue.valueOf(FileManager.rm(val.toString(), CID));
			}
		});
		
		fs.set("exists", new OneArgFunction () {
			public LuaValue call(LuaValue val) {
				return LuaValue.valueOf(FileManager.exists(val.toString(), CID));
			}
		});
		
		fs.set("printList", new OneArgFunction() { //TODO implement this in lua
			public LuaValue call(LuaValue val) {
				FileManager.printList(val.toString(), CID);
				return LuaValue.NIL;
			}
		});
		
		lua.set("fs", fs);
		
		// IO functions
		LuaTable io = new LuaTable();
		io.set("isDir", fs.get("isDir")); //deprecated, use fs.isDir
		io.set("mkdir",  new TwoArgFunction() { //deprecated, use fs.makeDir
			public LuaValue call(LuaValue val, LuaValue val2) {
				return LuaValue.valueOf(FileManager.mkDir(val.toString(), val2.toString(), CID));
			}
		});
		io.set("remove", fs.get("delete")); //deprecated, use fs.remove
		io.set("getDir", fs.get("getDir")); //deprecated, use fs.getDir
		io.set("printList", fs.get("printList")); //deprecated, use fs.printList

		io.set("fileExists", new TwoArgFunction() {//deprecated, use fs.exists
			@Override
			public LuaValue call(LuaValue val, LuaValue val2) {
				return LuaValue.valueOf(FileManager.exists(val.toString() + File.separator + val2, CID));
			}
		});

		io.set("separator", new ZeroArgFunction() { //deprecated, use fs.seperator
			@Override
			public LuaValue call() {
				return LuaValue.valueOf(FileManager.separator);
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
		LuaTable os = new LuaTable();
		os.set("computerID", new ZeroArgFunction() {
			public LuaValue call() {
				return LuaValue.valueOf(id);
			}
		});
		
		os.set("getComputerID", os.get("computerID")); // deprecated, use os.computerID instead
		
		os.set("isWireless", new ZeroArgFunction() {
			public LuaValue call() {
				ComputerData data = CCMain.instance.getDatabase().find(ComputerData.class)
						.where()
							.eq("id", id)
						.findUnique();
				
				return LuaValue.valueOf(data.isWireless());
			}
		});
		
		os.set("getComputerCoords", new ZeroArgFunction() {
			public LuaValue call() {
				ComputerData data = CCMain.instance.getDatabase().find(ComputerData.class)
				.where()
					.eq("id", id)
				.findUnique();
				
				String coords = data.getX() + "," + data.getY() + "," + data.getZ() + "," + data.getWorld();
				
				return LuaValue.valueOf(coords);
			}
		});
		
		os.set("shutdown", new ZeroArgFunction() {
			public LuaValue call() {
				// Do shutdown events, so that scripts might save configs for example
				lua.get("event").get("triggerEvent").call(LuaValue.valueOf("shutdown"), LuaValue.NIL);
				
				thread.interrupt();
				
				return LuaValue.NIL;
			}
		});
		
		lua.set("shutdown", os.get("shutdown")); //deprecated, use os.shutdown
		lua.set("os", os);
		lua.set("sys",os); // Deprecated
		
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

		redstone.set("isPowered", redstone.get("getInput")); //deprecated, use rs.getInput
		lua.set("rs", redstone);
		lua.set("redstone", redstone); //deprecated, use rs
		
		// Network API - This is for both internal and world-wide networking
		LuaTable rednet = new LuaTable();
		rednet.set("send", new TwoArgFunction() {
			public LuaValue call(LuaValue val1, LuaValue val2) {
				if (isWireless) {
					if (!SSID.isEmpty()) {
						return LuaValue.valueOf(RednetHandler.send(val1.toint(), val2.toString(), SSID, CID));
					}
					return LuaValue.valueOf("RN_NO_CONNECTION");
				}
				return LuaValue.valueOf("RN_NO_WIRELESS");
			}
		});
		
		rednet.set("connect", new TwoArgFunction() {
			public LuaValue call(LuaValue val1, LuaValue val2) {
				if (isWireless) {
					String ret = RednetHandler.connect(val1.toString(), val2.toString().trim(), CID);
					
					if (ret.equals("RN_CONNECTED")) {
						SSID = val1.toString();
					}
					
					return LuaValue.valueOf(ret);
				}
				return LuaValue.valueOf("RN_NO_WIRELESS");
			}
		});
		
		rednet.set("open", rednet.get("connect")); //deprecated, use rednet.connect
		lua.set("rednet", rednet);
		
		return lua;
	}
	
	public void addTask(ComputerTask task) {
		this.tasks.offer(task);
	}
	
	public void stop() {
		if(thread.isAlive()) thread.interrupt();
		
		if(CCMain.instance.ComputerThreads.containsKey(this.id)) {
			CCMain.instance.ComputerThreads.remove(this.id);
		}
	}
}
