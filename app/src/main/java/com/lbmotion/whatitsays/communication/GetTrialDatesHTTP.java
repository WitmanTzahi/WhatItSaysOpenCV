package com.lbmotion.whatitsays.communication;

import com.lbmotion.whatitsays.data.DB;
import com.lbmotion.whatitsays.data.TrialDate;

/**
 * Created by witman on 22/07/2017.
 */

public class GetTrialDatesHTTP extends BaseHTTP {
    public static GetTrialDatesHTTP     me;

    public GetTrialDatesHTTP() {
        REQUEST_TAG = "VolleyGetTrialDatesHTTPHTTP";
        TAG = "GetTrialDatesHTTP";
    }

    public static void abort() {
        try {
            if(me != null) {
                me.baseAbort();
            }
        }
        catch (Exception e) {}
    }

    public static GetTrialDatesHTTP doGetTrialDatesHTTP(String username, String authority) {
        DB.trialDates.removeAllElements();
        me = new GetTrialDatesHTTP();
        me.doRun("N_GetLocationXMLTbl_Mirs.asp?"+"Odbc="+authority+"&Pakach="+username);
        me.doWait();
        return me;
    }

    protected void parseResponse(String response) {
        try {
            result = true;
            while (response.length() > 0) {
                if (response.startsWith("<row>")) {
                    response = response.substring("<row>".length());
                    TrialDate trial = new TrialDate();
                    while (response.length() > 0) {
                        /**/if (response.startsWith("</row>")) {
                            DB.trialDates.add(trial);
                            response = response.substring("</row>".length());
                            break;
                        }
                        else if (response.startsWith("<C>")) {
                            response = response.substring("<C>".length());
                            try {trial.C = (short) Integer.parseInt(GetField(response).trim().trim());} catch (Exception e) {}
                            response = response.substring(response.indexOf("</C>") + "</C>".length());
                        }
                        else if (response.startsWith("<Ddate>")) {
                            response = response.substring("<Ddate>".length()).trim();
                            trial.Date = GetField(response).trim().trim();
                            response = response.substring(response.indexOf("</Ddate>") + "</Ddate>".length());
                        }
                        else if (response.startsWith("<Hhour>")) {
                            response = response.substring("<Hhour>".length()).trim();
                            trial.Hour = GetField(response).trim();
                            response = response.substring(response.indexOf("</Hhour>") + "</Hhour>".length());
                        } else {
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
