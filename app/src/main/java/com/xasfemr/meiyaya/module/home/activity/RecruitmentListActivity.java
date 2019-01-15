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
import com.xasfemr.meiyaya.module.home.adapter.RecruitmentAdapter;
import com.xasfemr.meiyaya.module.home.presenter.PostPresenter;
import com.xasfemr.meiyaya.module.home.protocol.PostProtocol;
import com.xasfemr.meiyaya.module.home.protocol.RecruitmentListProtocol;
import com.xasfemr.meiyaya.utils.LocationUtils;
import com.xasfemr.meiyaya.utils.ToastUtil;
import com.xasfemr.meiyaya.utils.ViewStatus;
import com.xasfemr.meiyaya.view.LoadDataView;
import com.xasfemr.meiyaya.view.MeiYaYaHeader;
import com.xasfemr.meiyaya.view.progress.SFProgressDialog;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;

/**
 * 人才招聘列表
 * Created by sen.luo on 2018/2/26.
 */

public class RecruitmentListActivity extends MVPBaseActivity implements RecruimentFilterAdapter.OnFilterClick{



    @BindView(R.id.layoutRoot)
    LinearLayout layoutRoot;

    @BindView(R.id.lvRecruitment)
    ListView lvRecruitment;
    @BindView(R.id.rvFilter)
    RecyclerView rvFilter;

    @BindView(R.id.tv_top_title)
    TextView tvTitle;
    @BindView(R.id.iv_top_back)
    ImageView ivBack;

    @BindView(R.id.root_View)
    LinearLayout root_View;
    @BindView(R.id.refresh_Layout)
    RefreshLayout refreshLayout;

    private PostPresenter postPresenter;

    private ArrayList<String> stringArrayList;

    private LoadDataView mLoadDataView;

    private ArrayList<RecruitmentListProtocol> recruitmentListProtocols;

    private RecruitmentAdapter recruitmentAdapter;
    private RecruimentFilterAdapter recruimentFilterAdapter;


    private String cusId="";
    private SFProgressDialog progressDialog;

    private String position="";

    @Override
    protected int layoutId() {
        return R.layout.activity_recruitment_list;
    }

    @Override
    protected ViewGroup loadDataViewLayout() {
        return root_View;
    }

    @Override
    protected void initView() {
        tvTitle.setText("人才招聘");
        ivBack.setOnClickListener(view -> finish());


        refreshLayout.setRefreshHeader(new MeiYaYaHeader(this));
        refreshLayout.setRefreshFooter(new ClassicsFooter(this).setSpinnerStyle(SpinnerStyle.Scale));
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                loadRecruitmentListData(false,false,position);
            }
        });
        refreshLayout.setOnLoadmoreListener(new OnLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                loadRecruitmentListData(true,false,position);
            }
        });



            rvFilter.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,false));
            stringArrayList =new ArrayList<>();
            recruimentFilterAdapter=new RecruimentFilterAdapter(this,stringArrayList);
            rvFilter.setAdapter(recruimentFilterAdapter);
        recruimentFilterAdapter.setOnFilterClick(this);



        recruitmentListProtocols=new ArrayList<>();
        recruitmentAdapter =new RecruitmentAdapter(this,recruitmentListProtocols);
        lvRecruitment.setAdapter(recruitmentAdapter);

        progressDialog=new SFProgressDialog(this);

    }

    @Override
    protected void initData() {
        mLoadDataView.changeStatusView(ViewStatus.START);


        loadFilterListData(); //获取分类
        loadRecruitmentListData(false,false,""); //获取列表


    }

    private void loadFilterListData() {
        HashMap<String,String> map =new HashMap<>();
        map.put("catId","5");
        postPresenter.postRecruitment(map, new PostFilterListIView() {
            @Override
            public void getFilterListSuccess(PostProtocol postProtocol) {

                stringArrayList.addAll(postProtocol.customList);

                cusId=postProtocol.cusid;
                recruimentFilterAdapter.notifyDataSetChanged();

            }

            @Override
            public void getFilterListOnFailure(String msg) {
                ToastUtil.showShort(RecruitmentListActivity.this,msg);
                mLoadDataView.changeStatusView(ViewStatus.FAILURE);
            }

            @Override
            public void onNetworkFailure(String message) {
                ToastUtil.showShort(RecruitmentListActivity.this,message);
                mLoadDataView.changeStatusView(ViewStatus.FAILURE);

            }
        });
    }

    private int pageNumber=0;
    private void loadRecruitmentListData( boolean isLoadMore,boolean isFilter,String position) {

        if (isLoadMore){
            pageNumber++;
        }else {
            pageNumber=0;
        }


        HashMap<String,String> map =new HashMap<>();
        map.put("catId","5");
        map.put("page", String.valueOf(pageNumber));
        LocationUtils.initLocation(this);
        map.put("longitude", Double.toString(LocationUtils.longitude));//经度
        map.put("latitude", Double.toString(LocationUtils.latitude));//纬度

//        if (isFilter){ //是否筛选
//            map.put("cusvalue", position);
//            map.put("cusid", cusId);
//            if (progressDialog!=null){
//                progressDialog.show();
//            }
//        }


        if (!TextUtils.isEmpty(position)){
            map.put("cusvalue", position);
            map.put("cusid", cusId);

        }

        postPresenter.postRecruitmentList(map, new RecruitmentListIView() {
            @Override
            public void getRecruitmentListSuccess(ArrayList<RecruitmentListProtocol> protocolArrayList) {

                if (refreshLayout != null) {
                    refreshLayout.finishRefresh();
                    refreshLayout.finishLoadmore();
                }

                if (progressDialog!=null&&progressDialog.isShowing()){
                    progressDialog.dismiss();
                }

                if (pageNumber==0){
                    recruitmentListProtocols.clear();

                }

                if (protocolArrayList!=null){
                    recruitmentListProtocols.addAll(protocolArrayList);
                    recruitmentAdapter.notifyDataSetChanged();


                }else {
                    ToastUtil.showShort(RecruitmentListActivity.this,"暂无数据");
                }


                if (pageNumber==0){
                    mLoadDataView.setFirstLoad();
                    mLoadDataView.changeStatusView(recruitmentListProtocols.size()==0 ||protocolArrayList==null?ViewStatus.EMPTY:ViewStatus.SUCCESS);
                }


            }

            @Override
            public void getRecruimentLsitOnFailure(String msg) {
                ToastUtil.showShort(RecruitmentListActivity.this,msg);
//                mLoadDataView.setFirstLoad();
//                mLoadDataView.changeStatusView(msg.equals("暂无数据")?ViewStatus.EMPTY:ViewStatus.FAILURE);
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
                ToastUtil.showShort(RecruitmentListActivity.this,message);
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

    @Override
    protected void getLoadView(LoadDataView loadView) {
    this.mLoadDataView=loadView;

    this.mLoadDataView.setErrorListner(view -> {
        loadRecruitmentListData(false,true,position);


    });
    }

    @Override
    protected void initPresenter() {
        postPresenter=new PostPresenter();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        postPresenter.destroy();
    }

    @Override
    public void onClick(String pos) {

        lvRecruitment.smoothScrollToPosition(0);
        if (progressDialog!=null){
            progressDialog.show();
        }
        if (pos.equals("全部")){
            loadRecruitmentListData(false,true,"");
        }else {
            position=pos;
            loadRecruitmentListData(false,true,pos);
        }

    }
}
