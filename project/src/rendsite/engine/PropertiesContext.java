/**
 * RendSite 2008
 * Copyright Erich P Gatejen 2008, 2009 
 */
package rendsite.engine;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Stack;

import org.apache.commons.io.FilenameUtils;

import rendsite.Codes;
import rendsite.Constants;
import rendsite.RendsiteConfiguration;
import rendsite.RendsiteException;
import rendsite.RendsiteProperties;
import rendsite.engine.node.Node;
import rendsite.engine.node.Node_Directory;
import rendsite.engine.node.Node_File;
import rendsite.engine.node.Node_MetaFile;
import rendsite.engine.node.Node_Scope;
import rendsite.engine.node.Node_Value;
import things.data.NV;
import things.data.NVImmutable;
import things.data.ThingsPropertyReaderToolkit;
import things.data.ThingsPropertyTree;
import things.data.ThingsPropertyView;
import things.data.impl.FileAccessor;
import things.data.impl.ThingsPropertyTreeRAM;
import things.data.impl.ThingsPropertyTrunkIO;

/**
 * Context for the stored properties.
 * <p>
 * will have to redo this completely, since it won't scale and they are shitty slow.  It's enough to get things going now.  
 * @author  erich
 * <i>Version History</i>
 * <pre>
 * 10NOV09 - EPG - First package release.
 * </pre>
 */
public class PropertiesContext extends Context implements RendsiteProperties, PropertiesGeneralContext, PropertiesFileContext {

	// ===================================================================================================================
	// = FIELDS
	
	// ===================================================================================================================
	// = DATA - STATEFUL - These should be reset.
	protected ThingsPropertyTree store;
	protected ThingsPropertyView storeView;
	protected ThingsPropertyReaderToolkit storeToolkit;
	protected ThingsPropertyTree local;
	protected ThingsPropertyView localView;
	protected static ThingsPropertyTree localEmpty = new ThingsPropertyTreeRAM();
	protected ThingsPropertyReaderToolkit localToolkit;
	protected Node_Directory scopedDirectory;			// The directory currently in scope

	private Stack<Node>  ss;
	
	// ===================================================================================================================
	// = DATA - CONSTRUCTOR - These should not be reset.
	private CategoryManager categoryManager;
	
	// ===================================================================================================================
	// = ABSTRACT	

