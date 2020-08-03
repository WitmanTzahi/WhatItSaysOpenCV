package com.lbmotion.whatitsays.communication;

import android.util.Log;

import static com.lbmotion.whatitsays.communication.GetWarningsAndEventsHTTP.doGetWarningsAndEventsHTTP;

public class GetExsitingWarningHTTP extends BaseHTTP {
	public static GetExsitingWarningHTTP me = null;
	GetWarningsAndEventsHTTP 			 getWarningsAndEventsHTTP = null;

	public GetExsitingWarningHTTP() {
		TAG = "GetExsitingWarningHTTP";
	}

	public static GetExsitingWarningHTTP doLoad(String authority, String username) {
		try {
			me = new GetExsitingWarningHTTP();
			Log.i(me.TAG, "run()");
			me.getWarningsAndEventsHTTP = doGetWarningsAndEventsHTTP(authority,username);
			if (me.getWarningsAndEventsHTTP != null && me.getWarningsAndEventsHTTP.result && !me.getWarningsAndEventsHTTP.errorOccurred && !me.didAbort) {
				me.result = true;
			}
			me.getWarningsAndEventsHTTP = null;
		}
		catch(Exception e) {
			try {
				me.abort();
				me.result = false;
				me.errorOccurred = true;
			}
			catch(Exception ee) {}
		}
		return me;
	}

	protected void parseResponse(String response) {
	}

	public static void abort() {
		try {
			if(me != null)
				me.baseAbort();
			if(me.getWarningsAndEventsHTTP != null)
				me.getWarningsAndEventsHTTP.baseAbort();
		}
		catch (Exception e) {}
	}
}