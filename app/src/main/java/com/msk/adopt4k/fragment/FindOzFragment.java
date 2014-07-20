package com.msk.adopt4k.fragment;



import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.msk.adopt4k.R;
import com.msk.adopt4k.utils.DBHelper;

/**
 * A simple {@link Fragment} subclass.
 *
 */
public class FindOzFragment extends Fragment {

    private TextView find;
    private EditText ozidInput;
    private WebView map;

    public FindOzFragment() {
        // Required empty public constructor
    }

    public void setMap(WebView map) {
        this.map = map;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_find_oz, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        View v = getView();

        ozidInput = (EditText)v.findViewById(R.id.input_ozid);
        find = (TextView)v.findViewById(R.id.find);
        find.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ozid = ozidInput.getText().toString();

                DBHelper dbHelper = new DBHelper(v.getContext());

                JsonObject geodata = dbHelper.getGeoData(ozid.toUpperCase());

                double cenX = geodata.get("Cen_x").getAsDouble();
                double cenY = geodata.get("Cen_y").getAsDouble();

                //String coords = geodata.get("Coords").getAsString();

                Log.d("geodata", geodata.toString());

                String polygonParam = "25.774252+-80.190262+18.46646+-66.11829+32.321384+-64.75737+25.774252+-80.190262";
                map.loadUrl("file:///android_asset/map.html?&lat="+cenX+"&lon="+cenY+"&polygon="+polygonParam.toString());
            }
        });

        TextView select = (TextView)v.findViewById(R.id.select);
        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().beginTransaction().replace(R.id.container, new SelectOzFragment()).commit();
            }
        });
    }
}
