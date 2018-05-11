/**
 * RendSite 2008
 * Copyright Erich P Gatejen 2008, 2009 
 */
package rendsite.engine.node;

import things.data.ThingsPropertyTree;

/**
 * A scope.
 * @author  erich
 * <i>Version History</i>
 * <pre>
 * 10NOV09 - EPG - First package release.
 * </pre>
 */
public class Node_Scope extends Node {

	// ==================================================================================================================
	// = FIELDS
	
	// ==================================================================================================================
	// = DATA
	
	/**
	 * The previous local values tree.
	 */
	public ThingsPropertyTree local;

	/**
	 * The previous directory scope.
	 */
	public Node_Directory directory;
	
	// ==================================================================================================================
	// = METHODS
	
	/**
	 * Create a node.
	 * @throws Throwable 
	 */
	public Node_Scope(ThingsPropertyTree local, Node_Directory directory) throws Throwable {
		super(NodeType.SCOPE);
		this.local = local;
		this.directory = directory;
	}

	// ==================================================================================================================
	// = INTERNAL
	
	
}



