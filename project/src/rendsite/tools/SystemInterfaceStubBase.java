/**
 * RendSite 2008
 * Copyright Erich P Gatejen 2008, 2009 
 */
package rendsite.tools;

import things.common.ThingsException;
import things.common.WhoAmI;
import things.data.ThingsPropertyView;
import things.thing.MODULE;
import things.thing.THING;
import things.thinger.ExpressionInterface;
import things.thinger.SystemException;
import things.thinger.SystemSuperInterface;
import things.thinger.io.FileSystemLocator;
import things.thinger.kernel.Clearance;
import things.thinger.kernel.PCB;
import things.thinger.kernel.ProcessInterface;
import things.thinger.kernel.ThingsProcess;
import things.thinger.kernel.ThingsState;
import things.universe.Universe;

/**
 * Stub out the system interface (like the test.things does).  We'll put the NOP methods here and 
 * implemented methods in subclasses.  
 * @author  erich
 * <i>Version History</i>
 * <pre>
 * 10NOV09 - EPG - First package release.
 * </pre>
 */
public abstract class SystemInterfaceStubBase implements SystemSuperInterface {

	
	// =================================================================================
	// == SUPER SYSTEM INTERFACE
	
	/**
	 * Get system global property view.  This is primarily for system work.
	 * @return a property view
	 * @throws things.thinger.SystemException
	 */
	public ThingsPropertyView getGlobalProperties() throws SystemException {
		return null;
	}
	
	/**
	 * Get user global property view.  These properties are copied into a user process as local properties when the process is created.  It's a
	 * snapshot, updated to the original global properties and the new local properties will not affect each other.
	 * @return a property view
	 * @throws things.thinger.SystemException
	 */
	public ThingsPropertyView getUserGlobalProperties() throws SystemException {
		return null;
	}
	
	/**
	 * Get the configuration properties that are writable.  Anything that has access to the SSI can touch these.
	 * @return a property view
	 * @throws things.thinger.SystemException
	 */
	public ThingsPropertyView getConfigPropertiesWritable() throws SystemException {
		return null;
	}
	
	/**
	 * Get local property view for the given  id.  
	 * @param id String id of the process.
	 * @return a property view or null if the id doesn't identify any known process.
	 * @throws things.thinger.SystemException
	 */
	public ThingsPropertyView getLocalProperties(String id) throws SystemException {
		return null;
	}
	
	/**
	 * Typically, this is a last ditch way for a process or
	 * module to pass info to the kernel when something very
	 * bad is happening.  There is no feedback loop.
	 * @param te a Things exception
	 * @throws things.thinger.SystemException
	 * @see things.common.WhoAmI
	 */
	public void flingException(ThingsException te) {
		// NOP
	}
	
	/**
	 * Typically, this is how a process will tell the kernel it
	 * is dying, so that the kernel can clear resources.  This really should be the LAST thing a process
	 * does before exiting run().  If can be used instead of flingException.
	 * @param te a Things exception that indicates the reason for the death.  It may be null if it was normal termination.
	 * @throws things.thinger.SystemException
	 * @see things.common.WhoAmI
	 */
	public void deathNotice(ThingsException te) {
		// NOP
	}
	
	/**
	 * Ask the server to quit.
	 */
	public void requestQuit() {
		// NOP
	}
	
	/**
	 * Start the passed process.  Assume a loader will be doing this.  The process should
	 * be loaded, constructed, but not initialized.   If the state is not STATE_CONSTRUCTION, it
	 * will throw an exception.
	 * <br>
	 * All processes started with this will have DEFAULT_USER_CLEARANCE.
	 * <p>
	 * @param processObject This will be a ThingsProcess or subclass.
	 * @param properties These are properties to add (or supplant) to the processes specific view before starting the process.  It is ok
	 * to pass null if there are none.
	 * @throws things.thinger.ThingsException
	 * @return the ID of the started process.
	 * @see things.common.WhoAmI
	 */
	public WhoAmI startProcess(ThingsProcess processObject, ThingsPropertyView properties) throws ThingsException {
		return null;
	}
	
	/**
	 * Start the passed process.  Assume a loader will be doing this.  The process should
	 * be loaded, constructed, but not initialized.   If the state is not STATE_CONSTRUCTION, it
	 * will throw an exception.
	 * <p>
	 * @param processObject This will be a ThingsProcess or subclass.
	 * @param properties These are properties to add (or supplant) to the processes specific view before starting the process.  It is ok
	 * to pass null if there are none.
	 * @param processClearance specify the process clearance level.  This must be at or lower than the calling process's clearance.
	 * @throws Throwable
	 * @return the ID of the started process.
	 * @see things.common.WhoAmI
	 */
	public WhoAmI startProcess(ThingsProcess processObject, ThingsPropertyView properties, Clearance	processClearance) throws Throwable {
		return null;
	}

	/**
	 * Register a ready-made PCB.  It'll allows you to set the clearance level.  The
	 * kernel may choose to deny the operation.  So normally, use startProcess instead.  The process will
	 * not be given standard property paths and initialization, as you get with the startProcess method.
	 * This exists mostly for debugging and testing, but may be useful for processes that live outside
	 * the normal processing framework.
	 * <br>
	 * @param processPCB This will be a ready-made PCB.
	 * @param processClearance The clearance level.  This will be immutable.
	 * @throws things.common.ThingsException
	 */
	public void registerProcess(PCB processPCB, Clearance		processClearance) throws ThingsException {
		// NOP
	}

	
	// =================================================================================
	// == SUSPER SYSTEM INTERFACE
	
