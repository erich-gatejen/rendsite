/**
 * RendSite 2008
 * Copyright Erich P Gatejen 2008, 2009 
 */
package rendsite;

import rendsite.resources.Resources;
import rendsite.tools.RendsiteLogger;
import things.thinger.SystemInterface;

/**
 * Configurations.
 * @author  erich
 * <i>Version History</i>
 * <pre>
 * 10NOV09 - EPG - First package release.
 * </pre>
 */
public interface RendsiteConfiguration  {

	/**
	 * Are we forcing updates?  
	 * @return If it returns true, update everything regardless of last modified dates.
	 */
	public boolean isForceUpdate();
	
	/**
	 * Are we in filesystem mode?  If true, set urls for filesystem browsing.  They will be relative, instead of absolute.  If on, do not upload anything 
	 * to a website!
	 * @return If true, set urls for filesystem browsing.
	 */
	public boolean isFilesystemMode();
		
	/**
	 * Are we not cleaning?  If true, do not clean during processing.  For instance, if a file was removed from the source, do not delete it from the target.  
	 * The system will clean by default.
	 * @return If true, do not clean during processing.
	 */
	public boolean isDontClean();
	
	/**
	 * Get the root/normal logger.
	 * @return The logger.
	 */
	public RendsiteLogger rootLogger();
	
	/**
	 * Get the system interface.
	 * @return the system interface
	 */
	public SystemInterface getSystemInterface();

	/**
	 * Get the system resources.
	 * @return the resources.
	 */
	public Resources getResources();
	
	// ------------------------------------------------------------------------------------------------------
	// - LOGGING OPTIONS
	
	/**
	 * Are we logging all file actions?
	 * @return true if we are.
	 */
	public boolean loggingAllFileActions();
}



