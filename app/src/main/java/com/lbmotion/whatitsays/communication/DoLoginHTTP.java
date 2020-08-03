package com.lbmotion.whatitsays.communication;

import com.lbmotion.whatitsays.UCApp;
import com.lbmotion.whatitsays.data.LoginData;

public class DoLoginHTTP extends BaseHTTP {
	public String username;
	public String password;
	public String authority;
	public LoginData loginData = null;
	public static DoLoginHTTP 			me;
	public static boolean				didDoLoginDown = false;

	public DoLoginHTTP(String username, String password, String authority) {
		REQUEST_TAG = "VolleyDoLoginHTTP";
		TAG = "DoLoginHTTP";
		this.password = password;
		this.username = username;
		this.authority = authority;
	}

	public static void abort() {
		try {
			if(me != null) {
				me.baseAbort();
				if(me.loginData == null)
					me.loginData = new LoginData();
				me.loginData.openFailed = true;
			}
		}
		catch (Exception e) {}
	}

	public static DoLoginHTTP doLogin(String username, String password, String authority) {
		me = new DoLoginHTTP(username, password, authority);
		me.loginData = new LoginData();
		me.doRun("N_ChkPassHanita.asp?"+
				"Lk=" + me.authority+
				"&User="+me.username+
				"&Password="+me.password);
		me.doWait();
		didDoLoginDown = true;
		if(me.loginData == null) {
			me.loginData = new LoginData();
			me.loginData.openFailed = true;
		}
		else {
			me.loginData.openFailed = me.openFailed;
		}
		return me;
	}

