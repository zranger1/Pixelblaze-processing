package pixelblaze.library;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Map;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.handshake.ServerHandshake;

import processing.core.*;

/**
 * Pixelblaze object - handles connection and conversation with a single Pixelblaze
 *
 * @example Pixelblaze(pApp,ip_address); 
 */

public class Pixelblaze {
	PApplet app;
	URI ipAddr = null;	
	PBWebsocket ws = null;

	boolean flash_save_enabled = false;

	// CONSTANTS
	public final static String VERSION = "##library.prettyVersion##";
	final int PORT = 81;

	/**
	 * Create a Pixelblaze object.  Takes the IP address (no protocol, no port)
	 * of a Pixelblaze.  Returns object if successful. Throws 
     * java.net.ConnectException if the connection times out, or
     * URISyntaxException if the address is not valid.
	 * 
	 * @example Pixelblaze(pParent,ip_address);
	 * @param theParent the parent PApplet
	 * @
	 */
	public Pixelblaze(PApplet pApp,String ip_address) {
		this.app = pApp;

		// register cleanup function
		app.registerMethod("dispose", this);
		
		// attempt to open websocket connection to Pixelblaze
		try {
			ipAddr = new URI(ip_address);
			ws = new PBWebsocket(ipAddr);
			ws.setReuseAddr(true);
			ws.setTcpNoDelay(true);
			ws.connect();	

			// wait for connection to open. 
			while (!ws.isOpen()) {
				try {
					Thread.sleep(250);
				} catch (InterruptedException ie) {
					Thread.currentThread().interrupt();
				}								
			}			
			
		} catch (URISyntaxException e) {
			// TODO:  Do something more interesting with this exception??
			e.printStackTrace();
		}		
	}

	public void sendMsg(String msg) {
		if (ws.isOpen()) {
			ws.send(msg);
		}
	}
	
	/**
	 *  Request a the current pattern list
	 */
	public void getPatternList() {
	    sendMsg("{\"listPrograms\" : true}");
	}		
	
	/**
	 *  Given the name of a pattern, return the
	 *  pattern's ID string and sort index, or  if not found.
	 * @param name
	 * @return String containing name of pattern if found, null otherwise
	 */ 
	public String patternIdFromName(String name) {
		// TODO:  NOT YET IMPLEMENTED
		return name;
	}
	
	/**
      Sets the active pattern by name. Does not attempt to validate
      the Id (TODO:  Maybe it should??)
	*/	
	public void setActivePatternById(String id) {  	
		sendMsg(String.format("{ \"activeProgramId\" : %s }",id));    	
	}	
	
	/**
       Sets the active pattern by name
	 */	
	public void setActivePatternByName(String name) {
    	String id = patternIdFromName(name);   	
    	if (id.length() > 0) {
    		setActivePatternById(id);
    	}
    }
    
    /*
      Requests the ID of the pattern currently running on
      the Pixelblaze if available.  Otherwise returns an empty dictionary
      object
     */
    public void getActivePattern() {
    	//TODO:  NOT YET IMPLEMENTED
    }
	
	/**
	 request list of Pixelblaze Web UI controls and settings for
	 the specified pattern if available.
	*/
    public void getControls(String id) {	
	  sendMsg("{ \"getControls\" : ${id} }");  		
	}
	
	/**
        Sets UI controls in the active pattern to values contained in
        the JSON object in argument json_ctl. To reduce wear on
        Pixelblaze's flash memory, the saveFlash parameter is ignored
        by default.  See documentation for _enable_flash_save() for
        more information.
	*/
	public void setControls(String jsonString,boolean saveFlash) {	
    	//TODO:  NOT YET IMPLEMENTED	    		
	}
	
	/**
    Sets UI controls in the active pattern to values contained in
    the JSON object in argument json_ctl. To reduce wear on
    Pixelblaze's flash memory, the saveFlash parameter is ignored
    by default.  See documentation for _enable_flash_save() for
    more information.
*/
	public void setControl(String name,float value,boolean saveFlash) {	
	  //TODO:  NOT YET IMPLEMENTED	    		
    }	
	
	/**
	 request list of Pixelblaze Web UI controls and settings for
	 the currently active pattern if available.
	*/
    public void getControls() {
	 // TODO: Not yet implemented
	 // id = device.getDataValue("activePatternId")
	 // if (id.length() < 1) {
	 //   logDebug("getControls: Active pattern not available.")
	 //   return 
	 //  }
	 //  getControls(id); 
	}
	
	/**
	 returns true if the specified control exists in the current pattern,
	 false otherwise.
	*/
	public boolean controlExists(String id) {	
	  return false;  // TODO: NOT YET IMPLEMENTED
	}
	
	/**
        Sets the 3-element color of the specified HSV or RGB color picker.
        The color argument should contain an RGB or HSV color with all values
        in the range 0-1. To reduce wear on Pixelblaze's flash memory, the saveFlash parameter
        is ignored by default.  See documentation for _enable_flash_save() for
        more information.
        # based on testing w/Pixelblaze, no run-time length or range validation is performed
        # on color. Pixelblaze ignores extra elements, sets unspecified elements to zero,
        # takes only the fractional part of elements outside the range 0-1, and
        # does something (1-(n % 1)) for negative elements.
        val = {ctl_name: color}	 
	 */
	
	public void setColorControl(String ctl_name, String color, boolean saveFlash) {
    	// TODO: NOT YET IMPLEMENTED
    }

