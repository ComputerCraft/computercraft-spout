package net.robbytu.computercraft.api;

import net.robbytu.computercraft.hardware.PlugType;

/**
 * Any piece of hardware in computercraft is a Device that can be plugged into HostDevices to be used.
 * 
 * @author hapm
 */
public interface Device {
	/**
	 * A {@link HostDevice} can call this message on Device
	 * to notify it for being plugged in.
	 * 
	 * @param type The {@link PlugType} used to plug the device in.
	 */
	public void onPlugIn(PlugType type);
	
	/**
	 * A {@link HostDevice} can call this message on Device
	 * to notify it for being plugged out.
	 * 
	 * @param type The {@link PlugType} used to plug the device in.
	 */
	public void onPlugOut(PlugType type);
}
