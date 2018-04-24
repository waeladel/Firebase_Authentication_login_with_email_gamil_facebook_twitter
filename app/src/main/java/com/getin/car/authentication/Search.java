package com.getin.car.authentication;

import android.text.TextUtils;

import com.google.firebase.firestore.GeoPoint;

import java.util.Date;

/**
 * Created by hp on 28/03/2018.
 */

public class Search {

    private GeoPoint origin;
    private GeoPoint destination;
    private Date fromDate;
    private Date tillDate;
    private String transportationType;
    private Integer minCost;
    private Integer maxCost;

    private String gender ;
    private String chat ;
    private String cursing ;
    private String smoking ;
    private String music ;
    private String driving ;

    public GeoPoint getOrigin() {
        return origin;
    }

    public void setOrigin(GeoPoint origin) {
        this.origin = origin;
    }

    public GeoPoint getDestination() {
        return destination;
    }

    public void setDestination(GeoPoint destination) {
        this.destination = destination;
    }

    public Date getFromDate() {
        return fromDate;
    }

    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    public Date getTillDate() {
        return tillDate;
    }

    public void setTillDate(Date tillDate) {
        this.tillDate = tillDate;
    }

    public String getTransportationType() {
        return transportationType;
    }

    public void setTransportationType(String transportationType) {
        this.transportationType = transportationType;
    }

    public Integer getMinCost() {
        return minCost;
    }

    public void setMinCost(Integer minCost) {
        this.minCost = minCost;
    }

    public Integer getMaxCost() {
        return maxCost;
    }

    public void setMaxCost(Integer maxCost) {
        this.maxCost = maxCost;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getChat() {
        return chat;
    }

    public void setChat(String chat) {
        this.chat = chat;
    }

    public String getCursing() {
        return cursing;
    }

    public void setCursing(String cursing) {
        this.cursing = cursing;
    }

    public String getSmoking() {
        return smoking;
    }

    public void setSmoking(String smoking) {
        this.smoking = smoking;
    }

    public String getMusic() {
        return music;
    }

    public void setMusic(String music) {
        this.music = music;
    }

    public String getDriving() {
        return driving;
    }

    public void setDriving(String driving) {
        this.driving = driving;
    }

    public boolean hasTransportationType() {
        return !(TextUtils.isEmpty(transportationType));
    }

    public boolean hasGender() {
        return !(TextUtils.isEmpty(gender));
    }

    public boolean hasChat() {
        return !(TextUtils.isEmpty(chat));
    }

    public boolean hasCursing() {
        return !(TextUtils.isEmpty(cursing));
    }

    public boolean hasSmoking() {
        return !(TextUtils.isEmpty(smoking));
    }

    public boolean hasMusic() {
        return !(TextUtils.isEmpty(music));
    }

    public boolean hasDriving() {
        return !(TextUtils.isEmpty(driving));
    }

}
