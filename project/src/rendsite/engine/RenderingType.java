/**
 * RendSite 2008
 * Copyright Erich P Gatejen 2008, 2009 
 */
package rendsite.engine;

/**
 * Rendering type gives hints to the render about the category to help in distinguishing the various types and subtypes.
 * 
 * @author  erich
 * <i>Version History</i>
 * <pre>
 * 10NOV09 - EPG - First package release.
 * </pre>
 */
public enum RenderingType  {
		DOCUMENT,
		WEB,
		SOURCE,
		APPLICATION,
		IMAGE,
		ARCHIVE,
		CONFIGURATION,
		OTHER;
		
		/**
		 * Match it (in a forgiving fashion).
		 * @param token the value to match.  This is not case sensitive!  It also forgives whitespace!
		 * @return the rendering type or null if it doesn't match.
		 */
		public static RenderingType match(String token) {
			RenderingType result = null;
			try {
				result = RenderingType.valueOf(token.toUpperCase());
			} catch (Throwable t) { 
				// Leave it null.
			}
			return result;
		}
}



