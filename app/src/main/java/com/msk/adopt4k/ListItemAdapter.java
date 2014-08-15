package com.msk.adopt4k;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Junwon on 2014-08-15.
 */
public class ListItemAdapter extends ArrayAdapter<String> {
    private ArrayList<String> arrayItem;
    private Context mContext;

    public ListItemAdapter(Context context, int resource, ArrayList<String> objects) {
        super(context, resource, objects);
        this.arrayItem = objects;
        this.mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;

        if(v == null ){
            LayoutInflater li = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = li.inflate(R.layout.listitem, null);
        }

        //final ListItem item = new ListItem(arrayItem.get(position));

        TextView textItem = (TextView)v.findViewById(R.id.text1);
        textItem.setText(arrayItem.get(position));

        return v;
    }
}
