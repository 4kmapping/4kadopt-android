package com.msk.adopt4k;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.widget.TextView;

import com.msk.adopt4k.utils.AnimationManager;
import com.msk.adopt4k.utils.DBHelper;

/*
    원래는 Login 화면으로 사용되었지만 용도가 변경됨
    데이터베이스에 로그인 정보가 기록되어있는지 확인한 후에
    기록되어 있으면 MainActivity로,
    기록되어있지 않으면 ProfileActivity로 화면을 전환함.
 */
public class LoginActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final TextView title = (TextView)findViewById(R.id.title);

        AnimationManager aniMgr = new AnimationManager();
        title.setAnimation(aniMgr.getBlinkAnimation());

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                DBHelper dbHelper = new DBHelper(getApplicationContext());

                Intent intent;
                if(dbHelper.getLoginstate()) {
                    intent = new Intent(LoginActivity.this, MainActivity.class);
                } else {
                    intent = new Intent(LoginActivity.this, ProfileActivity.class);
                }

                startActivity(intent);
                finish();
            }
        }, 1400);

    } // override method onCreate

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if(keyCode == KeyEvent.KEYCODE_BACK)
        {
            // 뒤로가기 버튼으로 종료되는 것을 막음
        }

        return super.onKeyDown(keyCode, event);
    }
} // class
