package net.robbytu.computercraft.material.block;

import net.robbytu.computercraft.CCMain;

import org.getspout.spoutapi.block.design.GenericCubeBlockDesign;
import org.getspout.spoutapi.block.design.Texture;
import org.getspout.spoutapi.material.block.GenericCustomBlock;

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
}
