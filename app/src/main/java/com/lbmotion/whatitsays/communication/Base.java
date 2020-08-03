package com.lbmotion.whatitsays.communication;

import android.content.Context;
import android.net.wifi.WifiManager;

import com.lbmotion.whatitsays.data.DB;
import com.lbmotion.whatitsays.data.Defendant;
import com.lbmotion.whatitsays.data.ExistingWarning;
import com.lbmotion.whatitsays.data.Witness;
import com.lbmotion.whatitsays.data.Violation;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicBoolean;

//import org.apache.http.util.ByteArrayBuffer;

abstract public class Base extends Thread {
	private static String TAG = "Base";
	public static boolean 			communicationViaMiddleware = true;
	protected static Vector<Base> communicationThreads = new Vector<Base>();
	public static String version = "1.1";
	public static String webVersion = null;
	protected AtomicBoolean isDone = new AtomicBoolean(false);
	protected Timer helperTimer = null;
	protected Socket socketConnection = null;
	protected InputStream inputStream = null;
	protected OutputStream outputStream = null;
//
	protected boolean 				openFailed = true;
//		
	public static final short 		portWorking1 = 4800;
	public static final short 		portBackground1 = 4801;
	public static final short 		portWorking2 = 4790;
	public static final short 		portBackground2 = 4792;

	public static short		 		lastPortWorking = 4800;
	public static short 			lastPortBackground = 4801;

	private static String IPAddressData = "212.179.107.21";
//  private static String 			IPAddressData = "212.179.107.29";
//	private static String 			IPAddressData = "62.90.108.241";
//	private static String 			IPAddressData = "127.0.0.1";

	public boolean 					errorOccurred = false;
	public boolean 					didAbort = false;
	public boolean 					result = false;

	protected char [] 				bufferChar;
	protected int 					i = 0;
	
	public boolean 					errorFromServer = false;
	
	private static char[] codeTable = {
        'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','V','U','W','X','Y','Z',
        'a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','v','u','w','x','y','z',
        '0','1','2','3','4','5','6','7','8','9',
        'א','ב','ג','ד','ה','ו','ז','ח','ט','י','כ','ך','ל','מ','ם','נ','ן','ס','ע','פ','ף','צ','ץ','ק','ר','ש','ת',
        '!','@','#','$','%','^','&','*','(',')','-','_','+','=','/','\'',']','}','[','{','\\','|',';',':','"',
        '\'',',',',','<','.','>','?','/','~',';','`','.',' ',
        (char)(8211),(char)(8362),(char)(1468),(char)(1464),(char)(1462),(char)(1463),(char)(1472),
        (char)(1456),(char)(8230),(char)(1460),(char)(1465),(char)(1461),(char)(1470),(char)(1471),
        (char)(8212),(char)(8216),(char)(1473),(char)(8218),(char)(8217),(char)(8221),(char)(8220),
        (char)(8219),(char)(8222),(char)(8206)};
	
	private static char[] codeTableSorted = null;
	
	public static Context mContext;
	
	public Base() {
		helperTimer = new Timer();
	}

	 protected void readServer() {
//		Log.i(TAG, "readServer()");
		try {
			int c;
			int l = 0;
			byte [] header = new byte[6];
			int readLength = 0;
			while(l != -1 && readLength != header.length) {
				l = inputStream.read(header,readLength,header.length-readLength);
				if(l != -1)
					readLength += l;
			}
			l = 0;
			for(int i = 0;i < header.length;i++) {
				if(header[i] <= codeTable.length)
					l = l*10+(codeTable[header[i]-1]-'0');
			}
			if(l > 0) {
				byte [] buf = new byte[l];
				readLength = 0;
				while(l != -1 && readLength != buf.length) {
					l = inputStream.read(buf,readLength,buf.length-readLength);
					if(l != -1)
						readLength += l;
				}
				if(readLength == buf.length) {
					bufferChar = new char[readLength];
					for(int i = 0;i < buf.length;i++) {
						int n = buf[i];
						if(n < 0) n = 127+(-n);
						if(n <= codeTable.length)
							bufferChar[i] = codeTable[n-1];
						else
							bufferChar[i] = '?';
					}
				}
			}
			else {
				bufferChar = new char[0];
			}
		}
		catch(Exception e) {
			errorOccurred = true;
		}
	}

	protected void sender(String message) throws IOException {
//		Log.i(TAG, "sender()");
		outputStream.write((message).getBytes());
		outputStream.write("\n".getBytes());
		outputStream.flush();
		try {
            Thread.sleep(100);}catch (InterruptedException ie) {}
	}
	
