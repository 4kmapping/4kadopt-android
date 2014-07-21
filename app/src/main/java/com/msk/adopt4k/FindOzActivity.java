package com.msk.adopt4k;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.msk.adopt4k.utils.DBHelper;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;


public class FindOzActivity extends Activity {

    private EditText ozidInput;
    private WebView map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_oz);

        map = (WebView) findViewById(R.id.map);
        map.getSettings().setJavaScriptEnabled(true);

        ozidInput = (EditText)findViewById(R.id.input_ozid);
        ozidInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if( s.length() > 3 ) {
                    for(int i = 1; i < s.length()/4+1; i++) {
                        if(s.charAt(4*i-1) != '-')
                            s.insert(4*i-1, "-");
                    }
                }
            }
        });


        TextView find = (TextView)findViewById(R.id.find);
        find.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(ozidInput.getWindowToken(), 0);
                map.invalidate();

                String ozid = ozidInput.getText().toString();

                DBHelper dbHelper = new DBHelper(getApplicationContext());

                JsonObject geodata = dbHelper.getGeoData(ozid.toUpperCase());

                double cenX = geodata.get("Cen_x").getAsDouble();
                double cenY = geodata.get("Cen_y").getAsDouble();
                double shapeArea = geodata.get("Shape_Area").getAsDouble();

                int zoom = (int)Math.floor((Math.log(shapeArea*20)));

                if(zoom < 5) {
                    zoom += 1;
                } else if (zoom > 5) {
                    zoom -= 1;
                }

                if(zoom < 0) {
                    zoom = 0;
                }

                String coords = geodata.get("Coords").getAsString();
                coords = coords.replaceAll("\\s+","");

                Log.d("cen_x", ""+cenX);
                Log.d("cen_y", ""+cenY);

                try {
                    String params = "?&lat=" + cenY + "&lon=" + cenX + "&zoom="+ zoom + "&polygon=" + URLEncoder.encode(coords, "UTF-8");

                    Log.d("params", params);
                    map.loadUrl("file:///android_asset/map.html"+params);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        });

        TextView cancel = (TextView)findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        TextView select = (TextView)findViewById(R.id.select);
        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
} // class
