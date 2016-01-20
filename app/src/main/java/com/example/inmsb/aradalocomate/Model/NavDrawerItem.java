package com.example.inmsb.aradalocomate.Model;

import android.graphics.drawable.Drawable;

/**
 * Created by inmsb on 12/17/2015.
 */
public class NavDrawerItem {
    private boolean showNotify;
    private String title;
    private Drawable drawable;

    public NavDrawerItem() {

    }

    public NavDrawerItem(boolean showNotify, String title, Drawable drawable) {
        this.showNotify = showNotify;
        this.title = title;
        this.drawable = drawable;
    }

    public boolean isShowNotify() {
        return showNotify;
    }

    public void setShowNotify(boolean showNotify) {
        this.showNotify = showNotify;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Drawable getDrawable() { return drawable; }

    public void setDrawable(Drawable drawable) { this.drawable = drawable;}
}