	protected void parseResponse(String response) {
		try {
			loginData.username = username;
			loginData.password = password;
			loginData.authority = authority;
			while (response.length() > 0) {
				if (response.startsWith("<row>")) {
					response = response.substring("<row>".length());
					String Err = "";
					String tmp;
					while (response.length() > 0) {
						/**/if (response.startsWith("</row>")) {
							if (Err.trim().length() == 0) {
								loginData.login = 1;
								loginData.reloadData = false;
								loginData.error = false;
								result = true;
								loginData.timeError = false;
							}
							break;
						}
						else if(response.startsWith("<EzorWork>")) {
							response = response.substring("<EzorWork>".length());
							loginData.EzorWork = ToHebrew(GetField(response));
							response = response.substring(response.indexOf("</EzorWork>")+"</EzorWork>".length());
						}
						else if(response.startsWith("<End_Shift>")) {
							response = response.substring("<End_Shift>".length());
							try {
								UCApp.shiftHoursLength = (short) Integer.parseInt(GetField(response).trim());
							}
							catch (Exception e) {
								UCApp.shiftHoursLength = 1440;
							}
							if(UCApp.shiftHoursLength == 0)
								UCApp.shiftHoursLength = 1440;
							response = response.substring(response.indexOf("</End_Shift>")+"</End_Shift>".length());
						}
						else if (response.startsWith("<Lk>")) {
							response = response.substring("<Lk>".length());
							tmp = GetField(response);
							try {
								loginData.Lk = (short) Integer.parseInt(tmp.trim());
							}
							catch (Exception e) {}
							response = response.substring(response.indexOf("</Lk>")+"</Lk>".length());
						}
						else if (response.startsWith("<UserCounter>")) {
							response = response.substring("<UserCounter>".length());
							tmp = GetField(response);
							try {
								loginData.UsrCounter = (short) Integer.parseInt(tmp.trim());
							}
							catch (Exception e) {}
							response = response.substring(response.indexOf("</UserCounter>")+"</UserCounter>".length());
						}
						else if (response.startsWith("<PakachC>")) {
							response = response.substring("<PakachC>".length());
							tmp = GetField(response);
							try {
								loginData.PakachC = (short) Integer.parseInt(tmp.trim());
							}
							catch (Exception e) {}
							response = response.substring(response.indexOf("</PakachC>")+"</PakachC>".length());
						}
						else if (response.startsWith("<PakachKod>")) {
							response = response.substring("<PakachKod>".length());
							tmp = GetField(response);
							try {
								loginData.PakachKod = (short) Integer.parseInt(tmp.trim());
							}
							catch (Exception e) {}
							response = response.substring(response.indexOf("</PakachKod>")+ "</PakachKod>".length());
						}
						else if (response.startsWith("<Mursh>")) {
							response = response.substring("<Mursh>".length());
							tmp = GetField(response);
							try {
								loginData.typePakach = (byte) Integer.parseInt(tmp.trim());
							}
							catch (Exception e) {
								loginData.typePakach = 0;
							}
							response = response.substring(response.indexOf("</Mursh>")+ "</Mursh>".length());
						}
						else if (response.startsWith("<DefaultE>")) {
							response = response.substring("<DefaultE>".length());
							tmp = GetField(response);
							try {
								loginData.DefaultE = (short) Integer.parseInt(tmp.trim());
							}
							catch (Exception e) {
								loginData.DefaultE = -1;
							}
							loginData.DefaultES= loginData.DefaultE;
							response = response.substring(response.indexOf("</DefaultE>")+ "</DefaultE>".length());
						}
						else if (response.startsWith("<DochPanui>")) {
							response = response.substring("<DochPanui>".length());
							tmp = GetField(response);
							try {
								loginData.DochPanui = (short) Integer.parseInt(tmp.trim());
							}
							catch (Exception e) {}
							response = response.substring(response.indexOf("</DochPanui>")+ "</DochPanui>".length());
						}
						else if (response.startsWith("<DochToDay>")) {
							response = response.substring("<DochToDay>".length());
							tmp = GetField(response);
							try {
								loginData.DochToDay = (short) Integer.parseInt(tmp.trim());
							}
							catch (Exception e) {}
							response = response.substring(response.indexOf("</DochToDay>")+ "</DochToDay>".length());
						}
						else if (response.startsWith("<CarNoTypeNo>")) {
							response = response.substring("<CarNoTypeNo>".length());
							tmp = GetField(response);
							try {
								loginData.CarNoTypeNo = (short) Integer.parseInt(tmp.trim());
							}
							catch (Exception e) {}
							response = response.substring(response.indexOf("</CarNoTypeNo>")+ "</CarNoTypeNo>".length());
						}
						else if (response.startsWith("<Err>")) {
							response = response.substring("<Err>".length());
							Err = GetField(response);
							response = response.substring(response.indexOf("</Err>")+"</Err>".length());
						}
						else if (response.startsWith("<UserName>")) {
							response = response.substring("<UserName>".length());
							loginData.PakachNm = ToHebrew(GetField(response));
							response = response.substring(response.indexOf("</UserName>")+"</UserName>".length());
						}
						else if (response.startsWith("<ServerDate>")) {
							response = response.substring("<ServerDate>".length());
							loginData.ServerDate = ToHebrew(GetField(response));
							response = response.substring(response.indexOf("</ServerDate>")+"</ServerDate>".length());
						}
						else if(response.startsWith("<SwNotActive>")) {
							response = response.substring("<SwNotActive>".length());
							loginData.SwNotActive = ToHebrew(GetField(response));
							response = response.substring(response.indexOf("</SwNotActive>")+"</SwNotActive>".length());
						}
						else if(response.startsWith("<AbsenceRason>")) {
							response = response.substring("<AbsenceRason>".length());
							loginData.AbsenceRason = ToHebrew(GetField(response));
							response = response.substring(response.indexOf("</AbsenceRason>")+"</AbsenceRason>".length());
						}
						else if(response.startsWith("<ALPR>")) {
							response = response.substring("<ALPR>".length());
							tmp = GetField(response).trim();
							if (tmp.length() == 0) // if the parameter field is empty
								tmp = "1";
							try {
								if(Integer.parseInt(tmp) != 0)
									loginData.ALPR = true;
								else
									loginData.ALPR = false;
							}
							catch(Exception e) {}
							response = response.substring(response.indexOf("</ALPR>")+"</ALPR>".length());
						}
						else {
							response = response.substring(1);
						}
					}
				} else {
					response = response.substring(1);
				}
			}
		}
		catch (Exception e) {}
	}
}
