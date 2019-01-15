package com.xasfemr.meiyaya.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.xasfemr.meiyaya.R;
import com.xasfemr.meiyaya.activity.LoginActivity;
import com.xasfemr.meiyaya.activity.UserPagerActivity;
import com.xasfemr.meiyaya.base.fragment.MVPBaseFragment;
import com.xasfemr.meiyaya.global.GlobalConstants;
import com.xasfemr.meiyaya.module.home.IView.MemberHotIView;
import com.xasfemr.meiyaya.module.home.adapter.MemberCourseHotAdapter;
import com.xasfemr.meiyaya.module.home.presenter.MemberPresenter;
import com.xasfemr.meiyaya.module.home.protocol.MemberCourseHotListProtocol;
import com.xasfemr.meiyaya.module.home.protocol.MemberCourseHotProtocol;
import com.xasfemr.meiyaya.utils.SPUtils;
import com.xasfemr.meiyaya.utils.ToastUtil;
import com.xasfemr.meiyaya.utils.ViewStatus;
import com.xasfemr.meiyaya.view.LoadDataView;
import com.xasfemr.meiyaya.view.MeiYaYaHeader;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;

/**
 * 课程--回放
 */
public class MemberCoursePlaybackFragment extends MVPBaseFragment {

    @BindView(R.id.rv_member_playback)
    RecyclerView   rvMemberPlayback;

    @BindView(R.id.refreshLayout)
    RefreshLayout refreshLayout;
    private LoadDataView loadDataView;
    private int pageNumber;

    private ArrayList<MemberCourseHotProtocol> memberCourseHotProtocols;

    private MemberPresenter memberPresenter;

    private String net_typeid="";
    private MemberCourseHotAdapter memberCourseHotAdapter;



    public MemberCoursePlaybackFragment(String net_typeid) {
        this.net_typeid = net_typeid;
    }


    @Override
    protected int layoutId() {
        return R.layout.fragment_member_course_playback;
    }

    @Override
    protected void initView() {

        memberCourseHotProtocols =new ArrayList<>();
        rvMemberPlayback.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        memberCourseHotAdapter=new MemberCourseHotAdapter(getActivity(), memberCourseHotProtocols);
        rvMemberPlayback.setAdapter(memberCourseHotAdapter);


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

    private void loadData(boolean b) {
        loadDataView.changeStatusView(ViewStatus.START);
        if (b){
            pageNumber++;
        }else {
            pageNumber=0;
        }
        HashMap<String,String> map =new HashMap<>();
        map.put("type",net_typeid);
        map.put("page", String.valueOf(pageNumber));

        memberPresenter.getMemberPlaybackList(map, new MemberHotIView() {
            @Override
            public void getMemberHotListSuccess(MemberCourseHotListProtocol memberCourseHotListProtocolist) {
                if (pageNumber==0){
                    memberCourseHotProtocols.clear();
                }
                memberCourseHotProtocols.addAll(memberCourseHotListProtocolist.list);
                memberCourseHotAdapter.notifyDataSetChanged();

                if (refreshLayout!=null){
                    refreshLayout.finishRefresh();
                    refreshLayout.finishLoadmore();
                }

                if (memberCourseHotListProtocolist.list.size()==0){
                    loadDataView.changeStatusView(ViewStatus.EMPTY);
                    if (pageNumber!=0){
                        ToastUtil.showShort(getActivity(),"暂无数据");
                    }
                    return;
                }else {
                    loadDataView.changeStatusView(ViewStatus.SUCCESS);
                }

            }

            @Override
            public void getMemberHotListFailure(String msg) {
//                if (msg.equals("暂无数据")){
//                    loadDataView.changeStatusView(ViewStatus.EMPTY);
//                }else {
//                    loadDataView.changeStatusView(ViewStatus.FAILURE);
//
//                }
                loadDataView.changeStatusView(ViewStatus.FAILURE);
                ToastUtil.showShort(getActivity(),msg);


                if (refreshLayout!=null){
                    refreshLayout.finishRefresh();
                    refreshLayout.finishLoadmore();
                }


            }

            @Override
            public void onNetworkFailure(String message) {
                loadDataView.changeStatusView(ViewStatus.NOTNETWORK);
                if (refreshLayout!=null){
                    refreshLayout.finishRefresh();
                    refreshLayout.finishLoadmore();
                }
                ToastUtil.showShort(getActivity(),message);


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
        loadData(false);

    }

    @Override
    protected void initPresenter() {
        memberPresenter=new MemberPresenter();
    }



    private void setEmpty(boolean isEmpty) {
        loadDataView.setFirstLoad();
        loadDataView.changeStatusView( isEmpty ? ViewStatus.EMPTY:ViewStatus.SUCCESS);
    }

}
