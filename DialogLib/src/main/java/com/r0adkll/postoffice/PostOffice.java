package com.r0adkll.postoffice;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.ArrayAdapter;

import com.r0adkll.postoffice.handlers.AlertHandler;
import com.r0adkll.postoffice.model.Delivery;
import com.r0adkll.postoffice.model.Design;
import com.r0adkll.postoffice.model.Stamp;
import com.r0adkll.postoffice.styles.EditTextStyle;
import com.r0adkll.postoffice.styles.ListStyle;
import com.r0adkll.postoffice.styles.ProgressStyle;

import org.jetbrains.annotations.NotNull;


/**
 * This static class will be used to generate new Delivery dialog interfaces
 *
 * Project: PostOffice
 * Package: com.r0adkll.postoffice
 * Created by drew.heavner on 8/20/14.
 */
public class PostOffice {

    // Singleton Instance Variable
    private static PostOffice _instance = null;

    /**
     * Singleton Accessor for the post office
     *
     * @return      the singleton instance
     */
    public static PostOffice getPostman(){
        if(_instance == null) _instance = new PostOffice();
        return _instance;
    }

    /**
     * Lick the default {@link com.r0adkll.postoffice.model.Stamp} and apply it to outgoing {@link com.r0adkll.postoffice.ui.Mail}
     * for any delivery that isn't started with {@link #newMail(android.content.Context)}
     *
     * This is a convenience method for the singleton
     *
     * @param stamp     the stamp to lick and apply
     */
    public static void lick(Stamp stamp){
        getPostman().lickStamp(stamp);
    }

    /**
     * This is the default stamp instance that can be set for
     * teh post office to apply deliveries
     */
    private Stamp defaultStamp;

    /**
     * Hidden constructor
     */
    private PostOffice(){}

    /**
     * Lick the default {@link com.r0adkll.postoffice.model.Stamp} and apply it to outgoing {@link com.r0adkll.postoffice.ui.Mail}
     * for any delivery that isn't started with {@link #newMail(android.content.Context)}
     *
     * @param stamp     the stamp to lick and apply
     */
    public void lickStamp(Stamp stamp){
        defaultStamp = stamp;
    }

    /**
     * Start generating a new 'Mail' object to display a new
     * dialog.
     *
     * @param ctx       the application context
     * @return          the Delivery Builder
     */
    public static Delivery.Builder newMail(@NotNull Context ctx){
        // Create a new builder
        Delivery.Builder builder = new Delivery.Builder(ctx);

        // Apply defaults if possible
        applyDefaults(builder);

        // Return it
        return builder;
    }

    /**
     * Create a new AlertDialog 'Mail' delivery to display
     *
     * @param ctx           the application context
     * @param title         the dialog title
     * @param message       the dialog message
     * @return              the delivery
     */
    public static Delivery newAlertMail(@NotNull Context ctx, @NotNull CharSequence title, @NotNull CharSequence message){
        // Create the delivery builder
        Delivery.Builder builder = newMail(ctx)
                .setTitle(title)
                .setMessage(message);

        // Return the delivery
        return builder.build();
    }

    /**
     * Create a new AlertDialog 'Mail' delivery to display
     * with text resources
     *
     * @param ctx       the application context
     * @param title     the title string resource id
     * @param message   the message string resource id
     * @return          the new Delivery
     */
    public static Delivery newAlertMail(@NotNull Context ctx, @NotNull Integer title, @NotNull Integer message){
        // Create new Delivery
        Delivery.Builder builder = newMail(ctx)
                .setTitle(title)
                .setMessage(message);

        // Return the delivery
        return builder.build();
    }

    /**
     * Create a new AlertDialog 'Mail' delivery to display
     * with text resources
     *
     * @param ctx       the application context
     * @param title     the title string resource id
     * @param message   the message string
     * @return          the new Delivery
     */
    public static Delivery newAlertMail(@NotNull Context ctx, @NotNull Integer title, @NotNull CharSequence message){
        // Create new Delivery
        Delivery.Builder builder = newMail(ctx)
                .setTitle(title)
                .setMessage(message);

        return builder.build();
    }

    /**
     * Create a new AlertDialog 'Mail' delivery to display
     * with text resources
     *
     * @param ctx       the application context
     * @param title     the title string
     * @param message   the message string resource id
     * @return          the new Delivery
     */
    public static Delivery newAlertMail(@NotNull Context ctx, @NotNull CharSequence title, @NotNull Integer message){
        // Create new Delivery
        Delivery.Builder builder = newMail(ctx)
                .setTitle(title)
                .setMessage(message);

        return builder.build();
    }

