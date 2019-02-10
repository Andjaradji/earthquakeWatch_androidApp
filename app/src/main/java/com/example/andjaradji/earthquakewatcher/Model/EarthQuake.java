package com.example.andjaradji.earthquakewatcher.Model;

/**
 * Created by Anjar on 16/12/2017.
 */

public class EarthQuake {
    private String eqLocation;
    private double eqMagnitude;
    private long eqTime;
    private String eqDetailURL;
    private String eqType;
    private double eqLat;
    private double eqLon;


    public EarthQuake() {

    }

    public EarthQuake(String eqLocation, double eqMagnitude, long eqTime, String eqDetailURL, String eqType, double eqLat, double eqLon) {
        this.eqLocation = eqLocation;
        this.eqMagnitude = eqMagnitude;
        this.eqTime = eqTime;
        this.eqDetailURL = eqDetailURL;
        this.eqType = eqType;
        this.eqLat = eqLat;
        this.eqLon = eqLon;
    }

    public String getEqLocation() {
        return eqLocation;
    }

    public void setEqLocation(String eqLocation) {
        this.eqLocation = eqLocation;
    }

    public double getEqMagnitude() {
        return eqMagnitude;
    }

    public void setEqMagnitude(double eqMagnitude) {
        this.eqMagnitude = eqMagnitude;
    }

    public long getEqTime() {
        return eqTime;
    }

    public void setEqTime(long eqTime) {
        this.eqTime = eqTime;
    }

    public String getEqDetailURL() {
        return eqDetailURL;
    }

    public void setEqDetailURL(String eqDetailURL) {
        this.eqDetailURL = eqDetailURL;
    }

    public String getEqType() {
        return eqType;
    }

    public void setEqType(String eqType) {
        this.eqType = eqType;
    }

    public double getEqLat() {
        return eqLat;
    }

    public void setEqLat(double eqLat) {
        this.eqLat = eqLat;
    }

    public double getEqLon() {
        return eqLon;
    }

    public void setEqLon(double eqLon) {
        this.eqLon = eqLon;
    }
}
