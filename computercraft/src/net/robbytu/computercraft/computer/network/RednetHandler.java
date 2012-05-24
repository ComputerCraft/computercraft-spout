package net.robbytu.computercraft.computer.network;

import net.robbytu.computercraft.CCMain;
import net.robbytu.computercraft.database.ComputerData;
import net.robbytu.computercraft.util.ConfigManager;

import org.bukkit.Bukkit;
import org.bukkit.Location;

public class RednetHandler {
	public static String connect(String networkName, String networkPassword, int CID) {
		// Step 1: Check for network
		
		// Step 2: Make sure the network is in range!
		ComputerData data = CCMain.instance.getDatabase().find(ComputerData.class)
				.where()
					.eq("id", CID)
				.findUnique();
		Location computerLocation = new Location(Bukkit.getWorld(data.getWorld()), data.getX(), data.getY(), data.getZ());
		
		// TODO: Make this actually work once Networks actually have names!
		Location routerLocation = computerLocation;
		routerLocation.setX(routerLocation.getX() + 30);
		
		if (computerLocation.toVector().subtract(routerLocation.toVector()).lengthSquared() <= ConfigManager.maxRouterDistance) {
			// Router within distance!
			if ("NETWORKPASSWORDHERE".equals(networkPassword)) {
				data.setNetworkName("NETWORKNAMEHERE");
				data.setNetworkPassword("NETWORKPASSWORDHERE");
				CCMain.instance.getDatabase().save(data);
				return "RN_CONNECTED";
			}
			else
				return "RN_INVALID_PASSWORD";
		}
		else
			return "RN_OUT_OF_RANGE";
		
		//return "RN_NO_NETWORK"; //TODO: Uncomment once it actually checks for the network name
	}
}
