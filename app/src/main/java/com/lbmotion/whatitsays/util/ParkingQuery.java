package com.lbmotion.whatitsays.util;

import android.util.Log;

import com.lbmotion.whatitsays.LicenseQueryCompleted;
import com.lbmotion.whatitsays.MainActivity;
import com.lbmotion.whatitsays.R;
import com.lbmotion.whatitsays.UCApp;
import com.lbmotion.whatitsays.common.BaseActivity;
import com.lbmotion.whatitsays.communication.Base;
import com.lbmotion.whatitsays.communication.LisenceNumber;
import com.lbmotion.whatitsays.communication.LisenceNumberHTTP;
import com.lbmotion.whatitsays.data.PictureInformation;
import com.lbmotion.whatitsays.data.TicketInformation;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

/*
2	טעות במספר הרישוי
3	הנהג הגיע
4	מבוקש משטרה
5	רכב נכה
6	הנהג ברח
7	דוח תמונה
9	דווח מפגע
10	טעות פקח
11	אמצעי תשלום קיים מצולם
12	בדיקת משרד נכה/תו דייר
13	טעות בסעיף עבירה
14	היעדר סימון/תמרור
15	רכב היברידי
16	תו דייר/גיל הזהב/בתפקיד
17	תקלת מסופון
18	תקלת תקשורת
19	תושב
20	רכב גנוב
21	חייב ארנונה
22	תו גנוב
23	דוח אזהרה
24	אחר
25	המפגע הוסר
26	תו נכה
27	הנהג ברכב
28	הנהג הציג כרטיס מדחן
50	לאחר כבוי
51	תו נכה
52	חניה סלולרית
53	תו דייר
54	דוח כפול
55	העבירה לא בתוקף
56	הפעלת קוצב זמן
57	חניה כחוק
58	כשל תקשורת לשרת
59	כשל תקשורת אוטומציה
60	כשל תקשורת פנגו
61	כשל תקשורת סלופארק
62	צילום לפני בדיקה
63	תו דייר
64	היתר מיוחד
65	רכב חכם
66	פנגו
67	סלופארק
68	תו תושב
69	התר מיוחד בתשלום
70	תו דייר מגורי קבע
71	תו זמני
72	תו מקומי
73	תו שכירות
74	התו הירוק
75	תו חריג
76	תו סטודנט
77	יקיר חיפה
78	תו תושב חדש
79	תו חריג לעמותות
80	תו עובד בית ספר
81	דייר
82	חניה סלולארית
83	מקומי
84	ביטול-תו חניה
85	ביטול-טעות הקלדה
86	תו מיוחד
88	כפילות רכב נטוש
89	שם/כתובת שגויה
90	טעות סופר
91	הבעלים הגיע
92	כלב ברח
93	רכב מזהם
94	רכב לא מזהם
95	הנהג הציג  טופס הזמנת מסנן
96	חניה בקליק
97	ללא מספר דוח
98	יש לעדכן תאריך ושעה למסופון
99	קריאת מוקד
100	רכב  מורשה
101	תו רווחה
102	ללא סיבה
105	קיים היתר פריקה באתר בנייה
106	הרכב פנה ימינה
107	לא נעברה עבירה
108	החנה במפרץ חניה
170	תחילת עבודה
171	סיום עבודה
*/

public class ParkingQuery extends Thread {
	public static final String TAG = "ParkingQuery";
	public enum MenuTypes {
		beginning,
		option1,
		option2,
		option3,
		option4,
		option5,
		noOptions,
		doCancel,
		doContinue,
		doReturn,
		doTryAgain
	}
	//           originalLicense, license
	public static HashMap<String,String>  licensePlates = new HashMap<>();
	public static HashMap<String,Integer> resultLicensePlates = new HashMap<>();
	public static long 			 		  endTime;
	public MenuTypes 			 		  pageCarActiveMenu;
	public MenuTypes 		 	 		  pageCarCommandLeft;
	public MenuTypes 			 		  pageCarCommandRight;
	public LicenseQueryCompleted 		  licenseQueryCompleted;

	public ParkingQuery(LicenseQueryCompleted licenseQueryCompleted) {
		endTime = (new Date()).getTime()+20*60*1000  * 20;//^^^^
		this.licenseQueryCompleted = licenseQueryCompleted;
	}

