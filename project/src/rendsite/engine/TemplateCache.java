/**
 * RendSite 2008
 * Copyright Erich P Gatejen 2008, 2009 
 */
package rendsite.engine;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;

import rendsite.Codes;
import rendsite.RendsiteException;
import things.common.ThingsException;
import things.common.ThingsNamespace;
import things.common.tools.FileTools;

/**
 * Cache for loadable templates.   
 * <p>
 * This version isn't particularly memory friendly.
 * @author  erich
 * <i>Version History</i>
 * <pre>
 * 10NOV09 - EPG - First package release.
 * </pre>
 */
public class TemplateCache {

	// ==================================================================================================================
	// = FIELDS
	
	// ==================================================================================================================
	// = DATA
	private HashMap<String,String> cache;

	// ==================================================================================================================
	// = METHODS

	/**
	 * Construct the cache.  
	 * @throws Throwable will only happen if the context is bad or the catalog could not be loaded.
	 */
	public TemplateCache() throws Throwable {
		cache = new HashMap<String,String>();
	}
	
	/**
	 * Get a template by path.
	 * @param theFile a File pointing to the template.  Let the context call this.  use the context methods to get templates.
	 * @throws ThingsException
	 */
	public synchronized String getTemplate(File theFile) throws ThingsException {
		if (theFile==null) RendsiteException.softwareProblem("Cannot getTemplate with a null theFile.");
		
		// In cache?
		 String result = cache.get(theFile.getAbsolutePath());
		
		// No, try to load it.
		if (result==null) {
			
			try {
				result = FileTools.loadFile2String(theFile);
				cache.put(theFile.getAbsolutePath(), result);
				
				if (result.trim().length()<1) throw new Exception("Template is empty.");
			
			} catch (FileNotFoundException fnfe) {
				throw new RendsiteException("Failed to load a template into the cache.  File not found.", Codes.FAULT_TEMPLATE_LOAD__FILE_NOT_FOUND, fnfe, ThingsNamespace.ATTR_PLATFORM_FILE_PATH, theFile.getAbsolutePath());
	
			} catch (IOException ieo) {
				throw new RendsiteException("Failed to load a template into the cache.  Could not read file.", Codes.FAULT_TEMPLATE_LOAD__FILE_COULD_NOT_READ, ieo, ThingsNamespace.ATTR_PLATFORM_FILE_PATH, theFile.getAbsolutePath());
			
			} catch (Throwable t) {
				throw new RendsiteException("Failed to load a template into the cache.", Codes.FAULT_TEMPLATE_LOAD, t, ThingsNamespace.ATTR_PLATFORM_FILE_PATH, theFile.getAbsolutePath());
			}
			
		}
		
		return result;
	}
	
	/**
	 * Get a template by path.
	 * @param path absolute path to the template.  Let the context call this.  use the context methods to get templates.
	 * @throws ThingsException
	 */
	public synchronized String getTemplate(String path) throws ThingsException {
		if (path==null) RendsiteException.softwareProblem("Cannot getTemplate with a null path.");
		if (path.trim().length()<1) RendsiteException.softwareProblem("Cannot getTemplate with an empty path.");
		return getTemplate(new File(path));
	}
	

}



