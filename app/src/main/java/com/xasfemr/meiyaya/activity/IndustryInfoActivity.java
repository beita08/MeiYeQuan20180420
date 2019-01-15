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
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.xasfemr.meiyaya.R;
import com.xasfemr.meiyaya.bean.MoreIndustryNewsData;
import com.xasfemr.meiyaya.global.GlobalConstants;
import com.xasfemr.meiyaya.main.BaseActivity;
import com.xasfemr.meiyaya.module.college.activity.WebViewActivity;
import com.xasfemr.meiyaya.utils.UiUtils;
import com.xasfemr.meiyaya.view.MeiYaYaHeader;
import com.xasfemr.meiyaya.view.RecycleViewDivider;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;

import okhttp3.Call;

public class IndustryInfoActivity extends BaseActivity {
    private static final String TAG = "IndustryInfoActivity";

    private ArrayList<MoreIndustryNewsData.MoreNewsInfo> mNewsList = new ArrayList<>();

    private boolean isPullRefresh = false;
    private int             pageNo;
    private RefreshLayout   refreshLayout;
    private RecyclerView    rvIndustryInfo;
    private LinearLayout    llLoading;
    private InduInfoAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_industry_info);
        initTopBar();
        setTopTitleText("美业资讯");
        pageNo = 0;

        refreshLayout = (RefreshLayout) findViewById(R.id.refreshLayout);
        rvIndustryInfo = (RecyclerView) findViewById(R.id.rv_industry_info);
        llLoading = (LinearLayout) findViewById(R.id.ll_loading);

        setRefreshLayout();
        gotoGetMoreIndustryInfo();
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
                mNewsList.clear();
                gotoGetMoreIndustryInfo();
            }
        });
        refreshLayout.setOnLoadmoreListener(new OnLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                pageNo++;
                gotoGetMoreIndustryInfo();
            }
        });
    }


    private void gotoGetMoreIndustryInfo() {
        if (pageNo == 0 && !isPullRefresh) {
            llLoading.setVisibility(View.VISIBLE);
        }
        OkHttpUtils.get().url(GlobalConstants.URL_INDUSTRY_NEWS_MORE)
                .addParams("page", pageNo + "")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.i(TAG, "onError: ---网络出现异常, 更多资讯获取失败---");
                        Toast.makeText(IndustryInfoActivity.this, "网络出现异常", Toast.LENGTH_SHORT).show();
                        refreshLayout.finishRefresh();
                        refreshLayout.finishLoadmore();
                        llLoading.setVisibility(View.GONE);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.i(TAG, "onResponse: ---更多资讯访问服务器成功---response = " + response + " ---");
                        try {
                            parserMoreIndustryJson(response);
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.i(TAG, "onResponse: ---解析更多行业资讯出现异常---");
                        }
                        refreshLayout.finishRefresh();
                        refreshLayout.finishLoadmore();
                        llLoading.setVisibility(View.GONE);
                    }
                });
    }

    private void parserMoreIndustryJson(String response) {
        Gson gson = new Gson();
        MoreIndustryNewsData moreIndustryNewsData = gson.fromJson(response, MoreIndustryNewsData.class);

        if (moreIndustryNewsData == null || moreIndustryNewsData.data == null || moreIndustryNewsData.data.size() == 0) {
            if (pageNo == 0) {
                Toast.makeText(this, "还没有美业资讯", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "没有更多美业资讯了", Toast.LENGTH_SHORT).show();
            }
        } else {
            mNewsList.addAll(moreIndustryNewsData.data);

            //RecyclerView加载数据
            if (mNewsList.size() <= 15 && pageNo == 0) {  //page:每次加载15条
                rvIndustryInfo.setLayoutManager(new LinearLayoutManager(IndustryInfoActivity.this));
                rvIndustryInfo.addItemDecoration(new RecycleViewDivider(this, LinearLayoutManager.HORIZONTAL, UiUtils.dp2px(this, 1), getResources().getColor(R.color.textcolor_f3f3f3)));
                mAdapter = new InduInfoAdapter();
                rvIndustryInfo.setAdapter(mAdapter);

            } else {
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    private class InduInfoAdapter extends RecyclerView.Adapter<InduInfoHolder> {

        @Override
        public InduInfoHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View view = View.inflate(IndustryInfoActivity.this, R.layout.item_industry_info, null);
            InduInfoHolder induInfoHolder = new InduInfoHolder(view);

            return induInfoHolder;
        }

        @Override
        public void onBindViewHolder(InduInfoHolder holder, int position) {
            //刷新数据
            Glide.with(IndustryInfoActivity.this).load(mNewsList.get(position).images).into(holder.ivNewsImg);
            holder.tvNewsTitle.setText(mNewsList.get(position).title);
            holder.tvNewsDes.setText(mNewsList.get(position).digest);
            holder.tvNewsTime.setText(mNewsList.get(position).time);
            holder.tvNewsScanNum.setText(mNewsList.get(position).hits);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i(TAG, "onClick: position = " + position);
                    //Intent newsDetailsIntent = new Intent(IndustryInfoActivity.this, NewsDetailsActivity.class);
                    //newsDetailsIntent.putExtra("NEWS_DETAILS_URL", GlobalConstants.URL_MORE_NEWS_DETAILS + "&id=" + mNewsList.get(position).id);
                    //startActivity(newsDetailsIntent);

                    startActivity(new Intent(IndustryInfoActivity.this, WebViewActivity.class)
                            .putExtra("url", GlobalConstants.URL_MORE_NEWS_DETAILS + "&id=" + mNewsList.get(position).id)
                            .putExtra("title", mNewsList.get(position).title)
                            .putExtra("image", mNewsList.get(position).images)
                            .putExtra("dev", mNewsList.get(position).digest)
                            .putExtra("url_id", mNewsList.get(position).id)
                            .putExtra("news", true));


                }
            });
        }

        @Override
        public int getItemCount() {
            return mNewsList.size();
        }
    }


    static class InduInfoHolder extends RecyclerView.ViewHolder {

        public ImageView ivNewsImg;
        public TextView  tvNewsTitle;
        public TextView  tvNewsDes;
        public TextView  tvNewsTime;
        public TextView  tvNewsScanNum;

        public InduInfoHolder(View itemView) {
            super(itemView);
            ivNewsImg = (ImageView) itemView.findViewById(R.id.iv_news_img);
            tvNewsTitle = (TextView) itemView.findViewById(R.id.tv_news_title);
            tvNewsDes = (TextView) itemView.findViewById(R.id.tv_news_des);
            tvNewsTime = (TextView) itemView.findViewById(R.id.tv_news_time);
            tvNewsScanNum = (TextView) itemView.findViewById(R.id.tv_news_scan_num);
        }
    }
}
