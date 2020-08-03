package com.lbmotion.whatitsays.communication;

import com.lbmotion.whatitsays.UCApp;

public class CheckQR extends Base {
	private static final String TAG = "CheckQR";
	private static CheckQR 				me = null;
	private String doc;
	public 	CheckQRData 				checkQRData = null;

	public CheckQR(String doc) {
		this.doc = doc;
	}

	public static CheckQR doCheck(String doc) {
		for(int i = 0;i < 6;i++) {
			me = new CheckQR(doc);
			me.start();
			communicationThreads.addElement(me);
			try {
				while (!me.isDone.get()) {
					try {
                        Thread.sleep(250);} catch (InterruptedException ie) {}
				}
				try{communicationThreads.removeElement(me);}catch (Exception e) {}
				if (me.errorOccurred && !me.didAbort) {
					try {
                        Thread.sleep(250);} catch (InterruptedException ie) {}
				}
				else {
					break;
				}
			}
			catch (Exception e) {}
			catch (Error e) {}
		}
		return me;
	}

	public static void abort() {
		try {
			if(me != null)
				me.doAbort();
		}
		catch (Exception e) {}
	}

	public void run() {
		try {
			open();
			sender("QRQ"+
					"version"+"\f"+version+"\f"+
					"LK"+"\f"+ UCApp.loginData.Lk+"\f"+
					"User"+"\f"+UCApp.loginData.UsrCounter+"\f"+
					"Doc"+"\f"+doc+"\f");
			helperTimer.schedule(new CancelCommunicationTask(this),10000);
			readServer();
			helperTimer.cancel();
			if(bufferChar != null && bufferChar.length > 0) {
				checkQRData = new CheckQRData();
				checkQRData.avaiable = bufferChar[0] == '1';
				checkQRData.ReportC = new String(bufferChar).substring(1);
			}
			close(true);
		}
		catch (Exception e) {
			if(openFailed)
				errorOccurred = true;
			close(false);
		}
		isDone.set(true);
	}
}
