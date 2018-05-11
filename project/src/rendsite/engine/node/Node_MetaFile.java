/**
 * RendSite 2008
 * Copyright Erich P Gatejen 2008, 2009 
 */
package rendsite.engine.node;

import java.io.File;

import things.common.ThingsException;

/**
 * Crawler node for a File.
 * @author  erich
 * <i>Version History</i>
 * <pre>
 * 10NOV09 - EPG - First package release.
 * </pre>
 */
public class Node_MetaFile extends Node {

	// ==================================================================================================================
	// = FIELDS
	
	// ==================================================================================================================
	// = DATA
	
	/**
	 * File.
	 */
	public File file;

	// ==================================================================================================================
	// = METHODS

	/**
	 * Create a node.
	 * @param file the file.
	 */
	public Node_MetaFile(File file) throws Throwable {
		super(NodeType.METAFILE);
		if (file==null) ThingsException.softwareProblem("You cannot have a null file.");
		this.file = file;
	}
	
	/**
	 * Get path.
	 * @return the path or an empty string if the file is not set.
	 */
	public String getPath() {
		if (file==null) return "";
		return file.getAbsolutePath();
	}

	/**
	 * Get name.
	 * @return the name or an empty string if the file is not set.
	 */
	public String getName() {
		if (file==null) return "";
		return file.getName();
	}
	
	// ==================================================================================================================
	// = INTERNAL
	
	
}



