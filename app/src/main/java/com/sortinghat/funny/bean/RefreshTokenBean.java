package com.sortinghat.funny.bean;

public class RefreshTokenBean {

    private String authToken;
    private int days;


    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public int getDays() {
        return days;
    }

    public void setDays(int days) {
        this.days = days;
    }
}
