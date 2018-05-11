/**
 * RendSite 2008
 * Copyright Erich P Gatejen 2008, 2009 
 */
package rendsite.engine;

import java.io.File;
import java.util.HashMap;

import rendsite.Codes;
import rendsite.Constants;
import rendsite.RendsiteConfiguration;
import rendsite.RendsiteException;
import rendsite.engine.node.Node_Directory;
import rendsite.engine.node.Node_File;
import rendsite.notice.FileNotice_Notifier;
import things.common.ThingsException;

/**
 * Context for files in a run.  It keeps track of file changes.
 * @author  erich
 * <i>Version History</i>
 * <pre>
 * 10NOV09 - EPG - First package release.
 * </pre>
 */
public class FileContext extends Context {

	// ===================================================================================================================
	// = FIELDS
	
	// ===================================================================================================================
	// = DATA - STATEFUL - These should be reset.
	private HashMap<String, File>  exemptFiles;
	
	// ===================================================================================================================
	// = DATA - CONSTRUCTOR - These should not be reset.
	
	// Where everything is...
	private String sourceRoot;
	private String outputRoot;
	//private File 				sourceDirectory;
	private Node_Directory		rootDirectoryNode;
	
	/**
	 * The file notifier.  If null, it will not notify anyone.
	 */
	private FileNotice_Notifier fileNotifier;


	// ===================================================================================================================
	// = ABSTRACT	

	/**
	 * Reset the context.  It should lose all state except what was set during construction;
	 */
	public void reset() throws Throwable {
		exemptFiles = new HashMap<String, File>();
	}
	
	// ======================================================================================================================
	// = METHODS

	/**
	 * Create a context.  
	 * @param configuration the configuration for the system.
	 * @param sourceDirectory path to the source directory.
	 * @param outputDirectory path to the output directory.
	 * @throws Throwable normally this will only happen if you pass a null or bad SystemInterface.
	 */
	public FileContext(RendsiteConfiguration configuration, File sourceDirectory, File outputDirectory) throws Throwable {
		super(configuration);
		
		// Set files and paths
		if (sourceDirectory==null) ThingsException.softwareProblem("Cannot construct a RendsiteContext with a null sourceDirectory.");
		if (!sourceDirectory.isDirectory()) throw new RendsiteException("Source directory is not actually a directory.", Codes.FAULT_CONTEXT_DIRECTORY__SOURCE_DOESNT_EXIST, Constants.NAME_DIRECTORY, sourceDirectory.getAbsolutePath());
		//this.sourceDirectory = sourceDirectory;
		sourceRoot = sourceDirectory.getAbsolutePath();
		if (outputDirectory==null) ThingsException.softwareProblem("Cannot construct a RendsiteContext with a null outputDirectory.");
		if (!outputDirectory.isDirectory()) throw new RendsiteException("Output directory is not actually a directory.", Codes.FAULT_CONTEXT_DIRECTORY__OUTPUT_DOESNT_EXIST, Constants.NAME_DIRECTORY, sourceDirectory.getAbsolutePath());
		outputRoot = outputDirectory.getAbsolutePath();
		rootDirectoryNode = new Node_Directory(sourceDirectory, this);
		
		// Set a proper logger
		logger = configuration.getSystemInterface().getNamedLogger("FILE");
		
		reset();
	}
	
	/**
	 * Register notifier. For now, we just allow one notifier.
	 * @param fileNotifier the notifier.  If null, it'll not notify anyone.  You can turn off a previously registered notifier by calling with a null.
	 */
	public void register(FileNotice_Notifier fileNotifier) {
		this.fileNotifier = fileNotifier;
	}
	
	/**
	 * Notify the context that the file changed.
	 * @param path absolute source path.  (Destination is ok too, but it best to be consistent).
	 * @param theFile the file that changed.
	 * @throws Throwable
	 */
	public void notifyChange(String path, Node_File theFile) throws Throwable {
		if (fileNotifier!=null) fileNotifier.notifyChange(theFile, path);
	}

	/**
	 * Notify the context that the file changed failed.
	 * @param path absolute source path.  (Destination is ok too, but it best to be consistent).
	 * @param theFile the file where the change failed.
	 * @throws Throwable
	 */
	public void notifyChangeFailed(String path, Node_File theFile) throws Throwable {
		if (fileNotifier!=null) fileNotifier.notifyChangeFailed(theFile, path);
	}
	
	/**
	 * Notify the context that the file changed because it was deleted.
	 * @param path absolute source path.  (Destination is ok too, but it best to be consistent).
	 * @param theFile the file where it was deleted.
	 * @throws Throwable
	 */
	public void notifyChangeDelete(String path, Node_File theFile) throws Throwable {
		if (fileNotifier!=null) fileNotifier.notifyChangeDeleted(theFile, path);
	}
	
	/**
	 * The file will be exempt from certain system functions, such as cleaning.
	 * @param theFile
	 * @throws Throwable
	 */
	public void registerExemptFile(File theFile) throws Throwable {
		if (theFile==null) RendsiteException.softwareProblem("Cannot registerExemptFile with a null theFile.");
		exemptFiles.put(theFile.getAbsolutePath(), theFile);
	}
	
