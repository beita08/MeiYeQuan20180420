package com.xasfemr.meiyaya.base.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.lzy.imagepicker.view.SystemBarTintManager;
import com.xasfemr.meiyaya.R;
import com.xasfemr.meiyaya.main.MainActivity;
import com.xasfemr.meiyaya.view.LoadDataView;
import com.xasfemr.meiyaya.view.swipbackhelper.SwipeBackHelper;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import butterknife.ButterKnife;

/**
 * Created by sen.luo on 2017/11/24.
 */

public abstract class MVPBaseActivity extends AppCompatActivity {

    protected abstract int layoutId();

    protected abstract ViewGroup loadDataViewLayout();

    protected abstract void initView();

    protected abstract void initData();

    protected abstract void getLoadView(LoadDataView loadView);

    protected abstract void initPresenter();


    private LoadDataView mLoadView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarLightMode(this, true);
        setContentView(layoutId());
//        initSwipeBack();
        ButterKnife.bind(this);
        initView();
        initViewGroup();
        initPresenter();
        initData();
        //immersiveMode();
    }

//    /**
//     * 覆写startActivity，设置切换动画
//     */
//    @Override
//    public void startActivity(Intent intent) {
//        super.startActivity(intent);
//        overridePendingTransition(R.anim.anim_next_enter, R.anim.anim_next_exit);
//    }
//
//    @Override
//    public void startActivityForResult(Intent intent, int requestCode) {
//        super.startActivityForResult(intent, requestCode);
//        overridePendingTransition(R.anim.anim_next_enter, R.anim.anim_next_exit);
//    }
//
//    /**
//     * 覆写onBackPressed，并设置切换动画
//     */
//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//        overridePendingTransition(R.anim.anim_pre_enter, R.anim.anim_pre_exit);
//    }
//
//    /**
//     * 覆写finish，并设置切换动画
//     */
//    @Override
//    public void finish() {
//        super.finish();
//        overridePendingTransition(R.anim.anim_pre_enter, R.anim.anim_pre_exit);
//    }

    /**
     * 嵌入loaddataview
     */
    private void initViewGroup() {
        ViewGroup view = loadDataViewLayout();
        if (null != view) {
            ViewGroup viewGroup = (ViewGroup) view.getParent();
            if (viewGroup != null) {
                viewGroup.removeView(view);
                this.mLoadView = new LoadDataView(this, view);
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                //                params.addRule(RelativeLayout.BELOW, R.id.include_topbar);
                //                params.setMargins(0, 0, 0, 0);
                viewGroup.addView(this.mLoadView, params);
                getLoadView(this.mLoadView);
            }
        }
    }


    private void initSwipeBack() {
        SwipeBackHelper.onCreate(this);
        SwipeBackHelper.getCurrentPage(this)//获取当前页面
                .setSwipeBackEnable(true)//设置是否可滑动
                .setSwipeEdge(100)//可滑动的范围。px。200表示为左边200px的屏幕
                .setSwipeEdgePercent(0.1f)//可滑动的范围。百分比。0.2表示为左边20%的屏幕
                .setSwipeSensitivity(1f)//对横向滑动手势的敏感程度。0为迟钝 1为敏感
                .setScrimColor(Color.TRANSPARENT)//底层阴影颜色
                .setClosePercent(0.8f)//触发关闭Activity百分比
                .setSwipeRelateEnable(true)//是否与下一级activity联动。默认关
                .setSwipeRelateOffset(500)//activity联动时的偏移量。默认500px。
                .setDisallowInterceptTouchEvent(false);//不抢占事件，默认true为关（事件将先由子View处理再由滑动关闭处理）
    }


    //沉浸式效果
    private void immersiveMode() {
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        //        ActionBar actionBar = getSupportActionBar();
        //        actionBar.hide();
    }


    /**
     * 设置状态栏黑色字体图标，
     * 适配4.4以上版本MIUIV、Flyme和6.0以上版本其他Android
     * @param activity
     * @param dark
     *         是否把状态栏字体及图标颜色设置为深色
     * @return 1:MIUUI 2:Flyme 3:android6.0
     */
    public void StatusBarLightMode(Activity activity, boolean dark) {
        if (MIUISetStatusBarLightMode(activity.getWindow(), dark)) {
            systemBarTint();
            if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)) {
                activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            systemBarTint();
            activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }

    public void systemBarTint() {
        //        BKConstant.isSystemBarTint = true;
      /*
        *已下处理状态栏的问题
                * **/
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor("#ffffff"));
            //   window.setNavigationBarColor(Color.parseColor("#000000"));

            if (this instanceof MainActivity) {
                window.setStatusBarColor(Color.parseColor("#00000000"));
                return;
            }

            ViewGroup mContentView = (ViewGroup) findViewById(Window.ID_ANDROID_CONTENT);
            View mChildView = mContentView.getChildAt(0);
            if (mChildView != null) {
                //注意不是设置 ContentView 的 FitsSystemWindows, 而是设置 ContentView 的第一个子 View . 使其不为系统 View 预留空间.
                ViewCompat.setFitsSystemWindows(mChildView, true);
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
            // 创建状态栏的管理实例
            SystemBarTintManager tintManager = new SystemBarTintManager(this);
            // 激活状态栏设置
            tintManager.setStatusBarTintEnabled(true);
            //		    // 激活导航栏设置
            tintManager.setNavigationBarTintEnabled(true);
            // 设置一个颜色给系统栏  #ff4444红色

            tintManager.setTintColor(Color.parseColor("#ffffff"));


            if (this instanceof MainActivity) {
                tintManager.setTintColor(Color.parseColor("#00000000"));
                return;
            }

            ViewGroup mContentView = (ViewGroup) findViewById(Window.ID_ANDROID_CONTENT);
            View mChildView = mContentView.getChildAt(0);
            if (mChildView != null) {
                //注意不是设置 ContentView 的 FitsSystemWindows, 而是设置 ContentView 的第一个子 View . 使其不为系统 View 预留空间.
                ViewCompat.setFitsSystemWindows(mChildView, true);
            }
        }


    }

    private void setTranslucentStatus(boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }


    /**
     * 设置状态栏图标为深色和魅族特定的文字风格
     * 可以用来判断是否为Flyme用户
     * @param window
     *         需要设置的窗口
     * @param dark
     *         是否把状态栏字体及图标颜色设置为深色
     * @return boolean 成功执行返回true
     */
    public boolean FlymeSetStatusBarLightMode(Window window, boolean dark) {
        boolean result = false;
        if (window != null) {
            try {
                WindowManager.LayoutParams lp = window.getAttributes();
                Field darkFlag = WindowManager.LayoutParams.class
                        .getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON");
                Field meizuFlags = WindowManager.LayoutParams.class
                        .getDeclaredField("meizuFlags");
                darkFlag.setAccessible(true);
                meizuFlags.setAccessible(true);
                int bit = darkFlag.getInt(null);
                int value = meizuFlags.getInt(lp);
                if (dark) {
                    value |= bit;
                } else {
                    value &= ~bit;
                }
                meizuFlags.setInt(lp, value);
                window.setAttributes(lp);
                result = true;
            } catch (Exception e) {

            }
        }
        return result;
    }

    /**
     * 设置状态栏字体图标为深色，需要MIUIV6以上
     * @param window
     *         需要设置的窗口
     * @param dark
     *         是否把状态栏字体及图标颜色设置为深色
     * @return boolean 成功执行返回true
     */
    public boolean MIUISetStatusBarLightMode(Window window, boolean dark) {
        boolean result = false;
        if (window != null) {
            Class clazz = window.getClass();
            try {
                int darkModeFlag = 0;
                Class layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
                Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
                darkModeFlag = field.getInt(layoutParams);
                Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);
                if (dark) {
                    extraFlagField.invoke(window, darkModeFlag, darkModeFlag);//状态栏透明且黑色字体
                } else {
                    extraFlagField.invoke(window, 0, darkModeFlag);//清除黑色字体
                }
                result = true;
            } catch (Exception e) {

            }
        }
        return result;
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
//        SwipeBackHelper.onPostCreate(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        SwipeBackHelper.onDestroy(this);
    }
}