	/**
	 * Get the state of a specific process.<p>
	 * If the process is not found, the state is ProcessInterface.ThingsState.STATE_INVALID.
	 * <p>
	 * <b>NO CLEARANCE REQUIRED.</b>
	 * <p>
	 * @param id String id of the process.
	 * @see things.thinger.kernel.ProcessInterface
	 * @return The state.
	 * @throws things.thinger.SystemException
	 */
	public ThingsState getProcessState(String id) throws SystemException {
		return ThingsState.STATE_RUNNING;
	}
	
	/**
	 * Get a process interface.  You can only get a process of equal or less clearance.
	 * <p>
	 * @param id String id of the process.
	 * @see things.thinger.kernel.ProcessInterface
	 * @return The interface
	 * @throws things.thinger.SystemException
	 */
	public ProcessInterface getProcessInterface(String id) throws SystemException {
		return null;
	}
	
	/**
	 * Wait until the named process if done (meaning any state that satisfies ProcessInterface.ThingsState.isDeadOrDying()==true).<p>
	 * If the process is not found, it will quietly return.
	 * <p>
	 * <b>NO CLEARANCE REQUIRED.</b>
	 * <p>
	 * @param id String id of the process.
	 * @see things.thinger.kernel.ProcessInterface
	 * @throws things.thinger.SystemException for general errors or InterruptedException for thread control.  Always let the InterruptedException out.
	 */
	public void waitProcessDone(String id) throws SystemException, InterruptedException {
		// NOP
	}
	
	/**
	 * Get a local reference to the log if possible.  This is totally up to the implementation.  It may be the whole log, a snippet, or null (no log at all).
	 * <p>
	 * <b>NO CLEARANCE REQUIRED.</b>
	 * <p>
	 * @param id String id of the process.
	 * @return log file locator or null
	 * @see things.thinger.kernel.ProcessInterface
	 * @see things.thinger.io.FileSystemLocator
	 * @throws things.thinger.SystemException for general errors or InterruptedException for thread control.  Always let the InterruptedException out.
	 */
	public FileSystemLocator getLogLocal(String id) throws SystemException, InterruptedException {
		return null;
	}
	
	/**
	 * Load a thing but don't run it.  It will only construct.  It's up to initialize and call it.   Typically, the user should
	 * call .init() and then .call_chain().  This is mostly so that THINGs can calll other THINGs, so perhaps it is best to 
	 * just use THING.CALL instead--if you can.
	 * @return The constructed thing.
	 * @throws things.thinger.SystemException
	 */
	public THING loadThing(String name) throws SystemException {
		return null;
	}
	
	/**
	 * Load a module but don't do anything with it.  It will only construct.  It's up to the user to initialize it. 
	 * @return The constructed MODUKE
	 * @param name the name that the loader can use to find it.  Typically, the full class name.
	 * @throws things.thinger.SystemException
	 */
	public MODULE loadModule(String name) throws SystemException {
		return null;
	}
	
	/**
	 * Load and run a thing in a new process.
	 * @return The name of the thing. 
	 * @throws things.thinger.SystemException
	 */
	public String runThing(String name) throws SystemException {
		return "";
	}
	
	/**
	 * Load and run a thing in a new process, giving an expression parent.  All expressions will go to the parent, plus whatever local
	 * mechanism the kernel decides.
	 * @return The name of the thing. 
	 * @param name the resolvable name of the thing.
	 * @param parentExpressor the parent expressor.
	 * @throws things.thinger.SystemException
	 */
	public String runThing(String name, ExpressionInterface  parentExpressor) throws SystemException {
		return "";	
	}

	/**
	 * Load and run a thing in a new process, giving an expression parent.  All expressions will go to the parent, plus whatever local
	 * mechanism the kernel decides.  This will let you add properties to the THING's view before it starts.
	 * @return The name of the thing. 
	 * @param name the resolvable name of the thing.
	 * @param parentExpressor the parent expressor.  Set to null if there is no parent.
	 * @param properties properties to add to the THING processes specific view.
	 * @throws things.thinger.SystemException
	 */
	public String runThing(String name, ExpressionInterface  parentExpressor, ThingsPropertyView	properties) throws SystemException {
		return "";
	}
	
	/**
	 * Ask the kernel for a SuperSystemInterface.  If you can't have it, you'll get a SystemException.  Generally, only services are allowed to have it.
	 * @return The super system interface.
	 * @throws things.thinger.SystemException
	 */
	public SystemSuperInterface requestSuperSystemInterface() throws SystemException {
		return null;
	}
	
	/**
	 * Get a universe by the local name. 
	 * <p>
	 * The stub version will only yield universe_testing.  ever. 
	 * <p>
	 * @param name the local name for the universe
	 * @return The universe.  
	 * @throws things.thinger.SystemException
	 * @see things.universe.Universe
	 */
	public synchronized Universe getUniverse(String name) throws SystemException {
		return null;
	}
	
}
