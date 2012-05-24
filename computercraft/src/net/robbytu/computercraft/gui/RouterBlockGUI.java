package net.robbytu.computercraft.gui;

import net.robbytu.computercraft.CCMain;

import org.getspout.spoutapi.gui.GenericPopup;
import org.getspout.spoutapi.gui.GenericTextField;
import org.getspout.spoutapi.gui.GenericLabel;
import org.getspout.spoutapi.gui.GenericTexture;
import org.getspout.spoutapi.gui.RenderPriority;
import org.getspout.spoutapi.player.SpoutPlayer;

public class RouterBlockGUI {
	int routerID;
	
	private GenericTexture bg;
	
	private GenericTextField SSID;
	private GenericTextField Password;
	
	public RouterBlockGUI(int routerID, SpoutPlayer player) {
		this.routerID = routerID;
		
		this.bg = new GenericTexture("http://robbytu.net/spout/computercraft/resources/GUIBackground.png");
		this.bg.setX(40).setY(1);
		this.bg.setWidth(352).setHeight(308);
		this.bg.setPriority(RenderPriority.High);

		GenericLabel ssid_label = new GenericLabel();
		ssid_label.setX(52).setY(12);
		ssid_label.setText("Name (SSID):");
		ssid_label.setPriority(RenderPriority.High);
		
		this.SSID = new GenericTextField();
		SSID.setX(104).setY(50);
		SSID.setWidth(300).setHeight(15);
		SSID.setPlaceholder("Required");
		SSID.setMaximumLines(1);
		SSID.setPriority(RenderPriority.Low);
		
		GenericPopup popup = new GenericPopup();
		player.getMainScreen().attachPopupScreen(popup);
		popup.attachWidgets(CCMain.instance, this.bg, ssid_label, this.SSID);
	}
}
