package com.lbmotion.whatitsays.common;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.lbmotion.whatitsays.MainActivity;
import com.lbmotion.whatitsays.R;
import com.lbmotion.whatitsays.UCApp;
import com.lbmotion.whatitsays.communication.Base;
import com.lbmotion.whatitsays.communication.Dochot;
import com.lbmotion.whatitsays.communication.DochotHTTP;
import com.lbmotion.whatitsays.communication.GetAuthorityData;
import com.lbmotion.whatitsays.communication.GetAuthorityDataHTTP;
import com.lbmotion.whatitsays.communication.GetViolations;
import com.lbmotion.whatitsays.data.DB;
import com.lbmotion.whatitsays.data.LoginData;
import com.lbmotion.whatitsays.data.Parameters;
import com.lbmotion.whatitsays.data.TicketInformation;
import com.lbmotion.whatitsays.util.Util;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Vector;

import static com.lbmotion.whatitsays.UCApp.ticketInformation;

//import com.lbmotion.whatitsays.util.LocationDetection;

public abstract class BaseActivity extends AppCompatActivity {
//
	public final static String TAG = "BaseActivity";
//
	public final static String REPORT_NAME = "REPORT_NAME";
	public final static String HANDLE_REPORT = "HANDLE_REPORT";
//
	public final static int 				WAIT_FOR_KEYBOARD_TO_SHOW = 500;
//
	public static final String LOGIN_DUE = "LOGIN_DUE";
	public final static int 				NEW_LOGIN = 13;
	public final static int 				SHIFT_ENDED = 22;
	protected final static int 				LOADING_MODE_AT_LOGIN = 0;
	public final static int		 			LOADING_MODE_AT_SHIFT_END = 2;
	public final static String GOT_NOTEBOOK_PARAMETER = "GOT_NOTEBOOK_PARAMETER";
	public final static String DO_GET_GENERAL_INSPECTION = "DO_GET_GENERAL_INSPECTION";
	public final static String DO_GET_CAR_INFORMATION = "DO_GET_CAR_INFORMATION";
	public final static String WAIT_TILL_AFTER_QUERY = "WAIT_TILL_AFTER_QUERY";
	public final static int					GOT_NOTEBOOK_VALUE = 1;
//	
	public final static int					NO_REFERENCE = 9999999;
	public final static int					NO_TRAIL_DATE = 999999;
//
	public static final String LOOK_FOR_PRINTER = "look_for_printer";
//
	protected ProgressDialog progressDialog = null;
//
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		requestWindowFeature(Window.FEATURE_NO_TITLE);
//		supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		try {
			final ViewGroup viewGroup = (ViewGroup) ((ViewGroup) this.findViewById(android.R.id.content)).getChildAt(0);
			setTitleActionBar(R.string.app_name, viewGroup);
		}
		catch (Exception e) {
            Log.i(TAG, "onCreate:" + e.getMessage());
        }
	}

	protected void setTitleActionBar(int id, ViewGroup vg) {
		View v = getLayoutInflater().inflate(R.layout.actionbar_title_layout,vg,false);
		((TextView)v.findViewById(R.id.title)).setText(id);
		getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		getSupportActionBar().setCustomView(v);
	}

	public static void showErrorMessageHandle(final int errorMessage, final AppCompatActivity activity) {
		showErrorMessageHandle(activity.getString(errorMessage),activity);
	}

    protected String getInstalledAppVersion() {
        String installVersion = "";
        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_ACTIVITIES);
            if (info != null && info.versionName != null && !info.versionName.equals(""))
                installVersion = info.versionName.toString();
        }
        catch (PackageManager.NameNotFoundException e) {
            Log.i("LoginActivity","getInstalledAppVersion:"+e.getMessage());}
        Base.version = installVersion;
        return "V"+installVersion;
    }

	public static void showErrorMessageHandle(final String errorMessage, final AppCompatActivity activity) {
		activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				try {
					showErrorMessage(errorMessage,activity,false);
				}
				catch (Exception e) {}
				catch (Error e) {}
			}
		});
	}

	public static void showErrorMessage(int errorMessage, Context context) {
		showErrorMessage(context.getString(errorMessage),context,false);
	}

	public static void showErrorMessage(String errorMessage, Context context) {
		showErrorMessage(errorMessage,context,false);
	}
	
	public static void showErrorMessage(final String errorMessage, final Context context, final boolean doExit) {
		try {
			AlertDialog.Builder builder = new AlertDialog.Builder(context);
			TextView message = new TextView(context);
			message.setText(errorMessage); 
			message.setPadding(10, 10, 10,10);
			message.setTextColor(Color.RED);
			message.setTextSize(20); 
			builder.setView(message);
			builder.setCancelable(false);
			builder.setPositiveButton(R.string.button_continue,
				new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					dialog.dismiss();
					if(doExit) {
						Intent intent = new Intent(Intent.ACTION_MAIN);
						intent.addCategory(Intent.CATEGORY_HOME);
						context.startActivity(intent);
						System.exit(0);
					}
				}
			});
			AlertDialog alert = builder.create();
			alert.show();
		}
		catch(Exception e) {
			try {
				showErrorMessage(errorMessage, UCApp.gContext,doExit);
			}
			catch(Exception ee) {}
			catch (Error ee) {}
		}
		catch (Error e) {
			try {
				showErrorMessage(errorMessage,UCApp.gContext,doExit);
			}
			catch(Exception ee) {}
			catch (Error ee) {}
		}
	}
