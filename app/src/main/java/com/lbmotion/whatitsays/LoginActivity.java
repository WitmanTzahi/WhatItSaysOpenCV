package com.lbmotion.whatitsays;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import androidx.appcompat.app.AppCompatActivity;

import com.lbmotion.whatitsays.common.BaseActivity;
import com.lbmotion.whatitsays.communication.Base;
import com.lbmotion.whatitsays.communication.DoLogin;
import com.lbmotion.whatitsays.communication.DoLoginHTTP;
import com.lbmotion.whatitsays.data.DB;
import com.lbmotion.whatitsays.data.List3;
import com.lbmotion.whatitsays.data.LoginData;
import com.lbmotion.whatitsays.data.Parameters;
import com.lbmotion.whatitsays.data.RecordStore;
import com.lbmotion.whatitsays.data.TicketInformation;
import com.lbmotion.whatitsays.managers.SendTicketsAndPictures;
import com.lbmotion.whatitsays.splash.Splash;

import java.util.Date;

public class LoginActivity extends BaseActivity {
	private static final String TAG = "LoginActivity";
	private boolean 			authorityIsOn = false;
	private LinearLayout 		linearLayoutAuthority;
	public static String 		username = "";
	private EditText 			name;
	private EditText 			authority;
	private EditText 			password;
	private boolean				displaySplashScreen = true;
	private TextView 			tickets,pictures;
	private boolean 			isDataLoadingRunning = false;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		String installVersion = "";
		try {
			PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_ACTIVITIES);
			if (info != null && info.versionName != null && !info.versionName.equals(""))
				installVersion = info.versionName.toString();
		}
		catch (Exception e) {}
		try {
			((AppCompatActivity)this).getSupportActionBar().setTitle(R.string.login_welcome);
		}
		catch (Exception e) {}

		Base.version = installVersion;
		Parameters.context = getApplicationContext();
		RecordStore.context = getApplicationContext();

		if (UCApp.loginData == null || UCApp.loginData.PakachNm == null)
			UCApp.loginData = LoginData.restore();

		UCApp.canWorkOffLine = !DB.GetDB();
//		makeCitiesDB();
		DB.readCitiesDB();
		linearLayoutAuthority = (LinearLayout)findViewById(R.id.layout_authority);
		name = (EditText)findViewById(R.id.name);
		authority = (EditText)findViewById(R.id.authority);
		password = (EditText)findViewById(R.id.password);

		tickets = (TextView)findViewById(R.id.tickets);
		pictures = (TextView)findViewById(R.id.pictures);

		name.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				checkIfDoneKey(actionId);
				return false;
			}
		});
		password.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				checkIfDoneKey(actionId);
				return false;
			}
		});
		authority.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				checkIfDoneKey(actionId);
				return false;
			}
		});
//
		if(UCApp.loginData == null || UCApp.loginData.authority.length() == 0)
			linearLayoutAuthority.setVisibility(View.VISIBLE);
		else
			authority.setText(UCApp.loginData.authority);
//
		if(displaySplashScreen) {
			displaySplashScreen = false;
			Intent intent = new Intent(this, Splash.class);
			this.startActivity(intent);
		}
//		
		((Button)findViewById(R.id.dologin)).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				callLogin();
			}
		});
		name.requestFocus();
		try {
			TicketInformation.createWorkingDirectory();
		}
		catch(Exception e) {
			try {
                Thread.sleep(1000);}catch (InterruptedException ie) {}
			TicketInformation.createWorkingDirectory();
		}
/*
		UCApp.ticketInformation.restoreTicket(TicketInformation.getPath("Data")+"Ticket");
		if(UCApp.ticketInformation.didCrashInTheMiddle) {
			UCApp.recoverTicketInformation = new TicketInformation();
			UCApp.recoverTicketInformation.restoreTicket(TicketInformation.getPath("Data")+"Ticket");
		}
		if(UCApp.ticketInformation.inProccess) {
			Toast.makeText(LoginActivity.this, R.string.cancel_report_after_crush, Toast.LENGTH_LONG).show();
			UCApp.ticketInformation.date = Util.makeDate();
			UCApp.ticketInformation.timestamp = (new Date()).getTime();
			UCApp.ticketInformation.reasonCode = (short)9999;
			BaseActivity.addImagesToQueue(true,false);//Add the cancelled ticket Handle error
		}
*/
		UCApp.ticketInformation = new TicketInformation();
		((TextView)findViewById(R.id.welcome_version)).setText(getInstalledAppVersion());
		isDataLoadingRunning = true;
		doShowDataLoading();
