/**
 * RendSite 2008
 * Copyright Erich P Gatejen 2008, 2009 
 */
package rendsite.engine;

import rendsite.Codes;
import rendsite.Constants;
import rendsite.RendsiteConfiguration;
import rendsite.RendsiteException;
import rendsite.RendsiteProperties;
import rendsite.engine.node.Node_Directory;
import rendsite.engine.node.Node_File;
import rendsite.renderer.Renderer;
import rendsite.renderer.RendererCatalog;
import rendsite.resources.Messaging;
import things.common.ThingsException;
import things.thinger.io.Logger;

/**
 * Processor is the bridge from the crawl engine and the renderers.  It will make any final decisions on what to render and what not
 * to render.
 * @author  erich
 * <i>Version History</i>
 * <pre>
 * 10NOV09 - EPG - First package release.
 * </pre>
 */
public class Processor implements RendsiteProperties {

	// ===================================================================================================================
	// = FIELDS
	
	// ===================================================================================================================
	// = DATA	
	private RendsiteConfiguration configuration;
	private RenderingContext rcontext;
	private PropertiesContext pcontext;
	private Logger logger;
	
	/**
	 * The most recent configuration date/time in milliseconds from epoch.
	 */
	private long configDateTime;
	
	/**
	 * Reason text.
	 */
	private String reasonFileUpdated; 		// "File updated."
	private String reasonFDU; 				// "Files/Directories updated."
	private String reasonConfigUpdated;		//"Configuration updated."
	private String reasonNewCatalogFile; 	// "New catalog file."
	private String reasonNewFile; 			// "New file."
	
	// ======================================================================================================================
	// = CONSTRUCTION AND USE

	/**
	 * Create the processor.  
	 * @param fcontext the file context
	 * @param rcontext the renderer context
	 * @param pcontext the property context
	 * @param configuration the configuration for the system.
	 * @throws Throwable normally this will only happen if you pass a null or bad SystemInterface.
	 */
	public Processor(FileContext fcontext, RenderingContext rcontext, PropertiesContext pcontext, RendsiteConfiguration configuration) throws Throwable {
		if (rcontext==null) RendsiteException.softwareProblem("Cannot construct a Processor with a null RenderingContext.");
		if (pcontext==null) RendsiteException.softwareProblem("Cannot construct a Processor with a null PropertiesContext.");
		if (configuration==null) RendsiteException.softwareProblem("Cannot construct a Processor with a null RendsiteConfiguration.");
		logger = rcontext.getLogger();
		this.rcontext = rcontext;
		this.pcontext = pcontext;
		this.configuration = configuration;
		
		// Set up the the reason text
		Messaging messaging = configuration.getResources().getMessaging();
		reasonFileUpdated = messaging.lookup("xxxreason.file.updated", "File updated.");
		reasonFDU = messaging.lookup("xxxreason.fd.updated", "Files/Directories updated.");
		reasonConfigUpdated = messaging.lookup("xxxreason.config.updated", "Configuration updated.");
		reasonNewCatalogFile = messaging.lookup("xxxreason.new.catalog", "New catalog file.");
		reasonNewFile = messaging.lookup("xxxreason.new.file", "New file.");
	}
	
	// ===================================================================================================================
	// = METHODS
	
	/**
	 * Set the newest configuration date/time.
	 * @param datetime date/time in milliseconds from epoch. 
	 */
	public void setConfigurationTime(long datetime) {
		configDateTime = datetime;
	}
	
	/**
	 * Get the newest configuration date/time.
	 * @return date/time in milliseconds from epoch. 
	 */
	public long getConfigurationTime() {
		return configDateTime;
	}
	
