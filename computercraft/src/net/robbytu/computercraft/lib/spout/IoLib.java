package net.robbytu.computercraft.lib.spout;

import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.RandomAccessFile;

import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaString;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.VarArgFunction;

import net.robbytu.computercraft.computer.ComputerThread;
import net.robbytu.computercraft.computer.FileManager;
import net.robbytu.computercraft.lib.LuaLib;

/**
 * The IoLib replaces the default IoLib and integrates additional
 * functions for Computercraft.
 * 
 * @author Markus Andree
 */
public class IoLib extends LuaLib {
	private ComputerThread computer;
	private File infile  = null;
	private File outfile = null;
	private File errfile = null;

	private static final LuaValue STDIN       = LuaValue.valueOf("stdin");
	private static final LuaValue STDOUT      = LuaValue.valueOf("stdout");
	private static final LuaValue STDERR      = LuaValue.valueOf("stderr");		
	private static final LuaValue FILE        = LuaValue.valueOf("file");
	private static final LuaValue CLOSED_FILE = LuaValue.valueOf("closed file");
	
	public IoLib() {
		super("io");
	}
	
	protected File wrapStdin() throws IOException {
		notimplemented();
		return null;
	}
	
	protected File wrapStdout() throws IOException {
		notimplemented();
		return null;
	}
	
	protected File openFile( String filename, boolean readMode, boolean appendMode, boolean updateMode, boolean binaryMode ) throws IOException {
		RandomAccessFile f = new RandomAccessFile(filename,readMode? "r": "rw");
		if ( appendMode ) {
			f.seek(f.length());
		} else {
			if ( ! readMode )
				f.setLength(0);
		}
		return new File(f, computer );
	}
	
	protected File openProgram(String prog, String mode) throws IOException {
		notimplemented();
		return null;
		
		/*final Process p = Runtime.getRuntime().exec(prog);
		return "w".equals(mode)? 
				new File( p.getOutputStream() ):  
				new File( p.getInputStream() );*/ 
	}

	protected File tmpFile() throws IOException {
		notimplemented();
		return null;
		/*java.io.File f = java.io.File.createTempFile(".luaj","bin");
		f.deleteOnExit();
		return new File( new RandomAccessFile(f,"rw") );*/
	}
	
	private static void notimplemented() {
		throw new LuaError("not implemented");
	}

	@Override
	public LuaTable init(ComputerThread computer, LuaValue env) {
		this.computer = computer;
		LuaTable io = new LuaTable();
		
		io.set("mkFile", new OneArgFunction() {
			public LuaValue call(LuaValue val) {
				return LuaValue.valueOf(mkFile(val.tojstring()));
			}
		});
		
		io.set("flush", new VarArgFunction() {
			@Override
			public Varargs invoke(Varargs args) {
				try {
					return _io_flush();
				} catch (IOException e) {
					return errorresult(e);
				}
			}
		});
		
		io.set("tmpfile", new VarArgFunction() {
			@Override
			public Varargs invoke(Varargs args) {
				try {
					return _io_tmpfile();
				} catch (IOException e) {
					return errorresult(e);
				}
			}
		});
		
		io.set("close", new VarArgFunction() {
			@Override
			public Varargs invoke(Varargs args) {
				try {
					return _io_close(args.arg1());
				} catch (IOException e) {
					return errorresult(e);
				}
			}
		});
		
		io.set("input", new VarArgFunction() {
			@Override
			public Varargs invoke(Varargs args) {
				return _io_input(args.arg1());
			}
		});
		
		io.set("output", new VarArgFunction() {
			@Override
			public Varargs invoke(Varargs args) {
				return _io_output(args.arg1());
			}
		});
		
		io.set("type", new VarArgFunction() {
			@Override
			public Varargs invoke(Varargs args) {
				return _io_type(args.arg1());
			}
		});
		
		io.set("popen", new VarArgFunction() {
			@Override
			public Varargs invoke(Varargs args) {
				try {
					return _io_popen(args.checkjstring(1),args.optjstring(2,"r"));
				} catch (IOException e) {
					return errorresult(e);
				}
			}
		});
		
		io.set("open", new VarArgFunction() {
			@Override
			public Varargs invoke(Varargs args) {
				try {
					return _io_open(args.checkjstring(1), args.optjstring(2,"r"));
				} catch (IOException e) {
					return errorresult(e);
				}
			}
		});
		
		io.set("lines", new VarArgFunction() {
			@Override
			public Varargs invoke(Varargs args) {
				return _io_lines(args.isvalue(1)? args.checkjstring(1): null);
			}
		});
		
		io.set("read", new VarArgFunction() {
			@Override
			public Varargs invoke(Varargs args) {
				try {
					return _io_read(args);
				} catch (IOException e) {
					return errorresult(e);
				}
			}
		});
		
		io.set("write", new VarArgFunction() {
			@Override
			public Varargs invoke(Varargs args) {
				try {
					return _io_write(args);
				} catch (IOException e) {
					return errorresult(e);
				}
			}
		});
		

		// set up file metatable
		LuaTable mt = new LuaTable();
		mt.set( "__index", new VarArgFunction() {
			public Varargs invoke(Varargs args) {
				return _io_index(args.arg(2));
			};
		} );
		
		io.setmetatable( mt );
		
		env.set(getName(), io);
		return io;
	}

