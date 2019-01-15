package com.xasfemr.meiyaya.module.college.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.xasfemr.meiyaya.R;
import com.xasfemr.meiyaya.global.GlobalConstants;
import com.xasfemr.meiyaya.module.college.protocol.CommentProttocol;
import com.xasfemr.meiyaya.weight.SFDialog;

import org.simple.eventbus.EventBus;

import java.util.ArrayList;

/**
 * Created by Administrator on 2018/1/24.
 */

public class VideoCommentAdapter extends BaseAdapter{

    private ArrayList<CommentProttocol> commentDataList;
    private Context mContext;

    public VideoCommentAdapter(ArrayList<CommentProttocol> commentDataList, Context mContext) {
        this.commentDataList = commentDataList;
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return commentDataList.size();
    }

    @Override
    public Object getItem(int position) {
        return commentDataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CommentViewHolder viewHolder =null;

        if (convertView==null){
            viewHolder=new CommentViewHolder();
            convertView= LayoutInflater.from(mContext).inflate(R.layout.item_dynamic_detail_comment,null);
            viewHolder.ivIcon= (ImageView) convertView.findViewById(R.id.civ_dy_user_icon);
            viewHolder.tvName= (TextView) convertView.findViewById(R.id.tv_dy_user_name);
            viewHolder.tvContent= (TextView) convertView.findViewById(R.id.tv_dy_comment_content);
            viewHolder.tvTime= (TextView) convertView.findViewById(R.id.tv_dy_send_time);
            convertView.setTag(viewHolder);

        }else {
            viewHolder= (CommentViewHolder) convertView.getTag();

        }

        CommentProttocol commentProttocol =commentDataList.get(position);

        Glide.with(mContext).load(commentProttocol.images).apply(new RequestOptions().placeholder(R.drawable.meiyaya_logo_round_gray)).into(viewHolder.ivIcon);
        viewHolder.tvTime.setText(commentProttocol.reviewtime);
        viewHolder.tvContent.setText(commentProttocol.content);
        viewHolder.tvName.setText(commentProttocol.username);


        convertView.setOnLongClickListener(v -> {
            if (commentProttocol.ismy==0){
                return  true;
            }else {
                SFDialog.basicDialog(mContext, "提示", "确定删除这条评论吗？", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        commentDataList.remove(position);
                        EventBus.getDefault().post(commentProttocol.id, GlobalConstants.EventBus.DELETE_VIDEO_comment);
                        notifyDataSetChanged();
                    }
                });


            }

            return true;
        });




        return convertView;
    }


    class CommentViewHolder{

        TextView tvName,tvTime,tvContent;
        ImageView ivIcon;

    }
}
