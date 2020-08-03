package com.lbmotion.whatitsays.data;

public class Witness {
	public String id			= "";
	public String last			= "";
	public String name			= "";
	public String street		= "";
	public String number		= "";
	public String flat			= "";
	public String city			= "";
	public String cityCode		= "";
	public String zipcode		= "";
	public String telephone 	= "";
	
	public Witness(String id, String last, String name, String street, String number, String flat, String city, String zipcode, String cityCode) {
		this.id = id;
		this.last = last;
		this.name = name;
		this.street = street;
		this.number = number;
		this.flat = flat;
		this.city = city;
		this.zipcode = zipcode;
		this.cityCode = cityCode;
		this.telephone 	= "";
	}
	public Witness () {}

	public void copy(Witness w) {
		if(w != null) {
			this.id = w.id;
			this.last = w.last;
			this.name = w.name;
			this.street = w.street;
			this.number = w.number;
			this.flat = w.flat;
			this.city = w.city;
			this.zipcode = w.zipcode;
			this.cityCode = w.cityCode;
			this.telephone = w.telephone;
		}
		else {
			copy(new Witness());
		}
	}
}
