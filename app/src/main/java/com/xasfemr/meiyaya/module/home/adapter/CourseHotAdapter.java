package com.xasfemr.meiyaya.module.home.adapter;

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
import com.xasfemr.meiyaya.R;
import com.xasfemr.meiyaya.module.college.protocol.HotCourseProtocol;
import com.xasfemr.meiyaya.neteasecloud.NELivePlayerActivity;
import com.xasfemr.meiyaya.module.player.VideoPlayerActivity;
import com.xasfemr.meiyaya.module.player.VideoPlayerIsUserActivity;

import java.util.ArrayList;


/**
 * 首页 直播 回放
 * 公用页面
 */

public class CourseHotAdapter extends RecyclerView.Adapter<CourseHotAdapter.ViewHolder>{

    private Context context;
    private ArrayList<HotCourseProtocol> hotCourseProtocols;


    public CourseHotAdapter(Context context, ArrayList<HotCourseProtocol> courseProtocolList) {
        this.context = context;
        this.hotCourseProtocols = courseProtocolList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View mainView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_live_content_single, null);
        return new ViewHolder(mainView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        HotCourseProtocol courseProtocol = hotCourseProtocols.get(position);

        if (courseProtocol.coursename!=null){
            holder.tvLiveTitle.setText(courseProtocol.coursename);
        }

        if (!TextUtils.isEmpty(courseProtocol.cover)){
            Glide.with(context).load(courseProtocol.cover).into(holder.ivLive);
        }
        if (!TextUtils.isEmpty(courseProtocol.icon)){
            Glide.with(context).load(courseProtocol.icon).into(holder.ivUserIcon);

        }
        if (courseProtocol.username!=null){
            holder.tvUserName.setText(courseProtocol.username);

        }
        if (courseProtocol.view !=null){
            holder.tvNums.setText(courseProtocol.view);


        }
        if (courseProtocol.title!=null){
            holder.tvCaption.setText(courseProtocol.title);

        }


        if (courseProtocol.status==1){
            holder.tvLiveTime.setText("开课中");
            holder.tvLiveTime.setBackgroundResource(R.drawable.live_time_bg);
        }else{
            holder.tvLiveTime.setText(courseProtocol.duration);
            holder.tvLiveTime.setBackgroundResource(R.drawable.course_time_bg);
        }



        holder.rl_live_screenshot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (courseProtocol.status == 1){
                    Intent intent =new Intent();
                    //intent.putExtra("media_type", "livestream");
                    //intent.putExtra("decode_type", "software");
                    intent.putExtra("videoPath", courseProtocol.addr.rtmpPullUrl);
                    intent.putExtra("cid", courseProtocol.cid); //直播间ID
                    intent.putExtra("icon", courseProtocol.icon);
                    intent.putExtra("user_name", courseProtocol.username);
                    intent.putExtra("user_id", courseProtocol.userid);
                    intent.setClass(context, NELivePlayerActivity.class);
                    context.startActivity(intent);

                }else if(courseProtocol.status == 2){
                    Intent intent =new Intent();
                    //intent.putExtra("media_type", "videoondemand");
                    //intent.putExtra("decode_type", "software");
                    intent.putExtra("user_name", courseProtocol.username);
                    intent.putExtra("user_id", courseProtocol.userid);
                    intent.putExtra("coursename", courseProtocol.coursename);
                    intent.putExtra("des", courseProtocol.des);
                    intent.putExtra("view", courseProtocol.view);
                    intent.putExtra("icon", courseProtocol.icon);
                    intent.putExtra("videoPath", courseProtocol.addr.rtmpPullUrl);
                    intent.putExtra("cid", courseProtocol.cid); //直播间ID
                    intent.putExtra("video_id", courseProtocol.vid); //直播间ID

                    if (courseProtocol.ismy.equals("0")){  //0 自营
                        intent.setClass(context, VideoPlayerActivity.class);
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


        return hotCourseProtocols.size();

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
