package es.unex.spilab.contigo.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Params {

	@SerializedName("latitude")
	@Expose
	private Double latitude;
	@SerializedName("longitude")
	@Expose
	private Double longitude;
	@SerializedName("radius")
	@Expose
	private Integer radius;
	@SerializedName("minActivityTime")
	@Expose
	private Double minActivityTime;
	@SerializedName("range")
	@Expose
	private Integer range;

	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	public Integer getRadius() {
		return radius;
	}

	public void setRadius(Integer radius) {
		this.radius = radius;
	}

	public Double getMinActivityTime() {
		return minActivityTime;
	}

	public void setMinActivityTime(Double minActivityTime) {
		this.minActivityTime = minActivityTime;
	}

	public Integer getRange() {
		return range;
	}

	public void setRange(Integer range) {
		this.range = range;
	}

	@Override
	public String toString() {
		return "Params [latitude=" + latitude + ", longitude=" + longitude + ", radius=" + radius + ", minActivityTime="
				+ minActivityTime + ", range=" + range + "]";
	}

}