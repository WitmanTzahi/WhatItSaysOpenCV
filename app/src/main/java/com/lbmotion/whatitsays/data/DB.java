package com.lbmotion.whatitsays.data;

import android.util.Log;

import com.lbmotion.whatitsays.MainActivity;
import com.lbmotion.whatitsays.UCApp;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.Vector;

public class DB {

	private static final String TAG = "DB";
	public static Vector<List3> vehicleTypes = new Vector<List3>();
	public static Vector<List2> vehicleManufacturers = new Vector<List2>();
	public static Vector<List2> vehicleColors = new Vector<List2>();
	public static Vector<List3> areas = new Vector<List3>();
	public static Vector<List2> cancelRemarks = new Vector<List2>();
	public static Vector<List3> remarks = new Vector<List3>();
	public static Vector<List2s> remarksToViolations = new Vector<List2s>();
	public static Vector<List3> citizenRemarks = new Vector<List3>();
	public static Vector<Violation> violations = new Vector<Violation>();
	public static Vector<Violation> allViolations = new Vector<Violation>();
	public static Vector<Violation> workingViolations = new Vector<Violation>();
	public static Vector<Street> streets = new Vector<Street>();
	public static Vector<TrialDate> trialDates = new Vector<TrialDate>();
	public static Vector<Defendant> defendantList = new Vector<Defendant>();
	public static Vector<ExistingWarning> existingWarnings = new Vector<ExistingWarning>();
	public static Vector<ExistingWarning> pastToHandleEvents = new Vector<ExistingWarning>();
	public static Vector<InsideCode> insideCodes = new Vector<InsideCode>();
	public static Vector<NotebookType> notebookTypes = new Vector<NotebookType>();
	public static Vector<NotebookType> nimhans = new Vector<NotebookType>();
	public static int [] 					buttonsMap = new int [8];
	public static HashMap<String, Vector<String>> cities = new HashMap<String, Vector<String>>(1400);

	public static Defendant defendant = null;
	public static Witness witness = null;
	public static Vehicle car = null;

	public static AuthorityCharacteristic authorityCharacteristic = new AuthorityCharacteristic();
	//
	public DB() {}

	public static void readVectorViolation(DataInputStream inputStream) throws IOException {
		allViolations.removeAllElements();
		short records = inputStream.readShort();
		for(short i = 0;i < records;i++) {
			Violation violation = new Violation();
			violation.C = inputStream.readInt();
			violation.K = inputStream.readInt();
			violation.VT = inputStream.readByte();
			violation.Nm = inputStream.readUTF();
			violation.MP = inputStream.readShort();
			violation.MR = inputStream.readShort();
			violation.P = inputStream.readShort();
			violation.D = inputStream.readShort();
			violation.SwHatraa = inputStream.readBoolean();
			violation.SwHanayaHok = inputStream.readShort();
			violation.title = inputStream.readUTF();
			violation.SwTz = inputStream.readByte();
			violation.SwRishui = inputStream.readByte();
			violation.SwPrivateCompany = inputStream.readByte();
			violation.NumCopies = inputStream.readByte();
			if(violation.NumCopies == 0)
				violation.NumCopies = 1;
			violation.SwMustTavHanaya = inputStream.readShort();
			violation.VTC = inputStream.readByte();
			violation.VT_Nm = inputStream.readUTF();
			violation.Sivug = inputStream.readByte();
			violation.OffenceIcon = inputStream.readByte();
			violation.hasVirtualDoc = inputStream.readBoolean();
			violation.QRSwWork = inputStream.readBoolean();
			violation.SwWorkPrinter = inputStream.readBoolean();
			violation.TypeForm = inputStream.readUTF();
			violation.toAtraa = inputStream.readShort();
			violation.toAvera = inputStream.readShort();
			violation.swPrintQR = inputStream.readByte();
			violation.codeMotavShort = inputStream.readUTF();
			for(byte s = inputStream.readByte();s > 0;s--) {
				violation.screensOrder.add(Integer.valueOf(inputStream.readByte()));
			}
			violation.TeurShort = inputStream.readUTF();
//			violation.QRSwWork = false;//^^^^
			allViolations.addElement(violation);
			Log.i(TAG,"Read:"+violation.C+"    "+violation.K+"    "+violation.QRSwWork);//^^^^
		}
	}

	public static void writeVectorViolation(DataOutputStream outputStream) throws IOException {
		outputStream.writeShort((short)allViolations.size());
		for(short i = 0;i < allViolations.size();i++) {
			Violation violation = allViolations.get(i);
//			violation.QRSwWork = false;//^^^^
			outputStream.writeInt(violation.C);
			outputStream.writeInt(violation.K);
			outputStream.writeByte(violation.VT);
			outputStream.writeUTF(violation.Nm);
			outputStream.writeShort(violation.MP);
			outputStream.writeShort(violation.MR);
			outputStream.writeShort(violation.P);
			outputStream.writeShort(violation.D);
			outputStream.writeBoolean(violation.SwHatraa);
			outputStream.writeShort(violation.SwHanayaHok);
			outputStream.writeUTF(violation.title);
			outputStream.writeByte(violation.SwTz);
			outputStream.writeByte(violation.SwRishui);
			outputStream.writeByte(violation.SwPrivateCompany);
			outputStream.writeByte(violation.NumCopies);
			outputStream.writeShort(violation.SwMustTavHanaya);
			outputStream.writeByte(violation.VTC);
			outputStream.writeUTF(violation.VT_Nm);
			outputStream.writeByte(violation.Sivug);
			outputStream.writeByte(violation.OffenceIcon);
			outputStream.writeBoolean(violation.hasVirtualDoc);
			outputStream.writeBoolean(violation.QRSwWork);
			outputStream.writeBoolean(violation.SwWorkPrinter);
			outputStream.writeUTF(violation.TypeForm);
			outputStream.writeShort(violation.toAtraa);
			outputStream.writeShort(violation.toAvera);
			outputStream.writeByte(violation.swPrintQR);
			outputStream.writeUTF(violation.codeMotavShort);
			outputStream.writeByte(violation.screensOrder.size());
			for(int s = 0;s < violation.screensOrder.size();s++) {
				outputStream.writeByte(violation.screensOrder.get(s).byteValue());
			}
			outputStream.writeUTF(violation.TeurShort);
			Log.i(TAG,"Write:"+violation.C+"    "+violation.K+"    "+violation.QRSwWork);//^^^^
		}
	}

	private static void ReadTrialDates(DataInputStream inputStream, Vector<TrialDate> v) throws IOException {
		v.removeAllElements();
		short l = inputStream.readShort();
		for(short i = 0;i < l;i++) {
			TrialDate trialDate = new TrialDate();
			trialDate.C = inputStream.readInt();
			trialDate.Date = inputStream.readUTF();
			trialDate.Hour = inputStream.readUTF();
			v.add(trialDate);
		}
	}

	private static void WriteTrialDates (DataOutputStream outputStream, Vector<TrialDate> v) throws IOException {
		outputStream.writeShort(v.size());
		for(short i = 0;i < v.size();i++) {
			TrialDate trialDate = v.get(i);
			outputStream.writeInt(trialDate.C);
			outputStream.writeUTF(trialDate.Date);
			outputStream.writeUTF(trialDate.Hour);
		}
	}

