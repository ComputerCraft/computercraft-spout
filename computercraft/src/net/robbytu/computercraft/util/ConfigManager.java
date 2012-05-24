package net.robbytu.computercraft.util;

import org.bukkit.configuration.file.FileConfiguration;

public class ConfigManager {
	public static int antennaRange;
	
	public static void loadConfig(FileConfiguration config) {
		config.options().copyDefaults(true);
		antennaRange = config.getInt("antennas.range", 10);
	}
}
