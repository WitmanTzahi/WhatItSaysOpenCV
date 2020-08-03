package com.lbmotion.whatitsays.communication;

import com.lbmotion.whatitsays.data.List3;

import java.util.Vector;

/**
 * Created by witman on 22/07/2017.
 */

public class GetList3HTTP extends BaseHTTP {
    public static GetList3HTTP         	me;
    public Vector<List3> list;

    public GetList3HTTP(String tag, Vector<List3> list) {
        REQUEST_TAG = tag;
        this.list = list;
        TAG = "List3HTTP";
    }

    public static void abort() {
        try {
            if(me != null) {
                me.baseAbort();
            }
        }
        catch (Exception e) {}
    }

    public static GetList3HTTP doGetList3HTTP(String tag, String command, String username, String authority, Vector<List3> list) {
        list.removeAllElements();
        me = new GetList3HTTP(tag,list);
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
                    List3 list3 = new List3();
                    while (response.length() > 0) {
                        /**/ if (response.startsWith("</row>")) {
                            list.add(list3);
                            response = response.substring("</row>".length());
                            break;
                        } else if (response.startsWith("<C>")) {
                            response = response.substring("<C>".length());
                            try {list3.code = (short) Integer.parseInt(GetField(response).trim());} catch (Exception e) {}
                            response = response.substring(response.indexOf("</C>") + "</C>".length());
                        } else if (response.startsWith("<Kod>")) {
                            response = response.substring("<Kod>".length());
                            try {list3.Kod = (short) Integer.parseInt(GetField(response).trim());} catch (Exception e) {}
                            response = response.substring(response.indexOf("</Kod>") + "</Kod>".length());
                        } else if (response.startsWith("<Nm>")) {
                            response = response.substring("<Nm>".length());
                            list3.name = ToHebrew(GetField(response));
                            response = response.substring(response.indexOf("</Nm>") + "</Nm>".length());
                        } else if (response.startsWith("<Remark>")) {
                            response = response.substring("<Remark>".length());
                            list3.name = ToHebrew(GetField(response));
                            response = response.substring(response.indexOf("</Remark>") + "</Remark>".length());
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