	private static void readVectorList2(DataInputStream inputStream, Vector<List2> v) throws IOException {
		v.removeAllElements();
		short l = inputStream.readShort();
		for(short i = 0;i < l;i++) {
			List2 list2 = new List2();
			list2.code = inputStream.readInt();
			list2.name = inputStream.readUTF();
			v.addElement(list2);
		}
	}

	private static void writeVectorList2(DataOutputStream outputStream, Vector<List2> v) throws IOException {
		outputStream.writeShort(v.size());
		for(short i = 0;i < v.size();i++) {
			List2 list2 = (List2)v.elementAt(i);
			outputStream.writeInt(list2.code);
			outputStream.writeUTF(list2.name);
		}
	}

	private static void ReadVectorList2s(DataInputStream inputStream, Vector<List2s> v) throws IOException {
		v.removeAllElements();
		short l = inputStream.readShort();
		for(short i = 0;i < l;i++) {
			List2s list2s = new List2s();
			list2s.code = inputStream.readInt();
			list2s.Violation = inputStream.readInt();
			v.addElement(list2s);
		}
	}

	private static void WriteVectorList2s(DataOutputStream outputStream, Vector<List2s> v) throws IOException {
		outputStream.writeShort(v.size());
		for(short i = 0;i < v.size();i++) {
			List2s list2s = (List2s)v.elementAt(i);
			outputStream.writeInt(list2s.code);
			outputStream.writeInt(list2s.Violation);
		}
	}

	private static void readVectorList3(DataInputStream inputStream, Vector<List3> v) throws IOException {
		v.removeAllElements();
		short l = inputStream.readShort();
		for(short i = 0;i < l;i++) {
			List3 list3 = new List3();
			list3.code = inputStream.readInt();
			list3.Kod = inputStream.readInt();
			list3.name = inputStream.readUTF();
			v.addElement(list3);
		}
	}

	private static void writeVectorList3(DataOutputStream outputStream, Vector<List3> v) throws IOException {
		outputStream.writeShort(v.size());
		for(short i = 0;i < v.size();i++) {
			List3 list3 = (List3)v.elementAt(i);
			outputStream.writeInt(list3.code);
			outputStream.writeInt(list3.Kod);
			outputStream.writeUTF(list3.name);
		}
	}

	private static void ReadAuthorityCharacteristic(DataInputStream inputStream) throws IOException {
		authorityCharacteristic.Violation = inputStream.readInt();
		authorityCharacteristic.CarType = inputStream.readInt();
		authorityCharacteristic.CarColor = inputStream.readInt();
		authorityCharacteristic.CarIzran = inputStream.readInt();
		authorityCharacteristic.SwN = inputStream.readInt();
		authorityCharacteristic.SwS = inputStream.readInt();
		authorityCharacteristic.SwT = inputStream.readInt();
		authorityCharacteristic.SwPic240_320 = inputStream.readInt();
		authorityCharacteristic.SwM = inputStream.readInt();
		authorityCharacteristic.ServTo = inputStream.readInt();
		authorityCharacteristic.TimeOut_CarNumber = inputStream.readInt();
		authorityCharacteristic.TimeOut_ChkHeter = inputStream.readInt();
		authorityCharacteristic.TimeOut_DoubleReport = inputStream.readInt();
		authorityCharacteristic.TimeOut_SendReport = inputStream.readInt();
		authorityCharacteristic.TimeOut_SendPic = inputStream.readInt();
		authorityCharacteristic.TimeOut_GetReport = inputStream.readInt();
		authorityCharacteristic.TimeOut_SendCancel = inputStream.readInt();
		authorityCharacteristic.TimeOut_SendCancelPic = inputStream.readInt();
		authorityCharacteristic.PicNo = inputStream.readInt();
		authorityCharacteristic.SwNoDeleteDochMeshofon = inputStream.readInt();
		authorityCharacteristic.SwAddPinkasMesofon = inputStream.readInt();
		authorityCharacteristic.SwPrintDochBreratMispat = inputStream.readInt();
		authorityCharacteristic.SwEnterCar = inputStream.readInt();
		authorityCharacteristic.FlashOnFrom = inputStream.readInt();
		authorityCharacteristic.FlashOnTo = inputStream.readInt();
		authorityCharacteristic.lastUpdate = inputStream.readLong();
		authorityCharacteristic.CntMaxPicMesofon = inputStream.readInt();
		authorityCharacteristic.SwPrintDate = inputStream.readInt();
		authorityCharacteristic.Minute_CloseMesofon = inputStream.readInt();
		authorityCharacteristic.DurationRecording = inputStream.readInt(); 
		authorityCharacteristic.DurationRecordingDoch = inputStream.readInt();
		authorityCharacteristic.SwSend_SMS = inputStream.readInt();
		authorityCharacteristic.SwAzara = inputStream.readInt();
		authorityCharacteristic.AuthorityName = inputStream.readUTF();
		authorityCharacteristic.SwPicHanaya = inputStream.readInt();
		authorityCharacteristic.SwPrintTotal = inputStream.readInt();
		authorityCharacteristic.SwQR = inputStream.readInt();
		authorityCharacteristic.PinkasWarningReport = inputStream.readBoolean();
		authorityCharacteristic.RepeatLicsence = inputStream.readBoolean();
		authorityCharacteristic.SwchooseNechePic = inputStream.readBoolean();
		authorityCharacteristic.SwQRManager = inputStream.readBoolean();
 		authorityCharacteristic.SwNotMustTz = inputStream.readBoolean();
		authorityCharacteristic.SwMustServTo = inputStream.readBoolean();
	}

