package net.robbytu.computercraft.computer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.getspout.spoutapi.block.SpoutBlock;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaThread;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.compiler.LuaC;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;

import net.robbytu.computercraft.CCMain;
import net.robbytu.computercraft.computer.network.RednetHandler;
import net.robbytu.computercraft.database.ComputerData;
import net.robbytu.computercraft.gui.ComputerBlockGUI;
import net.robbytu.computercraft.lib.LuaLib;
import net.robbytu.computercraft.lib.spout.DeprecatedLib;
import net.robbytu.computercraft.luaj.LuaInstance;
import net.robbytu.computercraft.luaj.SpoutPlatform;
import net.robbytu.computercraft.material.block.ComputerBlock;
import net.robbytu.computercraft.util.BlockManager;
import net.robbytu.computercraft.util.ScriptHelper;

public class ComputerThread {
	public Thread thread;
	public boolean busy;
	public ComputerBlockGUI gui;
	
	private int id;
	private LinkedBlockingQueue<ComputerTask> tasks;
	private HashMap<String, LuaLib> librarys = new HashMap<String, LuaLib>();
	
	private SpoutBlock block;
	
	private boolean isWireless;
	private String SSID;
	
	private LuaTable lua;
	
	private LuaInstance instance;
	
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
	
	public ComputerBlockGUI getGui() {
		return gui;
	}
	
	public LuaLib getLib(String name) {
		if (!librarys.containsKey(name)) return null;
		return librarys.get(name);
	}
	
	public LuaTable initLua(final int CID) {
		final LuaTable lua = new LuaTable();
		instance = LuaInstance.getActiveInstance();
		
		for (Class<? extends LuaLib> libClass : CCMain.instance.getLibrarys()) {
			try {
				LuaLib lib = libClass.newInstance();
				librarys.put(lib.getName(), lib);
			} catch (InstantiationException e) {
				Bukkit.getLogger().log(Level.WARNING, "ComputerCraft couldn't instanciate a library", e);
			} catch (IllegalAccessException e) {
				Bukkit.getLogger().log(Level.WARNING, "ComputerCraft couldn't instanciate a library", e);
			}
		}
		
		for (LuaLib library : librarys.values()) {
			library.init(this, lua);
		}
		
		// Initializing deprecated library here, needs to be done at least because we don't have a
		// dependency system at the moment.
		LuaLib depr = new DeprecatedLib();
		librarys.put(depr.getName(), depr);
		depr.init(this, lua);
		
		LuaThread.setGlobals(lua);
		LuaC.install();
		
		// Default functions
		
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
	
	public int getID() {
		return id;
	}
	
	public boolean isWireless() {
		return isWireless;
	}
	
	public int[] getPosition() {
		return new int[] { block.getX(), block.getY(), block.getZ() };
	}
	
	public SpoutBlock getBlock() {
		return block;
	}
	
	public String getWorld() {
		return block.getWorld().getName();
	}
	
	/**
	 * Prints the given text to the gui associated to this ComputerThread.
	 * 
	 * @param text The text to print ot the console.
	 */
	public void print(String text) {
    	String[] splitString = text.split("/n");
    	for (String str : splitString)
    		gui.addEntry(str);		
	}
	
	public void stop() {
		if(thread.isAlive()) thread.interrupt();
		
		if(CCMain.instance.ComputerThreads.containsKey(this.id)) {
			CCMain.instance.ComputerThreads.remove(this.id);
		}
	}
}
