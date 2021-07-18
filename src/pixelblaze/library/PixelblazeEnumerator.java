package pixelblaze.library;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.Arrays;

import processing.core.*;

/**
   Listens on a network to detect available Pixelblazes, which the user can then list
   or open as Pixelblaze objects.  Also provides time synchronization services for
   running synchronized patterns on a network of Pixelblazes.
 */
public class PixelblazeEnumerator extends Thread {
	final int PORT = 1889;
	final int SYNC_ID = 890;
	final int BEACON_PACKET = 42;
	final int TIMESYNC_PACKET = 43;
	final int DEVICE_TIMEOUT = 30000;
	final int LIST_CHECK_INTERVAL = 5000;

	final int listTimeoutCheck = 0;
	final boolean isRunning = false;
	final boolean autoSync = false;	
	
	//Map devices = [:];

	PApplet parent;
	DatagramSocket ds;
	DatagramPacket datagram;	

	byte[] buffer;
	byte[] sendbuf;

	boolean running;   
	int status;
	int lastActivity;
	
	PixelblazeEnumerator(PApplet parent,String hostIp) {
		this.parent = parent;
		buffer = new byte[1024];
    	datagram = new DatagramPacket(buffer, buffer.length); 
		lastActivity = parent.millis();

		try {
			InetSocketAddress listenAddr = new InetSocketAddress(hostIp,PORT);    			
			ds = new DatagramSocket(null);
			ds.setSoTimeout(0);		
			ds.bind(listenAddr);
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}
	
	// create PixelblazeEnumerator object, listen on all available
	// interfaces
	PixelblazeEnumerator(PApplet parent) {
		this(parent,new String("0.0.0.0"));	
	}
	
	PApplet getApplet() {
		return parent;
	}


	public void start() {
		System.out.println("PixelblazeEnumerator thread starting");  
		running = true;
		super.start();
	}

	public void run() {
		while (running) {
			waitForDatagram();
			Thread.yield();
		} 
	}
	
	void quit() {
		System.out.println("PixelblazeEnumerator thread stopping"); 
		running = false;  
		interrupt();
	}		

	void waitForDatagram() {    
		if (this.isRunning == true) {  
			try {
				ds.receive(datagram);
				lastActivity = parent.millis();
			} 
			catch (IOException e) { // catch those pesky timeouts 
				return;
			}     
		}  
	}	
}
