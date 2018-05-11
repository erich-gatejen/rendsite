/**
 * RendSite 2008
 * Copyright Erich P Gatejen 2008, 2009 
 */
package rendsite.commands;

import java.io.File;
import java.util.Locale;

import rendsite.Constants;
import rendsite.resources.Messaging;
import rendsite.resources.Resources;
import rendsite.tools.RendsiteLogger_Normal;
import rendsite.tools.SystemInterfaceStub;
import things.common.commands.CommandRoot;
import things.common.impl.WhoAmISimple;

/**
 * A basic render entry command
 * @author erich
 * <i>Version History</i>
 * <pre>
 * 10NOV09 - EPG - First package release.
 * </pre>
 */
public class Render extends CommandRoot implements Constants {
	
	// =============================================================================================================\
	// == CONSTRUCTION AND ENTRY 
	
	/**
	 * MAIN ENTRY.
	 */
	public static void main(String[] args) throws Throwable {
		CommandRoot cr = new Render();
		cr.mainEntry(args);
	}
	
	/**
	 * Default constructor.  It's needed because of the Throwable.
	 * @throws Throwable
	 */
	public Render() throws Throwable {
		super();
	}
	
	// =============================================================================================================\
	// == ABSTRACT INTERFACE

	/**
	 * Declare usage.  Gives the subclass a chance to declare values and options.
	 */
	protected void declare() throws Throwable {
		declareEntities(1, "Source directory.", true);
		declareEntities(2, "Output directory.", true);

		declareOption(RenderConfiguration.OPTION_LOCAL_FILESYSTEM, "Make links for local filesystem use.");
		declareOption(RenderConfiguration.OPTION_FORCE_UPDATE_ALL, "Force updates to everything.");
		declareOption(RenderConfiguration.OPTION_DONT_CLEAN, "Don't clean destination directories.");
		
		declareValues("changes", "File where to list file changes.", false);
		declareValues("locale", "Locale.  The default is US.", false);
		
		//suppressListingRootInstallEntity();
	}
	
	/**
	 * Run the command.  The implementation should NOT persist any state!  It's up the the creator to decide if
	 * to keep the command object around.  Any exception that finds its way out will be stopped and logged.  Obviously, ThingsExceptions will
	 * provide richer detail than plain Exceptions.
	 * @throws Throwable
	 */
	public void run() throws Throwable {

		
		// -- Entities ---------------------------------------------------------------
		String sourceDirectory  = getEntity(1);
		String outputDirectory = getEntity(2);

		// -- Values -----------------------------------------------------------------
		String changeFilePath = getValue("changes");
		String localeString = getValue("locale");
		
		
		// -- Resources --------------------------------------------------------------
		Resources resources;
		if (localeString==null)
			resources = new Resources(Locale.US);
		else
			resources = new Resources(new Locale(localeString));	
		Messaging messaging = resources.getMessaging();

		
		// Basic setup.
		File sourceFile  = null;
		File outputFile  = null;
		try {
			if ((sourceDirectory==null)||(sourceDirectory.length()<1)) throw new Exception(messaging.lookup("xxxcommand.bad.source", "Bad source directory.  ") + sourceDirectory);
			sourceFile = new File(sourceDirectory);
			if (!sourceFile.isDirectory()) throw new Exception(messaging.lookup("xxxcommand.bad.source.notexist", "Source directory does not exist.  ") + sourceDirectory);
			if ((outputDirectory==null)||(outputDirectory.length()<1)) throw new Exception(messaging.lookup("xxxcommand.bad.output", "Bad output directory.  ") +  outputDirectory);
			outputFile = new File(outputDirectory);
			if (!outputFile.isDirectory()) throw new Exception(messaging.lookup("xxxcommand.bad.output.notexist", "Output directory does not exist.  ") + outputDirectory);		
			
		} catch (Throwable t) {
			defaultLogger.error(messaging.lookup("xxxcommand.abort", "ABORT") + " :" + t.getMessage());
			return;
		}
		
		// -- Options and Configuration --------------------------------------------------------------
		RenderConfiguration configuration = new RenderConfiguration();
		try {
			configuration.forceUpdate = optionIsSet(RenderConfiguration.OPTION_FORCE_UPDATE_ALL);
			configuration.filesystemMode = optionIsSet(RenderConfiguration.OPTION_LOCAL_FILESYSTEM);
			configuration.dontClean = optionIsSet(RenderConfiguration.OPTION_DONT_CLEAN);
			configuration.rootLogger = new RendsiteLogger_Normal(new WhoAmISimple("CMND", "CMND"),  defaultLogger.getLevel(), getConsole(), resources);
			configuration.si = SystemInterfaceStub.getStub(sourceDirectory, valuePropsView, configPropsView, configuration, new WhoAmISimple("SYST", "SYST"));
			configuration.resources = new Resources(Locale.US);
			
			if (changeFilePath!=null) {
				configuration.changeFile = new File(changeFilePath);
				if (!configuration.changeFile.createNewFile()) {
					if (!configuration.changeFile.canWrite()) throw new Exception(messaging.lookup("xxxcommand.changefile.cannotwrite", "Cannot write to change file at ") + changeFilePath);				
				}
			}

			// Announce completion of config setup.  This should also test the resources before we start running.
			defaultLogger.info(messaging.lookup("xxxcommand.configcomplete", "Configuration complete.  Running."));
			
		} catch (Throwable t) {
			defaultLogger.error(messaging.lookup("xxxcommand.abort", "ABORT") + " :" + t.getMessage());
			return;
		}
		
		// -- Options and Configuration --------------------------------------------------------------
		RenderStandard standard = new RenderStandard();
		standard.run(configuration, sourceFile, outputFile);
		
	}

	/**
	 * Get the command name.  This is how the subclass defines the name (which is mostly used in the help).
	 */
	protected String getName() {
		return "render";
	}
	
	/**
	 * Get the command token.  This is a short token that will head each log line.
	 * @return the token.
	 */
	protected String getToken() {
		return "REND";
	}
	
	/**
	 * OVERRIDE IF YOU WANT TO PROVIDE MORE INFO in the help about what the
	 * command does.  Return null is you don't want to print a header.
	 * @return The header or null.
	 */
	protected String getHeader() {
		return " [OPTIONS] (source directory) (output directory)";
	}

	/**
	 * OVERRIDE IF YOU WANT TO PROVIDE PARAMETER INFORMATION to the command help.   Return null is you don't want to print a footer.
	 * @return a string that is the footer.
	 */
 	protected String getFooter() {
 		return "Run a render." + Constants.NEWLINE ;
	}
 	
	// =============================================================================================================\
	// == INTERNAL
 
	
}