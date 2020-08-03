package com.lbmotion.whatitsays.data;

import android.util.Log;

import com.lbmotion.whatitsays.MainActivity;
import com.lbmotion.whatitsays.UCApp;
import com.lbmotion.whatitsays.common.BaseActivity;
import com.lbmotion.whatitsays.managers.SendTicketsAndPictures;
import com.lbmotion.whatitsays.util.Util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Date;
import java.util.UUID;
import java.util.Vector;

public class TicketInformation {
	private static final String TAG = "TicketInformation";
	public boolean sendTicketAfterShotingPicture = false;
	private static Object syncAccees = new Object();
	public static String authority = "";
	public boolean isPrevious = false;
	public boolean isCanceled = false;
	public String filename = "";
	public long    timestamp = (new Date()).getTime();
	public long    timestampFile = -1;
	public String userid = "";
	public int     UserCounter = -1;
	public int     LK = -1;
	public String doch = "";
	public int     dochCode = -1;
	public int     streetCode = 0;            	//Step1
	public int     lastStreetCode = 0;            	//Step1
	public String streetName = "";			//Just for display
	public String lastStreetName = "";			//Just for display
	public int     CS = -1;
	public short   reasonCode = 0;
	public String streetNumber = "";
	public byte    streetByAcrossIn = 0;
	public int     violationListCode = 999999;
	public short   D = 0;
	public int     P = 0;
	public String DochKod = "0";     			//The code of the previously given warning
	public short   numAzhara = 0;     			//Warning #
	public short   saveNumAzhara = 0;     			//Warning #
	public short   NumCopies = 1;
	public boolean smoking = false;
	public String CarInformationId = "";
	public int     CarInformationType = -1;
	public int     CarInformationColor = -1;
	public int     CarInformationManufacturer = -1;
	public String parkingNumber1 = "";   	//Step3
	public String parkingNumber2 = "";
	public short   idType = 0;
	public Vector<Defendant> defendants = new Vector<Defendant>();
	public Witness witness = new Witness();
	public int	   currentDefendant = -1;
	public int	   printDefendantSoFar = 0;
	public Vehicle vehicle = new Vehicle();

	public int	   warningHours = 0;
	public String warningDate = "";
	public String trialDate = "";
	public String trialHour = "";
	public short   trialC = 0;

	public int     remarkReference1 = BaseActivity.NO_REFERENCE;
	public int     remarkReference2 = BaseActivity.NO_REFERENCE;
	public int     remarkReference3 = BaseActivity.NO_REFERENCE;
	public int     remarkReference4 = BaseActivity.NO_REFERENCE;

	public boolean remarkReference1Print;
	public boolean remarkReference2Print;
	public boolean remarkReference3Print;
	public boolean remarkReference4Print;

	public int     citizenRemarkReference1 = BaseActivity.NO_REFERENCE;
	public int     citizenRemarkReference2 = BaseActivity.NO_REFERENCE;
	public int     citizenRemarkReference3 = BaseActivity.NO_REFERENCE;
	public int     citizenRemarkReference4 = BaseActivity.NO_REFERENCE;

	public Vector<String> pictures = new Vector<String>();
	public Vector<String> sounds = new Vector<String>();
	public Vector<Integer> picturesRotation = new Vector<Integer>();

	public boolean inProccess = false;
	public boolean vSwHatraa = false;
	public boolean sSwHatraa = false;
	public short   type = 0;
	public String date = "";
	public boolean carOrGM = false;
	public byte    SwHazara = 0;
	public boolean openEvent = false;
	//
	public String p_connectedStreetName = "";	//Step1
	public String p_streetName = "";
	public String p_printedDochNumber = "";
	public String p_existingWarningPrintedDochNumber = "";
	public String p_PakachNm = "";
	public String p_carInformation = "";
	public String p_violationName = "";
	public String p_days = "";
	public String p_fun = "";
	public String p_title = "";
	public String p_Remark = "";
	public boolean p_Kod_SeloRemark = false;
	//
	public String citizenFreeText = "";
	public String pakachFreeText = "";
	public String PrintKod = "";
	public String PrintMsg = "";
	public boolean citizenCheckBox = false;

	public String saveCitizenFreeText = "";
	public String savePakachFreeText = "";

	public String validUpto = "";
	public String animalId = "";
	public double  latitude = 0,longitude = 0;
	public byte    sendReportC = -1;
	public boolean printTextBox = false;
	public String subItem = "";
	public String uuid = "";
	public long    checkLincenseTime = (new Date()).getTime();
	public boolean dochBedihavad = false;
	public byte    selectedHandicapPicture = -1;
	public byte    packachDecisionCode = -1;
	public byte    packachCreateDecisionCode = -1;
	public boolean isManagement = false;
	public boolean didCrashInTheMiddle = false;
	public boolean pageCarBeforeQuery = true;
	public boolean pageGIbeforeQuery = true;
	public MainActivity.StatesTypes statesTypes = MainActivity.StatesTypes.START_NEW_DOCH;
	public short 	pageAveraSelectedItemAveraType = 0;
	public String pageAveraSaveAutoCompleteTextViewAverotAsString = "";
	public String displayCarNumber = "";
	public int 		Kod_SeloRemark = 0;
	//
	public boolean 	wanted = false;
	public boolean 	handicap = false;
	public boolean 	resident = false;
	public boolean 	doubleReporting = false;
	public boolean 	cellularParking = false;
	public byte    	wantedAction = 0;
	public byte    	handicapAction = 0;
	public byte    	residentAction = 0;
	public byte    	cellularParkingAction = 0;
	public byte    	doubleReportingAction = 0;
	public String SugTavT = "";
	public String AveraAct = "";
	public String SugTav = "";
	public String SwWarning = "";
	public String SwAveraKazach = "";
	public short 	SwKazach = 0;
	public String TxtKazach = "";
	public String TxtAzara = "";
	public short   	keshelKod = 0;
	public String keshelMsg = "";
	public byte    	leftSide = 0;
	public byte    	rightSide = 0;
	public boolean 	handicapParkingPicture = false;
	public byte 	dataVersion = 0;
	public boolean 	canBackupFile = true;
	public boolean 	carConfirmation = false;
	public boolean  didQueryServer = false;
	public String quontity = "";
	public String recipientFirstName = "";
	public String recipientLastName = "";
	public String recipientId = "";
	public String docPehula = "";
	public String SwCellParkingPaid = "";
	public byte     signaturePosition = -1;
	public Vector<String> picturesURL = null;
	public boolean 	swExistHistory = false;
	public byte		swPrintQR = 0;
	public String picUrl = "";
	public boolean 	immediate = false;
	public String TasksC = "";
	public boolean  sendNewRemark = false;
	public String gosh = "";
	public String helka = "";

