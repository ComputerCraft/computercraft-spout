package net.robbytu.computercraft.material.block;

import net.robbytu.computercraft.material.Materials;

import org.bukkit.World;
import org.bukkit.plugin.Plugin;
import org.getspout.spoutapi.block.design.GenericCubeBlockDesign;
import org.getspout.spoutapi.block.design.Texture;
import org.getspout.spoutapi.inventory.SpoutItemStack;

public class WirelessComputerBlock extends ComputerBlock {

	public WirelessComputerBlock(Plugin plugin, String name, boolean isOpaque, int face) {
		super(plugin, name, isOpaque, face);
		this.setName("WirelessComputer");
		
		if (!name.equals("WirelessComputerBlockEast"))
			setItemDrop(new SpoutItemStack(Materials.WirelessComputerBlockEast, 1));
		
		setupDesign(plugin, face, "http://robbytu.net/spout/computercraft/resources/computerblock.png");
	}
	
	private void setupDesign(Plugin plugin, int face, String texture) {
		GenericCubeBlockDesign BlockDesign;
		
		if(face == 0) {
			BlockDesign = new GenericCubeBlockDesign(
					plugin,
					new Texture(plugin, texture, 256, 256, 16),
					new int[] { 2, 2, 2, 0, 2, 3 });
		}
		else if(face == 1) {
			BlockDesign = new GenericCubeBlockDesign(
					plugin,
					new Texture(plugin, texture, 256, 256, 16),
					new int[] { 2, 0, 2, 2, 2, 3 });
		}
		else if(face == 3) {
			BlockDesign = new GenericCubeBlockDesign(
					plugin,
					new Texture(plugin, texture, 256, 256, 16),
					new int[] { 2, 2, 2, 2, 0, 3 });
		}
		else {
			BlockDesign = new GenericCubeBlockDesign(
					plugin,
					new Texture(plugin, texture, 256, 256, 16),
					new int[] { 2, 2, 0, 2, 2, 3 });
		}
		
		this.setBlockDesign(BlockDesign);
	}
	
	@Override
	public void onBlockPlace(World world, int x, int y, int z) {
		onBlockPlace(world, x, y, z, true);
	}
}
