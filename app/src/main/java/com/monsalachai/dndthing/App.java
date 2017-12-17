package com.monsalachai.dndthing;

import android.app.Application;
import android.content.Context;

/**
 * Created by mesalu on 12/17/17.
 * see: https://stackoverflow.com/questions/4391720/how-can-i-get-a-resource-content-from-a-static-context/4391811#4391811
 */

public class App extends Application {
    private static Context __context;

    @Override
    public void onCreate()
    {
        super.onCreate();
        __context = this;
    }

    public static Context getGlobalContext() { return __context; }
}
