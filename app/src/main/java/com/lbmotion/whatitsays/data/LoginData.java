package com.lbmotion.whatitsays.data;

import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;

public class LoginData {
	private final static String TAG = "LoginData";
	public static String theFlashAuthority = "";

	public Vector<Doch> parkingDochot = new Vector<Doch>();
	public Vector<Doch> generalDochot = new Vector<Doch>();
	public Vector<Doch> remarkDochot = new Vector<Doch>();
	public Vector<Doch> managementDochot = new Vector<Doch>();
	public String username = "";
	public String password = "";
	public String authority = "";
	public int    	 		Lk = 0;
	public int     			UsrCounter = 0;
	public int     			PakachC = 0;
	public int     			PakachKod = 0;
	public short   			DefaultES = -1;
	public int     			DochPanui = 0;
	public int     			DochToDay = 0;
	public int     			reload = 0;
	public int     			CarNoTypeNo = 0;//Number of time to input Car number
	public int     			login = 0;	
	public String PakachNm = "";
	public String ServerDate = "";
	public byte    			typePakach;
	public String EzorWork = "";
	public boolean 			reloadData;	
	public boolean 			error = true;
	public long 			loginTime = 0;
	public long 			loginTimeHour = 0;	
	public int     			lastParkingDochNo;
	public byte    			lastParkingSidra;
	public int     			lastGeneralDochNo;
	public byte    			lastGeneralSidra;
	public int     			lastManagementDochNo;
	public byte    			lastManagementSidra;
	public short   			DefaultE = -1;
	public boolean 			openFailed = true;
	public short   			tracking = 0;
	public String SwNotActive = "0";
	public String AbsenceRason = "";
	public boolean 			timeError = false;
	public boolean			ALPR = true;
	//
	public static LoginData restore() {
		LoginData ld = null;
		RecordStore recordStore = null;
		try {
			recordStore = RecordStore.openRecordStore("LoginData",true);
			if(recordStore.getNumRecords() > 0) {
				ld = new LoginData();
				ByteArrayInputStream bais = new ByteArrayInputStream(recordStore.getRecord(1));
				DataInputStream inputStream = new DataInputStream(bais);
				ld.Lk = inputStream.readInt();
				ld.UsrCounter = inputStream.readInt();
				ld.PakachC = inputStream.readInt();
				ld.PakachKod = inputStream.readInt();
				ld.PakachNm = inputStream.readUTF();
				ld.DefaultE = inputStream.readShort();
				ld.DefaultES = inputStream.readShort();
				ld.DochPanui = inputStream.readInt();
				ld.DochPanui = inputStream.readInt();
				ld.DochToDay = inputStream.readInt();
				ld.reload = inputStream.readInt();
				ld.login = inputStream.readInt();
				ld.ServerDate = inputStream.readUTF();
				ld.CarNoTypeNo = inputStream.readInt();
				ld.username = inputStream.readUTF();
				ld.password = inputStream.readUTF();
				theFlashAuthority = ld.authority = inputStream.readUTF();
				for(short i = inputStream.readShort();i > 0;i--) {
					Doch doch = new Doch();
					doch.code = inputStream.readInt();
					doch.dochNumber = inputStream.readInt();
					doch.sidra = inputStream.readByte();
					doch.bikoret = inputStream.readByte();
					doch.swHazara  = inputStream.readByte();
					doch.swSug  = inputStream.readByte();
					ld.parkingDochot.addElement(doch);
				}
				for(short i = inputStream.readShort();i > 0;i--) {
					Doch doch = new Doch();
					doch.code = inputStream.readInt();
					doch.dochNumber = inputStream.readInt();
					doch.sidra = inputStream.readByte();
					doch.bikoret = inputStream.readByte();
					doch.swHazara  = inputStream.readByte();
					doch.swSug  = inputStream.readByte();
					ld.generalDochot.addElement(doch);
				}
				for(short i = inputStream.readShort();i > 0;i--) {
					Doch doch = new Doch();
					doch.code = inputStream.readInt();
					doch.dochNumber = inputStream.readInt();
					doch.sidra = inputStream.readByte();
					doch.bikoret = inputStream.readByte();
					doch.swHazara  = inputStream.readByte();
					doch.swSug  = inputStream.readByte();
					ld.remarkDochot.addElement(doch);
				}
				for(short i = inputStream.readShort();i > 0;i--) {
					Doch doch = new Doch();
					doch.code = inputStream.readInt();
					doch.dochNumber = inputStream.readInt();
					doch.sidra = inputStream.readByte();
					doch.bikoret = inputStream.readByte();
					doch.swHazara  = inputStream.readByte();
					doch.swSug  = inputStream.readByte();
					ld.managementDochot.addElement(doch);
				}
				ld.typePakach = inputStream.readByte();
				ld.EzorWork = inputStream.readUTF();
				ld.loginTime = inputStream.readLong();
				ld.loginTimeHour = inputStream.readLong();
				ld.tracking = inputStream.readShort();
				ld.ALPR = inputStream.readBoolean();
			}
		}
		catch(Exception e) {
			Log.i(TAG, "Get()"+e.getMessage());
		}
		try{recordStore.closeRecordStore();}catch(Exception e) {}
		return ld;
	}

