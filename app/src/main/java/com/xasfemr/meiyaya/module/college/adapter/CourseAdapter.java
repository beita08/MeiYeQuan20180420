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
import com.xasfemr.meiyaya.module.college.protocol.CourseProtocol;
import com.xasfemr.meiyaya.neteasecloud.NELivePlayerActivity;
import com.xasfemr.meiyaya.utils.SPUtils;
import com.xasfemr.meiyaya.utils.ToastUtil;

import java.util.ArrayList;


/**
 * 直播
 * Created by sen.luo on 2017/11/23.
 */

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.ViewHolder>{

    private Context context;
    private ArrayList<CourseProtocol> courseProtocolListList;
    private boolean isAll;


    public CourseAdapter(Context context, ArrayList<CourseProtocol> courseProtocolList,boolean is_all) {
        this.context = context;
        this.courseProtocolListList = courseProtocolList;
            this.isAll=is_all;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View mainView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_live_content_single, null);
        return new ViewHolder(mainView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        CourseProtocol courseProtocol = courseProtocolListList.get(position);

        if (courseProtocol.coursename!=null){
            holder.tvLiveTitle.setText(courseProtocol.coursename);
        }

        if (!TextUtils.isEmpty(courseProtocol.cover)){
            Glide.with(context).load(courseProtocol.cover).into(holder.ivLive);
        }
        if (courseProtocol.icon!=null){
            Glide.with(context).load(courseProtocol.icon).into(holder.ivUserIcon);

            Glide.with(context).load(courseProtocol.icon).apply(new RequestOptions().placeholder(R.drawable.meiyaya_logo_round_gray)).into(holder.ivUserIcon);

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
        }



        holder.rl_live_screenshot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (TextUtils.isEmpty(SPUtils.getString(context, GlobalConstants.userID,""))){
                    ToastUtil.showShort(context,"您未登录，请先去登录");
                    return;
                }

                Intent intent =new Intent();
//                intent.putExtra("media_type", "livestream");
//                intent.putExtra("decode_type", "software");
                intent.putExtra("videoPath", courseProtocol.addr.rtmpPullUrl);
                intent.putExtra("cid", courseProtocol.cid); //直播间ID
                intent.putExtra("icon", courseProtocol.icon);
                intent.putExtra("user_id", courseProtocol.userid);
                intent.putExtra("user_name", courseProtocol.username);
                intent.setClass(context, NELivePlayerActivity.class);
                context.startActivity(intent);

            }
        });
        holder.ivUserIcon.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, UserPagerActivity.class).putExtra("LOOK_USER_ID",courseProtocol.userid));
            }
        });


    }

    @Override
    public int getItemCount() {

        if (isAll){
            return courseProtocolListList.size();

        }else if (courseProtocolListList.size()>4){
            return 4;
        }

        return courseProtocolListList.size();

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
