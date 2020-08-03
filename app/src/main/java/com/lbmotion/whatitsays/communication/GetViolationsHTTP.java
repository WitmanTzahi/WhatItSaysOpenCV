package com.lbmotion.whatitsays.communication;

import com.lbmotion.whatitsays.data.DB;
import com.lbmotion.whatitsays.data.Violation;

/**
 * Created by witman on 21/07/2017.
 */

public class GetViolationsHTTP extends BaseHTTP {
    public static GetViolationsHTTP 	me;

    public GetViolationsHTTP() {
        REQUEST_TAG = "GetViolationsHTTP";
        TAG = "GetViolationsHTTP";
    }

    public static void abort() {
        try {
            if(me != null) {
                me.baseAbort();
            }
        }
        catch (Exception e) {}
    }

    public static GetViolationsHTTP doGetViolationsHTTP(String username, String authority) {
        me = new GetViolationsHTTP();
        DB.allViolations.removeAllElements();
        me.doRun("https://ws.comax.co.il/Hanita/Parking/MobileWs.asmx/GetViolation?LoginID=Hanita&LoginPassword=Hanita&Odbc=" + authority + "&Pakach=" + username);
//      me.doRun("N_GetViolationXMLTbl_Mirs.asp?Odbc=" + authority + "&Pakach=" + username);
        me.doWait();
        return me;
    }

