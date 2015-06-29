package com.haru.example.memo;

import android.app.Application;

import com.haru.Haru;

public class App extends Application {

    private static String APP_KEY = "934b90c0-20e5-40f4-94e7-31c05840ec83";
    private static String SDK_KEY = "SDK_KEY_HERE";

    @Override
    public void onCreate() {
        super.onCreate();
        Haru.init(this, APP_KEY, SDK_KEY);
    }
}