/*
	public void welcome(boolean shiftEnded) {
		if(UCApp.ticketsAndPicturesBinder != null)
			UCApp.ticketsAndPicturesBinder.setLoadOnlyTickets(false);
		setCarDefaultValues();
		Intent intent = new Intent(MainActivity.instance, WelcomeActivity.class);
		if(shiftEnded)
			intent.putExtra(LOGIN_DUE, SHIFT_ENDED);
		else
			intent.putExtra(LOGIN_DUE, NEW_LOGIN);
		MainActivity.instance.startActivity(intent);
	}
*/
	public static String getType() {
		for(int i = 0; i < DB.vehicleTypes.size(); i++)
			if(ticketInformation.CarInformationType == DB.vehicleTypes.get(i).code)
				return	DB.vehicleTypes.get(i).name;
		return "";
	}

	public static String getColor() {
		for(int i = 0;i < DB.vehicleColors.size();i++)
			if(ticketInformation.CarInformationColor == DB.vehicleColors.get(i).code)
				return	DB.vehicleColors.get(i).name;
		return "";
	}

	public static String getManufacturer() {
		for(int i = 0;i < DB.vehicleManufacturers.size();i++)
			if(ticketInformation.CarInformationManufacturer == DB.vehicleManufacturers.get(i).code)
				return	DB.vehicleManufacturers.get(i).name;
		return "";
	}

	private static String reverseNumbers(String input) {
		String out = "";
		int j = -1;
		for (int i = 0; i < input.length(); i++) {
			if ((input.charAt(i) >= '0' && input.charAt(i) <= '9') || input.charAt(i) == ')' || input.charAt(i) == '(') {
				if (j == -1)
					j = i;
			}
			else {
				if (j == -1) {
					out += input.charAt(i);
				}
				else {
					for (int n = j; n < i; n++) {
						out += input.charAt(i-1-n+j);

					}
					out += input.charAt(i);
					j = -1;
				}
			}
		}
		if(j != -1) {
			for(int n = j;n < input.length();n++) {
				out += input.charAt(input.length()-n+j-1);

			}
		}
		return out;
	}

	public static void setStreetDefaultValues() {
		if (UCApp.streets == null)
			UCApp.streets = new Vector<String>();
		if (UCApp.streetsReference == null)
			UCApp.streetsReference = new Vector<Integer>();
		if (DB.streets == null) {
			UCApp.loginData = LoginData.restore();
			DB.GetDB();
		}
		UCApp.streets.removeAllElements();
		UCApp.streetsReference.removeAllElements();
		for (short i = 0; i < DB.streets.size(); i++) {
			if (UCApp.loginData.DefaultE == -1 || UCApp.loginData.DefaultES == -1 || DB.streets.get(i).Ezor == -1 || UCApp.loginData.DefaultES == DB.streets.get(i).Ezor) {
				UCApp.streets.addElement(reverseNumbers(DB.streets.get(i).Nm));
				UCApp.streetsReference.addElement(Integer.valueOf(i));
			}
		}
	}

	public static void setCarDefaultValues() {
		setStreetDefaultValues();
		UCApp.carTypeDefaultValue = -1;
		UCApp.carColorDefaultValue = -1;
		UCApp.carIzranDefaultValue = -1;
		if(DB.authorityCharacteristic.CarType != 0) {
			for(short i = 0;i < DB.vehicleTypes.size();i++) {
				if((DB.vehicleTypes.elementAt(i)).code == DB.authorityCharacteristic.CarType) {
					UCApp.carTypeDefaultValue = i;
					break;
				}
			}
		}
		if(DB.authorityCharacteristic.CarColor != 0) {
			for(short i = 0;i < DB.vehicleColors.size();i++) {
				if((DB.vehicleColors.elementAt(i)).code == DB.authorityCharacteristic.CarColor) {
					UCApp.carColorDefaultValue = i;
					break;
				}
			}
		}
		if(DB.authorityCharacteristic.CarIzran != 0) {
			for(short i = 0;i < DB.vehicleManufacturers.size();i++) {
				if((DB.vehicleManufacturers.elementAt(i)).code == DB.authorityCharacteristic.CarIzran) {
					UCApp.carIzranDefaultValue = i;
					break;
				}
			}
		}
	}
	
	public static byte getAveraSivug() {
		for(int i = 0;i < DB.allViolations.size();i++) {
			if(DB.allViolations.get(i).C == ticketInformation.violationListCode) {
				return DB.allViolations.get(i).Sivug;
			}
		}
		return -1;
	}

	public static String getAveraName(int violationListCode) {
		for(int i = 0;i < DB.allViolations.size();i++) {
			if(DB.allViolations.get(i).C == violationListCode) {
				return DB.allViolations.get(i).VT_Nm;
			}
		}
		return null;
	}

	public final static void compressPicturesOfCancelledDochs(String filename) {
		InputStream in = null;
		OutputStream imageOutputStream = null;
		try {
			System.gc();
			in = new FileInputStream(TicketInformation.openFile(TicketInformation.getPath("Data")+filename));
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inSampleSize = 2; //try to decrease decoded image
			options.inPurgeable = true; //purge-able to disk
			Bitmap bitmap = BitmapFactory.decodeStream(in, null, options);
			in.close();
			in = null;
			boolean doShrinkBitmap = false;
			if(bitmap.getWidth() > bitmap.getHeight()) {
				if(bitmap.getWidth()/2 > 320 && bitmap.getHeight()/2 > 240) {
					int[] dim = Util.getDimensions(bitmap.getWidth(), bitmap.getHeight(), 320, 240);
					bitmap = Util.shrinkBitmap(TicketInformation.getPath("Data")+filename, dim[0], dim[1], true);
					doShrinkBitmap = true;
				}
			} else {
				if (bitmap.getWidth() / 2 > 240 && bitmap.getHeight() / 2 > 320) {
					int[] dim = Util.getDimensions(bitmap.getWidth(), bitmap.getHeight(), 240, 320);
					bitmap = Util.shrinkBitmap(TicketInformation.getPath("Data")+filename, dim[0], dim[1], true);
					doShrinkBitmap = true;
				}
			}
			System.gc();
			if(doShrinkBitmap) {
				File imageFile = TicketInformation.openFile(TicketInformation.getPath("Data")+filename);
				imageOutputStream = UCApp.gContext.getContentResolver().openOutputStream(Uri.fromFile(imageFile));
				bitmap.compress(Bitmap.CompressFormat.JPEG, 100, imageOutputStream);
				imageOutputStream.flush();
				imageOutputStream.close();
				imageFile = TicketInformation.openFile(TicketInformation.getPath("PicData")+filename);
				imageOutputStream = UCApp.gContext.getContentResolver().openOutputStream(Uri.fromFile(imageFile));
				bitmap.compress(Bitmap.CompressFormat.JPEG, 100, imageOutputStream);
				imageOutputStream.flush();
				imageOutputStream.close();
				imageOutputStream = null;
				if(TicketInformation.getFileSize(filename) == 0) {
					imageFile = TicketInformation.openFile(TicketInformation.getPath("Data")+filename);
					imageOutputStream = UCApp.gContext.getContentResolver().openOutputStream(Uri.fromFile(imageFile));
					bitmap.compress(Bitmap.CompressFormat.JPEG, 100, imageOutputStream);
					imageOutputStream.flush();
					imageOutputStream.close();
					imageOutputStream = null;
				}
			}
		}
		catch (Exception e) {
        	Log.i("BaseActivity","Error:"+e.getMessage());
		}
		catch (OutOfMemoryError e) {
        	Log.i("BaseActivity","OutOfMemoryError:"+e.getMessage());
		}
		try{if(in != null) in.close();}catch (Exception e) {}
		try{
			if(imageOutputStream != null) {
				imageOutputStream.flush();
				imageOutputStream.close();
			}
		}catch (Exception e) {}
	}
	
	public static boolean addImagesToQueue(boolean cancelled,boolean fromQR) {
/*
		boolean savedOK = fromQR;
		Location location = LocationDetection.getLocation();
		if (location != null) {
			ticketInformation.latitude = location.getLatitude();
			ticketInformation.longitude = location.getLongitude();
		}
		ticketInformation.didCrashInTheMiddle = false;
		if(ticketInformation.date == null || ticketInformation.date.length() == 0)
			ticketInformation.date = Util.makeDate(new Date(ticketInformation.timestamp));
		if(cancelled) {
//			UCApp.prictureFiles.removeAllElements();
			if(ticketInformation.didQueryServer) {
				for (int i = 0; i < ticketInformation.pictures.size(); i++) {
					compressPicturesOfCancelledDochs(ticketInformation.pictures.get(i));
				}
//				UCApp.prictureFiles.addAll(ticketInformation.pictures);
//				new Thread() { public void run() {
//                  for (int i = 0; i < UCApp.prictureFiles.size(); i++) {
//                      compressPicturesOfCancelledDochs(UCApp.prictureFiles.get(i));
//						try {Thread.sleep(1000);}catch (InterruptedException ie) {}
//              }}.start();
				if (!TicketInformation.save(ticketInformation) || !TicketInformation.saveTicket(ticketInformation, -1))
					showErrorMessage(Base.mContext.getResources().getString(R.string.memory_card_error), Base.mContext);
			}
			savedOK = true;
		}
		else {
			byte sivug = getAveraSivug();
			boolean azharaType = ticketInformation.warningDate.length() > 0;
			boolean eventType =  MainActivity.pagePakachDecisionWasClickedOpenEvent;
			if(ticketInformation.carOrGM && MainActivity.pagePakachDecisionIsCanceled) {
				ticketInformation.dochCode = 0;
			}
			else {
				if(!fromQR)
					ticketInformation.dochCode = UCApp.loginData.getCode(ticketInformation.carOrGM, azharaType, eventType, sivug == 2, ticketInformation.violationListCode);
			}
			if(ticketInformation.dochCode == -1 || (ticketInformation.dochCode == 0 && ticketInformation.packachDecisionCode < 3)) {
				showErrorMessage(Base.mContext.getResources().getString(R.string.error_getting_doch_code), Base.mContext);
			}
			if(!fromQR)
				ticketInformation.doch = UCApp.loginData.getSidra(ticketInformation.carOrGM,azharaType,eventType,sivug == 2, ticketInformation.violationListCode)+"_"+UCApp.loginData.getDochNumber(ticketInformation.carOrGM,azharaType,eventType,sivug == 2, ticketInformation.violationListCode);
			if(!TicketInformation.save(ticketInformation) || !TicketInformation.saveTicket(ticketInformation,-1))
				showErrorMessage(Base.mContext.getResources().getString(R.string.memory_card_error), Base.mContext);
			ticketInformation.backup();
			if(!fromQR) {
				try {
					if (ticketInformation.carOrGM) {
						if (MainActivity.pagePakachDecisionIsCanceled) {
							savedOK = true;
						}
						else {
							if (sivug == 2) {
								UCApp.loginData.lastManagementDochNo = UCApp.loginData.getDochNumber(ticketInformation.carOrGM, azharaType, eventType, sivug == 2, ticketInformation.violationListCode);
								UCApp.loginData.lastManagementSidra = UCApp.loginData.getSidra(ticketInformation.carOrGM, azharaType, eventType, sivug == 2, ticketInformation.violationListCode);
							} else {
								UCApp.loginData.lastGeneralDochNo = UCApp.loginData.getDochNumber(ticketInformation.carOrGM, azharaType, eventType, sivug == 2, ticketInformation.violationListCode);
								UCApp.loginData.lastGeneralSidra = UCApp.loginData.getSidra(ticketInformation.carOrGM, azharaType, eventType, sivug == 2, ticketInformation.violationListCode);
							}
							savedOK = UCApp.loginData.removeDoch(ticketInformation.carOrGM, azharaType, eventType, sivug == 2, ticketInformation.violationListCode);
						}
					}
//				    else if(MainActivity.pageAveraViolationObject.SwHanayaHok == 0) {
					else {
						UCApp.loginData.lastParkingDochNo = UCApp.loginData.getDochNumber(ticketInformation.carOrGM, azharaType, eventType, sivug == 2, ticketInformation.violationListCode);
						UCApp.loginData.lastParkingSidra = UCApp.loginData.getSidra(ticketInformation.carOrGM, azharaType, eventType, sivug == 2, ticketInformation.violationListCode);
						savedOK = UCApp.loginData.removeDoch(ticketInformation.carOrGM, azharaType, eventType, sivug == 2, ticketInformation.violationListCode);
					}
				} catch (Exception e) {
				}
			}
		}
		SendTicketsAndPictures.addImages(ticketInformation.pictures);
		return savedOK;
*/
		return true;
	}
