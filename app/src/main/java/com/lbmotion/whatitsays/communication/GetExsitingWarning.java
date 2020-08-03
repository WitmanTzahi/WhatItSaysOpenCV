package com.lbmotion.whatitsays.communication;

import android.util.Log;

import com.lbmotion.whatitsays.data.DB;
import com.lbmotion.whatitsays.data.ExistingWarning;

import java.util.Vector;

public class GetExsitingWarning extends Base {
	private final String TAG = "GetExsitingWarning";
	public String username;
	public String authority;
	public static GetExsitingWarning 	me = null;
	
	public GetExsitingWarning(String authority, String userid) {
		this.username = userid;
		this.authority = authority;
	}
	
	public static GetExsitingWarning doLoad(String authority, String userid) {
		me = new GetExsitingWarning(authority,userid);
		me.start();
		communicationThreads.addElement(me);
		try {
			while (!me.isDone.get()) {
				try {
                    Thread.sleep(250);} catch (InterruptedException ie) {}
			}
			try {communicationThreads.removeElement(me);} catch (Exception e) {}
		}
		catch (Exception e) {}
		catch (Error e) {}
		return me;
	}
	
	public void run() {
		Log.i(TAG, "run()");
		try {
			open();
			sender("JEH"+"version"+"\f"+version+"\f"+"username"+"\f"+username+"\f"+"authority"+"\f"+authority+"\f");
			helperTimer.schedule(new CancelCommunicationTask(this),60000);
			readServer();
			helperTimer.cancel();
			close(true);
			if(bufferChar != null) {
				if(!(bufferChar.length == 2 && bufferChar[0] == 'O' && bufferChar[1] == 'K')) 
					getExistingWarnings(DB.existingWarnings,DB.pastToHandleEvents);
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
	
	/** Extracts previously given warnings from the buffer.
	 *  Creates a vector containing the warnings. Each element of it is a warning.
	 */
	private void getExistingWarnings(Vector<ExistingWarning> v1, Vector<ExistingWarning> v2) {
		v1.removeAllElements();
		v2.removeAllElements();
		while (i < bufferChar.length) {
			addExistingWarning(v1,v2);
		}
	}
	
	public static void abort() {
		try {
			if(me != null)
				me.doAbort();
		}
		catch (Exception e) {}
	}
}
