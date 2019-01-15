package com.xasfemr.meiyaya.application;

import android.app.Application;
import android.app.Notification;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.multidex.MultiDex;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.view.CropImageView;
import com.lzy.ninegrid.NineGridView;
import com.mob.MobSDK;
import com.xasfemr.meiyaya.BuildConfig;
import com.xasfemr.meiyaya.R;
import com.xasfemr.meiyaya.utils.GlideImageLoader;

import cn.jiguang.api.JAnalyticsAction;
import cn.jpush.android.api.BasicPushNotificationBuilder;
import cn.jpush.android.api.JPushInterface;

/**
 * Description: 1、生命周期长 2、单实例 3、onCreate方法可以简单的认为是一个应用程序的入口,onCreate是运行在主线程中
 * Company    : shifeier
 * Author     : liuxb
 * Date       : 2017/9/8 0008 10:12
 */

public class MyApplication extends Application {
    private static final String TAG = "MyApplication";

    private static MyApplication singleton;

    public MyApplication() {
    }

    protected String a() {
        return null;
    }

    protected String b() {
        return null;
    }

    public static MyApplication getIns() {
        return singleton;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Fresco.initialize(this);//初始化Fresco,使用SimpleDraweeView加载大图
        singleton = this;
        MobSDK.init(this, this.a(), this.b());
        initJPush();

        NineGridView.setImageLoader(new GlideNineImageLoader());

        //初始化图片选择器ImagePicker
        initImagePicker();

        //网易云视频相关:
        /*CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(getApplicationContext());*/
    }

    private void initJPush() {
        JPushInterface.setDebugMode(BuildConfig.MieYeQuan_TEST);
        JPushInterface.init(this);
        BasicPushNotificationBuilder builder = new BasicPushNotificationBuilder(this);
        builder.statusBarDrawable = R.mipmap.meiyaya_logo2;
        builder.notificationFlags = Notification.FLAG_AUTO_CANCEL
                | Notification.FLAG_SHOW_LIGHTS;  //设置为自动消失和呼吸灯闪烁

        builder.notificationDefaults = Notification.DEFAULT_SOUND
                | Notification.DEFAULT_VIBRATE
                | Notification.DEFAULT_LIGHTS;  // 设置为铃声、震动、呼吸灯闪烁都要
        JPushInterface.setPushNotificationBuilder(1, builder);

    }

    //初始化图片选择器ImagePicker
    private void initImagePicker() {
        ImagePicker imagePicker = ImagePicker.getInstance();
        imagePicker.setImageLoader(new GlideImageLoader());   //设置图片加载器
        imagePicker.setShowCamera(true);    //显示拍照按钮
        imagePicker.setCrop(true);          //允许裁剪（单选才有效）
        imagePicker.setSaveRectangle(true); //是否按矩形区域保存
        imagePicker.setSelectLimit(9);      //选中数量限制
        imagePicker.setStyle(CropImageView.Style.RECTANGLE);  //裁剪框的形状
        imagePicker.setFocusWidth(1000);   //裁剪框的宽度。单位像素（圆形自动取宽高最小值）
        imagePicker.setFocusHeight(1000);  //裁剪框的高度。单位像素（圆形自动取宽高最小值）
        imagePicker.setOutPutX(1000);     //保存文件的宽度。单位像素
        imagePicker.setOutPutY(1000);     //保存文件的高度。单位像素
    }

    //GlideImageLoader 加载
    private class GlideNineImageLoader implements NineGridView.ImageLoader {

        @Override
        public void onDisplayImage(Context context, ImageView imageView, String url) {
            Glide.with(context).load(url).apply(new RequestOptions().placeholder(R.color.textcolor_f3f3f3)).into(imageView);
            //Glide.with(context).load(url).into(imageView);
        }

        @Override
        public Bitmap getCacheImage(String url) {
            return null;
        }
    }


    @Override
    public void onTerminate() {
        super.onTerminate();

    }

    /**
     * 因为API数超过了64K，用的MultiDex库,application中没有重写attachBaseContext方法。导致找不到类库的方法；
     */
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

}