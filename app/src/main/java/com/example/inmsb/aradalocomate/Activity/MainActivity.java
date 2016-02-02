package com.example.inmsb.aradalocomate.Activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.res.Resources;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.PersistableBundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.transition.Explode;
import android.transition.Slide;
import android.transition.Transition;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewAnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.inmsb.aradalocomate.Adapter.RadarItemAdapter;
import com.example.inmsb.aradalocomate.CustomImplementations.CustomArrayAdapter;
import com.example.inmsb.aradalocomate.CustomImplementations.CustomImageView;
import com.example.inmsb.aradalocomate.CustomImplementations.CustomRunnable;
import com.example.inmsb.aradalocomate.Model.AndroidLocomateMessagingServer;
import com.example.inmsb.aradalocomate.Model.DegreeModel;
import com.example.inmsb.aradalocomate.Model.RadarInfoItem;
import com.example.inmsb.aradalocomate.R;

import org.osmdroid.views.MapView;
import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity implements FragmentDrawer.FragmentDrawerListener, BaseFragment.CommunicateToActivity {
    //private String TAG= MainActivity.class.getSimpleName();
    private FragmentDrawer drawerFragment;
    private NavigationFragment navigationFragment;
    private RadarFragment radarFragment;
    private GoogleMapsFragment mapsFragment;
    private OsmMapFragment osmMapsFragment;
    private RadarItemAdapter adapter;
    int currentViewSize=3;
    Boolean[] VisibilityArray = new Boolean[currentViewSize];
    ArrayList<Fragment> FragmentList = new ArrayList<Fragment>();
    ArrayList<RadarInfoItem> RadarItems = new ArrayList<>();
    private Transition.TransitionListener mEnterTransitionListener;
    int checkPacketcount=0;

    private static final String TAG = "MainActivity";
    private static final boolean D = true;

    // Message types sent from the BluetoothChatService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;

    // Key names received from the BluetoothChatService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";

    // Intent request codes
    private static final int REQUEST_ENABLE_BT = 1;

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
    private Resources res;
    float prevDegree=0;
    Handler UIHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        res=getResources();
        Arrays.fill(VisibilityArray,false);
        getWindow().setEnterTransition(new Explode());
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        drawerFragment = (FragmentDrawer)
                getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), toolbar);
        drawerFragment.setDrawerListener(this);

        if(D) Log.e(TAG, "+++ ON CREATE +++");

        HandlerThread handlerThread = new HandlerThread("Worker");
        handlerThread.start();
        Looper mLooper = handlerThread.getLooper();
        Worker worker = new Worker();
        UIHandler = new Handler(mLooper,worker);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            finish();
            return;
        }



        FragmentManager fragmentManager = getSupportFragmentManager();
        //Fragment currFragment = fragmentManager.findFragmentById(R.id.container_body);
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        if( navigationFragment==null) {
            navigationFragment = new NavigationFragment();
            fragmentTransaction.add(R.id.container_body, navigationFragment, "navigationFragment");
            fragmentTransaction.hide(navigationFragment);
            FragmentList.add(navigationFragment);
            Log.d(TAG, "navigationFragment was added");
        }

        //radar communication fragment.
        if( radarFragment==null) {
            radarFragment = new RadarFragment();
            fragmentTransaction.add(R.id.container_body, radarFragment, "radarFragment");
            fragmentTransaction.hide(radarFragment);
            FragmentList.add(radarFragment);
            Log.d(TAG, "radarFragment was added");
        }

        //toggle between using google maps/osm maps
