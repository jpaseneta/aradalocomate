package com.example.inmsb.aradalocomate.Activity;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.transition.Transition;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.inmsb.aradalocomate.CustomImplementations.CustomArrayAdapter;
import com.example.inmsb.aradalocomate.CustomImplementations.CustomImageView;
import com.example.inmsb.aradalocomate.CustomImplementations.CustomRunnable;
import com.example.inmsb.aradalocomate.Model.AndroidLocomateMessagingServer;
import com.example.inmsb.aradalocomate.Model.DegreeModel;
import com.example.inmsb.aradalocomate.R;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class NavigationFragment extends BaseFragment {

    // Debugging
    private static final String TAG = "NavigationFragment";
    private static final boolean D = true;
    CommunicateToActivity mcallback;
    // Intent request codes
    private static final int REQUEST_ENABLE_BT = 1;

    // Layout Views
    private TextView mTitle;
    private ListView mConversationView;

    // Name of the connected device
    private String mConnectedDeviceName = null;
    // Array adapter for the conversation thread
    private CustomArrayAdapter mConversationArrayAdapter;
    // Local Bluetooth adapter
    private BluetoothAdapter mBluetoothAdapter = null;
    // Member object for the chat services
    private AndroidLocomateMessagingServer mChatService = null;
    private ImageView ArrowImageView = null;
    private TextView DistanceTextView = null;
    private Transition.TransitionListener mEnterTransitionListener;

    ArrayList<String> DisplayItems=new ArrayList<String>();
    ArrayList<Thread> ThreadList = new ArrayList<Thread>();
    float prevDegree=0;
    Handler UIHandler;
    public NavigationFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //setHasOptionsMenu(true);

        View rootView = inflater.inflate(R.layout.fragment_navigation, container, false);
        // Set up the custom title
        ArrowImageView = (CustomImageView) rootView.findViewById(R.id.arrowimageview);
        DistanceTextView = (TextView) rootView.findViewById(R.id.speed);
        mTitle = (TextView) rootView.findViewById(R.id.connectionStatus);

        if(savedInstanceState == null) {
            mTitle.setText("Not Connected");
            DistanceTextView.setText("0");
        }else{
            mTitle.setText(savedInstanceState.getString("connectionStatus"));
        }
        DistanceTextView.setTextSize(50);
        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(D) Log.e(TAG, "+++ ON CREATE +++");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "-- ON DESTROY --");
    }

    @Override
    public void onDestroyOptionsMenu() {
        super.onDestroyOptionsMenu();
        Log.d(TAG, "-- ON DESTROYOPTIONS --");
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        String test = mTitle.getText().toString();
        outState.putString("connectionStatus", test);
        Log.d(TAG, "++ ON SAVEINSTANCESTATE ++");
    }



//    @Override
//    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        inflater.inflate(R.menu.option_menu, menu);
//    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            mcallback = (CommunicateToActivity) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString()
                    + " must implement CommunicateToActivity");
        }
        setRetainInstance(true);
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



}
