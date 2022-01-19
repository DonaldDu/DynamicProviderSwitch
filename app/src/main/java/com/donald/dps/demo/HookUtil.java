package com.donald.dps.demo;


import android.content.Context;
import android.content.pm.ProviderInfo;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;


/**
 * Created by zhuguohui
 * Date: 2021/9/13
 * Time: 11:23
 * Desc:
 */
public class HookUtil {
    private static List<ProviderInfo> providers;
    private static Method installContentProvidersMethod;
    private static Object currentActivityThread;

    /*
     *用户同意后调用
     */
    public static void initProvider(Context context) {
        try {
            installContentProvidersMethod.invoke(currentActivityThread, context, providers);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void attachContext() throws Exception {
        // 先获取到当前的ActivityThread对象
        Class<?> activityThreadClass = Class.forName("android.app.ActivityThread");
        Method currentActivityThreadMethod = activityThreadClass.getDeclaredMethod("currentActivityThread");
        currentActivityThreadMethod.setAccessible(true);
        //currentActivityThread是一个static函数所以可以直接invoke，不需要带实例参数
        currentActivityThread = currentActivityThreadMethod.invoke(null);

        hookInstallContentProvider(activityThreadClass);
    }

    private static void hookInstallContentProvider(Class activityThreadClass) throws Exception {
        Field appDataField = activityThreadClass.getDeclaredField("mBoundApplication");
        appDataField.setAccessible(true);
        Object appData = appDataField.get(currentActivityThread);
        Field providersField = appData.getClass().getDeclaredField("providers");
        providersField.setAccessible(true);
        providers = (List<ProviderInfo>) providersField.get(appData);
        //清空provider，避免有些sdk通过provider来初始化
        providersField.set(appData, null);

        installContentProvidersMethod = activityThreadClass.getDeclaredMethod("installContentProviders", Context.class, List.class);
        installContentProvidersMethod.setAccessible(true);
    }
}


