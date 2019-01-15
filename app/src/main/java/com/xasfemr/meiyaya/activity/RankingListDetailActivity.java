package com.xasfemr.meiyaya.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.xasfemr.meiyaya.R;
import com.xasfemr.meiyaya.bean.RankingDetailData;
import com.xasfemr.meiyaya.global.GlobalConstants;
import com.xasfemr.meiyaya.main.BaseActivity;
import com.xasfemr.meiyaya.view.MeiYaYaHeader;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;

public class RankingListDetailActivity extends BaseActivity {
    private static final String TAG = "RankingListDetail";

    private final String[] rankingTitleArr = {"活跃榜", "财富榜", "人气榜", "邀请榜"};
    private final String[] rankingUrlArr   = {GlobalConstants.URL_RANKING_ACTIVE, GlobalConstants.URL_RANKING_MONEY, "", ""};

    private boolean isPullRefresh = false;
    private int           rankingType;
    private ImageView     ivRankingBg;
    private TextView      tvRankingTitle;
    private TextView      tvRankingDes;
    private RecyclerView  rvRankingListDetail;
    private LinearLayout  llLoading;
    private RefreshLayout refreshLayout;
    private LinearLayout  llEmptyData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking_list_detail);
        initTopBar();
        setTopTitleText("排行榜");

        ivRankingBg = (ImageView) findViewById(R.id.iv_ranking_list_bg);
        tvRankingTitle = (TextView) findViewById(R.id.tv_ranking_list_title);
        tvRankingDes = (TextView) findViewById(R.id.tv_ranking_list_des);
        rvRankingListDetail = (RecyclerView) findViewById(R.id.rv_ranking_list_detail);
        llLoading = (LinearLayout) findViewById(R.id.ll_loading);
        llEmptyData = (LinearLayout) findViewById(R.id.ll_empty_data);
        setRefreshLayout();

        Intent intent = getIntent();
        rankingType = intent.getIntExtra("RANKING_LIST", 0);
        initRankingView(rankingType);
        gotoGetRankingDetailData();
    }

    private void setRefreshLayout() {
        refreshLayout = (RefreshLayout) findViewById(R.id.refreshLayout);
        refreshLayout.setRefreshHeader(new MeiYaYaHeader(this));
        refreshLayout.setEnableLoadmore(false);

        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                isPullRefresh = true;
                gotoGetRankingDetailData();
            }
        });
    }

    private void initRankingView(int rankingType) {

        switch (rankingType) {
            case 0:
                setTopTitleText("活跃榜");
                ivRankingBg.setImageResource(R.drawable.ranking_list_bg_1_active);
                tvRankingTitle.setText("活跃榜");
                tvRankingDes.setText("周开课次数");
                break;
            case 1:
                setTopTitleText("财富榜");
                ivRankingBg.setImageResource(R.drawable.ranking_list_bg_2_money);
                tvRankingTitle.setText("财富榜");
                tvRankingDes.setText("周开课总收入");
                break;
            case 2:
                setTopTitleText("人气榜");
                ivRankingBg.setImageResource(R.drawable.ranking_list_bg_3_popular);
                tvRankingTitle.setText("人气榜");
                tvRankingDes.setText("累积");
                break;
            case 3:
                setTopTitleText("邀请榜");
                ivRankingBg.setImageResource(R.drawable.ranking_list_bg_4_invite);
                tvRankingTitle.setText("邀请榜");
                tvRankingDes.setText("累积");
                break;
            default:
                setTopTitleText("排行榜");
                Toast.makeText(this, "跳转有误", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void gotoGetRankingDetailData() {
        if (!isPullRefresh) {
            llLoading.setVisibility(View.VISIBLE);
        }
        OkHttpUtils.get().url(rankingUrlArr[rankingType]).build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.i(TAG, "onError: ---网络异常, " + rankingTitleArr[rankingType] + "数据获取失败---");
                        llLoading.setVisibility(View.GONE);
                        refreshLayout.finishRefresh();
                        refreshLayout.finishLoadmore();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.i(TAG, "onResponse: ---访问" + rankingTitleArr[rankingType] + "数据成功---response = " + response + " ---");
                        llLoading.setVisibility(View.GONE);
                        refreshLayout.finishRefresh();
                        refreshLayout.finishLoadmore();
                        try {
                            parserRankingDetailJson(response);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    private void parserRankingDetailJson(String response) {
        Gson gson = new Gson();
        RankingDetailData rankingDetailData = gson.fromJson(response, RankingDetailData.class);

        if (rankingDetailData.data == null || rankingDetailData.data.size() == 0) {
            llEmptyData.setVisibility(View.VISIBLE);
        } else {
            llEmptyData.setVisibility(View.GONE);
            rvRankingListDetail.setLayoutManager(new LinearLayoutManager(RankingListDetailActivity.this));
            rvRankingListDetail.setAdapter(new RankingDetailAdapter(rankingDetailData.data));
        }
    }

    private class RankingDetailAdapter extends RecyclerView.Adapter<RankingDetailHolder> {

        ArrayList<RankingDetailData.RankingUserInfo> mRankingDetailList;

        public RankingDetailAdapter(ArrayList<RankingDetailData.RankingUserInfo> rankingDetailList) {
            this.mRankingDetailList = rankingDetailList;
        }

        @Override
        public RankingDetailHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = View.inflate(RankingListDetailActivity.this, R.layout.item_ranking_list_user_detail, null);
            RankingDetailHolder rankingDetailHolder = new RankingDetailHolder(view);
            return rankingDetailHolder;
        }

        @Override
        public void onBindViewHolder(RankingDetailHolder holder, int position) {
            if (position == 0) {
                holder.tvRankingNumber.setVisibility(View.INVISIBLE);
                holder.ivRankingNumber.setVisibility(View.VISIBLE);
                holder.ivRankingNumber.setImageResource(R.drawable.ranking_list_medal_1);
                holder.tvUserScore.setTextColor(0xFFEB4F6F);
            } else if (position == 1) {
                holder.tvRankingNumber.setVisibility(View.INVISIBLE);
                holder.ivRankingNumber.setVisibility(View.VISIBLE);
                holder.ivRankingNumber.setImageResource(R.drawable.ranking_list_medal_2);
                holder.tvUserScore.setTextColor(0xFFEB4F6F);
            } else if (position == 2) {
                holder.tvRankingNumber.setVisibility(View.INVISIBLE);
                holder.ivRankingNumber.setVisibility(View.VISIBLE);
                holder.ivRankingNumber.setImageResource(R.drawable.ranking_list_medal_3);
                holder.tvUserScore.setTextColor(0xFFEB4F6F);
            } else {
                holder.tvRankingNumber.setVisibility(View.VISIBLE);
                holder.ivRankingNumber.setVisibility(View.INVISIBLE);
                holder.tvUserScore.setTextColor(0xFF696969);
                holder.tvRankingNumber.setText(position + 1 + "");
            }

            Glide.with(RankingListDetailActivity.this).load(mRankingDetailList.get(position).images).into(holder.civUserIcon);
            holder.tvUserName.setText(mRankingDetailList.get(position).username);
            switch (rankingType) {
                case 0:  //活跃榜
                    holder.tvUserScore.setText(mRankingDetailList.get(position).live + "次");
                    break;
                case 1:  //财富榜
                    holder.tvUserScore.setText(mRankingDetailList.get(position).payment + "金币");
                    break;
                default:
                    holder.tvUserScore.setText("第" + (position + 1) + "名");
                    break;
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(RankingListDetailActivity.this, UserPagerActivity.class);
                    intent.putExtra("LOOK_USER_ID", mRankingDetailList.get(position).userid);
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return mRankingDetailList.size();
        }
    }

    static class RankingDetailHolder extends RecyclerView.ViewHolder {

        public ImageView       ivRankingNumber;
        public TextView        tvRankingNumber;
        public CircleImageView civUserIcon;
        public TextView        tvUserName;
        public TextView        tvUserScore;

        public RankingDetailHolder(View itemView) {
            super(itemView);
            ivRankingNumber = (ImageView) itemView.findViewById(R.id.iv_ranking_number);
            tvRankingNumber = (TextView) itemView.findViewById(R.id.tv_ranking_number);
            civUserIcon = (CircleImageView) itemView.findViewById(R.id.civ_user_icon);
            tvUserName = (TextView) itemView.findViewById(R.id.tv_user_name);
            tvUserScore = (TextView) itemView.findViewById(R.id.tv_user_score);
        }
    }
}
