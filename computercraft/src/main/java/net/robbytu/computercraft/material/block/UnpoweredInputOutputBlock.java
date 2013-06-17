package net.robbytu.computercraft.material.block;

import net.robbytu.computercraft.CCMain;
import net.robbytu.computercraft.material.Materials;
import net.robbytu.computercraft.util.ConfigManager;

import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.plugin.Plugin;
import org.getspout.spoutapi.block.design.BlockDesign;
import org.getspout.spoutapi.block.design.GenericCubeBlockDesign;
import org.getspout.spoutapi.material.block.GenericCustomBlock;

public class UnpoweredInputOutputBlock extends GenericCustomBlock {	
	public UnpoweredInputOutputBlock(Plugin plugin, String name, boolean isOpaque) {
		super(CCMain.instance, name, 75, setupDesign(plugin, ConfigManager.graphicsBasepath + "computerblock.png"));
	}
	
	/*@Override
	public boolean isPowerSource() {
		return true;
	}
	
	@Override
	public boolean isProvidingPowerTo(World world, int x, int y, int z,
			BlockFace face) {
		return false;
	}*/
	
	private static BlockDesign setupDesign(Plugin plugin, String texture) {
		int design = 4;
		GenericCubeBlockDesign BlockDesign;
		
		BlockDesign = new GenericCubeBlockDesign(
				plugin,
				Materials.ComputerCraftTexture,
				new int[] { design, design, design, design, design, design });
		
		return BlockDesign;
	}
}
