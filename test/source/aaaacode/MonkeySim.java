/**
 * .
 * Copyright © 1999 Erich P G.
 *
 */
 
package autohit;

import java.io.Serializable;
import java.util.Vector;

import autohit.vm.VMInstruction;

/**
 * Sim is the basic class for a simulation.  Each represents a use-flow for
 * a single user.  It can handle authentication for a single user.
 * <p>
 * Sim's do <b>not</b> have a version reference.  Putting a compiled Sim from one
 * version into a vm of another version can have unpredictable results.
 * <p>
 * When creating a new Sim, you must call the init() member after construction.
 * If you do not, you will eventually get an internal exception.  If you are
 * deserializing, don't worry about it.
 * <p>
 * @author Erich P. Gatejen
 * @version 1.0
 * <i>Version History</i>
 * <code>EPG - Initial - 5Jan99</code> 
 * 
 */
public class Sim implements Serializable {
	
	// --- FINAL FIELDS ------------------------------------------------------	

	// --- FIELDS ------------------------------------------------------------

    /**
     *  A vector containing the Simulation executable.
     *  Each member-object will be a vmInstruction derived class object.
     *
     *  @see autohit.vm.VMInstruction
     *  @serial
     */      	
    public Vector      exec;
    
    /**
     * This sim's name.
     *
     * NOTE!  SimCompiler currently requires a uid attribute for the <name> tag,
     *  but it isn't used for anything now.  
     *  @serial
     */      	
    public String      name;
    
    /**
     * Associated note.
     *  
     *  @serial
     */      	
    public String      note;       

	// --- PUBLIC METHODS ----------------------------------------------------	

    /**
     *  Default Constructor.  It will create an empty Sim.  Remember!  If you are
     *  creating a new Sim, but sure to call init().
     *
     *  @see #init()
     */
    public Sim() {
        
    }    

    /**
     *  Initializes a brand-new Sim().
     */
    public void init() {

        exec = new Vector();
    }
    
    /**
     *  Dump this SIM.  I'm putting this in for debugging.  It might have some other
     *  uses...
     *
     *  @return a String containing the dump.
     */
    public String toString() {
    
        StringBuffer d = new StringBuffer();
        
        d.append("Sim Dump ===============================\n");
        d.append("Name = [" + name + "]\n");
        d.append("Note ----------------------------------- \n");
        d.append(note);
        d.append("\n---------------------------------------- \n");
        d.append("Executable ----------------------------- \n");
        VMInstruction  vmi;
        for (int idx = 0; idx < exec.size(); idx++) {
            d.append("IP = " + idx);
            vmi = (VMInstruction)exec.get(idx);
            d.append(vmi.toString());
        }
        d.append("---------------------------------------- \n");        
        return d.toString();        
    }       
    
	// --- PRIVATE METHODS ---------------------------------------------------	


} 
