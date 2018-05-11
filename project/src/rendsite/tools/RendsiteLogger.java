/**
 * THINGS/THINGER 2009
 * Copyright Erich P Gatejen (c) 2001 through 2009  ALL RIGHTS RESERVED
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package rendsite.tools;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;

import things.common.ThingsCodes;
import things.common.ThingsException;
import things.common.WhoAmI;
import things.data.Data;
import things.data.Entry;
import things.data.NV;
import things.data.NVImmutable;
import things.data.Receipt;
import things.thinger.SystemException;
import things.thinger.SystemNamespace;
import things.thinger.io.Logger;
import things.thinger.io.conduits.ConduitID;
import things.thinger.io.conduits.PushDrain;

/**
 * Implement a logger.  
 * <i>Version History</i>
 * <pre>
 * 10NOV09 - EPG - First package release.
 * </pre>
 */
public abstract class RendsiteLogger implements Logger, PushDrain {
	
	// ==========================================================================================
	// FIELDS

	// ==========================================================================================
	// DATA
	
	// Usable by the subclass for rendering entries.
	protected String		prefixId;
	protected ConduitID 	conduitId;
	protected Logger.LEVEL	currentLevel = Logger.LEVEL.TOP;
	
	// Internal only.
	private Logger.LEVEL	previousLevel = Logger.LEVEL.TOP;
	private Logger.LEVEL	defaultPostLevel = Logger.LEVEL.DATA;
	
	private Receipt 		stockDeliveryReceipt;			
	private Receipt			stockIrrelevantReceipt;

	// ==========================================================================================
	// CONSTRUCTION
	
    /**
     * Construct a logger.
     * @param ownerId the owner id.  The tag will be available as a prefix to the specific log posters.
     * @param level the starting log level.
     * @throws SystemException
     */
    public RendsiteLogger(WhoAmI  ownerId,  Logger.LEVEL  level) throws SystemException {
    	if (ownerId == null) SystemException.softwareProblem("Cannot construct a RendsiteLogger with a null ownerId.");
    	
    	prefixId = ownerId.toTag();
    	currentLevel = level;
    	previousLevel = level;
    }
	   
	// =====================================================================================================================
	// ABSTRACT
    
	/**
	 * Post it.  It should never give an exception.
	 * @param timestamp
	 * @param level
	 * @param priority
	 * @param numeric
	 * @param text
	 * @param attributes it may be null.
	 */
	public abstract void post(long timestamp, Logger.LEVEL level, Data.Priority priority, int numeric, String text, Collection<NVImmutable> attributes);
	
    /**
     * Create a child logger.  It will differ in ID only.
     * @param ownerId
     * @return the child logger
     * @throws SystemException
     */
    public abstract RendsiteLogger childLogger(WhoAmI  ownerId) throws SystemException;
	
	// =====================================================================================================================
	// POSTERS
	/**
	 * Post as a message.
	 * @param message String to post
	 * @throws ThingsException
	 */
	public void post(String message) throws ThingsException {
		try {
			if (currentLevel.dontpass(defaultPostLevel)) return;
			this.post(System.currentTimeMillis(),defaultPostLevel,Data.Priority.ROUTINE,ThingsCodes.NO_NUMERIC,message,null);			
		} catch (Throwable t) {
			throw new ThingsException("Posting fault.",ThingsException.IO_FAULT_POSTING_FAULT, t, SystemNamespace.ATTR_PLATFORM_MESSAGE, t.getMessage());
		}	
	}
	
	/**
	 * Post as a message.  Best effort.  Ignore errors.
	 * @param message String to post
	 */
	public void postit(String message) {
		try {
			if (currentLevel.dontpass(defaultPostLevel)) return;
			this.post(System.currentTimeMillis(),defaultPostLevel,Data.Priority.ROUTINE,ThingsCodes.NO_NUMERIC,message,null);			
		} catch (Throwable t) {
		}
	}
    
