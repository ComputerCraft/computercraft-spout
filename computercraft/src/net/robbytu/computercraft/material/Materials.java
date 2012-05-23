package net.robbytu.computercraft.material;

import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.inventory.SpoutItemStack;
import org.getspout.spoutapi.inventory.SpoutShapedRecipe;
import org.getspout.spoutapi.inventory.SpoutShapelessRecipe;
import org.getspout.spoutapi.material.MaterialData;

import net.robbytu.computercraft.CCMain;
import net.robbytu.computercraft.material.block.ComputerBlock;
import net.robbytu.computercraft.material.block.WirelessComputerBlock;
import net.robbytu.computercraft.material.item.WirelessAdapterItem;
import net.robbytu.computercraft.material.item.WirelessAntennaItem;

public class Materials {
	public static ComputerBlock ComputerBlockSouth;
	public static ComputerBlock ComputerBlockNorth;
	public static ComputerBlock ComputerBlockEast;
	public static ComputerBlock ComputerBlockWest;
	
	public static WirelessComputerBlock WirelessComputerBlockSouth;
	public static WirelessComputerBlock WirelessComputerBlockNorth;
	public static WirelessComputerBlock WirelessComputerBlockEast;
	public static WirelessComputerBlock WirelessComputerBlockWest;
	
	public static WirelessAdapterItem WirelessAdapterItem;
	public static WirelessAntennaItem WirelessAntennaItem;
	
	public Materials() {
		ComputerBlockEast = new ComputerBlock(CCMain.instance, "ComputerBlockEast", false, 2);
		ComputerBlockSouth = new ComputerBlock(CCMain.instance, "ComputerBlockSouth", false, 0);
		ComputerBlockNorth = new ComputerBlock(CCMain.instance, "ComputerBlockNorth", false, 1);
		ComputerBlockWest = new ComputerBlock(CCMain.instance, "ComputerBlockWest", false, 3);
		
		WirelessComputerBlockEast = new WirelessComputerBlock(CCMain.instance, "WirelessComputerBlockEast", false, 2);
		WirelessComputerBlockSouth = new WirelessComputerBlock(CCMain.instance, "WirelessComputerBlockSouth", false, 0);
		WirelessComputerBlockNorth = new WirelessComputerBlock(CCMain.instance, "WirelessComputerBlockNorth", false, 1);
		WirelessComputerBlockWest = new WirelessComputerBlock(CCMain.instance, "WirelessComputerBlockWest", false, 3);
		
		WirelessAdapterItem = new WirelessAdapterItem(CCMain.instance);
		WirelessAntennaItem = new WirelessAntennaItem(CCMain.instance);
		
		registerRecipes();
	}
	
	public void registerRecipes() {
		// Computer Block
		SpoutItemStack ComputerBlockRecipeResult = new SpoutItemStack(ComputerBlockEast);
		SpoutShapedRecipe ComputerBlockRecipe = new SpoutShapedRecipe(ComputerBlockRecipeResult);
		
		ComputerBlockRecipe.shape("AAA", "ABA", "ACA");
		ComputerBlockRecipe.setIngredient('A', MaterialData.stone);
		ComputerBlockRecipe.setIngredient('B', MaterialData.redstone);
		ComputerBlockRecipe.setIngredient('C', MaterialData.glassPane);
		
		SpoutManager.getMaterialManager().registerSpoutRecipe(ComputerBlockRecipe);
		
		// Wireless Antenna
		SpoutItemStack WirelessAntennaRecipeResult = new SpoutItemStack(WirelessAntennaItem);
		SpoutShapedRecipe WirelessAntennaRecipe = new SpoutShapedRecipe(WirelessAntennaRecipeResult);
		
		WirelessAntennaRecipe.shape("BAB", "BAB", "CCC");
		WirelessAntennaRecipe.setIngredient('A', MaterialData.redstone);
		WirelessAntennaRecipe.setIngredient('B', MaterialData.ironIngot);
		WirelessAntennaRecipe.setIngredient('C', MaterialData.stone);
		
		SpoutManager.getMaterialManager().registerSpoutRecipe(WirelessAntennaRecipe);
		
		// Wireless Adapter Item
		SpoutItemStack WirelessAdapterRecipeResult = new SpoutItemStack(WirelessAdapterItem);
		SpoutShapedRecipe WirelessAdapterRecipe = new SpoutShapedRecipe(WirelessAdapterRecipeResult);
		
		WirelessAdapterRecipe.shape("ABA", "BCB", "ABA");
		WirelessAdapterRecipe.setIngredient('A', MaterialData.stone);
		WirelessAdapterRecipe.setIngredient('B', MaterialData.redstone);
		WirelessAdapterRecipe.setIngredient('C', WirelessAntennaItem);
		
		SpoutManager.getMaterialManager().registerSpoutRecipe(WirelessAdapterRecipe);
		
		// Wireless Computer Block		
		SpoutItemStack WirelessComputerBlockRecipeResult = new SpoutItemStack(WirelessComputerBlockEast);
		SpoutShapelessRecipe WirelessComputerBlockRecipe = new SpoutShapelessRecipe(WirelessComputerBlockRecipeResult);
		
		WirelessComputerBlockRecipe.addIngredient(1, ComputerBlockEast);
		WirelessComputerBlockRecipe.addIngredient(1, WirelessAdapterItem); // TODO: Update this to the new block
		
		SpoutManager.getMaterialManager().registerSpoutRecipe(WirelessComputerBlockRecipe);
	}
}
