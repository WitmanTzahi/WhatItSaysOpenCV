package com.lbmotion.whatitsays.communication;

import android.util.Log;

import com.lbmotion.whatitsays.data.DB;
import com.lbmotion.whatitsays.data.ExistingWarning;
import com.lbmotion.whatitsays.data.InsideCode;
import com.lbmotion.whatitsays.data.List2;
import com.lbmotion.whatitsays.data.List2s;
import com.lbmotion.whatitsays.data.List3;
import com.lbmotion.whatitsays.data.NotebookType;
import com.lbmotion.whatitsays.data.Parameters;
import com.lbmotion.whatitsays.data.RecordStore;
import com.lbmotion.whatitsays.data.Street;
import com.lbmotion.whatitsays.data.TrialDate;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.Date;
import java.util.Vector;

public class GetAuthorityData extends Base {
	private final String TAG = "GetAuthorityData";
	public  String username;
	public  String authority;
	public int records = 0;
	public static GetAuthorityData me;

	public GetAuthorityData() {
	}

	public void run() {
		Log.i(TAG, "run()");
		try {
			String timestamp = Parameters.get("timestamp2");
			if(timestamp == null)
				timestamp = "1";
			String lk = Parameters.get("lk");
			if(lk == null)
				lk = "";
			if(!authority.equals(lk))
				timestamp = "1";
			open();
			sender("AU1"+"version"+"\f"+version+"\f"+"username"+"\f"+username+"\f"+"authority"+"\f"+authority+"\f"+"timestamp"+"\f"+timestamp+"\f");
			helperTimer.schedule(new CancelCommunicationTask(this),180000);
			readServer();
			helperTimer.cancel();
			close(true);
			if(bufferChar != null) {
				if(new String(bufferChar,6,"ReadLocal".length()).equals("ReadLocal")) {
					records++;
					RecordStore recordStore = RecordStore.openRecordStore("Vio", true);
					if (recordStore.getNumRecords() > 0) {
						ByteArrayInputStream bais = new ByteArrayInputStream(recordStore.getRecord(1));
						DataInputStream is = new DataInputStream(bais);
						DB.readVectorViolation(is);
						try {bais.close();} catch (Exception e) {}
						try {is.close();} catch (Exception e) {}
					} else {
						throw new Exception("Vio");
					}
					i = readBytes(6)+i;
					recordStore.closeRecordStore();
				}
				else {
					if(getViolations(DB.allViolations,false)) {
						records++;
					}
					try {RecordStore.deleteRecordStore("Vio");}catch(Exception e) {}
					RecordStore recordStore = RecordStore.openRecordStore("Vio",true);
					recordStore.setNumberofRecords(1);
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					DataOutputStream os = new DataOutputStream(baos);
					DB.writeVectorViolation(os);
					byte[] b = baos.toByteArray();
					recordStore.addRecord(b,0,b.length);
					baos.flush();
					os.flush();
					baos.close();
					os.close();
					recordStore.closeRecordStore();
				}
				Parameters.update("timestamp2",((new Date()).getTime()-5*60*60*1000L)+"");
				Parameters.update("lk",authority);
				getList3(DB.vehicleTypes);
				getList2(DB.vehicleManufacturers);
				getList2(DB.vehicleColors);
				getList3(DB.areas);
				getList2(DB.cancelRemarks);
				getList3(DB.remarks);
				getList2s(DB.remarksToViolations);
				getList3(DB.citizenRemarks);
				loadStreet();
				getAuthorityCharacteristic();
				getInsideCodes(DB.insideCodes);
				if(DB.insideCodes.size() == 0) {
					InsideCode insideCode = new InsideCode();
					insideCode.Nm = "Need Value";
					DB.insideCodes.addElement(insideCode);
				}
				getTrialDates(DB.trialDates);
				getExistingWarnings(DB.existingWarnings,DB.pastToHandleEvents);
				getNotebookTypes(DB.notebookTypes);
				getNotebookTypes(DB.nimhans);
				getButtonsMap();
			}
		}
		catch (Exception e) {
			errorOccurred = true;
			Log.i(TAG, "run()"+e.getMessage());
			close(false);
		}
		isDone.set(true);
	}

