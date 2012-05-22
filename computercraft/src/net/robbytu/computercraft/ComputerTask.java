package net.robbytu.computercraft;

import org.luaj.vm2.LuaTable;

public abstract interface ComputerTask {
	public abstract void execute(LuaTable lua, String ComputerID);
}
