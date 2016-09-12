package com.v1.firebase.scholarplus.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by sekartanjung on 6/5/16.
 */
public class Constants {

    public static final String KEY_ENCODED_EMAIL = "ENCODED_EMAIL";
    public static final String KEY_PROVIDER = "PROVIDER";
    public static final String GOOGLE_PROVIDER = "google";
    public static final String KEY_SIGNUP_EMAIL = "SIGNUP_EMAIL";
    public static final String KEY_USERS = "users";
    public static final String KEY_ID = "id";
    public static final String KEY_SEARCH = "search";
    public static final String KEY_ASAL = "asal";
    public static final String KEY_DUEDATE = "duedate";
    public static final String KEY_NAMA = "nama";



    public static final String FIREBASE_PROPERTY_NAME = "name";
    public static final String FIREBASE_PROPERTY_EMAIL = "email";
    public static final String FIREBASE_PROPERTY_INSTITUT = "institut";
    public static final String FIREBASE_PROPERTY_FAKULTAS = "fakultas";
    public static final String FIREBASE_PROPERTY_JURUSAN = "jurusan";
    public static final String FIREBASE_PROPERTY_CITY = "city";
    public static final String FIREBASE_PROPERTY_DOB = "dob";
    public static final String FIREBASE_PROPERTY_SEMESTER = "semester";
    public static final String FIREBASE_PROPERTY_IPK = "ipk";
    public static final String FIREBASE_PROPERTY_PROVINCE = "province";
    public static final String FIREBASE_PROPERTY_PHOTO = "photo";
    public static final String FIREBASE_PROPERTY_CV = "cv";


    public static final String FIREBASE_PROPERTY_BIDANG = "bidang";

    public static final String FIREBASE_PROPERTY_CONNECT = "connect";
    public static final String FIREBASE_PROPERTY_CONNECTC = "connect-comments";

    public static final String FIREBASE_PROPERTY_TIMESTAMP = "timestamp";
    public static final String FIREBASE_PROPERTY_KUANTITATIF = "kuantitatif";
    public static final String FIREBASE_PROPERTY_BEASISWA = "beasiswa";

    public static final String VALUE_ASAL_DN = "Dalam negeri";
    public static final String VALUE_ASAL_LN = "Luar negeri";


    public static final String DETAIL_PHOTO = "newsphoto";
    public static final String DETAIL_BY = "newsby";
    public static final String DETAIL_DESC = "newsdesc";
    public static final String DETAIL_NAME = "newsname";
    public static final String DETAIL_BG_PHOTO = "bgphoto";


    public static String getMonth(String date) throws ParseException {
        Date d = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).parse(date);
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        String monthName = new SimpleDateFormat("MMMM d ,yyyy",Locale.ENGLISH).format(cal.getTime());
        return monthName;
    }
    public static String getAge(String date) throws ParseException{
        String age = "";
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
        Calendar cal = Calendar.getInstance();
        String dateCurrent = format.format(cal.getTime());
        Date d1 = null;
        Date d2 = null;
        try {
            d1 = format.parse(date);
            d2 = format.parse(dateCurrent);

            long diff = d2.getTime() - d1.getTime();
            double diffyears = diff / (24 * 60 * 60 * 1000 *  365.25);

            int temp = (int) diffyears;
            age = String.valueOf(temp);


        }
        catch (Exception e)  {

        }
        return age;
    }

    public static int compareDate(String a,String b) {
        int i = 0;
        try{
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");


            String str1 = a;
            Date date1 = formatter.parse(str1);

            String str2 = b;
            Date date2 = formatter.parse(str2);

            i = date1.compareTo(date2);
        }
        catch (ParseException e){
            e.printStackTrace();
        }

        return i;


    }

}
