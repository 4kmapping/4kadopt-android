package com.msk.adopt4k;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.msk.adopt4k.utils.ConnectionHelper;
import com.msk.adopt4k.utils.DBHelper;
import com.msk.adopt4k.utils.DialogManager;

public class ProfileActivity extends Activity {

    private EditText nameInput;
    private EditText emailInput;
    private EditText apiKeyInput;

    private TextView edit;
    private TextView b2m;

    private boolean editflag = false;

    private String displayname;
    private int uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);



        nameInput = (EditText)findViewById(R.id.input_name);
        emailInput = (EditText)findViewById(R.id.input_email);
        apiKeyInput = (EditText)findViewById(R.id.input_apikey);

        edit = (TextView)findViewById(R.id.edit);
        b2m = (TextView)findViewById(R.id.b2m);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        final DBHelper dbHelper = new DBHelper(getApplicationContext());
        final DialogManager dialogManager = new DialogManager(this);
        JsonObject userinfo = dbHelper.getUserInfo();

        Log.d("userinfo", userinfo.toString());

        nameInput.setText(userinfo.get("name").getAsString());
        emailInput.setText(userinfo.get("email").getAsString());
        apiKeyInput.setText(userinfo.get("apikey").getAsString());

        nameInput.setBackgroundColor(Color.WHITE);
        nameInput.setEnabled(false);


        if(dbHelper.getLoginstate()) {
            // 로그인 정보가 있을 경우
            switchToBlockMode();

        } else {
            // 로그인 정보가 존재하지 않을 경우
            switchToEditMode();

        }

        final ConnectionHelper connectionHelper = new ConnectionHelper(getApplicationContext());

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editflag) {
                    // 편집중인경우 --> 저장하기


                    String url = "http://4kadopt.org/api/users/";

                    final String email = emailInput.getText().toString();
                    final String apiKey = apiKeyInput.getText().toString();
                    String credential = email+":"+apiKey;
                    String encodedCredential = Base64.encodeToString(credential.getBytes(), Base64.DEFAULT);



                    if(connectionHelper.isConnected()) {
                        Ion.with(getApplicationContext())
                                .load(url)
                                .addHeader("Authorization", "Basic " + encodedCredential)
                                .asJsonObject()
                                .setCallback(new FutureCallback<JsonObject>() {
                                    @Override
                                    public void onCompleted(Exception e, JsonObject result) {
                                        if(e == null) {
                                            if (result.has("results")) {
                                                JsonArray results = result.get("results").getAsJsonArray();
                                                displayname = results.get(0).getAsJsonObject().get("first_name").getAsString();
                                                uid = results.get(0).getAsJsonObject().get("id").getAsInt();
                                                nameInput.setText(displayname);
                                                dbHelper.insertUserInfo(displayname, email, apiKey, uid);

                                                switchToBlockMode();
                                            } else {
                                                // 다시한번 확인하세요
                                                dialogManager.apiKeyWarning();
                                            }
                                        } else {
                                            dialogManager.networkError();
                                        }
                                    }
                                });
                    }

                    //String name = nameInput.getText().toString();

                } else {
                    // 편집중이 아닌경우 --> 편집모드로
                    switchToEditMode();

                }
            }
        });

        b2m.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        final TextView adoptCount = (TextView)findViewById(R.id.adopt_count);
        adoptCount.setText(""+dbHelper.countAdoption());

        TextView sync = (TextView)findViewById(R.id.sync);
        sync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(connectionHelper.isConnected() && emailInput.getText().toString().length() > 0 && apiKeyInput.getText().length() > 0) {

                    final String email = emailInput.getText().toString();
                    final String apiKey = apiKeyInput.getText().toString();
                    String credential = email+":"+apiKey;
                    String encodedCredential = Base64.encodeToString(credential.getBytes(), Base64.DEFAULT);

                    Ion.with(getApplicationContext())
                            .load("http://4kadopt.org/api/adoptions/")
                            .addHeader("Authorization", "Basic " + encodedCredential)
                            .asJsonObject()
                            .setCallback(new FutureCallback<JsonObject>() {
                                @Override
                                public void onCompleted(Exception e, JsonObject result) {

                                    if(e == null && result.has("count")) {

                                        adoptCount.setText(result.get("count").getAsString());

                                        if (result.has("results")) {
                                            dbHelper.updateAdoption(result.get("results").getAsJsonArray());
                                        }

                                    } else {
                                        dialogManager.networkError();
                                    }

                                }
                            });
                }
            }
        });
    }

    private void switchToEditMode() {
        editflag = true;
        edit.setText("SAVE");

        //nameInput.setBackgroundResource(R.drawable.input_field);
        emailInput.setBackgroundResource(R.drawable.input_field);
        apiKeyInput.setBackgroundResource(R.drawable.input_field);

        //nameInput.setEnabled(true);
        emailInput.setEnabled(true);
        apiKeyInput.setEnabled(true);
    }

    private void switchToBlockMode() {
        editflag = false;
        edit.setText("EDIT");

        //nameInput.setBackgroundColor(Color.WHITE);
        emailInput.setBackgroundColor(Color.WHITE);
        apiKeyInput.setBackgroundColor(Color.WHITE);

        //nameInput.setEnabled(false);
        emailInput.setEnabled(false);
        apiKeyInput.setEnabled(false);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
        //super.onBackPressed();
    }

} // class
