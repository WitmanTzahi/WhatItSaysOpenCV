package com.lbmotion.whatitsays.communication;

import com.lbmotion.whatitsays.UCApp;
import com.lbmotion.whatitsays.data.CheckLincense;
import com.lbmotion.whatitsays.util.Util;

/**
 * Created by witman on 22/07/2017.
 */

public class LisenceNumberHTTP extends BaseHTTP {
    public static LisenceNumberHTTP     me;
    public CheckLincense checkLincense = null;

    public LisenceNumberHTTP() {
        REQUEST_TAG = "VolleyLisenceNumberHTTP";
        TAG = "LisenceNumberHTTP";
    }

    public static void abort() {
        try {
            if(me != null) {
                me.baseAbort();
            }
        }
        catch (Exception e) {}
    }

    public static LisenceNumberHTTP doLisenceNumberHTTP(String carInformationId, String secondQuery) {
        me = new LisenceNumberHTTP();
        me.doRun("N_ChkAdd_Doch_New.asp?Lk=" + UCApp.Lk +
                "&CarNo=" + me.codeData(carInformationId,true) +
                "&AveraC=" + UCApp.violation +
                "&MikumC=" + UCApp.location +
                "&StreetC=" + UCApp.streetCode +
                "&HouseNo=" + me.codeData(me.onlyNumeric(UCApp.streetNumber))+
                "&User=" +UCApp.user+
                "&ALPRCaMode=1"+
                "&ClientDate=" + me.codeBlank(Util.makeDate())+
                "&SecondQuery="+secondQuery);
        me.doWait();
        return me;
    }

    public static LisenceNumberHTTP doLisenceNumberHTTP(String carInformationId, int violationListCode, byte streetByAcrossIn, int streetCode, String streetNumber, String secondQuery) {
        me = new LisenceNumberHTTP();
        me.doRun("N_ChkAdd_Doch_New.asp?Lk=" + UCApp.loginData.Lk +
                "&CarNo=" + me.codeData(carInformationId,true) +
                "&AveraC=" + violationListCode +
                "&MikumC=" + streetByAcrossIn +
                "&StreetC=" + streetCode +
                "&HouseNo=" + me.codeData(me.onlyNumeric(streetNumber))+
                "&User=" +UCApp.loginData.UsrCounter+
                "&ClientDate=" + me.codeBlank(Util.makeDate())+
                "&SecondQuery="+secondQuery);
        me.doWait();
        return me;
    }

