package com.xasfemr.meiyaya.module.home.activity;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jakewharton.rxbinding.view.RxView;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.xasfemr.meiyaya.R;
import com.xasfemr.meiyaya.base.activity.MVPBaseActivity;
import com.xasfemr.meiyaya.module.college.protocol.HotCourseOrMemberProtocol;
import com.xasfemr.meiyaya.module.college.protocol.HotCourseProtocol;
import com.xasfemr.meiyaya.module.college.view.CourseHotIView;
import com.xasfemr.meiyaya.module.home.adapter.CourseHotAdapter;
import com.xasfemr.meiyaya.module.home.presenter.HomePresenter;
import com.xasfemr.meiyaya.utils.ToastUtil;
import com.xasfemr.meiyaya.utils.ViewStatus;
import com.xasfemr.meiyaya.view.LoadDataView;
import com.xasfemr.meiyaya.view.MeiYaYaHeader;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;


/**
 * 首页 热门课程、会员课程公用页面
 * 根据传值COURSE
 * 0 热门课程
 * 1 会员课程
 */
public class HotCourseActivity extends MVPBaseActivity {

    @BindView(R.id.rv_course)
    RecyclerView  rvCourse;
    @BindView(R.id.tv_top_title)
    TextView      tvTitle;
    @BindView(R.id.iv_top_back)
    ImageView     ivBack;
    @BindView(R.id.layoutRoot)
    LinearLayout  layoutRoot;
    @BindView(R.id.refresh_Layout)
    RefreshLayout refreshLayout;

    private LoadDataView loadDataView;
    private boolean isMember = false;
    private HomePresenter                homePresenter;
    private ArrayList<HotCourseProtocol> courseProtocolListList;
    private CourseHotAdapter             hotAdapter;
    private int                          pageNumber;

    @Override
    protected int layoutId() {
        return R.layout.activity_hot_live;
    }

    @Override
    protected ViewGroup loadDataViewLayout() {
        return layoutRoot;
    }