    /**
     * Create an alert dialog 'Mail' delivery that provides the user with a 'Yes' or 'No' choice to
     *
     * @param ctx       the application context
     * @param title     the dialog title
     * @param message   the dialog message
     * @param handler   the choice handler
     * @return          the alert choice handler
     */
    public static Delivery newAlertMail(@NotNull Context ctx, @NotNull CharSequence title, @NotNull CharSequence message,
                                        @NotNull final AlertHandler handler){

        // Compose the delivery
        Delivery.Builder builder = newMail(ctx)
                .setTitle(title)
                .setMessage(message)
                .setButton(Dialog.BUTTON_POSITIVE, "Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        handler.onAccept();
                    }
                })
                .setButton(Dialog.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        handler.onDecline();
                    }
                });

        // Build the delivery
        return builder.build();
    }

    /**
     * Create an alert dialog 'Mail' delivery that provides the user with a 'Yes' or 'No' choice to
     *
     * @param ctx       the application context
     * @param title     the dialog title
     * @param message   the dialog message
     * @param handler   the choice handler
     * @return          the alert choice handler
     */
    public static Delivery newAlertMail(@NotNull Context ctx, @NotNull Integer title, @NotNull Integer message,
                                        @NotNull final AlertHandler handler){

        // Compose the delivery
        Delivery.Builder builder = newMail(ctx)
                .setTitle(title)
                .setMessage(message)
                .setButton(Dialog.BUTTON_POSITIVE, "Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        handler.onAccept();
                    }
                })
                .setButton(Dialog.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        handler.onDecline();
                    }
                });

        // Build the delivery
        return builder.build();
    }

    /**
     * Create an alert dialog 'Mail' delivery that provides the user with a 'Yes' or 'No' choice to
     *
     * @param ctx       the application context
     * @param title     the dialog title
     * @param message   the dialog message
     * @param handler   the choice handler
     * @return          the alert choice handler
     */
    public static Delivery newAlertMail(@NotNull Context ctx, @NotNull Integer title, @NotNull CharSequence message,
                                        @NotNull final AlertHandler handler){

        // Compose the delivery
        Delivery.Builder builder = newMail(ctx)
                .setTitle(title)
                .setMessage(message)
                .setButton(Dialog.BUTTON_POSITIVE, "Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        handler.onAccept();
                    }
                })
                .setButton(Dialog.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        handler.onDecline();
                    }
                });

        // Build the delivery
        return builder.build();
    }

    /**
     * Create an alert dialog 'Mail' delivery that provides the user with a 'Yes' or 'No' choice to
     *
     * @param ctx       the application context
     * @param title     the dialog title
     * @param message   the dialog message
     * @param handler   the choice handler
     * @return          the alert choice handler
     */
    public static Delivery newAlertMail(@NotNull Context ctx, @NotNull CharSequence title, @NotNull Integer message,
                                        @NotNull final AlertHandler handler){

        // Compose the delivery
        Delivery.Builder builder = newMail(ctx)
                .setTitle(title)
                .setMessage(message)
                .setButton(Dialog.BUTTON_POSITIVE, "Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        handler.onAccept();
                    }
                })
                .setButton(Dialog.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        handler.onDecline();
                    }
                });

        // Build the delivery
        return builder.build();
    }

    /**
     * Create a new EditText Dialog 'Mail' Delivery to display
     * - This creates a dialog with 2 buttons and an EditText field for
     *   the content and retrieves text input from your user
     *
     * @param ctx           the application context
     * @param title         the dialog title
     * @param hint          the EditText hint text
     * @param inputType     the EditText input type
     * @param listener      the text acceptance listener
     * @return              the new delivery
     */
    public static Delivery newEditTextMail(@NotNull Context ctx, CharSequence title, CharSequence hint, int inputType, EditTextStyle.OnTextAcceptedListener listener){
        // Create the delivery
        Delivery.Builder builder = newMail(ctx)
                .setTitle(title)
                .setStyle(new EditTextStyle.Builder(ctx)
                        .setHint(hint)
                        .setInputType(inputType)
                        .setOnTextAcceptedListener(listener)
                        .build());

        // Return the delivery
        return builder.build();
    }

    /**
     * Create a new EditText Dialog 'Mail' Delivery to display
     * - This creates a dialog with 2 buttons and an EditText field for
     *   the content and retrieves text input from your user
     *
     * @param ctx           the application context
     * @param title         the dialog title
     * @param hint          the EditText hint text
     * @param inputType     the EditText input type
     * @param listener      the text acceptance listener
     * @return              the new delivery
     */
    public static Delivery newEditTextMail(@NotNull Context ctx, Integer title, CharSequence hint, int inputType, EditTextStyle.OnTextAcceptedListener listener){
        // Create the delivery
        Delivery.Builder builder = newMail(ctx)
                .setTitle(title)
                .setStyle(new EditTextStyle.Builder(ctx)
                        .setHint(hint)
                        .setInputType(inputType)
                        .setOnTextAcceptedListener(listener)
                        .build());

        // Apply Defaults
        applyDefaults(builder);

        // Return the delivery
        return builder.build();
    }

    /**
     * Create a new ProgressDialog 'Mail' delivery to display
     *
     * @param ctx               the application context
     * @param title             the dialog title
     * @param suffix            the progress text suffix
     * @param indeterminate     the progress indeterminate flag
     * @return                  the new Delivery
     */
    public static Delivery newProgressMail(@NotNull Context ctx, CharSequence title, String suffix, boolean indeterminate){
        Delivery.Builder builder = new Delivery.Builder(ctx)
                .setTitle(title)
                .setStyle(new ProgressStyle.Builder(ctx)
                                .setIndeterminate(indeterminate)
                                .setCloseOnFinish(true)
                                .setPercentageMode(false)
                                .setSuffix(suffix)
                                .build());

        // Override some defaults
        return builder.setCancelable(false)
               .setCanceledOnTouchOutside(false)
               .build();
    }

    /**
     * Create a new ProgressDialog 'Mail' delivery to display
     *
     * @param ctx               the application context
     * @param title             the dialog title resource id
     * @param suffix            the progress text suffix
     * @param indeterminate     the progress indeterminate flag
     * @return                  the new Delivery
     */
    public static Delivery newProgressMail(@NotNull Context ctx, Integer title, String suffix, boolean indeterminate){
        Delivery.Builder builder = new Delivery.Builder(ctx)
                .setTitle(title)
                .setStyle(new ProgressStyle.Builder(ctx)
                        .setIndeterminate(indeterminate)
                        .setCloseOnFinish(true)
                        .setPercentageMode(false)
                        .setSuffix(suffix)
                        .build());

        // Override some defaults
        return builder.setCancelable(false)
                .setCanceledOnTouchOutside(false)
                .build();
    }

    /**
     * Create a new Simple List Dialog
     *
     * @param ctx           the application context
     * @param title         the dialog title
     * @param design        the dialog design
     * @param contents      the list of CharSequence for the contents
     * @param listener      the acceptance listener that gets called when the user selects an item
     * @return              the new Delivery
     */
    public static Delivery newSimpleListMail(@NotNull Context ctx, CharSequence title, Design design, CharSequence[] contents, ListStyle.OnItemAcceptedListener<CharSequence> listener){
        ArrayAdapter<CharSequence> adapterHolo = new ArrayAdapter<>(ctx, android.R.layout.simple_list_item_1, contents);
        ArrayAdapter<CharSequence> adapterMtrl = new ArrayAdapter<>(ctx, design.isLight() ? R.layout.simple_listitem_mtrl_light : R.layout.simple_listitem_mtrl_dark, contents);

        return newMail(ctx)
                .setTitle(title)
                .setDesign(design)
                .setStyle(new ListStyle.Builder(ctx)
                        .setDividerHeight(design.isMaterial() ? 0 : 2)
                        .setOnItemAcceptedListener(listener)
                        .build(design.isMaterial() ? adapterMtrl : adapterHolo))
                .build();
    }

    /**
     * If possible, apply the default stamp to this outgoing delivery
     *
     * @param builder       the delivery builder to apply defaults to
     */
    private static void applyDefaults(Delivery.Builder builder){
        Stamp defaults = getPostman().defaultStamp;
        if(defaults != null){
            builder.setDesign(defaults.getDesign())
                   .setCancelable(defaults.isCancelable())
                   .setCanceledOnTouchOutside(defaults.isCanceledOnTouchOutside())
                   .setIcon(defaults.getIcon())
                   .setThemeColor(defaults.getThemeColor())
                   .showKeyboardOnDisplay(defaults.isShowKeyboardOnDisplay());
        }
    }



}
