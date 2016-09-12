package com.v1.firebase.scholarplus.model;

/**
 * Created by sekartanjung on 6/9/16.
 */
public class News {
    private String deskripsi,nama,photopath,source;

    public News(){

    }
    public News(String deskripsi, String nama, String photopath, String source) {

        this.deskripsi = deskripsi;
        this.nama = nama;
        this.photopath = photopath;
        this.source = source;
    }
    public String getDeskripsi() {
        return deskripsi;
    }

    public String getNama() {
        return nama;
    }

    public String getPhotopath() {
        return photopath;
    }

    public String getSource() {
        return source;
    }
}
