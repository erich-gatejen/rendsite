/**
 * RendSite 2008
 * Copyright Erich P Gatejen 2008, 2009 
 */
package rendsite.engine;

/**
 * Context for the stored properties.  Be careful with these.  The results are only valid for the directory you're currently processing, since
 * the metafile scope changes in every directory.
 * @author  erich
 * <i>Version History</i>
 * <pre>
 * 10NOV09 - EPG - First package release.
 * </pre>
 */
public interface PropertiesGeneralContext {

	// ===================================================================================================================
	// = FIELDS
	

	// ======================================================================================================================
	// = METHODS

	/**
	 * Get a property value or the given default, if the property is not set.  Both local and non-local properties will be checked.
	 * @param name the property name.
	 * @param theDefault the default value.
	 * @return the value property or the default value.
	 */
	public String GET_DEFAULT(String name, String theDefault);
	
	/**
	 * Get a property value or the given default, if the property is not set.  Only LOCAL properties will be check.
	 * @param name the property name.
	 * @param theDefault the default value.
	 * @return the value property or the default value.
	 */
	public String GET_LOCAL_DEFAULT(String name, String theDefault);
	
	/**
	 * Get a property multi-value.  Both local and non-local properties will be checked.
	 * @param name the property name.
	 * @return an array of values for this property or null if the property isn't set.
	 */
	public String[] GET_MULTIVALUE(String name) throws Throwable;
	
	/**
	 * Check to see if a token matches the value of a property.  It will work against a multi-value property.  
	 * It allows filename wildcards (like *).  It is case sensitive.
	 * @param name the property name.
	 * @param token the token to match.
	 * @return true if the property is set and the token matches any of its values, otherwise false.
	 */
	public boolean MATCH(String name, String token ) throws Throwable;
	

}



