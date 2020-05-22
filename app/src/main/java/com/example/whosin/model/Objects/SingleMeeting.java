package com.example.whosin.model.Objects;

public class SingleMeeting {
    long lat;
    long lon;
    String Sport;

    public SingleMeeting(long lat, long lon, String sport) {
        this.lat = lat;
        this.lon = lon;
        Sport = sport;
    }

    public SingleMeeting() {
    }

    public long getLat() {
        return lat;
    }

    public void setLat(long lat) {
        this.lat = lat;
    }

    public long getLon() {
        return lon;
    }

    public void setLon(long lon) {
        this.lon = lon;
    }

    public String getSport() {
        return Sport;
    }

    public void setSport(String sport) {
        Sport = sport;
    }
}
