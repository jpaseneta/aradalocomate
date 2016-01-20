package com.example.inmsb.aradalocomate.Activity;


import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.inmsb.aradalocomate.Adapter.RadarItemAdapter;
import com.example.inmsb.aradalocomate.Model.AndroidLocomateMessagingServer;
import com.example.inmsb.aradalocomate.Model.DegreeModel;
import com.example.inmsb.aradalocomate.Model.RadarInfoItem;
import com.example.inmsb.aradalocomate.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 */
public class RadarFragment extends BaseFragment {

    // Debugging
    private static final String TAG = "RadarFragment";
    private static final boolean D = true;
    CommunicateToActivity mcallback;
    Time today;
    // Layout Views
    private TextView mTitle;

    private RecyclerView recyclerView;
    private RadarItemAdapter adapter;
    public RadarFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(D) Log.e(TAG, "+++ ON CREATE +++");

        Calendar now = Calendar.getInstance();
        Log.d("NOW",String.valueOf(now.getTimeInMillis()));

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //setHasOptionsMenu(true);
        View layout = inflater.inflate(R.layout.fragment_radar, container, false);
        //adapter = new RadarItemAdapter(getActivity());
        //mcallback.getAdapterInstance(adapter);
        recyclerView = (RecyclerView) layout.findViewById(R.id.radarEventList);
        //recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        mTitle = (TextView) layout.findViewById(R.id.connectionStatusRadar);
        if(savedInstanceState == null) {
            mTitle.setText("Not Connected");
        }else{
            mTitle.setText(savedInstanceState.getString("connectionStatus"));
        }
        return layout;

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        String test = mTitle.getText().toString();
        outState.putString("connectionStatus", test);
        Log.d(TAG, "++ ON SAVEINSTANCESTATE ++");
    }

    @Override
    public synchronized void onResume() {
        super.onResume();
        if(D) Log.e(TAG, "+ ON RESUME +");
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mcallback = (CommunicateToActivity) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString()
                    + " must implement CommunicateToActivity");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG, "-- ON DETACH --");
    }

    @Override
    public synchronized void onPause() {
        super.onPause();
        if(D) Log.e(TAG, "- ON PAUSE -");
    }

    @Override
    public void onStop() {
        super.onStop();
        if(D) Log.e(TAG, "-- ON STOP --");
    }

    public void updateState(String status){
        mTitle.setText(status);
    }

    public void giveAdapterInstance(RadarItemAdapter adapter){
        this.adapter=adapter;
    }

    public void addRadarInfoItem(int laneIndex, Date date, int unforeseenDetectionType, double longitude, double latitude, double distance){
        RadarInfoItem item = new RadarInfoItem(laneIndex,date,unforeseenDetectionType,longitude,latitude,distance);

    }

}
