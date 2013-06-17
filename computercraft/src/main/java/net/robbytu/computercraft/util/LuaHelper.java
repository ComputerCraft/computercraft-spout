package net.robbytu.computercraft.util;

import java.util.List;

import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;

public class LuaHelper {
	public static LuaTable stringListToLuaTable(List<String> list) {
		LuaTable table = new LuaTable();
		
		for (String str : list) {
			table.add(LuaValue.valueOf(str));
		}
		
		return table;
	}
}
