package net.robbytu.computercraft.computer.network;

import net.robbytu.computercraft.CCMain;
import net.robbytu.computercraft.computer.ComputerThread;
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
			
			int range = ConfigManager.antennaRange * routerData.getAntennas();
			if (range == 0) {
				range = 5;
			}
			
			if ((computerLocation.getWorld().equals(routerLocation.getWorld())) && (computerLocation.toVector().subtract(routerLocation.toVector()).lengthSquared() <= range)) { // TODO: Replace two with the number of antennas router has
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
	
	public static String send(int sendTo, String message, String SSID, int CID) {
		ComputerData sendToCompData = CCMain.instance.getDatabase().find(ComputerData.class)
				.where()
				.eq("id", sendTo)
			.findUnique();
		
		if (sendToCompData != null) {
			ComputerThread sendToComp = CCMain.instance.ComputerThreads.get(sendToCompData.getId());
			
			if (sendToComp != null) {
				// TODO: Put in checks to see if the SSID is connected to rednet, for now only worry about internal network!
				if (sendToComp.connectedSSID().equals(SSID)) {
					// Send messsage to the computer!
					sendToComp.triggerEvent("rednet_receive", message);
					return "RN_SEND_SUCCESSFUL";
				}
			}
		}
		
		return "RN_DEST_NOT_FOUND";
	}
}
