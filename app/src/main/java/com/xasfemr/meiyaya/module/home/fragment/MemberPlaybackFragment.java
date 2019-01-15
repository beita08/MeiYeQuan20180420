package com.xasfemr.meiyaya.module.home.fragment;


import android.content.Intent;
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
import com.xasfemr.meiyaya.module.college.activity.PlaybackListActivity;
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
 * 会员课程--回放
 */
public class MemberPlaybackFragment extends MVPBaseFragment {

    @BindView(R.id.rv_member_playback)
    RecyclerView   rvMemberPlayback;

    @BindView(R.id.refreshLayout)
    RefreshLayout refreshLayout;
    private LoadDataView loadDataView;
    private int pageNumber;

    private ArrayList<MemberCourseHotProtocol> memberCourseHotProtocols;

    private MemberPresenter memberPresenter;
    private MemberCourseHotAdapter memberCourseHotAdapter;




    @Override
    protected int layoutId() {
        return R.layout.fragment_member_course_playback;
    }

    @Override
    protected void initView() {

        memberCourseHotProtocols =new ArrayList<>();
        memberCourseHotAdapter=new MemberCourseHotAdapter(getActivity(), memberCourseHotProtocols);
        rvMemberPlayback.setLayoutManager(new GridLayoutManager(getActivity(), 2));

        rvMemberPlayback.setAdapter(memberCourseHotAdapter);


        refreshLayout.setRefreshHeader(new MeiYaYaHeader(getActivity()));
        refreshLayout.setRefreshFooter(new ClassicsFooter(getActivity()).setSpinnerStyle(SpinnerStyle.Scale));
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
               loadata(false);
            }
        });
        refreshLayout.setOnLoadmoreListener(new OnLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                loadata(true);
            }
        });
    }


    @Override
    protected void getLoadView(LoadDataView mLoadView) {
        this.loadDataView=mLoadView;
        loadDataView.setErrorListner(v -> loadata(false));

    }

    @Override
    public void initData() {
        loadata(false);

    }

    private void loadata(boolean isLoadMore) {
        if (isLoadMore){
            pageNumber++;
        }else {
            pageNumber=0;
        }

        loadDataView.changeStatusView(ViewStatus.START);

        HashMap<String,String> map =new HashMap<>();
        map.put("page", String.valueOf(pageNumber));
        memberPresenter.getMemberPlayback( map,new MemberHotIView() {
            @Override
            public void getMemberHotListSuccess(MemberCourseHotListProtocol memberCourseHotListProtocolist) {
                if (pageNumber==0){
                    memberCourseHotProtocols.clear();
                }
                if (refreshLayout!=null){
                    refreshLayout.finishRefresh();
                    refreshLayout.finishLoadmore();
                }
                memberCourseHotProtocols.addAll(memberCourseHotListProtocolist.list);
                memberCourseHotAdapter.notifyDataSetChanged();
                if (memberCourseHotListProtocolist.list.size()==0){
                    loadDataView.changeStatusView(ViewStatus.EMPTY);
                    if (pageNumber!=0){
                        ToastUtil.showShort(getActivity(),"暂无数据");
                    }

                }else {
                    loadDataView.changeStatusView(ViewStatus.SUCCESS);
                }



            }

            @Override
            public void getMemberHotListFailure(String msg) {
                if (refreshLayout!=null){
                    refreshLayout.finishRefresh();
                    refreshLayout.finishLoadmore();
                }
                ToastUtil.showShort(getActivity(),msg);
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
        memberPresenter=new MemberPresenter();
    }



    private void setEmpty(boolean isEmpty) {
        loadDataView.setFirstLoad();
        loadDataView.changeStatusView( isEmpty ? ViewStatus.EMPTY:ViewStatus.SUCCESS);
    }

}
