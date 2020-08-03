package com.lbmotion.whatitsays.communication;

import com.lbmotion.whatitsays.UCApp;

/**
 * Created by witman on 22/07/2017.
 */

public class DochotHTTP extends BaseHTTP {
    private	static final int	GET_DOCHOT = 1;
    private	static final int	CREATE_PINKS = 2;
    public static DochotHTTP    me;
    private String pinkasResult = "";
    private int 				operationType;

    public DochotHTTP(boolean create) {
        TAG = "DochotHTTP";
        REQUEST_TAG = "VolleyDochotHTTP";
        if(create)
            operationType = CREATE_PINKS;
        else
            operationType = GET_DOCHOT;
    }


    public static void abort() {
        try {
            if(me != null) {
                me.baseAbort();
            }
        }
        catch (Exception e) {}
    }

    public static DochotHTTP doLoad(String userid, String authority) {
        me = new DochotHTTP(false);
        me.doRun("N_GetReportsXMLTbl_Mirs.asp?odbc="+authority+"&Pakach="+userid);
        me.doWait();
        return me;
    }

    public static String doCreate(String userid, String authority, String number, String series, char notebookType) {
        int n = 0;
        try {n = Integer.parseInt(number);}catch(Exception e){}
        n = (n+24)/25;
        me = new DochotHTTP(true);
        me.doRun("N_GetPinkasXMLTbl_Mirs.asp?"+ "Odbc=" + authority + "&Pakach=" + userid + "&Sidra=" + series + "&Pinkas=" + n+"" + "&PinkasType=" + notebookType);
        me.doWait();
        return me.pinkasResult;
    }

    protected void parseResponse(String response) {
        result = true;
        UCApp.loginData.clearDochot();
        try {
            while (response.length() > 0) {
                if(operationType == GET_DOCHOT) {
                    if (response.startsWith("<row>")) {
                        response = response.substring("<row>".length());
                        int Code = 0,DochNumber = 0;
                        byte Sidra = 0,Bikoret = 0,SwHazara = 0,Sug = 0;
                        while (response.length() > 0) {
                            /**/ if (response.startsWith("</row>")) {
                                response = response.substring("</row>".length());
                                UCApp.loginData.addDoch(Code, DochNumber, Sidra, Bikoret, SwHazara, Sug);
                                break;
                            } else if (response.startsWith("<Sug>")) {
                                response = response.substring("<Sug>".length());
                                try {Sug = (byte) Integer.parseInt(GetField(response).trim());} catch (Exception e) {}
                                response = response.substring(response.indexOf("</Sug>") + "</Sug>".length());
                            } else if (response.startsWith("<C>")) {
                                response = response.substring("<C>".length());
                                try {Code = Integer.parseInt(GetField(response).trim());} catch (Exception e) {}
                                response = response.substring(response.indexOf("</C>") + "</C>".length());
                            } else if (response.startsWith("<Kod>")) {
                                response = response.substring("<Kod>".length());
                                try {DochNumber = Integer.parseInt(GetField(response).trim());} catch (Exception e) {}
                                response = response.substring(response.indexOf("</Kod>") + "</Kod>".length());
                            } else if (response.startsWith("<SwHazara>")) {
                                response = response.substring("<SwHazara>".length());
                                int i = 0;
                                try {i = Integer.parseInt(GetField(response).trim());} catch (Exception e) {}
                                SwHazara = (byte) (i - i / 10 * 10);
                                response = response.substring(response.indexOf("</SwHazara>") + "</SwHazara>".length());
                            } else if (response.startsWith("<Sidra>")) {
                                response = response.substring("<Sidra>".length());
                                try {Sidra = (byte) Integer.parseInt(GetField(response).trim());} catch (Exception e) {}
                                response = response.substring(response.indexOf("</Sidra>") + "</Sidra>".length());
                            } else if (response.startsWith("<Bikoret>")) {
                                response = response.substring("<Bikoret>".length());
                                try {Bikoret = (byte) Integer.parseInt(GetField(response).trim());} catch (Exception e) {}
                                response = response.substring(response.indexOf("</Bikoret>") + "</Bikoret>".length());
                            } else {
                                response = response.substring(1);
                            }
                        }
                    }
                    else {
                        response = response.substring(1);
                    }
                }
                else {
                    pinkasResult = reverseNumber(response);
                    break;
                }
            }
        }
        catch (Exception e) {
        }
    }
}
