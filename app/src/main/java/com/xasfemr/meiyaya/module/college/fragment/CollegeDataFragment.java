package com.xasfemr.meiyaya.module.college.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
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
import com.xasfemr.meiyaya.module.college.adapter.CollegeDataAdapter;
import com.xasfemr.meiyaya.module.college.presenter.CollegePresenter;
import com.xasfemr.meiyaya.module.college.protocol.CollegeDataProtocol;
import com.xasfemr.meiyaya.module.college.protocol.CourseProtocolList;
import com.xasfemr.meiyaya.module.college.view.CollegeDataIView;
import com.xasfemr.meiyaya.module.college.view.CourseIView;
import com.xasfemr.meiyaya.utils.ToastUtil;
import com.xasfemr.meiyaya.utils.ViewStatus;
import com.xasfemr.meiyaya.view.LoadDataView;
import com.xasfemr.meiyaya.view.MeiYaYaHeader;

import java.util.ArrayList;

import butterknife.BindView;

/**
 * 商学院--资料
 */
public class CollegeDataFragment extends MVPBaseFragment {


    @BindView(R.id.rv_college_data)
    RecyclerView rvCollegeData;
    @BindView(R.id.refreshLayout)
    RefreshLayout refreshLayout;


    private ArrayList<CollegeDataProtocol> collegeDataProtocolList;
    private CollegeDataAdapter collegeDataAdapter;

    private LoadDataView loadDataView;
    private CollegePresenter collegePresenter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    protected int layoutId() {
        return R.layout.fragment_college_data;
    }

    @Override
    protected void initView() {
        collegeDataProtocolList=new ArrayList<>();

        collegeDataAdapter=new CollegeDataAdapter(getActivity(),collegeDataProtocolList);
        rvCollegeData.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvCollegeData.setAdapter(collegeDataAdapter);

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
        this.loadDataView =mLoadView;
        loadDataView.setErrorListner(v -> initData());
    }

    @Override
    public void initData() {

    loadDataView.changeStatusView(ViewStatus.START);
        collegePresenter.getCollegeDataList(new CollegeDataIView() {
            @Override
            public void getCollegeDataListSuccess(ArrayList<CollegeDataProtocol> collegeDataProtocols) {
                collegeDataProtocolList.clear();
                collegeDataProtocolList.addAll(collegeDataProtocols);
                setEmpty(collegeDataProtocols.size()==0);
                if (refreshLayout!=null){
                    refreshLayout.finishRefresh();

                }

            }

            @Override
            public void getCollegeDataListFailure(String msg) {
                if (refreshLayout!=null){
                    refreshLayout.finishRefresh();
                }


                loadDataView.changeStatusView(ViewStatus.FAILURE);
                ToastUtil.showShort(getActivity(),msg);
            }

            @Override
            public void onNetworkFailure(String message) {
                if (refreshLayout!=null){
                    refreshLayout.finishRefresh();
                }
                loadDataView.changeStatusView(ViewStatus.NOTNETWORK);
                ToastUtil.showShort(getActivity(),message);

            }
        });

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        collegePresenter.destroy();
    }
    @Override
    protected void initPresenter() {
        collegePresenter=new CollegePresenter();
    }

    private void setEmpty(boolean isEmpty) {
        loadDataView.setFirstLoad();
        loadDataView.changeStatusView( isEmpty ? ViewStatus.EMPTY:ViewStatus.SUCCESS);
    }

}
