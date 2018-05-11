/**
 * RendSite 2008
 * Copyright Erich P Gatejen 2008, 2009 
 */
package rendsite.engine.node;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import rendsite.Codes;
import rendsite.Constants;
import rendsite.RendsiteException;
import rendsite.engine.FileContext;
import things.common.ThingsException;
import things.common.ThingsNamespace;
import things.common.tools.FileTools;

/**
 * Crawler node for a Directory.
 * @author  erich
 * <i>Version History</i>
 * <pre>
 * 10NOV09 - EPG - First package release.
 * </pre>
 */
public class Node_Directory extends Node {

	// ==================================================================================================================
	// = ATTRIBUTES
	
	//  -------------------------------------------------------------------------------------------------------------
	// Attributes used during processing.  The engine will set all these before any processing has a chance to touch them.
	public boolean copyOnly;		// Copy but to not process.
	public boolean ignored;			// Do not process at all.
	public boolean excluded;		// Do not put in a catalog.
	
	//  -------------------------------------------------------------------------------------------------------------
	// Attributes used during frame rendering.   The engine will set all these before any frame rendering is done.
	
	/**
	 * Information about the file.  If the 'description' is null, there is no other info about the file.
	 */
	public String description= null;
	public String type;
	
	// ==================================================================================================================
	// = DATA

	/**
	 * Contents of the directory.  These will be created when fixed.
	 */
	private List<Node_Directory> subs;
	private List<Node_File> files;
	private List<Node_MetaFile> meta;
	private List<Node_MetaFile> local;
	
	/**
	 * The catalog file name.  If null, it may not have been set yet.  If blank, it'll never be set.  Let the context get this.
	 */
	private String catalogFileName;
	
	/**
	 * The file that represents this directory
	 */
	private File file;
	
	/**
	 * All the files in the directory.  This will be taken during construction.
	 */
	private File[] allFiles;
	
	/**
	 * The most recent member (all the way down the tree).  This is a time/date in milliseconds from epoch.
	 */
	private long  mostRecentModified;
	
	/**
	 * The file context.
	 */
	private FileContext fcontext;
	
	// ==================================================================================================================
	// = METHODS
	
	/**
	 * Create a directory node.
	 * @param file the file representing the directory.
	 * @param fcontext the file context.
	 * @throws Throwable either from bugs or because it can't process the contents of the directory.
	 */
	public Node_Directory(File file, FileContext	fcontext) throws Throwable {
		super(NodeType.DIRECTORY);
		
		// Qualify
		if (fcontext==null) RendsiteException.softwareProblem("You cannot have a null fcontext.");
		this.fcontext = fcontext;
		if (file==null) RendsiteException.softwareProblem("You cannot have a null file.");
		if (!file.isDirectory()) RendsiteException.softwareProblem("The file is not a directory.", ThingsNamespace.ATTR_PROPERTY_PATH, file.getAbsolutePath());
		this.file = file;
		
		// Settings
		mostRecentModified = file.lastModified();
		allFiles = file.listFiles();
	}
	
	/**
	 * Potentially set the most recent modified time.  The most recent modified value for this directory will be the greatest of the passed value and the existing setting.
	 * @param datetime  the proposed modified time in milliseconds from epoch.
	 */
	public void modifiedTime(long datetime) {
		if (datetime>mostRecentModified) mostRecentModified = datetime;
	}
	
	/**
	 * Get the most recent modified time for all files and subdirectories under this directory.  This value is NOT reliable until the directory
	 * is completely processed.
	 * @return the most recent modified time in seconds from epoch.
	 */
	public long getMostRecentModified() {
		return mostRecentModified;
	}

	/**
	 * Get the rendered name.  It'll be nothing or overridden by a sub class.
	 * @return the name.  DO NOT USE THIS TO OPEN A FILE!
	 */
	public String getName() {
		return file.getName();
	}
	
