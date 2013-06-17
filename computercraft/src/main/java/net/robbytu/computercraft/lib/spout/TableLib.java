package net.robbytu.computercraft.lib.spout;

import net.robbytu.computercraft.computer.ComputerThread;
import net.robbytu.computercraft.lib.LuaLib;

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
public class TableLib extends LuaLib {

	public TableLib() {
		super("table");
		// TODO Auto-generated constructor stub
	}

	@Override
	public LuaValue init(ComputerThread computer, LuaValue env) {
		LuaTable t = new LuaTable();
		t.set("getn", new OneArgFunction() {
			@Override
			public LuaValue call(LuaValue arg) {
				return arg.checktable().getn();
			}
		});
		
		t.set("maxn", new OneArgFunction() {
			@Override
			public LuaValue call(LuaValue arg) {
				return valueOf(arg.checktable().maxn());
			}
		});
		
		t.set("remove", new VarArgFunction() {
			public Varargs invoke(Varargs args) {
				// "remove" (table [, pos]) -> removed-ele
				LuaTable table = args.checktable(1);
				int pos = args.narg()>1? args.checkint(2): 0;
				return table.remove(pos);
			}
		});
		
		t.set("concat", new VarArgFunction() {
			public Varargs invoke(Varargs args) {
				// "concat" (table [, sep [, i [, j]]]) -> string
				LuaTable table = args.checktable(1);
				return table.concat(
						args.optstring(2,LuaValue.EMPTYSTRING),
						args.optint(3,1),
						args.isvalue(4)? args.checkint(4): table.length() );
			}
		});
		
		t.set("insert", new VarArgFunction() {
			public Varargs invoke(Varargs args) {
				// "insert" (table, [pos,] value) -> prev-ele
				final LuaTable table = args.checktable(1);
				final int pos = args.narg()>2? args.checkint(2): 0;
				final LuaValue value = args.arg( args.narg()>2? 3: 2 );
				table.insert( pos, value );
				return NONE;
			}
		});
		
		t.set("sort", new VarArgFunction() {
			public Varargs invoke(Varargs args) {
				// "sort" (table [, comp]) -> void
				LuaTable table = args.checktable(1);
				LuaValue compare = (args.isnoneornil(2)? NIL: args.checkfunction(2));
				table.sort( compare );
				return NONE;
			}
		});
		
		t.set("foreach", new VarArgFunction() {
			public Varargs invoke(Varargs args) {
				// (table, func) -> void
				return args.checktable(1).foreach( args.checkfunction(2) );
			}
		});
		
		t.set("foreachi", new VarArgFunction() {
			public Varargs invoke(Varargs args) {
				// "foreachi" (table, func) -> void
				return args.checktable(1).foreachi( args.checkfunction(2) );
			}
		});
		
		env.set("table", t);
		return t;
	}
}
