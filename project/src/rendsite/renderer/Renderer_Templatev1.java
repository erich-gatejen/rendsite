/**
 * RendSite 2008
 * Copyright Erich P Gatejen 2008, 2009 
 */
package rendsite.renderer;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringReader;

import rendsite.Codes;
import rendsite.Constants;
import rendsite.RendsiteException;
import rendsite.RendsiteProperties;
import rendsite.engine.PropertiesGeneralContext;
import rendsite.engine.RenderingContext;
import rendsite.engine.node.Node_Directory;
import rendsite.engine.node.Node_File;
import rendsite.tools.EscapeReader;
import things.common.ThingsException;
import things.common.ThingsNamespace;
import things.data.ThingsPropertyView;
import things.data.impl.ThingsPropertyTreeRAM;

/**
 * The base for template version 1 renderers.
 * <p>
 * How to make rows:<br>
 * <pre>
 *    <tr class="evenrow"> 
        <td>item.1</td>
        <td>type.1</td>
        <td>desc.1</td>
      </tr>
      <tr class="oddrow">
        <td>item.2</td>
        <td>type.2</td>
        <td>desc.2</td>
      </tr>
 * </pre>
 * @author  erich
 * <i>Version History</i>
 * <pre>
 * 10NOV09 - EPG - First package release.
 * </pre>
 */
public abstract class Renderer_Templatev1 extends Renderer {

	// ==================================================================================================================
	// = FIELDS
	
	// Template items to set.
	public static final String TEMPLATE_FIELD__PROJECT="project";
	public static final String TEMPLATE_FIELD__INFO="info";
	public static final String TEMPLATE_FIELD__DIRECTORY="directory";
	public static final String TEMPLATE_FIELD__FILE="file";
	public static final String TEMPLATE_FIELD__FILE_ORIGINAL="file.original";
	public static final String TEMPLATE_FIELD__ROOT="root";
	public static final String TEMPLATE_FIELD__COPYRIGHT="copyright";
	public static final String TEMPLATE_FIELD__RENDSITE_NOTICE="rendsite.notice";
	
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
	public abstract String setRows(Node_Directory source, ThingsPropertyView propertyView) throws Throwable;

	
	// ==================================================================================================================
	// = IMPLEMENT ABSTRACT RENDERER
	
	/**
	 * Render a file.
	 * @param source The file to render.   
	 * @throws ThingsException
	 */
	public void render(Node_File source) throws ThingsException {
		source.copy();
	}
	
	/**
	 * Check a file.  The file exists and the configuration has not changed, so you don't need to render it.  However, you may
	 * choose to render it anyway.
	 * <p>
	 * IMPORTANT!  IMPORTANT!  Neighbors are determined during rendering, not spidering.  If the neighbor is not opened for write, but an old version exists, it will
	 * be treated as a dead file and removed during cleanup.  You can make
	 * @param source The file to render.   
	 * @throws ThingsException
	 */
	public void check(Node_File source) throws ThingsException {
		//NOP
	}
	
	/**
	 * Render a directory context.  There may not be much to do here.  Any catalog page should be rendered by renderFrame() 
	 * so that it is done after all other processing is complete.
	 * @param source the source directory
	 * @throws ThingsException
	 */
	public void render(Node_Directory source) throws ThingsException {
		//NOP
	}	
		
