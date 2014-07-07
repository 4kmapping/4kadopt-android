package com.msk.adopt4k.fragment;



import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.msk.adopt4k.R;

import org.w3c.dom.Text;

/**
 * A simple {@link Fragment} subclass.
 *
 */
public class SelectOzFragment extends Fragment {


    public SelectOzFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_select_oz, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        View v = getView();
        TextView changeOz = (TextView) v.findViewById(R.id.change_oz);
        changeOz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().beginTransaction().replace(R.id.container, new FindOzFragment()).commit();
            }
        });
    }
}
