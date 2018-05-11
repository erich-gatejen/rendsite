/**
 * RendSite 2008
 * Copyright Erich P Gatejen 2008, 2009 
 */
package rendsite.notice;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;

import rendsite.Codes;
import rendsite.RendsiteException;
import rendsite.engine.node.Node_File;
import things.common.ThingsException;
import things.common.ThingsNamespace;
import things.common.WhoAmI;
import things.data.Data;
import things.data.Entity;
import things.data.Receipt;
import things.thinger.SystemException;
import things.thinger.io.conduits.ConduitController;
import things.thinger.io.conduits.ConduitID;
import things.thinger.io.conduits.PushDrain;

/**
 * File notice sink for a change file.
 * @author  erich
 * <i>Version History</i>
 * <pre>
 * 10NOV09 - EPG - First package release.
 * </pre>
 */
public class FileNotice_ChangeFile extends FileNotice implements PushDrain {
	
	// =============================================================================
	// DATA
	private PrintWriter pw;
	private ConduitID drainId;
	
	// =============================================================================
	// METHODS
	
	/**
	 * Set up a file notifier.  It needs access to the conduit controller and a given ID.
	 * @param controller the conduit controller
	 * @param id the given ID.
	 * @param theFile where to write the changes.
	 * @throws Throwable
	 */
	public FileNotice_ChangeFile(ConduitController controller, WhoAmI id, File theFile) throws Throwable {
		super(controller, id);
		
		// Open the file.
		try {
			pw = new PrintWriter(new BufferedOutputStream(new FileOutputStream(theFile)), true); 	
		} catch (Throwable t) {
			throw new RendsiteException("Failed to open change file.", Codes.PANIC_CHANGE_FILE__CANNOT_WRITE, t, ThingsNamespace.ATTR_PLATFORM_FILE_PATH, theFile.getAbsolutePath());
		}
		
		// Attach this as a drain.
		try {
			conduit.registerPushDrain(this);
		} catch (Throwable t) {
			ThingsException.softwareProblem("Failed to register FileNotice_ChangeFile as a push drain.  This is a bug.", t, ThingsNamespace.ATTR_ID, id.toString());
		}
	}
	
	/**
	 * Dispose the notifier or sink.  Generally, you'll have to do this yourself instead of relying on finalization, since
	 * the conduits will hold references to these objects.  The implementation should deregister from the conduit controller.
	 * @throws Throwable for any problem.  The should all be FAULTS.
	 */
	public synchronized void dispose() throws Throwable {
		try {
			conduit.deRegisterPushDrain(this);
		} catch (Throwable t) {
			throw new ThingsException("Could not deregister from notifications.", Codes.FAULT_NOTIFICATION_DISPOSE, t);
		} finally {
			try {
				// since we won't be getting any more notifications, we may as well close it.  There might be a small race-condition between
				// this and the postListener, but I'll deal with it later if it actualyl happens.
				pw.close();
			} catch (Throwable t) {
				// Don't care.
			}
		}
	}
	
	// =============================================================================
	// PUSH DRAIN INTERFACE
	
    /**
     * Initialize the PushDrain.  This will be called by it's controller.  An subsequent calls may result in a PANIC SystemException.  
     * Don't do it!
     * @param yourId The ConduitID for this PushDrain.
     * @see things.thinger.io.conduits.ConduitID
     * @throws things.thinger.SystemException
     */   
    public void init(ConduitID	yourId) throws SystemException {
    	drainId = yourId;
    }
	
	/**
	 * Listen for a post.  Consumers should implement this.
	 * @param n The data to post.
	 * @return a receipt
	 * @throws things.thinger.SystemException
	 */
	@SuppressWarnings("unchecked")
	public Receipt postListener(Data		n) throws SystemException {
		Receipt result;
		try {
			Entity<Node_File> node = (Entity<Node_File>)n;
			String path = node.getAttributes().getAttribute(ATTR_FILE_PATH).getValue();
			switch(node.getNumeric()) {
			case FILE_CHANGE_CHANGED:
				pw.println("Changed:\t\"" + path + "\"");
				break;
			case FILE_CHANGE_CHANGE_FAILED:
				pw.println("Failed:\t\"" + path + "\"");
				break;
			case FILE_CHANGE_CHANGE_DELETE:
				pw.println("Deleted:\t\"" + path + "\"");
				break;
			default:
				throw new Exception("Unknown numeric.");
			}
			result = new Receipt(drainId, Receipt.Type.ETERNAL_HAPPINESS);
		} catch (Throwable t) {
			throw new SystemException("Failed to record file change.  This is a bug.", Codes.PANIC_CHANGE_FILE, t);
		}
		return result;
	}
	
	// =============================================================================
	// INTERNAL

	/**
	 * Finalizer.  Note that this will not happen 
	 * @throws Throwable for bugs.
	 */
	protected  void 	finalize() throws Throwable {
		dispose();
	}
   
	
	
	
}



