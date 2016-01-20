package com.example.inmsb.aradalocomate.Activity;

import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;

import com.example.inmsb.aradalocomate.Adapter.RadarItemAdapter;

/**
 * Created by inmsb on 1/5/2016.
 */
public class BaseFragment extends Fragment {
    public interface CommunicateToActivity {
        public void getAdapterInstance(RadarItemAdapter adapter);
    }
}
