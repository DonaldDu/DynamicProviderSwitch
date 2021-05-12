package com.donald.dps.lib;

import android.annotation.SuppressLint;
import android.app.Instrumentation;
import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;


public class InstrumentationDelegate extends Instrumentation {
    private static final String TAG = "InstrumentationDelegate";

    @Override
    public boolean onException(Object obj, Throwable e) {
        Log.e(TAG, "onException currentThread:" + Thread.currentThread().getId());
        Log.e(TAG, e.getMessage());
        return e instanceof ClassNotFoundException;
    }

    public static void install() {
        Log.e(TAG, "currentThread:" + Thread.currentThread().getId());
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
            Instrumentation newInstrumentation = new InstrumentationDelegate();

            Field[] fields = Instrumentation.class.getDeclaredFields();
            for (Field field : fields) {
                if (!Modifier.isStatic(field.getModifiers())) {
                    field.setAccessible(true);
                    field.set(newInstrumentation, field.get(oldInstrumentation));
                }
            }
            mInstrumentationF.set(currentActivityThread, newInstrumentation);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }
}
