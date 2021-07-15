package com.donald.dps.lib;

import android.annotation.SuppressLint;
import android.app.Instrumentation;
import android.os.Build;
import android.util.Log;

import org.chickenhook.restrictionbypass.Unseal;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;


public class InstrumentationDelegate extends Instrumentation {
    private static final String TAG = "InstrumentationDelegate";

    public InstrumentationDelegate() {
        try {
            if (Build.VERSION.SDK_INT >= 28) Unseal.unseal();
            install();
        } catch (Exception e) {
            installResult(false);
            Log.e(TAG, "Unable to unseal hidden api access", e);
        }
    }

    @Override
    public boolean onException(Object obj, Throwable e) {
        Log.e(TAG, "Instrumentation.onException -> " + e.getMessage());
        return e instanceof ClassNotFoundException;
    }

    private void install() {
        try {
            @SuppressLint("PrivateApi")
            Class<?> cls = Class.forName("android.app.ActivityThread");
            @SuppressLint("DiscouragedPrivateApi")
            Method method = cls.getDeclaredMethod("currentActivityThread");
            Object currentActivityThread = method.invoke(null);
            //noinspection ConstantConditions
            Field mInstrumentationF = currentActivityThread.getClass().getDeclaredField("mInstrumentation");
            mInstrumentationF.setAccessible(true);
            Instrumentation oldInstrumentation = (Instrumentation) mInstrumentationF.get(currentActivityThread);
            if (oldInstrumentation == null || !oldInstrumentation.getClass().getName().equals("android.app.Instrumentation")) {
                Log.e(TAG, "copy Instrumentation canceled");
                installResult(false);
                return;
            }
            Instrumentation newInstrumentation = this;

            Field[] fields = Instrumentation.class.getDeclaredFields();
            int count = 0;
            for (Field field : fields) {
                if (!Modifier.isStatic(field.getModifiers())) {
                    count++;
                    field.setAccessible(true);
                    field.set(newInstrumentation, field.get(oldInstrumentation));
                }
            }
            if (count > 0) {
                mInstrumentationF.set(currentActivityThread, newInstrumentation);
                Log.i(TAG, "copy Instrumentation fieldCount=" + count);
                installResult(true);
            } else {
                Log.e(TAG, "copy Instrumentation failed");
                installResult(false);
            }
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    /**
     * report install result for user app
     */
    protected void installResult(boolean installed) {

    }
}
