/**
 * RendSite 2008
 * Copyright Erich P Gatejen 2008, 2009 
 */
package rendsite.engine;

import rendsite.RendsiteConfiguration;
import rendsite.RendsiteException;
import things.thinger.SystemInterface;
import things.thinger.io.Logger;

/**
 * Base context for all specific contexts.
 * @author  erich
 * <i>Version History</i>
 * <pre>
 * 10NOV09 - EPG - First package release.
 * </pre>
 */
public abstract class Context implements Resetable {

	// ===================================================================================================================
	// = FIELDS
	
	/**
	 *  A logger.
	 */
	public Logger logger;
	
	/**
	 * The system interface for THINGS.  
	 */
	public SystemInterface si;
	
	/**
	 * System configuration.
	 */
	protected RendsiteConfiguration configuration;
	
	// ===================================================================================================================
	// = ABSTRACT

	/**
	 * Reset the object.  It should lose all state except what was set during construction.
	 * @throws Throwable if it could not completely reset state.
	 */
	public abstract void reset() throws Throwable;
	
	// ======================================================================================================================
	// = METHODS

	/**
	 * Create a context.  
	 * @param configuration the configuration for the system.
	 * @throws Throwable normally this will only happen if you pass a null or bad SystemInterface.
	 */
	public Context(RendsiteConfiguration configuration) throws Throwable {
		if (configuration==null) RendsiteException.softwareProblem("Cannot construct a RendsiteContext with a null system interface.");
		this.configuration = configuration;		
		si = configuration.getSystemInterface();
		logger = si.getSystemLogger();
	}

}



