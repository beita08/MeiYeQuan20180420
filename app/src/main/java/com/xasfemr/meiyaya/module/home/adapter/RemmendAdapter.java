package com.xasfemr.meiyaya.module.home.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.xasfemr.meiyaya.R;
import com.xasfemr.meiyaya.module.college.activity.WebViewActivity;
import com.xasfemr.meiyaya.module.college.protocol.CollegeEventProtocol;
import com.xasfemr.meiyaya.module.college.protocol.HomeInterceptionProtocol;
import com.xasfemr.meiyaya.module.home.activity.InstrumentTransferDetailActivity;
import com.xasfemr.meiyaya.module.home.activity.RecruitmentDetailActivity;
import com.xasfemr.meiyaya.module.home.activity.RequestJobDetailActivity;
import com.xasfemr.meiyaya.utils.LocationUtils;
import com.xasfemr.meiyaya.utils.MapDistanceUtils;
import com.xasfemr.meiyaya.utils.ToastUtil;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Administrator on 2017/11/28.
 */


public class RemmendAdapter extends RecyclerView.Adapter<RemmendAdapter.EventHolder> {
    private Context                         context;
    private ArrayList<HomeInterceptionProtocol> recommendProtocolList;



    public RemmendAdapter(Context context, ArrayList<HomeInterceptionProtocol> recommendProtocolList) {
        this.context = context;
        this.recommendProtocolList = recommendProtocolList;


    }

    @Override
    public EventHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(context, R.layout.item_home_remmend, null);
        EventHolder eventHolder = new EventHolder(view);

        return eventHolder;
    }

    @Override
    public void onBindViewHolder(EventHolder holder, int position) {
        HomeInterceptionProtocol protocol=recommendProtocolList.get(position);

        Glide.with(context).load(protocol.thumb).into(holder.imgIcon);



        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        int width = wm.getDefaultDisplay().getWidth();
        int height = wm.getDefaultDisplay().getHeight();

        holder.layoutItem.setLayoutParams(new LinearLayout.LayoutParams(width, ViewGroup.LayoutParams.WRAP_CONTENT));


        //如果distance为空，则用返回经纬度计算距离，经纬度为空 显示 定位失败
        if (!TextUtils.isEmpty(protocol.distance)){
            holder.distanceGone.setVisibility(View.VISIBLE);
            holder.tvDistance.setText(protocol.distance);
        }else {
            holder.distanceGone.setVisibility(View.GONE);
        }

        if (!protocol.catid.equals("6")&&protocol.is_approve!=null){
            if (protocol.is_approve.equals("1")){
                holder.imgApprove.setVisibility(View.VISIBLE);
            }else {
                holder.imgApprove.setVisibility(View.GONE);
            }

        }else {
            holder.imgApprove.setVisibility(View.GONE);
        }


        switch (protocol.catid){
            case "6": //求职
                holder.tvTitle.setText(protocol.linkman);
                holder.tvStatus.setText(protocol.position);
                holder.tvAddress.setText(protocol.yearWork+"工作经验");

                if (protocol.expect_salary.equals("-1")){
                    holder.tvPrice.setText("面议");
                }else {
                    holder.tvPrice.setText(protocol.expect_salary); //期望薪资

                }
                holder.distanceGone.setVisibility(View.GONE);

                break;

            case "5":  //招聘
                holder.tvTitle.setText(protocol.jobName);
                holder.tvStatus.setText(protocol.title);
//                holder.tvDistance.setText(protocol.distance+"公里");
                holder.tvAddress.setText(protocol.workPlace);
                holder.tvPrice.setText(protocol.salary);
                holder.distanceGone.setVisibility(View.VISIBLE);
                break;

            case "3":  // 仪器转让
                holder.tvTitle.setText(protocol.title);
                holder.tvStatus.setText(protocol.company);
//                holder.tvDistance.setText(protocol.distance+"公里");
                holder.distanceGone.setVisibility(View.VISIBLE);
//                holder.tvDistance.setVisibility(View.GONE);
//                holder.tvDate.setVisibility(View.VISIBLE);
//                holder.tvDate.setText(protocol.postdate);

                if (!TextUtils.isEmpty(protocol.business)){
                    holder.tvAddress.setText(protocol.business);
                }else {
                    holder.tvAddress.setText("西安");
                }



                if (protocol.price.equals("-1")){
                    holder.tvPrice.setText("面议");
                }else {
                    holder.tvPrice.setText(protocol.price+"元");
                }

                break;

        }



        holder.layoutItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                switch (protocol.catid){
                    case "6": //求职
                        context.startActivity(new Intent(context, RequestJobDetailActivity.class).putExtra("info_id",protocol.id));
                        break;

                    case "5":
                        context.startActivity(new Intent(context, RecruitmentDetailActivity.class).putExtra("info_id",protocol.id));
                        break;

                    case "3":
                        context.startActivity(new Intent(context, InstrumentTransferDetailActivity.class).putExtra("info_id",protocol.id));
                        break;
                }

            }
        });



    }

    @Override
    public int getItemCount() {
        return recommendProtocolList.size();
    }

    class EventHolder extends RecyclerView.ViewHolder {

        TextView tvTitle,tvAddress,tvDistance,tvStatus ,tvPrice,tvDate;
        ImageView imgIcon,imgApprove;
        LinearLayout layoutItem,distanceGone;

        public EventHolder(View itemView) {
            super(itemView);
            layoutItem= (LinearLayout) itemView.findViewById(R.id.layoutItem);
            distanceGone= (LinearLayout) itemView.findViewById(R.id.distanceGone);
           tvTitle= (TextView) itemView.findViewById(R.id.tvTitle);
           imgIcon= (ImageView) itemView.findViewById(R.id.imgIcon);
           tvStatus= (TextView) itemView.findViewById(R.id.tvStatus);
           tvAddress= (TextView) itemView.findViewById(R.id.tvAddress);
           tvDistance= (TextView) itemView.findViewById(R.id.tvDistance);
           tvPrice= (TextView) itemView.findViewById(R.id.tvPrice);
            imgApprove= (ImageView) itemView.findViewById(R.id.imgApprove);
            tvDate= (TextView) itemView.findViewById(R.id.tvDate);


        }
    }
}


