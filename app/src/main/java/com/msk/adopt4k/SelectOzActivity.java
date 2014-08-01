package com.msk.adopt4k;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.DatePicker;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.msk.adopt4k.utils.ConnectionHelper;
import com.msk.adopt4k.utils.DialogManager;
import com.msk.adopt4k.utils.GeodataHelper;
import com.msk.adopt4k.utils.UserinfoHelper;

import java.lang.reflect.Field;
import java.util.Calendar;

public class SelectOzActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_oz);

        Intent intent = getIntent();
        final String worldID = intent.getStringExtra("worldID");
        final String url = intent.getStringExtra("url");

        TextView txtWorldId = (TextView) findViewById(R.id.world_id);
        txtWorldId.setText(worldID);


        WebView map = (WebView)findViewById(R.id.map);
        map.getSettings().setJavaScriptEnabled(true);

        GeodataHelper geodataHelper = new GeodataHelper(getApplicationContext());
        map.loadUrl("file:///android_asset/map.html" + geodataHelper.getMapParam(worldID));


        final UserinfoHelper userinfoHelper = new UserinfoHelper(getApplicationContext());
        final ConnectionHelper connectionHelper = new ConnectionHelper(getApplicationContext());
        final DialogManager dialogManager = new DialogManager(this);

        TextView changeOz = (TextView) findViewById(R.id.change_oz);
        changeOz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(connectionHelper.isConnected()) {
                    Ion.with(getApplicationContext())
                            .load("delete", url)
                            .addHeader("Authorization", "Basic " + userinfoHelper.getCredential())
                            .asJsonObject()
                            .setCallback(new FutureCallback<JsonObject>() {
                                @Override
                                public void onCompleted(Exception e, JsonObject result) {
                                    if (e == null) {
                                        Intent i = new Intent(SelectOzActivity.this, FindOzActivity.class);
                                        startActivity(i);
                                        finish();
                                    } else {
                                        e.printStackTrace();
                                    }
                                }
                            });
                    //Ion
                    //if
                } else {
                    dialogManager.showNetworkWarning();
                }
            }
        }); //setOnClickListener

        final TextView targetYear = (TextView) findViewById(R.id.target_year);
        final Calendar c = Calendar.getInstance();


        final DatePickerDialog dialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                targetYear.setText(""+year);
            }
        }, c.get(Calendar.YEAR), 0, 1);

        targetYear.setText(""+c.get(Calendar.YEAR));

        targetYear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Field[] f = dialog.getClass().getDeclaredFields();
                    for (Field dateField : f) {
                        if(dateField.getName().equals("mDatePicker")) {
                            dateField.setAccessible(true);

                            DatePicker datePicker = (DatePicker)dateField.get(dialog);

                            Field datePickerFields[] = dateField.getType().getDeclaredFields();

                            for(Field datePickerField : datePickerFields) {
                                if("mDayPicker".equals(datePickerField.getName()) || "mMonthPicker".equals(datePickerField.getName())
                                || "mDaySpinner".equals(datePickerField.getName()) || "mMonthSpinner".equals(datePickerField.getName())) {
                                    datePickerField.setAccessible(true);
                                    Object dayPicker;
                                    dayPicker = datePickerField.get(datePicker);
                                    ((View)dayPicker).setVisibility(View.GONE);
                                }
                            }
                        }
                    }
                    dialog.show();
                }
                catch (IllegalArgumentException e) {
                    e.printStackTrace();
                }
                catch (IllegalAccessException e) {
                    e.printStackTrace();
                }

            }
        });

        TextView select = (TextView) findViewById(R.id.select);
        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(connectionHelper.isConnected()) {
                    JsonObject params = new JsonObject();
                    params.addProperty("targetyear", Integer.parseInt(targetYear.getText().toString()));
                    Ion.with(getApplicationContext())
                            .load("patch", url)
                            .addHeader("Authorization", "Basic " + userinfoHelper.getCredential())
                            .setJsonObjectBody(params)
                            .asJsonObject()
                            .setCallback(new FutureCallback<JsonObject>() {
                                @Override
                                public void onCompleted(Exception e, JsonObject result) {

                                    if (e == null) {
                                        Intent intent = new Intent(SelectOzActivity.this, AdoptOzActivity.class);
                                        intent.putExtra("worldID", worldID);
                                        intent.putExtra("targetYear", targetYear.getText());
                                        intent.putExtra("url", url);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        e.printStackTrace();
                                    }
                                }
                            });
                    // Ion
                    // if
                } else {
                    dialogManager.showNetworkWarning();
                }
            }
        });
    }
} // class
