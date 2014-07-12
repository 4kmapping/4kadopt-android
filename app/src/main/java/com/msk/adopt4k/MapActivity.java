package com.msk.adopt4k;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.webkit.WebView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.msk.adopt4k.fragment.FindOzFragment;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class MapActivity extends FragmentActivity {

    private WebView map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        getSupportFragmentManager().beginTransaction().replace(R.id.container, new FindOzFragment()).commit();


        map = (WebView) findViewById(R.id.map);
        map.getSettings().setJavaScriptEnabled(true);

        BufferedReader reader = null;
        StringBuffer geojson = new StringBuffer();
        try {
            reader = new BufferedReader(new InputStreamReader(getAssets().open("EastAsia.geojson")));
            String mLine;
            while((mLine = reader.readLine()) != null)
            {
                geojson.append(mLine);
            }
        } catch (IOException e) {
            //log the exception
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    //log the exception
                }
            }
        }

        JsonObject json = (JsonObject) new JsonParser().parse(geojson.toString());
        JsonArray areas = json.getAsJsonArray("features");


        JsonObject area = areas.get(12).getAsJsonObject();
        JsonObject property = area.get("properties").getAsJsonObject();

        double cen_x = property.get("Cen_x").getAsDouble();
        double cen_y = property.get("Cen_y").getAsDouble();


        JsonObject polygon = area.get("geometry").getAsJsonObject();
        JsonArray coordinates = polygon.getAsJsonArray("coordinates");

        double firstCoordX = 0.f;
        double firstCoordY = 0.f;
        //String polygonParam = "25.774252+-80.190262+18.46646+-66.11829+32.321384+-64.75737+25.774252+-80.190262";
        StringBuffer polygonParam = new StringBuffer();

        for(int i = 0; i < coordinates.size(); i++)
        {
            JsonArray coord2 = coordinates.get(i).getAsJsonArray();
            for(int j = 0; j < coord2.size(); j++)
            {
                JsonArray coord3 = coord2.get(j).getAsJsonArray();

                for(int k = coord3.size()-1; k >=0; k--)
                //for(int k = 0; k < coord3.size() ; k++)
                {
                    if(i == 0 && j == 0 && k == coord3.size()-1) {
                        polygonParam.append(coord3.get(k).getAsDouble());
                        firstCoordY = coord3.get(k).getAsDouble();
                    } else if(i == 0 && j == 0 && k == 0) {
                        firstCoordX = coord3.get(k).getAsDouble();
                        polygonParam.append("+"+coord3.get(k).getAsDouble());
                    } else {
                        polygonParam.append("+"+coord3.get(k).getAsDouble());
                    }

                    /*
                    JsonArray coord4 = coord3.get(k).getAsJsonArray();
                    for(int l = coord4.size()-1; l >= 0; l--)
                    {
                        if(i == 0 && j == 0 && k == 0 && (l == coord4.size()-1)) {
                            polygonParam.append(coord4.get(l).getAsDouble());
                        } else {
                            polygonParam.append("+"+coord4.get(l).getAsDouble());
                        }
                    }
                    */
                }
            }
        }

        polygonParam.append("+"+firstCoordY+"+"+firstCoordX);
        //String polygonParam = "25.774252+-80.190262+18.46646+-66.11829+32.321384+-64.75737+25.774252+-80.190262";

        map.loadUrl("file:///android_asset/map.html?&lat="+cen_y+"&lon="+cen_x+"&polygon="+polygonParam.toString());
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if(keyCode == KeyEvent.KEYCODE_BACK)
        {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle("Quit");
            alertDialogBuilder.setMessage("Do you want to Quit Application?");
            alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    finish();
                }
            });
            alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            alertDialogBuilder.show();
        }

        return super.onKeyDown(keyCode, event);
    }

}
