package sdk.chat.firebase.push;

import static sdk.chat.core.dao.Keys.Type;
import static sdk.chat.core.push.AbstractPushHandler.Action;
import static sdk.chat.core.push.AbstractPushHandler.Body;
import static sdk.chat.core.push.AbstractPushHandler.SenderId;
import static sdk.chat.core.push.AbstractPushHandler.SenderName;
import static sdk.chat.core.push.AbstractPushHandler.ThreadId;
import static sdk.chat.core.push.AbstractPushHandler.UserIds;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationManagerCompat;


import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import notification.NotificationCancelActivity;
import notification.NotificationDisplayHandler;
import sdk.chat.core.dao.Keys;
import sdk.chat.core.push.BroadcastHandler;
import sdk.chat.core.session.ChatSDK;
import sdk.chat.ui.IncomingCallActivity;
import sdk.chat.ui.ReceiverActivity;

public class DefaultMessagingService extends FirebaseMessagingService {

    private Handler handler;
    private Runnable notificationCancellationRunnable;
    private boolean callReceived = false;
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {


        Intent remoteIntent = remoteMessage.toIntent();
        Bundle extras = remoteIntent.getExtras();

        final String threadEntityID = extras.getString(Keys.PushKeyThreadEntityID);
        final String userEntityID = extras.getString(Keys.PushKeyUserEntityID);
        String messageType = getMessageType(remoteMessage);


        String senderNumber = remoteMessage.getData().get("chat_sdk_push_title");

        if (messageType.equals("audio") || messageType.equals("video")) {
            sendRingingMessageAsync(senderNumber);
            if (!ChatSDK.db().isDatabaseOpen()) {
                String currentUserId = ChatSDK.auth().getCurrentUserEntityID();
                if (currentUserId != null) {
                    try {
                        ChatSDK.db().openDatabase(currentUserId);
                    } catch (Exception e) {
                        Log.d("DefaultMessagingService",e.getMessage());
                    }
                }
            }
            ChatSDK.mediaStart(true, getApplicationContext());
            Intent fullScreenIntent = new Intent(getApplicationContext(), IncomingCallActivity.class);
            fullScreenIntent.putExtra("senderNumber", senderNumber);
            fullScreenIntent.putExtra("type", messageType);
            PendingIntent fullScreenPendingIntent;
            final Context context = ChatSDK.ctx();
            fullScreenIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                fullScreenPendingIntent = PendingIntent.getActivity(context, 0, fullScreenIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
            } else {
                fullScreenPendingIntent = PendingIntent.getActivity(context, 0, fullScreenIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            }
            Intent answerIntent = new Intent(this, ReceiverActivity.class);
            answerIntent.putExtra("senderNumber", senderNumber);
            answerIntent.putExtra("type", messageType);

            Intent appIntent = new Intent(getApplicationContext(), IncomingCallActivity.class);
            appIntent.putExtra("senderNumber", senderNumber);
            appIntent.putExtra("type", messageType);
            appIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            Intent deleteIntent = new Intent(getApplicationContext(), NotificationCancelActivity.class);
            deleteIntent.putExtra("senderNumber", senderNumber);
            deleteIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

            NotificationDisplayHandler notification = NotificationDisplayHandler.getInstance();
            notification.createCallNotification(getApplicationContext(), appIntent, senderNumber, senderNumber, fullScreenPendingIntent, messageType, answerIntent, deleteIntent);
            scheduleNotificationCancellation(100001,senderNumber);
        } else if (messageType.equals("cancel")) {

            if (!ChatSDK.db().isDatabaseOpen()) {
                String currentUserId = ChatSDK.auth().getCurrentUserEntityID();
                if (currentUserId != null) {
                    try {
                        ChatSDK.db().openDatabase(currentUserId);
                    } catch (Exception e) {

                    }
                }
            }
            if (handler != null && notificationCancellationRunnable != null) {
                handler.removeCallbacks(notificationCancellationRunnable);
            }

            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            notificationManager.cancel(100001);
            ChatSDK.mediaStop();
            IncomingCallActivity incomingCallActivity = (IncomingCallActivity) ChatSDK.callActivities.get("incomingCallActivity");
//            AppToAppCall appToAppCallActivity = (AppToAppCall) ChatSDK.callActivities.get("AppToAppCall");
//            AppToAppVideo appToAppVideoActivity = (AppToAppVideo) ChatSDK.callActivities.get("AppToAppVideo");
            ReceiverActivity receiverActivity = (ReceiverActivity) ChatSDK.callActivities.get("ReceiverActivity");
            if (incomingCallActivity != null) {
                incomingCallActivity.finishAndRemoveTask();
            }
//            if (appToAppCallActivity != null) {
//                appToAppCallActivity.finish();
//            }
//            if (appToAppVideoActivity != null) {
//                appToAppVideoActivity.finish();
//            }
            if (receiverActivity != null) {
                receiverActivity.finishAndRemoveTask();
            }

        }
//        else if (messageType.equals("received")) {
//            AppToAppCall appToAppCallActivity = (AppToAppCall) ChatSDK.callActivities.get("AppToAppCall");
//            AppToAppVideo appToAppVideoActivity = (AppToAppVideo) ChatSDK.callActivities.get("AppToAppVideo");
//            if (appToAppCallActivity != null) {
//                AppToAppCall.onReceived();
//            } else if (appToAppVideoActivity != null) {
//                AppToAppVideo.onReceived();
//            }
//        }
//        else if (messageType.equals("ringing")) {
//            AppToAppCall appToAppCallActivity = (AppToAppCall) ChatSDK.callActivities.get("AppToAppCall");
//            if (appToAppCallActivity != null) {
//                AppToAppCall.onRinging();
//            }
//        }
//        else if (messageType.equals("noResponse")) {
//            AppToAppCall appToAppCallActivity = (AppToAppCall) ChatSDK.callActivities.get("AppToAppCall");
//            if (appToAppCallActivity != null) {
//                appToAppCallActivity.finish();
//            }
//        }
        else {
            if (ChatSDK.shared().isValid() && !ChatSDK.config().manualPushHandlingEnabled) {
                for (BroadcastHandler handler : ChatSDK.shared().broadcastHandlers()) {
                    if (handler.onReceive(getApplicationContext(), remoteMessage.toIntent())) {
                        break;
                    }
                }
            }
        }
    }
    private void sendRingingMessageAsync(final String senderNumber) {
        // Create a single-threaded executor to run the task in the background
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                sendRingingMessage(senderNumber);
            }
        });
        // Shutdown the executor to prevent further tasks from being submitted
        executor.shutdown();
    }
    private void sendRingingMessage(String senderNumber) {
        String messageReceiverNumber = senderNumber;
        HashMap<String, Object> newMessage = new HashMap<>();
        String threadEntityID = messageReceiverNumber + "@localhost";
        String senderId = ChatSDK.auth().getCurrentUserEntityID();
        HashMap<String, HashMap<String, String>> userIds = new HashMap<String, HashMap<String, String>>();
        HashMap<String, String> users = new HashMap<String, String>();
        users.put(threadEntityID, messageReceiverNumber);
        userIds.put("userIds", users);
        String action = "co.chatsdk.QuickReply";
        String body = "App to App call";
        int callType = -3;
        users.put(ThreadId,senderId);
        newMessage.put(ThreadId, threadEntityID);
        newMessage.put(SenderName, messageReceiverNumber);
        newMessage.put(SenderId, senderId);
        newMessage.put(UserIds, users);
        newMessage.put(Action, action);
        newMessage.put(Body, body);
        newMessage.put(Type, callType);
        ChatSDK.push().sendPushNotification(newMessage);
    }

    @NonNull
    private static String getMessageType(RemoteMessage remoteMessage) {
        String messageType = "default";
        try {
            String pushType = remoteMessage.getData().get("chat_sdk_push_type");
            if (pushType != null) {
                switch (pushType) {
                    case "100":
                        messageType = "audio";
                        break;
                    case "101":
                        messageType = "video";
                        break;
                    case "-1":
                        messageType = "cancel";
                        break;
                    case "-2":
                        messageType = "received";
                        break;
                    case "-3":
                        messageType = "ringing";
                        break;
                    case "-4":
                        messageType = "noResponse";
                        break;
                }
            }
        } catch (Exception e) {
            Log.d("DefaultMessagingService", "Error processing remote message: " + e.getMessage());
        }
        return messageType;
    }
    public void scheduleNotificationCancellation(final int notificationId,String senderNumber ) {

        handler = new Handler(Looper.getMainLooper());
        notificationCancellationRunnable = () -> {
            ReceiverActivity receiverActivity = (ReceiverActivity) ChatSDK.callActivities.get("ReceiverActivity");
            if(receiverActivity==null) {
                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(ChatSDK.ctx());
                notificationManager.cancel(notificationId);
                ChatSDK.mediaStop();
                String messageReceiverNumber = senderNumber;
                HashMap<String, Object> newMessage = new HashMap<>();
                String threadEntityID = messageReceiverNumber + "@localhost";
                String senderId = ChatSDK.auth().getCurrentUserEntityID();

                HashMap<String, HashMap<String, String>> userIds = new HashMap<>();
                HashMap<String, String> users = new HashMap<>();
                users.put(threadEntityID, messageReceiverNumber);
                userIds.put("userIds", users);

                String action = "co.chatsdk.QuickReply";
                String body = "App to App call";
                int callType = -4;

                users.put(ThreadId, senderId);
                newMessage.put(ThreadId, threadEntityID);
                newMessage.put(SenderName, messageReceiverNumber);
                newMessage.put(SenderId, senderId);
                newMessage.put(UserIds, users);
                newMessage.put(Action, action);
                newMessage.put(Body, body);
                newMessage.put(Type, callType);
                ChatSDK.push().sendPushNotification(newMessage);
                callReceived = false;
            }
        };
        handler.postDelayed(notificationCancellationRunnable, 30000); // 30 seconds delay
    }

}
