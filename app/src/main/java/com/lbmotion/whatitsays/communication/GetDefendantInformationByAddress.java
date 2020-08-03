package com.lbmotion.whatitsays.communication;

import com.lbmotion.whatitsays.UCApp;
import com.lbmotion.whatitsays.data.DB;

/**
 * Created by witman on 22/07/2017.
 */

public class GetDefendantInformationByAddress extends BaseHTTP {
    public static GetDefendantInformationByAddress me;

    public GetDefendantInformationByAddress() {
        REQUEST_TAG = "VolleyGetDefInfoByAddress";
        TAG = "GetDefInfoByAddress";
    }

    public static void abort() {
        try {
            if(me != null) {
                me.baseAbort();
            }
        }
        catch (Exception e) {}
    }

    public static GetDefendantInformationByAddress doGetDefendantInformationByAddress(String Lk, int AveraC, int StreetC, String HouseNo) {
        DB.defendantList.removeAllElements();
        me = new GetDefendantInformationByAddress();
        String url = "N_ChkAdd_Doch_New.asp?"+
                "Lk=" + Lk +
                "&AveraC=" + AveraC +
                "&StreetC=" + StreetC +
                "&HouseNo=" + me.onlyNumeric(HouseNo) +
                "&User"+ UCApp.loginData.UsrCounter+
                "&Version=1";
        me.doRun(me.codedURL(url));
        me.doWait();
        return me;
    }

    protected void parseResponse(String response) {
        result = true;
        try {
            getDefendants(response, DB.defendantList);
        }
        catch (Exception e) {
        }
    }
}
