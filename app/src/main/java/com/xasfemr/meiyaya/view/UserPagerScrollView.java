package com.xasfemr.meiyaya.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

/**
 * Description:
 * Company    : shifeier
 * Author     : liuxb
 * Date       : 2018/3/1 0001 11:44
 */

public class UserPagerScrollView extends ScrollView {


    public UserPagerScrollView(Context context) {
        this(context, null);
    }

    public UserPagerScrollView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public UserPagerScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    private OnScrollChangeListener mOnScrollChangeListener;

    /**
     * 设置滚动接口
     * @param
     */

    public void setOnScrollChangeListener(OnScrollChangeListener onScrollChangeListener) {
        mOnScrollChangeListener = onScrollChangeListener;
    }


    /**
     *
     *定义一个滚动接口
     * */

    public interface OnScrollChangeListener{
        void onScrollChanged(UserPagerScrollView scrollView,int l, int t, int oldl, int oldt);
    }

    /**
     * 当scrollView滑动时系统会调用该方法,并将该回调放过中的参数传递到自定义接口的回调方法中,
     * 达到scrollView滑动监听的效果
     *
     * */
    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if(mOnScrollChangeListener!=null){
            mOnScrollChangeListener.onScrollChanged(this,l,t,oldl,oldt);
        }
    }
}
