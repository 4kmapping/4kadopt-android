package com.msk.adopt4k.utils;

import android.content.Context;
import android.util.Base64;

import com.google.gson.JsonObject;

public class UserinfoHelper {
    private Context mContext;

    public UserinfoHelper(Context context) {
        mContext = context;
    }

    public String getUsername() {
        DBHelper dbHelper = new DBHelper(mContext);
        JsonObject userInfo = dbHelper.getUserInfo();

        return userInfo.get("name").getAsString();
    }

    public String getUseremail() {
        DBHelper dbHelper = new DBHelper(mContext);
        JsonObject userInfo = dbHelper.getUserInfo();

        return userInfo.get("email").getAsString();
    }

    public String getCredential() {
        String username = getUseremail();
        String apikey = getUserApiKey();

        String credential = username+":"+apikey;
        return Base64.encodeToString(credential.getBytes(), Base64.DEFAULT);
    }

    public String getUserApiKey() {
        DBHelper dbHelper = new DBHelper(mContext);
        JsonObject userInfo = dbHelper.getUserInfo();

        return userInfo.get("apikey").getAsString();
    }
}
