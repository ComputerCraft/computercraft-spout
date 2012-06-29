package net.robbytu.computercraft.lib.spout;

import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;

import net.robbytu.computercraft.computer.ComputerThread;
import net.robbytu.computercraft.lib.LuaLib;

public class ColorLib extends LuaLib {
	public static final String YELLOW = "\u00A7e";
	public static final String PURPLE = "\u00A7d";
	public static final String RED = "\u00A7c";
	public static final String AQUA = "\u00A7b";
	public static final String GREEN = "\u00A7a";
	public static final String BLUE = "\u00A79";
	public static final String DARKGREY = "\u00A78";
	public static final String GRAY = "\u00A77";
	public static final String GOLD = "\u00A76";
	public static final String DARKPRUPLE = "\u00A75";
	public static final String DARKRED = "\u00A74";
	public static final String DARKAQUA = "\u00A73";
	public static final String DARKGREEN = "\u00A72";
	public static final String DARKBLUE = "\u00A71";
	public static final String BLACK = "\u00A70";

	public ColorLib() {
		super("color");
	}

	@Override
	public LuaValue init(ComputerThread computer, LuaValue env) {
		// color.* functions
		LuaTable color = new LuaTable();
		color.set("byString", new OneArgFunction() {
			public LuaValue call(LuaValue val) {
				return LuaValue.valueOf(ColorLib.this.byString(val.tojstring()));
			}
		});
		
		color.set("BLACK", BLACK);
		color.set("DARKBLUE", DARKBLUE);
		color.set("DARKGREEN", DARKGREEN);
		color.set("DARKAQUA", DARKAQUA);
		color.set("DARKRED", DARKRED);
		color.set("DARKPURPLE", DARKPRUPLE);
		color.set("GOLD", GOLD);
		color.set("GREY", GRAY);
		color.set("DARKGREY", DARKGREY);
		color.set("BLUE", BLUE);
		color.set("GREEN", GREEN);
		color.set("AQUA", AQUA);
		color.set("RED", RED);
		color.set("PURPLE", PURPLE);
		color.set("YELLOW", YELLOW);
		
		env.set("color", color);
		return color;
	}
	
	/**
	 * Gets a color code string from a color name.
	 * 
	 * @param name The name of the color, see the static fields of this class for valid names.
	 * @return The color code of the color for the given name.
	 */
	public String byString(String name) {
		String toReturn = "\u00A7f";
		name = name.toUpperCase();
		if(name.equals("BLACK")) toReturn = BLACK;
		if(name.equals("DARK_BLUE") || name.equals("DARKBLUE")) toReturn = DARKBLUE;
		if(name.equals("DARK_GREEN") || name.equals("DARKGREEN")) toReturn = DARKGREEN;
		if(name.equals("DARK_AQUA") || name.equals("DARKAQUA")) toReturn = DARKAQUA;
		if(name.equals("DARK_RED") || name.equals("DARKRED")) toReturn = DARKRED;
		if(name.equals("DARK_PURPLE") || name.equals("DARKPURPLE")) toReturn = DARKPRUPLE;
		if(name.equals("GOLD")) toReturn = GOLD;
		if(name.equals("GRAY")) toReturn = GRAY;
		if(name.equals("DARK_GRAY") || name.equals("DARKGRAY")) toReturn = DARKGREY;
		if(name.equals("BLUE")) toReturn = BLUE;
		if(name.equals("GREEN")) toReturn = GREEN;
		if(name.equals("AQUA")) toReturn = AQUA;
		if(name.equals("RED")) toReturn = RED;
		if(name.equals("PURPLE")) toReturn = PURPLE;
		if(name.equals("YELLOW")) toReturn = YELLOW;
		
		return toReturn;		
	}

}
