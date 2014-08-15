package com.msk.adopt4k;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;

import com.msk.adopt4k.utils.DialogManager;
import com.msk.adopt4k.utils.GeodataHelper;

public class SelectOzActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_oz);

        final Intent intent = getIntent();
        final String worldID = intent.getStringExtra("worldID");
        //final String url = intent.getStringExtra("url");

        final String zoneName = intent.getStringExtra("zoneName");
        final String cntyName = intent.getStringExtra("cntyName");

        TextView info = (TextView) findViewById(R.id.info);
        info.setText(zoneName+", "+cntyName);

        TextView txtWorldId = (TextView) findViewById(R.id.world_id);
        txtWorldId.setText(worldID);


        WebView map = (WebView)findViewById(R.id.map);
        map.getSettings().setJavaScriptEnabled(true);

        GeodataHelper geodataHelper = new GeodataHelper(getApplicationContext());
        map.loadUrl("file:///android_asset/map.html" + geodataHelper.getMapParam(worldID));

        /*
                OZ 바꾸기, 그냥 취소이므로 되돌아간다.
         */
        TextView changeOz = (TextView) findViewById(R.id.change_oz);
        changeOz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(SelectOzActivity.this, FindOzActivity.class);
                startActivity(intent1);
                finish();
            }
        }); //setOnClickListener

        final TextView targetYear = (TextView) findViewById(R.id.target_year);

        targetYear.setText("Choose Year");

        /*
                연도 선택
         */
        targetYear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(SelectOzActivity.this, YearSelectActivity.class);
                startActivityForResult(intent1, 1);

            }
        });

        final DialogManager dialogManager = new DialogManager(this);

        /*
                오메가존 선택
         */
        TextView select = (TextView) findViewById(R.id.select);
        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(targetYear.getText().equals("Choose Year")) {
                    dialogManager.selectTargetYearWarning();
                } else {
                    Intent intent1 = new Intent(SelectOzActivity.this, AdoptOzActivity.class);
                    intent1.putExtra("worldID", worldID);
                    intent1.putExtra("targetYear", targetYear.getText());
                    intent1.putExtra("zoneName", zoneName);
                    intent1.putExtra("cntyName", cntyName);

                    startActivity(intent1);
                    finish();
                }


            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent intent1 = new Intent(SelectOzActivity.this, FindOzActivity.class);
        startActivity(intent1);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        final TextView targetYear = (TextView) findViewById(R.id.target_year);

        if(resultCode==RESULT_OK) {
            if(requestCode==1) {
                int year = data.getIntExtra("year", 2015);
                targetYear.setText(""+year);
            }
        }
    }
} // class
