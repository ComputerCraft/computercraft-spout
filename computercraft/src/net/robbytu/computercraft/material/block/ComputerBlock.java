package net.robbytu.computercraft.material.block;

import net.robbytu.computercraft.CCMain;
import net.robbytu.computercraft.material.Materials;
import net.robbytu.computercraft.util.ConfigManager;

import org.bukkit.plugin.Plugin;
import org.getspout.spoutapi.block.design.GenericCubeBlockDesign;
import org.getspout.spoutapi.block.design.Texture;
import org.getspout.spoutapi.inventory.SpoutItemStack;

public class ComputerBlock extends BaseComputerBlock {

	public ComputerBlock(Plugin plugin, String name, boolean isOpaque, int face) {
		super(plugin, name, ConfigManager.graphicsBasepath + "computerblock.png", isOpaque, face);
		this.setName("Computer");
		if (!name.equals("ComputerBlockEast"))
			setItemDrop(new SpoutItemStack(Materials.ComputerBlockEast, 1));
	}
	
	@Override
	protected void setupDesign(Plugin plugin, int face, String texture) {
		GenericCubeBlockDesign BlockDesign;
		
		if(face == 0) {
			BlockDesign = new GenericCubeBlockDesign(
					plugin,
					new Texture(plugin, texture, 256, 256, 16),
					new int[] { 2, 2, 2, 0, 2, 2 });
		}
		else if(face == 1) {
			BlockDesign = new GenericCubeBlockDesign(
					plugin,
					new Texture(plugin, texture, 256, 256, 16),
					new int[] { 2, 0, 2, 2, 2, 2 });
		}
		else if(face == 3) {
			BlockDesign = new GenericCubeBlockDesign(
					plugin,
					new Texture(plugin, texture, 256, 256, 16),
					new int[] { 2, 2, 2, 2, 0, 2 });
		}
		else {
			BlockDesign = new GenericCubeBlockDesign(
					plugin,
					new Texture(plugin, texture, 256, 256, 16),
					new int[] { 2, 2, 0, 2, 2, 2 });
		}
		
		this.setBlockDesign(BlockDesign);
	}
}