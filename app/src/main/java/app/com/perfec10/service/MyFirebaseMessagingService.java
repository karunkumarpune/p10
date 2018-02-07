package app.com.perfec10.service;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;


import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import app.com.perfec10.activity.MainActivity;
import app.com.perfec10.app.Config;
import app.com.perfec10.util.NotificationUtils;
import app.com.perfec10.util.PreferenceManager;


/**
 * created by fluper on Dec 29, 2017
 */
public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();

    private NotificationUtils notificationUtils;
    private PreferenceManager preferenceManager;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.e(TAG, "From: " + remoteMessage.getFrom());
       // preferenceManager = new PreferenceManager(mainActivity);
        if (remoteMessage == null)
            return;

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.e(TAG, "Notification Body: " + remoteMessage.getNotification().getBody());
            handleNotification(remoteMessage.getNotification().getBody());
        }

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.e(TAG, "Data Payload: " + remoteMessage.getData().toString());

            try {
              //  JSONObject json = new JSONObject(remoteMessage.getData().toString());
                Map<String, String> data = remoteMessage.getData();
                handleDataMessage(data);
            } catch (Exception e) {
                Log.e(TAG, "Exception: " + e.getMessage());
            }
        }
    }

    private void handleNotification(String message) {
        if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
            // app is in foreground, broadcast the push message
            Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
            pushNotification.putExtra("message", message);
            LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

            // play notification sound
            NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
            notificationUtils.playNotificationSound();
        }else{
            // If the app is in background, firebase itself handles the notification
        }
    }

    private void handleDataMessage(Map<String, String> json) {
        Log.e(TAG, "push json: " + json.toString());
        preferenceManager = new PreferenceManager(getApplicationContext());
        try {
           // JSONObject data = json.getJSONObject("data");
            String notification_type = json.get("notification_type");
          //  String notification_type = json.getString("notification_type");
            String post_id = json.get("post_id");
            String user_id = json.get("user_id");
            String image = json.get("image");
            String message = json.get("message");
           // JSONObject payload = data.getJSONObject("payload");

            Log.e(TAG, "notification_type: " + notification_type); // 1 = like 2 = comm 0 = share
            Log.e(TAG, "post_id: " + post_id);
            Log.e(TAG, "user_id: " + user_id);
          //  Log.e(TAG, "payload: " + payload.toString());
            Log.e(TAG, "message: " + message);
            Log.e(TAG, "image: " + image);

            if (notification_type.equals("0")){
                if (preferenceManager.getKey_notifiableother().equals("1")){
                    if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
                        // app is in foreground, broadcast the push message
                        Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
                        pushNotification.putExtra("message", message);
                        LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

                        // play notification sound
                        NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
                        notificationUtils.playNotificationSound();

                    } else {
                        // app is in background, show the notification in notification tray

                        Intent resultIntent = new Intent(getApplicationContext(), MainActivity.class);
                        resultIntent.putExtra("message", message);

                        // check for image attachment
                        if (TextUtils.isEmpty(image)) {
                            Log.d("app if", "in back ground ");
                            showNotificationMessage(getApplicationContext(), message, message, post_id, resultIntent);
                        } else {
                            Log.d("app else", "in back ground ");
                            // image is present, show notification with image
                            showNotificationMessage(getApplicationContext(), message, message, post_id, resultIntent);
                            //showNotificationMessageWithBigImage(getApplicationContext(), message, message, post_id, resultIntent, image);
                        }
                    }
                }else {
                    Log.d(TAG, "notification is off from settings ");
                }
            }else if (notification_type.equals("1") || notification_type.equals("2")){
                if (preferenceManager.getKey_notifiablePersonal().equals("1")){
                    if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
                        // app is in foreground, broadcast the push message
                        Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
                        pushNotification.putExtra("message", message);
                        LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

                        // play notification sound
                        NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
                        notificationUtils.playNotificationSound();

                    } else {
                        // app is in background, show the notification in notification tray

                        Intent resultIntent = new Intent(getApplicationContext(), MainActivity.class);
                        resultIntent.putExtra("message", message);

                        // check for image attachment
                        if (TextUtils.isEmpty(image)) {
                            Log.d("app if", "in back ground ");
                            showNotificationMessage(getApplicationContext(), message, message, post_id, resultIntent);
                        } else {
                            Log.d("app else", "in back ground ");
                            // image is present, show notification with image
                            showNotificationMessage(getApplicationContext(), message, message, post_id, resultIntent);
                            //showNotificationMessageWithBigImage(getApplicationContext(), message, message, post_id, resultIntent, image);
                        }
                    }
                }else {
                    Log.d(TAG, "notification is off from settings ");
                }
            }

        }  catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
    }

    /**
     * Showing notification with text only
     */
    private void showNotificationMessage(Context context, String title, String message, String timeStamp, Intent intent) {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent);
    }

    /**
     * Showing notification with text and image
     */
    private void showNotificationMessageWithBigImage(Context context, String title, String message, String timeStamp, Intent intent, String imageUrl) {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent, imageUrl);
    }
}
