package com.xasfemr.meiyaya.websocket;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.xasfemr.meiyaya.bean.MessageEvent;
import com.xasfemr.meiyaya.db.dao.ChatMessagesDao;
import com.xasfemr.meiyaya.global.GlobalConstants;
import com.xasfemr.meiyaya.utils.LogUtils;
import com.xasfemr.meiyaya.utils.SPUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.java_websocket.WebSocket;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;
import org.simple.eventbus.EventBus;

import java.net.SocketException;
import java.net.URI;

import okhttp3.Call;

import static android.content.Context.ACTIVITY_SERVICE;

/**
 * Description:
 * Company    : shifeier
 * Author     : liuxb
 * Date       : 2017/10/24 0024 15:42
 */

public class MyyWebSocketClient extends WebSocketClient {

    private static final int TYPE_RECEIVE  = 0;//接受的消息
    private static final int TYPE_SEND     = 1;//发送的消息
    private static final int STATUS_UNREAD = 0;//未读消息
    private static final int STATUS_READED = 1;//已读消息
    private static final int MSG_EMPTY     = 0;

    private int i = 0;

    private Context mContext;

    //3.选择懒汉模式
    private static MyyWebSocketClient mInstance;

    //1.私有化构造方法
    private MyyWebSocketClient(Context context) {
        //开启WebSocket客户端  webSocket链接: ws://123.138.111.15:8282  //ws://app.xasfemr.com:8282
        super(URI.create(GlobalConstants.SOCKET_URL));
        mContext = context;
    }

    //2.公开方法,返回单例对象
    public static MyyWebSocketClient getInstance(Context context) {
        //懒汉: 考虑线程安全问题, 两种方式: 1. 给方法加同步锁 synchronized, 效率低; 2. 给创建对象的代码块加同步锁
        if (mInstance == null) {
            synchronized (MyyWebSocketClient.class) {
                if (mInstance == null) {
                    GlobalConstants.webSocketConnectNumber = 1;
                    GlobalConstants.RECONNECT_DELAYED_TIME = 1000;
                    mInstance = new MyyWebSocketClient(context);
                    LogUtils.show("WebSocket", "**********初始化单例对象**********");
                }
            }
        }
        return mInstance;
    }


    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            LogUtils.show("WebSocket", "---- getReadyState(" + i++ + ") = " + mInstance.getReadyState());
            LogUtils.show("WebSocket", "GlobalConstants.webSocketConnectNumber = " + GlobalConstants.webSocketConnectNumber);

            if (GlobalConstants.webSocketConnectNumber < 15 && SPUtils.getboolean(mContext, GlobalConstants.isLoginState, false)) {
                try {
                    if (mInstance != null) {
                        WebSocket.READYSTATE readyState = mInstance.getReadyState();
                        if (readyState.equals(WebSocket.READYSTATE.NOT_YET_CONNECTED)) {
                            mInstance.connect();
                            LogUtils.show("WebSocket", "---同一mInstance对象-重新开启WebSocket客户端---");
                            LogUtils.show("WebSocket", "---getReadyState() = " + readyState);

                        } else if (readyState.equals(WebSocket.READYSTATE.CLOSED) || readyState.equals(WebSocket.READYSTATE.CLOSING)) {
                            LogUtils.show("WebSocket", "---先关闭之前的WebSocket---");
                            mInstance.closeBlocking();
                            mInstance = new MyyWebSocketClient(mContext);
                            mInstance.connect();
                            GlobalConstants.webSocketConnectNumber++;
                            LogUtils.show("WebSocket", "---new对象-重新开启WebSocket客户端---");
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    LogUtils.show("WebSocket", "...MyyWebSocketClient...开启WebSocket出现异常...");
                }
            } else {
                //未登录或者重连超过设置次数则清空重连消息队列，并将mInstance置为null（待外部初始化连接）
                mHandler.removeMessages(MSG_EMPTY);
                mInstance = null;
            }
        }
    };

    //链接打开
    @Override
    public void onOpen(ServerHandshake handshakedata) {
        LogUtils.show("WebSocket", "....MyyWebSocketClient....onOpen....");
        GlobalConstants.webSocketConnectNumber = 1;
        GlobalConstants.RECONNECT_DELAYED_TIME = 1000;
        mHandler.removeMessages(MSG_EMPTY);
    }

