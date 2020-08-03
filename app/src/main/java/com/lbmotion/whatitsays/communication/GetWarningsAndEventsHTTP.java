package com.lbmotion.whatitsays.communication;

import com.lbmotion.whatitsays.data.DB;
import com.lbmotion.whatitsays.data.Defendant;
import com.lbmotion.whatitsays.data.ExistingWarning;
import com.lbmotion.whatitsays.data.Witness;

/**
 * Created by witman on 22/07/2017.
 */

public class GetWarningsAndEventsHTTP extends BaseHTTP {
    public static GetWarningsAndEventsHTTP  me;
    public String username;
    public String authority;

    public GetWarningsAndEventsHTTP() {
        REQUEST_TAG = "VolleyGetWarningsAndEventsHTTP";
        TAG = "GetWarnings&EventsHTTP";
    }

    public static void abort() {
        try {
            if(me != null) {
                me.baseAbort();
            }
        }
        catch (Exception e) {}
    }

    public static GetWarningsAndEventsHTTP doGetWarningsAndEventsHTTP(String authority, String username) {
        me = new GetWarningsAndEventsHTTP();
        me.authority = authority;
        me.username = username;
        me.doRun("N_GetDochPWorningXMLTbl_Mirs.asp?"+"Odbc="+authority+"&Pakach="+username+"&Version=1");
        me.doWait();
        return me;
    }

