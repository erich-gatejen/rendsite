/**
 * RendSite 2008
 * Copyright Erich P Gatejen 2008, 2009 
 */
package rendsite;

/**
 * These are the properties used in metafiles.
 * <p>
 * @author  erich
 * <i>Version History</i>
 * <pre>
 * 10NOV09 - EPG - First package release.
 * </pre>
 */
public interface RendsiteProperties  {

	// ===================================================================================================================
	// = PROPERTIES - VERSION 1
	
	// Renderers
	public final static String PROP_RENDERER_NAME = "renderer.name";					// Null or empty yields the default.
	public final static String PROP_TEMPLATE_PATH_CATALOG = "template.path.catalog";	// Path to the template for a catalog used by certain renderers.  The path will be from the source root.
	public final static String PROP_TEMPLATE_PATH_FILE = "template.path.file";			// Path to the template for a file used by certain renderers.  The path will be from the source root.

	// File definition
	public final static String PROP_FILE	 = "file";	
	public final static String PROP_FILE_NAME = "name";
	public final static String PROP_FILE_DESCRIPTION = "desc";	
	public final static String PROP_FILE_TYPE = "type";					// Either root or as part of a document definition.  When root, the actual type name should be normalized to lower case.
	public final static String PROP_FILE_CATEGORY = "category";			// Category is an explicit, per file definition (or null).  It is primarily meant to signal specific processing to the renderers.
	public final static String PROP_FILE_RENDERING_TYPE = "rendering";  // Set the rendering type.  Usually only needed if there is no category, the extension doesn't infer one, and you want it to be something other than OTHER.
																		// The value must match a token in RenderingType.
	
	// Directory definition
	public final static String PROP_DIRECTORY	 = "directory";	
	public final static String PROP_DIRECTORY_NAME = "name";
	public final static String PROP_DIRECTORY_DESCRIPTION = "desc";	
	public final static String PROP_DIRECTORY_TYPE = "type";
	public final static String PROP_DIRECTORY_CATALOG = "catalog";		// Suggest a catalog for the directory. 
	
	// Pages
	public final static String PROP_CATALOG_FILE_NAME = "catalog";				// Null or empty yields the default.
	public final static String PROP_CATALOG_FILE_NAME__DEFAULT = "index.html";		

	// Directory/file functions
	public final static String PROP_DIR_IGNORE = "directory.ignore";	// Ignore the directory completely Multivalue
	public final static String PROP_DIR_EXCLUDE = "directory.exclude";	// Exclude from the catalogs
	public final static String PROP_DIR_COPY_ONLY = "directory.copy";	// Only copy the directory.  All subdirectories will inherit this!
	public final static String PROP_FILE_IGNORE = "file.ignore";		// Ignore the file completely. Overrides all other.  Multivalue
	public final static String PROP_FILE_EXCLUDE = "file.exclude";		// Exclude from the catalogs
	public final static String PROP_FILE_COPY_ONLY = "file.copy";		// Only copy the file.  Don't process it.  Multivalue
	
	// Information
	public final static String PROP_COPYRIGHT_NOTICE = "copyright.notice";
	public final static String PROP_PROJECT = "project";
	
	// LOCAL ONLY
	public final static String PROP_LOCAL_DESCRIPTION = "description";	
	
	// Tokens
	public final static char PROP_WILDCARD = '*';

}



