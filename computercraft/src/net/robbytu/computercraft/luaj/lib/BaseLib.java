package net.robbytu.computercraft.luaj.lib;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import net.robbytu.computercraft.computer.ComputerThread;
import net.robbytu.computercraft.computer.FileManager;
import net.robbytu.computercraft.luaj.LuaInstance;

import org.luaj.vm2.LoadState;
import org.luaj.vm2.Lua;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaString;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaThread;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.VarArgFunction;

/**
 * Spout LuaJ BaseLib providing the functionalities the original BaseLib provides.
 * 
 * @author Markus Andree
 *
 */
public class BaseLib extends OneArgFunction {
	private ComputerThread computer;
	
	private LuaValue next;
	private LuaValue inext;
	
	private static final String[] LIB2_KEYS = {
		//"collectgarbage", // ( opt [,arg] ) -> value
		"error", // ( message [,level] ) -> ERR
		//"setfenv", // (f, table) -> void
	};
	private static final String[] LIBV_KEYS = {
		"assert", // ( v [,message] ) -> v, message | ERR
		"dofile", // ( filename ) -> result1, ...
		"getfenv", // ( [f] ) -> env
		"getmetatable", // ( object ) -> table 
		"load", // ( func [,chunkname] ) -> chunk | nil, msg
		"loadfile", // ( [filename] ) -> chunk | nil, msg
		"loadstring", // ( string [,chunkname] ) -> chunk | nil, msg
		"pcall", // (f, arg1, ...) -> status, result1, ...
		"xpcall", // (f, err) -> result1, ...
		"print", // (...) -> void
		"select", // (f, ...) -> value1, ...
		"unpack", // (list [,i [,j]]) -> result1, ...
		"type",  // (v) -> value
		"rawequal", // (v1, v2) -> boolean
		"rawget", // (table, index) -> value
		"rawset", // (table, index, value) -> table
		"setmetatable", // (table, metatable) -> table
		"tostring", // (e) -> value
		"tonumber", // (e [,base]) -> value
		"pairs", // "pairs" (t) -> iter-func, t, nil
		"ipairs", // "ipairs", // (t) -> iter-func, t, 0
		"next", // "next"  ( table, [index] ) -> next-index, next-value
		"__inext", // "inext" ( table, [int-index] ) -> next-index, next-value
	};
	
	public BaseLib(ComputerThread computer) {
		this.computer = computer;
	}

	@Override
	public LuaValue call(LuaValue arg) {
		env.set("_G", env);
		env.set( "_VERSION", Lua._VERSION );
		bind( env, BaseLib2.class, LIB2_KEYS );
		bind( env, BaseLibV.class, LIBV_KEYS ); 
		
		// inject base lib int vararg instances
		for ( int i=0; i<LIBV_KEYS.length; i++ ) 
			((BaseLibV) env.get(LIBV_KEYS[i])).baselib = this;
		
		return null;
	}
	
	public static final class BaseLib2 extends TwoArgFunction {
		@Override
		public LuaValue call(LuaValue arg1, LuaValue arg2) {
			switch (opcode) {
			case 0:
				throw new LuaError( arg1.isnil()? null: arg1.tojstring(), arg2.optint(1) );
			}

			return NIL;
		}
	}
	
	public static final class BaseLibV extends VarArgFunction {
		public BaseLib baselib;
		
