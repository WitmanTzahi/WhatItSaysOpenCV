package com.lbmotion.whatitsays.data;

public class Vehicle
{
	public String licence 		= "";
	public int type	 			= -1;
	public int color	 		= -1;
	public int manufacturer 	= -1;
	public String TxtPopUp		= "";
	public String KeshelKod  	= "";
	public String KeshelMsg  	= "";
	public int	   RightSide 	= 0;
	public int     LeftSide   	= 0;

	//
	public Vehicle(String licence, int type, int color, int manufacturer) {
		this.licence       = licence;
		this.type          = type;
		this.color         = color;
		this.manufacturer  = manufacturer;
	}

	public Vehicle(String licence, String type, String color, String manufacturer, String TxtPopUp, String KeshelKod, String KeshelMsg, String RightSide, String LeftSide) {
		this.licence = licence;
		if(type.length() > 0) {
			try{this.type = Integer.parseInt(type);}catch (Exception e) {}
		}
		if(color.length() > 0) {
			try{this.color = Integer.parseInt(color);}catch (Exception e) {}
		}
		if(manufacturer.length() > 0) {
			try{this.manufacturer = Integer.parseInt(manufacturer);}catch (Exception e) {}
		}
		this.TxtPopUp = TxtPopUp;
		this.KeshelKod  = KeshelKod;
		this.KeshelMsg  = KeshelMsg;
//
		if (LeftSide.compareToIgnoreCase("ביטול") == 0)
			this.LeftSide = 1;
		else if (LeftSide.compareToIgnoreCase("המשך") == 0)
			this.LeftSide = 2;
		else if (LeftSide.compareToIgnoreCase("חזור") == 0)
			this.LeftSide = 3;
		else if (LeftSide.compareToIgnoreCase("נסה שנית") == 0)
			this.LeftSide = 4;
//
		if (RightSide.compareToIgnoreCase("ביטול") == 0)
			this.RightSide = 1;
		else if (RightSide.compareToIgnoreCase("המשך") == 0)
			this.RightSide = 2;
		else if (RightSide.compareToIgnoreCase("חזור") == 0)
			this.RightSide = 3;
		else if (RightSide.compareToIgnoreCase("נסה שנית") == 0)
			this.RightSide = 4;
	}

	public int getKeshelKod() {
		try {
			return Integer.parseInt(KeshelKod);
		}
		catch (Exception e) {
			return 0;
		}
	}

	public byte getRightSide() {
		try {
			return (byte)RightSide;
		}
		catch (Exception e) {
			return (byte)0;
		}
	}
	public byte getLeftSide() {
		try {
			return (byte)LeftSide;
		}
		catch (Exception e) {
			return (byte)0;
		}
	}

	public Vehicle() {}
}
