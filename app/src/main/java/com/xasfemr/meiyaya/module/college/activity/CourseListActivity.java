package com.xasfemr.meiyaya.module.college.activity;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.xasfemr.meiyaya.R;
import com.xasfemr.meiyaya.base.activity.MVPBaseActivity;
import com.xasfemr.meiyaya.module.college.adapter.CourseAdapter;
import com.xasfemr.meiyaya.module.college.presenter.CollegePresenter;
import com.xasfemr.meiyaya.module.college.protocol.CourseProtocol;
import com.xasfemr.meiyaya.module.college.protocol.CourseProtocolList;
import com.xasfemr.meiyaya.module.college.view.CourseIView;
import com.xasfemr.meiyaya.utils.ToastUtil;
import com.xasfemr.meiyaya.utils.ViewStatus;
import com.xasfemr.meiyaya.view.LoadDataView;
import com.xasfemr.meiyaya.view.MeiYaYaHeader;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;

/**
 * 所有直播列表
 * Created by sen.luo on 2017/11/24.
 */

public class CourseListActivity extends MVPBaseActivity{

    @BindView(R.id.layoutRoot)
    LinearLayout layoutRoot;
    @BindView(R.id.rvCourseList)
    RecyclerView rvCourseList;
    @BindView(R.id.tv_top_title)
    TextView tvTitle;
    @BindView(R.id.iv_top_back)
    ImageView ivBack;

    private LoadDataView loadDataView;
    private CollegePresenter collegePresenter;

    private ArrayList<CourseProtocol> courseProtocolListList;
    private CourseAdapter courseAdapter;

     @BindView(R.id.refresh_Layout)
     RefreshLayout refreshLayout;

    private int pageNumber;

    @Override
    protected int layoutId() {
        return R.layout.activity_course_list;
    }

    @Override
    protected ViewGroup loadDataViewLayout() {
        return layoutRoot;
    }

    @Override
    protected void initView() {
        tvTitle.setText("在线课程");
        ivBack.setOnClickListener(v -> finish());


        refreshLayout.setRefreshHeader(new MeiYaYaHeader(this));
        refreshLayout.setRefreshFooter(new ClassicsFooter(this).setSpinnerStyle(SpinnerStyle.Scale));
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                pageNumber=0;
                initData();
            }
        });

        refreshLayout.setOnLoadmoreListener(new OnLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                pageNumber++;
                initData();
            }
        });
        courseProtocolListList=new ArrayList<>();
        courseAdapter=new CourseAdapter(this,courseProtocolListList,true);
        rvCourseList.setLayoutManager(new GridLayoutManager(this, 2));
        rvCourseList.setNestedScrollingEnabled(false);
        rvCourseList.setAdapter(courseAdapter);

    }

    @Override
    protected void initData() {
        loadDataView.changeStatusView(ViewStatus.START);
        HashMap<String,String> map =new HashMap<>();
        map.put("page", String.valueOf(pageNumber));

        collegePresenter.getCollegeListData(map,new CourseIView() {
            @Override
            public void getCourseListSuccess(CourseProtocolList courseProtocolList) {
                if (refreshLayout!=null){
                    refreshLayout.finishRefresh();
                    refreshLayout.finishLoadmore();
                }

                if (pageNumber==0){
                    courseProtocolListList.clear();
                }
                courseProtocolListList.addAll(courseProtocolList.list);
                courseAdapter.notifyDataSetChanged();

                if (courseProtocolList.list.size()==0){
                    loadDataView.changeStatusView(ViewStatus.EMPTY);
                    if (pageNumber!=0){
                        ToastUtil.showShort(CourseListActivity.this,"暂无数据");
                    }

                }else {
                    loadDataView.changeStatusView(ViewStatus.SUCCESS);
                }

            }

            @Override
            public void getCourseListFailure(String msg) {
                if (refreshLayout!=null){
                    refreshLayout.finishRefresh();
                    refreshLayout.finishLoadmore();
                }

                ToastUtil.showShort(CourseListActivity.this,msg);
                loadDataView.changeStatusView(ViewStatus.FAILURE);

            }

            @Override
            public void onNetworkFailure(String message) {
                if (refreshLayout!=null){
                    refreshLayout.finishRefresh();
                    refreshLayout.finishLoadmore();
                }
                loadDataView.changeStatusView(ViewStatus.NOTNETWORK);
            }
        });

    }

    @Override
    protected void getLoadView(LoadDataView loadView) {
        this.loadDataView=loadView;
        loadDataView.setErrorListner(v -> initData());

    }

    @Override
    protected void initPresenter() {
        collegePresenter=new CollegePresenter();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        collegePresenter.destroy();
    }

    private void setEmpty(boolean isEmpty) {
        loadDataView.setFirstLoad();
        loadDataView.changeStatusView( isEmpty ? ViewStatus.EMPTY:ViewStatus.SUCCESS);
    }
}
