package com.lbmotion.whatitsays.data;

import java.util.Vector;

public class Violation {
	public Violation() {}
	public int     C                = 0;
	public int     K                = 0;
	public byte    VT               = 0;
	public String Nm               = "";
	public short   MP               = 0;
	public short   MR               = 0;
	public short   P                = 0;
	public short   D                = 0;
	public byte    SwTz             = 0;
	public byte    SwRishui         = 0;
	public byte    SwPrivateCompany = 0;
	public byte    NumCopies        = 1;
	public short   SwMustTavHanaya  = 0;
	public short   SwHanayaHok      = 0;
	public boolean SwHatraa         = false;
	public String title            = "";
	public byte	   VTC 				= 0;
	public String VT_Nm 			= "";
	public byte	   Sivug   		    = 0;
	public byte	   OffenceIcon  	= 0;
	public boolean hasVirtualDoc    = false;
	public boolean QRSwWork   	    = false;
	public boolean SwWorkPrinter    = false;
	public String TypeForm  		= "";
	public short   toAtraa          = 0;
	public short   toAvera          = 0;
	public byte	   swPrintQR  		= 0;
	public String codeMotavShort   = "";
	public String TeurShort        = "";
	public Vector<Integer> screensOrder = new Vector();

	public String toString() {
		return "C = " + "" + C + ", name = " + Nm + ", tav = " + SwMustTavHanaya + ", law = " + SwHanayaHok;
	}	
}