//typePakach = 0  all
//typePakach = 1  parking
//typePakach = 2  general
//typePakach = 3  general & management
//typePakach = 4  management
	public static boolean getShowAverotAtraot() {
		if(UCApp.loginData != null && UCApp.loginData.typePakach != 1) {
			for(int i = 0; i < DB.allViolations.size();i++) {
				if(DB.allViolations.get(i).toAtraa > 0 || DB.allViolations.get(i).toAvera > 0)
					return true;
			}
		}
		return false;
	}

	public void afterLoadDochot() {
		if(UCApp.workModeOffline.get()) {
			if(UCApp.loginData != null) {
				UCApp.loginData.loginTime = 0;
				UCApp.loginData.save();
			}
		}
		else { 
			if(UCApp.loginData != null) {
//				UCApp.loginData.loginTime = (new Date()).getTime();
				UCApp.loginData.loginTimeHour = Util.getHour()*60+Util.getMinute();
			}
		}
		if(UCApp.ticketsAndPicturesBinder != null)
			UCApp.ticketsAndPicturesBinder.setLoadOnlyTickets(false);
//		welcome(false);
	}

	protected void gotoOfflineMode() {
		UCApp.workModeOffline.set(true);
		if(UCApp.canWorkOffLine)
			afterLoadDochot();
		else
			BaseActivity.this.finish();		
	}
	public void loadDochot(final int lodingMode,final int message,final boolean doFinish) {
		if(UCApp.loginData == null || UCApp.loginData.UsrCounter == 0 || UCApp.loginData.Lk == 0) {
			Intent intent = new Intent(this, MainActivity.class);
			this.startActivity(intent);
			try {android.os.Process.killProcess(android.os.Process.myPid());}catch (Exception ee) {
                Log.i(TAG, "killMe:" + ee.getMessage());}
			System.exit(0);
/*
			Intent intent = new Intent(this, LoginActivity.class);
			startActivity(intent);
			MainActivity.didCallLogin = true;
*/
			return;
		}
		(new AsyncTask<String, Integer, Boolean>() {
			@Override
			protected void onPreExecute() {
				try{progressDialog.dismiss();}catch (Exception e) {}catch (Error e) {}
				try {
					progressDialog = new ProgressDialog(BaseActivity.this);
					progressDialog.setMessage(getResources().getString(message));
					int id = UCApp.canWorkOffLine ? R.string.button_offline : R.string.exit;
					progressDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(id), new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							Dochot.abort();
							dialog.dismiss();
							gotoOfflineMode();
						}
					});
					progressDialog.setCancelable(false);
					progressDialog.setIndeterminate(true);
					progressDialog.show();
				}
				catch(Exception e) {}
				catch (Error e) {}
			}
			@Override
			protected Boolean doInBackground(String... params) {
				boolean returnCode = false;
				try {
					boolean result;
					if(Base.communicationViaMiddleware) {
						Dochot r = Dochot.doLoad(UCApp.loginData.UsrCounter + "", UCApp.loginData.Lk + "");
						result = r == null?false:r.result;
					}
					else {
						DochotHTTP r = DochotHTTP.doLoad(UCApp.loginData.UsrCounter+"",UCApp.loginData.Lk+"");
						result = r == null?false:r.result;
					}
					if(UCApp.workModeOffline.get()) {
						returnCode = true;
					}
					else if(result && UCApp.loginData != null) {
						UCApp.loginData.save();
						returnCode = true;
					}
				}
				catch(Exception e) {}
				catch(Error e) {}
				return Boolean.valueOf(returnCode);
			}

			@Override
			protected void onPostExecute(Boolean result) {
				try{progressDialog.dismiss();}catch (Exception e) {}catch (Error e) {}
				Dochot.me = null;
				DochotHTTP.me = null;
				try {
					if (!UCApp.workModeOffline.get()) {
						if (lodingMode == LOADING_MODE_AT_LOGIN) {
							if (result)
								getAuthorityData(lodingMode,true);
							else
								showLoadingDataError(lodingMode, R.string.get_dohot_data_error);
						} else if (lodingMode != LOADING_MODE_AT_SHIFT_END && !result) {
							loadDochot(lodingMode, R.string.load_dochs_error_message,doFinish);
						} else if (lodingMode == LOADING_MODE_AT_SHIFT_END) {
/*
							if (result)
								welcome(true);
							else*/ if (!result)
								showLoadingDataError(lodingMode, R.string.get_dohot_data_error);
						}
					}
				}
				catch (Exception e) {
					showLoadingDataError(lodingMode, R.string.get_dohot_data_error);
				}
				catch (Error e) {
					showLoadingDataError(lodingMode, R.string.get_dohot_data_error);
				}
				if(doFinish)
					finish();
			}
		}).execute();
	}

	public static boolean cannotCreatePinkasByPakach() {
		return(DB.authorityCharacteristic.SwAddPinkasMesofon == 0);
	}
	
	protected void getAuthorityData(final int lodingMode,final boolean tryAgain) {
		(new AsyncTask<String, Integer, Boolean>() {
			@Override
			protected void onPreExecute() {
				try{progressDialog.dismiss();}catch (Exception e) {} catch (Error e) {}
				try {
					progressDialog = new ProgressDialog(BaseActivity.this);
					progressDialog.setMessage(getResources().getString(R.string.please_wait_get_authority_data));
					int id = UCApp.canWorkOffLine ? R.string.button_offline : R.string.exit;
					progressDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(id), new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							try{dialog.dismiss();}catch (Exception e) {}
							try{
								GetAuthorityData.abort();}catch (Exception e) {}
							try{gotoOfflineMode();}catch (Exception e) {}
						}
					});
					progressDialog.setCancelable(false);
					progressDialog.setIndeterminate(true);
					progressDialog.show();
				}
				catch (Exception e) {}
				catch (Error e) {}
			}
			@Override
			protected Boolean doInBackground(String... params) {
				if(Base.communicationViaMiddleware) {
					String timestamp = Parameters.get("timestamp2");
					if(timestamp == null) {
						GetViolations.doLoadViolations(UCApp.loginData.Lk + "", UCApp.loginData.UsrCounter + "");
					}
					boolean returnCode = GetAuthorityData.doGetAuthorityData(UCApp.loginData.Lk + "", UCApp.loginData.UsrCounter + "");
					return Boolean.valueOf(returnCode);
				}
				else {
					GetAuthorityDataHTTP getAuthorityDataHTTP = GetAuthorityDataHTTP.doGetAuthorityDataHTTP(UCApp.loginData.UsrCounter + "", UCApp.loginData.Lk + "");
					return Boolean.valueOf(getAuthorityDataHTTP != null && getAuthorityDataHTTP.result);
				}
			}

			@Override
			protected void onPostExecute(Boolean result) {
				try {progressDialog.dismiss();} catch (Exception e) {} catch (Error e) {}
				try {
					GetAuthorityData.me = null;
					GetAuthorityDataHTTP.me = null;
					if(!UCApp.workModeOffline.get()) {
						if(result) {
							DB.SaveDB();
							if(lodingMode == LOADING_MODE_AT_LOGIN) {
								BaseActivity.this.finish();
								afterLoadDochot();
							}
						}
						else {
							if(tryAgain) {
								getAuthorityData(lodingMode,false);
							}
							else {
								showLoadingDataError(lodingMode, R.string.get_authority_data_error);
							}
						}
					}
				}
				catch(Exception e) {}
				catch (Error e) {}
			}
		}).execute();
	}
	
	private void showLoadingDataError(final int lodingMode,final int msg) {
		try {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			TextView message = new TextView(this);
			message.setText(msg); 
			message.setPadding(10, 10, 10,10);
			message.setTextColor(Color.RED);
			message.setTextSize(20); 
			builder.setView(message);
			builder.setCancelable(false);
			builder.setPositiveButton(R.string.button_again,
					new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					dialog.dismiss();
					if(msg == R.string.get_authority_data_error)
						getAuthorityData(lodingMode,true);
					else if(msg == R.string.get_dohot_data_error)
						loadDochot(lodingMode,R.string.loading_docs,false);
				}
			}
					);
			builder.setNegativeButton(this.getString(R.string.close),
					new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					dialog.dismiss();
					if(lodingMode == LOADING_MODE_AT_LOGIN)
						BaseActivity.this.finish();
				}
			});
			AlertDialog alert = builder.create();
			alert.show();
		}
		catch(Exception e) {}
	}
		
	public final static boolean bothInspections() {
		return UCApp.parkingAllowed && (UCApp.generalAllowed || UCApp.managementAllowed);
	}

	public final static boolean onlyParking() {
		return UCApp.parkingAllowed && !(UCApp.generalAllowed || UCApp.managementAllowed);
	}
	
	public final static void resetRemarks() {
		ticketInformation.remarkReference1 = NO_REFERENCE;
		ticketInformation.remarkReference2 = NO_REFERENCE;
		ticketInformation.remarkReference3 = NO_REFERENCE;
		ticketInformation.remarkReference4 = NO_REFERENCE;
		ticketInformation.remarkReference1Print = false;
		ticketInformation.remarkReference2Print = false;
		ticketInformation.remarkReference3Print = false;
		ticketInformation.remarkReference4Print = false;
		ticketInformation.citizenRemarkReference1 = NO_REFERENCE;
		ticketInformation.citizenRemarkReference2 = NO_REFERENCE;
		ticketInformation.citizenRemarkReference3 = NO_REFERENCE;
		ticketInformation.citizenRemarkReference4 = NO_REFERENCE;
	}

	public static boolean canCancelDochByPakach() {
		return (DB.authorityCharacteristic.SwNoDeleteDochMeshofon == 0);
	}
