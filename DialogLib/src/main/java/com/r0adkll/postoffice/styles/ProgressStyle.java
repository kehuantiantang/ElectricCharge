package com.r0adkll.postoffice.styles;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Handler;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ftinc.fontloader.FontLoader;
import com.ftinc.fontloader.Types;
import com.r0adkll.postoffice.R;
import com.r0adkll.postoffice.model.Design;

/**
 * Created by r0adkll on 8/21/14.
 * @deprecated  This is an anti-pattern, so it will no longer be supported
 */
public class ProgressStyle implements Style {

    /***********************************************************************************************
     *
     * Constants
     *
     */

    public static final int SMALL = 0;
    public static final int NORMAL = 1;
    public static final int LARGE=  2;
    public static final int HORIZONTAL = 3;

    /***********************************************************************************************
     *
     * Variables
     *
     */

    private Context mCtx;
    private View mLayout;
    private ProgressBar mProgress;
    private TextView mProgressText, mProgressMax;

    private CharSequence mMessage;
    private CharSequence mSuffix;
    private boolean mIsCloseOnFinish = false;
    private boolean mIsPercentageMode = false;
    private boolean mIsIndeterminate = false;
    private int mInitProgress = 0;
    private int mInitMax = 0;
    private int mProgressStyle = HORIZONTAL;

    private DialogInterface mDialogInterface;

    /**
     * Constructor
     *
     * @param ctx
     * @deprecated  This is an anti-pattern, so it will no longer be supported
     */
    public ProgressStyle(Context ctx){
        mCtx = ctx;
        mLayout = LayoutInflater.from(mCtx).inflate(R.layout.layout_progress_style, null, false);
        mProgressText = (TextView) mLayout.findViewById(R.id.progress);
        mProgressMax = (TextView) mLayout.findViewById(R.id.total);
    }

    /***********************************************************************************************
     *
     * Lifecycle Methods
     *
     */

    /**
     * Get this style's content view
     *
     * @return      the view of the style
     */
    @Override
    public View getContentView() {
        return mLayout;
    }