	protected void open() throws IOException {
		open(8000);
	}
	
	protected void openBackground() throws IOException {
		openBackground(8000);
	}

	protected void openBackgroundVerify(short port) throws IOException {
//		Log.i(TAG, "open()");
		disableWifi();
		socketConnection = new Socket();
		socketConnection.connect(new InetSocketAddress(remoteIPAddressData(),port), 8000);
		inputStream = socketConnection.getInputStream();
		outputStream = socketConnection.getOutputStream();
		openFailed = false;
	}

	protected void open(int timeout) throws IOException {
//		Log.i(TAG, "open()");
		disableWifi();
		socketConnection = new Socket();
		short portWorking = (lastPortWorking == portWorking1)?portWorking2:portWorking1;
		lastPortWorking = portWorking;
		socketConnection.connect(new InetSocketAddress(remoteIPAddressData(),portWorking), timeout);
		inputStream = socketConnection.getInputStream();
		outputStream = socketConnection.getOutputStream();
		openFailed = false;
	}
	
	protected void openBackground(int timeout) throws IOException {
//		Log.i(TAG, "open()");
		disableWifi();
		socketConnection = new Socket();
		short portBackground = (lastPortBackground == portBackground1)?portBackground2:portBackground1;
		lastPortBackground = portBackground;
		socketConnection.connect(new InetSocketAddress(remoteIPAddressData(),portBackground), timeout);
		inputStream = socketConnection.getInputStream();
		outputStream = socketConnection.getOutputStream();
		openFailed = false;
	}

	protected void close(boolean sendBy) {
//		Log.i(TAG, "close()");
		if(sendBy) {
			try{sender("Bye");}catch (Exception e) {}
		}
		try{if(socketConnection != null)socketConnection.close();}catch (Exception e) {}
		try{if(inputStream != null)inputStream.close();}catch (Exception e) {}
		try{
			if(outputStream != null) {
				outputStream.flush();
				outputStream.close();
			}
		}catch (Exception e) {}
	}

	public int readBytes(int len) {
		if(len > 0) {
			int ii = i;
			i += len;
			try {return Integer.parseInt(new String(bufferChar,ii,len));}
			catch(Exception e) {}
		}
		return 0;
	}

	public String getString(int len) {
		int ii = i;
		i += len;
		return new String(bufferChar,ii,len);
	}

	public char getCharacter() {
		int ii = i;
		i++;
		return bufferChar[ii];
	}
	
	public static void abortAll() {
		for(short i = 0;i < communicationThreads.size();i++) {
			if(!((Base)communicationThreads.elementAt(i)).isDone.get())
				((Base)communicationThreads.elementAt(i)).errorOccurred = true;
			((Base)communicationThreads.elementAt(i)).didAbort = true;
			((Base)communicationThreads.elementAt(i)).isDone.set(true);
			((Base)communicationThreads.elementAt(i)).interrupt();
		}
	}

	public class CancelCommunicationTask extends TimerTask {
		private Thread networkingThread;
		public CancelCommunicationTask(Thread t) {
			networkingThread = t;
		}
		public void run() {
			if(!isDone.get())
				errorOccurred = true;
			isDone.set(true);
			networkingThread.interrupt(); 
		}
	}
	
	public static String remoteIPAddressData() {
		return IPAddressData;
	}
	
	public static String codedRequest(String request) {
		String ret = "";
		for(int i = 0;i < request.length();i++) {
			if(request.charAt(i) >= 'א' && request.charAt(i) <= 'ת') {
				ret += '\t';
				ret += (char)((request.charAt(i)-'א')+'a');
			}	
			else {
				if(request.charAt(i) > 255) {
					if(request.charAt(i) == '׳')
						ret += '\'';
					else
						ret += '?';
				}
				else {
					ret += request.charAt(i);
				}
			}
		}
		return ret;
	}
	
	protected void doAbort() {
		try {
			if(!isDone.get())
				errorOccurred = true;
			didAbort = true;
			interrupt();
			isDone.set(true);
		}
		catch (Exception e) {}
	}

