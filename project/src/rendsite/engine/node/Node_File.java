/**
 * RendSite 2008
 * Copyright Erich P Gatejen 2008, 2009 
 */
package rendsite.engine.node;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.HashMap;

import rendsite.Codes;
import rendsite.Constants;
import rendsite.RendsiteException;
import rendsite.engine.Category;
import rendsite.engine.FileContext;
import rendsite.engine.FileInterface;
import rendsite.engine.RenderingType;
import things.common.ThingsException;
import things.common.tools.FileTools;


/**
 * Crawler node for a File.
 * @author  erich
 * <i>Version History</i>
 * <pre>
 * 10NOV09 - EPG - First package release.
 * </pre>
 */
public class Node_File extends Node implements FileInterface {

	// ==================================================================================================================
	// = ATTRIBUTES
	
	//  -------------------------------------------------------------------------------------------------------------
	// Attributes used during processing.  The engine will set all these before any processing has a chance to touch them.
	public boolean copyOnly;		// Copy but to not process.
	public boolean ignored;			// Do not process at all.
	public boolean excluded;		// Do not put in a catalog.
	
	/**
	 * Information about the file.  If the 'description' is null, there is no other info about the file.
	 */
	public String description = null;
	
	/**
	 * Type may be configured per file, but if not present the renderer may decide a type based on other information.  
	 * In processing and rendering, configured type will always supersede type inferred from other sources.  If it is null or blank,
	 * then its type is not known through any means.
	 */
	public String type;							
	
	/**
	 * Rendering Type may be configured per file.  If it is not, the get method will see if a category can give a rendering type.  If it can't the get will return OTHER.
	 * In processing and rendering, configured type will always supersede type inferred from other sources (including category).  If it is null or blank,
	 * then its type is not known through any means.
	 */
	public RenderingType renderingType = null;	
	
	/**
	 * Unlike type, category must not be inferred.  It must be explicitly configured per file.  It will be used to derive a type, if type is not available.
	 * If it is null its category is not known.
	 */
	public Category category = null;
	
	// ==================================================================================================================
	// = DATA
	
	/**
	 * Source FS file.  It will be set even if this is a neighbor.
	 */
	private File sourceFile;
	
	/**
	 * Source Node Directory that contains this file.
	 */
	private Node_Directory containingDir;

	/**
	 * File.  This will only be set after the first time it is requested.
	 */
	private File destinationFile;
	
	/**
	 * Neighbor files.  These are files created by the rendering process.
	 */
	private HashMap<String, Node_File> neighborFiles = null;
	
	/**
	 * The file that should be cataloged.
	 */
	private Node_File catalogedFile;
	
	/**
	 * The file extension (cached).
	 */
	private String extension;
	
	/**
	 * Normal files have this set to null.  If this file is a neighbor, its origin file will be this.
	 */
	private Node_File neighborOrigin;
	
	/**
	 * The file context.
	 */
	private FileContext fcontext;


	// ==================================================================================================================
	// = METHODS
		
	/**
	 * Create a node.
	 * @param sourceFile the source FS file.
	 * @param containingDir the source Node Directory containing this file.
	 * @param fcontext the file context.
	 * @throws Throwable mostly for bugs.
	 */
	public Node_File(File sourceFile, Node_Directory containingDir, FileContext fcontext) throws Throwable {
		super(NodeType.FILE);
		constructor(sourceFile, containingDir, fcontext);
	}
	
	/**
	 * Create a node.
	 * @param sourceFile the source FS file.
	 * @param containingDir the source Node Directory containing this file.
	 * @param fcontext the file context.
	 * @param neighborOrigin set this to the file for which this is a neighbor.
	 * @throws Throwable mostly for bugs.
	 */
	public Node_File(File sourceFile, Node_Directory containingDir, FileContext fcontext, Node_File neighborOrigin) throws Throwable {
		super(NodeType.FILE);
		
		if (neighborOrigin==null) RendsiteException.softwareProblem("You cannot have a null neighborOrigin when calling  Node_File(File sourceFile, Node_Directory containingDir, FileContext fcontext, Node_File neighborOrigin) constructor.");
		this.neighborOrigin = neighborOrigin;
		constructor(sourceFile, containingDir, fcontext);
	}
	
