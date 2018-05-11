/**
 * RendSite 2008
 * Copyright Erich P Gatejen 2008, 2009 
 */
package rendsite.renderer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.util.List;

import rendsite.Codes;
import rendsite.Constants;
import rendsite.RendsiteException;
import rendsite.RendsiteProperties;
import rendsite.engine.PropertiesGeneralContext;
import rendsite.engine.RenderingContext;
import rendsite.engine.RenderingType;
import rendsite.engine.node.Node_Directory;
import rendsite.engine.node.Node_File;
import rendsite.tools.EscapeReader;
import things.common.ThingsException;
import things.common.ThingsNamespace;
import things.data.ThingsPropertyView;
import things.data.impl.ThingsPropertyTreeRAM;

/**
 * Template version 1 MAIN implementation.
 * @author  erich
 * <i>Version History</i>
 * <pre>
 * 10NOV09 - EPG - First package release.
 * </pre>
 */
public class Renderer_Templatev1_SOURCE extends Renderer_Templatev1 {

	// ==================================================================================================================
	// = FIELDS
	
	// Template items to set for the catalog
	public static final String TEMPLATE_FIELD__ROWS_DIRECTORIES="rows.directories";
	public static final String TEMPLATE_FIELD__ROWS_DOCS="rows.documentation";
	public static final String TEMPLATE_FIELD__ROWS_SOURCE="rows.source";
	public static final String TEMPLATE_FIELD__ROWS_OTHER="rows.other";
	
	// Template items to set for the file
	public static final String TEMPLATE_FIELD__PROJECT="project";
	public static final String TEMPLATE_FIELD__INFO="info";
	public static final String TEMPLATE_FIELD__SOURCE_LINES="source.lines";
	
	
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
		// Bucket all the categories.
		RowMaker docRow = new RowMaker();
		RowMaker sourcecRow = new RowMaker();	
		RowMaker otherRow = new RowMaker();
		for (Node_File theFile : files) {
			
			switch (rcontext.RESOLVE_RENDERING_TYPE(theFile)) {
			
			case DOCUMENT:
			case WEB:
				docRow.add(theFile.getURL(true), theFile.getName(), rcontext.RENDER_TYPE(theFile), blankIfNull(theFile.description));
				break;
			
			case SOURCE:
				sourcecRow.add(theFile.getCatalogedFile().getURL(true), theFile.getName(), rcontext.RENDER_TYPE(theFile), blankIfNull(theFile.description));
				break;
			
			case APPLICATION:	
			case IMAGE:
			case ARCHIVE:
			case CONFIGURATION:
			case OTHER:
			default: 
				otherRow.add(theFile.getURL(true), theFile.getName(), rcontext.RENDER_TYPE(theFile), blankIfNull(theFile.description));
				break;
			}
		} 	
		
		// ------------------------------------------------------------------------------------------------------------
		// Set file fields	
		propertyView.setProperty(TEMPLATE_FIELD__ROWS_DOCS, docRow.toString() );	
		propertyView.setProperty(TEMPLATE_FIELD__ROWS_SOURCE, sourcecRow.toString() );
		propertyView.setProperty(TEMPLATE_FIELD__ROWS_OTHER, otherRow.toString() );
		
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
	public Renderer_Templatev1_SOURCE(RenderingContext rcontext, PropertiesGeneralContext pcontext) throws ThingsException {
		super(rcontext, pcontext);
	}
	
