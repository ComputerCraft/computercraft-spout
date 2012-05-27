package net.robbytu.computercraft.gui;

import net.robbytu.computercraft.material.Materials;
import net.robbytu.computercraft.util.ConfigManager;

import org.getspout.spoutapi.event.screen.ButtonClickEvent;
import org.getspout.spoutapi.gui.GenericButton;
import org.getspout.spoutapi.inventory.SpoutItemStack;
import org.getspout.spoutapi.player.SpoutPlayer;

public class AddAntennaButton extends GenericButton {
	private RouterBlockGUI gui;
	private SpoutPlayer player;
	
	public AddAntennaButton(RouterBlockGUI gui, SpoutPlayer player) {
		super();
		this.gui = gui;
		this.player = player;
		this.setText("+").setWidth(15).setHeight(15);
	}
	
	@Override
	public void onButtonClick(ButtonClickEvent event) {
		if(this.gui.getAmountOfAntennas() < ConfigManager.antennaRange) {
			SpoutItemStack itemStack = new SpoutItemStack(Materials.WirelessAntennaItem, 1);
			itemStack.setAmount(1);
	
			if(this.player.getInventory().contains(itemStack, 1)) {
				this.player.getInventory().removeItem(itemStack);
				
				this.gui.setAmountOfAntennas(gui.getAmountOfAntennas() + 1);
			}
			else {
				player.sendMessage("�4You haven't got any spare wireless antennas left in your inventory!");
			}
		}
		else {
			player.sendMessage("�4You have put in the maximum amount of antennas already.");
		}
	}
}
