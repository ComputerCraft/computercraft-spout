package net.robbytu.computercraft.gui;

import org.getspout.spoutapi.event.screen.ButtonClickEvent;
import org.getspout.spoutapi.gui.GenericButton;

public class SendButton extends GenericButton {
	private ComputerBlockGUI gui;
	
	public SendButton(ComputerBlockGUI gui) {
		super();
		this.gui = gui;
	}
	
	@Override
	public void onButtonClick(ButtonClickEvent event) {
		this.gui.sendInputToScript();
	}
}
