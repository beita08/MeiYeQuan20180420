
package com.xasfemr.meiyaya.view.swipbackhelper;

import android.app.Activity;

import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * Created by Chaojun Wang on 6/9/14.
 */
public class SwipeUtils {
    private SwipeUtils() {
    }

    /**
     * Convert a translucent themed Activity
     * {@link android.R.attr#windowIsTranslucent} to a fullscreen opaque
     * Activity.
     * <p>
     * Call this whenever the background of a translucent Activity has changed
     * to become opaque. Doing so will allow the {@link android.view.Surface} of
     * the Activity behind to be released.
     * <p>
     * This call has no effect on non-translucent activities or on activities
     * with the {@link android.R.attr#windowIsFloating} attribute.
     */
    public static void convertActivityFromTranslucent(Activity activity) {
        try {
            Method method = Activity.class.getDeclaredMethod("convertFromTranslucent");
            method.setAccessible(true);
            method.invoke(activity);
        } catch (Throwable t) {
        }
    }

    /**
     * Convert a translucent themed Activity
     * {@link android.R.attr#windowIsTranslucent} back from opaque to
     * translucent following a call to
     * {@link #convertActivityFromTranslucent(Activity)} .
     * <p>
     * Calling this allows the Activity behind this one to be seen again. Once
     * all such Activities have been redrawn
     * <p>
     * This call has no effect on non-translucent activities or on activities
     * with the {@link android.R.attr#windowIsFloating} attribute.
     */
    public static void convertActivityToTranslucent(Activity activity,PageTranslucentListener listener) {
        try {
            Class<?>[] classes = Activity.class.getDeclaredClasses();
            Class<?> translucentConversionListenerClazz = null;
            for (Class clazz : classes) {
                if (clazz.getSimpleName().contains("TranslucentConversionListener")) {
                    translucentConversionListenerClazz = clazz;
                }
            }
            Method method = Activity.class.getDeclaredMethod("convertToTranslucent",
                    translucentConversionListenerClazz);
            method.setAccessible(true);
            method.invoke(activity, new Object[] {
                null
            });
        } catch (Throwable t) {
        }

//        try {
//            Method getActivityOptions = Activity.class.getDeclaredMethod("getActivityOptions");
//            getActivityOptions.setAccessible(true);
//            Object options = getActivityOptions.invoke(activity);
//
//            Class<?>[] classes = Activity.class.getDeclaredClasses();
//            Class<?> translucentConversionListenerClazz = null;
//            for (Class clazz : classes) {
//                if (clazz.getSimpleName().contains("TranslucentConversionListener")) {
//                    translucentConversionListenerClazz = clazz;
//                }
//            }
//
//
//            MyInvocationHandler myInvocationHandler = new MyInvocationHandler(new WeakReference<PageTranslucentListener>(listener));
//            Object obj = Proxy.newProxyInstance(Activity.class.getClassLoader(), new Class[]{translucentConversionListenerClazz}, myInvocationHandler);
//
//            Method convertToTranslucent = Activity.class.getDeclaredMethod("convertToTranslucent",
//                    translucentConversionListenerClazz, ActivityOptions.class);
//            convertToTranslucent.setAccessible(true);
//            convertToTranslucent.invoke(activity, obj, options);
//        } catch (Throwable t) {
//        }

    }





    /**
     * 通过动态代理的方式
     * 等待convertToTranslucent成功的回调，然后再触发Activity的侧滑。
     * 2017-2-20
     */
    static class MyInvocationHandler implements InvocationHandler{

        private WeakReference<PageTranslucentListener> listener;

        public MyInvocationHandler(WeakReference<PageTranslucentListener> listener) {
            this.listener = listener;
        }

        @Override
        public Object invoke(Object o, Method method, Object[] args) throws Throwable {
            try {
                boolean success = (boolean) args[0];
                if (success && listener.get() != null) {
                    listener.get().onPageTranslucent();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }


    public interface PageTranslucentListener {
        void onPageTranslucent();
    }
}
