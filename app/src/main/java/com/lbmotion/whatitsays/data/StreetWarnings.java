package com.lbmotion.whatitsays.data;

public class StreetWarnings {
	public int C  				= 0;
	public int CStreet 			= 0;
	public int KodStreet 		= 0;
	public int FromNoZugi 	 	= 0;
	public int ToNoZugi 		= 0;
	public int FromNoLoZugi 	= 0;
	public int ToNoLoZugi 	 	= 0;
	public String FromDate		= "";
	public String ToDate		= "";
	public int[] Averot; 

	public String toString() 	{
		String averot = new String("C = " + "" + C + ", CStreet = " + "" + CStreet + ", KodStreet = " +
				"" + KodStreet + ", from/to (even): " + "" + FromNoZugi + " " + "" + ToNoZugi +
				", from/to (odd): " + "" + FromNoLoZugi + " " + "" + ToNoLoZugi +
				", from " + FromDate + ", to " + ToDate + ". ");
		averot += "Warning numbers are: ";
		for (int i = 0; i < Averot.length; i++)
			averot += ("" + Averot[i] + ", ");
		return averot;
	}
}
