package com.derich.hama;

public class HousePics {
    private String pic,plotName,owner,houseNumber;

    public HousePics(String pic, String plotName, String owner, String houseNumber) {
        this.pic = pic;
        this.plotName = plotName;
        this.owner = owner;
        this.houseNumber = houseNumber;
    }

    public HousePics() {
    }

    public String getHouseNumber() {
        return houseNumber;
    }

    public void setHouseNumber(String houseNumber) {
        this.houseNumber = houseNumber;
    }

    public String getPlotName() {
        return plotName;
    }

    public void setPlotName(String plotName) {
        this.plotName = plotName;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public HousePics(String pic) {
        this.pic = pic;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }
}