	public void run() {
		licensePlates = new HashMap<>();
		resultLicensePlates = new HashMap<>();
		while(true) {
			try { Thread.sleep(250);} catch (InterruptedException ie) {}
			Vector<PictureInformation>  pictureInformations = getCarInformationId();
			if (pictureInformations == null) {
//				Log.i(TAG+"X","pictureInformations == null");
				break;
			}
			try { Thread.sleep(250);} catch (InterruptedException ie) {}
			if(pictureInformations.size() == 0) {
				try { Thread.sleep(500);} catch (InterruptedException ie) {}
				continue;
			}
//			Log.i(TAG+"X","WORKING"+licensePlates);
			try {
				for (PictureInformation pictureInformation : pictureInformations) {
					if(!licensePlates.containsKey(pictureInformation.carInformationId)) {
//						Log.i(TAG+"X","CONTINUE 1"+licensePlates+":"+pictureInformation.carInformationId);
						continue;
					}
					pageCarCommandLeft = pageCarCommandRight = pageCarActiveMenu = MenuTypes.beginning;
					UCApp.checkLincense = null;
					TicketInformation ti = new TicketInformation();
					ti.CarInformationId = licensePlates.get(pictureInformation.carInformationId);
					if(resultLicensePlates.containsKey(ti.CarInformationId)) {
						licenseQueryCompleted.notifyQueryCompletion(ti.CarInformationId, pictureInformation.carInformationId, resultLicensePlates.get(ti.CarInformationId).intValue());
//						Log.i(TAG+"X","CONTINUE 2"+licensePlates+":"+pictureInformation.carInformationId);
						continue;
					}
					ti.streetCode = UCApp.streetCode;
					ti.streetName = UCApp.streetName;
					ti.latitude = pictureInformation.latitude;
					ti.longitude = pictureInformation.longitude;
					if(UCApp.streetNumber == null)
						MainActivity.readConfigFile();
                    if (Base.communicationViaMiddleware) {
						UCApp.checkLincense = (LisenceNumber.doCheck(ti.CarInformationId,"NO")).checkLincense;
					} else {
						UCApp.checkLincense = (LisenceNumberHTTP.doLisenceNumberHTTP(ti.CarInformationId,"NO")).checkLincense;
					}
					ti.checkLincenseTime = (new Date()).getTime();
					if (UCApp.checkLincense != null) {
/*
						Log.i(TAG+"X","SugTavT"+UCApp.checkLincense.SugTavT);
						Log.i(TAG+"X","AveraAct"+UCApp.checkLincense.AveraAct);
						Log.i(TAG+"X","SugTav"+UCApp.checkLincense.SugTav);
						Log.i(TAG+"X","SwWarning"+UCApp.checkLincense.SwWarning);
						Log.i(TAG+"X","SwAveraKazach"+UCApp.checkLincense.SwAveraKazach);
						Log.i(TAG+"X","TxtKazach"+UCApp.checkLincense.TxtKazach);
						Log.i(TAG+"X","TxtAzara"+UCApp.checkLincense.TxtAzara);
						Log.i(TAG+"X","keshelMsg"+UCApp.checkLincense.keshelMsg);
						Log.i(TAG+"X","PrintKod"+UCApp.checkLincense.PrintKod);
						Log.i(TAG+"X","PrintMsg"+UCApp.checkLincense.PrintMsg);
						Log.i(TAG+"X","SwCellParkingPaid"+UCApp.checkLincense.SwCellParkingPaid);
*/
						ti.handicap = UCApp.checkLincense.handicap;
						ti.wanted = UCApp.checkLincense.handicap;
						ti.doubleReporting = UCApp.checkLincense.doubleReporting;
						ti.SwKazach = UCApp.checkLincense.SwKazach;
						ti.SugTavT = UCApp.checkLincense.SugTavT;
						ti.AveraAct = UCApp.checkLincense.AveraAct;
						ti.SugTav = UCApp.checkLincense.SugTav;
						ti.TxtAzara = UCApp.checkLincense.TxtAzara;
						ti.wantedAction = UCApp.checkLincense.wantedAction;
						ti.SwAveraKazach = UCApp.checkLincense.SwAveraKazach;
						ti.handicapAction = UCApp.checkLincense.handicapAction;
						ti.keshelKod = UCApp.checkLincense.keshelKod;
						if (UCApp.checkLincense.SwWarning.equals("1")) {
							ti.sSwHatraa = true;
							ti.vSwHatraa = true;
						}
						if (UCApp.checkLincense.KazachRemark > 0) {
							ti.printTextBox = true;
							ti.pakachFreeText = UCApp.gContext.getString(R.string.check_kazack);
							Calendar calendar = Calendar.getInstance();
							if (ti.checkLincenseTime < UCApp.checkLincense.KazachRemark * 60 * 1000L)
								ti.checkLincenseTime = (new Date()).getTime();
							calendar.setTimeInMillis(ti.checkLincenseTime - UCApp.checkLincense.KazachRemark * 60 * 1000L);
							String time = android.text.format.DateFormat.getTimeFormat(UCApp.gContext).format(calendar.getTime()) + " " + android.text.format.DateFormat.getDateFormat(UCApp.gContext).format(calendar.getTime());
							ti.pakachFreeText = ti.pakachFreeText.replace("%", time);
							ti.pakachFreeText = ti.pakachFreeText.replace("$", UCApp.checkLincense.KazachRemark + "");
						}
						ti.SwCellParkingPaid = UCApp.checkLincense.SwCellParkingPaid;
						checkLincenseForErrors(ti);
						final int re_query [] = {18, 58, 59, 60, 61};
						final int no_report [] = {2, 5, 10, 11, 12, 13, 14, 15, 16, 17, 19, 25, 26, 27, 28, 50, 51, 52, 53, 54, 55, 56, 57, 62, 63, 64, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75,
												  76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 88, 89, 90, 91, 92, 93, 94, 95, 96, 97, 98, 99, 100, 101, 102, 105, 106, 107, 108, 170, 171, 3};
//						final int report [] = {4, 6, 7, 9, 20, 21, 22, 23, 24};
//						Log.i(TAG+"X","case 0");
						if(pageCarCommandLeft != MenuTypes.doTryAgain && !contains(UCApp.checkLincense.keshelKod,re_query)) {
							if (pageCarCommandLeft != MenuTypes.beginning || pageCarCommandRight != MenuTypes.beginning || pageCarActiveMenu != MenuTypes.beginning) {
								ti.packachDecisionCode = 0;
//								Log.i(TAG+"X","case 1");
							}
							else {
								if (contains(UCApp.checkLincense.keshelKod,no_report)) {
									ti.packachDecisionCode = 0;
//									Log.i(TAG + "X", "case 2");
								}
								else {
									ti.packachDecisionCode = 1;
//									Log.i(TAG + "X", "case 3");
								}
							}
							UCApp.checkLincense.save(ti.CarInformationId, pictureInformation.carInformationId, ti.checkLincenseTime, ti.packachDecisionCode);
							saveTicket(ti);
							synchronized (resultLicensePlates) {
								resultLicensePlates.put(ti.CarInformationId, Integer.valueOf(ti.packachDecisionCode));
							}
							licenseQueryCompleted.notifyQueryCompletion(ti.CarInformationId, pictureInformation.carInformationId, ti.packachDecisionCode);
						}
					}
				}
			} catch (Exception e) {
				Log.i(TAG,"Run error:"+e.getMessage());
//				Log.i(TAG+"X","ERROR");
			}
		}
	}