	public TicketInformation() {
//		Log.i(TAG,"TicketInformation()");
	}

	public static byte[] getFileBuffer(String file) {
		InputStream is = null;
		byte [] byteArray;
		try {
			is = new FileInputStream(openFile(getPath("Data")+file));
			int length = is.available();
			byteArray = new byte[length];
			is.read(byteArray,0,length);
		}
		catch(Exception ex) {byteArray = null;}
		catch(Error e) {
			byteArray = null;
			try {if(is != null) is.close();}catch(Exception ex) {}
			is = null;
			System.gc();
			getFileBuffer(file);
		}
		try {if(is != null) is.close();}catch(Exception ex) {}
		return byteArray;
	}

	public static byte[] getPicDataFileBufferHide(String file) {
		InputStream is = null;
		byte [] byteArray;
		try {
			is = new FileInputStream(openFileHide(getPathHide("PicData")+file));
			int length = is.available();
			byteArray = new byte[length];
			is.read(byteArray,0,length);
		}
		catch(Exception ex) {byteArray = null;}
		catch(Error e) {
			byteArray = null;
			try {if(is != null) is.close();}catch(Exception ex) {}
			is = null;
			System.gc();
			getPicDataFileBufferHide(file);
		}
		try {if(is != null) is.close();}catch(Exception ex) {}
		return byteArray;
	}

	public static byte[] getPicDataFileBuffer(String file) {
		InputStream is = null;
		byte [] byteArray;
		try {
			is = new FileInputStream(openFile(getPath("PicData")+file));
			int length = is.available();
			byteArray = new byte[length];
			is.read(byteArray,0,length);
		}
		catch(Exception ex) {byteArray = null;}
		catch(Error e) {
			byteArray = null;
			try {if(is != null) is.close();}catch(Exception ex) {}
			is = null;
			System.gc();
			getPicDataFileBuffer(file);
		}
		try {if(is != null) is.close();}catch(Exception ex) {}
		return byteArray;
	}

	public static int getFileSize(String file) {
		int length = 0;
		InputStream is = null;
		try {
			is = new FileInputStream(openFile(getPath("Data")+file));
			length = is.available();
		}
		catch(Exception ex) {
			Log.i(TAG,"getFileSize():"+ ex.getMessage());
		}
		try {if(is != null) is.close();}catch(Exception ex) {}
		return length;
	}

	public static int getFilePicDataSizeHide(String file) {
		int length = 0;
		InputStream is = null;
		try {
			is = new FileInputStream(openFileHide(getPathHide("PicData")+file));
			length = is.available();
		}
		catch(Exception ex) {
			Log.i(TAG,"getFileSize():"+ ex.getMessage());
		}
		try {if(is != null) is.close();}catch(Exception ex) {}
		return length;
	}

	public static int getFilePicDataSize(String file) {
		int length = 0;
		InputStream is = null;
		try {
			is = new FileInputStream(openFile(getPath("PicData")+file));
			length = is.available();
		}
		catch(Exception ex) {
			Log.i(TAG,"getFileSize():"+ ex.getMessage());
		}
		try {if(is != null) is.close();}catch(Exception ex) {}
		return length;
	}

	public static TicketInformation getTicket(String filename) {
		TicketInformation ti = new TicketInformation();
		synchronized(syncAccees) {
			InputStream is = null;
			int length = -1;
			byte [] byteArray;
			try {
				is = new FileInputStream(openFile(getPath("Data")+filename));
				length = is.available();
				byteArray = new byte [length];
				is.read(byteArray);
				ti.getTI(byteArray);
			}
			catch(Exception ex) {
//				if(length == 0) {
//					try {if(is != null) is.close();}catch(Exception ee) {}
//					deleteFile(filename);
//				}
			}
			try {if(is != null) is.close();}catch(Exception ex) {}
		}
		return ti;
	}


	public static void deletePrintedTicketHide(String filename) {
		synchronized(syncAccees) {
			try {
				openFileHide(getPathHide("Prints")+filename).delete();
			}
			catch(Exception ex) {}
		}
	}

	public static TicketInformation getPrintedTicketHide(String filename) {
		TicketInformation ti = new TicketInformation();
		synchronized(syncAccees) {
			InputStream is = null;
			int length = -1;
			byte [] byteArray;
			try {
				is = new FileInputStream(openFileHide(getPathHide("Prints")+filename));
				length = is.available();
				byteArray = new byte [length];
				is.read(byteArray);
				ti.getTI(byteArray);
			}
			catch(Exception ex) {}
			try {if(is != null) is.close();}catch(Exception ex) {}
		}
		return ti;
	}

