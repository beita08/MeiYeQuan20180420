package com.xasfemr.meiyaya.module.college.fragment;


import android.graphics.Rect;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.xasfemr.meiyaya.R;
import com.xasfemr.meiyaya.base.fragment.MVPBaseFragment;
import com.xasfemr.meiyaya.global.GlobalConstants;
import com.xasfemr.meiyaya.module.college.adapter.LectureAdapter;
import com.xasfemr.meiyaya.module.college.presenter.CollegePresenter;
import com.xasfemr.meiyaya.module.college.protocol.AttentionEventProtocol;
import com.xasfemr.meiyaya.module.college.protocol.LectureProtocol;
import com.xasfemr.meiyaya.module.college.view.AttentionIView;
import com.xasfemr.meiyaya.module.college.view.LectureIView;
import com.xasfemr.meiyaya.module.home.activity.LecturerActivity;
import com.xasfemr.meiyaya.utils.LogUtils;
import com.xasfemr.meiyaya.utils.SPUtils;
import com.xasfemr.meiyaya.utils.ToastUtil;
import com.xasfemr.meiyaya.utils.UiUtils;
import com.xasfemr.meiyaya.utils.ViewStatus;
import com.xasfemr.meiyaya.view.LoadDataView;
import com.xasfemr.meiyaya.view.MeiYaYaHeader;
import com.xasfemr.meiyaya.view.progress.SFProgressDialog;


import junit.framework.Test;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;
import org.simple.eventbus.ThreadMode;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 商学院--讲师
 */
public class CollegeLectureFragment extends MVPBaseFragment {


    @BindView(R.id.rv_college_lecture)
    RecyclerView rvCollegeLecture;
    @BindView(R.id.refreshLayout)
    RefreshLayout refreshLayout;


    private LoadDataView loadDataView;
    private CollegePresenter collegePresenter;
    private ArrayList<LectureProtocol>lectureProtocols;

    private String userId="";

    private SFProgressDialog progressDialog;
    private  LectureAdapter lectureAdapter;
    private int pageNumber;

    @Override
    protected int layoutId() {
        return R.layout.fragment_college_lecture;
    }

    @Override
    protected void initView() {
        EventBus.getDefault().register(this);
        progressDialog=new SFProgressDialog(getActivity());

        lectureProtocols=new ArrayList<>();
        lectureAdapter=new LectureAdapter(getActivity(),lectureProtocols);
        rvCollegeLecture.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        rvCollegeLecture.addItemDecoration(new SpaceItemDecoration(UiUtils.dp2px(getActivity(), 10)));
        rvCollegeLecture.setAdapter(lectureAdapter);


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
        this.loadDataView=mLoadView;
        loadDataView.setErrorListner(v -> initData());

    }



    @Override
    public void initData() {


        loadData(false);

    }