	private static void WriteAuthorityCharacteristic(DataOutputStream outputStream) throws IOException {
		outputStream.writeInt(authorityCharacteristic.Violation);
		outputStream.writeInt(authorityCharacteristic.CarType);
		outputStream.writeInt(authorityCharacteristic.CarColor);
		outputStream.writeInt(authorityCharacteristic.CarIzran);
		outputStream.writeInt(authorityCharacteristic.SwN);
		outputStream.writeInt(authorityCharacteristic.SwS);
		outputStream.writeInt(authorityCharacteristic.SwT);
		outputStream.writeInt(authorityCharacteristic.SwPic240_320);
		outputStream.writeInt(authorityCharacteristic.SwM);
		outputStream.writeInt(authorityCharacteristic.ServTo);
		outputStream.writeInt(authorityCharacteristic.TimeOut_CarNumber);
		outputStream.writeInt(authorityCharacteristic.TimeOut_ChkHeter);
		outputStream.writeInt(authorityCharacteristic.TimeOut_DoubleReport);
		outputStream.writeInt(authorityCharacteristic.TimeOut_SendReport);
		outputStream.writeInt(authorityCharacteristic.TimeOut_SendPic);
		outputStream.writeInt(authorityCharacteristic.TimeOut_GetReport);
		outputStream.writeInt(authorityCharacteristic.TimeOut_SendCancel);
		outputStream.writeInt(authorityCharacteristic.TimeOut_SendCancelPic);
		outputStream.writeInt(authorityCharacteristic.PicNo);
		outputStream.writeInt(authorityCharacteristic.SwNoDeleteDochMeshofon);
		outputStream.writeInt(authorityCharacteristic.SwAddPinkasMesofon);
		outputStream.writeInt(authorityCharacteristic.SwPrintDochBreratMispat);
		outputStream.writeInt(authorityCharacteristic.SwEnterCar);
		outputStream.writeInt(authorityCharacteristic.FlashOnFrom);
		outputStream.writeInt(authorityCharacteristic.FlashOnTo);
		outputStream.writeLong(authorityCharacteristic.lastUpdate);
		outputStream.writeInt (authorityCharacteristic.CntMaxPicMesofon);
		outputStream.writeInt (authorityCharacteristic.SwPrintDate);
		outputStream.writeInt (authorityCharacteristic.Minute_CloseMesofon);
		outputStream.writeInt (authorityCharacteristic.DurationRecording);
		outputStream.writeInt (authorityCharacteristic.DurationRecordingDoch);
		outputStream.writeInt (authorityCharacteristic.SwSend_SMS);
		outputStream.writeInt (authorityCharacteristic.SwAzara);
		outputStream.writeUTF (authorityCharacteristic.AuthorityName);
		outputStream.writeInt(authorityCharacteristic.SwPicHanaya);
		outputStream.writeInt(authorityCharacteristic.SwPrintTotal);
		outputStream.writeInt(authorityCharacteristic.SwQR);
		outputStream.writeBoolean(authorityCharacteristic.PinkasWarningReport);
		outputStream.writeBoolean(authorityCharacteristic.RepeatLicsence);
		outputStream.writeBoolean(authorityCharacteristic.SwchooseNechePic);
		outputStream.writeBoolean(authorityCharacteristic.SwQRManager);
		outputStream.writeBoolean(authorityCharacteristic.SwNotMustTz);
		outputStream.writeBoolean(authorityCharacteristic.SwMustServTo);
	}

	private static void readInsideCodes(DataInputStream inputStream, Vector<InsideCode> v) throws IOException {
		v.removeAllElements();
		short l = inputStream.readShort();
		for(short i = 0;i < l;i++) {
			InsideCode insideCode = new InsideCode();
			insideCode.C = inputStream.readShort();
			insideCode.Kod = inputStream.readShort();
			insideCode.Nm = inputStream.readUTF();
			v.add(insideCode);
		}
	}

	private static void writeInsideCodes(DataOutputStream outputStream, Vector<InsideCode> v) throws IOException {
		outputStream.writeShort(v.size());
		for(short i = 0;i < v.size();i++) {
			InsideCode insideCode = (InsideCode)v.elementAt(i);
			outputStream.writeShort(insideCode.C);
			outputStream.writeShort(insideCode.Kod);
			outputStream.writeUTF(insideCode.Nm);
		}
	}
	
	private static void readVectorStreets(DataInputStream inputStream) throws IOException {
		int numberOfStreets = inputStream.readInt();
		for(short i = 0;i < numberOfStreets;i++) {
			Street street = new Street();
			street.code = inputStream.readInt();
			street.Nm = inputStream.readUTF();
			street.CS = inputStream.readInt();
			street.NPFNZ = inputStream.readShort();
			street.NPTNZ = inputStream.readShort();
			street.NPFZ = inputStream.readShort();
			street.NPTZ = inputStream.readShort();
			street.Kod = inputStream.readInt();
			street.Ezor = inputStream.readInt();
			street.SwHatraa = inputStream.readBoolean();
			street.date = inputStream.readLong();
			streets.add(street);
		}
	}

	private static void writeVectorStreets(DataOutputStream outputStream) throws IOException {
		outputStream.writeInt(streets.size());
		for(int i = 0;i < streets.size();i++) {
			Street street = streets.get(i);
			outputStream.writeInt(street.code);
			outputStream.writeUTF(street.Nm);
			outputStream.writeInt(street.CS);
			outputStream.writeShort(street.NPFNZ);
			outputStream.writeShort(street.NPTNZ);
			outputStream.writeShort(street.NPFZ);
			outputStream.writeShort(street.NPTZ);
			outputStream.writeInt(street.Kod);
			outputStream.writeInt(street.Ezor);
			outputStream.writeBoolean(street.SwHatraa);
			outputStream.writeLong(street.date);
		}
	}
/*
	private static void ReadStreetWarnings(DataInputStream inputStream,Vector<StreetWarnings> v) throws IOException {
		v.removeAllElements();
		short l = inputStream.readShort();
		for(short i = 0;i < l;i++) {
			StreetWarnings streetWarning = new StreetWarnings();
			streetWarning.C = inputStream.readInt();
			streetWarning.CStreet = inputStream.readInt();
			streetWarning.KodStreet = inputStream.readInt();
			streetWarning.FromNoZugi = inputStream.readInt();
			streetWarning.ToNoZugi = inputStream.readInt();
			streetWarning.FromNoLoZugi = inputStream.readInt();
			streetWarning.ToNoLoZugi = inputStream.readInt();
			streetWarning.FromDate = inputStream.readUTF();
			streetWarning.ToDate = inputStream.readUTF();
			int numAverot = inputStream.readShort();
			streetWarning.Averot = new int[numAverot];
			for(short j=0;j<streetWarning.Averot.length;j++)
				streetWarning.Averot[j] = inputStream.readInt();
			v.add(streetWarning);
		}
	}

	private static void WriteStreetWarnings(DataOutputStream outputStream,Vector<StreetWarnings> v) throws IOException {
		outputStream.writeShort(v.size());
		for(short i = 0;i < v.size();i++) {
			StreetWarnings streetWarning = v.get(i);
			outputStream.writeInt(streetWarning.C);
			outputStream.writeInt(streetWarning.CStreet);
			outputStream.writeInt(streetWarning.KodStreet);
			outputStream.writeInt(streetWarning.FromNoZugi);
			outputStream.writeInt(streetWarning.ToNoZugi);
			outputStream.writeInt(streetWarning.FromNoLoZugi);
			outputStream.writeInt(streetWarning.ToNoLoZugi);
			outputStream.writeUTF(streetWarning.FromDate);
			outputStream.writeUTF(streetWarning.ToDate);
			outputStream.writeShort(streetWarning.Averot.length);
			for(short j=0;j<streetWarning.Averot.length;j++)
				outputStream.writeInt(streetWarning.Averot[j]);
		}
	}
*/
	private static void readExistingWarnings(DataInputStream inputStream, Vector<ExistingWarning> v) throws IOException {
		v.removeAllElements();
		short l = inputStream.readShort();
		for(short i = 0;i < l;i++) {
			ExistingWarning existingWarning = new ExistingWarning();
			existingWarning.Lk = inputStream.readUTF();
			existingWarning.UsrKod = inputStream.readUTF();
			existingWarning.UsrNm = inputStream.readUTF();
			existingWarning.UsrC = inputStream.readUTF();
			existingWarning.DochC = inputStream.readUTF();
			existingWarning.DochKod = inputStream.readUTF();
			existingWarning.Sidra = inputStream.readUTF();
			existingWarning.Bikoret = inputStream.readUTF();
			existingWarning.vehicle.licence = inputStream.readUTF();
			existingWarning.vehicle.color = inputStream.readShort();
			existingWarning.vehicle.type = inputStream.readShort();
			existingWarning.vehicle.manufacturer = inputStream.readShort();
			existingWarning.AveraNm = inputStream.readUTF();
			existingWarning.AveraC = inputStream.readUTF();
			existingWarning.AveraKod = inputStream.readUTF();
			existingWarning.Mhr = inputStream.readUTF();
			existingWarning.Days = inputStream.readUTF();
			existingWarning.StreetC = inputStream.readUTF();
			existingWarning.StreetNm = inputStream.readUTF();
			existingWarning.MikumKod = inputStream.readUTF();
			existingWarning.MikumC = inputStream.readUTF();
			existingWarning.StreetKod = inputStream.readUTF();			
//			
			existingWarning.defendant.id			= inputStream.readUTF();
			existingWarning.defendant.last			= inputStream.readUTF();
			existingWarning.defendant.name			= inputStream.readUTF();
			existingWarning.defendant.street		= inputStream.readUTF();
			existingWarning.defendant.number		= inputStream.readUTF();
			existingWarning.defendant.flat			= inputStream.readUTF();
			existingWarning.defendant.cityCode		= inputStream.readUTF();
			existingWarning.defendant.cityName		= inputStream.readUTF();
			existingWarning.defendant.zipcode		= inputStream.readUTF();
			existingWarning.defendant.box			= inputStream.readUTF();
//
			existingWarning.witness.id = inputStream.readUTF();
			existingWarning.witness.last = inputStream.readUTF();
			existingWarning.witness.name = inputStream.readUTF();
			existingWarning.witness.street = inputStream.readUTF();
			existingWarning.witness.number = inputStream.readUTF();
			existingWarning.witness.flat = inputStream.readUTF();
			existingWarning.witness.city = inputStream.readUTF();
			existingWarning.witness.cityCode = inputStream.readUTF();
			existingWarning.witness.zipcode = inputStream.readUTF();
			existingWarning.witness.telephone = inputStream.readUTF();
//
			existingWarning.numAzhara = inputStream.readUTF();
			existingWarning.StreetNoAvera = inputStream.readUTF();
			existingWarning.CityNm = inputStream.readUTF();
			existingWarning.validUpto = inputStream.readUTF();
			existingWarning.animalId = inputStream.readUTF();
			existingWarning.remark = inputStream.readUTF();
			existingWarning.validUptoTime = inputStream.readUTF();
			v.add(existingWarning);
		}
	}

