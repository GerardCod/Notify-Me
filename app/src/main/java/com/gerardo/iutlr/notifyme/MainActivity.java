package com.gerardo.iutlr.notifyme;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    private static final String PRIMARY_CHANNEL_ID = "primary_notification_channel";
    private static final String ACTION_UPDATE_NOTIFICATION = "com.example.android.notifyme.ACTION_UPDATE_NOTIFICATION";
    private static final int NOTIFICATION_ID = 0;
    private NotificationReceiver mReceiver = new NotificationReceiver();

    private Button notify;
    private Button buttonUpdate;
    private Button buttonCancel;
    private NotificationManager mNotifyManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        notify = findViewById(R.id.notify);
        notify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendNotification();
            }
        });

        buttonUpdate = findViewById(R.id.update);
        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateNotification();
            }
        });

        buttonCancel = findViewById(R.id.cancel);
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelNotification();
            }
        });

        createNotificationChannel();
        registerReceiver(mReceiver, new IntentFilter(ACTION_UPDATE_NOTIFICATION));
        setNotificationButtonStates(true, false, false);
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(mReceiver);
        super.onDestroy();
    }

    public void sendNotification() {
        Intent updateIntent = new Intent(ACTION_UPDATE_NOTIFICATION);
        PendingIntent updatePendingIntent = PendingIntent.getBroadcast(this, NOTIFICATION_ID, updateIntent, PendingIntent.FLAG_ONE_SHOT);
        NotificationCompat.Builder notifyBuilder = getNotificationBuilder();
        notifyBuilder.addAction(R.drawable.ic_update, "Update Notification", updatePendingIntent);
        mNotifyManager.notify(NOTIFICATION_ID, notifyBuilder.build());
        setNotificationButtonStates(false, true, true);
    }

    public void updateNotification() {
        Bitmap androidImage = BitmapFactory.decodeResource(getResources(), R.drawable.mascot_1);
        NotificationCompat.Builder notifyBuilder = getNotificationBuilder();
        notifyBuilder.setStyle(new NotificationCompat.BigPictureStyle()
        .bigPicture(androidImage)
        .setBigContentTitle("Notification Updated!"));
        mNotifyManager.notify(NOTIFICATION_ID, notifyBuilder.build());
        setNotificationButtonStates(false, false, true);
    }

    public void cancelNotification() {
        mNotifyManager.cancel(NOTIFICATION_ID);
        setNotificationButtonStates(true, false, false);
    }

    public void createNotificationChannel() {
        mNotifyManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(PRIMARY_CHANNEL_ID, "Mascot Notification", NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setDescription("Notification from Mascot");
            mNotifyManager.createNotificationChannel(notificationChannel);
        }
    }

    private NotificationCompat.Builder getNotificationBuilder() {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingNotificationIntent = PendingIntent.getActivity(this, NOTIFICATION_ID, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        return new NotificationCompat.Builder(this, PRIMARY_CHANNEL_ID)
                .setContentTitle("You've been notified!")
                .setContentText("This is your notification text.")
                .setSmallIcon(R.drawable.ic_android)
                .setContentIntent(pendingNotificationIntent)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_ALL);
    }

    private void setNotificationButtonStates(boolean isNotifyEnabled, boolean isUpdateEnabled, boolean isCancelEnabled) {
        notify.setEnabled(isNotifyEnabled);
        buttonUpdate.setEnabled(isUpdateEnabled);
        buttonCancel.setEnabled(isCancelEnabled);
    }

    public class NotificationReceiver extends BroadcastReceiver {

        public NotificationReceiver() {}

        @Override
        public void onReceive(Context context, Intent intent) {
            updateNotification();
        }
    }
}