    @Override
    protected void initView() {
        if (getIntent().getIntExtra("COURSE", 0) == 1) {
            isMember = false;
        } else {
            isMember = true;
        }

        if (isMember) {
            tvTitle.setText("会员在线");
        } else {
            tvTitle.setText("热门在线");
        }


        refreshLayout.setRefreshHeader(new MeiYaYaHeader(this));
        refreshLayout.setRefreshFooter(new ClassicsFooter(this).setSpinnerStyle(SpinnerStyle.Scale));
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                loadData(false);
            }
        });
        refreshLayout.setOnLoadmoreListener(new OnLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                loadData(true);
            }
        });


        courseProtocolListList = new ArrayList<>();
        hotAdapter = new CourseHotAdapter(this, courseProtocolListList);
        rvCourse.setLayoutManager(new GridLayoutManager(this, 2));
        rvCourse.setNestedScrollingEnabled(false);
        rvCourse.setAdapter(hotAdapter);

        ivBack.setOnClickListener(v -> finish());

    }




    private void loadData(boolean b) {
        loadDataView.changeStatusView(ViewStatus.START);
        if (b) {
            pageNumber++;
        } else {
            pageNumber = 0;
        }


        HashMap<String, String> map = new HashMap<>();
        map.put("page", String.valueOf(pageNumber));
        if (isMember) {
            homePresenter.getMemberMoreList(map, new CourseHotIView() {

                @Override
                public void getCourseListSuccess(HotCourseOrMemberProtocol hotCourseOrMemberProtocols) {
                    if (refreshLayout != null) {
                        refreshLayout.finishRefresh();
                        refreshLayout.finishLoadmore();
                    }
                    if (pageNumber == 0) {
                        courseProtocolListList.clear();
                    }
                    courseProtocolListList.addAll(hotCourseOrMemberProtocols.list);
                    hotAdapter.notifyDataSetChanged();
                    setEmpty(hotCourseOrMemberProtocols.list.size() == 0);
//
//                    if (hotCourseOrMemberProtocols.list.size() == 0) {
//                        if (pageNumber != 0) {
//                            ToastUtil.showShort(HotCourseActivity.this, "暂无数据");
//                        }
//                        loadDataView.changeStatusView(ViewStatus.EMPTY);
//
//                        return;
//                    } else {
//                        loadDataView.changeStatusView(ViewStatus.SUCCESS);
//                    }


                }

                @Override
                public void getCourseListFailure(String msg) {
                    if (refreshLayout != null) {
                        refreshLayout.finishRefresh();
                        refreshLayout.finishLoadmore();
                    }

                    ToastUtil.showShort(HotCourseActivity.this, msg);
                    loadDataView.changeStatusView(ViewStatus.FAILURE);
                }


                @Override
                public void onNetworkFailure(String message) {
                    if (refreshLayout != null) {
                        refreshLayout.finishRefresh();
                        refreshLayout.finishLoadmore();
                    }

                    loadDataView.changeStatusView(ViewStatus.NOTNETWORK);
                }
            });

        } else {

            homePresenter.getHotCourseMoreLsit(map, new CourseHotIView() {
                @Override
                public void getCourseListSuccess(HotCourseOrMemberProtocol hotCourseOrMemberProtocols) {
                    if (refreshLayout != null) {
                        refreshLayout.finishRefresh();
                        refreshLayout.finishLoadmore();
                    }
                    if (pageNumber == 0) {
                        courseProtocolListList.clear();
                    }
                    courseProtocolListList.addAll(hotCourseOrMemberProtocols.list);
                    hotAdapter.notifyDataSetChanged();

                    setEmpty(hotCourseOrMemberProtocols.list.size() == 0);

//                    if (hotCourseOrMemberProtocols.list.size() == 0) {
//                        if (pageNumber != 0) {
//                            ToastUtil.showShort(HotCourseActivity.this, "暂无数据");
//                        }
//                        loadDataView.changeStatusView(ViewStatus.EMPTY);
//
//                        return;
//                    } else {
//                        loadDataView.changeStatusView(ViewStatus.SUCCESS);
//                    }

                }

                @Override
                public void getCourseListFailure(String msg) {
                    if (refreshLayout != null) {
                        refreshLayout.finishRefresh();
                        refreshLayout.finishLoadmore();
                    }
                    ToastUtil.showShort(HotCourseActivity.this, msg);
                    loadDataView.changeStatusView(ViewStatus.FAILURE);
                }

                @Override
                public void onNetworkFailure(String message) {
                    if (refreshLayout != null) {
                        refreshLayout.finishRefresh();
                        refreshLayout.finishLoadmore();
                    }

                    loadDataView.changeStatusView(ViewStatus.NOTNETWORK);

                }
            });

        }

    }

    @Override
    protected void initData() {
        loadData(false);

    }

    @Override
    protected void getLoadView(LoadDataView loadView) {
        this.loadDataView = loadView;
        loadDataView.setErrorListner(v -> initData());

    }

    @Override
    protected void initPresenter() {
        homePresenter = new HomePresenter();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        homePresenter.destroy();
    }


    private void setEmpty(boolean isEmpty) {
        loadDataView.setFirstLoad();
        loadDataView.changeStatusView(isEmpty ? ViewStatus.EMPTY : ViewStatus.SUCCESS);
    }

    //    @Override
    //    protected void onCreate(Bundle savedInstanceState) {
    //        super.onCreate(savedInstanceState);
    //        setContentView(R.layout.activity_hot_live);
    //        initTopBar();
    //        rvCourse = (RecyclerView) findViewById(R.id.rv_course);
    //
    //
    //        Intent intent = getIntent();
    //        int course = intent.getIntExtra("COURSE", 0);
    //        switch (course) {
    //            case 1:
    //                setTopTitleText("热门课程");
    //                gotoGetHotCourseFromServer();
    //                break;
    //            case 2:
    //                setTopTitleText("会员课程");
    //                break;
    //            case 3:
    //                setTopTitleText("课程直播");
    //                break;
    //            case 4:
    //                setTopTitleText("精彩回放");
    //                break;
    //            /*case 10:
    //                int[] classArr = intent.getIntArrayExtra("CLASS");
    //                System.out.println("CLASS---" + classArr[0] + "---" + classArr[1]);
    //                String title = secondClassStr[classArr[0] - 1][classArr[1] - 1];
    //                setTopTitleText(title);
    //                break;*/
    //            default:
    //                setTopTitleText("跳转错误");
    //                break;
    //        }
    //
    //
    //        rvCourse.setLayoutManager(new GridLayoutManager(this, 2));
    //        rvCourse.setAdapter(new CourseAdapter());
    //    }

    //    private void parserHotCourseJson(String response) {
    //        Gson gson = new Gson();
    //        MoreHotCourseData moreHotCourseData = gson.fromJson(response, MoreHotCourseData.class);
    //
    //
    //    }
    ////
    //    private class CourseAdapter extends RecyclerView.Adapter<CourseHolder> {
    //        /*
    //        private String[] mArray;
    //        //采用构造方法将要展示的数据的数组传进来,这样这个适配器就可以共用了
    //        public ClassAdapter(String[] strArray) {
    //            mArray = strArray;
    //        }
    //        */
    //        public CourseAdapter() {
    //
    //        }
    //
    //        @Override
    //        public CourseHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    //            View view = View.inflate(HotCourseActivity.this, R.layout.item_live_content_single, null);
    //            CourseHolder courseHolder = new CourseHolder(view);
    //
    //            return courseHolder;
    //        }
    //
    //        @Override
    //        public void onBindViewHolder(CourseHolder holder, int position) {
    //
    //            holder.ivUserIcon.setOnClickListener(new View.OnClickListener() {
    //                @Override
    //                public void onClick(View v) {
    //                    Intent intent = new Intent(HotCourseActivity.this, UserPagerActivity.class);
    //                    startActivity(intent);
    //                }
    //            });
    //
    //            holder.ivLiveScreenshot.setOnClickListener(new View.OnClickListener() {
    //                @Override
    //                public void onClick(View v) {
    //                    boolean isLoginState = SPUtils.getboolean(HotCourseActivity.this, GlobalConstants.isLoginState, false);
    //                    Intent intent = new Intent();
    //                    if (isLoginState) {
    //                        Toast.makeText(HotCourseActivity.this, "跳到视频播放页面", Toast.LENGTH_SHORT).show();
    //                    } else {
    //                        intent.setClass(HotCourseActivity.this, LoginActivity.class);
    //                        startActivity(intent);
    //                    }
    //                }
    //            });
    //        }
    //
    //        @Override
    //        public int getItemCount() {
    //            return 100;
    //        }
    //    }
    //
    //    static class CourseHolder extends RecyclerView.ViewHolder {
    //
    //        public ImageView ivUserIcon;
    //        public TextView  tvUserName;
    //        public TextView  tvPeopleNumber;
    //        public TextView  tvUserDes;
    //        public ImageView ivLiveScreenshot;
    //        public TextView  tvLiveTitleTag;
    //        public TextView  tvLiveTime;
    //
    //        public CourseHolder(View itemView) {
    //            super(itemView);
    //            ivUserIcon = (ImageView) itemView.findViewById(R.id.iv_user_icon);
    //            tvUserName = (TextView) itemView.findViewById(R.id.tv_user_name);
    //            tvPeopleNumber = (TextView) itemView.findViewById(R.id.tv_people_number);
    //            tvUserDes = (TextView) itemView.findViewById(R.id.tv_user_des);
    //            ivLiveScreenshot = (ImageView) itemView.findViewById(R.id.iv_live_screenshot);
    //            tvLiveTitleTag = (TextView) itemView.findViewById(R.id.tv_live_title_tag);
    //            tvLiveTime = (TextView) itemView.findViewById(R.id.tv_live_time);
    //        }
    //    }
}
