package com.msk.adopt4k.utils;

import android.content.Context;
import android.util.Log;

import com.google.gson.JsonObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class GeodataHelper {
    private Context mContext;


    public GeodataHelper(Context context) {
        mContext = context;
    }

    public String getWorld(String worldID) {
        DBHelper dbHelper = new DBHelper(mContext);

        JsonObject geodata = dbHelper.getGeoData(worldID.toUpperCase());

        return geodata.get("World").getAsString();
    }

    public double getPopulation(String worldID) {
        DBHelper dbHelper = new DBHelper(mContext);

        JsonObject geodata = dbHelper.getGeoData(worldID.toUpperCase());

        return geodata.get("Population").getAsDouble();
    }

    public String getCntyName(String worldID) {
        DBHelper dbHelper = new DBHelper(mContext);

        JsonObject geodata = dbHelper.getGeoData(worldID.toUpperCase());

        return geodata.get("Cnty_Name").getAsString();
    }

    public String getZoneName(String worldID) {
        DBHelper dbHelper = new DBHelper(mContext);

        JsonObject geodata = dbHelper.getGeoData(worldID.toUpperCase());

        return geodata.get("Zone_name").getAsString();
    }

    public String getMapParam(String worldID) {

        DBHelper dbHelper = new DBHelper(mContext);

        JsonObject geodata = dbHelper.getGeoData(worldID.toUpperCase());

        if(geodata != null) {

            double cenX = geodata.get("Cen_x").getAsDouble();
            double cenY = geodata.get("Cen_y").getAsDouble();
            double shapeArea = geodata.get("Shape_Area").getAsDouble();

            int zoom = (int) Math.floor((Math.log(shapeArea * 20)));

            if (zoom < 5) {
                zoom += 1;
            } else if (zoom > 5) {
                zoom -= 1;
            }

            if (zoom < 0) {
                zoom = 0;
            }

            String coords = geodata.get("Coords").getAsString();
            coords = coords.replaceAll("\\s+", "");

            Log.d("cen_x", "" + cenX);
            Log.d("cen_y", "" + cenY);

            try {
                String params = "?&lat=" + cenY + "&lon=" + cenX + "&zoom=" + zoom + "&polygon=" + URLEncoder.encode(coords, "UTF-8");

                return params;
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        return null;

    }
}
