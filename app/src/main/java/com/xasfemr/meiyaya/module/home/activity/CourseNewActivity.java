package com.xasfemr.meiyaya.module.home.activity;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.xasfemr.meiyaya.R;
import com.xasfemr.meiyaya.base.activity.MVPBaseActivity;
import com.xasfemr.meiyaya.module.home.IView.CourseListIView;
import com.xasfemr.meiyaya.module.home.adapter.CourseListAdapter;
import com.xasfemr.meiyaya.module.home.presenter.HomePresenter;
import com.xasfemr.meiyaya.module.home.protocol.CourseListProtocol;
import com.xasfemr.meiyaya.utils.UiUtils;
import com.xasfemr.meiyaya.utils.ViewStatus;
import com.xasfemr.meiyaya.view.LoadDataView;
import com.xasfemr.meiyaya.view.MeiYaYaHeader;
import com.xasfemr.meiyaya.view.RecycleViewDivider;

import java.util.ArrayList;

import butterknife.BindView;

/**
 * 课程重构页面
 * Created by sen.luo on 2017/11/29.
 */

public class CourseNewActivity extends MVPBaseActivity {

    @BindView(R.id.rvCourseList)
    RecyclerView rvCourseList;
    @BindView(R.id.layout_content)
    LinearLayout layoutContent;
    @BindView(R.id.tv_top_title)
    TextView     tvTitle;
    @BindView(R.id.iv_top_back)
    ImageView    ivBack;

    @BindView(R.id.refresh_Layout)
    RefreshLayout refreshLayout;
    private HomePresenter homePresenter;

    private LoadDataView loadDataView;

    private ArrayList<CourseListProtocol> courseListProtocols;
    private CourseListAdapter             courseListAdapter;

    @Override
    protected int layoutId() {
        return R.layout.activity_course_new;
    }

    @Override
    protected ViewGroup loadDataViewLayout() {
        return layoutContent;
    }

    @Override
    protected void initView() {
        tvTitle.setText("分类");
        ivBack.setOnClickListener(v -> finish());


        refreshLayout.setRefreshHeader(new MeiYaYaHeader(this));
        refreshLayout.setEnableLoadmore(false);
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                initData();
            }
        });

        courseListProtocols = new ArrayList<>();
        courseListAdapter = new CourseListAdapter(this, courseListProtocols);
        rvCourseList.setLayoutManager(new LinearLayoutManager(this));
        rvCourseList.addItemDecoration(new RecycleViewDivider(this, LinearLayoutManager.HORIZONTAL, UiUtils.dp2px(this, 10), getResources().getColor(R.color.textcolor_f3f3f3)));
        rvCourseList.setAdapter(courseListAdapter);
    }

    @Override
    protected void initData() {

        loadDataView.changeStatusView(ViewStatus.START);
        homePresenter.getCourseListData(new CourseListIView() {
            @Override
            public void getCourseListSuccess(ArrayList<CourseListProtocol> courseListProtocolArrayList) {
                if (refreshLayout != null) {
                    refreshLayout.finishRefresh();
                }
                courseListProtocols.clear();
                courseListProtocols.addAll(courseListProtocolArrayList);
                loadDataView.changeStatusView(ViewStatus.SUCCESS);

                setEmpty(courseListProtocolArrayList.size() == 0);
            }

            @Override
            public void getCourseListFailure(String msg) {
                if (refreshLayout != null) {
                    refreshLayout.finishRefresh();
                }

                loadDataView.changeStatusView(ViewStatus.FAILURE);
            }

            @Override
            public void onNetworkFailure(String message) {
                if (refreshLayout != null) {
                    refreshLayout.finishRefresh();
                }

                loadDataView.changeStatusView(ViewStatus.NOTNETWORK);

            }
        });


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

    private void setEmpty(boolean isEmpty) {
        loadDataView.setFirstLoad();
        loadDataView.changeStatusView(isEmpty ? ViewStatus.EMPTY : ViewStatus.SUCCESS);
    }
}
