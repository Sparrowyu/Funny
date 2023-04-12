package com.sortinghat.funny.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;



public class BannerBean implements Serializable {
    @SerializedName(value = "cover", alternate = {"image"})
    public String cover;
    public String url;

    public BannerBean(String cover, String url) {
        this.cover = cover;
        this.url = url;
    }
}
