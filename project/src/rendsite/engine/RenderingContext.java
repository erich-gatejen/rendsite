/**
 * RendSite 2008
 * Copyright Erich P Gatejen 2008, 2009 
 */
package rendsite.engine;

import java.util.LinkedList;
import java.util.List;

import rendsite.RendsiteConfiguration;
import rendsite.RendsiteException;
import rendsite.engine.node.Node_Directory;
import rendsite.engine.node.Node_File;
import rendsite.renderer.RendererCatalog;
import things.thinger.io.Logger;

/**
 * Context for rendering.
 * @author  erich
 * <i>Version History</i>
 * <pre>
 * 10NOV09 - EPG - First package release.
 * </pre>
 */
public class RenderingContext extends Context {

	// ===================================================================================================================
	// = FIELDS
	
	
	// ===================================================================================================================
	// = DATA - STATEFUL - These should be reset.
	private TemplateCache templates;
	public RendererCatalog renderers;
	
	// ===================================================================================================================
	// = DATA - CONSTRUCTOR - These should not be reset.
	private FileContext fcontext;
	private CategoryManager categoryManager;
	

	// ===================================================================================================================
	// = ABSTRACT	

	/**
	 * Reset the context.  It should lose all state except what was set during construction;
	 */
	public void reset() throws Throwable {
		templates = new TemplateCache();
		renderers = new RendererCatalog();
	}
	
	// ======================================================================================================================
	// = METHODS

	/**
	 * Create a context.  
	 * @param configuration the configuration for the system.
	 * @param fcontext the file context.
	 * @param categoryManager the category manager.
	 * @throws Throwable normally this will only happen if you pass a null or bad SystemInterface.
	 */
	public RenderingContext(RendsiteConfiguration configuration, FileContext fcontext, CategoryManager categoryManager) throws Throwable {
		super(configuration);
		
		if (fcontext==null) RendsiteException.softwareProblem("Cannot construct a RenderingContext with a null FileContext.");
		this.fcontext = fcontext;
		if (categoryManager==null) RendsiteException.softwareProblem("Cannot construct a RenderingContext with a null CategoryManager.");
		this.categoryManager = categoryManager;
		reset();
		
		// Set a proper logger
		logger = configuration.getSystemInterface().getNamedLogger("REND");
	}
	
	// ===================================================================================================================
	// = METHODS
	
	/**
	 * Get the logger for rendering.
	 * @return the logger.
	 */
	public Logger getLogger() {
		return logger;
	}
	
	// ===================================================================================================================
	// = TOOLS
	
	/**
	 * Get a template.  
	 * @param pathOffset path from the source root to the template file.
	 * @return the template in a string.  It will have been cached.
	 */
	public String GET_TEMPLATE(String pathOffset) throws Throwable {
		return templates.getTemplate(fcontext.getSourceFile(pathOffset));
	}
	
	/**
	 * Get the subdirectories for the catalog that have been filtered for ignores, excludes, and no catalog name (which will happen if the subdirectory was marked as copy only, but no catalog was explicitely configured.
	 * @return a list.  It may be empty.
	 * @param directory the directory from which to get the subs.
	 * @throws Throwable
	 */
	public List<Node_Directory> GET_CATALOGABLE_SUBDIRECTORIES(Node_Directory directory) throws Throwable {
		if (directory==null)RendsiteException.softwareProblem("GET_CATALOG_SUBDIRECTORIES called with null directory.");
		List<Node_Directory> result = new LinkedList<Node_Directory>();
		for (Node_Directory current : directory.getSubDirectories()) {
			if ( (!current.excluded) && (!current.ignored) && (current.getCatalogName()!=null) ) {
				result.add(current);
			}
		}
		return result;
	}
	
	/**
	 * Get the files for the catalog that have been filtered for ignores and excludes.
	 * TODO this doesn't handle neighbors at all.
	 * @return a list.  It may be empty.  Note that you should getCatalogedFile for each element to see if it has a neighbor cataloged for it.
	 * @param directory the directory from which to get the files.
	 * @throws Throwable
	 */
	public List<Node_File> GET_CATALOGABLE_FILES(Node_Directory directory) throws Throwable {
		if (directory==null) RendsiteException.softwareProblem("GET_FILES called with null directory.");
		List<Node_File> result = new LinkedList<Node_File>();
		for (Node_File current : directory.getFiles()) {
			if ( (!current.excluded) && (!current.ignored) ) {
				result.add(current);
			}
		}
		return result;
	}
	
	/**
	 * It'll give you whatever the Category manager finds for that extension.
	 * @param extension the file extension
	 * @return a category or a null.
	 */
	public Category CATEGORY_LOOKUP_EXTENSION(String extension) throws Throwable {
		return categoryManager.lookupExtension(extension);
	}

	// Cheeseball cache for category lookup.
	private Node_File lastFile;
	private Category lastCategory;
	
	/**
	 * Render a 'type' text.  First check the type field.  If null, try to infer it from the category.  If there is no category, yield an empty.
	 * @param theFile
	 * @return the type text
	 * @throws Throwable
	 */
	public String RENDER_TYPE(Node_File theFile) throws Throwable {
		if ((theFile.type!=null)&&(theFile.type.length()>0)) return theFile.type;
		if ((theFile.category!=null)&&(theFile.category.displayType!=null)) return theFile.category.displayType;
		Category category = lookupCategory(theFile);
		if (category!=null) return category.displayType;
		return "";
	}

	/**
	 * Get the rendering type.  If not set or inferred by category, it'll return OTHER.
	 * @param theFile
	 * @return the rendering type.
	 */
	public RenderingType RESOLVE_RENDERING_TYPE(Node_File theFile) throws Throwable{
		if (theFile.renderingType!=null) return theFile.renderingType;
		if ((theFile.category!=null)&&(theFile.category.renderingType!=null)) return theFile.category.renderingType;
		Category category = lookupCategory(theFile);
		if (category!=null) return category.renderingType;
		return RenderingType.OTHER;
	}

	// - INTERNAL TOOLS ---------------------------------------------------------------------------------------
	
	/**
	 * Get the category and cache it for the file.  Category lookups will probibly get expensive long before this (since lookups for the same file
	 * should come in one after the other).
	 * @param theFile
	 * @return the category.
	 * @throws Throwable
	 */
	private synchronized Category lookupCategory(Node_File theFile) throws Throwable {
		if (theFile==lastFile) return lastCategory;
		lastCategory = CATEGORY_LOOKUP_EXTENSION(theFile.getFileExtension());
		lastFile = theFile;
		return lastCategory;
	}
	


}



