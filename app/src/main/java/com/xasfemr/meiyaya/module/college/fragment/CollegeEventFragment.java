package com.xasfemr.meiyaya.module.college.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.xasfemr.meiyaya.R;
import com.xasfemr.meiyaya.base.fragment.MVPBaseFragment;
import com.xasfemr.meiyaya.main.MainActivity;
import com.xasfemr.meiyaya.module.college.adapter.CollegeEventAdapter;
import com.xasfemr.meiyaya.module.college.presenter.CollegePresenter;
import com.xasfemr.meiyaya.module.college.protocol.CollegeEventProtocol;
import com.xasfemr.meiyaya.module.college.view.CollegeEventIView;
import com.xasfemr.meiyaya.utils.ToastUtil;
import com.xasfemr.meiyaya.utils.ViewStatus;
import com.xasfemr.meiyaya.view.LoadDataView;
import com.xasfemr.meiyaya.view.MeiYaYaHeader;

import java.util.ArrayList;

import butterknife.BindView;

/**
 * 商学院--活动
 */
public class CollegeEventFragment extends MVPBaseFragment {


    @BindView(R.id.rv_college_event)
    RecyclerView rvCollegeEvent;
    @BindView(R.id.refreshLayout)
    RefreshLayout refreshLayout;

    private LoadDataView loadDataView;
    private CollegePresenter collegePresenter;

    private ArrayList<CollegeEventProtocol> collegeEventProtocols;

    private CollegeEventAdapter collegeEventAdapter;

    @Override
    protected int layoutId() {
        return R.layout.fragment_college_event;
    }

    @Override
    protected void initView() {
        collegeEventProtocols=new ArrayList<>();

        collegeEventAdapter=new CollegeEventAdapter(getActivity(),collegeEventProtocols);
        rvCollegeEvent.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvCollegeEvent.setAdapter(collegeEventAdapter);


        refreshLayout.setRefreshHeader(new MeiYaYaHeader(getActivity()));
        refreshLayout.setEnableLoadmore(false);
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                initData();
            }
        });

    }

    @Override
    protected void getLoadView(LoadDataView mLoadView) {
        this.loadDataView=mLoadView;
        loadDataView.setErrorListner(v -> initData());
    }


    @Override
    public void initData() {
        loadDataView.changeStatusView(ViewStatus.START);

        collegePresenter.getCollegeEventList(new CollegeEventIView() {
            @Override
            public void getCollegeEventListSuccess(ArrayList<CollegeEventProtocol> collegeEventProtocolList) {
                collegeEventProtocols.clear();
                collegeEventProtocols.addAll(collegeEventProtocolList);
                setEmpty(collegeEventProtocolList.size()==0);
                if (refreshLayout!=null){
                    refreshLayout.finishRefresh();
                }
            }

            @Override
            public void getCollegeEventListFailure(String msg) {
                if (refreshLayout!=null){
                    refreshLayout.finishRefresh();
                }
                ToastUtil.showShort(getActivity(),msg);
                loadDataView.changeStatusView(ViewStatus.FAILURE);

            }

            @Override
            public void onNetworkFailure(String message) {
                if (refreshLayout!=null){
                    refreshLayout.finishRefresh();
                }
                ToastUtil.showShort(getActivity(),message);
                loadDataView.changeStatusView(ViewStatus.NOTNETWORK);

            }
        });

    }

    @Override
    protected void initPresenter() {
        collegePresenter=new CollegePresenter();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        collegePresenter.destroy();
    }

    private void setEmpty(boolean isEmpty) {
        loadDataView.setFirstLoad();
        loadDataView.changeStatusView( isEmpty ? ViewStatus.EMPTY:ViewStatus.SUCCESS);
    }

}