    protected void parseResponse(String response) {
        try {
            result = true;
//          Violation toModifyViolation = null;
            Violation violation;
            while (response.length() > 0) {
                if (response.startsWith("<Violation>")) {
//              if (response.startsWith("<row>")) {
                    response = response.substring("<Violation>".length());
//                  response = response.substring("<row>".length());
                    violation = new Violation();
                    while (response.length() > 0) {
                        /**/if (response.startsWith("</Violation>")) {
                            if(violation.toAtraa == 0 && violation.toAvera == 0)
                                violation.toAtraa = -1;
                            boolean found = false;
                            for(Violation v : DB.allViolations) {
                                if(violation.C == v.C && violation.K == v.K) {
                                    found = true;
                                    break;
                                }
                            }
                            if(!found)
                                DB.allViolations.add(violation);
                            response = response.substring("</Violation>".length());
//                          response = response.substring("</row>".length());
                            break;
                        } else if (response.startsWith("<C>")) {
                            response = response.substring("<C>".length());
                            try {violation.C = (short) Integer.parseInt(GetField(response).trim());} catch (Exception e) {}
                            response = response.substring(response.indexOf("</C>") + "</C>".length());
                        } else if (response.startsWith("<SwRealTime>")) {
                            response = response.substring("<SwRealTime>".length());
                            String tmp = GetField(response).trim();
                            if(tmp.trim().equals("0"))
                                violation.hasVirtualDoc = true;
                            else
                                violation.hasVirtualDoc = false;
                            response = response.substring(response.indexOf("</SwRealTime>") + "</SwRealTime>".length());
                        } else if (response.startsWith("<Title>")) {
                            response = response.substring("<Title>".length());
                            violation.title = ToHebrew(GetField(response));
                            response = response.substring(response.indexOf("</Title>") + "</Title>".length());
                        }
                        else if (response.startsWith("<K>")) {
                            response = response.substring("<K>".length());
                            try {violation.K = (short) Integer.parseInt(GetField(response).trim());} catch (Exception e) {}
                            response = response.substring(response.indexOf("</K>") + "</K>".length());
                        }
                        else if (response.startsWith("<VT>")) {
                            response = response.substring("<VT>".length());
                            try {violation.VT = (byte) Integer.parseInt(GetField(response).trim());} catch (Exception e) {}
                            response = response.substring(response.indexOf("</VT>") + "</VT>".length());
                        } else if (response.startsWith("<Nm>")) {
                            response = response.substring("<Nm>".length());
                            violation.Nm = ToHebrew(GetField(response));
                            response = response.substring(response.indexOf("</Nm>") + "</Nm>".length());
                        } else if (response.startsWith("<MP>")) {
                            response = response.substring("<MP>".length());
                            try {violation.MP = (short) Integer.parseInt(GetField(response).trim());} catch (Exception e) {}
                            response = response.substring(response.indexOf("</MP>") + "</MP>".length());
                        } else if (response.startsWith("<MR>")) {
                            response = response.substring("<MR>".length());
                            try {violation.MR = (short) Integer.parseInt(GetField(response).trim());} catch (Exception e) {}
                            response = response.substring(response.indexOf("</MR>") + "</MR>".length());
                        } else if (response.startsWith("<VTC>")) {
                            response = response.substring("<VTC>".length());
                            try {violation.VTC = (byte) Integer.parseInt(GetField(response).trim());} catch (Exception e) {}
                            response = response.substring(response.indexOf("</VTC>")+ "</VTC>".length());
                        }
                        else if (response.startsWith("<Sivug>")) {
                            response = response.substring("<Sivug>".length());
                            try {violation.Sivug = (byte) Integer.parseInt(GetField(response).trim());} catch (Exception e) {}
                            response = response.substring(response.indexOf("</Sivug>")+ "</Sivug>".length());
                        } else if (response.startsWith("<VT_Nm>")) {
                            response = response.substring("<VT_Nm>".length());
                            violation.VT_Nm = ToHebrew(GetField(response));
                            response = response.substring(response.indexOf("</VT_Nm>") + "</VT_Nm>".length());
                        } else if (response.startsWith("<P>")) {
                            response = response.substring("<P>".length());
                            try {violation.P = (short) Integer.parseInt(GetField(response).trim());} catch (Exception e) {}
                            response = response.substring(response.indexOf("</P>")+ "</P>".length());
                        } else if (response.startsWith("<D>")) {
                            response = response.substring("<D>".length());
                            try {violation.D = (short) Integer.parseInt(GetField(response).trim());} catch (Exception e) {}
                            response = response.substring(response.indexOf("</D>")+ "</D>".length());
                        } else if (response.startsWith("<SwHatraa>")) {
                            response = response.substring("<SwHatraa>".length());
                            try {violation.SwHatraa = Integer.parseInt(GetField(response).trim()) != 0;} catch (Exception e) {}
                            response = response.substring(response.indexOf("</SwHatraa>")+ "</SwHatraa>".length());
                        } else if (response.startsWith("<SwTz>")) {
                            response = response.substring("<SwTz>".length());
                            violation.SwTz = (byte) Integer.parseInt(GetField(response).trim());
                            response = response.substring(response.indexOf("</SwTz>")+ "</SwTz>".length());
                        } else if (response.startsWith("<SwRishui>")) {
                            response = response.substring("<SwRishui>".length());
                            try {violation.SwRishui = (byte) Integer.parseInt(GetField(response).trim());}catch (Exception e) {}
                            response = response.substring(response.indexOf("</SwRishui>")+ "</SwRishui>".length());
                        } else if (response.startsWith("<SwPrivateCompany>")) {
                            response = response.substring("<SwPrivateCompany>".length());
                            try{violation.SwPrivateCompany = (byte) Integer.parseInt(GetField(response).trim());}catch (Exception e) {}
                            response = response.substring(response.indexOf("</SwPrivateCompany>")+ "</SwPrivateCompany>".length());
                        } else if (response.startsWith("<NumCopies>")) {
                            response = response.substring("<NumCopies>".length());
                            try{violation.NumCopies = (byte) Integer.parseInt(GetField(response).trim());}catch (Exception e) {}
                            if(violation.NumCopies == 0)
                                violation.NumCopies = 1;
                            response = response.substring(response.indexOf("</NumCopies>")+ "</NumCopies>".length());
                        }
                        else if(response.startsWith("<SwMustTavHanaya>")) {
                            response = response.substring("<SwMustTavHanaya>".length());
                            try{violation.SwMustTavHanaya = (byte) Integer.parseInt(GetField(response).trim());}catch (Exception e) {}
                            response = response.substring(response.indexOf("</SwMustTavHanaya>")+"</SwMustTavHanaya>".length());
                        }
                        else if(response.startsWith("<SwHanayaHok>")) {
                            response = response.substring("<SwHanayaHok>".length());
                            try{violation.SwHanayaHok = (byte) Integer.parseInt(GetField(response).trim());}catch (Exception e) {}
                            response = response.substring(response.indexOf("</SwHanayaHok>")+"</SwHanayahok>".length());
                        }
                        else if(response.startsWith("<OffenceIcon>")) {
                            response = response.substring("<OffenceIcon>".length());
                            try{violation.OffenceIcon = (byte) Integer.parseInt(GetField(response).trim());}catch (Exception e) {}
                            response = response.substring(response.indexOf("</OffenceIcon>")+"</OffenceIcon>".length());
                        }
                        else if(response.startsWith("<QRSwWork>")) {
                            response = response.substring("<QRSwWork>".length());
                            String tmp = GetField(response).trim();
                            violation.QRSwWork = false;
                            if(tmp.length() > 0 && Integer.parseInt(tmp) == 1)
                                violation.QRSwWork = true;
                            response = response.substring(response.indexOf("</QRSwWork>")+"</QRSwWork>".length());
                        }
                        else if(response.startsWith("<SwWorkPrinter>")) {
                            response = response.substring("<SwWorkPrinter>".length());
                            String tmp = GetField(response).trim();
                            violation.SwWorkPrinter = false;
                            if(tmp.length() > 0 && Integer.parseInt(tmp) == 1)
                                violation.SwWorkPrinter = true;
                            response = response.substring(response.indexOf("</SwWorkPrinter>")+"</SwWorkPrinter>".length());
                        }
                        else if(response.startsWith("<TypeForm>")) {
                            response = response.substring("<TypeForm>".length());
                            violation.TypeForm = GetField(response).trim();
                            response = response.substring(response.indexOf("</TypeForm>")+"</TypeForm>".length());
                        }
                        else if(response.startsWith("<SwAvera>")) {
                            response = response.substring("<SwAvera>".length());
                            violation.toAvera = (short) Integer.parseInt(GetField(response).trim());
                            response = response.substring(response.indexOf("</SwAvera>")+"</SwAvera>".length());
                        }
                        else if(response.startsWith("<SwAtraa>")) {
                            response = response.substring("<SwAtraa>".length());
                            violation.toAtraa = (short) Integer.parseInt(GetField(response).trim());
                            response = response.substring(response.indexOf("</SwAtraa>")+"</SwAtraa>".length());
                        }
                        else if(response.startsWith("<SwPrintQR>")) {
                            response = response.substring("<SwPrintQR>".length());
                            violation.swPrintQR = (byte)(Integer.parseInt(GetField(response).trim())+1);
                            response = response.substring(response.indexOf("</SwPrintQR>")+"</SwPrintQR>".length());
                        }
                        else if(response.startsWith("<CodeMotavShort>")) {
                            response = response.substring("<CodeMotavShort>".length());
                            violation.codeMotavShort = GetField(response).trim();
                            response = response.substring(response.indexOf("</CodeMotavShort>")+"</CodeMotavShort>".length());
                        }
                        else if(response.startsWith("<TeurShort>")) {
                            response = response.substring("<TeurShort>".length());
                            String[] screens = GetField(response).trim().split(",");
                            violation.TeurShort = GetField(response).trim();
                            response = response.substring(response.indexOf("</TeurShort>")+"</TeurShort>".length());
                        }
                        else if(response.startsWith("<OrderScreen>")) {
                            response = response.substring("<OrderScreen>".length());
                            String[] screens = GetField(response).trim().split(",");
                            for(int i = 0;i < screens.length;i++) {
                                violation.screensOrder.add(Integer.valueOf(screens[i]));
                            }
                            response = response.substring(response.indexOf("</OrderScreen>")+"</OrderScreen>".length());
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
    }
}
