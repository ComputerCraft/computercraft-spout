package net.robbytu.computercraft.api;

/**
 * An OutputDevice can be used to send data away from a HostDevice.
 * 
 * @author hapm
 */
public interface OutputDevice extends Device {
	/**
	 * The data to send over the OutputDevice
	 * 
	 * @param data The DataObject instance.
	 */
	public void writeData(DataObject data);
	
	/**
	 * Checks if the device is currently ready to be written to.
	 * 
	 * @return true, if the object can handle written data at the moment or not.
	 */
	public boolean canWrite();
}
