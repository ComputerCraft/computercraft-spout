package net.robbytu.computercraft.luaj;

/**
 * The InstanceThreads encapsulates a normal java Thread. When started it sets 
 * the active LuaInstance to the one that was active when the InstanceThread was instantiated. 
 * After that it starts the original Runnable, as a normal thread would do.
 * 
 * @author Markus Andree
 */
public class InstanceThread extends Thread {

	/**
	 * Saves the instance, that should be set when run is called.
	 */
	private LuaInstance instance;
	
	public InstanceThread() {
		super();
		instance = LuaInstance.getActiveInstance(); 
	}
	
	public InstanceThread(Runnable paramRunnable) {
		super(paramRunnable);
		instance = LuaInstance.getActiveInstance(); 
	}

	public InstanceThread(Runnable paramRunnable, String paramString) {
		super(paramRunnable, paramString);
		instance = LuaInstance.getActiveInstance(); 
	}

	public InstanceThread(String paramString) {
		super(paramString);
		instance = LuaInstance.getActiveInstance(); 
	}

	public InstanceThread(ThreadGroup paramThreadGroup,
			Runnable paramRunnable, String paramString, long paramLong) {
		super(paramThreadGroup, paramRunnable, paramString, paramLong);
		instance = LuaInstance.getActiveInstance(); 
	}

	public InstanceThread(ThreadGroup paramThreadGroup,
			Runnable paramRunnable, String paramString) {
		super(paramThreadGroup, paramRunnable, paramString);
		instance = LuaInstance.getActiveInstance(); 
	}

	public InstanceThread(ThreadGroup paramThreadGroup, Runnable paramRunnable) {
		super(paramThreadGroup, paramRunnable);
		instance = LuaInstance.getActiveInstance(); 
	}

	public InstanceThread(ThreadGroup paramThreadGroup, String paramString) {
		super(paramThreadGroup, paramString);
		instance = LuaInstance.getActiveInstance(); 
	}

	@Override
	public void run() {
		try {
			instance.inflate();
		} catch (InstanceAlreadySetException e) {
			// Can't happen as that would mean the thread was already running before.
		}
		
		super.run();
	}

}
