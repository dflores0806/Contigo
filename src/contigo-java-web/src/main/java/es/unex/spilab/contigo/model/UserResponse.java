package es.unex.spilab.contigo.model;

import java.util.List;

import com.google.gson.annotations.SerializedName;
import com.google.maps.model.LatLng;

public class UserResponse {

	@SerializedName("idRequest")
	private Integer idRequest = null;
	@SerializedName("uuid")
	private String uuid = null;
	@SerializedName("name")
	private String name = null;
	@SerializedName("age")
	private int age = 0;
	@SerializedName("genre")
	private int genre = 0; // 0:male, 1:female
	@SerializedName("location")
	private LatLng location = null;
	@SerializedName("address")
	private String address = null;
	@SerializedName("phone")
	private String phone = null;
	@SerializedName("postalAddress")
	private String postalAddress = null;
	@SerializedName("state")
	private Boolean state = null;
	@SerializedName("activityTime")
	private Long activityTime = null;

	// Extension for medical sensors (Software X)
	@SerializedName("healthParams")
	private List<UserHealth> healthParams = null;

	public UserResponse() {
		super();
		// TODO Auto-generated constructor stub
	}

	public UserResponse(Integer idRequest, String uuid, String name, int age, int genre, LatLng location,
			String address, String phone, String postalAddress, Boolean state, Long activityTime,
			List<UserHealth> heathParams) {
		super();
		this.idRequest = idRequest;
		this.uuid = uuid;
		this.name = name;
		this.age = age;
		this.genre = genre;
		this.location = location;
		this.address = address;
		this.phone = phone;
		this.postalAddress = postalAddress;
		this.state = state;
		this.activityTime = activityTime;
		this.healthParams = heathParams;
	}

	public Integer getIdRequest() {
		return idRequest;
	}

	public void setIdRequest(Integer idRequest) {
		this.idRequest = idRequest;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public int getGenre() {
		return genre;
	}

	public void setGenre(int genre) {
		this.genre = genre;
	}

	public LatLng getLocation() {
		return location;
	}

	public void setLocation(LatLng location) {
		this.location = location;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getPostalAddress() {
		return postalAddress;
	}

	public void setPostalAddress(String postalAddress) {
		this.postalAddress = postalAddress;
	}

	public Boolean getState() {
		return state;
	}

	public void setState(Boolean state) {
		this.state = state;
	}

	public Long getActivityTime() {
		return activityTime;
	}

	public void setActivityTime(Long activityTime) {
		this.activityTime = activityTime;
	}

	public List<UserHealth> getHeathParams() {
		return healthParams;
	}

	public void setHealthParams(List<UserHealth> heathParams) {
		this.healthParams = heathParams;
	}

	public int compareTo(Object o) {
		// TODO Auto-generated method stub
		String comparecp = ((UserResponse) o).getPostalAddress();
		/* For Ascending order */

		return this.postalAddress.compareTo(comparecp);

		/* For Descending order do like this */
		// return compareage-this.studentage;

	}

	public int compareTo1(Object o) {
		// TODO Auto-generated method stub
		return 0;
	}

}