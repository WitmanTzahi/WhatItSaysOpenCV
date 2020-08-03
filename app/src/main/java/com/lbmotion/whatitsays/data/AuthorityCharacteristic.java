package com.lbmotion.whatitsays.data;

public class AuthorityCharacteristic {
	public int    	Violation = 0;
	public int    	CarType = 0;
	public int    	CarColor = 0;
	public int    	CarIzran = 0;
	public int    	SwN = 0;
	public int    	SwS = 0;
	public int    	SwT = 0;
	public int    	SwPic240_320 = 0;
	public int    	SwM = 0;
	public int    	ServTo = 0;                //To whom to serve
	public int    	TimeOut_CarNumber = 0;
	public int    	TimeOut_ChkHeter = 0;
	public int    	TimeOut_DoubleReport = 0;
	public int    	TimeOut_SendReport = 0;
	public int    	TimeOut_SendPic = 0;
	public int    	TimeOut_GetReport = 0;
	public int    	TimeOut_SendCancel = 0;
	public int    	TimeOut_SendCancelPic = 0;
	public int    	PicNo = 0;                 //The number of pictures to send immidiatly
	public int    	SwNoDeleteDochMeshofon = 0;//Is the pakach can cancel a Doch
	public int    	SwAddPinkasMesofon = 0;    //Can the pakach issue a pinkas
	public int    	SwPrintDochBreratMispat = 0;
	public int    	SwEnterCar = 0;
	public int    	FlashOnFrom = 0;
	public int    	FlashOnTo    =   0;
	public int    	CntMaxPicMesofon = 0;
	public int    	SwPrintDate = 0;
	public int    	Minute_CloseMesofon = 1440;
	public int    	DurationRecording = 0;  // maximum duration of a single voice record
	public int    	DurationRecordingDoch = 0;  // total maximum duration of voice records for a single report
	public long   	lastUpdate = 0;
	public String 	BetMishpat = "";
	public int	 	SwSend_SMS = 0;
	public int	 	SwAzara = 0;
	public String 	AuthorityName;
	public int 		SwPicHanaya = 0;
	public int 		SwPrintTotal = 0;
	public int 		SwQR = 0;
	public boolean	PinkasWarningReport = false;
	public boolean	RepeatLicsence = false;
	public boolean	SwchooseNechePic = false;
	public boolean	SwQRManager = false;
	public boolean	SwNotMustTz = false;
	public boolean	SwMustServTo = false;
}