	public static void saveTicketPrintHide(final TicketInformation ti,final int tries) {
		OutputStream os = null;
		try {
			synchronized(syncAccees) {
				File file = null;
				try {
					file = openFileHide(getPathHide("Prints")+ti.filename);
					if(!file.exists()) {
						file.createNewFile();
						byte[] b = ti.save();
						os = new FileOutputStream(file);
						os.write(b);
						os.flush();
					}
				}
				catch(Exception e) {
					try {if(os != null) os.flush();}catch(Exception ex) {}
					try {if(os != null) os.close();}catch(Exception ex) {}
					os = null;
					if(tries >= 0)
						saveTicketPrintHide(ti,tries-1);
				}
			}
		}
		catch(Exception e) {}
		try {if(os != null) os.flush();}catch(Exception ex) {}
		try {if(os != null) os.close();}catch(Exception ex) {}
		new Thread() { public void run() {
			try {
                Thread.sleep(256);}catch(Exception e) {}
			File file = openFileHide(getPathHide("Prints")+ti.filename);
			if(!file.exists() || file.length() == 0 && tries >= 0)
				saveTicketPrintHide(ti,tries-1);
		}}.start();



	}

	public static TicketInformation getPrintedTicket(String filename) {
		TicketInformation ti = new TicketInformation();
		synchronized(syncAccees) {
			InputStream is = null;
			int length = -1;
			byte [] byteArray;
			try {
				is = new FileInputStream(openFile(getPath("Prints")+filename));
				length = is.available();
				byteArray = new byte [length];
				is.read(byteArray);
				ti.getTI(byteArray);
			}
			catch(Exception ex) {}
			try {if(is != null) is.close();}catch(Exception ex) {}
		}
		return ti;
	}

	public static void saveTicketPrint(TicketInformation ti,int tries) {
		OutputStream os = null;
		try {
			synchronized(syncAccees) {
				File file = null;
				try {
					file = openFile(getPath("Prints")+ti.filename);
					if(!file.exists()) {
						file.createNewFile();
						byte[] b = ti.save();
						os = new FileOutputStream(file);
						os.write(b);
						os.flush();
					}
				}
				catch(Exception e) {
					try {if(os != null) os.flush();}catch(Exception ex) {}
					try {if(os != null) os.close();}catch(Exception ex) {}
					os = null;
					if(tries >= 0)
						saveTicketPrint(ti,tries-1);
				}
			}
		}
		catch(Exception e) {}
		try {if(os != null) os.flush();}catch(Exception ex) {}
		try {if(os != null) os.close();}catch(Exception ex) {}
		if(openFile(getPath("Prints")+ti.filename).length() == 0 && tries >= 0)
			saveTicketPrint(ti,tries-1);
	}

	public static boolean saveTicket(TicketInformation ti,int r) {
		boolean returnCode;
		OutputStream os = null;
		short retryCount = 0;
		do {
			returnCode = true;
			if(retryCount > 0) {
				try{
                    Thread.sleep(250);}catch(InterruptedException ex){}
			}
			try {
				synchronized(syncAccees) {
					if(r == -1) {
						ti.filename = "Ticket"+ UUID.randomUUID().toString();
						Log.i(TAG,"saveTicket():"+ti.filename);
					}
					File file = null,renameToFile = null;
					boolean exist = false;
					try {
						file = openFile(getPath("Data")+ti.filename);
						exist = file.exists();
						if(exist) {
							renameToFile = openFile(getPath("Temp")+ti.filename);
							file.renameTo(renameToFile);
						}
						file.createNewFile();
						ti.timestampFile = (new Date()).getTime();
						byte[] b = ti.save();
						os = new FileOutputStream(file);
						os.write(b);
						os.flush();
						if(exist)
							renameToFile.delete();
					}
					catch(Exception e) {
						file.delete();
						if(exist)
							renameToFile.renameTo(file);
						throw e;
					}
				}
			}
			catch(Exception e) {returnCode = false;}
			try {if(os != null) os.flush();}catch(Exception ex) {}
			try {if(os != null) os.close();}catch(Exception ex) {}
			if(openFile(getPath("Data")+ti.filename).length() == 0)
				returnCode = false;
		} while(!returnCode && ++retryCount < 5);
		return returnCode;
	}

