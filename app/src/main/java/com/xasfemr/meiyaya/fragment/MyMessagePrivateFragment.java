package com.xasfemr.meiyaya.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.xasfemr.meiyaya.R;
import com.xasfemr.meiyaya.activity.ChatActivity;
import com.xasfemr.meiyaya.activity.MyMessageActivity;
import com.xasfemr.meiyaya.bean.UnreadMessageList;
import com.xasfemr.meiyaya.db.dao.ChatMessagesDao;
import com.xasfemr.meiyaya.global.GlobalConstants;
import com.xasfemr.meiyaya.utils.LogUtils;
import com.xasfemr.meiyaya.utils.SPUtils;
import com.xasfemr.meiyaya.view.MeiYaYaHeader;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import okhttp3.Call;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyMessagePrivateFragment extends BaseFragment {
    private static final String TAG = "MyMessagePrivate";

    private ArrayList<UnreadMessageList.UnreadMessage> messageList = new ArrayList<>();

    private boolean isPullRefresh = false;

    private MyMessageActivity mMsgActivity;
    private RefreshLayout     refreshLayout;
    private RecyclerView      rvMsgPrivate;
    private LinearLayout      llLoading;
    private LinearLayout      llEmptyData;
    private LinearLayout      llNetworkFailed;
    private Button            btnAgainLoad;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMsgActivity = (MyMessageActivity) getActivity();
    }

    @Override
    public View initView() {
        View view = View.inflate(mMsgActivity, R.layout.fragment_my_message_private, null);
        refreshLayout = (RefreshLayout) view.findViewById(R.id.refreshLayout);
        rvMsgPrivate = (RecyclerView) view.findViewById(R.id.rv_msg_private);
        llLoading = (LinearLayout) view.findViewById(R.id.ll_loading);
        llEmptyData = (LinearLayout) view.findViewById(R.id.ll_empty_data);
        llNetworkFailed = (LinearLayout) view.findViewById(R.id.ll_network_failed);
        btnAgainLoad = (Button) view.findViewById(R.id.btn_again_load);

        btnAgainLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isPullRefresh = false;
                getPrivateMessageData();
            }
        });
        setFreshLayout();
        return view;
    }

    private void setFreshLayout() {
        refreshLayout.setRefreshHeader(new MeiYaYaHeader(mMsgActivity));
        refreshLayout.setEnableLoadmore(false);

        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                isPullRefresh = true;
                getPrivateMessageData();
            }
        });
    }

    @Override
    public void initData() {
    }

    @Override
    public void onStart() {
        super.onStart();
        isPullRefresh = false;
        getPrivateMessageData();
    }

    //获取私信数据
    private void getPrivateMessageData() {
        messageList.clear();
        getMessageListFromDB();//从数据库获取私信列表 (同步方法)
        gotoGetUnreadMessageList();//从网络获取未读私信列表(异步方法)
    }

    private void getMessageListFromDB() {
        String mUserId = SPUtils.getString(mMsgActivity, GlobalConstants.userID, "");
        //自己与所有好友的最新一条聊天记录
        ArrayList<UnreadMessageList.UnreadMessage> AllerNewestMsgList = ChatMessagesDao.getInstance(mMsgActivity).queryAllNewest(mUserId);
        LogUtils.show(TAG, "getMessageListFromDB: AllerNewestMsgList.size() = " + AllerNewestMsgList.size());
        messageList.addAll(AllerNewestMsgList);
    }

    private void gotoGetUnreadMessageList() {
        if (!isPullRefresh) {
            llLoading.setVisibility(View.VISIBLE);
        }
        rvMsgPrivate.setVisibility(View.GONE);
        llEmptyData.setVisibility(View.GONE);
        llNetworkFailed.setVisibility(View.GONE);

        String mUserId = SPUtils.getString(mMsgActivity, GlobalConstants.userID, "");
        OkHttpUtils.get().url(GlobalConstants.URL_CHAT_MESSAGE_LIST)
                .addParams("userid", mUserId)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        LogUtils.show(TAG, "----获取未读消息列表失败!----");
                        refreshLayout.finishRefresh();
                        llLoading.setVisibility(View.GONE);
                        llEmptyData.setVisibility(View.GONE);
                        llNetworkFailed.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        LogUtils.show(TAG, "----获取未读消息列表成功!----response = [ " + response + " ]-----");
                        refreshLayout.finishRefresh();
                        try {
                            Gson gson = new Gson();
                            UnreadMessageList unreadMsgData = gson.fromJson(response, UnreadMessageList.class);

                            if (unreadMsgData == null || unreadMsgData.data == null || unreadMsgData.data.size() == 0) {
                                //Toast.makeText(mMsgActivity, "您没有未读消息", Toast.LENGTH_SHORT).show();
                                LogUtils.show(TAG, "onResponse: 您没有未读消息");
                            } else {
                                //首先去除服务器返回的 uncount=0的数据(未读数为零的数据本不应该返回的,因为服务器返回的就是未读消息列表)
                                for (int i = unreadMsgData.data.size() - 1; i >= 0; i--) {
                                    if (unreadMsgData.data.get(i).uncount <= 0) {
                                        unreadMsgData.data.remove(i);
                                    }
                                }

                                //去除重复的数据,(数据库有历史消息,并且有未读消息时就会重复,此时以未读消息为准)
                                ArrayList<Integer> repeatIndexs = new ArrayList<>();
                                for (int i = 0; i < messageList.size(); i++) {
                                    for (int j = 0; j < unreadMsgData.data.size(); j++) {
                                        if (TextUtils.equals(messageList.get(i).from_uid, unreadMsgData.data.get(j).from_uid)) {
                                            repeatIndexs.add(i);
                                        }
                                    }
                                }

                                for (int i = repeatIndexs.size() - 1; i >= 0; i--) {
                                    messageList.remove(repeatIndexs.get(i).intValue());
                                }

                                messageList.addAll(0, unreadMsgData.data);
                            }

                            if (messageList.size() > 0) {
                                rvMsgPrivate.setVisibility(View.VISIBLE);
                                llLoading.setVisibility(View.GONE);
                                llEmptyData.setVisibility(View.GONE);
                                llNetworkFailed.setVisibility(View.GONE);
                                initMessageView();
                            } else {
                                llLoading.setVisibility(View.GONE);
                                llEmptyData.setVisibility(View.VISIBLE);
                                llNetworkFailed.setVisibility(View.GONE);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                            LogUtils.show(TAG, "---解析未读消息列表出现异常---");
                            llLoading.setVisibility(View.GONE);
                            llEmptyData.setVisibility(View.GONE);
                            llNetworkFailed.setVisibility(View.VISIBLE);
                        }
                    }
                });
    }

    private void initMessageView() {
        rvMsgPrivate.setLayoutManager(new LinearLayoutManager(mMsgActivity));
        rvMsgPrivate.setAdapter(new MessageAdapter());
        LogUtils.show(TAG, "----setAdapter----");
    }

    private class MessageAdapter extends RecyclerView.Adapter<MessageHolder> {

        @Override
        public MessageHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = View.inflate(mMsgActivity, R.layout.item_my_message, null);
            MessageHolder messageHolder = new MessageHolder(view);
            return messageHolder;
        }

        @Override
        public void onBindViewHolder(MessageHolder holder, final int position) {

            Glide.with(MyMessagePrivateFragment.this).load(messageList.get(position).from_icon).into(holder.ivMsgUserIcon);
            holder.tvMsgUserName.setText(messageList.get(position).from_uidname);
            holder.tvMsgContent.setText(messageList.get(position).msg);

            if (messageList.get(position).uncount > 0) {
                holder.tvMsgUnreadMsg.setText(messageList.get(position).uncount + "");
                holder.tvMsgUnreadMsg.setVisibility(View.VISIBLE);
            } else {
                holder.tvMsgUnreadMsg.setVisibility(View.GONE);
            }


            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            Date sendDate = new Date(messageList.get(position).sendtime * 1000);
            String sendTime = formatter.format(sendDate);

            holder.tvMsgSendTime.setText(sendTime);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mMsgActivity, ChatActivity.class);
                    intent.putExtra("FRIEND_ID", messageList.get(position).from_uid);
                    intent.putExtra("FRIEND_NAME", messageList.get(position).from_uidname);
                    intent.putExtra("FRIEND_ICON", messageList.get(position).from_icon);
                    mMsgActivity.startActivity(intent);
                }
            });

            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    //Toast.makeText(mMsgActivity, "长按了" + position, Toast.LENGTH_SHORT).show();
                    return true;
                }
            });
        }

        @Override
        public int getItemCount() {
            return messageList.size();
        }
    }


    static class MessageHolder extends RecyclerView.ViewHolder {

        public View      itemView;
        public ImageView ivMsgUserIcon;
        public TextView  tvMsgUserName;
        public TextView  tvMsgContent;
        public TextView  tvMsgSendTime;
        public TextView  tvMsgUnreadMsg;

        public MessageHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            ivMsgUserIcon = (ImageView) itemView.findViewById(R.id.iv_msg_user_icon);
            tvMsgUserName = (TextView) itemView.findViewById(R.id.tv_msg_user_name);
            tvMsgContent = (TextView) itemView.findViewById(R.id.tv_msg_content);
            tvMsgSendTime = (TextView) itemView.findViewById(R.id.tv_msg_send_time);
            tvMsgUnreadMsg = (TextView) itemView.findViewById(R.id.tv_msg_unread_msg);
        }
    }

}
