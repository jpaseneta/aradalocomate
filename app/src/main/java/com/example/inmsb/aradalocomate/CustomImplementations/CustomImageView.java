package com.example.inmsb.aradalocomate.CustomImplementations;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by inmsb on 12/17/2015.
 */
public class CustomImageView extends ImageView {

    public CustomImageView(Context context){
        super(context);
    }

    public CustomImageView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param context
     * @param attrs
     * @param defStyle
     */
    public CustomImageView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        // TODO Auto-generated constructor stub
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Bitmap bm=((BitmapDrawable)this.getDrawable()).getBitmap();
        this.setScaleType(ScaleType.MATRIX);
        Matrix mat = new Matrix();
        float x=this.getLeft()+this.getWidth()/2;
        float y=this.getTop()+this.getHeight()/2;
        float z=this.getWidth()/2-this.getDrawable().getBounds().width()/2;
//        Log.d("TAG", "Width of drawable: "+this.getDrawable().getBounds().width());
//        Log.d("TAG", "On Draw Canvas X coord: "+x);
//        Log.d("TAG", "On Draw Canvas Y coord: "+y);
//        Log.d("TAG", "On Draw Canvas Z coord: "+z);
        canvas.drawBitmap(bm,z,0,null);
    }
}
