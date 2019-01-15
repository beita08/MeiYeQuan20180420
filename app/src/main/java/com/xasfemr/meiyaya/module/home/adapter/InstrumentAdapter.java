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
import com.xasfemr.meiyaya.module.home.activity.InstrumentTransferDetailActivity;
import com.xasfemr.meiyaya.module.home.activity.RecruitmentDetailActivity;
import com.xasfemr.meiyaya.module.home.protocol.InstrumentListProtocol;
import com.xasfemr.meiyaya.module.home.protocol.RecruitmentListProtocol;
import com.xasfemr.meiyaya.utils.LocationUtils;
import com.xasfemr.meiyaya.utils.MapDistanceUtils;

import java.util.ArrayList;

/**
 * Created by sen.luo on 2018/2/24.
 */

public class InstrumentAdapter extends BaseAdapter{


    private Context context;
    private ArrayList<InstrumentListProtocol> instrumentListProtocolList;

    public InstrumentAdapter(Context context, ArrayList<InstrumentListProtocol> instrumentListProtocols) {
        this.context = context;
        this.instrumentListProtocolList = instrumentListProtocols;
    }

    @Override
    public int getCount() {
        return instrumentListProtocolList.size();
    }

    @Override
    public Object getItem(int i) {
        return instrumentListProtocolList.get(i);
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

        InstrumentListProtocol protocol=instrumentListProtocolList.get(i);


        holder.tvTitle= (TextView) covertView.findViewById(R.id.tvTitle);
        holder.imgIcon= (ImageView) covertView.findViewById(R.id.imgIcon);
        holder.tvStatus= (TextView) covertView.findViewById(R.id.tvStatus);
        holder.tvAddress= (TextView) covertView.findViewById(R.id.tvAddress);
        holder.tvDistance= (TextView) covertView.findViewById(R.id.tvDistance);
        holder.tvPrice= (TextView) covertView.findViewById(R.id.tvPrice);
        holder.tvDate= (TextView) covertView.findViewById(R.id.tvDate);
        holder.imgApprove= (ImageView) covertView.findViewById(R.id.imgApprove);
        holder.distanceGone= (LinearLayout) covertView.findViewById(R.id.distanceGone);

        holder.tvTitle.setText(protocol.title);
        holder.tvStatus.setText(protocol.company);

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



        if (protocol.is_approve.equals("1")){
            holder.imgApprove.setVisibility(View.VISIBLE);
        }else {
            holder.imgApprove.setVisibility(View.GONE);
        }


//        holder.tvDistance.setVisibility(View.GONE);
//        holder.tvDate.setVisibility(View.VISIBLE);
//        holder.tvDate.setText(protocol.postdate);

        //如果distance为空，则用返回经纬度计算距离，经纬度为空 显示 定位失败
        if (TextUtils.isEmpty(protocol.distance)){
            holder.distanceGone.setVisibility(View.GONE);

//            if (TextUtils.isEmpty(protocol.latitude)||TextUtils.isEmpty(protocol.longitude)){
//
//                holder.tvDistance.setText("未知");
//            }else {
//
//                LocationUtils.initLocation(context);
//                if (Double.toString(LocationUtils.longitude).equals("0.0") || Double.toString(LocationUtils.longitude).equals("0.0")) {
//                    holder.tvDistance.setText("未知");
//
//                }else {
//                    holder.tvDistance.setText(MapDistanceUtils.getInstance().getLongDistance(
//                            Double.valueOf(protocol.longitude),Double.valueOf(protocol.latitude),LocationUtils.longitude,LocationUtils.latitude));
//                }
//
//
//            }

        }else {
            holder.tvDistance.setText(protocol.distance);
        }


        Glide.with(context).load(protocol.thumb).into(holder.imgIcon);

        covertView.setOnClickListener(view -> {
            context.startActivity(new Intent(context, InstrumentTransferDetailActivity.class).putExtra("info_id",protocol.id));
        });

        return covertView;
    }


   static class ViewHolder{
        TextView tvTitle,tvAddress,tvDistance,tvStatus ,tvPrice,tvDate;
        ImageView imgIcon,imgApprove;
        LinearLayout distanceGone;
    }
}
