/**
 * RendSite 2008
 * Copyright Erich P Gatejen 2008, 2009 
 */
package rendsite.engine;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import rendsite.Codes;
import rendsite.Constants;
import rendsite.RendsiteConfiguration;
import rendsite.RendsiteException;
import rendsite.RendsiteProperties;
import rendsite.engine.node.Node_Directory;
import rendsite.engine.node.Node_File;
import rendsite.engine.node.Node_MetaFile;
import rendsite.tools.Toolbox;
import things.common.ThingsException;
import things.data.ThingsPropertyView;
import things.thinger.io.Logger;

/**
 * Main engine.
 * <pre>
 * 1- TIME is pushed on the stack.  It is then set to the most recent datetime from all the metafiles.
 * 2- SCOPE is pushed.
 * 3- (obsolete)
 * 4- FILE list is prepared.
 * 5- DIRECTORY list is prepared.
 * 6- DIRECTORYs processed.  Each is an entry into a new frame.
 * 7- FILEs are processed.  (Yes, seems silly, but its a forward thing.)
 * 8- Process the frame and the frame is exited.
 * 9- Clean, if configuration allows it.
 * 10- SCOPE is popped
 * 11- TIME is restored.
 * </pre>
 * @author  erich
 * <pre>
 * 10NOV09 - EPG - First package release.
 * 02APR10 - EPG - Allow gaps of up to 100 in the ply numbers.
 * </pre>
 */
public class Engine {

	// ==================================================================================================================
	// = FIELDS
	
	public final static int MAX_FAULTS_TO_HOLD = 1;
	public final static int MAX_GLOBAL_ERRORS_TO_HOLD = 50;
	public final static int MAX_PLY_GAP = 100;					// This might be a performance problem.  Something to watch.
	
	
	// ==================================================================================================================
	// = DATA
	
	// ----------------------------------------------------------------
	// - Data per run, protected by the synchronized

	//private RendsiteConfiguration configuration;
	//private FileContext fcontext;
	private PropertiesContext pcontext;
	//private RenderingContext rcontext;
	private Processor processor;
	private Logger logger;
	private List<RendsiteException> errors;
	private CategoryManager categoryManager;
	private int localFaultsHeld;
	private int globalFaultsHeld;
	private int globalErrorsHeld;
	
	// ==================================================================================================================
	// = METHODS

	/**
	 * Run.  This is the only way into the engine, so it is synchronized.  The data per run is safe because of this.  
	 * @param processor
	 * @param fcontext
	 * @param pcontext
	 * @param rcontext
	 * @param startDirectory
	 * @throws ThingsException
	 */
	public synchronized void run(RendsiteConfiguration configuration, Processor processor, FileContext fcontext, PropertiesContext pcontext, RenderingContext rcontext, Node_Directory startDirectory) throws ThingsException {
		if (configuration==null) RendsiteException.softwareProblem("Cannot run the engine with a null RendsiteConfiguration.");
		if (processor==null) RendsiteException.softwareProblem("Cannot run the engine with a null Processor.");
		if (fcontext==null) RendsiteException.softwareProblem("Cannot run the engine with a null FileContext.");
		if (pcontext==null) RendsiteException.softwareProblem("Cannot run the engine with a null PropertiesContext.");
		if (rcontext==null) RendsiteException.softwareProblem("Cannot run the engine with a null RenderingContext.");
		if (startDirectory==null) RendsiteException.softwareProblem("Cannot run the engine with a null startDirectory.");
		//this.configuration = configuration;
		this.processor = processor;
		//this.fcontext = fcontext;
		this.pcontext = pcontext;
		//this.rcontext = rcontext;
		
		// Data
		localFaultsHeld = 0;
		globalFaultsHeld = 0;
		globalErrorsHeld = 0;
		
		logger = configuration.getSystemInterface().getNamedLogger("RUN ");
		categoryManager = pcontext.getCategoryManager();
		
		// Data
		try {
			
			// Data for run.
			errors = new LinkedList<RendsiteException>();
			
			// Process the start directory.  This will cause the root to be scoped twice, 
			frame(startDirectory);
			
		} catch (ThingsException te) {
			throw te;
			
		} catch (Throwable t) {
			throw new ThingsException("Engine aborted due to spurious exception.  This is probibly a bug.  Please report it.", Codes.PANIC_RUN, t);
			
		} finally {
			
			// Print errors
			if (errors.size()>0) {
				logger.error("ERRORS during run.", Codes.ERROR_RUN, Constants.NAME_ERROR_NUMBER, Integer.toString(errors.size()));
				int errorNumber =1;
				for (RendsiteException error : errors) {
					logger.exception(error);	
					errorNumber++;
				}
			} else {
				logger.info("Completed with no errors.", Codes.INFO_COMPLETED_NO_ERRORS);
			}
		}

	}
	
