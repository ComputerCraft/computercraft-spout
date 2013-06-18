package net.robbytu.computercraft.api;

/**
 * An InputDevice can be used to recieve data from a Device that is plugged in to a HostDevice.
 * 
 * @author hapm
 */
public interface InputDevice extends Device {
	/**
	 * Gets the current data from the device.
	 * 
	 * @return The DataObject instance to read.
	 */
	public DataObject readData();
	
	/**
	 * Checks if the device is currently ready to read from it.
	 * 
	 * @return true, if the object can handle reads for data at the moment or not.
	 */
	public boolean canRead();
}
