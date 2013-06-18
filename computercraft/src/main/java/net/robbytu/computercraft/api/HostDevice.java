package net.robbytu.computercraft.api;

import net.robbytu.computercraft.hardware.PlugType;

import org.bukkit.PortalType;

/**
 * A HostDevice has a given number of ports to plug in other devices.
 * 
 * The HostDevice can than communicate with the plugged in devices and get Device 
 * specific Data from the Devices. If a Device can be used by a HostDevice is determined by
 * the PlugType used to plug in the Device.
 * 
 * @author hapm
 */
public interface HostDevice extends Device {
	
	/**
	 * Used by {@link #getPortCount(PlugType)} to return an endless number of
	 * ports for a given {@link PortalType}
	 */
	public static final int ENDLESS_PORTS = -1; 
	
	/**
	 * Plugs in the given Device with the given PlugType.
	 * 
	 * @param device The Device to plug in.
	 * @param type The PlugType that should be used.
	 */
	public void plugIn(Device device, PlugType type);
	
	/**
	 * Plugs out the given Device with the given PlugType.
	 * 
	 * @param device The Device to plug out from the HostDevice
	 * @param type The PlugType used when the Device was plugged in.
	 */
	public void plugOut(Device device, PlugType type);
	
	/**
	 * Gets the number of ports the HostDive provides for the given PlugType.
	 * 
	 * @param type The type to get the count for.
	 * @return The number of ports the HostDevice provides for the given PlugType. 
	 *         Can return {@link #ENDLESS_PORTS} for an endless number of ports.
	 */
	public int getPortCount(PlugType type);
}
