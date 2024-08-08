/*
 * Created by Itzik Braun on 12/3/2015.
 * Copyright (c) 2015 deluge. All rights reserved.
 *
 * Last Modification at: 3/12/15 4:27 PM
 */

package notification;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.UiModeManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.PowerManager;

import androidx.core.app.NotificationManagerCompat;

import java.security.SecureRandom;

import io.reactivex.disposables.Disposable;

import io.reactivex.functions.Consumer;
import sdk.chat.core.dao.Message;
import sdk.chat.core.dao.Thread;
import sdk.chat.core.dao.User;
import sdk.chat.core.session.ChatSDK;


public class NotificationDisplayHandler implements Consumer<Throwable> {

    public static final int MESSAGE_NOTIFICATION_ID = 100001;
    private static NotificationDisplayHandler instance;

    // Private constructor to prevent instantiation
    public NotificationDisplayHandler() {
    }

    // Method to return the single instance of the class
    public static synchronized NotificationDisplayHandler getInstance() {
        if (instance == null) {
            instance = new NotificationDisplayHandler();
        }
        return instance;
    }
    @SuppressLint("MissingPermission")
    public Disposable createMessageNotification(Message message) {

        final Context context = ChatSDK.ctx();

        if (connectedToAuto(context)) {
            return new NotificationBuilder(context).forMessageAuto(message).build().subscribe(builder -> {
                        NotificationManagerCompat.from(context).notify(MESSAGE_NOTIFICATION_ID, builder.build());
                    }, this);
        } else {
            return new NotificationBuilder(context).forMessage(message).build().subscribe(builder -> {
                Notification notification = builder.build();
                notification.flags = Notification.FLAG_AUTO_CANCEL;
                NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.notify(MESSAGE_NOTIFICATION_ID, notification);
                wakeScreen(context);
            }, this);
        }

    }


    private static volatile SecureRandom numberGenerator = null;
    private static final long MSB = 0x8000000000000000L;

    public static String unique() {
        SecureRandom ng = numberGenerator;
        if (ng == null) {
            numberGenerator = ng = new SecureRandom();
        }

        return Long.toHexString(MSB | ng.nextLong()) + Long.toHexString(MSB | ng.nextLong());
    }
    @SuppressLint("MissingPermission")
    public Disposable createCallNotification(final Context context, Intent resultIntent,
                                             String userEntityID, String title, PendingIntent pendingIntent,String msgType,Intent answerIntent,Intent deleteIntent) {

        // We are not connected... so we can't mark read or reply
        NotificationBuilder builder = new NotificationBuilder(context);
        builder.disableMarkRead();
        builder.disableReply();
        double random = Math.random() * 49 + 1;
        builder = builder.useDefault()
                    .setChannelName("Call_Notification")
                    .setIntent(resultIntent)
                    .setDeleteView(deleteIntent)
                    .addIconForUserEntityID(userEntityID)
                    .setTitle(title)
                    .setText("Incoming "+msgType+" call")
                    .setFullScreenIntent(pendingIntent)
                    .setAnswerIntent(answerIntent);

        return builder.build().subscribe(nb -> {
            NotificationManagerCompat.from(context).notify(MESSAGE_NOTIFICATION_ID, nb.build());
            wakeScreen(context);
        }, this);

    }
    @SuppressLint("MissingPermission")
    public Disposable createOngoingCallNotification(final Context context, Intent resultIntent,
                                             String userEntityID, String title, PendingIntent pendingIntent,String msgType,Intent answerIntent,Intent deleteIntent) {

        // We are not connected... so we can't mark read or reply
        NotificationBuilder builder = new NotificationBuilder(context);
        builder.disableMarkRead();
        builder.disableReply();
        double random = Math.random() * 49 + 1;
        builder = builder.useDefault()
                .setChannelName("Call_Notification")
                .setIntent(resultIntent)
                .setDeleteView(deleteIntent)
                .addIconForUserEntityID(userEntityID)
                .setTitle(title)
                .setText("Incoming "+msgType+" call")
                .setFullScreenIntent(pendingIntent)
                .setAnswerIntent(answerIntent);

        return builder.build().subscribe(nb -> {
            NotificationManagerCompat.from(context).notify(MESSAGE_NOTIFICATION_ID, nb.build());
            wakeScreen(context);

        }, this);

    }
    @SuppressLint("MissingPermission")
    public Disposable createMessageNotification(final Context context, Intent resultIntent, String userEntityID, String threadEntityId, String title, String message) {

        Thread thread = ChatSDK.db().fetchThreadWithEntityID(threadEntityId);

        // We are not connected... so we can't mark read or reply
        NotificationBuilder builder = new NotificationBuilder(context);
//        builder.disableMarkRead();
//        builder.disableReply();

        boolean connectedToAuto = connectedToAuto(context);

        if (thread != null) {
            builder = builder.forAuto(title, message, null);
            if (!connectedToAuto) {
                builder = builder.setIntent(resultIntent);
            }
        } else {
            builder = builder.useDefault()
                    .setIntent(resultIntent)
                    .addIconForUserEntityID(userEntityID)
                    .setTitle(title)
                    .setText(message);
        }
        User user = ChatSDK.db().fetchUserWithEntityID(userEntityID);
        if (user != null) {
            builder.addIconForUser(user);
        }

        return builder.build().subscribe(nb -> {
            NotificationManagerCompat.from(context).notify(MESSAGE_NOTIFICATION_ID, nb.build());
            if (!connectedToAuto) {
                wakeScreen(context);
            }
        }, this);

    }



    /**
     * Waking up the screen
     * * * */
    protected void wakeScreen(Context context){

        // Waking the screen so the user will see the notification
        PowerManager pm = (PowerManager)context.getSystemService(Context.POWER_SERVICE);

        boolean isScreenOn;

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT_WATCH)
            isScreenOn = pm.isScreenOn();
        else
            isScreenOn = pm.isInteractive();

        if(!isScreenOn)
        {

            PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK
                    |PowerManager.ON_AFTER_RELEASE
                    |PowerManager.ACQUIRE_CAUSES_WAKEUP, "chat-sdk:MyLock");

            wl.acquire(5000);
            wl.release();
        }
    }

    public static boolean connectedToAuto(Context context) {
        UiModeManager uiModeManager = (UiModeManager) context.getSystemService(Context.UI_MODE_SERVICE);
        if (uiModeManager.getCurrentModeType() == Configuration.UI_MODE_TYPE_CAR) {
            return true;
        }
        return false;
    }

    @Override
    public void accept(Throwable t) {
        t.printStackTrace();
    }
}
