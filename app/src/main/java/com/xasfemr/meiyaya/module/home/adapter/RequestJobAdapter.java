package com.xasfemr.meiyaya.module.home.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.xasfemr.meiyaya.R;
import com.xasfemr.meiyaya.module.home.activity.RecruitmentDetailActivity;
import com.xasfemr.meiyaya.module.home.activity.RequestJobDetailActivity;
import com.xasfemr.meiyaya.module.home.protocol.RecruitmentListProtocol;
import com.xasfemr.meiyaya.module.home.protocol.RequestJobListProtocol;

import java.util.ArrayList;

/**
 * Created by sen.luo on 2018/2/24.
 */

public class RequestJobAdapter extends BaseAdapter{


    private Context context;
    private ArrayList<RequestJobListProtocol> requestJobListProtocols;




    public RequestJobAdapter(Context context, ArrayList<RequestJobListProtocol> requestJobListProtocols) {
        this.context = context;
        this.requestJobListProtocols = requestJobListProtocols;
    }




    @Override
    public int getCount() {
        return requestJobListProtocols.size();
    }

    @Override
    public Object getItem(int i) {
        return requestJobListProtocols.get(i);
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
            covertView= LayoutInflater.from(context).inflate(R.layout.item_request_job,null);

            covertView.setTag(holder);
        }else {
            holder= (ViewHolder) covertView.getTag();
        }

        RequestJobListProtocol protocol=requestJobListProtocols.get(i);


        holder.tvName= (TextView) covertView.findViewById(R.id.tvName);
        holder.imgIcon= (ImageView) covertView.findViewById(R.id.imgIcon);
        holder.tvStatus= (TextView) covertView.findViewById(R.id.tvStatus);
        holder.tvYearWork= (TextView) covertView.findViewById(R.id.tvYearWork);
        holder.tvPrice= (TextView) covertView.findViewById(R.id.tvPrice);

        holder.imgApprove= (ImageView) covertView.findViewById(R.id.imgApprove);

        holder.tvName.setText(protocol.linkman);
        holder.tvStatus.setText(protocol.title);
//        holder.tvDistance.setText(protocol.workPlace);
        holder.tvYearWork.setText(protocol.yearWork+"工作经验");
        holder.tvPrice.setText(protocol.expect_salary);


//        if (protocol.is_approve!=null) {
//
//            if (protocol.is_approve.equals("1")) {
//                holder.imgApprove.setVisibility(View.VISIBLE);
//            } else {
//                holder.imgApprove.setVisibility(View.GONE);
//            }
//
//
//        }
        Glide.with(context).load(protocol.thumb).into(holder.imgIcon);

        covertView.setOnClickListener(view -> {
            context.startActivity(new Intent(context, RequestJobDetailActivity.class).putExtra("info_id",protocol.id));
        });

        return covertView;
    }


    class ViewHolder{
        TextView tvName,tvYearWork,tvStatus ,tvPrice;
        ImageView imgIcon,imgApprove;
    }


}
