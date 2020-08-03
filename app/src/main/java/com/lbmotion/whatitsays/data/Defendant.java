package com.lbmotion.whatitsays.data;

public class Defendant {
	public String id			= "";
	public String last		= "";
	public String name		= "";
	public String streetCode	= "";
	public String street		= "";
	public String number		= "";
	public String flat		= "";
	public String cityCode	= "";
	public String cityName	= "";
	public String zipcode		= "";
    public String packachName = "";
    public String validUpto 	= "";
    public String chip	 	= "";
    public String box  	 	= "";
    public String TxtPopUp   	= "";
	public String KeshelKod  	= "";
	public String KeshelMsg  	= "";
	public String RightSide 	= "";
	public String LeftSide   	= "";
	public boolean  swExistHistory = false;
	public Defendant(String id, String last, String name, String streetCode, String street, String number, String flat, String cityCode, String cityName, String zipcode, String packachName, String validUpto, String chip, String box, String TxtPopUp,
                     String KeshelKod, String KeshelMsg, String RightSide, String LeftSide, String swExistHistory) {
		this.id      	= id;
		this.last       = last;
		this.name       = name;
		this.streetCode = streetCode;
		this.street  	= street;
		this.number  	= number;
		this.flat       = flat;
		this.cityCode 	= cityCode;
		this.cityName 	= cityName;
		this.zipcode 	= zipcode;
		this.packachName = packachName;
		this.validUpto 	= validUpto;
		this.chip 		= chip;
		this.box 		= box;
		this.TxtPopUp 	= TxtPopUp;
		this.KeshelKod  = KeshelKod;
		this.KeshelMsg  = KeshelMsg;
		this.RightSide 	= RightSide;
		this.LeftSide   = LeftSide;
		if(swExistHistory.equals("1"))
			this.swExistHistory = true;
	}

	public Defendant() {}
}
