package com.xasfemr.meiyaya.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.xasfemr.meiyaya.R;
import com.xasfemr.meiyaya.activity.IndustryInfoActivity;
import com.xasfemr.meiyaya.activity.LoginActivity;
import com.xasfemr.meiyaya.activity.RankingListActivity;
import com.xasfemr.meiyaya.activity.SearchActivity;
import com.xasfemr.meiyaya.activity.UserPagerActivity;
import com.xasfemr.meiyaya.bean.HomeBannerDate;
import com.xasfemr.meiyaya.bean.HomeCourseInfo;
import com.xasfemr.meiyaya.bean.HomeIndustryData;
import com.xasfemr.meiyaya.bean.HomeVideoListData;
import com.xasfemr.meiyaya.bean.LiveVideoListData;
import com.xasfemr.meiyaya.global.GlobalConstants;
import com.xasfemr.meiyaya.main.MainActivity;
import com.xasfemr.meiyaya.module.college.activity.WebViewActivity;
import com.xasfemr.meiyaya.module.home.activity.CourseNewActivity;
import com.xasfemr.meiyaya.module.home.activity.HotCourseActivity;
import com.xasfemr.meiyaya.module.home.activity.LecturerActivity;
import com.xasfemr.meiyaya.module.home.activity.MemberNewActivity;
import com.xasfemr.meiyaya.neteasecloud.NELivePlayerActivity;
import com.xasfemr.meiyaya.module.player.VideoPlayerActivity;
import com.xasfemr.meiyaya.module.player.VideoPlayerIsUserActivity;
import com.xasfemr.meiyaya.utils.LogUtils;
import com.xasfemr.meiyaya.utils.SPUtils;
import com.xasfemr.meiyaya.utils.ToastUtil;
import com.xasfemr.meiyaya.view.MeiYaYaHeader;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;

import okhttp3.Call;

import static com.xasfemr.meiyaya.global.GlobalConstants.LIVE_BANNER_DELAYED_TIME;

/**
 * 由于本项目支持最低API为19即4.4.4,所以Fragement导包为:android.app.Fragment不向下兼容
 */
public class LiveFragment extends BaseFragment implements View.OnClickListener {
    private static final String TAG = "LiveFragment";

    private static final int TYPE_LIVE_CONTENT = 0;   //直播内容类型
    private static final int TYPE_NEWS_TITLE   = 1;   //新闻标题类型
    private static final int TYPE_NEWS_INFO    = 2;   //新闻详情类型
    private static final int MSG_EMPTY         = 0;

    private static int BANNER_NUM = 1;    //Banner条目的数量,此处本应该是集合的大小,但是目前(写代码时)没有数据

    private boolean isPullRefresh = false;

    private ArrayList<HomeCourseInfo.CoursrInfo> HotCourseInfo    = new ArrayList<>();
    private ArrayList<HomeCourseInfo.CoursrInfo> MemberCourseInfo = new ArrayList<>();

    private ArrayList<LiveVideoListData.DataBean.LiveInfo> LiveVideoList = new ArrayList<>();

    private ArrayList<HomeBannerDate.BannerImg> bannerImgList;

    private ListView      lvLive;
    private MainActivity  mainActivity;
    private LinearLayout  llPointContainer;
    private ViewPager     vpBanner;
    private TextView      tvClassCourse;
    private TextView      tvClassLecturer;
    private TextView      tvClassMember;
    private TextView      tvClassRankingList;
    private ImageView     ivSerach;
    private LinearLayout  llLoading;
    private LinearLayout  llNetworkFailed;
    private Button        btnAgainLoad;
    private RefreshLayout refreshLayout;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            //切换到下一个页面
            int currentItem = vpBanner.getCurrentItem();
            vpBanner.setCurrentItem(++currentItem);

