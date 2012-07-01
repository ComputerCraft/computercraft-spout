package net.robbytu.computercraft.material.block;

import java.io.File;

import net.robbytu.computercraft.CCMain;
import net.robbytu.computercraft.computer.ComputerTask;
import net.robbytu.computercraft.computer.ComputerThread;
import net.robbytu.computercraft.computer.FileManager;
import net.robbytu.computercraft.database.ComputerData;
import net.robbytu.computercraft.gui.ComputerBlockGUI;
import net.robbytu.computercraft.lib.spout.ColorLib;
import net.robbytu.computercraft.util.BlockManager;
import net.robbytu.computercraft.util.ScriptHelper;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.plugin.Plugin;
import org.getspout.spoutapi.block.SpoutBlock;
import org.getspout.spoutapi.material.block.GenericCustomBlock;
import org.getspout.spoutapi.player.SpoutPlayer;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

public abstract class BaseComputerBlock  extends GenericCustomBlock {	
	public BaseComputerBlock(Plugin plugin, String name, String textureURL, boolean isOpaque, int face) {
		super(plugin, name, isOpaque);
		
		setupDesign(plugin, face, textureURL);
	}
	
	protected abstract void setupDesign(Plugin plugin, int face, String texture);
	
	@Override
	public void onBlockDestroyed(World world, int x, int y, int z) {
		ComputerData data = CCMain.instance.getDatabase().find(ComputerData.class)
						.where()
							.eq("x", x)
							.eq("y", y)
							.eq("z", z)
							.eq("world", (String)world.getName())
						.findUnique();
		
		if(data != null) {
			for(int i = 0; i < 3; i++) {
				SpoutBlock target = BlockManager.blockAtSide((SpoutBlock)world.getBlockAt(x, y, z), i);
				target.setBlockPowered(false);
			}
			
			if(CCMain.instance.ComputerThreads.containsKey(data.getId())) {
				CCMain.instance.ComputerThreads.get(data.getId()).thread.interrupt();
				CCMain.instance.ComputerThreads.remove(data.getId());
			}
			
			FileManager.deleteComputerEvent(data);
			
			CCMain.instance.getDatabase().delete(data);
		}
	}
	
	@Override
	public void onBlockPlace(World world, int x, int y, int z) {
		onBlockPlace(world, x, y, z, false);
	}
	
	@Override
	public void onNeighborBlockChange(org.bukkit.World world, int x, int y, int z, int changedId) {
		// TODO: Redstone check integration
	}

	@Override
	public boolean onBlockInteract(World world, int x, int y, int z, SpoutPlayer player) {
		return this.onBlockInteract(world, x, y, z, player, false);
	}
	
	public boolean onBlockInteract(World world, int x, int y, int z, final SpoutPlayer player, boolean didJustCreateDatabaseEntry) {
		ComputerData data = CCMain.instance.getDatabase().find(ComputerData.class)
						.where()
							.eq("x", x)
							.eq("y", y)
							.eq("z", z)
							.eq("world", (String)world.getName())
						.findUnique();
		
		if(data == null) {
			if(didJustCreateDatabaseEntry) {
				Bukkit.getLogger().warning("[ComputerCraft] Could not create database entry at interaction!");
				return false;
			}
			
			Bukkit.getLogger().info("[ComputerCraft] Interaction at (" + world.getName() + ", " + x + ", " + y + ", " + z + "), but no data was found. Creating it now.");
			
			this.onBlockPlace(world, x, y, z);
			return this.onBlockInteract(world, x, y, z, player, true);
		}
		
		if(CCMain.instance.ComputerThreads.containsKey(data.getId())) {
			CCMain.instance.ComputerThreads.get(data.getId()).gui.attachToScreen(player);
			return true;
		}
		
		ComputerThread thread = new ComputerThread(data.getId(), new ComputerBlockGUI(data.getId()), (SpoutBlock)world.getBlockAt(x, y, z), data.isWireless());
		
		CCMain.instance.ComputerThreads.put(data.getId(), thread);
		
		thread.addTask(getOSTask(data.getId()));
		thread.gui.attachToScreen(player);
		
		return true;
	}
	
	public static ComputerTask getOSTask(final int CID) {
		return new ComputerTask() {
			@Override
			public void execute(LuaTable lua, int ComputerID) {
				File os = new File(CCMain.instance.getDataFolder(), "rom" + File.separator + "boot.lua");
				try {
					String script = ScriptHelper.getScript(os);
					
					try {
						Varargs code = lua.get("loadstring").invoke(LuaValue.varargsOf(LuaValue.valueOf(script), LuaValue.valueOf("boot.lua")));
						if (code.isnil(1)) {
							ComputerThread thread = CCMain.instance.ComputerThreads.get(CID);
							if (thread != null) {
								thread.print(ColorLib.RED + "Error when compiling boot media: " + code.optjstring(2, "No error specified."));
								thread.print(ColorLib.GRAY + "System halted");
							}
						}
						else {
							lua.get("loadstring").call(LuaValue.valueOf(script)).call();							
						}
					}
					catch(LuaError ex) {
						lua.get("print").call(LuaValue.valueOf("\u00A7c" + ex.getMessage()));
						lua.get("print").call(LuaValue.valueOf("\u00A77System halted."));
						
						ex.printStackTrace();
						
						if(CCMain.instance.ComputerThreads.containsKey(ComputerID)) {
							CCMain.instance.ComputerThreads.get(ComputerID).thread.interrupt();
							CCMain.instance.ComputerThreads.remove(ComputerID);
						}
					}
				}
				catch(Exception ex) {
					lua.get("print").call(LuaValue.valueOf("Searching for bootable media... \u00A7c[FAILED]"));
					lua.get("print").call(LuaValue.valueOf("\u00A77System halted."));
					
					Bukkit.getLogger().warning("[ComputerCraft] Error while booting a computer!");
					Bukkit.getLogger().warning(ex.getMessage());
					
					if(CCMain.instance.ComputerThreads.containsKey(ComputerID)) {
						CCMain.instance.ComputerThreads.get(ComputerID).thread.interrupt();
						CCMain.instance.ComputerThreads.remove(ComputerID);
					}
				}
			}
		};
	}
	
	@Override
	public boolean isPowerSource() {
		return true;
	}
	
	@Override
	public boolean isProvidingPowerTo(World world, int x, int y, int z,
			BlockFace face) {
		if (face == BlockFace.DOWN || face == BlockFace.UP)
			return false;
		
		ComputerThread thread = CCMain.instance.getComputerAt(world.getName(), x, y, z);
		if (thread == null)
			return false;
		return false;
	}
	
	protected void onBlockPlace(World world, int x, int y, int z, boolean isWireless) {
		//this.setItemDrop(new SpoutItemStack(Materials.ComputerBlockEast, 1));
		ComputerData data = CCMain.instance.getDatabase().find(ComputerData.class)
						.where()
							.eq("x", x)
							.eq("y", y)
							.eq("z", z)
							.eq("world", (String)world.getName())
						.findUnique();
		
		if(data == null) {
			data = new ComputerData();
			data.setX(x);
			data.setY(y);
			data.setZ(z);
			data.setWorld(world.getName());
			data.setWireless(isWireless);
			
			CCMain.instance.getDatabase().save(data);
			
			FileManager.newComputerEvent(data);
		}
	}
}
