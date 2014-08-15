package com.msk.adopt4k;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.msk.adopt4k.utils.DBHelper;

import java.util.ArrayList;
import java.util.HashMap;

public class HistoryActivity extends Activity {

    private ArrayList<HashMap<String, String>> buff;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        final DBHelper dbHelper = new DBHelper(getApplicationContext());

        TextView back = (TextView)findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HistoryActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        ListView historyList = (ListView) findViewById(R.id.history_list);

        ArrayList<String> contentArray = new ArrayList<String>();
        buff = new ArrayList<HashMap<String, String>>();


        JsonArray adoptions = dbHelper.getAdoptions();




        for(JsonElement adoption : adoptions) {

            JsonObject item = (JsonObject) adoption;
            String worldid = item.get("worldid").getAsString();
            String targetyear = item.get("targetyear").getAsString();

            String zoneName = item.get("zone_name").getAsString();
            String cntyName = item.get("cnty_name").getAsString();

            String world = item.get("world").getAsString();
            String population = item.get("population").getAsString();

            String url = item.get("url").getAsString();

            HashMap<String, String> params = new HashMap<String, String>();
            params.put("worldid", worldid);
            params.put("year", targetyear);
            params.put("zoneName", zoneName);
            params.put("cntyName", cntyName);
            params.put("world", world);
            params.put("population", population);
            params.put("url", url);

            buff.add(0, params);

            contentArray.add(0,   zoneName + ", " + cntyName + "("+ worldid + " ) By " + targetyear);
        }

        //ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, contentArray);
        historyList.setAdapter(new ListItemAdapter(this, R.layout.listitem, contentArray));



        historyList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                HashMap params = buff.get(i);

                String worldid = (String)params.get("worldid");
                String year = (String)params.get("year");
                String zoneName = (String)params.get("zoneName");
                String cntyName = (String)params.get("cntyName");
                String world = (String)params.get("world");
                String population = (String)params.get("population");
                String url = (String)params.get("url");

                Intent intent = new Intent(HistoryActivity.this, HistoryItemActivity.class);
                intent.putExtra("worldid", worldid);
                intent.putExtra("year", year);
                intent.putExtra("zoneName", zoneName);
                intent.putExtra("cntyName", cntyName);
                intent.putExtra("world", world);
                intent.putExtra("population", population);
                intent.putExtra("url", url);

                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(HistoryActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