	/**
	 * If the file is exempt, return true.
	 * @param theFile the file to check for exemption.
	 * @return true if it is, otherwise false.
	 * @throws Throwable
	 */
	public boolean isExempt(File theFile) throws Throwable {
		if (theFile==null) return false;
		if (exemptFiles.containsKey(theFile.getAbsolutePath())) return true;
		return false;
	}
	
	
	// ======================================================================================================================
	// = PATH AND URL METHODS
	
	/**
	 * Get the root directory.
	 * @return the Node_Directory for the root.
	 */
	public Node_Directory getRoot() {
		return rootDirectoryNode;
	}
	
	/**
	 * Get a source file.  These must be for read-only access!  (Don't force me to abstract this further.)
	 * @param pathOffset path from the source root to the file.
	 * @return the File.  It does not check to see if the file is really there.
	 * @throws Throwable if the pathOffset is null or empty.
	 */
	public File getSourceFile(String pathOffset) throws Throwable {
		if (pathOffset==null) RendsiteException.softwareProblem("Cannot getSourceFile with a null pathOffset.");
		if (pathOffset.trim().length()<1) ThingsException.softwareProblem("Cannot getSourceFile with an empty pathOffset.");
		return new File(sourceRoot + File.separatorChar + pathOffset);
	}
	
	/**
	 * Get the output file for the given node.  
	 * @param sourcePath absolute path to source file.
	 * @return a File pointing to the target output destination.
	 */
	public File getOutputFile(String sourcePath) throws Throwable {
		if (sourcePath==null) ThingsException.softwareProblem("Cannot map a null sourcePath.");
		
		File result = null;
		try {
			
			// Snip off the root
			if (sourcePath.indexOf(sourceRoot)!=0) throw new RendsiteException("Not within original source directory", Codes.ERROR_IN_DIRECTORY__NOT_IN_SOURCE, Constants.NAME_DIRECTORY_SOURCE, sourcePath);
			String outputPath = sourcePath.substring(sourceRoot.length());
			
			// Snip trailing separator (to prevent a double)
			if (outputPath.length()<1) outputPath=File.separator;
			else if ((outputPath.length()==1)&&(outputPath.charAt(0)==File.separatorChar)) outputPath=File.separator;
			else if ((outputPath.length()>1)||(outputPath.charAt(sourcePath.length()-1)==File.separatorChar)) outputPath = outputPath.substring(0, outputPath.length());
				
			// New file
			result = new File(outputRoot + outputPath);
			
		} catch (ThingsException te) {
			throw te;
		} catch (Throwable t) {
			ThingsException.softwareProblem("Got a bad sourcePath for mapOutput.", t);
		}
		return result;
	}
	
	/**
	 * Get the output file for the given directory node.  
	 * @param source
	 * @return a File pointing to the target output destination.
	 */
	public File getOutputDirectory(String source) throws Throwable {
		if (source==null) ThingsException.softwareProblem("Cannot map a null source.");
		
		File result = null;
		try {
			
			// Snip off the root
			if (source.indexOf(sourceRoot)!=0) throw new RendsiteException("Not within original source directory", Codes.ERROR_IN_DIRECTORY__NOT_IN_SOURCE, Constants.NAME_DIRECTORY_SOURCE, source);
			String sourcePath = source.substring(sourceRoot.length());
			
			// Snip trailing separator (to prevent a double)
			if ((sourcePath.length()==1)&&(sourcePath.charAt(0)==File.separatorChar)) sourcePath="";
			else if ((sourcePath.length()>1)&&(sourcePath.charAt(sourcePath.length()-1)==File.separatorChar)) sourcePath = sourcePath.substring(0, sourcePath.length());
				
			// New directory - try to make the fs.
			// TODO this happens way too often.  Fix it.
			result = new File(outputRoot + sourcePath);
			result.mkdirs();
			
		} catch (ThingsException te) {
			throw te;
		} catch (Throwable t) {
			ThingsException.softwareProblem("Got a bad source for mapOutput.", t);
		}
		return result;
	}
	
	/**
	 * Get related URL for the directory from the root.  These should be ok for links.
	 * @param source where we are starting from.
	 * @param url the url.  Do not start with a 'slash' (will cause an exception).
	 * @param encode encode the URL properly.  Set to false for it to be display worthy.
	 * @throws Throwable
	 * @return the URL
	 */
	public String getURLFromRoot(Node_Directory source, String url, boolean encode) throws Throwable {
		return getURLToRoot(source, encode) + url;
	}
	