    /*
        Returns a list of names of the specified pattern's rgbPicker or
        hsvPicker controls if any exist, None otherwise.  If the pattern
        argument is not specified, check the currently running pattern
     */
    public ArrayList getColorControlNames(String pattern) {
    	//TODO: NOT YET IMPLEMENTED
    	return null;    	
    }
    
    /*
        Returns the name of the specified pattern's first rgbPicker or
        hsvPicker control if one exists, None otherwise.  If the pattern
        argument is not specified, checks in the currently running pattern
     */
    public String getColorControlName(String pattern) {
    	//TODO: NOT YET IMPLEMENTED    	
    	return null;
    }

/*
        Sets custom bit timing for WS2812-type LEDs.
        CAUTION: For advanced users only.  If you don't know
        exactly why you want to do this, DON'T DO IT. Works on
        Pixelblaze 2+ only. 
        
        See discussion in this thread on the Pixelblaze forum:
        https://forum.electromage.com/t/timing-of-a-cheap-strand/739
        
        Note that you must call _enable_flash_save() in order to use
        the saveFlash parameter to make your new timing (semi) permanent.
 */
    public void setDataspeed(int speed, boolean saveFlash) {
     //TODO: NOT YET IMPLEMENTED    	
     //   self.send_string('{"dataSpeed":%d %s}' % (speed, saveStr))
    }
	
	/**
	 *  request a json-ized list of exported variables from
	 *  the currently active pattern.
	 */
	public void getVariables() {
	    sendMsg("{ \"getVars\" : true }");
	}	
	
	/**
	  Set one or more exported pattern variables on the Pixelblaze.
	  Takes a json-formatted string of the names and values
	  of variables exported in the currently active pattern.
	  see readme.md and the Pixelblaze expression documentation
	  for details.
	 * @param jsonString
	 * @return
	 */
	public void  setVariables(String jsonString) {
		String msg = String.format("{ \"setVars\" : %s }",jsonString);		
	    sendMsg(msg);
	}	
	
	/**
      Set the value of a single exported pattern variable.
	 * @param js
	 * @return
	 */
	public void  setVariable(String name, float val) {
		// TODO:  need other overloaded forms of setVariable(s)
		String msg = String.format("{ \"setVars\" : {\"%s\" : %f }}",name, val);		
	    sendMsg(msg);
	}		
	
	/**
    Returns True if the specified variable exists in the active pattern,
    False otherwise.
    */	
	public boolean variableExists(String name) {
		// NOT YET IMPLEMENTED
		return false;
	}
	
	/**
	* Requests hardware configuration info.
	*/ 
	public void getHardwareConfig() {
	    sendMsg("{ \"getConfig\" : true }");    
	}	

	/**
	 * Set the Pixelblaze's global brightness.  Valid range is 0-1
	 */	
	public void setBrightness(float bri) {	
		// clamp to proper 0-1 range
		bri = Math.max(0, Math.min(bri,1.0f)); 
		String msg = String.format("{ \"brightness\" : %f }",bri);
		sendMsg(msg);		
	}	

	/**
        Sets number of milliseconds the Pixelblaze's sequencer will run each pattern
        before switching to the next.
	 */		
	public void setSequenceTimer(int ms) {
		String msg = String.format("{ \"sequenceTimer\" : %d }",ms);
		sendMsg(msg);				
	}

	/**
    Enable and start the Pixelblaze's internal sequencer. The mode parameters
    can be 1 - shuffle all patterns, or 2 - playlist mode.  Defaults to mode 1
    if mode is not provided. The playlist must be configured through the Pixelblaze's web UI.  
	 */
	public void startSequencer(int mode) {
		String msg = String.format("{ \"sequencerMode\" : %d, \"runSequencer\" : true}",mode);
		sendMsg(msg);	    
	}		

	/**
    Enable and start the Pixelblaze's internal sequencer. The mode parameters
    can be 1 - shuffle all patterns, or 2 - playlist mode.  Defaults to mode 1
    if mode is not provided. The playlist must be configured through the Pixelblaze's web UI.  
	 */    
	public void startSequencer() {
		startSequencer(1);
	}

	/**
    Stop and disable the Pixelblaze's internal sequencer 
	 */
	public void stopSequencer() {
		String msg = String.format("{ \"sequencerMode\" :0, \"runSequencer\" : false}");
		sendMsg(msg);	    
	}	    

	/**
     Temporarily pause the Pixelblaze's internal sequencer, without
     losing your place in the shuffle or playlist. Call "playSequencer"
     to restart.  Has no effect if the sequencer is not currently running. 
	 */
	public void pauseSequencer() {
		String msg = String.format("{\"runSequencer\" : false}");
		sendMsg(msg);	    
	}	   

	/**
    Start the Pixelblaze's internal sequencer in the current mode,
    at the current place in the shuffle or playlist.  Compliment to
    "pauseSequencer".  Will not start the sequencer if it has not
    been enabled via "startSequencer" or the Web UI.
	 */
	public void playSequencer() {
		String msg = String.format("{\"runSequencer\" : true}");
		sendMsg(msg);	    
	}	    

	/**
	 * Attempt to reestablish a connection that was lost or broken
	 */
	public void reconnect() {
		if ((ws != null) && ws.isClosed()) {
			ws.connect();
		}
	}

	public void close() {
		if ((ws != null) && ws.isOpen()) {
			ws.close(1000,"Connection closed by client.");
		}
	}

	/**
		Called when the library shuts down.
		<p>
		Closes open devices and shuts down any
		active threads. 
	 */		
	public void dispose() {
		this.close();
	}	

	/**
	 * return the library version number
	 * 
	 * @return String
	 */
	public static String version() {
		return VERSION;
	}
}

