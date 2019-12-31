package com.example.myapplication;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.widget.Toast;

public class EndlessService extends Service {

    /**
     * Initialize PowerManager.WakeLock
     * So that our service will not be impacted by Doze Mode.
     */
    private PowerManager.WakeLock wakeLock = null;
    /**
     * Boolean if our service is started or not.
     */
    private boolean isServiceStarted = false;

    /**
     * Override onBind method.
     *
     * @param intent : Intent.
     * @return always null.
     */
    @Override
    public IBinder onBind(Intent intent) {
        new log("Some component want to bind with the service");
        return null;
    }

    /**
     * Override onStartCommand method.
     *
     * @param intent  : Intent.
     * @param flags   : Flags.
     * @param startId : startId.
     * @return START_STICKY.
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new log("onStartCommand executed with startId: " + startId);
        if (intent != null) {
            String action = intent.getAction();
            new log("using an intent with action " + action);
            if (action != null) {
                if (action.equals(Actions.START.name())) startService();
                else if (action.equals(Actions.STOP.name())) stopService();
                else new log("This should never happen. No action in the received intent");
            }
        } else {
            new log("with a null intent. It has been probably restarted by the system.");
        }
        // by returning this we make sure the service is restarted if the system kills the service
        return START_STICKY;
    }

    /**
     * Override onCreate method.
     * Create the service in foreground.
     */
    @Override
    public void onCreate() {
        super.onCreate();
        new log("The service has been created".toUpperCase());
        startForeground(1, createNotification());
    }

    /**
     * Override onDestroy method.
     * Destroy the running service.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        new log("The service has been destroyed".toUpperCase());
        Toast.makeText(this, "Service destroyed", Toast.LENGTH_SHORT).show();
    }

    /**
     * Method executed when the service is running.
     */
    private void startService() {
        // If the service already running, do nothing.
        if (isServiceStarted) return;
        new log("Starting the foreground service task");
        Toast.makeText(this, "Service starting its task", Toast.LENGTH_SHORT).show();
        isServiceStarted = true;
        new ServiceTracker().setServiceState(this, com.example.myapplication.ServiceState.STARTED);

        // we need this lock so our service gets not affected by Doze Mode
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        if (pm != null) {
            wakeLock = pm.newWakeLock(1, "EndlessService::lock");
            wakeLock.acquire(60 * 1000L /*1 minutes*/);
        }

        // Create a thread and loop while the service is running.
        Thread thread = new Thread(() -> {
            while (isServiceStarted) {
                try {
                    Thread.sleep(2000);
                    pingFakeServer();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        // Start thread.
        thread.start();
    }

    /**
     * Method executed to stop the running service.
     */
    private void stopService() {
        new log("Stopping the foreground service");
        Toast.makeText(this, "Service stopping", Toast.LENGTH_SHORT).show();
        try {
            if (wakeLock.isHeld()) {
                wakeLock.release();
            }
            stopForeground(true);
            stopSelf();
        } catch (Exception e) {
            new log("Service stopped without being started: ${e.message}");
        }
        isServiceStarted = false;
        new ServiceTracker().setServiceState(this, com.example.myapplication.ServiceState.STOPPED);
    }

    /**
     * Method executed while the service is running.
     */
    private void pingFakeServer() {
        new log("Ping Fake Server");
    }

    /**
     * Method to create the notification show to the user.
     *
     * @return Notification with all params.
     */
    private Notification createNotification() {
        String notificationChannelId = "ENDLESS SERVICE CHANNEL";

        // depending on the Android API that we're dealing with we will have
        // to use a specific method to create the notification
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationChannel channel = new NotificationChannel(
                    notificationChannelId,
                    "Endless Service notifications channel",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Endless Service channel");
            channel.enableLights(true);
            channel.setLightColor(Color.RED);
            channel.enableVibration(true);
            channel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }

        Notification.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder = new Notification.Builder(
                    this,
                    notificationChannelId
            );
        } else {
            builder = new Notification.Builder(this);
        }

        return builder
                .setContentTitle("Endless Service")
                .setContentText("This is your favorite endless service working")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setTicker("Ticker text")
                .setPriority(Notification.PRIORITY_HIGH) // for under android 26 compatibility
                .build();
    }
}
