/**
 * RendSite 2008
 * Copyright Erich P Gatejen 2008, 2009 
 */
package rendsite.renderer;

import rendsite.engine.PropertiesGeneralContext;
import rendsite.engine.RenderingContext;
import rendsite.engine.node.Node_Directory;
import rendsite.engine.node.Node_File;
import things.common.ThingsException;
import things.thinger.io.Logger;

/**
 * Base renderer.
 * @author  erich
 * <i>Version History</i>
 * <pre>
 * 10NOV09 - EPG - First package release.
 * </pre>
 */
public abstract class Renderer {

	// ==================================================================================================================
	// = FIELDS
	
	
	// ==================================================================================================================
	// = DATA
	
	/** 
	 * The rendering context.
	 */
	protected  RenderingContext rcontext;
	
	/** 
	 * The general properties context.
	 */
	protected  PropertiesGeneralContext pcontext;

	/** 
	 * A logger.
	 */
	protected  Logger logger;
	
	// ==================================================================================================================
	// = ABSTRACT INTERFACE
	
	/**
	 * Render a file.  The file is new or the configuration has changed, so it must be rendered.
	 * @param source The file to render.   
	 * @throws ThingsException
	 */
	public abstract void render(Node_File source) throws ThingsException;
	
	/**
	 * Check a file.  The file exists and the configuration has not changed, so you don't need to render it.  However, you may
	 * choose to render it anyway.
	 * <p>
	 * IMPORTANT!  IMPORTANT!  Neighbors are determined during rendering, not spidering.  If the neighbor is not opened for write, but an old version exists, it will
	 * be treated as a dead file and removed during cleanup.  You can make
	 * @param source The file to render.   
	 * @throws ThingsException
	 */
	public abstract void check(Node_File source) throws ThingsException;
	
	/**
	 * Render a directory context.  There may not be much to do here.  Any catalog page should be rendered by renderFrame() 
	 * so that it is done after all other processing is complete.
	 * @param source the source directory
	 * @throws ThingsException
	 */
	public abstract void render(Node_Directory source) throws ThingsException;
	
	/**
	 * Render the frame itself.  If you make a catalog, it is important you set the catalogFileName in the source directory
	 * if you want the parent catalog to point at it.  
	 * @param source the source directory
	 * @throws ThingsException
	 */
	public abstract void renderFrame(Node_Directory source) throws ThingsException;
		
	// ==================================================================================================================
	// = METHODS

	/**
	 * Construct a renderer.  Let the system do this for you.
	 * @param rcontext The rendering context.  A renderer cannot change context.
	 * @param pcontext general properties context.
	 * @throws ThingsException.  This will usually happen only if you give it a bad context.
	 */
	public Renderer(RenderingContext rcontext, PropertiesGeneralContext pcontext) throws ThingsException {
		if (rcontext==null) ThingsException.softwareProblem("Cannot create a renderer with a null RenderingContext.");
		if (pcontext==null) ThingsException.softwareProblem("Cannot create a renderer with a null PropertiesGeneralContext.");
		
		this.rcontext = rcontext;
		this.pcontext = pcontext;
		logger = rcontext.logger;
	}
	
	// ==================================================================================================================
	// = TOOLS

	/**
	 * Return a blank string if the passed string is null.
	 */
	public static String blankIfNull(String value) {
		if (value==null) return "";
		else return value;
	}
	

	
}



