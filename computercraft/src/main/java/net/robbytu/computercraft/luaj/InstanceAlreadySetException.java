package net.robbytu.computercraft.luaj;

/**
 * This exception is thrown, when you try to inflate a LuaInstance
 * to a thread that already has an active instance set.
 *  
 * @author Markus Andree
 */
public class InstanceAlreadySetException extends Exception {
	/**
	 * Serial version id of this exception.
	 */
	private static final long serialVersionUID = -7066204875502572931L;

	public InstanceAlreadySetException() {
	}

	public InstanceAlreadySetException(String arg0) {
		super(arg0);
	}

	public InstanceAlreadySetException(Throwable arg0) {
		super(arg0);
	}

	public InstanceAlreadySetException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

}
