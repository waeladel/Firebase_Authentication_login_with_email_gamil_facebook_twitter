package com.getin.car.authentication;

import android.location.Location;
import android.text.TextUtils;

import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.ServerTimestamp;

import java.security.Timestamp;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by hp on 14/03/2018.
 */

public class Trip extends TripPostId{


    private GeoPoint origin;
    private GeoPoint destination;
    private String polyline;
    //private String Country;
    private Date date;
    private String label;
    private String details;
    private int seats;
    private int freeSeats;
    private int takenSeats;

    private String transportationType;
    private String transportationModel ;
    private int distance;
    private int duration;
    private Integer cost;


    private String gender ;
    private String chat ;
    private String cursing ;
    private String smoking ;
    private String music ;
    private String genre ;
    private String driving ;

    private String ownerId ;
    private String ownerName ;
    private String ownerPic ;
    //private Date  created;


    @ServerTimestamp
    private Date created;// anotation to put server timestamp

    public Trip() {}

    public Trip(GeoPoint origin, GeoPoint destination, String polyline) {
        // String
    }

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

    public String getPolyline() {
        return polyline;
    }

    public void setPolyline(String polyline) {
        this.polyline = polyline;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Date getDate() {
        return date;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public int getSeats() {
        return seats;
    }

    public void setSeats(int seats) {
        this.seats = seats;
    }

    /*public String getCountry() {
        return Country;
    }

    public void setCountry(String country) {
        Country = country;
    }*/

    public String getTransportationType() {
        return transportationType;
    }

    public void setTransportationType(String transportationType) {
        this.transportationType = transportationType;
    }

    public String getTransportationModel() {
        return transportationModel;
    }

    public void setTransportationModel(String transportationModel) {
        this.transportationModel = transportationModel;
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

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getOwnerPic() {
        return ownerPic;
    }

    public void setOwnerPic(String ownerPic) {
        this.ownerPic = ownerPic;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public Integer getCost() {
        return cost;
    }

    public void setCost(Integer cost) {
        this.cost = cost;
    }

    public int getFreeSeats() {
        return freeSeats;
    }

    public void setFreeSeats(int freeSeats) {
        this.freeSeats = freeSeats;
    }

    public int getTakenSeats() {
        return takenSeats;
    }

    public void setTakenSeats(int takenSeats) {
        this.takenSeats = takenSeats;
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

    public static int TimeToInteger(AlarmTime time) {
        Calendar c = time.calendar();
        int hourOfDay = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        int second = c.get(Calendar.SECOND);
        return hourOfDay * 3600 + minute * 60 + second;
    }

}
