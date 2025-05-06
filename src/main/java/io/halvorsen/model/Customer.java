package io.halvorsen.model;

public class Customer {

    private String name;
    private String email;
    private String address;

    // For fraud detection, usually a device fingerprint or some other metric is captured
    // at time of order submit
    private String deviceFingerprint;

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public String getDeviceFingerprint() {
        return deviceFingerprint;
    }
    public void setDeviceFingerprint(String deviceFingerprint) {
        this.deviceFingerprint = deviceFingerprint;
    }

}
