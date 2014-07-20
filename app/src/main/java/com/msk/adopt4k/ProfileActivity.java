package com.msk.adopt4k;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.msk.adopt4k.utils.DBHelper;

public class ProfileActivity extends Activity {

    private EditText nameInput;
    private EditText emailInput;
    private EditText apiKeyInput;

    private TextView edit;
    private TextView b2m;

    private boolean editflag = false;

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

        JsonObject userinfo = dbHelper.getUserInfo();

        Log.d("userinfo", userinfo.toString());

        nameInput.setText(userinfo.get("name").getAsString());
        emailInput.setText(userinfo.get("email").getAsString());
        apiKeyInput.setText(userinfo.get("apikey").getAsString());

        if(dbHelper.getLoginstate()) {
            // 로그인 정보가 있을 경우
            switchToBlockMode();

        } else {
            // 로그인 정보가 존재하지 않을 경우
            switchToEditMode();

        }

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editflag) {
                    // 편집중인경우 --> 저장하기
                    switchToBlockMode();

                    String name = nameInput.getText().toString();
                    String email = emailInput.getText().toString();
                    String apiKey = apiKeyInput.getText().toString();

                    dbHelper.insertUserInfo(name, email, apiKey);

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
    }

    private void switchToEditMode() {
        editflag = true;
        edit.setText("SAVE");

        nameInput.setBackgroundResource(R.drawable.input_field);
        emailInput.setBackgroundResource(R.drawable.input_field);
        apiKeyInput.setBackgroundResource(R.drawable.input_field);

        nameInput.setEnabled(true);
        emailInput.setEnabled(true);
        apiKeyInput.setEnabled(true);
    }

    private void switchToBlockMode() {
        editflag = false;
        edit.setText("EDIT");

        nameInput.setBackgroundColor(Color.WHITE);
        emailInput.setBackgroundColor(Color.WHITE);
        apiKeyInput.setBackgroundColor(Color.WHITE);

        nameInput.setEnabled(false);
        emailInput.setEnabled(false);
        apiKeyInput.setEnabled(false);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if(keyCode == KeyEvent.KEYCODE_BACK)
        {
            finish();
        }

        return super.onKeyDown(keyCode, event);
    }
} // class
