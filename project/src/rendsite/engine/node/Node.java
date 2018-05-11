/**
 * RendSite 2008
 * Copyright Erich P Gatejen 2008, 2009 
 */
package rendsite.engine.node;

import things.common.ThingsException;

/**
 * Crawler node.
 * @author  erich
 * <i>Version History</i>
 * <pre>
 * 10NOV09 - EPG - First package release.
 * </pre>
 */
public class Node {

	// ==================================================================================================================
	// = FIELDS
	
	// ==================================================================================================================
	// = DATA
	
	/**
	 * The immutable node type.
	 */
	private NodeType type;
	
	// ==================================================================================================================
	// = METHODS
	
	/**
	 * Create a node.
	 * @param type the node type.  It cannot be null.
	 * @throws Throwable only if a null is passed as the type.
	 */
	public Node(NodeType type) throws Throwable {
		if (type==null) ThingsException.softwareProblem("Node created with null type.");
		this.type = type;
	}

	/**
	 * Get the node type.
	 * @return the type.
	 * @see rendsite.engine.node.NodeType
	 */
	public NodeType getType() {
		return type;
	}
	
	/**
	 * Get the name.  It'll be nothing or overridden by a sub class.
	 * @return the name.  DO NOT USE THIS TO OPEN A FILE!
	 */
	public String getName() {
		return "";
	}
	
	// ==================================================================================================================
	// = INTERNAL
	
	
}



