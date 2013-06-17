package net.robbytu.computercraft.material.block;

import net.robbytu.computercraft.util.ConfigManager;

import org.bukkit.World;
import org.bukkit.plugin.Plugin;
import org.getspout.spoutapi.block.design.BlockDesign;
import org.getspout.spoutapi.block.design.GenericCubeBlockDesign;
import org.getspout.spoutapi.block.design.Texture;

public class WirelessComputerBlock extends BaseComputerBlock {

	public WirelessComputerBlock(Plugin plugin, String name, boolean isOpaque) {
		super(plugin, name, isOpaque, setupDesign(plugin, ConfigManager.graphicsBasepath + "computerblock.png"), true);
		this.setName("Computer with Wireless Network Card");		
	}
	
	protected static BlockDesign setupDesign(Plugin plugin, String texture) {
		GenericCubeBlockDesign BlockDesign;
		
		BlockDesign = new GenericCubeBlockDesign(
				plugin,
				new Texture(plugin, texture, 256, 256, 16),
				new int[] { 2, 3, 3, 0, 3, 2 });
		
		return BlockDesign;
	}
	
	@Override
	public void onBlockPlace(World world, int x, int y, int z) {
		onBlockPlace(world, x, y, z, true);
	}
}
