package net.robbytu.computercraft.lib.spout;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;

import net.robbytu.computercraft.computer.ComputerThread;
import net.robbytu.computercraft.lib.LuaLib;

import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaString;
import org.luaj.vm2.LuaValue;

public class File extends LuaValue {
	private final RandomAccessFile file;
	private final InputStream is;
	private final OutputStream os;
	private boolean closed = false;
	private boolean nobuffer = false;
	private ComputerThread computer;
	private LuaValue filemethods;
	
	File( RandomAccessFile file, InputStream is, OutputStream os, ComputerThread computer ) {
		this.file = file;
		this.is = is!=null? is.markSupported()? is: new BufferedInputStream(is): null;
		this.os = os;
		this.computer = computer;
		LuaLib lib = this.computer.getLib("io");
		if (!(lib instanceof IoLib)) {
			throw new LuaError("IoLib not registered");
		}
		
		filemethods = ((IoLib)lib).generateFileMethods(this);
	}
	File( RandomAccessFile f, ComputerThread computer ) {
		this( f, null, null, computer );
	}
	File( InputStream i, ComputerThread computer ) {
		this( null, i, null, computer );
	}
	File( OutputStream o, ComputerThread computer ) {
		this( null, null, o, computer );
	}

	public boolean isstdfile() {
		return file == null;
	}
	public void close() throws IOException  {
		closed = true;
		if ( file != null ) {
			file.close();
		}
	}
	public void flush() throws IOException {
		if ( os != null )
			os.flush();
	}
	public void write(LuaString s) throws IOException {
		if ( os != null )
			os.write( s.m_bytes, s.m_offset, s.m_length );
		else if ( file != null )
			file.write( s.m_bytes, s.m_offset, s.m_length );
		else
			notimplemented();
		if ( nobuffer )
			flush();
	}
	public boolean isclosed() {
		return closed;
	}
	public int seek(String option, int pos) throws IOException {
		if ( file != null ) {
			if ( "set".equals(option) ) {
				file.seek(pos);
			} else if ( "end".equals(option) ) {
				file.seek(file.length()+pos);
			} else {
				file.seek(file.getFilePointer()+pos);
			}
			return (int) file.getFilePointer();
		}
		notimplemented();
		return 0;
	}
	public void setvbuf(String mode, int size) {
		nobuffer = "no".equals(mode);
	}

	// get length remaining to read
	public int remaining() throws IOException {
		return file!=null? (int) (file.length()-file.getFilePointer()): -1;
	}
	
	// peek ahead one character
	public int peek() throws IOException {
		if ( is != null ) {
			is.mark(1);
			int c = is.read();
			is.reset();
			return c;
		} else if ( file != null ) {
			long fp = file.getFilePointer();
			int c = file.read();
			file.seek(fp);
			return c;
		}
		notimplemented();
		return 0;
	}		
	
	// return char if read, -1 if eof, throw IOException on other exception 
	public int read() throws IOException {
		if ( is != null ) 
			return is.read();
		else if ( file != null ) {
			return file.read();
		}
		notimplemented();
		return 0;
	}

	// return number of bytes read if positive, -1 if eof, throws IOException
	public int read(byte[] bytes, int offset, int length) throws IOException {
		if (file!=null) {
			return file.read(bytes, offset, length);
		} else if (is!=null) {
			return is.read(bytes, offset, length);
		} else {
			notimplemented();
		}
		return length;
	}

	
	private static void notimplemented() {
		throw new LuaError("not implemented");
	}
	
	// delegate method access to file methods table
	public LuaValue get( LuaValue key ) {
		return filemethods.get(key);
	}

	// essentially a userdata instance
	public int type() {
		return LuaValue.TUSERDATA;
	}
	public String typename() {
		return "userdata";
	}
	
	// displays as "file" type
	public String tojstring() {
		return "file: " + Integer.toHexString(hashCode());
	}
}
