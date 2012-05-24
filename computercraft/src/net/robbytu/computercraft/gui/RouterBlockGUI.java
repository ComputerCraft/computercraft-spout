package net.robbytu.computercraft.gui;

import net.robbytu.computercraft.CCMain;

import org.getspout.spoutapi.gui.GenericPopup;
import org.getspout.spoutapi.player.SpoutPlayer;

public class RouterBlockGUI {
	int routerID;
	
	public RouterBlockGUI(int routerID, SpoutPlayer player) {
		this.routerID = routerID;

		GenericPopup popup = new GenericPopup();
		player.getMainScreen().attachPopupScreen(popup);
		popup.attachWidgets(CCMain.instance);
	}
}
