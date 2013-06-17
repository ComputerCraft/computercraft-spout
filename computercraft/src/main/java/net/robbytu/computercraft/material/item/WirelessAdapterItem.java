package net.robbytu.computercraft.material.item;

import net.robbytu.computercraft.util.ConfigManager;

import org.bukkit.plugin.Plugin;
import org.getspout.spoutapi.material.item.GenericCustomItem;

public class WirelessAdapterItem extends GenericCustomItem {

	public WirelessAdapterItem(Plugin plugin) {
		super(plugin, "Wireless Adapter", ConfigManager.graphicsBasepath + "adapter.png");
		
	}
}