	/**
	 * Construction common code.
	 * @param sourceFile the source FS file.
	 * @param containingDir the source Node Directory containing this file.
	 * @param fcontext the file context.
	 * @throws Throwable mostly for bugs.
	 */
	private void constructor(File sourceFile, Node_Directory containingDir, FileContext fcontext) throws Throwable {
		if (fcontext==null) RendsiteException.softwareProblem("You cannot have a null fcontext.");
		if (sourceFile==null) RendsiteException.softwareProblem("You cannot have a null file.");
		this.sourceFile = sourceFile;
		if (containingDir==null) RendsiteException.softwareProblem("You cannot have a null containingDir.");
		this.containingDir = containingDir;
		
		// Cache the extension, if it has one.
		extension = snipExtension(sourceFile);
		catalogedFile = this;
		this.fcontext = fcontext;
		
		neighborFiles =  new HashMap<String, Node_File>(); 
		
	}
	
	/**
	 * Get the destination file.  Generate it if necessary.  
	 * @return the destination file.
	 * @throws Throwable
	 */
	private File getDestinationFile() throws Throwable {
		if (destinationFile==null) {
			destinationFile = fcontext.getOutputFile(sourceFile.getAbsolutePath());
		}
		return destinationFile;
	}
	
	// ==================================================================================================================
	// = FILE INTERFACE
	
	/**
	 * Open for the file for reading.  The stream will not be buffered.  You should close it when you are done.
	 * @return an input stream for reading.
	 * @throws ThingsException
	 */
	public InputStream openForRead() throws ThingsException {
		InputStream result = null;
		
		// Neighbors can't be read!  They don't exist in the source.
		if (isNeighbor()) throw new ThingsException("Cannot open a neighbor for read (since it doesn't exist).", Codes.FAULT_NEIGHBOR_OPENED_FOR_READ, Constants.NAME_FILE_SOURCE, sourceFile.getAbsolutePath());
		
		try {
			result = new FileInputStream(sourceFile);
		} catch (Throwable t) {
			throw new ThingsException("Failed to open source file.", Codes.FAULT_IN_FILE_COULD_NOT_OPEN, t, Constants.NAME_FILE_SOURCE, sourceFile.getAbsolutePath());
		}
		return result;
	}
	
	/**
	 * Open for the file for writing.  The stream will not be buffered.  You should close it when you are done.
	 * It will mark the file as having been updated for other processing.
	 * @return an output stream for writing.
	 * @throws ThingsException
	 */
	public OutputStream openForWrite() throws ThingsException {
		OutputStream result = null;
		try {
			File destFile = getDestinationFile();
			result = new FileOutputStream(destFile);
			fcontext.notifyChange(destFile.getAbsolutePath(), this);
		} catch (Throwable t) {
			throw new ThingsException("Failed to open destination file for writing.", Codes.ERROR_IN_FILE_COULD_NOT_OPEN, t, Constants.NAME_FILE_DESTINATION, destinationFile.getAbsolutePath());
		}
		return result;
	}
	
	/**
	 * Not that the file write failed.  The last modified date will be set to 1 (one millisecond after epoch) and the filecontext will be notified.
	 * @throws ThingsException will only happen for bugs, which will be a panic.
	 */
	public void failWrite() throws Throwable {
		try {
			File theFile = getDestinationFile();
			if (theFile.canWrite())	theFile.setLastModified(1);
			fcontext.notifyChangeFailed(sourceFile.getAbsolutePath(), this);
		} catch (Throwable t) {
			throw new ThingsException("Could not failWrite() in Node_File.  This is a bug.", Codes.PANIC_RUN, t, Constants.NAME_FILE_DESTINATION, destinationFile.getAbsolutePath());
		}
	}
	
