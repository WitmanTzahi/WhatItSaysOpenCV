package com.lbmotion.whatitsays;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.RectF;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;
import com.lbmotion.whatitsays.data.TicketInformation;
import com.lbmotion.whatitsays.location.LocationUpdatesService;
import com.lbmotion.whatitsays.model.Classification;
import com.lbmotion.whatitsays.model.ClassifierPB;
import com.lbmotion.whatitsays.util.ParkingQuery;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvException;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.lbmotion.whatitsays.data.TicketInformation.openFile;
import static org.opencv.imgcodecs.Imgcodecs.IMREAD_COLOR;

//https://www.programcreek.com/java-api-examples/?class=org.opencv.core.Mat&method=channels
public class MainActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2, LicenseQueryCompleted {
    public static final String      TAG = "MainActivity";

    public final static String      LICENSE_PLATE = "LICENSE_PLATE";
    public final static String      DIRECTORY_LICENSE_PLATE = "DIRECTORY_LICENSE_PLATE";

    private static final int        DIM_BATCH_SIZE = 1;
    private static final int        DIM_PIXEL_SIZE = 3;
    private static final int        DIM_IMG_SIZE_X = 224;
    private static final int        DIM_IMG_SIZE_Y = 224;
    private final int[]             intValues = new int[DIM_IMG_SIZE_X * DIM_IMG_SIZE_Y];
    private long                    lastToneGenerator = 0;
    private List<ClassifierPB>      mClassifiers = new ArrayList<>();
    private long                    lastProcessTime = (new Date()).getTime();

    private static  AtomicBoolean   working = new AtomicBoolean(false);
    private CameraBridgeViewBase    _cameraBridgeViewBase;
//  private PortraitCameraBridgeViewBase _cameraBridgeViewBase;

    private Handler                 handler;
    private HandlerThread           handlerThread;

    private int                     scrollListHeight;

    private boolean                 testing = false;
    private ImageView               bigImageView;
    private TextView                bigTextView;
    private LinearLayout            licenseArea;
    private RelativeLayout          nextCarLayout;

    private Vector<ShowLicense>     showLicenses = new Vector<ShowLicense>();

    private Vector<String>          inputFiles = new Vector<String>();

    static {
        System.loadLibrary("native-lib");
        InitPlates();
    }

//  private List<List<FirebaseVisionText.TextBlock>> blocks = new Vector<>();

    // These variables are used (at the moment) to fix camera orientation from 270degree to 0degree
    private Mat                     mRgba;

    // A reference to the service used to get location updates.
    private LocationUpdatesService  mService = null;
    // Tracks the bound state of the service.
    private boolean                 mBound = false;

    private RecyclerView            mPlatesRecyclerView;
    private RecyclerView.Adapter    mPlatesAdapter;
    private RecyclerView.LayoutManager mPlatesLayoutManager;
    private Vector<Plate>           plates = new Vector<>();
    private Vector<Plate>           allPlates = new Vector<>();
    private ParkingQuery            parkingQuery = new ParkingQuery(this);
    private Rect                    roi = null;

    private Mat                     digits[] = {};
    private Mat                     plateClassifaier = null;

    public enum StatesTypes {
        START_NEW_DOCH,
        STEP_TO_GET_CAR_REGISTRATION_NUMBER,
        STEP_TO_GET_GENERAL_INSPECTION_IDS,
        STEP_TO_COMPLETE_GENERAL_INSPECTION_DOCH,
        STEP_TO_COMPLETE_CAR_DOCH
    }

    private String                  topLicens_licensePlate = "";
    private String                  topLicens_directoryLicensePlate = "";
    private int                     topLicens_count = 0;
    private AtomicBoolean           topLicens_on = new AtomicBoolean(false);
    private AtomicBoolean           topLicens_send = new AtomicBoolean(true);

    private static Vector<TicketInformation> ticketInformations = null;

