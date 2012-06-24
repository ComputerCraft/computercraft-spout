package net.robbytu.computercraft.material.item;

import net.robbytu.computercraft.CCMain;
import net.robbytu.computercraft.util.ConfigManager;

import org.bukkit.plugin.Plugin;
import org.getspout.spoutapi.material.item.GenericCustomItem;

public class WirelessAntennaItem extends GenericCustomItem {
	
	public WirelessAntennaItem(Plugin plugin) {
		super(plugin, "Wireless Antenna", ConfigManager.graphicsBasepath + "antenna.png");
		
	}
}