	/**
	 * Open a neighbor file in the destination for writing.  The stream will not be buffered.  You should close it when you are done.
	 * It will mark the file as having been updated for other processing.  If the file already exists, it will be overwritten.
	 * @param neighbor the neighbor file name.  This name must be unique for the neighborhood (containing directory) and can be used for other methods.
	 * @param isCataloged will this file be cataloged?
	 * @return an output stream for writing.
	 * @throws ThingsException
	 */
	public OutputStream openNeighborForWrite(String neighbor, boolean isCataloged) throws ThingsException {
		if (neighbor==null) RendsiteException.softwareProblem("You cannot have a null neighbor.");
		if ((neighbor.indexOf('/')>=0)||(neighbor.indexOf('\\')>=0)) RendsiteException.softwareProblem("neighbor cannot be a path--only a file name.  path=" + neighbor);
		String theNeighbor = neighbor.trim();
		if (neighborFiles.containsKey(theNeighbor)) throw new ThingsException("Neighbor already defined for this file.", Codes.FAULT_NEIGHBOR_ALREADY_DEFINED, Constants.NAME_FILE, theNeighbor, Constants.NAME_FILE_SOURCE, sourceFile.getAbsolutePath());
		OutputStream result = null;
		
		try {
			
			// Create the neighbor and register it as such
			File nFile = new File(containingDir.getPath() + File.separatorChar + theNeighbor);	
			Node_File newNeighbor = new Node_File(nFile, containingDir, fcontext, this);
			neighborFiles.put(theNeighbor,newNeighbor);
			
			// Get the stream
			result = newNeighbor.openForWrite();
			//fcontext.notifyChange(nFile.getAbsolutePath(), newNeighbor);
			
		} catch (Throwable t) {
			throw new ThingsException("Failed to open neighbor file for writing.", Codes.ERROR_IN_FILE_COULD_NOT_OPEN, t, Constants.NAME_FILE_DESTINATION, destinationFile.getAbsolutePath());
			
		}
		return result;
	}

	/**
	 * Assert that a neighbor exists.  If the file does not exist, it will cause an exception.
	 * @param neighbor the neighbor file name.  This name must be unique for the neighborhood (containing directory) and can be used for other methods.
	 * @param isCataloged will this file be cataloged?
	 * @throws ThingsException
	 */
	public void assertNeighbor(String neighbor, boolean isCataloged) throws ThingsException {
		if (neighbor==null) RendsiteException.softwareProblem("You cannot have a null neighbor.");
		if ((neighbor.indexOf('/')>=0)||(neighbor.indexOf('\\')>=0)) RendsiteException.softwareProblem("neighbor cannot be a path--only a file name.  path=" + neighbor);
		String theNeighbor = neighbor.trim();
		if (neighborFiles.containsKey(theNeighbor)) throw new ThingsException("Neighbor already defined for this file.", Codes.FAULT_NEIGHBOR_ALREADY_DEFINED, Constants.NAME_FILE, theNeighbor, Constants.NAME_FILE_SOURCE, sourceFile.getAbsolutePath());
		
		try {
			
			// Create the neighbor and register it as such
			File nFile = new File(containingDir.getPath() + File.separatorChar + theNeighbor);	
			Node_File newNeighbor = new Node_File(nFile, containingDir, fcontext, this);
			
			// Check
			File destFile = newNeighbor.getDestinationFile();
			if (!destFile.exists()) throw new Exception("File does not exist.");
			
			// remember it
			neighborFiles.put(theNeighbor,newNeighbor);
			
		} catch (Throwable t) {
			throw new ThingsException("Failed to assert neighbor.", Codes.ERROR_IN_FILE_COULD_NOT_ASSERT__NEIGHBOR, t, Constants.NAME_FILE_DESTINATION, destinationFile.getAbsolutePath());
			
		}
	}
	
