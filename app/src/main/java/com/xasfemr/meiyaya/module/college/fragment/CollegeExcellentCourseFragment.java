package com.xasfemr.meiyaya.module.college.fragment;


import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.xasfemr.meiyaya.R;
import com.xasfemr.meiyaya.base.fragment.MVPBaseFragment;
import com.xasfemr.meiyaya.global.GlobalConstants;
import com.xasfemr.meiyaya.module.college.adapter.ExcellentAdapter;
import com.xasfemr.meiyaya.module.college.presenter.CollegePresenter;
import com.xasfemr.meiyaya.module.college.protocol.ExcellentListProtocol;
import com.xasfemr.meiyaya.module.college.view.ExcellentIView;
import com.xasfemr.meiyaya.utils.LogUtils;
import com.xasfemr.meiyaya.utils.SPUtils;
import com.xasfemr.meiyaya.utils.ToastUtil;
import com.xasfemr.meiyaya.utils.UiUtils;
import com.xasfemr.meiyaya.utils.ViewStatus;
import com.xasfemr.meiyaya.view.LoadDataView;
import com.xasfemr.meiyaya.view.MeiYaYaHeader;
import com.xasfemr.meiyaya.view.RecycleViewDivider;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;
import org.simple.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;

/**
 * 精品课程
 * A simple {@link Fragment} subclass.
 */
public class CollegeExcellentCourseFragment extends MVPBaseFragment {

    @BindView(R.id.refreshLayout)
    RefreshLayout refreshLayout;
    @BindView(R.id.rvExcellent)
    RecyclerView rvExcellent;


    private LoadDataView loadDataView;
    private CollegePresenter collegePresenter;
    private ExcellentAdapter excellentAdapter;
    private int pageNumber;

    private ArrayList<ExcellentListProtocol> excellentListProtocols;

    @Override
    protected int layoutId() {
        return R.layout.fragment_college_excellent_course;
    }

    @Override
    protected void initView() {
        EventBus.getDefault().register(this);
        excellentListProtocols=new ArrayList<>();
        excellentAdapter=new ExcellentAdapter(getActivity(),excellentListProtocols);
        rvExcellent.setLayoutManager(new GridLayoutManager(getActivity(),2));
//        rvExcellent.addItemDecoration(new RecycleViewDivider(getActivity(), LinearLayoutManager.VERTICAL, UiUtils.dp2px(getActivity(), 10), getResources().getColor(R.color.textcolor_f3f3f3)));
        rvExcellent.setAdapter(excellentAdapter);


        refreshLayout.setRefreshHeader(new MeiYaYaHeader(getActivity()));
        refreshLayout.setRefreshFooter(new ClassicsFooter(getActivity()).setSpinnerStyle(SpinnerStyle.Scale));
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

    }

    @Override
    protected void getLoadView(LoadDataView mLoadView) {
        this.loadDataView =mLoadView;
        this.loadDataView.setErrorListner(v -> initData());

    }

    @Override
    protected void initData() {
        loadData(false);

    }

    private void loadData(boolean isLoadMore) {
        if (isLoadMore){
            pageNumber++;
        }else {
            pageNumber=0;
        }

        loadDataView.changeStatusView(ViewStatus.START);
        HashMap<String,String>map =new HashMap<>();
        map.put("userid", SPUtils.getString(getActivity(), GlobalConstants.userID,""));
        map.put("page", String.valueOf(pageNumber));
        collegePresenter.getExcellentList(map,new ExcellentIView() {
            @Override
            public void getExcellentListSuccess(ArrayList<ExcellentListProtocol> excellentListProtocolList) {
                if (refreshLayout!=null){
                    refreshLayout.finishRefresh();
                    refreshLayout.finishLoadmore();
                }
                if (pageNumber==0){
                    excellentListProtocols.clear();
                }

                excellentListProtocols.addAll(excellentListProtocolList);
                excellentAdapter.notifyDataSetChanged();

                setEmpty(excellentListProtocolList.size()==0);
//                if (excellentListProtocolList.size()==0){
//                    loadDataView.changeStatusView(ViewStatus.EMPTY);
//                    if (pageNumber!=0){
//                        ToastUtil.showShort(getActivity(),"暂无数据");
//                    }
//
//                }else {
//                    loadDataView.changeStatusView(ViewStatus.SUCCESS);
//                }



            }

            @Override
            public void getExcellentListOnFailure(String message) {
                if (refreshLayout!=null){
                    refreshLayout.finishRefresh();
                    refreshLayout.finishLoadmore();
                }
                ToastUtil.showShort(getActivity(),message);
                loadDataView.changeStatusView(ViewStatus.FAILURE);
            }

            @Override
            public void onNetworkFailure(String message) {
                if (refreshLayout!=null){
                    refreshLayout.finishRefresh();
                    refreshLayout.finishLoadmore();
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

    private void setEmpty(boolean isEmpty) {
        loadDataView.setFirstLoad();
        loadDataView.changeStatusView( isEmpty ? ViewStatus.EMPTY:ViewStatus.SUCCESS);
    }


    @Subscriber(mode = ThreadMode.MAIN,tag = GlobalConstants.EventBus.UPDATE_EXCELLENT_COURSE_LIST)
    public void updateData(String update){
        LogUtils.show("精品课程","刷新列表啦");
      loadData(false);
        if (excellentAdapter!=null){
            excellentAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        collegePresenter.destroy();
        EventBus.getDefault().unregister(this);
    }
}

