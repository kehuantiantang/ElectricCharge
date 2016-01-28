package com.yj.eleccharge.util;

import android.graphics.drawable.Drawable;

public class DrawerListItem {
    private Drawable icon;
    private String title;
    private String classString;

    public DrawerListItem(Drawable icon, String title, String classString) {
        this.icon = icon;
        this.title = title;
        this.classString = classString;
    }
    public String getClassString() {
        return classString;
    }

    public void setClassString(String classString) {
        this.classString = classString;
    }

    public Drawable getIcon() {
        return icon;
    }

    public String getTitle() {
        return title;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public void setTitle(String title) {
        this.title = title;
    }

}
