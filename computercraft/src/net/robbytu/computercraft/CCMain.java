
package net.robbytu.computercraft;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import net.robbytu.computercraft.computer.ComputerThread;
import net.robbytu.computercraft.database.ComputerData;
import net.robbytu.computercraft.database.RouterData;
import net.robbytu.computercraft.lib.LuaLib;
import net.robbytu.computercraft.lib.spout.BaseLib;
import net.robbytu.computercraft.lib.spout.ColorLib;
import net.robbytu.computercraft.lib.spout.EventsLib;
import net.robbytu.computercraft.lib.spout.FileSystemLib;
import net.robbytu.computercraft.lib.spout.IoLib;
import net.robbytu.computercraft.lib.spout.MathLib;
import net.robbytu.computercraft.lib.spout.OsLib;
import net.robbytu.computercraft.lib.spout.RednetLib;
import net.robbytu.computercraft.lib.spout.RedstoneLib;
import net.robbytu.computercraft.lib.spout.StringLib;
import net.robbytu.computercraft.lib.spout.TableLib;
import net.robbytu.computercraft.lib.spout.TerminalLib;
import net.robbytu.computercraft.listeners.ComputerBlockPlacementListener;
import net.robbytu.computercraft.material.Materials;
import net.robbytu.computercraft.util.ConfigManager;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class CCMain extends JavaPlugin {
	
	// For use in other classes
	public static CCMain instance;
	public HashMap<Integer, ComputerThread> ComputerThreads;
	private ArrayList<Class<? extends LuaLib>> libClasses = new ArrayList<Class<? extends LuaLib>>();
	
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
		new File(getDataFolder().getAbsolutePath(), "computers" + File.separator).mkdir();
		File romDir = new File(getDataFolder().getAbsolutePath(), "rom" + File.separator);
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
		ConfigManager.loadConfig(getConfig());
		saveConfig();
		
		// Fill in the static variables
		instance = this;
		ComputerThreads = new HashMap<Integer, ComputerThread>();
		
		// Register recipes with Spout
		new Materials();
		
		// Register default libs
		registerLib(BaseLib.class);
		registerLib(FileSystemLib.class);
		registerLib(IoLib.class);
		registerLib(OsLib.class);
		registerLib(StringLib.class);
		registerLib(TableLib.class);
		registerLib(MathLib.class);
		registerLib(ColorLib.class);
		registerLib(EventsLib.class);
		registerLib(TerminalLib.class);
		registerLib(RedstoneLib.class);
		registerLib(RednetLib.class);
		
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
	
	public void registerLib(Class<? extends LuaLib> libclass) {
		libClasses.add(libclass);
	}
	
	@SuppressWarnings("unchecked")
	public Collection<Class<? extends LuaLib>> getLibrarys() {
		return (Collection<Class<? extends LuaLib>>) libClasses.clone();
	}
	
	@Override
	public void onDisable() {
		Bukkit.getLogger().info("ComputerCraft for Spout is disabled.");
	}
	
	public ComputerData findComputerData(String world, int x, int y, int z) {
		ComputerData data = CCMain.instance.getDatabase().find(ComputerData.class)
				.where()
					.eq("x", x)
					.eq("y", y)
					.eq("z", z)
					.eq("world", (String)world)
				.findUnique();
		return data;
	}
	
	public ComputerThread getComputerAt(String world, int x, int y, int z) {
		ComputerData data = findComputerData(world, x, y, z);
		if (data == null)
			return null;
		
		return ComputerThreads.get(data.getId());
	}
	
	@Override
	public List<Class<?>> getDatabaseClasses() {
		List<Class<?>> list = new ArrayList<Class<?>>();
		
		list.add(ComputerData.class);
		list.add(RouterData.class);
		
		return list;
	}
	
}
