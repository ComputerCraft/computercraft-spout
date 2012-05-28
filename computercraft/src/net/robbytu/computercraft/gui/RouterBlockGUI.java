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
	private GenericTextField password;
	private GenericLabel antennas;
	
	private RouterData data;
	
	private SpoutPlayer player;
	
	public RouterBlockGUI(int routerID, SpoutPlayer player) {
		this.routerID = routerID;
		this.player = player;
		
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
		
		this.password = new GenericTextField();
		password.setX(154).setY(62);
		password.setWidth(220).setHeight(15);
		password.setPlaceholder("¤7Optional");
		password.setText(this.data.getPassword());
		password.setPasswordField(true);
		password.setMaximumLines(1);
		password.setPriority(RenderPriority.Low);

		GenericLabel antenna_label = new GenericLabel();
		antenna_label.setX(57).setY(86);
		antenna_label.setText("Antennas:");
		antenna_label.setPriority(RenderPriority.High);

		this.antennas = new GenericLabel();
		antennas.setX(154).setY(86);
		antennas.setText(Integer.toString(data.getAntennas()));
		antennas.setPriority(RenderPriority.Low);
		
		AddAntennaButton plus = new AddAntennaButton(this, player);
		plus.setX(154+25).setY(82);
		
		MinAntennaButton min = new MinAntennaButton(this, player);
		min.setX(154+25+20).setY(82);
		
		RouterApplyButton apply = new RouterApplyButton(this, player);
		apply.setX(154).setY(82+20);
		
		GenericPopup popup = new GenericPopup();
		player.getMainScreen().attachPopupScreen(popup);
		popup.attachWidgets(CCMain.instance, this.bg, conf_label, ssid_label, this.SSID, password_label, this.password, antenna_label, this.antennas, plus, min, apply);
	}

	public int getAmountOfAntennas() {
		return this.data.getAntennas();
	}
	
	public void setAmountOfAntennas(int amount) {
		this.data.setAntennas(amount);
		this.antennas.setText(Integer.toString(amount)).setDirty(true);
		CCMain.instance.getDatabase().save(this.data);
	}

	public void applyChanges() {
		if(this.SSID.getText().isEmpty()) this.player.sendMessage("¤4SSID was not applied: it was empty");
		else this.data.setSSID(SSID.getText());
		
		this.data.setPassword(password.getText());
		
		CCMain.instance.getDatabase().save(this.data);
	}
}
