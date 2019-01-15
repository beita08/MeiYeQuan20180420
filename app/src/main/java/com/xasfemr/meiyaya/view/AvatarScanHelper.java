package com.xasfemr.meiyaya.view;

import android.app.Dialog;
import android.content.Context;
import android.net.Uri;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.facebook.drawee.view.SimpleDraweeView;
import com.xasfemr.meiyaya.R;

/**
 * Description:
 * Company    : shifeier
 * Author     : liuxb
 * Date       : 2017/11/24 0024 19:29
 **/
public class AvatarScanHelper extends Dialog{

    private String           avatarUrl;
    private Context          mContext;
    private SimpleDraweeView mSimpleDraweeView;

    public AvatarScanHelper(Context context, String avatarUrl) {
        // 设置自定义样式
        super(context, R.style.CustomDialog_fill);
        this.mContext = context;
        this.avatarUrl = avatarUrl;
        initImageView(avatarUrl);
    }

    //直接使用imageview展示头像图片
    private void initImageView(String avatarUrl) {
        //重点在于用setContentView()加载自定义布局
        setContentView(R.layout.dialog_avatar_scan);
        mSimpleDraweeView = (SimpleDraweeView) findViewById(R.id.simple_image);
        //fresco加载图片
        mSimpleDraweeView.setImageURI(Uri.parse(avatarUrl));
        //点击取消对话框
        mSimpleDraweeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        setParams();
    }

    //设置对话框的宽高适应全屏
    private void setParams() {
        //原理: 修改dialog所在窗口Window的位置, dialog随窗口显示
        Window window = this.getWindow();//获取dialog所在的窗口对象
        WindowManager.LayoutParams attributes = window.getAttributes();//获取当前窗口的属性(布局参数)
        attributes.gravity = Gravity.CENTER;
        attributes.height = WindowManager.LayoutParams.MATCH_PARENT;
        attributes.width = WindowManager.LayoutParams.MATCH_PARENT;     //宽度
        window.setAttributes(attributes);//重新设置布局参数
    }
}
