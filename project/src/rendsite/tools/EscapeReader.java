/**
 * RendSite 2008
 * Copyright Erich P Gatejen 2008, 2009 
 */
package rendsite.tools;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;

import things.data.ThingsPropertyViewReader;

/**
 * This is an escaping reader.  When it finds an escape sequence, it'll ask what to put in its place.
 * 
 * @author  erich
 * <i>Version History</i>
 * <pre>
 * 10NOV09 - EPG - First package release.
 * </pre>
 */
public class EscapeReader extends Reader {

	// ===================================================================================================================
	// = FIELDS
	
	// SEQUENCE
	public final static String ESCAPE_OPEN = "[`<[";
	public final static char	   ESCAPE_OPEN_0 = '[';	
	public final static char    ESCAPE_OPEN_1 = '`';	
	public final static char    ESCAPE_OPEN_2 = '<';	
	public final static char    ESCAPE_OPEN_3 = '[';
	public final static String ESCAPE_CLOSE = "]>']";
	public final static char	   ESCAPE_CLOSE_0 = ']';	
	public final static char    ESCAPE_CLOSE_1 = '>';	
	public final static char    ESCAPE_CLOSE_2 = '\'';	
	public final static char    ESCAPE_CLOSE_3 = ']';	
	
	// ===================================================================================================================
	// = DATA
	
	private Reader bis;
	private ThingsPropertyViewReader props;
	private	int rover;	
	private String open;
	private String close;
	private	byte[] buffer = null;

	// Our static caches for the break values.
	private static byte[] CLOSURE_OPEN_1 = { ESCAPE_OPEN_0, ' ' };
	private static byte[] CLOSURE_OPEN_1_2 = { ESCAPE_OPEN_0, ESCAPE_OPEN_1, ' ' };
	private static byte[] CLOSURE_OPEN_1_3 = { ESCAPE_OPEN_0, ESCAPE_OPEN_1, ESCAPE_OPEN_2, ' ' };
	
	// ===================================================================================================================
	// = METHODS
	
	/**
	 * Construct the stream.
	 * @param source the source stream
	 * @param properties the replacement properties.
	 */
	public EscapeReader(Reader source, ThingsPropertyViewReader properties) {
		super();
		bis = source;
		props = properties;
	}
	
	// ===================================================================================================================
	// = ABSTRACT IMPLEMENTATION AND READER OVERLOADS
	
	/**
	 * Read implementation.
	 * @throws IOException
	 */
	public synchronized int read() throws IOException {
		int candidate;
		
		// Empty the buffer from the engine
		if (buffer != null) {
				candidate = buffer[rover];
				if (candidate < 0) {
					// Deal with the unsigned byte.
					candidate = buffer[rover] & 0x80;
					candidate += buffer[rover] & 0x7F;
				}
				rover++;
				if (rover >= buffer.length)
					buffer = null;
		} else {
			
			// read
			candidate = bis.read();
			if (candidate == ESCAPE_OPEN_0) {
				escape();
				candidate = this.read(); 
			}
		}

		return candidate;
	}

	/**
	 * Reads characters into a portion of an array. This method will block until some input is available, an I/O error occurs, or the end of the stream is reached.
	 * @param cbuf Destination buffer
	 * @param off Offset at which to start storing characters
	 * @param len Maximum number of characters to read 
	 * @return The number of characters read, or -1 if the end of the stream has been reached 
	 * @throws IOException  If an I/O error occurs
	 */
	public int read(char[] cbuf, int off, int len) throws IOException {
		
		// GOAT OF AN IMPLEMENTATION RIGHT NOW!!!   I WILL GO BACK AND SPEED IT UP LATER
		
		// Get the first
		int character = this.read();
		if (character < 0) {
		    return character;
		}
		
		cbuf[off] = (char)character;
		return 1;

	}
	
	public boolean markSupported() {
		return false;
	}	 
	
	
	public void close() throws IOException {
		if (bis!=null) bis.close();
	}
	
	// ===================================================================================================================
	// = INTERNAL 
	
	private void escape() throws IOException {
		
		// See if we open
		int candidate = bis.read();
		if (candidate != ESCAPE_OPEN_1) {
			CLOSURE_OPEN_1[1] = (byte)candidate;
			buffer = CLOSURE_OPEN_1;
			rover =  0;
			return;
		}
		candidate = bis.read();
		if (candidate != ESCAPE_OPEN_2) {
			CLOSURE_OPEN_1_2[2] = (byte)candidate;
			buffer = CLOSURE_OPEN_1_2;
			rover =  0;
			return;
		}
		candidate = bis.read();
		if (candidate != ESCAPE_OPEN_3) {
			CLOSURE_OPEN_1_3[3] = (byte)candidate;
			buffer = CLOSURE_OPEN_1_3;
			rover =  0;
			return;
		}		
		
		// We are open!  Build the name until we fully close or run out.
		StringBuffer readBuffer = new StringBuffer();
		candidate = bis.read();
		while (candidate >= 0) {
			
			// Closing?  Brute force this badness.  We'll sweep up the breaks later.
			if (candidate == ESCAPE_CLOSE_0) {
				candidate = bis.read();
				if (candidate < 0) break; // busted anyway.
				if (candidate == ESCAPE_CLOSE_1) {
					candidate = bis.read();
					if (candidate < 0) break;
					if (candidate == ESCAPE_CLOSE_2) {	
						candidate = bis.read();
						if (candidate < 0) break;
						if (candidate == ESCAPE_CLOSE_3) {	
						
								// CLOSURE
								String value = null;	
								if (props != null) {
									try {
										value = props.getProperty(readBuffer.toString());
									} catch (Throwable t) {
										// It'll stay null.
									}
									if (value==null) {
										if (open!=null) {
											value = open + readBuffer.toString() + close;
										} 
									} 
								} else {
									value = open + readBuffer.toString() + close;
								}
								
								// See if we even need a drain buffer.  If not, this whole process will quietly go away.
								if ( (value != null) && ( value.length() > 0)) {
									buffer = value.getBytes();
									rover = 0;
								}		
								return;						
							
						} else {
							readBuffer.append((char)ESCAPE_CLOSE_0);
							readBuffer.append((char)ESCAPE_CLOSE_1);
							readBuffer.append((char)ESCAPE_CLOSE_2);
							readBuffer.append((char)candidate);
						}
					} else {
						readBuffer.append((char)ESCAPE_CLOSE_0);
						readBuffer.append((char)ESCAPE_CLOSE_1);
						readBuffer.append((char)candidate);
					}
				} else {
					readBuffer.append((char)ESCAPE_CLOSE_0);
					readBuffer.append((char)candidate);		
				}
			} else {
				readBuffer.append((char)candidate);				
			}

			// Iterate
			candidate = bis.read();
			
		} // end while
		
		// Do we have a dangling escape?
		if (candidate < 0) throw new IOException("Dangling escape.");
		
	}
	
	// ===================================================================================================================
	// STATIC METHODS
	 
	/**
	 * Process a single string.
	 * @param input a string to process.
	 * @param properties properties for replacement.
	 * @throws Throwable Dangling escapes will NOT cause exceptions.
	 */
	public static String process(String input, ThingsPropertyViewReader properties) throws Throwable {
		
		EscapeReader newReader = new EscapeReader(new StringReader(input), properties);
		StringWriter sw = new StringWriter();
		int character = newReader.read();
		while (character >= 0) {
			sw.write(character);
			character = newReader.read();
		}
		return sw.toString();
	}

	
}
