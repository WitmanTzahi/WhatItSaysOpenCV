package com.lbmotion.whatitsays.communication;

import android.util.Log;

public class SendLocation extends Base {
	private final static String TAG = "SendLocation";
	private double latitude,longitude;
	private String authority;
	
	public SendLocation(String authority, double latitude, double  longitude) {
		this.latitude = latitude;
		this.longitude = longitude;
		this.authority = authority;
	}

	public static SendLocation doSend(String authority, double latitude, double  longitude) {
		SendLocation me = new SendLocation(authority,latitude,longitude);
		me.start();
//		communicationThreads.addElement(me);
		while(!me.isDone.get()) {
			try {
                Thread.sleep(250);}catch (InterruptedException ie) {}
		}
//		communicationThreads.removeElement(me);
		return me;
	}

	public void run() {
		Log.i(TAG, "run()");
		try {
			openBackground();
			sender("LCN"+"version"+"\f"+version+"\f"+"authority"+"\f"+authority+"\f"+"latitude"+"\f"+latitude+"\f"+"longitude"+"\f"+longitude+"\f"+"\n");
			helperTimer.schedule(new CancelCommunicationTask(this),35000);
			readServer();
			helperTimer.cancel();
			close(true);
			if(bufferChar != null) {
				result = true;
			}
		}
		catch (Exception e) {
			errorOccurred = true;
			Log.i(TAG, "run()"+e.getMessage());
			close(false);
		}
		isDone.set(true);
	}
}
