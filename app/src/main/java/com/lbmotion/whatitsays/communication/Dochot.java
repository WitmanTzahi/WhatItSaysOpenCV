package com.lbmotion.whatitsays.communication;

import com.lbmotion.whatitsays.UCApp;

public class Dochot extends Base {
//	private final String 	TAG = "Dochot";
	private	static final int	GET_DOCHOT 		= 1;
	private	static final int	CREATE_PINKS 	= 2;
//
	private String userid;
	private String authority;
	private String number;
	private String series;
	private char 				notebookType;
	private String pinkasResult 	= "";
	private int 				operationType;
	
	public static Dochot 		me = null;
	
	public Dochot(String userid, String authority) {
		this.userid = userid;
		this.authority = authority;
		operationType = GET_DOCHOT;
	}

	public Dochot(String userid, String authority, String number, String series, char notebookType) {
		this.userid = userid;
		this.authority = authority;
		this.number = number;
		this.series = series;
		this.notebookType = notebookType;
		operationType = CREATE_PINKS;
	}

	public static Dochot doLoad(String userid, String authority) {
		me = new Dochot(userid,authority);
		me.start();
		communicationThreads.addElement(me);
		try {
			while(!me.isDone.get()) {
				try {
                    Thread.sleep(250);} catch (InterruptedException ie) {}
			}
			try {communicationThreads.removeElement(me);}catch (Exception e) {}
			return me;
		}
		catch(Exception e) {}
		catch (Error e) {}
		return null;
	}

	public static String doCreate(String userid, String authority, String number, String series, char notebookType) {
		try {
			int n = 0;
			try {n = Integer.parseInt(number);}catch(Exception e){}
			n = (n+24)/25;
			me = new Dochot(userid,authority,n+"",series,notebookType);
			me.start();
			communicationThreads.addElement(me);
			while(!me.isDone.get())
				try {
                    Thread.sleep(250);}catch (InterruptedException ie) {}
			communicationThreads.removeElement(me);
			return me.pinkasResult;
		}
		catch(Exception e) {}
		return "";
	}

	public void run() {
		try {
			open(6000);
			if(operationType == GET_DOCHOT)
				sender("APL"+"version"+"\f"+version+"\f"+"username"+"\f"+userid+"\f"+"authority"+"\f"+authority+"\f");
			else
				sender("APN"+"version"+"\f"+version+"\f"+"username"+"\f"+userid+"\f"+"authority"+"\f"+authority+"\f"+"series"+"\f"+series+"\f"+"number"+"\f"+number+"\f"+"notebookType"+"\f"+notebookType+"\f");
			helperTimer.schedule(new CancelCommunicationTask(this),35000);
			readServer();
			helperTimer.cancel();
			if(bufferChar != null) {
				if(operationType == GET_DOCHOT) {
					if(bufferChar.length > 0)
						UCApp.loginData.clearDochot();
					UCApp.loginData.clearDochot();
					while(i < bufferChar.length) {
						int Code = (int)readBytes(readBytes(1));
						int DochNumber = (int)readBytes(readBytes(1));
						byte Sidra = (byte)readBytes(1);
						byte Bikoret = (byte)readBytes(1);
						byte SwHazara = (byte)readBytes(1);
						byte Sug = (byte)readBytes(1);
						UCApp.loginData.addDoch(Code,DochNumber,Sidra,Bikoret,SwHazara,Sug);
					}
				}
				else {
					pinkasResult = new String(bufferChar);
				}
				result = true;
			}
			close(true);
		}
		catch (Exception e) {
			errorOccurred = true;
			close(false);
		}
		isDone.set(true);
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
