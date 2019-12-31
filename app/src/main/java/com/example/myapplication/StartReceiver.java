package com.example.myapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import java.util.Objects;

public class StartReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Objects.equals(intent.getAction(), Intent.ACTION_BOOT_COMPLETED) && new ServiceTracker().getServiceState(context) == ServiceState.STARTED) {
            intent = new Intent(context, EndlessService.class);
            intent.setAction(Actions.START.name());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                new log("Starting the service in >=26 Mode");
                context.startForegroundService(intent);
                return;
            }
            new log("Starting the service in < 26 Mode");
            context.startService(intent);
        }
    }
}
