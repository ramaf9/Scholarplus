package com.v1.firebase.scholarplus.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by sekartanjung on 6/11/16.
 */
public class Scholarship implements Serializable {

    private String nama,deskripsi,duedate,instansi,photopath,startdate,web,bgphotopath,kategori,asal,jenis;
    private HashMap<String,Object> kuantitatif;
    private ArrayList<String> berkas,persyaratankualitatif;


    public Scholarship(){

    }

    public String getNama() {
        return nama;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public String getDuedate() {
        return duedate;
    }

    public String getInstansi() {
        return instansi;
    }

    public String getPhotopath() {
        return photopath;
    }

    public String getBgphotopath() {
        return bgphotopath;
    }

    public String getStartdate() {
        return startdate;
    }

    public String getKategori() {
        return kategori;
    }

    public String getAsal() {
        return asal;
    }

    public String getJenis() {
        return jenis;
    }

    public String getWeb() {
        return web;
    }

    public ArrayList<String> getBerkas() {
        return berkas;
    }

    public ArrayList<String> getPersyaratankualitatif() {
        return persyaratankualitatif;
    }

    public HashMap<String, Object> getKuantitatif() {
        return kuantitatif;
    }

}
