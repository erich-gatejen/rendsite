/**
 * RendSite 2008
 * Copyright Erich P Gatejen 2008, 2009 
 */
package rendsite.commands;

import java.io.File;

import rendsite.Codes;
import rendsite.Constants;
import rendsite.engine.CategoryManager;
import rendsite.engine.Engine;
import rendsite.engine.FileContext;
import rendsite.engine.Processor;
import rendsite.engine.PropertiesContext;
import rendsite.engine.RenderingContext;
import rendsite.resources.Messaging;
import things.common.ThingsException;
import things.common.ThingsNamespace;
import things.thinger.io.conduits.ConduitController;

/**
 * A basic render entry command
 * @author erich
 * <i>Version History</i>
 * <pre>
 * 10NOV09 - EPG - First package release.
 * </pre>
 */
public class RenderStandard implements Constants {
	
	// =============================================================================================================\
	// == METHODS
	
	/**
	 * Run the command.  The implementation should NOT persist any state!  It's up the the creator to decide if
	 * to keep the command object around.  Any exception that finds its way out will be stopped and logged.  Obviously, ThingsExceptions will
	 * provide richer detail than plain Exceptions.
	 * @throws Throwable
	 */
	public void run(RenderConfiguration configuration, File sourcefile, File destinationFile) throws Throwable {
				
		// Cheeseball flag 
		boolean oktoRun = false;
		
		// Component setup.
		FileContext fcontext = null;
		PropertiesContext pcontext = null;
		RenderingContext rcontext = null;
		Processor processor = null;
		CategoryManager cmanager = null;
		Engine engine = null;
		ChangeContext change = null;
		
		Messaging messaging = null;
		
		try {
			
			messaging = configuration.resources.getMessaging();
			
			// Conduits and change system.
			ConduitController cc  = configuration.si.requestSuperSystemInterface().getSystemConduits();
			change = new ChangeContext(cc);
			configuration.rootLogger.debug("BOOT : Change system built.");

			// Tools
			cmanager = new CategoryManager();
			
			// Contexts
			fcontext = new FileContext(configuration, sourcefile, destinationFile); 
			pcontext = new PropertiesContext(configuration, sourcefile, destinationFile);
			rcontext = new RenderingContext(configuration, fcontext, cmanager);
			configuration.rootLogger.debug("BOOT : Contexts built.");
			
			processor =  new Processor(fcontext, rcontext, pcontext, configuration);
			engine = new Engine();
			configuration.rootLogger.debug("BOOT : Engine and processor built.");
			
			// Changefile notice
			if (configuration.changeFile!=null) {
				change.createChangeFile(fcontext, configuration.changeFile);
				configuration.rootLogger.debug("BOOT : Change file configured @" + configuration.changeFile.getAbsolutePath());
			}
			
			// Flag ok to run.
			oktoRun = true;
			configuration.rootLogger.debug("BOOT : Complete");
			
		} catch (Throwable t) {
			if (configuration.rootLogger.debuggingState())
				configuration.rootLogger.error(messaging.lookup("xxxcommand.abort", "ABORT") + " :" + t.getMessage(), Codes.PANIC_SETUP, ThingsNamespace.ATTR_PLATFORM_MESSAGE_COMPLETE, ThingsException.toStringCauses(t));
			else
				configuration.rootLogger.error(messaging.lookup("xxxcommand.abort", "ABORT") + " :" + t.getMessage());	
		}
			
		// Run it.
		try {
			if (oktoRun) engine.run(configuration, processor, fcontext, pcontext, rcontext, fcontext.getRoot());
			
		} catch (Throwable t) {
			if (configuration.rootLogger.debuggingState())
				configuration.rootLogger.error(messaging.lookup("xxxcommand.abort", "ABORT") + " :" + t.getMessage(), Codes.PANIC_RUN, ThingsNamespace.ATTR_PLATFORM_MESSAGE_COMPLETE, ThingsException.toStringComplex(t));
			else
				configuration.rootLogger.error(messaging.lookup("xxxcommand.abort", "ABORT") + " :" + t.getMessage());	
		} 
		
		// Shutdown.
		try {
			// TODO - collect these errors.
			change.reset();
			
		} catch (Throwable t) {
			if (configuration.rootLogger.debuggingState())
				configuration.rootLogger.error(messaging.lookup("xxxcommand.exiterror", "EXIT ERROR") + " :" + t.getMessage(), Codes.PANIC_RUN, ThingsNamespace.ATTR_PLATFORM_MESSAGE_COMPLETE, ThingsException.toStringComplex(t));
			else
				configuration.rootLogger.error(messaging.lookup("xxxcommand.exiterror", "EXIT ERROR") + " :" + t.getMessage());	
		} 
	}

}