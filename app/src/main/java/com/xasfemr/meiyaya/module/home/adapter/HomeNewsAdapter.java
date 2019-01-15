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
import com.xasfemr.meiyaya.global.GlobalConstants;
import com.xasfemr.meiyaya.module.college.activity.WebViewActivity;
import com.xasfemr.meiyaya.module.home.protocol.HomeNewsprotocol;

import java.util.ArrayList;

/**
 * Created by sen.luo on 2018/2/23.
 */

public class HomeNewsAdapter extends BaseAdapter{

    private Context context;
    private ArrayList<HomeNewsprotocol>homeNewsprotocols;


    public HomeNewsAdapter(Context context, ArrayList<HomeNewsprotocol> homeNewsprotocols) {
        this.context = context;
        this.homeNewsprotocols = homeNewsprotocols;
    }

    @Override
    public int getCount() {
        return homeNewsprotocols.size();
    }

    @Override
    public Object getItem(int i) {
        return homeNewsprotocols.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {

        NewsViewHodler viewHodler=null;

        if (convertView==null){
            viewHodler=new NewsViewHodler();
            convertView= LayoutInflater.from(context).inflate(R.layout.item_industry_info,null);

            viewHodler.ivNewsImg = (ImageView) convertView.findViewById(R.id.iv_news_img);
            viewHodler. tvNewsTitle = (TextView) convertView.findViewById(R.id.tv_news_title);
            viewHodler.tvNewsDes = (TextView) convertView.findViewById(R.id.tv_news_des);
            viewHodler.tvNewsTime = (TextView) convertView.findViewById(R.id.tv_news_time);
            viewHodler. tvNewsScanNum = (TextView) convertView.findViewById(R.id.tv_news_scan_num);

            convertView.setTag(viewHodler);
        }else {
            viewHodler= (NewsViewHodler) convertView.getTag();
        }

        Glide.with(context).load(homeNewsprotocols.get(position).images).into(viewHodler.ivNewsImg);
        viewHodler.tvNewsTitle.setText(homeNewsprotocols.get(position ).title);
        viewHodler.tvNewsDes.setText(homeNewsprotocols.get(position ).digest);
        viewHodler.tvNewsTime.setText(homeNewsprotocols.get(position ).time);
        viewHodler.tvNewsScanNum.setText(homeNewsprotocols.get(position ).hits);


        convertView.setOnClickListener(view -> {

                context.startActivity(new Intent(context, WebViewActivity.class)
                    .putExtra("url", GlobalConstants.URL_HOME_NEWS_DETAILS)
                    .putExtra("title", homeNewsprotocols.get(position).title)
                    .putExtra("image", homeNewsprotocols.get(position).images)
                    .putExtra("dev", homeNewsprotocols.get(position).digest)
                    .putExtra("url_id", homeNewsprotocols.get(position).id)
                    .putExtra("share_status", "1")
                    .putExtra("news", true));


        });



        return convertView;
    }



    class NewsViewHodler{
        ImageView ivNewsImg;
        TextView  tvNewsTitle;
        TextView  tvNewsDes;
        TextView  tvNewsTime;
        TextView  tvNewsScanNum;
    }
}
