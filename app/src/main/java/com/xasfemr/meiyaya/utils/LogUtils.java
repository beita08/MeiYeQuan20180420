package com.xasfemr.meiyaya.utils;

import com.xasfemr.meiyaya.BuildConfig;
import android.util.Log;

/**
 * Created by Administrator on 2017/11/17.
 */

public class LogUtils {

    public static void show(String TAG, String msg) {


        if (BuildConfig.MieYeQuan_LIVE){
            return;
        }
        show(TAG, msg, Log.INFO);
    }


    /**
     * 显示LOG
     */
    public static void show(String TAG, String msg, int level) {
        if (BuildConfig.MieYeQuan_LIVE){
            return;
        }
        switch (level) {
            case Log.VERBOSE:
                Log.v(TAG, msg);
                break;
            case Log.DEBUG:
                Log.d(TAG, msg);
                break;
            case Log.INFO:
                Log.i(TAG, msg);
                break;
            case Log.WARN:
                Log.w(TAG, msg);
                break;
            case Log.ERROR:
                Log.e(TAG, msg);
                break;
            default:
                Log.i(TAG, msg);
                break;
        }
    }


}
