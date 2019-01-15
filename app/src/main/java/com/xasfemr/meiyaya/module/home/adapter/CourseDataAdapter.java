package com.xasfemr.meiyaya.module.home.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.xasfemr.meiyaya.R;
import com.xasfemr.meiyaya.module.home.activity.CourseDetailActivity;
import com.xasfemr.meiyaya.module.home.protocol.CourseDataProtocol;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/11/29.
 */

public class CourseDataAdapter extends RecyclerView.Adapter<CourseDataAdapter.ViewHolder> {

    private Context                       context;
    private ArrayList<CourseDataProtocol> protocols;

    public CourseDataAdapter(Context context, ArrayList<CourseDataProtocol> courseProtocols) {
        this.context = context;
        this.protocols = courseProtocols;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View mainView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_course, null);
        return new ViewHolder(mainView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        CourseDataProtocol courseProtocol = protocols.get(position);
        Glide.with(context).load(courseProtocol.images).into(holder.ivIcon);
        holder.tvContent.setText(courseProtocol.cname);


        holder.item_layout_root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, CourseDetailActivity.class)
                        .putExtra("net_typeid", courseProtocol.net_typeid)
                        .putExtra("cname", courseProtocol.cname));
            }
        });

    }

    @Override
    public int getItemCount() {
        return protocols.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ImageView    ivIcon;
        TextView     tvContent;
        LinearLayout item_layout_root;

        public ViewHolder(View itemView) {
            super(itemView);
            ivIcon = (ImageView) itemView.findViewById(R.id.ivIcon);
            tvContent = (TextView) itemView.findViewById(R.id.tvContent);
            item_layout_root = (LinearLayout) itemView.findViewById(R.id.item_layout_root);

        }
    }
}
