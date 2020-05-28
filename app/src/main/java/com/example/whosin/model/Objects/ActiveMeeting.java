package com.example.whosin.model.Objects;

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.RequiresApi;

import java.io.Serializable;

public class ActiveMeeting implements Serializable , Parcelable {

    private int year;
    private int month;
    private int day;
    private int hour;
    private int minute;
    private double lat;
    private double lon;
    private String id;
    private boolean isOpen;


    public ActiveMeeting() {
    }

    public ActiveMeeting(int year, int month, int day, int hour, int minute) {
        this.year = year;
        this.month = month;
        this.day = day;
        this.hour = hour;
        this.minute = minute;
    }


    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDay() {
        return day;
    }

    public int getHour() {
        return hour;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public void setLatLon(double lat , double lon){
        this.lat = lat;
        this.lon = lon;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }

    @Override
    public String toString() {
        return "ActiveMeeting{" +
                "year=" + year +
                ", month=" + month +
                ", day=" + day +
                ", hour=" + hour +
                ", minute=" + minute +
                '}';
    }

    @Override
    public int describeContents() {
        return this.hashCode();
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.day);
        dest.writeInt(this.hour);
        dest.writeInt(this.minute);
        dest.writeInt(this.month);
        dest.writeInt(this.year);
        dest.writeString(this.id);
//        try {
//            dest.writeBoolean(this.isOpen);
//        }catch (Exception e){
//            Log.d("TAG", "writeToParcel: " + e.toString());
//        }
        dest.writeDouble(this.lat);
        dest.writeDouble(this.lon);
    }

    protected ActiveMeeting(Parcel in) {
        year = in.readInt();
        month = in.readInt();
        day = in.readInt();
        hour = in.readInt();
        minute = in.readInt();
        lat = in.readDouble();
        lon = in.readDouble();
        id = in.readString();
        isOpen = in.readByte() != 0;
    }

    public static final Creator<ActiveMeeting> CREATOR = new Creator<ActiveMeeting>() {
        @Override
        public ActiveMeeting createFromParcel(Parcel in) {
            return new ActiveMeeting(in);
        }

        @Override
        public ActiveMeeting[] newArray(int size) {
            return new ActiveMeeting[size];
        }
    };
}