	public void save() {
		RecordStore recordStore = null;
		try {
			RecordStore.deleteRecordStore("LoginData");
			recordStore = RecordStore.openRecordStore("LoginData",true);
			recordStore.setNumberofRecords(1);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			DataOutputStream outputStream = new DataOutputStream(baos);
			outputStream.writeInt(Lk);
			outputStream.writeInt(UsrCounter);
			outputStream.writeInt(PakachC);
			outputStream.writeInt(PakachKod);
			outputStream.writeUTF(PakachNm);
			outputStream.writeShort(DefaultE);
			outputStream.writeShort(DefaultES);
			outputStream.writeInt(DochPanui);
			outputStream.writeInt(DochPanui);
			outputStream.writeInt(DochToDay);
			outputStream.writeInt(reload);
			outputStream.writeInt(login);
			outputStream.writeUTF(ServerDate);
			outputStream.writeInt(CarNoTypeNo);
			outputStream.writeUTF(username);
			outputStream.writeUTF(password);
			outputStream.writeUTF(authority);
			outputStream.writeShort(parkingDochot.size());
			for(short i = 0;i < parkingDochot.size();i++) {
				outputStream.writeInt(parkingDochot.elementAt(i).code);
				outputStream.writeInt(parkingDochot.elementAt(i).dochNumber);
				outputStream.writeByte(parkingDochot.elementAt(i).sidra);
				outputStream.writeByte(parkingDochot.elementAt(i).bikoret);
				outputStream.writeByte(parkingDochot.elementAt(i).swHazara);
				outputStream.writeByte(parkingDochot.elementAt(i).swSug);
			}
			outputStream.writeShort(generalDochot.size());
			for(short i = 0;i < generalDochot.size();i++) {
				outputStream.writeInt(generalDochot.elementAt(i).code);
				outputStream.writeInt(generalDochot.elementAt(i).dochNumber);
				outputStream.writeByte(generalDochot.elementAt(i).sidra);
				outputStream.writeByte(generalDochot.elementAt(i).bikoret);
				outputStream.writeByte(generalDochot.elementAt(i).swHazara);
				outputStream.writeByte(generalDochot.elementAt(i).swSug);
			}
			outputStream.writeShort(remarkDochot.size());
			for(short i = 0;i < remarkDochot.size();i++) {
				outputStream.writeInt(remarkDochot.elementAt(i).code);
				outputStream.writeInt(remarkDochot.elementAt(i).dochNumber);
				outputStream.writeByte(remarkDochot.elementAt(i).sidra);
				outputStream.writeByte(remarkDochot.elementAt(i).bikoret);
				outputStream.writeByte(remarkDochot.elementAt(i).swHazara);
				outputStream.writeByte(remarkDochot.elementAt(i).swSug);
			}
			outputStream.writeShort(managementDochot.size());
			for(short i = 0;i < managementDochot.size();i++) {
				outputStream.writeInt(managementDochot.elementAt(i).code);
				outputStream.writeInt(managementDochot.elementAt(i).dochNumber);
				outputStream.writeByte(managementDochot.elementAt(i).sidra);
				outputStream.writeByte(managementDochot.elementAt(i).bikoret);
				outputStream.writeByte(managementDochot.elementAt(i).swHazara);
				outputStream.writeByte(managementDochot.elementAt(i).swSug);
			}
			outputStream.writeByte(typePakach);
			outputStream.writeUTF(EzorWork);
			outputStream.writeLong(loginTime);
			outputStream.writeLong(loginTimeHour);
			outputStream.writeShort(tracking);
			outputStream.writeBoolean(ALPR);
			byte[] b = baos.toByteArray();
			recordStore.addRecord(b,0,b.length);
		}
		catch(Exception e) {}
		try{recordStore.closeRecordStore();}catch(Exception e) {}
	}

