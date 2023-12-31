package com.buzzware.monymarket.classes;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.buzzware.monymarket.R;
import com.buzzware.monymarket.activities.SplashActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;


import java.util.Map;


/**
 * Created by prabh on 25-10-2017.
 */

public class
MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "FCM Service";

    private Bitmap bitmap;

    private Context context = this;

    public static final String FIREBASE_TOKEN = "token";

    @Override
    public void onNewToken(@NonNull String refreshedToken) {
        super.onNewToken(refreshedToken);
//        SharedPrefUtil.getInstance(this).put(FIREBASE_TOKEN, refreshedToken);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Map<String, String> map = remoteMessage.getData();


        if (remoteMessage.getNotification().getTitle() != null && remoteMessage.getNotification().getBody() != null) {

            sendUserNotification(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody());

        }

    }


    private void sendUserNotification(String title, String mess) {

        int notifyID = 1;

        Intent intent = null;

        NotificationChannel mChannel;

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        intent = new Intent(context, SplashActivity.class);


        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        String CHANNEL_ID = context.getPackageName();// The id of the channel.
        CharSequence name = "Sample one";// The user-visible name of the channel.
        int importance = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            importance = NotificationManager.IMPORTANCE_HIGH;
        }
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, CHANNEL_ID);
        notificationBuilder.setContentTitle(title);
        notificationBuilder.setAutoCancel(true);
        notificationBuilder.setSmallIcon(R.mipmap.ic_launcher);
        notificationBuilder.setPriority(Notification.PRIORITY_HIGH);
        notificationBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
        notificationBuilder.setContentIntent(pendingIntent);
        notificationBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(mess));
        notificationBuilder.setContentText(mess);
        notificationBuilder.setDefaults(Notification.DEFAULT_ALL);
        notificationBuilder.setSmallIcon(getNotificationIcon(notificationBuilder));

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            notificationManager.createNotificationChannel(mChannel);
        }

        if (notificationManager != null) {
            notificationManager.notify(notifyID /* ID of notification */, notificationBuilder.build());
        }


    }

    private int getNotificationIcon(NotificationCompat.Builder notificationBuilder) {

        return R.mipmap.ic_launcher;

    }

}