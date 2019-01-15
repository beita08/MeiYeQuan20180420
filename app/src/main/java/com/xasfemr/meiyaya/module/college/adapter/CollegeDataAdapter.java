package com.xasfemr.meiyaya.module.college.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.xasfemr.meiyaya.R;
import com.xasfemr.meiyaya.module.college.activity.WebViewActivity;
import com.xasfemr.meiyaya.module.college.protocol.CollegeDataProtocol;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/11/28.
 */

public class CollegeDataAdapter extends RecyclerView.Adapter<CollegeDataAdapter.DataHolder>{

    private Context context;

    private ArrayList<CollegeDataProtocol> collegeDataProtocols;

    public CollegeDataAdapter(Context context,ArrayList<CollegeDataProtocol>collegeDataProtocolArrayList) {
        this.context = context;
        this.collegeDataProtocols=collegeDataProtocolArrayList;
    }

    @Override
    public DataHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(context, R.layout.item_college_data_info, null);
        DataHolder dataHolder = new DataHolder(view);
        return dataHolder;
    }

    @Override
    public void onBindViewHolder(DataHolder holder, int position) {

        CollegeDataProtocol collegeDataProtocol =collegeDataProtocols.get(position);

        Glide.with(context).load(collegeDataProtocol.images).into(holder.ivDataImg);
        holder.tvDataTitle.setText(collegeDataProtocol.title);
        holder.tvDataTime.setText(collegeDataProtocol.time);
        holder.tvDataScanNum.setText(collegeDataProtocol.hits);
        holder.tvDataDes.setText(collegeDataProtocol.digest);

        holder.layout_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, WebViewActivity.class)
                        .putExtra("url",collegeDataProtocol.url)
                        .putExtra("title",collegeDataProtocol.title)
                        .putExtra("image",collegeDataProtocol.images)
                        .putExtra("dev",collegeDataProtocol.digest)
                        .putExtra("url_id",collegeDataProtocol.id)
                        .putExtra("share_status","1")
                        .putExtra("news",true));

            }
        });



    }

    @Override
    public int getItemCount() {
        return collegeDataProtocols.size();
    }
    class DataHolder extends RecyclerView.ViewHolder{

        public ImageView ivDataImg;
        public TextView tvDataTitle;
        public TextView tvDataDes;
        public TextView tvDataTime;
        public TextView tvDataScanNum;
        RelativeLayout layout_item;

        public DataHolder(View itemView) {
            super(itemView);
            ivDataImg = (ImageView) itemView.findViewById(R.id.iv_data_img);
            tvDataTitle = (TextView) itemView.findViewById(R.id.tv_data_title);
            tvDataDes = (TextView) itemView.findViewById(R.id.tv_data_des);
            tvDataTime = (TextView) itemView.findViewById(R.id.tv_data_time);
            tvDataScanNum = (TextView) itemView.findViewById(R.id.tv_data_scan_num);
            layout_item= (RelativeLayout) itemView.findViewById(R.id.layout_item);
        }
    }

}


