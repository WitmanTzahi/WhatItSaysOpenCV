package com.lbmotion.whatitsays.util;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.media.ExifInterface;
import android.net.Uri;
import android.view.TouchDelegate;
import android.view.View;

import com.lbmotion.whatitsays.data.DB;
import com.lbmotion.whatitsays.data.TicketInformation;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;

import static android.graphics.BitmapFactory.decodeStream;
import static com.lbmotion.whatitsays.data.TicketInformation.openFile;

public class Util {

	public static Calendar calendar = Calendar.getInstance();

	public static short getYear() {
		return getYear(new Date());
	}

	public static short getMonth() {
		return getMonth(new Date());
	}

	public static short getDay() {
		return getDay(new Date());
	}

	public static short getHour() {
		calendar.setTime(new Date());
		return (short) calendar.get(Calendar.HOUR_OF_DAY);
	}

	public static short getHour(Date d) {
		calendar.setTime(d);
		return (short) calendar.get(Calendar.HOUR_OF_DAY);
	}

	public static short getMinute() {
		calendar.setTime(new Date());
		return (short) calendar.get(Calendar.MINUTE);
	}

	public static short getMinute(Date d) {
		calendar.setTime(d);
		return (short) calendar.get(Calendar.MINUTE);
	}

	public static short getSeconds(Date d) {
		calendar.setTime(d);
		return (short) calendar.get(Calendar.SECOND);
	}

	public static short getYear(Date date) {
		calendar.setTime(date);
		int i = calendar.get(Calendar.YEAR);
		if (i >= 2000) i -= 2000;
		return (short) i;
	}

	public static short getMonth(Date date) {
		calendar.setTime(date);
		return (short) calendar.get(Calendar.MONTH);
	}

	public static short getDay(Date date) {
		calendar.setTime(date);
		return (short) calendar.get(Calendar.DAY_OF_MONTH);
	}

