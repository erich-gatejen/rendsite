/**
 * RendSite 2008
 * Copyright Erich P Gatejen 2008, 2009 
 */
package rendsite.resources;

import java.util.ResourceBundle;

import rendsite.Codes;
import rendsite.RendsiteException;
import things.common.ThingsException;

/**
 * Convenience class for deal with messages.
 * @author  erich
 * <i>Version History</i>
 * <pre>
 * 10NOV09 - EPG - First package release.
 * </pre>
 */
public class Messaging {
	
	// ========================================================================================
	// RESOURCES
	
	// ========================================================================================
	// DATA
	//private Resources resources;
	private ResourceBundle messages;
	
	// ========================================================================================
	// METHODS

	/**
	 * Construct.
	 * @param resources the system resources.
	 * @throws Throwable
	 */
	public Messaging(Resources resources) throws Throwable {
		if (resources==null) ThingsException.softwareProblem("Cannot instantiate Messaging with a null Resources.");
		//this.resources = resources;
		
		// Test it.
		try {
			messages = resources.getMessagesBundle();
		} catch (Throwable t) {
			throw new RendsiteException("Unable to to create Messaging convenience instance.", Codes.PANIC_SETUP_RESOURCES__COULD_NOT_BUILD_MESSAGING_OBJ, t);
		}
	}

	/**
	 * Do a safe lookup.  If the key isn't found, return null.
	 * @param key the key
	 * @return the value or null
	 */
	public String lookup(String key) {
		return Resources.lookup(messages, key);
	}
	
	/**
	 * Do a safe lookup.  If the key isn't found, return the defaultText.
	 * @param key the key
	 * @param defaultText the default text
	 * @return the value or the default text.
	 */
	public String lookup(String key, String defaultText) {
		return Resources.lookup(messages, key, defaultText);
	}
	
	/**
	 * Do a safe lookup of a numeric.  It will be converted to the 4 digit hexidecimal for a key.  This is how the numerics are
	 * mapped to messages.  If the key isn't found, return null.
	 * @param numeric the numeric key.
	 * @return the value or null
	 */
	public String lookupNumeric(int numeric) {
		return Resources.lookupNumeric(messages, numeric);
	}
	
	/**
	 * Do a safe lookup of a numeric.  It will be converted to the 4 digit hexidecimal for a key.  This is how the numerics are
	 * mapped to messages.  If the key isn't found, return defaultText.
	 * @param bundle the resource bundle.
	 * @param numeric the numeric key.
	 * @param defaultText the default text
	 * @return the value or the default text.
	 */
	public String lookupNumeric(ResourceBundle bundle, int numeric, String defaultText) {
		return Resources.lookupNumeric(messages, numeric, defaultText);
	}
	
}



