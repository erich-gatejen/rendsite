/**
 * RendSite 2008
 * Copyright Erich P Gatejen 2008, 2009 
 */
package rendsite.engine;

import java.io.File;

import rendsite.engine.node.Node_Directory;
import rendsite.engine.node.Node_File;

/**
 * Context for the file properties.  BE VERY CAREFUL WITH THESE.  The results are only valid for the directory you're currently processing, since
 * the metafile scope changes in every directory.  <b>IN FACT, DON'T USE THEM AT ALL.  THE ENGINE WILL SET THESE ATTRIBUTES FOR YOU.</b>
 * @author  erich
 * <i>Version History</i>
 * <pre>
 * 10NOV09 - EPG - First package release.
 * </pre>
 */
public interface PropertiesFileContext {

	// ===================================================================================================================
	// = FIELDS
	

	// ======================================================================================================================
	// = METHODS
	
	/**
	 * Is the directory excluded?
	 * @param directory the directory.
	 * @return true if it is excluded, otherwise false.
	 * @throws Throwable for severe platform problems only.
	 */
	public boolean EXCLUDED(Node_Directory directory) throws Throwable ;
	
	/**
	 * Is the file excluded?
	 * @param file the file.
	 * @return true if it is excluded, otherwise false.
	 * @throws Throwable for severe platform problems only.
	 */
	public boolean EXCLUDED(Node_File file) throws Throwable;
	
	/**
	 * Is the file or directory excluded?
	 * @param file the java File representing a directory or file.
	 * @return true if it is excluded, otherwise false.
	 * @throws Throwable for severe platform problems only.
	 */
	public boolean EXCLUDED(File file) throws Throwable;
	
	/**
	 * Is the directory ignored?
	 * @param directory the directory.
	 * @return true if it is ignored, otherwise false.
	 * @throws Throwable for severe platform problems only.
	 */
	public boolean IGNORED(Node_Directory directory) throws Throwable;
	
	/**
	 * Is the file ignored?
	 * @param file the file.
	 * @return true if it is ignored, otherwise false.
	 * @throws Throwable for severe platform problems only.
	 */
	public boolean IGNORED(Node_File file) throws Throwable;
	
	/**
	 * Is the file or directory ignored?
	 * @param file the java File representing a directory or file.
	 * @return true if it is ignored, otherwise false.
	 * @throws Throwable for severe platform problems only.
	 */
	public boolean IGNORED(File file) throws Throwable;
	
	/**
	 * Is the file copy only?
	 * @param file the  file.
	 * @return true if it is copy only, otherwise false.
	 * @throws Throwable for severe platform problems only.
	 */
	public boolean COPY_ONLY(Node_File file) throws Throwable;
	
	/**
	 * Is the directory copy only?
	 * @param directory the directory.
	 * @return true if it is copy only, otherwise false.
	 * @throws Throwable for severe platform problems only.
	 */
	public boolean COPY_ONLY(Node_Directory directory) throws Throwable;
	
	
}



