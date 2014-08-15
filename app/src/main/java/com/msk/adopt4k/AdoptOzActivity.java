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

    private DBHelper dbHelper;
    private UserinfoHelper userinfoHelper;
    private ConnectionHelper connectionHelper;
    private DialogManager dialogManager;

    private String worldID;
    private String zoneName;
    private String cntyName1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adopt_oz);

        Intent intent = getIntent();
        worldID = intent.getStringExtra("worldID");
        final int targetYear = Integer.parseInt(intent.getStringExtra("targetYear"));
        zoneName = intent.getStringExtra("zoneName");
        cntyName1 = intent.getStringExtra("cntyName");

        dbHelper = new DBHelper(getApplicationContext());
        userinfoHelper = new UserinfoHelper(getApplicationContext());
        connectionHelper = new ConnectionHelper(getApplicationContext());
        dialogManager = new DialogManager(this);



        // display name
        TextView username = (TextView)findViewById(R.id.username);
        final String txtUsername = dbHelper.getUserInfo().get("name").getAsString();
        username.setText(txtUsername);

        // world id
        TextView txtWorldID = (TextView) findViewById(R.id.world_id);
        txtWorldID.setText(worldID);

        // contry name
        //GeodataHelper geodataHelper = new GeodataHelper(this);
        final TextView cntyName = (TextView) findViewById(R.id.cnty_name);
        cntyName.setText(cntyName1);

        // target year
        TextView txtTargetYear = (TextView) findViewById(R.id.target_year);
        txtTargetYear.setText(""+targetYear);

        // zone name
        //final String zoneName = geodataHelper.getZoneName(worldID);



        /*
         *      Yes, I will
         */
        TextView select = (TextView) findViewById(R.id.select);
        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(connectionHelper.isConnected()) {

                    JsonObject params = new JsonObject();
                    params.addProperty("is_adopted", true);
                    params.addProperty("worldid", worldID);
                    params.addProperty("targetyear", targetYear);
                    params.addProperty("oz_zone_name", zoneName);
                    params.addProperty("oz_country_name", cntyName1);
                    params.addProperty("user_display_name", txtUsername);

                    postAdoption(params, new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {

                            if(e == null && result.has("url")) {

                                String url = result.get("url").getAsString();
                                dbHelper.insertAdoption(worldID, targetYear, url);

                                Intent i = new Intent(AdoptOzActivity.this, MainActivity.class);
                                startActivity(i);
                                finish();
                            } else {
                                dialogManager.networkError();
                            }
                        }
                    });
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
                    params.addProperty("worldid", worldID);
                    params.addProperty("targetyear", targetYear);
                    params.addProperty("oz_zone_name", zoneName);
                    params.addProperty("oz_country_name", cntyName.getText().toString());
                    params.addProperty("user_display_name", txtUsername);

                    postAdoption(params, new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {


                            if(e == null && result.has("url")) {
                                String url = result.get("url").getAsString();
                                dbHelper.insertAdoption(worldID, targetYear, url);

                                Intent i = new Intent(AdoptOzActivity.this, FindOzActivity.class);
                                startActivity(i);
                                finish();
                            } else {
                                dialogManager.networkError();
                            }
                        }
                    });
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

                Intent i = new Intent(AdoptOzActivity.this, FindOzActivity.class);
                startActivity(i);
                finish();
            }
        });

    }


    private void postAdoption(JsonObject params, FutureCallback<JsonObject> futureCallback) {
        Ion.with(getApplicationContext())
                .load("post", "http://4kadopt.org/api/adoptions/")
                .addHeader("Authorization", "Basic " + userinfoHelper.getCredential())
                .setJsonObjectBody(params)
                .asJsonObject()
                .setCallback(futureCallback);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(AdoptOzActivity.this, SelectOzActivity.class);
        intent.putExtra("worldID",worldID);
        intent.putExtra("zoneName", zoneName);
        intent.putExtra("cntyName", cntyName1);
        startActivity(intent);
        finish();
    }
}