    public synchronized void notifyQueryCompletion(final String licensePlate, final String directoryLicensePlate, int packachDecisionCode) {
        int count = 0;
        synchronized (allPlates) {
            for(Plate plate : allPlates) {
                if(plate.originalLicense.equals(licensePlate)) {
                    count = plate.count;
                }
                if(plate.originalLicense.equals(topLicens_licensePlate)) {
                    topLicens_count = plate.count;
                }
            }
        }
        if(UCApp.breakMode && packachDecisionCode == 1) {
            Log.i(TAG, "notifyQueryCompletion:"+count);
            if (count > UCApp.countVerification) {
                topLicens_send.set(false);
                if(count > topLicens_count)
                    gotoUrbanControl(licensePlate, directoryLicensePlate);
                else
                    gotoUrbanControl(topLicens_licensePlate, topLicens_directoryLicensePlate);
            }
            else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(UCApp.doNotQuery)
                            plates = bestLicensePlatesDisplay();
                        try {mPlatesAdapter.notifyDataSetChanged();} catch (Exception e) {} catch (Error e) {}
                    }
                });
                if(count > topLicens_count && count > 1) {
                    topLicens_licensePlate = licensePlate;
                    topLicens_directoryLicensePlate = directoryLicensePlate;
                    topLicens_count = count;
                    if(!topLicens_on.get()) {
                        topLicens_on.set(true);
                        new Thread() { public void run() {
                            try { Thread.sleep(7500);} catch (InterruptedException ie) {}
                            if(topLicens_send.get()) {
                                gotoUrbanControl(topLicens_licensePlate, topLicens_directoryLicensePlate);
                            }
                        }}.start();
                    }
                }
            }
        }
        else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(UCApp.doNotQuery)
                        plates = bestLicensePlatesDisplay();
                    try {mPlatesAdapter.notifyDataSetChanged();} catch (Exception e) {} catch (Error e) {}
                }
            });
        }
    }

    private void deleteRemovedLicenses() {
        File dir = openFile(getPath("Licenses"));
        File[] licensePlates = dir.listFiles();
        for (int i = 0; licensePlates != null && i < licensePlates.length; i++) {
            boolean found = false;
            for (Plate plate : plates) {
                if(licensePlates[i].getName().equals(plate.license)) {
                    found = true;
                    break;
                }
            }
            if(!found) {
                try {
                    deleteAllFiles(licensePlates[i]);
                }
                catch(Exception e) {}
            }
        }
        File [] plateFiles = openFile(getPath("CarPics")).listFiles();
        for (int i = 0; plateFiles != null && i < plateFiles.length; i++) {
            boolean found = false;
            for (Plate plate : plates) {
                if(plateFiles[i].getName().equals(plate.license)) {
                    found = true;
                    break;
                }
            }
            if(!found) {
                try {
                    deleteAllFiles(plateFiles[i]);
                }
                catch(Exception e) {}
            }
        }
    }

    private void deleteAllFiles(File file) {
        if(file.isDirectory()) {
            File [] files = file.listFiles();
            for (int i = 0; files != null && i < files.length;i++) {
                deleteAllFiles(files[i]);
            }
        }
        file.delete();
    }

    private AtomicBoolean doExit = new AtomicBoolean(false);
    private void gotoUrbanControl(final String licensePlate, final String directoryLicensePlate) {
        if(!doExit.get()) {
            doExit.set(true);
            deleteRemovedLicenses();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ParkingQuery.endTime = 0;
                    (new ToneGenerator(AudioManager.STREAM_ALARM, ToneGenerator.MAX_VOLUME)).startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 1500);
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra(MainActivity.LICENSE_PLATE, licensePlate);
                    resultIntent.putExtra(MainActivity.DIRECTORY_LICENSE_PLATE, directoryLicensePlate);
                    MainActivity.this.setResult(RESULT_OK, resultIntent);
                    clearData();
                    MainActivity.this.finish();
                }
            });
        }
    }

    private Vector<Plate> bestLicensePlatesDisplay() {
        Vector<Plate> tempPlates = new Vector<>();
        if(allPlates.size() > 0) {
            synchronized (allPlates) {
                for(Plate plate : allPlates)
                    plate.license = plate.originalLicense;
                tempPlates.addAll(allPlates);
            }
            boolean search = false;
            int i = 0;
            while(!search) {
                search = true;
                for (; i < tempPlates.size() && search; i++) {
                    String license = tempPlates.get(i).license;
                    for (int n = i + 1; n < tempPlates.size(); n++) {
                        String licenseSub = tempPlates.get(n).license;
                        if (i >= 0 && n >= 0 && licenseSub.length() != license.length()) {
                            if (licenseSub.indexOf(license) != -1 || license.indexOf(licenseSub) != -1 || isMissingOneDigit(license, licenseSub)) {
                                if (tempPlates.get(i).count > tempPlates.get(n).count) {
                                    tempPlates.remove(tempPlates.get(n));
                                    n--;
                                }
                                else {
                                    tempPlates.remove(tempPlates.get(i));
                                    i--;
                                }
                                search = false;
                            }
                        } else if (i >= 0 && n >= 0 && !isDifferentByMoreThanOneDigit(license, licenseSub)) {
                            if (tempPlates.get(i).count > tempPlates.get(n).count) {
                                tempPlates.remove(tempPlates.get(n));
                                n--;
                            }
                            else {
                                tempPlates.remove(tempPlates.get(i));
                                i--;
                            }
                            search = false;
                        } else if (i >= 0 && n >= 0 && license.substring(1).equals(licenseSub.substring(0,licenseSub.length()-1))) {
                            if (tempPlates.get(i).count == tempPlates.get(n).count) {
                                tempPlates.get(i).license += licenseSub.substring(licenseSub.length()-1);
                                tempPlates.remove(tempPlates.get(n));
                                n--;
                            }
                            else if (tempPlates.get(i).count > tempPlates.get(n).count) {
                                tempPlates.remove(tempPlates.get(n));
                                n--;
                            }
                            else {
                                tempPlates.remove(tempPlates.get(i));
                                i--;
                            }
                            search = false;
                        } else if (i >= 0 && n >= 0 && licenseSub.substring(1).equals(license.substring(0,license.length()-1))) {
                            if (tempPlates.get(i).count == tempPlates.get(n).count) {
                                tempPlates.get(n).license += license.substring(license.length()-1);
                                tempPlates.remove(tempPlates.get(i));
                                i--;
                            }
                            else if (tempPlates.get(i).count > tempPlates.get(n).count) {
                                tempPlates.remove(tempPlates.get(n));
                                n--;
                            }
                            else {
                                tempPlates.remove(tempPlates.get(i));
                                i--;
                            }
                            search = false;
                        }
                    }
                }
            }
        }
        synchronized (ParkingQuery.licensePlates) {
            for (Plate plate : tempPlates)
                ParkingQuery.licensePlates.put(plate.originalLicense, plate.license);
        }
        return tempPlates;
    }

    public synchronized void notifyExceptionPlates() {
        try {
            final Vector<Plate> tempPlates = bestLicensePlatesDisplay();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    synchronized (plates) {
                        mPlatesRecyclerView.setAdapter(null);
                        plates.removeAllElements();
                        plates.addAll(tempPlates);
                        if(!UCApp.doNotQuery) {
                            Log.i(TAG, "notifyExceptionPlates1:" + plates.size());
                            try {
                                for (Plate plate : plates) {
                                    Log.i(TAG, "notifyExceptionPlates2:" + plate.license + ":" + plate.originalLicense);
                                    if (plate.count <= UCApp.countVerification)
                                        plates.remove(plate);
                                }
                            } catch (Exception e) {
                            }
                            Log.i(TAG, "notifyExceptionPlates3:"+plates.size());
                        }
                        mPlatesRecyclerView.setAdapter(mPlatesAdapter);
                    }
                    try {mPlatesAdapter.notifyDataSetChanged();} catch (Exception e) {} catch (Error e) {}
                }
            });
        } catch (Exception e) {
            Log.i(TAG, "notifyExceptionPlates:"+e.getMessage());
            Log.e(TAG, "exception", e);
        } catch (Error e) {
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_main);

        if(testing)
            readFiles();

        topLicens_licensePlate = "";
        topLicens_directoryLicensePlate = "";
        topLicens_count = 0;
        topLicens_on = new AtomicBoolean(false);
        topLicens_send = new AtomicBoolean(true);

        createClassifier();

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;
// action bar height
        int actionBarHeight = 0;
        final TypedArray styledAttributes = getTheme().obtainStyledAttributes(
                new int[]{android.R.attr.actionBarSize}
        );
        actionBarHeight = (int) styledAttributes.getDimension(0, 0);
        styledAttributes.recycle();

        nextCarLayout = findViewById(R.id.next_car);
        nextCarLayout.setVisibility(View.GONE);

        scrollListHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 90, getResources().getDisplayMetrics());
        int extra = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, getResources().getDisplayMetrics());


        _cameraBridgeViewBase = (CameraBridgeViewBase) findViewById(R.id.camera);
