package net.robbytu.computercraft.materials;

import java.io.File;
import net.robbytu.computercraft.CCMain;
import net.robbytu.computercraft.ComputerTask;
import net.robbytu.computercraft.ComputerThread;
import net.robbytu.computercraft.FileManager;
import net.robbytu.computercraft.database.ComputerData;
import net.robbytu.computercraft.gui.ComputerBlockGUI;
import net.robbytu.computercraft.util.ScriptHelper;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;
import org.getspout.spoutapi.block.design.GenericCubeBlockDesign;
import org.getspout.spoutapi.block.design.Texture;
import org.getspout.spoutapi.inventory.SpoutItemStack;
import org.getspout.spoutapi.material.block.GenericCustomBlock;
import org.getspout.spoutapi.player.SpoutPlayer;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;

public class ComputerBlock extends GenericCustomBlock{

	public ComputerBlock(Plugin plugin, String name, boolean isOpaque, int face) {
		super(plugin, name, isOpaque);

		GenericCubeBlockDesign BlockDesign;
		
		if(face == 0) {
			BlockDesign = new GenericCubeBlockDesign(
					plugin,
					new Texture(plugin, "http://robbytu.net/spout/computercraft/resources/computerblock.png", 256, 256, 16),
					new int[] { 2, 2, 2, 0, 2, 2 });
		}
		else if(face == 1) {
			BlockDesign = new GenericCubeBlockDesign(
					plugin,
					new Texture(plugin, "http://robbytu.net/spout/computercraft/resources/computerblock.png", 256, 256, 16),
					new int[] { 2, 0, 2, 2, 2, 2 });
		}
		else if(face == 3) {
			BlockDesign = new GenericCubeBlockDesign(
					plugin,
					new Texture(plugin, "http://robbytu.net/spout/computercraft/resources/computerblock.png", 256, 256, 16),
					new int[] { 2, 2, 2, 2, 0, 2 });
		}
		else {
			BlockDesign = new GenericCubeBlockDesign(
					plugin,
					new Texture(plugin, "http://robbytu.net/spout/computercraft/resources/computerblock.png", 256, 256, 16),
					new int[] { 2, 2, 0, 2, 2, 2 });
		}
		
		this.setBlockDesign(BlockDesign);
		this.setName("Computer");
	}

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
			if(CCMain.instance.ComputerThreads.containsKey(Integer.toString(data.getId()))) {
				CCMain.instance.ComputerThreads.get(Integer.toString(data.getId())).thread.interrupt();
				CCMain.instance.ComputerThreads.remove(Integer.toString(data.getId()));
			}
			
			FileManager.deleteComputerEvent(data);
			
			CCMain.instance.getDatabase().delete(data);
		}
	}
	
	@Override
	public void onBlockPlace(World world, int x, int y, int z) {
		this.setItemDrop(new SpoutItemStack(Materials.ComputerBlockEast, 1));
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
			
			CCMain.instance.getDatabase().save(data);
			
			FileManager.newComputerEvent(data);
		}
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
		
		if(CCMain.instance.ComputerThreads.containsKey(Integer.toString(data.getId()))) {
			CCMain.instance.ComputerThreads.get(Integer.toString(data.getId())).gui.attachToScreen(player);
			return true;
		}
		
		ComputerThread thread = new ComputerThread(data.getId(), new ComputerBlockGUI(data.getId()));
		
		CCMain.instance.ComputerThreads.put(Integer.toString(data.getId()), thread);
		
		thread.addTask(getOSTask(data.getId()));
		thread.gui.attachToScreen(player);
		
		return true;
	}
	
	public static ComputerTask getOSTask(final int CID) {
		return new ComputerTask() {
			@Override
			public void execute(LuaTable lua, String ComputerID) {
				File os = new File(CCMain.instance.getDataFolder() + "/rom/boot.lua");
				try {
					String script = ScriptHelper.getScript(os);
					
					try {
						lua.get("loadstring").call(LuaValue.valueOf(script)).call();
					}
					catch(LuaError ex) {
						lua.get("print").call(LuaValue.valueOf("¤c" + ex.getMessage()));
						lua.get("print").call(LuaValue.valueOf("¤7System halted."));
						
						ex.printStackTrace();
						
						if(CCMain.instance.ComputerThreads.containsKey(ComputerID)) {
							CCMain.instance.ComputerThreads.get(ComputerID).thread.interrupt();
							CCMain.instance.ComputerThreads.remove(ComputerID);
						}
					}
				}
				catch(Exception ex) {
					lua.get("print").call(LuaValue.valueOf("Searching for bootable media... ¤c[FAILED]"));
					lua.get("print").call(LuaValue.valueOf("¤7System halted."));
					
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
}