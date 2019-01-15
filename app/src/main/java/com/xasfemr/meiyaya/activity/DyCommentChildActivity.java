package com.xasfemr.meiyaya.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xasfemr.meiyaya.R;
import com.xasfemr.meiyaya.main.BaseActivity;
import com.xasfemr.meiyaya.view.CommentFrame;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.xasfemr.meiyaya.view.CommentFrame.liveCommentEdit;
/*
  DyCommentChildActivity此类已抛弃;
  DyCommentChildActivity这个界面是对动态下面的评论的回复,即二级评论,类似于新浪微博的层级;
  在后面的产品设计中由于这样层级太深,逻辑比较麻烦.所以抛弃二级评论,全部采用一级评论;
  全部采用一级评论,这样所有的评论不再是父子关系,而全部是兄弟姐妹的关系;
*/
public class DyCommentChildActivity extends BaseActivity {

    private static final int TYPE_DY_COMMENT       = 0;   //
    private static final int TYPE_DY_COMMENT_CHILD = 1;   //

    private RecyclerView rvDynamicCommentChild;
    private TextView     tvAddCommentContent;
    private LinearLayout llRoot;
    private LinearLayoutManager mLinearLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dy_comment_child);

        initTopBar();
        setTopTitleText("30条回复");
        llRoot = (LinearLayout) findViewById(R.id.ll_root);
        tvAddCommentContent = (TextView) findViewById(R.id.tv_add_comment_content);
        rvDynamicCommentChild = (RecyclerView) findViewById(R.id.rv_dynamic_comment_child);

        mLinearLayoutManager = new LinearLayoutManager(this);
        rvDynamicCommentChild.setLayoutManager(mLinearLayoutManager);
        rvDynamicCommentChild.setAdapter(new DyCommentChildAdapter());

        tvAddCommentContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                liveCommentEdit(DyCommentChildActivity.this, llRoot, null);
            }
        });
    }


    private class DyCommentChildAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        @Override
        public int getItemViewType(int position) {
            if (position == 0) {
                //动态详情
                return TYPE_DY_COMMENT;
            } else {
                //动态详情评论
                return TYPE_DY_COMMENT_CHILD;
            }
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = null;
            RecyclerView.ViewHolder holder = null;
            switch (viewType) {
                case TYPE_DY_COMMENT:
                    view = View.inflate(DyCommentChildActivity.this, R.layout.item_dy_comment, null);
                    holder = new DyCommentHolder(view);
                    break;
                case TYPE_DY_COMMENT_CHILD:
                    view = View.inflate(DyCommentChildActivity.this, R.layout.item_dy_comment_child, null);
                    holder = new DyCommentChildHolder(view);
                    break;
                default:
                    break;
            }
            return holder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

            int type = getItemViewType(position);
            switch (type) {
                case TYPE_DY_COMMENT:
                    DyCommentHolder dyCommentHolder = (DyCommentHolder) holder;

                    dyCommentHolder.ivDyAddComment.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    });
                    break;
                case TYPE_DY_COMMENT_CHILD:
                    DyCommentChildHolder dyCommentChildHolder = (DyCommentChildHolder) holder;

                    dyCommentChildHolder.tvDyCommentChildContent.setText("条目" + position);

                    dyCommentChildHolder.ivDyAddComment.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(DyCommentChildActivity.this, "评论" + position, Toast.LENGTH_SHORT).show();
                            CommentFrame.liveCommentEdit(DyCommentChildActivity.this, llRoot, null);
                            //rvDynamicCommentChild.sm
                            mLinearLayoutManager.scrollToPositionWithOffset(position, 0);
                            mLinearLayoutManager.setStackFromEnd(true);
                            //rvDynamicCommentChild.smoothScrollBy(0,rvDynamicCommentChild.);
                        }
                    });
                    break;
                default:
                    break;
            }


        }

        @Override
        public int getItemCount() {
            return 50;
        }
    }


    static class DyCommentHolder extends RecyclerView.ViewHolder {

        public View            itemView;
        public CircleImageView civDyUserIcon;
        public TextView        tvDyUserName;
        public TextView        tvDySendTime;
        public ImageView       ivDyAddComment;
        public TextView        tvDyLikeComment;
        public TextView        tvDyCommentContent;

        public DyCommentHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            civDyUserIcon = (CircleImageView) itemView.findViewById(R.id.civ_dy_user_icon);     //civ_dy_user_icon
            tvDyUserName = (TextView) itemView.findViewById(R.id.tv_dy_user_name);              //tv_dy_user_name
            tvDySendTime = (TextView) itemView.findViewById(R.id.tv_dy_send_time);              //tv_dy_send_time
            ivDyAddComment = (ImageView) itemView.findViewById(R.id.iv_dy_add_comment);   //iv_dy_add_comment
            tvDyLikeComment = (TextView) itemView.findViewById(R.id.tv_dy_like_comment);  //tv_dy_like_comment                                                                                //tv_dy_like_comment
            tvDyCommentContent = (TextView) itemView.findViewById(R.id.tv_dy_comment_content);  //tv_dy_comment_content
        }
    }


    static class DyCommentChildHolder extends RecyclerView.ViewHolder {

        public View            itemView;
        public CircleImageView civDyUserIcon;
        public TextView        tvDyUserName;
        public TextView        tvDySendTime;
        public ImageView       ivDyAddComment;
        public TextView        tvDyLikeComment;
        public TextView        tvDyCommentChildContent;

        public DyCommentChildHolder(View itemView) {
            super(itemView);

            this.itemView = itemView;
            civDyUserIcon = (CircleImageView) itemView.findViewById(R.id.civ_dy_user_icon);  //
            tvDyUserName = (TextView) itemView.findViewById(R.id.tv_dy_user_name);           //
            tvDySendTime = (TextView) itemView.findViewById(R.id.tv_dy_send_time);           //
            ivDyAddComment = (ImageView) itemView.findViewById(R.id.iv_dy_add_comment);      //
            tvDyLikeComment = (TextView) itemView.findViewById(R.id.tv_dy_like_comment);     //                                                                                //tv_dy_like_comment
            tvDyCommentChildContent = (TextView) itemView.findViewById(R.id.tv_dy_comment_child_content);//
        }
    }


}
