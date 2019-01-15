package com.xasfemr.meiyaya.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xasfemr.meiyaya.R;
import com.xasfemr.meiyaya.main.BaseActivity;

public class MyCollectActivity extends BaseActivity implements View.OnClickListener {

    private boolean boolEdit = false;

    private TextView       tvTopRight;
    private RecyclerView   rvMyCollect;
    private View           vEditBottomLine;
    private RelativeLayout rlEditBottomLine;
    private ImageView      ivEditAllSelect;
    private ImageView      ivEditDelete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_collect);
        initTopBar();
        setTopTitleText("我的收藏");

        tvTopRight = (TextView) findViewById(R.id.tv_top_right);
        rvMyCollect = (RecyclerView) findViewById(R.id.rv_my_collect);
        vEditBottomLine = findViewById(R.id.view_edit_bottom_line);
        rlEditBottomLine = (RelativeLayout) findViewById(R.id.rl_edit_bottom_line);
        ivEditAllSelect = (ImageView) findViewById(R.id.iv_edit_all_select);
        ivEditDelete = (ImageView) findViewById(R.id.iv_edit_delete);

        tvTopRight.setVisibility(View.VISIBLE);
        tvTopRight.setText("编辑");
        tvTopRight.setOnClickListener(this);
        ivEditAllSelect.setOnClickListener(this);
        ivEditDelete.setOnClickListener(this);

        rvMyCollect.setLayoutManager(new LinearLayoutManager(MyCollectActivity.this));
        rvMyCollect.setAdapter(new CollectAdapter());

    }

    @Override
    public void onClick(View v) {


        switch (v.getId()) {
            case R.id.tv_top_right:
                if (boolEdit) {
                    tvTopRight.setText("编辑");
                    vEditBottomLine.setVisibility(View.GONE);
                    rlEditBottomLine.setVisibility(View.GONE);
                    boolEdit = false;
                } else {
                    tvTopRight.setText("完成");
                    vEditBottomLine.setVisibility(View.VISIBLE);
                    rlEditBottomLine.setVisibility(View.VISIBLE);
                    boolEdit = true;
                }
                rvMyCollect.setAdapter(new CollectAdapter());
                //TODO 控制RecyclerView中iv_course_edit_tag的显示和隐藏
                break;
            case R.id.iv_edit_all_select: //全选

                break;
            case R.id.iv_edit_delete:    //删除

                break;
            default:
                break;
        }
    }

    private class CollectAdapter extends RecyclerView.Adapter<CollectHolder>{

        @Override
        public CollectHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = View.inflate(MyCollectActivity.this, R.layout.item_course_collect, null);
            CollectHolder collectHolder = new CollectHolder(view);
            return collectHolder;
        }

        @Override
        public void onBindViewHolder(CollectHolder holder, int position) {

            if (boolEdit) {
                holder.ivCollectEditTag.setVisibility(View.VISIBLE);
            } else {
                holder.ivCollectEditTag.setVisibility(View.GONE);
            }
        }

        @Override
        public int getItemCount() {
            return 3;
        }
    }


    static class CollectHolder extends RecyclerView.ViewHolder {

        public TextView  tvCollectTime;
        public ImageView ivCollectEditTag;
        public ImageView ivCollectCover;
        public TextView  tvCollectNickname;
        public TextView  tvCollectTitle;

        public CollectHolder(View itemView) {
            super(itemView);
            tvCollectTime = (TextView) itemView.findViewById(R.id.tv_collect_time);
            ivCollectEditTag = (ImageView) itemView.findViewById(R.id.iv_collect_edit_tag);
            ivCollectCover = (ImageView) itemView.findViewById(R.id.iv_collect_cover);
            tvCollectNickname = (TextView) itemView.findViewById(R.id.tv_collect_nickname);
            tvCollectTitle = (TextView) itemView.findViewById(R.id.tv_collect_title);
        }
    }
}
