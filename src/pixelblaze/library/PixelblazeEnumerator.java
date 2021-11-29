package pixelblaze.library;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import processing.core.*;
import processing.data.StringList;

/**
   Listens on a network to detect available Pixelblazes, which the user can then list
   or open as Pixelblaze objects.  Also provides time synchronization services for
   running synchronized patterns on a network of Pixelblazes.
 */
public class PixelblazeEnumerator extends Thread {
	final int PORT = 1889;	
	final int BEACON_PACKET = 42;
	final int TIMESYNC_PACKET = 43;
	final int LIST_CHECK_INTERVAL = 5000;

	PApplet pApp;
	int listTimeoutCheck = 0;
	int syncId = 890;
	int deviceTimeout = 30000;
	boolean isRunning = false;
	boolean autoSync = false;	
	
	HashMap<String,Object> deviceList;
	 
	DatagramSocket ds;
	DatagramPacket datagram;
	InetSocketAddress listenAddr;	

	byte[] buffer;
	byte[] sendbuf;

	/**
	 Create a PixelblazeEnumerator object, listening for Pixelblazes on only the
	 specified network interface.  (Use PixelblazeEnumerator() to listen on all
	 interfaces.) 
	 */
	public PixelblazeEnumerator(PApplet parent,String hostIp) {
		pApp = parent;
		buffer = new byte[1024];
    	datagram = new DatagramPacket(buffer, buffer.length); 
    	deviceList = new HashMap<String, Object>();

		try {	
			ds = new DatagramSocket(null);
	    	listenAddr = new InetSocketAddress(hostIp,PORT);
			ds.setSoTimeout(0);		
			ds.bind(listenAddr);
			start();
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 Create a PixelblazeEnumerator object, listen for Pixelblazes on all available
	 network interfaces. (Use PixelblazeEnumerator(String hostIp) to listen on a
	 single, specific interface.)  
	 */
	public PixelblazeEnumerator(PApplet parent) {
		this(parent,new String("0.0.0.0"));	
	}
		
	/**
	 * Retrieve the list of currently visible Pixelblazes, along with all the information
	 * contained in that Pixelblaze's beacon packet.
	 * 
	 * @return a HashMap containing a list of Pixelblazes currently visible 
	 * on the network, and their individual beacon information. Can be empty
	 * if no Pixelblazes are found. 
	 */
	@SuppressWarnings("rawtypes")
	public HashMap getPixelblazeListEx() {		
		return deviceList;
	}
	
	/**
	 * Get list of IP addresses of all currently visible Pixelblazes
	 * 
	 * @return a processing StringList object containing the IP addresses of all
	 * available Pixelblazes
	 */
	public StringList getPixelblazeList() {
		return new StringList(deviceList.keySet());
	}
		
    /**
     * Utility Method:  Sets the PixelblazeEnumerator object's network
       id for time synchronization. At the moment, any 32 bit value will
       do, and calling this method does (almost) nothing.  In the
       future, the ID might be used to determine priority among multiple time sources. 
     */
	public void setSyncId(int id) {
		this.syncId = id;
	}
	
	/**
     Sets the interval in milliseconds which the enumerator will wait without
     hearing from a device before removing it from the active devices list.
          
     The default timeout is 30000 (30 seconds).
	 */
	public void setDeviceTimeout(int ms) {
        this.deviceTimeout = ms;
	}
	
	/**
	 * Synchronize clocks between all active Pixelblazes.  Allows synchronization
	 * of patterns.  Timesync is off by default.
	 */
	public void enableTimesync() {
		this.autoSync = true;
	}
	
	/**
	 * Disable automatic synchronization of Pixelblaze clocks.   
	 */	
	public void disableTimesync() {
		this.autoSync = false;		
	}
	
	/**
	 * Start the Pixelblaze enumerator thread.  Called automatically
	 * by the object contstructor - user should never need to call this.
	 */
	public void start() {
		System.out.println("PixelblazeEnumerator thread starting");  
		isRunning = true;
		super.start();
	}

	/**
	 * Main loop for the Pixelblaze enumerator thread. Configured automatically
	 * at object creation. - users should never need to call this.
	 */
	public void run() {
		while (isRunning) {
			waitForDatagram();
			Thread.yield();
		} 
	}
	
	@SuppressWarnings("unchecked")
	// Processing can call quit() on program termination, but Java just kills the thread.
	// Obsolete, since no cleanup is really necesaary, and Java won't use this. 
	// it's here in just in case I want to go back to being a dedicated Processing-only library
	// at some point.  Uncomment should want/need to use...
	/*
	void quit() {
		System.out.println("PixelblazeEnumerator thread stopping"); 
		isRunning = false;  
		interrupt();
	}
	*/	
	
	// Check the device list for Pixelblazes that haven't been heard from in a while,
	// and remove them if necessary.
	//
	void _updatePixelblazeList(int now) {		
		Iterator<HashMap.Entry<String,Object>> iter = deviceList.entrySet().iterator();
		while (iter.hasNext()) {
		    HashMap.Entry<String,Object> pb = iter.next();
		    HashMap<String,Object> info = (HashMap<String,Object>) pb.getValue();
		    if (now - (int) info.get("timestamp") > this.deviceTimeout) {
		    	iter.remove();
		    }
		}		
	}
		
	// Does all the actual work around here...
	// Receive and process Pixelblaze datagram traffic, and manage device list.
	void waitForDatagram() { 
		ByteBuffer pkt;
		
		if (this.isRunning == true) { 
			// take lowest 32 bits of system millisecond count.
			int now = (pApp.millis() & 0xFFFFFFFF);
			
			if (now >= listTimeoutCheck) {
				// update list of current Pixelblazes
				_updatePixelblazeList(now);
				
				// schedule next list timeout check
				listTimeoutCheck = now + this.LIST_CHECK_INTERVAL;
			}

			try {
				ds.receive(datagram);
				InetAddress sender = datagram.getAddress();
				pkt = ByteBuffer.wrap(datagram.getData());
				pkt.order(ByteOrder.LITTLE_ENDIAN);
				int packetType = pkt.getInt();
				int senderID = pkt.getInt();
				int senderTime = pkt.getInt();
				
				// Do things, and other stuff.  And things!
				switch (packetType) {
				    // when we get a beacon packet from a Pixelblaze,
				    // add or update its record in our device list
					case BEACON_PACKET:
						// if enabled, send timesync packet as quickly 
						// as possible
						if (this.autoSync) {
							pkt.rewind();
							pkt.putInt(this.TIMESYNC_PACKET);
							pkt.putInt(this.syncId);
							pkt.putInt(now);
							pkt.putInt(senderID);
							pkt.putInt(senderTime);
							datagram.setAddress(sender);
							ds.send(datagram);
							// TODO - not yet implemented: timesync
						}
						// add Pixelblaze to our list of devices
						HashMap<String,Object> pb = new HashMap<String, Object>();
						pb.put("id", senderID);
						pb.put("senderTime", senderTime);
						pb.put("timestamp",now);
						deviceList.put(sender.getHostAddress(),pb);
				
						break;
					// if we get a timesync packet, that means somebody else on the 
					// network is already established as timing master. At the moment
					// we defer to all other time sources.
					case TIMESYNC_PACKET:
						if (this.autoSync == true) {
						  this.disableTimesync();	
						}
						break;
					default:
						break;
				}
			} 
			catch (IOException e) { // catch those pesky timeouts 
				return;
			}     
		}  
	}	
}