	// ==================================================================================================================
	// = INTERNAL                                                                                       
	
	/**
	 * Enter a frame (basically, a directory).
	 * @param directory the directory info.
	 * @throws ThingsException Any exception that gets out is fatal to the run.
	 */
	private void frame(Node_Directory	directory) throws ThingsException {
		long previousTime;
		
		// Save the faults held for the previous frame and set it to 0 for this frame.  it will have to be restored in the finally clause below.
		// this really should be part of the stack operation.
		int entryFaultsHeld = localFaultsHeld;			
		localFaultsHeld = 0;
		
		if (logger.debuggingState()) logger.debug("Enter directory", Codes.DEBUG, Constants.NAME_FRAME_LOCATION, directory.getPath());
		
		// Initialization
		try {
			// Nothing right now.

		// } catch (ThingsException te) {
		//	 throw new ThingsException("Failed initialization", Codes.ERROR_FRAME_INITIALIZATION, te);
		 } catch (Throwable t) {
			RendsiteException.softwareProblem("Failed initialization.", t);
		}
		
		// 1- TIME is pushed on the stack.  It is then set to the most recent datetime from all the metafiles.
		// Only PANIC level problems should come out.
		 previousTime = TIME_1(directory); 
	
		// Try encapsulates anything that would require a cleaning the scope.
		// Only FAULTS should be let out of any item below.  A fault will break this directory, but none below it.
		try {
			
			// ----------------------------------------------------------------
			// 2- SCOPE
			pcontext.scope(directory);
					
			// Process the directory - this will create the path and whatnot.
			processor.process(directory);			
			
			// Make sure the subdirectory has set it's catalog name.
			pcontext.GET_CATALOG_NAME(directory);
			
			// ----------------------------------------------------------------
			// 3- FILE list is prepared.	
			List<Node_File> files = FILE_3(directory);
			
			// ----------------------------------------------------------------
			// 4- DIRECTORY list is prepared.
			List<Node_Directory> directories =  DIRECTORY_4(directory);
		
			// ----------------------------------------------------------------
			// 6- DIRECTORYs processed.  Each is an entry into a new frame.
			for (Node_Directory subdir : directories) {
				DIRECTORY_6(directory, subdir);
			}
			 
			// ----------------------------------------------------------------
			// 7- FILEs processed.  (Yes, seems silly, but its a forward thing.)
			for (Node_File file : files) {
				FILE_7(file, directory);
			}
			
			// ----------------------------------------------------------------
			// 8- Process the frame and the frame is exited.	
			if (!directory.copyOnly) processor.processCatalog(directory);
			
			// ----------------------------------------------------------------
			// 9- Clean	
			if (!pcontext.configuration.isDontClean()) directory.clean();

		} catch (ThingsException te) {
			
			// Is it a panic or bug?
			if ( te.isWorseThanFault() || RendsiteException.isBug(te.getRootCause()) ) {
				throw te;
				
			} else  {	
				RendsiteException actualException;
				
				if (!te.getAttributesReader().hasAttribute(Constants.NAME_FRAME_LOCATION))
					te.addAttribute(Constants.NAME_FRAME_LOCATION, directory.getURLRendering());
				te.addAttribute(Constants.NAME_BASE_FRAME_LOCATION, directory.getPath());
				
				if (te.isWorseThanError()) {
					
					// Fix it
					if (te instanceof RendsiteException)
						actualException  = (RendsiteException)te;
					else 
						actualException = new RendsiteException("Fault while processing directory.", Codes.FAULT_IN_DIRECTORY, te);

					// FAULT.  Did we hit our limit?
					errors.add(actualException);	
					logger.exception(actualException);
					
					// Did we hit our limit?
					if (globalFaultsHeld>MAX_FAULTS_TO_HOLD) {
						//YES.  PANIC.	
						throw new RendsiteException("Fault processing directory.  Exceeded allowable faults.", Codes.PANIC_PROCESSING_DIRECTORY__EXCEEDED_ALLOWABLE, te, Constants.NAME_FAULT_NUMBER_ALLOWED, Integer.toString(MAX_FAULTS_TO_HOLD), Constants.NAME_FRAME_LOCATION, directory.getURLRendering(), Constants.NAME_BASE_FRAME_LOCATION, directory.getPath());
					}
					
					// We've broken this frame, but that'll be the end of it. 
					globalFaultsHeld++;
					
				} else {
					
					// Fix it
					if (te instanceof RendsiteException)
						actualException  = (RendsiteException)te;
					else 
						actualException = new RendsiteException("Error during processing.", Codes.ERROR_IN_FILE, te);

					// FAULT.  Did we hit our limit?
					errors.add(actualException);	
					logger.exception(actualException);
					
					// ERROR.  Did we hit our limit?
					errors.add(actualException);
					if (globalErrorsHeld>MAX_GLOBAL_ERRORS_TO_HOLD) {
						//YES.  PANIC.
						throw new RendsiteException("Error processing directory.  Exceeded allowable errors.", Codes.PANIC_PROCESSING_FILE__EXCEEDED_ALLOWABLE, te, Constants.NAME_ERROR_NUMBER_ALLOWED, Integer.toString(MAX_GLOBAL_ERRORS_TO_HOLD), Constants.NAME_FRAME_LOCATION, directory.getURLRendering(), Constants.NAME_BASE_FRAME_LOCATION, directory.getPath());
					}
					
					// Add to errors help
					globalErrorsHeld++;
				}
			} 
			
		} catch (Throwable t) {
			// Not recoverable.  Go ahead and panic.
			throw new ThingsException("Engine aborted due to spurious exception.  This is probibly a bug.  Please report it.", Codes.PANIC_RUN, t);
			
		} finally {
			
			// ----------------------------------------------------------------
			// 10- SCOPE is popped
			// Any exception here will a PANIC with takes precedence over all other  exceptions.
			try {
				pcontext.unscope();
			} catch (ThingsException tte) {
				throw tte;
			} catch (Throwable tt) {
				RendsiteException.softwareProblem("Spurious exception while unscoping.", tt);
			}
			
			// ----------------------------------------------------------------
			// 11- TIME is restored.
			processor.setConfigurationTime(previousTime);

			// Restore faults held.
			localFaultsHeld = entryFaultsHeld;
		}
		
		// Done with frame!
		if (logger.debuggingState()) logger.debug("Exit directory successfully", Codes.DEBUG, Constants.NAME_FRAME_LOCATION, directory.getPath());
		
	}
	
