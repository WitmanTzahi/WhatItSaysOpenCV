package com.lbmotion.whatitsays;

import android.app.ActivityManager;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.multidex.MultiDex;

import com.lbmotion.whatitsays.data.CheckLincense;
import com.lbmotion.whatitsays.data.DB;
import com.lbmotion.whatitsays.data.LoginData;
import com.lbmotion.whatitsays.data.TicketInformation;
import com.lbmotion.whatitsays.service.TicketsAndPicturesService;

import java.util.Date;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicBoolean;


/**
 * Created by witman on 29/06/16.
 */
public class UCApp extends Application {

    public static Context gContext = null;
    public static TicketsAndPicturesService.TicketsAndPicturesBinder ticketsAndPicturesBinder = null;
    public static TicketInformation         recoverTicketInformation = null;
    public static LoginData                 loginData = null;
    public static int 						shiftHoursLength = 1440;
    public static int 						carTypeDefaultValue, carColorDefaultValue, carIzranDefaultValue;
    public static Vector<String> streets = new Vector<String>();
    public static Vector<Integer> streetsReference = new Vector<Integer>();
    public static int		 				pakachSelectedItemWarning = -1;
    public static boolean 					doEndOfShift = false;
    public static Vector<Integer> citizenSelectedItemRemarks = new Vector<Integer>();
    public static Vector<Integer> citizenRemarksToViolationReference = new Vector<Integer>();
    public static Vector<String> citizenRemarksToViolation = new Vector<String>();
    public static Vector<Integer> pakachRemarksToViolationReference = new Vector<Integer>();
    public static Vector<Integer> pakachSelectedItemRemarks = new Vector<Integer>();
    public static Vector<Boolean> pakachSelectedItemRemarksPrint = new Vector<Boolean>();
    public static int 						pakachMandatoryRemark = -1;
    public static Vector<String> pakachRemarksToViolation = new Vector<String>();
    public static CheckLincense checkLincense = null;
    public static boolean 					isDoingPreviousWarning = false;
    public static boolean 					isDoingPreviousOpenEvent = false;
    public static boolean		 			isDoingAssiment = false;
    public static long 						lastTicketTime = (new Date()).getTime();
    public static boolean 					doExitNow = false;
    public static AtomicBoolean workModeOffline = new AtomicBoolean(false);
    public static boolean 					canWorkOffLine;
    public static boolean 					parkingAllowed = false, generalAllowed = false, managementAllowed = false;
    public static String printURL = null;
    public static byte 						printerType = -1;
    public static byte 						printerSubType = -1;
//  public static BluetoothDiscoverer       bluetoothDiscoverer = null;
    public static boolean 					hasToReQueryAgain = false;
    public static TicketInformation			ticketInformation = new TicketInformation();
    public static boolean 		        	showAverotAtraot = false;
    public static boolean 		        	showLoadDetails = false;
    public static boolean 		        	showPopupText = false;
    public Intent serviceIntent;
    public static boolean                   parkingQRSwWork = false;
    public static boolean                   generalQRSwWork = false;
    public static boolean                   managementQRSwWork = false;
    public static String                    extraPicture = null;
    public static String                    streetName, version, streetNumber;
    public static short                     location;
    public static int                       Lk, violation, user, streetCode, timeout;
    public static boolean                   breakMode = false;
    public static boolean                   motorcycleToo = true;
    public static boolean                   highAccuracy = false;
    public static int                       countVerification = 2;

    @Override
    public void onCreate() {
        super.onCreate();
//      Fabric.with(this, new Crashlytics());
        gContext = this;
        serviceIntent = new Intent(this, TicketsAndPicturesService.class);
        if(isServiceRunning()) {
            Log.i("UCApp","onRunning()");
            bindService(serviceIntent,//TicketsAndPicturesService.getIntent(UCApp.gContext),
                    mConnection,
                    Context.BIND_AUTO_CREATE);
        }
        else {
            try {
                Log.i("UCApp", "onCreate()");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForegroundService(serviceIntent);
                } else {
                    startService(serviceIntent);
                }
                bindService(serviceIntent,//TicketsAndPicturesService.getIntent(UCApp.gContext),
                        mConnection,
                        Context.BIND_AUTO_CREATE);
            }
            catch (Exception e) {}
            catch (Error e) {}
        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            ticketsAndPicturesBinder = (TicketsAndPicturesService.TicketsAndPicturesBinder) service;
            if(ticketsAndPicturesBinder != null)
                ticketsAndPicturesBinder.setLoadOnlyTickets(true);
//              ticketsAndPicturesBinder.setLoadOnlyTickets(TicketInformation.hasToLoadTickets());
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };

    private boolean isServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)){
            Log.i("UCApp",service.service.getClassName());
            if("com.lbmotion.service.TicketsAndPicturesService".equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public static void initApp() {
        shiftHoursLength = 1440;
        pakachSelectedItemWarning = -1;
        doEndOfShift = false;
        pakachMandatoryRemark = -1;
        isDoingPreviousWarning = false;
        isDoingPreviousOpenEvent = false;
        isDoingAssiment = false;
        doExitNow = false;
        parkingAllowed = false;
        generalAllowed = false;
        managementAllowed = false;
        parkingQRSwWork = false;
        generalQRSwWork = false;
        managementQRSwWork = false;
    }

    public static void setQRSwWork() {
        if (UCApp.parkingQRSwWork)
            UCApp.parkingAllowed = true;
        if (UCApp.generalQRSwWork)
            UCApp.generalAllowed = true;
        if (UCApp.managementQRSwWork)
            UCApp.managementAllowed = true;
    }

    public static void readQRSwWork() {
        UCApp.parkingQRSwWork = getQRSwWorkParking();
        UCApp.generalQRSwWork = getQRSwWorkGeneral();
        UCApp.managementQRSwWork = getQRSwWorkManagement();
    }

    public static boolean getQRSwWorkParking() {
        boolean found = false;
        for(int i = 0; i < DB.allViolations.size(); i++) {
            if(DB.allViolations.get(i).Sivug == 0) {
                 if(!DB.allViolations.get(i).QRSwWork)
                    return false;
                found = true;
            }
        }
        return found;
    }

    public static boolean getQRSwWorkGeneral() {
        boolean found = false;
        for(int i = 0; i < DB.allViolations.size();i++) {
            if(DB.allViolations.get(i).Sivug == 1) {
                if(!DB.allViolations.get(i).QRSwWork)
                    return false;
                found = true;
            }
        }
        return found;
    }

    public static boolean getQRSwWorkManagement() {
        boolean found = false;
        for(int i = 0; i < DB.allViolations.size();i++) {
            if(DB.allViolations.get(i).Sivug == 2) {
                if(!DB.allViolations.get(i).QRSwWork)
                    return false;
                found = true;
            }
        }
        return found;
    }
}
