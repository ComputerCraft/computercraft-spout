package net.robbytu.computercraft.material;

import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.inventory.SpoutItemStack;
import org.getspout.spoutapi.inventory.SpoutShapedRecipe;
import org.getspout.spoutapi.inventory.SpoutShapelessRecipe;
import org.getspout.spoutapi.material.MaterialData;

import net.robbytu.computercraft.CCMain;
import net.robbytu.computercraft.material.block.ComputerBlock;
import net.robbytu.computercraft.material.block.RouterBlock;
import net.robbytu.computercraft.material.block.WirelessComputerBlock;
import net.robbytu.computercraft.material.item.WirelessAdapterItem;
import net.robbytu.computercraft.material.item.WirelessAntennaItem;

public class Materials {
	public static ComputerBlock ComputerBlock;	
	public static WirelessComputerBlock WirelessComputerBlock;
	
	public static WirelessAdapterItem WirelessAdapterItem;
	public static WirelessAntennaItem WirelessAntennaItem;
	
	public static RouterBlock RouterBlock;
	
	public Materials() {
		ComputerBlock = new ComputerBlock(CCMain.instance, "ComputerBlock", false);
		
		WirelessComputerBlock = new WirelessComputerBlock(CCMain.instance, "WirelessComputerBlock", false, 2);
		
		WirelessAdapterItem = new WirelessAdapterItem(CCMain.instance);
		WirelessAntennaItem = new WirelessAntennaItem(CCMain.instance);
		
		RouterBlock = new RouterBlock();
		
		registerRecipes();
	}
	
	public void registerRecipes() {
		// Computer Block
		SpoutItemStack ComputerBlockRecipeResult = new SpoutItemStack(ComputerBlock);
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
		SpoutItemStack WirelessComputerBlockRecipeResult = new SpoutItemStack(WirelessComputerBlock);
		SpoutShapelessRecipe WirelessComputerBlockRecipe = new SpoutShapelessRecipe(WirelessComputerBlockRecipeResult);
		
		WirelessComputerBlockRecipe.addIngredient(1, ComputerBlock);
		WirelessComputerBlockRecipe.addIngredient(1, WirelessAdapterItem);
		
		SpoutManager.getMaterialManager().registerSpoutRecipe(WirelessComputerBlockRecipe);

		// Router Block
		SpoutItemStack RouterBlockRecipeResult = new SpoutItemStack(RouterBlock);
		SpoutShapedRecipe RouterBlockRecipe = new SpoutShapedRecipe(RouterBlockRecipeResult);
		
		RouterBlockRecipe.shape("ABA", "ABA", "AAA");
		RouterBlockRecipe.setIngredient('A', MaterialData.ironIngot);
		RouterBlockRecipe.setIngredient('B', MaterialData.redstone);
		
		SpoutManager.getMaterialManager().registerSpoutRecipe(RouterBlockRecipe);
	}
}
