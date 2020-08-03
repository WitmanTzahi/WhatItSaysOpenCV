package com.lbmotion.whatitsays.data;

public class PictureInformation {
	public String carInformationId;
	public double latitude;
	public double longitude;
	public int count;

	public PictureInformation(String carInformationId, double latitude, double longitude, int count) {
		this.carInformationId = carInformationId;
		this.latitude = latitude;
		this.longitude = longitude;
		this.count = count;
	}
}