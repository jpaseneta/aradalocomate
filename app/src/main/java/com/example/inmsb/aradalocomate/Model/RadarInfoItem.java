package com.example.inmsb.aradalocomate.Model;

import android.graphics.drawable.Drawable;

import com.example.inmsb.aradalocomate.R;

import java.util.Date;

/**
 * Created by inmsb on 1/4/2016.
 */
public class RadarInfoItem {
    private Date Date;
    private int LaneIndex;
    private int UnforeseenDetectionType;
    private double Longitude;
    private double Latitude;
    private double Distance;
    private String Info;
    private String EventName;


    private int WarningPhoto;

    public RadarInfoItem(int laneIndex, Date date, int unforeseenDetectionType, double longitude, double latitude, double distance) {
        this.LaneIndex = laneIndex;
        this.Date = date;
        this.UnforeseenDetectionType = unforeseenDetectionType;
        this.Longitude = longitude;
        this.Latitude = latitude;
        this.Distance = distance;
        Info="Latitude: "+String.format("%.2f",Latitude)+ " " + "Longitude: " + String.format("%.2f",Longitude) + System.getProperty("line.separator")
            + "Lane Index: " + Integer.toString(LaneIndex) + " " + "Distance: " + String.format("%.2f",Distance);
        this.WarningPhoto = getWarningPhotoDrawableResource(this.UnforeseenDetectionType);
    }

    public int getLaneIndex() {
        return LaneIndex;
    }

    public void setLaneIndex(int laneIndex) {
        LaneIndex = laneIndex;
    }

    public java.util.Date getDate() {
        return Date;
    }

    public void setDate(Date date) {
        Date = date;
    }

    public int getUnforeseenDetectionType() {
        return UnforeseenDetectionType;
    }

    public void setUnforeseenDetectionType(int unforeseenDetectionType) {
        UnforeseenDetectionType = unforeseenDetectionType;
    }

    public double getLongitude() {
        return Longitude;
    }

    public void setLongitude(double longitude) {
        Longitude = longitude;
    }

    public double getLatitude() {
        return Latitude;
    }

    public void setLatitude(double latitude) {
        Latitude = latitude;
    }

    public double getDistance() {
        return Distance;
    }

    public void setDistance(double distance) {
        Distance = distance;
    }

    public String getInfo() {
        return Info;
    }

    public String getEventName(){ return this.EventName; }
    public int getWarningPhoto() {
        return WarningPhoto;
    }

    public int getWarningPhotoDrawableResource(int unforeseenDetectionType){
        switch(unforeseenDetectionType){
            case 1:
                this.EventName = "Falling Objects";
                return R.drawable.debris;
            case 2:
                this.EventName = "Wrong way";
                return R.drawable.wrongway;
            case 3:
                this.EventName = "Pedestrian";
                return R.drawable.pedestrian;
            case 4:
                this.EventName = "Slow Moving Vehicle";
                return R.drawable.slowmovingvehicle;
            case 5:
                this.EventName = "Traffic Jam";
                return R.drawable.trafficjam;
        }
        return -1;
    }

}
