package com.example.andjaradji.earthquakewatcher.UI;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.andjaradji.earthquakewatcher.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

/**
 * Created by Anjar on 18/12/2017.
 */

public class CustomInfoWindow implements GoogleMap.InfoWindowAdapter {
    private View view;
    private Context context;
    private LayoutInflater layoutInflater;

    public CustomInfoWindow(Context context) {
        this.context = context;
        layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = layoutInflater.inflate(R.layout.custom_info_window, null);
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        TextView infoTitle = (TextView)view.findViewById(R.id.infoWinTitleID);
//        ImageView iconInfo = (ImageView)view.findViewById(R.id.iconInfoID);
        TextView infoMagnitude = (TextView)view.findViewById(R.id.infoMagnitudeID);
//        Button moreInfoButton = (Button)view.findViewById(R.id.moreInfoButtonID);


        infoTitle.setText(marker.getTitle());
        infoMagnitude.setText(marker.getSnippet());
        return view;
    }
}
