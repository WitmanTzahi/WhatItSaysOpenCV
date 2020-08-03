package com.lbmotion.whatitsays.data;

import java.util.Vector;

public class ExistingWarning {
	public String Lk					= "";
	public String UsrKod				= "";
	public String UsrNm				= "";
	public String UsrC				= "";
	public String DochC				= "";
	public String DochKod				= "";
	public String Sidra				= "";
	public String Bikoret				= "";
	public String AveraNm				= "";
	public String AveraC				= "";
	public String AveraKod			= "";
	public String Mhr					= "";
	public String Days				= "";
	
	public Defendant 	defendant 			= new Defendant();
	
	public String StreetC				= "";
	public String StreetNm			= "";
	public String StreetKod			= "";
	public String MikumKod			= "";
	public String MikumC				= "";

	public Witness 		witness 			= new Witness();

	public String numAzhara			= "0";
	public String StreetNoAvera		= "";
	public String CityNm				= "";
	public String validUpto			= "";
	public String validUptoTime		= "";
	public String animalId			= "";
	
	public String remark				= "";

	public String FreeRemark = "";
	public String CitizenFreeText = "";

	public String Pakach_Remark1 = "", Pakach_Remark2 = "", Pakach_Remark3 = "", Pakach_Remark4 = "";
	public String Citizen_Remark1 = "", Citizen_Remark2 = "", Citizen_Remark3 = "", Citizen_Remark4 = "";

	public Vehicle 		vehicle = new Vehicle();

	public Vector<String> pictures   		= new Vector<>();

	public ExistingWarning() {
	}
}
