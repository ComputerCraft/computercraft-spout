package net.robbytu.computercraft.listener;

import net.robbytu.computercraft.material.Materials;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class ComputerBlockPlacementListener implements Listener {
	@EventHandler
	public void onBlockChange(BlockPlaceEvent event) {
		if (event.getBlock().getType().equals(Materials.PoweredIOBlock)) 
			event.getBlock().getWorld();
	}
}
