package com.xasfemr.meiyaya.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lzy.ninegrid.ImageInfo;
import com.lzy.ninegrid.NineGridView;
import com.lzy.ninegrid.preview.NineGridViewClickAdapter;
import com.xasfemr.meiyaya.R;
import com.xasfemr.meiyaya.activity.DynamicCommentActivity;
import com.xasfemr.meiyaya.activity.UserPagerActivity;
import com.xasfemr.meiyaya.global.GlobalConstants;
import com.xasfemr.meiyaya.view.ShareDialog;

import java.util.ArrayList;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserDynamicFragment extends BaseFragment {

    private ArrayList<Boolean> LikeList  = new ArrayList<>();
    private ArrayList<Boolean> FocusList = new ArrayList<>();

    private UserPagerActivity userPagerActivity;
    private RecyclerView      rvUserDynamic;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userPagerActivity = (UserPagerActivity) getActivity();
    }

    @Override
    public View initView() {
        View view = View.inflate(userPagerActivity, R.layout.fragment_user_dynamic, null);
        rvUserDynamic = (RecyclerView) view.findViewById(R.id.rv_user_dynamic);
        return view;
    }

    @Override
    public void initData() {
        rvUserDynamic.setLayoutManager(new LinearLayoutManager(userPagerActivity));
        rvUserDynamic.setAdapter(new DynamicAdapter());
    }


    private class DynamicAdapter extends RecyclerView.Adapter<DynamicHolder> {

        public DynamicAdapter() {
            for (int i = 0; i < 50; i++) {
                LikeList.add(i, false);
                FocusList.add(i, false);
            }
        }

        @Override
        public DynamicHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = View.inflate(userPagerActivity, R.layout.item_dynamic, null);
            DynamicHolder dynamicHolder = new DynamicHolder(view);
            return dynamicHolder;
        }

        @Override
        public void onBindViewHolder(final DynamicHolder holder, final int position) {

            Random random = new Random();
            int imageNum = random.nextInt(10);
            ArrayList<ImageInfo> imageList = new ArrayList<>();
            for (int i = 0; i < imageNum; i++) {
                ImageInfo imageInfo = new ImageInfo();
                imageInfo.setThumbnailUrl("https://gss3.bdstatic.com/7Po3dSag_xI4khGkpoWK1HF6hhy/baike/s%3D500/sign=54f57c850db30f24319aec03f894d192/2f738bd4b31c87018ef76502257f9e2f0608ff95.jpg");
                imageInfo.setBigImageUrl("https://gss3.bdstatic.com/7Po3dSag_xI4khGkpoWK1HF6hhy/baike/s%3D500/sign=54f57c850db30f24319aec03f894d192/2f738bd4b31c87018ef76502257f9e2f0608ff95.jpg");
                imageList.add(imageInfo);
            }
            holder.nineGridView.setAdapter(new NineGridViewClickAdapter(userPagerActivity, imageList));
            //ClickNineGridViewAdapter(context, imageInfo)

            holder.ivDyFocus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (FocusList.get(position)) {
                        FocusList.set(position, false);
                        holder.ivDyFocus.setImageResource(R.drawable.focus);
                    } else {
                        FocusList.set(position, true);
                        holder.ivDyFocus.setImageResource(R.drawable.focused);
                    }
                }
            });

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(userPagerActivity, DynamicCommentActivity.class);
                    userPagerActivity.startActivity(intent);
                }
            });

            holder.tvDyComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(userPagerActivity, DynamicCommentActivity.class);
                    //mainActivity.startActivityForResult(intent,1);
                    intent.putExtra("DYNAMIC", GlobalConstants.INTENT_DYNAMIC_ADD_COMMENT);
                    startActivity(intent);
                }
            });

            holder.tvDyLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String like = holder.tvDyLike.getText().toString().trim();
                    Integer likeNum = new Integer(like);
                    if (LikeList.get(position)) {
                        LikeList.set(position, false);
                        Drawable fabulous = getResources().getDrawable(R.drawable.fabulous);
                        fabulous.setBounds(0, 0, fabulous.getMinimumWidth(), fabulous.getMinimumWidth());
                        holder.tvDyLike.setCompoundDrawables(fabulous, null, null, null);
                        likeNum = likeNum - 1;
                        holder.tvDyLike.setText(likeNum + "");
                    } else {
                        LikeList.set(position, true);
                        Drawable fabulousSelected = getResources().getDrawable(R.drawable.fabulous_selected);
                        fabulousSelected.setBounds(0, 0, fabulousSelected.getMinimumWidth(), fabulousSelected.getMinimumWidth());
                        holder.tvDyLike.setCompoundDrawables(fabulousSelected, null, null, null);
                        likeNum = likeNum + 1;
                        holder.tvDyLike.setText(likeNum + "");
                    }
                }
            });

            holder.tvDyShare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ShareDialog shareDialog = new ShareDialog(userPagerActivity);
                    shareDialog.show();
                }
            });
        }

        @Override
        public int getItemCount() {
            return 50;
        }
    }


    static class DynamicHolder extends RecyclerView.ViewHolder {

        public View            itemView;
        public CircleImageView civDyUserIcon;
        public TextView        tvDyUserName;
        public TextView        tvDySendTime;
        public ImageView       ivDyFocus;
        public ImageView       ivDyDelete;
        public TextView        tvDyContent;
        public NineGridView    nineGridView;
        public TextView        tvDyLike;
        public TextView        tvDyComment;
        public TextView        tvDyShare;


        public DynamicHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            civDyUserIcon = (CircleImageView) itemView.findViewById(R.id.civ_dy_user_icon);
            tvDyUserName = (TextView) itemView.findViewById(R.id.tv_dy_user_name);
            tvDySendTime = (TextView) itemView.findViewById(R.id.tv_dy_send_time);
            ivDyFocus = (ImageView) itemView.findViewById(R.id.iv_dy_focus);
            ivDyDelete = (ImageView) itemView.findViewById(R.id.iv_dy_delete);
            tvDyContent = (TextView) itemView.findViewById(R.id.tv_dy_content);
            nineGridView = (NineGridView) itemView.findViewById(R.id.nineGridView);
            tvDyLike = (TextView) itemView.findViewById(R.id.tv_dy_like);
            tvDyComment = (TextView) itemView.findViewById(R.id.tv_dy_comment);
            tvDyShare = (TextView) itemView.findViewById(R.id.tv_dy_share);

            ivDyFocus.setVisibility(View.GONE);
            ivDyDelete.setVisibility(View.VISIBLE);
        }
    }


}
