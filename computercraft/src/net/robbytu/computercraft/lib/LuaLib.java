package net.robbytu.computercraft.lib;

import net.robbytu.computercraft.computer.ComputerThread;

import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;

/**
 * You can extend Computercraft-Spout by extending this class and register your class.
 * If you want to extend this class, be sure to implement a default constructor in
 * your library. Each computer gets a new instance of your library, that will be instantiated
 * by using the default constructor. 
 * 
 * @author Markus Andree
 */
public abstract class LuaLib {
	/**
	 * Saves the name for the library.
	 */
	private final String name;
	
	/**
	 * Initializes a new instance of the {@link LuaLib} and set the 
	 * given name as the library name.
	 * 
	 * @param name The name used in lua to reference this library.
	 */
	public LuaLib(String name) {
		this.name = name;
	}
	
	/**
	 * Gets the name of the library used to reference it from lua.
	 * 
	 * @return The name of the library.
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * The {@link #init(LuaValue)} method is called when the library needs to
	 * initialize for a new computer instance.
	 * 
	 * @param computer The computer, the library needs to be initialized for.
	 * @param env The environment, the library should bind to.
	 * @return The new binding for the computer instance.
	 */
	public abstract LuaValue init(ComputerThread computer, LuaValue env);
	
	/**
	 * Helps to bind a given {@link LibFunction} to an environment.
	 * 
	 * @param env  The environment to bind the library to.
	 * @param func The function to bind to the environment.
	 */
	protected void bind(LuaValue env, LibFunction func) {
		env.set(func.getName(), func);
	}
	
	/**
	 * Helps to bind a given {@link LibFunction} to an environment under a given name.
	 * 
	 * @param env  The environment to bind the function to.
	 * @param name The name to use to bind the function.
	 * @param func The function to bind to the environment.
	 */
	protected void bind(LuaValue env, String name, LibFunction func) {
		env.set(func.getName(), func);
	}
}