	protected void addExistingWarning(Vector<ExistingWarning> v1, Vector<ExistingWarning> v2) {
		ExistingWarning existingWarning = new ExistingWarning();
		existingWarning.Lk = getString(readBytes(2));
		existingWarning.UsrKod = getString(readBytes(2));
		existingWarning.UsrNm = getString(readBytes(2));
		existingWarning.UsrC = getString(readBytes(2));
		existingWarning.DochC = getString(readBytes(2));
		existingWarning.DochKod = getString(readBytes(2));
		existingWarning.Sidra = getString(readBytes(2));
		existingWarning.Bikoret = getString(readBytes(2));
		existingWarning.vehicle.licence = getString(readBytes(2));
		if(existingWarning.vehicle.licence.equals("0"))
			existingWarning.vehicle.licence = "";
		existingWarning.AveraKod = getString(readBytes(2));
		existingWarning.AveraNm = getString(readBytes(3));
		existingWarning.AveraC = getString(readBytes(2));
		existingWarning.Mhr = getString(readBytes(2));
		existingWarning.Days = getString (readBytes(2));
		existingWarning.StreetKod = getString (readBytes(2));
		existingWarning.StreetC = getString (readBytes(2));
		existingWarning.StreetNm = getString (readBytes(2));
		existingWarning.MikumKod = getString (readBytes(2));
		existingWarning.MikumC = getString (readBytes(2));
//		
		existingWarning.defendant = new Defendant();
		existingWarning.defendant.id = getString (readBytes(2));
		if(existingWarning.defendant.id.equals("0"))
			existingWarning.defendant.id = "";
		existingWarning.defendant.name = getString (readBytes(2));
		existingWarning.defendant.last = getString (readBytes(2));
		existingWarning.defendant.zipcode = getString (readBytes(2));
		existingWarning.defendant.cityCode = getString (readBytes(2));
		existingWarning.defendant.number = getString (readBytes(2));
		existingWarning.defendant.street = getString (readBytes(2));
		existingWarning.defendant.flat = getString (readBytes(2));
		existingWarning.defendant.box = getString (readBytes(2));
//
		existingWarning.witness = new Witness();
		existingWarning.witness.id = getString (readBytes(2));
		existingWarning.witness.name = getString (readBytes(2));
		existingWarning.witness.last = getString (readBytes(2));
		existingWarning.witness.street = getString (readBytes(2));
		existingWarning.witness.number = getString (readBytes(2));
		existingWarning.witness.zipcode = getString (readBytes(2));
		existingWarning.witness.telephone = getString (readBytes(2));
		existingWarning.witness.cityCode = getString (readBytes(2));
//		
		existingWarning.numAzhara = getString (readBytes(2));
		existingWarning.StreetNoAvera = getString (readBytes(2));
		existingWarning.CityNm = getString (readBytes(2));
		existingWarning.witness.city = getString (readBytes(2));
		existingWarning.validUpto = getString (readBytes(2));
		existingWarning.animalId = getString (readBytes(2));
		existingWarning.remark = getString (readBytes(3));
		existingWarning.validUptoTime = getString (readBytes(2));
//
		existingWarning.defendant.cityName = existingWarning.CityNm;
		int type = readBytes(1);
		existingWarning.FreeRemark = getString (readBytes(3));
		existingWarning.CitizenFreeText = getString (readBytes(3));
		existingWarning.Pakach_Remark1 = getString (readBytes(2));
		existingWarning.Pakach_Remark2 = getString (readBytes(2));
		existingWarning.Pakach_Remark3 = getString (readBytes(2));
		existingWarning.Pakach_Remark4 = getString (readBytes(2));
		existingWarning.Citizen_Remark1 = getString (readBytes(2));
		existingWarning.Citizen_Remark2 = getString (readBytes(2));
		existingWarning.Citizen_Remark3 = getString (readBytes(2));
		existingWarning.Citizen_Remark4 = getString (readBytes(2));
		int pictures = readBytes(1);
		for(int i = 0;i < pictures;i++) {
			String pic = getString (readBytes(3)).trim();
			if(pic.length() > 0)
				existingWarning.pictures.add(pic);
		}
		try{existingWarning.vehicle.type = Integer.parseInt(getString (readBytes(2)));}catch (Exception e) {}
		try{existingWarning.vehicle.manufacturer = Integer.parseInt(getString (readBytes(2)));}catch (Exception e) {}
		try{existingWarning.vehicle.color = Integer.parseInt(getString (readBytes(2)));}catch (Exception e) {}
		existingWarning.witness.flat = getString (readBytes(2));
		if(type == 0)
			v1.addElement(existingWarning);
		else
			v2.addElement(existingWarning);
	}
	
