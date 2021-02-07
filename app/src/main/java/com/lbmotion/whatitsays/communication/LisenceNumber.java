package com.lbmotion.whatitsays.communication;

import com.lbmotion.whatitsays.data.CheckLincense;
import com.lbmotion.whatitsays.UCApp;
import com.lbmotion.whatitsays.util.Util;

import java.util.UUID;

public class LisenceNumber extends Base {
	private static final String 		TAG = "LisenceNumber";
	public 	CheckLincense 				checkLincense = null;
	private String carInformationId;
	private int							violationListCode;
	private byte						streetByAcrossIn;
	private int							streetCode;
	private String streetNumber;
	private String secondQuery;
		
	public LisenceNumber(String carInformationId, int violationListCode, byte streetByAcrossIn, int streetCode, String streetNumber, String secondQuery) {
		this.carInformationId = removeIllegalCharacters(carInformationId);
		this.violationListCode = violationListCode;
		this.streetByAcrossIn = streetByAcrossIn;
		this.streetCode = streetCode;
		this.streetNumber = removeIllegalCharacters(streetNumber);
		this.secondQuery = secondQuery;
	}

	public static LisenceNumber doCheck(String CarInformationId, String secondQuery) {
//		for(int i = 0;i < 6;i++) {
			LisenceNumber me = new LisenceNumber(CarInformationId,UCApp.violation,(byte)UCApp.location,UCApp.streetCode,UCApp.streetNumber,secondQuery);
			me.start();
			communicationThreads.addElement(me);
			try {
				while (!me.isDone.get()) {
					try {Thread.sleep(250);} catch (InterruptedException ie) {}
				}
				try{communicationThreads.removeElement(me);}catch (Exception e) {}
//				if (me.errorOccurred && !me.didAbort) {
//					try {Thread.sleep(250);} catch (InterruptedException ie) {}
//				}
//				else {
//					break;
//				}
			}
			catch (Exception e) {}
			catch (Error e) {}
//		}
		return me;
	}

	public static LisenceNumber doCheck(String CarInformationId, int violationListCode, byte streetByAcrossIn, int streetCode, String streetNumber, String secondQuery) {
//		for(int i = 0;i < 6;i++) {
		LisenceNumber me = new LisenceNumber(CarInformationId,violationListCode,streetByAcrossIn,streetCode,streetNumber,secondQuery);
			me.start();
			communicationThreads.addElement(me);
			try {
				while (!me.isDone.get()) {
					try {
                        Thread.sleep(250);} catch (InterruptedException ie) {}
				}
				try{communicationThreads.removeElement(me);}catch (Exception e) {}
//				if (me.errorOccurred && !me.didAbort) {
//					try {
//                        Thread.sleep(250);} catch (InterruptedException ie) {}
//				}
//				else {
//					break;
//				}
			}
			catch (Exception e) {}
			catch (Error e) {}
//		}
		return me;
	}

//	public static void abort() {
//		try {
//			if(me != null)
//				me.doAbort();
//		}
//		catch (Exception e) {}
//	}

	public void run() {
		try {
			open();
			char charCtreetByAcrossIn = (char)('0'+streetByAcrossIn);
			UCApp.ticketInformation.uuid = UUID.randomUUID().toString();
			sender("APT"+
					"version"+"\f"+"0.0"+"\f"+
//					"LK"+"\f"+ UCApp.loginData.Lk+"\f"+
					"LK"+"\f"+ UCApp.Lk+"\f"+
					"Registration"+"\f"+Base.codedRequest(carInformationId)+"\f"+
					"Violation"+"\f"+violationListCode+"\f"+
					"Location"+"\f"+charCtreetByAcrossIn+""+"\f"+
					"StreetCode"+"\f"+streetCode+""+"\f"+
					"Number"+"\f"+streetNumber+""+"\f"+
					"ClientDate"+"\f"+Util.makeDate()+""+"\f"+
					"UUID"+"\f"+UCApp.ticketInformation.uuid+"\f"+
//					"User"+"\f"+UCApp.loginData.UsrCounter+"\f"+
					"User"+"\f"+UCApp.user+"\f"+
					"ALPRCaMode"+"\f"+1+"\f"+
					"SecondQuery"+"\f"+secondQuery+"\f");
			helperTimer.schedule(new CancelCommunicationTask(this),25000);
			readServer();
			helperTimer.cancel();
			if(bufferChar != null) {
				checkLincense = getCheckLisenceNumber();
			}
			close(true);
		}
		catch (Exception e) {
			if(openFailed)
				errorOccurred = true;
			checkLincense = null;
			close(false);
		}
		isDone.set(true);
	}
		
	public CheckLincense getCheckLisenceNumber() {
		CheckLincense cl = new CheckLincense();
		cl.type = (short)readBytes(readBytes(1));
		cl.color = (short)readBytes(readBytes(1));
		cl.manufacturer = (short)readBytes(readBytes(1));
		int readData = readBytes(1);
		if (readData != 0) {
			cl.resident = true;
			cl.residentAction = (byte)readData;
		}
		readData = readBytes(1);
		if (readData != 0) {
			cl.cellularParking = true;
			cl.cellularParkingAction = (byte)readData;
		}
		readData = readBytes(1);
		if (readData != 0) {
			cl.handicap = true;
			cl.handicapAction = (byte)readData;
		}
		readData = readBytes(1);
		if (readData != 0) {
			cl.wanted = true;
			cl.wantedAction = (byte)readData;
		}
		readData = readBytes(1);
		if (readData != 0) {
			cl.doubleReporting = true;
			cl.doubleReportingAction = (byte)readData;
		}
		cl.Kod_SeloRemark = readBytes(readBytes(1));
		cl.SugTavT = (getString(readBytes(2))).trim();
		cl.AveraAct = (getString(readBytes(2))).trim();
		cl.SugTav = (getString(readBytes(2))).trim();
		cl.SwWarning = (getString(readBytes(2))).trim();
		cl.SwAveraKazach = (getString(readBytes(2))).trim();
		try	{cl.SwKazach = Short.parseShort(getString(readBytes(2)).trim());}catch (Exception ex){}
		cl.TxtKazach = (getString(readBytes(2))).trim();
		cl.TxtKazach = CheckLincense.reverseNumbers(cl.TxtKazach);
		cl.TxtAzara = (getString(readBytes(2))).trim();
		cl.TxtAzara = CheckLincense.reverseNumbers(cl.TxtAzara);
		cl.keshelKod = (short)readBytes(3);
		cl.keshelMsg = (getString(readBytes(2))).trim();
		cl.keshelMsg = CheckLincense.reverseNumbers(cl.keshelMsg);
		cl.rightSide = (byte)readBytes(1);
		cl.leftSide = (byte)readBytes(1);
		cl.PrintKod = (getString(readBytes(3))).trim();
		cl.PrintMsg = (getString(readBytes(3))).trim();
		cl.handicapParkingPicture = readBytes(1) != 0;
		try{cl.KazachRemark = Integer.parseInt(getString(readBytes(1)).trim());}catch (Exception e) {}
		cl.SwCellParkingPaid = (getString(readBytes(2))).trim();
		return cl;
	}
}
