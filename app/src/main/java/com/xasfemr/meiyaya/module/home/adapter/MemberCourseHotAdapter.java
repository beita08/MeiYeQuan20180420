package com.xasfemr.meiyaya.module.home.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.xasfemr.meiyaya.R;
import com.xasfemr.meiyaya.module.home.protocol.MemberCourseHotProtocol;
import com.xasfemr.meiyaya.neteasecloud.NELivePlayerActivity;
import com.xasfemr.meiyaya.module.player.VideoPlayerActivity;
import com.xasfemr.meiyaya.module.player.VideoPlayerIsUserActivity;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/11/29.
 */

public class MemberCourseHotAdapter extends RecyclerView.Adapter<MemberCourseHotAdapter.ViewHolder>{

    private Context context;
    private ArrayList<MemberCourseHotProtocol> memberCourseHotProtocols;

    public MemberCourseHotAdapter(Context context,ArrayList<MemberCourseHotProtocol> memberCourseHotProtocol) {
        this.context = context;
        this.memberCourseHotProtocols = memberCourseHotProtocol;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(context, R.layout.item_live_content_single, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        MemberCourseHotProtocol memberCourseHotProtocol =memberCourseHotProtocols.get(position);
        if (memberCourseHotProtocol.coursename!=null){
            holder.tvLiveTitleTag.setText(memberCourseHotProtocol.coursename);
        }

        if (!TextUtils.isEmpty(memberCourseHotProtocol.cover)){
            Glide.with(context).load(memberCourseHotProtocol.cover).into(holder.ivLiveScreenshot);
        }
        if (memberCourseHotProtocol.icon!=null){
            Glide.with(context).load(memberCourseHotProtocol.icon).into(holder.ivUserIcon);

        }
        if (memberCourseHotProtocol.username!=null){
            holder.tvUserName.setText(memberCourseHotProtocol.username);

        }
        if (memberCourseHotProtocol.view !=null){
            holder.tvPeopleNumber.setText(memberCourseHotProtocol.view);


        }
        if (memberCourseHotProtocol.title!=null){
            holder.tvUserDes.setText(memberCourseHotProtocol.title);

        }

        if (memberCourseHotProtocol.status==1){
            holder.tvLiveTime.setText("开课中");
        }else {
            holder.tvLiveTime.setText(memberCourseHotProtocol.duration);
        }


        holder.rl_live_screenshot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (memberCourseHotProtocol.status == 1){
                    Intent intent =new Intent();
                    //intent.putExtra("media_type", "livestream");
                    //intent.putExtra("decode_type", "software");
                    intent.putExtra("videoPath", memberCourseHotProtocol.addr.rtmpPullUrl);
                    intent.putExtra("cid", memberCourseHotProtocol.cid); //直播间ID
                    intent.putExtra("icon", memberCourseHotProtocol.icon);
                    intent.putExtra("user_name", memberCourseHotProtocol.username);
                    intent.putExtra("user_id", memberCourseHotProtocol.userid);
                    intent.setClass(context, NELivePlayerActivity.class);
                    context.startActivity(intent);
                }else if(memberCourseHotProtocol.status == 2){
                    Intent intent =new Intent();
                    intent.putExtra("videoPath", memberCourseHotProtocol.addr.rtmpPullUrl);
                    //intent.putExtra("media_type", "videoondemand");
                    //intent.putExtra("decode_type", "software");
                    intent.putExtra("user_name", memberCourseHotProtocol.username);
                    intent.putExtra("user_id", memberCourseHotProtocol.userid);
                    intent.putExtra("coursename", memberCourseHotProtocol.coursename);
                    intent.putExtra("des", memberCourseHotProtocol.des);
                    intent.putExtra("view", memberCourseHotProtocol.view);
                    intent.putExtra("icon", memberCourseHotProtocol.icon);
                    intent.putExtra("video_id", memberCourseHotProtocol.vid);
                    intent.putExtra("media_type", "videoondemand");

                    if (memberCourseHotProtocol.ismy.equals("0")){ //0 自营 1第三方录制
                        intent.setClass(context,VideoPlayerActivity.class);
                    }else {
                        intent.setClass(context, VideoPlayerIsUserActivity.class);
                    }
                    context.startActivity(intent);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return memberCourseHotProtocols.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        public ImageView ivUserIcon;
        public TextView tvUserName;
        public TextView  tvPeopleNumber;
        public TextView  tvUserDes;
        public ImageView ivLiveScreenshot;
        public TextView  tvLiveTitleTag;
        public TextView  tvLiveTime;
        RelativeLayout rl_live_screenshot;

        public ViewHolder(View itemView) {
            super(itemView);
            ivUserIcon = (ImageView) itemView.findViewById(R.id.iv_user_icon);
            tvUserName = (TextView) itemView.findViewById(R.id.tv_user_name);
            tvPeopleNumber = (TextView) itemView.findViewById(R.id.tv_people_number);
            tvUserDes = (TextView) itemView.findViewById(R.id.tv_user_des);
            ivLiveScreenshot = (ImageView) itemView.findViewById(R.id.iv_live_screenshot);
            tvLiveTitleTag = (TextView) itemView.findViewById(R.id.tv_live_title_tag);
            tvLiveTime = (TextView) itemView.findViewById(R.id.tv_live_time);
            rl_live_screenshot= (RelativeLayout) itemView.findViewById(R.id.rl_live_screenshot);
        }
    }
}
