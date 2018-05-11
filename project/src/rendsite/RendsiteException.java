/**
 * RendSite 2008
 * Copyright Erich P Gatejen 2008, 2009 
 */
package rendsite;

import things.common.ThingsException;

/**
 * A Rendsite exception.  These should be used for terminal errors, meaning they will be rendered to the user.  Things Exceptions can use used for most processing, 
 * of either these exceptions or any subclass of ThignsException should be done through this class.
 * <p>
 * The message may be coder friendly, since the numeric should map to the actual printed message.
 * <p>
 * @author erich
 * <i>Version History</i>
 * <pre>
 * 10NOV09 - EPG - First package release.
 * </pre>
 */
public class RendsiteException extends ThingsException {
	public static final long serialVersionUID=1;
	

	// ===============================================================================
	// = CONSTRUCTORS.  Why the hell can't the base class constructors accept pass through....  this sucks.

	/**
	 * Message and numeric constructor
	 * @param message text message for exception
	 * @param n  numeric error
	 */
	public RendsiteException(String message, int n) {
		super(message, n);
	}

	/**
	 * Message and numeric constructor with cause
	 * @param message text message for exception
	 * @param n numeric error
	 * @param theCause for exception chaining
	 */
	public RendsiteException(String message, int n, Throwable theCause) {
		super(message, n, theCause);
	}

	/**
	 * Message and numeric constructor
	 * @param message text message for exception
	 * @param attr  A list of attributes in name/value pairs.
	 * @param n numeric error
	 */
	public RendsiteException(String message, int n, String... attr) {
		super(message, n, attr);
	}

	/**
	 * Message and numeric constructor with cause.
	 * @param message text message for exception
	 * @param n numeric error
	 * @param theCause for exception chaining
	 * @param attr A list of attributes.
	 */
	public RendsiteException(String message, int n, Throwable theCause, String... attr) {
		super(message, n, theCause, attr);
	}
	
	/**
	 * A RendSite bug.  It will generate a PANIC.  It will throw a ThingsException, not at RendsiteException.
	 * @param message  information message
	 * @throws things.common.ThingsException
	 */
	public static void softwareProblem(String message) throws ThingsException {
		throw new ThingsException("SOFTWARE PROBLEM (bug):" + message, SYSTEM_FAULT_SOFTWARE_PROBLEM);
	}

	/**
	 * A RendSite bug.  It will generate a PANIC.  It will throw a ThingsException, not at RendsiteException.
	 * @param message  information message
	 * @param t  The throwable to add to chain
	 * @throws things.common.ThingsException
	 */
	public static void softwareProblem(String message, Throwable t) throws ThingsException {
		throw new ThingsException("SOFTWARE PROBLEM (bug):" + message, Codes.PANIC_BUG, t);
	}

	/**
	 * A RendSite bug.  It will generate a PANIC.  It will throw a ThingsException, not at RendsiteException.
	 * @param message  information message
	 * @param t  The throwable to add to chain
	 * @param attr A list of attributes. 
	 * @throws things.common.ThingsException
	 */
	public static void softwareProblem(String message, Throwable t, String... attr) throws ThingsException {
		throw new ThingsException("SOFTWARE PROBLEM (bug):" + message, Codes.PANIC_BUG, t, attr);
	}
	
	/**
	 * Is this a Rendsite or Things bug?
	 * @param te
	 * @return true if it is, otherwise false.
	 */
	public static boolean isBug(ThingsException te) {
		if ((te.numeric==ThingsException.SYSTEM_FAULT_SOFTWARE_PROBLEM)||(te.numeric==Codes.PANIC_BUG)) return true;
		return false;
	}
	 
}
