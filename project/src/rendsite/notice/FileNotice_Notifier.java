/**
 * RendSite 2008
 * Copyright Erich P Gatejen 2008, 2009 
 */
package rendsite.notice;

import rendsite.Codes;
import rendsite.engine.node.Node_File;
import things.common.ThingsException;
import things.common.ThingsNamespace;
import things.common.WhoAmI;
import things.data.Entity;
import things.data.Receipt;
import things.thinger.io.conduits.ConduitController;
import things.thinger.io.conduits.ConduitID;
import things.thinger.io.conduits.Injector;
import things.thinger.io.conduits.Conduit.InjectorType;

/**
 * Send file notices.
 * @author  erich
 * <i>Version History</i>
 * <pre>
 * 10NOV09 - EPG - First package release.
 * </pre>
 */
public class FileNotice_Notifier extends FileNotice {
	
	// =============================================================================
	// DATA
	
	// Injector and conduit.
	private Injector injector;
	
	// =============================================================================
	// CHANNELS
	
	/**
	 * All file changes should be submitted to this conduit.  
	 */
	public final static ConduitID fileChangeConduit = new ConduitID("file.change", "FC");
	
	// =============================================================================
	// METHODS
	
	/**
	 * Set up a file notifier.  It needs access to the conduit controller and a given ID.
	 * @param controller
	 * @param id
	 * @throws Throwable
	 */
	public FileNotice_Notifier(ConduitController controller, WhoAmI id) throws Throwable {
		super(controller, id);
		try {
			injector = conduit.getInjector(InjectorType.REQUIRE_ALL_DRAIN);
		} catch (Throwable t) {
			ThingsException.softwareProblem("Failed to tune file notifier.  This is a bug.", t, ThingsNamespace.ATTR_ID, id.toString());
		}
	}
	
	/**
	 * Dispose the notifier or sink.  Generally, you'll have to do this yourself instead of relying on finalization, since
	 * the conduits will hold references to these objects.  The implementation should deregister from the conduit controller.
	 * @throws Throwable for any problem.  The should all be FAULTS.
	 */
	public synchronized void  dispose() throws Throwable {
		try {
			if (injector != null) {
				// it is possible the finalizer did this too, since the conduits have a reference to the injector, but not this object.
				conduit.disposeInjector(injector);
				injector = null;
			}
		} catch (Throwable t) {
			throw new ThingsException("Could not deregister for notifications.", Codes.FAULT_NOTIFICATION_DISPOSE, t);
		} 
	}
	
	/**
	 * Notify a file change.
	 * @param theFile the file node
	 * @param path the full path at the time of the change.
	 * @throws Throwable
	 */
	public void notifyChange(Node_File	theFile, String path) throws Throwable {
		postFile(theFile, FILE_CHANGE_CHANGED, path);
	}
	
	/**
	 * Notify a file change that failed.
	 * @param theFile the file node
	 * @param path the full path at the time of the change.
	 * @throws Throwable
	 */
	public void notifyChangeFailed(Node_File	theFile, String path) throws Throwable {
		postFile(theFile, FILE_CHANGE_CHANGE_FAILED, path);
	}
	
	/**
	 * Notify a file change for a file that was deleted.
	 * @param theFile the file node
	 * @param path the full path at the time of the change.
	 * @throws Throwable
	 */
	public void notifyChangeDeleted(Node_File	theFile, String path) throws Throwable {
		postFile(theFile, FILE_CHANGE_CHANGE_DELETE, path);
	}
	
	// =============================================================================
	// INTERNAL
	
	/**
	 * Post the file to the injector.
	 * @param fnode
	 * @param numeric
	 * @param path
	 * @throws Throwable
	 */
	private void postFile(Node_File fnode, int numeric, String path) throws Throwable {
		Receipt result = null;
		try {
			Entity<Node_File>	e = new Entity<Node_File>(numeric, fnode, ATTR_FILE_PATH, path);
			result = injector.post(e).firstOk();
		} catch (Throwable t) {
			ThingsException.softwareProblem("Failed to post file notification due to internal problem.  This is a bug.", t);
		}
		if (result==null) ThingsException.softwareProblem("Failed to post file notification due to internal problem.  Did not get a good Receipt.  This is a bug.");
	}

	/**
	 * Finalizer.
	 * @throws Throwable for bugs.
	 */
	protected  void 	finalize() throws Throwable {
		dispose();
	}

	
}



