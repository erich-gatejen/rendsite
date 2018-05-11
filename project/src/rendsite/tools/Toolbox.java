/*
 * General constants.
 * Erich - Sep 2007
 */
package rendsite.tools;

/**
 * General tools.
 * @author  erich
 * <i>Version History</i>
 * <pre>
 * 10NOV09 - EPG - First package release.
 * </pre>
 */
public class Toolbox  {
	
	/**
	 * Return whichever is not null nor blank.  If neither is, it'll return the first string.
	 * @param v1 String 1
	 * @param v2 String 2
	 * @return the best string.  It'll return blank instead of a null;
	 */
	public static String pickNotNullNorBlank(String v1, String v2) {
		if (v1==null) {
			if (v2==null) return "";
			return v2;
		}
		if (v1.length()<1) {
			if (v2==null) return v1;
			if (v2.length()>0) return v2;
		}
		return v1;
	}

	/**
	 * Get file extension.
	 * @param name 
	 * @return the extension, if present, or null.
	 */
	public static String getExtension(String name) {
		String result = null;
		try {
			result = name.substring(name.lastIndexOf('.')+1);
		} catch (Throwable t) {
			// Let it return null
		}
		return result;
	}
	
}



