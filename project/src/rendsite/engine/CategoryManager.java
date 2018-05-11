/**
 * RendSite 2008
 * Copyright Erich P Gatejen 2008, 2009 
 */
package rendsite.engine;

import java.util.HashMap;

import rendsite.Constants;
import rendsite.RendsiteException;
import things.common.ThingsException;

/**
 * A Category Manager.  It'll be hardcoded for now.
 * 
 * @author  erich
 * <i>Version History</i>
 * <pre>
 * 10NOV09 - EPG - First package release.
 * </pre>
 */
public class CategoryManager  {
	
	// =============================================================================
	// FIELDS
	
	
	// =============================================================================
	// DATA
	
	/**
	 * Categories by extensions.
	 */
	private static HashMap<String, Category> catagoryExtMap;
	
	/**
	 * Categories by id.  Duplicates ids are NOT allowed.
	 */
	private static HashMap<String, Category> catagoryIdMap;

	
	// =============================================================================
	// METHODS
	
	/**
	 * Constructor.
	 */
	public CategoryManager() throws Throwable {
		catagoryMapsCheck();
	}
	
	/**
	 * Look up an extension to see if it has a category.
	 * @param extension the extension.  if null, the method will return a null.
	 * @return the category.
	 * @throws Throwable
	 */
	public Category lookupExtension(String extension) {
		if (extension==null) return null;
		return catagoryExtMap.get(extension.toLowerCase());
	}
	
	/**
	 * Get a category by id.
	 * @param id the id.  if null, the method will return a null.
	 * @return the category.
	 * @throws Throwable
	 */
	public Category get(String id) {
		if (id==null) return null;
		return catagoryIdMap.get(id);
	}

	// =============================================================================
	// INTERNAL	
	
	/**
	 * Do we have the category map?  if not, build it--the hardcoded way.  Yuck, I know.  We can move it to a 
	 * configuration later.
	 */
	private synchronized void catagoryMapsCheck() throws Throwable {
		if (catagoryExtMap==null) {
			
			// Ugly hax hardcode for now.
			catagoryExtMap = new HashMap<String, Category>();
			catagoryIdMap = new HashMap<String, Category>();
			//Category working;
/**
 * 		DOCUMENT,
		WEB,
		SOURCE,
		APPLICATION,
		IMAGE,
		ARCHIVE,
		CONFIGURATION,
		OTHER;
 */
			putMap(new Category("program", "Program", "application", "octet-stream", RenderingType.APPLICATION, "exe"));
			putMap(new Category("batch", "Batch script", "application", "x-msdos-program", RenderingType.APPLICATION, "bat", "cmd"));
			
			putMap(new Category("text/plain", "Text", "text", "plain", RenderingType.DOCUMENT, "txt", "text"));
			putMap(new Category("text/rtf", "Rich Text", "text", "rtf", RenderingType.DOCUMENT, "rtf"));
			putMap(new Category("word", "MS Word Document", "application", "msword", RenderingType.DOCUMENT, "doc"));
			putMap(new Category("excel", "MS Excel Spreadsheet", "application", "excel", RenderingType.DOCUMENT, "xls"));
			putMap(new Category("powerpoint", "MS Powerpoint Presentation", "application", "vnd.ms-powerpoint", RenderingType.DOCUMENT, "ppt"));
			putMap(new Category("pdf", "PDF", "application", "pdf", RenderingType.DOCUMENT, "pdf"));
		
			putMap(new Category("asm", "Assembler Source", "text", "plain", RenderingType.SOURCE, "asm"));	
			putMap(new Category("use", "Assembler Header Source", "text", "plain", RenderingType.SOURCE, "use"));
			putMap(new Category("c", "C Source", "text", "plain", RenderingType.SOURCE, "c"));		
			putMap(new Category("c++", "C++ Source", "text", "plain", RenderingType.SOURCE, "cpp"));	
			putMap(new Category("h", "C Header Source", "text", "plain", RenderingType.SOURCE, "h"));		
			putMap(new Category("h++", "C++ Header Source", "text", "plain", RenderingType.SOURCE, "hpp"));	
			putMap(new Category("java", "Java Source", "text", "java", RenderingType.SOURCE, "java"));	
			
			putMap(new Category("text/html", "Web Page", "text", "html", RenderingType.WEB, "html", "htm", "xhtml"));
			putMap(new Category("text/xml", "XML", "text", "xml", RenderingType.WEB, "xml"));
			putMap(new Category("text/css", "Style sheet", "text", "css", RenderingType.WEB, "css"));
			putMap(new Category("xmldtd", "XML DTD", "application", "xml-dtd", RenderingType.WEB, "dtd"));
			
			putMap(new Category("javaclass", "Java class", "application", "java", RenderingType.OTHER, "class"));
			
			putMap(new Category("prop", "Property file", "application", "x-config", RenderingType.CONFIGURATION, "prop", "ini", "config"));
			
			putMap(new Category("gif", "GIF image", "image", "gif", RenderingType.IMAGE, "gif"));
			putMap(new Category("vga.bitmap", "VGA flat bitmap", "image", "x-vga-bitmap", RenderingType.IMAGE, "vga"));
			putMap(new Category("jpeg", "JPEG image", "image", "jpeg", RenderingType.IMAGE, "jpg", "jpeg", "jpe"));
			putMap(new Category("png", "PNG image", "image", "png", RenderingType.IMAGE, "png"));			
			
			putMap(new Category("jar", "Java archive", "application", "java-archive", RenderingType.ARCHIVE, "jar"));
			putMap(new Category("zip", "ZIP archive", "application", "zip", RenderingType.ARCHIVE, "zip"));

		}
	}
	
	private void putMap(Category category) throws Throwable {
		if (category.id==null) ThingsException.softwareProblem("Catagory Manager maps created failed because of a null category.id", Constants.NAME_CATEGORY_COMPLETE, category.toString());
		if (catagoryIdMap.containsKey(category.id)) ThingsException.softwareProblem("Catagory Manager already has an entry with that category.id", Constants.NAME_CATEGORY_COMPLETE, category.toString(), Constants.NAME_CATEGORY_COMPLETE_EXISTING, catagoryIdMap.get(category.id).toString());
		catagoryIdMap.put(category.id, category);
		String extensionNormal;
		for (String item : category.extensions) {
			// All extensions are normalized to lowercase
			extensionNormal = item.toLowerCase();
			if (catagoryExtMap.containsKey(extensionNormal))  RendsiteException.softwareProblem("Catagory Manager already has an entry with that file extension", Constants.NAME_CATEGORY_COMPLETE, category.toString(), Constants.NAME_CATEGORY_COMPLETE_EXISTING, catagoryExtMap.get(item).toString());
			catagoryExtMap.put(extensionNormal, category);
		}
	}


}



