package net.robbytu.computercraft.material.block;

import net.robbytu.computercraft.util.ConfigManager;

import org.bukkit.plugin.Plugin;
import org.getspout.spoutapi.block.design.BlockDesign;
import org.getspout.spoutapi.block.design.GenericCubeBlockDesign;
import org.getspout.spoutapi.block.design.Texture;

public class ComputerBlock extends BaseComputerBlock {

	public ComputerBlock(Plugin plugin, String name, boolean isOpaque) {
		super(plugin, name, isOpaque, setupDesign(plugin, ConfigManager.graphicsBasepath + "computerblock.png"), true);
		this.setName("Computer");
	}
	
	protected static BlockDesign setupDesign(Plugin plugin, String texture) {
		GenericCubeBlockDesign BlockDesign;
		BlockDesign = new GenericCubeBlockDesign(
				plugin,
				new Texture(plugin, texture, 256, 256, 16),
				new int[] { 2, 2, 2, 0, 2, 2 });
		
		return BlockDesign;
	}
}