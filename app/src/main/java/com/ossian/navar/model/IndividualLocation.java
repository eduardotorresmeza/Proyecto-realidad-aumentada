package com.ossian.navar.model;

import com.mapbox.mapboxsdk.geometry.LatLng;

/**
 * POJO class for an individual location
 */
public class IndividualLocation {

    private String name;
    private String address;
    private String hours;
    private String phoneNum;
    private String distance;
    private String imageurlcv;
    private String imageone;
    private String imagetwo;
    private String imagethree;
    private String typepoi;
    private String description;
    private String keypoi;
    private LatLng location;


    public IndividualLocation(String name, String address, String hours, String phoneNum, String imagecv,
                              String imageone, String imagetwo, String imagethree, String typepoi, String description,
                              String keypoi, LatLng location) {
        this.name = name;
        this.address = address;
        this.hours = hours;
        this.phoneNum = phoneNum;
        this.imageurlcv = imagecv;
        this.imageone = imageone;
        this.imagetwo = imagetwo;
        this.imagethree = imagethree;
        this.typepoi = typepoi;
        this.description = description;
        this.keypoi = keypoi;
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public String getHours() {
        return hours;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public String getImageurlcv() {
        return imageurlcv;
    }

    public String getImageone() {
        return imageone;
    }

    public String getImagetwo() {
        return imagetwo;
    }

    public String getImagethree() {
        return imagethree;
    }

    public String getTypepoi() {
        return typepoi;
    }

    public String getDescription() {
        return description;
    }

    public String getKeypoi() {
        return keypoi;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public LatLng getLocation() {
        return location;
    }


}
