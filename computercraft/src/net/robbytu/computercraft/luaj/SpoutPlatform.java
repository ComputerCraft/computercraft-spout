package net.robbytu.computercraft.luaj;

import net.robbytu.computercraft.computer.ComputerThread;
import net.robbytu.computercraft.luaj.lib.BaseLib;
import net.robbytu.computercraft.luaj.lib.DebugLib;
import net.robbytu.computercraft.luaj.lib.MathLib;
import net.robbytu.computercraft.luaj.lib.OsLib;
import net.robbytu.computercraft.luaj.lib.PackageLib;
import net.robbytu.computercraft.luaj.lib.StringLib;
import net.robbytu.computercraft.luaj.lib.TableLib;

import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaThread;
import org.luaj.vm2.compiler.LuaC;

/**
 * The SpoutPlatform supports all libraries needed in LuaJ to run in Spout
 * 
 * @author Markus Andree
 */
public class SpoutPlatform {

	/**
	 * Create a standard set of globals for Spout including all the libraries.
	 * 
	 * @return Table of globals initialized with the standard Spout libraries
	 * @see #debugGlobals()
	 * @see JsePlatform
	 * @see JmePlatform
	 */
	public static LuaTable standardGlobals(ComputerThread computer) {
		LuaTable _G = new LuaTable();
		LuaInstance activeInstance = LuaInstance.getActiveInstance();
		//activeInstance.baseLib = new BaseLib(computer);
		//activeInstance.packageLib = new PackageLib();
		//_G.load(activeInstance.baseLib); 
		//_G.load(activeInstance.packageLib);
		//_G.load(new TableLib());
		//_G.load(new StringLib()); 
		//_G.load(new CoroutineLib()); // TODO: need rewrite for ComputerCraft
		//_G.load(new MathLib());
		//_G.load(new OsLib());
		//_G.load(new LuajavaLib()); // TODO: need rewrite for ComputerCraft
		LuaThread.setGlobals(_G);
		LuaC.install();
		return _G;		
	}

	/** Create standard globals including the {@link debug} library.
	 * 
	 * @return Table of globals initialized with the standard JSE and debug libraries
	 * @see #standardGlobals()
	 * @see JsePlatform
	 * @see JmePlatform
	 * @see DebugLib
	 */
	public static LuaTable debugGlobals(ComputerThread thread) {
		LuaTable _G = standardGlobals(thread);
		_G.load(new DebugLib());  // TODO: need rewrite for ComputerCraft
		return _G;
	}
}
