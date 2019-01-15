package com.xasfemr.meiyaya.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import java.util.ArrayList;
import java.util.List;

public class NEScreenStateObserver {
    private Context                 mContext;
    private ScreenBroadcastReceiver mScreenReceiver;
    private List<Observer<ScreenStateEnum>> observers = new ArrayList<>(1);

    public NEScreenStateObserver(Context context) {
        mContext = context;
        mScreenReceiver = new ScreenBroadcastReceiver();
    }

    /**
     * 是否监听screen状态
     * 
     * @param register
     */
    public void observeScreenStateObserver(Observer<ScreenStateEnum> observer, boolean register) {
        ObserverUtils.registerObservers(this.observers, observer, register);
        if(register) {
            registerListener();
        }else {
            unregisterListener();
        }
    }

    /**
     * 停止screen状态监听
     */
    public void unregisterListener() {
        mContext.unregisterReceiver(mScreenReceiver);
    }

    /**
     * 启动screen状态广播接收器
     */
    private void registerListener() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_USER_PRESENT);
        mContext.registerReceiver(mScreenReceiver, filter);
    }

    public enum ScreenStateEnum {
        SCREEN_ON,
        SCREEN_OFF,
        USER_PRESENT
    }

    /**
     * screen状态广播接收者
     */
    private class ScreenBroadcastReceiver extends BroadcastReceiver {
        private String action = null;

        @Override
        public void onReceive(Context context, Intent intent) {
            action = intent.getAction();
            if (Intent.ACTION_SCREEN_ON.equals(action)) { // 开屏
                ObserverUtils.notifyObservers(observers, ScreenStateEnum.SCREEN_ON);
            } else if (Intent.ACTION_SCREEN_OFF.equals(action)) { // 锁屏
                ObserverUtils.notifyObservers(observers, ScreenStateEnum.SCREEN_OFF);
            } else if (Intent.ACTION_USER_PRESENT.equals(action)) { // 解锁
                ObserverUtils.notifyObservers(observers, ScreenStateEnum.USER_PRESENT);
            }
        }
    }

}