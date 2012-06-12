package net.robbytu.computercraft.luaj;

import net.robbytu.computercraft.luaj.lib.BaseLib;
import net.robbytu.computercraft.luaj.lib.PackageLib;

import org.luaj.vm2.LuaThread;

/**
 * The LuaInstance class allows to run more than one lua script in different threads. 
 * 
 * Each thread that starts a lua script gets an associated LuaInstance, to run the script in.
 * 
 * @author hapm
 */
public class LuaInstance extends LuaThread {
	/**
	 * Saves the lua instance for the current thread 
	 */
	private static ThreadLocal<LuaInstance> activeInstance = new ThreadLocal<LuaInstance>();
	
	/**
	 * Saves the currently active child thread of this instance
	 */
	private LuaThread runningThread;
	
	BaseLib baseLib;
	
	PackageLib packageLib;
	
	protected static LuaThread test;

	/**
	 * Initializes a new instance of the LuaInstance class.
	 */
	protected LuaInstance() {
		super();
		runningThread = this;
	}

	/**
	 * Gets the currently active coroutine LuaThread of the instance.
	 * 
	 * @return The LuaThread instance for the currently active coroutine of this instance.
	 */
	public static LuaThread getRunning() {
		LuaInstance instance = getActiveInstance();
		return instance.runningThread;
	}
	
	/**
	 * Sets a LuaThread to be the running thread.
	 * 
	 * <b>This method is for internal use only!</b>
	 * 
	 * @param thread The new LuaThread instance to use as the running thread.
	 */
	public static void setRunning(LuaThread thread) {
		getActiveInstance().runningThread = thread;
	}

	/**
	 * Gets the instance, that is associated to the current thread.
	 * 
	 * If there is no LuaInstance for the active thread, this call will create a new LuaInstance.
	 * 
	 * @return The instance for the current thread.
	 */
	public static LuaInstance getActiveInstance() {
		LuaInstance result = activeInstance.get();
		if (result == null) {
			result = new LuaInstance();
			activeInstance.set(result);
		}
		
		return result;
	}

	/**
	 * Inflates the LuaInstance to the active thread.
	 * 
	 * Be careful with that, as LuaJ is not thread save. Having more than run
	 * thread simultaneously in the same instance can lead to threading problems.
	 * @throws InstanceAlreadySetException If the thread already has an active instance, this exception
	 *                                     is thrown.
	 */
	public void inflate() throws InstanceAlreadySetException {
		LuaInstance activeInstance = LuaInstance.activeInstance.get();
		if (activeInstance == this)
			return;
		
		if (activeInstance != null)
			throw new InstanceAlreadySetException("Tried to inflate a LuaInstance into a thread, that already has a vaild instance.");
		
		LuaInstance.activeInstance.set(this);		
	}

	public BaseLib getBaseLib() {
		return baseLib;
	}

	public PackageLib getPackageLib() {
		return packageLib;
	}
}
