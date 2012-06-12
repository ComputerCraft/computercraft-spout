package net.robbytu.computercraft.luaj.lib;

import net.robbytu.computercraft.luaj.LuaInstance;

import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.VarArgFunction;

/**
 * The TableLib is a rewrite of the original TableLib, that use the new PackageLib
 * 
 * @author Markus Andree
 */
public class TableLib extends OneArgFunction {

	private LuaTable init() {
		LuaTable t = new LuaTable();
		bind(t, TableLib.class, new String[] { "getn", "maxn", }, 1 );
		bind(t, TableLibV.class, new String[] {
			"remove", "concat", "insert", "sort", "foreach", "foreachi", } );
		env.set("table", t);
		LuaInstance.getActiveInstance().getPackageLib().LOADED.set("table", t);
		return t;
	}
	
	public LuaValue call(LuaValue arg) {
		switch ( opcode ) {
		case 0: // init library
			return init();
		case 1:  // "getn" (table) -> number
			return arg.checktable().getn();
		case 2: // "maxn"  (table) -> number 
			return valueOf( arg.checktable().maxn());
		}
		return NIL;
	}

	public static final class TableLibV extends VarArgFunction {
		public Varargs invoke(Varargs args) {
			switch ( opcode ) {
			case 0: { // "remove" (table [, pos]) -> removed-ele
				LuaTable table = args.checktable(1);
				int pos = args.narg()>1? args.checkint(2): 0;
				return table.remove(pos);
			}
			case 1: { // "concat" (table [, sep [, i [, j]]]) -> string
				LuaTable table = args.checktable(1);
				return table.concat(
						args.optstring(2,LuaValue.EMPTYSTRING),
						args.optint(3,1),
						args.isvalue(4)? args.checkint(4): table.length() );
			}
			case 2: { // "insert" (table, [pos,] value) -> prev-ele
				final LuaTable table = args.checktable(1);
				final int pos = args.narg()>2? args.checkint(2): 0;
				final LuaValue value = args.arg( args.narg()>2? 3: 2 );
				table.insert( pos, value );
				return NONE;
			}
			case 3: { // "sort" (table [, comp]) -> void
				LuaTable table = args.checktable(1);
				LuaValue compare = (args.isnoneornil(2)? NIL: args.checkfunction(2));
				table.sort( compare );
				return NONE;
			}
			case 4: { // (table, func) -> void
				return args.checktable(1).foreach( args.checkfunction(2) );
			}
			case 5: { // "foreachi" (table, func) -> void
				return args.checktable(1).foreachi( args.checkfunction(2) );
			}
			}
			return NONE;
		}
	}
}