	/**
	 * Set a neighbor as the catalog file instead of this.  
	 * @param neighbor the neighbor file name.  This name must be unique for the neighborhood (containing directory) and can be used for other methods.  It is case sensitive.
	 * @throws ThingsException this will always happen if the neighbor has not been created through a openNeighborForWrite() call.
	 */
	public void setCatalogNeighbor(String neighbor) throws ThingsException { 
		if (neighbor==null) RendsiteException.softwareProblem("You cannot have a null neighbor.");
		Node_File theFile = neighborFiles.get(neighbor.trim());
		if (theFile==null) throw new ThingsException("Neighbor not defined", Codes.FAULT_NEIGHBOR_NOT_DEFINED, Constants.NAME_FILE, neighbor.trim());
		catalogedFile = theFile;
	}
	
	/**
	 * Get the last modified date for the file.  If it is a neighbor, it'll get it for its origin.
	 * @return the last modified date as milliseconds from epoch.
	 * @throws ThingsException
	 */
	public long lastModified() throws ThingsException {
		File theFile = null;
		if (isNeighbor()) theFile = neighborOrigin.sourceFile;
		else theFile = sourceFile;
		return theFile.lastModified();
	}
	
	/**
	 * Get the last modified date for the destination file.  If it is a neighbor, it'll get it for its origin.
	 * @return the last modified date as milliseconds from epoch or 0 if the file does not exist.
	 * @throws ThingsException
	 */
	public long lastModifiedDestination() throws ThingsException {
		File theFile;
		try {
			theFile = getDestinationFile();
		} catch (Throwable t) {
			throw new ThingsException("Could not getthe defination file.  This is a bug.", Codes.PANIC_FILE_SPURIOUS, t);		
		}
		return getLastModified(theFile);
	}
	
	/**
	 * Get all the neighbors.
	 * @return the neighbors.
	 */
	public Collection<Node_File> getNeighbors() {
		return neighborFiles.values();
	}
	
	// ==================================================================================================================
	// = GENERAL METHODS
	
	/**
	 * Get the rendered name.  It'll be nothing or overridden by a sub class.
	 * @return the name.  DO NOT USE THIS TO OPEN A FILE!
	 */
	public String getName() {
		if (sourceFile==null) return "";
		return sourceFile.getName();
	}
	
	/**
	 * Get file extension.
	 * @return the file extension or a blank string.
	 */
	public String getFileExtension() {
		return extension;
	}
	
	/**
	 * Set the rendering type.  This should only be done by configuration.
	 * @param rType the rendering type.  null is acceptable.
	 */
	public void setRenderingType(RenderingType rType) {
		renderingType = rType;
	}
	
	/**
	 * Get the cataloged file.
	 * @return the cataloged file.
	 */
	public Node_File getCatalogedFile() {
		return catalogedFile;
	}
	
	/**
	 * Is this is a neighbor file?  If it is, the source doesn't actually exist.  It should only be opened for writing.
	 * @return true if a neighbor, otherwise false.
	 */
	public boolean isNeighbor() {
		if (neighborOrigin==null) return false;
		return true;
	}
	
	/**
	 * Get the URL for this file.
	 * @param encode encode the URL properly.  Set to false for it to be display worthy.
	 * @return the URL
	 * @throws Throwable if there is a problem forming the URL or a bug.
	 */
	public String getURL(boolean encode) throws Throwable {
		// Yield the source URL if present.  Otherwise, try the destination, in case this is a neighbor.
		if (sourceFile!=null) return fcontext.getURL(containingDir, containingDir, sourceFile.getName(), encode);
		if (destinationFile!=null) return fcontext.getURL(containingDir, containingDir, destinationFile.getName(), encode);
		RendsiteException.softwareProblem("getURL() called when neither sourceFile nor destinationFile is set");
		return null;		// This will never happen.
	}
	
	/**
	 * Get the rendering URL for this file.  basically, it traps any exception and just returns an error string.  This is mostly just for exception rendering.
	 * @param encode encode the URL properly.  Set to false for it to be display worthy.
	 * @return the URL or an error string.
	 */
	public String getURLRendering(boolean encode) {
		String result = "ERRORED";
		try {
			result = getURL(encode);
		} catch (Throwable t) {
			// Just let it stay ERRORED
		}
		return result;
	}
	
