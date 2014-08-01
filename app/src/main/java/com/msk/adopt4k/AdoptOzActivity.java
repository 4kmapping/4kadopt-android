package com.msk.adopt4k;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.msk.adopt4k.utils.ConnectionHelper;
import com.msk.adopt4k.utils.DBHelper;
import com.msk.adopt4k.utils.DialogManager;
import com.msk.adopt4k.utils.GeodataHelper;
import com.msk.adopt4k.utils.UserinfoHelper;


public class AdoptOzActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adopt_oz);

        Intent intent = getIntent();
        final String worldID = intent.getStringExtra("worldID");
        final String targetYear = intent.getStringExtra("targetYear");
        final String url = intent.getStringExtra("url");

        TextView username = (TextView)findViewById(R.id.username);
        final DBHelper dbHelper = new DBHelper(getApplicationContext());
        String txtUsername = dbHelper.getUserInfo().get("name").getAsString();
        username.setText(txtUsername);

        TextView txtWorldID = (TextView) findViewById(R.id.world_id);
        txtWorldID.setText(worldID);

        GeodataHelper geodataHelper = new GeodataHelper(this);
        TextView cntyName = (TextView) findViewById(R.id.cnty_name);
        cntyName.setText(geodataHelper.getCntyName(worldID));

        TextView txtTargetYear = (TextView) findViewById(R.id.target_year);
        txtTargetYear.setText(targetYear);


        final UserinfoHelper userinfoHelper = new UserinfoHelper(getApplicationContext());
        final ConnectionHelper connectionHelper = new ConnectionHelper(getApplicationContext());
        final DialogManager dialogManager = new DialogManager(this);

        /*
         * Yes, I will
         */
        TextView select = (TextView) findViewById(R.id.select);
        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(connectionHelper.isConnected()) {

                    JsonObject params = new JsonObject();
                    params.addProperty("is_adopted", true);
                    Ion.with(getApplicationContext())
                            .load("patch", url)
                            .addHeader("Authorization", "Basic " + userinfoHelper.getCredential())
                            .setJsonObjectBody(params)
                            .asJsonObject()
                            .setCallback(new FutureCallback<JsonObject>() {
                                @Override
                                public void onCompleted(Exception e, JsonObject result) {

                                    if (e == null) {
                                        dbHelper.insertAdoption(worldID, Integer.parseInt(targetYear));
                                        finish();
                                    } else {
                                        e.printStackTrace();
                                    }
                                }
                            });
                    // Ion
                } else {
                    dialogManager.showNetworkWarning();
                }
            }
        });

        /*
         * Yes, I will and I will adopt more
         */
        TextView selectNContinue = (TextView)findViewById(R.id.select_n_continue);
        selectNContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(connectionHelper.isConnected()) {
                    JsonObject params = new JsonObject();
                    params.addProperty("is_adopted", true);
                    Ion.with(getApplicationContext())
                            .load("patch", url)
                            .addHeader("Authorization", "Basic " + userinfoHelper.getCredential())
                            .setJsonObjectBody(params)
                            .asJsonObject()
                            .setCallback(new FutureCallback<JsonObject>() {
                                @Override
                                public void onCompleted(Exception e, JsonObject result) {

                                    if (e == null) {

                                        dbHelper.insertAdoption(worldID, Integer.parseInt(targetYear));

                                        Intent i = new Intent(AdoptOzActivity.this, FindOzActivity.class);
                                        startActivity(i);
                                        finish();
                                    } else {
                                        e.printStackTrace();
                                    }
                                }
                            });
                    // Ion
                } else {
                    dialogManager.showNetworkWarning();
                }
            }
        });

        /*
         * CHANGE
         */
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
                                        Intent i = new Intent(AdoptOzActivity.this, FindOzActivity.class);
                                        startActivity(i);
                                        finish();
                                    } else {
                                        e.printStackTrace();
                                    }
                                }
                            });
                    //Ion
                } else {
                    dialogManager.showNetworkWarning();
                }
            }
        });

    }


}
