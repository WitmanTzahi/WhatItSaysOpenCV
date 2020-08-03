package com.lbmotion.whatitsays.communication;


import com.lbmotion.whatitsays.data.List2;

import java.util.Vector;

/**
 * Created by witman on 22/07/2017.
 */

public class GetList2HTTP extends BaseHTTP {
    private final String TAG = "List2HTTP";
    public static GetList2HTTP          me;
    public Vector<List2> list;

    public GetList2HTTP(String tag, Vector<List2> list) {
        REQUEST_TAG = tag;
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

    public static GetList2HTTP doGetList2HTTP(String tag, String command, String username, String authority, Vector<List2> list) {
        list.removeAllElements();
        me = new GetList2HTTP(tag,list);
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
                    List2 list2 = new List2();
                    while (response.length() > 0) {
                        /**/if (response.startsWith("</row>")) {
                            list.add(list2);
                            response = response.substring("</row>".length());
                            break;
                        } else if (response.startsWith("<C>")) {
                            response = response.substring("<C>".length());
                            try {list2.code = (short) Integer.parseInt(GetField(response));} catch (Exception e) {}
                            response = response.substring(response.indexOf("</C>") + "</C>".length());
                        } else if (response.startsWith("<Nm>")) {
                            response = response.substring("<Nm>".length());
                            list2.name = ToHebrew(GetField(response));
                            response = response.substring(response.indexOf("</Nm>") + "</Nm>".length());
                        } else if (response.startsWith("<Teur>")) {
                            response = response.substring("<Teur>".length());
                            list2.name = ToHebrew(GetField(response));
                            response = response.substring(response.indexOf("</Teur>") + "</Teur>".length());
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
        catch (Exception e) {}
    }
}