	/**
	 * This will set the level of entries that will pass.  It starts at whatever the implementation sets during construction.
	 * @param newLevel the new level.
	 */
	public void setLevel(LEVEL	newLevel) {
		previousLevel = currentLevel;
		currentLevel = newLevel;
	}
	
	/**
	 * This will get the level of entries that will pass.  It starts at whatever the implementation sets during construction.
	 * @return the level.
	 */
	public LEVEL getLevel() {
		return currentLevel;
	}
	
	/**
	 * This will set the default level of StringPoster posted entries.  The default starts as DATA.  That means, all posted strings will be
	 * treated as DATA level.
	 * @param newLevel the new default level.
	 * @throws things.thinger.SystemException
	 */
	public void setPostLevel(LEVEL	newLevel) {
		defaultPostLevel = newLevel;
	}
	
	// =====================================================================================================================
	// PUSH DRAIN IMPLEMENTATION
	
    /**
     * Initialize the PushDrain.  This will be called by it's controller.  An subsequent calls may result in a PANIC SystemException.  
     * Don't do it!
     * @param yourId The ConduitID for this PushDrain.
     * @see things.thinger.io.conduits.ConduitID
     * @throws things.thinger.SystemException
     */   
    public void init(ConduitID	yourId) throws SystemException {
    	synchronized(this) {
    		if (conduitId != null) throw new SystemException("LoggerWriter initialized more than once.  This is likely a software bug.", SystemException.PANIC_THINGER_INITIALIZATION_VIOLATION);
    		conduitId = yourId;
    		prefixId = yourId.toString();
    		try {
    			stockDeliveryReceipt = new Receipt(conduitId, Receipt.Type.DELIVERY);
    			stockIrrelevantReceipt = new Receipt(conduitId, Receipt.Type.IRRELEVENT);
    		} catch (Throwable t) {
    			throw new SystemException("KernelBasic_Logger2File.init() failed trying to create stock reciepts.  This is likely a software bug.", SystemException.PANIC_THINGER_INITIALIZATION_FAULT, SystemNamespace.ATTR_PLATFORM_MESSAGE, t.getMessage());
    		}
    	}
    }
	
	/**
	 * Listen for a post.  Consumers should implement this.
	 * @param n The data to post.
	 * @return a receipt
	 * @throws things.thinger.SystemException
	 */
	public Receipt postListener(Data		n) throws SystemException {
		try {
			if ((n.getType()==Data.Type.ENTRY)&&(currentLevel.pass(defaultPostLevel))) {
				this.post((Entry)n);
				return stockDeliveryReceipt;
			} else {
				return stockIrrelevantReceipt;
			}
		} catch (Throwable t) {
			throw new SystemException("Logger failed while listening to a conduit.", SystemException.SYSTEM_ERROR_LOGGING_FAILED_ON_CONDUIT,t);
		}
	}
	
	// =====================================================================================================================
	// LOGGER INTERFACE
	
	/**
	 * Initialized the logger.  This will be done by the constructing system, typically the System or Kernel, so most users should
	 * not call this directly.
	 * @param loggerType The type of logger this should be.   This is more a request than a demand.
	 * @throws things.thinger.SystemException
	 */
	public void init(TYPE loggerType) throws SystemException {
	}
	
	/**
	 * Turn debugging on.  Logs with debug level priority will be passed.
	 */
	public void debuggingOn() {
		previousLevel = currentLevel;
		currentLevel = LEVEL.DEBUG;
	}
	
	/**
	 * Turn debugging off.  Logs with debug level priority will not be passed.
	 */
	public void debuggingOff() {
		currentLevel = previousLevel;
	}
	
	/**
	 * Get the current debugging state.
	 * @return debugging state
	 */
	public boolean debuggingState() {
		if (currentLevel.pass(LEVEL.DEBUG)) return true;
		return false;
	}
	