/*
		if (Build.VERSION.SDK_INT >= 23) {
			Vector<String> list = new Vector<String>();
			if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
				list.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
			if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
				list.add(Manifest.permission.READ_EXTERNAL_STORAGE);
			if (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
				list.add(Manifest.permission.ACCESS_FINE_LOCATION);
			if (checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
				list.add(Manifest.permission.ACCESS_COARSE_LOCATION);
			if (checkSelfPermission(android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
				list.add(Manifest.permission.CAMERA);
			if (checkSelfPermission(android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED)
				list.add(Manifest.permission.RECORD_AUDIO);
			if (checkSelfPermission(android.Manifest.permission.MODIFY_AUDIO_SETTINGS) != PackageManager.PERMISSION_GRANTED)
				list.add(Manifest.permission.MODIFY_AUDIO_SETTINGS);
			if (checkSelfPermission(android.Manifest.permission.CAPTURE_SECURE_VIDEO_OUTPUT) != PackageManager.PERMISSION_GRANTED)
				list.add(Manifest.permission.CAPTURE_SECURE_VIDEO_OUTPUT);
			if (checkSelfPermission(android.Manifest.permission.CAPTURE_VIDEO_OUTPUT) != PackageManager.PERMISSION_GRANTED)
				list.add(Manifest.permission.CAPTURE_VIDEO_OUTPUT);
			String[] requestPermissions = new String[list.size()];
			for(int i = 0;i < list.size();i++)
				requestPermissions[i] = list.get(i);
			if(requestPermissions.length > 0)
				ActivityCompat.requestPermissions(this, requestPermissions, 1);
		}
		else { //permission is automatically granted on sdk<23 upon installation
			Log.v(TAG,"Permission is granted");
		}
*/
	}
	
	protected void checkIfDoneKey(int actionId) {
		if (actionId == EditorInfo.IME_ACTION_DONE)
			callLogin();
	}

	@Override
	public void onBackPressed() {
//		super.onBackPressed();
	}

	@Override
	protected void onStop() {
		isDataLoadingRunning = false;
		super.onStop();
	}
		
	private void doShowDataLoading() {
		(new AsyncTask<String, Integer, Boolean>() {
			@Override
			protected Boolean doInBackground(String... params) {
				try {
                    Thread.sleep(5000);}catch (InterruptedException ie) {}
				return Boolean.valueOf(true);
			}
			@Override
			protected void onPostExecute(Boolean result) {
				try {
					tickets.setText(getString(R.string.login_docs)+":"+ SendTicketsAndPictures.getNumberOfTicketsWaiting());
					pictures.setText(getString(R.string.login_pictures)+":"+SendTicketsAndPictures.getNumberOfPicturesWaiting());
				}
				catch (Exception e) {}
				if(isDataLoadingRunning)
					doShowDataLoading();
						
			}
		}).execute();
	}

	protected void callLogin() {
		if(checkLoginInput()) {
			doLogin();
//			MainActivity.pressLoginCommand = true;
		}
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		if(hasFocus && name.hasFocus()) {
			showSoftKeyboard(name,this);
		}
	}


	@Override
	public boolean onMenuOpened(int featureId, Menu menu) {
		try {
			MenuItem menuItem = null;
			for (int i = 0; i < menu.size(); i++) {
				if (menu.getItem(i).getItemId() == R.id.middleware) {
					menuItem = menu.getItem(i);
					break;
				}
			}
			if (menuItem != null) {
				menuItem.setTitle(Base.communicationViaMiddleware?R.string.middlewareOff:R.string.middlewareOn);
			}
		} catch (Exception e) {
		}
		return super.onMenuOpened(featureId, menu);
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		if(authorityIsOn)
			inflater.inflate(R.menu.login_menu2, menu);
		else
			inflater.inflate(R.menu.login_menu1, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.login_authority:
				authorityIsOn = true;
				linearLayoutAuthority.setVisibility(View.VISIBLE);
 				return true;
// 			case R.id.last_version:
//				BaseActivity.update(false,this);
//				return true;
			case R.id.exit:
				UCApp.doExitNow = true;
				setResult(RESULT_OK, null);
				finish();
				Intent intent = new Intent(Intent.ACTION_MAIN);
				intent.addCategory(Intent.CATEGORY_HOME);
				startActivity(intent);
				System.exit(0);
				return true;
			case R.id.middleware:
				Base.communicationViaMiddleware = !Base.communicationViaMiddleware;
				item.setTitle(Base.communicationViaMiddleware?R.string.middlewareOff:R.string.middlewareOn);
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	
	private boolean checkLoginInput() {
		String errorMessage = "";
		if(name.getText().toString().length() == 0) 
			errorMessage = LoginActivity.this.getString(R.string.login_username_error);
		if(password.getText().toString().length() == 0)
			if(errorMessage.length() == 0)
				errorMessage = LoginActivity.this.getString(R.string.login_password_error);
			else
				errorMessage += ", "+LoginActivity.this.getString(R.string.login_password_error);
		if(authority.getText().toString().length() == 0)
			if(errorMessage.length() == 0)
				errorMessage = LoginActivity.this.getString(R.string.login_authority_error);
			else
				errorMessage += ", "+LoginActivity.this.getString(R.string.login_authority_error);
		if(errorMessage.length() > 0) {
			BaseActivity.showErrorMessage(errorMessage,this);
			return false;
		}
		return true;
	}

	private void doLogin() {
		(new AsyncTask<String, Integer, Boolean>() {
			private String inputName,inputPassword,inputAuthority;
			@Override
			protected void onPreExecute() {
				progressDialog = new ProgressDialog(LoginActivity.this);
				progressDialog.setMessage(getResources().getString(R.string.please_wait));
				progressDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.button_cancel), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						if(Base.communicationViaMiddleware)
							DoLogin.abort();
						else
							DoLoginHTTP.abort();
					}
				});
				progressDialog.setIndeterminate(true);
				progressDialog.setCancelable(false);
				progressDialog.show();
				username = inputName = name.getText().toString();
				inputPassword = password.getText().toString();
				inputAuthority = authority.getText().toString();

			}
			@Override
			protected Boolean doInBackground(String... params) {
				if(Base.communicationViaMiddleware) {
					DoLogin.didDoLoginDone = false;
					DoLogin doLogin = DoLogin.doLogin(inputName, inputPassword, inputAuthority);
					if (doLogin != null && !doLogin.errorOccurred) {
						UCApp.loginData = doLogin.loginData;
						UCApp.workModeOffline.set(false);
						UCApp.loginData.save();
					}
				}
				else {
					DoLoginHTTP.didDoLoginDown = false;
					DoLoginHTTP doLogin = DoLoginHTTP.doLogin(inputName, inputPassword, inputAuthority);
					if (doLogin != null && !doLogin.errorOccurred) {
						UCApp.loginData = doLogin.loginData;
						UCApp.workModeOffline.set(false);
						UCApp.loginData.save();
					}
				}
				return Boolean.valueOf(true);
			}
			@Override
			protected void onPostExecute(Boolean result) {
				try {progressDialog.dismiss();} catch (Exception e) {}catch (Error e) {}
				DoLogin.me = null;
				DoLoginHTTP.me = null;
				if(UCApp.loginData == null) {
					showLoginError(R.string.login_error_communication);
				}
				else if(UCApp.loginData.error) {
					if(UCApp.loginData.timeError)
						showLoginError(R.string.login_error_clock);
					else if(UCApp.loginData.openFailed)
						showLoginError(R.string.login_error_communication);
					else
						showLoginError(R.string.login_error_server);
				}
				else {
					if(UCApp.loginData.SwNotActive.equals("0")) {
						UCApp.loginData.loginTime = (new Date()).getTime();
						UCApp.loginData.save();
						continueAfterLoginDoLoadDohot();
					}
					else {
						showLoginError(UCApp.loginData.AbsenceRason);
					}
				}
			}
		}).execute();
	}
	
	private void showLoginError(String error) {
		try {
			UCApp.loginData = LoginData.restore();
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			TextView message = new TextView(this);
			message.setText(error);
			message.setPadding(10, 10, 10, 10);
			message.setTextColor(Color.RED);
			message.setTextSize(20);
			builder.setView(message);
			builder.setCancelable(false);
			builder.setPositiveButton(getString(R.string.button_continue),
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							dialog.dismiss();
							UCApp.doExitNow = true;
							LoginActivity.this.setResult(RESULT_OK, null);
							LoginActivity.this.finish();
						}
					}
			);
			AlertDialog alert = builder.create();
			alert.show();
		}
		catch (Exception e) {}
		catch (Error e) {}
	}

	private void showLoginError(int errorId) {
		try {
			UCApp.loginData = LoginData.restore();
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			TextView message = new TextView(this);
			message.setText(LoginActivity.this.getString(errorId));
			message.setPadding(10, 10, 10,10);
			message.setTextColor(Color.RED);
			message.setTextSize(20);
			builder.setView(message);
			builder.setCancelable(false);
			builder.setPositiveButton(getString(R.string.button_continue),
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							dialog.dismiss();
						}
					}
			);
			if(UCApp.canWorkOffLine) {
				builder.setNegativeButton(LoginActivity.this.getString(R.string.button_offline),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.dismiss();
								if(UCApp.loginData.username.equals(name.getText().toString()) && UCApp.loginData.password.equals(password.getText().toString()) &&
										(authority.toString().length() == 0 || UCApp.loginData.authority.equals(authority.getText().toString()))) {
									UCApp.workModeOffline.set(true);
									afterLoadDochot();
								}
								else {
									showErrorMessage(LoginActivity.this.getResources().getString(R.string.offline_credential_error), LoginActivity.this);
								}
							}
						});
			}
			AlertDialog alert = builder.create();
			alert.show();
		}
		catch (Exception e) {}
		catch (Error e) {}
	}

	public void continueAfterLoginDoLoadDohot() {
		(new AsyncTask<String, Integer, Boolean>() {
			@Override
			protected void onPreExecute() {
				if(UCApp.loginData.DefaultE != -1) {
					int DefaultE = UCApp.loginData.DefaultE;
					UCApp.loginData.DefaultE = -2;
					for(short i = 0;i < DB.areas.size();i++) {
						List3 list3 = DB.areas.get(i);
						if(list3.code == DefaultE) {
							UCApp.loginData.DefaultE = i;
							break;
						}
					}
				}
				if(TicketInformation.hasToLoadTickets()) {
					try {
						progressDialog = new ProgressDialog(LoginActivity.this);
						progressDialog.setMessage(getResources().getString(R.string.loading_tickets));
						int id = UCApp.canWorkOffLine ? R.string.button_offline : R.string.exit;
						progressDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(id), new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								dialog.dismiss();
								gotoOfflineMode();
							}
						});
						progressDialog.setIndeterminate(true);
						progressDialog.setCancelable(false);
						progressDialog.show();
					}
					catch (Exception e) {}
				}
			}
			@Override
			protected Boolean doInBackground(String... params) {
				if(UCApp.ticketsAndPicturesBinder != null)
					UCApp.ticketsAndPicturesBinder.setLoadOnlyTickets(true);
				while(TicketInformation.hasToLoadTickets() && !UCApp.workModeOffline.get())
					try {
                        Thread.sleep(500);}catch (InterruptedException ie) {}
				return Boolean.valueOf(true);
			}

			@Override
			protected void onPostExecute(Boolean result) {
				try {progressDialog.dismiss();} catch (Exception e) {}catch (Error e) {}
				progressDialog = null;
				if(!UCApp.workModeOffline.get()) {
					TicketInformation.authority = UCApp.loginData.authority;
					UCApp.ticketInformation.userid = UCApp.loginData.UsrCounter+"";
					UCApp.ticketInformation.LK = UCApp.loginData.Lk;
					loadDochot(LOADING_MODE_AT_LOGIN,R.string.loading_docs,true);
				}
			}
		}).execute();
	}