	/**
	 * Reset the context.  It should lose all state except what was set during construction;
	 */
	public void reset() throws Throwable {
		store = new ThingsPropertyTreeRAM();		
		storeView = store.getRoot();
		storeToolkit = new ThingsPropertyReaderToolkit(store);
		
		ss = new Stack<Node>();
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
	public PropertiesContext(RendsiteConfiguration configuration, File sourceDirectory, File outputDirectory) throws Throwable {
		super(configuration);
		reset();
	
		// Set a proper logger
		logger = configuration.getSystemInterface().getNamedLogger("PROP");
		
		// Create the static category manager.  We'll build this from configuration later.
		categoryManager = new CategoryManager();
	}
	
	/**
	 * Get a FILE ply from the local.
	 * @param ply
	 * @return the view to the ply.  It may be empty, not not configured.
	 * @throws Throwable
	 */
	public ThingsPropertyView viewLocalFilesPly(int ply) throws Throwable {
		if (local==null) RendsiteException.softwareProblem("Called viewLocalFilesPly before any local was instantiated.");
		return localView.cutting(RendsiteProperties.PROP_FILE + "." + Integer.toString(ply));
	}
	
	/**
	 * Get a DIRECTORY ply from the local.
	 * @param ply
	 * @return the view to the ply.  It may be empty, not not configured.
	 * @throws Throwable
	 */
	public ThingsPropertyView viewLocalDirectoriesPly(int ply) throws Throwable {
		if (local==null) RendsiteException.softwareProblem("Called viewLocalDirectoriesPly before any local was instantiated.");
		return localView.cutting(RendsiteProperties.PROP_DIRECTORY + "." + Integer.toString(ply));
	}
	
	/**
	 * Get the category manager.
	 * @return the category manager
	 */
	public CategoryManager getCategoryManager() {
		return categoryManager;
	}
	
	// ======================================================================================================================
	// = PROPERTY TOOLS
	
	/**
	 * Get a property value or the given default, if the property is not set.  Both local and non-local properties will be checked.
	 * @param name the property name.
	 * @param theDefault the default value.
	 * @return the value property or the default value.
	 */
	public String GET_DEFAULT(String name, String theDefault) {
		String result = null;
		if (localToolkit!=null) result = localToolkit.getDefaulted(name, null);
		if (result==null) result = storeToolkit.getDefaulted(name, theDefault);
		return result;
	}
	
	/**
	 * Get a property value.  If it isn't set, it will throw an exception.  Both local and non-local properties will be checked.
	 * @param name the property name.
	 * @return the value property or the default value.
	 * @throws Throwable if the property isn't set.
	 */
	public String GET_REQUIRED(String name) throws Throwable {
		String result = null;
		if (localToolkit!=null) result = localToolkit.getOptional(name);
		if (result==null) result = storeToolkit.getRequired(name);
		return result;
	}
	
	/**
	 * Get a property value or the given default, if the property is not set.  Only LOCAL properties will be check.
	 * @param name the property name.
	 * @param theDefault the default value.
	 * @return the value property or the default value.
	 */
	public String GET_LOCAL_DEFAULT(String name, String theDefault) {
		String result = theDefault;
		if (localToolkit!=null) result = localToolkit.getDefaulted(name, theDefault);
		return result;
	}
	
	/**
	 * Get a property multi-value.  Both local and non-local properties will be checked.
	 * @param name the property name.
	 * @return an array of values for this property or null if the property isn't set.
	 * @throws Throwable for severe platform problems only.
	 */
	public String[] GET_MULTIVALUE(String name) throws Throwable {
		String[] result = null;
		if (localToolkit!=null) result = localToolkit.getOptionalAsMulti(name);
		if (result==null) result = storeToolkit.getOptionalAsMulti(name);
		return result;
	}
	
	/**
	 * Check to see if a token matches the value of a property.  It will work against a multi-value property.  
	 * It allows filename wildcards (like *).  It is case sensitive.
	 * @param propname the property name.
	 * @param token the token to match.
	 * @return true if the property is set and the token matches any of its values, otherwise false.
	 * @throws Throwable for severe platform problems only. 
	 */
	public boolean MATCH(String propname, String token) throws Throwable {
		String[] ed = GET_MULTIVALUE(propname);
		if (ed==null) return false;
		if (ed!=null) { 
			for (String item : ed) {
				if (FilenameUtils.wildcardMatch(token,item)) return true;
			}
		}
		return false;
	}
	
	/**
	 * Get the name of the catalog from the directory.  If it is not already set in the node, it will try to figure it out from
	 * properties.  A copy only directory will yield a null, unless it has already been set.
	 * @param directory the directory
	 * @return the name of the catalog.
	 * @throws Throwable for severe platform problems only. 
	 */
	public String GET_CATALOG_NAME(Node_Directory directory) throws Throwable {
		if (directory.getCatalogName()==null) {
			if (!COPY_ONLY(directory)) 
				directory.setCatalogName(GET_DEFAULT(PROP_CATALOG_FILE_NAME, PROP_CATALOG_FILE_NAME__DEFAULT));
		}
		// NOTE: copy only directories will be left with null, meaning there is no default catalog, or the name imposed by the engine due to 
		// explicit configuration.
		return directory.getCatalogName();
	}
	
	/**
	 * Is the directory excluded?
	 * @param directory the directory.
	 * @return true if it is excluded, otherwise false.
	 * @throws Throwable for severe platform problems only.
	 */
	public boolean EXCLUDED(Node_Directory directory) throws Throwable {
		if (directory==null)return false;
		if (directory.excluded==true) return true;
		return MATCH(PROP_DIR_EXCLUDE, directory.getName());
	}
	
	/**
	 * Is the file excluded?
	 * @param file the file.
	 * @return true if it is excluded, otherwise false.
	 * @throws Throwable for severe platform problems only.
	 */
	public boolean EXCLUDED(Node_File file) throws Throwable {
		if (file==null)return false;
		if (file.excluded==true) return true;
		return MATCH(PROP_FILE_EXCLUDE, file.getName());
	}
	
	/**
	 * Is the file or directory excluded?
	 * @param file the java File representing a directory or file.
	 * @return true if it is excluded, otherwise false.
	 * @throws Throwable for severe platform problems only.
	 */
	public boolean EXCLUDED(File file) throws Throwable {
		if (file==null)return false;
		return ( (MATCH(PROP_FILE_EXCLUDE, file.getName())) | (MATCH(PROP_DIR_EXCLUDE, file.getName())) );
	}
	
	/**
	 * Is the directory ignored?
	 * @param directory the directory.
	 * @return true if it is ignored, otherwise false.
	 * @throws Throwable for severe platform problems only.
	 */
	public boolean IGNORED(Node_Directory directory) throws Throwable {
		if (directory==null)return false;
		if (directory.ignored==true) return true;
		return MATCH(PROP_DIR_IGNORE, directory.getName());
	}
	
	/**
	 * Is the file ignored?
	 * @param file the file.
	 * @return true if it is ignored, otherwise false.
	 * @throws Throwable for severe platform problems only.
	 */
	public boolean IGNORED(Node_File file) throws Throwable {
		if (file==null)return false;
		if (file.ignored==true) return true;
		return MATCH(PROP_FILE_IGNORE, file.getName());
	}
	
	/**
	 * Is the file or directory ignored?
	 * @param file the java File representing a directory or file.
	 * @return true if it is ignored, otherwise false.
	 * @throws Throwable for severe platform problems only.
	 */
	public boolean IGNORED(File file) throws Throwable {
		if (file==null)return false;
		return ( (MATCH(PROP_FILE_IGNORE, file.getName())) | (MATCH(PROP_DIR_IGNORE, file.getName())) );
	}
	
	/**
	 * Is the file copy only?
	 * @param file the  file.
	 * @return true if it is copy only, otherwise false.
	 * @throws Throwable for severe platform problems only.
	 */
	public boolean COPY_ONLY(Node_File file) throws Throwable {
		if (file==null)return false;
		if (file.copyOnly==true) return true;
		return MATCH(PROP_FILE_COPY_ONLY, file.getName());
	}
	
	/**
	 * Is the directory copy only?
	 * @param directory the directory.
	 * @return true if it is copy only, otherwise false.
	 * @throws Throwable for severe platform problems only.
	 */
	public boolean COPY_ONLY(Node_Directory directory) throws Throwable {
		if (directory==null)return false;
		if (directory.copyOnly==true) return true;
		return MATCH(PROP_DIR_COPY_ONLY, directory.getName());
	}
	
	// ======================================================================================================================
	// = SCOPE MANAGEMENT
	
	/**
	 * Enter a scope.  Load metafiles for a directory.  Overlapping properties will defer to the last loaded.
	 * The metafiles will be sorted before the load.
	 * @param directory
	 * @throws Throwable will cause ERRORs if they can't load, but the scope should be ok.  Be sure to call unscope() even if you get an exception.
	 */
	public void scope(Node_Directory	directory) throws Throwable {
		File current = null;
		ThingsPropertyTrunkIO trunk = new ThingsPropertyTrunkIO();
		NV nv = null;
		NVImmutable old = null;

		// Scope it.
		ss.push(new Node_Scope(local, scopedDirectory));
		scopedDirectory = directory;
		
		// Load each one.  Any failure is an ERROR for this scope
		try {
		
			// - Handle local metafiles -----------------------------------------------------------------------------------------
			// Get the metafiles as files and sort them.  
			List<Node_MetaFile> metaFiles = directory.getLocalMetaFiles();
			File[] metaFileArray = new File[metaFiles.size()];
			for (int rover = 0 ; rover < metaFiles.size() ; rover++) {
				metaFileArray[rover] = metaFiles.get(rover).file;
			}
			Arrays.sort(metaFileArray);
			
			// Read them and create a merge space.
			ThingsPropertyTreeRAM localtree = new ThingsPropertyTreeRAM();
			localtree.init(null);
			for (File meta : metaFileArray) {	
				current = meta;
			
				// We're going to use THING's property format.  Multivalues not supported for now.
				trunk.init(meta.getName(), new FileAccessor(meta));
				trunk.startRead();
				
				nv = trunk.readNext();
				while (nv != null) {
					localtree.setProperty(nv);
					nv = trunk.readNext();
				}
				
				// Success for that file.
				if (logger.debuggingState()) logger.debug("Loading Local MetaFile", Codes.DEBUG, Constants.NAME_METAFILE, directory.getPath());
				trunk.endRead();
				current = null;
			}
			local = localtree;
			localView = local.getRoot();
			localToolkit = new ThingsPropertyReaderToolkit(local);
			
			// - Handle metafiles  -------------------------------------------------------------------------------------------------
			
			// Get the metafiles as files and sort them.  
			metaFiles = directory.getMetaFiles();
			metaFileArray = new File[metaFiles.size()];
			for (int rover = 0 ; rover < metaFiles.size() ; rover++) {
				metaFileArray[rover] = metaFiles.get(rover).file;
			}
			Arrays.sort(metaFileArray);
			
			// Read them and create a merge space.
			ThingsPropertyTreeRAM tree = new ThingsPropertyTreeRAM();
			tree.init(null);
			for (File meta : metaFileArray) {	
				current = meta;
			
				// We're going to use THING's property format.  Multivalues not supported for now.
				trunk.init(meta.getName(), new FileAccessor(meta));
				trunk.startRead();
				
				nv = trunk.readNext();
				while (nv != null) {
					tree.setProperty(nv);
					nv = trunk.readNext();
				}
				
				// Success for that file.
				if (logger.debuggingState()) logger.debug("Loading MetaFile", Codes.DEBUG, Constants.NAME_METAFILE, directory.getPath());
				trunk.endRead();
				current = null;
			}
			
			// Map them into the store.
			// if the property name exists already, push old value on the scope stack.
			Collection<String> newProperties = tree.sub("");
			NVImmutable newnv;
			for (String currentProp : newProperties) {
				newnv = tree.getPropertyNV(currentProp);
				old = storeView.getPropertyNV(newnv.getName());
				if (old==null) {
					ss.push(new Node_Value(newnv.getName(), null));	
				} else {
					ss.push(new Node_Value( new NV(newnv.getName(), old.getValues()) ));
				}
				
				// Always put it in store.  Use the get methods to force them to single value--for now.
				storeView.setProperty(newnv);
			}

		} catch (Throwable t) {
			if (current!=null) 
				throw new RendsiteException("Failed to load metafile due to error.", Codes.FAULT_METAFILE_LOAD, t, Constants.NAME_METAFILE, current.getAbsolutePath());
			else
				throw new RendsiteException("Failed to load metafile due to error.", Codes.FAULT_METAFILE_LOAD, t);	
		}
	}
	
	/**
	 * Exit a scope.
	 * @throws Throwable Any problem unscoping is a PANIC, since the scope stack will be in an unknown state.
	 */
	public void unscope() throws Throwable {
		try {
			
			// Pop the scope stack until we get a NodeScope.
			Node currentNode = ss.pop();
			while(true) {			
				switch(currentNode.getType()) {
				
				case VALUE:
					removeValue((Node_Value)currentNode);
					break;
					
				case SCOPE:
					local = ((Node_Scope)currentNode).local;
					scopedDirectory = ((Node_Scope)currentNode).directory;
					if (local==null) local = localEmpty;
					localToolkit = new ThingsPropertyReaderToolkit(local);
					return;
					
				default:
					throw new Exception("Unexpected type on the scope stack.  Type=" + currentNode.getType().name());
				}	
				currentNode = ss.pop();
			}
			
		} catch (Throwable t) {
			throw new RendsiteException("Failed to unscope.  This is fatal.", Codes.PANIC_RUN_SCOPE_RUINED, t);	
		}
	}
	
	// ======================================================================================================================
	// = PATH AND URL METHODS
	
	/**
	 * Remove a value.  If the value is not null, it will replace the value in the store for this name.  If the value is null, it
	 * will remove any entry in the store for the name.
	 * @param value the node value
	 * @throws Throwable
	 */
	private void removeValue(Node_Value value) throws Throwable {
		
		if (value.nv.getValue()==null) {			
			storeView.removeProperty(value.nv.getName());
		} else {			
			storeView.setProperty(value.nv);
		}		
	}
	
	
	
}



