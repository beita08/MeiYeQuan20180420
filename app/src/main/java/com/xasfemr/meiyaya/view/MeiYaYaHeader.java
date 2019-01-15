package com.xasfemr.meiyaya.view;

import android.content.Context;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshKernel;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.util.DensityUtil;
import com.xasfemr.meiyaya.R;

/**
 * Description:
 * Company    : shifeier
 * Author     : liuxb
 * Date       : 2017/11/10 0010 17:21
 */

public class MeiYaYaHeader extends RelativeLayout implements RefreshHeader {


    public MeiYaYaHeader(Context context) {
        super(context);
        initView(context);
    }

    private void initView(Context context) {
        setGravity(Gravity.CENTER);
        ImageView imageView = new ImageView(context);
        Glide.with(this).load(R.drawable.header_logo).into(imageView);

        addView(imageView, DensityUtil.dp2px(50), DensityUtil.dp2px(50));
        setMinimumHeight(DensityUtil.dp2px(60));
    }

    @NonNull
    @Override
    public View getView() {
        return this;
    }

    @Override
    public SpinnerStyle getSpinnerStyle() {
        return SpinnerStyle.Translate;
    }

    //lxb
    @Override
    public void onPullingDown(float percent, int offset, int headerHeight, int extendHeight) {

    }

    //lxb
    @Override
    public void onReleasing(float percent, int offset, int headerHeight, int extendHeight) {

    }

    //lxb
    @Override
    public void setPrimaryColors(@ColorInt int... colors) {

    }

    //lxb
    @Override
    public void onInitialized(RefreshKernel kernel, int height, int extendHeight) {

    }

    //lxb
    @Override
    public void onHorizontalDrag(float percentX, int offsetX, int offsetMax) {

    }

    //lxb:
    @Override
    public void onStartAnimator(RefreshLayout layout, int height, int extendHeight) {

    }

    //lxb:美页圈延迟500毫秒
    @Override
    public int onFinish(RefreshLayout layout, boolean success) {
        return 500;//延迟500毫秒之后再弹回
    }

    //lxb
    @Override
    public boolean isSupportHorizontalDrag() {
        return false;
    }

    //lxb:美页圈没有实现
    @Override
    public void onStateChanged(RefreshLayout refreshLayout, RefreshState oldState, RefreshState newState) {

    }
}
