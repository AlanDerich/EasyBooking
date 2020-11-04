package com.polycarp.cheprentals;

public class HouseBooking {
    String houseNo,ownerName,username,date_booked,phoneNo,plotName;

    public HouseBooking() {
    }

    public HouseBooking(String houseNo, String ownerName, String username, String date_booked, String phoneNo, String plotName) {
        this.houseNo = houseNo;
        this.ownerName = ownerName;
        this.username = username;
        this.date_booked = date_booked;
        this.phoneNo = phoneNo;
        this.plotName = plotName;
    }

    public String getPlotName() {
        return plotName;
    }

    public void setPlotName(String plotName) {
        this.plotName = plotName;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getHouseNo() {
        return houseNo;
    }

    public void setHouseNo(String houseNo) {
        this.houseNo = houseNo;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDate_booked() {
        return date_booked;
    }

    public void setDate_booked(String date_booked) {
        this.date_booked = date_booked;
    }
}
