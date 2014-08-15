package com.msk.adopt4k;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.msk.adopt4k.utils.DBHelper;
import com.msk.adopt4k.utils.DialogManager;
import com.msk.adopt4k.utils.GeodataHelper;
import com.msk.adopt4k.utils.UserinfoHelper;

public class HistoryItemActivity extends Activity {

    private TextView back;
    private TextView delete;

    private TextView displayName;
    private TextView targetYear;
    private TextView ozInfo;
    private TextView ozDesc;

    private WebView map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_item);

        back = (TextView) findViewById(R.id.back);
        delete = (TextView) findViewById(R.id.delete);

        back.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HistoryItemActivity.this, HistoryActivity.class);
                startActivity(intent);
                finish();
            }
        });



        displayName = (TextView) findViewById(R.id.display_name);
        targetYear = (TextView) findViewById(R.id.target_year);
        ozInfo = (TextView) findViewById(R.id.oz_info);
        ozDesc = (TextView) findViewById(R.id.oz_desc);

        Intent intent = getIntent();
        final String worldid = intent.getStringExtra("worldid");
        final String cntyName = intent.getStringExtra("cntyName");
        final String zoneName = intent.getStringExtra("zoneName");
        final String year = intent.getStringExtra("year");
        final String world = intent.getStringExtra("world");
        final String population = intent.getStringExtra("population");
        final String url = intent.getStringExtra("url");

        final UserinfoHelper userinfoHelper = new UserinfoHelper(getApplicationContext());
        final DialogManager dialogManager = new DialogManager(this);
        final DBHelper dbHelper = new DBHelper(getApplicationContext());

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialogManager.deleteWarning(new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialogInterface, int i) {
                        Ion.with(getApplicationContext())
                                .load("delete", url)
                                .addHeader("Authorization", "Basic " + userinfoHelper.getCredential())
                                .asString()
                                .setCallback(new FutureCallback<String>() {
                                    @Override
                                    public void onCompleted(Exception e, String result) {
                                        if(e != null) {
                                            // error handle
                                        } else {
                                            dbHelper.deleteAdoption(worldid);
                                            Intent intent = new Intent(HistoryItemActivity.this, HistoryActivity.class);
                                            dialogInterface.dismiss();
                                            startActivity(intent);
                                            finish();
                                        }
                                    }
                                });
                    }
                });


            }
        });


        displayName.setText("You are "+userinfoHelper.getUserName());

        targetYear.setText("Year "+year);
        ozInfo.setText(zoneName+", "+cntyName+"("+worldid+")");

        String gospelAccess = "No Info";
        if(world.equals("A")) {
            gospelAccess = "Low";
        } else if(world.equals("B")) {
            gospelAccess = "Medium";
        } else if(world.equals("c")) {
            gospelAccess = "High";
        }

        ozDesc.setText("Gospel Access: "+gospelAccess+"\tPopulation: "+population);

        map = (WebView) findViewById(R.id.map);
        map.getSettings().setJavaScriptEnabled(true);

        GeodataHelper geodataHelper = new GeodataHelper(getApplicationContext());
        map.loadUrl("file:///android_asset/map.html" + geodataHelper.getMapParam(worldid));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent intent = new Intent(HistoryItemActivity.this, HistoryActivity.class);
        startActivity(intent);
        finish();
    }
}
