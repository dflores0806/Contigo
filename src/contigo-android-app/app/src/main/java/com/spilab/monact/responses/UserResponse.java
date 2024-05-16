package com.spilab.monact.responses;



import com.google.gson.annotations.SerializedName;



public class UserResponse {

    @SerializedName("resource")
    private String resource;

    @SerializedName("method")
    private String method;

    @SerializedName("params")
    private Params params;

    @SerializedName("sender")
    private String sender;

    @SerializedName("idRequest")
    private Integer idRequest;

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

    public Params getParams() {
        return params;
    }

    public void setParams(Params params) {
        this.params = params;
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

    public class Params {
    
      
        @SerializedName("latitude")
        private Double latitude;

      
        @SerializedName("longitude")
        private Double longitude;

      
        @SerializedName("radius")
        private Double radius;

      
        @SerializedName("minActivityTime")
        private Double minActivityTime;

      
        @SerializedName("range")
        private Long range;



      
        public Double getlatitude() {
            return latitude;
        }

      
        public Double getlongitude() {
            return longitude;
        }

      
        public Double getradius() {
            return radius;
        }

      
        public Double getminActivityTime() {
            return minActivityTime;
        }

      
        public Long getrange() {
            return range;
        }


    }


}



