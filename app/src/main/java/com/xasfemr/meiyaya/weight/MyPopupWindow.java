package com.xasfemr.meiyaya.weight;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;

import com.xasfemr.meiyaya.R;


/**
 * Created by Administrator on 2015/9/22.
 */
public class MyPopupWindow extends PopupWindow {

    public View view;

    private Context mContext;


    public MyPopupWindow(Activity context, int layoutId) {
        super(context);
        this.mContext=context;

        view = LayoutInflater.from(context).inflate(layoutId, null);
        setContentView(view);

        this.setContentView(view);
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setFocusable(true);
        this.setAnimationStyle(R.style.AppTheme);

//        this.setBackgroundAlpha(0.5f);
//        this.setBackgroundDrawable(new BitmapDrawable());
        setOutsideTouchable(true);


    }

    public View getView() {
        return view;
    }



}