    protected void parseResponse(String response) {
        DB.existingWarnings.removeAllElements();
        DB.pastToHandleEvents.removeAllElements();
        result = true;
        try {
            while (response.length() > 0) {
                if (response.startsWith("<row>")) {
                    response = response.substring ("<row>".length());
                    ExistingWarning warning = new ExistingWarning();
                    warning.defendant = new Defendant();
                    warning.witness = new Witness();
                    int type = 0;
                    while (response.length() > 0) {
                        if (response.startsWith ("</row>")) {
                            warning.defendant.cityName = warning.CityNm;
                            if(type == 0)
                                DB.existingWarnings.add(warning);
                            else
                                DB.pastToHandleEvents.add(warning);
                            response = response.substring("</row>".length());
                            break;
                        }
                        else if (response.startsWith("<Lk>")) {
                            response = response.substring ("<Lk>".length());
                            warning.Lk = GetField (response).trim();
                            response = response.substring (response.indexOf ("</Lk>") + "</Lk>".length());
                        }
                        else if (response.startsWith ("<UsrKod>")) {
                            response = response.substring ("<UsrKod>".length());
                            warning.UsrKod = GetField (response).trim();
                            response = response.substring (response.indexOf ("</UsrKod>") + "</UsrKod>".length());
                        }
                        else if (response.startsWith ("<UsrNm>")) {
                            response = response.substring ("<UsrNm>".length());
                            warning.UsrNm = ToHebrew(GetField (response).trim());
                            response = response.substring (response.indexOf ("</UsrNm>") + "</UsrNm>".length());
                        }
                        else if (response.startsWith ("<UsrC>")) {
                            response = response.substring ("<UsrC>".length());
                            warning.UsrC = GetField (response).trim();
                            response = response.substring (response.indexOf ("</UsrC>") + "</UsrC>".length());
                        }
                        else if (response.startsWith ("<DochC>")) {
                            response = response.substring ("<DochC>".length());
                            warning.DochC = GetField (response).trim();
                            response = response.substring (response.indexOf ("</DochC>") + "</DochC>".length());
                        }
                        else if (response.startsWith ("<DochKod>")) {
                            response = response.substring ("<DochKod>".length());
                            warning.DochKod = GetField (response).trim();
                            response = response.substring (response.indexOf ("</DochKod>") + "</DochKod>".length());
                        }
                        else if (response.startsWith ("<Sidra>")) {
                            response = response.substring ("<Sidra>".length());
                            warning.Sidra = GetField (response).trim();
                            response = response.substring (response.indexOf ("</Sidra>") + "</Sidra>".length());
                        }
                        else if (response.startsWith ("<Bikoret>")) {
                            response = response.substring ("<Bikoret>".length());
                            warning.Bikoret = GetField (response).trim();
                            response = response.substring (response.indexOf ("</Bikoret>") + "</Bikoret>".length());
                        }
                        else if (response.startsWith ("<AveraNm>")) {
                            response = response.substring ("<AveraNm>".length());
                            warning.AveraNm = ToHebrew(GetField (response).trim());
                            response = response.substring (response.indexOf ("</AveraNm>") + "</AveraNm>".length());
                        }
                        else if (response.startsWith ("<AveraC>")) {
                            response = response.substring ("<AveraC>".length());
                            warning.AveraC = GetField (response).trim();
                            response = response.substring (response.indexOf ("</AveraC>") + "</AveraC>".length());
                        }
                        else if (response.startsWith ("<Mhr>")) {
                            response = response.substring ("<Mhr>".length());
                            warning.Mhr = GetField (response).trim();
                            response = response.substring (response.indexOf ("</Mhr>") + "</Mhr>".length());
                        }
                        else if (response.startsWith ("<Days>")) {
                            response = response.substring ("<Days>".length());
                            warning.Days = GetField (response).trim();
                            response = response.substring (response.indexOf ("</Days>") + "</Days>".length());
                        }
                        else if (response.startsWith ("<StreetKod>")) {
                            response = response.substring ("<StreetKod>".length());
                            warning.StreetKod = GetField (response).trim();
                            response = response.substring (response.indexOf ("</StreetKod>") + "</StreetKod>".length());
                        }
                        else if (response.startsWith ("<StreetC>")) {
                            response = response.substring ("<StreetC>".length());
                            warning.StreetC = GetField (response).trim();
                            response = response.substring (response.indexOf ("</StreetC>") + "</StreetC>".length());
                        }
                        else if (response.startsWith ("<StreetNm>")) {
                            response = response.substring ("<StreetNm>".length());
                            warning.StreetNm = ToHebrew(GetField (response).trim());
                            response = response.substring (response.indexOf ("</StreetNm>") + "</StreetNm>".length());
                        }
                        else if (response.startsWith ("<MikumKod>")) {
                            response = response.substring ("<MikumKod>".length());
                            warning.MikumKod = GetField (response).trim();
                            response = response.substring (response.indexOf ("</MikumKod>") + "</MikumKod>".length());
                        }
                        else if (response.startsWith ("<MikumC>")) {
                            response = response.substring ("<MikumC>".length());
                            warning.MikumC = GetField (response).trim();
                            response = response.substring (response.indexOf ("</MikumC>") + "</MikumC>".length());
                        }
                        else if (response.startsWith ("<AveraD>")) {
                            response = response.substring ("<AveraD>".length());
//                                              warning.AveraD = GetField (response).trim();
                            response = response.substring (response.indexOf ("</AveraD>") + "</AveraD>".length());
                        }
                        else if (response.startsWith ("<TZ>")) {
                            response = response.substring ("<TZ>".length());
                            warning.defendant.id = GetField (response).trim();
                            if(warning.defendant.id.equals("0"))
                                warning.defendant.id = "";
                            response = response.substring (response.indexOf ("</TZ>") + "</TZ>".length());
                        }
                        else if (response.startsWith ("<NmS>")) {
                            response = response.substring ("<NmS>".length());
                            warning.defendant.last = ToHebrew(GetField (response).trim());
                            response = response.substring (response.indexOf ("</NmS>") + "</NmS>".length());
                        }
                        else if (response.startsWith ("<NmF>")) {
                            response = response.substring ("<NmF>".length());
                            warning.defendant.name = ToHebrew(GetField (response).trim());
                            response = response.substring (response.indexOf ("</NmF>") + "</NmF>".length());
                        }
                        else if (response.startsWith ("<Mikod>")) {
                            response = response.substring ("<Mikod>".length());
                            warning.defendant.zipcode = GetField (response).trim();
                            response = response.substring (response.indexOf ("</Mikod>") + "</Mikod>".length());
                        }
                        else if (response.startsWith ("<CityKod>")) {
                            response = response.substring ("<CityKod>".length());
                            warning.defendant.cityCode = GetField (response).trim();
                            response = response.substring (response.indexOf ("</CityKod>") + "</CityKod>".length());
                        }
                        else if (response.startsWith ("<StreetNo>")) {
                            response = response.substring ("<StreetNo>".length());
                            warning.defendant.number = ToHebrew(GetField (response).trim());
                            response = response.substring (response.indexOf ("</StreetNo>") + "</StreetNo>".length());
                        }
                        else if (response.startsWith ("<Street>")) {
                            response = response.substring ("<Street>".length());
                            warning.defendant.street = ToHebrew(GetField (response).trim());
                            response = response.substring (response.indexOf ("</Street>") + "</Street>".length());
                        }
                        else if (response.startsWith ("<Dira>")) {
                            response = response.substring ("<Dira>".length());
                            warning.defendant.flat = ToHebrew(GetField (response).trim());
                            response = response.substring (response.indexOf ("</Dira>") + "</Dira>".length());
                        }
                        else if (response.startsWith ("<HedTeudatZeut1>")) {
                            response = response.substring ("<HedTeudatZeut1>".length());
                            warning.witness.id = GetField (response).trim();
                            response = response.substring (response.indexOf ("</HedTeudatZeut1>") + "</HedTeudatZeut1>".length());
                        }
                        else if (response.startsWith ("<HedNm>")) {
                            response = response.substring ("<HedNm>".length());
                            warning.witness.name = ToHebrew(GetField (response).trim());
                            response = response.substring (response.indexOf ("</HedNm>") + "</HedNm>".length());
                        }
                        else if (response.startsWith ("<HedNmF1>")) {
                            response = response.substring ("<HedNmF1>".length());
                            warning.witness.last = ToHebrew(GetField (response).trim());
                            response = response.substring (response.indexOf ("</HedNmF1>") + "</HedNmF1>".length());
                        }
                        else if (response.startsWith ("<HedStreet>")) {
                            response = response.substring ("<HedStreet>".length());
                            warning.witness.street = ToHebrew(GetField (response).trim());
                            response = response.substring (response.indexOf ("</HedStreet>") + "</HedStreet>".length());
                        }
                        else if (response.startsWith ("<HedNmHouse>")) {
                            response = response.substring ("<HedNmHouse>".length());
                            warning.witness.number = ToHebrew(GetField (response).trim());
                            response = response.substring (response.indexOf ("</HedNmHouse>") + "</HedNmHouse>".length());
                        }
                        else if (response.startsWith ("<HedMikud>")) {
                            response = response.substring ("<HedMikud>".length());
                            warning.witness.zipcode = GetField (response).trim();
                            response = response.substring (response.indexOf ("</HedMikud>") + "</HedMikud>".length());
                        }
                        else if (response.startsWith ("<HedTel>")) {
                            response = response.substring ("<HedTel>".length());
                            warning.witness.telephone = GetField (response).trim();
                            response = response.substring (response.indexOf ("</HedTel>") + "</HedTel>".length());
                        }
                        else if (response.startsWith ("<HedCityC>")) {
                            response = response.substring ("<HedCityC>".length());
                            warning.witness.cityCode = GetField (response).trim();
                            response = response.substring (response.indexOf ("</HedCityC>") + "</HedCityC>".length());
                        }
                        else if (response.startsWith ("<NumAzhara>")) {
                            response = response.substring ("<NumAzhara>".length());
                            warning.numAzhara = GetField (response).trim();
                            response = response.substring (response.indexOf ("</NumAzhara>") + "</NumAzhara>".length());
                        }
                        else if (response.startsWith ("<AveraKod>")) {
                            response = response.substring ("<AveraKod>".length());
                            warning.AveraKod = GetField (response).trim();
                            response = response.substring (response.indexOf ("</AveraKod>") + "</AveraKod>".length());
                        }
                        else if (response.startsWith ("<StreetNoAvera>")) {
                            response = response.substring ("<StreetNoAvera>".length());
                            warning.StreetNoAvera = ToHebrew(GetField (response).trim());
                            response = response.substring (response.indexOf ("</StreetNoAvera>") + "</StreetNoAvera>".length());
                        }
                        else if (response.startsWith ("<CityNm>")) {
                            response = response.substring ("<CityNm>".length());
                            warning.CityNm = ToHebrew(GetField (response).trim());
                            response = response.substring (response.indexOf ("</CityNm>") + "</CityNm>".length());
                        }
                        else if (response.startsWith ("<CityNmHed>")) {
                            response = response.substring ("<CityNmHed>".length());
                            warning.witness.city = ToHebrew(GetField (response).trim());
                            response = response.substring (response.indexOf ("</CityNmHed>") + "</CityNmHed>".length());
                        }
                        else if (response.startsWith ("<HedDira>")) {
                            response = response.substring ("<HedDira>".length());
                            warning.witness.flat = ToHebrew(GetField (response).trim());
                            response = response.substring (response.indexOf ("</HedDira>") + "</HedDira>".length());
                        }
                        else if (response.startsWith ("<Box>")) {
                            response = response.substring ("<Box>".length());
                            warning.defendant.box = ToHebrew(GetField (response).trim());
                            response = response.substring (response.indexOf ("</Box>") + "</Box>".length());
                        }
                        else if (response.startsWith("<ValidUpTo>")) {
                            response = response.substring ("<ValidUpTo>".length());
                            warning.validUpto = GetField (response).trim();
                            response = response.substring (response.indexOf ("</ValidUpTo>") + "</ValidUpTo>".length());
                        }
                        else if (response.startsWith("<Type>")) {
                            response = response.substring ("<Type>".length());
                            try{type = Integer.parseInt(GetField (response).trim());}catch (Exception e) {}
                            response = response.substring (response.indexOf ("</Type>") + "</Type>".length());
                        }
                        else if (response.startsWith("<UptoHour>")) {
                            response = response.substring ("<UptoHour>".length());
                            warning.validUptoTime = GetField (response).trim();
                            response = response.substring (response.indexOf ("</UptoHour>") + "</UptoHour>".length());
                        }
                        else if (response.startsWith("<DisplayMessage>")) {
                            response = response.substring ("<DisplayMessage>".length());
                            warning.remark = ToHebrew(GetField (response).trim());
                            response = response.substring (response.indexOf ("</DisplayMessage>") + "</DisplayMessage>".length());
                        }
                        else if (response.startsWith("<Animal>")) {
                            response = response.substring ("<Animal>".length());
                            warning.animalId = ToHebrew(GetField(response).trim());
                            response = response.substring (response.indexOf ("</Animal>") + "</Animal>".length());
                        }
                        else if (response.startsWith ("<MishpatKod>")) {
                            response = response.substring ("<MishpatKod>".length());
//                          warning.MishpatKod = GetField (response).trim();
                            response = response.substring (response.indexOf ("</MishpatKod>") + "</MishpatKod>".length());
                        }
                        else if (response.startsWith ("<DateTokef>")) {
                            response = response.substring ("<DateTokef>".length());
//                           warning.DateTokef = GetField (response).trim();
                            response = response.substring (response.indexOf ("</DateTokef>") + "</DateTokef>".length());
                        }
                        else if (response.startsWith ("<FreeRemark>")) {
                            response = response.substring ("<FreeRemark>".length());
                            warning.FreeRemark = ToHebrew(GetField (response).trim());
                            response = response.substring (response.indexOf ("</FreeRemark>") + "</FreeRemark>".length());
                        }
                        else if (response.startsWith ("<CitizenFreeText>")) {
                            response = response.substring ("<CitizenFreeText>".length());
                            warning.CitizenFreeText = ToHebrew(GetField (response).trim());
                            response = response.substring (response.indexOf ("</CitizenFreeText>") + "</CitizenFreeText>".length());
                        }
                        else if (response.startsWith ("<Pakach_Remark1>")) {
                            response = response.substring ("<Pakach_Remark1>".length());
                            warning.Pakach_Remark1 = GetField (response).trim();
                            response = response.substring (response.indexOf ("</Pakach_Remark1>") + "</Pakach_Remark1>".length());
                        }
                        else if (response.startsWith ("<Pakach_Remark2>")) {
                            response = response.substring ("<Pakach_Remark2>".length());
                            warning.Pakach_Remark2 = GetField (response).trim();
                            response = response.substring (response.indexOf ("</Pakach_Remark2>") + "</Pakach_Remark2>".length());
                        }
                        else if (response.startsWith ("<Pakach_Remark3>")) {
                            response = response.substring ("<Pakach_Remark3>".length());
                            warning.Pakach_Remark3 = GetField (response).trim();
                            response = response.substring (response.indexOf ("</Pakach_Remark3>") + "</Pakach_Remark3>".length());
                        }
                        else if (response.startsWith ("<Pakach_Remark4>")) {
                            response = response.substring ("<Pakach_Remark4>".length());
                            warning.Pakach_Remark4 = GetField (response).trim();
                            response = response.substring (response.indexOf ("</Pakach_Remark4>") + "</Pakach_Remark4>".length());
                        }
                        else if (response.startsWith ("<Citizen_Remark1>")) {
                            response = response.substring ("<Citizen_Remark1>".length());
                            warning.Citizen_Remark1 = GetField (response).trim();
                            response = response.substring (response.indexOf ("</Citizen_Remark1>") + "</Citizen_Remark1>".length());
                        }
                        else if (response.startsWith ("<Citizen_Remark2>")) {
                            response = response.substring ("<Citizen_Remark2>".length());
                            warning.Citizen_Remark2 = GetField (response).trim();
                            response = response.substring (response.indexOf ("</Citizen_Remark2>") + "</Citizen_Remark2>".length());
                        }
                        else if (response.startsWith ("<Citizen_Remark3>")) {
                            response = response.substring ("<Citizen_Remark3>".length());
                            warning.Citizen_Remark3 = GetField (response).trim();
                            response = response.substring (response.indexOf ("</Citizen_Remark3>") + "</Citizen_Remark3>".length());
                        }
                        else if (response.startsWith ("<Citizen_Remark4>")) {
                            response = response.substring ("<Citizen_Remark4>".length());
                            warning.Citizen_Remark4 = GetField (response).trim();
                            response = response.substring (response.indexOf ("</Citizen_Remark4>") + "</Citizen_Remark4>".length());
                        }
                        else if (response.startsWith ("<CarNo>")) {
                            response = response.substring ("<CarNo>".length());
                            warning.vehicle.licence = GetField (response).trim();
                            if(warning.vehicle.licence.equals("0"))
                                warning.vehicle.licence = "";
                            response = response.substring (response.indexOf ("</CarNo>") + "</CarNo>".length());
                        }
                        else if (response.startsWith ("<Car_TypeC>")) {
                            response = response.substring ("<Car_TypeC>".length());
                            try{warning.vehicle.type = Integer.parseInt(GetField (response).trim());}catch (Exception e) {}
                            response = response.substring (response.indexOf ("</Car_TypeC>") + "</Car_TypeC>".length());
                        }
                        else if (response.startsWith ("<Car_IzranC>")) {
                            response = response.substring ("<Car_IzranC>".length());
                            try{warning.vehicle.manufacturer = Integer.parseInt(GetField (response).trim());}catch (Exception e) {}
                            response = response.substring (response.indexOf ("</Car_IzranC>") + "</Car_IzranC>".length());
                        }
                        else if (response.startsWith ("<Car_ColorC>")) {
                            response = response.substring ("<Car_ColorC>".length());
                            try{warning.vehicle.color = Integer.parseInt(GetField (response).trim());}catch (Exception e) {}
                            response = response.substring (response.indexOf ("</Car_ColorC>") + "</Car_ColorC>".length());
                        }
                        else if (response.startsWith ("<Pic")) {
                            response = response.substring ("<PicX>".length());
                            String pic = GetField (response).trim();
                            if(pic.length() > 0)
                                warning.pictures.add(GetField (response).trim());
                            response = response.substring (response.indexOf ("</Pic") + "</PicX>".length());
                        }
                        else {
                            response = response.substring(1);
                        }
                    }
                    // inner while loop
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
