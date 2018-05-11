/**
 * RendSite 2008
 * Copyright Erich P Gatejen 2008, 2009 
 */
package rendsite.engine;


/**
 * Interface to resetable objects.
 * @author  erich
 * <i>Version History</i>
 * <pre>
 * 10NOV09 - EPG - First package release.
 * </pre>
 */
public interface Resetable {

	/**
	 * Reset the object.  It should lose all state except what was set during construction.
	 * @throws Throwable if it could not completely reset state.
	 */
	public void reset() throws Throwable;
	
}