	/**
	 * Post an Entry.
	 * @param e The entry.  
	 * @throws things.thinger.SystemException
	 * @see things.data.Entry
	 */
	public void post(Entry e) throws SystemException {
		if(e.attributes.getAttributeCount()>0) {
			try {
				this.post(e.getStamp(),defaultPostLevel,e.getPriority(), e.getNumeric(), e.toText(), e.attributes.getAttributes());
			} catch (Throwable t) {
				throw new SystemException("Failed to encode attributes in post log.", SystemException.SYSTEM_ERROR_MESSAGE_ENCODING_FAILED,t,SystemNamespace.ATTR_PLATFORM_MESSAGE, t.getMessage());
			}
		} else {
			this.post(e.getStamp(), defaultPostLevel, e.getPriority(), e.getNumeric(), e.toText(), null);
		}
	}
	
	/**
	 * Log an exception.  The implementation should try to deal with the ThingsException features.
	 * @param tr The Exception.  
	 * @throws things.thinger.SystemException
	 */
	public void exception(Throwable tr) throws SystemException {
		if (tr instanceof ThingsException) {
			ThingsException te = (ThingsException) tr;
			error(te.getMessage(), te.numeric, te.getAttributesNVDecorated());
		} else {
			error(tr.getMessage(), ThingsCodes.ERROR);
		}
	}	
	
	/**
	 * Log a trivial error entry.
	 * @param msg The text message.
	 * @throws things.thinger.SystemException
	 */
	public void error(String msg) throws SystemException {
		if (currentLevel.dontpass(LEVEL.ERROR)) return;
		this.post(System.currentTimeMillis(),LEVEL.ERROR,Data.Priority.ROUTINE,ThingsCodes.NO_NUMERIC,msg,null);	
	}

	/**
	 * Log a trivial error entry with a numeric.
	 * @param msg The text message. 
	 * @param numeric The numeric.
	 * @throws things.thinger.SystemException
	 */
	public void error(String msg, int numeric) throws SystemException {
		if (currentLevel.dontpass(LEVEL.ERROR)) return;
		this.post(System.currentTimeMillis(),LEVEL.ERROR,Data.Priority.ROUTINE,numeric,msg,null);
	}
	
	/**
	 * Log a complex error entry with numerics and attributes.
	 * @param msg The text message.
	 * @param numeric The numeric.
	 * @param attributes Sequence of NVs representing attributes.
	 * @throws things.thinger.SystemException
	 * @see things.data.NVImmutable
	 */
	public void error(String msg, int numeric, NVImmutable... attributes) throws SystemException {
		if (currentLevel.dontpass(LEVEL.ERROR)) return;
		this.post(System.currentTimeMillis(),LEVEL.ERROR,Data.Priority.ROUTINE,numeric,msg,attrib(attributes));
	}	
	
	/**
	 * Log a complex error entry with numerics and attributes.
	 * @param msg The text message.
	 * @param numeric The numeric.
	 * @param attributes Sequence of NVs representing attributes.
	 * @throws things.thinger.SystemException
	 * @see things.data.NVImmutable
	 */
	public void error(String msg, int numeric, Collection<NVImmutable> attributes) throws SystemException {
		if (currentLevel.dontpass(LEVEL.ERROR)) return;
		this.post(System.currentTimeMillis(),LEVEL.ERROR,Data.Priority.ROUTINE,numeric,msg,attributes);
	}	
	
	/**
	 * Log a complex error entry with numerics and attributes.
	 * @param msg The text message.
	 * @param numeric The numeric.
	 * @param attributes Name/value pairs for attributes.  Must be an even number of Strings.
	 * @throws things.thinger.SystemException
	 */
	public void error(String msg, int numeric, String... attributes) throws SystemException {
		if (currentLevel.dontpass(LEVEL.ERROR)) return;
		this.post(System.currentTimeMillis(),LEVEL.ERROR,Data.Priority.ROUTINE,numeric,msg,attrib(attributes));
	}
	
