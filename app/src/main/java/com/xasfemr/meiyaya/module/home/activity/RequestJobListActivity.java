package com.xasfemr.meiyaya.module.home.activity;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.xasfemr.meiyaya.R;
import com.xasfemr.meiyaya.base.activity.MVPBaseActivity;
import com.xasfemr.meiyaya.module.college.adapter.RecruimentFilterAdapter;
import com.xasfemr.meiyaya.module.home.IView.PostFilterListIView;
import com.xasfemr.meiyaya.module.home.IView.RecruitmentListIView;
import com.xasfemr.meiyaya.module.home.IView.RequestJobListIView;
import com.xasfemr.meiyaya.module.home.adapter.RequestJobAdapter;
import com.xasfemr.meiyaya.module.home.presenter.PostPresenter;
import com.xasfemr.meiyaya.module.home.protocol.PostProtocol;
import com.xasfemr.meiyaya.module.home.protocol.RecruitmentListProtocol;
import com.xasfemr.meiyaya.module.home.protocol.RequestJobListProtocol;
import com.xasfemr.meiyaya.utils.ToastUtil;
import com.xasfemr.meiyaya.utils.ViewStatus;
import com.xasfemr.meiyaya.view.LoadDataView;
import com.xasfemr.meiyaya.view.MeiYaYaHeader;
import com.xasfemr.meiyaya.view.progress.SFProgressDialog;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;

/**
 * 求职列表
 * Created by sen.luo on 2018/3/7.
 */

public class RequestJobListActivity extends MVPBaseActivity implements RecruimentFilterAdapter.OnFilterClick{

    @BindView(R.id.rvFilter)
    RecyclerView rvFilter;
    @BindView(R.id.root_View)
    LinearLayout root_View;
    @BindView(R.id.lvRequestJob)
    ListView lvRequestJob;

    @BindView(R.id.refresh_Layout)
    RefreshLayout refreshLayout;

    @BindView(R.id.tv_top_title)
    TextView tvTitle;
    @BindView(R.id.iv_top_back)
    ImageView ivBack;

    private RecruimentFilterAdapter recruimentFilterAdapter;
    private ArrayList<String> stringArrayList;
    private LoadDataView mLoadDataView;

    private PostPresenter postPresenter;

    private RequestJobAdapter requestJobAdapter;
    private ArrayList<RequestJobListProtocol> requestJobListProtocols;

    private String cusId="";
    private SFProgressDialog progressDialog;
    private String position="";
    @Override
    protected int layoutId() {
        return R.layout.activity_request_job_list;
    }

    @Override
    protected ViewGroup loadDataViewLayout() {
        return root_View;
    }