	/**
	 * Get the subdirectories in this directory.
	 * @return a list.  It may be empty.
	 * @throws Throwable
	 */
	public List<Node_Directory> getSubDirectories() throws Throwable {
		if (subs==null) fixSubs();
		return subs;
	}
	
	/**
	 * Get the files in this directory.
	 * @return a list.  It may be empty..
	 * @throws Throwable
	 */
	public List<Node_File> getFiles() throws Throwable {
		if (files==null) fixFiles();	
		return files;
	}
	
	/**
	 * Get the metafiles in this directory.
	 * @return a list.  It may be empty.
	 * @throws Throwable
	 */
	public List<Node_MetaFile> getMetaFiles() throws Throwable {
		if (meta==null) fixMetaFiles();
		return meta;
	}
	
	/**
	 * Get the local metafiles in this directory.
	 * @return a list.  It may be empty.
	 * @throws Throwable
	 */
	public List<Node_MetaFile> getLocalMetaFiles() throws Throwable {
		if (local==null) fixLocalMetaFiles();
		return local;
	}
	
	/**
	 * Get a file representing this directory.  It might be null, if it hasn't been fixed yet.
	 * @return the file.
	 */
	public File getFile() {
		return file;
	}
	
	/**
	 * Set the files.  Only the engine should ever do this. 
	 * @param newFileList the files to set.
	 * @throws Throwable
	 */
	public void setFiles(List<Node_File> newFileList) throws Throwable {
		if (newFileList==null) RendsiteException.softwareProblem("You setFiles a null newFileList.");
		files = newFileList;
	}
	
	/**
	 * Set the sub directories.  Only the engine should ever do this.
	 * @param newSubList the files to set as the subs.
	 * @throws Throwable
	 */
	public void setSubDirectories(List<Node_Directory> newSubList) throws Throwable {
		if (newSubList==null) RendsiteException.softwareProblem("You setFiles a null newSubList.");
		subs = newSubList;
	}
	
	/**
	 * Get the catalog name.  Only the context or engine should ever use this method!!!  Everyone else should call PropertiesContext.GET_CATALOG_NAME. 
	 * I realize I can create an intermediate interface or class, but that's just too much for now.  
	 * This is buffoonery protection and would be a whole lot more elegant in another language.
	 * @return The name or null if it hasn't been set.
	 */
	public String getCatalogName() {
		return catalogFileName;
	}
	
	/**
	 * Set the catalog name.  Only the  context or engine should ever use this method!!!  Only the context or engine should ever use this method!!!  
	 * Everyone else should call PropertiesContext.GET_CATALOG_NAME. 
	 * This is buffoonery protection and would be a whole lot more elegant in another language.
	 * @param name the name
	 */
	public void setCatalogName(String  name) {
		catalogFileName = name;
	}
	
	/**
	 * Make sure the directory exists in the destination.
	 * @throws Throwable
	 */
	public void build() throws Throwable {
		try {
			File destinationFile = fcontext.getOutputFile(file.getAbsolutePath());
			if (!destinationFile.exists()) destinationFile.mkdirs();	
		} catch (Throwable t) {
			throw new RendsiteException("Failed to create output directory.", Codes.ERROR_CANNOT_MAKE_SUBDIRECTORY, t, Constants.NAME_DIRECTORY_SOURCE, getPath());
		}
	}

	/**
	 * Get the absolute path to this directory.  NEVER, EVER use this to open a file!  This is a "unique name" for the directory.  All other uses are UNRELIABLE!
	 * @return the path.
	 */
	public String getPath() {
		return file.getAbsolutePath();
	}
	
	/**
	 * Get the last modified date for the catalog.  
	 * @return the last modified date as milliseconds from epoch, 0 if the catalog does not exist on the fs, and NOW if the catalogName has not been set.
	 * @throws ThingsException
	 */
	public long lastModifiedCatalog() throws ThingsException {
		if (catalogFileName==null) return System.currentTimeMillis();
		long result = 0;
		try {
			File theFile = getCatalogFile();
			if (theFile.exists()) result = theFile.lastModified();
		} catch (Throwable t) {
			throw new RendsiteException("Could not get the definition catalog.  This is a bug.", Codes.PANIC_FILE_SPURIOUS, t);		
		}
		return result;
	}
	