	private String checkLincenseForErrors(TicketInformation ti) {
		String errorMsg = "";
		if(UCApp.checkLincense.wanted) {
			if(UCApp.checkLincense.wantedAction == 1)
				errorMsg += "parentActivity.getString(R.string.car_infomation_wanted1)";
			else
				errorMsg += "parentActivity.getString(R.string.car_infomation_wanted2)";
		}
		if(UCApp.checkLincense.doubleReporting) {
			if(errorMsg.length() > 0)
				errorMsg += '\n';
			if(UCApp.checkLincense.doubleReportingAction == 1)
				errorMsg += "parentActivity.getString(R.string.car_infomation_double1)";
			else
				errorMsg += "parentActivity.getString(R.string.car_infomation_double2)";
		}
/*
		if(UCApp.checkLincense.handicap) {
			if(errorMsg.length() > 0)
				errorMsg += '\n';
			if(UCApp.checkLincense.handicapAction == 1)
				errorMsg += activity.getString(R.string.car_infomation_handicap1);
			else
				errorMsg += activity.getString(R.string.car_infomation_handicap2);
		}
*/
/*
		if(UCApp.checkLincense.resident) {
			if(errorMsg.length() > 0)
				errorMsg += '\n';
			if(UCApp.checkLincense.residentAction == 1)
				if(UCApp.checkLincense.handicapAction == 1)
					errorMsg += activity.getString(R.string.car_infomation_resident1);
				else
					errorMsg += activity.getString(R.string.car_infomation_resident2);
		}
*/
/*
		if(UCApp.checkLincense.cellularParking) {
			if(errorMsg.length() > 0)
				errorMsg += '\n';
			if(UCApp.checkLincense.cellularParkingAction == 1)
				errorMsg += activity.getString(R.string.car_infomation_cellular_parking1);
			else
				errorMsg += activity.getString(R.string.car_infomation_cellular_parking2);
		}
*/
		if(UCApp.checkLincense.SwKazach == 1) {
			if(errorMsg.length() > 0)
				errorMsg += '\n';
/*
			errorMsg += activity.getString(R.string.car_infomation_command_keshel);
			if(UCApp.checkLincense.SwAveraKazach.equals("1"))
				errorMsg += UCApp.checkLincense.TxtKazach+" "+activity.getString(R.string.car_infomation_kazach1);
			else
				errorMsg += UCApp.checkLincense.TxtKazach+" "+activity.getString(R.string.car_infomation_kazach2);
*/
			errorMsg += UCApp.checkLincense.TxtKazach;
		}
		else if(UCApp.checkLincense.SwKazach > 1) {
			if(errorMsg.length() > 0)
				errorMsg += '\n';
//			errorMsg += activity.getString(R.string.car_infomation_command_keshel);
//			errorMsg += UCApp.checkLincense.TxtKazach;
		}
		if(UCApp.checkLincense.SugTavT.length() > 0) {
			if(errorMsg.length() > 0)
				errorMsg += '\n';
//			errorMsg += activity.getString(R.string.car_infomation_command_keshel);
			errorMsg += UCApp.checkLincense.SugTavT+".";
		}
		if(UCApp.checkLincense.AveraAct.length() > 0) {
			if(errorMsg.length() > 0)
				errorMsg += '\n';
//			errorMsg += activity.getString(R.string.car_infomation_command_keshel);
			errorMsg += UCApp.checkLincense.AveraAct+".";
		}
		if(UCApp.checkLincense.SugTav.length() > 0) {
			if(errorMsg.length() > 0)
				errorMsg += '\n';
//			errorMsg += activity.getString(R.string.car_infomation_command_keshel);
			errorMsg += UCApp.checkLincense.SugTav+".";
		}
		if(UCApp.checkLincense.TxtAzara.length() > 0) {
			if(errorMsg.length() > 0)
				errorMsg += '\n';
//			errorMsg += activity.getString(R.string.car_infomation_command_keshel);
			errorMsg += UCApp.checkLincense.TxtAzara;
		}
		if(UCApp.checkLincense.SwWarning.equals("1")) {
			ti.sSwHatraa = true;
			ti.vSwHatraa = true;
		}
/*
		if(UCApp.checkLincense.wantedAction == 1 || UCApp.checkLincense.handicapAction == 1 || UCApp.checkLincense.residentAction == 1 || UCApp.checkLincense.cellularParkingAction == 1 || UCApp.checkLincense.doubleReportingAction == 1 ||
		   (UCApp.checkLincense.SwKazach == 1 && UCApp.checkLincense.SwAveraKazach.equals("1"))) {
			if(errorMsg.length() > 0)
				errorMsg += '\n';
			errorMsg += activity.getString(R.string.car_infomation_kazach3);
		}
*/
		if(UCApp.checkLincense.SugTavT.length()+UCApp.checkLincense.AveraAct.length()+UCApp.checkLincense.SugTav.length() > 0 ||
				UCApp.checkLincense.wantedAction == 2 || UCApp.checkLincense.handicapAction == 2 || UCApp.checkLincense.residentAction == 2 || UCApp.checkLincense.cellularParkingAction == 2 || UCApp.checkLincense.doubleReportingAction == 2 ||
				(UCApp.checkLincense.SwKazach == 1 && UCApp.checkLincense.SwAveraKazach.equals("0"))) {
			if(BaseActivity.canCancelDochByPakach() && !hasCancelOption())
				pageCarActiveMenu = MenuTypes.option2;
		}
/*
		if(UCApp.checkLincense.keshelKod > 0) {
			if(errorMsg.length() > 0)
				errorMsg += '\n';
//			errorMsg += activity.getString(R.string.car_infomation_command_keshel)+UCApp.checkLincense.keshelMsg;
			errorMsg += UCApp.checkLincense.keshelMsg;
			if(UCApp.checkLincense.rightSide == 0 && UCApp.checkLincense.leftSide == 0) {
				pageCarActiveMenu = MenuTypes.option3;
			}
			else {
				pageCarActiveMenu = MenuTypes.noOptions;
				pageCarCommandLeft = setServerCommand(UCApp.checkLincense.leftSide);
				pageCarCommandRight = setServerCommand(UCApp.checkLincense.rightSide);
			}
		}
*/
		if(UCApp.checkLincense.KazachRemark > 0) {
			ti.printTextBox = true;
			ti.pakachFreeText = "parentActivity.getString(R.string.check_kazack)";
			Calendar calendar = Calendar.getInstance();
			if(ti.checkLincenseTime < UCApp.checkLincense.KazachRemark*60*1000L)
				ti.checkLincenseTime = (new Date()).getTime();
			calendar.setTimeInMillis(ti.checkLincenseTime-UCApp.checkLincense.KazachRemark*60*1000L);
			String time = "android.license.format.DateFormat.getTimeFormat(parentActivity).format(calendar.getTime())+ +android.license.format.DateFormat.getDateFormat(parentActivity).format(calendar.getTime())";
			ti.pakachFreeText = ti.pakachFreeText.replace("%",time);
			ti.pakachFreeText = ti.pakachFreeText.replace("$",UCApp.checkLincense.KazachRemark+"");
		}
		ti.SwCellParkingPaid = UCApp.checkLincense.SwCellParkingPaid;
		return errorMsg;
	}

