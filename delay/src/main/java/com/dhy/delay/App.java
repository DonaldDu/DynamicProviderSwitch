package com.dhy.delay;

import android.app.Application;
import android.content.Context;
import android.os.Build;

import com.donald.dps.lib.DelayProviderHelper;

public class App extends Application {
    @Override
    protected void attachBaseContext(Context base) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
//            DelayProviderHelper.delayProvider(false);
            DelayProviderHelper.cancelDelayProvider(MyDerectContentProvider.class.getName());
        }
        super.attachBaseContext(base);
    }
}