	LuaValue generateFileMethods(final File file) {
		LuaTable methods = new LuaTable();
		
		methods.set("close", new VarArgFunction() {
			@Override
			public Varargs invoke(Varargs args) {
				try {
					return _file_close(args.arg1());
				} catch (IOException e) {
					return errorresult(e);
				}
			}
		});

		methods.set("flush", new VarArgFunction() {
			@Override
			public Varargs invoke(Varargs args) {
				try {
					return _file_flush(args.arg1());
				} catch (IOException e) {
					return errorresult(e);
				}
			}
		});

		methods.set("setvbuf", new VarArgFunction() {
			@Override
			public Varargs invoke(Varargs args) {
				return _file_setvbuf(args.arg1(),args.checkjstring(2),args.optint(3,1024));
			}
		});
		
		methods.set("lines", new VarArgFunction() {
			@Override
			public Varargs invoke(Varargs args) {
				return _file_lines(args.arg1());
			}
		});
		
		methods.set("read", new VarArgFunction() {
			@Override
			public Varargs invoke(Varargs args) {
				try {
					return _file_read(args.arg1(),args.subargs(2));
				} catch (IOException e) {
					return errorresult(e);
				}
			}
		});
		
		methods.set("seek", new VarArgFunction() {
			@Override
			public Varargs invoke(Varargs args) {
				try {
					return _file_seek(args.arg1(),args.optjstring(2,"cur"),args.optint(3,0));
				} catch (IOException e) {
					return errorresult(e);
				}
			}
		});
		
		methods.set("write", new VarArgFunction() {
			@Override
			public Varargs invoke(Varargs args) {
				try {
					return _file_write(args.arg1(),args.subargs(2));
				} catch (IOException e) {
					return errorresult(e);
				}
			}
		});
		
		return methods;
	}		
	
	public boolean mkFile(String path) {
		java.io.File newFile = FileManager.toRealPath(path, computer.getID());
		if (newFile != null) {
			if (!newFile.exists()) {
				try {
					return newFile.createNewFile();
				} 
				catch (IOException e) { }
			}
		}
		
		return false;
	}
	
	private File input() {
		return infile!=null? infile: (infile=ioopenfile("-","r"));
	}
	
	//	io.flush() -> bool 
	public Varargs _io_flush() throws IOException {
		checkopen(output());
		outfile.flush();
		return LuaValue.TRUE;
	}

	//	io.tmpfile() -> file
	public Varargs _io_tmpfile() throws IOException {
		return tmpFile();
	}

	//	io.close([file]) -> void
	public Varargs _io_close(LuaValue file) throws IOException {
		File f = file.isnil()? output(): checkfile(file);
		checkopen(f);
		return ioclose(f);
	}

	//	io.input([file]) -> file
	public Varargs _io_input(LuaValue file) {
		infile = file.isnil()? input(): 
				file.isstring()? ioopenfile(file.checkjstring(),"r"):
				checkfile(file);
		return infile;
	}

	// io.output(filename) -> file
	public Varargs _io_output(LuaValue filename) {
		outfile = filename.isnil()? output(): 
				  filename.isstring()? ioopenfile(filename.checkjstring(),"w"):
				  checkfile(filename);
		return outfile;
	}

