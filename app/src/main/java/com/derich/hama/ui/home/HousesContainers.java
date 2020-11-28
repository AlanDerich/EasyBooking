package com.derich.hama.ui.home;

public class HousesContainers {
    String rent,location,deposit,type,phoneNo, plotName, houseImage,owner,houseNumber;
//    String rent,location,deposit,details,type,phoneNo, plotName,plotImage;
    int status;

    public HousesContainers() {
    }

    public HousesContainers(String rent, String location, String deposit, String type, String phoneNo, String plotName, String houseImage, String owner, String houseNumber, int status) {
        this.rent = rent;
        this.location = location;
        this.deposit = deposit;
        this.type = type;
        this.phoneNo = phoneNo;
        this.plotName = plotName;
        this.houseImage = houseImage;
        this.owner = owner;
        this.houseNumber = houseNumber;
        this.status = status;
    }

    public String getHouseNumber() {
        return houseNumber;
    }

    public void setHouseNumber(String houseNumber) {
        this.houseNumber = houseNumber;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getHouseImage() {
        return houseImage;
    }

    public void setHouseImage(String houseImage) {
        this.houseImage = houseImage;
    }

    public String getRent() {
        return rent;
    }

    public void setRent(String rent) {
        this.rent = rent;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDeposit() {
        return deposit;
    }

    public void setDeposit(String deposit) {
        this.deposit = deposit;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getPlotName() {
        return plotName;
    }

    public void setPlotName(String plotName) {
        this.plotName = plotName;
    }
}
