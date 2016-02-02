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
        float z=this.getWidth()/2-this.getDrawable().getBounds().width()/2;
        canvas.drawBitmap(bm,z,0,null);
    }
}
