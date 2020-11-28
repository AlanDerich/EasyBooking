package com.derich.hama;

public class OfferDetails {
    private String pic,offerName,newPrice,location,details,owner;
    public OfferDetails() {
    }

    public OfferDetails(String pic, String offerName, String newPrice, String location, String details, String owner) {
        this.pic = pic;
        this.offerName = offerName;
        this.newPrice = newPrice;
        this.location = location;
        this.details = details;
        this.owner = owner;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getNewPrice() {
        return newPrice;
    }

    public void setNewPrice(String newPrice) {
        this.newPrice = newPrice;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getOfferName() {
        return offerName;
    }

    public void setOfferName(String offerName) {
        this.offerName = offerName;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }
}
