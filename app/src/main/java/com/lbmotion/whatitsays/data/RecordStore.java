package com.lbmotion.whatitsays.data;

import android.content.Context;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class RecordStore {
	private final static String TAG = "RecordStore";
	public static Context context;
	private String recordStoreName;
	private DataOutputStream outputStream;
	private int 					numberofRecords = -1;
	private DataInputStream inputStream;
	
	public String getM_recordStoreName() {return recordStoreName;}
	public void setM_recordStoreName(String m_recordStoreName) {this.recordStoreName = m_recordStoreName;}
	public DataOutputStream getOutputStream() {return outputStream;}
	public void setOutputStream(DataOutputStream outputStream) {this.outputStream = outputStream;}
	public DataInputStream getInputStream() {return inputStream;}
	public void setInputStream(DataInputStream inputStream) {this.inputStream = inputStream;}
	
	public static RecordStore openRecordStore(String recordStoreName, boolean createIfNecessary) throws Exception {
		RecordStore me = new RecordStore(); 
		me.setM_recordStoreName(recordStoreName);
		File file = context.getFileStreamPath(me.getM_recordStoreName());
		if(file.exists()) {
			me.setInputStream(new DataInputStream(new BufferedInputStream(context.openFileInput(recordStoreName))));
		}
		else {
			if(createIfNecessary) {
				me.setOutputStream(new DataOutputStream(new BufferedOutputStream(context.openFileOutput(recordStoreName, AppCompatActivity.MODE_PRIVATE))));
				me.setInputStream(new DataInputStream(new BufferedInputStream(context.openFileInput(recordStoreName))));
			}
			else {
				throw new FileNotFoundException();
			}
		}
		return me;
	}
	
	public void setNumberofRecords(int records) throws Exception {
		numberofRecords = records;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream os = new DataOutputStream(baos);
		os.writeInt(numberofRecords);
		byte[] b = baos.toByteArray();
		addRecord(b,0,b.length);
		os.flush();
		os.close();
	}
	
	public int getNumRecords() {
		if(numberofRecords == -1) {
			numberofRecords = 0;
			try{numberofRecords = inputStream.readInt();}
			catch (IOException e) {
				Log.i(TAG, "getNumRecords():"+e.getMessage());
			}
		}
		return numberofRecords;
	}
	
	public byte[] getRecord(int i) throws Exception {
		byte[] bufferRead = null;
		try {
			byte[] buffer = new byte[1024 * (256 + 128)];
			if (numberofRecords == -1)
				numberofRecords = inputStream.readInt();
			int length = inputStream.read(buffer);
			if (length != -1) {
				bufferRead = new byte[length];
				System.arraycopy(buffer, 0, bufferRead, 0, length);
			}
		}
		catch (Exception e) {}
		catch (OutOfMemoryError m) {
			if(i != -1) {
				System.gc();
				return getRecord(-1);
			}
		}
		return bufferRead;
	}
	
	public void addRecord(byte buffer[],int offset,int count) throws Exception {
		outputStream.write(buffer, offset, count);
	}
	
	public static void deleteRecordStore(String recordStoreName) {
		File file = context.getFileStreamPath(recordStoreName);
		if(file.exists())
			file.delete();
	}

	public void closeRecordStore() throws Exception {
		try{if(outputStream != null) outputStream.flush();}catch (Exception e) {}
		try{if(outputStream != null) outputStream.close();}catch (Exception e) {}
		try{if(inputStream != null) inputStream.close();}catch (Exception e) {}
	}
}