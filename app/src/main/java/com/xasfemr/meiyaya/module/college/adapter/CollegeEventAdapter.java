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
import com.xasfemr.meiyaya.module.college.protocol.CollegeEventProtocol;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/11/28.
 */


public class CollegeEventAdapter extends RecyclerView.Adapter<CollegeEventAdapter.EventHolder> {
    private Context                         context;
    private ArrayList<CollegeEventProtocol> collegeEventProtocols;

    public CollegeEventAdapter(Context context, ArrayList<CollegeEventProtocol> collegeEventProtocolArrayList) {
        this.context = context;
        this.collegeEventProtocols = collegeEventProtocolArrayList;
    }

    @Override
    public EventHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(context, R.layout.item_college_event_info, null);
        EventHolder eventHolder = new EventHolder(view);

        return eventHolder;
    }

    @Override
    public void onBindViewHolder(EventHolder holder, int position) {
        CollegeEventProtocol collegeEventProtocol = collegeEventProtocols.get(position);
        //        holder.tvEventSlogan.setText(collegeEventProtocol.title);
        Glide.with(context).load(collegeEventProtocol.bgimg).into(holder.ivEventSloganBg);
        holder.layout_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, WebViewActivity.class)
                        .putExtra("url", collegeEventProtocol.url)
                        .putExtra("title", collegeEventProtocol.title)
                        .putExtra("url_id",collegeEventProtocol.id)
                        .putExtra("image",collegeEventProtocol.bgimg)
                        .putExtra("share_status","1")
                        .putExtra("news", true));
            }
        });
    }

    @Override
    public int getItemCount() {
        return collegeEventProtocols.size();
    }

    class EventHolder extends RecyclerView.ViewHolder {

        public ImageView ivEventSloganBg;
        public TextView  tvEventSlogan;
        RelativeLayout layout_item;

        public EventHolder(View itemView) {
            super(itemView);
            ivEventSloganBg = (ImageView) itemView.findViewById(R.id.iv_event_slogan_bg);
            tvEventSlogan = (TextView) itemView.findViewById(R.id.tv_event_slogan);
            layout_item = (RelativeLayout) itemView.findViewById(R.id.layout_item);
        }
    }
}