	/**
	 * TIME_1 block. 
	 * @param directory the processed directory.
	 * @return the current config time (to be saved).
	 * @throws ThingsExceptionn only PANICs, ever.
	 */
	private long TIME_1(Node_Directory	directory) throws ThingsException {
		long oldNewestTime  = 0;
		try {
			long filetime;
			
			// Get current setting to be saved.
			oldNewestTime = processor.getConfigurationTime();
			long currentNewestTime  = oldNewestTime;
			
			// See if any of the metafiles are newer
			for (Node_MetaFile mfile : directory.getLocalMetaFiles()) {
				filetime = mfile.file.lastModified();
				if (filetime > currentNewestTime) currentNewestTime = filetime;
			}
			for (Node_MetaFile mfile : directory.getMetaFiles()) {
				filetime = mfile.file.lastModified();
				if (filetime > currentNewestTime) currentNewestTime = filetime;
			}
			
			// Set it as the current
			processor.setConfigurationTime(currentNewestTime);
			
		} catch (Throwable t) {
			throw new RendsiteException("TIME_1 : Failed to set TIME.", Codes.PANIC_SETUP__FRAME_TIME, t);
		}
		return oldNewestTime;
	}
	
	/**
	 * FILE_3 block.  FILE list is prepared.
	 * @param directory the processing directory.
	 * @throws ThingsException only FAULTs or PANICs.  It will (likely) not have information about the current directory.
	 * @return the list of files.
	 */
	private List<Node_File> FILE_3(Node_Directory	directory) throws ThingsException {
		
		// Create the list we'll use during processing.  Configured files go first how they appear in the config and then what is left in natural order.
		List<Node_File> newFileList = new LinkedList<Node_File>();
		
		try {
	
			// Create catalog for lookup.  This will cleanse the directory of any excluded files.
			HashMap<String, Node_File> files = new HashMap<String, Node_File>();
			for (Node_File file : directory.getFiles()) {
				if (pcontext.IGNORED(file)) {
					if (logger.debuggingState()) logger.debug("Ignore a file.", Codes.DEBUG, Constants.NAME_FILE_SOURCE, file.getURL(false));
				} else {
					files.put(file.getName(),file);
					
					// Is it excluded from the catalog?
					if(pcontext.EXCLUDED(file)) {
						if (logger.debuggingState()) logger.debug("Exclude a file from the catalog.",  Codes.DEBUG, Constants.NAME_FILE_SOURCE, file.getURL(false));
						file.excluded = true;
					}
				}
			} 
			
			// Work plies first meaning they have been configured by number in a LOCAL metafile.
			int ply = 0;
			int gap = 0;
			ThingsPropertyView itemView;
			String plyName;
			Node_File newFile;
			do {
				// Get the ply by number.
				itemView = pcontext.viewLocalFilesPly(ply+1);
				ply++;	// For the next iteration..
				plyName = itemView.getProperty(RendsiteProperties.PROP_FILE_NAME);
				if (plyName==null) {
					
					// Ply not configured.
					if (gap >= MAX_PLY_GAP) break;	// Exceeded the allowed gap.
					gap++;
					
				} else {
				
					// Ply is configured.  Does this file actually exist?
					if (files.containsKey(plyName)) {
			
						// Yes
						newFile = files.remove(plyName);
						
						// The ply has info
						newFile.description = itemView.getProperty(RendsiteProperties.PROP_FILE_DESCRIPTION);		
						newFile.type = Toolbox.pickNotNullNorBlank(itemView.getProperty(RendsiteProperties.PROP_FILE_TYPE), null); 	
						newFile.category = categoryManager.get(itemView.getProperty(RendsiteProperties.PROP_FILE_CATEGORY)); 
						newFile.setRenderingType(RenderingType.match(itemView.getProperty(RendsiteProperties.PROP_FILE_RENDERING_TYPE)));
						
						// Add it to the list
						newFileList.add(newFile);
						
					} else {
						throw new RendsiteException("File configured but not present in directory.", Codes.FAULT_METAFILE__FILE_CONFIGD_BUT_NOT_PRESENT, Constants.NAME_FILE_EXCPECTED, plyName);
					}
				}
				
			} while (true);			
			
			// Add anything without information - it'll still be in the map.
			for (Node_File fileNoInfo : files.values()) {
				newFileList.add(fileNoInfo);
			}
			
			// Set the directory to the new list.  
			directory.setFiles(newFileList);	
		
		} catch (ThingsException te) {	
			if (te.isWorseThanError()) {
				throw te;
			} else {
				// Promote to fault.
				throw new RendsiteException("Fault while processing files.", Codes.FAULT_IN_DIRECTORY_FILES, te);
			}
		} catch (Throwable t) {
			throw new ThingsException("Spurious exception while processing files.", Codes.PANIC_RUN_SPURIOUS, t);
		}
			
		// Done
		return newFileList;
		
	}
	