//      _cameraBridgeViewBase = (PortraitCameraBridgeViewBase) findViewById(R.id.camera);
        _cameraBridgeViewBase.setCvCameraViewListener(this);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            _cameraBridgeViewBase.setMaxFrameSize(width, height - actionBarHeight - scrollListHeight - extra);
        } else {
            _cameraBridgeViewBase.setMaxFrameSize(width - scrollListHeight, height - actionBarHeight);
        }
        _cameraBridgeViewBase.setVisibility(SurfaceView.VISIBLE);

        if (Build.VERSION.SDK_INT >= 23) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                            Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, /*Manifest.permission.ACCESS_BACKGROUND_LOCATION*/},
                    1);
        }

        findViewById(R.id.test_mode).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                testing = !testing;
                setButtons();
                if(testing) {
                    readFiles();
                    synchronized (allPlates) {
                        allPlates.removeAllElements();
                    }
                    synchronized (ticketInformations) {
                        for(TicketInformation ticketInformation : ticketInformations) {
                            ticketInformation.pictures.removeAllElements();
                        }
                    }
                    notifyExceptionPlates();
                }
            }
        });
        setButtons();
        bigImageView = findViewById(R.id.big_image);
        bigImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int index = view.getId();
                if (index < allPlates.size()) {
                    gotoUrbanControl(allPlates.get(index).license, allPlates.get(index).originalLicense);
                }
            }
        });
        bigTextView = findViewById(R.id.big_text);
        licenseArea = findViewById(R.id.license_area);

        mPlatesRecyclerView = (RecyclerView) findViewById(R.id.plates);
        // use a linear layout manager
        mPlatesLayoutManager = new LinearLayoutManager(this);
        mPlatesRecyclerView.setLayoutManager(mPlatesLayoutManager);
        // specify an adapter (see also next example)
        mPlatesAdapter = new PlatesAdapter();
        mPlatesRecyclerView.setAdapter(mPlatesAdapter);
        try {
            // use this setting to improve performance if you know that change in content do not change the layout size of the RecyclerView
            mPlatesRecyclerView.setHasFixedSize(true);
        }
        catch (Exception e) {}
        catch (Error e) {}

        TicketInformation.createWorkingDirectory();
        if (savedInstanceState == null) {
            Bundle extraBundle = getIntent().getExtras();
            if (extraBundle != null) {
                if (savedInstanceState == null)
                    startQueryProcessing(extraBundle);
            }
            else {
                if (ticketInformations == null) {
                    startQueryProcessing(extraBundle);
//                  Intent intent = new Intent(this, LoginActivity.class);
//                  startActivity(intent);
                }
            }
        }
        ((TextView)findViewById(R.id.operation_mode)).setText(UCApp.breakMode?R.string.mode1:R.string.mode2);
    }

    private void setButtons() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ((Button)findViewById(R.id.test_mode)).setVisibility(View.GONE);
                if(testing)
                    ((Button)findViewById(R.id.test_mode)).setText(R.string.live);
                else
                    ((Button)findViewById(R.id.test_mode)).setText(R.string.test);
            }
        });
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        UCApp.streetNumber = savedInstanceState.getString("streetNumber");
        UCApp.streetName = savedInstanceState.getString("streetName");
        UCApp.version = savedInstanceState.getString("version");
        UCApp.Lk = savedInstanceState.getInt("Lk");
        UCApp.violation = savedInstanceState.getInt("violation");
        UCApp.streetCode = savedInstanceState.getInt("streetCode");
        UCApp.user = savedInstanceState.getInt("user");
        UCApp.location = savedInstanceState.getShort("location");
        UCApp.breakMode = savedInstanceState.getBoolean("breakMode");
        UCApp.doNotQuery = savedInstanceState.getBoolean("doNotQuery");
        UCApp.motorcycleToo = savedInstanceState.getBoolean("motorcycleToo");
        testing = savedInstanceState.getBoolean("testing");
        ParkingQuery.endTime = savedInstanceState.getLong("endTime");
        ParkingQuery.licensePlates = (HashMap<String,String>) savedInstanceState.getSerializable("licensePlates");
        ParkingQuery.resultLicensePlates = (HashMap<String,Integer>) savedInstanceState.getSerializable("resultLicensePlates");
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putString("streetNumber", UCApp.streetNumber);
        savedInstanceState.putString("streetName", UCApp.streetName);
        savedInstanceState.putString("version", UCApp.version);
        savedInstanceState.putInt("Lk", UCApp.Lk);
        savedInstanceState.putInt("violation", UCApp.violation);
        savedInstanceState.putInt("streetCode", UCApp.streetCode);
        savedInstanceState.putInt("user", UCApp.user);
        savedInstanceState.putShort("location", UCApp.location);
        savedInstanceState.putBoolean("breakMode", UCApp.breakMode);
        savedInstanceState.putBoolean("doNotQuery", UCApp.doNotQuery);
        savedInstanceState.putBoolean("motorcycleToo", UCApp.motorcycleToo);
        savedInstanceState.putBoolean("testing", testing);
        savedInstanceState.putLong("endTime", ParkingQuery.endTime);
        savedInstanceState.putSerializable("licensePlates", ParkingQuery.licensePlates);
        savedInstanceState.putSerializable("resultLicensePlates", ParkingQuery.resultLicensePlates);
    }

    private void startQueryProcessing(Bundle extraBundle) {
        File file = openFile(getPath("CarPics")+"Done");
        file.delete();
        ticketInformations = new Vector<TicketInformation>();
        if(extraBundle == null || extraBundle.getString("StreetNumber") == null) {
            UCApp.version = "1.1";
            UCApp.Lk = 1547;
            UCApp.violation = 1;
            UCApp.location = 1;
            UCApp.streetCode = 1;
            UCApp.streetName = "StreetName";
            UCApp.user = 10;
            UCApp.streetNumber = "1";
            UCApp.breakMode = false;
            UCApp.doNotQuery = true;
            UCApp.motorcycleToo = true;
            UCApp.highAccuracy = false;
            UCApp.timeout = 20;
            UCApp.countVerification = 2;
        }
        else {
            UCApp.version = extraBundle.getString("Version");
            UCApp.Lk = extraBundle.getInt("LK");
            UCApp.violation = extraBundle.getInt("Violation");
            UCApp.location = extraBundle.getShort("Location");
            UCApp.streetCode = extraBundle.getInt("StreetCode");
            UCApp.streetName = extraBundle.getString("StreetName");
            UCApp.user = extraBundle.getInt("User");
            UCApp.streetNumber = extraBundle.getString("StreetNumber");
            UCApp.breakMode = extraBundle.getBoolean("BreakMode");
            UCApp.timeout = extraBundle.getInt("timeout");
            UCApp.doNotQuery = extraBundle.getBoolean("DoNotQuery");
            try {UCApp.motorcycleToo = extraBundle.getBoolean("SupportMotorcycle",true);} catch (Exception e) {}
            try {UCApp.highAccuracy = extraBundle.getBoolean("HighAccuracy",false);} catch (Exception e) {}
            try {UCApp.countVerification = extraBundle.getInt("CountVerification",2);} catch (Exception e) {}
        }
        saveConfigFile();
        plates = new Vector<>();
        if(!UCApp.doNotQuery) {
            new Thread() { public void run() {
                try {Thread.sleep(5000); } catch (InterruptedException ie) { }
                parkingQuery.run();
            }}.start();
        }
    }

    protected synchronized void runInBackground(final Runnable r) {
        if (handler != null) {
            handler.post(r);
        }
    }

    private BaseLoaderCallback _baseLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    Log.i(TAG, "OpenCV loaded successfully");
                    Mat tempDigits[] = {new Mat(),new Mat(),new Mat(),new Mat(),new Mat(),new Mat(),new Mat(),new Mat(),new Mat(),new Mat()};
                    digits = tempDigits;
                    plateClassifaier = new Mat();
                    // Load ndk built module, as specified in moduleName in build.gradle after opencv initialization
                    _cameraBridgeViewBase.enableView();
                }
                break;
                default: {
                    super.onManagerConnected(status);
                }
            }
        }
    };

    @Override
    public void onPause() {
        handlerThread.quitSafely();
        try {
            handlerThread.join();
            handlerThread = null;
            handler = null;
        } catch (InterruptedException e) {
        } catch (Exception e) {
        }
        try {
            super.onPause();
            disableCamera();
        } catch (Exception e) {
        } catch (Error e) {
        }
    }

    @Override
    public void onBackPressed() {
        deleteRemovedLicenses();
        disableCamera();
        ParkingQuery.endTime = 0;
        Intent resultIntent = new Intent();
        setResult(RESULT_OK, resultIntent);
        clearData();
        finish();
        super.onBackPressed();
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            super.onResume();
            handlerThread = new HandlerThread("inference");
            handlerThread.start();
            handler = new Handler(handlerThread.getLooper());
            if (!OpenCVLoader.initDebug()) {
                Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
                OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION, this, _baseLoaderCallback);
            } else {
                Log.d(TAG, "OpenCV library found inside package. Using it!");
                _baseLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
            }
        } catch (Exception e) {
        } catch (Error e) {
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onDestroy 1:");
        try {
            disableCamera();
        } catch (Exception e) {
            Log.e(TAG, "onDestroy 2:"+e.getMessage());
        } catch (Error e) {
            Log.e(TAG, "onDestroy 3:"+e.getMessage());
        }
        Log.e(TAG, "onDestroy 4:");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the contacts-related task you need to do.
                } else {
                    // permission denied, boo! Disable the functionality that depends on this permission.
                    Toast.makeText(MainActivity.this, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
                }
                return;
            }
            // other 'case' lines to check for other permissions this app might request
        }
    }

    public void onCameraViewStopped() {
        Log.e(TAG, "onCameraViewStopped 1:");
        mRgba.release();
    }

    public void onCameraViewStarted(int width, int height) {
        Log.e(TAG, "onCameraViewStarted 1:");
        mRgba = new Mat(height, width, CvType.CV_8UC4);
    }

    public void disableCamera() {
        try {
            if (_cameraBridgeViewBase != null)
                _cameraBridgeViewBase.disableView();
        } catch (Exception e) {
            Log.e(TAG, "disableCamera:" + e.getMessage());
        }
    }

    static int pic_index = 0;
    private void getNextFlate() {
        if(pic_index < inputFiles.size()) {
            mRgba = Imgcodecs.imread(inputFiles.get(pic_index), IMREAD_COLOR);
//          Imgproc.cvtColor(mRgba, mRgba, Imgproc.COLOR_BGR2GRAY);
            Imgproc.cvtColor(mRgba, mRgba, Imgproc.COLOR_BGR2RGB);
            if(!working.get())
                pic_index++;
        }
        else {
            testing = false;
            notifyExceptionPlates();
            setButtons();
            pic_index = 0;
        }
    }

    private void clearData() {
        plates = new Vector<>();
        allPlates = new Vector<>();
        topLicens_licensePlate = "";
        topLicens_directoryLicensePlate = "";
        topLicens_count = 0;
        topLicens_on = new AtomicBoolean(false);
        topLicens_send = new AtomicBoolean(true);
    }

