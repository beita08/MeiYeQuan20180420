package com.xasfemr.meiyaya.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.xasfemr.meiyaya.R;
import com.xasfemr.meiyaya.activity.ChatActivity;
import com.xasfemr.meiyaya.activity.MyMessageActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyMessagePublicFragment extends BaseFragment {

    private MyMessageActivity mMsgActivity;
    private RecyclerView      rvMsgPublic;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMsgActivity = (MyMessageActivity) getActivity();
    }

    @Override
    public View initView() {
        View view = View.inflate(mMsgActivity, R.layout.fragment_my_message_public, null);
        rvMsgPublic = (RecyclerView) view.findViewById(R.id.rv_msg_public);

        rvMsgPublic.setLayoutManager(new LinearLayoutManager(mMsgActivity));
        rvMsgPublic.setAdapter(new MessageAdapter());
        return view;
    }

    @Override
    public void initData() {

    }

    private class MessageAdapter extends RecyclerView.Adapter<MessageHolder> {

        @Override
        public MessageHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = View.inflate(mMsgActivity, R.layout.item_my_message, null);
            MessageHolder messageHolder = new MessageHolder(view);
            return messageHolder;
        }

        @Override
        public void onBindViewHolder(MessageHolder holder, int position) {
            holder.tvMsgUserName.setText("美页圈官方");
            holder.ivMsgUserIcon.setImageResource(R.drawable.meiyaya_oval);
            holder.tvMsgContent.setText("您有红包待领取!");

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mMsgActivity, ChatActivity.class);
                    mMsgActivity.startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return 1000;
        }
    }

    static class MessageHolder extends RecyclerView.ViewHolder {

        public ImageView ivMsgUserIcon;
        public TextView  tvMsgUserName;
        public TextView  tvMsgContent;
        public TextView  tvMsgSendTime;

        public MessageHolder(View itemView) {
            super(itemView);
            ivMsgUserIcon = (ImageView) itemView.findViewById(R.id.iv_msg_user_icon);
            tvMsgUserName = (TextView) itemView.findViewById(R.id.tv_msg_user_name);
            tvMsgContent = (TextView) itemView.findViewById(R.id.tv_msg_content);
            tvMsgSendTime = (TextView) itemView.findViewById(R.id.tv_msg_send_time);
        }
    }
}
