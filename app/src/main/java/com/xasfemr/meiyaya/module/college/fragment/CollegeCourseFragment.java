package com.xasfemr.meiyaya.module.college.fragment;


import android.content.Intent;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;

import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.xasfemr.meiyaya.R;
import com.xasfemr.meiyaya.activity.LoginActivity;
import com.xasfemr.meiyaya.activity.RankingListActivity;
import com.xasfemr.meiyaya.base.fragment.MVPBaseFragment;
import com.xasfemr.meiyaya.global.GlobalConstants;
import com.xasfemr.meiyaya.module.college.activity.CourseListActivity;
import com.xasfemr.meiyaya.module.college.activity.PlaybackListActivity;
import com.xasfemr.meiyaya.module.college.adapter.CourseAdapter;
import com.xasfemr.meiyaya.module.college.adapter.PlaybackAdapter;
import com.xasfemr.meiyaya.module.college.presenter.CollegePresenter;
import com.xasfemr.meiyaya.module.college.protocol.CourseProtocol;
import com.xasfemr.meiyaya.module.college.protocol.CourseProtocolList;
import com.xasfemr.meiyaya.module.college.protocol.PlaybackListProtocol;
import com.xasfemr.meiyaya.module.college.protocol.PlaybackProtocol;
import com.xasfemr.meiyaya.module.college.view.CourseIView;
import com.xasfemr.meiyaya.module.college.view.PlaybackIView;
import com.xasfemr.meiyaya.module.home.activity.CourseNewActivity;
import com.xasfemr.meiyaya.module.home.activity.MemberNewActivity;
import com.xasfemr.meiyaya.utils.SPUtils;
import com.xasfemr.meiyaya.utils.ToastUtil;
import com.xasfemr.meiyaya.utils.ViewStatus;
import com.xasfemr.meiyaya.view.LoadDataView;
import com.xasfemr.meiyaya.view.MeiYaYaHeader;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 商学院--课程
 */
public class CollegeCourseFragment extends MVPBaseFragment {

    @BindView(R.id.rvCollege)
    RecyclerView  rvCollege;
    @BindView(R.id.rvPlayback)
    RecyclerView  rvPlayback;
    @BindView(R.id.layoutCourse)
    LinearLayout  layoutCourse;
    @BindView(R.id.layoutPlayback)
    LinearLayout  layoutPlayback;
    @BindView(R.id.refreshLayout)
    RefreshLayout refreshLayout;

    @BindView(R.id.layout_empty)
    LinearLayout layoutEmpty; //直播数据为空时替换布局
    @BindView(R.id.layout_content)
    LinearLayout layoutContent;

    private LoadDataView     loadDataView;
    private CollegePresenter collegePresenter;

    private ArrayList<CourseProtocol>   courseProtocolListList;
    private ArrayList<PlaybackProtocol> playbackProtocolList;

    private CourseAdapter   courseAdapter;
    private PlaybackAdapter playbackAdapter;

    @Override
    protected int layoutId() {
        return R.layout.fragment_college_course;
    }