	/**
	 * Log a trivial warning entry.
	 * @param msg The text message.
	 * @throws things.thinger.SystemException
	 */
	public void warning(String msg) throws SystemException {
		if (currentLevel.dontpass(LEVEL.WARNING)) return;
		this.post(System.currentTimeMillis(),LEVEL.WARNING,Data.Priority.ROUTINE,ThingsCodes.NO_NUMERIC,msg,null);	
	}

	/**
	 * Log a trivial warning entry with a numeric.
	 * @param msg The text message. 
	 * @param numeric The numeric.
	 * @throws things.thinger.SystemException
	 */
	public void warning(String msg, int numeric) throws SystemException {
		if (currentLevel.dontpass(LEVEL.WARNING)) return;
		this.post(System.currentTimeMillis(),LEVEL.WARNING,Data.Priority.ROUTINE,numeric,msg,null);	
	}
	
	/**
	 * Log a complex warning entry with numerics and attributes.
	 * @param msg The text message.
	 * @param numeric The numeric.
	 * @param attributes Sequence of NVs representing attributes.
	 * @throws things.thinger.SystemException
	 * @see things.data.NVImmutable
	 */
	public void warning(String msg, int numeric, NVImmutable... attributes) throws SystemException {
		if (currentLevel.dontpass(LEVEL.WARNING)) return;
		this.post(System.currentTimeMillis(),LEVEL.WARNING,Data.Priority.ROUTINE,numeric,msg,attrib(attributes));
	}
	
	/**
	 * Log a complex warning entry with numerics and attributes.
	 * @param msg The text message.
	 * @param numeric The numeric.
	 * @param attributes Sequence of NVs representing attributes.
	 * @throws things.thinger.SystemException
	 * @see things.data.NVImmutable
	 */
	public void warning(String msg, int numeric, Collection<NVImmutable> attributes) throws SystemException {
		if (currentLevel.dontpass(LEVEL.WARNING)) return;
		this.post(System.currentTimeMillis(),LEVEL.WARNING,Data.Priority.ROUTINE,numeric,msg,attributes);
	}

	/**
	 * Log a complex warning entry with numerics and attributes.
	 * @param msg The text message.
	 * @param numeric The numeric.
	 * @param attributes Name/value pairs for attributes.  Must be an even number of Strings.
	 * @throws things.thinger.SystemException
	 */
	public void warning(String msg, int numeric, String... attributes) throws SystemException {
		if (currentLevel.dontpass(LEVEL.WARNING)) return;
		this.post(System.currentTimeMillis(),LEVEL.WARNING,Data.Priority.ROUTINE,numeric,msg,attrib(attributes));
	}
	
	/**
	 * Log a trivial information entry.
	 * @param msg The text message.
	 * @throws things.thinger.SystemException
	 */
	public void info(String msg) throws SystemException {
		if (currentLevel.dontpass(LEVEL.INFO)) return;
		this.post(System.currentTimeMillis(),LEVEL.INFO,Data.Priority.ROUTINE,ThingsCodes.NO_NUMERIC,msg,null);
	}

	/**
	 * Log a trivial information entry with a numeric.
	 * @param msg The text message. 
	 * @param numeric The numeric.
	 * @throws things.thinger.SystemException
	 */
	public void info(String msg, int numeric) throws SystemException {
		if (currentLevel.dontpass(LEVEL.INFO)) return;
		this.post(System.currentTimeMillis(),LEVEL.INFO,Data.Priority.ROUTINE,numeric,msg,null);	
	}
	
	/**
	 * Log a complex information entry with numerics and attributes.
	 * @param msg The text message.
	 * @param numeric The numeric.
	 * @param attributes Sequence of NVs representing attributes.
	 * @throws things.thinger.SystemException
	 * @see things.data.NVImmutable
	 */
	public void info(String msg, int numeric, NVImmutable... attributes) throws SystemException {
		if (currentLevel.dontpass(LEVEL.INFO)) return;
		this.post(System.currentTimeMillis(),LEVEL.INFO,Data.Priority.ROUTINE,numeric,msg,attrib(attributes));
	}
	
