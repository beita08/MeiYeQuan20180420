package com.xasfemr.meiyaya.utils;

import android.content.Context;

/**
 * Description:
 * Company    : shifeier
 * Author     : liuxb
 * Date       : 2017/9/27 0027 9:10
 */

public class UiUtils {

    //dp尺寸转为px
    public static int dp2px(Context context, int dp) {
        float density = context.getResources().getDisplayMetrics().density;
        return (int) (dp * density + 0.5f);
    }//dp尺寸转为px

    public static int dp2px(Context context, double dp) {
        float density = context.getResources().getDisplayMetrics().density;
        return (int) (dp * density + 0.5f);
    }

    //px转为dp尺寸
    public static int px2dp(Context context, int px) {
        float density = context.getResources().getDisplayMetrics().density;
        return (int) (px / density + 0.5f);
    }
}
