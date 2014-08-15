package com.msk.adopt4k.utils;


import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.io.InputStreamReader;

import au.com.bytecode.opencsv.CSVReader;

public class DBHelper extends SQLiteOpenHelper {

    private Context mContext;
    private SQLiteDatabase db;

    public DBHelper(Context context) {
        super(context, "4kgeodata.sqlite", null, 7);
        mContext = context;
    } // constructor

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE geodata (World TEXT, World_ID TEXT, Zone_name TEXT, Population INTEGER, Cnty_ID TEXT, Cnty_Name TEXT, Cen_x REAL, Cen_y REAL, Shape_Area REAL, Coords TEXT)");
        db.execSQL("CREATE TABLE userinfo (key TEXT, value TEXT)");
        db.execSQL("CREATE TABLE adoptions (worldid TEXT, targetyear INTEGER, created DATETIME DEFAULT_TIMESTAMP, world TEXT, population INTEGER, zone_name TEXT, cnty_name TEXT, url TEXT)");

        AssetManager manager = mContext.getAssets();

        try {
            CSVReader csvReader = new CSVReader(new InputStreamReader(manager.open("ozfeatures.csv")), '\t');

            String[] line;
            while((line = csvReader.readNext()) != null) {
                String world = line[0].trim();
                String worldID = line[1].trim();
                String zoneName = line[2].trim();
                String population = line[3].trim();
                String cntyID = line[4].trim();
                String cntyName = line[5].trim();
                String cenX = line[6].trim();
                String cenY = line[7].trim();
                String shapeArea = line[8].trim();
                String coords = line[9].trim();

                db.execSQL("INSERT INTO geodata VALUES ("
                        + "\"" + world + "\","
                        + "\"" + worldID + "\","
                        + "\"" + zoneName + "\","
                        + population + ","
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
        db.execSQL("INSERT INTO userinfo(key, value) VALUES('uid', '')");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE geodata");
        db.execSQL("DROP TABLE userinfo");
        db.execSQL("DROP TABLE adoptions");

        onCreate(db);
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

    public void insertUserInfo(String name, String email, String apiKey, int uid) {

        db = getWritableDatabase();

        db.execSQL("update userinfo set value='"+name+"' where key='name'");
        db.execSQL("update userinfo set value='"+email+"' where key='email'");
        db.execSQL("update userinfo set value='"+apiKey+"' where key='apikey'");
        db.execSQL("update userinfo set value='"+uid+"' where key='uid'");

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

    public JsonObject   getGeoData(String worldID) {
        db = getReadableDatabase();

        JsonObject geodata = new JsonObject();

        Cursor c = db.rawQuery("SELECT * FROM geodata WHERE World_ID = \"" + worldID + "\"", null);
        if(c.moveToNext()) {

            geodata.addProperty(c.getColumnName(0), c.getString(0));//World
            geodata.addProperty(c.getColumnName(1), c.getString(1));//World_ID
            geodata.addProperty(c.getColumnName(2), c.getString(2));//Zone_name
            geodata.addProperty(c.getColumnName(3), c.getString(3));//Population
            geodata.addProperty(c.getColumnName(4), c.getString(4));//Cnty_ID
            geodata.addProperty(c.getColumnName(5), c.getString(5));//Cnty_name
            geodata.addProperty(c.getColumnName(6), c.getDouble(6));//Cen_x
            geodata.addProperty(c.getColumnName(7), c.getDouble(7));//Cen_y
            geodata.addProperty(c.getColumnName(8), c.getDouble(8));//Shape_Area
            geodata.addProperty(c.getColumnName(9), c.getString(9));//Coords

            return geodata;
        }

        return null;
    }

    public void insertAdoption(String worldID, int targetYear, String url) {
        GeodataHelper geodataHelper = new GeodataHelper(mContext);
        String world = geodataHelper.getWorld(worldID);
        int population = (int) geodataHelper.getPopulation(worldID);
        String zoneName = geodataHelper.getZoneName(worldID);
        String cntyName = geodataHelper.getCntyName(worldID);

        db = getWritableDatabase();

        db.execSQL("INSERT INTO adoptions(worldid, targetyear, world, population, zone_name, cnty_name, url) VALUES('"+worldID+"',"+targetYear+", '"+world+"', "+population+", '"+zoneName+"', '"+cntyName+"', '"+ url + "')");
    }

    public int countAdoption() {
        db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT COUNT(worldid) from adoptions", null);
        if(c.moveToNext()) {
            return c.getInt(0);
        }

        return 0;
    }

    public void updateAdoption(JsonArray adoptions) {
        db = getWritableDatabase();
        db.execSQL("DROP TABLE adoptions");
        db.execSQL("CREATE TABLE adoptions (worldid TEXT, targetyear INTEGER, created DATETIME DEFAULT_TIMESTAMP, world TEXT, population INTEGER, zone_name TEXT, cnty_name TEXT, url TEXT)");

        for(JsonElement adoption:adoptions) {
            JsonObject item = (JsonObject) adoption;

            String worldID = item.get("worldid").getAsString();
            int targetYear = item.get("targetyear").getAsInt();
            GeodataHelper geodataHelper = new GeodataHelper(mContext);
            String world = geodataHelper.getWorld(worldID);
            int population = (int) geodataHelper.getPopulation(worldID);
            String zoneName = item.get("oz_zone_name").getAsString();
            String cntyName = item.get("oz_country_name").getAsString();
            String url = item.get("url").getAsString();


            db.execSQL("INSERT INTO adoptions(worldid, targetyear, world, population, zone_name, cnty_name, url) VALUES('"+worldID+"',"+targetYear+", '"+world+"', "+population+", '"+zoneName+"', '"+cntyName+"', '"+ url + "')");
        }

    }

    public void deleteAdoption(String ozid) {
        db = getWritableDatabase();
        db.execSQL("DELETE FROM adoptions WHERE worldid = '"+ozid+"'");
    }

    public JsonArray getAdoptions() {
        db = getReadableDatabase();
        JsonArray adoptions = new JsonArray();
        Cursor c = db.rawQuery("SELECT * FROM adoptions", null);
        while(c.moveToNext()) {
            JsonObject adoption = new JsonObject();
            adoption.addProperty(c.getColumnName(0), c.getString(0));
            adoption.addProperty(c.getColumnName(1), c.getInt(1));
            adoption.addProperty(c.getColumnName(3), c.getString(3));
            adoption.addProperty(c.getColumnName(4), c.getInt(4));
            adoption.addProperty(c.getColumnName(5), c.getString(5));
            adoption.addProperty(c.getColumnName(6), c.getString(6));
            adoption.addProperty(c.getColumnName(7), c.getString(7));

            adoptions.add(adoption);
        }

        return adoptions;
    }
} // class