	/**
	 * Get the absolute URL to the catalog.
	 * @param encoded encode it for html?
	 * @return the URL
	 * @throws Throwable
	 */
	public String getCatalogUrlAbsolute(boolean encoded) throws Throwable {
		if (catalogFileName==null) RendsiteException.softwareProblem("getCatalogUrl called before the catalog file was set.");
		return fcontext.getURLAbsolute(this, catalogFileName, encoded);
	}

	/**
	 * Get the URL to the catalog relative to this directory from the passes directory.
	 * @param source from where this will point.
	 * @param encode encode the URL properly.  Set to false for it to be display worthy.
	 * @return the URL
	 * @throws Throwable
	 */
	public String getCatalogUrl(Node_Directory source, boolean encode) throws Throwable {
		if (catalogFileName==null) RendsiteException.softwareProblem("getCatalogUrl called before the catalog file was set.");
		return fcontext.getURL(source, this, catalogFileName, encode);
	}
	
	/**
	 * Get the URL for this directory from the root.
	 * @param encode encode the URL properly.  Set to false for it to be display worthy.
	 * @return the URL
	 * @throws Throwable if there is a problem forming the URL or a bug.
	 */
	public String getURLAbsolute(boolean encode) throws Throwable {
		return fcontext.getURLAbsolute(this, encode);
	}
	
	/**
	 * Get the URL for this directory from the root for rendering purposes only.  Any problem will yield some form of error test.  It'll  will never throw an exception.
	 * @return the URL
	 */
	public String getURLRendering() {
		String result = "ERROR";
		try {
			result = fcontext.getURLAbsolute(this, false);
		} catch (Throwable t) {
			// Let the error text stay as the result.
		}
		return result; 
	}
	
	/**
	 * Get the URL to the root from this directory.  
	 * @return the URL to the root.
	 * @param encode encode the URL properly.  Set to false for it to be display worthy.
	 * @throws Throwable
	 */
	public String getURLtoRoot(boolean encode) throws Throwable {
		return fcontext.getURLToRoot(this, encode);
	}
	
	/**
	 * Open for the catalog file for writing.  The stream will not be buffered.  You should close it when you are done.
	 * It will mark the file as having been updated for other processing.
	 * @return an output stream for writing.
	 * @throws ThingsException
	 */
	public OutputStream openCatalogForWrite() throws ThingsException {
		OutputStream result = null;
		File catalogFile = null;
		try {
			catalogFile = getCatalogFile();
			result = new FileOutputStream(catalogFile);
			fcontext.notifyChange(catalogFile.getAbsolutePath(), new Node_File(catalogFile, this, fcontext));
		} catch (Throwable t) {
			if (catalogFile==null)
				throw new RendsiteException("Failed to open destination file for writing.  Null catalog file.", Codes.ERROR_IN_FILE_COULD_NOT_OPEN__CATALOG, t);	
			else 
				throw new RendsiteException("Failed to open destination file for writing.", Codes.ERROR_IN_FILE_COULD_NOT_OPEN__CATALOG, t, Constants.NAME_FILE_DESTINATION, catalogFile.getAbsolutePath());
		}
		return result;
	}
	
	/**
	 * Note that the catalog write failed.  The last modified date will be set to 1 (one millisecond after epoch) and the filecontext will be notified.
	 * @throws ThingsException will only happen for bugs, which will be a panic.
	 */
	public void failCatalogWrite() throws Throwable {
		try {
			File catalogFile = getCatalogFile();
			if (catalogFile.canWrite())	catalogFile.setLastModified(1);
			fcontext.notifyChangeFailed(catalogFile.getAbsolutePath(), new Node_File(catalogFile, this, fcontext));
		} catch (Throwable t) {
			throw new RendsiteException("Could not failCatalogWrite().", Codes.PANIC_RUN, t, Constants.NAME_DIRECTORY_SOURCE, getPath());
		}
	}
	