	private static void WriteExistingWarnings(DataOutputStream outputStream, Vector<ExistingWarning> v) throws IOException {
		outputStream.writeShort(v.size());
		for(short i = 0;i < v.size();i++) {
			ExistingWarning existingWarning = v.get(i);
			outputStream.writeUTF(existingWarning.Lk);
			outputStream.writeUTF(existingWarning.UsrKod);
			outputStream.writeUTF(existingWarning.UsrNm);
			outputStream.writeUTF(existingWarning.UsrC);
			outputStream.writeUTF(existingWarning.DochC);
			outputStream.writeUTF(existingWarning.DochKod);
			outputStream.writeUTF(existingWarning.Sidra);
			outputStream.writeUTF(existingWarning.Bikoret);
			outputStream.writeUTF(existingWarning.vehicle.licence);
			outputStream.writeShort(existingWarning.vehicle.color);
			outputStream.writeShort(existingWarning.vehicle.type);
			outputStream.writeShort(existingWarning.vehicle.manufacturer);
			outputStream.writeUTF(existingWarning.AveraNm);
			outputStream.writeUTF(existingWarning.AveraC);
			outputStream.writeUTF(existingWarning.AveraKod);
			outputStream.writeUTF(existingWarning.Mhr);
			outputStream.writeUTF(existingWarning.Days);
			outputStream.writeUTF(existingWarning.StreetC);
			outputStream.writeUTF(existingWarning.StreetNm);
			outputStream.writeUTF(existingWarning.MikumKod);
			outputStream.writeUTF(existingWarning.MikumC);
			outputStream.writeUTF(existingWarning.StreetKod);
//			
			outputStream.writeUTF(existingWarning.defendant.id);
			outputStream.writeUTF(existingWarning.defendant.last);
			outputStream.writeUTF(existingWarning.defendant.name);
			outputStream.writeUTF(existingWarning.defendant.street);
			outputStream.writeUTF(existingWarning.defendant.number);
			outputStream.writeUTF(existingWarning.defendant.flat);
			outputStream.writeUTF(existingWarning.defendant.cityCode);
			outputStream.writeUTF(existingWarning.defendant.cityName);
			outputStream.writeUTF(existingWarning.defendant.zipcode);
			outputStream.writeUTF(existingWarning.defendant.box);
//			
			outputStream.writeUTF(existingWarning.witness.id);
			outputStream.writeUTF(existingWarning.witness.last);
			outputStream.writeUTF(existingWarning.witness.name);
			outputStream.writeUTF(existingWarning.witness.street);
			outputStream.writeUTF(existingWarning.witness.number);
			outputStream.writeUTF(existingWarning.witness.flat);
			outputStream.writeUTF(existingWarning.witness.city);
			outputStream.writeUTF(existingWarning.witness.cityCode);
			outputStream.writeUTF(existingWarning.witness.zipcode);
			outputStream.writeUTF(existingWarning.witness.telephone);
			
			outputStream.writeUTF(existingWarning.numAzhara);
			outputStream.writeUTF(existingWarning.StreetNoAvera);
			outputStream.writeUTF(existingWarning.CityNm);
			outputStream.writeUTF(existingWarning.validUpto);
			outputStream.writeUTF(existingWarning.animalId);
			outputStream.writeUTF(existingWarning.remark);
			outputStream.writeUTF(existingWarning.validUptoTime);
		}
	}

	private static void readNotebookTypes(DataInputStream inputStream, Vector<NotebookType> v) throws IOException {
		v.removeAllElements();
		short l = inputStream.readShort();
		for(short i = 0;i < l;i++) {
			NotebookType notebookType = new NotebookType();
			notebookType.C = inputStream.readShort();
			notebookType.Kod = inputStream.readShort();
			notebookType.Nm = inputStream.readUTF();
			v.addElement(notebookType);
		}
	}

	private static void writeNotebookTypes(DataOutputStream outputStream, Vector<NotebookType> v) throws IOException {
		outputStream.writeShort(v.size());
		for(short i = 0;i < v.size();i++) {
			NotebookType notebookType = (NotebookType)v.elementAt(i);
			outputStream.writeShort(notebookType.C);
			outputStream.writeShort(notebookType.Kod);
			outputStream.writeUTF(notebookType.Nm);
		}
	}

