package com.example.myapplication;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btnStartService).setOnClickListener(view -> {
            new log("START THE FOREGROUND SERVICE ON DEMAND");
            actionOnService(Actions.START);
        });

        findViewById(R.id.btnStopService).setOnClickListener(view -> {
            new log("STOP THE FOREGROUND SERVICE ON DEMAND");
            actionOnService(Actions.STOP);
        });
    }

    private void actionOnService(Actions action) {
        Intent intent = new Intent(this, EndlessService.class);
        intent.setAction(action.name());
        if (new ServiceTracker().getServiceState(this) == ServiceState.STOPPED && action == Actions.STOP)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                new log("Starting the service in >=26 Mode");
                startForegroundService(intent);
                return;
            }
        new log("Starting the service in < 26 Mode");
        startService(intent);
    }
}

