package net.robbytu.computercraft.lib.spout;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.TwoArgFunction;

import net.robbytu.computercraft.CCMain;
import net.robbytu.computercraft.computer.ComputerThread;
import net.robbytu.computercraft.database.ComputerData;
import net.robbytu.computercraft.database.RouterData;
import net.robbytu.computercraft.lib.LuaLib;
import net.robbytu.computercraft.util.ConfigManager;

public class RednetLib extends LuaLib {

	private ComputerThread computer;
	private String ssid;

	public RednetLib() {
		super("rednet");
	}
	
	public String getConnectedSSID() {
		return ssid;
	}

	@Override
	public LuaValue init(ComputerThread computer, LuaValue env) {
		this.computer = computer;
		LuaTable rednet = new LuaTable();
		rednet.set("send", new TwoArgFunction() {
			public LuaValue call(LuaValue val1, LuaValue val2) {
				return valueOf(send(val1.checkint(), val2.checkjstring()));
			}
		});
		
		rednet.set("connect", new TwoArgFunction() {
			public LuaValue call(LuaValue val1, LuaValue val2) {
				return valueOf(connect(val1.checkjstring(), val2.checkjstring()));
			}
		});
		
		env.set("rednet", rednet);
		return rednet;
	}
	
	public String send(int sendTo, String message) {
		if (computer.isWireless()) {
			if (ssid != null && !ssid.isEmpty()) {
				ComputerData sendToCompData = CCMain.instance.getDatabase().find(ComputerData.class)
						.where()
						.eq("id", sendTo)
					.findUnique();
				
				if (sendToCompData != null) {
					ComputerThread sendToComp = CCMain.instance.ComputerThreads.get(sendToCompData.getId());
					
					if (sendToComp != null) {
						RednetLib rednet = sendToComp.getLib("rednet");
						if (rednet == null) 
							return "RN_SEND_ERR_EVENTSLIBMISSING";
						
						// TODO: Put in checks to see if the SSID is connected to rednet, for now only worry about internal network!
						if (rednet.getConnectedSSID().equals(ssid)) {
							// Send messsage to the computer!
							EventsLib lib = sendToComp.getLib("events");
							if (lib == null) 
								return "RN_SEND_ERR_EVENTSLIBMISSING";
							lib.triggerEvent("rednet_receive", message);
							return "RN_SEND_SUCCESSFUL";
						}
					}
				}
				
				return "RN_DEST_NOT_FOUND";
			}
			
			return "RN_NO_CONNECTION";
		}

		return "RN_NO_WIRELESS";
	}
	
	public String connect(String ssid, String password) {
		if (computer.isWireless()) {
			// Step 1: Check for network
			RouterData routerData = CCMain.instance.getDatabase().find(RouterData.class)
					.where()
						.eq("SSID", ssid)
					.findUnique();
			if (routerData != null) {
			
				// Step 2: Make sure the network is in range!
				Location computerLocation = computer.getPosition();
				Location routerLocation = new Location(Bukkit.getWorld(routerData.getWorld()), routerData.getX(), routerData.getY(), routerData.getZ());
				
				int range = ConfigManager.antennaRange * routerData.getAntennas();
				if (range == 0) {
					range = 5;
				}
				
				if ((computerLocation.getWorld().equals(routerLocation.getWorld())) && (computerLocation.toVector().subtract(routerLocation.toVector()).lengthSquared() <= range)) { // TODO: Replace two with the number of antennas router has
					if (routerData.getPassword().equals(password)) {
						this.ssid = ssid;					
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
		
		return "RN_NO_WIRELESS";
	}

}
