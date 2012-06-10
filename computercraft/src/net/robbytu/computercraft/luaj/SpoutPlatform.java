package net.robbytu.computercraft.luaj;

import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaThread;
import org.luaj.vm2.compiler.LuaC;
import org.luaj.vm2.lib.CoroutineLib;
import org.luaj.vm2.lib.DebugLib;
import org.luaj.vm2.lib.PackageLib;
import org.luaj.vm2.lib.StringLib;
import org.luaj.vm2.lib.TableLib;
import org.luaj.vm2.lib.jse.JseBaseLib;
import org.luaj.vm2.lib.jse.JseMathLib;
import org.luaj.vm2.lib.jse.JseOsLib;
import org.luaj.vm2.lib.jse.JsePlatform;
import org.luaj.vm2.lib.jse.LuajavaLib;

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
	public static LuaTable standardGlobals() {
		LuaTable _G = new LuaTable();
		_G.load(new JseBaseLib()); // TODO: need rewrite for ComputerCraft
		_G.load(new PackageLib()); // TODO: need rewrite for ComputerCraft
		_G.load(new TableLib()); // TODO: need rewrite for ComputerCraft
		_G.load(new StringLib()); // TODO: need rewrite for ComputerCraft
		_G.load(new CoroutineLib()); // TODO: need rewrite for ComputerCraft
		_G.load(new JseMathLib()); // TODO: need rewrite for ComputerCraft
		_G.load(new JseOsLib()); // TODO: need rewrite for ComputerCraft
		_G.load(new LuajavaLib()); // TODO: need rewrite for ComputerCraft
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
	public static LuaTable debugGlobals() {
		LuaTable _G = standardGlobals();
		_G.load(new DebugLib());  // TODO: need rewrite for ComputerCraft
		return _G;
	}
}
