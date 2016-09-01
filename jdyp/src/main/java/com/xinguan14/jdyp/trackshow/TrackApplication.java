package com.xinguan14.jdyp.trackshow;

import android.app.Application;
import android.content.Context;
import android.os.Looper;
import android.widget.Toast;

public class TrackApplication extends Application {

    private static Context context;

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();

        context = getApplicationContext();
    }

    public static Context getContext() {
        return context;
    }

    public static void showMessage(String message) {
        Looper.prepare();
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
        Looper.loop();
    }
}
