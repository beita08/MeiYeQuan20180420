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
import com.xasfemr.meiyaya.module.home.IView.InstrumentDetailIView;
import com.xasfemr.meiyaya.module.home.IView.InstrumentListIView;
import com.xasfemr.meiyaya.module.home.IView.PostFilterListIView;
import com.xasfemr.meiyaya.module.home.adapter.InstrumentAdapter;
import com.xasfemr.meiyaya.module.home.presenter.PostPresenter;
import com.xasfemr.meiyaya.module.home.protocol.InstrumentListProtocol;
import com.xasfemr.meiyaya.module.home.protocol.PostProtocol;
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
 * 仪器转让列表
 * Created by sen.luo on 2018/2/26.
 */

public class InstrumentTransferListActivity extends MVPBaseActivity implements RecruimentFilterAdapter.OnFilterClick{


    @BindView(R.id.refresh_Layout)
    RefreshLayout refreshLayout;
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


    private PostPresenter postPresenter;

    private RecruimentFilterAdapter recruimentFilterAdapter;
    private ArrayList<String> stringArrayList;

    private ArrayList<InstrumentListProtocol> instrumentListProtocolList;
    private InstrumentAdapter instrumentAdapter;

    private LoadDataView mLoadDataView;


    private String cusId="";
    private SFProgressDialog progressDialog;
    private String position="";

    @Override
    protected int layoutId() {
        return R.layout.activity_recruitment_list;
    }

    @Override
    protected ViewGroup loadDataViewLayout() {
        return layoutRoot;
    }

    @Override
    protected void initView() {
        tvTitle.setText("仪器转让");
        ivBack.setOnClickListener(view -> finish());

        progressDialog=new SFProgressDialog(this);


        refreshLayout.setRefreshHeader(new MeiYaYaHeader(this));
        refreshLayout.setRefreshFooter(new ClassicsFooter(this).setSpinnerStyle(SpinnerStyle.Scale));
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                loadInstrumentListData(false,false,position);
            }
        });
        refreshLayout.setOnLoadmoreListener(new OnLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                loadInstrumentListData(true,false,position);
            }
        });



        rvFilter.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,false));
        stringArrayList =new ArrayList<>();
        recruimentFilterAdapter=new RecruimentFilterAdapter(this,stringArrayList);
        rvFilter.setAdapter(recruimentFilterAdapter);
        recruimentFilterAdapter.setOnFilterClick(this);

        instrumentListProtocolList=new ArrayList<>();
        instrumentAdapter =new InstrumentAdapter(this,instrumentListProtocolList);
        lvRecruitment.setAdapter(instrumentAdapter);
    }

    @Override
    protected void initData() {
        mLoadDataView.changeStatusView(ViewStatus.START);
        loadFilterListData(); //获取分类
        loadInstrumentListData(false,false,"");

    }

    private int pageNumber=0;
    private void loadInstrumentListData( boolean isLoadMore,boolean isFilter,String position) {

        if (isLoadMore){
            pageNumber++;
        }else {
            pageNumber=0;
        }


        HashMap<String,String> map =new HashMap<>();
        map.put("catId","3");
        map.put("page", String.valueOf(pageNumber));
        LocationUtils.initLocation(this);

        map.put("longitude", Double.toString(LocationUtils.longitude));//经度
        map.put("latitude", Double.toString(LocationUtils.latitude));//纬度

        if (!TextUtils.isEmpty(position)){ //是否筛选
            map.put("cusvalue", position);
            map.put("cusid", cusId);

        }


        postPresenter.postInstrumentList(map, new InstrumentListIView() {
            @Override
            public void getInstrumentListSuccess(ArrayList<InstrumentListProtocol> instrumentListProtocols) {

                if (refreshLayout != null) {
                    refreshLayout.finishRefresh();
                    refreshLayout.finishLoadmore();
                }

                if (progressDialog!=null&&progressDialog.isShowing()){
                    progressDialog.dismiss();
                }

                if (pageNumber==0){
                    instrumentListProtocolList.clear();
                }


                if (instrumentListProtocols!=null){
                    instrumentListProtocolList.addAll(instrumentListProtocols);
                    instrumentAdapter.notifyDataSetChanged();

                }else {
                    ToastUtil.showShort(InstrumentTransferListActivity.this,"暂无数据");
                }


                if (pageNumber==0){
                    mLoadDataView.setFirstLoad();
                    mLoadDataView.changeStatusView(instrumentListProtocolList.size()==0 ||instrumentListProtocols==null?ViewStatus.EMPTY:ViewStatus.SUCCESS);
                }




            }

            @Override
            public void getInstrumentLsitOnFailure(String msg) {

                ToastUtil.showShort(InstrumentTransferListActivity.this,msg);
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
                ToastUtil.showShort(InstrumentTransferListActivity.this,message);

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
        map.put("catId","3");
        postPresenter.postRecruitment(map, new PostFilterListIView() {
            @Override
            public void getFilterListSuccess(PostProtocol postProtocol) {
                stringArrayList.addAll(postProtocol.customList);
                cusId=postProtocol.cusid;
                recruimentFilterAdapter.notifyDataSetChanged();

            }

            @Override
            public void getFilterListOnFailure(String msg) {
                ToastUtil.showShort(InstrumentTransferListActivity.this,msg);
                mLoadDataView.changeStatusView(ViewStatus.FAILURE);
            }

            @Override
            public void onNetworkFailure(String message) {
                ToastUtil.showShort(InstrumentTransferListActivity.this,message);
                mLoadDataView.changeStatusView(ViewStatus.FAILURE);

            }
        });
    }




    @Override
    protected void getLoadView(LoadDataView loadView) {
        this.mLoadDataView=loadView;
        this.mLoadDataView.setErrorListner(view -> {
            loadInstrumentListData(false,true,position);
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
            loadInstrumentListData(false,true,"");
        }else {
            position=pos;
            loadInstrumentListData(false,true,pos);
        }


    }
}
