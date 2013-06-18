package net.robbytu.computercraft.hardware;

import net.robbytu.computercraft.api.Device;
import net.robbytu.computercraft.api.HostDevice;

/**
 * The PlugType specifies the way of how a {@link Device} is
 * connected to its {@link HostDevice}. There are default PlugTypes
 * but developers can create their own PlugType for their devices 
 * as well.
 * 
 * @author hapm
 */
public final class PlugType {
	/**
	 * Saves the internal PlugType id used to identify the PlugType.
	 */
	private int id;
	
	/**
	 * Saves the string used to identify the PlugType.
	 */
	private String hardwareIdentifier;
	
	/**
	 * Creates a new PlugType with the given hardwareIdentifier and id.
	 * 
	 * @param hardwareIdentifier
	 */
	PlugType(String hardwareIdentifier, int id) {
		this.hardwareIdentifier = hardwareIdentifier;
		this.id = id;
	}
	
	/**
	 * Gets the hardware identify string of the PlugType.
	 *  
	 * @return The identifier as a String.
	 */
	public String getHardwareIdentifier() {
		return hardwareIdentifier;
	}

	/**
	 * Gets the internal id for the PlugType.
	 * 
	 * @return The internal id.
	 */
	int getId() {
		return id;
	}
}
