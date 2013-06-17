package net.robbytu.computercraft.lib.spout;

import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.VarArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;

import net.robbytu.computercraft.computer.ComputerThread;
import net.robbytu.computercraft.computer.FileManager;
import net.robbytu.computercraft.lib.LuaLib;

/**
 * Special library for backward compatibility.
 * 
 * @author Markus Andree
 *
 */
public class DeprecatedLib extends LuaLib {
	private ComputerThread computer;
	public DeprecatedLib() {
		super("deprecated");
	}

	@Override
	public LuaValue init(ComputerThread computer, LuaValue env) {
		this.computer = computer;
		initBase(env);
		initFs(env, computer.getID());
		initIo(env, computer.getID());
		initOs(env);
		initRedstone(env);
		initRednet(env);
		return env;
	}
	
	private void initBase(LuaValue env) {
		
		env.set("run", new VarArgFunction() { // can be fully rewritten in lua
			@Override
			public Varargs invoke(Varargs args) {
				final BaseLib base = computer.getLib("base");
				final FileSystemLib fs = computer.getLib("fs");
				if (base == null) error("base library not found");
				if (fs == null) error("filesystem library not found");
				String file = args.checkjstring(1);
				if (args.isvalue(2)) {
					file = fs.combine(file, args.tojstring(2));
				}
				
				Varargs code = base.loadFile(file);
				
				if (code.isnil(1)) {
					return varargsOf(LuaValue.FALSE, code.arg(2));
				}
				
				return base.pcall((LuaValue)code, env, null);
			}
		});
	}

	private void initRednet(LuaValue env) {
		if (!env.istable() || !env.get("rednet").istable()) return;
		LuaTable rednet = env.get("rednet").checktable();
		rednet.set("open", rednet.get("connect")); //deprecated, use rednet.connect
	}

	private void initRedstone(LuaValue env) {
		if (!env.istable() || !env.get("rs").istable()) return;
		LuaTable rs = env.get("rs").checktable();
		env.set("redstone", rs); //deprecated, use rs		
		rs.set("isPowered", rs.get("getInput")); //deprecated, use rs.getInput
	}

	private void initOs(LuaValue env) {
		if (!env.istable() || !env.get("os").istable()) return;
		LuaTable os = env.get("os").checktable();
		os.set("getComputerID", os.get("computerID")); // deprecated, use os.computerID instead
		env.set("sys",os); // Deprecated
		env.set("shutdown", os.get("shutdown")); //deprecated, use os.shutdown
	}

	private void initFs(LuaValue env, final int CID) {		
		if (!env.istable() || !env.get("fs").istable()) return;
		LuaTable fs = env.get("fs").checktable();
		fs.set("printList", new OneArgFunction() { //TODO implement this in lua
			public LuaValue call(LuaValue val) {
				FileManager.printList(val.toString(), CID);
				return LuaValue.NIL;
			}
		});
	}

	private void initIo(LuaValue env, final int CID) {
		if (!env.istable() || !env.get("fs").istable() || !env.get("io").istable()) return;
		LuaTable io = env.get("io").checktable();
		LuaTable fs = env.get("fs").checktable();
		io.set("isDir", fs.get("isDir")); //deprecated, use fs.isDir
		io.set("mkdir",  new TwoArgFunction() { //deprecated, use fs.makeDir
			public LuaValue call(LuaValue val, LuaValue val2) {
				return LuaValue.valueOf(FileManager.mkDir(val.toString(), val2.toString(), CID));
			}
		});
		io.set("remove", fs.get("delete")); //deprecated, use fs.remove
		io.set("getDir", fs.get("getDir")); //deprecated, use fs.getDir
		io.set("printList", fs.get("printList")); //deprecated, use fs.printList
		io.set("fileExists", new TwoArgFunction() {//deprecated, use fs.exists
			@Override
			public LuaValue call(LuaValue val, LuaValue val2) {
				return LuaValue.valueOf(FileManager.exists(FileManager.combine(val.tojstring(), val2.tojstring()), CID));
			}
		});
		io.set("separator", new ZeroArgFunction() { //deprecated, use fs.seperator
			@Override
			public LuaValue call() {
				return LuaValue.valueOf(FileManager.separator);
			}
		});
		
		io.set("mkFile", new VarArgFunction() {
			@Override
			public Varargs invoke(Varargs args) {
				String path = args.checkjstring(1);
				String file = args.optjstring(2, null);
				if (file == null) {
					LuaLib lib = computer.getLib("io");
					if (lib instanceof IoLib) {
						IoLib io = (IoLib)lib;
						return valueOf(io.mkFile(path));
					}
					
					return FALSE;
				}
				else {
					return valueOf(FileManager.mkFile(path, file, CID));
				}
			}
		});
		
		io.set("getFile", new TwoArgFunction() { // rewrite in lua
			public LuaValue call(LuaValue val, LuaValue val2) {
				String file = FileManager.getFileAsString(val.toString(), val2.toString(), CID);
				return LuaValue.valueOf(file);
			}
		});
	}
}
