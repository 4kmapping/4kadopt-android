package com.msk.adopt4k;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.msk.adopt4k.utils.ConnectionHelper;
import com.msk.adopt4k.utils.DialogManager;
import com.msk.adopt4k.utils.GeodataHelper;
import com.msk.adopt4k.utils.UserinfoHelper;


public class FindOzActivity extends Activity {

    private EditText ozidInput;
    private WebView map;

    private DialogManager dialogManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_oz);

        dialogManager = new DialogManager(this);

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


        final UserinfoHelper userinfoHelper = new UserinfoHelper(getApplicationContext());
        final ConnectionHelper connectionHelper = new ConnectionHelper(getApplicationContext());
        final GeodataHelper geodataHelper = new GeodataHelper(getApplicationContext());

        TextView find = (TextView)findViewById(R.id.find);
        find.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String worldID = ozidInput.getText().toString().toUpperCase();


                if (worldID.length() > 0 && connectionHelper.isConnected()) {
                    String url = "http://4kadopt.org/api/adoptions/?wid="+worldID;

                    Log.d("url", url);


                    Ion.with(getApplicationContext())
                            .load(url)
                            .addHeader("Authorization", "Basic " + userinfoHelper.getCredential())
                            .asJsonObject()
                            .setCallback(new FutureCallback<JsonObject>() {
                                @Override
                                public void onCompleted(Exception e, JsonObject result) {
                                    if(e != null) {
                                        e.printStackTrace();
                                    }

                                    if(result.get("count").getAsInt() > 0) {
                                        // lock인 경우
                                        // 다이얼로그 출력
                                        JsonArray results = result.get("results").getAsJsonArray();
                                        JsonObject json = results.get(0).getAsJsonObject();
                                        if(json.get("is_adopted").getAsBoolean()) {
                                            // 이미 adopt됨
                                            dialogManager.showOzAdopted();
                                        } else {
                                            // 누군가 보고있음
                                            dialogManager.showOzLocked();
                                        }
                                    }

                                    // find 에서는 lock이 아닌 경우를 딱히 신경 쓸 필요가 없음
                                    Log.d("JsonResult", result.toString());
                                }
                            });

                    // 버튼을 눌렀을 때 소프트 키보드를 감춘다
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(ozidInput.getWindowToken(), 0);
                    map.invalidate();

                    if(geodataHelper.getMapParam(worldID) != null) {

                        map.loadUrl("file:///android_asset/map.html" + geodataHelper.getMapParam(worldID));
                    } else {
                        dialogManager.showBadWorldID();
                    }

                    // if
                } else {
                    if(worldID.length() == 0) {
                        dialogManager.showRequiredField();
                    } else {
                        dialogManager.showNetworkWarning();
                    }
                }
            }
        });

        TextView cancel = (TextView)findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        TextView select = (TextView)findViewById(R.id.select);
        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String worldID = ozidInput.getText().toString().toUpperCase();

                if(worldID.length() > 0 && connectionHelper.isConnected() && geodataHelper.getMapParam(worldID) != null) {

                    String url = "http://4kadopt.org/api/adoptions/?wid="+worldID;

                    Log.d("url", url);


                    Ion.with(getApplicationContext())
                            .load(url)
                            .addHeader("Authorization", "Basic " + userinfoHelper.getCredential())
                            .asJsonObject()
                            .setCallback(new FutureCallback<JsonObject>() {
                                @Override
                                public void onCompleted(Exception e, JsonObject result) {
                                    if(e != null) {
                                        e.printStackTrace();
                                    }

                                    if(result.get("count").getAsInt() > 0) {
                                        // lock인 경우
                                        // 다이얼로그 출력
                                        JsonArray results = result.get("results").getAsJsonArray();
                                        JsonObject json = results.get(0).getAsJsonObject();
                                        if(json.get("is_adopted").getAsBoolean()) {
                                            // 이미 adopt됨
                                            dialogManager.showOzAdopted();
                                        } else {
                                            // 누군가 보고있음
                                            dialogManager.showOzLocked();
                                        }
                                    } else {
                                        // lock이 아닌 경우
                                        // lock 걸고 다음 단계로
                                        //lockOz(worldID);


                                        JsonObject params = new JsonObject();
                                        params.addProperty("worldid", worldID);
                                        params.addProperty("targetyear", 0);
                                        params.addProperty("is_adopted", false);

                                        Ion.with(FindOzActivity.this)
                                                .load("post", "http://4kadopt.org/api/adoptions/")
                                                .addHeader("Authorization", "Basic " + userinfoHelper.getCredential())
                                                .setJsonObjectBody(params)
                                                .asJsonObject()
                                                .setCallback(new FutureCallback<JsonObject>() {
                                                    @Override
                                                    public void onCompleted(Exception e, JsonObject result) {
                                                        if(e != null) {
                                                            e.printStackTrace();
                                                        }

                                                        if(result != null) {
                                                            Log.d("post result", result.toString());
                                                        }
                                                        Intent i = new Intent(FindOzActivity.this, SelectOzActivity.class);
                                                        i.putExtra("worldID", worldID.toUpperCase());
                                                        i.putExtra("url", result.get("url").getAsString());
                                                        startActivity(i);
                                                        finish();
                                                    }
                                                });
                                    }
                                    Log.d("JsonResult", result.toString());
                                }
                            });
                    // Ion

                    // if
                } else {
                    if(worldID.length() == 0) {
                        dialogManager.showRequiredField();
                    } else if(geodataHelper.getMapParam(worldID) == null) {
                        dialogManager.showBadWorldID();
                    } else {
                        dialogManager.showNetworkWarning();
                    }
                }
            }
        });
    }
} // class
