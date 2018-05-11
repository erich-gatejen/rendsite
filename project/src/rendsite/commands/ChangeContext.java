/**
 * RendSite 2008
 * Copyright Erich P Gatejen 2008, 2009 
 */
package rendsite.commands;

import java.io.File;

import rendsite.Codes;
import rendsite.Constants;
import rendsite.RendsiteException;
import rendsite.engine.FileContext;
import rendsite.notice.FileNotice_ChangeFile;
import rendsite.notice.FileNotice_Notifier;
import things.common.ThingsNamespace;
import things.common.impl.WhoAmISimple;
import things.thinger.io.conduits.ConduitController;

/**
 * Change management context.
 * @author erich
 * <i>Version History</i>
 * <pre>
 * 10NOV09 - EPG - First package release.
 * </pre>
 */
public class ChangeContext implements Constants {
	
	// =============================================================================================================\
	// == FIELDS
	
	// =============================================================================================================\
	// == DATA
	private FileNotice_ChangeFile changeFileNotice;			// will finalize when it falls out of scope.
	private FileNotice_Notifier notifier;					// standard notifier.
	
	private FileContext fcontext;
	private ConduitController cc;
	
	// =============================================================================================================\
	// == METHODS
	
	/**
	 * Default constructor. 
	 * @throws Throwable
	 */
	public ChangeContext(ConduitController cc) throws Throwable {
		if (cc==null) RendsiteException.softwareProblem("Cannot construct a ChangeContext with a null cc.");
		this.cc = cc; 
		try {
			notifier = new FileNotice_Notifier(cc, new WhoAmISimple("ChangeSystem", "CS"));
		} catch (Throwable t) {
			RendsiteException.softwareProblem("Failed to construct the ChangeContext.  This is a bug.", t);
		}
	}
	
	/**
	 * Reset the context.  It'll dispose anything under management.  It is appropriate to call this when shutting down too (or wait for the finalizer to do 
	 * it for you).
	 * @throws Throwable
	 */
	public synchronized void reset() throws Throwable {
		
		try {
			
			// If we have created a change file,  kill it.
			if (changeFileNotice!=null) {
				changeFileNotice.dispose();
				changeFileNotice = null;		// Kick it out of scope.
			}
			
			// Detach notifications.
			if (fcontext!=null) fcontext.register(null);
			
		} catch (Throwable t) {
			throw new RendsiteException("Failed to reset the change context.", Codes.FAULT_CHANGE_CONTEXT_RESET, t);
		}
	}
	
	/**
	 * Create a change file.
	 * @param fcontext
	 * @param changeFile the actual file.  
	 * @throws Throwable
	 */
	public synchronized void createChangeFile(FileContext fcontext, File changeFile) throws Throwable {
		if (changeFile==null) RendsiteException.softwareProblem("Cannot Call createChangeFile with a null changeFile.");
		if (fcontext==null) RendsiteException.softwareProblem("Cannot Call createChangeFile with a null fcontext.");
		if (this.fcontext!=null) RendsiteException.softwareProblem("ChangeContext only allows one Change File active at a time."); 
		this.fcontext = fcontext;

		// Validate change file
		try {
			
			// Can we access it?
			if (!changeFile.createNewFile()) {
				if (!changeFile.canWrite()) throw new RendsiteException("Cannot write to change file.", Codes.PANIC_CHANGE_FILE__CANNOT_WRITE, ThingsNamespace.ATTR_PLATFORM_FILE_PATH, changeFile.getAbsolutePath());
			}	
			
		} catch (Throwable t) {
			throw new RendsiteException("Cannot access change file.", Codes.PANIC_CHANGE_FILE__CANNOT_ACCESS, t, ThingsNamespace.ATTR_PLATFORM_FILE_PATH, changeFile.getAbsolutePath());
		}

		// Set up sink
		try {
			fcontext.registerExemptFile(changeFile);
			changeFileNotice = new FileNotice_ChangeFile(cc, new WhoAmISimple("Changefile", "Changefile"), changeFile);
		} catch (Throwable t) {
			throw new RendsiteException("Cannot register change file processing.", Codes.PANIC_CHANGE_FILE, t, ThingsNamespace.ATTR_PLATFORM_FILE_PATH, changeFile.getAbsolutePath());
		}

		// Make sure the notifier is registered.
		fcontext.register(notifier);
		
	}


	// =============================================================================================================\
	// == INTERNAL
 	
	/**
	 * Finalizer.
	 * @throws Throwable for bugs.
	 */
	protected  void 	finalize() throws Throwable {
		reset();
	}
	
}