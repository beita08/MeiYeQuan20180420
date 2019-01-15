package com.xasfemr.meiyaya.module.home.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.xasfemr.meiyaya.R;

import java.util.ArrayList;

/**
 * 详情页照片列表  可通用 招聘、转让等详情页
 * Created by sen.luo on 2018/3/5.
 */

public class DetailImagesAdapter extends BaseAdapter {

    private ArrayList<String> imagesList;
    private Context           context;


    public DetailImagesAdapter(Context context, ArrayList<String> imagesList) {
        this.context = context;
        this.imagesList = imagesList;
    }

    @Override
    public int getCount() {
        return imagesList.size();
    }

    @Override
    public Object getItem(int i) {
        return imagesList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        ImagesViewHolder viewHolder = null;

        if (convertView == null) {
            viewHolder = new ImagesViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_basic_images, null);

            viewHolder.images = (ImageView) convertView.findViewById(R.id.images);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ImagesViewHolder) convertView.getTag();
        }

        Glide.with(context).load(imagesList.get(position)).into(viewHolder.images);


        return convertView;
    }


    class ImagesViewHolder {

        ImageView images;
    }
}