	//	io.type(obj) -> "file" | "closed file" | nil
	public Varargs _io_type(LuaValue obj) {
		File f = optfile(obj);
		return f!=null?
			f.isclosed()? CLOSED_FILE: FILE: LuaValue.NIL;
	}

	// io.popen(prog, [mode]) -> file
	public Varargs _io_popen(String prog, String mode) throws IOException {
		return openProgram(prog, mode);
	}

	//	io.open(filename, [mode]) -> file | nil,err
	public Varargs _io_open(String filename, String mode) throws IOException {
		return rawopenfile(filename, mode);
	}

	//	io.lines(filename) -> iterator
	public Varargs _io_lines(String filename) {
		infile = filename==null? input(): ioopenfile(filename,"r");
		checkopen(infile);
		return lines(infile);
	}

	//	io.read(...) -> (...)
	public Varargs _io_read(Varargs args) throws IOException {
		checkopen(input());
		return ioread(infile,args);
	}

	//	io.write(...) -> void
	public Varargs _io_write(Varargs args) throws IOException {
		checkopen(output());
		return iowrite(outfile,args);
	}

	// file:close() -> void
	public Varargs _file_close(LuaValue file) throws IOException {
		return ioclose(checkfile(file));
	}

	// file:flush() -> void
	public Varargs _file_flush(LuaValue file) throws IOException {
		checkfile(file).flush();
		return LuaValue.TRUE;
	}

	// file:setvbuf(mode,[size]) -> void
	public Varargs _file_setvbuf(LuaValue file, String mode, int size) {
		checkfile(file).setvbuf(mode,size);
		return LuaValue.TRUE;
	}

	// file:lines() -> iterator
	public Varargs _file_lines(LuaValue file) {
		return lines(checkfile(file));
	}

	//	file:read(...) -> (...)
	public Varargs _file_read(LuaValue file, Varargs subargs) throws IOException {
		return ioread(checkfile(file),subargs);
	}

	//  file:seek([whence][,offset]) -> pos | nil,error
	public Varargs _file_seek(LuaValue file, String whence, int offset) throws IOException {
		return LuaValue.valueOf( checkfile(file).seek(whence,offset) );
	}

	//	file:write(...) -> void		
	public Varargs _file_write(LuaValue file, Varargs subargs) throws IOException {
		return iowrite(checkfile(file),subargs);
	}

	// __index, returns a field
	public Varargs _io_index(LuaValue v) {
		return v.equals(STDOUT)?output():
			   v.equals(STDIN)?  input():
			   v.equals(STDERR)? errput(): LuaValue.NIL;
	}

	//	lines iterator(s,var) -> var'
	public Varargs _lines_iter(LuaValue file) throws IOException {
		return freadline(checkfile(file));
	}

	private File output() {
		return outfile!=null? outfile: (outfile=ioopenfile("-","w"));
	}
	
	private File errput() {
		return errfile!=null? errfile: (errfile=ioopenfile("-","w"));
	}
	
	private File ioopenfile(String filename, String mode) {
		try {
			return rawopenfile(filename, mode);
		} catch ( Exception e ) {
			LuaValue.error("io error: "+e.getMessage());
			return null;
		}
	}

	private static Varargs ioclose(File f) throws IOException {
		if ( f.isstdfile() )
			return errorresult("cannot close standard file");
		else {
			f.close();
			return successresult();
		}
	}

	private static Varargs successresult() {
		return LuaValue.TRUE;
	}

	private static Varargs errorresult(Exception ioe) {
		String s = ioe.getMessage();		
		return errorresult("io error: "+(s!=null? s: ioe.toString()));
	}
	
	private static Varargs errorresult(String errortext) {
		return LuaValue.varargsOf(LuaValue.NIL, LuaValue.valueOf(errortext));
	}

	private Varargs lines(final File f) {
		try {
			return new VarArgFunction(f) {
				@Override
				public Varargs invoke(Varargs args) {
					try {
						return _lines_iter(env);
					} catch (IOException e) {
						return errorresult(e);
					}
				}
			};
		} catch ( Exception e ) {
			return LuaValue.error("lines: "+e);
		}
	}

	private static Varargs iowrite(File f, Varargs args) throws IOException {
		for ( int i=1, n=args.narg(); i<=n; i++ )
			f.write( args.checkstring(i) );
		return LuaValue.TRUE;
	}

