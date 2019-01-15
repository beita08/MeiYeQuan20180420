package com.xasfemr.meiyaya.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.RelativeLayout;

import com.xasfemr.meiyaya.BuildConfig;
import com.xasfemr.meiyaya.R;
import com.xasfemr.meiyaya.global.GlobalConstants;
import com.xasfemr.meiyaya.main.BaseActivity;
import com.xasfemr.meiyaya.main.MainActivity;
import com.xasfemr.meiyaya.utils.LogUtils;
import com.xasfemr.meiyaya.utils.SPUtils;
import com.xasfemr.meiyaya.utils.ToastUtil;
import com.xasfemr.meiyaya.websocket.MyyWebSocketClient;

import org.java_websocket.WebSocket;

public class SplashActivity extends BaseActivity {

    private RelativeLayout rlRoot;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            //界面的跳转
            //判断是否是第一次使用这款软件
            boolean isFirstIn = SPUtils.getboolean(SplashActivity.this, "isFirstIn", true);
            Intent intent = new Intent();
            if (isFirstIn) {
                //跳转新手引导界面
                intent.setClass(SplashActivity.this, GuideActivity.class);
            } else {
                //跳转主界面
                intent.setClass(SplashActivity.this, MainActivity.class);
            }
            //当前的界面消失
            finish();
            startActivity(intent);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        rlRoot = (RelativeLayout) findViewById(R.id.rl_root);

        boolean isLoginState = SPUtils.getboolean(this, GlobalConstants.isLoginState, false);
        if (isLoginState) {
            //开启聊天的WebSocket
            openMyyWebSocket();
        }

        if (BuildConfig.MieYeQuan_TEST){
            ToastUtil.showShort(SplashActivity.this,"测试环境登陆成功");
        }
        initAnimation();
    }

    private void initAnimation() {

        //2、缩放动画
        ScaleAnimation scaleAnimation = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimation.setFillAfter(true);
        scaleAnimation.setDuration(1000);

        //3、渐变动画
        AlphaAnimation alphaAnimation = new AlphaAnimation(0.0f, 1.0f);
        alphaAnimation.setFillAfter(true);
        alphaAnimation.setDuration(1000);

        //动画集合
        AnimationSet animationSet = new AnimationSet(false);
        animationSet.addAnimation(scaleAnimation);
        animationSet.addAnimation(alphaAnimation);

        rlRoot.startAnimation(animationSet);

        animationSet.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mHandler.sendEmptyMessageDelayed(0, 1000);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
    }

    private void openMyyWebSocket() {
        //开启WebSocket客户端
        try {
            WebSocket.READYSTATE readyState = MyyWebSocketClient.getInstance(this).getReadyState();
            LogUtils.show("WebSocket", "getReadyState() = " + readyState);

            //|| readyState.equals(WebSocket.READYSTATE.CLOSED) || readyState.equals(WebSocket.READYSTATE.CLOSING)
            if (readyState.equals(WebSocket.READYSTATE.NOT_YET_CONNECTED)) {
                LogUtils.show("WebSocket", "---开启WebSocket客户端---");
                MyyWebSocketClient.getInstance(this).connect();
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.show("WebSocket", "...MyApplication...开启WebSocket出现异常...");
        }
    }
}
