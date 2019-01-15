package com.xasfemr.meiyaya.module.home.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.xasfemr.meiyaya.R;
import com.xasfemr.meiyaya.module.home.activity.RecruitmentDetailActivity;
import com.xasfemr.meiyaya.module.home.protocol.RecruitmentListProtocol;

import java.util.ArrayList;

/**
 * Created by sen.luo on 2018/2/24.
 */

public class RecruitmentAdapter extends BaseAdapter{


    private Context context;
    private ArrayList<RecruitmentListProtocol> recruitmentListProtocols;

    public RecruitmentAdapter(Context context, ArrayList<RecruitmentListProtocol> recruitmentListProtocols) {
        this.context = context;
        this.recruitmentListProtocols = recruitmentListProtocols;
    }

    @Override
    public int getCount() {
        return recruitmentListProtocols.size();
    }

    @Override
    public Object getItem(int i) {
        return recruitmentListProtocols.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View covertView, ViewGroup viewGroup) {

        ViewHolder holder =null;

        if (holder==null){
            holder=new ViewHolder();
            covertView= LayoutInflater.from(context).inflate(R.layout.item_home_all_see,null);

            covertView.setTag(holder);
        }else {
            holder= (ViewHolder) covertView.getTag();
        }

        RecruitmentListProtocol protocol=recruitmentListProtocols.get(i);


        holder.tvTitle= (TextView) covertView.findViewById(R.id.tvTitle);
        holder.imgIcon= (ImageView) covertView.findViewById(R.id.imgIcon);
        holder.tvStatus= (TextView) covertView.findViewById(R.id.tvStatus);
        holder.tvAddress= (TextView) covertView.findViewById(R.id.tvAddress);
        holder.tvDistance= (TextView) covertView.findViewById(R.id.tvDistance);
        holder.tvPrice= (TextView) covertView.findViewById(R.id.tvPrice);
        holder.imgApprove= (ImageView) covertView.findViewById(R.id.imgApprove);
        holder.distanceGone= (LinearLayout) covertView.findViewById(R.id.distanceGone);


        if (protocol.is_approve!=null) {

            if (protocol.is_approve.equals("1")) {
                holder.imgApprove.setVisibility(View.VISIBLE);
            } else {
                holder.imgApprove.setVisibility(View.GONE);
            }
        }

        holder.tvTitle.setText(protocol.jobName);
        holder.tvStatus.setText(protocol.companyName);
//        holder.tvDistance.setText(protocol.workPlace);
        holder.tvAddress.setText(protocol.workPlace);
        holder.tvPrice.setText(protocol.salary);

        if (TextUtils.isEmpty(protocol.distance)) {
            holder.distanceGone.setVisibility(View.GONE);

        }else {
            holder.tvDistance.setText(protocol.distance);
        }




        Glide.with(context).load(protocol.thumb).into(holder.imgIcon);

        covertView.setOnClickListener(view -> {
            context.startActivity(new Intent(context, RecruitmentDetailActivity.class).putExtra("info_id",protocol.id));
        });

        return covertView;
    }


    class ViewHolder{
        TextView tvTitle,tvAddress,tvDistance,tvStatus ,tvPrice;
        ImageView imgIcon,imgApprove;
        LinearLayout distanceGone;
    }
}