    private void loadData(boolean isLoadMore) {
        if (TextUtils.isEmpty(SPUtils.getString(getActivity(),GlobalConstants.userID,""))){
            ToastUtil.showShort(getActivity(),"您未登录，请先去登录");
            if (refreshLayout.isRefreshing()){
                refreshLayout.finishRefresh();
            }
            return;
        }

        if (isLoadMore){
            pageNumber++;
        }else {
            pageNumber=0;
        }


        loadDataView.changeStatusView(ViewStatus.START);
        HashMap<String,String>map =new HashMap<>();
        map.put("id",SPUtils.getString(getActivity(), GlobalConstants.userID,""));
        map.put("page", String.valueOf(pageNumber));
        collegePresenter.getLectureListData(map,new LectureIView() {
            @Override
            public void getLectureListSuccess(ArrayList<LectureProtocol> lectureProtocoList) {
                if (pageNumber==0){
                    lectureProtocols.clear();
                }

                if (refreshLayout!=null){
                    refreshLayout.finishRefresh();
                    refreshLayout.finishLoadmore();
                }
                lectureProtocols.addAll(lectureProtocoList);
                lectureAdapter.notifyDataSetChanged();

              setEmpty(lectureProtocoList.size()==0);



            }

            @Override
            public void getLectureListFailure(String msg) {
                loadDataView.changeStatusView(ViewStatus.FAILURE);
                if (refreshLayout!=null){
                    refreshLayout.finishRefresh();
                    refreshLayout.finishLoadmore();
                }
                ToastUtil.showShort(getActivity(),msg);


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
    protected void initPresenter() {
        collegePresenter=new CollegePresenter();

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        collegePresenter.destroy();
        EventBus.getDefault().unregister(this);
    }


    /**
     * 发起关注
     * @param userId
     */
    private void getAttention(String userId,int postion){

        if (TextUtils.isEmpty(SPUtils.getString(getActivity(),GlobalConstants.userID,""))){
            ToastUtil.showShort(getActivity(),"您未登陆，请先去登陆");
            return;
        }

        if (SPUtils.getString(getActivity(),GlobalConstants.userID,"").equals(userId)){
            ToastUtil.showShort(getActivity(),"自己不能关注自己");
            return;
        }


        progressDialog.show();
        HashMap map =new HashMap();
        map.put("userid",SPUtils.getString(getActivity(),GlobalConstants.userID,""));
        map.put("uid",userId);

        collegePresenter.getAttention(map, new AttentionIView() {
            @Override
            public void getAttentionSuccess(String message) {
                progressDialog.dismiss();
                ToastUtil.showShort(getActivity(),message);
                lectureProtocols.get(postion).attention="1";
                lectureAdapter.notifyDataSetChanged();

            }

            @Override
            public void getAttentionOnFailure(String message) {
                progressDialog.dismiss();
                ToastUtil.showShort(getActivity(),message);
            }

            @Override
            public void onNetworkFailure(String message) {
                progressDialog.dismiss();
                ToastUtil.showShort(getActivity(),message);
            }
        });

    }


    /**
     * 取消关注
     * @param userId
     */
    private void cancelAttention(String userId,int postion){

        progressDialog.show();
        HashMap map =new HashMap();
        map.put("userid",SPUtils.getString(getActivity(),GlobalConstants.userID,""));
        map.put("uid",userId);


        collegePresenter.cancelAttention(map, new AttentionIView() {
            @Override
            public void getAttentionSuccess(String message) {
                progressDialog.dismiss();
                ToastUtil.showShort(getActivity(),message);
                lectureProtocols.get(postion).attention="0";
                lectureAdapter.notifyDataSetChanged();
            }

            @Override
            public void getAttentionOnFailure(String message) {
                progressDialog.dismiss();
                ToastUtil.showShort(getActivity(),message);
            }

            @Override
            public void onNetworkFailure(String message) {
                progressDialog.dismiss();
                ToastUtil.showShort(getActivity(),message);
            }
        });
    }


    @Subscriber(mode = ThreadMode.MAIN, tag = GlobalConstants.EventBus.GET_ATTENTION)
    public void onGetAttention(AttentionEventProtocol attentionEventProtocol) { //发起关注

        if (progressDialog==null){
            progressDialog=new SFProgressDialog(getActivity());
        }

        if (attentionEventProtocol.type==0){ //发起关注
            getAttention(attentionEventProtocol.userId,attentionEventProtocol.position);
        }else { //取消关注
            cancelAttention(attentionEventProtocol.userId,attentionEventProtocol.position);
        }
    }






    class SpaceItemDecoration extends RecyclerView.ItemDecoration {

        private int mSpace;

        public SpaceItemDecoration(int mSpace) {
            this.mSpace = mSpace;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            outRect.top = mSpace;
            outRect.right = mSpace;

            if (parent.getChildAdapterPosition(view) % 3 == 0) {
                outRect.left = mSpace;
            }
        }
    }


    private void setEmpty(boolean isEmpty) {
        loadDataView.setFirstLoad();
        loadDataView.changeStatusView( isEmpty ? ViewStatus.EMPTY:ViewStatus.SUCCESS);
    }

}
