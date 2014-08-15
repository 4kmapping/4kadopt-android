package com.msk.adopt4k;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
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

import java.text.Format;


public class FindOzActivity extends Activity {

    private TextView info;
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
        map.loadUrl("file:///android_asset/map.html");

        ozidInput = (EditText)findViewById(R.id.input_ozid);
        ozidInput.addTextChangedListener(new TextWatcher() {
            int keyDel = 0;
            @Override
            public void afterTextChanged(Editable s) {

                ozidInput.setOnKeyListener(new View.OnKeyListener() {
                    @Override
                    public boolean onKey(View view, int i, KeyEvent keyEvent) {
                        if(i == KeyEvent.KEYCODE_DEL) {
                            keyDel = 1;
                        }

                        return false;
                    }
                });

                if( s.length() > 3 ) {
                    for(int i = 0; i < s.length(); i++) {
                        if(s.charAt(i) == '-' && (i+1)%4 != 0) {
                            s.replace(i, i+1, "");
                        }

                        if((i+1)%4 == 0 && s.charAt(i) != '-') {
                            s.insert(i, "-");
                        }
                    }
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {    //텍스트가 변경될때마다 실행

            }

        });


        info = (TextView) findViewById(R.id.info);

        final UserinfoHelper userinfoHelper = new UserinfoHelper(getApplicationContext());
        final ConnectionHelper connectionHelper = new ConnectionHelper(getApplicationContext());
        final GeodataHelper geodataHelper = new GeodataHelper(getApplicationContext());


        /*
                오메가존 찾기, 지도 위에 보여주기
         */
        TextView find = (TextView)findViewById(R.id.find);
        find.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String worldID = ozidInput.getText().toString().toUpperCase();

                if(worldID.length() > 0) {
                    String mapParam = geodataHelper.getMapParam(worldID);

                    if(mapParam != null) {
                        map.loadUrl("file:///android_asset/map.html" + mapParam);
                        info.setText(geodataHelper.getZoneName(worldID)+", "+geodataHelper.getCntyName(worldID));
                    } else {
                        dialogManager.badOZIDWarning();
                    }
                } else {
                    dialogManager.badOZIDWarning();
                }

                // 버튼을 눌렀을 때 소프트 키보드를 감춘다
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(ozidInput.getWindowToken(), 0);
                map.invalidate();

            }
        });

        /*
                메인 화면으로 돌아기기
         */
        TextView cancel = (TextView)findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FindOzActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        /*
                오메가존 선택하기
         */
        TextView select = (TextView)findViewById(R.id.select);
        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String worldID = ozidInput.getText().toString().toUpperCase();
                final int uid = userinfoHelper.getUID();


                if(worldID.length() > 0 && connectionHelper.isConnected() && geodataHelper.getMapParam(worldID) != null) {

                    String url = "http://4kadopt.org/api/ozstatus/?wid="+worldID+"&uid="+uid;
                    Log.d("url", url);

                    Ion.with(getApplicationContext())
                            .load(url)
                            .addHeader("Authorization", "Basic " + userinfoHelper.getCredential())
                            .asString()
                            .setCallback(new FutureCallback<String>() {
                                @Override
                                public void onCompleted(Exception e, String result) {

                                    // 다른 사람이 taken 한 경우
                                    if(result.equals("taken")) {
                                        dialogManager.takenWarning(new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                Intent intent = new Intent(FindOzActivity.this, SelectOzActivity.class);

                                                intent.putExtra("worldID", worldID.toUpperCase());
                                                intent.putExtra("zoneName", geodataHelper.getZoneName(worldID.toUpperCase()));
                                                intent.putExtra("cntyName", geodataHelper.getCntyName(worldID.toUpperCase()));

                                                startActivity(intent);

                                                dialogInterface.dismiss();
                                                finish();
                                            }
                                        });
                                    }

                                    // 사용자가 이미 owned 한 경우
                                    else if(result.equals("owned")) {
                                        dialogManager.ownedWarning();
                                    }

                                    // 아무도 adopt 하지 않은 경우
                                    else if(result.equals("none")) {
                                        Intent intent = new Intent(FindOzActivity.this, SelectOzActivity.class);
                                        intent.putExtra("worldID", worldID.toUpperCase());
                                        intent.putExtra("zoneName", geodataHelper.getZoneName(worldID.toUpperCase()));
                                        intent.putExtra("cntyName", geodataHelper.getCntyName(worldID.toUpperCase()));

                                        startActivity(intent);
                                        finish();
                                    }

                                    // 잘못된 ozid
                                    else if(result.equals("wrongId")) {
                                        dialogManager.badOZIDWarning();
                                    }
                                } // onCompleted
                            });

                    // if
                } else {


                    if(worldID.length() == 0) {
                        // ozid가 입력되지 않았을 때
                        dialogManager.showRequiredField();
                    } else if(geodataHelper.getMapParam(worldID) == null) {
                        // ozid와 매치되는 데이터베이스의 worldid가 없을때, 잘못된 ozid
                        dialogManager.badOZIDWarning();
                    } else {
                        // 네트워크가 연결되지 않았을 경우
                        dialogManager.showNetworkWarning();
                    }
                }
            }
        });
        // 오메가존 선택하기 - 끝
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(FindOzActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
} // class
