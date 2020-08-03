package com.lbmotion.whatitsays.communication;


import com.lbmotion.whatitsays.data.DB;
import com.lbmotion.whatitsays.data.InsideCode;

/**
 * Created by witman on 22/07/2017.
 */

public class GetInsideCodesHTTP extends BaseHTTP {
    public static GetInsideCodesHTTP    me;

    public GetInsideCodesHTTP() {
        REQUEST_TAG = "VolleyGetInsideCodesHTTP";
        TAG = "GetInsideCodeHTTP";
    }

    public static void abort() {
        try {
            if(me != null) {
                me.baseAbort();
            }
        }
        catch (Exception e) {}
    }

    public static GetInsideCodesHTTP doGetInsideCodesHTTP(String username, String authority) {
        DB.insideCodes.removeAllElements();
        me = new GetInsideCodesHTTP();
        me.doRun("N_GetLocationXMLTbl_Mirs.asp?"+"Odbc="+authority+"&Pakach="+username);
        me.doWait();
        return me;
    }

    protected void parseResponse(String response) {
        result = true;
        try {
            while (response.length() > 0) {
                if (response.startsWith("<row>")) {
                    response = response.substring("<row>".length());
                    InsideCode insideCode = new InsideCode();
                    while (response.length() > 0) {
                        /**/ if (response.startsWith("</row>")) {
                            DB.insideCodes.add(insideCode);
                            response = response.substring("</row>".length());
                            break;
                        } else if (response.startsWith("<C>")) {
                            response = response.substring("<C>".length());
                            try {insideCode.C = (short) Integer.parseInt(GetField(response).trim());} catch (Exception e) {}
                            response = response.substring(response.indexOf("</C>") + "</C>".length());
                        } else if (response.startsWith("<Kod>")) {
                            response = response.substring("<Kod>".length());
                            try {insideCode.Kod = (short) Integer.parseInt(GetField(response).trim());} catch (Exception e) {}
                            response = response.substring(response.indexOf("</Kod>") + "</Kod>".length());
                        } else if (response.startsWith("<Nm>")) {
                            response = response.substring("<Nm>".length());
                            insideCode.Nm = ToHebrew(GetField(response));
                            response = response.substring(response.indexOf("</Nm>") + "</Nm>".length());
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
        if(DB.insideCodes.size() == 0) {
            InsideCode insideCode = new InsideCode();
            insideCode.Nm = "Need Value";
            DB.insideCodes.addElement(insideCode);
        }
    }
}
