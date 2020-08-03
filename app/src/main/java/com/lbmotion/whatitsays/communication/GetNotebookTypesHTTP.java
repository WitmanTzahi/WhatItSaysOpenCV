package com.lbmotion.whatitsays.communication;


import com.lbmotion.whatitsays.data.NotebookType;

import java.util.Vector;

/**
 * Created by witman on 22/07/2017.
 */

public class GetNotebookTypesHTTP extends BaseHTTP {
    public static GetNotebookTypesHTTP  me;
    public Vector<NotebookType> list;

    //"VolleyGetNotebookTypesHTTP";
    //""DB.notebookTypes

    public GetNotebookTypesHTTP(String tag, Vector<NotebookType> list) {
        REQUEST_TAG = tag;
        TAG = "GetNotebookTypesHTTP";
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

    public static GetNotebookTypesHTTP doGetNotebookTypesHTTP(String tag, String command, String username, String authority, Vector<NotebookType> list) {
        list.removeAllElements();
        me = new GetNotebookTypesHTTP(tag,list);
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
                    NotebookType notebookType = new NotebookType();
                    while (response.length() > 0) {
                        /**/ if (response.startsWith("</row>")) {
                            list.add(notebookType);
                            response = response.substring("</row>".length());
                            break;
                        } else if (response.startsWith("<C>")) {
                            response = response.substring("<C>".length());
                            try {notebookType.C = (short) Integer.parseInt(GetField(response).trim());} catch (Exception e) {}
                            response = response.substring(response.indexOf("</C>") + "</C>".length());
                        } else if (response.startsWith("<Kod>")) {
                            response = response.substring("<Kod>".length());
                            try {notebookType.Kod = (short) Integer.parseInt(GetField(response).trim());} catch (Exception e) {}
                            response = response.substring(response.indexOf("</Kod>") + "</Kod>".length());
                        } else if (response.startsWith("<Nm>")) {
                            response = response.substring("<Nm>".length());
                            notebookType.Nm = ToHebrew(GetField(response));
                            response = response.substring(response.indexOf("</Nm>") + "</Nm>".length());
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
