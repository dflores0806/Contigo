package es.unex.spilab.contigo.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ParamsAlert {

	@SerializedName("alert")
	@Expose
	private Alert alert;

	public ParamsAlert(Alert alert) {
		super();
		this.alert = alert;
	}

	public Alert getAlert() {
		return alert;
	}

	public void setAlert(Alert alert) {
		this.alert = alert;
	}

	@Override
	public String toString() {
		return "ParamsAlert [alert=" + alert + "]";
	}

}