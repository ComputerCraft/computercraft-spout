package net.robbytu.computercraft;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.robbytu.computercraft.computer.ComputerThread;
import net.robbytu.computercraft.database.ComputerData;
import net.robbytu.computercraft.listeners.ComputerBlockPlacementListener;
import net.robbytu.computercraft.material.Materials;
import net.robbytu.computercraft.util.ConfigManager;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class CCMain extends JavaPlugin {
	
	// For use in other classes
	public static CCMain instance;
	public HashMap<String, ComputerThread> ComputerThreads;
	
	@Override
	public void onEnable() {
		// Check for Spout
		if(!Bukkit.getPluginManager().isPluginEnabled("Spout")) {
			Bukkit.getLogger().severe("You need to have SpoutPlugin to run ComputerCraft!");
			this.setEnabled(false);
			
			return;
		}
			
		// Setup all the defaults - This MIGHT be better off in it's own class as we add more configs
		getDataFolder().mkdir(); // This will not do anythig if it already exists
		new File(getDataFolder().getAbsolutePath() + "/computers/").mkdir();
		File romDir = new File(getDataFolder().getAbsolutePath() + "/rom/");
		romDir.mkdir();
		File defaultRom = new File(romDir, "boot.lua");
		if (!defaultRom.exists()) {
			try {
				defaultRom.createNewFile();
				OutputStream output = new FileOutputStream(defaultRom, false);
		        InputStream input = CCMain.class.getResourceAsStream("/defaults/boot.lua");
		        byte[] buf = new byte[8192];
		        while (true) {
		          int length = input.read(buf);
		          getLogger().info("" + length);
		          if (length < 0) {
		            break;
		          }
		          output.write(buf, 0, length);
		        }
		        input.close();
		        output.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		// Load configs
		ConfigManager.LoadConfig(getConfig());
		
		// Fill in the static variables
		instance = this;
		ComputerThreads = new HashMap<String, ComputerThread>();
		
		// Register recipes with Spout
		new Materials();
		
		// Register listeners
		Bukkit.getPluginManager().registerEvents(new ComputerBlockPlacementListener(), this);
		
		// Database stuff
		try {
			getDatabase().find(ComputerData.class).findRowCount();
		}
		catch (Exception ex) {
			installDDL();
		}
	}
	
	@Override
	public void onDisable() {
		Bukkit.getLogger().info("ComputerCraft for Spout is disabled.");
	}
	
	@Override
	public List<Class<?>> getDatabaseClasses() {
		List<Class<?>> list = new ArrayList<Class<?>>();
		
		list.add(ComputerData.class);
		
		return list;
	}
	
}