	/**
	 * Log a complex information entry with numerics and attributes.
	 * @param msg The text message.
	 * @param numeric The numeric.
	 * @param attributes Sequence of NVs representing attributes.
	 * @throws things.thinger.SystemException
	 * @see things.data.NVImmutable
	 */
	public void info(String msg, int numeric, Collection<NVImmutable> attributes) throws SystemException {
		if (currentLevel.dontpass(LEVEL.INFO)) return;
		this.post(System.currentTimeMillis(),LEVEL.INFO,Data.Priority.ROUTINE,numeric,msg,attributes);
	}
	
	/**
	 * Log a complex info entry with numerics and attributes.
	 * @param msg The text message.
	 * @param numeric The numeric.
	 * @param attributes Name/value pairs for attributes.  Must be an even number of Strings.
	 * @throws things.thinger.SystemException
	 */
	public void info(String msg, int numeric, String... attributes) throws SystemException {
		if (currentLevel.dontpass(LEVEL.INFO)) return;
		this.post(System.currentTimeMillis(),LEVEL.INFO,Data.Priority.ROUTINE,numeric,msg,attrib(attributes));	
	}
	
	/**
	 * Log a trivial debug entry.
	 * @param msg The text message.
	 * @throws things.thinger.SystemException
	 */
	public void debug(String msg) throws SystemException {
		if (currentLevel.dontpass(LEVEL.DEBUG)) return;
		this.post(System.currentTimeMillis(),LEVEL.DEBUG,Data.Priority.ROUTINE,ThingsCodes.NO_NUMERIC,msg,null);
	}

	/**
	 * Log a trivial error entry with a numeric.
	 * @param msg The text message. 
	 * @param numeric The numeric.
	 * @throws things.thinger.SystemException
	 */
	public void debug(String msg, int numeric) throws SystemException {
		if (currentLevel.dontpass(LEVEL.DEBUG)) return;
		this.post(System.currentTimeMillis(),LEVEL.DEBUG,Data.Priority.ROUTINE,numeric,msg,null);		
	}
	
	/**
	 * Log a complex debug entry with numerics and attributes.
	 * @param msg The text message.
	 * @param numeric The numeric.
	 * @param attributes Sequence of NVs representing attributes.
	 * @throws things.thinger.SystemException
	 * @see things.data.NVImmutable
	 */
	public void debug(String msg, int numeric, NVImmutable... attributes) throws SystemException {
		if (currentLevel.dontpass(LEVEL.DEBUG)) return;
		this.post(System.currentTimeMillis(),LEVEL.DEBUG,Data.Priority.ROUTINE,numeric,msg,attrib(attributes));
	}
	
	/**
	 * Log a complex debug entry with numerics and attributes.
	 * @param msg The text message.
	 * @param numeric The numeric.
	 * @param attributes Sequence of NVs representing attributes.
	 * @throws things.thinger.SystemException
	 * @see things.data.NVImmutable
	 */
	public void debug(String msg, int numeric, Collection<NVImmutable> attributes) throws SystemException {
		if (currentLevel.dontpass(LEVEL.DEBUG)) return;
		this.post(System.currentTimeMillis(),LEVEL.DEBUG,Data.Priority.ROUTINE,numeric,msg,attributes);
	}
	
	/**
	 * Log a complex debug entry with numerics and attributes.
	 * @param msg The text message.
	 * @param numeric The numeric.
	 * @param attributes Name/value pairs for attributes.  Must be an even number of Strings.
	 * @throws things.thinger.SystemException
	 */
	public void debug(String msg, int numeric, String... attributes) throws SystemException {
		if (currentLevel.dontpass(LEVEL.DEBUG)) return;
		this.post(System.currentTimeMillis(),LEVEL.DEBUG,Data.Priority.ROUTINE,numeric,msg,attrib(attributes));
	}
	
