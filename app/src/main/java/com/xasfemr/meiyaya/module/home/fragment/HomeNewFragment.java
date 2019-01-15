package com.xasfemr.meiyaya.module.home.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.xasfemr.meiyaya.BuildConfig;
import com.xasfemr.meiyaya.R;
import com.xasfemr.meiyaya.activity.IndustryInfoActivity;
import com.xasfemr.meiyaya.activity.LiveCreateActivity;
import com.xasfemr.meiyaya.activity.MeetingDetailsActivity;
import com.xasfemr.meiyaya.activity.SearchActivity;
import com.xasfemr.meiyaya.base.fragment.MVPBaseFragment;
import com.xasfemr.meiyaya.module.college.activity.WebViewActivity;
import com.xasfemr.meiyaya.module.college.protocol.HomeInterceptionProtocol;
import com.xasfemr.meiyaya.module.college.view.HomeGetRecommendIView;
import com.xasfemr.meiyaya.module.home.IView.HomeGetBannerIView;
import com.xasfemr.meiyaya.module.home.IView.HomeGetNewsIView;
import com.xasfemr.meiyaya.module.home.activity.InstrumentTransferListActivity;
import com.xasfemr.meiyaya.module.home.activity.RecruitmentListActivity;
import com.xasfemr.meiyaya.module.home.activity.RequestJobListActivity;
import com.xasfemr.meiyaya.module.home.adapter.HomeNewsAdapter;
import com.xasfemr.meiyaya.module.home.adapter.RemmendAdapter;
import com.xasfemr.meiyaya.module.home.presenter.HomePresenter;
import com.xasfemr.meiyaya.module.home.protocol.HomeBannerProtocol;
import com.xasfemr.meiyaya.module.home.protocol.HomeNewsprotocol;
import com.xasfemr.meiyaya.utils.LocationUtils;
import com.xasfemr.meiyaya.utils.LogUtils;
import com.xasfemr.meiyaya.utils.ToastUtil;
import com.xasfemr.meiyaya.utils.UiUtils;
import com.xasfemr.meiyaya.utils.ViewStatus;
import com.xasfemr.meiyaya.utils.permission.MPermission;
import com.xasfemr.meiyaya.utils.permission.annotation.OnMPermissionDenied;
import com.xasfemr.meiyaya.utils.permission.annotation.OnMPermissionGranted;
import com.xasfemr.meiyaya.view.LoadDataView;
import com.xasfemr.meiyaya.view.MarqueeView;
import com.xasfemr.meiyaya.view.MeiYaYaHeader;
import com.xasfemr.meiyaya.view.RecycleViewDivider;
import com.xasfemr.meiyaya.view.progress.SFProgressDialog;
import com.xasfemr.meiyaya.weight.SFDialog;
import com.youth.banner.Banner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 2.0.0 版本首页
 * Created by sen.luo on 2018/2/7.
 */

public class HomeNewFragment extends MVPBaseFragment {


    @BindView(R.id.home_banner)
    Banner   homeBanner;
    @BindView(R.id.lvNews)
    ListView lvNews;
    //    @BindView(R.id.vpAllSee)
    //    ViewPager viewPager;

//    @BindView(R.id.imgActivity)
//    ImageView imgActivity;


    @BindView(R.id.marqueeView)
    MarqueeView marqueeView;

    @BindView(R.id.refresh_Layout)
    RefreshLayout refreshLayout;

    @BindView(R.id.rvRecommend)
    RecyclerView rvRecommend;

    @BindView(R.id.tvFuJin)
    TextView tvFuJin;
    @BindView(R.id.imgFuJin)
    ImageView imgFuJin;


    @BindView(R.id.imgReMen)
    ImageView imgReMen;
    @BindView(R.id.tvReMen)
    TextView tvReMen;

    private LoadDataView  loadDataView;
    private HomePresenter homePresenter;

    private ArrayList<String> imageList;


    private ArrayList<HomeNewsprotocol> homeNewList;

    private HomeNewsAdapter                          newsAdapter;
    private ArrayList<HomeBannerProtocol.BannerBean> homeBannerProtocolList;
    private ArrayList<HomeInterceptionProtocol>      recommendProtocolList;

    private ArrayList<HomeBannerProtocol.ActivityBean> homeActivityList;

