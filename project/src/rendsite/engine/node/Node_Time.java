/**
 * RendSite 2008
 * Copyright Erich P Gatejen 2008, 2009 
 */
package rendsite.engine.node;

/**
 * Crawler node for a marker.
 * @author  erich
 * <i>Version History</i>
 * <pre>
 * 10NOV09 - EPG - First package release.
 * </pre>
 */
public class Node_Time extends Node {

	// ==================================================================================================================
	// = FIELDS
	
	// ==================================================================================================================
	// = DATA
	
	/**
	 * Date/Time in milliseconds from epoch.
	 */
	private long datetime;

	// ==================================================================================================================
	// = METHODS
	
	/**
	 * Create a node.
	 * @param datetime Date and time in milliseconds from epoch.
	 * @throws Throwable this will never happen.
	 */
	public Node_Time(long datetime) throws Throwable {
		super(NodeType.TIME);
		this.datetime = datetime;
	}
	
	/**
	 * Get the date/time.
	 * @return Date and time in milliseconds from epoch.
	 */
	public long get() {
		return datetime;
	}

	// ==================================================================================================================
	// = INTERNAL
	
	
}



