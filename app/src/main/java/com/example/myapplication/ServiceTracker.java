package com.example.myapplication;

import android.content.Context;
import android.content.SharedPreferences;

enum ServiceState {
    STARTED,
    STOPPED,
}

class ServiceTracker{
    private String key = "SPYSERVICE_STATE";

    void setServiceState(Context context, ServiceState state) {
        SharedPreferences settings = getPreferences(context);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(key, state.name());
        editor.apply();
    }

    ServiceState getServiceState(Context context) {
        SharedPreferences settings = getPreferences(context);
        return ServiceState.valueOf(settings.getString(key, ServiceState.STOPPED.name()));
    }

    private SharedPreferences getPreferences(Context context) {
        String name = "SPYSERVICE_KEY";
        return context.getSharedPreferences(name, 0);
    }
}