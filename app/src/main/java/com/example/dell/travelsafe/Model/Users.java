package com.example.dell.travelsafe.Model;

public class Users {
    private String name,phone,password,image,emergencyno,vehicleNo,bloodgrp,allergies,surgeries,phoneOwner;

    public Users(){

    }

    public Users(String name, String phone, String password, String image, String emergencyno,String vehicleNo ,String bloodgrp, String allergies, String surgeries, String phoneOwner) {
        this.name = name;
        this.phone = phone;
        this.password = password;
        this.image = image;
        this.emergencyno = emergencyno;
        this.bloodgrp = bloodgrp;
        this.allergies = allergies;
        this.surgeries = surgeries;
        this.phoneOwner=phoneOwner;
        this.vehicleNo=vehicleNo;
    }

    public String getVehicleNo() {
        return vehicleNo;
    }
    public String getPhoneOwner() {
        return phoneOwner;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getPassword() {
        return password;
    }

    public String getImage() {
        return image;
    }

    public String getEmergencyno() {
        return emergencyno;
    }

    public String getBloodgrp() {
        return bloodgrp;
    }

    public String getAllergies() {
        return allergies;
    }

    public String getSurgeries() {
        return surgeries;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setEmergencyno(String emergencyno) {
        this.emergencyno = emergencyno;
    }

    public void setBloodgrp(String bloodgrp) {
        this.bloodgrp = bloodgrp;
    }

    public void setAllergies(String allergies) {
        this.allergies = allergies;
    }

    public void setSurgeries(String surgeries) {
        this.surgeries = surgeries;
    }
    public void setPhoneOwner(String phoneOwner) {
        this.phoneOwner = phoneOwner;
    }
}
