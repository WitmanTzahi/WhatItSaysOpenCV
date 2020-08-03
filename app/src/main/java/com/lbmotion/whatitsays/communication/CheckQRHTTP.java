package com.lbmotion.whatitsays.communication;

import com.lbmotion.whatitsays.UCApp;

/**
 * Created by witman on 22/07/2017.
 */

public class CheckQRHTTP extends BaseHTTP {
    public static CheckQRHTTP           me;
    public 	CheckQRData 				checkQRData = null;


    public CheckQRHTTP() {
        REQUEST_TAG = "VolleyCheckQRHTTP";
        TAG = "CheckQRHTTP";
    }

    public static void abort() {
        try {
            if(me != null) {
                me.baseAbort();
            }
        }
        catch (Exception e) {}
    }

    public static CheckQRHTTP doLisenceNumberHTTP(String doc) {
        me = new CheckQRHTTP();
        me.doRun("https://www.comax.co.il/Max2000MobileCE/Hanita/Mirs/N_Checking_Free_Report.asp?Odbc=" + UCApp.loginData.Lk + "&ReportNumber=" + doc);
        me.doWait();
        return me;
    }

    protected void parseResponse(String response) {
        try {
            result = true;
            if (response.length() > 0) {
                checkQRData = new CheckQRData();
                if (response.startsWith("<SwExist>")) {
                    String res = response.substring("<SwExist>".length());
                    if ((GetField(res).trim()).charAt(0) == '0')
                        checkQRData.avaiable = true;
                    else
                        checkQRData.avaiable = false;
                }
                if (response.startsWith("<ReportC>")) {
                    String res = response.substring("<ReportC>".length());
                    checkQRData.ReportC = GetField(res).trim();
                }
            }
        }
        catch (Exception e) {
        }
    }
}
