package com.xasfemr.meiyaya.utils;

import android.content.Context;
import android.os.Message;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.xasfemr.meiyaya.activity.UserPagerActivity;

/**
 * Description:
 * Company    : shifeier
 * Author     : liuxb
 * Date       : 2018/3/7 0007 10:04
 */

public class UserPagerBehavior extends CoordinatorLayout.Behavior<LinearLayout> {

    Context mContext;

    public UserPagerBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
        DisplayMetrics display = context.getResources().getDisplayMetrics();
        mContext = context;
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, LinearLayout child, View dependency) {

        //Toolbar+状态栏的高度
        float maxL = DensityUtil.dip2px(mContext, 117) + DensityUtil.getZhuangtai(mContext);

        Log.d("Behavior", "Y轴滑动距离 = " + dependency.getY() + ", 总共 = " + DensityUtil.dip2px(mContext, 310) + ", toolsbar = " + maxL);

        Message message = new Message();

        if (dependency.getY() <= 0) {

            float a = (Math.abs(dependency.getY()) / (DensityUtil.dip2px(mContext, 310) - maxL));

            Log.d("Behavior", "a = " + a);
            message.what = (int) (a * 100f);

            if (UserPagerActivity.mHandler != null)
                UserPagerActivity.mHandler.sendMessage(message);
        }
        return super.layoutDependsOn(parent, child, dependency);
    }
}