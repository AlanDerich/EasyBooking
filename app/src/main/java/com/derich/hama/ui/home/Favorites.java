package com.derich.hama.ui.home;

public class Favorites {
    String plotName,houseNumber,ownerName,username;

    public Favorites() {
    }

    public Favorites(String plotName, String houseNumber, String ownerName, String username) {
        this.plotName = plotName;
        this.houseNumber = houseNumber;
        this.ownerName = ownerName;
        this.username = username;
    }

    public String getPlotName() {
        return plotName;
    }

    public void setPlotName(String plotName) {
        this.plotName = plotName;
    }

    public String getHouseNumber() {
        return houseNumber;
    }

    public void setHouseNumber(String houseNumber) {
        this.houseNumber = houseNumber;
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
}
