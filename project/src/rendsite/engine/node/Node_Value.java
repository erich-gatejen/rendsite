/**
 * RendSite 2008
 * Copyright Erich P Gatejen 2008, 2009 
 */
package rendsite.engine.node;

import things.common.ThingsException;
import things.data.NV;

/**
 * Crawler node for a marker.
 * @author  erich
 * <i>Version History</i>
 * <pre>
 * 10NOV09 - EPG - First package release.
 * </pre>
 */
public class Node_Value extends Node {

	// ==================================================================================================================
	// = FIELDS
	
	// ==================================================================================================================
	// = DATA
	
	/**
	 * Name and the value.  If the value isNULL, then it does not represent a previous value in the store; just remove it.
	 */
	public NV nv;
	
	// ==================================================================================================================
	// = METHODS
	
	/**
	 * Create a node.
	 */
	public Node_Value() throws Throwable {
		super(NodeType.VALUE);
	}

	/**
	 * Create a node.
	 * @param name The name
	 * @param value the value
	 */
	public Node_Value(String name, String value) throws Throwable {
		super(NodeType.VALUE);
		
		if (name==null) ThingsException.softwareProblem("Cannot create Node_Value with a null 'name'.");
		nv = new NV(name, value);
	}
	
	/**
	 * Create a node.
	 * @param nv the name/value.
	 */
	public Node_Value(NV nv) throws Throwable {
		super(NodeType.VALUE);
		
		if (nv==null) ThingsException.softwareProblem("Cannot create Node_Value with a null 'nv'.");
		if (nv.getName()==null) ThingsException.softwareProblem("Cannot create Node_Value with an nv that has a null name.");
		this.nv = nv;
	}
	
	// ==================================================================================================================
	// = INTERNAL
	
	
}



