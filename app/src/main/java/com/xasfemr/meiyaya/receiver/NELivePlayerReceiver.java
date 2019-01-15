package com.xasfemr.meiyaya.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.netease.neliveplayer.sdk.constant.NEType;

public class NELivePlayerReceiver extends BroadcastReceiver {
    private final static String TAG = NELivePlayerReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        //接收播放器资源释放结束消息
        if (intent.getAction().equals(context.getPackageName() + NEType.NELP_ACTION_RECEIVE_RELEASE_SUCCESS_NOTIFICATION)) {
            Log.i(TAG, "NELivePlayer RELEASE SUCCESS!");
            NELivePlayerObserver.getInstance().onReceive();

        }
    }
}