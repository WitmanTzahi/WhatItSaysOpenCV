package com.lbmotion.whatitsays.data;

import com.lbmotion.whatitsays.MainActivity;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

public class CheckLincense {
	public short   	type = -1;
	public short   	manufacturer = -1;
	public short   	color = -1;
	public int     	Kod_SeloRemark = 0;
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
	public String 	SugTavT = "";
	public String 	AveraAct = "";
	public String 	SugTav = "";
	public String 	SwWarning = "";
	public String 	SwAveraKazach = "";
	public short 	SwKazach = 0;
	public String 	TxtKazach = "";
	public String 	TxtAzara = "";
	public short   	keshelKod = 0;
	public String 	keshelMsg = "";
	public byte    	leftSide = 0;
	public byte    	rightSide = 0;
	public String 	PrintKod = "";
	public String 	PrintMsg = "";
	public boolean 	handicapParkingPicture = false;
	public int		KazachRemark = 0;
	public String 	SwCellParkingPaid = "";

	private static String reverseDate(String input) {
		String out = "";
		for(int i = 0;i < input.length();i++) {
			if(input.charAt(i) >= '0' && input.charAt(i) <= '9') {
				int n;
				for(n = i+1;n < input.length();n++) {
					if(input.charAt(n) < '0' || input.charAt(n) > '9') {
						for(int m = n-1;m >= i;m--)
							out += input.charAt(m);
						if(n < input.length())
							out += input.charAt(n);
						i = n;
						break;
					}
				}
				for(int m = n-1;m >= i;m--)
					out += input.charAt(m);
				i = n;
			}
			else {
				out += input.charAt(i);
			}
		}
		return out;
	}

	public static String reverseNumbers(String input) {
		String out = "";
		for(int i = 0;i < input.length();i++) {
			if((input.charAt(i) >= '0' && input.charAt(i) <= '9') || input.charAt(i) == '/') {
				int n;
				for(n = i+1;n < input.length();n++) {
					if((input.charAt(n) < '0' || input.charAt(n) > '9') && (input.charAt(n) != '/')) {
						String date = "";
						boolean hasDate = false;
						for(int m = n-1;m >= i;m--) {
							if(input.charAt(m) == '/')
								hasDate = true;
							date += input.charAt(m);
						}
						if(hasDate)
							date = reverseDate(date);
						out += date;
						if(n < input.length())
							out += input.charAt(n);
						i = n;
						break;
					}
				}
				String date = "";
				boolean hasDate = false;
				for(int m = n-1;m >= i;m--) {
					if(input.charAt(m) == '/')
						hasDate = true;
					date += input.charAt(m);
				}
				if(hasDate)
					date = reverseDate(date);
				out += date;
				i = n;
			}
			else {
				out += input.charAt(i);
			}
		}
		return out;
	}

	private static boolean checkIfLeftToRight(char c) {
		if(c >= '0' && c <= '9')
			return true;
		if(c >= 'a' && c <= 'z')
			return true;
		if(c >= 'A' && c <= 'Z')
			return true;
		return false;
	}

	public static String reverseNumbersDateAndTime(String input) {
		String out = "";
		for(int i = 0;i < input.length();i++) {
			if(checkIfLeftToRight(input.charAt(i)) || input.charAt(i) == '/' || input.charAt(i) == ':') {
				int n;
				for(n = i+1;n < input.length();n++) {
					if(!checkIfLeftToRight(input.charAt(n)) && (input.charAt(n) != '/') && (input.charAt(n) != ':')) {
						String date = "";
						boolean hasDate = false;
						for(int m = n-1;m >= i;m--) {
//							if(input.charAt(m) == '/' || input.charAt(m) == ':')
							if(input.charAt(m) == '/')
								hasDate = true;
							date += input.charAt(m);
						}
						if(hasDate)
							date = reverseDate(date);
						out += date;
						if(n < input.length())
							out += input.charAt(n);
						i = n;
						break;
					}
				}
				String date = "";
				boolean hasDate = false;
				for(int m = n-1;m >= i;m--) {
//					if(input.charAt(m) == '/' || input.charAt(m) == ':')
					if(input.charAt(m) == '/')
						hasDate = true;
					date += input.charAt(m);
				}
				if(hasDate)
					date = reverseDate(date);
				out += date;
				i = n;
			}
			else {
				out += input.charAt(i);
			}
		}
		return out;
	}
/*
	public static String reverseNumbersDateAndTime(String input) {
		String out = "";
		for(int i = 0;i < input.length();i++) {
			if((input.charAt(i) >= '0' && input.charAt(i) <= '9') || input.charAt(i) == '/' || input.charAt(i) == ':') {
				int n;
				for(n = i+1;n < input.length();n++) {
					if((input.charAt(n) < '0' || input.charAt(n) > '9') && (input.charAt(n) != '/') && (input.charAt(n) != ':')) {
						String date = "";
						boolean hasDate = false;
						for(int m = n-1;m >= i;m--) {
//							if(input.charAt(m) == '/' || input.charAt(m) == ':')
							if(input.charAt(m) == '/')
								hasDate = true;
							date += input.charAt(m);
						}
						if(hasDate)
							date = reverseDate(date);
						out += date;
						if(n < input.length())
							out += input.charAt(n);
						i = n;
						break;
					}
				}
				String date = "";
				boolean hasDate = false;
				for(int m = n-1;m >= i;m--) {
//					if(input.charAt(m) == '/' || input.charAt(m) == ':')
					if(input.charAt(m) == '/')
						hasDate = true;
					date += input.charAt(m);
				}
				if(hasDate)
					date = reverseDate(date);
				out += date;
				i = n;
			}
			else {
				out += input.charAt(i);
			}
		}
		return out;
	}
*/

