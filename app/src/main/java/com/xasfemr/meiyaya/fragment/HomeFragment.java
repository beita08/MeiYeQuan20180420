package com.xasfemr.meiyaya.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.xasfemr.meiyaya.R;
import com.xasfemr.meiyaya.adapter.HomeAdapter;
import com.xasfemr.meiyaya.bean.HomeBannerDate;
import com.xasfemr.meiyaya.bean.HomeIndustryData;
import com.xasfemr.meiyaya.bean.HomeVideoListData;
import com.xasfemr.meiyaya.global.GlobalConstants;
import com.xasfemr.meiyaya.main.MainActivity;
import com.xasfemr.meiyaya.utils.LogUtils;
import com.xasfemr.meiyaya.utils.UiUtils;
import com.xasfemr.meiyaya.view.MeiYaYaHeader;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;

import okhttp3.Call;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends BaseFragment implements View.OnClickListener {
    private static final String TAG = "HomeFragment";

    private ArrayList<HomeBannerDate.BannerImg> bannerImgList;

    private boolean isPullRefresh = false;
    private MainActivity mainActivity;
    private HomeAdapter  mhomeAdapter;

    private ImageView     ivSearch;
    private RefreshLayout refreshLayout;
    private RecyclerView  rvHome;
    private LinearLayout  llLoading;
    private LinearLayout  llEmptyData;
    private LinearLayout  llNetworkFailed;
    private Button        btnAgainLoad;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity) getActivity();
    }

    @Override
    public View initView() {
        View view = View.inflate(mainActivity, R.layout.fragment_home, null);
        ivSearch = (ImageView) view.findViewById(R.id.iv_search);
        refreshLayout = (RefreshLayout) view.findViewById(R.id.refreshLayout);
        rvHome = (RecyclerView) view.findViewById(R.id.rv_home);
        llLoading = (LinearLayout) view.findViewById(R.id.ll_loading);
        llEmptyData = (LinearLayout) view.findViewById(R.id.ll_empty_data);
        llNetworkFailed = (LinearLayout) view.findViewById(R.id.ll_network_failed);
        btnAgainLoad = (Button) view.findViewById(R.id.btn_again_load);


        //设置点击监听和刷新布局监听
        setListenerAndFreshLayout();

        rvHome.setLayoutManager(new GridLayoutManager(mainActivity, 2));
        rvHome.addItemDecoration(new HomeAdapter.SpaceItemDecoration(UiUtils.dp2px(mainActivity, 10)));
        mhomeAdapter = new HomeAdapter(mainActivity);
        rvHome.setAdapter(mhomeAdapter);

        return view;
    }

    //设置点击监听和刷新布局监听
    private void setListenerAndFreshLayout() {
        ivSearch.setOnClickListener(this);
        btnAgainLoad.setOnClickListener(this);

        refreshLayout.setRefreshHeader(new MeiYaYaHeader(mainActivity));
        refreshLayout.setEnableLoadmore(false);

        refreshLayout.setOnRefreshListener(v -> {
            refreshLayout.finishRefresh(2000);
            //isPullRefresh = true;
            //initData();
        });
    }

    @Override
    public void initData() {
        gotoGetBannerFromServer();  //从服务器获取Banner信息

        gotoGetHomeVideoList(); //从服务器获取首页四个视频的列表

        gotoGetIndustryInfo();  //从服务器获取美业信息
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_search:
                Toast.makeText(mainActivity, "iv_search", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_again_load:
                Toast.makeText(mainActivity, "btn_again_load", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
    }


    private void gotoGetIndustryInfo() {
        OkHttpUtils.get().url(GlobalConstants.URL_INDUSTRY_NEWS_HOME).build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        LogUtils.show(TAG, "----获取美业资讯失败----" + e.getMessage());
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        LogUtils.show(TAG, "----获取美业资讯成功----" + response);

                        try {
                            parserIndustryInfoJson(response);
                        } catch (Exception e) {
                            e.printStackTrace();
                            LogUtils.show(TAG, "----获取美业资讯解析异常----");
                        }
                    }
                });
    }

    private void parserIndustryInfoJson(String response) {
        Gson gson = new Gson();
        HomeIndustryData homeIndustryData = gson.fromJson(response, HomeIndustryData.class);

        if (homeIndustryData != null && homeIndustryData.data != null && homeIndustryData.data.size() > 0) {
            mhomeAdapter.setIndustryInfoList(homeIndustryData.data);
            mhomeAdapter.notifyDataSetChanged();
        }
    }


    //从服务器获取首页四个视频的列表
    private void gotoGetHomeVideoList() {
        if (!isPullRefresh) {
            llLoading.setVisibility(View.VISIBLE);
        }
        llNetworkFailed.setVisibility(View.GONE);
        OkHttpUtils.get().url(GlobalConstants.URL_HOME_FOUR_VIDEO).build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        LogUtils.show(TAG, "----获取4个视频列表失败----");
                        e.printStackTrace();
                        refreshLayout.finishRefresh();
                        llLoading.setVisibility(View.GONE);
                        llNetworkFailed.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        LogUtils.show(TAG, "----获取4个视频列表成功----");
                        refreshLayout.finishRefresh();
                        llLoading.setVisibility(View.GONE);
                        llNetworkFailed.setVisibility(View.GONE);
                        try {
                            parserVideoListJson(response);
                        } catch (Exception e) {
                            e.printStackTrace();
                            LogUtils.show(TAG, "----获取4个视频列表解析失败----");
                        }
                    }
                });
    }

    private void parserVideoListJson(String response) {

        Gson gson = new Gson();
        HomeVideoListData homeVideoListData = gson.fromJson(response, HomeVideoListData.class);

        if (homeVideoListData != null && homeVideoListData.data != null && homeVideoListData.data.size() > 0) {
            mhomeAdapter.setHomeVideoList(homeVideoListData.data);
            mhomeAdapter.notifyDataSetChanged();

        } else {
            Toast.makeText(mainActivity, "没有找到视频数据", Toast.LENGTH_SHORT).show();
        }
    }


    private void gotoGetBannerFromServer() {

        OkHttpUtils.get().url(GlobalConstants.URL_HOME_BANNER).build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        LogUtils.show(TAG, "onError: 获取首页Banner失败!");
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        LogUtils.show(TAG, "onResponse: 成功获取首页Banner数据");
                        try {
                            parserBannerImage(response);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    private void parserBannerImage(String response) {
        Gson gson = new Gson();
        HomeBannerDate homeBannerDate = gson.fromJson(response, HomeBannerDate.class);
        bannerImgList = homeBannerDate.data;

        /*BANNER_NUM = homeBannerDate.data.size();
        //根据Banner页数量, 初始化小圆点
        initBannerPoint();

        vpBanner.setAdapter(new BannerAdapter());
        vpBanner.setCurrentItem(BANNER_NUM * 10000);
        mHandler.removeMessages(MSG_EMPTY);
        mHandler.sendEmptyMessageDelayed(MSG_EMPTY, LIVE_BANNER_DELAYED_TIME);*/
    }


}
