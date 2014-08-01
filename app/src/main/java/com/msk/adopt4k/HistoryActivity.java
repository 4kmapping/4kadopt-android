package com.msk.adopt4k;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.msk.adopt4k.utils.DBHelper;
import com.msk.adopt4k.utils.GeodataHelper;

import java.util.ArrayList;

public class HistoryActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        ListView historyList = (ListView) findViewById(R.id.history_list);

        ArrayList<String> contentArray = new ArrayList<String>();

        DBHelper dbHelper = new DBHelper(getApplicationContext());
        GeodataHelper geodataHelper = new GeodataHelper(getApplicationContext());

        JsonArray adoptions = dbHelper.getAdoptions();




        for(JsonElement adoption : adoptions) {
            String worldid = ((JsonObject)adoption).get("worldid").getAsString();
            String targetyear = ((JsonObject)adoption).get("targetyear").getAsString();

            String cntyName = geodataHelper.getCntyName(worldid);

            contentArray.add(worldid + " in " + cntyName + " By " + targetyear);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, contentArray);
        historyList.setAdapter(adapter);
    }
}