    //新消息
    @Override
    public void onMessage(String message) {
        mHandler.removeMessages(MSG_EMPTY);
        LogUtils.show("WebSocket", "....MyyWebSocketClient....onMessage....message=" + message);
        try {
            JSONObject jo = new JSONObject(message);

            if (jo.has("code")) {
                int code = jo.getInt("code");
                switch (code) {
                    case 1:
                        String client_id = jo.getString("client_id");
                        LogUtils.show("WebSocket", "...MyyWebSocketClient......链接成功!.....");
                        gotoBindBoth(client_id);
                        break;
                    case 10:  //绑定或注册成功
                        LogUtils.show("WebSocket", "*************客户端和用户绑定成功**************");
                        break;
                    case 20:  //直播间系统管理员消息
                        LogUtils.show("WebSocket", "---收到-直播间系统管理员消息---");
                        break;
                    case 100: //直播间送金币
                        LogUtils.show("WebSocket", "---收到-直播间送金币消息---");
                        break;
                    case 200: //直播间普通消息
                        LogUtils.show("WebSocket", "---收到-直播间聊天室普通消息---");
                        break;
                    case 300:  //私信聊天
                        String msg = jo.getString("msg");
                        String from_uid = jo.getString("from_uid");
                        String from_icon = jo.getString("from_icon");
                        String from_name = jo.getString("from_name");
                        String to_uid = jo.getString("to_uid");
                        long sendtime = jo.getLong("sendtime") * 1000;  //java的时间戳是毫秒级的

                        LogUtils.show("WebSocket", "...MyyClient收到私信聊天消息...message = " + message + " ---");
                        //1.将收到消息存入本地消息数据库
                        boolean insert = ChatMessagesDao.getInstance(mContext).insert(to_uid, from_uid, from_icon, from_name, TYPE_RECEIVE, msg, sendtime, STATUS_UNREAD);

                        //2.使用EventBus将消息发至ChatActivity
                        MessageEvent messageEvent = new MessageEvent(to_uid, from_uid, from_icon, from_name, TYPE_RECEIVE, msg, sendtime, STATUS_UNREAD);
                        System.out.println("messageEvent = " + messageEvent.toString());
                        EventBus.getDefault().post(messageEvent, GlobalConstants.EventBus.CHAT_MSG_RECEIVE);

                        ActivityManager activityManager = (ActivityManager) mContext.getSystemService(ACTIVITY_SERVICE);
                        String topActivity = activityManager.getRunningTasks(1).get(0).topActivity.getShortClassName();
                        if (TextUtils.equals(topActivity, ".main.MainActivity")) {
                            EventBus.getDefault().post("",GlobalConstants.EventBus.RECEIVE_MSG_UPDATA_DOT);
                        }

                        break;
                    //case 301: //通知金币充值成功
                    //    String goldmoney = jo.getString("goldmoney");
                    //    SPUtils.putString(mContext, GlobalConstants.USER_GOLD, goldmoney); //将用户余额保存至本地
                    //    break;
                    default:
                        break;
                }
            } else {
                LogUtils.show("WebSocket", "...心跳包...");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void gotoBindBoth(String client_id) {

        String userId = SPUtils.getString(mContext, GlobalConstants.userID, "");
        if (TextUtils.isEmpty(userId)) {
            return;
        }

        OkHttpUtils.post().url(GlobalConstants.URL_BIND_CLIENT_USER)
                .addParams("client_id", client_id)
                .addParams("uid", userId)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        LogUtils.show("WebSocket", "----绑定调试失败!----");
                        e.printStackTrace();
                        try {
                            mInstance.closeBlocking();
                            mHandler.removeMessages(MSG_EMPTY);
                            mHandler.sendEmptyMessageDelayed(MSG_EMPTY, 1000);
                        } catch (InterruptedException e1) {
                            e1.printStackTrace();
                        }
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        LogUtils.show("WebSocket", "----绑定调试成功!----response = [" + response + "]----");
                    }
                });
    }

    //链接关闭
    @Override
    public void onClose(int code, String reason, boolean remote) {
        LogUtils.show("WebSocket", ".....MyyWebSocketClient......onClose....");
        mHandler.removeMessages(MSG_EMPTY);
        mHandler.sendEmptyMessageDelayed(MSG_EMPTY, GlobalConstants.RECONNECT_DELAYED_TIME);
        GlobalConstants.RECONNECT_DELAYED_TIME = GlobalConstants.RECONNECT_DELAYED_TIME * 2;
    }

    //链接发生错误
    @Override
    public void onError(Exception ex) {
        LogUtils.show("WebSocket", ".....MyyWebSocketClient......onError....");
        if (ex instanceof SocketException && mContext != null) {
            //Toast.makeText(mContext, "网络连接超时", Toast.LENGTH_SHORT).show();
            LogUtils.show("WebSocket", ".....MyyWebSocketClient......onError....SocketException....");
        }

    }
}