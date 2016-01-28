package com.r0adkll.postoffice.model;

import android.content.Context;

/**
 * Stamp
 *
 * A stamp is the configuration for Mail (i.e. Dialogs) and can be supplied with a mail
 * delivery call, or it can be configured at app launch with a default configuration.
 */
public class Stamp{

    /***********************************************
     *
     * Variables
     *
     */

    private Design mDesign;
    private boolean mIsCancelable = true;
    private boolean mIsCanceledOnTouchOutside = true;
    private int mThemeColor = -1;
    private int mIcon = -1;
    private boolean mShowKeyboardOnDisplay = true;

    /**
     * Hidden constructor
     */
    private Stamp(){}

    /**
     * Get this stamps design
     *
     * @return      the design, or null
     */
    public Design getDesign() {
        return mDesign;
    }

    /**
     * Return whether or not this configuration is cancelable
     *
     * @return      true if cancelable, false otherwise
     */
    public boolean isCancelable() {
        return mIsCancelable;
    }

    /**
     * Return whether or not the dialog is canceled when outside touch
     * is detected.
     *
     * @return      true if dialog cancels on outside touch
     */
    public boolean isCanceledOnTouchOutside() {
        return mIsCanceledOnTouchOutside;
    }

    /**
     * Get this stamps theme color
     *
     * @return      the theme color of this stamp
     */
    public int getThemeColor() {
        return mThemeColor;
    }

    /**
     * Get the stamp's icon
     *
     * @return      the dialog icon
     */
    public int getIcon() {
        return mIcon;
    }

    /**
     * Return whether or not the keyboard is displayed on show
     * of the dialog if the style is applicable
     *
     * @return      true if show on display, false otherwise
     */
    public boolean isShowKeyboardOnDisplay() {
        return mShowKeyboardOnDisplay;
    }

    /**
     * Stamp builder factory
     */
    public static class Builder{

        // App Context
        private Context ctx;

        // The builder object
        private Stamp stamp;

        /**
         * Constructor
         * @param ctx
         */
        public Builder(Context ctx){
            this.ctx = ctx;
            stamp = new Stamp();
        }

        /**
         * Set the default design of the PostOffice dialogs
         *
         * @param design        the default design
         * @return              self for chaining
         */
        public Builder setDesign(Design design){
            stamp.mDesign = design;
            return this;
        }

        /**
         * Set the dialog as cancelable
         *
         * @param val       the flag value
         * @return          self for chaining
         */
        public Builder setCancelable(boolean val){
            stamp.mIsCancelable = val;
            return this;
        }

        /**
         * Cancel the dialog on outside touch
         *
         * @param val           the flag value
         * @return              self for chaining
         */
        public Builder setCanceledOnTouchOutside(boolean val){
            stamp.mIsCanceledOnTouchOutside = val;
            return this;
        }

        /**
         * Set the default theme color
         *
         * @param color     the theme color
         * @return          self for chaining
         */
        public Builder setThemeColor(int color){
            stamp.mThemeColor = color;
            return this;
        }

        /**
         * Set the theme color with a resource color id
         *
         * @param colorResId        the color resource id
         * @return                  self for chaining
         */
        public Builder setThemeColorResource(int colorResId){
            stamp.mThemeColor = ctx.getResources().getColor(colorResId);
            return this;
        }

        /**
         * Set the default icon to use
         *
         * @param icon      the icon to use by default
         * @return
         */
        public Builder setIcon(int icon){
            stamp.mIcon = icon;
            return this;
        }

        /**
         * Show the keyboard on launch if applicable for the style
         * @param flag      the flag
         * @return          self for chaining
         */
        public Builder setShowKeyboardOnLaunch(boolean flag){
            stamp.mShowKeyboardOnDisplay = flag;
            return this;
        }

        /**
         * Build/Return the stamp
         *
         * @return      teh built Stamp configuration
         */
        public Stamp build(){
            return stamp;
        }

    }

}