package com.lbmotion.whatitsays.communication;

import com.lbmotion.whatitsays.data.HistoryForAddressOrId;

import java.util.Vector;

/**
 * Created by witman on 22/07/2017.
 */

public class GetHistoryByAddressOrIdHTTP extends BaseHTTP {
    private final String TAG = "GetHistoryByAddOrIdHTTP";
    public static GetHistoryByAddressOrIdHTTP me;
    public Vector<HistoryForAddressOrId> list = new Vector<>();

    public GetHistoryByAddressOrIdHTTP() {
        REQUEST_TAG = "VolleyGetHistoryByAddressOrIdHTTP";
    }

    public static void abort() {
        try {
            if(me != null) {
                me.baseAbort();
            }
        }
        catch (Exception e) {}
    }

    public static GetHistoryByAddressOrIdHTTP doLoad(String authority, String userid, String query, String parameter) {
        me = new GetHistoryByAddressOrIdHTTP();
        String url = "N_GetDetalies.asp?Odbc="+me.codeData(authority);
        if(parameter.equals("A")) {
            String[] pars = query.split(";");
            if(pars.length == 2)
                url += "&Type=1&StreetC="+me.codeData(pars[0])+"&StreetNo="+me.codeData(pars[1]);
        }
        else {
            url += "&Type=2&TeudatZeut="+me.codeData(query);
        }
        me.doRun(url);
        me.doWait();
        return me;
    }

    protected void parseResponse(String response) {
        result = true;
        try {
            while (response.length() > 0) {
                if (response.startsWith("<row>")) {
                    response = response.substring("<row>".length());
                    HistoryForAddressOrId averaHistory = new HistoryForAddressOrId();
                    while (response.length() > 0) {
                        if (response.startsWith("</row>")) {
                            list.add(averaHistory);
                            response = response.substring(response.indexOf("</row>") + "</row>".length());
                            break;
                        } else if (response.startsWith("<ReportType>")) {
                            response = response.substring("<ReportType>".length());
                            averaHistory.ReportType = ToHebrew(GetField(response));
                            response = response.substring(response.indexOf("</ReportType>") + "</ReportType>".length());
                        } else if (response.startsWith("<FreeRemark>")) {
                            response = response.substring("<FreeRemark>".length());
                            averaHistory.FreeRemark = ToHebrew(GetField(response));
                            response = response.substring(response.indexOf("</FreeRemark>") + "</FreeRemark>".length());
                        } else if (response.startsWith("<DDate>")) {
                            response = response.substring("<DDate>".length());
                            averaHistory.DDate = ToHebrew(GetField(response));
                            response = response.substring(response.indexOf("</DDate>") + "</DDate>".length());
                        } else if (response.startsWith("<DHour>")) {
                            response = response.substring("<DHour>".length());
                            averaHistory.DHour = ToHebrew(GetField(response));
                            response = response.substring(response.indexOf("</DHour>") + "</DHour>".length());
                        } else if (response.startsWith("<Report>")) {
                            response = response.substring("<Report>".length());
                            averaHistory.Report = ToHebrew(GetField(response));
                            response = response.substring(response.indexOf("</Report>") + "</Report>".length());
                        } else if (response.startsWith("<TeudatZeut>")) {
                            response = response.substring("<TeudatZeut>".length());
                            averaHistory.TeudatZeut = ToHebrew(GetField(response));
                            response = response.substring(response.indexOf("</TeudatZeut>") + "</TeudatZeut>".length());
                        } else if (response.startsWith("<Nm>")) {
                            response = response.substring("<Nm>".length());
                            averaHistory.Nm = ToHebrew(GetField(response));
                            response = response.substring(response.indexOf("</Nm>") + "</Nm>".length());
                        } else if (response.startsWith("<NmF>")) {
                            response = response.substring("<NmF>".length());
                            averaHistory.NmF = ToHebrew(GetField(response));
                            response = response.substring(response.indexOf("</NmF>") + "</NmF>".length());
                        } else if (response.startsWith("<Seif>")) {
                            response = response.substring("<Seif>".length());
                            averaHistory.Seif = ToHebrew(GetField(response));
                            response = response.substring(response.indexOf("</Seif>") + "</Seif>".length());
                        } else if (response.startsWith("<AveraSug>")) {
                            response = response.substring("<AveraSug>".length());
                            averaHistory.AveraSug = ToHebrew(GetField(response));
                            response = response.substring(response.indexOf("</AveraSug>") + "</AveraSug>".length());
                        } else if (response.startsWith("<AveraCode>")) {
                            response = response.substring("<AveraCode>".length());
                            averaHistory.AveraCode = ToHebrew(GetField(response));
                            response = response.substring(response.indexOf("</AveraCode>") + "</AveraCode>".length());
                        } else
                            response = response.substring(1);
                    }
                }
                else {
                    response = response.substring(1);
                }
            }
        }
        catch (Exception e) {}
    }
}
