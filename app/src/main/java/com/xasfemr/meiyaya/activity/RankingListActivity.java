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

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.xasfemr.meiyaya.R;
import com.xasfemr.meiyaya.bean.RankingListData;
import com.xasfemr.meiyaya.global.GlobalConstants;
import com.xasfemr.meiyaya.main.BaseActivity;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;

public class RankingListActivity extends BaseActivity {
    private static final String TAG = "RankingListActivity";

    private final int[]    rangingLogoArr  = {R.drawable.ranking_list_1_active, R.drawable.ranking_list_2_money, R.drawable.ranking_list_3_popular, R.drawable.ranking_list_4_invite};
    private final String[] rankingTitleArr = {"活跃榜", "财富榜", "人气榜", "邀请榜"};
    private final String[] rankingDesArr   = {"每周更新", "每周更新", "累积", "累积"};

    private ArrayList<RankingListData.DataBean.ActiveUserInfo>   activeList   = new ArrayList<>(); //活跃榜
    private ArrayList<RankingListData.DataBean.TreasureUserInfo> treasureList = new ArrayList<>(); //财富榜

    private RecyclerView rvRankingList;
    private LinearLayout llLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking_list);
        initTopBar();
        setTopTitleText("排行榜");
        rvRankingList = (RecyclerView) findViewById(R.id.rv_ranking_list);
        llLoading = (LinearLayout) findViewById(R.id.ll_loading);

        gotoGetRankingListData();
    }

    private void gotoGetRankingListData() {
        llLoading.setVisibility(View.VISIBLE);
        OkHttpUtils.get().url(GlobalConstants.URL_RANKING_LIST)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.i(TAG, "onError: ---网络异常,排行榜数据获取失败---");
                        llLoading.setVisibility(View.GONE);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.i(TAG, "onResponse: ---访问排行榜数据成功---response = " + response + " ---");
                        llLoading.setVisibility(View.GONE);
                        try {
                            parserRankingListJson(response);
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.i(TAG, "onResponse: ---解析排行榜数据出现异常---");
                        }
                    }
                });
    }

    private void parserRankingListJson(String response) {
        Gson gson = new Gson();
        RankingListData rankingListData = gson.fromJson(response, RankingListData.class);

        activeList = rankingListData.data.active;
        treasureList = rankingListData.data.treasure;

        rvRankingList.setLayoutManager(new LinearLayoutManager(RankingListActivity.this));
        rvRankingList.setAdapter(new RankingAdapter());
    }


    private class RankingAdapter extends RecyclerView.Adapter<RankingHolder> {

        @Override
        public RankingHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = View.inflate(RankingListActivity.this, R.layout.item_ranking_list, null);
            RankingHolder rankingHolder = new RankingHolder(view);
            return rankingHolder;
        }

        @Override
        public void onBindViewHolder(RankingHolder holder, final int position) {
            holder.ivRankingLogo.setImageResource(rangingLogoArr[position]);
            holder.tvRankingTitle.setText(rankingTitleArr[position]);
            holder.tvRankingDes.setText(rankingDesArr[position]);

            switch (position) {
                case 0:
                    if (activeList != null && activeList.size() >= 1) { //第一名
                        holder.llRankingFirst.setVisibility(View.VISIBLE);
                        Glide.with(RankingListActivity.this).load(activeList.get(0).images).into(holder.civIconFirst);
                        holder.tvNameFirst.setText(activeList.get(0).username);
                        holder.tvScoreFirst.setText(activeList.get(0).live + "次");
                    }

                    if (activeList != null && activeList.size() >= 2) { //第二名
                        holder.llRankingSecond.setVisibility(View.VISIBLE);
                        Glide.with(RankingListActivity.this).load(activeList.get(1).images).into(holder.civIconSecond);
                        holder.tvNameSecond.setText(activeList.get(1).username);
                        holder.tvScoreSecond.setText(activeList.get(1).live + "次");
                    }

                    if (activeList != null && activeList.size() >= 3) { //第三名
                        holder.llRankingThird.setVisibility(View.VISIBLE);
                        Glide.with(RankingListActivity.this).load(activeList.get(2).images).into(holder.civIconThird);
                        holder.tvNameThird.setText(activeList.get(2).username);
                        holder.tvScoreThird.setText(activeList.get(2).live + "次");
                    }
                    break;

                case 1:

                    if (treasureList != null && treasureList.size() >= 1) { //第一名
                        holder.llRankingFirst.setVisibility(View.VISIBLE);
                        Glide.with(RankingListActivity.this).load(treasureList.get(0).images).into(holder.civIconFirst);
                        holder.tvNameFirst.setText(treasureList.get(0).username);
                        holder.tvScoreFirst.setText(treasureList.get(0).payment + "金币");
                    }

                    if (treasureList != null && treasureList.size() >= 2) { //第二名
                        holder.llRankingSecond.setVisibility(View.VISIBLE);
                        Glide.with(RankingListActivity.this).load(treasureList.get(1).images).into(holder.civIconSecond);
                        holder.tvNameSecond.setText(treasureList.get(1).username);
                        holder.tvScoreSecond.setText(treasureList.get(1).payment + "金币");
                    }

                    if (treasureList != null && treasureList.size() >= 3) { //第三名
                        holder.llRankingThird.setVisibility(View.VISIBLE);
                        Glide.with(RankingListActivity.this).load(treasureList.get(2).images).into(holder.civIconThird);
                        holder.tvNameThird.setText(treasureList.get(2).username);
                        holder.tvScoreThird.setText(treasureList.get(2).payment + "金币");
                    }
                    break;
                default:
                    break;
            }

            //条目点击
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(RankingListActivity.this, RankingListDetailActivity.class);
                    intent.putExtra("RANKING_LIST", position);
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return 2;
        }

    }

    static class RankingHolder extends RecyclerView.ViewHolder {

        public ImageView       ivRankingLogo;
        public TextView        tvRankingTitle;
        public TextView        tvRankingDes;
        public CircleImageView civIconFirst;
        public TextView        tvNameFirst;
        public TextView        tvScoreFirst;
        public CircleImageView civIconSecond;
        public TextView        tvNameSecond;
        public TextView        tvScoreSecond;
        public CircleImageView civIconThird;
        public TextView        tvNameThird;
        public TextView        tvScoreThird;
        public LinearLayout    llRankingFirst;
        public LinearLayout    llRankingSecond;
        public LinearLayout    llRankingThird;

        public RankingHolder(View itemView) {
            super(itemView);
            ivRankingLogo = (ImageView) itemView.findViewById(R.id.iv_ranking_list_logo);
            tvRankingTitle = (TextView) itemView.findViewById(R.id.tv_ranking_list_title);
            tvRankingDes = (TextView) itemView.findViewById(R.id.tv_ranking_list_des);
            civIconFirst = (CircleImageView) itemView.findViewById(R.id.civ_user_icon_first);
            tvNameFirst = (TextView) itemView.findViewById(R.id.tv_user_name_first);
            tvScoreFirst = (TextView) itemView.findViewById(R.id.tv_user_score_first);
            civIconSecond = (CircleImageView) itemView.findViewById(R.id.civ_user_icon_second);
            tvNameSecond = (TextView) itemView.findViewById(R.id.tv_user_name_second);
            tvScoreSecond = (TextView) itemView.findViewById(R.id.tv_user_score_second);
            civIconThird = (CircleImageView) itemView.findViewById(R.id.civ_user_icon_third);
            tvNameThird = (TextView) itemView.findViewById(R.id.tv_user_name_third);
            tvScoreThird = (TextView) itemView.findViewById(R.id.tv_user_score_third);
            llRankingFirst = (LinearLayout) itemView.findViewById(R.id.ll_rankinglist_first);
            llRankingSecond = (LinearLayout) itemView.findViewById(R.id.ll_rankinglist_second);
            llRankingThird = (LinearLayout) itemView.findViewById(R.id.ll_rankinglist_third);
        }
    }
}
