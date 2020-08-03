package com.lbmotion.whatitsays.communication;

/**
 * Created by witman on 22/07/2017.
 */

public class AddPictureToHistoryDocHTTP extends BaseHTTP {
    public static AddPictureToHistoryDocHTTP me;

    public AddPictureToHistoryDocHTTP() {
        TAG = "AddPicToHistoryDocHTTP";
        REQUEST_TAG = "VolleyAddPictureToHistoryDocHTTP";
    }

    public static void abort() {
        try {
            if(me != null) {
                me.baseAbort();
            }
        }
        catch (Exception e) {}
    }

    public static AddPictureToHistoryDocHTTP doLoad(String authority, String userid, String fileName, String dochCode, String doch, String dochKod) {
        me = new AddPictureToHistoryDocHTTP();
        me.doRun("N_AddPicture.asp?Odbc="+me.codeData(authority)+"&DochC="+me.codeData(dochCode)+"&Pic0="+me.codeData(fileName));
        me.doWait();
        return me;
    }

    protected void parseResponse(String response) {
        try {
            result = response.startsWith("SwChk=0");
        }
        catch (Exception e) {
        }
    }
}
