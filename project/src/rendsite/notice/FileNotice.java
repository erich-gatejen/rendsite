/**
 * RendSite 2008
 * Copyright Erich P Gatejen 2008, 2009 
 */
package rendsite.notice;

import things.common.ThingsException;
import things.common.ThingsNamespace;
import things.common.WhoAmI;
import things.thinger.io.conduits.Conduit;
import things.thinger.io.conduits.ConduitController;
import things.thinger.io.conduits.ConduitID;

/**
 * Send file notices.
 * @author  erich
 * <i>Version History</i>
 * <pre>
 * 10NOV09 - EPG - First package release.
 * </pre>
 */
public abstract class FileNotice {
	
	// =============================================================================
	// NUMERICS
	
	public final static int FILE_CHANGE_CHANGED = 1;
	public final static int FILE_CHANGE_CHANGE_FAILED = 2;
	public final static int FILE_CHANGE_CHANGE_DELETE = 3;
	
	// =============================================================================
	// ATTRIBUTES
	
	public final static String ATTR_FILE_PATH = "file.path";
	
	// =============================================================================
	// CHANNELS
	
	/**
	 * All file changes should be submitted to this conduit.  
	 */
	public final static ConduitID fileChangeConduit = new ConduitID("file.change", "FC");
	
	
	// =============================================================================
	// DATA
	
	protected Conduit conduit;
	
	// =============================================================================
	// ABSTRACT
	
	/**
	 * Dispose the notifier or sink.  Generally, you'll have to do this yourself instead of relying on finalization, since
	 * the conduits will hold references to these objects.  The implementation should deregister from the conduit controller.
	 * @throws Throwable for any problem.  The should all be FAULTS.
	 */
	public abstract void dispose() throws Throwable;
	
	// =============================================================================
	// METHODS
	
	/**
	 * Set up a file notice.  It needs access to the conduit controller and a given ID.
	 * @param controller
	 * @param id
	 * @throws Throwable
	 */
	public FileNotice(ConduitController controller, WhoAmI id) throws Throwable {
		try {
			conduit = controller.tune(fileChangeConduit, id);
		} catch (Throwable t) {
			ThingsException.softwareProblem("Failed to tune file notice.  This is a bug.", t, ThingsNamespace.ATTR_ID, id.toString());
		}
	}
	
	
}



