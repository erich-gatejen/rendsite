/**
 * RendSite 2008
 * Copyright Erich P Gatejen 2008, 2009 
 */
package rendsite.commands;

import java.io.File;

import rendsite.RendsiteConfiguration;
import rendsite.resources.Resources;
import rendsite.tools.RendsiteLogger;
import things.thinger.SystemInterface;

/**
 * Configuration for the Render implementation.  This is a dangerous implementation right now, because it doesn't assert anything.
 * @author  erich
 * <i>Version History</i>
 * <pre>
 * 10NOV09 - EPG - First package release.
 * </pre>
 */
public class RenderConfiguration implements RendsiteConfiguration  {
	
	// =============================================================================================================\
	// == OPTIONS
	
	/**
	 * Option: Render to the local filesystem instead of to a website.  This will make the links work
	 * properly when things are opened in Windows explorer.  Normally, all the links will be from the 
	 * site root (/).  This will make them relative.  Do not upload this to a webserver!  It could be 
	 * a serious security problem!
	 */
	public final static char OPTION_LOCAL_FILESYSTEM = 'F';
	
	/**
	 * Normally, files will be processed only if they or the configuration has been changed since the last run.
	 * This option forces everything to be processed.
	 */
	public final static char OPTION_FORCE_UPDATE_ALL = 'U';
	
	/**
	 * Normally, the target directories will be cleaned during processing.  for instance, if a source file has been removed between runs,
	 * it will be deleted.
	 */
	public final static char OPTION_DONT_CLEAN = 'C';
	
	/**
	 * Log all file actions.  This is as verbose as we can get without debugging.
	 */
	public final static char OPTION_LOG_ALL_FILE_ACTIONS = 'A';
	
	// ==================================================================================================================
	// = CONFIGURATIONS
	
	/**
	 * If true, update everything regardless of last modified dates.
	 */
	public boolean forceUpdate;
	
	/**
	 * If true, set urls for filesystem browsing.  They will be relative, instead of absolute.  If on, do not upload anything to a website!
	 */
	public boolean filesystemMode;

	/**
	 * Do not clean during process.  For instance, if a file was removed from the source, do not delete it from the target.  The system will clean by default.
	 */
	public boolean dontClean;
	
	/**
	 * Use a change file.  If set, use a change file.
	 */
	public File changeFile;
	
	/**
	 * Will we log all file actions?  Typically, only dismissed or failed actions will be logged, as well as whatever the renderers choose to log.  This asks
	 * that everything be logged, if possible.
	 */
	boolean loggingAllFileActions;
	
	// ==================================================================================================================
	// = FIELDS
	
	/**
	 * Root logger.
	 */
	public RendsiteLogger rootLogger;
	
	/**
	 * The system interface.
	 */
	public SystemInterface si;
	
	/**
	 * The resources manager.
	 */
	public Resources resources;
	
	// ==================================================================================================================
	// = INTERFACE MATHODS
	
	/**
	 * Are we forcing updates?  
	 * @return If it returns true, update everything regardless of last modified dates.
	 */
	public boolean isForceUpdate() {
		return forceUpdate;
	}
	
	/**
	 * Are we in filesystem mode?  If true, set urls for filesystem browsing.  They will be relative, instead of absolute.  If on, do not upload anything 
	 * to a website!
	 * @return If true, set urls for filesystem browsing.
	 */
	public boolean isFilesystemMode() {
		return filesystemMode;
	}
		
	/**
	 * Are we not cleaning?  If true, do not clean during processing.  For instance, if a file was removed from the source, do not delete it from the target.  
	 * The system will clean by default.
	 * @return If true, do not clean during processing.
	 */
	public boolean isDontClean() {
		return dontClean;
	}
	
	/**
	 * Get the root/normal logger.
	 * @return The logger.
	 */
	public RendsiteLogger rootLogger() {
		return rootLogger;
	}

	/**
	 * Get the system interface.
	 * @return the system interface
	 */
	public SystemInterface getSystemInterface() {
		return si;
	}
	
	/**
	 * Get the system resources.
	 * @return the resources.
	 */
	public Resources getResources() {
		return resources;
	}
	
	// ------------------------------------------------------------------------------------------------------
	// - LOGGING OPTIONS
	
	/**
	 * Are we logging all file actions?
	 * @return true if we are.
	 */
	public boolean loggingAllFileActions() {
		return loggingAllFileActions;
	}

}