    @Override
    protected void initView() {


        tvTitle.setText("简历");
        ivBack.setOnClickListener(view -> finish());



        refreshLayout.setRefreshHeader(new MeiYaYaHeader(this));
        refreshLayout.setRefreshFooter(new ClassicsFooter(this).setSpinnerStyle(SpinnerStyle.Scale));
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                loadRequestJobData(false,false,position);
                }
        });
        refreshLayout.setOnLoadmoreListener(new OnLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                loadRequestJobData(true,false,position);
            }
        });


        rvFilter.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,false));
        stringArrayList =new ArrayList<>();
        recruimentFilterAdapter=new RecruimentFilterAdapter(this,stringArrayList);
        rvFilter.setAdapter(recruimentFilterAdapter);
        recruimentFilterAdapter.setOnFilterClick(this);



        requestJobListProtocols=new ArrayList<>();
        requestJobAdapter =new RequestJobAdapter(this,requestJobListProtocols);

        lvRequestJob.setAdapter(requestJobAdapter);

        progressDialog=new SFProgressDialog(this);
    }

    @Override
    protected void initData() {
        mLoadDataView.changeStatusView(ViewStatus.START);


        loadFilterListData(); //获取分类

        loadRequestJobData(false,false,"");
    }


    private int pageNumber=0;
    private void loadRequestJobData(boolean isLoadMore,boolean isFilter,String position) {

        if (isLoadMore){
            pageNumber++;
        }else {
            pageNumber=0;
        }

        HashMap<String,String> map =new HashMap<>();
        map.put("catId","6");
        map.put("page", String.valueOf(pageNumber));

        if (!TextUtils.isEmpty(position)){ //是否筛选
            map.put("cusvalue", position);
            map.put("cusid", cusId);

        }

        postPresenter.postRequestJobList(map, new RequestJobListIView() {
            @Override
            public void getRequestJobListSuccess(ArrayList<RequestJobListProtocol> requestJobList) {

                if (refreshLayout != null) {
                    refreshLayout.finishRefresh();
                    refreshLayout.finishLoadmore();
                }

                if (progressDialog!=null&&progressDialog.isShowing()){
                    progressDialog.dismiss();
                }

                if (pageNumber==0){
                    requestJobListProtocols.clear();
                }

                if (requestJobList!=null){
                    requestJobListProtocols.addAll(requestJobList);
                    requestJobAdapter.notifyDataSetChanged();


                }else {
                    ToastUtil.showShort(RequestJobListActivity.this,"暂无数据");
                }


                if (pageNumber==0){
                    mLoadDataView.setFirstLoad();
                    mLoadDataView.changeStatusView(requestJobListProtocols.size()==0 ||requestJobList==null?ViewStatus.EMPTY:ViewStatus.SUCCESS);
                }
            }

            @Override
            public void getRequestJobLsitOnFailure(String msg) {
                ToastUtil.showShort(RequestJobListActivity.this,msg);

//                mLoadDataView.setFirstLoad();
                mLoadDataView.changeStatusView(ViewStatus.FAILURE);
                if (refreshLayout != null) {
                    refreshLayout.finishRefresh();
                    refreshLayout.finishLoadmore();
                }
                if (progressDialog!=null&&progressDialog.isShowing()){
                    progressDialog.dismiss();
                }


            }

            @Override
            public void onNetworkFailure(String message) {
                ToastUtil.showShort(RequestJobListActivity.this,message);
                mLoadDataView.changeStatusView(ViewStatus.NOTNETWORK);
                if (refreshLayout != null) {
                    refreshLayout.finishRefresh();
                    refreshLayout.finishLoadmore();
                }

                if (progressDialog!=null&&progressDialog.isShowing()){
                    progressDialog.dismiss();
                }

            }
        });



    }


    private void loadFilterListData() {
        HashMap<String,String> map =new HashMap<>();
        map.put("catId","6");
        postPresenter.postRecruitment(map, new PostFilterListIView() {
            @Override
            public void getFilterListSuccess(PostProtocol postProtocol) {
                stringArrayList.addAll(postProtocol.customList);
                cusId=postProtocol.cusid;
                recruimentFilterAdapter.notifyDataSetChanged();

            }

            @Override
            public void getFilterListOnFailure(String msg) {
                ToastUtil.showShort(RequestJobListActivity.this,msg);
                mLoadDataView.changeStatusView(ViewStatus.FAILURE);
            }

            @Override
            public void onNetworkFailure(String message) {
                ToastUtil.showShort(RequestJobListActivity.this,message);
                mLoadDataView.changeStatusView(ViewStatus.FAILURE);

            }
        });
    }


    /**
     * 筛选回调
     * @param pos
     */
    @Override
    public void onClick(String pos) {
        lvRequestJob.smoothScrollToPosition(0);
        if (pos.equals("全部")){
            if (progressDialog!=null){
                progressDialog.show();
            }
            loadRequestJobData(false,true,"");
        }else {
            position=pos;
            loadRequestJobData(false,true,pos);
        }


    }


    @Override
    protected void getLoadView(LoadDataView loadView) {
        this.mLoadDataView=loadView;
        this.mLoadDataView.setErrorListner(view -> {
            loadRequestJobData(false,true,position);
        });
    }

    @Override
    protected void initPresenter() {
        postPresenter =new PostPresenter();
    }


}