		@Override
		public Varargs invoke(Varargs args) {
			switch ( opcode ) {
			case 0: // "assert", // ( v [,message] ) -> v, message | ERR
				if ( !args.arg1().toboolean() ) 
					error( args.narg()>1? args.optjstring(2,"assertion failed!"): "assertion failed!" );
				return args;
			case 1: // "dofile", // ( filename ) -> result1, ...
			{
				Varargs v = baselib.loadFile( args.checkjstring(1) );
				return v.isnil(1)? error(v.tojstring(2)): v.arg1().invoke();
			}
			case 2: // "getfenv", // ( [f] ) -> env
			{
				LuaValue f = LuaInstance.getActiveInstance().getBaseLib().getfenvobj(args.arg1());
			    LuaValue e = f.getfenv();
				return e!=null? e: NIL;
			}
			case 3: // "getmetatable", // ( object ) -> table
			{
				LuaValue mt = args.checkvalue(1).getmetatable();
				return mt!=null? mt.rawget(METATABLE).optvalue(mt): NIL;
			}
			case 4: // "load", // ( func [,chunkname] ) -> chunk | nil, msg
			{
				LuaValue func = args.checkfunction(1);
				String chunkname = args.optjstring(2, "function");
				return LuaInstance.getActiveInstance().getBaseLib().loadStream(LuaInstance.getActiveInstance().getBaseLib().new StringInputStream(func), chunkname);
			}
			case 5: // "loadfile", // ( [filename] ) -> chunk | nil, msg
			{
				return baselib.loadFile( args.checkjstring(1) );
			}
			case 6: // "loadstring", // ( string [,chunkname] ) -> chunk | nil, msg
			{
				LuaString script = args.checkstring(1);
				String chunkname = args.optjstring(2, "string");
				return LuaInstance.getActiveInstance().getBaseLib().loadStream(script.toInputStream(),chunkname);
			}
			case 7: // "pcall", // (f, arg1, ...) -> status, result1, ...
			{
				LuaValue func = args.checkvalue(1);
				LuaThread.onCall(this);
				try {
					return LuaInstance.getActiveInstance().getBaseLib().pcall(func,args.subargs(2),null);
				} finally {
					LuaThread.onReturn();
				}
			}
			case 8: // "xpcall", // (f, err) -> result1, ...				
			{
				LuaThread.onCall(this);
				try {
					return LuaInstance.getActiveInstance().getBaseLib().pcall(args.arg1(),NONE,args.checkvalue(2));
				} finally {
					LuaThread.onReturn();
				}
			}
			case 9: // "print", // (...) -> void
			{
				LuaValue tostring = LuaThread.getGlobals().get("tostring"); 
				StringBuilder text = new StringBuilder();
				for ( int i=1, n=args.narg(); i<=n; i++ ) {
					if ( i>1 ) text.append('\t');
					LuaString s = tostring.call( args.arg(i) ).strvalue();
					text.append(s.toString());
				}
				
				LuaInstance.getActiveInstance().getBaseLib().computer.print(text.toString());
				
				return NONE;
			}
			case 10: // "select", // (f, ...) -> value1, ...
			{
				int n = args.narg()-1; 				
				if ( args.arg1().equals(valueOf("#")) )
					return valueOf(n);
				int i = args.checkint(1);
				if ( i == 0 || i < -n )
					argerror(1,"index out of range");
				return args.subargs(i<0? n+i+2: i+1);
			}
			case 11: // "unpack", // (list [,i [,j]]) -> result1, ...
			{
				int na = args.narg();
				LuaTable t = args.checktable(1);
				int n = t.length();
				int i = na>=2? args.checkint(2): 1;
				int j = na>=3? args.checkint(3): n;
				n = j-i+1;
				if ( n<0 ) return NONE;
				if ( n==1 ) return t.get(i);
				if ( n==2 ) return varargsOf(t.get(i),t.get(j));
				LuaValue[] v = new LuaValue[n];
				for ( int k=0; k<n; k++ )
					v[k] = t.get(i+k);
				return varargsOf(v);
			}
			case 12: // "type",  // (v) -> value
				return valueOf(args.checkvalue(1).typename());
			case 13: // "rawequal", // (v1, v2) -> boolean
				return valueOf(args.checkvalue(1) == args.checkvalue(2));
			case 14: // "rawget", // (table, index) -> value
				return args.checktable(1).rawget(args.checkvalue(2));
			case 15: { // "rawset", // (table, index, value) -> table
				LuaTable t = args.checktable(1);
				t.rawset(args.checknotnil(2), args.checkvalue(3));
				return t;
			}
			case 16: { // "setmetatable", // (table, metatable) -> table
				final LuaValue t = args.arg1();
				final LuaValue mt0 = t.getmetatable();
				if ( mt0!=null && !mt0.rawget(METATABLE).isnil() )
					error("cannot change a protected metatable");
				final LuaValue mt = args.checkvalue(2);
				return t.setmetatable(mt.isnil()? null: mt.checktable());
			}
			case 17: { // "tostring", // (e) -> value
				LuaValue arg = args.checkvalue(1);
				LuaValue h = arg.metatag(TOSTRING);
				if ( ! h.isnil() ) 
					return h.call(arg);
				LuaValue v = arg.tostring();
				if ( ! v.isnil() ) 
					return v;
				return valueOf(arg.tojstring());
			}
			case 18: { // "tonumber", // (e [,base]) -> value
				LuaValue arg1 = args.checkvalue(1);
				final int base = args.optint(2,10);
				if (base == 10) {  /* standard conversion */
					return arg1.tonumber();
				} else {
					if ( base < 2 || base > 36 )
						argerror(2, "base out of range");
					return arg1.checkstring().tonumber(base);
				}
			}
			case 19: // "pairs" (t) -> iter-func, t, nil
				return varargsOf( baselib.next, args.checktable(1), NIL );
			case 20: // "ipairs", // (t) -> iter-func, t, 0
				return varargsOf( baselib.inext, args.checktable(1), ZERO );
			case 21: // "next"  ( table, [index] ) -> next-index, next-value
				return args.checktable(1).next(args.arg(2));
			case 22: // "inext" ( table, [int-index] ) -> next-index, next-value
				return args.checktable(1).inext(args.arg(2));
			}
			return NONE;
		}
	}

