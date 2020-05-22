package com.example.whosin.model.Objects;

public class AvailableUser {
    private double lan;
    private double lon;
    private User user;
    private String Sports;


    public AvailableUser() {
    }

    public AvailableUser(double lan, double lon, User user, String sports) {
        this.lan = lan;
        this.lon = lon;
        this.user = user;
        Sports = sports;
    }

    public double getLan() {
        return lan;
    }

    public void setLan(double lan) {
        this.lan = lan;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getSports() {
        return Sports;
    }

    public void setSports(String sports) {
        Sports = sports;
    }

    @Override
    public String toString() {
        return
                "user=" + user +
                ",lan=" + lan +
                ", lon=" + lon +
                ", Sports='" + Sports ;
    }
}