    protected void parseResponse(String response) {
        try {
            result = true;
            while (response.length() > 0) {
                if (response.startsWith("<row>")) {
                    checkLincense = new CheckLincense();
                    while (response.length() > 0) {
                        /**/ if (response.startsWith("</row>")) {
                            response = response.substring("</row>".length());
                            result = true;
                            break;
                        } else if (response.startsWith("<KazachRemark>")) {
                            response = response.substring("<KazachRemark>".length());
                            try {checkLincense.KazachRemark = Integer.parseInt(GetField(response));} catch (Exception e) {}
                            response = response.substring(response.indexOf("</KazachRemark>") + "</KazachRemark>".length());
                        } else if (response.startsWith("<PrintKod>")) {
                            response = response.substring("<PrintKod>".length());
                            checkLincense.PrintKod = ToHebrew(GetField(response));
                            response = response.substring(response.indexOf("</PrintKod>") + "</PrintKod>".length());
                        } else if (response.startsWith("<PrintMsg>")) {
                            response = response.substring("<PrintMsg>".length());
                            checkLincense.PrintMsg = ToHebrew(GetField(response));
                            response = response.substring(response.indexOf("</PrintMsg>") + "</PrintMsg>".length());
                        } else if (response.startsWith("<LeftSide>")) {
                            response = response.substring("<LeftSide>".length());
                            String L = ToHebrew(GetField(response).trim());
                            if (L.compareToIgnoreCase("ביטול") == 0)
                                checkLincense.leftSide = 1;
                            else if (L.compareToIgnoreCase("המשך") == 0)
                                checkLincense.leftSide = 2;
                            else if (L.compareToIgnoreCase("חזור") == 0)
                                checkLincense.leftSide = 3;
                            else if (L.compareToIgnoreCase("נסה שנית") == 0)
                                checkLincense.leftSide = 4;
                            response = response.substring(response.indexOf("</LeftSide>") + "</LeftSide>".length());
                        } else if (response.startsWith("<RightSide>")) {
                            response = response.substring("<RightSide>".length());
                            String R = ToHebrew(GetField(response).trim());
                            if (R.compareToIgnoreCase("ביטול") == 0)
                                checkLincense.rightSide = 1;
                            else if (R.compareToIgnoreCase("המשך") == 0)
                                checkLincense.rightSide = 2;
                            else if (R.compareToIgnoreCase("חזור") == 0)
                                checkLincense.rightSide = 3;
                            else if (R.compareToIgnoreCase("נסה שנית") == 0)
                                checkLincense.rightSide = 4;
                            response = response.substring(response.indexOf("</RightSide>") + "</RightSide>".length());
                        } else if (response.startsWith("<KeshelMsg>")) {
                            response = response.substring("<KeshelMsg>".length());
                            checkLincense.keshelMsg = ToHebrew(GetField(response));
                            response = response.substring(response.indexOf("</KeshelMsg>") + "</KeshelMsg>".length());
                        } else if (response.startsWith("<KeshelKod>")) {
                            response = response.substring("<KeshelKod>".length());
                            try {checkLincense.keshelKod = (short) Integer.parseInt(GetField(response));} catch (Exception e) {}
                            response = response.substring(response.indexOf("</KeshelKod>") + "</KeshelKod>".length());
                        } else if (response.startsWith("<DType>")) {
                            response = response.substring("<DType>".length());
                            try {checkLincense.type = (short) Integer.parseInt(GetField(response));} catch (Exception e) {}
                            response = response.substring(response.indexOf("</DType>") + "</DType>".length());
                        } else if (response.startsWith("<DColor>")) {
                            response = response.substring("<DColor>".length());
                            try {checkLincense.color = (short) Integer.parseInt(GetField(response));} catch (Exception e) {}
                            response = response.substring(response.indexOf("</DColor>") + "</DColor>".length());
                        } else if (response.startsWith("<DIzran>")) {
                            response = response.substring("<DIzran>".length());
                            try {checkLincense.manufacturer = (short) Integer.parseInt(GetField(response));} catch (Exception e) {}
                            response = response.substring(response.indexOf("</DIzran>") + "</DIzran>".length());
                        } else if (response.startsWith("<SwHeterHanaya>")) {
                            response = response.substring("<SwHeterHanaya>".length());
                            String SwHeterHanaya = ToHebrew(GetField(response));
                                            /**/
                            if (SwHeterHanaya.compareTo("0") == 0)
                                SwHeterHanaya = "2";
                            else if (SwHeterHanaya.compareTo("-99") == 0)
                                SwHeterHanaya = "0";
                            try {checkLincense.residentAction = (byte) Integer.parseInt(SwHeterHanaya);} catch (Exception e) {}
                            if (checkLincense.residentAction != 0)
                                checkLincense.resident = true;
                            response = response.substring(response.indexOf("</SwHeterHanaya>") + "</SwHeterHanaya>".length());
                        } else if (response.startsWith("<SwSelolar>")) {
                            response = response.substring("<SwSelolar>".length());
                            String SwSelolar = ToHebrew(GetField(response));
                                            /**/
                            if (SwSelolar.compareTo("0") == 0)
                                SwSelolar = "2";
                            else if (SwSelolar.compareTo("-99") == 0)
                                SwSelolar = "0";
                            try {checkLincense.cellularParkingAction = (byte) Integer.parseInt(SwSelolar);} catch (Exception e) {}
                            if (checkLincense.cellularParkingAction != 0)
                                checkLincense.cellularParking = true;
                            response = response.substring(response.indexOf("</SwSelolar>") + "</SwSelolar>".length());
                        } else if (response.startsWith("<SwNeche>")) {
                            response = response.substring("<SwNeche>".length());
                            String SwNeche = ToHebrew(GetField(response));
                                            /**/
                            if (SwNeche.compareTo("0") == 0)
                                SwNeche = "2";
                            else if (SwNeche.compareTo("-99") == 0)
                                SwNeche = "0";
                            try {checkLincense.handicapAction = (byte) Integer.parseInt(SwNeche);} catch (Exception e) {}
                            if (checkLincense.handicapAction != 0)
                                checkLincense.handicap = true;
                            response = response.substring(response.indexOf("</SwNeche>") + "</SwNeche>".length());
                        } else if (response.startsWith("<SwMevokash>")) {
                            response = response.substring("<SwMevokash>".length());
                            String SwMevokash = ToHebrew(GetField(response));
                                            /**/
                            if (SwMevokash.compareTo("0") == 0)
                                SwMevokash = "2";
                            else if (SwMevokash.compareTo("-99") == 0)
                                SwMevokash = "0";
                            try {checkLincense.wantedAction = (byte) Integer.parseInt(SwMevokash);} catch (Exception e) {}
                            if (checkLincense.wantedAction != 0)
                                checkLincense.wanted = true;
                            response = response.substring(response.indexOf("</SwMevokash>") + "</SwMevokash>".length());
                        } else if (response.startsWith("<SwDblReport>")) {
                            response = response.substring("<SwDblReport>".length());
                            String SwDblReport = ToHebrew(GetField(response));
                                            /**/
                            if (SwDblReport.compareTo("0") == 0)
                                SwDblReport = "2";
                            else if (SwDblReport.compareTo("-99") == 0)
                                SwDblReport = "0";
                            try {checkLincense.doubleReportingAction = (byte) Integer.parseInt(SwDblReport);} catch (Exception e) {}
                            if (checkLincense.doubleReportingAction != 0)
                                checkLincense.doubleReporting = true;
                            response = response.substring(response.indexOf("</SwDblReport>") + "</SwDblReport>".length());
                        } else if (response.startsWith("<Kod_SeloRemark>")) {
                            response = response.substring("<Kod_SeloRemark>".length());
                            try {checkLincense.Kod_SeloRemark = Integer.parseInt(GetField(response));} catch (Exception e) {}
                            response = response.substring(response.indexOf("</Kod_SeloRemark>") + "</Kod_SeloRemark>".length());
                        } else if (response.startsWith("<SugTavT>")) {
                            response = response.substring("<SugTavT>".length());
                            checkLincense.SugTavT = ToHebrew(GetField(response));
                            response = response.substring(response.indexOf("</SugTavT>") + "</SugTavT>".length());
                        } else if (response.startsWith("<AveraAct>")) {
                            response = response.substring("<AveraAct>".length());
                            checkLincense.AveraAct = ToHebrew(GetField(response));
                            response = response.substring(response.indexOf("</AveraAct>") + "</AveraAct>".length());
                        } else if (response.startsWith("<SugTav>")) {
                            response = response.substring("<SugTav>".length());
                            checkLincense.SugTav = ToHebrew(GetField(response));
                            response = response.substring(response.indexOf("</SugTav>") + "</SugTav>".length());
                        } else if (response.startsWith("<SwWarning>")) {
                            response = response.substring("<SwWarning>".length());
                            checkLincense.SwWarning = ToHebrew(GetField(response));
                            if (checkLincense.SwWarning.length() == 0)
                                checkLincense.SwWarning = "0";
                            response = response.substring(response.indexOf("</SwWarning>") + "</SwWarning>".length());
                        } else if (response.startsWith("<SwAveraKazach>")) {
                            response = response.substring("<SwAveraKazach>".length());
                            checkLincense.SwAveraKazach = ToHebrew(GetField(response));
                            if (checkLincense.SwAveraKazach.length() == 0)
                                checkLincense.SwAveraKazach = "0";
                            response = response.substring(response.indexOf("</SwAveraKazach>") + "</SwAveraKazach>".length());
                        } else if (response.startsWith("<SwKazach>")) {
                            response = response.substring("<SwKazach>".length());
                            String SwKazach = GetField(response);
                            if (SwKazach.length() == 0)
                                SwKazach = "0";
                            try {checkLincense.SwKazach = (short) Integer.parseInt(SwKazach);} catch (Exception e) {}
                            response = response.substring(response.indexOf("</SwKazach>") + "</SwKazach>".length());
                        } else if (response.startsWith("<TxtKazach>")) {
                            response = response.substring("<TxtKazach>".length());
                            checkLincense.TxtKazach = ToHebrew(GetField(response));
                            if (checkLincense.TxtKazach.length() == 0)
                                checkLincense.TxtKazach = "0";
                            response = response.substring(response.indexOf("</TxtKazach>") + "</TxtKazach>".length());
                        } else if (response.startsWith("<TxtAzara>")) {
                            response = response.substring("<TxtAzara>".length());
                            checkLincense.TxtAzara = ToHebrew(GetField(response));
                            response = response.substring(response.indexOf("</TxtAzara>") + "</TxtAzara>".length());
                        } else if (response.startsWith("<SelectPicture>")) {
                            response = response.substring("<SelectPicture>".length());
                            String doSelectPicture = ToHebrew(GetField(response));
                            if (doSelectPicture.length() == 0)
                                doSelectPicture = "0";
                            else if (doSelectPicture.length() > 1)
                                doSelectPicture = doSelectPicture.substring(0, 1);
                            try {
                                checkLincense.handicapParkingPicture = Integer.parseInt(doSelectPicture) != 0;
                            } catch (Exception e) {
                            }
                            response = response.substring(response.indexOf("</SelectPicture>") + "</SelectPicture>".length());
                        }
                        else if(response.startsWith("<SwCellParkingPaid>")) {
                            response = response.substring("<SwCellParkingPaid>".length());
                            checkLincense.SwCellParkingPaid = ToHebrew(GetField(response));
                            response = response.substring(response.indexOf("</SwCellParkingPaid>") + "</SwCellParkingPaid>".length());
                        } else {
                            response = response.substring(1);
                        }
                    }
                }
                else {
                    response = response.substring(1);
                }
            }
        }
        catch (Exception e) {
        }
    }
}
