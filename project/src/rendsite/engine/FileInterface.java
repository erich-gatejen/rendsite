/**
 * RendSite 2008
 * Copyright Erich P Gatejen 2008, 2009 
 */
package rendsite.engine;

import java.io.InputStream;
import java.io.OutputStream;

import things.common.ThingsException;

/**
 * Public interface to interacting with a file from the engine.
 * @author  erich
 * <i>Version History</i>
 * <pre>
 * 10NOV09 - EPG - First package release.
 * </pre>
 */
public interface FileInterface {

	/**
	 * Open for the file for reading.  The stream will not be buffered.  You should close it when you are done.
	 * @return an input stream for reading.
	 * @throws ThingsException
	 */
	public InputStream openForRead() throws ThingsException;
	
	/**
	 * Open for the file in the destination for writing.  The stream will not be buffered.  You should close it when you are done.
	 * It will mark the file as having been updated for other processing.
	 * @return an output stream for writing.
	 * @throws ThingsException
	 */
	public OutputStream openForWrite() throws ThingsException;	
	
	/**
	 * Open a neighbor file in the destination for writing.  The stream will not be buffered.  You should close it when you are done.
	 * It will mark the file as having been updated for other processing.
	 * @param neighbor the neighbor file name
	 * @param isCataloged will this file be cataloged?
	 * @return an output stream for writing.
	 * @throws ThingsException
	 */
	public OutputStream openNeighborForWrite(String neighbor, boolean isCataloged) throws ThingsException;	
	
	/**
	 * Set a neighbor as the catalog file instead of this.  
	 * @param neighbor the neighbor file name.  This name must be unique for the neighborhood (containing directory) and can be used for other methods.  It is case sensitive.
	 * @throws ThingsException this will always happen if the neighbor has not been created through a openNeighborForWrite() call.
	 */
	public void setCatalogNeighbor(String neighbor) throws ThingsException;
	
	/**
	 * Get the last modified date for the source file.  If it is a neighbor, it'll get it for its origin.
	 * @return the last modified date as milliseconds from epoch.
	 * @throws ThingsException
	 */
	public long lastModified() throws ThingsException;
	
	/**
	 * Get the last modified date for the destination file.  If it is a neighbor, it'll get it for its origin.
	 * @return the last modified date as milliseconds from epoch.
	 * @throws ThingsException
	 */
	public long lastModifiedDestination() throws ThingsException;
	
}