	public static boolean doGetAuthorityData(String authority,String username) {
		try {
			me = new GetAuthorityData();
			me.username = username;
			me.authority = authority;
			me.start();
			communicationThreads.addElement(me);
			while(!me.isDone.get()) {
				try {Thread.sleep(100);}catch (InterruptedException ie) {}
			}
			try {communicationThreads.removeElement(me);}catch(Exception e) {}
			return me.records == 16 && !me.errorOccurred;
		}
		catch(Exception e) {}
		catch(Error e) {}
		return false;
	}

	private void getList3(Vector<List3> v) {
		v.removeAllElements();
		int n = readBytes(6)+i;
		if (i <= n)
			records++;
		if(bufferChar[i] == '<' && bufferChar[i+1] == 'E' && bufferChar[i+2] == 'r' &&	bufferChar[i+3] == 'r' && bufferChar[i+4] == '>') {
			i += 5;
			return;
		}
		while (i < n) {
			List3 list3 = new List3();
			list3.code = (short)readBytes(readBytes(1));
			list3.Kod = (short)readBytes(readBytes(1));
			list3.name = getString(readBytes(4));
			v.addElement(list3);
		}
	}

	private void getList2(Vector<List2> v) {
		v.removeAllElements();
		int n = readBytes(6)+i;
		if (i <= n)
			records++;
		if(bufferChar[i] == '<' && bufferChar[i+1] == 'E' && bufferChar[i+2] == 'r' &&	bufferChar[i+3] == 'r' && bufferChar[i+4] == '>') {
			i += 5;
			return;
		}
		while (i < n) {
			List2 list2 = new List2();
			list2.code = (short)readBytes(readBytes(1));
			list2.name = getString(readBytes(4));
			v.addElement(list2);
		}
	}

	private void getList2s(Vector<List2s> v) {
		v.removeAllElements();
		int n = readBytes(6)+i;
		if (i <= n)
			records++;
		if(bufferChar[i] == '<' && bufferChar[i+1] == 'E' && bufferChar[i+2] == 'r' &&	bufferChar[i+3] == 'r' && bufferChar[i+4] == '>') {
			i += 5;
			return;
		}
		while (i < n) {
			List2s list2s = new List2s();
			list2s.code = (short)readBytes(readBytes(1));
			list2s.Violation = (short)readBytes(readBytes(1));
			v.addElement(list2s);
		}
	}

	public void loadStreet() {
		DB.streets.removeAllElements();
		int n = readBytes(6)+i;
		while (i < n) {
			Street street = new Street();
			street.code = (short)readBytes(readBytes(1));
			street.Kod = (int)readBytes(readBytes(1));
			street.Nm = getString(readBytes(4));
			street.CS = (int)readBytes(readBytes(1));
			street.NPFNZ = (short)readBytes(readBytes(1));
			street.NPTNZ = (short)readBytes(readBytes(1));
			street.NPFZ = (short)readBytes(readBytes(1));
			street.NPTZ = (short)readBytes(readBytes(1));
			street.Ezor = (short)readBytes(readBytes(1));
			if (readBytes(readBytes(1)) != 0)
				street.SwHatraa = true;
			else
				street.SwHatraa = false;
			street.date = readBytes(readBytes(2));
			DB.streets.addElement(street);
		}
	}