	/**
	 * Get related URL to the root from the directory.  These should be ok for links.
	 * @param source where we are starting from.
	 * @param encode encode the URL properly.  Set to false for it to be display worthy.
	 * @throws Throwable
	 * @return the URL
	 */
	public String getURLToRoot(Node_Directory source, boolean encode) throws Throwable {
		if (configuration.isFilesystemMode()) {
			// This is a clugeball.  Find how far down the tree we are and that'll let us know how far we have to backtrack.
			String findSteps = getURL(rootDirectoryNode, source, encode);
			String bits[] = findSteps.split("/");
			StringBuffer result = new StringBuffer();
			// Root case?
			if ((bits.length==1)&&(bits[0].length()==0)) {
				return "";
			} else {
				for (int index = 0; index < bits.length ; index++) {
					result.append("../");
				}
				return result.toString();
			}
		} else {
			return "/";	// Easy, everyone is from the site root.
		}
	}
	
	/**
	 * Get the URL for this directory based on the path which will be pointed at by the source directory.
	 * @param source from where this will be pointed at.  It is useful for relative paths (if needed).  If set to null, it will always be treated as the same directory.
	 * @param target the target from which to get the URL.
	 * @param encode encode the URL properly.  Set to false for it to be display worthy.
	 * @return the base 
	 */
	public String getURL(Node_Directory source, Node_Directory target, boolean encode) throws Throwable {
		if (target==null) RendsiteException.softwareProblem("Cannot getURL(source,target) a null target directory.");
		Node_Directory actualSource = source;
		if (source==null) source =  target;

		return  getUrl(actualSource.getPath(), target.getPath(), encode);
	}

	/**
	 * Get a URL for this file in the target directory which will be pointed at by the source directory.
	 * @param source from where this will be pointed at.  It is useful for relative paths (if needed).  If set to null, it will always be treated as the same directory.
	 * @param target the target from which to get the URL.
	 * @param fileName the filename for the file.
	 * @param encode encode the URL properly.  Set to false for it to be display worthy.
	 * @return the base 
	 */
	public String getURL(Node_Directory source, Node_Directory target, String fileName, boolean encode) throws Throwable {
		if (target==null) RendsiteException.softwareProblem("Cannot getURL(source,target,fileName) a null target directory.");
		Node_Directory actualSource = source;
		if (source==null) source =  target;
		if (fileName==null)
			RendsiteException.softwareProblem("Cannot getURL(source,target,fileName) a null fileName.", new Exception(), "source", source.getFile().getAbsolutePath(), "target", target.getFile().getAbsolutePath());
		
		return getUrl(actualSource.getPath(), target.getPath() + "/" + fileName, encode) ;
	}
	
	/**
	 * Get a url from a path within the root.  Process it for filesystem mode if necessary.
	 * @param base from where this will be pointed (useful if the context needs to make relative paths).
	 * @return target the target path from which to get the url
	 * @param encode encode the URL properly.
	 * @throws Throwable
	 */
	public String getUrl(String base, String target, boolean encode) throws Throwable { 
		if (target==null) ThingsException.softwareProblem("Cannot get url from a null path.");
		if (target.indexOf(sourceRoot)!=0) throw new RendsiteException("Path not within the source.", Codes.ERROR_IN_FILE__PATH_NOT_IN_SOURCE, Constants.NAME_FILE_SOURCE, target);
		
		// Slow and lame implementation for now.  Get and clean the URL
		String url = target.substring(sourceRoot.length());
		url = url.replace('\\', '/');
		if (encode) url =url .replace(" ", "%20");
		
		// Special filesystem mode processing?
		if (configuration.isFilesystemMode()) {
			if (base.indexOf(sourceRoot)!=0) throw new RendsiteException("Path not within the source.", Codes.ERROR_IN_FILE__PATH_NOT_IN_SOURCE, Constants.NAME_FILE_SOURCE, target);
			
			// Yes, clip off the source
			String sourceurl = base.substring(sourceRoot.length()).replace('\\', '/');
			if (encode) sourceurl =sourceurl .replace(" ", "%20");
			url = url.substring(sourceurl.length());
			
			if ((url.length()>1)&&(url.charAt(0)=='/')) {
				url = url.substring(1);
			}
			
		} else {
			
			// not filesystem mode
			if ((url.length()<1) || ((url.length()==1)&&(url.charAt(0)==File.separatorChar))) url="/";
			else if ((url.length()>1)&&(url.charAt(url.length()-1)==File.separatorChar)) url = url.substring(0, url.length());
		}
		
		return url;
	}
	
	/**
	 * Get absolute URL for the directory.  Never use these for links--only display.
	 * @param target the target from which to get the URL.
	 * @param encode encode the URL properly.  Set to false for it to be display worthy.
	 * @return the base 
	 */
	public String getURLAbsolute(Node_Directory target, boolean encode) throws Throwable {
		return getURL(rootDirectoryNode, target, encode);
	}
	
	/**
	 * Get absolute URL for the file.  Never use these for links--only display.
	 * @param target the directory containing the file.
	 * @param fileName the filename for the file.
	 * @param encode encode the URL properly.  Set to false for it to be display worthy.
	 * @return the base 
	 */
	public String getURLAbsolute(Node_Directory target, String fileName, boolean encode) throws Throwable {
		return getURL(rootDirectoryNode, target, fileName, encode);
	}
	
}