	private boolean contains(int KazachRemark,final int array []) {
		for (final int i : array) {
			if (i == KazachRemark) {
				return true;
			}
		}
		return false;
	}

	private boolean hasCancelOption() {
		if(pageCarActiveMenu == MenuTypes.beginning || pageCarActiveMenu == MenuTypes.option2 || pageCarActiveMenu == MenuTypes.option3 || pageCarActiveMenu == MenuTypes.option4)
			return true;
		else
			return false;
	}

	public static MenuTypes setServerCommand(byte cmd) {
		switch(cmd) {
			case 1:
				return MenuTypes.doCancel;
			case 2:
				return MenuTypes.doContinue;
			case 3:
//			return MenuTypes.doReturn;
				return MenuTypes.noOptions;
			case 4:
				return MenuTypes.doTryAgain;
		}
		return MenuTypes.noOptions;
	}

	public static boolean saveTicket(TicketInformation ti) {
		boolean returnCode;
		OutputStream os = null;
		short retryCount = 0;
		returnCode = false;
		String licensePlate = ti.CarInformationId;
		do {
			if(retryCount > 0) {
				try{Thread.sleep(250);}catch(InterruptedException ex){}
			}
			File file = null;
			try {
				ti.filename = "Ticket";
				file = TicketInformation.openFile(TicketInformation.getPath("CarPics/"+licensePlate)+ti.filename);
				file.createNewFile();
				byte[] b = ti.save();
				os = new FileOutputStream(file);
				os.write(b);
				os.flush();
				returnCode = true;
			}
			catch(Exception e) {
				file.delete();
			}
			try {if(os != null) os.flush();}catch(Exception ex) {}
			try {if(os != null) os.close();}catch(Exception ex) {}
			if(TicketInformation.openFile(TicketInformation.getPath("CarPics/"+licensePlate)+ti.filename).length() == 0)
				returnCode = false;
		} while(!returnCode && ++retryCount < 5);
		return returnCode;
	}

