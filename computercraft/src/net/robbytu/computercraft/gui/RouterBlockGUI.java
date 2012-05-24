package net.robbytu.computercraft.gui;

import net.robbytu.computercraft.CCMain;
import net.robbytu.computercraft.database.RouterData;

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
	
	private RouterData data;
	
	public RouterBlockGUI(int routerID, SpoutPlayer player) {
		this.routerID = routerID;
		
		this.data = CCMain.instance.getDatabase().find(RouterData.class)
				.where()
					.eq("id", this.routerID)
				.findUnique();
		
		this.bg = new GenericTexture("http://robbytu.net/spout/computercraft/resources/GUIBackground.png");
		this.bg.setX(40).setY(1);
		this.bg.setWidth(352).setHeight(308);
		this.bg.setPriority(RenderPriority.Highest);

		GenericLabel conf_label = new GenericLabel();
		conf_label.setX(140).setY(12);
		conf_label.setText("Router configuration wizard");
		conf_label.setPriority(RenderPriority.High);

		GenericLabel ssid_label = new GenericLabel();
		ssid_label.setX(57).setY(42);
		ssid_label.setText("Name (SSID):");
		ssid_label.setPriority(RenderPriority.High);
		
		this.SSID = new GenericTextField();
		SSID.setX(154).setY(40);
		SSID.setWidth(220).setHeight(15);
		SSID.setPlaceholder("¤cRequired");
		SSID.setText(this.data.getSSID());
		SSID.setMaximumLines(1);
		SSID.setPriority(RenderPriority.Low);
		
		GenericLabel password_label = new GenericLabel();
		password_label.setX(57).setY(64);
		password_label.setText("Password:");
		password_label.setPriority(RenderPriority.High);
		
		this.Password = new GenericTextField();
		Password.setX(154).setY(62);
		Password.setWidth(220).setHeight(15);
		Password.setPlaceholder("¤7Optional");
		Password.setText(this.data.getPassword());
		Password.setPasswordField(true);
		Password.setMaximumLines(1);
		Password.setPriority(RenderPriority.Low);
		
		GenericPopup popup = new GenericPopup();
		player.getMainScreen().attachPopupScreen(popup);
		popup.attachWidgets(CCMain.instance, this.bg, conf_label, ssid_label, this.SSID, password_label, this.Password);
	}
}
