package net.robbytu.computercraft.gui;

import net.robbytu.computercraft.material.Materials;

import org.getspout.spoutapi.event.screen.ButtonClickEvent;
import org.getspout.spoutapi.gui.GenericButton;
import org.getspout.spoutapi.inventory.SpoutItemStack;
import org.getspout.spoutapi.player.SpoutPlayer;

public class RouterApplyButton extends GenericButton {
	private RouterBlockGUI gui;
	private SpoutPlayer player;
	
	public RouterApplyButton(RouterBlockGUI gui, SpoutPlayer player) {
		super();
		this.gui = gui;
		this.player = player;
		this.setText("Apply").setWidth(150).setHeight(15);
	}
	
	@Override
	public void onButtonClick(ButtonClickEvent event) {
		this.gui.applyChanges();

		player.getMainScreen().closePopup();
	}
}
