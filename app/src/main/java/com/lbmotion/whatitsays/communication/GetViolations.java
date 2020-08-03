package com.lbmotion.whatitsays.communication;

import android.util.Log;

import com.lbmotion.whatitsays.data.DB;
import com.lbmotion.whatitsays.data.Parameters;
import com.lbmotion.whatitsays.data.RecordStore;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.Date;

public class GetViolations extends Base {
	private final String TAG = "GetViolations";
	public String username;
	public String authority;
	public static GetViolations me;

	public GetViolations(String auth, String userid) {
		username = userid;
		authority = auth;
	}
	
	public void run() {
		Log.i(TAG, "run()");
		try {
			open();
			sender("Vix"+"version"+"\f"+version+"\f"+"username"+"\f"+username+"\f"+"authority"+"\f"+authority+"\f");
			helperTimer.schedule(new CancelCommunicationTask(this),120000);
			readServer();
			helperTimer.cancel();
			close(true);
			if(bufferChar != null) {
				if(getViolations(DB.allViolations,true)) {
					try {
						RecordStore.deleteRecordStore("Vio");} catch (Exception e) {}
					RecordStore recordStore = RecordStore.openRecordStore("Vio", true);
					recordStore.setNumberofRecords(1);
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					DataOutputStream os = new DataOutputStream(baos);
					DB.writeVectorViolation(os);
					byte[] b = baos.toByteArray();
					recordStore.addRecord(b, 0, b.length);
					baos.flush();
					os.flush();
					baos.close();
					os.close();
					recordStore.closeRecordStore();
					Parameters.update("timestamp2", ((new Date()).getTime()) + "");
					Parameters.update("lk", authority);
				}
			}
		}
		catch (Exception e) {
			errorOccurred = true;
			Log.i(TAG, "run()"+e.getMessage());
			close(false);
		}
		isDone.set(true);
	}

	public static GetViolations doLoadViolations(String authority, String userid) {
		me = new GetViolations(authority,userid);
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

	public static void abort() {
		try {
			if(me != null) {
				me.doAbort();
				me.errorOccurred = true;
			}
		}
		catch (Exception e) {}
	}
}
