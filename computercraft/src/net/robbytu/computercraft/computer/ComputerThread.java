package net.robbytu.computercraft.computer;

import java.util.HashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;

import net.robbytu.computercraft.CCMain;
import net.robbytu.computercraft.gui.ComputerBlockGUI;
import net.robbytu.computercraft.lib.LuaLib;
import net.robbytu.computercraft.lib.spout.DeprecatedLib;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.getspout.spoutapi.block.SpoutBlock;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaThread;
import org.luaj.vm2.compiler.LuaC;

public class ComputerThread {
	public Thread thread;
	public boolean busy;
	public ComputerBlockGUI gui;
	
	private int id;
	private LinkedBlockingQueue<ComputerTask> tasks;
	private HashMap<String, LuaLib> librarys = new HashMap<String, LuaLib>();
	
	private SpoutBlock block;
	
	private boolean isWireless;
	
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
	
	public ComputerBlockGUI getGui() {
		return gui;
	}
	
	@SuppressWarnings("unchecked")
	public <T extends LuaLib> T getLib(String name) {
		if (!librarys.containsKey(name)) return null;
		LuaLib lib = librarys.get(name);
		try {
			return (T) lib;
		}
		catch (ClassCastException ex) {
			return null;
		}
	}
	
	public LuaTable initLua(final int CID) {
		final LuaTable lua = new LuaTable();
		
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
	
	public Location getPosition() {
		return block.getLocation();
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