//        if( mapsFragment==null) {
//            mapsFragment = new GoogleMapsFragment();
//            fragmentTransaction.add(R.id.container_body, mapsFragment, "mapsFragment");
//            fragmentTransaction.hide(mapsFragment);
//            FragmentList.add(mapsFragment);
//            Log.d(TAG, "mapsFragment was added");
//        }

        if( osmMapsFragment==null) {
            osmMapsFragment = new OsmMapFragment();
            fragmentTransaction.add(R.id.container_body, osmMapsFragment, "mapsFragment");
            fragmentTransaction.hide(osmMapsFragment);
            FragmentList.add(osmMapsFragment);
            Log.d(TAG, "mapsFragment was added");
        }
        fragmentTransaction.commit();

        displayView(0);
        adapter = new RadarItemAdapter(this,RadarItems);
        radarFragment.giveAdapterInstance(adapter);

        //Log.d("TAG", "saved instance state is null");

    }

    @Override
    public void onStart() {
        super.onStart();
        if(D) Log.e(TAG, "++ ON START ++");

        // If BT is not on, request that it be enabled.
        // setupChat() will then be called during onActivityResult
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
            // Otherwise, setup the chat session
        } else {
            if (mChatService == null) setupChat();
        }
    }


    @Override
    public synchronized void onResume() {
        super.onResume();
        if(D) Log.e(TAG, "+ ON RESUME +");

        // Performing this check in onResume() covers the case in which BT was
        // not enabled during onStart(), so we were paused to enable it...
        // onResume() will be called when ACTION_REQUEST_ENABLE activity returns.
        if (mChatService != null) {
            // Only if the state is STATE_NONE, do we know that we haven't started already
            if (mChatService.getState() == AndroidLocomateMessagingServer.STATE_NONE) {
                // Start the Bluetooth chat services
                mChatService.start();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(D) Log.d(TAG, "onActivityResult " + resultCode);
        switch (requestCode) {
            case REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if (resultCode == this.RESULT_OK) {
                    // Bluetooth is now enabled, so set up a chat session
                    setupChat();
                } else {
                    // User did not enable Bluetooth or an error occured
                    Log.d(TAG, "BT not enabled");
                    Toast.makeText(getApplicationContext(), R.string.bt_not_enabled_leaving, Toast.LENGTH_SHORT).show();
                    finish();
                }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Stop the Bluetooth chat services

        if (mChatService != null) mChatService.stop();
        if(D) Log.e(TAG, "--- ON DESTROY ---");
    }

    private void setupChat() {
        Log.d(TAG, "setupChat()");

        // Initialize the array adapter for the conversation thread
//        mConversationArrayAdapter = new CustomArrayAdapter(this, R.layout.message,DisplayItems);
//        mConversationView = (ListView) getActivity().findViewById(R.id.in);
//        mConversationView.setAdapter(mConversationArrayAdapter);

        // Initialize the BluetoothChatService to perform bluetooth connections
        mChatService = new AndroidLocomateMessagingServer(this, mHandler);
        ArrowImageView = (CustomImageView) findViewById(R.id.arrowimageview);
        DistanceTextView = (TextView) findViewById(R.id.speed);
    }


    // The Handler that gets information back from the BluetoothChatService
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_STATE_CHANGE:
                    if(D) Log.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
                    switch (msg.arg1) {
                        case AndroidLocomateMessagingServer.STATE_CONNECTED:
                            navigationFragment.updateState(res.getString(R.string.title_connected_to)+mConnectedDeviceName);
                            radarFragment.updateState(res.getString(R.string.title_connected_to)+mConnectedDeviceName);
                            //mTitle.setText(R.string.title_connected_to);
                            //mTitle.append(mConnectedDeviceName);
                            //mConversationArrayAdapter.clear();
                            break;
                        case AndroidLocomateMessagingServer.STATE_CONNECTING:
                            //mTitle.setText(R.string.title_connecting);
                            navigationFragment.updateState(res.getString(R.string.title_connecting));
                            radarFragment.updateState(res.getString(R.string.title_connecting));
                            break;
                        case AndroidLocomateMessagingServer.STATE_LISTEN:
                        case AndroidLocomateMessagingServer.STATE_NONE:
                            //mTitle.setText(R.string.title_not_connected);
                            navigationFragment.updateState(res.getString(R.string.title_not_connected));
                            radarFragment.updateState(res.getString(R.string.title_not_connected));
                            break;
                    }
                    break;
                case MESSAGE_WRITE:
                    byte[] writeBuf = (byte[]) msg.obj;
                    // construct a string from the buffer
                    String writeMessage = new String(writeBuf);
                    mConversationArrayAdapter.add("Me:  " + writeMessage);
                    break;
                case MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    String readMessage = new String(readBuf, 0, msg.arg1);

                    Log.d(TAG, "readMessageArg1: " + readMessage);
                    String[] splittedStrings = readMessage.split(",");
                    Log.d("SPLITTEDLEN", "" + splittedStrings.length);

                    if(splittedStrings[0].equalsIgnoreCase("test_rx") && !(splittedStrings.length < 7)){
                        float relativebearing=0;
                        float truebearing=Float.parseFloat(splittedStrings[4]);
                        float course=Float.parseFloat(splittedStrings[5]);
                        float speed=Float.parseFloat(splittedStrings[6]);
                        float distance = Float.parseFloat(splittedStrings[7])*1000;
                        if(distance < 5){
                            ArrowImageView.setImageResource(R.drawable.redarrow);
                        }else {
                            ArrowImageView.setImageResource(R.drawable.orangearrow);
                        }
                        relativebearing=truebearing-course;
                        Message msgToUI = UIHandler.obtainMessage();
                        msgToUI.obj = new DegreeModel(prevDegree, relativebearing);
                        UIHandler.sendMessageDelayed(msgToUI,100);

                        prevDegree=relativebearing;
                        DistanceTextView.setText(String.format("%.2f", distance));
                    }else if(splittedStrings[0].equalsIgnoreCase("autowbss_encdec") && !(splittedStrings.length < 6)){
                        float relativebearing=0;
                        float truebearing=Float.parseFloat(splittedStrings[3]);
                        float course=Float.parseFloat(splittedStrings[4]);
                        float speed=Float.parseFloat(splittedStrings[5]);
                        float distance = Float.parseFloat(splittedStrings[2])*1000;
                        if(distance < 5){
                            ArrowImageView.setImageResource(R.drawable.redarrow);
                        }else {
                            ArrowImageView.setImageResource(R.drawable.orangearrow);
                        }

                        //if(course != 28800f) {
                        if(course == 28800f)
                            course=0f;
                        Log.d("TAG", "course is "+course);
                        relativebearing = truebearing - course;
                        Message msgToUI = UIHandler.obtainMessage();
                        msgToUI.obj = new DegreeModel(prevDegree, relativebearing);
                        UIHandler.sendMessageDelayed(msgToUI, 100);
                        prevDegree = relativebearing;
                        DistanceTextView.setText(String.format("%.2f", distance));
                        //}
                    }else  if(splittedStrings[0].equalsIgnoreCase("radar") && !(splittedStrings.length < 9)){
                        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmssSSS");
                        int packetCount=Integer.parseInt(splittedStrings[8]);
                        if(packetCount==checkPacketcount) break;
                        checkPacketcount=packetCount;
                        Date date=null;
                        try {
                            date = df.parse(splittedStrings[1]);
                            Log.d("parsedDate",date.toString());
                            Date now = new Date();
                            //for checking latency time.
                            Log.d("TIMERECEIVED", date.toString() + " " + now.toString());
                            Log.d("TIMERADARTOPHONE",date.getTime()+" "+now.getTime() + " "+ packetCount);
                        }catch (Exception e){Log.d(TAG,"can't parse date");}

                        int laneIndex = Integer.parseInt(splittedStrings[3]);
                        int unforeseenDetectionType = Integer.parseInt(splittedStrings[4]);
                        float lng = Float.parseFloat(splittedStrings[5]);
                        float lat = Float.parseFloat(splittedStrings[6]);
                        int distance = Integer.parseInt(splittedStrings[7]);

                        boolean duplicate=false;
                        int index=-1;
                        if(RadarItems.isEmpty()){
                            RadarItems.add(new RadarInfoItem(
                                    laneIndex,date,unforeseenDetectionType,lng,lat,distance
                            ));
                            adapter.notifyDataSetChanged();
                        }
                        else {
                            for (RadarInfoItem items : RadarItems) {
                                if (items.getUnforeseenDetectionType() == unforeseenDetectionType) {
                                    duplicate=true;
                                    index=RadarItems.indexOf(items);
                                    break;
                                }
                            }

                            //create warning notifications from radar
                            if(duplicate){
                                RadarItems.set(index,new RadarInfoItem(
                                        laneIndex, date, unforeseenDetectionType, lng, lat, distance
                                ));
                            }else {
                                RadarItems.add(new RadarInfoItem(
                                        laneIndex, date, unforeseenDetectionType, lng, lat, distance
                                ));

                                adapter.notifyDataSetChanged();
                            }
                        }

                    }

                    break;
                case MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
                    Toast.makeText(getApplicationContext(), "Connected to "
                            + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                    break;
                case MESSAGE_TOAST:
                    Toast.makeText(getApplicationContext(), msg.getData().getString(TOAST),
                            Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        if(id == R.id.discoverable){
        // Ensure this device is discoverable by others
            ensureDiscoverable();
            return true;
        }

//        if(id == R.id.action_search){
//            Toast.makeText(getApplicationContext(), "Search action is selected!", Toast.LENGTH_SHORT).show();
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    private void ensureDiscoverable() {
        if(D) Log.d(TAG, "ensure discoverable");
        if (mBluetoothAdapter.getScanMode() !=
                BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(discoverableIntent);
        }
    }

    private void displayView(int position) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        String title = getString(R.string.app_name);


        for(int i=0;i<VisibilityArray.length; i++) {
            if(VisibilityArray[i]){
                fragmentTransaction.hide(FragmentList.get(i));
                //exitReveal();
                VisibilityArray[i]=false;
                break;
            }
        }

        switch (position) {
            case 0:
                if (navigationFragment!=null && !VisibilityArray[0]){ //reshow hidden fragment;

                    fragmentTransaction.setCustomAnimations(R.animator.slide_in_left,
                            R.animator.fade_out);
                    fragmentTransaction.show(navigationFragment);
                    //enterReveal();
                    Log.d(TAG,"navigationFragment was shown");

                }
                VisibilityArray[FragmentList.indexOf(navigationFragment)]=true;
                title = getString(R.string.title_navigation);
                break;
            case 1:
                if (radarFragment!=null && !VisibilityArray[1]){
                    fragmentTransaction.show(radarFragment); //reshow hidden fragment;
                    Log.d(TAG, "radarFragment was shown");

                }
                VisibilityArray[FragmentList.indexOf(radarFragment)]=true;
                title = getString(R.string.title_radar);
                break;
            case 2:
//                if (mapsFragment!=null && !VisibilityArray[2]){
//                    fragmentTransaction.show(mapsFragment); //reshow hidden fragment;
//                    Log.d(TAG, "radarFragment was shown");
//
//                }
//                VisibilityArray[FragmentList.indexOf(mapsFragment)]=true;
                if (osmMapsFragment!=null && !VisibilityArray[2]){
                    fragmentTransaction.show(osmMapsFragment); //reshow hidden fragment;
                    Log.d(TAG, "osmMapsFragment was shown");

                }
                VisibilityArray[FragmentList.indexOf(osmMapsFragment)]=true;
                title = getString(R.string.title_maps);
                break;
            default:
                break;
        }
        fragmentTransaction.commit();
        getSupportActionBar().setTitle(title);
    }

    private void enterReveal() {
        // previously invisible view
        final View myView = findViewById(R.id.framelayout);

        // get the center for the clipping circle
        int cx = myView.getMeasuredWidth() / 2;
        int cy = myView.getMeasuredHeight() / 2;

        // get the final radius for the clipping circle
        int finalRadius = Math.max(myView.getWidth(), myView.getHeight()) / 2;

        // create the animator for this view (the start radius is zero)
        Animator anim =
                ViewAnimationUtils.createCircularReveal(myView, cx, cy, 0, finalRadius);

        // make the view visible and start the animation
        myView.setVisibility(View.VISIBLE);
//        anim.addListener(new Animator.AnimatorListener() {
//            @Override
//            public void onAnimationStart(Animator animation) {
//
//            }
//
//            @Override
//            public void onAnimationEnd(Animator animation) {
//                getWindow().getEnterTransition().removeListener(mEnterTransitionListener);
//            }
//
//            @Override
//            public void onAnimationCancel(Animator animation) {
//
//            }
//
//            @Override
//            public void onAnimationRepeat(Animator animation) {
//
//            }
//        });
        anim.start();
    }

    private void exitReveal() {
        // previously visible view
        final View myView = findViewById(R.id.framelayout);

        // get the center for the clipping circle
        int cx = myView.getMeasuredWidth() / 2;
        int cy = myView.getMeasuredHeight() / 2;

        // get the initial radius for the clipping circle
        int initialRadius = myView.getWidth() / 2;

        // create the animation (the final radius is zero)
        Animator anim =
                ViewAnimationUtils.createCircularReveal(myView, cx, cy, initialRadius, 0);

        // make the view invisible when the animation is done
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                myView.setVisibility(View.INVISIBLE);
            }
        });
        // start the animation
        anim.start();
    }

    @Override
    public void onDrawerItemSelected(View view, int position) {
        displayView(position);
    }

    @Override
    public void getAdapterInstance(RadarItemAdapter adapter){
        if(this.adapter == null) {
            this.adapter=adapter;
            Log.d(TAG,"Adapter is set");
        }
    }

    public class Worker implements Handler.Callback {
        public Worker() {}
        public boolean handleMessage(Message msg) {
            DegreeModel dm=(DegreeModel)msg.obj;
            float x=ArrowImageView.getWidth()/2;
            float y=ArrowImageView.getHeight()/2;
            //Random rand=new Random();
            //newDegree = rand.nextFloat() * 720 - 360;
            Runnable run = new CustomRunnable(dm.prevDegree,dm.newDegree,ArrowImageView);
            Log.d("TAG", "Moving from " + dm.prevDegree + " to: " + dm.newDegree);
            //prevDegree=newDegree;
            ArrowImageView.post(run);
            return true;
        }
    }
}
