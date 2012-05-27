package net.robbytu.computercraft.gui;

import net.robbytu.computercraft.material.Materials;

import org.getspout.spoutapi.event.screen.ButtonClickEvent;
import org.getspout.spoutapi.gui.GenericButton;
import org.getspout.spoutapi.inventory.SpoutItemStack;
import org.getspout.spoutapi.player.SpoutPlayer;

public class MinAntennaButton extends GenericButton {
	private RouterBlockGUI gui;
	private SpoutPlayer player;
	
	public MinAntennaButton(RouterBlockGUI gui, SpoutPlayer player) {
		super();
		this.gui = gui;
		this.player = player;
		this.setText("-").setWidth(15).setHeight(15);
	}
	
	@Override
	public void onButtonClick(ButtonClickEvent event) {
		if(this.gui.getAmountOfAntennas() != 0) {
			SpoutItemStack itemStack = new SpoutItemStack(Materials.WirelessAntennaItem, 1);
			itemStack.setAmount(1);

			this.player.getInventory().addItem(itemStack);
		
			this.gui.setAmountOfAntennas(gui.getAmountOfAntennas() - 1);
		}
	}
}
