package com.r0adkll.postoffice.styles;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;

import com.r0adkll.postoffice.R;
import com.r0adkll.postoffice.model.Design;

/**
 * Created by r0adkll on 8/20/14.
 */
public class EditTextStyle implements Style {

    private EditText mInputField;
    private OnTextAcceptedListener mListener;

    /**
     * Constructor
     * @param ctx
     */
    private EditTextStyle(Context ctx){
        mInputField = new EditText(ctx);


    }

    /**
     * Get the content view for this style
     * @return
     */
    @Override
    public View getContentView() {
        return mInputField;
    }

    /**
     * Apply a design to the style
     * @param design    the design, i.e. Holo, Material, Light, Dark
     */
    @Override
    public void applyDesign(Design design, int themeColor) {
        Context ctx = mInputField.getContext();

        int smallPadId = design.isMaterial() ? R.dimen.default_margin : R.dimen.default_margin_small;
        int largePadId = design.isMaterial() ? R.dimen.material_edittext_spacing : R.dimen.default_margin_small;

        int padLR = ctx.getResources().getDimensionPixelSize(largePadId);
        int padTB = ctx.getResources().getDimensionPixelSize(smallPadId);

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(padLR, padTB, padLR, padTB);
        mInputField.setLayoutParams(params);

        if(design.isLight())
            mInputField.setTextColor(mInputField.getResources().getColor(R.color.background_material_dark));
        else
            mInputField.setTextColor(mInputField.getResources().getColor(R.color.background_material_light));

        Drawable drawable;
        if(design.isMaterial()) {
            drawable = mInputField.getResources().getDrawable(R.drawable.edittext_mtrl_alpha);
        }else{
            drawable = mInputField.getBackground();
        }

        drawable.setColorFilter(themeColor, PorterDuff.Mode.SRC_ATOP);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
            mInputField.setBackground(drawable);
        else
            mInputField.setBackgroundDrawable(drawable);

    }

    /**
     * Called when one of the three available buttons are clicked
     * so that this style can perform a special action such as calling a content
     * delivery callback.
     *
     * @param which                 which button was pressed
     * @param dialogInterface
     */
    @Override
    public void onButtonClicked(int which, DialogInterface dialogInterface) {
        switch (which){
            case Dialog.BUTTON_POSITIVE:
                if(mListener != null) mListener.onAccepted(getText());
                break;
            case Dialog.BUTTON_NEGATIVE:
                // Do nothing for the negative click
                break;
        }
    }

    @Override
    public void onDialogShow(Dialog dialog) {}

    /**
     * Get the core {@link android.widget.EditText} widget for this style
     * to apply special/custom attributes to
     *
     * @return      the raw edit text view of this Style
     */
    public EditText getEditTextView(){
        return mInputField;
    }

    /**
     * Get the text content of the input field
     *
     * @return      the user entered text content
     */
    public String getText(){
        return mInputField.getText().toString();
    }

    /**
     * This classes builder class, to chain-construct
     * this object
     */
    public static class Builder{

        // The style being built
        private EditTextStyle style;

        /**
         * Constructor
         * @param ctx
         */
        public Builder(Context ctx){
            style = new EditTextStyle(ctx);
        }

        /**
         * Set the EditText fields text content
         *
         * @param text      the text content to fill with
         * @return          self for chaining
         */
        public Builder setText(CharSequence text){
            style.getEditTextView().setText(text);
            return this;
        }

        /**
         * Set the EditText fields hint text content
         *
         * @param hint      the text fields hint to display
         * @return          self for chaining
         */
        public Builder setHint(CharSequence hint){
            style.getEditTextView().setHint(hint);
            return this;
        }

        /**
         * Set the text color of the text field to display
         *
         * @param color     the text color
         * @return          self for chaining
         */
        public Builder setTextColor(int color){
            style.getEditTextView().setTextColor(color);
            return this;
        }

        /**
         * Set the hint text color of the input field
         *
         * @param hintColor     the hint text color
         * @return              self for chaining
         */
        public Builder setHintColor(int hintColor){
            style.getEditTextView().setHintTextColor(hintColor);
            return this;
        }

        /**
         * Add a text change listener for when the user changes the text in
         * the text field.
         *
         * @param watcher       the text watcher to observe input changes
         * @return              self for chaining
         */
        public Builder addTextWatcher(TextWatcher watcher){
            style.getEditTextView().addTextChangedListener(watcher);
            return this;
        }

        /**
         * Set the input fields input type (i.e. email, caps, numbers, etc)
         *
         * @param type      the input type to set
         * @return          self for chaining
         */
        public Builder setInputType(int type){
            style.getEditTextView().setInputType(type);
            return this;
        }

        /**
         * Set the text acceptance listener that gets called when the
         * user presses the POSITIVE_BUTTON on the dialog to accept the
         * text input, and this is the way to listen for that input.
         *
         * @param listener      the acceptance listener
         * @return              self for chaining
         */
        public Builder setOnTextAcceptedListener(OnTextAcceptedListener listener){
            style.mListener = listener;
            return this;
        }

        /**
         * Build the input field style
         *
         * @return      the built EditTextStyle style
         */
        public EditTextStyle build(){
            return style;
        }

    }

    /**
     * The text acceptance listener that gets called when the user accepts
     * the inputed text in this style
     */
    public static interface OnTextAcceptedListener{
        public void onAccepted(String text);
    }

}