	public void clearDochot() {
		parkingDochot.removeAllElements();
		generalDochot.removeAllElements();
		remarkDochot.removeAllElements();
		managementDochot.removeAllElements();
	}

	public void addDoch(int Code, int DochNumber, byte Sidra,byte Bikoret, byte SwHazara, byte SwSug) {
		Doch doch = new Doch();
		doch.code = Code;
		doch.dochNumber = DochNumber;
		doch.sidra = Sidra;
		doch.bikoret = Bikoret;
		doch.swHazara = SwHazara;
		doch.swSug = SwSug;
		if(SwSug == 3) {
			for(Doch d : managementDochot) {
				if(d.code == Code && d.dochNumber == DochNumber)
					return;
			}
			managementDochot.addElement(doch);
		}
		else if(SwSug == 2) {
			for(Doch d : remarkDochot) {
				if(d.code == Code && d.dochNumber == DochNumber)
					return;
			}
			remarkDochot.addElement(doch);
		}
		else if(SwSug == 1) {
			for(Doch d : generalDochot) {
				if(d.code == Code && d.dochNumber == DochNumber)
					return;
			}
			generalDochot.addElement(doch);
		}
		else {
			for(Doch d : parkingDochot) {
				if(d.code == Code && d.dochNumber == DochNumber)
					return;
			}
			parkingDochot.addElement(doch);
		}
	}

	public short available(boolean isGeneral,boolean azhara,boolean event,boolean management,int violationListCode) {
		boolean onlyRealDocs = false;
		for(Violation v : DB.allViolations) {
			if(v.C == violationListCode) {
				onlyRealDocs = v.hasVirtualDoc;
				break;
			}
		}
		return available(isGeneral,azhara,event,management,onlyRealDocs);
	}

	public short available(boolean isGeneral,boolean azhara,boolean event,boolean management,boolean onlyRealDocs) {
		if(management && !event) {
			if(DB.authorityCharacteristic.PinkasWarningReport && onlyRealDocs && azhara)
				return (short)remarkDochot.size();
			else
				return (short) managementDochot.size();
		}
		else if(isGeneral && azhara) {
			if(DB.authorityCharacteristic.PinkasWarningReport && onlyRealDocs)
				return (short)remarkDochot.size();
			else
				return (short)generalDochot.size();
		}
		else if(isGeneral && event && onlyRealDocs) {
			return (short)remarkDochot.size();
		}
		else if(isGeneral) {
			return (short)generalDochot.size();
		}
		else {
			return (short)parkingDochot.size();
		}
	}

	public String getDoch(int i, boolean isGeneral, boolean isManagement) {
		if(isGeneral) {
			if(generalDochot.size() > i) 
				return generalDochot.elementAt(i).dochNumber+"-"+generalDochot.elementAt(i).sidra;
			else
				return "";
		}
		else if(isManagement) {
			if(managementDochot.size() > i)
				return managementDochot.elementAt(i).dochNumber+"-"+managementDochot.elementAt(i).sidra;
			else
				return "";			
		}
		else {
			if(parkingDochot.size() > i) 
				return parkingDochot.elementAt(i).dochNumber+"-"+parkingDochot.elementAt(i).sidra;
			else
				return "";
		}
	}

	public int getDochNumber(boolean isGeneral,boolean azhara,boolean event,boolean management,int violationListCode) {
		boolean onlyRealDocs = false;
		for(Violation v : DB.allViolations) {
			if(v.C == violationListCode) {
				onlyRealDocs = v.hasVirtualDoc;
				break;
			}
		}
		return getDochNumber(isGeneral,azhara,event,management,onlyRealDocs);
	}

