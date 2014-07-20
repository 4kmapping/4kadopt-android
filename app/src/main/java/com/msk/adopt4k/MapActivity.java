package com.msk.adopt4k;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.WindowManager;
import android.webkit.WebView;

import com.msk.adopt4k.fragment.FindOzFragment;


public class MapActivity extends FragmentActivity {

    private WebView map;
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        map = (WebView) findViewById(R.id.map);
        map.getSettings().setJavaScriptEnabled(true);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        FindOzFragment fragment = new FindOzFragment();
        fragment.setMap(map);
        getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();



        //String polygonParam = "25.774252+-80.190262+18.46646+-66.11829+32.321384+-64.75737+25.774252+-80.190262";

        //map.loadUrl("file:///android_asset/map.html?&lat="+cen_y+"&lon="+cen_x+"&polygon="+polygonParam.toString());
    }



}
