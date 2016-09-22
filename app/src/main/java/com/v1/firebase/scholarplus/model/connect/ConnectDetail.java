package com.v1.firebase.scholarplus.model.connect;

/**
 * Created by MacBook on 6/17/16.
 */
public class ConnectDetail {
    private String body,uid;

    public ConnectDetail() {
    }

    public ConnectDetail(String body, String uid) {
        this.body = body;

        this.uid = uid;
    }

    public String getBody() {
        return body;
    }



    public String getUid() {
        return uid;
    }


}
