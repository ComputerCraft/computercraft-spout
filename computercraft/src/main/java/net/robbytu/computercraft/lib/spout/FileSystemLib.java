package net.robbytu.computercraft.lib.spout;

import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;

import net.robbytu.computercraft.computer.ComputerThread;
import net.robbytu.computercraft.computer.FileManager;
import net.robbytu.computercraft.lib.LuaLib;

public class FileSystemLib extends LuaLib {
	private ComputerThread computer;
	//TODO move FileManager into here

	public FileSystemLib() {
		super("fs");
	}

	@Override
	public LuaValue init(ComputerThread computer, LuaValue env) {
		this.computer = computer;
		LuaTable fs = new LuaTable();
		fs.set("isDir", new OneArgFunction() {
			public LuaValue call(LuaValue val) {
				return LuaValue.valueOf(isDir(val.toString()));
			}
		});
		
		fs.set("separator", getSeperator());
		
		fs.set("makeDir", new OneArgFunction() {
			public LuaValue call(LuaValue val) {
				return LuaValue.valueOf(makeDir(val.tojstring()));
			}
		});
		
		fs.set("combine", new TwoArgFunction() {
			public LuaValue call(LuaValue basePath, LuaValue childPath) {
				return LuaValue.valueOf(combine(basePath.tojstring(), childPath.tojstring()));
			}
		});
		
		fs.set("getDir", new OneArgFunction() {
			public LuaValue call(LuaValue val) {
				return LuaValue.valueOf(getDir(val.tojstring()));
			}
		});

		fs.set("delete", new OneArgFunction() {
			public LuaValue call(LuaValue val) {
				return LuaValue.valueOf(delete(val.tojstring()));
			}
		});
		
		fs.set("exists", new OneArgFunction () {
			public LuaValue call(LuaValue val) {
				return LuaValue.valueOf(exists(val.tojstring()));
			}
		});
		
		env.set("fs", fs);
		return fs;
	}
	
	public String getSeperator() {
		return FileManager.separator;
	}

	public boolean isDir(String path) {
		return FileManager.isDir(path, computer.getID()); 
	}
	
	public boolean makeDir(String path) {
		return FileManager.mkDir(path, computer.getID());
	}
	
	public String getDir(String path) {
		return FileManager.getDir(path, computer.getID());
	}
	
	public String combine(String parent, String child) {
		return FileManager.combine(parent, child);
	}
	
	public String delete(String path) {
		return FileManager.rm(path, computer.getID());
	}
	
	public boolean exists(String path) {
		return FileManager.exists(path, computer.getID());
	}
}
