package com.lbmotion.whatitsays.communication;

import static com.lbmotion.whatitsays.communication.Base.webVersion;

/**
 * Created by witman on 22/07/2017.
 */

public class GetVersionHTTP extends BaseHTTP {
    public static GetVersionHTTP             me;

    public GetVersionHTTP() {
        REQUEST_TAG = "VolleyGetVersionHTTP";
        TAG = "GetVersionHTTP";
    }

    public static void abort() {
        try {
            if(me != null) {
                me.baseAbort();
            }
        }
        catch (Exception e) {}
    }

    public static GetVersionHTTP getVersion() {
        me = new GetVersionHTTP();
        me.doRun("http://www.trapnearu.com/Hanita/version.txt");
        me.doWait();
        return me;
    }

    protected void parseResponse(String response) {
        result = true;
        try {
            webVersion = (new String(response)).trim();
        }
        catch (Exception e) {
        }
    }
}