	public byte [] save() throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream outputStream = new DataOutputStream(baos);
		outputStream.writeBoolean(carOrGM);
		outputStream.writeUTF(userid);
		outputStream.writeUTF(authority);
		outputStream.writeInt(UserCounter);
		outputStream.writeInt(LK);
		outputStream.writeInt(dochCode);
		outputStream.writeInt(streetCode);
		outputStream.writeShort(reasonCode);
		outputStream.writeUTF(streetNumber);
		outputStream.writeByte(streetByAcrossIn);
		outputStream.writeInt(violationListCode);
		outputStream.writeShort(D);
		outputStream.writeInt(P);
		outputStream.writeUTF(DochKod);
		outputStream.writeShort(numAzhara);
		outputStream.writeShort(NumCopies);
		outputStream.writeBoolean(smoking);
		outputStream.writeInt(remarkReference1);
		outputStream.writeInt(remarkReference2);
		outputStream.writeInt(remarkReference3);
		outputStream.writeInt(remarkReference4);
		if(!carOrGM) {
			outputStream.writeUTF(CarInformationId);
			outputStream.writeInt(CarInformationType);
			outputStream.writeInt(CarInformationColor);
			outputStream.writeInt(CarInformationManufacturer);

			outputStream.writeUTF(parkingNumber1);
			outputStream.writeUTF(parkingNumber2);
		}
		else {
			outputStream.writeShort(idType);
			outputStream.writeShort((short)printDefendantSoFar);
			outputStream.writeShort((short)currentDefendant);
			outputStream.writeShort((short)defendants.size());
			for(int i = 0;i < defendants.size();i++) {
				outputStream.writeUTF(defendants.get(i).id);
				outputStream.writeUTF(defendants.get(i).last);
				outputStream.writeUTF(defendants.get(i).name);
				outputStream.writeUTF(defendants.get(i).street);
				outputStream.writeUTF(defendants.get(i).streetCode);
				outputStream.writeUTF(defendants.get(i).number);
				outputStream.writeUTF(defendants.get(i).flat);
				outputStream.writeUTF(defendants.get(i).cityName);
				outputStream.writeUTF(defendants.get(i).cityCode);
				outputStream.writeUTF(defendants.get(i).zipcode);
				outputStream.writeUTF(defendants.get(i).box);
			}
			outputStream.writeUTF(witness.id);
			outputStream.writeUTF(witness.last);
			outputStream.writeUTF(witness.name);
			outputStream.writeUTF(witness.street);
			outputStream.writeUTF(witness.number);
			outputStream.writeUTF(witness.flat);
			outputStream.writeUTF(witness.city);
			outputStream.writeUTF(witness.zipcode);
//
			outputStream.writeUTF(vehicle.licence);
			outputStream.writeInt(vehicle.type);
			outputStream.writeInt(vehicle.color);
			outputStream.writeInt(vehicle.manufacturer);
//
			outputStream.writeUTF(warningDate);
			outputStream.writeInt(warningHours);
			outputStream.writeUTF(trialDate);
			outputStream.writeUTF(trialHour);
			outputStream.writeShort(trialC);

			outputStream.writeInt(citizenRemarkReference1);
			outputStream.writeInt(citizenRemarkReference2);
			outputStream.writeInt(citizenRemarkReference3);
			outputStream.writeInt(citizenRemarkReference4);
		}   // if-else
		outputStream.writeShort(pictures.size());
		for(short i = 0;i < pictures.size();i++)
			outputStream.writeUTF(pictures.get(i));
		outputStream.writeShort(sounds.size());
		for(short i = 0;i < sounds.size();i++)
			outputStream.writeUTF(sounds.get(i));
		outputStream.writeBoolean(inProccess);
		outputStream.writeBoolean(vSwHatraa);
		outputStream.writeBoolean(sSwHatraa);
		outputStream.writeShort(type);
		outputStream.writeUTF(date);
		outputStream.writeUTF(doch);
		outputStream.writeByte(SwHazara);
		outputStream.writeInt(CS);
		outputStream.writeUTF(p_connectedStreetName);
		outputStream.writeUTF(p_streetName);
		outputStream.writeUTF(p_printedDochNumber);
		outputStream.writeUTF(p_PakachNm);
		if(!carOrGM)
			outputStream.writeUTF(p_carInformation);
		outputStream.writeUTF(p_violationName);
		outputStream.writeUTF(p_days);
		outputStream.writeUTF(p_fun);
		outputStream.writeUTF(p_title);
		outputStream.writeUTF(p_Remark);
		outputStream.writeBoolean(p_Kod_SeloRemark);
		outputStream.writeLong(timestamp);
		outputStream.writeUTF(filename);
		outputStream.writeBoolean(isPrevious);
		outputStream.writeBoolean(isCanceled);
		outputStream.writeUTF(pakachFreeText);
		outputStream.writeUTF(citizenFreeText);
		outputStream.writeUTF(PrintKod);
		outputStream.writeUTF(PrintMsg);
		outputStream.writeUTF(validUpto);
		outputStream.writeUTF(animalId);
		outputStream.writeDouble(latitude);
		outputStream.writeDouble(longitude);
		outputStream.writeUTF(uuid);
		outputStream.writeLong(checkLincenseTime);
		outputStream.writeByte(sendReportC);
		outputStream.writeBoolean(printTextBox);
		outputStream.writeUTF(subItem);
		outputStream.writeUTF(p_existingWarningPrintedDochNumber);
		outputStream.writeBoolean(openEvent);
		outputStream.writeBoolean(dochBedihavad);
		outputStream.writeByte(selectedHandicapPicture);
		outputStream.writeByte(packachDecisionCode);
		outputStream.writeBoolean(isManagement);
		outputStream.writeBoolean(didCrashInTheMiddle);
		outputStream.writeBoolean(pageCarBeforeQuery);
		outputStream.writeBoolean(pageGIbeforeQuery);
		outputStream.writeByte(MainActivity.statesTypesToByte(statesTypes));
		outputStream.writeShort(pageAveraSelectedItemAveraType);
		if(pageAveraSaveAutoCompleteTextViewAverotAsString == null)
			pageAveraSaveAutoCompleteTextViewAverotAsString = "";
		outputStream.writeUTF(pageAveraSaveAutoCompleteTextViewAverotAsString);
		if(displayCarNumber.length() == 0)
			outputStream.writeUTF(CarInformationId);
		else
			outputStream.writeUTF(displayCarNumber);
		outputStream.writeInt(Kod_SeloRemark);
		outputStream.writeBoolean(wanted);
		outputStream.writeBoolean(handicap);
		outputStream.writeBoolean(resident);
		outputStream.writeBoolean(doubleReporting);
		outputStream.writeBoolean(cellularParking);
		outputStream.writeBoolean(handicapParkingPicture);
		outputStream.writeByte(wantedAction);
		outputStream.writeByte(handicapAction);
		outputStream.writeByte(residentAction);
		outputStream.writeByte(cellularParkingAction);
		outputStream.writeByte(doubleReportingAction);
		outputStream.writeByte(leftSide);
		outputStream.writeByte(rightSide);
		outputStream.writeUTF(SugTavT);
		outputStream.writeUTF(AveraAct);
		outputStream.writeUTF(SugTav);
		outputStream.writeUTF(SwWarning);
		outputStream.writeUTF(SwAveraKazach);
		outputStream.writeUTF(keshelMsg);
		outputStream.writeUTF(TxtKazach);
		outputStream.writeUTF(TxtAzara);
		outputStream.writeShort(SwKazach);
		outputStream.writeShort(keshelKod);
		outputStream.writeByte(dataVersion);
		outputStream.writeShort(saveNumAzhara);
		outputStream.writeBoolean(canBackupFile);
		outputStream.writeUTF(quontity);
		outputStream.writeBoolean(citizenCheckBox);
		outputStream.writeBoolean(remarkReference1Print);
		outputStream.writeBoolean(remarkReference2Print);
		outputStream.writeBoolean(remarkReference3Print);
		outputStream.writeBoolean(remarkReference4Print);
		outputStream.writeUTF(recipientFirstName);
		outputStream.writeUTF(recipientLastName);
		outputStream.writeUTF(recipientId);
		outputStream.writeByte(packachCreateDecisionCode);
		outputStream.writeLong(timestampFile);
		outputStream.writeUTF(docPehula);
		outputStream.writeUTF(SwCellParkingPaid);
		outputStream.writeByte(signaturePosition);
		outputStream.writeBoolean(swExistHistory);
		outputStream.writeByte(swPrintQR);
		outputStream.writeShort(picturesRotation.size());
		for(short i = 0;i < picturesRotation.size();i++)
			outputStream.writeInt(picturesRotation.get(i).intValue());
		outputStream.writeUTF(picUrl);
		outputStream.writeBoolean(immediate);
		outputStream.writeUTF(TasksC);
		outputStream.writeBoolean(sendNewRemark);
		outputStream.writeUTF(gosh);
		outputStream.writeUTF(helka);
		return baos.toByteArray();
	}

	public static void deleteFile(String filename) {
		synchronized(syncAccees) {
			try {
				File file = openFile(getPath("Data") + filename);
				file.delete();
			}
			catch (Exception e) {}
		}
	}

	public static void deletePicDataFileHide(String filename) {
		synchronized(syncAccees) {
			try {
				File file = openFileHide(getPathHide("PicData")+filename);
				file.delete();
			}
			catch (Exception e) {}
		}
	}

	public static void deletePicDataFile(String filename) {
		synchronized(syncAccees) {
			try {
				File file = openFile(getPath("PicData")+filename);
				file.delete();
			}
			catch (Exception e) {}
		}
	}

	public static void deleteTempFile(String filename) {
		synchronized(syncAccees) {
			File file = openFile(getPath("Temp")+filename);
			file.delete();
		}
	}

	public static void deleteBackupFile(String filename) {
		File file = openFile(getPath("DataBackup")+filename);
		file.delete();
	}

	private static boolean doSave(TicketInformation ti) {
		boolean returnCode;
		OutputStream os = null;
		short retryCount = 0;
		do {
			returnCode = true;
			if(retryCount > 0) {
				try{
                    Thread.sleep(250);}catch(InterruptedException ex){}
			}
			try {
				synchronized(syncAccees) {
					deleteFile("Ticket");
					File file = openFile(getPath("Data")+"Ticket");
					file.createNewFile();
					byte[] b = ti.save();
					os = new FileOutputStream(file);
					os.write(b);
					os.flush();
				}
			}
			catch(Exception e) {
				returnCode = false;
			}
			try {if(os != null) os.flush();}catch(Exception ex) {}
			try {if(os != null) os.close();}catch(Exception ex) {}
			if(openFile(getPath("Data")+"Ticket").length() == 0)
				returnCode = false;
		} while(!returnCode && ++retryCount < 5);
		return returnCode;
	}

	public static TicketInformation tempGet() {
		TicketInformation ti = new TicketInformation();
		InputStream is = null;
		byte [] byteArray;
		try {
			is = new FileInputStream(openFile(getPath("Temp")+"Ticket"));
			int length = is.available();
			byteArray = new byte [length];
			is.read(byteArray);
			ti.getTI(byteArray);
		}
		catch(Exception ex) {}
		try {if(is != null) is.close();}catch(Exception ex) {}
		return ti;
	}

	public static void tempSave(TicketInformation ti) {
		boolean returnCode;
		OutputStream os = null;
		short retryCount = 0;
		do {
			returnCode = true;
			if(retryCount > 0) {
				try{
                    Thread.sleep(250);}catch(InterruptedException ex){}
			}
			try {
				synchronized(syncAccees) {
					deleteTempFile("Ticket");
					File file = openFile(getPath("Temp")+"Ticket");
					file.createNewFile();
					byte[] b = ti.save();
					os = new FileOutputStream(file);
					os.write(b);
					os.flush();
				}
			}
			catch(Exception e) {returnCode = false;}
			try {if(os != null) os.flush();}catch(Exception ex) {}
			try {if(os != null) os.close();}catch(Exception ex) {}
			if(openFile(getPath("Temp")+"Ticket").length() == 0)
				returnCode = false;
		} while(!returnCode && ++retryCount < 5);
	}

	public static boolean doSave(final TicketInformation ti,boolean inBackground) {
		if(inBackground) {
			new Thread() { public void run() {
				try {
                    Thread.sleep(0);}catch(Exception e) {}
				doSave(ti);
			}}.start();
			return true;
		}
		else {
			return doSave(ti);
		}
	}

	public static boolean save(TicketInformation ti) {
		UCApp.ticketInformation.inProccess = false;
		return doSave(ti);
	}

	public boolean restoreTicket(String path) {
		boolean returnCode = true;
		InputStream is = null;
		byte [] byteArray;
		try {
			is = new FileInputStream(openFile(path));
			int length = is.available();
			byteArray = new byte [length];
			is.read(byteArray);
			getTI(byteArray);
		}
		catch(Exception ex) {returnCode = false;}
		try {if(is != null) is.close();}catch(Exception ex) {}
		return returnCode;
	}

	public void getTI(byte [] byteArray) throws IOException {
		ByteArrayInputStream bais = new ByteArrayInputStream(byteArray);
		DataInputStream inputStream = new DataInputStream(bais);
		carOrGM = inputStream.readBoolean();
		userid = inputStream.readUTF();
		authority = inputStream.readUTF();
		UserCounter = inputStream.readInt();
		LK = inputStream.readInt();
		dochCode = inputStream.readInt();
		streetCode = inputStream.readInt();
		reasonCode = inputStream.readShort();
		streetNumber = inputStream.readUTF();
		streetByAcrossIn = inputStream.readByte();
		violationListCode = inputStream.readInt();
		D = inputStream.readShort();
		P = inputStream.readInt();
		DochKod = inputStream.readUTF();
		numAzhara = inputStream.readShort();
		NumCopies = inputStream.readShort();
		smoking = inputStream.readBoolean();
		remarkReference1 = inputStream.readInt();
		remarkReference2 = inputStream.readInt();
		remarkReference3 = inputStream.readInt();
		remarkReference4 = inputStream.readInt();
		if(!carOrGM) {
			CarInformationId = inputStream.readUTF();
			CarInformationType = inputStream.readInt();
			CarInformationColor = inputStream.readInt();
			CarInformationManufacturer = inputStream.readInt();
			parkingNumber1 = inputStream.readUTF();
			parkingNumber2 = inputStream.readUTF();
		}
		else {
			idType = inputStream.readShort();
			{
				printDefendantSoFar = inputStream.readShort();
				currentDefendant = inputStream.readShort();
				int i = inputStream.readShort();
				for(;i > 0;i--) {
					Defendant defendant = new Defendant();
					defendant.id = inputStream.readUTF();
					defendant.last = inputStream.readUTF();
					defendant.name = inputStream.readUTF();
					defendant.street = inputStream.readUTF();
					defendant.streetCode = inputStream.readUTF();
					defendant.number = inputStream.readUTF();
					defendant.flat = inputStream.readUTF();
					defendant.cityName = inputStream.readUTF();
					defendant.cityCode = inputStream.readUTF();
					defendant.zipcode = inputStream.readUTF();
					defendant.box = inputStream.readUTF();
					defendants.add(defendant);
				}
			}
//			
			witness.id = inputStream.readUTF();
			witness.last = inputStream.readUTF();
			witness.name = inputStream.readUTF();
			witness.street = inputStream.readUTF();
			witness.number = inputStream.readUTF();
			witness.flat = inputStream.readUTF();
			witness.city = inputStream.readUTF();
			witness.zipcode = inputStream.readUTF();
//
			vehicle.licence       = inputStream.readUTF();
			vehicle.type          = inputStream.readInt();
			vehicle.color         = inputStream.readInt();
			vehicle.manufacturer  = inputStream.readInt();
//			
			warningDate        = inputStream.readUTF();
			warningHours	   = inputStream.readInt();
			trialDate          = inputStream.readUTF();
			trialHour          = inputStream.readUTF();
			trialC             = inputStream.readShort();
//
			citizenRemarkReference1 = inputStream.readInt();
			citizenRemarkReference2 = inputStream.readInt();
			citizenRemarkReference3 = inputStream.readInt();
			citizenRemarkReference4 = inputStream.readInt();
		}
		pictures = new Vector<String>();
		for(short i = inputStream.readShort();i > 0;i--)
			pictures.add(inputStream.readUTF());
		sounds = new Vector<String>();
		for(short i = inputStream.readShort();i > 0;i--)
			sounds.add(inputStream.readUTF());
		inProccess = inputStream.readBoolean();
		vSwHatraa = inputStream.readBoolean();
		sSwHatraa = inputStream.readBoolean();
		type = inputStream.readShort();
		date = inputStream.readUTF();
		doch  = inputStream.readUTF();
		SwHazara = inputStream.readByte();
		CS = inputStream.readInt();
		p_connectedStreetName = inputStream.readUTF();
		p_streetName = inputStream.readUTF();
		p_printedDochNumber = inputStream.readUTF();
		p_PakachNm = inputStream.readUTF();
		if (!carOrGM)
			p_carInformation = inputStream.readUTF();
		p_violationName = inputStream.readUTF();
		p_days = inputStream.readUTF();
		p_fun = inputStream.readUTF();
		p_title = inputStream.readUTF();
		p_Remark = inputStream.readUTF();
		p_Kod_SeloRemark = inputStream.readBoolean();
		timestamp = inputStream.readLong();
		filename = inputStream.readUTF();
		isPrevious = inputStream.readBoolean();
		isCanceled = inputStream.readBoolean();
		pakachFreeText = inputStream.readUTF();
		citizenFreeText = inputStream.readUTF();
		PrintKod = inputStream.readUTF();
		PrintMsg = inputStream.readUTF();
		validUpto = inputStream.readUTF();
		animalId = inputStream.readUTF();
		latitude = inputStream.readDouble();
		longitude = inputStream.readDouble();
		uuid = inputStream.readUTF();
		checkLincenseTime = inputStream.readLong();
		sendReportC = inputStream.readByte();
		printTextBox = inputStream.readBoolean();
		subItem = inputStream.readUTF();
		p_existingWarningPrintedDochNumber = inputStream.readUTF();
		openEvent = inputStream.readBoolean();
		dochBedihavad = inputStream.readBoolean();
		selectedHandicapPicture = inputStream.readByte();
		packachDecisionCode = inputStream.readByte();
		isManagement = inputStream.readBoolean();
		didCrashInTheMiddle = inputStream.readBoolean();
		pageCarBeforeQuery = inputStream.readBoolean();
		pageGIbeforeQuery = inputStream.readBoolean();
		statesTypes = MainActivity.byteToStatesTypes(inputStream.readByte());
		pageAveraSelectedItemAveraType = inputStream.readShort();
		pageAveraSaveAutoCompleteTextViewAverotAsString = inputStream.readUTF();
		displayCarNumber = inputStream.readUTF();
		Kod_SeloRemark = inputStream.readInt();
		wanted = inputStream.readBoolean();
		handicap = inputStream.readBoolean();
		resident = inputStream.readBoolean();
		doubleReporting = inputStream.readBoolean();
		cellularParking = inputStream.readBoolean();
		handicapParkingPicture = inputStream.readBoolean();
		wantedAction = inputStream.readByte();
		handicapAction = inputStream.readByte();
		residentAction = inputStream.readByte();
		cellularParkingAction = inputStream.readByte();
		doubleReportingAction = inputStream.readByte();
		leftSide = inputStream.readByte();
		rightSide = inputStream.readByte();
		SugTavT = inputStream.readUTF();
		AveraAct = inputStream.readUTF();
		SugTav = inputStream.readUTF();
		SwWarning = inputStream.readUTF();
		SwAveraKazach = inputStream.readUTF();
		keshelMsg = inputStream.readUTF();
		TxtKazach = inputStream.readUTF();
		TxtAzara = inputStream.readUTF();
		SwKazach = inputStream.readShort();
		keshelKod = inputStream.readShort();
		dataVersion = inputStream.readByte();
		saveNumAzhara = inputStream.readShort();
		canBackupFile = inputStream.readBoolean();
		quontity = inputStream.readUTF();
		citizenCheckBox = inputStream.readBoolean();
		remarkReference1Print = inputStream.readBoolean();
		remarkReference2Print = inputStream.readBoolean();
		remarkReference3Print = inputStream.readBoolean();
		remarkReference4Print = inputStream.readBoolean();
		recipientFirstName = inputStream.readUTF();
		recipientLastName = inputStream.readUTF();
		recipientId = inputStream.readUTF();
		packachCreateDecisionCode = inputStream.readByte();
		timestampFile = inputStream.readLong();
		docPehula = inputStream.readUTF();
		SwCellParkingPaid = inputStream.readUTF();
		signaturePosition = inputStream.readByte();
		swExistHistory = inputStream.readBoolean();
		swPrintQR = inputStream.readByte();
		picturesRotation = new Vector<Integer>();
		for(short i = inputStream.readShort();i > 0;i--)
			picturesRotation.add(Integer.valueOf(inputStream.readInt()));
		picUrl = inputStream.readUTF();
		immediate = inputStream.readBoolean();
		TasksC = inputStream.readUTF();
		sendNewRemark = inputStream.readBoolean();
		gosh = inputStream.readUTF();
		helka = inputStream.readUTF();
	}

	public boolean restoreTicket() {
		return restoreTicket(getPath("DataBackup")+"Last");
	}

	public static String getFilename(String file) {
		int slash = file.lastIndexOf('/');
		if(slash != -1 && slash+1 < file.length())
			return file.substring(slash+1);
		else
			return file;
	}

	public static synchronized File openFile(String filename) {
		if(android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
			return new File(filename);
		else
			return new File(getDirectory(filename), getFilename(filename));
	}

	public static synchronized File openFileHide(String filename) {
		return new File(getDirectory(filename), getFilename(filename));
	}

	public static String getPath(final String directory) {
		File dir;
		if(android.os.Environment.getExternalStorageState() != null && android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
			File storageDir = android.os.Environment.getExternalStorageDirectory();
			dir = new File(storageDir.getAbsolutePath(),"Hanita");
			if(!dir.exists())
				dir.mkdirs();
			dir = new File(dir.getAbsolutePath(),directory);
		}
		else {
			dir = RecordStore.context.getFileStreamPath(directory);
		}
		if(!dir.exists())
			dir.mkdirs();
		return dir.getAbsolutePath()+"/";
	}

	public static String getPathHide(final String directory) {
		File dir = RecordStore.context.getFileStreamPath(directory);
		if(!dir.exists())
			dir.mkdirs();
		return dir.getAbsolutePath()+"/";
	}

	public static String getDirectory(String file) {
		int slash = 0;
		for(int i = 0;i < file.length();i++)
			if(file.charAt(i) == '/')
				slash = i;
		return file.substring(0,slash);
	}

	public static void addSubDirectory(String path, String subDirectory) {
		try {
			File dir = null;
			if(android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
				dir = new File(getDirectory(path),"");
			else
				dir = RecordStore.context.getFileStreamPath(getDirectory(path));
			if(!dir.exists())
				dir.mkdirs();
			File subDir = new File(dir.getAbsolutePath(),subDirectory);
			if(!subDir.exists())
				subDir.mkdirs();
		}
		catch(Exception e) {}
	}

	public static void createWorkingDirectory() {
		addSubDirectory(getPath(""),"Data");
		addSubDirectory(getPath(""),"DataBackup");
		addSubDirectory(getPath(""),"Log");
		addSubDirectory(getPath(""),"Totals");
		addSubDirectory(getPath(""),"Temp");
 		addSubDirectory(getPath(""),"Prints");
		addSubDirectory(getPath(""),"CrushLog");
		addSubDirectory(getPath(""),"PicData");
		addSubDirectory(getPath(""),"CarPics");
		addSubDirectory(getPath(""),"Licenses");
	}

	public static boolean hasToLoadTickets() {
		boolean hasMore = false;
		try {
			File dir = openFile(getPath("Data"));
			File[] files = dir.listFiles();
			for(int i = 0;files != null && i < files.length;i++) {
				if(files[i].getName().compareTo("Ticket") != 0 && files[i].getName().charAt(0) == 'T') {
					if(SendTicketsAndPictures.retryServerErrorNofityUser.get(files[i].getName()) == null) {
						hasMore = true;
						break;
					}
				}
			}
		}
		catch(Exception e) {}
		return hasMore;
	}

	public static void printTotals(TicketInformation ticketInformation) {
		OutputStream os = null;
		PrintStream ps = null;
		try {
			synchronized(syncAccees) {
				String mm = Util.getMonth()+"";
				if(mm.length() < 2)
					mm = '0'+mm;
				String dd = +Util.getDay()+"";
				if(dd.length() < 2)
					dd = '0'+dd;
				File file = openFile(getPath("Totals")+"F"+Util.getYear()+mm+dd+".txt");
				if(!file.exists())
					file.createNewFile();
				os = new FileOutputStream(file,true);
				ps = new PrintStream(os);
				ps.println(ticketInformation.p_printedDochNumber);
				if(ticketInformation.timestamp == 0)
					ps.println(Util.getTime((new Date()).getTime()));
				else
					ps.println(Util.getTime(ticketInformation.timestamp));
				if(ticketInformation.vSwHatraa || ticketInformation.sSwHatraa || ticketInformation.isCanceled || ticketInformation.p_fun.length() == 0)
					ps.println(0);
				else
					ps.println(ticketInformation.p_fun);
				ps.flush();
			}
		}
		catch(Exception e) {}
		try {if(ps != null) ps.flush();}catch(Exception ex) {}
		try {if(os != null) os.flush();}catch(Exception ex) {}
		try {if(ps != null) ps.close();}catch(Exception ex) {}
		try {if(os != null) os.close();}catch(Exception ex) {}
	}
/*
	public static void printTotals() {
		InputStream is = null;
		try {
			synchronized(syncAccees) {
				String mm = Util.getMonth()+"";
				if(mm.length() < 2)
					mm = '0'+mm;
				String dd = +Util.getDay()+"";
				if(dd.length() < 2)
					dd = '0'+dd;
				File file = openFile(getPath("Totals")+"F"+Util.getYear()+mm+dd+".txt");
				if(file.exists()) {
					Vector<String> docs = new Vector<String>();
					Vector<String> times = new Vector<String>();
					Vector<String> funs = new Vector<String>();
					int totalFuns = 0;
					int counter = 0;
					is = new FileInputStream(file);
					int ch;
					String line = "";
					while((ch = is.read()) != -1) {
						if(ch == 10 || ch == 13) {
							if(line.length() > 0) {
								if(counter == 0) {
									docs.addElement(line);
									counter = 1;
								}
								else if(counter == 1) {
									times.addElement(line);
									counter = 2;
								}
								else {
									try {totalFuns += Integer.parseInt(line);}catch(Exception e){}
									funs.addElement(line);
									counter = 0;
								}
							}
							line = "";
						}
						else {
							line += (char)ch;
						}
					}
					BluetoothPrinter.TotalsZEBRA(docs,times,funs,totalFuns, UCApp.loginData.PakachNm);
				}
			}
		}
		catch (Exception e) {
			System.out.println("Error: " + e.getMessage());
		}
		try {if(is != null) is.close();}catch(Exception ex) {}
	}
*/
	public boolean backup() {
		if(canBackupFile) {
			boolean isSuccess = false;
			deleteBackupFile("Last");
			isSuccess = backup("Last");
			if(!isCanceled) {
				deleteBackupFile(doch);
				isSuccess = backup(doch);
			}
			return isSuccess;
		}
		else {
			return true;
		}
	}

	private boolean backup(String filename) {
		if(canBackupFile) {
			boolean returnCode = true;
			OutputStream os = null;
			try {
				synchronized(syncAccees) {
					File file = openFile(getPath("DataBackup")+filename);
					file.createNewFile();
					byte[] b = save();
					os = new FileOutputStream(file);
					os.write(b);
					os.flush();
				}
			}
			catch(Exception e) {returnCode = false;}
			try {if(os != null) os.flush();}catch(Exception ex) {}
			try {if(os != null) os.close();}catch(Exception ex) {}
			return returnCode;
		}
		else {
			return true;
		}
	}

	public boolean restoreTicketFromCard(String doch) {
		return restoreTicket(getPath("DataBackup")+doch);
	}
}
