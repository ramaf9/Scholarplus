package com.v1.firebase.scholarplus.model.profile;

/**
 * Created by MacBook on 6/25/16.
 */
public class Portfolio {
    private String name,photo;

    public Portfolio(String name, String photo) {
        this.name = name;
        this.photo = photo;
    }

    public Portfolio() {
    }

    public String getName() {
        return name;
    }

    public String getPhoto() {
        return photo;
    }
}