//  public Mat onCameraFrame(PortraitCameraBridgeViewBase.CvCameraViewFrame inputFrame) {
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        if (ParkingQuery.getCarInformationId() == null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    disableCamera();
                    ParkingQuery.endTime = 0;
                    Intent resultIntent = new Intent();
                    setResult(RESULT_OK, resultIntent);
                    deleteRemovedLicenses();
                    clearData();
                    MainActivity.this.finish();
                }
            });
            return inputFrame.rgba();
        }
        if(working.get() && ((new Date()).getTime()-lastProcessTime)/1000 >= 10)
            working.set(false);
//      if (channels.size() > 0 && channels.get(0).cols() > 0 && channels.get(0).rows() > 0 && !working.get()) {
        if (!working.get()) {
            if(testing)
                getNextFlate();
            else
                mRgba = inputFrame.rgba();
            List<Mat> channels = new ArrayList<Mat>();
            Core.split(mRgba, channels);
            lastProcessTime = (new Date()).getTime();
            working.set(true);
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                Mat rgba = channels.get(0).t();
                clearMatArray(channels);
                Core.flip(rgba, rgba, 1);
                final Bitmap bm = Bitmap.createBitmap(rgba.cols(), rgba.rows(), Bitmap.Config.ARGB_8888);
                Utils.matToBitmap(rgba, bm);
                rgba.release();
                runInBackground(() -> processImage(bm, mRgba));
            } else {
                final Bitmap bm = Bitmap.createBitmap(channels.get(0).cols(), channels.get(0).rows(), Bitmap.Config.ARGB_8888);
                Utils.matToBitmap(channels.get(0), bm);
                clearMatArray(channels);
                runInBackground(() -> processImage(bm, mRgba));
            }
        }
