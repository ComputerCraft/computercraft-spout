package net.robbytu.computercraft.util;

import org.bukkit.configuration.file.FileConfiguration;

public class ConfigManager {
	public static int maxRouterDistance;
	
	public static void loadConfig(FileConfiguration config) {
		config.options().copyDefaults(true);
		maxRouterDistance = config.getInt("routers.max_distance", 10);
	}
}
