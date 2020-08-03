package com.lbmotion.whatitsays.communication;

import android.util.Log;

import com.lbmotion.whatitsays.data.TicketInformation;

public class Photo extends Base {
	private final static String TAG = "Photo";
	private String file;
	private String authority;
	public short   portBackgroundUsed;
	private boolean sendFromPicData;
	private boolean sendFromPicDataHide;

	public Photo(String file, String authority, boolean sendFromPicData, boolean sendFromPicDataHide) {
		this.file = file;
		this.authority = authority;
		this.sendFromPicData = sendFromPicData;
		this.sendFromPicDataHide = sendFromPicDataHide;
	}

	public static Photo doSave(String file, String authority, boolean sendFromPicData, boolean sendFromPicDataHide) {
		Photo me = new Photo(file,authority,sendFromPicData,sendFromPicDataHide);
		me.start();
//		communicationThreads.addElement(me);
		while(!me.isDone.get()) {
			try {
                Thread.sleep(250);}catch (InterruptedException ie) {}
		}
//		communicationThreads.removeElement(me);
		return me;
	}

	public void run() {
		try {
			byte[] byteArray;
			if(sendFromPicDataHide)
				byteArray = TicketInformation.getPicDataFileBufferHide(file);
			else if(sendFromPicData)
				byteArray = TicketInformation.getPicDataFileBuffer(file);
			else
				byteArray = TicketInformation.getFileBuffer(file);
			openBackground();
			portBackgroundUsed = lastPortBackground;
			Log.i(TAG,"byteArray.length:"+byteArray.length);
			sender("PHV"+"version"+"\f"+version+"\f"+"authority"+"\f"+authority+"\f"+"file"+"\f"+file+"\f"+"length"+"\f"+byteArray.length+"\f"+"\n");
			outputStream.flush();
			outputStream.write(byteArray);
			outputStream.flush();
			helperTimer.schedule(new CancelCommunicationTask(this),120000);
			readServer();
			helperTimer.cancel();
			if(bufferChar != null) {
				if(bufferChar != null && bufferChar.length >= 2 && bufferChar[0] == 'O' && bufferChar[1] == 'K')
					result = true;
			}
			close(true);
		}
		catch (Exception e) {close(false);}
		catch (Error e) {close(false);}
		isDone.set(true);
	}	

}
