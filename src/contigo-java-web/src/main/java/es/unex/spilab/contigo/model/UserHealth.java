package es.unex.spilab.contigo.model;

import java.util.Objects;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserHealth {

	/**
	 * Timestamp of the measurement
	 */
	@SerializedName("timestamp")
	@Expose
	private String timestamp;

	/**
	 * Age of the person
	 */
	@SerializedName("age")
	@Expose
	private int age;

	/**
	 * Sex: 0=man; 1=woman
	 */
	@SerializedName("sex")
	@Expose
	private int sex;

	/**
	 * Blood presure
	 */
	@SerializedName("trestbps")
	@Expose
	private int thresbps;

	/**
	 * Fasting blood sugar > 120mg/dl: 0=no; 1=yes
	 */
	@SerializedName("fbs")
	@Expose
	private int fbs;

	/**
	 * Maximum heart rate achieved
	 */
	@SerializedName("thalach")
	@Expose
	private int thalach;

	/**
	 * Pathological risk: 0 = normal; 1 = fixed defect; 2 = reversible defect
	 */
	@SerializedName("thal")
	@Expose
	private int thal;

	/**
	 * The predicted value is based on the likelihood of a heart attack occurring: -
	 * Value 0: < 50% diameter narrowing in any major vessel - Value 1: > 50%
	 * diameter narrowing in any major vessel
	 */
	@SerializedName("target")
	@Expose
	private int target;

	/**
	 * If the prediction is valid or not considering the expert criteria: 0 = not
	 * valid; 1 = valid
	 */
	@SerializedName("valid")
	@Expose
	private int valid;

	/**
	 * Accuracy
	 */
	@SerializedName("precision")
	@Expose
	private int precision;

	public UserHealth() {
		this.timestamp = null;
		this.age = 0;
		this.sex = 0;
		this.thresbps = 0;
		this.fbs = 0;
		this.thalach = 0;
		this.thal = 0;
		this.target = 0;
		this.precision = 0;
	}

	public UserHealth(String timestamp, int age, int sex, int thresbps, int fbs, int thalach, int thal, int target,
			int valid, int precision) {
		super();
		this.timestamp = timestamp;
		this.age = age;
		this.sex = sex;
		this.thresbps = thresbps;
		this.fbs = fbs;
		this.thalach = thalach;
		this.thal = thal;
		this.target = target;
		this.valid = valid;
		this.precision = precision;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public int getSex() {
		return sex;
	}

	public void setSex(int sex) {
		this.sex = sex;
	}

	public int getThresbps() {
		return thresbps;
	}

	public void setThresbps(int thresbps) {
		this.thresbps = thresbps;
	}

	public int getFbs() {
		return fbs;
	}

	public void setFbs(int fbs) {
		this.fbs = fbs;
	}

	public int getThalach() {
		return thalach;
	}

	public void setThalach(int thalach) {
		this.thalach = thalach;
	}

	public int getThal() {
		return thal;
	}

	public void setThal(int thal) {
		this.thal = thal;
	}

	public int getTarget() {
		return target;
	}

	public void setTarget(int target) {
		this.target = target;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public int getValid() {
		return valid;
	}

	public void setValid(int valid) {
		this.valid = valid;
	}

	public int getPrecision() {
		return precision;
	}

	public void setPrecision(int precision) {
		this.precision = precision;
	}

	@Override
	public int hashCode() {
		return Objects.hash(age, fbs, precision, sex, target, thal, thalach, thresbps, timestamp, valid);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UserHealth other = (UserHealth) obj;
		return age == other.age && fbs == other.fbs && precision == other.precision && sex == other.sex
				&& target == other.target && thal == other.thal && thalach == other.thalach
				&& thresbps == other.thresbps && Objects.equals(timestamp, other.timestamp) && valid == other.valid;
	}

	@Override
	public String toString() {
		return "UserHealth [timestamp=" + timestamp + ", age=" + age + ", sex=" + sex + ", thresbps=" + thresbps
				+ ", fbs=" + fbs + ", thalach=" + thalach + ", thal=" + thal + ", target=" + target + ", valid=" + valid
				+ ", precision=" + precision + "]";
	}

}