	/**
	 * DIRECTORY_4 block.  DIRECTORY list is prepared.
	 * @param directory the processing directory.
	 * @throws ThingsException only FAULTs or PANICs.  It will (likely) not have information about the current directory.
	 * @return the list of directories.
	 */
	private List<Node_Directory> DIRECTORY_4(Node_Directory	directory) throws ThingsException {
		
		// Create the list we'll use during processing.  Configured directories go first how they appear in the config and then what is left in natural order.
		List<Node_Directory> newDirectoryList = new LinkedList<Node_Directory>();
		
		try {
			
			// Create catalog for lookup.  This will cleanse the directory of any excluded subdirectories.
			// directory.copy is inherited by all sub directories.  
			HashMap<String, Node_Directory> directories = new HashMap<String, Node_Directory>();
			for (Node_Directory dir : directory.getSubDirectories()) {
				if (pcontext.IGNORED(dir)) {
					// Mark as excluded and do not put in the processing list.  This is the end of the road for the directory, so
					// no other attribute work is necessary.
					dir.ignored = true;
					if (logger.debuggingState()) logger.debug("Ignore a directory.", Codes.DEBUG, Constants.NAME_DIRECTORY, dir.getURLAbsolute(false));
				} else {
					// Put it in the processing list.
					directories.put(dir.getName(),dir);
					
					// Is it excluded from the catalog?
					if(pcontext.EXCLUDED(dir)) {
						if (logger.debuggingState()) logger.debug("Exclude a directory from the catalog.",  Codes.DEBUG, Constants.NAME_DIRECTORY, dir.getURLAbsolute(false));
						dir.excluded = true;
					}
					
					// Is it copy only by setting or by inheritance?
					if((directory.copyOnly==true)||(pcontext.COPY_ONLY(dir))) {
						dir.copyOnly = true;
					}
				}
			}	
			
			// Work plies first meaning they have been configured by number in a LOCAL metafile.
			int ply = 0;
			ThingsPropertyView itemView;
			String plyName;
			Node_Directory newDirectory;
			do {
				// Get the ply by number.
				itemView = pcontext.viewLocalDirectoriesPly(ply+1);
				plyName = itemView.getProperty(RendsiteProperties.PROP_DIRECTORY_NAME);
				if (plyName==null) break;
				
				// Does this file actually exist?
				if (directories.containsKey(plyName)) {
					// Yes
					newDirectory = directories.remove(plyName);
					
					// The ply has info
					newDirectory.description = itemView.getProperty(RendsiteProperties.PROP_DIRECTORY_DESCRIPTION);				
					newDirectory.type = itemView.getProperty(RendsiteProperties.PROP_DIRECTORY_TYPE);	
					newDirectory.setCatalogName(itemView.getProperty(RendsiteProperties.PROP_DIRECTORY_CATALOG));				// Forced local configuration for catalog name.
					
					// Add it to the list
					newDirectoryList.add(newDirectory);
					
				} else {
					throw new RendsiteException("Directory configured but not present in directory.", Codes.FAULT_METAFILE__DIR_CONFIGD_BUT_NOT_PRESENT, Constants.NAME_FILE_EXCPECTED, plyName);
				}
				ply++;
				
			} while (true);			
			
			// Add anything without information - it'll still be in the map.
			for (Node_Directory dirNoInfo : directories.values()) {
				newDirectoryList.add(dirNoInfo);
			}			
			
			// Set the directory to the new list.  
			directory.setSubDirectories(newDirectoryList);
			
		} catch (ThingsException te) {	
			if (te.isWorseThanError()) {
				throw te;
			} else {
				// Promote to fault.
				throw new RendsiteException("Fault while processing subdirectories.", Codes.FAULT_IN_DIRECTORY_DIRECTORIES, te);
			}
		} catch (Throwable t) {
			throw new ThingsException("Spurious exception while processing subdirectories.", Codes.PANIC_RUN_SPURIOUS, t);
		}
			
		// Done
		return newDirectoryList;	
	}
	
