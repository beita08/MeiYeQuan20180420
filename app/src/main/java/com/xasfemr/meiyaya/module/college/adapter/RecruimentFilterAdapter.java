package com.xasfemr.meiyaya.module.college.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xasfemr.meiyaya.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 招聘 仪器 列表 通用分类适配器
 * Created by sen.luo on 2018/2/26.
 */

public class RecruimentFilterAdapter extends RecyclerView.Adapter<RecruimentFilterAdapter.FilterViewHolder>{


    private Context context;
    private ArrayList<String> stringArrayList;

    private int mSelectedPos  = 0;


    private OnFilterClick onFilterClick;

    public RecruimentFilterAdapter(Context context, ArrayList<String> stringList) {
        this.context = context;
        this.stringArrayList =stringList;
        stringArrayList.add(0,"全部");

    }


    public void setOnFilterClick(OnFilterClick onFilterClick1){
        this.onFilterClick=onFilterClick1;
    }


    @Override
    public FilterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view =LayoutInflater.from(context).inflate(R.layout.item_recruiment_filter,null);

        return new FilterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FilterViewHolder holder, int position) {
            holder.tvContent.setText(stringArrayList.get(position));


            holder.tvContent.setSelected(mSelectedPos==position);

             holder.tvContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onFilterClick.onClick(stringArrayList.get(position));
                holder.tvContent.setSelected(true);
                mSelectedPos=position;
                notifyDataSetChanged();

            }
        });
    }


    @Override
    public int getItemCount() {
        return stringArrayList.size();
    }


    class FilterViewHolder extends RecyclerView.ViewHolder{
        TextView tvContent;
        LinearLayout layoutItem;

        public FilterViewHolder(View itemView) {
            super(itemView);

            tvContent= (TextView) itemView.findViewById(R.id.tvContent);
            layoutItem= (LinearLayout) itemView.findViewById(R.id.layoutItem);
        }
    }


    public interface OnFilterClick{
        void onClick(String position);
    }

}
