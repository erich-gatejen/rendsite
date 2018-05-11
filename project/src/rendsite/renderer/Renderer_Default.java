/**
 * RendSite 2008
 * Copyright Erich P Gatejen 2008, 2009 
 */
package rendsite.renderer;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import rendsite.Codes;
import rendsite.Constants;
import rendsite.RendsiteException;
import rendsite.RendsiteProperties;
import rendsite.engine.PropertiesGeneralContext;
import rendsite.engine.RenderingContext;
import rendsite.engine.node.Node_Directory;
import rendsite.engine.node.Node_File;
import things.common.ThingsException;

/**
 * Base renderer.
 * @author  erich
 * <i>Version History</i>
 * <pre>
 * 10NOV09 - EPG - First package release.
 * </pre>
 */
public class Renderer_Default extends Renderer {

	// ==================================================================================================================
	// = FIELDS
	
	// ==================================================================================================================
	// = DATA
	
	private final static String PAGE_PART1 = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\"><html xmlns=\"http://www.w3.org/1999/xhtml\"><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=iso-8859-1\" /><title>";
	private final static String PAGE_PART2 = "</title><style type=\"text/css\"><!--.style5 {font-size: small;font-weight: bold;} .style6 {font-size: large} .style7 {font-size: x-small;} div {display: block;} .style8 {width: 1000px;text-align: left;}.style10 {width: 1000px; text-align: left; font-size: x-large; }--></style></head><body><span class=\"style10\">Directory Listing for ";
	private final static String PAGE_PART2A = "</span><br /><div class=\"style8\">";
	private final static String PAGE_PART3 = "</div><br /><div><div><div class=\"style6\">DIRECTORIES</div><div><table width=\"1000\" border=\"0\"><tr  bgcolor=\"#D1E9E9\"><td width=\"28%\"><span class=\"style5\">NAME</span></td><td width=\"12%\"><span class=\"style5\">TYPE</span></td><td width=\"60%\"><span class=\"style5\">DESCRIPTION</span></td></tr>";
	private final static String PAGE_PART4 = "</table></div></div><p><div><div class=\"style6\">FILES</div><div><table width=\"1000\" border=\"0\"><tr  bgcolor=\"#D1E9E9\"><td width=\"28%\"><span class=\"style5\">NAME</span></td><td width=\"12%\"><span class=\"style5\">TYPE</span></td><td width=\"60%\"><span class=\"style5\">DESCRIPTION</span></td></tr>";
	private final static String PAGE_PART5 = "</table></div></div><br><table width=\"1000\"><tr><td align=\"left\" class=\"style7\">";
	private final static String PAGE_PART6 = "</td><td align=\"right\" class=\"style7\">";
	private final static String PAGE_PART7 = "</td></tr></table></body></html>";
	
	// ==================================================================================================================
	// = ABSTRACT INTERFACE
	
	/**
	 * Render a file.  Return the final rendered file, suitable for cataloging.
	 * @param source
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
		PrintWriter pow = null;
		
		try {

			// Set up Catalog
			catalogFile = source.getCatalogUrlAbsolute(false);
			logger.debug("Building catalog @ " + catalogFile);
			
			pow = new PrintWriter(new BufferedOutputStream(source.openCatalogForWrite()), true);
			String url = source.getURLAbsolute(false);
			if (url.length()==0) url = "/";
			
			// Render
			pow.println(PAGE_PART1);
			pow.print(pcontext.GET_DEFAULT(RendsiteProperties.PROP_PROJECT,"") + " " + url);
			pow.print(PAGE_PART2);		
			pow.print(url);
			pow.print(PAGE_PART2A);
			pow.print(pcontext.GET_LOCAL_DEFAULT(RendsiteProperties.PROP_LOCAL_DESCRIPTION, ""));
			pow.println(PAGE_PART3);			
			
			List<Node_Directory> directories =  rcontext.GET_CATALOGABLE_SUBDIRECTORIES(source);
			if (directories.size()>0) {
				for (Node_Directory directory : directories) {
					pow.println("<tr bgcolor=\"#F1F1F1\">");
					pow.println("<td align=\"left\"><a href=\"" +  source.getCatalogUrl(source, true) + "\">" + directory.getName() + "</a></td><td align=\"left\">" + blankIfNull(directory.type) + "</td><td align=\"left\">" + blankIfNull(directory.description) + "</td>");
					pow.println("</tr>");
				}	
			} else {
				pow.println("<tr bgcolor=\"#F1F1F1\"><td></td><td></td><td></td></tr>");
			}
	
			pow.println(PAGE_PART4);		

			List<Node_File> files =  rcontext.GET_CATALOGABLE_FILES(source);
			if (files.size()>0) {
				for (Node_File thefile : files) {
					pow.println("<tr bgcolor=\"#F1F1F1\">");
					pow.println("<td align=\"left\"><a href=\"" + thefile.getURL(true) + "\">" + thefile.getName() + "</a></td><td align=\"left\">" + rcontext.RENDER_TYPE(thefile) + "</td><td align=\"left\">" + blankIfNull(thefile.description) + "</td>");
					pow.println("</tr>");
				}	
			} else {
				pow.println("<tr bgcolor=\"#F1F1F1\"><td></td><td></td><td></td></tr>");
			}

			pow.println();
			pow.print(PAGE_PART5);	
			pow.print(pcontext.GET_DEFAULT(RendsiteProperties.PROP_COPYRIGHT_NOTICE, ""));
			pow.print(PAGE_PART6);	
			pow.print(Constants.RENDSITE_NOTICE);
			pow.print(PAGE_PART7);

		} catch (IOException ioe) {
			throw new RendsiteException("Failed to make DEFAULT catalog file.", Codes.ERROR_RENDERER_CATALOG__WRITE_ERROR, ioe, Constants.NAME_FILE_DESTINATION, catalogFile);
		} catch (Throwable t) {
			throw new RendsiteException("Failed to make DEFAULT catalog file.", Codes.ERROR_RENDERER_CATALOG, t, Constants.NAME_FILE_DESTINATION, catalogFile);
		} finally {
			try {
				pow.close();
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
	public Renderer_Default(RenderingContext rcontext, PropertiesGeneralContext pcontext) throws ThingsException {
		super(rcontext, pcontext);
	}

}