    /**
     * Apply the design of a delivery to this style
     *
     * @param design    the design, i.e. Holo, Material, Light, Dark
     * @param themeColor    the theme color
     */
    @Override
    public void applyDesign(Design design, int themeColor) {

        if(mProgressStyle == HORIZONTAL) {

            LayerDrawable drawable = (LayerDrawable) mLayout.getResources().getDrawable(R.drawable.progress_material_horizontal);

            Drawable bg = drawable.getDrawable(0);
            int color = design.isLight() ? R.color.grey_700 : R.color.grey_500;
            bg.setColorFilter(mLayout.getResources().getColor(color), PorterDuff.Mode.SRC_ATOP);

            Drawable secProg = drawable.getDrawable(1);
            secProg.setColorFilter(lighten(themeColor), PorterDuff.Mode.SRC_ATOP);

            Drawable prg = drawable.getDrawable(2);
            prg.setColorFilter(themeColor, PorterDuff.Mode.SRC_ATOP);

            mProgress.setProgressDrawable(drawable);

            // Style the indeterminate drawable color
            Drawable animDrawable = mProgress.getIndeterminateDrawable();
            animDrawable.setColorFilter(themeColor, PorterDuff.Mode.SRC_ATOP);

        }

        if(design.isLight()){
            mProgressText.setTextColor(mLayout.getResources().getColor(R.color.tertiary_text_material_light));
            mProgressMax.setTextColor(mLayout.getResources().getColor(R.color.tertiary_text_material_light));
        }else{
            mProgressText.setTextColor(mLayout.getResources().getColor(R.color.tertiary_text_material_dark));
            mProgressMax.setTextColor(mLayout.getResources().getColor(R.color.tertiary_text_material_dark));
        }

        if(design.isMaterial()){
            FontLoader.applyTypeface(mProgressText, Types.ROBOTO_MEDIUM);
            FontLoader.applyTypeface(mProgressMax, Types.ROBOTO_MEDIUM);
        }

        // Style the progress message if available
        TextView progressMessage = (TextView) mLayout.findViewById(R.id.progress_mesage);
        if(progressMessage != null){

            if(design.isMaterial()){
                LinearLayout progressContainer = (LinearLayout) mLayout.findViewById(R.id.progress_container);
                progressContainer.removeView(progressMessage);

                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) progressMessage.getLayoutParams();
                CharSequence text = progressMessage.getText();
                if(design.isLight()){
                    progressMessage = new TextView(mCtx, null, R.style.Widget_PostOffice_Material_Light_Dialog_Message);
                }else{
                    progressMessage = new TextView(mCtx, null, R.style.Widget_PostOffice_Material_Dark_Dialog_Message);
                }

                // Re Update
                progressMessage.setText(text);

                // Set the roboto font
                FontLoader.applyTypeface(progressMessage, Types.ROBOTO_REGULAR);

                // Add it back to the layout
                progressContainer.addView(progressMessage, params);

            }
        }

    }


    @Override
    public void onButtonClicked(int which, DialogInterface dialogInterface) {}

    @Override
    public void onDialogShow(Dialog dialog) {
        mDialogInterface = dialog;
    }

    /***********************************************************************************************
     *
     * Helper Methods
     *
     */

    private void initView(){
        LinearLayout mProgressContainer = (LinearLayout) mLayout.findViewById(R.id.progress_container);

        int padding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, mLayout.getResources().getDisplayMetrics());
        mLayout.setPadding(padding, padding, padding, padding);

        // Based on configuration from Builder, construct the Layout
        switch (mProgressStyle){
            case HORIZONTAL:
                mProgress = new ProgressBar(mCtx, null, android.R.attr.progressBarStyleHorizontal);
                break;
            case SMALL:
                mIsIndeterminate = true;
                mProgressText.setVisibility(View.GONE);
                mProgressMax.setVisibility(View.GONE);
                mProgress = new ProgressBar(mCtx, null, android.R.attr.progressBarStyleSmall);
                break;
            case NORMAL:
                mIsIndeterminate = true;
                mProgressText.setVisibility(View.GONE);
                mProgressMax.setVisibility(View.GONE);
                mProgress = new ProgressBar(mCtx);
                break;
            case LARGE:
                mIsIndeterminate = true;
                mProgressText.setVisibility(View.GONE);
                mProgressMax.setVisibility(View.GONE);
                mProgress = new ProgressBar(mCtx, null, android.R.attr.progressBarStyleLarge);
                break;
        }

        // The ProgressBar parameters
        LinearLayout.LayoutParams progressParams;

        // Generate the message parameters
        int margin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, mCtx.getResources().getDisplayMetrics());
        LinearLayout.LayoutParams messageParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);
        messageParams.weight = 1f;
        messageParams.gravity = Gravity.CENTER_VERTICAL;
        messageParams.setMargins(margin, 0, margin, 0);

        // Now based on the provided message content, change up hte LayoutParameters of the progresses
        if(mProgressStyle == HORIZONTAL){
            progressParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }else{
            if(mMessage != null){
                progressParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            }else{
                progressParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            }
        }

        Log.d("ProgressStyle", "Setting ProgressBar LayoutParams(" + progressParams.width + "," + progressParams.height + ")");

        // Add the stylized progress bar to the layout
        mProgress.setLayoutParams(progressParams);
        mProgressContainer.addView(mProgress);

        // If the message isn't null, and it's not the progress style
        if(mMessage != null && mProgressStyle != HORIZONTAL){

            // Create and add the text view
            TextView message = new TextView(mCtx);
            message.setId(R.id.progress_mesage);
            FontLoader.applyTypeface(message, Types.ROBOTO_REGULAR);
            message.setText(mMessage);

            // Add this to the layout
            mProgressContainer.addView(message, messageParams);
        }

        // Now based on other attributes style the progressBar
        if(mProgressStyle == HORIZONTAL) {
            if (mIsIndeterminate) {
                Log.d(ProgressStyle.class.getName(), "Setting Indeterminate for Horizontal ProgressStyle");

                // Update the ProgressBar
                mProgress.setIndeterminate(true);

                // Hide the auxillary text
                mProgressText.setVisibility(View.GONE);
                mProgressMax.setVisibility(View.GONE);

            } else {
                Log.d(ProgressStyle.class.getName(), "Setting Progress for Horizontal ProgressStyle");

                // Only if the progress style is horizontal, should
                // we attempt to set the progress and max
                mProgress.setIndeterminate(false);
                setProgress(mInitProgress);
                setMax(mInitMax);

            }
        }
    }

    /**
     * Set the progress of the progress bar
     *
     * @param value     the progress out of the max
     * @return          self for chaining
     */
    public ProgressStyle setProgress(int value){

        if(mProgressStyle == HORIZONTAL) {

            if (mProgress.isIndeterminate())
                mProgress.setIndeterminate(false);

            if (mProgress.getMax() <= value)
                mProgress.setMax(value);

            mProgress.setProgress(value);

            if (!mIsPercentageMode) {
                mProgressText.setText(String.format("%d %s", value, mSuffix));
            } else {
                mProgressText.setVisibility(View.GONE);

                int progress = mProgress.getProgress();
                int max = mProgress.getMax();
                float percent = ((float) progress / (float) max);
                int percentNorm = (int) (percent * 100);

                mProgressMax.setText(String.format("%d %%", percentNorm));
            }

            if (value >= mProgress.getMax() && mIsCloseOnFinish && mDialogInterface != null) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mDialogInterface.dismiss();

                    }
                }, 300);
            }

        }

        return this;
    }

    /**
     * Set the max total progress of the progress bar
     *
     * @param value     the max value
     * @return          self for chaining
     */
    public ProgressStyle setMax(int value){

        if(mProgressStyle == HORIZONTAL) {

            if (mProgress.isIndeterminate())
                mProgress.setIndeterminate(false);

            mProgress.setMax(value);

            if (!mIsPercentageMode)
                mProgressMax.setText(String.format("%d %s", value, mSuffix));

        }

        return this;
    }

    /**
     * Set this progress as Indeterminate
     *
     * @param value     indeterminate value
     * @return          self for chaining
     */
    public ProgressStyle setIndeterminate(boolean value){
        if(mProgressStyle == HORIZONTAL) {
            mProgress.setIndeterminate(value);
            mProgressText.setVisibility(value ? View.GONE : View.VISIBLE);
            mProgressMax.setVisibility(value ? View.GONE : View.VISIBLE);
        }
        return this;
    }

    /**
     * Get a lighter color for a given color
     *
     * @param color     the color to lighten
     * @return          the lightened color
     */
    private int lighten(int color){
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] = 1.0f - 0.8f * (1.0f - hsv[2]);
        color = Color.HSVToColor(hsv);
        return color;
    }


    /***********************************************************************************************
     *
     * Builder Class
     *
     */


    /**
     * This style's builder class
     */
    public static class Builder{

        // The Object being built
        private Context mCtx;
        private ProgressStyle style;

        /**
         * Constructor
         * @param ctx
         */
        public Builder(Context ctx){
            mCtx = ctx;
            style = new ProgressStyle(ctx);
        }

        /**
         * Set the progress message text (instead of using {@link com.r0adkll.postoffice.model.Delivery.Builder#setMessage(CharSequence)})
         * if you are using a progress style other than {@link #HORIZONTAL}
         *
         * @param message       the message to set
         * @return              self for chaining
         */
        public Builder setProgressMessage(CharSequence message){
            style.mMessage = message;
            return this;
        }

        /**
         * Set the progress message text using {@link java.lang.String#format(String, Object...)}
         * (instead of using {@link com.r0adkll.postoffice.model.Delivery.Builder#setMessage(CharSequence)})
         * if you are using a progress style other than {@link #HORIZONTAL}
         *
         * @param message       the message to set with format parameters
         * @param args          the format arguments to insert into the message string
         * @return              self for chaining
         */
        public Builder setProgressMesssage(String message, Object... args){
            style.mMessage = String.format(message, args);
            return this;
        }

        /**
         * Set the progress message text (instead of using {@link com.r0adkll.postoffice.model.Delivery.Builder#setMessage(CharSequence)})
         * if you are using a progress style other than {@link #HORIZONTAL}
         *
         * @param messageResId  the resource id of the string you want to use
         * @return              self for chaining
         */
        public Builder setProgressMessage(int messageResId){
            style.mMessage = mCtx.getString(messageResId);
            return this;
        }

        /**
         * Set the progress and max label suffixes i.e. Mb, Gb, byte
         *
         * @param sfx       the suffix to set
         * @return          self for chaining
         */
        public Builder setSuffix(String sfx){
            style.mSuffix = sfx;
            return this;
        }

        /**
         * Set the progress bar as an indeterminate
         *
         * @param value     the indeterminate value
         * @return          self for chaining
         */
        public Builder setIndeterminate(boolean value){
            style.mIsIndeterminate = value;
            return this;
        }

        /**
         * Set this progress dialog to close when it finishes
         * i.e. when progress >= max
         *
         * @param value     the flag value
         * @return          self for chaining
         */
        public Builder setCloseOnFinish(boolean value){
            style.mIsCloseOnFinish = value;
            return this;
        }

        /**
         * Set this progress style to percentage mode where it only displays the
         * progress' percentage completion
         *
         * @param value     the flag value
         * @return          self for chaining
         */
        public Builder setPercentageMode(boolean value){
            style.mIsPercentageMode = value;
            return this;
        }

        /**
         * This this progress's indeterminate style to horizontal mode
         *
         * @param value     true for horizontal indeterminate mode
         * @return          self for chaining
         * @deprecated      Please use {@link #setProgressStyle(int)}, this function now does nothing
         */
        public Builder setHorizontalMode(boolean value){
            return this;
        }

        /**
         * Set the style to use for the progress bar style
         *
         * @see #HORIZONTAL
         * @see #SMALL
         * @see #NORMAL
         * @see #LARGE
         *
         * @param progressStyle     the progress style to use in the dialog
         * @return                  self for chaining
         */
        public Builder setProgressStyle(int progressStyle){
            style.mProgressStyle = progressStyle;
            return this;
        }

        /**
         * Set the initial progress and max for the progress bar
         *
         * @param progress      the progress amount
         * @param max           the max progress allowed
         * @return              self for chaining
         */
        public Builder setProgress(int progress, int max){
            style.mInitProgress = progress;
            style.mInitMax = max;
            return this;
        }

        /**
         * Complete and return the Progress Style build
         * @return  built progress style
         */
        public ProgressStyle build(){
            style.initView();
            return style;
        }

    }

}
