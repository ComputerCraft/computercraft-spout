package net.robbytu.computercraft.listeners;

import net.robbytu.computercraft.materials.ComputerBlock;
import net.robbytu.computercraft.materials.Materials;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.block.SpoutBlock;

public class ComputerBlockPlacementListener implements Listener {

	@EventHandler
	public void rotateComputerBlock(BlockPlaceEvent event) {
		if((SpoutBlock)event.getBlock() == null) return;
		
		if(((SpoutBlock)event.getBlock()).getCustomBlock() == null) return;
		
		if(((SpoutBlock)event.getBlock()).getCustomBlock().getName().startsWith("Computer")) {
			// Many thanks to Jukebukkit's source (by Thedudeguy),
			// cause I didn't really know how to make a block face the player :)
			
			ComputerBlock NewBlock;
			
			double yaw = (event.getPlayer().getLocation().getYaw() - 90) % 360;
			if(yaw < 0) yaw += 360.0;
			
			if(0 <= yaw && yaw < 45) NewBlock = Materials.ComputerBlockEast;
			else if(45 <= yaw && yaw < 135) NewBlock = Materials.ComputerBlockSouth;
			else if(135 <= yaw && yaw < 215) NewBlock = Materials.ComputerBlockWest;
			else if(215 <= yaw && yaw < 305) NewBlock = Materials.ComputerBlockNorth;
			else if(305 <= yaw && yaw <= 360) NewBlock = Materials.ComputerBlockEast;
			else NewBlock = Materials.ComputerBlockSouth;
			
			((SpoutBlock)event.getBlock()).setCustomBlock(NewBlock);
			SpoutManager.getMaterialManager().overrideBlock((SpoutBlock)event.getBlock(), NewBlock);
		}
	}
}
