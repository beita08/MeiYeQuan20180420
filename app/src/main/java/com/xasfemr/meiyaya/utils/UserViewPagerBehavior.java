package com.xasfemr.meiyaya.utils;

import android.content.Context;
import android.os.Message;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.xasfemr.meiyaya.activity.UserPagerActivity;

/**
 * Description:
 * Company    : shifeier
 * Author     : liuxb
 * Date       : 2018/3/7 0007 10:04
 */

public class UserViewPagerBehavior extends AppBarLayout.ScrollingViewBehavior {

    Context mContext;

    public UserViewPagerBehavior() {
    }

    public UserViewPagerBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, View child, View dependency) {

        //Toolbar+状态栏的高度
        float maxL = DensityUtil.dip2px(mContext, 117) + DensityUtil.getZhuangtai(mContext);

        Log.d("Behavior", "-----Y轴滑动距离 = " + dependency.getY() + ", 总共 = " + DensityUtil.dip2px(mContext, 310) + ", toolsbar = " + maxL);

        Message message = new Message();

        if (dependency.getY() <= 0) {

            float a = (Math.abs(dependency.getY()) / (DensityUtil.dip2px(mContext, 310) - maxL));

            Log.d("Behavior", "-----a = " + a);
            message.what = (int) (a * 100f);

            if (UserPagerActivity.mHandler != null)
                UserPagerActivity.mHandler.sendMessage(message);
        }

        return super.layoutDependsOn(parent, child, dependency);
    }
}
