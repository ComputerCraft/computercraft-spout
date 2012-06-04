package net.robbytu.computercraft.computer;

import org.luaj.vm2.LuaTable;

public abstract interface ComputerTask {
	public abstract void execute(LuaTable lua, int id);
}