	/**
	 * Get the catalog file.  It may be opened
	 * @return
	 * @throws Throwable
	 */
	private File getCatalogFile() throws Throwable {
		if (catalogFileName==null) RendsiteException.softwareProblem("getCatalogFile called before the catalog file was set.");
		return fcontext.getOutputFile(file.getAbsolutePath() + File.separatorChar + catalogFileName);
	}
	
	// ==================================================================================================================
	// = TOOLS
	
	/**
	 * Is this file a METAFILE?
	 * @param theFile the file.  If null, it'll always return false.
	 * @return true if it is, otherwise false.
	 */
	public static boolean isMetafile(File theFile) {
		if (theFile==null) return false;
		if ( (theFile.getName().indexOf(Constants.METAFILE_PREFIX)==0) && 
			 (!theFile.getName().endsWith(Constants.METAFILE_LOCAL_SUFFIX)) ) return true;
		return false;
	}
	
	/**
	 * Is this file a LOCAL METAFILE?
	 * @param theFile the file.  If null, it'll always return false.
	 * @return true if it is.
	 */
	public static boolean isLocalMetafile(File theFile) {
		if (theFile==null) return false;
		if ( (theFile.getName().indexOf(Constants.METAFILE_PREFIX)==0) && 
			 (theFile.getName().endsWith(Constants.METAFILE_LOCAL_SUFFIX)) ) return true;
		return false;
	}
	
	/**
	 * Clean the directory.  It will wipe out anything in the output directory that cannot be matched to something in the Node_Directory.
	 * @throws Throwable for a wide range of offenses.
	 */
	public void clean() throws Throwable {
		
		// Make the destination directory
		try {
			
			// Create a map of what's there.
			HashMap<String,File> fileMap = new HashMap<String,File>();
			for (File item : fcontext.getOutputDirectory(file.getAbsolutePath()).listFiles()) {
				fileMap.put(item.getName(), item);
			}
			
			// Dismiss the directories, files, and catalog
			for (Node_Directory dir : getSubDirectories()) {
				fileMap.remove(dir.file.getName());
			}
			for (Node_File file : getFiles()) {
				fileMap.remove(file.getName());
				removeNeighbors(fileMap, file);
			}
			for (Node_MetaFile metafile : getMetaFiles()) {
				fileMap.remove(metafile.getName());
			}
			if (catalogFileName!=null) fileMap.remove(catalogFileName);
			
			// Smack what's left
			for (File doomedFile : fileMap.values()) {
				if (!fcontext.isExempt(doomedFile)) {
					FileTools.destroy(doomedFile);
					fcontext.notifyChangeDelete(doomedFile.getAbsolutePath(), new Node_File(doomedFile, this, fcontext));
					if (fcontext.logger.debuggingState()) fcontext.logger.debug("Cleaning: sucessfully deleted.", Codes.DEBUG_FILE_DELETED, ThingsNamespace.ATTR_PLATFORM_FILE_PATH, doomedFile.getAbsolutePath());
				}
			}
			
		} catch (Throwable t) {
			throw new RendsiteException("Failed to clean output directory.", Codes.ERROR_CANNOT_CLEAN_SUBDIRECTORY, t, Constants.NAME_DIRECTORY_SOURCE, getPath());
		}
		
	}
	
	
	// ==================================================================================================================
	// = INTERNAL
	
