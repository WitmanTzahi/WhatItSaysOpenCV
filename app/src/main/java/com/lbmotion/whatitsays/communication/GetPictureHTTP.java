package com.lbmotion.whatitsays.communication;
import java.util.Vector;

/**
 * Created by witman on 22/07/2017.
 */

public class GetPictureHTTP extends BaseHTTP {
    private final String TAG = "GetPictureHTTP";
    public static GetPictureHTTP       me;
    public Vector<String> list = new Vector<>();

    public GetPictureHTTP() {
        REQUEST_TAG = "VolleyGetPictureHTTP";
    }

    public static void abort() {
        try {
            if(me != null) {
                me.baseAbort();
            }
        }
        catch (Exception e) {}
    }

    public static GetPictureHTTP doLoad(String url) {
        me = new GetPictureHTTP();
        me.doRun(url,4000,1);
        me.doWait();
        return me;
    }

    protected void parseResponse(String response) {
        result = true;
        try {
            while (response.length() > 0) {
                if (response.startsWith("<PathImg>")) {
                    response = response.substring("<PathImg>".length());
                    list.add(GetField(response));
                    response = "";
                }
                else {
                    response = response.substring(1);
                }
            }
        }
        catch (Exception e) {
        }
    }
}