    private RemmendAdapter remmendAdapter;
    private SFProgressDialog progressDialog;

    private  GridLayoutManager gridLayoutManager;

    private List<String> info;
    @Override
    protected int layoutId() {
        return R.layout.fragment_home_new;
    }

    @Override
    protected void initView() {
        info= new ArrayList<>();
        homeActivityList=new ArrayList<>();

        marqueeView.setOnItemClickListener((position, textView) -> {

            if (homeActivityList.size()<1) return;

            startActivity(new Intent(getActivity(), MeetingDetailsActivity.class).putExtra("info_id",homeActivityList.get(position).id).putExtra("user_id",homeActivityList.get(position).userid));


        });

        refreshLayout.setRefreshHeader(new MeiYaYaHeader(getActivity()));
        refreshLayout.setEnableLoadmore(false);
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {

                tvFuJin.setTextColor(getResources().getColor(R.color.textcolor_9e9e9e));
                tvReMen.setTextColor(getResources().getColor(R.color.textcolor_3c3c3c));
                imgFuJin.setImageDrawable(getResources().getDrawable(R.drawable.icon_positioning));
                imgReMen.setImageDrawable(getResources().getDrawable(R.drawable.icon_latest_pressed));

                initData();
            }
        });

        homeBanner.setDelayTime(4000);
        imageList = new ArrayList<>();
        homeBannerProtocolList = new ArrayList<>();
        homeBanner.setBannerStyle(Banner.CIRCLE_INDICATOR);
        homeBanner.setIndicatorGravity(Banner.CENTER);



        progressDialog =new SFProgressDialog(getActivity());
        homeNewList = new ArrayList<>();
        newsAdapter = new HomeNewsAdapter(getActivity(), homeNewList);
        lvNews.setAdapter(newsAdapter);


        recommendProtocolList = new ArrayList<>();
        remmendAdapter = new RemmendAdapter(getActivity(), recommendProtocolList);

        gridLayoutManager=new GridLayoutManager(getActivity(), 3, OrientationHelper.HORIZONTAL, false) ;

        rvRecommend.setLayoutManager(gridLayoutManager);
        rvRecommend.addItemDecoration(new RecycleViewDivider(getActivity(), LinearLayoutManager.HORIZONTAL, UiUtils.dp2px(getActivity(), 1), getResources().getColor(R.color.textcolor_f3f3f3)));
        rvRecommend.setAdapter(remmendAdapter);




    }

    @Override
    protected void getLoadView(LoadDataView mLoadView) {
        this.loadDataView = mLoadView;
        loadDataView.setErrorListner(view -> initData());


    }


    @SuppressLint("ResourceType")
    @OnClick({R.id.layoutRecruitment, R.id.layoutInstrumentTransfer, R.id.layoutRequestJob, R.id.layoutStoreTransfer, R.id.layoutEquipmentRent, R.id.layoutSerach, R.id.tvLocation, R.id.tv_industry_info_more,R.id.tvReMen,R.id.tvFuJin})
    public void onViewClick(View view) {

        switch (view.getId()) {
            case R.id.layoutRecruitment:
                startActivity(new Intent(getActivity(), RecruitmentListActivity.class));
                break;

            case R.id.layoutInstrumentTransfer:
                startActivity(new Intent(getActivity(), InstrumentTransferListActivity.class));
                break;

            case R.id.layoutRequestJob:
                startActivity(new Intent(getActivity(), RequestJobListActivity.class));
                break;

            case R.id.layoutStoreTransfer:
                ToastUtil.showShort(getActivity(), "店铺转让,即将上线");
                break;

            case R.id.layoutEquipmentRent:
                ToastUtil.showShort(getActivity(), "设备租赁,即将上线");
                break;

            case R.id.layoutSerach:
                startActivity(new Intent(getActivity(), SearchActivity.class));
                break;

            case R.id.tvLocation:
                ToastUtil.showShort(getActivity(), "其他城市暂未开通，敬请期待");
                break;

            case R.id.tv_industry_info_more: //资讯更多
                startActivity(new Intent(getActivity(), IndustryInfoActivity.class));
                break;


            case R.id.tvFuJin:  //附近


                if (progressDialog!=null){
                    progressDialog.show();
                }

                getRecommend(false);
                tvFuJin.setTextColor(getResources().getColor(R.color.textcolor_3c3c3c));
                tvReMen.setTextColor(getResources().getColor(R.color.textcolor_9e9e9e));
                imgFuJin.setImageDrawable(getResources().getDrawable(R.drawable.icon_positioning_pressed));
                imgReMen.setImageDrawable(getResources().getDrawable(R.drawable.icon_latest));

                break;

            case R.id.tvReMen:  //最新



                if (progressDialog!=null){
                    progressDialog.show();
                }
                getRecommend(true);

                tvFuJin.setTextColor(getResources().getColor(R.color.textcolor_9e9e9e));
                tvReMen.setTextColor(getResources().getColor(R.color.textcolor_3c3c3c));
                imgFuJin.setImageDrawable(getResources().getDrawable(R.drawable.icon_positioning));
                imgReMen.setImageDrawable(getResources().getDrawable(R.drawable.icon_latest_pressed));
                break;
        }
    }

    @Override
    protected void initData() {
        loadDataView.changeStatusView(ViewStatus.START);
        //获取Banner
        homePresenter.getHomeBannerData(new HomeGetBannerIView() {
            @Override
            public void getBannerSuccess(HomeBannerProtocol homeBanner) {
                homeBannerProtocolList.clear();
                imageList.clear();
                homeActivityList.clear();
                homeBannerProtocolList.addAll(homeBanner.banner);
                homeActivityList.addAll(homeBanner.activity);
                for (int i = 0; i < homeBanner.banner.size(); i++) {
                    imageList.add(homeBanner.banner.get(i).images);
                }


                setBannerData();
                if (refreshLayout != null) {
                    refreshLayout.finishRefresh();
                }


                for (int i = 0; i < homeBanner.activity.size(); i++) {

                    if (!TextUtils.isEmpty(homeBanner.activity.get(i).title)){
                        info.add(homeBanner.activity.get(i).title);
                    }
                }

                marqueeView.startWithList(info, R.anim.anim_bottom_in, R.anim.anim_top_out);
//                Glide.with(getActivity()).load(homeBanner.activity.images).into(imgActivity);
//                imgActivity.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        startActivity(new Intent(getActivity(), WebViewActivity.class)
//                                .putExtra("title", homeBanner.activity.title)
//                                .putExtra("url", homeBanner.activity.url)
//                                .putExtra("image", homeBanner.activity.images)
//                                .putExtra("dev", "美页圈App")
//                                .putExtra("url_id", homeBanner.activity.id)
//                                .putExtra("news", true));
//
//
//                    }
//                });
            }

            @Override
            public void getBannerOnFailure(String message) {
                ToastUtil.showShort(getActivity(), message);
                loadDataView.changeStatusView(ViewStatus.FAILURE);
                if (refreshLayout != null) {
                    refreshLayout.finishRefresh();
                }
            }

            @Override
            public void onNetworkFailure(String message) {
                ToastUtil.showShort(getActivity(), message);
                loadDataView.changeStatusView(ViewStatus.NOTNETWORK);
                if (refreshLayout != null) {
                    refreshLayout.finishRefresh();
                }

            }
        });


        getNewsData();



        if (bWritePermission) {
            getRecommend(true);
        } else {
            requestBasicPermission();
        }


    }


    private String longitude = "0.0";
    private String latitude  = "0.0";

    //获取推荐
    private void getRecommend(boolean isNew) {

        gridLayoutManager.scrollToPosition(0);


        Map<String, String> map = new HashMap<>();

        if (!isNew){  //获取最新不传参
            LocationUtils.initLocation(getActivity());
            longitude =Double.toString(LocationUtils.longitude);
            latitude =Double.toString(LocationUtils.latitude);

            LogUtils.show("经度：", longitude + "");
            LogUtils.show("纬度：", latitude + "");


            map.put("longitude", longitude);//经度
            map.put("latitude", latitude);//纬度
            //        map.put("distance","1");//范围 km
        }



        homePresenter.getHomeRecommendData(map, new HomeGetRecommendIView() {
            @Override
            public void getHomeRecommendOnSuccess(ArrayList<HomeInterceptionProtocol> recommendProtocolArrayList) {
                recommendProtocolList.clear();
                recommendProtocolList.addAll(recommendProtocolArrayList);
                remmendAdapter.notifyDataSetChanged();



                if (progressDialog!=null){
                    progressDialog.dismiss();
                }

            }

            @Override
            public void getHomeRecommendonFailure(String msg) {
                ToastUtil.showShort(getActivity(), msg);
                if (refreshLayout != null) {
                    refreshLayout.finishRefresh();
                }

                if (progressDialog!=null){
                    progressDialog.dismiss();
                }

            }

            @Override
            public void onNetworkFailure(String message) {
                ToastUtil.showShort(getActivity(), message);
                if (refreshLayout != null) {
                    refreshLayout.finishRefresh();
                }

                if (progressDialog!=null){
                    progressDialog.dismiss();
                }
            }
        });

    }

    private void getNewsData() {
        //获取资讯
        homePresenter.getHomeNewsData(new HomeGetNewsIView() {
            @Override
            public void getNewSuccess(ArrayList<HomeNewsprotocol> homeList) {
                homeNewList.clear();
                homeNewList.addAll(homeList);
                newsAdapter.notifyDataSetChanged();

                loadDataView.changeStatusView(ViewStatus.SUCCESS);
                if (refreshLayout != null) {
                    refreshLayout.finishRefresh();
                }
            }

            @Override
            public void getNewsOnFailure(String message) {
                ToastUtil.showShort(getActivity(), message);
                loadDataView.changeStatusView(ViewStatus.FAILURE);
                if (refreshLayout != null) {
                    refreshLayout.finishRefresh();
                }
            }

            @Override
            public void onNetworkFailure(String message) {
                ToastUtil.showShort(getActivity(), message);
                loadDataView.changeStatusView(ViewStatus.NOTNETWORK);
                if (refreshLayout != null) {
                    refreshLayout.finishRefresh();
                }
            }
        });
    }

    private void setBannerData() {
        homeBanner.setImages(imageList, new Banner.OnLoadImageListener() {
            @Override
            public void OnLoadImage(ImageView view, Object url) {

                LogUtils.show("Banner--Url", url + "");
                Glide.with(HomeNewFragment.this).load(url).into(view);
            }
        });

        homeBanner.setOnBannerClickListener(new Banner.OnBannerClickListener() {
            @Override
            public void OnBannerClick(View view, int position) {

                if (!TextUtils.isEmpty(homeBannerProtocolList.get(position - 1).url)) {
                    startActivity(new Intent(getActivity(), WebViewActivity.class)
                            .putExtra("url", homeBannerProtocolList.get(position - 1).url)
                            .putExtra("title", homeBannerProtocolList.get(position - 1).title)
                            .putExtra("image", homeBannerProtocolList.get(position - 1).images)
                            .putExtra("dev", "美页圈App")
                            .putExtra("url_id", homeBannerProtocolList.get(position - 1).id)
                            .putExtra("news", true));
                }

            }
        });
    }


    private final String[] BASIC_PERMISSIONS = new String[]{
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
    };

    /**
     * 申请权限
     */
    private void requestBasicPermission() {
        //        MPermission.printMPermissionResult(true, getActivity(), BASIC_PERMISSIONS); //打印Logo
        MPermission.with(this)
                .setRequestCode(BASIC_PERMISSION_REQUEST_CODE)
                .permissions(BASIC_PERMISSIONS)
                .request();
    }


    private boolean bWritePermission;
    private final int BASIC_PERMISSION_REQUEST_CODE = 110;


    @OnMPermissionGranted(BASIC_PERMISSION_REQUEST_CODE)
    public void onBasicPermissionSuccess() {
        LogUtils.show("权限获取成功", "---");
        bWritePermission = true;
        getRecommend(true);
    }

    @OnMPermissionDenied(BASIC_PERMISSION_REQUEST_CODE)
    public void onBasicPermissionFailed() {
        LogUtils.show("权限获取失败", "---");
        bWritePermission = false;

        SFDialog.onlyConfirmDialog(getActivity(), "提示", "请授予必要权限", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                requestBasicPermission();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        MPermission.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }


    @Override
    protected void initPresenter() {
        homePresenter = new HomePresenter();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        homePresenter.destroy();
    }
}
