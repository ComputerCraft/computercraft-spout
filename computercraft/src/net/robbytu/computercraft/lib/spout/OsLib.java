/*******************************************************************************
* Copyright (c) 2009 Luaj.org. All rights reserved.
*
* Permission is hereby granted, free of charge, to any person obtaining a copy
* of this software and associated documentation files (the "Software"), to deal
* in the Software without restriction, including without limitation the rights
* to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
* copies of the Software, and to permit persons to whom the Software is
* furnished to do so, subject to the following conditions:
*
* The above copyright notice and this permission notice shall be included in
* all copies or substantial portions of the Software.
* 
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
* IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
* FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
* AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
* LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
* OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
* THE SOFTWARE.
******************************************************************************/
package net.robbytu.computercraft.lib.spout;

import java.io.IOException;

import net.robbytu.computercraft.computer.ComputerThread;
import net.robbytu.computercraft.lib.LuaLib;

import org.bukkit.Location;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.VarArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;

/**
 * Subclass of {@link LuaLib} which implements the standard lua {@code os} library.
 *
 * @see <a href="http://www.lua.org/manual/5.1/manual.html#5.8">http://www.lua.org/manual/5.1/manual.html#5.8</a>
 */
public class OsLib extends LuaLib {
	public static String TMP_PREFIX    = ".luaj";
	public static String TMP_SUFFIX    = "/tmp/tmp";
	
	private static final long t0 = System.currentTimeMillis();
	private static long tmpnames = t0;
	private ComputerThread computer;

	/** 
	 * Create and OsLib instance.   
	 */
	public OsLib() {
		super("os");
	}

	@Override
	public LuaValue init(ComputerThread computer, LuaValue env) {
		this.computer = computer;
		LuaTable os = new LuaTable();
		os.set("clock", new ZeroArgFunction() {
			@Override
			public LuaValue call() {
				return valueOf(clock());
			}
		});
		
		os.set("date", new VarArgFunction() {
			public Varargs invoke(Varargs args) {
				String s = args.optjstring(1, null);
				double t = args.optdouble(2,-1);
				return valueOf( date(s, t==-1? System.currentTimeMillis()/1000.: t) );				
			};
		});
		
		os.set("difftime", new VarArgFunction() {
			public Varargs invoke(Varargs args) {
				return valueOf(difftime(args.checkdouble(1),args.checkdouble(2)));
			}
		});
		
		os.set("execute", new VarArgFunction() {
			@Override
			public Varargs invoke(Varargs args) {
				return valueOf(execute(args.optjstring(1, null)));
			}
		});
		
		os.set("exit", new VarArgFunction() {
			@Override
			public Varargs invoke(Varargs args) {
				exit(args.optint(1, 0));
				return NONE;
			}
		});
		
		os.set("getenv", new OneArgFunction() {
			@Override
			public LuaValue call(LuaValue arg) {
				final String val = getenv(arg.checkjstring());
				return val!=null? valueOf(val): NIL;
			}
		});
		
		os.set("remove", new VarArgFunction() {
			@Override
			public Varargs invoke(Varargs args) {
				try {
					remove(args.checkjstring(1));
				} catch (IOException e) {
					return varargsOf(NIL, valueOf(e.getMessage()));
				}
				return LuaValue.TRUE;
			}
		});
		
		os.set("rename", new VarArgFunction() {
			@Override
			public Varargs invoke(Varargs args) {
				try {
					rename(args.checkjstring(1), args.checkjstring(2));
				} catch ( IOException e ) {
					return varargsOf(NIL, valueOf(e.getMessage()));
				}
				
				return LuaValue.TRUE;
			}
		});
		
		os.set("setlocale", new VarArgFunction() {
			@Override
			public Varargs invoke(Varargs args) {
				String s = setlocale(args.optjstring(1,null), args.optjstring(2, "all"));
				return s!=null? valueOf(s): NIL;
			}
		});

		os.set("time", new VarArgFunction() {
			@Override
			public Varargs invoke(Varargs args) {
				return valueOf(time(args.arg1().isnil()? null: args.checktable(1)));
			}
		});
		
		os.set("tmpname", new ZeroArgFunction() {
			@Override
			public LuaValue call() {
				return valueOf(tmpname());
			}
		});
		
		os.set("computerID", new ZeroArgFunction() {
			public LuaValue call() {
				return valueOf(computerID());
			}
		});
		
		os.set("isWireless", new ZeroArgFunction() {
			public LuaValue call() {				
				return LuaValue.valueOf(isWireless());
			}
		});
		
		os.set("getComputerCoords", new ZeroArgFunction() {
			public LuaValue call() {
				return LuaValue.valueOf(getComputerCoords());
			}
		});
		
		os.set("shutdown", new ZeroArgFunction(env) {
			public LuaValue call() {
				// Do shutdown events, so that scripts might save configs for example
				env.get("event").get("triggerEvent").call(LuaValue.valueOf("shutdown"), LuaValue.NIL);
				
				throw new LuaError("Shutdown requested"); // FIXME: better then Thread.interrupt, but only returns to last pcall.
			}
		});
		
		env.set("os", os);
		return os;
	}

	public int computerID() {
		return computer.getID();
	}

	public boolean isWireless() {
		return computer.isWireless();
	}
	
