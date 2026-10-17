package com.example.afinal;

public class EmergencyContact {
    private String serviceName;
    private String phoneNumber;
    private boolean isPriority;
    private int serviceIconRes;

    public EmergencyContact(String serviceName, String phoneNumber, boolean isPriority) {
        this.serviceName = serviceName;
        this.phoneNumber = phoneNumber;
        this.isPriority = isPriority;
    }

    // Getters and setters
    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public boolean isPriority() {
        return isPriority;
    }

    public void setPriority(boolean priority) {
        isPriority = priority;
    }

    public int getServiceIconRes() {
        return serviceIconRes;
    }

    public void setServiceIconRes(int serviceIconRes) {
        this.serviceIconRes = serviceIconRes;
    }
}