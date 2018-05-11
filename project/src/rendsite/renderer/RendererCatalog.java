/**
 * RendSite 2008
 * Copyright Erich P Gatejen 2008, 2009 
 */
package rendsite.renderer;

import java.lang.reflect.Constructor;
import java.util.HashMap;

import rendsite.Codes;
import rendsite.Constants;
import rendsite.RendsiteException;
import rendsite.engine.PropertiesGeneralContext;
import rendsite.engine.RenderingContext;
import rendsite.engine.Resetable;
import things.common.ThingsException;

/**
 * Catalog of renderers.  
 * <p>
 * Mostly hardcoded for now.  When it gets bigger, the configuration can go into a file.
 * @author  erich
 * <i>Version History</i>
 * <pre>
 * 10NOV09 - EPG - First package release.
 * </pre>
 */
public class RendererCatalog implements Resetable {

	// ==================================================================================================================
	// = FIELDS
	
	public final static int MAX_CLASS_NOT_FOUND_FOR_PANIC = 5;
	
	/**
	 * Default renderer name.
	 */
	public final static String DEFAULT_RENDERER_NAME = "default";
	public final static String DEFAULT_RENDERER_CLASS_NAME = "rendsite.renderer.Renderer_Default";
	
	/**
	 * Template processing version 1.
	 */
	public final static String TEMPLATE_V1_RENDERER_NAME = "templatev1";
	public final static String TEMPLATE_V1_RENDERER_CLASS_NAME = "rendsite.renderer.Renderer_Templatev1_MAIN";
	
	/**
	 * Template for code (software) processing version 1.  It will process rather than just copy code files.
	 */
	public final static String SOURCETEMPLATE_V1_RENDERER_NAME = "templatev1_source";
	public final static String SOURCETEMPLATE_V1_RENDERER_CLASS_NAME = "rendsite.renderer.Renderer_Templatev1_SOURCE";
	
	// ==================================================================================================================
	// = DATA
	private HashMap<String,String> lookup;
	private HashMap<String,Renderer> cache;

	// ==================================================================================================================
	// = METHODS

	/**
	 * Construct a renderer catalog.  
	 * @throws Throwable will only happen if the context is bad or the catalog could not be loaded.
	 */
	public RendererCatalog() throws Throwable {
		lookup = getLookup();
		cache = new HashMap<String,Renderer>();
	}
	
	/**
	 * Get a renderer by name.
	 * @param name The name.  It is case sensitive!  If null or empty, it will get the default.
	 * @param rcontext needed if the renderer will be constructed.
	 * @param pcontext needed if the renderer will be constructed.
	 * @return an instantiated renderer.  There will be only one instance per catalog, so instantiate more catalogs if you expect lots of simultaneous usage.
	 * @throws ThingsException
	 */
	@SuppressWarnings("unchecked")
	public synchronized Renderer getRenderer(String name, RenderingContext rcontext, PropertiesGeneralContext pcontext) throws ThingsException {
		Renderer result = null;
		String actualName = name;
		if ((name==null)||(name.length()<1)) actualName = DEFAULT_RENDERER_NAME;
		
		// In cache?
		if (cache.containsKey(actualName)) return cache.get(actualName);
		
		// In catalog?
		String className = lookup.get(actualName);
		if (className==null) throw new RendsiteException("Renderer not defined in catalog", Codes.ERROR_RENDERER_INST__DOES_NOT_EXIST, Constants.NAME_RENDERER_NAME, actualName);
		
		// Yes, try to instantiate it.
		try {
			Class<Renderer> actualClass = (Class<Renderer>) Class.forName(className);
			Constructor<Renderer> constructor = actualClass.getConstructor(RenderingContext.class, PropertiesGeneralContext.class);
			result = constructor.newInstance(rcontext, pcontext);
			
		} catch (ClassNotFoundException	cnfe) {
			if (classNotFoundCounter()) {
				throw new RendsiteException("Could not find configured renderer too many times.", Codes.PANIC_RENDERER_INST__COUNT_NOT_FIND_TOO_MANY_TIMES, Constants.NAME_RENDERER_NAME, actualName, Constants.NAME_RENDERER_CLASS, className);
			} else {
				throw new RendsiteException("Could not find configured renderer.", Codes.FAULT_RENDERER_INST__COUNT_NOT_FIND, Constants.NAME_RENDERER_NAME, actualName, Constants.NAME_RENDERER_CLASS, className);
			}
		} catch (Throwable t) {
			throw new RendsiteException("Failed to instantiate renderer due to spurious exception.", Codes.PANIC_RENDERER_INSTANTIATION, t, Constants.NAME_RENDERER_NAME, actualName, Constants.NAME_RENDERER_CLASS, className);
		}
		
		return result;
	}

	// ==================================================================================================================
	// = RESET INTERFACE - this implementation is not threads safe.
	
	/**
	 * Reset the object.  It should lose all state except what was set during construction.
	 * @throws Throwable if it could not completely reset state.
	 */
	public void reset() throws Throwable {
		cache = new HashMap<String,Renderer>();
		System.gc();
	}
	
	// ==================================================================================================================
	// = INTERNAL METHODS
	
	private static int counter = 0;
	/**
	 * Have we exceeded class not found faults?
	 * @return
	 */
	private static synchronized boolean classNotFoundCounter() {
		counter++;
		if (counter > MAX_CLASS_NOT_FOUND_FOR_PANIC) {
			counter = 0;
			return true;
		} 
		return false;
	}
	
	// ==================================================================================================================
	// = HARDCODE CATALOG	
	private static HashMap<String,String> 	hardcodeLookup = null;
	
	private static synchronized HashMap<String,String> getLookup() {
		if (hardcodeLookup==null) {
			hardcodeLookup = new HashMap<String,String>();
			
			hardcodeLookup.put(DEFAULT_RENDERER_NAME, DEFAULT_RENDERER_CLASS_NAME);
			hardcodeLookup.put(TEMPLATE_V1_RENDERER_NAME, TEMPLATE_V1_RENDERER_CLASS_NAME);
			hardcodeLookup.put(SOURCETEMPLATE_V1_RENDERER_NAME, SOURCETEMPLATE_V1_RENDERER_CLASS_NAME);
		}
		return hardcodeLookup;
	}
	

}



