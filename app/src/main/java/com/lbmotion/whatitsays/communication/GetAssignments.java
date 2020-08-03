package com.lbmotion.whatitsays.communication;

import android.util.Log;

import com.lbmotion.whatitsays.data.Assignment;

public class GetAssignments extends Base {
	private final String TAG = "GetAssignments";
	public static GetAssignments 		me = null;
	public String username;
	public String authority;

	public GetAssignments(String authority, String userid) {
		this.username = userid;
		this.authority = authority;
	}
	
	public static GetAssignments doLoad(String authority, String userid) {
		me = new GetAssignments(authority,userid);
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
			sender("ASS"+"version"+"\f"+version+"\f"+"username"+"\f"+username+"\f"+"authority"+"\f"+authority+"\f");
			helperTimer.schedule(new CancelCommunicationTask(this),60000);
			readServer();
			helperTimer.cancel();
			close(true);
			if(bufferChar != null) {
				if(!(bufferChar.length == 2 && bufferChar[0] == 'O' && bufferChar[1] == 'K')) {
					while (i < bufferChar.length) {
						Assignment assignment = new Assignment();
						assignment.TasksC = getString(readBytes(2));
						assignment.UniqueNumber = getString(readBytes(2));
						assignment.Avera = getString(readBytes(2));
						assignment.Street = getString(readBytes(2));
						try{assignment.WhereC = (byte)(Integer.parseInt(getString(readBytes(2))));}catch (Exception e) {}
						assignment.StreetNo = getString(readBytes(2));
						assignment.FreeRemark = getString(readBytes(4));
						assignment.FromDate = getString(readBytes(2));
						assignment.ApplicantNm = getString(readBytes(2));
						assignment.ApplicantTel = getString(readBytes(2));
						assignment.ApplicantEmail = getString(readBytes(2));
						Assignment.assignments.add(assignment);
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
