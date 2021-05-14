package com.donald.dps.lib;

import android.annotation.SuppressLint;
import android.app.Instrumentation;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import me.weishu.reflection.Reflection;


public class InstrumentationDelegate extends Instrumentation {
    private static final String TAG = "InstrumentationDelegate";

    public InstrumentationDelegate(@NonNull Context context) {
        Reflection.unseal(context);
        install();
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
            Instrumentation newInstrumentation = this;

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