	public static synchronized Vector<PictureInformation> getCarInformationId() {
		if(endTime < (new Date()).getTime())
			return null;
		Vector<PictureInformation> carInformationIds = new Vector<PictureInformation>();
		try {
			File dir = TicketInformation.openFile(TicketInformation.getPath("CarPics"));
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
				if (filePlateList.get(i).getName().equals("Done"))
					return null;
				dir = TicketInformation.openFile(TicketInformation.getPath("CarPics")+filePlateList.get(i).getName());
//Run over license plate pictures
				File[] licensePlateDir = dir.listFiles();
				int count = 0;
				for (int n = 0;n < licensePlateDir.length; n++) {
					String stringToSplit = licensePlateDir[n].getName();
					if (!stringToSplit.equals("Ticket") && !stringToSplit.equals("image.jpg"))
						count++;
				}
				for (int n = 0;n < licensePlateDir.length; n++) {
					String stringToSplit = licensePlateDir[n].getName();
					if(!stringToSplit.equals("Ticket") && !stringToSplit.equals("image.jpg")) {
						String[] tempArray = stringToSplit.split("_");
						double latitude = Double.parseDouble(tempArray[1]);
						double longitude = Double.parseDouble(tempArray[2]);
						Log.i(TAG,"getCarInformationId:"+filePlateList.get(i).getName()+" "+count);
						PictureInformation pictureInformation = new PictureInformation(filePlateList.get(i).getName(), latitude, longitude, count);
						carInformationIds.add(pictureInformation);
						break;
					}
				}
			}
		}
		catch(Exception e) {}
		return carInformationIds;
	}
}