	/**
	 * Render the frame itself.  If you make a catalog, it is important you set the catalogFileName in the source directory
	 * if you want the parent catalog to point at it.  
	 * @param source the source directory
	 * @throws ThingsException
	 */
	public void renderFrame(Node_Directory source) throws ThingsException {
		String catalogFile = Constants.ERRORED;
		BufferedWriter writer = null;
		
		try {
			
			// ------------------------------------------------------------------------------------------------------------
			// Setup
			ThingsPropertyTreeRAM tree = new ThingsPropertyTreeRAM();
			ThingsPropertyView propertyView = tree.getRoot();
			
			String templateName = pcontext.GET_DEFAULT(RendsiteProperties.PROP_TEMPLATE_PATH_CATALOG, null);
			if (templateName==null) throw new RendsiteException("Missing template path configuration.", Codes.FAULT_CONTEXT_MISSING_REQUIRED_PROP__TEMPLATE_PATH, ThingsNamespace.ATTR_PROPERTY_NAME, RendsiteProperties.PROP_TEMPLATE_PATH_CATALOG);
			String template = rcontext.GET_TEMPLATE(templateName);
			
			catalogFile = source.getCatalogUrlAbsolute(false);
			logger.debug("Building catalog @ " + catalogFile);
			writer = new BufferedWriter(new OutputStreamWriter(source.openCatalogForWrite()));
			
			String url = source.getURLAbsolute(false);
			if (url.length()==0) url = "/";
			
			// ------------------------------------------------------------------------------------------------------------
			// Set fields
			propertyView.setProperty(TEMPLATE_FIELD__PROJECT, pcontext.GET_DEFAULT(RendsiteProperties.PROP_PROJECT,"") );
			propertyView.setProperty(TEMPLATE_FIELD__DIRECTORY, url);
			propertyView.setProperty(TEMPLATE_FIELD__COPYRIGHT, pcontext.GET_DEFAULT(RendsiteProperties.PROP_COPYRIGHT_NOTICE, "") );
			propertyView.setProperty(TEMPLATE_FIELD__RENDSITE_NOTICE, Constants.RENDSITE_NOTICE);
			propertyView.setProperty(TEMPLATE_FIELD__ROOT, source.getURLtoRoot(true) );

			// ------------------------------------------------------------------------------------------------------------
			// Set directory field		
			String info = setRows(source, propertyView);
			if (info==null) info = "";
			
			// ------------------------------------------------------------------------------------------------------------
			// Set directory field	
			propertyView.setProperty(TEMPLATE_FIELD__INFO, pcontext.GET_LOCAL_DEFAULT(RendsiteProperties.PROP_LOCAL_DESCRIPTION, info) );
			
			// ------------------------------------------------------------------------------------------------------------
			// Merge and write template
			// Crappy slow way for now.
			EscapeReader ereader = new EscapeReader(new StringReader(template), propertyView);
			int character = ereader.read();
			while(character >= 0) {
				writer.write(character);
				character = ereader.read();
			}
			
		} catch (IOException ioe) {
			throw new RendsiteException("Failed to make Templatev1 catalog file.", Codes.ERROR_RENDERER_CATALOG__WRITE_ERROR, ioe, Constants.NAME_FILE_DESTINATION, catalogFile);
		} catch (Throwable t) {
			throw new RendsiteException("Failed to make Templatev1 catalog file.", Codes.ERROR_RENDERER_CATALOG, t, Constants.NAME_FILE_DESTINATION, catalogFile);

		} finally {
			try {
				writer.close();
			} catch (Throwable tt) {
				// Don't care.
			}
		}
		
	}
		
	// ==================================================================================================================
	// = METHODS

	/**
	 * Construct a renderer.  Let the system do this for you.
	 * @param rcontext The rendering context.  A renderer cannot change context.
	 * @param pcontext general properties context.
	 * @throws ThingsException.  This will usually happen only if you give it a bad context.
	 */
	public Renderer_Templatev1(RenderingContext rcontext, PropertiesGeneralContext pcontext) throws ThingsException {
		super(rcontext, pcontext);
	}
	
	// ==================================================================================================================
	// = SUBCLASS

	/**
	 * The subclasses should use these to make rows.
	 */
	protected class RowMaker {
		
		private int evenodd = 0;
		private StringBuffer buffer = new StringBuffer();
		
		/**
		 * Add a row.
		 * @param url
		 * @param name
		 * @param type
		 * @param description
		 * @throws Throwable
		 */
		public void add(String url, String name, String type, String description) throws Throwable {
		
			if ( (evenodd % 2) >0) {
				buffer.append("<tr class=\"oddrow\">");	
			} else {
				buffer.append("<tr class=\"evenrow\">");
			}
			buffer.append("<td align=\"left\"><a href=\"" + url + "\">" + name + "</a></td><td align=\"left\">" + type + "</td><td align=\"left\">" + description + "</td>");
			buffer.append("</tr>\r\n");
			evenodd++;
		
		}
		
		/**
		 * Get the data.  If nothing was added, it'll return a single blank row.
		 * @return all the rows as a string.
		 */
		public String toString() {
			String result = buffer.toString();
			if (result.trim().length()<1) result = "<tr class=\"evenrow\"><td></td><td></td><td></td></tr>\r\n";
			return result;
		}
		
	} // end RowMaker

}



