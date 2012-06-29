package net.robbytu.computercraft.lib.spout;

import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;

import net.robbytu.computercraft.computer.ComputerThread;
import net.robbytu.computercraft.gui.ComputerBlockGUI;
import net.robbytu.computercraft.lib.LuaLib;

public class TerminalLib extends LuaLib {
	private ComputerThread computer;

	public TerminalLib() {
		super("term");
	}

	@Override
	public LuaValue init(ComputerThread computer, LuaValue env) {
		this.computer = computer;
		LuaTable term = new LuaTable();
		term.set("clear", new ZeroArgFunction() {
			public LuaValue call() {
				clear();
				return LuaValue.NIL;
			}
		});
		
		term.set("setInputTip", new OneArgFunction() {
			public LuaValue call(LuaValue val) {
				setInputTip(val.tojstring());
				return LuaValue.NIL;
			}
		});

		term.set("setInputPasswordField", new OneArgFunction() {
			public LuaValue call(LuaValue val) {
				setInputPasswordField(val.toboolean());
				return LuaValue.NIL;
			}
		});
		
		term.set("getInput", new ZeroArgFunction() {
			public LuaValue call() {
				try {
					return LuaValue.valueOf(getInput());
				} catch (InterruptedException e) {
					return LuaValue.valueOf("ERR_THREAD_INTERUPTION"); // change this to use the default way of returning errors in lua (return two parameters, first nil and second the error)
				}
			}
		});
		
		env.set("term", term);
		return term;
	}
	
	/**
	 * Clears the terminal of the computer associated to this lib instance.
	 */
	public void clear() {
		computer.getGui().clearConsole();		
	}
	
	/**
	 * Sets the terminal input tooltip of the computer associated to this lib instance.
	 * 
	 * @param text The text to set as the tip.
	 */
	public void setInputTip(String text) {
		computer.getGui().input.setPlaceholder(ColorLib.DARKGREY + text);		
	}

	/**
	 * Enables or disables asterisk input for the terminal of the computer associated to this lib instance.
	 * @param value If true, user input is hidden by asterisk, on false it is display plaintext.
	 */
	public void setInputPasswordField(boolean value) {
		computer.getGui().input.setPasswordField(value);
	}
	
	/**
	 * Reads input from the terminal of the computer associated to this lib instance.
	 * 
	 * @return The typed input of the user.
	 */
	public String getInput() throws InterruptedException {
		ComputerBlockGUI gui = computer.getGui();
		gui.buttonClicked = false;
		gui.input.setEnabled(true);
		
		String inp = "";
		//while(inp.equals("")) {
		while(!gui.buttonClicked) {
			//inp = gui.inputBuffer;
			try {
				Thread.sleep(100); // Don't remove. If you do, your CPU is not going to be happy with you
			} catch (InterruptedException e) {
				gui.inputBuffer = "";
				gui.input.setEnabled(false);
				throw e;
			}
		}

		inp = gui.inputBuffer;
						
		gui.inputBuffer = "";
		gui.input.setEnabled(false);
		
		return inp;		
	}
}