	/**
	 * Shout a log entry.  This is best effort and it will never return an exception.
	 * @param msg The text message.
	 * @param theLevel The level of the message.
	 */
	public void shout(String msg, LEVEL theLevel) {
		if (currentLevel.dontpass(theLevel)) return;
		this.post(System.currentTimeMillis(),theLevel,Data.Priority.ROUTINE,ThingsCodes.NO_NUMERIC,msg,null);
	}

	/**
	 * Shout a log entry with numerics.  This is best effort and it will never return an exception.
	 * @param msg The text message.
	 * @param numeric The numeric.
	 * @param theLevel The level of the message.
	 */
	public void shout(String msg, int numeric, LEVEL theLevel) {
		if (currentLevel.dontpass(theLevel)) return;
		this.post(System.currentTimeMillis(),theLevel,Data.Priority.ROUTINE,numeric,msg,null);		
	}
	
	/**
	 * Shout a log entry with numerics and attributes.  This is best effort and it will never return an exception.
	 * @param msg The text message.
	 * @param numeric The numeric.
	 * @param theLevel The level of the message.
	 * @param attributes Sequence of NVs representing attributes.
	 * @see things.data.NVImmutable
	 */
	public void shout(String msg, int numeric, LEVEL theLevel, NVImmutable... attributes) {
		if (currentLevel.dontpass(theLevel)) return;
		this.post(System.currentTimeMillis(),theLevel,Data.Priority.ROUTINE,numeric,msg,attrib(attributes));
	}
	
	/**
	 * Shout a log entry with numerics and attributes.  This is best effort and it will never return an exception.
	 * @param msg The text message.
	 * @param numeric The numeric.
	 * @param theLevel The level of the message.
	 * @param attributes Sequence of NVs representing attributes.
	 * @see things.data.NVImmutable
	 */
	public void shout(String msg, int numeric, LEVEL theLevel, Collection<NVImmutable> attributes) {
		if (currentLevel.dontpass(theLevel)) return;
		this.post(System.currentTimeMillis(),theLevel,Data.Priority.ROUTINE,numeric,msg,attributes);
	}
	
	/**
	 * Shout a log entry with numerics and attributes.  This is best effort and it will never return an exception.
	 * @param msg The text message.
	 * @param numeric The numeric.
	 * @param theLevel The level of the message.
	 * @param attributes Name/value pairs for attributes.  Must be an even number of Strings.
	 * @see things.data.NV
	 */
	public void shout(String msg, int numeric, LEVEL theLevel, String... attributes) {
		if (currentLevel.dontpass(theLevel)) return;
		this.post(System.currentTimeMillis(),theLevel,Data.Priority.ROUTINE,numeric,msg,attrib(attributes));
	}

	// =====================================================================================================================
	// INTERNAL
	
	/**
	 * Box the attributes.  This is so we can have one post method, though I admit it adds an unfortunate overhead.
	 * @param attributes 
	 * @return a collection of attributes as NVImmutable
	 */
	private Collection<NVImmutable> attrib(String... attributes) {
		Collection<NVImmutable> result = new LinkedList<NVImmutable>();
		try {
			for (int index = 0 ; index < attributes.length ; index+=2) {
				result.add(new NV(attributes[index], attributes[index+1]));
			}
		} catch (Throwable t) {
			// If there is a problem, like it being unbalanced, just return what we have.
		}
		return result;
	}
    
	/**
	 * Box the attributes.  This is so we can have one post method, though I admit it adds an unfortunate overhead.
	 * @param attributes 
	 * @return a collection of attributes as NVImmutable
	 */
	private Collection<NVImmutable> attrib(NVImmutable... attributes) {
		ArrayList<NVImmutable> result = new ArrayList<NVImmutable>();
		for (NVImmutable item : attributes) {
			result.add(item);
		}
		return result;
	}
	
	
	
}