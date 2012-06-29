package net.robbytu.computercraft.lib;

import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaValue;

/**
 * The LibFunction class is used to write user defined lua functions
 * for ComputerCraft-Spout.
 *  
 * @author Markus Andree
 */
public class LibFunction extends LuaFunction {
	private String name;

	/**
	 * Initializes a new instance of the LibFunction class
	 * and sets the given name as the name for the new function. 
	 * @param name
	 */
	public LibFunction(String name) {
		this.name = name;
	}

	/**
	 * Initializes a new instance of the LibFunction class, sets the
	 * given name as the name for the new function and sets the given
	 * environment as the environment for the function. 
	 * 
	 * @param name The name of the function.
	 * @param env The environment to register the function to.
	 */
	public LibFunction(String name, LuaValue env) {
		super(env);
		this.name = name;
		env.set(name, this); //TODO think about if that is needed (bad design?)
	}
	
	/**
	 * Gets the name of the function.
	 * 
	 * @return The name of the function.
	 */
	public String getName() {
		return name;
	}
}