	private static void readButtonsMap(DataInputStream inputStream) throws IOException {
		short l = inputStream.readShort();
		for(short i = 0;i < l && i < buttonsMap.length;i++) {
			buttonsMap[i] = inputStream.readInt();
		}
	}

	private static void writeButtonsMap(DataOutputStream outputStream) throws IOException {
		outputStream.writeShort(buttonsMap.length);
		for(short i = 0;i < buttonsMap.length;i++)
			outputStream.writeInt(buttonsMap[i]);
	}

	public static void SaveDBVariable() {
		try {RecordStore.deleteRecordStore("TD");}catch(Exception e) {}
		try {RecordStore.deleteRecordStore("EW");}catch(Exception e) {}
		try {RecordStore.deleteRecordStore("EWTH");}catch(Exception e) {}
		try {RecordStore.deleteRecordStore("NbT");}catch(Exception e) {}
		try {RecordStore.deleteRecordStore("NiT");}catch(Exception e) {}
		try {
			RecordStore recordStore = null;
			ByteArrayOutputStream baos;
			DataOutputStream outputStream;
			byte[] b;
//
			recordStore = RecordStore.openRecordStore("TD",true);
			recordStore.setNumberofRecords(1);
			baos = new ByteArrayOutputStream();
			outputStream = new DataOutputStream(baos);
			WriteTrialDates(outputStream,trialDates);
			b = baos.toByteArray();
			recordStore.addRecord(b,0,b.length);
			try{baos.flush();}catch(Exception e) {}
			try{outputStream.flush();}catch(Exception e) {}
			try{baos.close();}catch(Exception e) {}
			try{outputStream.close();}catch(Exception e) {}
			try{recordStore.closeRecordStore();}catch(Exception e) {}
//
			recordStore = RecordStore.openRecordStore("EW",true);
			recordStore.setNumberofRecords(1);
			baos = new ByteArrayOutputStream();
			outputStream = new DataOutputStream(baos);
			WriteExistingWarnings(outputStream,existingWarnings);
			b = baos.toByteArray();
			recordStore.addRecord(b,0,b.length);
			try{baos.flush();}catch(Exception e) {}
			try{outputStream.flush();}catch(Exception e) {}
			try{baos.close();}catch(Exception e) {}
			try{outputStream.close();}catch(Exception e) {}
			try{recordStore.closeRecordStore();}catch(Exception e) {}
//
			recordStore = RecordStore.openRecordStore("EWTH",true);
			recordStore.setNumberofRecords(1);
			baos = new ByteArrayOutputStream();
			outputStream = new DataOutputStream(baos);
			WriteExistingWarnings(outputStream,pastToHandleEvents);
			b = baos.toByteArray();
			recordStore.addRecord(b,0,b.length);
			try{baos.flush();}catch(Exception e) {}
			try{outputStream.flush();}catch(Exception e) {}
			try{baos.close();}catch(Exception e) {}
			try{outputStream.close();}catch(Exception e) {}
			try{recordStore.closeRecordStore();}catch(Exception e) {}
//
			recordStore = RecordStore.openRecordStore("NbT",true);
			recordStore.setNumberofRecords(1);
			baos = new ByteArrayOutputStream();
			outputStream = new DataOutputStream(baos);
			writeNotebookTypes(outputStream,notebookTypes);
			b = baos.toByteArray();
			recordStore.addRecord(b,0,b.length);
			try{baos.flush();}catch(Exception e) {}
			try{outputStream.flush();}catch(Exception e) {}
			try{baos.close();}catch(Exception e) {}
			try{outputStream.close();}catch(Exception e) {}
			try{recordStore.closeRecordStore();}catch(Exception e) {}
//
			recordStore = RecordStore.openRecordStore("NiT",true);
			recordStore.setNumberofRecords(1);
			baos = new ByteArrayOutputStream();
			outputStream = new DataOutputStream(baos);
			writeNotebookTypes(outputStream,nimhans);
			b = baos.toByteArray();
			recordStore.addRecord(b,0,b.length);
			try{baos.flush();}catch(Exception e) {}
			try{outputStream.flush();}catch(Exception e) {}
			try{baos.close();}catch(Exception e) {}
			try{outputStream.close();}catch(Exception e) {}
			try{recordStore.closeRecordStore();}catch(Exception e) {}
		}
		catch(Exception e) {}
	}
	
