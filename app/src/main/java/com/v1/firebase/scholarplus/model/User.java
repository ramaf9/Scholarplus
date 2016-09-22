package com.v1.firebase.scholarplus.model;

import com.v1.firebase.scholarplus.utils.Constants;

import java.util.HashMap;

/**
 * Created by sekartanjung on 6/6/16.
 */
public class User {
    private String
            name;
    private String dob;
    private String province;

    private String photo;
    private String cv;
    private String pitch;

    private String city;
    private String email;
    private String institut;
    private String fakultas;
    private String jurusan;
    private String semester;
    private String ipk;

    private HashMap<String, Object> timestampJoined;


    /**
     * Required public constructor
     */
    public User() {
    }


    /**
     * Use this constructor to create new User.
     * Takes user name, email and timestampJoined as params
     *

     * @param timestampJoined
     */
    public User(HashMap<String,Object> profile, HashMap<String, Object> timestampJoined) {
        try {
            this.name = profile.get(Constants.FIREBASE_PROPERTY_NAME).toString();
            this.email = profile.get(Constants.FIREBASE_PROPERTY_EMAIL).toString();
            this.institut = profile.get(Constants.FIREBASE_PROPERTY_INSTITUT).toString();
            this.fakultas = profile.get(Constants.FIREBASE_PROPERTY_FAKULTAS).toString();
            this.jurusan = profile.get(Constants.FIREBASE_PROPERTY_JURUSAN).toString();
            this.city = profile.get(Constants.FIREBASE_PROPERTY_CITY).toString();
            this.dob = profile.get(Constants.FIREBASE_PROPERTY_DOB).toString();
            this.semester = profile.get(Constants.FIREBASE_PROPERTY_SEMESTER).toString();
            this.ipk = profile.get(Constants.FIREBASE_PROPERTY_IPK).toString();
            this.province = profile.get(Constants.FIREBASE_PROPERTY_PROVINCE).toString();
            this.cv = profile.get(Constants.FIREBASE_PROPERTY_CV).toString();


            this.timestampJoined = timestampJoined;
        }
        catch (Exception e){

        }
    }
    public String getDob() {
        return dob;
    }

    public String getCity() {
        return city;
    }

    public String getProvince() {
        return province;
    }

    public String getSemester() {
        return semester;
    }

    public String getIpk() {
        return ipk;
    }

    public String getName() {
        return name;
    }

    public String getFakultas() {
        return fakultas;
    }

    public String getJurusan() {
        return jurusan;
    }

    public String getInstitut() {return institut;}

    public String getEmail() {
        return email;
    }

    public String getPhoto() {
        return photo;
    }

    public String getCv() {
        return cv;
    }

    public HashMap<String, Object> getTimestampJoined() {
        return timestampJoined;
    }

    public String getPitch() {
        return pitch;
    }
}
