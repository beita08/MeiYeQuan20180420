package com.xasfemr.meiyaya.module.player;

/**
 * Created by Administrator on 2017/11/27.
 */


import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import android.content.Context;
import android.os.Build;
import android.view.Window;

public class PolicyCompat {
    /*
     * Private constants
     */
    private static final String PHONE_WINDOW_CLASS_NAME   = "com.android.internal.policy.PhoneWindow";
    private static final String POLICY_MANAGER_CLASS_NAME = "com.android.internal.policy.PolicyManager";
    private static Context context;


    private PolicyCompat() {
    }


    /*
     * Private methods
     */
    private static Window createPhoneWindow(Context context) {
        try {
            /* Find class */
            Class<?> cls = Class.forName(PHONE_WINDOW_CLASS_NAME);

            /* Get constructor */
            Constructor c = cls.getConstructor(Context.class);

            /* Create instance */
            return (Window)c.newInstance(context);
        }
        catch (ClassNotFoundException e) {
            throw new RuntimeException(PHONE_WINDOW_CLASS_NAME + " could not be loaded", e);
        }
        catch (Exception e) {
            throw new RuntimeException(PHONE_WINDOW_CLASS_NAME + " class could not be instantiated", e);
        }
    }

    private static Window makeNewWindow(Context context) {
        try {
            /* Find class */
            Class<?> cls = Class.forName(POLICY_MANAGER_CLASS_NAME);

            /* Find method */
            Method m = cls.getMethod("makeNewWindow", Context.class);

            /* Invoke method */
            return (Window)m.invoke(null, context);
        }
        catch (ClassNotFoundException e) {
            throw new RuntimeException(POLICY_MANAGER_CLASS_NAME + " could not be loaded", e);
        }
        catch (Exception e) {
            throw new RuntimeException(POLICY_MANAGER_CLASS_NAME + ".makeNewWindow could not be invoked", e);
        }
    }

    /*
     * Public methods
     * 6.0 以上通过反射拿对象的方法和6.0以下不一样
     */
    public static Window createWindow(Context context) {

        if (Build.VERSION.SDK_INT>=23){
            return createPhoneWindow(context);
        }else {
            return makeNewWindow(context);
        }

    }
//        if (false)
//            return createPhoneWindow(context);
//        else
//            return makeNewWindow(context);
//    }
}