	public static short getDayOfTheWeek(Date date) {
		calendar.setTime(date);
		if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY)
			return (short) 26;
		else
			return (short) calendar.get(Calendar.DAY_OF_WEEK);
	}

	public static String reverseString(String in) {
		String out = "";
		for (int i = in.length() - 1; i >= 0; i--) out += in.charAt(i);
		return out;
	}

	public static String calculatePaymentDate(short D) {
		String paymentDate = "";
		short day = (short) calendar.get(Calendar.DAY_OF_MONTH);
		short month = (short) (calendar.get(Calendar.MONTH) + 1);
		short year = (short) calendar.get(Calendar.YEAR);
		for (short i = 0; i < D; i++) {
			day++;
			if (day > 31 && (month == 1 || month == 3 || month == 5 || month == 7 || month == 8 || month == 10 || month == 12)) {
				day = 1;
				month++;
				if (month > 12) {
					month = 1;
					year++;
				}
			} else if (day > 30 && (month == 4 || month == 6 || month == 9 || month == 11)) {
				day = 1;
				month++;
			} else if (day > 29 && month == 2 && (year % 4) == 0) {
				day = 1;
				month++;
			} else if (day > 28 && month == 2 && (year % 4) != 0) {
				day = 1;
				month++;
			}
		}   // for loop
		if (("" + day).length() < 2)
			paymentDate += "0" + "" + day + "/";
		else
			paymentDate += "" + day + "/";
		if (("" + month).length() < 2)
			paymentDate += "0" + "" + month + "/";
		else
			paymentDate += "" + month + "/";
		paymentDate += "" + year;
		return paymentDate;
	}

	public static String makeDate() {
		String dd = Util.getDay() + "";
		while (dd.length() < 2) dd = '0' + dd;
		String mm = (Util.getMonth() + 1) + "";
		while (mm.length() < 2) mm = '0' + mm;
		String yy = Util.getYear() + "";
		while (yy.length() < 2) yy = '0' + yy;
		String hh = Util.getHour() + "";
		while (hh.length() < 2) hh = '0' + hh;
		String mn = Util.getMinute() + "";
		while (mn.length() < 2) mn = '0' + mn;
		return dd + "/" + mm + "/20" + yy + ' ' + hh + ':' + mn + ":00";
	}

	public static String makeDate(Date date) {
		String dd = Util.getDay(date) + "";
		while (dd.length() < 2) dd = '0' + dd;
		String mm = (Util.getMonth(date) + 1) + "";
		while (mm.length() < 2) mm = '0' + mm;
		String yy = Util.getYear(date) + "";
		while (yy.length() < 2) yy = '0' + yy;
		String hh = Util.getHour(date) + "";
		while (hh.length() < 2) hh = '0' + hh;
		String mn = Util.getMinute(date) + "";
		while (mn.length() < 2) mn = '0' + mn;
		return dd + "/" + mm + "/20" + yy + ' ' + hh + ':' + mn + ":00";
	}

	public static String getTime(long timestamp) {
		Date date = new Date(timestamp);
		calendar.setTime(date);
		String DD = calendar.get(Calendar.HOUR_OF_DAY) + "";
		if (DD.length() < 2)
			DD = '0' + DD;
		String MM = calendar.get(Calendar.MINUTE) + "";
		if (MM.length() < 2)
			MM = '0' + MM;
		return DD + ":" + MM;
	}

	public static boolean checksum(String idNumber, boolean chip) {
		long tz = 0;
		try {
			tz = Long.parseLong(idNumber);
		}
		catch (Exception e) {}
		if (tz == 0)
			return false;
		if (chip)
			return true;
		//א. רושמים את מספר תעודת הזהות ב 9 ספרות, כאשר אם מספר תעודת הזהות הינו פחות מ 9 ספרות, משלימים אפסים מובילים בצד שמאל של המספר
		while (idNumber.length() < 9)
			idNumber = '0' + idNumber;
//ב. מתחת לכל סיפרה של מספר תעודת הזהות, רושמים החל מצד ימין של המספר את הספרות 1, אחר כך 2, אחר כך שוב 1, אחר כך שוב 2 וחוזר חלילה עד לסיום. מספרים אלו נקראים משקלים.
		int multipleDigits[] = {1, 2, 1, 2, 1, 2, 1, 2, 1};
//ג. מכפילים בכפל מקוצר, ללא נשא כל סיפרה במספר תעודת הזהות עם הסיפרה מתחתיה (המשקל). את התוצאה, גם אם היא היא בשני ספרות, רושמים מתחת בשורה שלישית.
//        int sumDigits [] = new int[9];
//ד. את התוצאות הופכים לתוצאות של סיפרה אחת. למשל, אם תוצאה כלשהי היא מספר דו סיפרתי, מחברים את כל ספרות המספר לספרה אחת. למשל, את התוצאה 14 מחברים כ 4 ועוד 1, ומקבלים 5. את התוצאה 10 מחברים כ 1 ועוד 0 ומקבלים 1. את כל התוצאות רושמים בשורה רביעית.
		int sum = 0;
		for (int i = 0; i < 9; i++)
			sum += digitSum((idNumber.charAt(i) - '0') * multipleDigits[i]);
//ה. כעת, מחברים את כל הספרות בחיבור חשבוני פשוט עד לקבלת תוצאה. התוצאה חייבת להיות מספר המתחלק ב 10 ללא שארית, כלומר שסיפרת האחדות שלו היא 0.
		if (sum % 10 == 0)
			return true;
		else
			return false;
	}

	public static int digitSum(int num) {
		while (num >= 10)
			num = num / 10 + num % 10;
		return num;
	}

	public static int[] getDimensions(int width, int height, int maxWidth, int maxHeight) {
		int[] dim = new int[2];
		while (width / 2 > maxWidth && height / 2 > maxHeight) {
			width /= 2;
			height /= 2;
		}
		dim[0] = width;
		dim[1] = height;
		return dim;
	}

	private static Bitmap getBitmap(Bitmap bitmap, String cameraWorkingPath) {
		if (bitmap.getWidth() > bitmap.getHeight()) {
			if (bitmap.getWidth() / 2 > 896 && bitmap.getHeight() / 2 > 672) {
				int[] dim = Util.getDimensions(bitmap.getWidth(), bitmap.getHeight(), 896, 672);
				bitmap = Util.shrinkBitmap(cameraWorkingPath, dim[0], dim[1], true);
			}
		} else {
			if (bitmap.getWidth() / 2 > 672 && bitmap.getHeight() / 2 > 896) {
				int[] dim = Util.getDimensions(bitmap.getWidth(), bitmap.getHeight(), 672, 896);
				bitmap = Util.shrinkBitmap(cameraWorkingPath, dim[0], dim[1], true);
			}
		}
		return bitmap;
	}

	private static Bitmap getBitmap(Context context , Bitmap bitmap, Uri image) throws Exception {
		if (bitmap.getWidth() > bitmap.getHeight()) {
			if (bitmap.getWidth() / 2 > 896 && bitmap.getHeight() / 2 > 672) {
				int[] dim = Util.getDimensions(bitmap.getWidth(), bitmap.getHeight(), 896, 672);
				bitmap = Util.shrinkBitmap(image,context , dim[0], dim[1], true);
			}
		} else {
			if (bitmap.getWidth() / 2 > 672 && bitmap.getHeight() / 2 > 896) {
				int[] dim = Util.getDimensions(bitmap.getWidth(), bitmap.getHeight(), 672, 896);
				bitmap = Util.shrinkBitmap(image, context,dim[0], dim[1], true);
			}
		}
		return bitmap;
	}

	public static Bitmap getBitmap(Activity activity, Uri image, int reduction, String cameraWorkingPath) throws Exception {
		Bitmap bitmap = null;
		System.gc();
		InputStream in = activity.getContentResolver().openInputStream(image);
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inSampleSize = reduction; //try to decrease decoded image
		options.inPurgeable = true; //purge-able to disk
		bitmap = BitmapFactory.decodeStream(in, null, options);
		in.close();
		bitmap = getBitmap(activity, bitmap,image);
		System.gc();
		return bitmap;
	}

	public static Bitmap getBitmap(String cameraWorkingPath) throws Exception {
		Bitmap bitmap = null;
		System.gc();
		File temporaryFile = TicketInformation.openFile(cameraWorkingPath);
		InputStream inputStreamTemporary = new BufferedInputStream(new FileInputStream(temporaryFile));
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inSampleSize = 1; //try to decrease decoded image
		options.inPurgeable = true; //purge-able to disk
		bitmap = BitmapFactory.decodeStream(inputStreamTemporary, null, options);
		inputStreamTemporary.close();
		bitmap = getBitmap(bitmap,cameraWorkingPath);
		temporaryFile.delete();
		System.gc();
		return bitmap;
	}

	public static Bitmap getBitmap(int reduction, String cameraWorkingPath) throws Exception {
		Bitmap bitmap = null;
		System.gc();
		File temporaryFile = openFile(cameraWorkingPath);
		InputStream inputStreamTemporary = new BufferedInputStream(new FileInputStream(temporaryFile));
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inSampleSize = reduction; //try to decrease decoded image
		options.inPurgeable = true; //purge-able to disk
//      options.inPreferredConfig = Config.ARGB_8888;
		bitmap = BitmapFactory.decodeStream(inputStreamTemporary, null, options);
		inputStreamTemporary.close();
		bitmap = getBitmap(bitmap,cameraWorkingPath);
		temporaryFile.delete();
		System.gc();
		return bitmap;
	}

	public static Bitmap shrinkBitmap(String file, int width, int height, boolean doRotate) {
		BitmapFactory.Options bmpFactoryOptions = new BitmapFactory.Options();
		bmpFactoryOptions.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(file, bmpFactoryOptions);
		int heightRatio = (int) Math.ceil(bmpFactoryOptions.outHeight/(float)height);
		int widthRatio = (int) Math.ceil(bmpFactoryOptions.outWidth/(float)width);
		if (heightRatio > 1 || widthRatio > 1) {
			if (heightRatio > widthRatio) {
				bmpFactoryOptions.inSampleSize = Math.max(heightRatio,1);
			}
			else {
				bmpFactoryOptions.inSampleSize = Math.max(widthRatio,1);
			}
		}
		bmpFactoryOptions.inJustDecodeBounds = false;
		Bitmap cameraBitmap = BitmapFactory.decodeFile(file, bmpFactoryOptions);
		if(doRotate) {
			try {
				ExifInterface exif = new ExifInterface(file);
				float rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
				float rotationInDegrees = exifToDegrees(rotation);
				if (rotationInDegrees > 0) {
					Matrix matrix = new Matrix();
					matrix.postRotate(rotationInDegrees);
					Bitmap scaledBitmap = Bitmap.createBitmap(cameraBitmap);
					return Bitmap.createBitmap(cameraBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
				}
			} catch (Exception e) {
			} catch (Error e) {
			}
		}
		return cameraBitmap;
	}

	public static Bitmap shrinkBitmap(Uri image, Context context , int width, int height, boolean doRotate) throws IOException {
		BitmapFactory.Options bmpFactoryOptions = new BitmapFactory.Options();
		bmpFactoryOptions.inJustDecodeBounds = true;
		InputStream in = context.getContentResolver().openInputStream(image);
		BitmapFactory.decodeStream(in, null, bmpFactoryOptions);
		in.close();
		int heightRatio = (int) Math.ceil(bmpFactoryOptions.outHeight/(float)height);
		int widthRatio = (int) Math.ceil(bmpFactoryOptions.outWidth/(float)width);
		if (heightRatio > 1 || widthRatio > 1) {
			if (heightRatio > widthRatio) {
				bmpFactoryOptions.inSampleSize = Math.max(heightRatio,1);
			}
			else {
				bmpFactoryOptions.inSampleSize = Math.max(widthRatio,1);
			}
		}
		bmpFactoryOptions.inJustDecodeBounds = false;
		in = context.getContentResolver().openInputStream(image);
		Bitmap cameraBitmap = decodeStream(in, null, bmpFactoryOptions);
		if(doRotate) {
			try {
				ExifInterface exif = new ExifInterface(image.getPath());
				float rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
				float rotationInDegrees = exifToDegrees(rotation);
				if (rotationInDegrees > 0) {
					Matrix matrix = new Matrix();
					matrix.postRotate(rotationInDegrees);
					Bitmap scaledBitmap = Bitmap.createBitmap(cameraBitmap);
					return Bitmap.createBitmap(cameraBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
				}
			} catch (Exception e) {
			} catch (Error e) {
			}
		}
		return cameraBitmap;
	}

	public static String displayField(String s) {
		String o = "";
		for(short i = 0;i < s.length();i++) {
			if(Character.isDigit(s.charAt(i)) || s.charAt(i) == '.') {
				short j = (short)(i+1);
				while(j < s.length() && (Character.isDigit(s.charAt(j)) || s.charAt(j) == '.')) j++;
				for(short k = (short)(j-1);k >= i;k--) o += s.charAt(k);
				i = (short)(j-1);
			}
			else {
				o += s.charAt(i);
			}
		}
		return o;
	}

	public static String reverseNumberOnly(String s) {
		String o = "";
		short checkFromPoint = -1;
		for(short i = 0;i < s.length();i++) {
			if(Character.isDigit(s.charAt(i)) && i > checkFromPoint) {
				short j = (short)(i+1);
				boolean hasDate = false;
				while(j < s.length() && (Character.isDigit(s.charAt(j)) || s.charAt(j) == '/')) {
					j++;
					if(s.charAt(j) == '/')
						hasDate = true;
				}
				if(hasDate)
					checkFromPoint = j;
			}
			if((Character.isDigit(s.charAt(i)) || s.charAt(i) == '.') && (i > checkFromPoint)) {
				short j = (short)(i+1);
				while(j < s.length() && (Character.isDigit(s.charAt(j)) || s.charAt(j) == '.')) j++;
				for(short k = (short)(j-1);k >= i;k--) o += s.charAt(k);
				i = (short)(j-1);
			}
			else {
				o += s.charAt(i);
			}
		}
		return o;
	}

	public static boolean isInDebugMode(Activity activity) {
		boolean debuggable = false;
        PackageManager pm = activity.getPackageManager();
        try {
           ApplicationInfo appinfo = pm.getApplicationInfo(activity.getPackageName(), 0);
           debuggable = (0 != (appinfo.flags &= ApplicationInfo.FLAG_DEBUGGABLE));
        }
        catch(NameNotFoundException e) {
           /*debuggable variable will remain false*/
        }
       return debuggable;
	}
	
	public static Runnable getTouchDelegateAction(final View parent, final View delegate, final int topPadding, final int bottomPadding, final int leftPadding, final int rightPadding) {
        return new Runnable() {
            @Override
            public void run() {
                //Construct a new Rectangle and let the Delegate set its values
                Rect touchRect = new Rect();
                delegate.getHitRect(touchRect);
                
                //Modify the dimensions of the Rectangle
                //Padding values below zero are replaced by zeros
                touchRect.top-= Math.max(0, topPadding);
                touchRect.bottom+= Math.max(0, bottomPadding);
                touchRect.left-= Math.max(0, leftPadding);
                touchRect.right+= Math.max(0, rightPadding);
                
                //Now we are going to construct the TouchDelegate
                TouchDelegate touchDelegate = new TouchDelegate(touchRect, delegate);
                
                //And set it on the parent
                parent.setTouchDelegate(touchDelegate);
                
            }
        };
    }
/*
	public static Bitmap showImageWithTheProperOrientation(String file) {
		if(file == null || file.length() == 0)
			return null;
		try {
			System.gc();
			BitmapFactory.Options bounds = new BitmapFactory.Options();
			bounds.inJustDecodeBounds = true;
			bounds.inSampleSize = 2;
			bounds.inPurgeable = true;
			BitmapFactory.decodeFile(file, bounds);

			BitmapFactory.Options opts = new BitmapFactory.Options();
			Bitmap bm = BitmapFactory.decodeFile(file, opts);
			ExifInterface exif = new ExifInterface(file);
			String orientString = exif.getAttribute(ExifInterface.TAG_ORIENTATION);
			int orientation = orientString != null ? Integer.parseInt(orientString) :  ExifInterface.ORIENTATION_NORMAL;

			int rotationAngle = 0;
			if (orientation == ExifInterface.ORIENTATION_ROTATE_90) rotationAngle = 90;
			if (orientation == ExifInterface.ORIENTATION_ROTATE_180) rotationAngle = 180;
			if (orientation == ExifInterface.ORIENTATION_ROTATE_270) rotationAngle = 270;

			Matrix matrix = new Matrix();
			matrix.postRotate(rotationAngle);
			return Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
		} 
		catch (Exception e) {return null;}
	    catch (OutOfMemoryError e) {return null;}
	}

	public static void rotatePhoto(String cameraWorkingPath) {
		try {
			BitmapFactory.Options bmOptions = new BitmapFactory.Options();
			bmOptions.inJustDecodeBounds = false;
			bmOptions.inPurgeable = true;
			Bitmap cameraBitmap = BitmapFactory.decodeFile(cameraWorkingPath);//get file path from intent when you take iamge.
//          ByteArrayOutputStream bos = new ByteArrayOutputStream();
//          cameraBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
			ExifInterface exif = new ExifInterface(cameraWorkingPath);
			float rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
			float rotationInDegrees = exifToDegrees(rotation);
			if (rotationInDegrees > 0) {
				Matrix matrix = new Matrix();
				matrix.postRotate(rotationInDegrees);
				Bitmap scaledBitmap = Bitmap.createBitmap(cameraBitmap);
				Bitmap rotatedBitmap = Bitmap.createBitmap(cameraBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
				FileOutputStream fos = new FileOutputStream(cameraWorkingPath);
				rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
				fos.flush();
				fos.close();
			}
		}
		catch (Exception e) {}
		catch (Error e) {}
	}
*/
	private static float exifToDegrees(float exifOrientation) {
		if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) { return 90; }
		else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {  return 180; }
		else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {  return 270; }
		return 0;
	}

	public static String getCarInformationTypeByCode(int code) {
		for(int i = 0; i < DB.vehicleTypes.size(); i++)
			if(DB.vehicleTypes.get(i).code == code)
				return DB.vehicleTypes.get(i).name;
		return "";
	}

	public static String getCarInformationColorByCode(int code) {
		for(int i = 0;i < DB.vehicleColors.size();i++)
			if(DB.vehicleColors.get(i).code == code)
				return DB.vehicleColors.get(i).name;
		return "";
	}

	public static String getCarInformationManufacturerByCode(int code) {
		for(int i = 0;i < DB.vehicleManufacturers.size();i++)
			if(DB.vehicleManufacturers.get(i).code == code)
				return DB.vehicleManufacturers.get(i).name;
		return "";
	}

	public static byte getIdType(String ids) {
		long id = 0;
		try {id = Long.parseLong(ids);}catch (Exception e) {}
		if(id < 500000000L || id/100000000L == 9)
			return 1;
		else
			return 0;
	}

}

