package com.xasfemr.meiyaya.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.xasfemr.meiyaya.R;
import com.xasfemr.meiyaya.bean.ChatItem;
import com.xasfemr.meiyaya.bean.MessageEvent;
import com.xasfemr.meiyaya.bean.SendChatMsgData;
import com.xasfemr.meiyaya.db.dao.ChatMessagesDao;
import com.xasfemr.meiyaya.global.GlobalConstants;
import com.xasfemr.meiyaya.utils.LogUtils;
import com.xasfemr.meiyaya.utils.SPUtils;
import com.xasfemr.meiyaya.websocket.MyyWebSocketClient;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.java_websocket.WebSocket;
import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;
import org.simple.eventbus.ThreadMode;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;

public class ChatActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "ChatActivity";

    private ImageView ivTopBack;
    private TextView  tvTopTitle;
    private String    friendId;
    private String    friendName;
    private String    friendIcon;

    private ArrayList<ChatItem> chatList = new ArrayList<>();

    private static final int TYPE_RECEIVE  = 0;//接受的消息
    private static final int TYPE_SEND     = 1;//发送的消息
    private static final int STATUS_UNREAD = 0;//未读消息
    private static final int STATUS_READED = 1;//已读消息
    private RecyclerView rvChat;
    private TextView     tvChatSend;
    private EditText     etAddChatContent;
    private ChatAdapter  mChatAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        initTopBar();
        setTopTitleText("私信");
        Intent intent = getIntent();
        friendId = intent.getStringExtra("FRIEND_ID");
        friendName = intent.getStringExtra("FRIEND_NAME");
        friendIcon = intent.getStringExtra("FRIEND_ICON");
        setTopTitleText(friendName);

        rvChat = (RecyclerView) findViewById(R.id.rv_chat);
        etAddChatContent = (EditText) findViewById(R.id.et_add_chat_content);
        tvChatSend = (TextView) findViewById(R.id.tv_chat_send);
        tvChatSend.setOnClickListener(this);
        etAddChatContent.setOnClickListener(this);

        //开启聊天的WebSocket
        openMyyWebSocket();

        //从数据库查询历史信息,显示出来
        String mUserId = SPUtils.getString(ChatActivity.this, GlobalConstants.userID, "");
        ArrayList<ChatItem> chatListOld = ChatMessagesDao.getInstance(this).query(mUserId, friendId);
        //数据库查询后,将与该好友的所有聊天置为已读
        ChatMessagesDao.getInstance(this).update(mUserId, friendId, STATUS_READED);
        chatList.addAll(chatListOld);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ChatActivity.this);
        linearLayoutManager.setStackFromEnd(true);
        rvChat.setLayoutManager(linearLayoutManager);
        mChatAdapter = new ChatAdapter();
        rvChat.setAdapter(mChatAdapter);
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
            LogUtils.show("WebSocket", "...ChatActivity...开启WebSocket出现异常...");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_chat_send: //将聊天信息发送到服务器
                String chatContent = etAddChatContent.getText().toString();
                chatContent = removeEnterKey(chatContent);

                if (TextUtils.isEmpty(chatContent)) {
                    Toast.makeText(this, "请键入消息", Toast.LENGTH_SHORT).show();
                } else {
                    tvChatSend.setEnabled(false);
                    gotoSendChatContent(chatContent);
                }
                break;
            case R.id.et_add_chat_content:
                //点击输入框的EditText,现在啥也不干;
                break;
            default:
                break;
        }
    }

    private void gotoSendChatContent(final String chatContent) {
        String mUserId = SPUtils.getString(ChatActivity.this, GlobalConstants.userID, "");
        OkHttpUtils.post().url(GlobalConstants.URL_SEND_CHAT_CONTENT)
                .addParams("uid", friendId)         //接收方
                .addParams("from_uid", mUserId)     //发送者
                .addParams("msg", chatContent)      //发送的消息
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        LogUtils.show(TAG, "----" + mUserId + "的消息" + chatContent + "失败发送至" + friendId + "----");
                        Toast.makeText(ChatActivity.this, "消息发送失败,请重试", Toast.LENGTH_SHORT).show();
                        tvChatSend.setEnabled(true);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        LogUtils.show(TAG, "----" + mUserId + "的消息" + chatContent + "成功发送至" + friendId + "----");
                        LogUtils.show(TAG, "----- " + response + " -----");
                        tvChatSend.setEnabled(true);
                        etAddChatContent.setText("");
                        try {
                            Gson gson = new Gson();
                            SendChatMsgData sendMsg = gson.fromJson(response, SendChatMsgData.class);
                            chatList.add(new ChatItem(sendMsg.data.from_uid, sendMsg.data.to_uid, sendMsg.data.to_icon, sendMsg.data.to_name, TYPE_SEND, sendMsg.data.msg, sendMsg.data.sendtime * 1000, STATUS_READED));
                            ChatMessagesDao.getInstance(ChatActivity.this).insert(sendMsg.data.from_uid, sendMsg.data.to_uid, sendMsg.data.to_icon, sendMsg.data.to_name, TYPE_SEND, sendMsg.data.msg, sendMsg.data.sendtime * 1000, STATUS_READED);
                        } catch (Exception e) {
                            e.printStackTrace();
                            //如果解析异常则使用本地数据
                            chatList.add(new ChatItem(mUserId, friendId, friendIcon, friendName, TYPE_SEND, chatContent, System.currentTimeMillis(), STATUS_READED));
                            ChatMessagesDao.getInstance(ChatActivity.this).insert(mUserId, friendId, friendIcon, friendName, TYPE_SEND, chatContent, System.currentTimeMillis(), STATUS_READED);
                        }
                        mChatAdapter.notifyDataSetChanged();
                        rvChat.smoothScrollToPosition(chatList.size());
                    }
                });
    }

    private class ChatAdapter extends RecyclerView.Adapter<ChatHolder> {

        @Override
        public int getItemViewType(int position) {
            if (chatList.get(position).type == TYPE_RECEIVE) {
                //接受消息
                return TYPE_RECEIVE;
            } else if (chatList.get(position).type == TYPE_SEND) {
                //发送消息
                return TYPE_SEND;
            }
            return -1;
        }

        @Override
        public ChatHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = null;
            switch (viewType) {
                case TYPE_RECEIVE:
                    view = View.inflate(ChatActivity.this, R.layout.item_chat_receive, null);
                    break;

                case TYPE_SEND:
                    view = View.inflate(ChatActivity.this, R.layout.item_chat_send, null);
                    break;

                default:
                    break;
            }
            ChatHolder chatHolder = new ChatHolder(view);

            return chatHolder;
        }

        @Override
        public void onBindViewHolder(ChatHolder holder, int position) {
            String mUserIcon = SPUtils.getString(ChatActivity.this, GlobalConstants.USER_HEAD_IMAGE, "");
            int type = getItemViewType(position);
            if (type == TYPE_RECEIVE) {
                Glide.with(ChatActivity.this).load(chatList.get(position).friendicon).into(holder.civUserIcon);
            } else if (type == TYPE_SEND) {
                Glide.with(ChatActivity.this).load(mUserIcon).into(holder.civUserIcon);
            }

            holder.tvChatContent.setText(chatList.get(position).content);

            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            Date curDate = new Date(chatList.get(position).time);
            String curTime = formatter.format(curDate);
            holder.tvChatTime.setText(curTime);

            if (position > 0 && (chatList.get(position).time - chatList.get(position - 1).time) < 60000) {
                holder.tvChatTime.setVisibility(View.GONE);
            } else {
                holder.tvChatTime.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public int getItemCount() {
            return chatList.size();
        }
    }

    static class ChatHolder extends RecyclerView.ViewHolder {

        public TextView        tvChatTime;
        public CircleImageView civUserIcon;
        public TextView        tvChatContent;

        public ChatHolder(View itemView) {
            super(itemView);
            tvChatTime = (TextView) itemView.findViewById(R.id.tv_chat_time);
            civUserIcon = (CircleImageView) itemView.findViewById(R.id.civ_user_icon);
            tvChatContent = (TextView) itemView.findViewById(R.id.tv_chat_content);
        }
    }


    /*---------------------------------华丽的分割线初始化开始---------------------------------------*/
    public void initTopBar() {
        ivTopBack = (ImageView) findViewById(R.id.iv_top_back);
        tvTopTitle = (TextView) findViewById(R.id.tv_top_title);
        ivTopBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.anim_pre_enter, R.anim.anim_pre_exit);
            }
        });
    }

    //设置标题内容
    public void setTopTitleText(String titleText) {
        tvTopTitle.setText(titleText);
    }
    /*---------------------------------华丽的分割线初始化结束---------------------------------------*/

    //多个回车键换行连续的话,只保留一个
    private String removeEnterKey(String dynamicContent) {

        ArrayList<Integer> indexList = new ArrayList<>();

        for (int i = 1; i < dynamicContent.length(); i++) {
            if (dynamicContent.charAt(i) == '\n' && dynamicContent.charAt(i - 1) == '\n') {
                indexList.add(i);
            }
        }

        for (int i = 0; i < indexList.size(); i++) {
            Log.i(TAG, "gotoAddDynamic: index = " + indexList.get(i));
        }

        for (int i = indexList.size() - 1; i >= 0; i--) {
            dynamicContent = removeChar(indexList.get(i), dynamicContent);
        }
        return dynamicContent;
    }

    private String removeChar(int index, String Str) {
        Str = Str.substring(0, index) + Str.substring(index + 1, Str.length());//substring的取值范围是:[,)
        return Str;
    }

    private void gotoMarkReadMessage(String from_uid, String to_uid) {
        OkHttpUtils.get().url(GlobalConstants.URL_CHAT_READ_MESSAGE)
                .addParams("from_uid", from_uid)
                .addParams("to_uid", to_uid)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        LogUtils.show(TAG, "---标记消息已读,网络访问失败---");
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        LogUtils.show(TAG, "---标记消息已读成功---");
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
        String mUserId = SPUtils.getString(ChatActivity.this, GlobalConstants.userID, "");
        gotoMarkReadMessage(friendId, mUserId);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    //从MyyWebSocketClient使用EventBus发送过来的消息
    @Subscriber(mode = ThreadMode.MAIN, tag = GlobalConstants.EventBus.CHAT_MSG_RECEIVE)
    public void onMessageEvent(MessageEvent event) {
        LogUtils.show("---EventBus---onMessageEvent()----收到消息:", event.content);
        String mUserId = SPUtils.getString(ChatActivity.this, GlobalConstants.userID, "");
        //只展示与当前聊天朋友的消息,其余消息已缓存至数据库中了;
        if (TextUtils.equals(event.friendid, friendId) && TextUtils.equals(event.myid, mUserId)) {

            chatList.add(new ChatItem(event.myid, event.friendid, event.friendicon, event.friendname, event.type, event.content, event.time, event.status));
            mChatAdapter.notifyDataSetChanged();
            rvChat.smoothScrollToPosition(chatList.size());
            gotoMarkReadMessage(friendId, mUserId);
        }
    }
}

/*
SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
Date curDate = new Date(System.currentTimeMillis()); //获取系统当前时间
String curTime = formatter.format(curDate);

Log.i(TAG, "onResponse: System.currentTimeMillis() = " + System.currentTimeMillis());
Log.i(TAG, "onResponse: curDate = " + curDate);
Log.i(TAG, "onResponse: curTime = " + curTime);
*/