	public static void SaveDB() {
		RecordStore recordStore = null;
		try {RecordStore.deleteRecordStore("L3");}catch(Exception e) {}
		try {RecordStore.deleteRecordStore("L2");}catch(Exception e) {}
		try {RecordStore.deleteRecordStore("L2s");}catch(Exception e) {}
		try {RecordStore.deleteRecordStore("Vio");}catch(Exception e) {}
		try {RecordStore.deleteRecordStore("AC");}catch(Exception e) {}
		try {RecordStore.deleteRecordStore("ST");}catch(Exception e) {}
		try {RecordStore.deleteRecordStore("TD");}catch(Exception e) {}     // Trial Dates
		try {RecordStore.deleteRecordStore("SW");}catch(Exception e) {}     // Street warnings
		try {RecordStore.deleteRecordStore("EW");}catch(Exception e) {}     // Existing warnings
		try {RecordStore.deleteRecordStore("IC");}catch(Exception e) {}     // Inside codes
		try {RecordStore.deleteRecordStore("NbT");}catch(Exception e) {}    // Notebook types
		try {RecordStore.deleteRecordStore("NiT");}catch(Exception e) {}
		try {RecordStore.deleteRecordStore("EWTH");}catch(Exception e) {}
		try {
			recordStore = RecordStore.openRecordStore("ST",true);
			recordStore.setNumberofRecords(1);
			ByteArrayOutputStream baos;
			DataOutputStream outputStream;
			byte[] b;
			baos = new ByteArrayOutputStream();
			outputStream = new DataOutputStream(baos);
			writeVectorStreets(outputStream);
			b = baos.toByteArray();
			recordStore.addRecord(b,0,b.length);
			try{baos.flush();}catch(Exception e) {}
			try{outputStream.flush();}catch(Exception e) {}
			try{baos.close();}catch(Exception e) {}
			try{outputStream.close();}catch(Exception e) {}
			try{recordStore.closeRecordStore();}catch(Exception e) {}
//
			recordStore = RecordStore.openRecordStore("L3",true);
			recordStore.setNumberofRecords(1);
			baos = new ByteArrayOutputStream();
			outputStream = new DataOutputStream(baos);
			writeVectorList3(outputStream,vehicleTypes);
			writeVectorList3(outputStream,areas);
			writeVectorList3(outputStream,remarks);
			writeVectorList3(outputStream,citizenRemarks);
			b = baos.toByteArray();
			recordStore.addRecord(b,0,b.length);
			try{baos.flush();}catch(Exception e) {}
			try{outputStream.flush();}catch(Exception e) {}
			try{baos.close();}catch(Exception e) {}
			try{outputStream.close();}catch(Exception e) {}
			try{recordStore.closeRecordStore();}catch(Exception e) {}
			//
			recordStore = RecordStore.openRecordStore("L2",true);
			recordStore.setNumberofRecords(1);
			baos = new ByteArrayOutputStream();
			outputStream = new DataOutputStream(baos);
			writeVectorList2(outputStream,vehicleManufacturers);
			writeVectorList2(outputStream,vehicleColors);
			writeVectorList2(outputStream,cancelRemarks);
			b = baos.toByteArray();
			recordStore.addRecord(b,0,b.length);
			try{baos.flush();}catch(Exception e) {}
			try{outputStream.flush();}catch(Exception e) {}
			try{baos.close();}catch(Exception e) {}
			try{outputStream.close();}catch(Exception e) {}
			try{recordStore.closeRecordStore();}catch(Exception e) {}
//
			recordStore = RecordStore.openRecordStore("L2s",true);
			recordStore.setNumberofRecords(1);
			baos = new ByteArrayOutputStream();
			outputStream = new DataOutputStream(baos);
			WriteVectorList2s(outputStream,remarksToViolations);
			b = baos.toByteArray();
			recordStore.addRecord(b,0,b.length);
			try{recordStore.closeRecordStore();}catch(Exception e) {}
			try{baos.flush();}catch(Exception e) {}
			try{outputStream.flush();}catch(Exception e) {}
			try{baos.close();}catch(Exception e) {}
			try{outputStream.close();}catch(Exception e) {}
//
			recordStore = RecordStore.openRecordStore("AC",true);
			recordStore.setNumberofRecords(1);
			baos = new ByteArrayOutputStream();
			outputStream = new DataOutputStream(baos);
			WriteAuthorityCharacteristic(outputStream);
			b = baos.toByteArray();
			recordStore.addRecord(b,0,b.length);
			try{recordStore.closeRecordStore();}catch(Exception e) {}
			try{baos.flush();}catch(Exception e) {}
			try{outputStream.flush();}catch(Exception e) {}
			try{baos.close();}catch(Exception e) {}
			try{outputStream.close();}catch(Exception e) {}
			//
			recordStore = RecordStore.openRecordStore("Vio",true);
			recordStore.setNumberofRecords(1);
			baos = new ByteArrayOutputStream();
			outputStream = new DataOutputStream(baos);
			writeVectorViolation(outputStream);
			b = baos.toByteArray();
			recordStore.addRecord(b,0,b.length);
			try{recordStore.closeRecordStore();}catch(Exception e) {}
			try{baos.flush();}catch(Exception e) {}
			try{outputStream.flush();}catch(Exception e) {}
			try{baos.close();}catch(Exception e) {}
			try{outputStream.close();}catch(Exception e) {}
	//
			recordStore = RecordStore.openRecordStore("TD",true);
			recordStore.setNumberofRecords(1);
			baos = new ByteArrayOutputStream();
			outputStream = new DataOutputStream(baos);
			WriteTrialDates(outputStream,trialDates);
			b = baos.toByteArray();
			recordStore.addRecord(b,0,b.length);
			try{baos.flush();}catch(Exception e) {}
			try{outputStream.flush();}catch(Exception e) {}
			try{baos.close();}catch(Exception e) {}
			try{outputStream.close();}catch(Exception e) {}
			try{recordStore.closeRecordStore();}catch(Exception e) {}
	//			
			recordStore = RecordStore.openRecordStore("EW",true);
			recordStore.setNumberofRecords(1);
			baos = new ByteArrayOutputStream();
			outputStream = new DataOutputStream(baos);
			WriteExistingWarnings(outputStream,existingWarnings);
			b = baos.toByteArray();
			recordStore.addRecord(b,0,b.length);
			try{baos.flush();}catch(Exception e) {}
			try{outputStream.flush();}catch(Exception e) {}
			try{baos.close();}catch(Exception e) {}
			try{outputStream.close();}catch(Exception e) {}
			try{recordStore.closeRecordStore();}catch(Exception e) {}
//
			recordStore = RecordStore.openRecordStore("EWTH",true);
			recordStore.setNumberofRecords(1);
			baos = new ByteArrayOutputStream();
			outputStream = new DataOutputStream(baos);
			WriteExistingWarnings(outputStream,pastToHandleEvents);
			b = baos.toByteArray();
			recordStore.addRecord(b,0,b.length);
			try{baos.flush();}catch(Exception e) {}
			try{outputStream.flush();}catch(Exception e) {}
			try{baos.close();}catch(Exception e) {}
			try{outputStream.close();}catch(Exception e) {}
			try{recordStore.closeRecordStore();}catch(Exception e) {}
//
			recordStore = RecordStore.openRecordStore("IC",true);
			recordStore.setNumberofRecords(1);
			baos = new ByteArrayOutputStream();
			outputStream = new DataOutputStream(baos);
			writeInsideCodes(outputStream,insideCodes);
			b = baos.toByteArray();
			recordStore.addRecord(b,0,b.length);
			try{recordStore.closeRecordStore();}catch(Exception e) {}
			try{baos.flush();}catch(Exception e) {}
			try{outputStream.flush();}catch(Exception e) {}
			try{baos.close();}catch(Exception e) {}
			try{outputStream.close();}catch(Exception e) {}
//
			recordStore = RecordStore.openRecordStore("NbT",true);
			recordStore.setNumberofRecords(1);
			baos = new ByteArrayOutputStream();
			outputStream = new DataOutputStream(baos);
			writeNotebookTypes(outputStream,notebookTypes);
			b = baos.toByteArray();
			recordStore.addRecord(b,0,b.length);
			try{baos.flush();}catch(Exception e) {}
			try{outputStream.flush();}catch(Exception e) {}
			try{baos.close();}catch(Exception e) {}
			try{outputStream.close();}catch(Exception e) {}
			try{recordStore.closeRecordStore();}catch(Exception e) {}
//
			recordStore = RecordStore.openRecordStore("NiT",true);
			recordStore.setNumberofRecords(1);
			baos = new ByteArrayOutputStream();
			outputStream = new DataOutputStream(baos);
			writeNotebookTypes(outputStream,nimhans);
			b = baos.toByteArray();
			recordStore.addRecord(b,0,b.length);
			try{baos.flush();}catch(Exception e) {}
			try{outputStream.flush();}catch(Exception e) {}
			try{baos.close();}catch(Exception e) {}
			try{outputStream.close();}catch(Exception e) {}
			try{recordStore.closeRecordStore();}catch(Exception e) {}
//
			recordStore = RecordStore.openRecordStore("but",true);
			recordStore.setNumberofRecords(1);
			baos = new ByteArrayOutputStream();
			outputStream = new DataOutputStream(baos);
			writeButtonsMap(outputStream);
			b = baos.toByteArray();
			recordStore.addRecord(b,0,b.length);
			try{baos.flush();}catch(Exception e) {}
			try{outputStream.flush();}catch(Exception e) {}
			try{baos.close();}catch(Exception e) {}
			try{outputStream.close();}catch(Exception e) {}
			try{recordStore.closeRecordStore();}catch(Exception e) {}
		}
		catch(Exception e) {try{recordStore.closeRecordStore();}catch(Exception ee) {}}
	}

