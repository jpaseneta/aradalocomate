package com.example.inmsb.aradalocomate.CustomImplementations;

import android.graphics.Matrix;
import android.util.Log;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

import java.util.Random;

/**
 * Created by inmsb on 12/17/2015.
 */
public class CustomRunnable implements Runnable {
    float rotationDegree;
    float prevDegree;
    ImageView imv;
    //Bitmap bm;
    public CustomRunnable(float prevdegree,float rotationdegree,ImageView imv){
        this.rotationDegree=rotationdegree;
        this.prevDegree=prevdegree;
        this.imv=imv;
//        this.bm=bm;
    }

    public void run() {

        float x=imv.getWidth()/2;
        float y=imv.getHeight()/2;

        RotateAnimation rotateAnim = new RotateAnimation(prevDegree, rotationDegree, x, y);
        Log.d("Tag", "Rotate Pivot X coord: " + x);
        Log.d("Tag","Rotate Pivot Y coord: "+y);
        rotateAnim.setDuration(200);
        rotateAnim.setFillAfter(true);
        imv.startAnimation(rotateAnim);

    }

}
