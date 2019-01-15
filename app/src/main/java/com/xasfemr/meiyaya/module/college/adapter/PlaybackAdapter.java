package com.xasfemr.meiyaya.module.college.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.xasfemr.meiyaya.R;
import com.xasfemr.meiyaya.activity.UserPagerActivity;
import com.xasfemr.meiyaya.global.GlobalConstants;
import com.xasfemr.meiyaya.module.college.protocol.PlaybackProtocol;
import com.xasfemr.meiyaya.module.player.VideoPlayerActivity;
import com.xasfemr.meiyaya.module.player.VideoPlayerIsUserActivity;
import com.xasfemr.meiyaya.utils.SPUtils;
import com.xasfemr.meiyaya.utils.ToastUtil;

import java.util.ArrayList;


/**
 * 回放适配器
 * Created by sen.luo on 2017/11/23.
 */

public class PlaybackAdapter  extends RecyclerView.Adapter<PlaybackAdapter.ViewHolder>{

    private Context context;
    private ArrayList<PlaybackProtocol> PlaybackProtocolList;
    private boolean isAll;


    public PlaybackAdapter(Context context, ArrayList<PlaybackProtocol> courseProtocolList,boolean is_all) {
        this.context = context;
        this.PlaybackProtocolList = courseProtocolList;
        this.isAll=is_all;
    }

    @Override
    public PlaybackAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View mainView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_playerback, null);
        return new PlaybackAdapter.ViewHolder(mainView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        PlaybackProtocol playbackProtocol = PlaybackProtocolList.get(position);

        if (!TextUtils.isEmpty(playbackProtocol.cover)){
            Glide.with(context).load(playbackProtocol.cover).into(holder.ivLive);
        }
        if (playbackProtocol.icon!=null){
            Glide.with(context).load(playbackProtocol.icon).apply(new RequestOptions().placeholder(R.drawable.meiyaya_logo_round_gray)).into(holder.ivUserIcon);

        }
        if (playbackProtocol.username!=null){
            holder.tvUserName.setText(playbackProtocol.username);

        }
        if (playbackProtocol.view !=null){
            holder.tvNums.setText(playbackProtocol.view);

        if (playbackProtocol.coursename!=null){
            holder.tvLiveTitle.setText(playbackProtocol.coursename);
        }

        }
        if (playbackProtocol.title!=null){
            holder.tvCaption.setText(playbackProtocol.title);

        }

        if (playbackProtocol.status1!=null){
            holder.tvLiveTime.setText(playbackProtocol.duration);
        }

        holder.rl_live_screenshot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (TextUtils.isEmpty(SPUtils.getString(context, GlobalConstants.userID,""))){
                    ToastUtil.showShort(context,"您未登录，请先去登录");
                    return;
                }


                Intent intent =new Intent();
                intent.putExtra("videoPath", playbackProtocol.addr.rtmpPullUrl);
                intent.putExtra("media_type", "videoondemand");
                intent.putExtra("user_name", playbackProtocol.username);
                intent.putExtra("user_id", playbackProtocol.userid);
                intent.putExtra("coursename", playbackProtocol.coursename);
                intent.putExtra("des", playbackProtocol.des);
                intent.putExtra("view", playbackProtocol.view);
                intent.putExtra("icon", playbackProtocol.icon);
                intent.putExtra("video_id", playbackProtocol.vid);
//                intent.putExtra("media_type", "videoondemand");
//                intent.putExtra("decode_type", "software");

                if (playbackProtocol.ismy.equals("0")){
                    intent.setClass(context,VideoPlayerActivity.class);
                }else {
                    intent.setClass(context, VideoPlayerIsUserActivity.class);

                }
                context.startActivity(intent);
            }
        });


        holder.ivUserIcon.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, UserPagerActivity.class).putExtra("LOOK_USER_ID",playbackProtocol.userid));
            }
        });

    }

    @Override
    public int getItemCount() {

        if (isAll){
            return PlaybackProtocolList.size();
        }else if (PlaybackProtocolList.size()>4){
            return 4;
        }


        return PlaybackProtocolList.size();

    }

    class ViewHolder extends RecyclerView.ViewHolder{
        TextView tvLiveTitle;
        TextView tvLiveTime;
        ImageView ivLive;
        ImageView ivUserIcon;
        TextView tvUserName;
        TextView tvCaption;  //说明
        TextView tvNums;   //点播数量
        RelativeLayout rl_live_screenshot;

        public ViewHolder(View itemView) {
            super(itemView);
            ivLive= (ImageView) itemView.findViewById(R.id.iv_live_screenshot);
            ivUserIcon= (ImageView) itemView.findViewById(R.id.iv_user_icon);
            tvLiveTime= (TextView) itemView.findViewById(R.id.tv_live_time);
            tvLiveTitle= (TextView) itemView.findViewById(R.id.tv_live_title_tag);
            tvUserName= (TextView) itemView.findViewById(R.id.tv_user_name);
            tvCaption= (TextView) itemView.findViewById(R.id.tv_user_des);
            tvNums= (TextView) itemView.findViewById(R.id.tv_people_number);
            rl_live_screenshot= (RelativeLayout) itemView.findViewById(R.id.rl_live_screenshot);
        }
    }
}
