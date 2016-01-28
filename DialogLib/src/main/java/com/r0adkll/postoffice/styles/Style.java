package com.r0adkll.postoffice.styles;

import android.app.Dialog;
import android.content.DialogInterface;
import android.view.View;

import com.r0adkll.postoffice.model.Design;

/**
 * Project: PostOffice
 * Package: com.r0adkll.postoffice.model
 * Created by drew.heavner on 8/20/14.
 */
public interface Style {

    /**
     * Get the content view of the style
     *
     * @return  return the content view used as the content for this dialog
     */
    public View getContentView();

    /**
     * Apply the design of the delivery to this style
     *
     * @param design    the design, i.e. Holo, Material, Light, Dark
     */
    public void applyDesign(Design design, int themeColor);

    /**
     * Called when a button is clicked
     *
     * @param which
     * @param dialogInterface
     */
    public void onButtonClicked(int which, DialogInterface dialogInterface);

    /**
     * Called when the dialog this style is attached to is shown
     * but will also return the dialog object itself for further manipulation
     *
     * @param dialog        the dialog being shown
     */
    public void onDialogShow(Dialog dialog);

}