	/**
	 * Fix the subdirectories.  This is only done the first time the list of subs are requested.  We do this so that we don't
	 * spider the whole tree when we construct out first Node_Directory.
	 * @throws Throwable Generally, this will only happen if the directory cannot be read.  Though odd filesystem things might cause it.
	 */
	private synchronized void fixSubs() throws Throwable {
		 
		// Check one more time, because we may have just been hung up by the synchronize.
		if (subs!=null) return;
		
		// Build them
		subs = new LinkedList<Node_Directory>();
		
		// Fill them
		try {
			for (File item : allFiles) {
				if (item.isDirectory()) subs.add(new Node_Directory(item,fcontext));	
			}
		} catch (Throwable t) {
			throw new RendsiteException("Failed to get subdirectories.", Codes.ERROR_IN_DIRECTORY__GET_SUBS, t, Constants.NAME_DIRECTORY, file.getAbsolutePath());
		}
	}
	
	/**
	 * Fix the files.  This is only done the first time the list of files are requested.  We do this so that we don't
	 * spider the whole tree when we construct out first Node_Directory.
	 * @throws Throwable Generally, this will only happen if the directory cannot be read.  Though odd filesystem things might cause it.
	 */
	private synchronized void fixFiles() throws Throwable {
		 
		// Check one more time, because we may have just been hung up by the synchronize.
		// Also, give it a 
		if (files!=null) return;
		
		// Build them
		files = new LinkedList<Node_File>();
		
		// Fill them
		try {
			for (File item : allFiles) {
				if (item.isFile() && !isMetafile(item) && !isLocalMetafile(item)) files.add(new Node_File(item, this, fcontext));	
			}
		} catch (Throwable t) {
			throw new ThingsException("Failed to get files in directory.", Codes.ERROR_IN_DIRECTORY__GET_FILES, t, Constants.NAME_DIRECTORY, file.getAbsolutePath());
		}
	}
	
	/**
	 * Fix the meta files.  This is only done the first time the list of metafiles files are requested.  We do this so that we don't
	 * spider the whole tree when we construct out first Node_Directory.
	 * @throws Throwable Generally, this will only happen if the directory cannot be read.  Though odd filesystem things might cause it.
	 */
	private synchronized void fixMetaFiles() throws Throwable {
		 
		// Check one more time, because we may have just been hung up by the synchronize.
		// Also, give it a 
		if (meta!=null) return;
		
		// Build them
		meta = new LinkedList<Node_MetaFile>();
		
		// Fill them
		try {
			for (File item : allFiles) {
				if (item.isFile() && isMetafile(item) ) meta.add(new Node_MetaFile(item));	
			}
		} catch (Throwable t) {
			throw new RendsiteException("Failed to get meta files in directory.", Codes.ERROR_IN_DIRECTORY__GET_METAFILES, t, Constants.NAME_DIRECTORY, file.getAbsolutePath());
		}
	}
	
	/**
	 * Fix the local meta files.  This is only done the first time the list of local metafiles files are requested.  We do this so that we don't
	 * spider the whole tree when we construct out first Node_Directory.
	 * @throws Throwable Generally, this will only happen if the directory cannot be read.  Though odd filesystem things might cause it.
	 */
	private synchronized void fixLocalMetaFiles() throws Throwable {
		 
		// Check one more time, because we may have just been hung up by the synchronize.
		// Also, give it a 
		if (local!=null) return;
		
		// Build them
		local = new LinkedList<Node_MetaFile>();
		
		// Fill them
		try {
			for (File item : allFiles) {
				if (item.isFile() && isLocalMetafile(item)) local.add(new Node_MetaFile(item));	
			}
		} catch (Throwable t) {
			throw new RendsiteException("Failed to get meta files in directory.", Codes.ERROR_IN_DIRECTORY__GET_METAFILES, t, Constants.NAME_DIRECTORY, file.getAbsolutePath());
		}
	}
	
	/**
	 * Recurse and keep neighbors.
	 * @param theMap
	 * @param theFile
	 */
	private void removeNeighbors(Map<String,File> theMap, Node_File theFile) throws Throwable {
		for (Node_File neighbor : theFile.getNeighbors()) {
			theMap.remove(neighbor.getName());
			removeNeighbors(theMap, neighbor);
		}
	}
	

}