	/**
	 * Process a file.
	 * @param theFile
	 * @throws Throwable for a wide range of offenses.
	 * @return the last modified date of the file (which might be now).
	 */
	public long process(Node_File theFile) throws Throwable {
		// Only if it is newer, the config is newer, or we are forcing it.
		long destLastModified = 0;
		
		if (configuration.isForceUpdate()) {
			process_file(theFile);  // Forced.  Don't bother with a message.
			destLastModified = theFile.lastModified();
			
		} else {
			String reason = null;
			
			destLastModified = theFile.lastModifiedDestination();
			if (destLastModified>0) {
				if (destLastModified <= theFile.lastModified()) {
					reason = reasonFileUpdated;
				} else if (destLastModified <= configDateTime) {
					reason = reasonConfigUpdated;
				}
				
			} else {
				reason = reasonNewFile;
			}
			
			if (reason == null) {
				logger.info("No update needed.", Codes.INFO_FILE_NO_UPDATE_NEEDED, Constants.NAME_FILE_SOURCE, theFile.getURL(false));
				check_file(theFile);
			} else {
				if (configuration.loggingAllFileActions()||logger.debuggingState()) logger.info("File update needed.", Codes.INFO_FILE_UPDATED, Constants.NAME_FILE_SOURCE, theFile.getURL(false), Constants.NAME_REASON, reason);
				process_file(theFile);		
			}
			destLastModified = theFile.lastModifiedDestination();
		}
		return destLastModified;
		
	}	
	
	/**
	 * Process a file (internal method to cut copy/paste yuckiness).  Called only by the public method process(Node_File theFile, Node_Directory containingDirectory).
	 * @param theFile
	 * @throws Throwable for a wide range of offenses.
	 */
	private void process_file(Node_File theFile) throws Throwable {
		if (theFile.copyOnly) {
			theFile.copy();	
		} else {
			// Pass it to the renderer.
			String rendererName = pcontext.GET_DEFAULT(PROP_RENDERER_NAME, RendererCatalog.DEFAULT_RENDERER_NAME);
			Renderer renderer = rcontext.renderers.getRenderer(rendererName, rcontext, pcontext);
			try {
				renderer.render(theFile);
			} catch (ThingsException te) {
				
				// Tell the file that the catazlog write failed.
				theFile.failWrite();
				
				// Don't let the renderer mask PANICS or FAULT
				if (te.getWorst().isWorseThanFault()) {
					throw new RendsiteException("PANIC while rendering file.", Codes.PANIC_RENDERER__PROCESS_FILE, te);
				} else if (te.getWorst().isWorseThanError()) {
					throw new RendsiteException("Fault while rendering file.", Codes.FAULT_RENDERER__PROCESS_FILE, te);
				}
				te.addAttribute(Constants.NAME_RENDERER_NAME, rendererName);
				throw te;
			}
		}
	}
	
	/**
	 * Check a file (internal method to cut copy/paste yuckiness).  Called only by the public method process(Node_File theFile, Node_Directory containingDirectory).
	 * @param theFile
	 * @throws Throwable for a wide range of offenses.
	 */
	private void check_file(Node_File theFile) throws Throwable {
		if (theFile.copyOnly) {
			theFile.copy();	
		} else {
			// Pass it to the renderer.
			String rendererName = pcontext.GET_DEFAULT(PROP_RENDERER_NAME, RendererCatalog.DEFAULT_RENDERER_NAME);
			Renderer renderer = rcontext.renderers.getRenderer(rendererName, rcontext, pcontext);
			try {
				renderer.check(theFile);
			} catch (ThingsException te) {
				
				// Tell the file that the catazlog write failed.
				theFile.failWrite();
				
				// Don't let the renderer mask PANICS or FAULT
				if (te.getWorst().isWorseThanFault()) {
					throw new RendsiteException("PANIC while checking file.", Codes.PANIC_RENDERER__CHECK_FILE, te);
				} else if (te.getWorst().isWorseThanError()) {
					throw new RendsiteException("Fault while checking file.", Codes.FAULT_RENDERER__CHECK_FILE, te);
				}
				te.addAttribute(Constants.NAME_RENDERER_NAME, rendererName);
				throw te;
			}
		}
	}
	
