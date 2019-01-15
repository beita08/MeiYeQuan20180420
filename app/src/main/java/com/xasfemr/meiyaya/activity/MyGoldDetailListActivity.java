package com.xasfemr.meiyaya.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.xasfemr.meiyaya.R;
import com.xasfemr.meiyaya.bean.GoldDetailListData;
import com.xasfemr.meiyaya.global.GlobalConstants;
import com.xasfemr.meiyaya.main.BaseActivity;
import com.xasfemr.meiyaya.utils.LogUtils;
import com.xasfemr.meiyaya.utils.SPUtils;
import com.xasfemr.meiyaya.view.MeiYaYaHeader;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;

import okhttp3.Call;

public class MyGoldDetailListActivity extends BaseActivity {
    private static final String TAG = "MyGoldDetailList";

    private boolean isPullRefresh = false;

    private RecyclerView  rvGoldDetail;
    private RefreshLayout refreshLayout;
    private LinearLayout  llLoading;
    private LinearLayout  llEmptyData;
    private LinearLayout  llNetworkFailed;
    private Button        btnAgainLoad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_gold_detail_list);
        initTopBar();
        setTopTitleText("明细");

        rvGoldDetail = (RecyclerView) findViewById(R.id.rv_gold_detail);
        refreshLayout = (RefreshLayout) findViewById(R.id.refreshLayout);
        llLoading = (LinearLayout) findViewById(R.id.ll_loading);
        llEmptyData = (LinearLayout) findViewById(R.id.ll_empty_data);
        llNetworkFailed = (LinearLayout) findViewById(R.id.ll_network_failed);
        btnAgainLoad = (Button) findViewById(R.id.btn_again_load);

        btnAgainLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isPullRefresh = false;
                gotoGetGoldDetail();
            }
        });

        setRefreshLayout();
        gotoGetGoldDetail();
    }

    private void setRefreshLayout() {
        refreshLayout.setRefreshHeader(new MeiYaYaHeader(this));
        refreshLayout.setEnableLoadmore(false);

        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                //refreshlayout.finishRefresh(2000);
                //下拉刷新
                isPullRefresh = true;
                gotoGetGoldDetail();
            }
        });
    }

    private void gotoGetGoldDetail() {
        if (!isPullRefresh) {
            llLoading.setVisibility(View.VISIBLE);
        }
        llEmptyData.setVisibility(View.GONE);
        llNetworkFailed.setVisibility(View.GONE);

        String mUserId = SPUtils.getString(MyGoldDetailListActivity.this, GlobalConstants.userID, "");
        OkHttpUtils.get().url(GlobalConstants.PAY_GOLD_DETAIL)
                .addParams("userid", mUserId)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        LogUtils.show(TAG, "---获取金币访问网络失败---");
                        Toast.makeText(MyGoldDetailListActivity.this, "网络出现异常,刷新试试", Toast.LENGTH_SHORT).show();
                        refreshLayout.finishRefresh();
                        refreshLayout.finishLoadmore();
                        llLoading.setVisibility(View.GONE);
                        llEmptyData.setVisibility(View.GONE);
                        llNetworkFailed.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        LogUtils.show(TAG, "---获取金币访问网络成功---response = " + response + " ---");
                        refreshLayout.finishRefresh();
                        refreshLayout.finishLoadmore();
                        llLoading.setVisibility(View.GONE);
                        try {
                            parserGoldDetailJson(response);
                        } catch (Exception e) {
                            e.printStackTrace();
                            LogUtils.show(TAG, "onResponse: -----金币明细解析出现异常-----");
                            llEmptyData.setVisibility(View.GONE);
                            llNetworkFailed.setVisibility(View.VISIBLE);
                        }
                    }
                });
    }

    private void parserGoldDetailJson(String response) {
        Gson gson = new Gson();
        GoldDetailListData goldDetailListData = gson.fromJson(response, GoldDetailListData.class);

        if (goldDetailListData == null || goldDetailListData.data == null || goldDetailListData.data.size() == 0) {
            //还没有金币
            Toast.makeText(this, "没有金币明细~", Toast.LENGTH_SHORT).show();
            llEmptyData.setVisibility(View.VISIBLE);
            llNetworkFailed.setVisibility(View.GONE);
        } else {
            llEmptyData.setVisibility(View.GONE);
            llNetworkFailed.setVisibility(View.GONE);
            rvGoldDetail.setLayoutManager(new LinearLayoutManager(MyGoldDetailListActivity.this));
            rvGoldDetail.setAdapter(new GoldDetailAdapter(goldDetailListData.data));
        }
    }


    private class GoldDetailAdapter extends RecyclerView.Adapter<GoldDetailHolder> {

        private ArrayList<GoldDetailListData.GoldChangeInfo> mGoldList;

        public GoldDetailAdapter(ArrayList<GoldDetailListData.GoldChangeInfo> goldList) {
            this.mGoldList = goldList;
        }

        @Override
        public GoldDetailHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = View.inflate(MyGoldDetailListActivity.this, R.layout.item_gold_detail_list, null);
            GoldDetailHolder goldDetailHolder = new GoldDetailHolder(view);
            return goldDetailHolder;
        }

        @Override
        public void onBindViewHolder(GoldDetailHolder holder, int position) {
            holder.tvDetailType.setText(mGoldList.get(position).type);
            holder.tvDetailTime.setText(mGoldList.get(position).notify_time);
            holder.tvMoneyNum.setText(mGoldList.get(position).sgin + mGoldList.get(position).goldmoney);
            if (TextUtils.equals(mGoldList.get(position).sgin, "+")) {
                holder.tvMoneyNum.setTextColor(0xFFEB4F6F);
            } else if (TextUtils.equals(mGoldList.get(position).sgin, "-")) {
                holder.tvMoneyNum.setTextColor(0xFF000000);
            } else {
                holder.tvMoneyNum.setTextColor(0xFF000000);
            }
        }

        @Override
        public int getItemCount() {
            return mGoldList.size();
        }
    }

    static class GoldDetailHolder extends RecyclerView.ViewHolder {
        public TextView tvDetailType;
        public TextView tvDetailTime;
        public TextView tvMoneyNum;

        public GoldDetailHolder(View itemView) {
            super(itemView);
            tvDetailType = (TextView) itemView.findViewById(R.id.tv_gold_detail_type);
            tvDetailTime = (TextView) itemView.findViewById(R.id.tv_gold_detail_time);
            tvMoneyNum = (TextView) itemView.findViewById(R.id.tv_gold_money_num);
        }
    }
}
