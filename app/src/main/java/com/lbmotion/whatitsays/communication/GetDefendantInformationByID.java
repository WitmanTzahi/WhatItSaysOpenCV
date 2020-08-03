package com.lbmotion.whatitsays.communication;

import com.lbmotion.whatitsays.UCApp;
import com.lbmotion.whatitsays.data.DB;
import com.lbmotion.whatitsays.data.Defendant;

import java.util.Vector;

/**
 * Created by witman on 22/07/2017.
 */

public class GetDefendantInformationByID extends BaseHTTP {
    public static GetDefendantInformationByID me;

    public GetDefendantInformationByID() {
        REQUEST_TAG = "VolleyDefendantInformationHTTP";
        TAG = "GetDefendantInformation";
    }

    public static void abort() {
        try {
            if(me != null) {
                me.baseAbort();
            }
        }
        catch (Exception e) {}
    }

    public static GetDefendantInformationByID doGetDefendantInformationByID(String Lk, int AveraC, int StreetC, String HouseNo, String TeudatZeut, int type) {
        me = new GetDefendantInformationByID();
        DB.defendant = new Defendant();
        String url = "N_ChkAdd_Doch_New.asp?Lk="+me.codeData(Lk) +
                "&AveraC="+ AveraC +
                "&TeudatZeut=" + me.codeData(TeudatZeut) +
                "&Version=1" +
                "&Type="+type +
                "&StreetC=" + StreetC +
                "&User"+ UCApp.loginData.UsrCounter+
                "&HouseNo=" + me.codeData(me.onlyNumeric(HouseNo));
        me.doRun(me.codedURL(url));
        me.doWait();
        return me;
    }

    protected void parseResponse(String response) {
        result = true;
        try {
            Vector<Defendant> defendantList = new Vector<Defendant>();
            getDefendants(response,defendantList);
            if(defendantList.size() > 0)
                DB.defendant = defendantList.get(0);

        }
        catch (Exception e) {
        }
    }
}
