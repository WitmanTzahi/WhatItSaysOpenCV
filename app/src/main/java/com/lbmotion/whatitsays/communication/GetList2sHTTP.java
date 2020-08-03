package com.lbmotion.whatitsays.communication;

import com.lbmotion.whatitsays.data.List2s;

import java.util.Vector;

/**
 * Created by witman on 22/07/2017.
 */

public class GetList2sHTTP extends BaseHTTP {
    public static GetList2sHTTP         me;
    public Vector<List2s> list;

    //"VolleyGetList2sHTTP";
    //"N_GetWardensRemarksCXMLTbl_Mirs.asp?"//DB.remarksToViolations

    public GetList2sHTTP(String tag, Vector<List2s> list) {
        REQUEST_TAG = tag;
        TAG = "List2sHTTP";
        this.list = list;
    }

    public static void abort() {
        try {
            if(me != null) {
                me.baseAbort();
            }
        }
        catch (Exception e) {}
    }

    public static GetList2sHTTP doGetList2sHTTP(String tag, String command, String username, String authority, Vector<List2s> list) {
        list.removeAllElements();
        me = new GetList2sHTTP(tag,list);
        me.doRun(command+"Odbc="+authority+"&Pakach="+username);
        me.doWait();
        return me;
    }

    protected void parseResponse(String response) {
        try {
            result = true;
            while (response.length() > 0) {
                if (response.startsWith("<row>")) {
                    response = response.substring("<row>".length());
                    List2s List2s = new List2s();
                    while (response.length() > 0) {
                        /**/if (response.startsWith("</row>")) {
                            list.add(List2s);
                            response = response.substring("</row>".length());
                            break;
                        } else if (response.startsWith("<C>")) {
                            response = response.substring("<C>".length());
                            try {List2s.code = (short) Integer.parseInt(GetField(response));} catch (Exception e) {}
                            response = response.substring(response.indexOf("</C>") + "</C>".length());
                        } else if (response.startsWith("<Violation>")) {
                            response = response.substring("<Violation>".length());
                            try {List2s.Violation = (short) Integer.parseInt(GetField(response).trim());} catch (Exception e) {}
                            response = response.substring(response.indexOf("</Violation>") + "</Violation>".length());
                        } else {
                            response = response.substring(1);
                        }
                    }
                } else {
                    response = response.substring(1);
                }
            }
        }
        catch (Exception e) {}
    }
}
