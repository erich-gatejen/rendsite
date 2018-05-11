/**
 * RendSite 2008
 * Copyright Erich P Gatejen 2008, 2009 
 */
package rendsite.tools;

import java.io.File;
import java.util.HashMap;

import rendsite.RendsiteConfiguration;
import things.common.ThingsException;
import things.common.WhoAmI;
import things.common.impl.WhoAmISimple;
import things.data.ThingsPropertyTree;
import things.data.ThingsPropertyView;
import things.data.ThingsPropertyViewReader;
import things.data.impl.ThingsPropertyTreeRAM;
import things.data.tables.Table;
import things.thing.MODULE;
import things.thing.THING;
import things.thinger.ExpressionInterface;
import things.thinger.SystemException;
import things.thinger.SystemSuperInterface;
import things.thinger.io.FileSystemLocator;
import things.thinger.io.Logger;
import things.thinger.io.conduits.ConduitController;
import things.thinger.io.conduits.basic.BasicConduitController;
import things.thinger.kernel.ProcessInterface;
import things.thinger.kernel.ThingsState;
import things.thinger.kernel.basic.KernalBasic_LoggingExpressor;
import things.universe.Universe;

/**
 * Stub out the system interface (like the test.things does).  This is for stand-alone rendering tools.  
 * @author  erich
 * <i>Version History</i>
 * <pre>
 * 10NOV09 - EPG - First package release.
 * </pre>
 */
public class SystemInterfaceStub extends SystemInterfaceStubBase {

	// =================================================================================
	// == FIELDS
	
	/**
	 * Given as shared and local properties.  They will appear to be local to every caller.
	 */
	public ThingsPropertyView properties;
	
	/**
	 * Config properties.  They would normally root under user.config. in the main server.  berzerq2_install_root will be automatically 
	 * set (and possibly overriden) by the stub.
	 */
	public ThingsPropertyView configProperties;
	
	/**
	 * System stuff.
	 */
	public RendsiteLogger rootLogger;
	public RendsiteLogger systemLogger;
	public HashMap<String,RendsiteLogger> namedLoggers;
	private BasicConduitController systemConduits;
	
	public WhoAmI myId;
	public String root;
	public RendsiteConfiguration configuration;
	
	// =================================================================================
	// == STATIC TOOLS 
	
	private static SystemInterfaceStub currentStub;
	//private static Universe stubUniverse;
	
	/**
	 * Get the global stub.
	 * @param root the root of all the action.
	 * @param properties used as local and global properties.
	 * @param configProperties used as config properties, normally rooted at user.config. for the main server.
	 * @param configuration The RendSite configuration.
	 * @param systemId the imposed system id.
	 * @return the stub.
	 * @throws Throwable
	 */
	public static synchronized SystemInterfaceStub getStub(String root, ThingsPropertyView properties, ThingsPropertyView configProperties, 
			RendsiteConfiguration configuration, WhoAmI systemId) throws Throwable  {

		if (currentStub==null) {
			currentStub = new SystemInterfaceStub(root, properties, configProperties, configuration, systemId);
		} 
		return currentStub;
	}
	
	/**
	 * Get the global stub.  This assumes it has already been built.
	 * @return the global stub.
	 * @throws Throwable
	 */
	public static synchronized SystemInterfaceStub getStub() throws Throwable {
		if (currentStub==null) {
			throw new Exception("Static global stub not created yet, so can't use getStub() method.");
		} 
		return currentStub;
	}
	
	// =================================================================================
	// == METHODS 
	
	/**
	 * Create the stub using a real properties file.
	 * @param root the root of all the action.
	 * @param properties used as local and global properties.
	 * @param configProperties used as config properties, normally rooted at user.config. for the main server.
	 * @param configuration the configuration.  It must have the root logger set!
	 * @param systemId the system id.
	 * @throws Throwable
	 */
	public SystemInterfaceStub(String root, ThingsPropertyView properties, ThingsPropertyView configProperties,
			RendsiteConfiguration configuration, WhoAmI systemId) throws Throwable { 
		
		// Qualify
		if (root==null) throw new Exception("Root cannot be null.");
		if (systemId==null) throw new Exception("systemId cannot be null.");
		if (configuration==null) throw new Exception("RendsiteConfiguration cannot be null.");
		
		
		if (properties==null) 
			this.properties = new ThingsPropertyTreeRAM();
		else
			this.properties = properties;	
		if (configProperties==null) 
			this.configProperties = new ThingsPropertyTreeRAM();
		else
			this.configProperties = properties;				
		
		myId = systemId;
		
		File installRootCheck = new File(root);
		if (!installRootCheck.isDirectory()) throw new Exception("Root not a valid directory.");
		this.root = root;
		
		// Conduits
		systemConduits = new BasicConduitController();
		
		// Logger is a child of the root
		rootLogger = configuration.rootLogger();
		systemLogger = rootLogger.childLogger(systemId);
		namedLoggers = new HashMap<String,RendsiteLogger>();
	}
	
