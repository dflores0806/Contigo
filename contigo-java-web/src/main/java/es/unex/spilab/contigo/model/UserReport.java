package es.unex.spilab.contigo.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserReport {

	@SerializedName("name")
	@Expose
	private String name;

	@SerializedName("address")
	@Expose
	private String address;

	@SerializedName("postalAddress")
	@Expose
	private String postalAddress;

	@SerializedName("phone")
	@Expose
	private String phone;

	@SerializedName("activityTime")
	@Expose
	private String activityTime;

	public UserReport() {
		super();
		// TODO Auto-generated constructor stub
	}

	public UserReport(String name, String address, String postalAddress, String phone, String activityTime) {
		super();
		this.name = name;
		this.address = address;
		this.postalAddress = postalAddress;
		this.phone = phone;
		this.activityTime = activityTime;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getPostalAddress() {
		return postalAddress;
	}

	public void setPostalAddress(String postalAddress) {
		this.postalAddress = postalAddress;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getActivityTime() {
		return activityTime;
	}

	public void setActivityTime(String activityTime) {
		this.activityTime = activityTime;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((activityTime == null) ? 0 : activityTime.hashCode());
		result = prime * result + ((address == null) ? 0 : address.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((phone == null) ? 0 : phone.hashCode());
		result = prime * result + ((postalAddress == null) ? 0 : postalAddress.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UserReport other = (UserReport) obj;
		if (activityTime == null) {
			if (other.activityTime != null)
				return false;
		} else if (!activityTime.equals(other.activityTime))
			return false;
		if (address == null) {
			if (other.address != null)
				return false;
		} else if (!address.equals(other.address))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (phone == null) {
			if (other.phone != null)
				return false;
		} else if (!phone.equals(other.phone))
			return false;
		if (postalAddress == null) {
			if (other.postalAddress != null)
				return false;
		} else if (!postalAddress.equals(other.postalAddress))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "UserReport [name=" + name + ", address=" + address + ", postalAddress=" + postalAddress + ", phone="
				+ phone + ", activityTime=" + activityTime + "]";
	}

}