	private synchronized boolean disableWifi() {
	 	if(1 == 1) return true;
		try {
			WifiManager wm = (WifiManager)mContext.getSystemService(Context.WIFI_SERVICE);
			if(wm == null)
				return false;
			if(wm.isWifiEnabled()) {
				wm.setWifiEnabled(false);
				long start = System.currentTimeMillis();
				long delta;
				do {
					if(!wm.isWifiEnabled()) {
						try {
                            Thread.sleep(500);}catch (InterruptedException ie) {}
						return true;
					}
					try {
                        Thread.sleep(125);}catch (InterruptedException ie) {}
					delta = System.currentTimeMillis()-start;
				} while(delta < 2000);
				return false;
			}
		} catch (SecurityException e) {e.printStackTrace();}
		return false;
	}
/*
	public static void getVersion() {
		getWebVersion = true;
		BufferedInputStream bis = null;
		ByteArrayBuffer baf = null;
		InputStream is = null;
		byte [] buffer;
		try {
			URL url = new URL("http://www.trapnearu.com/Hanita/version.txt");
			URLConnection ucon = url.openConnection();
			ucon.setUseCaches(false);
			is = ucon.getInputStream();
			bis = new BufferedInputStream(is);
			baf = new ByteArrayBuffer(1024*6);
			buffer = new byte[1024*6];
			int current = 0;
			while((current = bis.read(buffer)) != -1)
				baf.append(buffer, 0, current);
			webVersion = (new String(buffer)).trim();
		}
		catch (Exception e) {}
		try{bis.close();}catch (Exception e) {}
		try{is.close();}catch (Exception e) {}
		if(baf != null) baf.clear();
		getWebVersion = false;
	}
*/
	public String removeIllegalCharacters(String field) {
		if(codeTableSorted == null) {
			codeTableSorted = new char[codeTable.length];
			System.arraycopy(codeTable, 0, codeTableSorted, 0, codeTable.length);
			Arrays.sort(codeTableSorted);
		}
		String out = "";
		for(int i = 0;i < field.length();i++) {
			if(Arrays.binarySearch(codeTableSorted, field.charAt(i)) != -1)
				out += field.charAt(i);
		}
		return out;
	}

	protected boolean getViolations(Vector<Violation> v, boolean all) {
		v.removeAllElements();
		int n;
		if(all) {
			n = bufferChar.length;
		}
		else {
			n = readBytes(6) + i;
		}
		if(bufferChar[i] == '<' && bufferChar[i+1] == 'E' && bufferChar[i+2] == 'r' &&	bufferChar[i+3] == 'r' && bufferChar[i+4] == '>') {
			i += 5;
			return false;
		}
		while (i < n) {
			Violation violation = new Violation();
			violation.C = (short)readBytes(readBytes(1));
			violation.K = (short)readBytes(readBytes(1));
			violation.VT = (byte)readBytes(readBytes(1));
			violation.Nm = getString(readBytes(4));
			violation.MP = (short)readBytes(readBytes(1));
			violation.MR = (short)readBytes(readBytes(1));
			violation.P = (short)readBytes(readBytes(1));
			violation.D = (short)readBytes(readBytes(1));
			if (readBytes(readBytes(1))!= 0)
				violation.SwHatraa = true;
			else
				violation.SwHatraa = false;
			violation.SwTz = (byte)'0';getCharacter();
			violation.SwRishui = (byte)'0';getCharacter();
			violation.SwPrivateCompany = (byte)'0';getCharacter();
			violation.NumCopies = (byte)readBytes(readBytes(1));
			if (violation.NumCopies == 0)
				violation.NumCopies = 1;
			violation.SwMustTavHanaya = (short)readBytes(readBytes(1));
			violation.SwHanayaHok = (short)readBytes(readBytes(1));
			violation.title = getString(readBytes(2));
			violation.VTC = (byte)readBytes(readBytes(1));
			violation.VT_Nm = getString(readBytes(4));
			violation.Sivug = (byte)readBytes(1);
			violation.OffenceIcon = (byte)readBytes(2);
			violation.hasVirtualDoc = readBytes(1) == 0;
			violation.QRSwWork = readBytes(1) == 1;
			violation.SwWorkPrinter = readBytes(1) == 1;
			violation.TypeForm = getString(readBytes(1));
			violation.toAvera = (short)readBytes(readBytes(1));
			violation.toAtraa = (short)readBytes(readBytes(1));
			if(violation.toAtraa == 0 && violation.toAvera == 0)
				violation.toAtraa = -1;
			violation.swPrintQR = (byte)(readBytes(readBytes(1)));
			for(int s = (byte)(readBytes(readBytes(1)));s > 0;s--) {
				violation.screensOrder.add(Integer.valueOf((byte)(readBytes(readBytes(1)))));
			}
			violation.codeMotavShort = getString(readBytes(2));
			violation.TeurShort = getString(readBytes(2));
			boolean found = false;
			for(Violation vv : DB.allViolations) {
				if(violation.C == vv.C && violation.K == vv.K) {
					found = true;
					break;
				}
			}
			if(!found)
				v.addElement(violation);
		}
		return true;
	}
}