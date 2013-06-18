package net.robbytu.computercraft.api;

import java.io.File;
import java.io.IOException;

/**
 * A StorageDevice can be used to write and read permanent data.
 * 
 * @author hapm
 */
public interface StorageDevice extends InputDevice, OutputDevice {
	/**
	 * Gets the overall capacity of the device.
	 * 
	 * @return The capacity, that can be saved on the device.
	 */
	public long getCapacity();
	
	/**
	 * Gets the amount of already used capacity.
	 * 
	 * @return The capacity that is already used by data saved
	 *         to the device.
	 */
	public long getUsedCapacity();
	
	/**
	 * Gets the {@link File} for the given path.
	 * 
	 * @param path The path to look up on the device.
	 * @return The path to look up.
	 * @throws IOException Thrown when the path is wrong or couldn't be resolved to a file.
	 */
	public File pathToFile(String path) throws IOException;
	
	/**
	 * Gets the real root directory File for the given StorageDevice.
	 * 
	 * @return The File that represents the root directory.
	 */
	public File getRoot();
}
