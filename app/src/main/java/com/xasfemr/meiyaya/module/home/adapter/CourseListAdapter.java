package com.xasfemr.meiyaya.module.home.adapter;

import android.content.Context;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xasfemr.meiyaya.R;
import com.xasfemr.meiyaya.module.home.protocol.CourseListProtocol;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/11/29.
 */

public class CourseListAdapter extends RecyclerView.Adapter<CourseListAdapter.ViewHolder> {


    private Context context;

    private ArrayList<CourseListProtocol> courseListProtocols;

    public CourseListAdapter(Context context, ArrayList<CourseListProtocol> courseListProtocolArrayList) {
        this.context = context;
        this.courseListProtocols = courseListProtocolArrayList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View mainView = LayoutInflater.from(context).inflate(R.layout.item_course_list, null);

        return new ViewHolder(mainView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        CourseListProtocol courseListProtocol = courseListProtocols.get(position);
        holder.tvTitle.setText(courseListProtocol.cname);
        CourseDataAdapter courseDataAdapter = new CourseDataAdapter(context, courseListProtocol.list);
        holder.rvContent.setAdapter(courseDataAdapter);
    }

    @Override
    public int getItemCount() {
        return courseListProtocols.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        RecyclerView rvContent;
        TextView     tvTitle;

        public ViewHolder(View itemView) {
            super(itemView);

            rvContent = (RecyclerView) itemView.findViewById(R.id.rvContent);
            tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
            //rvContent.setLayoutManager(new GridLayoutManager(context, 4));
            rvContent.setItemAnimator(new DefaultItemAnimator());
        }
    }

}