	/**
	 * DIRECTORY 6 processed.  Each is an entry into a new frame.
	 * @param dir the containing directory.
	 * @param subDirectory the new subdirectory that we are processing.
	 * @throws ThingsException only FAULTS or PANICs, ever.  If we get more than one FAULT from the subdirectories, we'll go ahead and let a FAULT out.  Othwerise, only a PANIC will get out.
	 */
	private void DIRECTORY_6(Node_Directory	dir, Node_Directory	subDirectory) throws ThingsException {
	
		try {
			
			// Call the sub
			frame(subDirectory);
			
			// Propagate the last modified from the sub if it is newer.
			dir.modifiedTime(subDirectory.getMostRecentModified());
	
		} catch (ThingsException te) {	
			reportExceptionDirectory(te, subDirectory.getPath());
		} catch (Throwable t) {
			throw new ThingsException("Spurious exception while processing directory.", Codes.PANIC_RUN_SPURIOUS, t, Constants.NAME_FRAME_LOCATION, subDirectory.getPath());
		}	
	}
		
	/**
	 * FILE_7 processed.  Process the file.
	 * @param file the file to process
	 * @param dir the owning directory.
	 * @param result the result file
	 * @throws ThingsExceptionn only FAULTS or PANICs, ever.
	 */
	private void FILE_7(Node_File file, Node_Directory dir) throws ThingsException {
		try {
			long lastModified = processor.process(file);
			dir.modifiedTime(lastModified);						// Two lines for debugging purposes.
			
		} catch (ThingsException te) {
			reportExceptionFile(te, dir.getPath(), file.getURLRendering(false));
		} catch (Throwable t) {
			throw new ThingsException("Spurious exception while processing directory.", Codes.PANIC_RUN_SPURIOUS, t, Constants.NAME_FILE_SOURCE, file.getURLRendering(false));
		}
	}