	public Varargs pcall(LuaValue func, Varargs args, LuaValue errfunc) {
		try {
			LuaThread thread = LuaThread.getRunning();
			LuaValue olderr = thread.err;
			try {
				thread.err = errfunc;
				return varargsOf(LuaValue.TRUE, func.invoke(args));
			} finally {
				thread.err = olderr;
			}
		} catch ( LuaError le ) {
			String m = le.getMessage();
			return varargsOf(FALSE, m!=null? valueOf(m): NIL);
		} catch ( Exception e ) {
			String m = e.getMessage();
			return varargsOf(FALSE, valueOf(m!=null? m: e.toString()));
		}
	}
	
	/** 
	 * Load from a named file, returning the chunk or nil,error of can't load
	 * 
	 * This method was changed to be secured by the FileManager class, when accessing a file.
	 * 
	 * @return Varargs containing chunk, or NIL,error-text on error
	 */
	public Varargs loadFile(String filename) {
		File f = FileManager.get(filename, computer.getID());
		InputStream is = null;
		if ( f == null )
			return varargsOf(NIL, valueOf("cannot open "+filename+": No such file or directory"));
		try {
			is = new FileInputStream(f);
		} catch (FileNotFoundException e) {
			return varargsOf(NIL, valueOf("cannot open "+filename+": No such file or directory"));
		}
		
		try {
			return loadStream(is, "@"+filename);
		} finally {
			try {
				is.close();
			} catch ( Exception e ) {
				e.printStackTrace();
			}
		}
	}

	public Varargs loadStream(InputStream is, String chunkname) {
		try {
			if ( is == null )
				return varargsOf(NIL, valueOf("not found: "+chunkname));
			return LoadState.load(is, chunkname, LuaThread.getGlobals());
		} catch (Exception e) {
			return varargsOf(NIL, valueOf(e.getMessage()));
		}
	}
	
	private LuaValue getfenvobj(LuaValue arg) {
		if ( arg.isfunction() )
			return arg;
		int level = arg.optint(1);
	    arg.argcheck(level>=0, 1, "level must be non-negative");
		if ( level == 0 )
			return LuaThread.getRunning();
		LuaValue f = LuaThread.getCallstackFunction(level);
	    arg.argcheck(f != null, 1, "invalid level");
	    return f;
	}
	
	private class StringInputStream extends InputStream {
		LuaValue func;
		byte[] bytes; 
		int offset;
		StringInputStream(LuaValue func) {
			this.func = func;
		}
		public int read() throws IOException {
			if ( func == null ) return -1;
			if ( bytes == null ) {
				LuaValue s = func.call();
				if ( s.isnil() ) {
					func = null;
					bytes = null;
					return -1;
				}
				bytes = s.tojstring().getBytes();
				offset = 0;
			}
			if ( offset >= bytes.length )
				return -1;
			return bytes[offset++];
			
		}
	}
}
