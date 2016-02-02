package com.example.inmsb.aradalocomate.Activity;


import android.content.Context;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.inmsb.aradalocomate.R;

import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.overlays.Marker;
import org.osmdroid.bonuspack.overlays.Polyline;
import org.osmdroid.bonuspack.routing.MapQuestRoadManager;
import org.osmdroid.bonuspack.routing.OSRMRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.bonuspack.routing.RoadNode;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.DirectedLocationOverlay;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;

public class OsmMapFragment extends BaseFragment {
    LocationManager mLocationManager;
    DirectedLocationOverlay myLocationOverlay;
    MapView map;
    ArrayList<GeoPoint> waypoints;
    Road road;
    private Handler handler;
    private Runnable runnable;
    public OsmMapFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLocationManager = (LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout= inflater.inflate(R.layout.fragment_osm_map, container, false);
        map = (MapView) layout.findViewById(R.id.osmmap);
        map.setTileSource(TileSourceFactory.MAPNIK);
        //map.setBuiltInZoomControls(true);
        map.setMultiTouchControls(true);

        // Default location and zoom level
        IMapController mapController = map.getController();
        mapController.setZoom(12);
        GeoPoint startPoint = new GeoPoint(37.4333, 127.1500);
        mapController.setCenter(startPoint);
       // MyLocationNewOverlay mLocationOverlay = new MyLocationNewOverlay(getActivity(), new GpsMyLocationProvider(getActivity()),map);
        //map.getOverlays().add(mLocationOverlay);

        Marker startMarker = new Marker(map);
        startMarker.setPosition(startPoint);
        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        map.getOverlays().add(startMarker);

        RoadManager roadManager = new OSRMRoadManager(getActivity());
        waypoints = new ArrayList<GeoPoint>();
        waypoints.add(startPoint);
        GeoPoint endPoint = new GeoPoint(37.4314, 127.1297);
        waypoints.add(endPoint);

        Location location = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        myLocationOverlay = new DirectedLocationOverlay(getActivity());
        map.getOverlays().add(myLocationOverlay);
        myLocationOverlay.setLocation(new GeoPoint(location));
        map.invalidate();

        handler= new Handler(){
            @Override
            public void handleMessage(Message msg) {
                GeoPoint geo = (GeoPoint)msg.obj;
                map.getController().animateTo(geo);
                map.invalidate();

            }
        };

        return layout;
    }

    @Override
    public void onResume() {
        super.onResume();
        LongOperation lo = new LongOperation(waypoints,getActivity(),map);
        lo.execute("execute");
        new traversePath().execute("execute");
    }



    private class traversePath extends AsyncTask<String,Void,String>{

        @Override
        protected String doInBackground(String... params) {

            try{
                Thread.sleep(3000);
            }catch(Exception e){};
            for(GeoPoint geo:road.mRouteHigh){

                myLocationOverlay.setLocation(geo);
                myLocationOverlay.setBearing(180);
                try{
                    Thread.sleep(100);
                }catch(Exception e){};
                Message msgToUI = new Message();
                msgToUI.obj = geo;
                handler.sendMessage(msgToUI);
            }
            return "done";
        }

        @Override
        protected void onPostExecute(String s) {

        }
    }

    private class LongOperation extends AsyncTask<String,Void,String>{

        RoadManager roadManager;
        ArrayList<GeoPoint> waypoints;
        Context context;
        MapView map;
        public LongOperation(ArrayList<GeoPoint> waypoints, Context context,MapView map){
            roadManager= new MapQuestRoadManager("uuXhAZpDNrd7WsZ2rOgMBiF6H5FLSiiZ",context);
            this.waypoints=waypoints;
            this.context=context;
            this.map=map;
        }
        @Override
        protected String doInBackground(String... params) {
            roadManager.addRequestOption("routeType=bicycle");
            road = roadManager.getRoad(waypoints);

            return "executed";
        }

        @Override
        protected void onPostExecute(String s) {
            Polyline roadOverlay = RoadManager.buildRoadOverlay(road, context);
            map.getOverlays().add(roadOverlay);
            Drawable nodeIcon = getResources().getDrawable(R.drawable.marker_node);
            for (int i=0; i<road.mNodes.size(); i++){
                RoadNode node = road.mNodes.get(i);
                Marker nodeMarker = new Marker(map);
                nodeMarker.setPosition(node.mLocation);
                nodeMarker.setIcon(nodeIcon);
                nodeMarker.setTitle("Step " + i);
                nodeMarker.setSnippet(node.mInstructions);
                nodeMarker.setSubDescription(Road.getLengthDurationText(node.mLength, node.mDuration));
                map.getOverlays().add(nodeMarker);
            }
            map.invalidate();


        }
    }


}
