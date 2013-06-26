package net.robbytu.computercraft.listener;

import net.robbytu.computercraft.gui.ComputerBlockGUI;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.getspout.spoutapi.event.input.KeyPressedEvent;
import org.getspout.spoutapi.event.screen.TextFieldChangeEvent;
import org.getspout.spoutapi.gui.GenericTextField;
import org.getspout.spoutapi.keyboard.Keyboard;

public class ComputerGuiListener implements Listener {
	
	@EventHandler
	public void onKeyPressed(KeyPressedEvent event) {
		if (event.getPlayer().getMainScreen().getActivePopup() instanceof ComputerBlockGUI) {
			if (event.getKey() == Keyboard.KEY_RETURN) {
				ComputerBlockGUI gui = (ComputerBlockGUI)event.getPlayer().getMainScreen().getActivePopup();
				gui.waitForText = true;
			}
		}
		
		return;
	}

	@EventHandler
	public void onTextFieldChanged(TextFieldChangeEvent event) {
		if (event.getTextField().getScreen() instanceof ComputerBlockGUI) {
			ComputerBlockGUI gui = (ComputerBlockGUI) event.getTextField().getScreen();
			if (gui.input == event.getTextField() && gui.waitForText) {
				gui.sendInputToScript(event.getNewText());
				gui.waitForText = false;
				gui.input.setText("");
				event.setCancelled(true);
			}
		}
	}
}