//        List<FirebaseVisionText.TextBlock> block = null;
//        synchronized (blocks) {
//            if (blocks.size() > 0) {
//                block = blocks.get(blocks.size() - 1);
//                blocks.remove(blocks.size() - 1);
//            }
//        }
//        if (block != null) {
//
//        }
        return inputFrame.rgba();
    }

    private void clearMatArray(List<Mat> channels) {
        for(int i = 0;i <= 3;i++)
            if(channels.size() > i)
                channels.get(i).release();
        channels.clear();
    }

    public synchronized String doClassifiers(final Mat image) {
        Log.i(TAG, "doClassifiers start:");
        String licenseNumbers = "";
        try {
            synchronized(licenseNumbers) {
                long digitsCount = GetPlates(image.getNativeObjAddr(),
                        plateClassifaier.getNativeObjAddr(),
                        digits[0].getNativeObjAddr(), digits[1].getNativeObjAddr(), digits[2].getNativeObjAddr(), digits[3].getNativeObjAddr(),
                        digits[4].getNativeObjAddr(), digits[5].getNativeObjAddr(), digits[6].getNativeObjAddr(), digits[7].getNativeObjAddr(),
                        digits[8].getNativeObjAddr(), digits[9].getNativeObjAddr());
                Log.i(TAG, "doClassifiers end:" + digitsCount);
                if (digitsCount > 6) {//5
                    for (int i = 0; i < digitsCount && i < digits.length; i++) {
                        if (digits[i].cols() > 0) {
                            if (mClassifiers.size() > 0 && mClassifiers.get(0) != null) {
                                SetDigit(digits[i].getNativeObjAddr());
                                Classification number = mClassifiers.get(0).recognize(digits[i]);
                                if (number.getLabel() != null && !number.getLabel().equals("A"))
                                    licenseNumbers += number.getLabel();
                            }
                        }
                    }
                } else {
                    for (int i = 0; i < digits.length; i++)
                        digits[i].release();
                }
                Log.i(TAG, "doClassifiers end:" + licenseNumbers);
            }
        }
        catch (Exception e) {
            Log.i(TAG, "onCameraFrame:"+e.getMessage());
        }
        catch (Error e) {
            Log.i(TAG, "onCameraFrame:"+e.getMessage());
        }
        return licenseNumbers;
    }

    public void processImage(Bitmap image, Mat rgba) {
        runTextRecognition(image, rgba);
    }

    private void runTextRecognition(final Bitmap picture, final Mat rgba) {
        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(picture);
        FirebaseVisionTextRecognizer recognizer = FirebaseVision.getInstance().getOnDeviceTextRecognizer();
        recognizer.processImage(image)
                .addOnSuccessListener(
                        new OnSuccessListener<FirebaseVisionText>() {
                            @Override
                            public void onSuccess(FirebaseVisionText texts) {
                                try {
                                    processTextRecognitionResult(picture, texts, rgba);
                                    picture.recycle();
                                }
                                catch (Exception e) {}
                                catch (Error e) {}
                                System.gc();
                                working.set(false);
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Task failed with an exception
                                e.printStackTrace();
                                picture.recycle();
                                System.gc();
                                working.set(false);
                            }
                        });
    }

    private void processTextRecognitionResult(Bitmap picture, FirebaseVisionText texts, Mat rgba) {
        List<FirebaseVisionText.TextBlock> block = texts.getTextBlocks();
//        synchronized (blocks) {
//            blocks.add(block);
//        }
        String selectLicensePlate = "";
        boolean licensePlateMotorcycleFlag = false;
        Rect selectedRect = new Rect();
        for (int i = 0; i < block.size(); i++) {
            List<FirebaseVisionText.Line> lines = block.get(i).getLines();
            for (int j = 0; j < lines.size(); j++) {
                List<FirebaseVisionText.Element> elements = lines.get(j).getElements();
                for (int k = 0; k < elements.size(); k++) {
                    RectF rectF = new RectF(elements.get(k).getBoundingBox());
                    Rect rect = new Rect();
                    rect.x = (int) rectF.left;
                    rect.y = (int) rectF.top;
                    rect.height = (int) (rectF.bottom - rectF.top);
                    rect.width = (int) (rectF.right - rectF.left);
                    if (rect.height > 0 && Math.abs(rect.width / (float) rect.height - 4.7272) < 4 && rect.height * 18 > rgba.rows()) {
                        String text = elements.get(k).getText();
                        String licensePlate = "";
                        for (int n = 0; n < text.length(); n++) {
                            if (Character.isDigit(text.charAt(n)))
                                licensePlate += text.charAt(n);
                        }
                        licensePlate = licensePlate.replace("-", "");
                        licensePlate = licensePlate.replace(".", "");
                        licensePlate = licensePlate.replace(":", "");
                        if (licensePlate.length() >= 7 && licensePlate.length() <= 8 && licensePlate.indexOf('/') == -1 && licensePlate.charAt(0) != '0') {
                            selectLicensePlate = licensePlate;
                            selectedRect = rect;
                            licensePlateMotorcycleFlag = false;
                        }
                        if(UCApp.motorcycleToo && licensePlate.length() >= 5) {
                            for (int jj = j + 1; jj <= j + 1 && jj < lines.size();jj++) {
                                elements = lines.get(jj).getElements();
                                for (int kk = 0; kk < elements.size(); kk++) {
                                    RectF rectF_kk = new RectF(elements.get(kk).getBoundingBox());
                                    Rect rect_kk = new Rect();
                                    rect_kk.x = (int) rectF_kk.left;
                                    rect_kk.y = (int) rectF_kk.top;
//                                  rect_kk.height = (int) (rectF_kk.bottom - rectF_kk.top);
//                                  rect_kk.width = (int) (rectF_kk.right - rectF_kk.left);
                                    if(rect.contains(new Point(rect_kk.x, rect_kk.y))) {
                                        text = elements.get(kk).getText();
                                        String licensePlateMotorcycle = "";
                                        for (int n = 0; n < text.length(); n++) {
                                            if (Character.isDigit(text.charAt(n)))
                                                licensePlateMotorcycle += text.charAt(n);
                                        }
                                        licensePlateMotorcycle = licensePlateMotorcycle.replace("-", "");
                                        licensePlateMotorcycle = licensePlateMotorcycle.replace(".", "");
                                        licensePlateMotorcycle = licensePlateMotorcycle.replace(":", "");
                                        if ((licensePlateMotorcycle.length() == 2 || licensePlateMotorcycle.length() == 3) && licensePlate.indexOf('/') == -1) {
                                            selectLicensePlate = licensePlate+licensePlateMotorcycle;
                                            licensePlateMotorcycleFlag = true;
                                            selectedRect = rect;
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
//      Log.i(TAG, "processTextRecognitionResult:"+selectLicensePlate);
        if (selectLicensePlate.length() >= 7 && selectLicensePlate.length() <= 8 && selectLicensePlate.charAt(0) != '0') {
//          if (mService != null && mService.getLocation() != null && !matchAllButOne(selectLicensePlate)) {
            if (mService != null) {
                Log.i(TAG, selectLicensePlate);
                Rect licenseRect = new Rect(selectedRect.x,selectedRect.y,selectedRect.width,selectedRect.height);
                if(roi == null)
                    roi = new Rect(rgba.cols() * 36 / 100, (int)(rgba.rows() * 45 / 100), rgba.cols() * 28 / 100, rgba.rows() * 10 / 100);
                selectedRect.x = (selectedRect.x+selectedRect.width/2)-roi.width/2;
                selectedRect.y = (selectedRect.y+selectedRect.height/2)-roi.height/2;
                selectedRect.x = Math.max(selectedRect.x,0);
                selectedRect.y = Math.max(selectedRect.y,0);
                selectedRect.width = roi.width;
                selectedRect.height = roi.height;
                if(selectedRect.x+selectedRect.width > rgba.cols())
                    selectedRect.width = rgba.cols()-selectedRect.x-1;
                if(selectedRect.y+selectedRect.height > rgba.rows())
                    selectedRect.height = rgba.rows()-selectedRect.y-1;
                String licensePlateSecond = "";
                boolean changePlate = false;
                if(!licensePlateMotorcycleFlag && UCApp.highAccuracy) {
                    try {
                        licensePlateSecond = doClassifiers(new Mat(rgba, licenseRect/*selectedRect*/));
                    } catch (Exception e) {
                    } catch (Error e) {}
                    if (licensePlateSecond.length() > 6) {
                        selectLicensePlate = licensePlateSecond;
                        changePlate = true;
                    }
                }
                if (isPlatesContains(selectLicensePlate)) {
                    synchronized (ticketInformations) {
                        for(TicketInformation ticketInformation : ticketInformations) {
                            if (ticketInformation.pictures.size() < 5 && ticketInformation.CarInformationId.equals(selectLicensePlate)) {
                                String filename = writeFileCar(rgba, selectLicensePlate, selectedRect, ticketInformation.pictures.size());
                                if (filename.length() > 0) {
                                    ticketInformation.pictures.add(filename);
                                    synchronized (allPlates) {
                                        for (Plate plate : allPlates) {
                                            if (plate.originalLicense.equals(selectLicensePlate)) {
                                                plate.count = ticketInformation.pictures.size();
                                                if (!plate.accurate && changePlate) {
                                                    plate.accurate = true;
                                                    if( plate.image  != null) {
                                                        plate.image.recycle();
                                                        System.gc();
                                                    }
                                                    plate.image = Bitmap.createBitmap(plateClassifaier.cols(), plateClassifaier.rows(), Bitmap.Config.ARGB_8888);
                                                    Utils.matToBitmap(plateClassifaier, plate.image);
                                                    writeFileLicense(plate.image, selectLicensePlate, selectedRect);
                                                }
                                                showNextCar(allPlates, plate);
                                                notifyExceptionPlates();
                                                break;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else {
                    TicketInformation ticketInformation = new TicketInformation();
                    ticketInformation.carOrGM = false;
                    ticketInformation.CarInformationId = selectLicensePlate;
                    ticketInformation.timestamp = (new Date()).getTime();
                    String filename = writeFileCar(rgba, selectLicensePlate, selectedRect , 0);
                    if (filename.length() > 0) {
                        Bitmap image;
                        if(changePlate) {
                            image = Bitmap.createBitmap(plateClassifaier.cols(), plateClassifaier.rows(), Bitmap.Config.ARGB_8888);
                            Utils.matToBitmap(plateClassifaier, image);
                        }
                        else {
                            if(picture.getWidth()/3 <= licenseRect.width) {
                                image = Bitmap.createBitmap(picture, licenseRect.x, licenseRect.y, licenseRect.width, licenseRect.height);
                            }
                            else {
                                int x = licenseRect.x + licenseRect.width / 8;
                                int w = licenseRect.width / 8 * 6 + 50;
                                if(picture.getWidth() < x + w)
                                    w = picture.getWidth() - x - 1;
                                image = Bitmap.createBitmap(picture, x, licenseRect.y, w, licenseRect.height);
                            }
                        }
                        ticketInformation.pictures.add(filename);
                        synchronized (ticketInformations) {
                            ticketInformations.add(ticketInformation);
                        }
                        Plate plate = new Plate();
                        plate.originalLicense = plate.license = selectLicensePlate;
                        plate.image = image;
                        plate.count = ticketInformation.pictures.size();
                        plate.index = allPlates.size();
                        plate.accurate = changePlate;
                        writeFileLicense(plate.image, selectLicensePlate, selectedRect);
                        synchronized (allPlates) {
//                          if (allPlates.size() == 0)
                            allPlates.add(plate);
//                              else
//                                  allPlates.insertElementAt(plate, 0);
                            showNextCar(allPlates, plate);
                        }
                        notifyExceptionPlates();
                    }
                }
            }
        }
        if(UCApp.doNotQuery) {
            notifyExceptionPlates();
        }
    }

    /**
     * Writes Image data into a {@code ByteBuffer}.
     */
    private synchronized ByteBuffer convertBitmapToByteBuffer(Bitmap bitmap, int width, int height) {
        ByteBuffer imgData = ByteBuffer.allocateDirect(DIM_BATCH_SIZE * DIM_IMG_SIZE_X * DIM_IMG_SIZE_Y * DIM_PIXEL_SIZE);
        imgData.order(ByteOrder.nativeOrder());
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, DIM_IMG_SIZE_X, DIM_IMG_SIZE_Y, true);
        imgData.rewind();
        scaledBitmap.getPixels(intValues, 0, scaledBitmap.getWidth(), 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight());
        // Convert the image to int points.
        int pixel = 0;
        for (int i = 0; i < DIM_IMG_SIZE_X; ++i) {
            for (int j = 0; j < DIM_IMG_SIZE_Y; ++j) {
                final int val = intValues[pixel++];
                imgData.put((byte) ((val >> 16) & 0xFF));
                imgData.put((byte) ((val >> 8) & 0xFF));
                imgData.put((byte) (val & 0xFF));
            }
        }
        return imgData;
    }

    //////////////////////////////////////

    public String getPath(final String directory) {
        File dir;
        if (android.os.Environment.getExternalStorageState() != null && android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
            File storageDir = android.os.Environment.getExternalStorageDirectory();
            dir = new File(storageDir.getAbsolutePath(), "Hanita");
            if (!dir.exists())
                dir.mkdirs();
            dir = new File(dir.getAbsolutePath(), directory);
        } else {
            dir = getFileStreamPath(directory);
        }
        if (!dir.exists())
            dir.mkdirs();
        return dir.getAbsolutePath() + "/";
    }


    // Monitors the state of the connection to the service.
    private final ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            LocationUpdatesService.LocalBinder binder = (LocationUpdatesService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
            mBound = false;
        }
    };

    @Override
    protected void onStart() {
        Log.e(TAG, "onStart 1:");
        super.onStart();
        // Bind to the service. If the service is in foreground mode, this signals to the service
        // that since this activity is in the foreground, the service can exit foreground mode.
        bindService(new Intent(this, LocationUpdatesService.class), mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        Log.e(TAG, "onStop 1:");
        if (mBound) {
            // Unbind from the service. This signals to the service that this activity is no longer
            // in the foreground, and the service can respond by promoting itself to a foreground
            // service.
            unbindService(mServiceConnection);
            mBound = false;
        }
        super.onStop();
    }

    public class ShowLicense implements Runnable {
        public boolean status = true;
        @Override
        public void run() {
            for(int i = 0;i < 10 && status;i++) {
                try { Thread.sleep(1000);} catch (InterruptedException ie) {}
            }
            if(status) {
                runOnUiThread(() -> {
                    licenseArea.setVisibility(View.INVISIBLE);
                    showLicenses.removeAllElements();
                });
            }
        }
    }

    public class PlatesAdapter extends RecyclerView.Adapter<PlatesAdapter.MyViewHolder> {
        public class MyViewHolder extends RecyclerView.ViewHolder {
            public TextView       textView;
            public ImageView      imageView;
            public RelativeLayout layout;

            public MyViewHolder(View v) {
                super(v);
                textView = v.findViewById(R.id.text);
                imageView = v.findViewById(R.id.image);
                layout = v.findViewById(R.id.image_layout);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int index = view.getId();
                        if (index < allPlates.size()) {
                            licenseArea.setVisibility(View.VISIBLE);
                            bigImageView.setImageBitmap(allPlates.get(index).image);
                            bigTextView.setText(allPlates.get(index).license);
                            bigImageView.setId(index);
                            for (ShowLicense showLicense : showLicenses)
                                showLicense.status = false;
                            showLicenses.removeAllElements();
                            ShowLicense showLicense = new ShowLicense();
                            showLicenses.add(showLicense);
                            licenseArea.setVisibility(View.VISIBLE);
                            new Thread(showLicense).start();
                        }
                    }
                });
                imageView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        int index = view.getId();
                        if (index < allPlates.size()) {
                            gotoUrbanControl(allPlates.get(index).license, allPlates.get(index).originalLicense);
                        }
                        return true;
                    }
                });
            }
        }

        public PlatesAdapter() {
        }

        // Create new views (invoked by the layout manager)
        @Override
        public PlatesAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.plate, parent, false);
            MyViewHolder vh = new MyViewHolder(v);
            return vh;
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            // - get element from your dataset at this position
            // - replace the contents of the view with that element
            int index = 0;
            int count = 0;
            int color = Color.GRAY;
            Bitmap image = null;
            String license = "";
            synchronized (plates) {
                license = plates.get(position).license;
                image = plates.get(position).image;
                count = plates.get(position).count;
                index = plates.get(position).index;
                synchronized (ParkingQuery.resultLicensePlates) {
                    if(ParkingQuery.resultLicensePlates.containsKey(plates.get(position).license)) {
                        int status = ParkingQuery.resultLicensePlates.get(plates.get(position).license).intValue();
                        if(status == 0) {
                            color = Color.GREEN;
                        }
                        else if(status == 1) {
                            if(count <= UCApp.countVerification) {
                                color = Color.YELLOW;
                            }
                            else {
                                color = Color.RED;
                            }
                        }
                    }
                }
            }
            holder.layout.setBackgroundColor(color);
            if(image != null) {
                holder.imageView.setImageBitmap(image);
                holder.imageView.setId(index);
            }
            holder.textView.setText(license + "-" + count);
        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            int size = 0;
            synchronized (plates) {
                size = plates.size();
            }
            return size;
        }
    }

    private void showNextCar(Vector<Plate> plates, Plate plate) {
        int count = 0;
        for(Plate p : plates) {
            if(plate.originalLicense.equals(p.originalLicense))
                count += p.count;
        }
        if(count > UCApp.countVerification) {
            if((new Date()).getTime() > lastToneGenerator) {
                lastToneGenerator = (new Date()).getTime()+2000;
                (new ToneGenerator(AudioManager.STREAM_NOTIFICATION, ToneGenerator.MAX_VOLUME)).startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 1000);
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        nextCarLayout.setVisibility(View.VISIBLE);
                        if(UCApp.doNotQuery)
                            gotoUrbanControl(plate.originalLicense, "");
                    } catch (Exception e) {
                    } catch (Error e) {
                    }
                }
            });
            new Thread() { public void run() {
                try { Thread.sleep(750);} catch (InterruptedException ie) {}
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            nextCarLayout.setVisibility(View.GONE);
                        } catch (Exception e) {
                        } catch (Error e) {
                        }
                    }
                });
            }}.start();
        }
    }

    public static byte statesTypesToByte(StatesTypes s) {
        switch (s) {
            case START_NEW_DOCH:
                return 0;
            case STEP_TO_GET_CAR_REGISTRATION_NUMBER:
                return 1;
            case STEP_TO_GET_GENERAL_INSPECTION_IDS:
                return 2;
            case STEP_TO_COMPLETE_GENERAL_INSPECTION_DOCH:
                return 3;
            case STEP_TO_COMPLETE_CAR_DOCH:
                return 4;
        }
        return -1;
    }

    public static StatesTypes byteToStatesTypes(byte s) {
        switch (s) {
            case 0:
                return StatesTypes.START_NEW_DOCH;
            case 1:
                return StatesTypes.STEP_TO_GET_CAR_REGISTRATION_NUMBER;
            case 2:
                return StatesTypes.STEP_TO_GET_GENERAL_INSPECTION_IDS;
            case 3:
                return StatesTypes.STEP_TO_COMPLETE_GENERAL_INSPECTION_DOCH;
            case 4:
                return StatesTypes.STEP_TO_COMPLETE_CAR_DOCH;
        }
        return StatesTypes.START_NEW_DOCH;
    }

    public static String getFileName() {
        long l1 = ((new Date()).getTime() / 1000);
        long l2 = l1 / 65536;
        l1 = l1 - l2 * 65536;
        int userCounter = UCApp.ticketInformation.UserCounter;
        if (UCApp.loginData != null && userCounter < 0)
            userCounter = UCApp.loginData.UsrCounter;
        return Integer.toHexString(userCounter) + "_" + Integer.toHexString((int) l2) + Integer.toHexString((int) l1);
    }

    private String writeFileCar(Mat rgba, String licensePlate, Rect rect, int index) {
        FileOutputStream out = null;
        String filename = "";
        try {
            Bitmap bmp = Bitmap.createBitmap(rgba.cols(), rgba.rows(), Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(rgba, bmp);
            TicketInformation.addSubDirectory(getPath("CarPics"), licensePlate);
            if(mService.getLocation() == null) {
                filename = index + "_" +
                           "32_34_" + rect.x + "_" + rect.y + "_" + rect.height + "_" + rect.width + ".jpg";
            }
            else {
                filename = index + "_" +
                        mService.getLocation().getLatitude() + "_" + mService.getLocation().getLongitude() + "_" +
                        rect.x + "_" + rect.y + "_" + rect.height + "_" + rect.width + ".jpg";
            }
            out = new FileOutputStream(openFile(getPath("CarPics/" + licensePlate) + filename));
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, out);
            bmp.recycle();
            System.gc();
        } catch (CvException e) {
            Log.d(TAG, e.getMessage());
        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
        } finally {
            try {if (out != null) {out.close(); }} catch (Exception e) {}
        }
        return filename;
    }

    private String writeFileLicense(Bitmap bmp, String licensePlate, Rect rect) {
        FileOutputStream out = null;
        String filename = "";
        try {
            TicketInformation.addSubDirectory(getPath("CarPics"), licensePlate);
            filename = "image.jpg";
            out = new FileOutputStream(openFile(getPath("CarPics/" + licensePlate) + filename));
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, out);
        } catch (CvException e) {
            Log.d(TAG, e.getMessage());
        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
        } finally {
            try {if (out != null) {out.close(); }} catch (Exception e) {}
        }
        return filename;
    }

    private void createClassifier() {
        try {
            mClassifiers.add(ClassifierPB.create(getAssets(), "opt_converted_keras.pb", 30,
                    "conv2d_1_input", "dense_2/Softmax", false));
        }
        catch (final Exception e) {
            Log.i(TAG, "createClassifier:"+e.getMessage());
            runOnUiThread(() -> {
                Toast.makeText(this, "Error create ClassifierPB.", Toast.LENGTH_LONG).show();
            });
        }
    }

    private boolean isPlatesContains(String selectLicensePlate) {
        synchronized (allPlates) {
            for (Plate plate : allPlates) {
                if (plate.originalLicense.equals(selectLicensePlate))
                    return true;
            }
        }
        return false;
    }

    public boolean isDifferentByMoreThanOneDigit(String n1, String n2) {
        if(n1.length() != n2.length())
            return true;
        char [] d1 = n1.toCharArray();
        char [] d2 = n2.toCharArray();
        int diff = 0;
        for(int i = 0;i < d1.length;i++) {
            if(d1[i] != d2[i]) {
                if(++diff >= 2)
                    return true;
            }
        }
        return false;
    }

    public boolean isMissingOneDigit(String n1, String n2) {
        if(n1.length() < n2.length()) {
            String n = n2;
            n2 = n1;
            n1 = n;
        }
        for(int i = 1;i < n1.length()-1;i++ ) {
            String n = n1.substring(0,i) + n1.substring(i+1);
            if(n.equals(n2))
                return true;
        }
        return false;
    }

    private class Plate {
        public Bitmap  image;
        public String  license;
        public String  originalLicense;
        public int     count;
        public int     index;
        public boolean accurate;
    }