	/**
	 * Report an error or fault in a directory.  Panics will always just propagate.  Faults will propagate if we busted the max.  
	 * Otherwise, it will not propagate.  the faultsHeld value will be incremented.  I'm sure this method could be reduced, but that'll have to happen later.
	 * @param te the cause.
	 * @param frameLocation where did it happen?
	 * @param fileName if null, it assumes it was a directory problem.
	 * @throws ThingsException which will be a PANIC because of a bug only.
	 */
	private void reportExceptionDirectory(ThingsException te, String frameLocation) throws ThingsException {
	
		try {
			RendsiteException  actualException;
			
			if (te.isWorseThanFault()) {
				te.addAttribute(Constants.NAME_DIRECTORY_FAULT, frameLocation);
				//if (te.numeric==Codes.PANIC_PROCESSING_DIRECTORY) throw te;	
				if (te instanceof RendsiteException) throw te;
				throw new RendsiteException("Panic processing directory.", Codes.PANIC_PROCESSING_DIRECTORY, te);
			
			} else if (te.isWorseThanError()) {
				te.addAttribute(Constants.NAME_DIRECTORY_FAULT, frameLocation);
				
				// Just report it.
				if (te instanceof RendsiteException)
					actualException  = (RendsiteException)te;
				else 
					actualException = new RendsiteException("Fault processing directory.", Codes.FAULT_IN_DIRECTORY, te);
				logger.exception(actualException);	
				errors.add(actualException);	

				// FAULT.  Did we hit our limit?
				if (localFaultsHeld>MAX_FAULTS_TO_HOLD) {
					throw new RendsiteException("Fault processing directory.  Exceeded allowable faults.", Codes.PANIC_PROCESSING_DIRECTORY__EXCEEDED_ALLOWABLE, reportGetCause(te), Constants.NAME_FRAME_LOCATION, frameLocation, Constants.NAME_FAULT_NUMBER_ALLOWED, Integer.toString(MAX_FAULTS_TO_HOLD));
				}
				
				// Count it.
				localFaultsHeld++;
				
			} else {
				te.addAttribute(Constants.NAME_DIRECTORY_ERROR, frameLocation);
				
				// ERROR, just report it.
				if (te instanceof RendsiteException)
					actualException  = (RendsiteException)te;
				else 
					actualException = new RendsiteException("Error processing directory.", Codes.ERROR_IN_DIRECTORY, te);
				logger.exception(actualException);	
				errors.add(actualException);	
			}
				
		} catch (ThingsException tte) {
			throw tte;
		} catch (Throwable t) {
			throw new ThingsException("Spurious exception in reportExceptionDirectory.  This is a bug", Codes.PANIC_BUG, t);
		}
	}
	