	private Varargs ioread(File f, Varargs args) throws IOException {
		int i,n=args.narg();
		LuaValue[] v = new LuaValue[n];
		LuaValue ai,vi;
		LuaString fmt;
		for ( i=0; i<n; ) {
			item: switch ( (ai = args.arg(i+1)).type() ) {
				case LuaValue.TNUMBER:
					vi = freadbytes(f,ai.toint());
					break item;
				case LuaValue.TSTRING:
					fmt = ai.checkstring();
					if ( fmt.m_length == 2 && fmt.m_bytes[fmt.m_offset] == '*' ) {
						switch ( fmt.m_bytes[fmt.m_offset+1] ) {
						case 'n': vi = freadnumber(f); break item;
						case 'l': vi = freadline(f); break item;
						case 'a': vi = freadall(f); break item;
						}
					}
				default: 
					return LuaValue.argerror( i+1, "(invalid format)" ); 
			}
			if ( (v[i++] = vi).isnil() )
				break;
		}
		return i==0? LuaValue.NIL: LuaValue.varargsOf(v, 0, i);
	}

	private static File checkfile(LuaValue val) {
		File f = optfile(val);
		if ( f == null )
			LuaValue.argerror(1,"file");
		checkopen( f );
		return f;
	}
	
	private static File optfile(LuaValue val) {
		return (val instanceof File)? (File) val: null;
	}
	
	private static File checkopen(File file) {
		if ( file.isclosed() )
			LuaValue.error("attempt to use a closed file");
		return file;
	}
	
	private File rawopenfile(String filename, String mode) throws IOException {
		boolean isstdfile = "-".equals(filename);
		boolean isreadmode = mode.startsWith("r");
		if ( isstdfile ) {
			return isreadmode? 
				wrapStdin():
				wrapStdout();
		}
		boolean isappend = mode.startsWith("a");
		boolean isupdate = mode.indexOf("+") > 0;
		boolean isbinary = mode.endsWith("b");
		return openFile( filename, isreadmode, isappend, isupdate, isbinary );
	}


	// ------------- file reading utilitied ------------------
	
	public static LuaValue freadbytes(File f, int count) throws IOException {
		byte[] b = new byte[count];
		int r;
		if ( ( r = f.read(b,0,b.length) ) < 0 )
			return LuaValue.NIL;
		return LuaString.valueOf(b, 0, r);
	}
	public static LuaValue freaduntil(File f,boolean lineonly) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		int c;
		try {
			if ( lineonly ) {
				loop: while ( (c = f.read()) > 0 ) { 
					switch ( c ) {
					case '\r': break;
					case '\n': break loop;
					default: baos.write(c); break;
					}
				}
			} else {
				while ( (c = f.read()) > 0 ) 
					baos.write(c);
			}
		} catch ( EOFException e ) {
			c = -1;
		}
		return ( c < 0 && baos.size() == 0 )? 
			(LuaValue) LuaValue.NIL:
			(LuaValue) LuaString.valueOf(baos.toByteArray());
	}
	public static LuaValue freadline(File f) throws IOException {
		return freaduntil(f,true);
	}
	public static LuaValue freadall(File f) throws IOException {
		int n = f.remaining();
		if ( n >= 0 ) {
			return freadbytes(f, n);
		} else {
			return freaduntil(f,false);
		}
	}
	public static LuaValue freadnumber(File f) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		freadchars(f," \t\r\n",null);
		freadchars(f,"-+",baos);
		//freadchars(f,"0",baos);
		//freadchars(f,"xX",baos);
		freadchars(f,"0123456789",baos);
		freadchars(f,".",baos);
		freadchars(f,"0123456789",baos);
		//freadchars(f,"eEfFgG",baos);
		// freadchars(f,"+-",baos);
		//freadchars(f,"0123456789",baos);
		String s = baos.toString();
		return s.length()>0? LuaValue.valueOf( Double.parseDouble(s) ): LuaValue.NIL;
	}
	private static void freadchars(File f, String chars, ByteArrayOutputStream baos) throws IOException {
		int c;
		while ( true ) {
			c = f.peek();
			if ( chars.indexOf(c) < 0 ) {
				return;
			}
			f.read();
			if ( baos != null )
				baos.write( c );
		}
	}
}
