package com.r0adkll.postoffice.handlers;

/**
 * Alert listener for handling the actions
 *
 * Created by r0adkll on 9/15/14.
 */
public interface AlertHandler {

    /**
     * Called when the user accepts the alert dialog via
     * the {@link android.app.Dialog#BUTTON_POSITIVE} press
     */
    public void onAccept();

    /**
     * Called when the user declines the alert dialog via
     * the {@link android.app.Dialog#BUTTON_NEGATIVE} press
     */
    public void onDecline();

}
