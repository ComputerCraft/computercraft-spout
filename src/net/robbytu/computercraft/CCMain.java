package net.robbytu.computercraft;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.robbytu.computercraft.database.ComputerData;
import net.robbytu.computercraft.listeners.ComputerBlockPlacementListener;
import net.robbytu.computercraft.materials.Materials;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.inventory.SpoutItemStack;
import org.getspout.spoutapi.inventory.SpoutShapedRecipe;
import org.getspout.spoutapi.material.MaterialData;

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
		
		// Fill in the static variables
		instance = this;
		ComputerThreads = new HashMap<String, ComputerThread>();
		
		// Register recipes with Spout
		new Materials();
		this.registerRecipes();
		
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
	public void registerRecipes() {
		SpoutItemStack ComputerBlockRecipeResult = new SpoutItemStack(Materials.ComputerBlockEast);
		SpoutShapedRecipe ComputerBlockRecipe = new SpoutShapedRecipe(ComputerBlockRecipeResult);
		
		ComputerBlockRecipe.shape("AAA", "ABA", "ACA");
		ComputerBlockRecipe.setIngredient('A', MaterialData.stone);
		ComputerBlockRecipe.setIngredient('B', MaterialData.redstone);
		ComputerBlockRecipe.setIngredient('C', MaterialData.glassPane);
		
		SpoutManager.getMaterialManager().registerSpoutRecipe(ComputerBlockRecipe);
	}
}