	// =================================================================================
	// == SUPER SYSTEM INTERFACE ABSTRACT IMPLEMENTATION
	
	/**
	 * Get the system conduit controller.  These are for conduits between privileged services.
	 * @return a ConduitController
	 * @throws things.thinger.SystemException
	 * @see things.thinger.io.conduits.ConduitController
	 */
	public ConduitController getSystemConduits() throws SystemException {
		return systemConduits;
	}

	// =================================================================================
	// == SYSTEM INTERFACE ABSTRACT IMPLEMENTATION
	
	/**
	 * Get a system logger for the process.<br>
	 * This is implemented with the numbered call GET_SYSTEM_LOGGER.
	 * @return A logger.
	 * @see things.thinger.io.Logger
	 * @throws things.thinger.SystemException
	 */
	public Logger getSystemLogger() throws SystemException {
		return systemLogger;
	}
	
	/**
	 * Forge a new named logger.  If a logger already exists for that name, it may cause an exception, depending on how it resolves.
	 * <br>
	 * @param name the name.  It will be unique.  It's up to kernel on how the name is resolved.
	 * @return A logger.
	 * @see things.thinger.io.Logger
	 * @throws things.thinger.SystemException
	 */
	public synchronized Logger getNamedLogger(String name) throws SystemException {
		RendsiteLogger result = namedLoggers.get(name);
		if (result == null) {
			result = rootLogger.childLogger(new WhoAmISimple(name, name));
			namedLoggers.put(name, result);
		} 
		return result;
	}
	
	/**
	 * Forge a new named expressor.
	 * <br>
	 * @param name the name.  Generally, it should be unique.  It's up to kernel on how the name is resolved and if name reuse is allowed.
	 * @return an expression interface.
	 * @see things.thinger.ExpressionInterface
	 * @throws things.common.ThingsException
	 */
	public ExpressionInterface getNamedExpressor(String name) throws ThingsException {
		return new KernalBasic_LoggingExpressor(systemLogger, this);
	}
	
	/**
	 * Get local property view for the caller only.  
	 * @return a property view
	 * @throws things.thinger.SystemException
	 */
	public ThingsPropertyView getLocalProperties() throws SystemException {
		return properties;
	}
	
	/**
	 * Get the read only properties for this for the caller only.  
	 * @return a property view reader
	 * @throws things.thinger.SystemException
	 */
	public ThingsPropertyViewReader getConfigProperties() throws SystemException {
		return properties;
	}
	
	/**
	 * Get shared property view for this server.  Anyone can read and write to them.  
	 * @return a property view
	 * @throws things.thinger.SystemException
	 */
	public ThingsPropertyView getSharedProperties() throws SystemException {
		return properties;	
	}
	
	/**
	 * Get the process list.<p>
	 * The process list will be a Table.
	 * @return A table representing the process list.
	 * @see things.data.tables.Table
	 * @throws things.thinger.SystemException
	 */
	public Table<String> getProcessList() throws SystemException {
		return new Table<String>();
	}
	
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
		return this;
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
	
	/**
	 * Get process ID for the calling prosess. 
	 * <p>
	 * @return The ID.
	 * @throws things.thinger.SystemException
	 */
	public WhoAmI getCallingProcessId() throws SystemException {
		return myId;
	}


	/**
	 * Get an empty tree using the preferred, non-persistent implementation for the local host.
	 * @return a new property tree.  
	 */	
	public ThingsPropertyTree getLocalPropertiesImplementation() throws ThingsException {
		return new ThingsPropertyTreeRAM();
	}
	
	
}