/*
	public void makeCitiesDB() {
		try {
			InputStream in = getAssets().open("cities.txt");
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			String line = reader.readLine();
			while(line != null) {
				String shortStreet;
				String city;
//				int s,e = line.indexOf('\t');
				int s,e = line.indexOf(',');
//				s = e+1;e = line.indexOf('\t',s);
				s = e+1;e = line.indexOf(',',s);
				shortStreet = line.substring(s, e).trim();
				s = e+1;e = line.indexOf(',',s);
				s = e+1;e = line.indexOf(',',s);
				city = line.substring(s, e).trim();
				if(shortStreet.length() > 0 && shortStreet.charAt(0) == '\"')
					shortStreet = shortStreet.substring(1).trim();
				if(shortStreet.length() > 0 && shortStreet.charAt(shortStreet.length()-1) == '\"')
					shortStreet = shortStreet.substring(0,shortStreet.length()-1).trim();
				if(city.length() > 0 && city.charAt(0) == '\"')
					city = city.substring(1).trim();
				if(city.length() > 0 && city.charAt(city.length()-1) == '\"')
					city = city.substring(0,city.length()-1).trim();
				Vector<String> vectorShortStreet = DB.cities.get(city);
				if(vectorShortStreet == null) {
					vectorShortStreet = new Vector<String>();
					DB.cities.put(city,vectorShortStreet);
				}
				Log.i(TAG,city+":"+shortStreet);
				vectorShortStreet.add(shortStreet);
				line = reader.readLine();
			}
			Vector<String> cities = new Vector<String>(DB.cities.keySet());
			Collections.sort(cities);
			for(String city : cities) {
				Vector<String> vectorShortStreet = DB.cities.get(city);
				Collections.sort(vectorShortStreet);
			}
			File storageDir = android.os.Environment.getExternalStorageDirectory();
			File dir = new File(storageDir.getAbsolutePath(),"Hanita");
			if(!dir.exists())
				dir.mkdirs();
			File file = new File(dir, "cities.db");
			ObjectOutput out = new ObjectOutputStream(new FileOutputStream(file));
			out.writeInt(cities.size());
			for(String city : cities) {
				Vector<String> vectorShortStreet = DB.cities.get(city);
				out.writeUTF(city);
				out.writeInt(vectorShortStreet.size());
				for(int i = 0;i < vectorShortStreet.size();i++) {
					out.writeUTF(vectorShortStreet.get(i));
				}
			}
			out.flush();
			out.close();
		}
		catch (Exception fne) {
			fne.getMessage();
		}
	}
*/
/*
	public void makeCitiesDB() {
		try {
			InputStream in = getAssets().open("cities.txt");
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			int s = 1;
			String line = reader.readLine();
			while(line != null) {
				ShortStreet shortStreet = new ShortStreet();
				String city;
				int e = line.indexOf('\t');
				shortStreet.setCode1(Integer.parseInt(line.substring(s, e)));
				s = e+1;e = line.indexOf('\t',s);
				shortStreet.setName(line.substring(s, e));
				s = e+1;e = line.indexOf('\t',s);
				shortStreet.setCode2(Integer.parseInt(line.substring(s, e)));
				s = e+1;e = line.indexOf('\t',s);
				city = line.substring(s, e);
				Vector<ShortStreet> vectorShortStreet = DB.cities.get(city);
				if(vectorShortStreet == null) {
					vectorShortStreet = new Vector<ShortStreet>();
					DB.cities.put(city,vectorShortStreet);
				}
				vectorShortStreet.add(shortStreet);
				line = reader.readLine();
				s = 0;
			}
			Vector<String> cities = new Vector<String>(DB.cities.keySet());
			Collections.sort(cities);
			for(String city : cities) {
				Vector<ShortStreet> vectorShortStreet = DB.cities.get(city);
				Collections.sort(vectorShortStreet, new Comparator<ShortStreet>() {
				     @Override
				     public int compare(ShortStreet entry1, ShortStreet entry2) {
				    	 return entry1.getName().compareTo(entry1.getName());
				     }
				});
			}
			File sdCard = Environment.getExternalStorageDirectory();
			File dir = new File (sdCard.getAbsolutePath());
			File file = new File(dir, "cities.db");
		    ObjectOutput out = new ObjectOutputStream(new FileOutputStream(file));
		    out.writeInt(cities.size());
			for(String city : cities) {
				Vector<ShortStreet> vectorShortStreet = DB.cities.get(city);
			    out.writeUTF(city);
			    out.writeInt(vectorShortStreet.size());
			    for(int i = 0;i < vectorShortStreet.size();i++) {
			    	out.writeInt(vectorShortStreet.get(i).code1);
			    	out.writeInt(vectorShortStreet.get(i).code2);
			    	out.writeUTF(vectorShortStreet.get(i).name);
			    }
		    }
	        out.close();			
		} 
		catch (Exception fne) {
			fne.getMessage();
		}
	}
*/
}