	public static boolean GetDB() {
		RecordStore recordStore = null;
		try {
			recordStore = RecordStore.openRecordStore("ST",true);
			if (recordStore.getNumRecords() > 0) {
				streets.removeAllElements();
				ByteArrayInputStream bais = new ByteArrayInputStream(recordStore.getRecord(0));
				DataInputStream inputStream = new DataInputStream(bais);
				readVectorStreets(inputStream);
				bais.close();
				inputStream.close();
			}
			else {
				return true;
			}
			try{recordStore.closeRecordStore();}catch(Exception e) {}
			//
			recordStore = RecordStore.openRecordStore("Vio",true);
			if(recordStore.getNumRecords() > 0) {
				ByteArrayInputStream bais = new ByteArrayInputStream(recordStore.getRecord(1));
				DataInputStream inputStream = new DataInputStream(bais);
				readVectorViolation(inputStream);
				bais.close();
				inputStream.close();
			}
			else {
				return true;
			}
			try{recordStore.closeRecordStore();}catch(Exception e) {}
			//
			recordStore = RecordStore.openRecordStore("L3",true);
			if(recordStore.getNumRecords() > 0) {
				ByteArrayInputStream bais = new ByteArrayInputStream(recordStore.getRecord(1));
				DataInputStream inputStream = new DataInputStream(bais);
				readVectorList3(inputStream,vehicleTypes);
				readVectorList3(inputStream,areas);
				readVectorList3(inputStream,remarks);
				readVectorList3(inputStream,citizenRemarks);
				bais.close();
				inputStream.close();
			}
			else {
				return true;
			}
			try{recordStore.closeRecordStore();}catch(Exception e) {}
			//
			recordStore = RecordStore.openRecordStore("L2",true);
			if(recordStore.getNumRecords() > 0) {
				ByteArrayInputStream bais = new ByteArrayInputStream(recordStore.getRecord(1));
				DataInputStream inputStream = new DataInputStream(bais);
				readVectorList2(inputStream,vehicleManufacturers);
				readVectorList2(inputStream,vehicleColors);
				readVectorList2(inputStream,cancelRemarks);
				bais.close();
				inputStream.close();
			}
			else {
				return true;
			}
			try{recordStore.closeRecordStore();}catch(Exception e) {}
			//
			recordStore = RecordStore.openRecordStore("L2s",true);
			if(recordStore.getNumRecords() > 0) {
				ByteArrayInputStream bais = new ByteArrayInputStream(recordStore.getRecord(1));
				DataInputStream inputStream = new DataInputStream(bais);
				ReadVectorList2s(inputStream,remarksToViolations);
				bais.close();
				inputStream.close();
			}	
			else {
				return true;
			}
			try{recordStore.closeRecordStore();}catch(Exception e) {}
			//
			recordStore = RecordStore.openRecordStore("AC",true);
			if(recordStore.getNumRecords() > 0) {
				ByteArrayInputStream bais = new ByteArrayInputStream(recordStore.getRecord(1));
				DataInputStream inputStream = new DataInputStream(bais);
				ReadAuthorityCharacteristic(inputStream);
				bais.close();
				inputStream.close();
			}	
			else {
				return true;
			}
			try{recordStore.closeRecordStore();}catch(Exception e) {}
			//
			recordStore = RecordStore.openRecordStore("TD",true);
			if(recordStore.getNumRecords() > 0) {
				ByteArrayInputStream bais = new ByteArrayInputStream(recordStore.getRecord(1));
				DataInputStream inputStream = new DataInputStream(bais);
				ReadTrialDates(inputStream,trialDates);
				bais.close();
				inputStream.close();
			}   
			else {
				return true;
			}
			try{recordStore.closeRecordStore();}catch(Exception e) {}
//			
			recordStore = RecordStore.openRecordStore("EW",true);
			if(recordStore.getNumRecords() > 0) {
				ByteArrayInputStream bais = new ByteArrayInputStream(recordStore.getRecord(1));
				DataInputStream inputStream = new DataInputStream(bais);
				readExistingWarnings(inputStream,existingWarnings);
				bais.close();
				inputStream.close();
			}   
			else {
				return true;
			}
			try{recordStore.closeRecordStore();}catch(Exception e) {}
			//
			recordStore = RecordStore.openRecordStore("EWTH",true);
			if(recordStore.getNumRecords() > 0) {
				ByteArrayInputStream bais = new ByteArrayInputStream(recordStore.getRecord(1));
				DataInputStream inputStream = new DataInputStream(bais);
				readExistingWarnings(inputStream,pastToHandleEvents);
				bais.close();
				inputStream.close();
			}   
			else {
				return true;
			}
			try{recordStore.closeRecordStore();}catch(Exception e) {}
			//
			recordStore = RecordStore.openRecordStore("IC",true);
			if(recordStore.getNumRecords() > 0) {
				ByteArrayInputStream bais = new ByteArrayInputStream(recordStore.getRecord(1));
				DataInputStream inputStream = new DataInputStream(bais);
				readInsideCodes(inputStream,insideCodes);
				bais.close();
				inputStream.close();
			}
			else  {
				return true;
			}
			try{recordStore.closeRecordStore();}catch(Exception e) {}
			//
			recordStore = RecordStore.openRecordStore("NbT",true);
			if(recordStore.getNumRecords() > 0) {
				ByteArrayInputStream bais = new ByteArrayInputStream(recordStore.getRecord(1));
				DataInputStream inputStream = new DataInputStream(bais);
				readNotebookTypes(inputStream,notebookTypes);
				bais.close();
				inputStream.close();
			}   
			else {
				return true;
			}
			try{recordStore.closeRecordStore();}catch(Exception e) {}
//
			recordStore = RecordStore.openRecordStore("NiT",true);
			if(recordStore.getNumRecords() > 0) {
				ByteArrayInputStream bais = new ByteArrayInputStream(recordStore.getRecord(1));
				DataInputStream inputStream = new DataInputStream(bais);
				readNotebookTypes(inputStream,nimhans);
				bais.close();
				inputStream.close();
			}   
			else {
				return true;
			}
			try{recordStore.closeRecordStore();}catch(Exception e) {}
//
			recordStore = RecordStore.openRecordStore("but",true);
			if(recordStore.getNumRecords() > 0) {
				ByteArrayInputStream bais = new ByteArrayInputStream(recordStore.getRecord(1));
				DataInputStream inputStream = new DataInputStream(bais);
				readButtonsMap(inputStream);
				bais.close();
				inputStream.close();
			}   
			else {
				return true;
			}
			try{recordStore.closeRecordStore();}catch(Exception e) {}
		}   
		catch(Exception e)  {
			try {
				recordStore.closeRecordStore();
				return true;
			}   
			catch(Exception ee)  {}
		}
		return false;
	}

/*
	public static void getReloginInformation(boolean allSetup) {
		RecordStore recordStore;
		try {
			recordStore = RecordStore.openRecordStore("ReloginInformation", true);
			if (recordStore.getNumRecords() > 0) {
				ByteArrayInputStream bais = new ByteArrayInputStream(recordStore.getRecord(1));
				DataInputStream inputStream = new DataInputStream(bais);
				UCApp.parkingAllowed = inputStream.readBoolean();
				UCApp.generalAllowed = inputStream.readBoolean();
				UCApp.managementAllowed = inputStream.readBoolean();
				MainActivity.fixAvera = inputStream.readBoolean();
				UCApp.parkingQRSwWork = inputStream.readBoolean();
				UCApp.generalQRSwWork = inputStream.readBoolean();
				UCApp.managementQRSwWork = inputStream.readBoolean();
				if(allSetup) {
					int length = inputStream.readInt();
					UCApp.streetsReference.removeAllElements();
					for (int i = 0; i < length; i++)
						UCApp.streetsReference.add(Integer.valueOf(inputStream.readInt()));
					UCApp.shiftHoursLength = inputStream.readInt();
					NotebookSlidingTabContentFragment.mCreateFlag = inputStream.readBoolean();
					NotebookSlidingTabContentFragment.mShowBikoret = inputStream.readBoolean();
					MainActivity.pageLocationSavedStreetBetweenActivations = inputStream.readUTF();
					MainActivity.pageLocationConnectedStreetName = inputStream.readUTF();
					MainActivity.pageLocationSaveStreetNumber = inputStream.readUTF();
					if (MainActivity.pageLocationSaveStreetNumber != null && MainActivity.pageLocationSaveStreetNumber.equals("MainActivity.pageLocationSaveStreetNumber"))
						MainActivity.pageLocationSaveStreetNumber = null;
					MainActivity.pageLocationSaveStreetByAcrossIn = inputStream.readByte();
					MainActivity.pageLocationSaveSelectedItemLocation = inputStream.readInt();
					MainActivity.pageAveraViolationTypeWasChosen = inputStream.readBoolean();
					MainActivity.pageAveraSelectedItemAveraType = inputStream.readShort();
					MainActivity.pageAveraSaveAutoCompleteTextViewAverotAsString = inputStream.readUTF();
					if (MainActivity.pageAveraSaveAutoCompleteTextViewAverotAsString != null && MainActivity.pageAveraSaveAutoCompleteTextViewAverotAsString.equals("MainActivity.pageAveraSaveAutoCompleteTextViewAverotAsString"))
						MainActivity.pageAveraSaveAutoCompleteTextViewAverotAsString = null;
					MainActivity.pageAveraViolationObject = null;
					int code = inputStream.readInt();
					for (int i = 0; i < DB.allViolations.size(); i++) {
						if (DB.allViolations.get(i).C == code) {
							MainActivity.pageAveraViolationObject = DB.allViolations.get(i);
							break;
						}
					}
					for (int i = 0; i < UCApp.streets.size(); i++) {
						if (MainActivity.pageLocationSavedStreetBetweenActivations.equals(UCApp.streets.get(i))) {
							if (UCApp.streetsReference.get(i) < DB.streets.size()) {
								MainActivity.pageLocationSaveStreetObject = DB.streets.get(UCApp.streetsReference.get(i));
							}
							break;
						}
					}
				}
				bais.close();
				inputStream.close();
			}
		}
		catch(Exception e) {}
	}


	public static void saveReloginInformation() {
		SaveDBVariable();
		RecordStore recordStore = null;
		ByteArrayOutputStream baos = null;
		DataOutputStream outputStream = null;
		byte[] b;
		try {RecordStore.deleteRecordStore("ReloginInformation");}catch(Exception e) {}
		try {
			recordStore = RecordStore.openRecordStore("ReloginInformation",true);
			recordStore.setNumberofRecords(1);
			baos = new ByteArrayOutputStream();
			outputStream = new DataOutputStream(baos);
			outputStream.writeBoolean(UCApp.parkingAllowed);
			outputStream.writeBoolean(UCApp.generalAllowed);
			outputStream.writeBoolean(UCApp.managementAllowed);
			if(MainActivity.fixAveraOnceForRequery)
				outputStream.writeBoolean(false);
			else
				outputStream.writeBoolean(MainActivity.fixAvera);
			outputStream.writeBoolean(UCApp.parkingQRSwWork);
			outputStream.writeBoolean(UCApp.generalQRSwWork);
			outputStream.writeBoolean(UCApp.managementQRSwWork);
//
			outputStream.writeInt(UCApp.streetsReference.size());
			for(int i = 0;i < UCApp.streetsReference.size();i++)
				outputStream.writeInt(UCApp.streetsReference.get(i).intValue());
			outputStream.writeInt(UCApp.shiftHoursLength);
			outputStream.writeBoolean(NotebookSlidingTabContentFragment.mCreateFlag);
			outputStream.writeBoolean(NotebookSlidingTabContentFragment.mShowBikoret);
			outputStream.writeUTF(MainActivity.pageLocationSavedStreetBetweenActivations);
			outputStream.writeUTF(MainActivity.pageLocationConnectedStreetName);
			if(MainActivity.pageLocationSaveStreetNumber == null)
				outputStream.writeUTF("MainActivity.pageLocationSaveStreetNumber");
			else
				outputStream.writeUTF(MainActivity.pageLocationSaveStreetNumber);
			outputStream.writeByte(MainActivity.pageLocationSaveStreetByAcrossIn);
			outputStream.writeInt(MainActivity.pageLocationSaveSelectedItemLocation);
			outputStream.writeBoolean(MainActivity.pageAveraViolationTypeWasChosen);
			outputStream.writeShort(MainActivity.pageAveraSelectedItemAveraType);
			if(MainActivity.pageAveraSaveAutoCompleteTextViewAverotAsString == null)
				outputStream.writeUTF("MainActivity.pageAveraSaveAutoCompleteTextViewAverotAsString");
			else
				outputStream.writeUTF(MainActivity.pageAveraSaveAutoCompleteTextViewAverotAsString);
			if(MainActivity.pageAveraViolationObject == null)
				outputStream.writeInt(-1);
			else
				outputStream.writeInt(MainActivity.pageAveraViolationObject.C);
			b = baos.toByteArray();
			recordStore.addRecord(b,0,b.length);
		}
		catch(Exception e) {}
		try{baos.flush();}catch(Exception e) {}
		try{outputStream.flush();}catch(Exception e) {}
		try{baos.close();}catch(Exception e) {}
		try{outputStream.close();}catch(Exception e) {}
		try{recordStore.closeRecordStore();}catch(Exception e) {}
	}
*/
	public static void readCitiesDB() {
		new Thread() { public void run() {
			try {
				cities.clear();
				InputStream inputStream = RecordStore.context.getAssets().open("cities.db");
				ObjectInput objectInput = new ObjectInputStream(inputStream);
				int numberOfCities = objectInput.readInt();
				for(int c = 0;c < numberOfCities;c++) {
					String city = objectInput.readUTF();
					int numberOfStreets = objectInput.readInt();
					Vector<String> vectorShortStreet = new Vector<String>(numberOfStreets);
					for(int s = 0;s < numberOfStreets;s++) {
						String street = objectInput.readUTF().trim();
						vectorShortStreet.add(street);
//						Log.i(TAG,city+":"+street);
					}
					cities.put(city,vectorShortStreet);
				}
				objectInput.close();
				inputStream.close();
			}
			catch(Exception e) {}
		}}.start();
	}
}