	/**
	 * Report an error or fault for a file.  Panics will always just propagate.  Faults will propagate if we busted the max.  
	 * Otherwise, it will not propagate.  the faultsHeld value will be incremented.  I'm sure this method could be reduced, but that'll have to happen later.
	 * @param te the cause.
	 * @param frameLocation where did it happen?
	 * @param fileName if null, it assumes it was a directory problem.
	 * @throws ThingsException which will be a PANIC because of a bug only.
	 */
	private void reportExceptionFile(ThingsException te, String frameLocation, String fileName) throws ThingsException {
		
		try {
			RendsiteException  actualException;
			
			if (te.isWorseThanFault()) {
				te.addAttribute(Constants.NAME_FILE_FAULT, fileName);
				te.addAttribute(Constants.NAME_DIRECTORY_FAULT, frameLocation);
				//if (te.numeric==Codes.PANIC_PROCESSING_DIRECTORY) throw te;	
				if (te instanceof RendsiteException) throw te;
				throw new RendsiteException("Panic processing file.", Codes.PANIC_PROCESSING_FILE, te);
			
			} else if (te.isWorseThanError()) {
				te.addAttribute(Constants.NAME_FILE_FAULT, fileName);
				te.addAttribute(Constants.NAME_DIRECTORY_FAULT, frameLocation);
				
				// Just report it.
				if (te instanceof RendsiteException)
					actualException  = (RendsiteException)te;
				else 
					actualException = new RendsiteException("Fault processing file.", Codes.FAULT_IN_FILE, te);
				logger.exception(actualException);	
				errors.add(actualException);	

				// FAULT.  Did we hit our limit?
				if (localFaultsHeld>MAX_FAULTS_TO_HOLD) {
					throw new RendsiteException("Fault processing file.  Exceeded allowable faults.", Codes.PANIC_PROCESSING_FILE__EXCEEDED_ALLOWABLE, reportGetCause(te), Constants.NAME_FRAME_LOCATION, frameLocation, Constants.NAME_FAULT_NUMBER_ALLOWED, Integer.toString(MAX_FAULTS_TO_HOLD));
				}
				
				// Count it.
				localFaultsHeld++;
				
			} else {
				te.addAttribute(Constants.NAME_FILE_ERROR, fileName);
				te.addAttribute(Constants.NAME_DIRECTORY_ERROR, frameLocation);
				
				// ERROR, just report it.
				if (te instanceof RendsiteException)
					actualException  = (RendsiteException)te;
				else 
					actualException = new RendsiteException("Error processing file.", Codes.ERROR_IN_FILE, te);
				logger.exception(actualException);	
				errors.add(actualException);	
			}
				
		} catch (ThingsException tte) {
			throw tte;
		} catch (Throwable t) {
			throw new ThingsException("Spurious exception in reportExceptionFile.  This is a bug", Codes.PANIC_BUG, t);
		}
	}

	/**
	 * Filter the actual reported exception.  We do this to trim the cause tree.
	 * @param cause
	 * @return the exception.
	 */
	private ThingsException reportGetCause(ThingsException cause) {
		ThingsException result = cause;
		switch (cause.numeric) {
		case Codes.PANIC_RENDERER__PROCESS_FILE:
		case Codes.PANIC_RENDERER__CHECK_FILE:
		case Codes.PANIC_RENDERER__PROCESS_DIRECTORY:
		case Codes.PANIC_RENDERER__CATALOG:
		case Codes.FAULT_RENDERER__PROCESS_FILE:
		case Codes.FAULT_RENDERER__CHECK_FILE:
		case Codes.FAULT_RENDERER__PROCESS_DIRECTORY:
		case Codes.FAULT_RENDERER__CATALOG:
			Throwable root = cause.getCause();
			if ( (root!=null) && (root instanceof ThingsException) ) result = (ThingsException)root;
			break;
		}
		return result;
	}
	
}



