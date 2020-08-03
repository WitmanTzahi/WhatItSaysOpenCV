package com.lbmotion.whatitsays.communication;

import android.util.Log;

import com.lbmotion.whatitsays.UCApp;
import com.lbmotion.whatitsays.data.DB;
import com.lbmotion.whatitsays.data.Defendant;
import com.lbmotion.whatitsays.data.Vehicle;
import com.lbmotion.whatitsays.data.Witness;

import static com.lbmotion.whatitsays.communication.GetDefendantCarInformation.doGetDefendantCarInformation;
import static com.lbmotion.whatitsays.communication.GetDefendantInformationByAddress.doGetDefendantInformationByAddress;
import static com.lbmotion.whatitsays.communication.GetDefendantInformationByID.doGetDefendantInformationByID;
import static com.lbmotion.whatitsays.communication.GetDefendantInformationByName.doGetDefendantInformationByName;

/**
 * Created by witman on 22/07/2017.
 */

public class LoadDetailsHTTP extends BaseHTTP {
    public static LoadDetailsHTTP               me;
    private GetDefendantInformationByID         getDefendantInformationByID = null;
    private GetDefendantInformationByName       getDefendantInformationByName = null;
    private GetDefendantCarInformation          getDefendantCarInformation = null;
    private GetDefendantInformationByAddress    getDefendantInformationByAddress = null;


    public LoadDetailsHTTP() {
        REQUEST_TAG = "VolleyLoadDetailsHTTP";
        TAG = "LoadDetailsHTTP";
    }

    public static void abort() {
        try {
            if(me != null) {
                me.baseAbort();
                if(me.getDefendantInformationByID != null)
                    me.getDefendantInformationByID.abort();
                if(me.getDefendantInformationByName != null)
                    me.getDefendantInformationByName.abort();
                if(me.getDefendantCarInformation != null)
                    me.getDefendantCarInformation.abort();
                if(me.getDefendantInformationByAddress != null)
                    me.getDefendantInformationByAddress.abort();
            }
        }
        catch (Exception e) {}
    }

    public static boolean doLoadDetailsHTTP(String Lk, String username, int AveraC, int StreetC, String HouseNo, String TeudatZeut, String CarNo, byte MikumC, int type, String firstName, String lastName, String companyName) {
        UCApp.showPopupText = UCApp.showLoadDetails = true;
        DB.witness = new Witness();
        DB.defendant = new Defendant();
        DB.car = new Vehicle();
        DB.defendantList.removeAllElements();
        me = new LoadDetailsHTTP();
        try {
            if(TeudatZeut.length() > 0) {
                me.getDefendantInformationByID = doGetDefendantInformationByID(Lk, AveraC, StreetC, HouseNo, TeudatZeut,type);
                if (me.getDefendantInformationByID != null && me.getDefendantInformationByID.result && !me.getDefendantInformationByID.errorOccurred && !me.didAbort) {
                    me.getDefendantInformationByID = null;
                    me.result = true;
                }
            }
            else if(firstName.length()+lastName.length() > 0) {
                me.getDefendantInformationByName = doGetDefendantInformationByName(Lk, AveraC, firstName, lastName, companyName, type,StreetC+"",HouseNo);
                if (me.getDefendantInformationByName != null && me.getDefendantInformationByName.result && !me.getDefendantInformationByName.errorOccurred && !me.didAbort) {
                    me.getDefendantInformationByName = null;
                    me.result = true;
                }
            }
            else if(CarNo.length() > 0) {
                me.getDefendantCarInformation = doGetDefendantCarInformation(Lk, CarNo, AveraC, MikumC, StreetC, HouseNo);
                if (me.getDefendantCarInformation != null && me.getDefendantCarInformation.result && !me.getDefendantCarInformation.errorOccurred && !me.didAbort) {
                    me.getDefendantCarInformation = null;
                    me.result = true;
                }
            }
            else {
                me.getDefendantInformationByAddress = doGetDefendantInformationByAddress(Lk,  AveraC,  StreetC,  HouseNo);
                if (me.getDefendantInformationByAddress != null && me.getDefendantInformationByAddress.result && !me.getDefendantInformationByAddress.errorOccurred && !me.didAbort) {
                    me.getDefendantInformationByAddress = null;
                    me.result = true;
                }
            }
        }
        catch (Exception e) {
            me.doAbort();
            Log.i(me.TAG, "run()"+e.getMessage());
        }
        boolean result = false;
        if(me != null)
            result = me.result;
        return result;
    }

    protected void parseResponse(String response) {

    }

}
