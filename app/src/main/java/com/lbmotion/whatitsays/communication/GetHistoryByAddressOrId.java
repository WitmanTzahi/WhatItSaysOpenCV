package com.lbmotion.whatitsays.communication;

import android.util.Log;

import com.lbmotion.whatitsays.data.HistoryForAddressOrId;

import java.util.Vector;

public class GetHistoryByAddressOrId extends Base {
	private final String TAG = "GetHistoryByAddressOrId";
	public String query;
	public String parameter;
	public String username;
	public String authority;
	public static GetHistoryByAddressOrId me = null;
	public Vector<HistoryForAddressOrId> list = new Vector<>();

	public GetHistoryByAddressOrId(String authority, String userid, String query, String parameter) {
		this.query = query;
		this.parameter = parameter;
		this.username = userid;
		this.authority = authority;
	}
	
	public static GetHistoryByAddressOrId doLoad(String authority, String userid, String query, String parameter) {
		me = new GetHistoryByAddressOrId(authority,userid,query, parameter);
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
			sender("HI4"+"version"+"\f"+version+"\f"+"username"+"\f"+username+"\f"+"authority"+"\f"+authority+"\f"+"query"+"\f"+Base.codedRequest(query)+"\f"+"parameter"+"\f"+parameter+"\f");
			helperTimer.schedule(new CancelCommunicationTask(this),60000);
			readServer();
			helperTimer.cancel();
			close(true);
			if(bufferChar != null) {
				i = 0;
				if (!(bufferChar[0] == '<' && bufferChar[1] == 'E' && bufferChar[2] == 'r' && bufferChar[3] == 'r' && bufferChar[4] == '>')) {
					while (i < bufferChar.length) {
						HistoryForAddressOrId h = new HistoryForAddressOrId();
						h.ReportType = getString(readBytes(3));
						h.DDate = getString(readBytes(3));
						h.DHour = getString(readBytes(3));
						h.Report = getString(readBytes(3));
						h.TeudatZeut = getString(readBytes(3));
						h.Nm = getString(readBytes(3));
						h.NmF = getString(readBytes(3));
						h.Seif = getString(readBytes(3));
						h.AveraSug = getString(readBytes(3));
						h.AveraCode = getString(readBytes(3));
						h.FreeRemark = getString(readBytes(3));
						list.add(h);
					}
				}
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
