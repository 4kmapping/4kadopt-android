package com.msk.adopt4k;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.msk.adopt4k.R;

public class YearSelectActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_year_select);

        TextView yr2015 = (TextView)findViewById(R.id.yr2015);
        TextView yr2016 = (TextView)findViewById(R.id.yr2016);
        TextView yr2017 = (TextView)findViewById(R.id.yr2017);
        TextView yr2018 = (TextView)findViewById(R.id.yr2018);
        TextView yr2019 = (TextView)findViewById(R.id.yr2019);
        TextView yr2020 = (TextView)findViewById(R.id.yr2020);

        yr2015.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = getIntent();
                intent.putExtra("year", 2015);
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        yr2016.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = getIntent();
                intent.putExtra("year", 2016);
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        yr2017.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = getIntent();
                intent.putExtra("year", 2017);
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        yr2018.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = getIntent();
                intent.putExtra("year", 2018);
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        yr2019.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = getIntent();
                intent.putExtra("year", 2019);
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        yr2020.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = getIntent();
                intent.putExtra("year", 2020);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = getIntent();
        setResult(RESULT_CANCELED, intent);
        finish();
    }
}
