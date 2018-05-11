/**
 * RendSite 2008
 * Copyright Erich P Gatejen 2008, 2009 
 */
package rendsite.renderer;

import java.util.List;

import rendsite.engine.PropertiesGeneralContext;
import rendsite.engine.RenderingContext;
import rendsite.engine.node.Node_Directory;
import rendsite.engine.node.Node_File;
import things.common.ThingsException;
import things.data.ThingsPropertyView;

/**
 * Template version 1 MAIN implementation.
 * @author  erich
 * <i>Version History</i>
 * <pre>
 * 10NOV09 - EPG - First package release.
 * </pre>
 */
public class Renderer_Templatev1_MAIN extends Renderer_Templatev1 {

	// ==================================================================================================================
	// = FIELDS
	
	// Template items to set.
	public static final String TEMPLATE_FIELD__ROWS_DIRECTORIES="rows.directories";
	public static final String TEMPLATE_FIELD__ROWS_FILES="rows.files";
	
	
	// ==================================================================================================================
	// = DATA
	
	
	// ==================================================================================================================
	// = ABSTRACT INTERFACE
	
	/**
	 * Set the row properties for replacement.
	 * @param source the source directory
	 * @param propertyView the replacement properties
	 * @return optional default info about the directory.  It could be null.
	 * @throws Throwable
	 */
	public String setRows(Node_Directory source, ThingsPropertyView propertyView) throws Throwable {
			
		List<Node_Directory> directories =  rcontext.GET_CATALOGABLE_SUBDIRECTORIES(source);
		List<Node_File> files =  rcontext.GET_CATALOGABLE_FILES(source);
		String result = Integer.toString(directories.size()) + " directories and " + files.size() + " files.";
		
		// ------------------------------------------------------------------------------------------------------------
		// Set directory field		
		RowMaker dirRow = new RowMaker();
		for (Node_Directory directory : directories) {
			dirRow.add(directory.getCatalogUrl(source, true), directory.getName(), blankIfNull(directory.type), blankIfNull(directory.description));	
		} 
		propertyView.setProperty(TEMPLATE_FIELD__ROWS_DIRECTORIES, dirRow.toString() );
			
		// ------------------------------------------------------------------------------------------------------------
		// Set file field	
		RowMaker fileRow = new RowMaker();
		for (Node_File thefile : files) {
			fileRow.add(thefile.getURL(true), thefile.getName(), rcontext.RENDER_TYPE(thefile), blankIfNull(thefile.description));	
		} 
		propertyView.setProperty(TEMPLATE_FIELD__ROWS_FILES, fileRow.toString() );	
		
		return result;
	}
		
	// ==================================================================================================================
	// = METHODS

	/**
	 * Construct a renderer.  Let the system do this for you.
	 * @param rcontext The rendering context.  A renderer cannot change context.
	 * @param pcontext general properties context.
	 * @throws ThingsException.  This will usually happen only if you give it a bad context.
	 */
	public Renderer_Templatev1_MAIN(RenderingContext rcontext, PropertiesGeneralContext pcontext) throws ThingsException {
		super(rcontext, pcontext);
	}
	
}



