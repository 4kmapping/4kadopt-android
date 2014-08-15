package com.msk.adopt4k;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;

import com.msk.adopt4k.utils.DBHelper;

/*
    원래는 Login 화면으로 사용되었지만 용도가 변경됨
    데이터베이스에 로그인 정보가 기록되어있는지 확인한 후에
    기록되어 있으면 MainActivity로,
    기록되어있지 않으면 ProfileActivity로 화면을 전환함.
 */
public class LoginActivity extends Activity {
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //final TextView title = (TextView)findViewById(R.id.title);

        //AnimationManager aniMgr = new AnimationManager();
        //title.setAnimation(aniMgr.getBlinkAnimation());

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                DBInitTask dbInitTask = new DBInitTask();
                dbInitTask.execute();
            }
        }, 100);

    } // override method onCreate

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        //뒤로가기 버튼으로 종료되는 것을 막음
    }


    private class DBInitTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            dbHelper = new DBHelper(getApplicationContext());
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            Intent intent;
            if(dbHelper.getLoginstate()) {
                intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            } else {
                intent = new Intent(LoginActivity.this, ProfileActivity.class);
                startActivity(intent);
                finish();
            }
        }
    }
} // class
