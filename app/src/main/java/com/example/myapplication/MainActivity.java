package com.example.myapplication;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    /**
     * Override onCreate method.
     *
     * @param savedInstanceState : savedInstanceState.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // When user click on Start button
        findViewById(R.id.btnStartService).setOnClickListener(view -> {
            new log("START THE FOREGROUND SERVICE ON DEMAND");
            actionOnService(Actions.START);
        });

        // When user click on Stop button
        findViewById(R.id.btnStopService).setOnClickListener(view -> {
            new log("STOP THE FOREGROUND SERVICE ON DEMAND");
            actionOnService(Actions.STOP);
        });
    }

    /**
     * Start service if the service is not running.
     *
     * @param action : Enum of Action.
     */
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

