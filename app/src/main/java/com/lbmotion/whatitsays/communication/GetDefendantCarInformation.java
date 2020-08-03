package com.lbmotion.whatitsays.communication;


import com.lbmotion.whatitsays.UCApp;
import com.lbmotion.whatitsays.data.DB;
import com.lbmotion.whatitsays.data.Vehicle;

/**
 * Created by witman on 22/07/2017.
 */

public class GetDefendantCarInformation extends BaseHTTP {
    public static GetDefendantCarInformation    me;
    private String CarNo;

    public GetDefendantCarInformation(String CarNo) {
        REQUEST_TAG = "VolleyGetDefendantCarInformation";
        TAG = "GetDefendantCarInfo";
        this.CarNo 		 = CarNo;
    }

    public static void abort() {
        try {
            if(me != null) {
                me.baseAbort();
            }
        }
        catch (Exception e) {}
    }

    public static GetDefendantCarInformation doGetDefendantCarInformation(String Lk, String CarNo, int AveraC, byte MikumC, int StreetC, String HouseNo) {
        me = new GetDefendantCarInformation(CarNo);
        DB.car = new Vehicle();
        String url = "N_ChkAdd_Doch_New.asp?" +
                "Lk=" + Lk +
                "&CarNo=" + me.codeData(CarNo,true) +
                "&AveraC="+ AveraC +
                "&StreetC=" + StreetC +
                "&User"+ UCApp.loginData.UsrCounter+
                "&HouseNo=" + me.onlyNumeric(HouseNo);
        url = me.codedURL(url);
        me.doRun(url);
        me.doWait();
        return me;
    }

    protected void parseResponse(String response) {
        result = true;
        try {
            while (response.length() > 0) {
                if (response.startsWith("<row>")) {
                    String DIzran = "",DType="",DColor="",TxtPopUp = "", KeshelKod = "",KeshelMsg = "",RightSide = "",LeftSide = "";
                    response = response.substring("<row>".length());
                    while (response.length() > 0) {
                        if (response.startsWith("</row>")) {
                            DB.car = new Vehicle(CarNo, DType, DColor, DIzran,TxtPopUp,KeshelKod, KeshelMsg, RightSide, LeftSide);
                            break;
                        }
                        else if (response.startsWith("<LeftSide>")) {
                            response = response.substring("<LeftSide>".length());
                            LeftSide = ToHebrew(GetField(response));
                            response = response.substring(response.indexOf("</LeftSide>")+"</LeftSide>".length());
                        }
                        else if (response.startsWith("<RightSide>")) {
                            response = response.substring("<RightSide>".length());
                            RightSide = ToHebrew(GetField(response));
                            response = response.substring(response.indexOf("</RightSide>")+"</RightSide>".length());
                        }
                        else if (response.startsWith("<KeshelMsg>")) {
                            response = response.substring("<KeshelMsg>".length());
                            KeshelMsg = ToHebrew(GetField(response));
                            response = response.substring(response.indexOf("</KeshelMsg>")+"</KeshelMsg>".length());
                        }
                        else if (response.startsWith("<KeshelKod>")) {
                            response = response.substring("<KeshelKod>".length());
                            KeshelKod = ToHebrew(GetField(response));
                            response = response.substring(response.indexOf("</KeshelKod>")+"</KeshelKod>".length());
                        }
                        else if (response.startsWith("<TxtPopUp>")) {
                            response = response.substring("<TxtPopUp>".length());
                            TxtPopUp = ToHebrew(GetField(response));
                            response = response.substring(response.indexOf("</TxtPopUp>")+"</TxtPopUp>".length());
                        }
                        else if (response.startsWith("<DType>")) {
                            response = response.substring("<DType>".length());
                            DType = ToHebrew(GetField(response));
                            response = response.substring(response.indexOf("</DType>")+ "</DType>".length());
                        }
                        else if (response.startsWith ("<DColor>")){
                            response = response.substring("<DColor>".length());
                            DColor = ToHebrew(GetField(response));
                            response = response.substring (response.indexOf("</DColor>")+ "</DColor>".length());
                        }
                        else if (response.startsWith ("<DIzran>")) {
                            response = response.substring("<DIzran>".length());
                            DIzran = ToHebrew(GetField(response));
                            response = response.substring(response.indexOf("</DIzran>") + "</DIzran>".length());
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
