package com.msk.adopt4k.utils;


import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.google.gson.JsonObject;

import java.io.IOException;
import java.io.InputStreamReader;

import au.com.bytecode.opencsv.CSVReader;

public class DBHelper extends SQLiteOpenHelper {

    private static String DB_FILENAME = "4kgeodata.sqlite";

    private Context mContext;
    private SQLiteDatabase db;

    public DBHelper(Context context) {
        super(context, DB_FILENAME, null, 1);
        mContext = context;
    } // constructor

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE \"geodata\" (\"Object_ID\" INTEGER, \"World_ID\" TEXT, \"Zone_name\" TEXT, \"Cnty_ID\" TEXT, \"Cnty_Name\" TEXT, \"Cen_x\" REAL, \"Cen_y\" REAL, \"Shape_Area\" REAL, \"Coords\" TEXT)");
        db.execSQL("CREATE TABLE \"userinfo\" (\"key\" TEXT, \"value\" TEXT)");
        db.execSQL("CREATE TABLE \"adoptions\" (\"worldid\" TEXT, \"targetyear\" INTEGER, \"created\" DATETIME)");

        AssetManager manager = mContext.getAssets();

        try {
            CSVReader csvReader = new CSVReader(new InputStreamReader(manager.open("eastasia.csv")), '\t');

            String[] line;
            while((line = csvReader.readNext()) != null) {
                String objectID = line[0];
                String worldID = line[1];
                String zoneName = line[2];
                String cntyID = line[3];
                String cntyName = line[4];
                String cenX = line[5];
                String cenY = line[6];
                String shapeArea = line[7];
                String coords = line[8];

                db.execSQL("INSERT INTO 'geodata' VALUES ("
                        + objectID + ","
                        + "\"" + worldID + "\","
                        + "\"" + zoneName + "\","
                        + "\"" + cntyID + "\","
                        + "\"" + cntyName + "\","
                        + cenX + ","
                        + cenY + ","
                        + shapeArea + ","
                        + "\"" + coords + "\")"
                );
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        db.execSQL("INSERT INTO userinfo(key, value) VALUES('loginstate', 'false')");
        db.execSQL("INSERT INTO userinfo(key, value) VALUES('name', '')");
        db.execSQL("INSERT INTO userinfo(key, value) VALUES('email', '')");
        db.execSQL("INSERT INTO userinfo(key, value) VALUES('apikey', '')");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }



    public boolean getLoginstate() {

        db = getReadableDatabase();

        Cursor c = db.rawQuery("select value from userinfo where key='loginstate'", null);
        if(c.moveToFirst()) {
            String state = c.getString(c.getColumnIndex("value"));

            if(state.equals("true")) {
                return true;
            }
        }

        return false;
    }

    public void insertUserInfo(String name, String email, String apiKey) {

        db = getWritableDatabase();

        db.execSQL("update userinfo set value='"+name+"' where key='name'");
        db.execSQL("update userinfo set value='"+email+"' where key='email'");
        db.execSQL("update userinfo set value='"+apiKey+"' where key='apikey'");

        // TODO login check process

        db.execSQL("update userinfo set value='true' where key='loginstate'");
    }

    public JsonObject getUserInfo() {
        db = getReadableDatabase();

        JsonObject userinfo = new JsonObject();
        Cursor c = db.rawQuery("select * from userinfo", null);

        while(c.moveToNext()) {
            String key = c.getString(c.getColumnIndex("key"));
            String value = c.getString(c.getColumnIndex("value"));

            userinfo.addProperty(key, value);
        }

        return userinfo;
    }

    public JsonObject getGeoData(String worldID) {
        db = getReadableDatabase();

        JsonObject geodata = new JsonObject();

        Cursor c = db.rawQuery("SELECT * FROM geodata WHERE World_ID = \"" + worldID + "\"", null);
        if(c.moveToNext()) {

            geodata.addProperty(c.getColumnName(0), c.getInt(0));//Object_ID
            geodata.addProperty(c.getColumnName(1), c.getString(1));//World_ID
            geodata.addProperty(c.getColumnName(2), c.getString(2));//Zone_name
            geodata.addProperty(c.getColumnName(3), c.getString(3));//Cnty_ID
            geodata.addProperty(c.getColumnName(4), c.getString(4));//Cnty_name
            geodata.addProperty(c.getColumnName(5), c.getDouble(5));//Cen_x
            geodata.addProperty(c.getColumnName(6), c.getDouble(6));//Cen_y
            geodata.addProperty(c.getColumnName(7), c.getDouble(7));//Shape_Area
            geodata.addProperty(c.getColumnName(8), c.getString(8));//Coords
        }

        return geodata;
    }

} // class

