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

import java.io.PrintWriter;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

import rendsite.Codes;
import rendsite.Constants;
import rendsite.resources.Resources;
import things.common.ThingsCodes;
import things.common.ThingsConstants;
import things.common.ThingsException;
import things.common.ThingsUtilityBelt;
import things.common.WhoAmI;
import things.data.Data;
import things.data.NVImmutable;
import things.thinger.SystemException;
import things.thinger.io.Logger;

/**
 * Implement a logger.  
 * <i>Version History</i>
 * <pre>
 * 10NOV09 - EPG - First package release.
 * </pre>
 */
public class RendsiteLogger_Normal extends RendsiteLogger {
	
	// ==========================================================================================
	// FIELDS

	// ==========================================================================================
	// DATA
	private PrintWriter pw;
	private List<RendsiteLogger_Normal> children;
	private Resources resources;
	private ResourceBundle mb;
	
	// ==========================================================================================
	// CONSTRUCTION
	
    /**
     * Construct a logger.
     * @param ownerId the owner id.  The tag will be available as a prefix to the specific log posters.
     * @param level the starting log level.
     * @param pw the output destination.  it'll be best if it isn't auto-flush.
     * @throws SystemException
     */
    public RendsiteLogger_Normal(WhoAmI  ownerId,  Logger.LEVEL  level, PrintWriter pw, Resources resources) throws SystemException {
    	super(ownerId, level);
    	if (pw==null) SystemException.softwareProblem("Cannot contruct a RendsiteLogger_Normal with a null PrintWriter");
    	this.pw = pw;
    	if (resources==null) SystemException.softwareProblem("Cannot contruct a RendsiteLogger_Normal with a null Resources");
    	this.resources = resources; 
    	try {
    		mb = resources.getMessagesBundle();
    	} catch (ThingsException te) {
    		throw new SystemException("Could not get Messages bundle.", te.numeric, te);   		
    	} catch (Throwable t) {
    		throw new SystemException("Could not get Messages bundle.", Codes.PANIC_SETUP_RESOURCES__COULD_NOT_LOAD_MESSAGES_FOR_LOCALE, t);
    	}
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
	public void post(long timestamp, Logger.LEVEL level, Data.Priority priority, int numeric, String text, Collection<NVImmutable> attributes) {

		StringBuffer entry = new StringBuffer();
		entry.append(ThingsUtilityBelt.timestampFormatterDDDHHMMSS(timestamp));
		entry.append(ThingsConstants.CODEC_SEPARATOR_CHARACTER);
		entry.append(level.toString5());
		entry.append(ThingsConstants.CODEC_SEPARATOR_CHARACTER);
		entry.append(ThingsUtilityBelt.hexFormatter16bit(numeric));
		entry.append(ThingsConstants.CODEC_SEPARATOR_CHARACTER);
		
		String ltext = Resources.lookupNumeric(mb, numeric);
		if (ltext==null) {
			entry.append(text);
		} else {
			if (debuggingState()) {
				entry.append(ltext);	
				entry.append(ThingsConstants.CODEC_SEPARATOR_CHARACTER);
				entry.append(text);
			} else {
				entry.append(ltext);					
			}
		}
		
		entry.append(ThingsConstants.CODEC_SEPARATOR_CHARACTER);
		entry.append(renderAttributes(attributes));
		
		pw.println(entry.toString());			// Do it all at once, so we don't collide with children, should we ever go multiprocessing.
		pw.flush();	
	}
	
	
    /**
     * Create a child logger.  It will differ in ID only.
     * @param ownerId
     * @return the child logger.
     * @throws SystemException
     */
    public synchronized RendsiteLogger childLogger(WhoAmI  ownerId) throws SystemException {
    	RendsiteLogger_Normal logger = new RendsiteLogger_Normal(ownerId,  currentLevel, pw, resources);
    	if (children==null) {
    		children = new LinkedList<RendsiteLogger_Normal>();
    		children.add(logger);
    	}
    	return logger;
    }

	/**
	 * Flush.
	 */
	public void flush() {
		pw.flush();
	}
	
	// =====================================================================================================================
	// OVERLOADED METHODS - overload so we can pass down changes to the children.
	
	/**
	 * This will set the level of entries that will pass.  It starts at whatever the implementation sets during construction.
	 * @param newLevel the new level.
	 */
	public void setLevel(LEVEL	newLevel) {
		super.setLevel(newLevel);
		for (RendsiteLogger_Normal logger : children) {
			logger.setLevel(newLevel);
		}
	}
	
	/**
	 * This will set the default level of StringPoster posted entries.  The default starts as DATA.  That means, all posted strings will be
	 * treated as DATA level.
	 * @param newLevel the new default level.
	 */
	public synchronized void setPostLevel(LEVEL	newLevel) {
		super.setPostLevel(newLevel);
		for (RendsiteLogger_Normal logger : children) {
			logger.setPostLevel(newLevel);
		}
	}
	
	/**
	 * Turn debugging on.  Logs with debug level priority will be passed.
	 */
	public synchronized void debuggingOn() {
		super.debuggingOn();
		for (RendsiteLogger_Normal logger : children) {
			logger.debuggingOn();
		}
	}
	
	/**
	 * Turn debugging off.  Logs with debug level priority will not be passed.
	 */
	public synchronized void debuggingOff() {
		super.debuggingOff();
		for (RendsiteLogger_Normal logger : children) {
			logger.debuggingOn();
		}
	}
	
	/**
	 * Log an exception.  The implementation should try to deal with the ThingsException features.  This overrides the simple implementation in RendsiteLogger.
	 * @param tr The Exception.  
	 * @throws things.thinger.SystemException
	 */
	public void exception(Throwable tr) throws SystemException {
		if (tr instanceof ThingsException) {
			
			ThingsException te = (ThingsException) tr;
			Throwable tc = tr.getCause();
			
			if ((tc!=null)&&(tc instanceof ThingsException)) {
				
				ThingsException tce = (ThingsException) tc;
				
				if (debuggingState()) {
					error(tce.getMessage(), tce.numeric, te.getAttributesNVDecorated(Constants.NAME_ERROR__ROOT_CAUSE, ThingsException.toStringCauses(tr)));
				} else {
					error(tce.getMessage(), tce.numeric, te.getAttributesNVDecorated());
				}
				
			} else {
				if (debuggingState()) {
					error(te.getMessage(), te.numeric, te.getAttributesNVDecorated(Constants.NAME_ERROR__ROOT_CAUSE, ThingsException.toStringCauses(tr)));
				} else {
					error(te.getMessage(), te.numeric, te.getAttributesNVDecorated());
				}
			}
				
		} else {
			if (debuggingState()) {
				error(tr.getMessage(), ThingsCodes.ERROR, Constants.NAME_ERROR__ROOT_CAUSE, ThingsException.toStringCauses(tr));
			} else {
				error(tr.getMessage(), ThingsCodes.ERROR);
			}
		}
	}	
	
	
	// =====================================================================================================================
	// INTERNAL
	
	private final static Pattern splitNL = Pattern.compile("(\\n|\\r)+");
	private final static String FOLD_1 = System.getProperty("line.separator") + "\t";
	private final static String FOLD_2 = System.getProperty("line.separator") + "\t\t";
	private final static char MULTIVAR_SEPARATOR = ',';
	
	/**
	 * Render the attributes.  Use brute force for now.  I'll reduce it later, if need be.
	 * @param attributes
	 * @return the rendering.  It will nto have a final CRLF.
	 */
	private String renderAttributes(Collection<NVImmutable> attributes) {
		if (attributes==null) return "";
		StringBuffer result = new StringBuffer();
		String[] split;
		String[] multi;
		
		try {
			Iterator<NVImmutable> items = attributes.iterator();
			NVImmutable item = null;
			while(items.hasNext()) {
				item = items.next();
				
				if (item.isMultivalue()) {
				
					result.append(FOLD_1);			
					result.append(item.getName());
					result.append(ThingsConstants.CODEC_EQUALITY);
					
					multi = item.getValues();
					for (int index = 0 ; index < multi.length ; index++) {
						split = splitNL.split(item.getValue());
						if (split.length>1) {
							result.append(FOLD_2);	
							for (int index2 = 0 ; index2 < split.length ; index2++) {
								result.append(split[index2]);
								if (index2+1 < split.length) result.append(FOLD_2);
							}
							if (index+1 < multi.length) result.append(FOLD_2);
							else if (items.hasNext()) result.append(FOLD_1);
							
						} else {
							result.append(split[0]);
							if (index+1 < multi.length) result.append(MULTIVAR_SEPARATOR);
							else if (items.hasNext()) result.append(FOLD_1);
						}
					}
					
				} else {
					split = splitNL.split(item.getValue());
					if (split.length>1) {
						result.append(FOLD_1);			
						result.append(item.getName());
						result.append(ThingsConstants.CODEC_EQUALITY);
						result.append(FOLD_2);	
						for (int index = 0 ; index < split.length ; index++) {
							result.append(split[index]);
							if (index+1 < split.length) result.append(FOLD_2);
						}
						if (items.hasNext()) result.append(FOLD_1);
						
					} else {
						result.append(item.getName());
						result.append(ThingsConstants.CODEC_EQUALITY);
						result.append(split[0]);
						if (items.hasNext()) result.append(ThingsConstants.CODEC_SEPARATOR_CHARACTER);
					}
					
				} // end if multivalue
				
			} // end for
			
		} catch (Throwable t) {
			return "ERROR rendering attributes.";
		}
		return result.toString();
	}
	
	
}