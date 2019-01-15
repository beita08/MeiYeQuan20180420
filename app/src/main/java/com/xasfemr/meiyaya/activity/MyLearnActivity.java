package com.xasfemr.meiyaya.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.xasfemr.meiyaya.R;
import com.xasfemr.meiyaya.bean.LearnCollectData;
import com.xasfemr.meiyaya.global.GlobalConstants;
import com.xasfemr.meiyaya.main.BaseActivity;
import com.xasfemr.meiyaya.module.college.activity.ExcellentCourseActivity;
import com.xasfemr.meiyaya.module.college.protocol.ExcellentListProtocol;
import com.xasfemr.meiyaya.utils.LogUtils;
import com.xasfemr.meiyaya.utils.SPUtils;
import com.xasfemr.meiyaya.view.MeiYaYaHeader;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;

import okhttp3.Call;

public class MyLearnActivity extends BaseActivity {
    private static final String TAG = "MyLearnActivity";

    private ArrayList<ExcellentListProtocol> mLearnList = new ArrayList<>();

    private boolean isPullRefresh = false;
    private int pageNo;

    private RefreshLayout  refreshLayout;
    private RecyclerView   rvMyLearn;
    private MyLearnAdapter mLearnAdapter;
    private LinearLayout   llLoading;
    private LinearLayout   llEmptyData;
    private LinearLayout   llNetworkFailed;
    private Button         btnAgainLoad;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_learn);
        initTopBar();
        setTopTitleText("我的学习");
        pageNo = 0;

        refreshLayout = (RefreshLayout) findViewById(R.id.refreshLayout);
        rvMyLearn = (RecyclerView) findViewById(R.id.rv_my_learn);
        llLoading = (LinearLayout) findViewById(R.id.ll_loading);
        llEmptyData = (LinearLayout) findViewById(R.id.ll_empty_data);
        llNetworkFailed = (LinearLayout) findViewById(R.id.ll_network_failed);
        btnAgainLoad = (Button) findViewById(R.id.btn_again_load);

        btnAgainLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pageNo = 0;
                isPullRefresh = false;
                mLearnList.clear();
                gotoGetMyLearnCourse();
            }
        });

        setRefreshLayout();
        gotoGetMyLearnCourse();
    }

    private void setRefreshLayout() {

        refreshLayout.setRefreshHeader(new MeiYaYaHeader(this));
        refreshLayout.setRefreshFooter(new ClassicsFooter(this).setSpinnerStyle(SpinnerStyle.Scale));

        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                //下拉刷新
                pageNo = 0;
                isPullRefresh = true;
                mLearnList.clear();
                gotoGetMyLearnCourse();
            }
        });

        refreshLayout.setOnLoadmoreListener(new OnLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                pageNo++;
                gotoGetMyLearnCourse();
            }
        });
    }

    private void gotoGetMyLearnCourse() {
        if (pageNo == 0 && !isPullRefresh) {
            llLoading.setVisibility(View.VISIBLE);
        }
        llEmptyData.setVisibility(View.GONE);
        llNetworkFailed.setVisibility(View.GONE);

        String mUserId = SPUtils.getString(MyLearnActivity.this, GlobalConstants.userID, "");
        OkHttpUtils.get().url(GlobalConstants.URL_MY_LEARN)
                .addParams("userid", mUserId)
                .addParams("page", pageNo + "")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        LogUtils.show(TAG, "---获取我的学习课程失败---");
                        Toast.makeText(MyLearnActivity.this, "网络异常，请重试", Toast.LENGTH_SHORT).show();
                        refreshLayout.finishRefresh();
                        refreshLayout.finishLoadmore();
                        llLoading.setVisibility(View.GONE);
                        llEmptyData.setVisibility(View.GONE);
                        llNetworkFailed.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        LogUtils.show(TAG, "---获取我的学习课程成功---response = " + response + " ---");
                        refreshLayout.finishRefresh();
                        refreshLayout.finishLoadmore();
                        llLoading.setVisibility(View.GONE);
                        llNetworkFailed.setVisibility(View.GONE);

                        try {
                            parserMyLearnJson(response);
                        } catch (Exception e) {
                            e.printStackTrace();
                            LogUtils.show(TAG, "---解析我的学习课程出现异常---");
                            llNetworkFailed.setVisibility(View.VISIBLE);
                        }
                    }
                });
    }

    private void parserMyLearnJson(String response) {
        Gson gson = new Gson();
        LearnCollectData learnCollectData = gson.fromJson(response, LearnCollectData.class);

        if (learnCollectData == null || learnCollectData.data == null || learnCollectData.data.size() == 0) {
            if (pageNo == 0) {
                llEmptyData.setVisibility(View.VISIBLE);
                Toast.makeText(this, "您还没有学习课程哦~", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "没有更多学习课程了", Toast.LENGTH_SHORT).show();
            }
        } else {
            mLearnList.addAll(learnCollectData.data);

            //RecyclerView加载数据
            if (mLearnList.size() <= 15 && pageNo == 0) {  //后台每页显示15条数据,这是第一页数据
                rvMyLearn.setLayoutManager(new LinearLayoutManager(MyLearnActivity.this));
                mLearnAdapter = new MyLearnAdapter();
                rvMyLearn.setAdapter(mLearnAdapter);
            } else {
                mLearnAdapter.notifyDataSetChanged();
            }
        }
    }

    private class MyLearnAdapter extends RecyclerView.Adapter<LearnHolder> {

        @Override
        public LearnHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = View.inflate(MyLearnActivity.this, R.layout.item_my_learn, null);
            LearnHolder learnHolder = new LearnHolder(view);
            return learnHolder;
        }

        @Override
        public void onBindViewHolder(LearnHolder holder, int position) {
            Glide.with(MyLearnActivity.this).load(mLearnList.get(position).cover).into(holder.ivLearnImg);
            holder.tvLearnTitle.setText(mLearnList.get(position).title);
            holder.tvLearnFreeTime.setText(mLearnList.get(position).fee);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MyLearnActivity.this, ExcellentCourseActivity.class);
                    intent.putExtra("protocol", mLearnList.get(position));
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return mLearnList.size();
        }
    }

    static class LearnHolder extends RecyclerView.ViewHolder {

        public ImageView ivLearnImg;
        public TextView  tvLearnTitle;
        public TextView  tvLearnFreeTime;
        public ImageView ivLearnContinue;

        public LearnHolder(View itemView) {
            super(itemView);
            ivLearnImg = (ImageView) itemView.findViewById(R.id.iv_learn_img);
            tvLearnTitle = (TextView) itemView.findViewById(R.id.tv_learn_title);
            tvLearnFreeTime = (TextView) itemView.findViewById(R.id.tv_learn_free_time);
            ivLearnContinue = (ImageView) itemView.findViewById(R.id.iv_learn_continue);
        }
    }
}