/*
	public static RemarkHasProperty getRemarkHasProperty() {
		RemarkHasProperty remarkHasProperty = new RemarkHasProperty();
		try {
			for(int i = 0;i < DB.remarksToViolations.size();i++) {
				List2s remarkToViolation = DB.remarksToViolations.get(i);
				if(MainActivity.pageAveraViolationObject.C == remarkToViolation.Violation) {
					for(short j = 0;j < DB.remarks.size();j++) {
						List3 remark = DB.remarks.elementAt(j);
						if(remarkToViolation.code == remark.code) {
							if (remark.name.indexOf("*?=*") != -1) {
								if(remark.name.length() > 4)
									remarkHasProperty.mName = remark.name.substring(remark.name.indexOf("*?=*") + 5);
								else
									remark.name = "";
								remarkHasProperty.mType = 1;
								return remarkHasProperty;
							}
							if (remark.name.indexOf("*?D*") != -1) {
								if(remark.name.length() > 4)
									remarkHasProperty.mName = remark.name.substring(remark.name.indexOf("*?=*") + 5);
								else
									remark.name = "";
								remarkHasProperty.mType = 2;
								return remarkHasProperty;
							}
							if (remark.name.indexOf("*?U*") != -1) {
								if(remark.name.length() > 4)
									remarkHasProperty.mName = remark.name.substring(remark.name.indexOf("*?=*") + 5);
								else
									remark.name = "";
								remarkHasProperty.mType = 3;
								return remarkHasProperty;
							}
						}
					}
				}
			}
		}
		catch(Exception e){}
		return null;
	}

	public static Vector<String> pakachRemarksList() {
		UCApp.pakachRemarksToViolation.removeAllElements();
		UCApp.pakachRemarksToViolationReference.removeAllElements();
		try {
			for(int i = 0;i < DB.remarksToViolations.size();i++) {
				List2s remarkToViolation = DB.remarksToViolations.get(i);
				if(MainActivity.pageAveraViolationObject.C == remarkToViolation.Violation) {
					for(short j = 0;j < DB.remarks.size();j++) {
						List3 remark = DB.remarks.elementAt(j);
						if(remark.name.indexOf("*?=*") != -1)
							continue;
						if(remark.name.indexOf("*?D*") != -1)
							continue;
						if(remark.name.indexOf("*?U*") != -1)
							continue;
						if(remarkToViolation.code == remark.code) {
							if(UCApp.checkLincense != null && UCApp.checkLincense.Kod_SeloRemark != 0 && UCApp.checkLincense.Kod_SeloRemark == remark.Kod)
								UCApp.pakachMandatoryRemark = UCApp.pakachRemarksToViolationReference.size();
							if(UCApp.checkLincense != null && UCApp.checkLincense.SwKazach != 0 && UCApp.checkLincense.SwKazach == remark.Kod)
								UCApp.pakachMandatoryRemark = UCApp.pakachRemarksToViolationReference.size();
							UCApp.pakachRemarksToViolation.add(remark.Kod+"   "+reverseNumbers(remark.name));
							UCApp.pakachRemarksToViolationReference.add(Integer.valueOf(j));
							if(UCApp.pakachMandatoryRemark != -1 && UCApp.pakachSelectedItemRemarks.size() == 0) {
								UCApp.pakachSelectedItemRemarks.add(Integer.valueOf(UCApp.pakachMandatoryRemark));
								UCApp.pakachSelectedItemRemarksPrint.add(Boolean.valueOf(ticketInformation.carOrGM));
							}
						}
					}
				}
			}
		}
		catch(Exception e){}
		return UCApp.pakachRemarksToViolation;
	}
*/
	public static Vector<String> citizenRemarksList() {
		UCApp.citizenRemarksToViolation.removeAllElements();
		UCApp.citizenRemarksToViolationReference.removeAllElements();
		try {
			for(short i = 0 ; i < DB.citizenRemarks.size();i++) {
				UCApp.citizenRemarksToViolation.add(DB.citizenRemarks.get(i).Kod+"   "+reverseNumbers(DB.citizenRemarks.get(i).name));
				UCApp.citizenRemarksToViolationReference.add(Integer.valueOf(i));
			}
		}
		catch(Exception e){}
		return UCApp.citizenRemarksToViolation;
	}

	public static void showSoftKeyboard(final View view, final AppCompatActivity activity) {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
        }
        catch (Exception e) {}
		(new AsyncTask<String, Integer, Boolean>() {
			@Override
			protected Boolean doInBackground(String... params) {
				try {
                    Thread.sleep(BaseActivity.WAIT_FOR_KEYBOARD_TO_SHOW/3);}catch (InterruptedException ie) {}
				return Boolean.valueOf(true);
			}

			@Override
			protected void onPostExecute(Boolean result) {
				try {
					InputMethodManager inputMethodManager = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
					inputMethodManager.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
				}
				catch (Exception e) {}
				catch (Error e) {}
			}
		}).execute();
		(new AsyncTask<String, Integer, Boolean>() {
			@Override
			protected Boolean doInBackground(String... params) {
				try {
                    Thread.sleep(BaseActivity.WAIT_FOR_KEYBOARD_TO_SHOW);}catch (InterruptedException ie) {}
				return Boolean.valueOf(true);
			}

			@Override
			protected void onPostExecute(Boolean result) {
				try {
					InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
					inputMethodManager.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
				}
				catch (Exception e) {}
				catch (Error e) {}
		}
		}).execute();
	}
	
	public static String getFirstRemark() {
		String remark = "";
		if(ticketInformation.remarkReference1 != NO_REFERENCE)
			remark = DB.remarks.get(ticketInformation.remarkReference1).name;
		return remark;
	}
	
	public static void showHowManyPicturesWhereTaken(TextView tv, int extra) {
		try {
			if(tv != null)
				tv.setText(ticketInformation.pictures.size()+extra+"/9");
		}
		catch (Exception e) {}
	}
	
	public final static boolean isGeneralAllowed() {
		return UCApp.generalAllowed;
	}
	
	public final static boolean isManagementAllowed() {
		return UCApp.managementAllowed;
	}

	public final static boolean isParkingAllowed() {
		return UCApp.parkingAllowed;
	}
	
	public static Vector<String> warningsList(boolean isExistingWarning) {
		Vector<String> existingWarningsList = new Vector<String>();
		try {
			if(isExistingWarning) {
				for (int i = 0; i < DB.existingWarnings.size();i++) {
					existingWarningsList.addElement(
							DB.existingWarnings.get(i).AveraKod+"\t"+
							DB.existingWarnings.get(i).StreetNm+"\t"+
							DB.existingWarnings.get(i).StreetNoAvera+"\t"+
							DB.existingWarnings.get(i).defendant.name+" "+DB.existingWarnings.get(i).defendant.last+"\t"+
							DB.existingWarnings.get(i).DochKod+"-"+DB.existingWarnings.get(i).Sidra+"-"+ DB.existingWarnings.get(i).Bikoret);
				}
			}
			else {
				for (int i = 0; i < DB.pastToHandleEvents.size();i++) {
					existingWarningsList.addElement(
							DB.pastToHandleEvents.get(i).DochKod+"-"+DB.pastToHandleEvents.get(i).Sidra+"-"+DB.pastToHandleEvents.get(i).Bikoret+"\t"+
							DB.pastToHandleEvents.get(i).StreetNm+"\t"+
							DB.pastToHandleEvents.get(i).StreetNoAvera+"\t"+
							DB.pastToHandleEvents.get(i).defendant.name+" "+ DB.pastToHandleEvents.get(i).defendant.last+"\t"+
							DB.pastToHandleEvents.get(i).AveraKod);
				}
			}
		}
		catch(Exception e){}
		return existingWarningsList;
	}
	
	public String getRemarkWarningsList(int i, boolean isExistingWarning) {
		try {
			if(isExistingWarning)
				return DB.existingWarnings.get(i).remark;
			else
				return DB.pastToHandleEvents.get(i).remark;
		}
		catch(Exception e){}
		return "";
	}
