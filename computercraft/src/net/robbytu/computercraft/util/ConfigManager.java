package net.robbytu.computercraft.util;

import org.bukkit.configuration.file.FileConfiguration;

public class ConfigManager {
	public static int antennaRange;
	public static String graphicsBasepath;
	
	public static void loadConfig(FileConfiguration config) {
		config.options().copyDefaults(true);
		antennaRange = config.getInt("antennas.range", 10);
		graphicsBasepath = config.getString("graphics.basepath", "http://www.robbytu.net/spout/computercraft/resources/");
	}
}