	public int getDochNumber(boolean isGeneral,boolean azhara,boolean event,boolean management,boolean onlyRealDocs) {
		if(management && !event) {
			if(DB.authorityCharacteristic.PinkasWarningReport && onlyRealDocs && azhara) {
				if(remarkDochot != null && remarkDochot.size() > 0)
					return remarkDochot.elementAt(0).dochNumber;
				else
					return -1;
			}
			else {
				if (managementDochot.size() > 0)
					return managementDochot.elementAt(0).dochNumber;
				else
					return -1;
			}
		}
		else if(isGeneral && azhara) {
			if(DB.authorityCharacteristic.PinkasWarningReport && onlyRealDocs) {
				if(remarkDochot != null && remarkDochot.size() > 0)
					return remarkDochot.elementAt(0).dochNumber;
				else
					return -1;
			}
			else {
				if(generalDochot.size() > 0) 
					return generalDochot.elementAt(0).dochNumber;
				else
					return -1;				
			}
		}
		else if(isGeneral && event && onlyRealDocs) {
			if(remarkDochot != null && remarkDochot.size() > 0)
				return (remarkDochot.elementAt(0)).dochNumber;
			else return -1;
		}
		else if(isGeneral) {
			if(generalDochot.size() > 0) 
				return generalDochot.elementAt(0).dochNumber;
			else
				return -1;
		}
		else {
			if(parkingDochot.size() > 0) 
				return parkingDochot.elementAt(0).dochNumber;
			else
				return -1;
		}
	}


	public byte getSidra(boolean isGeneral,boolean azhara,boolean event,boolean management,int violationListCode) {
		boolean onlyRealDocs = false;
		for(Violation v : DB.allViolations) {
			if(v.C == violationListCode) {
				onlyRealDocs = v.hasVirtualDoc;
				break;
			}
		}
		return getSidra(isGeneral,azhara,event,management,onlyRealDocs);
	}

	public byte getSidra(boolean isGeneral,boolean azhara,boolean event,boolean management,boolean onlyRealDocs) {
		if(management && !event) {
			if(DB.authorityCharacteristic.PinkasWarningReport && onlyRealDocs && azhara) {
				if(remarkDochot != null && remarkDochot.size() > 0)
					return remarkDochot.elementAt(0).sidra;
				else
					return (byte)-1;
			}
			else {
				if (managementDochot.size() > 0)
					return managementDochot.elementAt(0).sidra;
				else
					return (byte) -1;
			}
		}
		else if(isGeneral && azhara) {
			if(DB.authorityCharacteristic.PinkasWarningReport && onlyRealDocs) {
				if(remarkDochot != null && remarkDochot.size() > 0)
					return remarkDochot.elementAt(0).sidra;
				else
					return (byte)-1;
			}
			else {
				if(generalDochot.size() > 0) 
					return generalDochot.elementAt(0).sidra;
				else
					return (byte)-1;				
			}
		}
		else if(isGeneral && event && onlyRealDocs) {
			if(remarkDochot != null && remarkDochot.size() > 0)
				return remarkDochot.elementAt(0).sidra;
			else
				return (byte)-1;
		}
		else if(isGeneral) {
			if(generalDochot.size() > 0) 
				return generalDochot.elementAt(0).sidra;
			else
				return (byte)-1;
		}
		else {
			if(parkingDochot.size() > 0)
				return parkingDochot.elementAt(0).sidra;
			else
				return (byte)-1;
		}
	}

	public byte getBikoret(boolean isGeneral,boolean azhara,boolean event,boolean management,int violationListCode) {
		boolean onlyRealDocs = false;
		for(Violation v : DB.allViolations) {
			if(v.C == violationListCode) {
				onlyRealDocs = v.hasVirtualDoc;
				break;
			}
		}
		return getBikoret(isGeneral,azhara,event,management,onlyRealDocs);
	}

