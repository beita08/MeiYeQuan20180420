package com.xasfemr.meiyaya.websocket;

import android.content.Context;
import android.widget.Toast;

import com.xasfemr.meiyaya.bean.ChatMessage;
import com.xasfemr.meiyaya.global.GlobalConstants;
import com.xasfemr.meiyaya.module.player.protocol.GiftMessageProtocol;
import com.xasfemr.meiyaya.utils.LogUtils;
import com.xasfemr.meiyaya.utils.SPUtils;
import com.xasfemr.meiyaya.utils.ToastUtil;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;
import org.simple.eventbus.EventBus;

import java.net.SocketException;
import java.net.URI;

/**
 * 直播页面聊天室Socket
 * Created by sen.luo on 2017/11/20.
 */

public class LiveChatClient extends WebSocketClient{

    private Context context;
    private String liveCid;

    public LiveChatClient(Context context,URI serverUri) {
        super(serverUri);
        this.context = context;

    }

//    public LiveChatClient(Context context, URI serverUri) {
//        super(serverUri);
//        this.context=context;
//        this.liveCid =cid;
//    }


    @Override
    public void onOpen(ServerHandshake handshakedata) {

    }

    @Override
    public void onMessage(String message) {
        LogUtils.show("长连接---message",message);
        ChatMessage chatMessage=new ChatMessage();
        try {
            JSONObject jsonObject =new JSONObject(message);

            if (jsonObject.has("code")){

            int code =jsonObject.getInt("code");

            switch (code){
                case 1: //链接成功

                    if (jsonObject.has("client_id")){
                        String client_id = jsonObject.getString("client_id");
                        EventBus.getDefault().post(client_id,GlobalConstants.EventBus.PUT_CLIENT_ID);
                        SPUtils.putString(context,GlobalConstants.CLIENT_ID,client_id);
                    }
                    break;

                case 10: //初始化
                    String msg =jsonObject.getString("msg");
                    chatMessage.message=msg;
                    LogUtils.show("测试client_10---",chatMessage.message);
                    chatMessage.chat_code=10;
                    if (jsonObject.has("radio_goldmoney")){
                        chatMessage.radio_goldmoney=jsonObject.getString("radio_goldmoney");  //主播金币余额

                    }

//                    if (jsonObject.has("from_goldmoney")){
//                        chatMessage.from_goldmoney=jsonObject.getString("from_goldmoney");  //用户金币余额
//                        SPUtils.putString(context,GlobalConstants.USER_GOLD_NUMBER,chatMessage.from_goldmoney); //将用户余额保存至本地
//                    }

                    EventBus.getDefault().post(chatMessage,GlobalConstants.EventBus.CHAT_LIVE_ANCHOR);
                    break;

                case 20:  //根据code值判断 改变聊天文本颜色
                    chatMessage.chat_code=20;
                    String msg_20 =jsonObject.getString("msg");
                    chatMessage.message=msg_20;
                    LogUtils.show("测试client_20---",msg_20);
                    EventBus.getDefault().post(chatMessage,GlobalConstants.EventBus.CHAT_LIVE_ANCHOR);
                    break;

                case 30: //播放间人数变化
                    if (jsonObject.has("view")){
                        String view =jsonObject.getString("view");
                        EventBus.getDefault().post(view,GlobalConstants.EventBus.LIVE_HORSE_NUMS_CHANGE);
                    }
                    break;

                case 100: //直播间送礼物

                    GiftMessageProtocol messageProtocol =new GiftMessageProtocol();
                    String mess =jsonObject.getString("msg");
                    messageProtocol.msg=mess;
                    LogUtils.show("测试client_100---",mess);

                    if (jsonObject.has("from_name")){
                        messageProtocol.from_name=jsonObject.getString("from_name");
                    }
                    if (jsonObject.has("to_name")){
                        messageProtocol.to_name=jsonObject.getString("to_name");
                    }
                    if (jsonObject.has("radio_goldmoney")){
                        messageProtocol.radio_goldmoney =jsonObject.getString("radio_goldmoney");
                    }
                    if (jsonObject.has("from_goldmoney")){
                        messageProtocol.from_goldmoney =jsonObject.getString("from_goldmoney");

                    }
                    LogUtils.show("直播间--用户剩余--金币",""+messageProtocol.from_goldmoney);
                    if (jsonObject.has("goldmoney")){
                        messageProtocol.goldmoney =jsonObject.getString("goldmoney");
                    }

                    if (jsonObject.has("from_icon")){
                        messageProtocol.from_icon =jsonObject.getString("from_icon");
                    }

                    if (jsonObject.has("from_uid")){
                        messageProtocol.from_uid =jsonObject.getString("from_uid");
                    }

                    EventBus.getDefault().post(messageProtocol,GlobalConstants.EventBus.CHAT_LIVE_SEND_GIFT_BACK);

                    break;

                case 200: //
//                    chatMessage.user_name=null;
                    chatMessage.chat_code=200;
                    String text =jsonObject.getString("msg");
                    String userName =jsonObject.getString("username");
                    LogUtils.show("测试client_200---",text);
                    chatMessage.user_name=userName;
                    chatMessage.message=text;
                    EventBus.getDefault().post(chatMessage,GlobalConstants.EventBus.CHAT_LIVE_ANCHOR);
                    break;

                case 250: //直播结束
                    if (jsonObject.has("status")){
                        int status=jsonObject.getInt("status"); //直播结束状态 0 为结束
                        LogUtils.show("直播结束",status+"");
                        EventBus.getDefault().post(status,GlobalConstants.EventBus.LIVE_END);
                    }

                    break;
            }

            }


        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    private void getClientID(String clientId) {

//        String userId = SPUtils.getString(context, GlobalConstants.userID, "");
//        if (TextUtils.isEmpty(userId)) {
//            toastTip("获取用户信息失败,请重新登录");
//            return;
//        }
//
////        LogUtils.show("直播频道",liveCid);
//        OkHttpUtils
//                .post()
//                .url(GlobalConstants.URL_GET_LIVE_CHAT)
//                .addParams("client_id", clientId)
//                .addParams("from_uid", userId)
//                .addParams("cid",liveCid)
//                .build()
//                .execute(new StringCallback() {
//                    @Override
//                    public void onError(Call call, Exception e, int id) {
//                        e.printStackTrace();
//                        toastTip(e.toString());
//
//                    }
//
//                    @Override
//                    public void onResponse(String response, int id) {
////                        toastTip(response);
//                    }
//                });


    }
    private void toastTip(final String str) {
        Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onClose(int code, String reason, boolean remote) {

    }

    @Override
    public void onError(Exception ex) {
        if (ex instanceof SocketException){
            ToastUtil.showShort(context,"网络连接超时");
        }

//        toastTip(ex.toString());
    }
}
