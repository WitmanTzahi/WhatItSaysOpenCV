package com.lbmotion.whatitsays.communication;

import android.util.Log;

public class AddPictureToHistoryDoc extends Base {
	private final String TAG = "AddPictureToHistoryDoc";
	private String fileName;
	private String dochCode;
	private String doch;
	private String dochKod;
	public String username;
	public String authority;
	public static AddPictureToHistoryDoc  me = null;

	public AddPictureToHistoryDoc(String authority, String userid, String fileName, String dochCode, String doch, String dochKod) {
		this.fileName = fileName;
		this.dochCode = dochCode;
		this.dochKod = dochKod;
		this.doch = doch;
		this.username = userid;
		this.authority = authority;
	}
	
	public static AddPictureToHistoryDoc doLoad(String authority, String userid, String fileName, String dochCode, String doch, String dochKod) {
		me = new AddPictureToHistoryDoc(authority, userid,fileName, dochCode, doch, dochKod);
		me.start();
		communicationThreads.addElement(me);
		try {
			while (!me.isDone.get()) {
				try {
                    Thread.sleep(250);} catch (InterruptedException ie) {}
			}
			communicationThreads.removeElement(me);
		}
		catch (Exception e) {}
		catch (Error e) {}
		return me;
	}
	
	public void run() {
		Log.i(TAG, "run()");
		try {
			open();
			sender("API"+"version"+"\f"+version+"\f"+"authority"+"\f"+authority+"\f"+"username"+"\f"+username+"\f"+"fileName"+"\f"+fileName+"\f"+"dochCode"+"\f"+dochCode+"\f"+"\f"+"doch"+"\f"+doch+"\f"+"dochKod"+"\f"+dochKod+"\f");
			helperTimer.schedule(new CancelCommunicationTask(this),60000);
			readServer();
			helperTimer.cancel();
			close(true);
			if(bufferChar != null) {
				if(bufferChar.length == 2 && bufferChar[0] == 'O' && bufferChar[1] == 'K')
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

	public static void abort() {
		try {
			if(me != null)
				me.doAbort();
		}
		catch (Exception e) {}
	}
}