	/**
	 * Render a file.  Return the final rendered file, suitable for cataloging.  OVERLOADED from base.  If it comes up as a source file in category, then go ahead and render it.  Otherwise, let
	 * the super handle it.
	 * @param source The file to render.
	 * @throws ThingsException
	 */
	public void render(Node_File source) throws ThingsException {
		String url = Constants.ERRORED;
		try {
			
			url = source.getURL(false);
			
			if (rcontext.RESOLVE_RENDERING_TYPE(source) == RenderingType.SOURCE) {
			
				BufferedWriter writer = null;
				BufferedReader brin = null;
				String neighborName = Constants.UNKNOWN;
				try {

					// ------------------------------------------------------------------------------------------------------------
					// Setup
					ThingsPropertyTreeRAM tree = new ThingsPropertyTreeRAM();
					ThingsPropertyView propertyView = tree.getRoot();
					
					String templateName = pcontext.GET_DEFAULT(RendsiteProperties.PROP_TEMPLATE_PATH_FILE, null);
					if (templateName==null) throw new ThingsException("Missing template path configuration.", Codes.FAULT_CONTEXT_MISSING_REQUIRED_PROP, ThingsNamespace.ATTR_PROPERTY_NAME, RendsiteProperties.PROP_TEMPLATE_PATH_CATALOG);
					String template = rcontext.GET_TEMPLATE(templateName);
					
					logger.debug("Rendering source code file @ " + url);
					
					// This will be rendered file.
					neighborName = getNeighborName(source);
					writer = new BufferedWriter(new OutputStreamWriter(source.openNeighborForWrite(neighborName, true)));
					source.setCatalogNeighbor(neighborName);
					
					// Go ahead and copy the original.  We'll give a link to download.
					source.copy();
					
					// ------------------------------------------------------------------------------------------------------------
					// Set fields
					propertyView.setProperty(TEMPLATE_FIELD__PROJECT, pcontext.GET_DEFAULT(RendsiteProperties.PROP_PROJECT,"") );
					propertyView.setProperty(TEMPLATE_FIELD__FILE, url);
					propertyView.setProperty(TEMPLATE_FIELD__COPYRIGHT, pcontext.GET_DEFAULT(RendsiteProperties.PROP_COPYRIGHT_NOTICE, "") );
					propertyView.setProperty(TEMPLATE_FIELD__RENDSITE_NOTICE, Constants.RENDSITE_NOTICE);
					propertyView.setProperty(TEMPLATE_FIELD__FILE_ORIGINAL, "<a href=\"" + source.getURL(true) + "\">" + source.getName() + "</a>");
					propertyView.setProperty(TEMPLATE_FIELD__ROOT, source.getURLtoRoot(true) );
					
					// ------------------------------------------------------------------------------------------------------------
					// Build the source
					// "<span class=\"ln\">1   </span> // this is a comment"
					StringBuffer outb = new StringBuffer();
					brin = new BufferedReader(new InputStreamReader(source.openForRead()));
					String line = brin.readLine();
					int lineNumber = 1;
					while (line != null) {
						outb.append("<span class=\"ln\">");
						outb.append(size4Formatter(lineNumber));						
						outb.append("</span> ");			
						outb.append(scrub(line));
						outb.append("\r\n");
						lineNumber++;
						line = brin.readLine();
					}
					propertyView.setProperty(TEMPLATE_FIELD__SOURCE_LINES, outb.toString());
					
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
					throw new RendsiteException("Failed to render source neighbor during writing.", Codes.ERROR_RENDERER_FILE_NEIGHBOR__WRITE_ERROR, ioe, Constants.NAME_FILE_SOURCE, url, Constants.NAME_FILE_RENDERED, neighborName);
				} catch (Throwable t) {
					throw new RendsiteException("Failed to render source neighbor.", Codes.ERROR_RENDERER_FILE_NEIGHBOR, t,Constants.NAME_FILE_SOURCE, url, Constants.NAME_FILE_RENDERED, neighborName);
				} finally {
					try {
						writer.close();
					} catch (Throwable tt) {
						// Don't care.
					}
					try {
						brin.close();
					} catch (Throwable tt) {
						// Don't care.
					}					
				}
				logger.debug("File rendered as source code: " + url);
				
			} else {
				super.render(source);
			}
			
		} catch (RendsiteException re) {
			throw re;
		} catch (IOException ioe) {
			throw new RendsiteException("Failed to render file during writing.", Codes.ERROR_RENDERER_FILE__WRITE_ERROR, ioe, Constants.NAME_FILE_SOURCE, url);
		} catch (Throwable t) {
			throw new RendsiteException("Failed to render file.", Codes.ERROR_RENDERER_FILE, t,Constants.NAME_FILE_SOURCE, url);
		}
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
		String neighborName = Constants.UNKNOWN;
		try {
			
			if (rcontext.RESOLVE_RENDERING_TYPE(source) == RenderingType.SOURCE) {
				neighborName = getNeighborName(source);
				source.assertNeighbor(neighborName, true);
				source.setCatalogNeighbor(neighborName);
			}
			
		} catch (Throwable t) {
			// Box the Throwable.  That's all we care about here.
			throw new RendsiteException("Failed to neighbor source check file.  (Only done for SOURCE types.)", Codes.ERROR_RENDERER_CHECK__NEIGHBOR, t, Constants.NAME_FILE_SOURCE, source.getURLRendering(false), Constants.NAME_FILE_RENDERED, neighborName);
		}
	}
	
	// ==================================================================================================================
	// = INTERNAL
	
	/**
	 * Pad the line number.
	 * @param lineNumber the line number.
	 * @return the padded string representing the number.
	 */
	private String size4Formatter(int lineNumber) {
		if (lineNumber < 10) return "   " + lineNumber;
		if (lineNumber < 100) return "  " + lineNumber;
		if (lineNumber < 1000) return " " + lineNumber;
		return Integer.toString(lineNumber);
	}
	
	/**
	 * Slow and lazy scrubbing of the string.
	 * @param theString
	 * @return
	 * @throws Throwable
	 */
	private String scrub(String theString) throws Throwable {
		String result = theString.replace("<", "&lt;");
		result = result.replace(">", "&gt;");
		return result;
	}
	
	/**
	 * Make sure we get the neighbor name in a consistent way.
	 * @param source
	 * @return
	 * @throws Throwable
	 */
	private String getNeighborName(Node_File source) throws Throwable {
		return source.getName() + ".html";
	}
	
	
}



