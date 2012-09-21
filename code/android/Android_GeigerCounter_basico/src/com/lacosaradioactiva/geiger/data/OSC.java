package com.lacosaradioactiva.geiger.data;

import netP5.NetAddress;
import oscP5.OscEventListener;
import oscP5.OscMessage;
import oscP5.OscP5;
import oscP5.OscStatus;
import android.util.Log;

public class OSC {

	public OSC() { 


	}

	OscP5 oscP5;
	NetAddress myRemoteLocation;

	int remotePort = 9000;

	private boolean isEnabled;

	public void connect(boolean b, String remoteIP) { 
		
		this.isEnabled = b; 
		
		/* start oscP5, listening for incoming messages at port 12000 */
		
		myRemoteLocation = new NetAddress(remoteIP, remotePort);

		oscP5 = new OscP5(this, 12001);
		oscP5.addListener(new OscEventListener() {
			
			@Override
			public void oscStatus(OscStatus arg0) {
			}
			
			@Override
			public void oscEvent(OscMessage theOscMessage) {
				if (theOscMessage.checkAddrPattern("/android") == true) {
					
					int MAX_SLIDERS = theOscMessage.get(0).intValue();
					
				}
			}
		}); 

	}

	
	public void sendParam(float var1, float var2, float var3, float var4) {
	
		Log.d("qq", "enviando"); 
		// myRemoteLocation = new NetAddress("127.0.0.1", 12001);

		/* create a new osc message object */
		OscMessage myMessage = new OscMessage("/cpm");

		myMessage.add(var1); /* add an int to the osc message */
		myMessage.add(var2); /* add an int to the osc message */
		myMessage.add(var3); /* add an int to the osc message */
		myMessage.add(var4); /* add an int to the osc message */

		/* send the message */
		try{
		oscP5.send(myMessage, myRemoteLocation);
		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.d("err","no se puede enviar");
		}
		//Log.d("qq", "enviado"); 

	}

	public void sendBang(int q) {
		
		/* create a new osc message object */
		OscMessage myMessage = new OscMessage("/playsound");

		myMessage.add(q); /* add an int to the osc message */

		/* send the message */
		oscP5.send(myMessage, myRemoteLocation);

	} 
	
	public boolean isEnabled() {
		
		return isEnabled; 
		
	}

}
