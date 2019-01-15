package com.xasfemr.meiyaya.module.college.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.xasfemr.meiyaya.R;
import com.xasfemr.meiyaya.activity.UserPagerActivity;
import com.xasfemr.meiyaya.bean.CollegeLectureData;
import com.xasfemr.meiyaya.global.GlobalConstants;
import com.xasfemr.meiyaya.module.college.fragment.CollegeLectureFragment;
import com.xasfemr.meiyaya.module.college.protocol.AttentionEventProtocol;
import com.xasfemr.meiyaya.module.college.protocol.LectureProtocol;
import com.xasfemr.meiyaya.weight.SFDialog;

import org.simple.eventbus.EventBus;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Administrator on 2017/11/28.
 */

public class LectureAdapter extends RecyclerView.Adapter<LectureAdapter.LectureHolder> {


    private ArrayList<LectureProtocol>lectureProtocols;
    private Context context;

    public LectureAdapter(Context mContext,ArrayList<LectureProtocol>lectureProtocolArrayList) {
        this.lectureProtocols=lectureProtocolArrayList;
        this.context =mContext;
    }

    @Override
    public LectureHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(context, R.layout.item_college_lecture_no_space, null);
        LectureHolder lectureHolder = new LectureHolder(view);
        return lectureHolder;
    }

    @Override
    public void onBindViewHolder(LectureHolder holder, int position) {

        holder.tvLecName.setText(lectureProtocols.get(position).username);
        holder.tvLecFans.setText("关注Ta的:" + lectureProtocols.get(position).fans);

        Glide.with(context)
                .load(lectureProtocols.get(position).images)
                .into(holder.civLecIcon);

        holder.civLecIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, UserPagerActivity.class);
                intent.putExtra("LOOK_USER_ID",lectureProtocols.get(position).userid);
                context.startActivity(intent);
            }
        });

        //0：未关注   1：已关注

        switch (lectureProtocols.get(position).attention){

            case "0":
                holder.ivLecFocus.setVisibility(View.VISIBLE);
                holder.ivLecFocus.setImageDrawable(context.getResources().getDrawable(R.drawable.focus));
                break;
            case "1":
                holder.ivLecFocus.setVisibility(View.VISIBLE);
                holder.ivLecFocus.setImageDrawable(context.getResources().getDrawable(R.drawable.focused));
                break;
            case "-1":
                holder.ivLecFocus.setVisibility(View.GONE);
                break;
        }
//
//        if (lectureProtocols.get(position).attention.equals("0")){
//            holder.ivLecFocus.setImageDrawable(context.getResources().getDrawable(R.drawable.focus));
//        }else {
//            holder.ivLecFocus.setImageDrawable(context.getResources().getDrawable(R.drawable.focused));
//        }

        holder.ivLecFocus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lectureProtocols.get(position).attention.equals("1")){
                    SFDialog.basicDialog(context, "提示", "确定取消关注吗？", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            AttentionEventProtocol attentionEventProtocol =new AttentionEventProtocol();
                            attentionEventProtocol.position=position;
                            attentionEventProtocol.userId=lectureProtocols.get(position).userid;
                            attentionEventProtocol.type=1;

                            EventBus.getDefault().post(attentionEventProtocol,GlobalConstants.EventBus.GET_ATTENTION);
                        }
                    });
                }else {
                    AttentionEventProtocol attentionEventProtocol =new AttentionEventProtocol();
                    attentionEventProtocol.position=position;
                    attentionEventProtocol.userId=lectureProtocols.get(position).userid;
                    attentionEventProtocol.type=0;
                    EventBus.getDefault().post(attentionEventProtocol,GlobalConstants.EventBus.GET_ATTENTION);

                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return lectureProtocols.size();
    }
    class LectureHolder extends RecyclerView.ViewHolder {

        public CircleImageView civLecIcon;
        public TextView tvLecName;
        public TextView        tvLecFans;
        public ImageView ivLecFocus;

        public LectureHolder(View itemView) {
            super(itemView);
            civLecIcon = (CircleImageView) itemView.findViewById(R.id.civ_lec_icon);
            tvLecName = (TextView) itemView.findViewById(R.id.tv_lec_name);
            tvLecFans = (TextView) itemView.findViewById(R.id.tv_lec_fans);
            ivLecFocus = (ImageView) itemView.findViewById(R.id.iv_lec_focus);
        }
    }

}




