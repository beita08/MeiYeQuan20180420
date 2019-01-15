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
import com.xasfemr.meiyaya.module.college.adapter.PlaybackAdapter;
import com.xasfemr.meiyaya.module.college.presenter.CollegePresenter;
import com.xasfemr.meiyaya.module.college.protocol.PlaybackListProtocol;
import com.xasfemr.meiyaya.module.college.protocol.PlaybackProtocol;
import com.xasfemr.meiyaya.module.college.view.PlaybackIView;
import com.xasfemr.meiyaya.utils.ToastUtil;
import com.xasfemr.meiyaya.utils.ViewStatus;
import com.xasfemr.meiyaya.view.LoadDataView;
import com.xasfemr.meiyaya.view.MeiYaYaHeader;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;

/**
 * 所有回放列表
 * Created by sen.luo on 2017/11/24.
 */

public class PlaybackListActivity extends MVPBaseActivity{

    @BindView(R.id.layoutRoot)
    LinearLayout layoutRoot;
    @BindView(R.id.rvCourseList)
    RecyclerView rvCourseList;
    @BindView(R.id.tv_top_title)
    TextView tvTitle;
    @BindView(R.id.iv_top_back)
    ImageView ivBack;
    @BindView(R.id.refresh_Layout)
    RefreshLayout refreshLayout;

    private LoadDataView loadDataView;
    private CollegePresenter collegePresenter;

    private ArrayList<PlaybackProtocol> playbackProtocolList;
    private PlaybackAdapter playbackAdapter;

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
        tvTitle.setText("精彩回放");
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

        playbackProtocolList=new ArrayList<>();
        playbackAdapter=new PlaybackAdapter(this,playbackProtocolList,true);
        rvCourseList.setLayoutManager(new GridLayoutManager(this, 2));
        rvCourseList.setNestedScrollingEnabled(false);
        rvCourseList.setAdapter(playbackAdapter);

    }

    @Override
    protected void initData() {
        loadDataView.changeStatusView(ViewStatus.START);
        HashMap<String,String> map =new HashMap<>();
        map.put("page", String.valueOf(pageNumber));
        map.put("ismy", "1");

        collegePresenter.getPlaybackListData(map,new PlaybackIView() {
            @Override
            public void getPlaybackListSuccess(PlaybackListProtocol playbackListProtocol) {
                if (pageNumber==0){
                    playbackProtocolList.clear();
                }

                if (refreshLayout!=null){
                    refreshLayout.finishRefresh();
                    refreshLayout.finishLoadmore();
                }
                playbackProtocolList.addAll(playbackListProtocol.list);
                playbackAdapter.notifyDataSetChanged();

                if (playbackListProtocol.list.size()==0){
                    loadDataView.changeStatusView(ViewStatus.EMPTY);
                    if (pageNumber!=0){
                        ToastUtil.showShort(PlaybackListActivity.this,"暂无数据");
                    }

                }else {
                    loadDataView.changeStatusView(ViewStatus.SUCCESS);
                }

            }

            @Override
            public void getPlaybackListFailure(String msg) {
                if (refreshLayout!=null){
                    refreshLayout.finishRefresh();
                    refreshLayout.finishLoadmore();
                }

                ToastUtil.showShort(PlaybackListActivity.this,msg);
                loadDataView.changeStatusView(ViewStatus.FAILURE);
            }

            @Override
            public void onNetworkFailure(String message) {
                if (refreshLayout!=null){
                    refreshLayout.finishRefresh();
                    refreshLayout.finishLoadmore();
                }

                ToastUtil.showShort(PlaybackListActivity.this,message);
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