	public byte getBikoret(boolean isGeneral,boolean azhara,boolean event,boolean management,boolean onlyRealDocs) {
		if(management && !event) {
			if(DB.authorityCharacteristic.PinkasWarningReport && onlyRealDocs && azhara) {
				if(remarkDochot != null && remarkDochot.size() > 0)
					return remarkDochot.elementAt(0).bikoret;
				else
					return (byte)-1;
			}
			else {
				if (managementDochot.size() > 0)
					return managementDochot.elementAt(0).bikoret;
				else
					return (byte) -1;
			}
		}
		else if(isGeneral && azhara) {
			if(DB.authorityCharacteristic.PinkasWarningReport && onlyRealDocs) {
				if(remarkDochot != null && remarkDochot.size() > 0)
					return remarkDochot.elementAt(0).bikoret;
				else                  
					return (byte)-1;
			}
			else {
				if(generalDochot.size() > 0) 
					return generalDochot.elementAt(0).bikoret;
				else                  
					return (byte)-1;				
			}
		}
		else if(isGeneral && event && onlyRealDocs) {
			if(remarkDochot != null && remarkDochot.size() > 0)
				return remarkDochot.elementAt(0).bikoret;
			else                  
				return (byte)-1;
		}
		else if(isGeneral) {
			if(generalDochot.size() > 0) 
				return generalDochot.elementAt(0).bikoret;
			else                  
				return (byte)-1;
		}
		else {
			if(parkingDochot.size() > 0)
				return parkingDochot.elementAt(0).bikoret;
			else                
				return (byte)-1;
		}
	}

	public byte getSwHazara(boolean isGeneral,boolean azhara,boolean event,boolean management,int violationListCode) {
		boolean onlyRealDocs = false;
		for(Violation v : DB.allViolations) {
			if(v.C == violationListCode) {
				onlyRealDocs = v.hasVirtualDoc;
				break;
			}
		}
		return getSwHazara(isGeneral,azhara,event,management,onlyRealDocs);
	}

	public byte getSwHazara(boolean isGeneral,boolean azhara,boolean event,boolean management,boolean onlyRealDocs) {
		if(management && !event) {
			if(DB.authorityCharacteristic.PinkasWarningReport && onlyRealDocs && azhara) {
				if(remarkDochot != null && remarkDochot.size() > 0)
					return remarkDochot.elementAt(0).swHazara;
				else
					return (byte)-1;
			}
			else {
				if (managementDochot.size() > 0)
					return managementDochot.elementAt(0).swHazara;
				else
					return (byte) -1;
			}
		}
		else if(isGeneral && azhara) {
			if(DB.authorityCharacteristic.PinkasWarningReport && onlyRealDocs) {
				if(remarkDochot != null && remarkDochot.size() > 0)
					return remarkDochot.elementAt(0).swHazara;
				else                
					return (byte)-1;
			}
			else {
				if(generalDochot.size() > 0) 
					return generalDochot.elementAt(0).swHazara;
				else                
					return (byte)-1;				
			}
		}
		else if(isGeneral && event && onlyRealDocs) {
			if(remarkDochot != null && remarkDochot.size() > 0)
				return remarkDochot.elementAt(0).swHazara;
			else                
				return (byte)-1;			
		}
		else if(isGeneral) {
			if(generalDochot.size() > 0) 
				return generalDochot.elementAt(0).swHazara;
			else                
				return (byte)-1;
		}
		else {
			if(parkingDochot.size() > 0)
				return parkingDochot.elementAt(0).swHazara;
			else                       
				return (byte)-1;
		}
	}

	public int getCode(boolean isGeneral,boolean azhara,boolean event,boolean management,int violationListCode) {
		boolean onlyRealDocs = false;
		for (Violation v : DB.allViolations) {
			if (v.C == violationListCode) {
				onlyRealDocs = v.hasVirtualDoc;
				break;
			}
		}
		return getCode(isGeneral, azhara, event, management, onlyRealDocs);
	}

