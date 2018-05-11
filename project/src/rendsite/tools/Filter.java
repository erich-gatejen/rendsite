/**
 * RendSite 2008
 * Copyright Erich P Gatejen 2008, 2009 
 */
package rendsite.tools;

import java.io.Writer;
import java.util.StringTokenizer;

import rendsite.Codes;
import rendsite.Constants;
import rendsite.RendsiteException;
import things.data.processing.PhraseMatcher;

/**
 * Filter system.
 * <p>
 * Matches declared from a series of open brace '{' separated fields.  The first field will define the type with the
 * remaining fields as parameters. After the parameters are depleted, it will assume the next field is new type.
 * It will continue until all fields are depleted.  CR and LF are not significant within this series, but may be to the original source.
 * Each field will be trimmed once parsed out of the delimiter.  In this first version, the open brace '{' is NOT escapable; it will always break fields.
 * <p>
 * <br>TYPES:</b>
 * <pre>
 * URL{Phrase text{www.goatmonkey.com/smort/loobie.html?bork=ZONK
 * </pre>
 * <p>
 * @author  erich
 * <i>Version History</i>
 * <pre>
 * 2APR10 - EPG - Add filters.
 * </pre>
 */
public class Filter extends PhraseMatcher  {

	// =================================================================================
	// == FIELDS
	
	public final static String FIELD_BREAK = "{";
	
	/**
	 * The match type URL.    
	 * <pre>
	 * It expects two parameters:
	 * 1- Phrase text.
	 * 2- URL to apply to the phrase.
	 * </pre>  
	 */
	public final static String TYPE_URL = "URL";

	// =================================================================================
	// == DATA
	
	// =================================================================================
	// == ABSTRACT METHODS
	
	/**
	 * All declarations should be put here, so they are done with any initialization.
	 * @throws Throwable
	 */
	protected void declarations() throws Throwable {
		// We will be bad and do them in the constructor, since they are not hardcoded.
	}
	
	/**
	 * Start on a specific document.  This gives the implementation a chance to initialize.
	 * @param docId The id for the document, data, or whatever.  The implementation may choose to ignore it.
	 * @throws Throwable
	 */
	protected void start(String docId) throws Throwable {
		
	}
	 
	/**
	 * This method will be called when a phrase is matched.  Be sure to write to outs if you want anything preserved!
	 * 
	 * The read() method will supply the read of the header line.  
	 * @param id The defined id.
	 * @param phrase The phrase data as it exactly appears in the stream.
	 * @param len The number of valid characters in the phraseBuffer.  The offset is always 0.
	 * @param out Writer to write the processed data.  If null, then the caller asked not to write anything, but it is up to the implementation.
	 * @throws Throwable
	 */
	protected void match(int	id, char[] phrase, int len, Writer 	out) throws Throwable {
		
	}
	
	// =================================================================================
	// == METHODS 
	
	/**
	 * Constructor.
	 * @param declaration the declarations.
	 * @throws Throwable
	 */
	public Filter(String 	declaration) throws Throwable {
		super();
		
		// Do declarations.
		String type;
		
		StringTokenizer st = new StringTokenizer(declaration, FIELD_BREAK, false);
		while (st.hasMoreTokens()) {
			type = st.nextToken().trim().toUpperCase();
			if (type.equals(TYPE_URL)) {
				
				
				
			} else {
				throw new RendsiteException("Bad filter definition.", Codes.ERROR_CONFIG__FILTER, Constants.NAME_FILTER, declaration, Constants.NAME_FILTER_TYPE, type);
			}
			
		} // end while new definition
	}
}