	public long read(String licensePlate) {
		long timestamp = 0;
		String directoryLicensePlate = "";
		byte queryResult = 0;
		InputStream is = null;
		byte [] byteArray;
		try {
			if(licensePlate == null)
				is = new FileInputStream(TicketInformation.openFile(TicketInformation.getPath("Temp")+"CheckLincense"));
			else
				is = new FileInputStream(TicketInformation.openFile(TicketInformation.getPath("Licenses/"+licensePlate)));
			int length = is.available();
			byteArray = new byte [length];
			is.read(byteArray);
			ByteArrayInputStream bais = new ByteArrayInputStream(byteArray);
			DataInputStream inputStream = new DataInputStream(bais);
			type = inputStream.readShort();
			manufacturer = inputStream.readShort();
			color = inputStream.readShort();
			SwKazach = inputStream.readShort();
			keshelKod = inputStream.readShort();
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
			Kod_SeloRemark = inputStream.readInt();
			KazachRemark = inputStream.readInt();
			SugTavT = inputStream.readUTF();
			AveraAct = inputStream.readUTF();
			SugTav = inputStream.readUTF();
			SwWarning = inputStream.readUTF();
			SwAveraKazach = inputStream.readUTF();
			TxtKazach = inputStream.readUTF();
			TxtAzara = inputStream.readUTF();
			keshelMsg = inputStream.readUTF();
			PrintKod = inputStream.readUTF();
			PrintMsg = inputStream.readUTF();
			SwCellParkingPaid = inputStream.readUTF();
			if(licensePlate != null) {
				timestamp = inputStream.readLong();
				directoryLicensePlate = inputStream.readUTF();
				queryResult = inputStream.readByte();
			}
		}
		catch(Exception ex) {}
		try {if(is != null) is.close();}catch(Exception ex) {}
		return timestamp;
	}

	public void save(String licensePlate, String directoryLicensePlate, long timestamp, byte queryResult) {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			DataOutputStream outputStream = new DataOutputStream(baos);
			outputStream.writeShort(type);
			outputStream.writeShort(manufacturer);
			outputStream.writeShort(color);
			outputStream.writeShort(SwKazach);
			outputStream.writeShort(keshelKod);
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
			outputStream.writeInt(Kod_SeloRemark);
			outputStream.writeInt(KazachRemark);
			outputStream.writeUTF(SugTavT);
			outputStream.writeUTF(AveraAct);
			outputStream.writeUTF(SugTav);
			outputStream.writeUTF(SwWarning);
			outputStream.writeUTF(SwAveraKazach);
			outputStream.writeUTF(TxtKazach);
			outputStream.writeUTF(TxtAzara);
			outputStream.writeUTF(keshelMsg);
			outputStream.writeUTF(PrintKod);
			outputStream.writeUTF(PrintMsg);
			outputStream.writeUTF(SwCellParkingPaid);
			File file;
			if(licensePlate == null) {
				file = TicketInformation.openFile(TicketInformation.getPath("Temp") + "CheckLincense");
			}
			else {
				outputStream.writeLong(timestamp);
				outputStream.writeUTF(directoryLicensePlate);
				outputStream.writeByte(queryResult);
				file = TicketInformation.openFile(TicketInformation.getPath("Licenses/" + licensePlate));
			}
			file.delete();
			file.createNewFile();
			FileOutputStream os = new FileOutputStream(file);
			os.write(baos.toByteArray());
			os.flush();
		}
		catch (Exception e) {}
	}
}
