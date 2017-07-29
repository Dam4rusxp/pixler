package de.damarus.pixler;

import android.app.Application;

public class PixlerApplication extends Application {

    private static PixlerApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;
    }

    public static PixlerApplication getContext() {
        return instance;
    }
}
