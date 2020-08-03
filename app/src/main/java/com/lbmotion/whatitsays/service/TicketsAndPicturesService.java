package com.lbmotion.whatitsays.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.lbmotion.whatitsays.R;
import com.lbmotion.whatitsays.managers.SendTicketsAndPictures;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import static android.app.NotificationManager.IMPORTANCE_MIN;

/**
 * Created by witman on 06/03/2018.
 */

public class TicketsAndPicturesService extends Service {

    private static final int FOREGROUND_ID = 1338;
    private static final int NOTIFICATION_ID = 1339;
    private static final int NOTIFICATION_ID_STOP = 1340;

    public class TicketsAndPicturesBinder extends Binder {
        public void setLoadOnlyTickets(boolean v) {
            if(sendTicketsAndPictures != null)
                sendTicketsAndPictures.hasToLoadOnlyTickets.set(v);
        }
    }

    public SendTicketsAndPictures sendTicketsAndPictures = null;

    private TicketsAndPicturesBinder mBinder = new TicketsAndPicturesBinder();

    @NonNull
    public static Intent getIntent(Context context) {
        return new Intent(context, TicketsAndPicturesService.class);
    }

    public void doStartForeground() {
        startForeground(FOREGROUND_ID, buildForegroundNotification());
    }

    public void doStopForeground() {
        stopForeground(true);
        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
        stopSelf();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        kickOffTheServiceWork();
//      return super.onStartCommand(intent, flags, startId);
        doStartForeground();
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        kickOffTheServiceWork();
        doStartForeground();
        return mBinder;
    }

    // In case the service is deleted or crashes some how
    @Override
    public void onDestroy() {
        sendTicketsAndPictures = null;
        super.onDestroy();
    }

    /**
     * Kick off the service's work.
     */
    private void kickOffTheServiceWork() {
        if(sendTicketsAndPictures == null)
            sendTicketsAndPictures = new SendTicketsAndPictures(this);
    }

    private Notification buildForegroundNotification() {
        try {
            Thread.sleep(0);} catch (InterruptedException ie) {}
        String msg = getString(R.string.check_picture_text);
        msg = msg.replace("X",SendTicketsAndPictures.getNumberOfTicketsWaiting()+"");
        msg = msg.replace("Y",SendTicketsAndPictures.getNumberOfPicturesWaiting()+"");
        SimpleDateFormat curFormater = new SimpleDateFormat("dd/MM/yyyy kk:mm:ss");
        curFormater.setTimeZone(TimeZone.getDefault());
        int datestyle = DateFormat.SHORT;
        int timestyle = DateFormat.MEDIUM;
        DateFormat df = DateFormat.getDateTimeInstance(datestyle, timestyle, Locale.getDefault());
        Intent notificationIntent = new Intent();
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        notificationIntent.setClass(this, CheckTicketsAndPicturesService.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, NOTIFICATION_ID, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);
//
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            String id = "pictures";
            CharSequence name = "Hanita";                             // The user-visible name of the channel.
            String description = "Hanit loading pictures";            // The user-visible description of the channel.
            NotificationChannel mChannel = new NotificationChannel(id, name, IMPORTANCE_MIN);
// Configure the notification channel.
            mChannel.setDescription(description);
            mChannel.enableLights(true);
// Sets the notification light color for notifications posted to this channel, if the device supports this feature.
            mChannel.setLightColor(Color.RED);
            notificationManager.createNotificationChannel(mChannel);
            Notification.Builder b = new Notification.Builder(this, mChannel.getId());
//
            Intent notificationCancelIntent = new Intent();
            notificationCancelIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            notificationCancelIntent.setClass(this, CancelTicketsAndPicturesService.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, NOTIFICATION_ID_STOP, notificationCancelIntent, PendingIntent.FLAG_CANCEL_CURRENT);
            Notification.Action stopAction = new Notification.Action.Builder(android.R.drawable.stat_sys_download_done, getString(R.string.app_name), pendingIntent).build();
//
            b.setOngoing(true)
                    .setContentTitle(getString(R.string.downloading))
                    .setContentText(msg+" "+df.format(new Date()))//getString(R.string.communication)+" "+
                    .setSmallIcon(android.R.drawable.stat_sys_download)
                    .setTicker(getString(R.string.downloading))
                    .setContentIntent(contentIntent)
                    .setActions(stopAction);
            return(b.build());
        }
        else {
            Intent notificationCancelIntent = new Intent();
            notificationCancelIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            notificationCancelIntent.setClass(this, CancelTicketsAndPicturesService.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, NOTIFICATION_ID_STOP, notificationCancelIntent, PendingIntent.FLAG_CANCEL_CURRENT);
            NotificationCompat.Action stopAction = new NotificationCompat.Action(android.R.drawable.stat_sys_download_done, getString(R.string.app_name), pendingIntent);
//
            NotificationCompat.Builder b = new NotificationCompat.Builder(this);
            b.setOngoing(true)
                    .setContentTitle(getString(R.string.downloading))
                    .setContentText(msg+" "+df.format(new Date()))//getString(R.string.communication)+" "+
                    .setSmallIcon(android.R.drawable.stat_sys_download)
                    .setTicker(getString(R.string.downloading))
                    .setContentIntent(contentIntent)
                    .addAction(stopAction);
            return(b.build());
        }
    }
}
