package com.lbmotion.whatitsays.communication;

import android.util.Log;

import com.lbmotion.whatitsays.data.DB;
import com.lbmotion.whatitsays.data.Street;

import java.text.SimpleDateFormat;
import java.util.Date;

public class LoadStreetsHTTP extends BaseHTTP {
	public static LoadStreetsHTTP 		me;

	public LoadStreetsHTTP() {
		REQUEST_TAG = "VolleyLoadStreetsHTTP";
		TAG = "LoadStreetsHTTP";
	}

	public static void abort() {
		try {
			if(me != null) {
				me.baseAbort();
			}
		}
		catch (Exception e) {}
	}

	public static LoadStreetsHTTP doLoadStreetsHTTP(String username, String authority, boolean wait) {
		DB.streets.removeAllElements();
		me = new LoadStreetsHTTP();
		me.doRun("N_GetStreetXMLTbl1_Mirs.asp?"+ "Odbc="+ authority + "&Pakach=" + username);
		if(wait)
			me.doWait();
		return me;
	}

	protected void parseResponse(String response) {
		try {
			result = true;
			while (response.length() > 0) {
				if (response.startsWith("<row>")) {
					response = response.substring("<row>".length());
					Street street = new Street();
					while (response.length() > 0) {
						/**/if (response.startsWith("</row>")) {
							DB.streets.addElement(street);
							response = response.substring("</row>".length());
							break;
						}
						else if (response.startsWith("<CS>")) {
							response = response.substring("<CS>".length());
							try {street.CS = Integer.parseInt(GetField(response).trim());} catch (Exception e) {}
							response = response.substring(response.indexOf("</CS>") + "</CS>".length());
						} else if (response.startsWith("<TokefAtraa>")) {
							response = response.substring("<TokefAtraa>".length());
							SimpleDateFormat formater = new SimpleDateFormat("dd/MM/yyyy");
							try {
								Date date = formater.parse(GetField(response).trim());
								street.date = date.getTime();
							} catch (Exception e) {
								// frame.AddLine("Error in dates...." + tmp);
							}
							response = response.substring(response.indexOf("</TokefAtraa>") + "</TokefAtraa>".length());
						} else if (response.startsWith("<NPFNZ>")) {
							response = response.substring("<NPFNZ>".length());
							try {street.NPFNZ = (short) Integer.parseInt(GetField(response).trim());} catch (Exception e) {}
							response = response.substring(response.indexOf("</NPFNZ>") + "</NPFNZ>".length());
						} else if (response.startsWith("<NPTNZ>")) {
							response = response.substring("<NPTNZ>".length());
							try {street.NPTNZ = (short) Integer.parseInt(GetField(response).trim());} catch (Exception e) {}
							response = response.substring(response.indexOf("</NPTNZ>") + "</NPTNZ>".length());
						} else if (response.startsWith("<NPFZ>")) {
							response = response.substring("<NPFZ>".length());
							try {street.NPFZ = (short) Integer.parseInt(GetField(response).trim());} catch (Exception e) {}
							response = response.substring(response.indexOf("</NPFZ>") + "</NPFZ>".length());
						} else if (response.startsWith("<NPTZ>")) {
							response = response.substring("<NPTZ>".length());
							try {street.NPTZ = (short) Integer.parseInt(GetField(response).trim());} catch (Exception e) {}
							response = response.substring(response.indexOf("</NPTZ>") + "</NPTZ>".length());
						} else if (response.startsWith("<Ezor>")) {
							response = response.substring("<Ezor>".length());
							try {street.Ezor = (short) Integer.parseInt(GetField(response).trim());} catch (Exception e) {}
							response = response.substring(response.indexOf("</Ezor>") + "</Ezor>".length());
						} else if (response.startsWith("<SwHatraa>")) {
							response = response.substring("<SwHatraa>".length());
							try {street.SwHatraa = Integer.parseInt(GetField(response).trim()) != 0;} catch (Exception e) {}
							response = response.substring(response.indexOf("</SwHatraa>") + "</SwHatraa>".length());
						} else if (response.startsWith("<C>")) {
							response = response.substring("<C>".length());
							try {street.code = Integer.parseInt(GetField(response).trim());} catch (Exception e) {}
							response = response.substring(response.indexOf("</C>") + "</C>".length());
						} else if (response.startsWith("<Kod>")) {
							response = response.substring("<Kod>".length());
							try {street.Kod = (short) Integer.parseInt(GetField(response).trim());} catch (Exception e) {}
							response = response.substring(response.indexOf("</Kod>") + "</Kod>".length());
						} else if (response.startsWith("<Nm>")) {
							response = response.substring("<Nm>".length());
							street.Nm = ToHebrew(GetField(response));
							response = response.substring(response.indexOf("</Nm>") + "</Nm>".length());
						} else {
							response = response.substring(1);
						}
					}
				} else {
					response = response.substring(1);
				}
			}
			Log.i(TAG, "run() parse OK");
		}
		catch (Exception e) {
			Log.i(TAG, "run() ERROR:"+e.getMessage());
			errorOccurred = true;
			result = false;
		}
	}
}