	public String getComputerCoords() {
		Location pos = computer.getPosition();
		String result = pos.getBlockX() + "," + pos.getBlockY() + "," + pos.getBlockZ() + "," + pos.getWorld(); //TODO rewrite this to return VarArgs
		return result;
	}
	
	/**
	 * @return an approximation of the amount in seconds of CPU time used by 
	 * the program.
	 */
	protected double clock() {
		return (System.currentTimeMillis()-t0) / 1000.;
	}

	/**
	 * Returns the number of seconds from time t1 to time t2. 
	 * In POSIX, Windows, and some other systems, this value is exactly t2-t1.
	 * @param t2
	 * @param t1
	 * @return difference in time values, in seconds
	 */
	protected double difftime(double t2, double t1) {
		return t2 - t1;
	}

	/**
	 * If the time argument is present, this is the time to be formatted 
	 * (see the os.time function for a description of this value). 
	 * Otherwise, date formats the current time.
	 * 
	 * If format starts with '!', then the date is formatted in Coordinated 
	 * Universal Time. After this optional character, if format is the string 
	 * "*t", then date returns a table with the following fields: year 
	 * (four digits), month (1--12), day (1--31), hour (0--23), min (0--59), 
	 * sec (0--61), wday (weekday, Sunday is 1), yday (day of the year), 
	 * and isdst (daylight saving flag, a boolean).
	 * 
	 * If format is not "*t", then date returns the date as a string, 
	 * formatted according to the same rules as the C function strftime.
	 * 
	 * When called without arguments, date returns a reasonable date and 
	 * time representation that depends on the host system and on the 
	 * current locale (that is, os.date() is equivalent to os.date("%c")).
	 *  
	 * @param format 
	 * @param time time since epoch, or -1 if not supplied
	 * @return a LString or a LTable containing date and time, 
	 * formatted according to the given string format.
	 */
	protected String date(String format, double time) {
		return new java.util.Date((long)(time*1000)).toString();
	}

	/** 
	 * This function is equivalent to the C function system. 
	 * It passes command to be executed by an operating system shell. 
	 * It returns a status code, which is system-dependent. 
	 * If command is absent, then it returns nonzero if a shell 
	 * is available and zero otherwise.
	 * @param command command to pass to the system
	 */ 
	protected int execute(String command) {
		return 0; //TODO let shell execute this
	}

	/**
	 * Calls the C function exit, with an optional code, to terminate the host program. 
	 * @param code
	 */
	protected void exit(int code) {
		//System.exit(code); we need to implement something else here
	}

	/**
	 * Returns the value of the process environment variable varname, 
	 * or null if the variable is not defined. 
	 * @param varname
	 * @return String value, or null if not defined
	 */
	protected String getenv(String varname) {
		//return System.getProperty(varname);
		return ""; //TODO allow virtual system variables for each computer, that are remembered on restart
	}

	/**
	 * Deletes the file or directory with the given name. 
	 * Directories must be empty to be removed. 
	 * If this function fails, it throws and IOException
	 *  
	 * @param filename
	 * @throws IOException if it fails
	 */
	protected void remove(String filename) throws IOException {
		throw new IOException( "not implemented" ); //TODO map this to the FileSystem
	}

	/**
	 * Renames file or directory named oldname to newname. 
	 * If this function fails,it throws and IOException
	 *  
	 * @param oldname old file name
	 * @param newname new file name
	 * @throws IOException if it fails
	 */
	protected void rename(String oldname, String newname) throws IOException { //TODO map this to the FileSystem
		throw new IOException( "not implemented" ); //TODO map this to the FileSystem
	}

	/**
	 * Sets the current locale of the program. locale is a string specifying 
	 * a locale; category is an optional string describing which category to change: 
	 * "all", "collate", "ctype", "monetary", "numeric", or "time"; the default category 
	 * is "all". 
	 * 
	 * If locale is the empty string, the current locale is set to an implementation-
	 * defined native locale. If locale is the string "C", the current locale is set 
	 * to the standard C locale.
	 * 
	 * When called with null as the first argument, this function only returns the 
	 * name of the current locale for the given category.
	 *  
	 * @param locale
	 * @param category
	 * @return the name of the new locale, or null if the request 
	 * cannot be honored.
	 */
	protected String setlocale(String locale, String category) {
		return "C";
	}

	/**
	 * Returns the current time when called without arguments, 
	 * or a time representing the date and time specified by the given table. 
	 * This table must have fields year, month, and day, 
	 * and may have fields hour, min, sec, and isdst 
	 * (for a description of these fields, see the os.date function).
	 * @param table
	 * @return long value for the time
	 */
	protected long time(LuaTable table) {
		//TODO allow table argument to work
		return System.currentTimeMillis(); //TODO change this to be something minecraft related
	}

	/**
	 * Returns a string with a file name that can be used for a temporary file. 
	 * The file must be explicitly opened before its use and explicitly removed 
	 * when no longer needed.
	 * 
	 * On some systems (POSIX), this function also creates a file with that name, 
	 * to avoid security risks. (Someone else might create the file with wrong 
	 * permissions in the time between getting the name and creating the file.) 
	 * You still have to open the file to use it and to remove it (even if you 
	 * do not use it). 
	 * 
	 * @return String filename to use
	 */
	protected String tmpname() {
		synchronized ( OsLib.class ) {
			return TMP_PREFIX+(tmpnames++)+TMP_SUFFIX;
		}
	}
}