//            DataInputStream dataInputStream = new DataInputStream(new BufferedInputStream(openFileInput("config")));

    private void saveConfigFile() {
        Log.e(TAG,"saveConfigFile 1");
        DataOutputStream dataOutputStream = null;
        ByteArrayOutputStream baos = null;
        DataOutputStream outputStream = null;
        try {
            File file = getFileStreamPath("config");
            if(file.exists())
                file.delete();
            dataOutputStream = new DataOutputStream(new BufferedOutputStream(openFileOutput("config", AppCompatActivity.MODE_PRIVATE)));
            baos = new ByteArrayOutputStream();
            outputStream = new DataOutputStream(baos);
            outputStream.writeUTF(UCApp.streetNumber);
            outputStream.writeUTF(UCApp.streetName);
            outputStream.writeUTF(UCApp.version);
            outputStream.writeInt(UCApp.Lk);
            outputStream.writeInt(UCApp.violation);
            outputStream.writeInt(UCApp.streetCode);
            outputStream.writeInt(UCApp.user);
            outputStream.writeShort(UCApp.location);
            outputStream.writeBoolean(UCApp.breakMode);
            outputStream.writeBoolean(UCApp.motorcycleToo);
            outputStream.writeBoolean(UCApp.doNotQuery);
            byte[] b = baos.toByteArray();
            dataOutputStream.write(b, 0, b.length);
            dataOutputStream.flush();
            Log.e(TAG,"saveConfigFile 2");
        }
        catch(Exception e) {
            Log.e(TAG,"saveConfigFile 3:"+e.getMessage());
            e.printStackTrace();
        }
        try{outputStream.close();}catch (Exception e) {}
        try{baos.close();}catch (Exception e) {}
        try{dataOutputStream.close();}catch (Exception e) {}
    }

    public static void readConfigFile() {
        Log.e(TAG,"readConfigFile 1");
        DataInputStream dataInputStream = null;
        ByteArrayInputStream bais = null;
        DataInputStream inputStream = null;
        try {
            byte[] buffer = new byte[10240];
            dataInputStream = new DataInputStream(new BufferedInputStream(UCApp.gContext.openFileInput("config")));
            dataInputStream.read(buffer);
            bais = new ByteArrayInputStream(buffer);
            inputStream = new DataInputStream(bais);
            UCApp.streetNumber = inputStream.readUTF();
            UCApp.streetName = inputStream.readUTF();
            UCApp.version = inputStream.readUTF();
            UCApp.Lk = inputStream.readInt();
            UCApp.violation = inputStream.readInt();
            UCApp.streetCode = inputStream.readInt();
            UCApp.user = inputStream.readInt();
            UCApp.location = inputStream.readShort();
            UCApp.breakMode = inputStream.readBoolean();
            UCApp.motorcycleToo = inputStream.readBoolean();
            UCApp.doNotQuery = inputStream.readBoolean();
            Log.e(TAG,"readConfigFile 2");
        }
        catch (Exception e) {
            Log.e(TAG,"readConfigFile 3:"+e.getMessage());
            e.printStackTrace();
        }
        try{inputStream.close();}catch (Exception e) {}
        try{bais.close();}catch (Exception e) {}
        try{dataInputStream.close();}catch (Exception e) {}
    }

    private void readFiles() {
        File dir = openFile(getPath("CarPicsInput"));
        File[] filePlates = dir.listFiles();
        List<File> filePlateList = new ArrayList<File>();
        for (int n = 0; filePlates != null && n < filePlates.length; n++) {
            filePlateList.add(filePlates[n]);
        }
        Collections.sort(filePlateList, new Comparator<File>() {
            @Override
            public int compare(File file1, File file2) {
                long k = file1.lastModified() - file2.lastModified();
                if(k > 0) 		return 1;
                else if(k == 0) return 0;
                else 			return -1;
            }
        });
        for (int i = 0;i < filePlateList.size(); i++) {
            if (!filePlateList.get(i).getName().equals("Done")) {
                dir = openFile(getPath("CarPicsInput") + filePlateList.get(i).getName());
                File file = new File(TicketInformation.getPath("CarPicsInput/" + filePlateList.get(i).getName()) + "Ticket");
                file.delete();
                File[] licensePlateDir = dir.listFiles();
                if(licensePlateDir != null) {
                    for (int n = 0; n < licensePlateDir.length; n++) {
                        if (!licensePlateDir[n].getName().equals("Ticket") && !licensePlateDir[n].getName().equals("image"))
                            inputFiles.add(TicketInformation.getPath("CarPicsInput/" + filePlateList.get(i).getName()) + licensePlateDir[n].getName());
                    }
                }
            }
        }
    }

        /**
         * A native method that is implemented by the 'native-lib' native library,
         * which is packaged with this application.
         */

    public native long GetPlates(long addrRgba, long plate, long digit1, long digit2, long digit3, long digit4, long digit5, long digit6, long digit7, long digit8, long digit9, long digit10);
//  public native long GetPlates(long addrRgba, long digit1, long digit2, long digit3, long digit4, long digit5, long digit6, long digit7, long digit8, long digit9, long digit10);
    public native static long InitPlates();
    public native long SetDigit(long digit);
}
