package es.unex.spilab.contigo.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MsgMQTT {

	@SerializedName("resource")
	@Expose
	private String resource;
	@SerializedName("method")
	@Expose
	private String method;
	@SerializedName("sender")
	@Expose
	private String sender;
	@SerializedName("idRequest")
	@Expose
	private Integer idRequest;
	@SerializedName("params")
	@Expose
	private Params params;

	public MsgMQTT() {
		super();
		// TODO Auto-generated constructor stub
	}

	public MsgMQTT(String resource, String method, String sender, Integer idRequest, Params params) {
		super();
		this.resource = resource;
		this.method = method;
		this.sender = sender;
		this.idRequest = idRequest;
		this.params = params;
	}

	public String getResource() {
		return resource;
	}

	public void setResource(String resource) {
		this.resource = resource;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public Integer getIdRequest() {
		return idRequest;
	}

	public void setIdRequest(Integer idRequest) {
		this.idRequest = idRequest;
	}

	public Params getParams() {
		return params;
	}

	public void setParams(Params params) {
		this.params = params;
	}

	@Override
	public String toString() {
		return "MsgMQTT [resource=" + resource + ", method=" + method + ", sender=" + sender + ", idRequest="
				+ idRequest + ", params=" + params + "]";
	}

}