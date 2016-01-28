package com.r0adkll.postoffice.styles;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.r0adkll.postoffice.R;
import com.r0adkll.postoffice.model.Design;

/**
 * This style facilates setting up custom list dialogs
 *
 * Created by r0adkll on 8/22/14.
 */
public class ListStyle<T> implements Style, AdapterView.OnItemClickListener {

    // The content view of this style
    private DialogInterface mDialogInterface;
    private LinearLayout mContainer;
    private ImageView mSeperator;
    private ListView mListView;
    private OnItemAcceptedListener<T> mListener;

    /**
     * Constructor
     * @param ctx   the application context
     */
    public ListStyle(Context ctx){
        mContainer = new LinearLayout(ctx);
        mContainer.setOrientation(LinearLayout.VERTICAL);
        mSeperator = new ImageView(ctx);
        mSeperator.setBackgroundResource(R.color.grey_400);
        mSeperator.setVisibility(View.GONE);
        mSeperator.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 2));
        mContainer.addView(mSeperator);

        mListView = new ListView(ctx);
        mContainer.addView(mListView);

        // Set Default item click listener
        mListView.setOnItemClickListener(this);
        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if(firstVisibleItem > 0){
                    mSeperator.setVisibility(View.VISIBLE);
                }else{
                    mSeperator.setVisibility(View.GONE);
                }
            }
        });

    }

    @Override
    public View getContentView() {
        return mContainer;
    }

    @Override
    public void applyDesign(Design design, int themeColor) {
        if(!design.isMaterial()){
            mContainer.removeView(mSeperator);
        }else{
            if(design.isLight()){
                mSeperator.setBackgroundResource(R.color.grey_400);
            }else{
                mSeperator.setBackgroundResource(R.color.grey_800);
            }
        }
    }

    @Override
    public void onButtonClicked(int which, DialogInterface dialogInterface) {
        switch (which){
            case Dialog.BUTTON_POSITIVE:

                // Accept the selected content
                T selectedItem = (T) mListView.getSelectedItem();
                if(selectedItem != null){
                    if(mListener!=null)mListener.onItemAccepted(selectedItem, mListView.getSelectedItemPosition());
                }

                dialogInterface.dismiss();
                break;
            case Dialog.BUTTON_NEGATIVE:
                dialogInterface.dismiss();
                break;
            case Dialog.BUTTON_NEUTRAL:
                break;
        }
    }

    @Override
    public void onDialogShow(Dialog dialog) {
        mDialogInterface = dialog;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        T item = (T) parent.getItemAtPosition(position);
        if(mListener != null)
            mListener.onItemAccepted(item, position);

        if(mDialogInterface != null)
            mDialogInterface.dismiss();
    }

    /**
     * The Builder factory for this class
     */
    public static class Builder{

        // The object being built
        private ListStyle style;

        /**
         * Constructor
         * @param ctx       the application context
         */
        public Builder(Context ctx){
            style = new ListStyle(ctx);
        }

        /**
         * Set the ListView divider height
         *
         * @param height      the height of the divider between cells
         * @return            self for chaining
         */
        public Builder setDividerHeight(int height){
            style.mListView.setDividerHeight(height);
            return this;
        }

        /**
         * Set the divider drawable
         *
         * @param drawable      the drawable of the divider between cells
         * @return              self for chaining
         */
        public Builder setDivider(Drawable drawable){
            style.mListView.setDivider(drawable);
            return this;
        }

        /**
         * Set the List Selector on the ListView
         *
         * @param selector      the selector drawable
         * @return              self for chaining
         */
        public Builder setListSelector(Drawable selector){
            style.mListView.setSelector(selector);
            return this;
        }

        /**
         * Set the List Selector from a resource
         *
         * @param resourceId        the selector resource id
         * @return                  self for chaining
         */
        public Builder setListSelector(int resourceId){
            style.mListView.setSelector(resourceId);
            return this;
        }

        /**
         * Set the flag of whether or not to draw the list selector on
         * top of the content
         *
         * @param flag      the flag
         * @return          self for chaining
         */
        public Builder setDrawSelectorOnTop(boolean flag){
            style.mListView.setDrawSelectorOnTop(flag);
            return this;
        }

        /**
         * Set whether or not the footer dividers are drawn
         *
         * @param flag      the flag
         * @return          self for chaining
         */
        public Builder setFooterDividersEnabled(boolean flag){
            style.mListView.setFooterDividersEnabled(flag);
            return this;
        }

        /**
         * Set whether or not the header dividers are drawn
         *
         * @param flag      the flag
         * @return          self for chaining
         */
        public Builder setHeaderDividersEnabled(boolean flag){
            style.mListView.setHeaderDividersEnabled(flag);
            return this;
        }

        /**
         * Add a header view to the listview
         *
         * @param headerView        the header view to add
         * @return                  self for chaining
         */
        public Builder addHeader(View headerView){
            style.mListView.addHeaderView(headerView);
            return this;
        }

        /**
         * Add a header view to the listview
         *
         * @param headerView        the header view to add
         * @param data              data to attach to the view
         * @param isSelectable      whether or not the header view is selectable
         * @return                  self for chaining
         */
        public Builder addHeader(View headerView, Object data, boolean isSelectable){
            style.mListView.addHeaderView(headerView, data, isSelectable);
            return this;
        }

        /**
         * Add a footer view to the ListView
         *
         * @param footerView        the footer view to add
         * @return                  self for chaining
         */
        public Builder addFooter(View footerView){
            style.mListView.addFooterView(footerView);
            return this;
        }

        /**
         * Add a footer view to the ListView
         *
         * @param footerView        the footer view to add
         * @param data              data to attach to the view
         * @param isSelectable      whether or not the footer view is selectable
         * @return                  self for chaining
         */
        public Builder addFooter(View footerView, Object data, boolean isSelectable){
            style.mListView.addFooterView(footerView, data, isSelectable);
            return this;
        }

        /**
         * Set the on item click listener on the listview
         *
         * @param listener      the item click listener
         * @return              self for chaining
         */
        public Builder setOnItemClickListener(AdapterView.OnItemClickListener listener){
            style.mListView.setOnItemClickListener(listener);
            return this;
        }

        /**
         * Set the item long click listener on the ListView
         *
         * @param listener      the item long click listener
         * @return              self for chaining
         */
        public Builder setOnItemLongClickListener(AdapterView.OnItemLongClickListener listener){
            style.mListView.setOnItemLongClickListener(listener);
            return this;
        }

        /**
         * Set the item accepted item that the user selects from the list, this will dismiss the dialog
         *
         * @param listener      the listener
         * @return              self for chaining
         */
        public Builder setOnItemAcceptedListener(OnItemAcceptedListener listener){
            style.mListener = listener;
            return this;
        }

        /**
         * Build / Return the the ListStyle with an adapter last so to prevent
         * errors.
         *
         * @param adapter       the list adapter to bind to the listview
         * @return              the built ListStyle
         */
        public ListStyle build(BaseAdapter adapter){
            style.mListView.setAdapter(adapter);
            return style;
        }
    }

    /**
     * The item acceptance interface
     *
     * @param <T>       the list adapter item
     */
    public static interface OnItemAcceptedListener<T>{
        public void onItemAccepted(T item, int position);
    }

}
