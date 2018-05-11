/**
 * RendSite 2008
 * Copyright Erich P Gatejen 2008, 2009 
 */
package rendsite.resources;

import java.util.Locale;
import java.util.ResourceBundle;

import rendsite.Codes;
import rendsite.Constants;
import rendsite.RendsiteException;
import things.common.ThingsException;
import things.common.ThingsUtilityBelt;

/**
 * Resource management.
 * @author  erich
 * <i>Version History</i>
 * <pre>
 * 10NOV09 - EPG - First package release.
 * </pre>
 */
public class Resources {
	
	// ========================================================================================
	// RESOURCES
	public final static String RESOURCE_MESSAGES = "RendsiteResourceMessages";
	
	static public Locale GOATLANDINESE = new Locale("xx__", "xx", "");
	static public Locale GOATLANDIA = new Locale("xx_XX_", "xx", "XX");
	
	// ========================================================================================
	// DATA
	private Locale locale;
	
	// ========================================================================================
	// METHODS

	/**
	 * Construct.
	 * @param locale the locale.
	 * @throws Throwable
	 */
	public Resources(Locale locale) throws Throwable {
		if (locale==null) ThingsException.softwareProblem("Cannot instantiate Resources with a null Locale.");
		this.locale = locale;
		
		// Test it.
		try {
			ResourceBundle.getBundle(RESOURCE_MESSAGES, locale);
		} catch (Throwable t) {
			throw new RendsiteException("Unable to load messages resource for the locale.", Codes.PANIC_SETUP_RESOURCES__COULD_NOT_LOAD_MESSAGES_FOR_LOCALE, t, Constants.NAME_LOCALE, locale.toString());
		}
	}
	
	/**
	 * Get the MESSAGES bundle.
	 * @return the resource bundle.
	 * @throws Throwable
	 */
	public ResourceBundle getMessagesBundle() throws Throwable {
		return getBundle(RESOURCE_MESSAGES);
	}
	
	/**
	 * Get the messaging convenience.
	 * @return messaging convenience class.
	 * @throws Throwable
	 */
	public Messaging getMessaging() throws Throwable {
		return new Messaging(this);
	}
	
	/**
	 * Get the named bundle.
	 * @param resourceName the resource name
	 * @return the resource bundle
	 * @throws Throwable
	 */
	public ResourceBundle getBundle(String resourceName) throws Throwable {
		if (resourceName==null) ThingsException.softwareProblem("Cannot instantiate Resources with a null resourceName.");
		ResourceBundle result = null;
		try {
			result =  ResourceBundle.getBundle(resourceName, locale);
		} catch (Throwable t) {
			ThingsException.softwareProblem("Unable to load bundle.  This is a bug.  The load should have been tested during construction.", t);
		}
		return result;
	}

	// ========================================================================================
	// STATIC TOOLS
	
	/**
	 * Do a safe lookup.  If the key isn't found, return null.
	 * @param bundle the resource bundle.
	 * @param key the key
	 * @return the value or null
	 */
	public static String lookup(ResourceBundle bundle, String key) {
		String result = null;
		try {
			result = bundle.getString(key);
		} catch (Throwable t) {
			// Let the null return
		}
		return result;
	}
	
	/**
	 * Do a safe lookup.  If the key isn't found, return the defaultText.
	 * @param bundle the resource bundle.
	 * @param key the key
	 * @param defaultText the default text
	 * @return the value or the default text.
	 */
	public static String lookup(ResourceBundle bundle, String key, String defaultText) {
		String result = defaultText;
		try {
			result = bundle.getString(key);
		} catch (Throwable t) {
			// Let the default return
		}
		return result;
	}
	
	/**
	 * Do a safe lookup of a numeric.  It will be converted to the 4 digit hexidecimal for a key.  This is how the numerics are
	 * mapped to messages.  If the key isn't found, return null.
	 * @param bundle the resource bundle.
	 * @param numeric the numeric key.
	 * @return the value or null
	 */
	public static String lookupNumeric(ResourceBundle bundle, int numeric) {
		String result = null;
		try {
			result = bundle.getString(ThingsUtilityBelt.hexFormatter16bit(numeric));
		} catch (Throwable t) {
			// Let the null return
		}
		return result;
	}
	
	/**
	 * Do a safe lookup of a numeric.  It will be converted to the 4 digit hexidecimal for a key.  This is how the numerics are
	 * mapped to messages.  If the key isn't found, return defaultText.
	 * @param bundle the resource bundle.
	 * @param numeric the numeric key.
	 * @param defaultText the default text
	 * @return the value or the default text.
	 */
	public static String lookupNumeric(ResourceBundle bundle, int numeric, String defaultText) {
		String result = defaultText;
		try {
			result = bundle.getString(ThingsUtilityBelt.hexFormatter16bit(numeric));
		} catch (Throwable t) {
			// Let the null return
		}
		return result;
	}
	
}



