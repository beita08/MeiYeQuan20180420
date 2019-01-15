package com.xasfemr.meiyaya.receiver;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.xasfemr.meiyaya.activity.LoginActivity;
import com.xasfemr.meiyaya.global.GlobalConstants;
import com.xasfemr.meiyaya.main.MainActivity;
import com.xasfemr.meiyaya.neteasecloud.NELivePlayerActivity;
import com.xasfemr.meiyaya.utils.LogUtils;
import com.xasfemr.meiyaya.utils.SPUtils;

import org.json.JSONException;
import org.json.JSONObject;

import cn.jpush.android.api.JPushInterface;

/**
 * 极光推送自定义接受广播
 * Created by sen.luo on 2017/12/26.
 */

public class PushReceiver extends BroadcastReceiver{
    private String extra="",cid="",icon="",user_id="",user_name="",videoPath="";

    @Override
    public void onReceive(Context context, Intent intent) {

        Bundle bundle = intent.getExtras();
        LogUtils.show("PushReceiver:", "onReceive - " + intent.getAction());

        switch (intent.getAction()){

            case JPushInterface.ACTION_NOTIFICATION_RECEIVED: //收到了通知
//                String content = bundle.getString(JPushInterface.EXTRA_ALERT);
//                LogUtils.show("PushReceiver","content:"+content);
//                extra =bundle.getString(JPushInterface.EXTRA_EXTRA);
////                SPUtils.putString(context,GlobalConstants.PUSEH_MESSAGE,extra);
//                LogUtils.show("PushReceiver","extra"+extra);
//                if (extra!=null){
//                    try {
//                        JSONObject jsonObject =new JSONObject(extra);
//                        String data =jsonObject.getString("data");
//                        JSONObject js1 =new JSONObject(data);
//                        String addr =js1.getString("addr");
//                        JSONObject js3 =new JSONObject(addr);
//
//                        cid =js1.getString("cid");
//                        icon =js1.getString("icon");
//                        user_id =js1.getString("userid");
//                        user_name =js1.getString("username");
//                        videoPath =js3.getString("rtmpPullUrl");
//
//
//
//                        LogUtils.show("推送直播内容：",cid+icon+user_id+user_name+videoPath);
//
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//
//                }

                break;

            case JPushInterface.ACTION_NOTIFICATION_OPENED: //用户点击的通知
                LogUtils.show("点击通知","没有登录");

                if (!bundle.getString(JPushInterface.EXTRA_EXTRA).equals("{}")){

                    if (TextUtils.isEmpty(SPUtils.getString(context, GlobalConstants.userID,""))){
                        context.startActivity(new Intent(context,LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));

                    }else {
                        extra =bundle.getString(JPushInterface.EXTRA_EXTRA);
                        LogUtils.show("跳转之前：",extra);
                        if (extra!=null){
                            try {
                                JSONObject jsonObject =new JSONObject(extra);
                                String data =jsonObject.getString("data");
                                JSONObject js1 =new JSONObject(data);
                                String addr =js1.getString("addr");
                                JSONObject js3 =new JSONObject(addr);

                                cid =js1.getString("cid");
                                icon =js1.getString("icon");
                                user_id =js1.getString("userid");
                                user_name =js1.getString("username");
                                videoPath =js3.getString("rtmpPullUrl");

                                LogUtils.show("推送直播内容：",cid+icon+user_id+user_name+videoPath);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                        LogUtils.show("跳转之前：","cid="+cid+"icon:"+icon+"user_id:"+user_id+"user_name"+user_name+"videopath:"+videoPath);
                        Intent intentStart =new Intent(context,NELivePlayerActivity.class);
                        intentStart.putExtra("videoPath", videoPath);
                        intentStart.putExtra("cid", cid); //直播间ID
                        intentStart.putExtra("icon", icon);
                        intentStart.putExtra("user_id", user_id);
                        intentStart.putExtra("user_name", user_name);
                        intentStart.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intentStart);
                    }
                    }


                }else {
                    context.startActivity(new Intent(context,MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                }


                break;

        }


//        if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
//            String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
//            LogUtils.show("PushReceiver", "[MyReceiver] 接收Registration Id : " + regId);
//        }else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
//            LogUtils.show("PushReceiver", "收到了自定义消息。消息内容是：" + bundle.getString(JPushInterface.EXTRA_MESSAGE));
//            // 自定义消息不会展示在通知栏，完全要开发者写代码去处理
//        } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
//            LogUtils.show("PushReceiver", "收到了通知");
//            // 在这里可以做些统计，或者做些其他工作
//        } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
//            LogUtils.show("PushReceiver", "用户点击打开了通知");
//            // 在这里可以自己写代码去定义用户点击后的行为
////            Intent i = new Intent(context, TestActivity.class);  //自定义打开的界面
////            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
////            context.startActivity(i);
//        } else {
//            LogUtils.show("PushReceiver" ,"Unhandled intent - " + intent.getAction());
//        }


//        String content =bundle.getString("JPushInterface.EXTRA_ALERT");

//        LogUtils.show("推送：",content+"");


    }
}
