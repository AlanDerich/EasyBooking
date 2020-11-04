package com.polycarp.cheprentals;

public class Plots {
    //electricity,rules/curfew,location,water,wifi,parking,rent,deposit;
    String plotName,location,water,wifi,electricity,plotImage,phoneNo,otherComments,owner;

    public Plots(String plotName, String location, String water, String wifi, String electricity, String plotImage, String phoneNo,String otherComments,String owner) {
        this.plotName = plotName;
        this.owner=owner;
        this.otherComments=otherComments;
        this.location = location;
        this.water = water;
        this.wifi = wifi;
        this.electricity = electricity;
        this.plotImage = plotImage;
        this.phoneNo = phoneNo;
    }

    public Plots() {
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getOtherComments() {
        return otherComments;
    }

    public void setOtherComments(String otherComments) {
        this.otherComments = otherComments;
    }

    public String getPlotName() {
        return plotName;
    }

    public void setPlotName(String plotName) {
        this.plotName = plotName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getWater() {
        return water;
    }

    public void setWater(String water) {
        this.water = water;
    }

    public String getWifi() {
        return wifi;
    }

    public void setWifi(String wifi) {
        this.wifi = wifi;
    }

    public String getElectricity() {
        return electricity;
    }

    public void setElectricity(String electricity) {
        this.electricity = electricity;
    }

    public String getPlotImage() {
        return plotImage;
    }

    public void setPlotImage(String plotImage) {
        this.plotImage = plotImage;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }
}
