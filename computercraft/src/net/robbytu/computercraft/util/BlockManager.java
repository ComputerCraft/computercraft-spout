package net.robbytu.computercraft.util;

import org.bukkit.World;
import org.getspout.spoutapi.block.SpoutBlock;

public class BlockManager {
	public static SpoutBlock blockAtSide(SpoutBlock block,int side) {
		World w = block.getWorld();

		if (side == 0) return (SpoutBlock) w.getBlockAt(block.getLocation().getBlockX(), block.getLocation().getBlockY(), block.getLocation().getBlockZ() +1);
		if (side == 1) return (SpoutBlock) w.getBlockAt(block.getLocation().getBlockX(), block.getLocation().getBlockY(), block.getLocation().getBlockZ() -1);
	    if (side == 2) return (SpoutBlock) w.getBlockAt(block.getLocation().getBlockX() +1, block.getLocation().getBlockY(), block.getLocation().getBlockZ());
	    if (side == 3) return (SpoutBlock) w.getBlockAt(block.getLocation().getBlockX() -1, block.getLocation().getBlockY(), block.getLocation().getBlockZ());

		return null;
	}
}