	/**
	 * Process a directory (for frame context).
	 * @param theDirectory
	 * @throws Throwable for a wide range of offenses.
	 */
	public void process(Node_Directory theDirectory) throws Throwable {
		theDirectory.build();
		if (!theDirectory.copyOnly) { 

			String rendererName = pcontext.GET_DEFAULT(PROP_RENDERER_NAME, RendererCatalog.DEFAULT_RENDERER_NAME);
			Renderer renderer = rcontext.renderers.getRenderer(rendererName, rcontext, pcontext);
			try {
				renderer.render(theDirectory);
			} catch (ThingsException te) {
				// Don't let the renderer mask PANICS or FAULT
				if (te.getWorst().isWorseThanFault()) {
					throw new RendsiteException("PANIC while rendering directory.", Codes.PANIC_RENDERER__PROCESS_DIRECTORY, te);
				} else if (te.getWorst().isWorseThanError()) {
					throw new RendsiteException("Fault while rendering directory.", Codes.FAULT_RENDERER__PROCESS_DIRECTORY, te);
				}
				te.addAttribute(Constants.NAME_RENDERER_NAME, rendererName);
				throw te;
			}
		}
	}
	
	/**
	 * Process the catalog.  It should only be called when done with the frame and ready to render its catalog.
	 * @param theDirectory
	 * @throws Throwable for a wide range of offenses.
	 * @return the last modified date of the file (which might be now).
	 */
	public long processCatalog(Node_Directory theDirectory) throws Throwable {
		
		// Only if it is newer, the config is newer, or we are forcing it.);
		
		if (!theDirectory.copyOnly) {
			if (configuration.isForceUpdate()) {
				process_catalog(theDirectory);  // Forced.  Dont' bother with a message.
				theDirectory.lastModifiedCatalog();
				
			} else {
				String reason = null;
				long catalogLastModified = theDirectory.lastModifiedCatalog();
				
				if (catalogLastModified > 0) {

					if (theDirectory.getMostRecentModified() > catalogLastModified) {
						reason = reasonFDU;
					} else if (configDateTime > catalogLastModified) {
						reason = reasonConfigUpdated;
					}
					
				} else {
					reason = reasonNewCatalogFile;
				}
				
				if (reason == null) {
					logger.info("No update needed for catalog.", Codes.INFO_CATALOG_NO_UPDATE_NEEDED, Constants.NAME_FILE_SOURCE, theDirectory.getCatalogUrlAbsolute(false));
				} else {
					if (configuration.loggingAllFileActions()||logger.debuggingState()) logger.info("Catalog file update needed.", Codes.INFO_CATALOG_UPDATED, Constants.NAME_FILE_SOURCE, theDirectory.getCatalogUrlAbsolute(false), Constants.NAME_REASON, reason);
					process_catalog(theDirectory);	
					theDirectory.modifiedTime(theDirectory.lastModifiedCatalog());
				}
			}
		} // end if not copy only

		return theDirectory.getMostRecentModified();
	}	
	
	/**
	 * Process a catalog file (internal method to cut copy/paste yuckiness).  Called only by the public method process(Node_File theFile, Node_Directory containingDirectory).
	 * @param theDirectory 
	 * @throws Throwable for a wide range of offenses.
	 */
	private void process_catalog(Node_Directory theDirectory) throws Throwable {
		String rendererName = pcontext.GET_DEFAULT(PROP_RENDERER_NAME, RendererCatalog.DEFAULT_RENDERER_NAME);
		Renderer renderer = rcontext.renderers.getRenderer(pcontext.GET_REQUIRED(PROP_RENDERER_NAME), rcontext, pcontext);
		try {
			renderer.renderFrame(theDirectory);
		} catch (ThingsException te) {
			
			// Tell the dir that the catalog write failed.
			theDirectory.failCatalogWrite();
			
			// Don't let the renderer mask PANICS or FAULT
			if (te.getWorst().isWorseThanFault()) {
				throw new RendsiteException("PANIC while rendering catalog.", Codes.PANIC_RENDERER__CATALOG, te);
			} else if (te.getWorst().isWorseThanError()) {
				throw new RendsiteException("Fault while rendering catalog.", Codes.FAULT_RENDERER__CATALOG, te);
			}
			te.addAttribute(Constants.NAME_RENDERER_NAME, rendererName);
			throw te;
		}
	}
		
}

