package com.xasfemr.meiyaya.module.college.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.xasfemr.meiyaya.R;
import com.xasfemr.meiyaya.activity.LoginActivity;
import com.xasfemr.meiyaya.global.GlobalConstants;
import com.xasfemr.meiyaya.module.college.activity.ExcellentCourseActivity;
import com.xasfemr.meiyaya.module.college.protocol.ExcellentListProtocol;

import com.xasfemr.meiyaya.utils.SPUtils;

import java.util.ArrayList;


/**
 * 精品课程适配器
 * Created by sen.luo on 2017/11/23.
 */

public class ExcellentAdapter extends RecyclerView.Adapter<ExcellentAdapter.ViewHolder>{

    private Context context;
    private ArrayList<ExcellentListProtocol> excellentListProtocols;


    public ExcellentAdapter(Context context, ArrayList<ExcellentListProtocol> excellentListProtocolArrayList) {
        this.context = context;
        this.excellentListProtocols = excellentListProtocolArrayList;
    }

    @Override
    public ExcellentAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View mainView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_excellent, null);
        return new ExcellentAdapter.ViewHolder(mainView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ExcellentListProtocol excellentListProtocol = excellentListProtocols.get(position);

            Glide.with(context).load(excellentListProtocol.cover).into(holder.ivLive);

            holder.tvUserName.setText(excellentListProtocol.title);
            holder.tvNums.setText(excellentListProtocol.view);

            holder.tvCaption.setText(excellentListProtocol.fee);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!TextUtils.isEmpty(SPUtils.getString(context, GlobalConstants.userID,""))){
                    context.startActivity(new Intent(context, ExcellentCourseActivity.class).putExtra("protocol",excellentListProtocol));
                }else {
                    context.startActivity(new Intent(context, LoginActivity.class));
                }


            }
        });



    }

    @Override
    public int getItemCount() {
        return excellentListProtocols.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        ImageView ivLive;
        TextView tvUserName;
        TextView tvCaption;  //说明
        TextView tvNums;   //点播数量

        public ViewHolder(View itemView) {
            super(itemView);
            ivLive= (ImageView) itemView.findViewById(R.id.iv_live_screenshot);
            tvUserName= (TextView) itemView.findViewById(R.id.tv_user_name);
            tvCaption= (TextView) itemView.findViewById(R.id.tv_user_des);
            tvNums= (TextView) itemView.findViewById(R.id.tv_people_number);
        }
    }
}
