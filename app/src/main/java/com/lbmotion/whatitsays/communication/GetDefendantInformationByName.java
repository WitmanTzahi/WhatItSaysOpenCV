package com.lbmotion.whatitsays.communication;

import com.lbmotion.whatitsays.UCApp;
import com.lbmotion.whatitsays.data.DB;

/**
 * Created by witman on 22/07/2017.
 */

public class GetDefendantInformationByName extends BaseHTTP {
    public static GetDefendantInformationByName me;

    public GetDefendantInformationByName() {
        REQUEST_TAG = "VolleyDefInfoName";
        TAG = "GetDefendantInfoByName";
    }

    public static void abort() {
        try {
            if(me != null) {
                me.baseAbort();
            }
        }
        catch (Exception e) {}
    }

    public static GetDefendantInformationByName doGetDefendantInformationByName(String Lk, int AveraC, String First, String Last, String companyName, int type, String StreetC, String HouseNo) {
        DB.defendantList.removeAllElements();
        me = new GetDefendantInformationByName();
        if(type == 3) {
            First = companyName;
            Last = "";
        }
        String url = "N_ChkAdd_Doch_New.asp?Lk="+Lk
                + "&AveraC="+ AveraC
                + "&LastName=" + me.codeData(Last)
                + "&Version=1"
                + "&StreetC=" + StreetC
                + "&HouseNo=" + me.onlyNumeric(HouseNo)
                + "&Type="+type
                + "&User"+ UCApp.loginData.UsrCounter
                + "&FirstName=" + me.codeData(First);
        me.doRun(me.codedURL(url));
        me.doWait();

        return me;
    }

    protected void parseResponse(String response) {
        result = true;
        try {
            getDefendants(response,DB.defendantList);
        }
        catch (Exception e) {
        }
    }
}
