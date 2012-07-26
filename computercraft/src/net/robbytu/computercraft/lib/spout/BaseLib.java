package net.robbytu.computercraft.lib.spout;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

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

import net.robbytu.computercraft.computer.ComputerThread;
import net.robbytu.computercraft.computer.FileManager;
import net.robbytu.computercraft.lib.LuaLib;

public class BaseLib extends LuaLib {
	private ComputerThread computer;
	private LuaValue next;
	private LuaValue inext;
	private boolean shutdown;
	
	public BaseLib() {
		super("base");
	}

	@Override
	public LuaValue init(ComputerThread computer, LuaValue env) {
		env.set("_G", env);
		env.set( "_VERSION", Lua._VERSION );
		this.computer = computer;
		env.set("error", new TwoArgFunction() {
			@Override
			public LuaValue call(LuaValue arg1, LuaValue arg2) {
				BaseLib.this.error( arg1.isnil()? null : arg1.tojstring(), arg2.optint(1) );
				return null;
			}
		});
		
		env.set("assert", new VarArgFunction() {
			@Override
			public Varargs invoke(Varargs args) {
				if ( !args.arg1().toboolean() ) 
					error( args.narg()>1? args.optjstring(2,"assertion failed!"): "assertion failed!" );
				return args;
			}
		});

		env.set("dofile", new VarArgFunction() {
			@Override
			public Varargs invoke(Varargs args) {
				Varargs v = loadFile( args.checkjstring(1) );
				return v.isnil(1)? error(v.tojstring(2)): v.arg1().invoke();
			}
		});
		
		env.set("getfenv", new VarArgFunction() {
			@Override
			public Varargs invoke(Varargs args) {
				LuaValue f = getfenvobj(args.arg1());
			    LuaValue e = f.getfenv();
				return e!=null? e: NIL;
			}
		});
		
		env.set("getmetatable", new VarArgFunction() {
			@Override
			public Varargs invoke(Varargs args) {
				LuaValue mt = args.checkvalue(1).getmetatable();
				return mt!=null? mt.rawget(METATABLE).optvalue(mt): NIL;
			}
		});
		
		env.set("load", new VarArgFunction() {
			@Override
			public Varargs invoke(Varargs args) {
				LuaValue func = args.checkfunction(1);
				String chunkname = args.optjstring(2, "function");
				return loadStream(new StringInputStream(func), chunkname);
			}
		});
		
		env.set("loadfile", new VarArgFunction() {
			@Override
			public Varargs invoke(Varargs args) {
				return loadFile( args.checkjstring(1) );
			}
		});
		
		env.set("loadstring", new VarArgFunction() {
			@Override
			public Varargs invoke(Varargs args) {
				LuaString script = args.checkstring(1);
				String chunkname = args.optjstring(2, "string");
				return loadStream(script.toInputStream(),chunkname);
			}
		});
		
		env.set("pcall", new VarArgFunction() {
			@Override
			public Varargs invoke(Varargs args) {
				LuaValue func = args.checkvalue(1);
				LuaThread.onCall(this);
				try {
					return pcall(func,args.subargs(2),null);
				} finally {
					LuaThread.onReturn();
				}
			}
		});
		
		env.set("xpcall", new VarArgFunction() {
			@Override
			public Varargs invoke(Varargs args) {
				LuaThread.onCall(this);
				try {
					return pcall(args.arg1(),NONE,args.checkvalue(2));
				} finally {
					LuaThread.onReturn();
				}
			}
		});
		
		env.set("print", new VarArgFunction() {
			@Override
			public Varargs invoke(Varargs args) { 
				StringBuilder text = new StringBuilder();
				for ( int i=1, n=args.narg(); i<=n; i++ ) {
					if ( i>1 ) text.append('\t');
					LuaString s = BaseLib.this.tostring( args.arg(i) ).strvalue();
					text.append(s.tojstring());
				}
				
				BaseLib.this.computer.print(text.toString());
				return NIL;
			}
		});
		
		env.set("select", new VarArgFunction() {
			@Override
			public Varargs invoke(Varargs args) {
				int n = args.narg()-1; 				
				if ( args.arg1().equals(valueOf("#")) )
					return valueOf(n);
				int i = args.checkint(1);
				if ( i == 0 || i < -n )
					argerror(1,"index out of range");
				return args.subargs(i<0? n+i+2: i+1);
			}
		});
		
		env.set("tostring", new VarArgFunction() {
			@Override
			public Varargs invoke(Varargs args) {
				return BaseLib.this.tostring(args);
			}
		});
		
		env.set("unpack", new VarArgFunction() {
			@Override
			public Varargs invoke(Varargs args) {
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
		});
		
		env.set("type", new VarArgFunction() {
			@Override
			public Varargs invoke(Varargs args) {
				return valueOf(args.checkvalue(1).typename());
			}
		});
		
		env.set("rawequal", new VarArgFunction() {
			@Override
			public Varargs invoke(Varargs args) {
				return valueOf(args.checkvalue(1) == args.checkvalue(2));
			}
		});
		
		env.set("rawget", new VarArgFunction() {
			@Override
			public Varargs invoke(Varargs args) {
				return args.checktable(1).rawget(args.checkvalue(2));
			}
		});
		
		env.set("rawset", new VarArgFunction() {
			@Override
			public Varargs invoke(Varargs args) {
				LuaTable t = args.checktable(1);
				t.rawset(args.checknotnil(2), args.checkvalue(3));
				return t;
			}
		});
		
		env.set("setmetatable", new VarArgFunction() {
			@Override
			public Varargs invoke(Varargs args) {
				final LuaValue t = args.arg1();
				final LuaValue mt0 = t.getmetatable();
				if ( mt0!=null && !mt0.rawget(METATABLE).isnil() )
					error("cannot change a protected metatable");
				final LuaValue mt = args.checkvalue(2);
				return t.setmetatable(mt.isnil()? null: mt.checktable());
			}
		});
		
		env.set("tonumber", new VarArgFunction() {
			@Override
			public Varargs invoke(Varargs args) {
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
		});
		
		env.set("pairs", new VarArgFunction() {
			@Override
			public Varargs invoke(Varargs args) {
				return varargsOf( next, args.checktable(1), NIL );
			}
		});
		
		env.set("ipairs", new VarArgFunction() {
			@Override
			public Varargs invoke(Varargs args) {
				return varargsOf( inext, args.checktable(1), ZERO );
			}
		});
		
		env.set("next", new VarArgFunction() {
			@Override
			public Varargs invoke(Varargs args) {
				return args.checktable(1).next(args.arg(2));
			}
		});
		
		env.set("__inext", new VarArgFunction() {
			@Override
			public Varargs invoke(Varargs args) {
				return args.checktable(1).inext(args.arg(2));
			}
		});
		
		env.set("write", new OneArgFunction(env) {
            public LuaValue call(LuaValue val) {
            	BaseLib.this.computer.getGui().addEntry(val.toString());
                return LuaValue.NIL;
            }
		});
		
		env.set("writeline", new OneArgFunction(env) {
            public LuaValue call(LuaValue val) {
            	BaseLib.this.computer.getGui().addEntry(val.toString());
                return LuaValue.NIL;
            }
		});
		
		return null;
	}
	
	public void error(String msg, int level) {
		throw new LuaError( msg, level );
	}
	
	public LuaValue tostring(Varargs args) {
		LuaValue arg = args.checkvalue(1);
		LuaValue h = arg.metatag(LuaValue.TOSTRING);
		if ( ! h.isnil() ) 
			return h.call(arg);
		LuaValue v = arg.tostring();
		if ( ! v.isnil() ) 
			return v;
		return LuaValue.valueOf(arg.tojstring());
		
	}
	
	public Varargs pcall(LuaValue func, Varargs args, LuaValue errfunc) {
		try {
			LuaThread thread = LuaThread.getRunning();
			LuaValue olderr = thread.err;
			try {
				thread.err = errfunc;
				return LuaValue.varargsOf(LuaValue.TRUE, func.invoke(args));
			} finally {
				thread.err = olderr;
			}
		} catch ( LuaError le ) {
			String m = le.getMessage();
			OsLib os = computer.getLib("os");
			if (os != null && os.isShuttingDown()) 
				throw new LuaError("Shutdown requested");
			return LuaValue.varargsOf(LuaValue.FALSE, m!=null? LuaValue.valueOf(m) : LuaValue.NIL);
		} catch ( Exception e ) {
			String m = e.getMessage();
			return LuaValue.varargsOf(LuaValue.FALSE,LuaValue. valueOf(m!=null? m: e.toString()));
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
			return LuaValue.varargsOf(LuaValue.NIL, LuaValue.valueOf("cannot open "+filename+": No such file or directory"));
		try {
			is = new FileInputStream(f);
		} catch (FileNotFoundException e) {
			return LuaValue.varargsOf(LuaValue.NIL, LuaValue.valueOf("cannot open "+filename+": No such file or directory"));
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
				return LuaValue.varargsOf(LuaValue.NIL, LuaValue.valueOf("not found: "+chunkname));
			return LoadState.load(is, chunkname, LuaThread.getGlobals());
		} catch (Exception e) {
			return LuaValue.varargsOf(LuaValue.NIL, LuaValue.valueOf(e.getMessage()));
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