	private void getAuthorityCharacteristic() {
		readBytes(6);
		records++;
		DB.authorityCharacteristic.Violation = (short)readBytes(readBytes(1));
		DB.authorityCharacteristic.CarType = (short)readBytes(readBytes(1));
		DB.authorityCharacteristic.CarColor = (short)readBytes(readBytes(1));
		DB.authorityCharacteristic.CarIzran = (short)readBytes(readBytes(1));
		DB.authorityCharacteristic.SwN = (short)readBytes(readBytes(1));
		DB.authorityCharacteristic.SwS = (short)readBytes(readBytes(1));
		DB.authorityCharacteristic.SwT = (short)readBytes(readBytes(1));
		DB.authorityCharacteristic.SwPic240_320 = (short)readBytes (readBytes(1));
		DB.authorityCharacteristic.SwM = (short)readBytes(readBytes(1));
		DB.authorityCharacteristic.ServTo = (short)readBytes(readBytes(1));
		DB.authorityCharacteristic.TimeOut_CarNumber = (int)readBytes(readBytes(1));
		DB.authorityCharacteristic.TimeOut_ChkHeter = (int)readBytes(readBytes(1));
		DB.authorityCharacteristic.TimeOut_DoubleReport = (int)readBytes(readBytes(1));
		DB.authorityCharacteristic.TimeOut_SendReport = (int)readBytes(readBytes(1));
		DB.authorityCharacteristic.TimeOut_SendPic = (int)readBytes(readBytes(1));
		DB.authorityCharacteristic.TimeOut_GetReport = (int)readBytes(readBytes(1));
		DB.authorityCharacteristic.TimeOut_SendCancel = (int)readBytes(readBytes(1));
		DB.authorityCharacteristic.TimeOut_SendCancelPic = (int)readBytes(readBytes(1));
		DB.authorityCharacteristic.PicNo = (short)readBytes(readBytes(1));
		DB.authorityCharacteristic.SwNoDeleteDochMeshofon = (short)readBytes(readBytes(1));
		DB.authorityCharacteristic.SwAddPinkasMesofon = (short)readBytes(readBytes(1));
		DB.authorityCharacteristic.SwPrintDochBreratMispat = (short)readBytes(readBytes(1));
		DB.authorityCharacteristic.SwEnterCar = (short)readBytes(readBytes(1));
		DB.authorityCharacteristic.FlashOnFrom = (short)readBytes(readBytes(1));
		DB.authorityCharacteristic.FlashOnTo = (short)readBytes(readBytes(1));
		DB.authorityCharacteristic.BetMishpat = getString(readBytes(2));
		DB.authorityCharacteristic.CntMaxPicMesofon = (short)readBytes(readBytes(1));
		if (DB.authorityCharacteristic.CntMaxPicMesofon == 0)
			DB.authorityCharacteristic.CntMaxPicMesofon = 10000;
		DB.authorityCharacteristic.SwPrintDate = (short)readBytes(readBytes(1));
		DB.authorityCharacteristic.Minute_CloseMesofon = (short)readBytes(readBytes(1));
		if (DB.authorityCharacteristic.Minute_CloseMesofon == 0)
			DB.authorityCharacteristic.Minute_CloseMesofon = 1440; // 24 hours
		DB.authorityCharacteristic.DurationRecording = (short)readBytes(readBytes(1));
		DB.authorityCharacteristic.DurationRecordingDoch = (short)readBytes(readBytes(1));
		if (DB.authorityCharacteristic.DurationRecordingDoch > 1200)   // 20 minutes
			DB.authorityCharacteristic.DurationRecordingDoch = 1200;
		DB.authorityCharacteristic.SwSend_SMS = (short)readBytes(readBytes(1));
		DB.authorityCharacteristic.SwAzara = (short)readBytes(readBytes(1));
		DB.authorityCharacteristic.SwPicHanaya = (short)readBytes(readBytes(1));
		DB.authorityCharacteristic.SwPrintTotal = (short)readBytes(readBytes(1));
		DB.authorityCharacteristic.AuthorityName = getString(readBytes(2));
		DB.authorityCharacteristic.SwQR = readBytes(readBytes(1));
		DB.authorityCharacteristic.PinkasWarningReport = readBytes(1) == 1;
		DB.authorityCharacteristic.RepeatLicsence = readBytes(1) == 1;
		DB.authorityCharacteristic.SwchooseNechePic = readBytes(1) == 1;
		DB.authorityCharacteristic.SwQRManager = readBytes(1) == 1;
		DB.authorityCharacteristic.SwNotMustTz = readBytes(1) == 1;
		DB.authorityCharacteristic.SwMustServTo = readBytes(1) == 1;
		DB.authorityCharacteristic.lastUpdate = readBytes(readBytes(2));
	}