    @Override
    protected void initView() {

        refreshLayout.setRefreshHeader(new MeiYaYaHeader(getActivity()));
        refreshLayout.setEnableLoadmore(false);
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                initData();
            }
        });


        courseProtocolListList = new ArrayList<>();
        playbackProtocolList = new ArrayList<>();

        courseAdapter = new CourseAdapter(getActivity(), courseProtocolListList, false);
        rvCollege.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        rvCollege.setNestedScrollingEnabled(false);
        rvCollege.setAdapter(courseAdapter);

        playbackAdapter = new PlaybackAdapter(getActivity(), playbackProtocolList, false);
        rvPlayback.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        rvPlayback.setNestedScrollingEnabled(false);
        rvPlayback.setAdapter(playbackAdapter);

    }


    @Override
    protected void getLoadView(LoadDataView mLoadView) {
        this.loadDataView = mLoadView;
        loadDataView.setErrorListner(v -> initData());
    }

    @Override
    public void initData() {
        loadDataView.changeStatusView(ViewStatus.START);
        getCourseData();
        getPlaybackData();
    }

    /**
     * 回放列表
     */
    private void getPlaybackData() {
        HashMap<String, String> map = new HashMap<>();
        map.put("page", "0");
        map.put("ismy", "1");

        collegePresenter.getPlaybackListData(map, new PlaybackIView() {
            @Override
            public void getPlaybackListSuccess(PlaybackListProtocol playbackListProtocol) {
                if (refreshLayout != null) {
                    refreshLayout.finishRefresh();
                }

                loadDataView.changeStatusView(ViewStatus.SUCCESS);
                playbackProtocolList.clear();
                playbackProtocolList.addAll(playbackListProtocol.list);


            }

            @Override
            public void getPlaybackListFailure(String msg) {
                if (refreshLayout != null) {
                    refreshLayout.finishRefresh();
                }
                ToastUtil.showShort(getActivity(), msg);
                loadDataView.changeStatusView(ViewStatus.FAILURE);
            }

            @Override
            public void onNetworkFailure(String message) {
                if (refreshLayout != null) {
                    refreshLayout.finishRefresh();
                }
                ToastUtil.showShort(getActivity(), message);
                loadDataView.changeStatusView(ViewStatus.NOTNETWORK);

            }
        });
    }

    /**
     * 直播数据
     */
    private void getCourseData() {
        HashMap<String, String> map = new HashMap<>();
        map.put("page", "0");
        collegePresenter.getCollegeListData(map, new CourseIView() {
            @Override
            public void getCourseListSuccess(CourseProtocolList courseProtocolList) {
                if (refreshLayout != null) {
                    refreshLayout.finishRefresh();
                }
                courseProtocolListList.clear();
                courseProtocolListList.addAll(courseProtocolList.list);

                if (courseProtocolListList.size() > 0) {
                    layoutEmpty.setVisibility(View.GONE);
                    layoutContent.setVisibility(View.VISIBLE);
                } else {
                    layoutEmpty.setVisibility(View.VISIBLE);
                    layoutContent.setVisibility(View.GONE);
                }


            }

            @Override
            public void getCourseListFailure(String msg) {
                if (refreshLayout != null) {
                    refreshLayout.finishRefresh();
                }
                ToastUtil.showShort(getActivity(), msg);


            }

            @Override
            public void onNetworkFailure(String message) {
                if (refreshLayout != null) {
                    refreshLayout.finishRefresh();
                }
                ToastUtil.showShort(getActivity(), message);
            }
        });


    }

    @OnClick({R.id.layoutCourse, R.id.layoutPlayback, R.id.rl_college_course_classification, R.id.rl_college_course_member, R.id.rl_college_course_ranking_list})
    public void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.layoutCourse://直播
                startActivity(new Intent(getActivity(), CourseListActivity.class));
                break;

            case R.id.layoutPlayback: //回放
                startActivity(new Intent(getActivity(), PlaybackListActivity.class));
                break;

            case R.id.rl_college_course_classification: //分类
                if (TextUtils.isEmpty(SPUtils.getString(getActivity(), GlobalConstants.userID, ""))) {
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                } else {
                    Intent courseIntent = new Intent(getActivity(), CourseNewActivity.class);
                    startActivity(courseIntent);
                }
                break;

            case R.id.rl_college_course_member: //会员
                if (TextUtils.isEmpty(SPUtils.getString(getActivity(), GlobalConstants.userID, ""))) {
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                } else {
                    Intent memberIntent = new Intent(getActivity(), MemberNewActivity.class);
                    startActivity(memberIntent);
                }
                break;

            case R.id.rl_college_course_ranking_list: //排行榜
                Intent rankingListIntent = new Intent(getActivity(), RankingListActivity.class);
                startActivity(rankingListIntent);
                break;

            default:
                break;
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        collegePresenter.destroy();
    }

    @Override
    protected void initPresenter() {
        collegePresenter = new CollegePresenter();
    }


    private void setEmpty(boolean isEmpty) {
        loadDataView.setFirstLoad();
        loadDataView.changeStatusView(isEmpty ? ViewStatus.EMPTY : ViewStatus.SUCCESS);
    }

}
