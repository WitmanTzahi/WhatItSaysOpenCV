package com.lbmotion.whatitsays.communication;

import android.util.Log;


import com.lbmotion.whatitsays.UCApp;
import com.lbmotion.whatitsays.data.LoginData;

import java.util.Date;

public class DoLogin extends Base {
	private final String TAG = "DoLogin";
	public String username;
	public String password;
	public String authority;
	public LoginData loginData = null;
	public static DoLogin 	me;
	public static boolean didDoLoginDone = false;

	public DoLogin() {
	}

	public void run() {
		Log.i(TAG, "run()");
		try {
			loginData = new LoginData();
			open();
			sender("ALE"+"version"+"\f"+version+"\f"+"username"+"\f"+username+"\f"+"password"+"\f"+password+"\f"+"authority"+"\f"+authority+"\f"+"time"+"\f"+(new Date()).getTime()+"\f");
			helperTimer.schedule(new CancelCommunicationTask(this),35000);
			readServer();
			helperTimer.cancel();
			close(true);
			loginData.timeError = false;
			if(bufferChar != null) {
				if(bufferChar.length == 1 && bufferChar[0] == '1') {
					loginData.error = true;					
				}
				else if(bufferChar.length == 1 && bufferChar[0] == '2') {
					loginData.timeError = true;
					loginData.error = true;
				}
				else {
					loginData.username = username;
					loginData.password = password;
					loginData.authority = authority;
					loginData.Lk = (short)readBytes(readBytes(1));
					loginData.UsrCounter = readBytes(readBytes(1));
					loginData.PakachC = readBytes(readBytes(1));
					loginData.PakachKod = readBytes(readBytes(1));
					loginData.DefaultES = loginData.DefaultE = (short)readBytes(readBytes(1));
					loginData.DochPanui = readBytes(readBytes(1));
					loginData.DochToDay = readBytes(readBytes(1));
					loginData.reload = readBytes(readBytes(1));
					loginData.CarNoTypeNo = readBytes(readBytes(1));
					loginData.login = readBytes(readBytes(1));
					loginData.PakachNm = getString(readBytes(4));
					loginData.ServerDate = getString(readBytes(4));
					loginData.typePakach = (byte)(getCharacter()-'0');
					loginData.EzorWork = getString(readBytes(4));
					UCApp.shiftHoursLength = readBytes(readBytes(1));
					loginData.SwNotActive = getString(readBytes(1));
					loginData.AbsenceRason = getString(readBytes(4));
					loginData.ALPR = readBytes(1) == 1;
					loginData.reloadData = (getCharacter() == '0');
					loginData.error = false;
					result = true;
				}
			}
		}
		catch (Exception e) {
			errorOccurred = true;
			Log.i(TAG, "run()"+e.getMessage());
			close(false);
		}
		loginData.openFailed = openFailed;
		didDoLoginDone = true;
		isDone.set(true);
	}

	public static void abort() {
		try {
			if(me != null) {
				me.errorOccurred = true;
				me.doAbort();
				me.loginData.openFailed = true;
			}
		}
		catch (Exception e) {}
	}

	public static DoLogin doLogin(String username, String password, String authority) {
		try {
			me = new DoLogin();
			me.username = username;
			me.password = password;
			me.authority = authority;		
			me.start();
			communicationThreads.addElement(me);
			while(!me.isDone.get()) {
				try {
                    Thread.sleep(100);}catch (InterruptedException ie) {}
			}
			communicationThreads.removeElement(me);
		}
		catch(Exception e) {
			try {
				me.result = false;
				me.errorOccurred = true;
				me.loginData = null;
			}
			catch(Exception ee) {}
		}
		return me;
	}
}