	public int getCode(boolean isGeneral,boolean azhara,boolean event,boolean management,boolean onlyRealDocs) {
		if(management && !event) {
			if(DB.authorityCharacteristic.PinkasWarningReport && onlyRealDocs && azhara) {
				if(remarkDochot != null && remarkDochot.size() > 0)
					return remarkDochot.elementAt(0).code;
				else
					return -1;
			}
			else {
				if (managementDochot.size() > 0)
					return managementDochot.elementAt(0).code;
				else
					return -1;
			}
		}
		else if(isGeneral && azhara) {
			if(DB.authorityCharacteristic.PinkasWarningReport && onlyRealDocs) {
				if(remarkDochot != null && remarkDochot.size() > 0)
					return remarkDochot.elementAt(0).code;
				else                
					return -1;
			}
			else {
				if(generalDochot.size() > 0) 
					return generalDochot.elementAt(0).code;
				else                
					return -1;				
			}
		}
		else if(isGeneral && event && onlyRealDocs) {
			if(remarkDochot != null && remarkDochot.size() > 0)
				return remarkDochot.elementAt(0).code;
			else                
				return -1;			
		}
		else if(isGeneral) {
			if(generalDochot.size() > 0) 
				return generalDochot.elementAt(0).code;
			else                
				return -1;
		}
		else {
			if(parkingDochot.size() > 0)
				return parkingDochot.elementAt(0).code;
			else                 
				return -1;
		}
	}

	public boolean removeDoch(boolean isGeneral, boolean azhara, boolean event, boolean management, int violationListCode) {
		boolean onlyRealDocs = false;
		for(Violation v : DB.allViolations) {
			if(v.C == violationListCode) {
				onlyRealDocs = v.hasVirtualDoc;
				break;
			}
		}
		return removeDoch(isGeneral,azhara,event,management,onlyRealDocs);
	}

	private boolean removeDoch(boolean isGeneral, boolean azhara, boolean event, boolean management, boolean onlyRealDocs) {
		boolean foundIt = false;
		if(management && !event) {
			if(DB.authorityCharacteristic.PinkasWarningReport && onlyRealDocs && azhara) {
					if(remarkDochot != null && remarkDochot.size() > 0) {
						remarkDochot.removeElementAt(0);
						foundIt = true;
					}
			}
			else {
				if (managementDochot.size() > 0) {
					managementDochot.removeElementAt(0);
					foundIt = true;
				}
			}
		}
		else if(isGeneral && azhara) {
			if(DB.authorityCharacteristic.PinkasWarningReport && onlyRealDocs) {
				if(remarkDochot != null && remarkDochot.size() > 0) {
					remarkDochot.removeElementAt(0);
					foundIt = true;
				}
			}
			else {
				if(generalDochot.size() > 0) {
					generalDochot.removeElementAt(0);
					foundIt = true;
				}
			}
		}
		else if(isGeneral && event && onlyRealDocs) {
			if(remarkDochot != null && remarkDochot.size() > 0) {
				remarkDochot.removeElementAt(0);
				foundIt = true;
			}
		}
		else if(isGeneral) {
			if(generalDochot.size() > 0) {
				generalDochot.removeElementAt(0);
				foundIt = true;
			}
		}   
		else {
			if(parkingDochot.size() > 0) {
				parkingDochot.removeElementAt(0);
				foundIt = true;
			}
		}
		if(foundIt) {
			save();
		}
		return foundIt;
	}

	public void popAndSaveUpTo(int length, Vector<Doch> docs) {
		Doch doch = docs.elementAt(length);
		Collections.sort(docs, new Comparator<Doch>() {
			@Override
			public int compare(Doch o1, Doch o2) {
				int r = o1.dochNumber-o2.dochNumber;
				if(r != 0)
					return r;
				return (int)o1.sidra-(int)o2.sidra;
			}           
		});
		for(int i = 0;i < docs.size();i++) {
			if(doch.code == docs.get(i).code) {
				length = i;
				break;
			}
		}
		for(int i = 0;i < length;i++) {
			doch = docs.elementAt(0);
			docs.removeElementAt(0);
			docs.addElement(doch);
		}
	}
	
	public void popAndSaveUpTo(int length, boolean isGeneral,boolean isManagement) {
		if(isManagement)
			popAndSaveUpTo(length, managementDochot);
		else if(isGeneral)
			popAndSaveUpTo(length, generalDochot);
		else
			popAndSaveUpTo(length, parkingDochot);
		save();
	}
	
	public static class Doch {
		public Doch() {}
		public int   code;
		public int   dochNumber;
		public byte  sidra;
		public byte  bikoret;
		public byte  swHazara;
		public byte  swSug;
	}
}
