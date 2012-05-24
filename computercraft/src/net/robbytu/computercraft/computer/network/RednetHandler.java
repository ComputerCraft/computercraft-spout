package net.robbytu.computercraft.computer.network;

import net.robbytu.computercraft.CCMain;
import net.robbytu.computercraft.database.ComputerData;
import net.robbytu.computercraft.database.RouterData;
import net.robbytu.computercraft.util.ConfigManager;

import org.bukkit.Bukkit;
import org.bukkit.Location;

public class RednetHandler {
	public static String connect(String SSID, String password, int CID) {
		// Step 1: Check for network
		RouterData routerData = CCMain.instance.getDatabase().find(RouterData.class)
				.where()
					.eq("SSID", SSID)
				.findUnique();
		if (routerData != null) {
		
			// Step 2: Make sure the network is in range!
			ComputerData computerData = CCMain.instance.getDatabase().find(ComputerData.class)
					.where()
						.eq("id", CID)
					.findUnique();
			
			Location computerLocation = new Location(Bukkit.getWorld(computerData.getWorld()), computerData.getX(), computerData.getY(), computerData.getZ());
			Location routerLocation = new Location(Bukkit.getWorld(routerData.getWorld()), routerData.getX(), routerData.getY(), routerData.getZ());
			
			if ((computerLocation.getWorld().equals(routerLocation.getWorld())) && (computerLocation.toVector().subtract(routerLocation.toVector()).lengthSquared() <= ConfigManager.antennaRange * routerData.getAntennas())) { // TODO: Replace two with the number of antennas router has
				if (routerData.getPassword().equals(password)) {					
					return "RN_CONNECTED";
				}
				else
					return "RN_INVALID_PASSWORD";
			}
			else
				return "RN_OUT_OF_RANGE";
		}
		return "RN_NO_NETWORK";
	}
}
