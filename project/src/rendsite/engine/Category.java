/**
 * RendSite 2008
 * Copyright Erich P Gatejen 2008, 2009 
 */
package rendsite.engine;

import java.util.HashSet;

/**
 * A category.  It is a classification for files.
 * 
 * @author  erich
 * <i>Version History</i>
 * <pre>
 * 10NOV09 - EPG - First package release.
 * </pre>
 */
public class Category  {
	
	// =============================================================================
	// FIELDS
	
	/**
	 * Unique name.   Suitable for identifying.
	 */
	public String id;
	
	/**
	 * Long name format, suitable for rendering.
	 */
	public String displayType;
	
	/**
	 * MIME or fabricated type.  If applicable, use a proper mime type, otherwise make something up.
	 */
	public String type;
	
	/**
	 * Rendering type.
	 */
	public RenderingType renderingType;
	
	/**
	 * MIME or fabricated subtype.  If applicable, use a proper mime subtype, otherwise make something up.
	 */
	public String subtype;

	/**
	 * File extensions.  These are common extensions found for this file.
	 */
	public HashSet<String> extensions;
	
	// =============================================================================
	// DATA
	

	// =============================================================================
	// METHODS
	
	/**
	 * Construct without extensions (the list will be empty)
	 */
	public Category(String id, String displayType, String type, String subtype, RenderingType renderingType) {
		this.id = id;
		this.type = type;
		this.displayType = displayType;
		this.subtype = subtype;
		this.renderingType = renderingType;
		this.extensions = new HashSet<String>();
	}
	
	/**
	 * Construct with extensions.
	 */
	public Category(String id, String displayType, String type, String subtype, RenderingType renderingType, String... extensions) {
		this.id = id;
		this.type = type;
		this.displayType = displayType;
		this.subtype = subtype;
		this.renderingType = renderingType;
		this.extensions = new HashSet<String>();
		for (String item : extensions) {
			this.extensions.add(item);
		}
		
	}
	
	/**
	 * Render the category as a simple String.
	 * @return the String.
	 */
	public String toString() {
		StringBuffer result = new StringBuffer();
		result.append(id);
		result.append(':');
		result.append(displayType);
		result.append(':');
		result.append(type);
		result.append(':');
		result.append(subtype);
		result.append(':');
		result.append(renderingType);
		result.append(':');
		for (String item : extensions) {
			result.append(item);
			result.append(',');		
		}
		return result.toString();
	}
	
	// =============================================================================
	// INTERNAL	
	

}