	private void getInsideCodes(Vector<InsideCode> v) {
		v.removeAllElements();
		int n = readBytes(6)+i;
		if (i <= n)
			records++;
		if(bufferChar[i] == '<' && bufferChar[i+1] == 'E' && bufferChar[i+2] == 'r' &&	bufferChar[i+3] == 'r' && bufferChar[i+4] == '>') {
			i += 5;
			return;
		}
		while (i < n) {
			if(bufferChar[i] == '<' && bufferChar[i+1] == 'E' && bufferChar[i+2] == 'r' &&	bufferChar[i+3] == 'r' && bufferChar[i+4] == '>') {
				i += 5;
				break;
			}
			InsideCode insideCode = new InsideCode();
			insideCode.C = (short)readBytes(readBytes(2));
			insideCode.Nm = getString(readBytes(2));
			insideCode.Kod = (short)readBytes(readBytes(2));
			v.addElement(insideCode);
		}
	}

	private void getTrialDates(Vector<TrialDate> v) {
		v.removeAllElements();
		int n = readBytes(6)+i;
		if (i <= n)
			records++;
		if(bufferChar[i] == '<' && bufferChar[i+1] == 'E' && bufferChar[i+2] == 'r' &&	bufferChar[i+3] == 'r' && bufferChar[i+4] == '>') {
			i += 5;
			return;
		}
		while (i < n) {
			TrialDate trialDates = new TrialDate();
			trialDates.C = (int)readBytes(readBytes(1));
			trialDates.Date = getString(readBytes(2));
			trialDates.Hour = getString(readBytes(1));
			v.addElement(trialDates);
		}
	}

	/** Extracts previously given warnings from the buffer.
	 *  Creates a vector containing the warnings. Each element of it is a warning.
	 */
	private void getExistingWarnings(Vector<ExistingWarning> v1,Vector<ExistingWarning> v2) {
		v1.removeAllElements();
		v2.removeAllElements();
		int n = readBytes(6)+i;
		if (i <= n)
			records++;
		if (bufferChar[i] == '<' && bufferChar[i+1] == 'E' && bufferChar[i+2] == 'r' && bufferChar[i+3] == 'r' && bufferChar[i+4] == '>') {
			i += 5;
			return;
		}
		if (bufferChar[i] == 'O' && bufferChar[i+1] == 'K') {
			i += 2;
			return;
		}
		while (i < n) {
			addExistingWarning(v1,v2);
		}
	}

	private void getButtonsMap() {
		int n = readBytes(6)+i;
		if (i <= n)
			records++;
		if (i+4 < bufferChar.length && bufferChar[i] == '<' && bufferChar[i+1] == 'E' && bufferChar[i+2] == 'r' &&	bufferChar[i+3] == 'r' && bufferChar[i+4] == '>') {
			i += 5;
			return;
		}
		int buttonsMapCount = 0;
		while (i < n) {
			if(buttonsMapCount < DB.buttonsMap.length) {
				DB.buttonsMap[buttonsMapCount] = (int)readBytes(readBytes(2));
				buttonsMapCount++;
			}
		}
	}

	private void getNotebookTypes(Vector<NotebookType> v) {
		v.removeAllElements();
		int n = readBytes(6)+i;
		if (i <= n)
			records++;
		if (bufferChar[i] == '<' && bufferChar[i+1] == 'E' && bufferChar[i+2] == 'r' &&	bufferChar[i+3] == 'r' && bufferChar[i+4] == '>') {
			i += 5;
			return;
		}
		while (i < n) {
			NotebookType notebookType = new NotebookType();
			notebookType.C = (short)readBytes(readBytes(2));
			notebookType.Nm = getString(readBytes(2));
			notebookType.Kod = (short)readBytes(readBytes(2));
			v.addElement(notebookType);
		}
	}

	public static void abort() {
		try {
			if(me != null) {
				me.doAbort();
				me.errorOccurred = true;
			}
		}
		catch (Exception e) {}
	}
}