            //继续发送消息, 形成类似递归的循环
            mHandler.removeMessages(MSG_EMPTY);
            mHandler.sendEmptyMessageDelayed(MSG_EMPTY, LIVE_BANNER_DELAYED_TIME);
        }
    };
    private HomeVideoListData homeVideoListData;
    private HomeIndustryData  homeIndustryData;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity) getActivity();
    }

    /**
     * 填充布局初始化fragment中的view
     * @return 返回fragment所包装的viw对象
     */
    @Override
    public View initView() {
        View view = View.inflate(mainActivity, R.layout.fragment_live, null);
        lvLive = (ListView) view.findViewById(R.id.lv_live);
        ivSerach = (ImageView) view.findViewById(R.id.iv_search);
        llLoading = (LinearLayout) view.findViewById(R.id.ll_loading);
        llNetworkFailed = (LinearLayout) view.findViewById(R.id.ll_network_failed);
        btnAgainLoad = (Button) view.findViewById(R.id.btn_again_load);

        View headerView = View.inflate(mainActivity, R.layout.item_live_pager, null);
        vpBanner = (ViewPager) headerView.findViewById(R.id.vp_banner);
        llPointContainer = (LinearLayout) headerView.findViewById(R.id.ll_point_container);
        tvClassCourse = (TextView) headerView.findViewById(R.id.tv_class_course);
        tvClassLecturer = (TextView) headerView.findViewById(R.id.tv_class_lecturer);
        tvClassMember = (TextView) headerView.findViewById(R.id.tv_class_member);
        tvClassRankingList = (TextView) headerView.findViewById(R.id.tv_class_ranking_list);

        lvLive.addHeaderView(headerView);
        //lvLive.setAdapter(new LiveAdapter());

        ivSerach.setOnClickListener(this);
        tvClassCourse.setOnClickListener(this);
        tvClassLecturer.setOnClickListener(this);
        tvClassMember.setOnClickListener(this);
        tvClassRankingList.setOnClickListener(this);
        btnAgainLoad.setOnClickListener(this);

        lvLive.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                LogUtils.show(TAG, "position = " + position);
                if (position >= 4) {
                    startActivity(new Intent(mainActivity, WebViewActivity.class)
                            .putExtra("url", GlobalConstants.URL_HOME_NEWS_DETAILS) //.putExtra("url", GlobalConstants.URL_HOME_NEWS_DETAILS + "&id=" + homeIndustryData.data.get(position - 4).id)
                            .putExtra("title", homeIndustryData.data.get(position - 4).title)
                            .putExtra("image", homeIndustryData.data.get(position - 4).images)
                            .putExtra("dev", homeIndustryData.data.get(position - 4).digest)
                            .putExtra("url_id", homeIndustryData.data.get(position - 4).id)
                            .putExtra("share_status", "1")
                            .putExtra("news", true));
                }
            }
        });

        vpBanner.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                //页面选中监听
                position = position % BANNER_NUM;
                for (int i = 0; i < BANNER_NUM; i++) {
                    ImageView point = (ImageView) llPointContainer.getChildAt(i);
                    if (i != position) {
                        point.setImageResource(R.drawable.shape_banner_point_grey);
                    } else {
                        point.setImageResource(R.drawable.shape_banner_point_red);
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        vpBanner.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        mHandler.removeMessages(MSG_EMPTY);
                        LogUtils.show(TAG, "MotionEvent.ACTION_DOWN");
                        break;
                    case MotionEvent.ACTION_MOVE:
                        mHandler.removeMessages(MSG_EMPTY);
                        //LogUtils.show(TAG, "MotionEvent.ACTION_MOVE");
                        break;
                    case MotionEvent.ACTION_UP:
                        LogUtils.show(TAG, "MotionEvent.ACTION_UP");
                    case MotionEvent.ACTION_CANCEL:
                        LogUtils.show(TAG, "MotionEvent.ACTION_CANCEL");
                        mHandler.removeMessages(MSG_EMPTY);
                        mHandler.sendEmptyMessageDelayed(MSG_EMPTY, LIVE_BANNER_DELAYED_TIME);
                        break;
                }
                return false;
            }
        });

        refreshLayout = (RefreshLayout) view.findViewById(R.id.refreshLayout);
        //设置 Header 为 Material样式 new MaterialHeader(mainActivity).setShowBezierWave(true)
        refreshLayout.setRefreshHeader(new MeiYaYaHeader(mainActivity));
        //设置 Footer 为 球脉冲  new BallPulseFooter(mainActivity ).setSpinnerStyle(SpinnerStyle.Scale)
        //refreshLayout.setRefreshFooter(null);
        refreshLayout.setEnableLoadmore(false);

        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                isPullRefresh = true;
                initData();
                //refreshlayout.finishRefresh(2000);
            }
        });
        refreshLayout.setOnLoadmoreListener(new OnLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                refreshlayout.finishLoadmore(2000);
            }
        });
        isPullRefresh = false;
        return view;
    }

    /**
     * 初始化fragment中的数据
     */
    @Override
    public void initData() {
        //从服务器获取Banner信息
        gotoGetBannerFromServer();

        //从服务器获取首页四个视频的列表
        gotoGetHomeVideoList();

        //从服务器获取美业信息
        gotoGetIndustryInfo();
    }

    private void gotoGetIndustryInfo() {
        OkHttpUtils.get().url(GlobalConstants.URL_INDUSTRY_NEWS_HOME).build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        LogUtils.show("首页", "----获取美业资讯失败----" + e.getMessage());
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        LogUtils.show("首页", "----获取美业资讯成功----" + response);

                        try {
                            parserIndustryInfoJson(response);
                        } catch (Exception e) {
                            e.printStackTrace();
                            LogUtils.show("首页", "----获取美业资讯解析异常----");
                        }
                    }
                });
    }

    private void parserIndustryInfoJson(String response) {
        Gson gson = new Gson();
        homeIndustryData = gson.fromJson(response, HomeIndustryData.class);
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
                        LogUtils.show("", "----获取4个视频列表失败----");
                        e.printStackTrace();
                        refreshLayout.finishRefresh();
                        llLoading.setVisibility(View.GONE);
                        llNetworkFailed.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        LogUtils.show(response, "----获取4个视频列表成功----");
                        refreshLayout.finishRefresh();
                        llLoading.setVisibility(View.GONE);
                        llNetworkFailed.setVisibility(View.GONE);
                        try {
                            parserVideoListJson(response);
                        } catch (Exception e) {
                            e.printStackTrace();
                            LogUtils.show("", "----获取4个视频列表解析失败----");
                        }
                    }
                });
    }

    private void parserVideoListJson(String response) {

        Gson gson = new Gson();
        homeVideoListData = gson.fromJson(response, HomeVideoListData.class);

        if (homeVideoListData != null) {
            lvLive.setAdapter(new LiveAdapter());
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

        BANNER_NUM = homeBannerDate.data.size();
        //根据Banner页数量, 初始化小圆点
        initBannerPoint();

        vpBanner.setAdapter(new BannerAdapter());
        vpBanner.setCurrentItem(BANNER_NUM * 10000);
        mHandler.removeMessages(MSG_EMPTY);
        mHandler.sendEmptyMessageDelayed(MSG_EMPTY, LIVE_BANNER_DELAYED_TIME);
    }

    private void initBannerPoint() {
        //根据Banner页数量, 初始化小圆点
        llPointContainer.removeAllViews();
        for (int i = 0; i < BANNER_NUM; i++) {
            ImageView point = new ImageView(mainActivity);

            //设置margin
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            //ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

            //从第二个点开始设置左边距
            if (i > 0) {
                params.leftMargin = 10;
                point.setImageResource(R.drawable.shape_banner_point_grey);
            } else {
                point.setImageResource(R.drawable.shape_banner_point_red);
            }
            //设置布局参数
            point.setLayoutParams(params);
            //将小圆点添加到线性布局上
            llPointContainer.addView(point);
        }
    }

    //ViewPager的适配器,继承PagerAdapter
    private class BannerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return Integer.MAX_VALUE;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            position = position % BANNER_NUM;
            ImageView imageView = new ImageView(mainActivity);

            if (bannerImgList != null && bannerImgList.size() > 0) {
                Glide.with(LiveFragment.this).load(bannerImgList.get(position).images).into(imageView);
            }
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

            int finalPosition = position;
            imageView.setOnClickListener(v -> {
                LogUtils.show(TAG, "点击了跳转");
                if (!TextUtils.isEmpty(bannerImgList.get(finalPosition).url)) {
                    startActivity(new Intent(mainActivity, WebViewActivity.class)
                            .putExtra("url", bannerImgList.get(finalPosition).url)
                            .putExtra("title", bannerImgList.get(finalPosition).title)
                            .putExtra("image", bannerImgList.get(finalPosition).images)
                            .putExtra("dev", "美页圈App")
                            .putExtra("url_id", bannerImgList.get(finalPosition).id)
                            .putExtra("news", true));
                }
            });
            container.addView(imageView);
            return imageView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }

    //ListView的适配器,继承BaseAdapter
    private class LiveAdapter extends BaseAdapter {
        @Override
        public int getViewTypeCount() {
            return 3;
        }

        @Override
        public int getItemViewType(int position) {

            if (position == 0 || position == 1) {
                return TYPE_LIVE_CONTENT;
            } else if (position == 2) {
                return TYPE_NEWS_TITLE;
            } else {
                return TYPE_NEWS_INFO;
            }
        }

        @Override
        public int getCount() {
            return 6;
        }

        @Override
        public Object getItem(int position) {
            /*if (position == 0 || position == 1) {
                return null;
            } else if (position == 2) {
                return null;
            } else {
                return homeIndustryData.data.get(position - 3);
            }*/
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = null;
            int type = getItemViewType(position);
            switch (type) {
                case TYPE_LIVE_CONTENT:

                    if (convertView == null) {
                        view = View.inflate(mainActivity, R.layout.item_live_content_double_and_title, null);
                    } else {
                        view = convertView;
                    }

                    TextView tvLiveTitle = (TextView) view.findViewById(R.id.tv_live_title);
                    TextView tvLiveMore = (TextView) view.findViewById(R.id.tv_live_more);

                    ImageView ivUserIconLeft = (ImageView) view.findViewById(R.id.iv_user_icon_left);
                    TextView tvUserNameLeft = (TextView) view.findViewById(R.id.tv_user_name_left);
                    TextView tvPeopleNumberLeft = (TextView) view.findViewById(R.id.tv_people_number_left);
                    TextView tvUserDesLeft = (TextView) view.findViewById(R.id.tv_user_des_left);
                    ImageView ivLiveScreenshotLeft = (ImageView) view.findViewById(R.id.iv_live_screenshot_left);
                    TextView tvLiveTitleTagLeft = (TextView) view.findViewById(R.id.tv_live_title_tag_left);
                    TextView tvLiveTimeLeft = (TextView) view.findViewById(R.id.tv_live_time_left);

                    ImageView ivUserIconRight = (ImageView) view.findViewById(R.id.iv_user_icon_right);
                    TextView tvUserNameRight = (TextView) view.findViewById(R.id.tv_user_name_right);
                    TextView tvPeopleNumberRight = (TextView) view.findViewById(R.id.tv_people_number_right);
                    TextView tvUserDesRight = (TextView) view.findViewById(R.id.tv_user_des_right);
                    ImageView ivLiveScreenshotRight = (ImageView) view.findViewById(R.id.iv_live_screenshot_right);
                    TextView tvLiveTitleTagRight = (TextView) view.findViewById(R.id.tv_live_title_tag_right);
                    TextView tvLiveTimeRight = (TextView) view.findViewById(R.id.tv_live_time_right);

                    if (position == 0) {
                        tvLiveTitle.setText("热门在线");
                        tvLiveMore.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                if (TextUtils.isEmpty(SPUtils.getString(getActivity(), GlobalConstants.userID, ""))) {
                                    ToastUtil.showShort(getActivity(), "您还未登录，请先去登录");
                                    return;
                                }

                                Intent hotLiveIntent = new Intent(mainActivity, HotCourseActivity.class);
                                hotLiveIntent.putExtra("COURSE", 1);
                                mainActivity.startActivity(hotLiveIntent);
                            }
                        });

                        if (homeVideoListData != null && homeVideoListData.data != null && homeVideoListData.data.size() > 1) {
                            tvUserNameLeft.setText(homeVideoListData.data.get(0).username);
                            tvPeopleNumberLeft.setText(homeVideoListData.data.get(0).view);
                            tvUserDesLeft.setText(homeVideoListData.data.get(0).title);
                            tvLiveTitleTagLeft.setText(homeVideoListData.data.get(0).coursename);
                            Glide.with(mainActivity).load(homeVideoListData.data.get(0).icon).into(ivUserIconLeft);
                            Glide.with(mainActivity).load(homeVideoListData.data.get(0).cover).into(ivLiveScreenshotLeft);
                            if (homeVideoListData.data.get(0).status == 1) {
                                tvLiveTimeLeft.setText("开课中");
                                tvLiveTimeLeft.setBackgroundResource(R.drawable.live_time_bg);
                            } else if (homeVideoListData.data.get(0).status == 2) {
                                tvLiveTimeLeft.setText(homeVideoListData.data.get(0).duration);
                                tvLiveTimeLeft.setBackgroundResource(R.drawable.course_time_bg);
                            } else {
                                tvLiveTimeLeft.setText(homeVideoListData.data.get(0).status1);
                                tvLiveTimeLeft.setBackgroundResource(R.drawable.live_time_bg);
                            }

                            tvUserNameRight.setText(homeVideoListData.data.get(1).username);
                            tvPeopleNumberRight.setText(homeVideoListData.data.get(1).view);
                            tvUserDesRight.setText(homeVideoListData.data.get(1).title);
                            tvLiveTitleTagRight.setText(homeVideoListData.data.get(1).coursename);
                            Glide.with(mainActivity).load(homeVideoListData.data.get(1).icon).into(ivUserIconRight);
                            Glide.with(mainActivity).load(homeVideoListData.data.get(1).cover).into(ivLiveScreenshotRight);
                            if (homeVideoListData.data.get(1).status == 1) {
                                tvLiveTimeRight.setText("开课中");
                                tvLiveTimeRight.setBackgroundResource(R.drawable.live_time_bg);
                            } else if (homeVideoListData.data.get(1).status == 2) {
                                tvLiveTimeRight.setText(homeVideoListData.data.get(1).duration);
                                tvLiveTimeRight.setBackgroundResource(R.drawable.course_time_bg);
                            } else {
                                tvLiveTimeRight.setText(homeVideoListData.data.get(1).status1);
                                tvLiveTimeRight.setBackgroundResource(R.drawable.live_time_bg);
                            }


                            ivUserIconLeft.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (!TextUtils.equals(homeVideoListData.data.get(0).userid, "0")) {
                                        Intent intent = new Intent(mainActivity, UserPagerActivity.class);
                                        intent.putExtra("LOOK_USER_ID", homeVideoListData.data.get(0).userid);
                                        mainActivity.startActivity(intent);
                                    }
                                }
                            });
                            /*
                            mediaType = "livestream";
                            mediaType = "videoondemand";
                            */
                            ivLiveScreenshotLeft.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    boolean isLoginState = SPUtils.getboolean(mainActivity, GlobalConstants.isLoginState, false);
                                    Intent intent = new Intent();
                                    if (isLoginState) {
                                        if (homeVideoListData.data.get(0).status == 1) {  //直播
                                            intent.setClass(mainActivity, NELivePlayerActivity.class);
                                            //intent.putExtra("media_type", "livestream");
                                        } else if (homeVideoListData.data.get(0).status == 2) {                                                      //点播
                                            //intent.putExtra("media_type", "videoondemand");
                                            intent.putExtra("coursename", homeVideoListData.data.get(0).coursename);
                                            intent.putExtra("des", homeVideoListData.data.get(0).des);
                                            intent.putExtra("view", homeVideoListData.data.get(0).view);
                                            intent.putExtra("video_id", homeVideoListData.data.get(0).vid);

                                            if (homeVideoListData.data.get(0).ismy.equals("0")) {   //0 自营  1 第三方录制
                                                intent.setClass(mainActivity, VideoPlayerActivity.class);
                                            } else {
                                                intent.setClass(mainActivity, VideoPlayerIsUserActivity.class);
                                            }
                                        }
                                        intent.putExtra("user_name", homeVideoListData.data.get(0).username);
                                        intent.putExtra("icon", homeVideoListData.data.get(0).icon);
                                        intent.putExtra("user_id", homeVideoListData.data.get(0).userid);
                                        LogUtils.show("首页讲师第一视频ID",homeVideoListData.data.get(0).userid);
                                        //                                        intent.putExtra("decode_type", "software");
                                        intent.putExtra("videoPath", homeVideoListData.data.get(0).addr.rtmpPullUrl);
                                        intent.putExtra("cid", homeVideoListData.data.get(0).cid); //直播间ID

                                        mainActivity.startActivity(intent);
                                    } else {
                                        intent.setClass(mainActivity, LoginActivity.class);
                                        mainActivity.startActivity(intent);
                                    }
                                }
                            });

                            ivLiveScreenshotRight.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    boolean isLoginState = SPUtils.getboolean(mainActivity, GlobalConstants.isLoginState, false);
                                    Intent intent = new Intent();
                                    if (isLoginState) {
                                        if (homeVideoListData.data.get(1).status == 1) { //直播
                                            intent.setClass(mainActivity, NELivePlayerActivity.class);
                                            //intent.putExtra("media_type", "livestream");
                                        } else if (homeVideoListData.data.get(1).status == 2) {                                                     //点播
                                            //intent.putExtra("media_type", "videoondemand");
                                            intent.putExtra("coursename", homeVideoListData.data.get(1).coursename);
                                            intent.putExtra("des", homeVideoListData.data.get(1).des);
                                            intent.putExtra("view", homeVideoListData.data.get(1).view);
                                            intent.putExtra("video_id", homeVideoListData.data.get(1).vid);

                                            if (homeVideoListData.data.get(1).ismy.equals("0")) {   //0 自营  1 第三方录制
                                                intent.setClass(mainActivity, VideoPlayerActivity.class);
                                            } else {
                                                intent.setClass(mainActivity, VideoPlayerIsUserActivity.class);
                                            }
                                        }
                                        intent.putExtra("user_id", homeVideoListData.data.get(1).userid);
                                        intent.putExtra("user_name", homeVideoListData.data.get(1).username);
                                        intent.putExtra("icon", homeVideoListData.data.get(1).icon);
                                        //                                        intent.putExtra("decode_type", "software");
                                        intent.putExtra("videoPath", homeVideoListData.data.get(1).addr.rtmpPullUrl);
                                        intent.putExtra("cid", homeVideoListData.data.get(1).cid); //直播间ID
                                        mainActivity.startActivity(intent);
                                    } else {
                                        intent.setClass(mainActivity, LoginActivity.class);
                                        mainActivity.startActivity(intent);
                                    }
                                }
                            });

                            ivUserIconRight.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (!TextUtils.equals(homeVideoListData.data.get(1).userid, "0")) {
                                        Intent intent = new Intent(mainActivity, UserPagerActivity.class);
                                        intent.putExtra("LOOK_USER_ID", homeVideoListData.data.get(1).userid);
                                        mainActivity.startActivity(intent);
                                    }
                                }
                            });
                        }

                    } else if (position == 1) {
                        tvLiveTitle.setText("会员在线");
                        tvLiveMore.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                if (TextUtils.isEmpty(SPUtils.getString(getActivity(), GlobalConstants.userID, ""))) {
                                    ToastUtil.showShort(getActivity(), "您还未登录，请先去登录");
                                    return;
                                }
                                Intent memberLiveIntent = new Intent(mainActivity, HotCourseActivity.class);
                                memberLiveIntent.putExtra("COURSE", 2);
                                mainActivity.startActivity(memberLiveIntent);
                            }
                        });


                        if (homeVideoListData != null && homeVideoListData.data != null && homeVideoListData.data.size() > 3) {

                            tvUserNameLeft.setText(homeVideoListData.data.get(2).username);
                            tvPeopleNumberLeft.setText(homeVideoListData.data.get(2).view);
                            tvUserDesLeft.setText(homeVideoListData.data.get(2).title);
                            tvLiveTitleTagLeft.setText(homeVideoListData.data.get(2).coursename);
                            Glide.with(mainActivity).load(homeVideoListData.data.get(2).icon).into(ivUserIconLeft);
                            Glide.with(mainActivity).load(homeVideoListData.data.get(2).cover).into(ivLiveScreenshotLeft);
                            if (homeVideoListData.data.get(2).status == 1) {
                                tvLiveTimeLeft.setText("开课中");
                                tvLiveTimeLeft.setBackgroundResource(R.drawable.live_time_bg);
                            } else if (homeVideoListData.data.get(2).status == 2) {
                                tvLiveTimeLeft.setText(homeVideoListData.data.get(2).duration);
                                tvLiveTimeLeft.setBackgroundResource(R.drawable.course_time_bg);
                            } else {
                                tvLiveTimeLeft.setText(homeVideoListData.data.get(2).status1);
                                tvLiveTimeLeft.setBackgroundResource(R.drawable.live_time_bg);
                            }

                            tvUserNameRight.setText(homeVideoListData.data.get(3).username);
                            tvPeopleNumberRight.setText(homeVideoListData.data.get(3).view);
                            tvUserDesRight.setText(homeVideoListData.data.get(3).title);
                            tvLiveTitleTagRight.setText(homeVideoListData.data.get(3).coursename);
                            Glide.with(mainActivity).load(homeVideoListData.data.get(3).icon).into(ivUserIconRight);
                            Glide.with(mainActivity).load(homeVideoListData.data.get(3).cover).into(ivLiveScreenshotRight);
                            if (homeVideoListData.data.get(3).status == 1) {
                                tvLiveTimeRight.setText("开课中");
                                tvLiveTimeRight.setBackgroundResource(R.drawable.live_time_bg);
                            } else if (homeVideoListData.data.get(3).status == 2) {
                                tvLiveTimeRight.setText(homeVideoListData.data.get(3).duration);
                                tvLiveTimeRight.setBackgroundResource(R.drawable.course_time_bg);
                            } else {
                                tvLiveTimeRight.setText(homeVideoListData.data.get(3).status1);
                                tvLiveTimeRight.setBackgroundResource(R.drawable.live_time_bg);
                            }

                            ivUserIconLeft.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (!TextUtils.equals(homeVideoListData.data.get(2).userid, "0")) {
                                        Intent intent = new Intent(mainActivity, UserPagerActivity.class);
                                        intent.putExtra("LOOK_USER_ID", homeVideoListData.data.get(2).userid);
                                        mainActivity.startActivity(intent);
                                    }
                                }
                            });

                            ivLiveScreenshotLeft.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    boolean isLoginState = SPUtils.getboolean(mainActivity, GlobalConstants.isLoginState, false);
                                    Intent intent = new Intent();
                                    if (isLoginState) {
                                        if (homeVideoListData.data.get(2).status == 1) { //直播
                                            intent.setClass(mainActivity, NELivePlayerActivity.class);
                                            //intent.putExtra("media_type", "livestream");
                                        } else if (homeVideoListData.data.get(2).status == 2) {                                                     //点播
                                            //intent.putExtra("media_type", "videoondemand");
                                            intent.putExtra("coursename", homeVideoListData.data.get(2).coursename);
                                            intent.putExtra("des", homeVideoListData.data.get(2).des);
                                            intent.putExtra("view", homeVideoListData.data.get(2).view);
                                            intent.putExtra("video_id", homeVideoListData.data.get(2).vid);


                                            if (homeVideoListData.data.get(2).ismy.equals("0")) {   //0 自营  1 第三方录制
                                                intent.setClass(mainActivity, VideoPlayerActivity.class);
                                            } else {
                                                intent.setClass(mainActivity, VideoPlayerIsUserActivity.class);
                                            }
                                        }
                                        intent.putExtra("user_name", homeVideoListData.data.get(2).username);
                                        intent.putExtra("user_id", homeVideoListData.data.get(2).userid);
                                        intent.putExtra("icon", homeVideoListData.data.get(2).icon);
                                        //intent.putExtra("decode_type", "software");
                                        intent.putExtra("videoPath", homeVideoListData.data.get(2).addr.rtmpPullUrl);
                                        intent.putExtra("cid", homeVideoListData.data.get(2).cid); //直播间ID
                                        mainActivity.startActivity(intent);
                                    } else {
                                        intent.setClass(mainActivity, LoginActivity.class);
                                        mainActivity.startActivity(intent);
                                    }
                                }
                            });

                            ivLiveScreenshotRight.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    boolean isLoginState = SPUtils.getboolean(mainActivity, GlobalConstants.isLoginState, false);
                                    Intent intent = new Intent();
                                    if (isLoginState) {
                                        if (homeVideoListData.data.get(3).status == 1) { //直播
                                            intent.setClass(mainActivity, NELivePlayerActivity.class);
                                            //intent.putExtra("media_type", "livestream");
                                        } else if (homeVideoListData.data.get(3).status == 2) {                                                     //点播
                                            //intent.putExtra("media_type", "videoondemand");
                                            intent.putExtra("coursename", homeVideoListData.data.get(3).coursename);
                                            intent.putExtra("des", homeVideoListData.data.get(3).des);
                                            intent.putExtra("view", homeVideoListData.data.get(3).view);
                                            intent.putExtra("video_id", homeVideoListData.data.get(3).vid);

                                            if (homeVideoListData.data.get(3).ismy.equals("0")) {   //0 自营  1 第三方录制
                                                intent.setClass(mainActivity, VideoPlayerActivity.class);
                                            } else {
                                                intent.setClass(mainActivity, VideoPlayerIsUserActivity.class);
                                            }

                                        }
                                        intent.putExtra("user_name", homeVideoListData.data.get(3).username);
                                        intent.putExtra("user_id", homeVideoListData.data.get(3).userid);
                                        intent.putExtra("icon", homeVideoListData.data.get(3).icon);
                                        //intent.putExtra("decode_type", "software");
                                        intent.putExtra("videoPath", homeVideoListData.data.get(3).addr.rtmpPullUrl);
                                        intent.putExtra("cid", homeVideoListData.data.get(3).cid); //直播间ID
                                        mainActivity.startActivity(intent);
                                    } else {
                                        intent.setClass(mainActivity, LoginActivity.class);
                                        mainActivity.startActivity(intent);
                                    }
                                }
                            });

                            ivUserIconRight.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (!TextUtils.equals(homeVideoListData.data.get(3).userid, "0")) {
                                        Intent intent = new Intent(mainActivity, UserPagerActivity.class);
                                        intent.putExtra("LOOK_USER_ID", homeVideoListData.data.get(3).userid);
                                        mainActivity.startActivity(intent);
                                    }
                                }
                            });
                        }
                    }
                    break;
                case TYPE_NEWS_TITLE:
                    //view = View.inflate(mainActivity, R.layout.item_industry_info_title, null);

                    if (convertView == null) {
                        view = View.inflate(mainActivity, R.layout.item_industry_info_title, null);
                    } else {
                        view = convertView;
                    }

                    //美业资讯
                    TextView tvIndustryInfoTitle = (TextView) view.findViewById(R.id.tv_industry_info_title);
                    tvIndustryInfoTitle.setText("美业资讯");
                    TextView tvIndustryInfoMore = (TextView) view.findViewById(R.id.tv_industry_info_more);
                    tvIndustryInfoMore.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent industryInfoIntent = new Intent(mainActivity, IndustryInfoActivity.class);
                            mainActivity.startActivity(industryInfoIntent);
                        }
                    });
                    break;
                case TYPE_NEWS_INFO:

                    if (convertView == null) {
                        view = View.inflate(mainActivity, R.layout.item_industry_info, null);
                        LogUtils.show(TAG, "getView: TYPE_NEWS_INFO ---------初始化----" + position);
                    } else {
                        view = convertView;
                    }

                    ImageView ivNewsImg = (ImageView) view.findViewById(R.id.iv_news_img);
                    TextView tvNewsTitle = (TextView) view.findViewById(R.id.tv_news_title);
                    TextView tvNewsDes = (TextView) view.findViewById(R.id.tv_news_des);
                    TextView tvNewsTime = (TextView) view.findViewById(R.id.tv_news_time);
                    TextView tvNewsScanNum = (TextView) view.findViewById(R.id.tv_news_scan_num);

                    if (homeIndustryData != null && homeIndustryData.data != null) {
                        //资讯的第0条是ListView的第3个条目,所以取数据时-3;
                        Glide.with(mainActivity).load(homeIndustryData.data.get(position - 3).images).into(ivNewsImg);
                        tvNewsTitle.setText(homeIndustryData.data.get(position - 3).title);
                        tvNewsDes.setText(homeIndustryData.data.get(position - 3).digest);
                        tvNewsTime.setText(homeIndustryData.data.get(position - 3).time);
                        tvNewsScanNum.setText(homeIndustryData.data.get(position - 3).hits);
                    }
                    break;
                default:
                    break;
            }
            return view;
        }
    }

    //点击事件在这里处理
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_search: //搜索
                Intent searchIntent = new Intent(mainActivity, SearchActivity.class);
                mainActivity.startActivity(searchIntent);
                break;
            case R.id.btn_again_load: //网络错误时,重新加载
                isPullRefresh = false;
                initData();
                break;
            case R.id.tv_class_course: //课程

                if (TextUtils.isEmpty(SPUtils.getString(getActivity(), GlobalConstants.userID, ""))) {
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                } else {
                    Intent courseIntent = new Intent(mainActivity, CourseNewActivity.class);
                    mainActivity.startActivity(courseIntent);
                }

                break;
            case R.id.tv_class_lecturer: //讲师
                if (TextUtils.isEmpty(SPUtils.getString(getActivity(), GlobalConstants.userID, ""))) {
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                } else {
                    Intent lecturerIntent = new Intent(mainActivity, LecturerActivity.class);
                    mainActivity.startActivity(lecturerIntent);
                }

                break;
            case R.id.tv_class_member: //会员
                if (TextUtils.isEmpty(SPUtils.getString(getActivity(), GlobalConstants.userID, ""))) {
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                } else {
                    Intent memberIntent = new Intent(mainActivity, MemberNewActivity.class);
                    mainActivity.startActivity(memberIntent);
                }

                break;
            case R.id.tv_class_ranking_list: //排行榜
                Intent rankingListIntent = new Intent(mainActivity, RankingListActivity.class);
                mainActivity.startActivity(rankingListIntent);
                break;
            default:
                break;
        }
    }
}