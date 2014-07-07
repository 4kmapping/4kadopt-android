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
import com.msk.adopt4k.fragment.FindOzFragment;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class MapActivity extends FragmentActivity implements GooglePlayServicesClient.ConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener {

    private LocationClient mLocationClient;
    private double lat;
    private double lon;
    private WebView map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        // 위치정보 불러오기
        mLocationClient = new LocationClient(this, this, this);
        mLocationClient.connect();

        getSupportFragmentManager().beginTransaction().replace(R.id.container, new FindOzFragment()).commit();


        map = (WebView) findViewById(R.id.map);
        map.getSettings().setJavaScriptEnabled(true);

        /*JsonArray jsonArray = new JsonArray();

        JsonObject loc1 = new JsonObject();
        loc1.addProperty("lat", 25.774252);
        loc1.addProperty("lon", -80.190262);
        jsonArray.add(loc1);

        JsonObject loc2 = new JsonObject();
        loc2.addProperty("lat", 18.46646);
        loc2.addProperty("lon", -66.11829);
        jsonArray.add(loc2);

        JsonObject loc3 = new JsonObject();
        loc3.addProperty("lat", 32.321384);
        loc3.addProperty("lon", -64.75737);
        jsonArray.add(loc3);

        JsonObject loc4 = new JsonObject();
        loc4.addProperty("lat", 25.774252);
        loc4.addProperty("lon", -80.190262);
        jsonArray.add(loc4);

        String polygonParam = "";
        try {
            polygonParam = URLEncoder.encode(jsonArray.toString(), "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }*/

        String polygonParam = "25.774252+-80.190262+18.46646+-66.11829+32.321384+-64.75737+25.774252+-80.190262";

        map.loadUrl("file:///android_asset/map.html?&lat=24.886436490787712&lon=-70.2685546875&polygon="+polygonParam);
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        mLocationClient.connect();
    }

    @Override
    protected void onStop()
    {
        mLocationClient.disconnect();
        super.onStop();
    }


    @Override
    public void onConnected(Bundle bundle)
    {
        Location location = mLocationClient.getLastLocation();

        if(location == null)
        {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle("Warning");
            alertDialogBuilder.setMessage("Check GPS is on");
            alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            alertDialogBuilder.show();
        }
        else {
            lat = location.getLatitude();
            lon = location.getLongitude();
            //if(map != null)
                //map.loadUrl("file:///android_asset/map.html?&lat="+lat+"&lon="+lon);
        }

    }


    @Override
    public void onDisconnected()
    {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult)
    {

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
