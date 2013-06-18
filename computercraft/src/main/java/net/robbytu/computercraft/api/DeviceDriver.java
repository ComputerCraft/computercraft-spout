package net.robbytu.computercraft.api;

import net.robbytu.computercraft.computer.ComputerThread;
import net.robbytu.computercraft.hardware.PlugType;

/**
 * A DeviceDriver can be used to access a Device from a ComputerThread.
 * 
 * The driver registers the needed lua functions to make the device accessible from 
 * a computer.
 * 
 * @author hapm
 *
 */
public interface DeviceDriver {
	/**
	 * Gets the class type of the devices that can be handled by this driver.
	 * 
	 * @return The {@link Class} instance for the handled device type.
	 */
	public Class<? extends Device> getDeviceType();
	
	/**
	 * Registers the given {@link Device} in the driver.
	 * 
	 * @param device The device to register.
	 * @param type The {@link PlugType} that was used to plug in the Device
	 */
	public void register(Device device, PlugType type);
	
	/**
	 * Removes the registered {@link Device} from the driver.
	 * 
	 * @param device The {@link Device} to remove.
	 * @param type The {@link PlugType} that was used to plug in the Device
	 */
	public void unregister(Device device, PlugType type);
}