	/**
	 * Get the URL to the root from directory which contains this file.  
	 * @return the URL to the root.
	 * @param encode encode the URL properly.  Set to false for it to be display worthy.
	 * @throws Throwable
	 */
	public String getURLtoRoot(boolean encode) throws Throwable {
		return containingDir.getURLtoRoot(encode);
	}
	
	/**
	 * Get path to the source file.   DO NOT use this to open the file.  It's for reporting and other management tasks only!
	 * @return the path for the source file.
	 */
	//public String getPath() {
	//	return sourceFile.getAbsolutePath();
	//}
	
	/**
	 * Get the directory path for this file.
	 * @return the path or an empty string if the file is not set.
	 */
//	public String getDirectoryPath() {
//		if (file==null) return "";
//		String name = file.getParent();
//		if (name==null) return "";
//		return name;
//	}

	/**
	 * The renderer may choose to create a completely different file.
	 * This will be in the exact same location as the actual file but with a different file name
	 * @param renderedFileName the file name.
	 * @throws Throwable if it passed a null file name.
	 */
	//public void setRenderedFileByName(String renderedFileName) throws Throwable {
	//	if (file==null) RendsiteException.softwareProblem("You cannot have a null renderedFileName.");
	//	this.renderedFile = new File(getDirectoryPath() + File.separatorChar + renderedFileName);
	//}
	
	// ==================================================================================================================
	// = TOOLS
	
	/**
	 * Copy file from source to destination.
	 * @throws Throwable if the file could not be copied.
	 */
	public void copy() throws ThingsException {
		BufferedInputStream bis = null;
		BufferedOutputStream bos = null;
		
		try {
			bis = new BufferedInputStream(openForRead());
			bos = new BufferedOutputStream(openForWrite());
			FileTools.copy(bis, bos);
			bos.flush();
			fcontext.logger.debug("File copied: " + getURL(false));
			
		} catch (Throwable t) {
			String path = "ERRORED";
			try {
				path = getDestinationFile().getAbsolutePath();
			} catch (Throwable tt) {
				// Just let it stay ERRORED.
			}
			throw new RendsiteException("Failed to copy file.", Codes.ERROR_IN_FILE_NOT_COPY, t, Constants.NAME_FILE_SOURCE, sourceFile.getAbsolutePath(), Constants.NAME_FILE_DESTINATION,  path, Constants.NAME_CLEAR_REASON, t.getMessage());
			
		} finally {
			try {
				bis.close();
			} catch (Throwable tt) {
				// Eat it.
			}
			try {
				bos.close();
			} catch (Throwable tt) {
				// Eat it.
			}
		}

	}
	
	
	// ==================================================================================================================
	// = INTERNAL

	
	/**
	 * Snip the file extension off the file path.
	 * @param theFile
	 * @return
	 * @throws Throwable
	 */
	private String snipExtension(File theFile) throws Throwable {
		String name = theFile.getName();
		int rover = name.lastIndexOf('.');
		if ((rover>=0)&&(name.length() > rover+1)) {
			return name.substring(rover+1);
		} else {
			return "";
		}
	}
	
	/**
	 * Get last modified date for the file.
	 * @param theFile
	 * @return the last modified date as milliseconds from epoch or 0 if the file does not exist.
	 * @throws ThingsException if the file is null or there is an IO problem.
	 */
	private long getLastModified(File theFile) throws ThingsException {
		long result;
		if (theFile==null) throw new RendsiteException("Could not get last modified date for file because the file is null.", Codes.FAULT_IN_FILE_COULD_GET_LAST_MODIFIED, Constants.NAME_FILE, "null");
		try {
			result = theFile.lastModified();
		} catch (Throwable t) {
			throw new ThingsException("Could not get last modified date for file.  This is a bug.", Codes.PANIC_FILE_SPURIOUS, Constants.NAME_FILE, theFile.getAbsolutePath());		
		}
		return result;
	}
	
}