/*
	public void installNewVersion(Activity activity) {
		if(Base.webVersion == null) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			TextView message = new TextView(this);
			message.setText(R.string.checking_new_version);
			message.setPadding(10, 10, 10,10);
			message.setTextColor(Color.RED);
			message.setTextSize(20);
			builder.setView(message);
			builder.setCancelable(false);
			builder.setPositiveButton(R.string.button_continue,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							dialog.dismiss();
						}
					}
			);
			AlertDialog alert = builder.create();
			alert.show();
		}
		else {
			update(true,activity);
		}
	}

	public static void update(final boolean isNewVersion,final Activity activity) {
		if(!new InstallUpdateHanita(activity).appInstalledOrNot("com.lbmotion.updatehanita")) {
			(new InstallUpdateHanita(activity)).checkUpdateSetup();
			return;
		}
		if(SendTicketsAndPictures.getNumberOfTicketsWaiting() > 0 || SendTicketsAndPictures.getNumberOfPicturesWaiting() > 0) {
			showErrorMessage(activity.getResources().getString(R.string.update_message), activity);
		}
		final ProgressDialog progressDialog = new ProgressDialog(activity);
		progressDialog.setMessage(activity.getResources().getString(R.string.downloading_new_version));
		progressDialog.setIndeterminate(true);
		progressDialog.show();
		(new AsyncTask<Integer, Integer, Boolean>() {
			@Override
			protected Boolean doInBackground(Integer... params) {
				String apkurl;
				if(isNewVersion)
					apkurl = "http://www.trapnearu.com/Hanita/UrbanControl.apk";
				else
					apkurl = "http://www.trapnearu.com/Hanita/Last/UrbanControl.apk";
				try {
					URL url = new URL(apkurl);
					HttpURLConnection c = (HttpURLConnection) url.openConnection();
					c.setRequestMethod("GET");
					c.setRequestProperty("User-Agent","Mozilla/5.0 ( compatible ) ");
					c.setRequestProperty("Accept",);
//					c.setDoOutput(true);
					c.connect();
					InputStream is = c.getInputStream();

					String PATH = getPath(activity);
					File file = new File(PATH);
					File outputFile = new File(file, "UrbanControl.apk");
					if(!outputFile.exists())
						outputFile.delete();
					FileOutputStream fos = new FileOutputStream(outputFile);

					byte[] buffer = new byte[4096];
					int len1 = 0;
					while ((len1 = is.read(buffer)) != -1) {
						fos.write(buffer, 0, len1);
					}
					fos.flush();
					fos.close();
					is.close();// .apk is download to sdcard in download file
					return Boolean.valueOf(true);
				}
				catch (Exception e) {
					return Boolean.valueOf(false);
				}
			}
			@Override
			protected void onPostExecute(Boolean result) {
				if(result.booleanValue()) {
					try {
						String PATH = getPath(activity);
						File file = new File(PATH);
						File toInstall = new File(file, "UrbanControl.apk");
						if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//							Uri apkUri = FileProvider.getUriForFile(activity, BuildConfig.APPLICATION_ID + ".provider", toInstall);
//							Intent intent = new Intent(Intent.ACTION_INSTALL_PACKAGE);
//							intent.setData(apkUri);
//							intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION|Intent.FLAG_ACTIVITY_CLEAR_TOP);
//							activity.startActivity(intent);
							Intent LaunchIntent = activity.getPackageManager().getLaunchIntentForPackage("com.lbmotion.updatehanita");
							activity.startActivity(LaunchIntent);
							try {
								Intent intent = new Intent(Intent.ACTION_DELETE);
								intent.setData(Uri.parse("package:com.lbmotion.urbancontrol"));
								activity.startActivity(intent);
							}
							catch (Exception e) {}
							catch (Error e) {}
						} else {
							Uri apkUri = Uri.fromFile(toInstall);
							Intent intent = new Intent(Intent.ACTION_VIEW);
							intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
							intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							activity.startActivity(intent);
						}
					}
					catch (Exception e) {
						Toast.makeText(activity.getApplicationContext(), "Update error!", Toast.LENGTH_LONG).show();
					}
				}
				else {
					Toast.makeText(activity.getApplicationContext(), "Update error!", Toast.LENGTH_LONG).show();
				}
				try{progressDialog.dismiss();}catch (Exception e) {}catch (Error e) {}
			}
		}).execute();
	}
*/
    public static String getPath(AppCompatActivity activity) {
        File dir = null;
        if(android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
            File storageDir = android.os.Environment.getExternalStorageDirectory();
            dir = new File(storageDir.getAbsolutePath(),"Hanita");
            if(!dir.exists())
                dir.mkdirs();
            dir = new File(dir.getAbsolutePath(),"download");
        }
        else {
            dir = activity.getFileStreamPath("download");
        }
        if(!dir.exists())
            dir.mkdirs();
        return dir.getAbsolutePath();
    }

	public static class RemarkHasProperty {
		public String mName;
		public int    mType;
	}
/*
	public static void hideKeyboard() {
		InputMethodManager imm = (InputMethodManager) MainActivity.instance.getSystemService(Activity.INPUT_METHOD_SERVICE);
		View view = MainActivity.instance.getCurrentFocus();
		if (view == null) {
			view = new View(MainActivity.instance);
		}
		imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
	}
*/
}
