package com.lbmotion.whatitsays.communication;

import com.lbmotion.whatitsays.data.Assignment;

/**
 * Created by witman on 21/07/2017.
 */

public class GetAssignmentsHTTP extends BaseHTTP {
	public static GetAssignmentsHTTP 	me;
	public String username;
	public String authority;

	public GetAssignmentsHTTP() {
		REQUEST_TAG = "GetAssignmentsHTTP";
		TAG = "GetAssignmentsHTTP";
	}
	public static void abort() {
		try {
			if(me != null) {
				me.baseAbort();
			}
		}
		catch (Exception e) {}
	}

	public static GetAssignmentsHTTP doLoad(String authority, String username) {
		me = new GetAssignmentsHTTP();
		me.authority = authority;
		me.username = username;
		Assignment.assignments.clear();
		me.doRun("https://ws.comax.co.il/Hanita/Parking/MobileWs.asmx/GetPakach_Tasks?LoginID=Hanita&LoginPassword=Hanita&Odbc=" + authority + "&Pakach=" + username);
		me.doWait();
		return me;
	}

	protected void parseResponse(String response) {
		try {
			result = true;
			Assignment assignment;
			while (response.length() > 0) {
				if (response.startsWith("<Pakach_Tasks>")) {
					response = response.substring("<Pakach_Tasks>".length());
					assignment = new Assignment();
					while (response.length() > 0) {
						/**/if (response.startsWith("</Pakach_Tasks>")) {
							Assignment.assignments.add(assignment);
							response = response.substring("</Pakach_Tasks>".length());
							break;
						} else if (response.startsWith("<TasksC>")) {
							response = response.substring("<TasksC>".length());
							try {assignment.TasksC = Integer.parseInt(GetField(response).trim())+"";} catch (Exception e) {}
							response = response.substring(response.indexOf("</TasksC>") + "</TasksC>".length());
						} else if (response.startsWith("<UniqueNumber>")) {
							response = response.substring("<UniqueNumber>".length());
							assignment.UniqueNumber = GetField(response).trim();
							response = response.substring(response.indexOf("</UniqueNumber>") + "</UniqueNumber>".length());
						} else if (response.startsWith("<Avera>")) {
							response = response.substring("<Avera>".length());
							assignment.Avera = ToHebrew(GetField(response));
							response = response.substring(response.indexOf("</Avera>") + "</Avera>".length());
						} else if (response.startsWith("<Street>")) {
							response = response.substring("<Street>".length());
							assignment.Street = GetField(response).trim();
							response = response.substring(response.indexOf("</Street>") + "</Street>".length());
						} else if (response.startsWith("<StreetNo>")) {
							response = response.substring("<StreetNo>".length());
							assignment.StreetNo = GetField(response).trim();
							response = response.substring(response.indexOf("</StreetNo>") + "</StreetNo>".length());
						} else if (response.startsWith("<WhereC>")) {
							response = response.substring("<WhereC>".length());
							try{assignment.WhereC = (byte) Integer.parseInt(ToHebrew(GetField(response)));} catch (Exception e) {assignment.WhereC = 0;}
							response = response.substring(response.indexOf("</WhereC>") + "</WhereC>".length());
						} else if (response.startsWith("<FreeRemark>")) {
							response = response.substring("<FreeRemark>".length());
							assignment.FreeRemark = GetField(response).trim();
							response = response.substring(response.indexOf("</FreeRemark>") + "</FreeRemark>".length());
						} else if (response.startsWith("<FromDate>")) {
							response = response.substring("<FromDate>".length());
							assignment.FromDate = GetField(response).trim();
							response = response.substring(response.indexOf("</FromDate>") + "</FromDate>".length());
						} else if (response.startsWith("<ApplicantNm>")) {
							response = response.substring("<ApplicantNm>".length());
							assignment.ApplicantNm = GetField(response).trim();
							response = response.substring(response.indexOf("</ApplicantNm>")+ "</ApplicantNm>".length());
						} else if (response.startsWith("<ApplicantTel>")) {
							response = response.substring("<ApplicantTel>".length());
							assignment.ApplicantTel = GetField(response).trim();
							response = response.substring(response.indexOf("</ApplicantTel>")+ "</ApplicantTel>".length());
						} else if (response.startsWith("<ApplicantEmail>")) {
							response = response.substring("<ApplicantEmail>".length());
							assignment.ApplicantEmail = ToHebrew(GetField(response));
							response = response.substring(response.indexOf("</ApplicantEmail>") + "</ApplicantEmail>".length());
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
		catch (Exception e) {
		}
	}
}
