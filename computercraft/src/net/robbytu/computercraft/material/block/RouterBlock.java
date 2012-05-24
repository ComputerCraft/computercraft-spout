package net.robbytu.computercraft.material.block;

import net.robbytu.computercraft.CCMain;
import net.robbytu.computercraft.database.RouterData;
import net.robbytu.computercraft.gui.RouterBlockGUI;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.getspout.spoutapi.block.design.GenericCubeBlockDesign;
import org.getspout.spoutapi.block.design.Texture;
import org.getspout.spoutapi.material.block.GenericCustomBlock;
import org.getspout.spoutapi.player.SpoutPlayer;

public class RouterBlock extends GenericCustomBlock {

	public RouterBlock() {
		super(CCMain.instance, "RouterBlock");
		this.setName("Wireless Router");
		
		GenericCubeBlockDesign BlockDesign = new GenericCubeBlockDesign(
				CCMain.instance,
				new Texture(CCMain.instance, "http://robbytu.net/spout/computercraft/resources/computerblock.png", 256, 256, 16),
				new int[] { 2, 3, 3, 3, 3, 2});
		
		this.setBlockDesign(BlockDesign);
	}

	@Override
	public void onBlockPlace(World world, int x, int y, int z) {
		RouterData data = CCMain.instance.getDatabase().find(RouterData.class)
					.where()
						.eq("x", x)
						.eq("y", y)
						.eq("z", z)
						.eq("world", (String)world.getName())
					.findUnique();
		
		if(data == null) {
			data = new RouterData();
			
			data.setX(x);
			data.setY(y);
			data.setZ(z);
			data.setWorld(world.getName());
			data.setSSID("CCAP-" + data.getId());
			data.setPassword("");
			
			CCMain.instance.getDatabase().save(data);
		}
	}
	
	public boolean onBlockInteract(World world, int x, int y, int z, SpoutPlayer player) {
		return this.onBlockInteract(world, x, y, z, player, false);
	}
	
	public boolean onBlockInteract(World world, int x, int y, int z, SpoutPlayer player, boolean didJustCreateDatabaseEntry) {
		RouterData data = CCMain.instance.getDatabase().find(RouterData.class)
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
		
		new RouterBlockGUI(data.getId(), player);
		
		return true;
	}
}
