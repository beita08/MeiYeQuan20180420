package com.xasfemr.meiyaya.module.college.fragment;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.xasfemr.meiyaya.R;
import com.xasfemr.meiyaya.base.fragment.MVPBaseFragment;
import com.xasfemr.meiyaya.global.GlobalConstants;
import com.xasfemr.meiyaya.module.college.adapter.VideoCommentAdapter;
import com.xasfemr.meiyaya.module.college.presenter.CommentPresenter;
import com.xasfemr.meiyaya.module.college.protocol.CommentProttocol;
import com.xasfemr.meiyaya.module.college.view.CommentListIView;
import com.xasfemr.meiyaya.module.college.view.SendCommentIView;
import com.xasfemr.meiyaya.utils.LogUtils;
import com.xasfemr.meiyaya.utils.SPUtils;
import com.xasfemr.meiyaya.utils.ToastUtil;
import com.xasfemr.meiyaya.utils.ViewStatus;
import com.xasfemr.meiyaya.view.CommentFrame;
import com.xasfemr.meiyaya.view.LoadDataView;
import com.xasfemr.meiyaya.view.MeiYaYaHeader;
import com.xasfemr.meiyaya.view.progress.SFProgressDialog;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;
import org.simple.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 视频评论页面
 * 精品课程、自营录制、第三方录制  共用
 * 需构造传入视频id、videoType =1精品课程 =0 普通视频
 * Created by sen.luo on 2018/1/23.
 */

public class CommentFragment extends MVPBaseFragment{
    @BindView(R.id.refreshLayout)
    RefreshLayout refreshLayout;
    @BindView(R.id.lvComment)
    ListView lvComment;
    @BindView(R.id.root_View)
    RelativeLayout rootView;
    @BindView(R.id.layoutEmpty)
    LinearLayout layoutEmpty;

    @BindView(R.id.et_content_add)
    TextView etContent;
    @BindView(R.id.tv_content_send)
    TextView tvSend;

    private CommentPresenter commentPresenter;
    private SFProgressDialog progressDialog;
    private ArrayList<CommentProttocol> commentDataList;

    private VideoCommentAdapter commentAdapter;
    private LoadDataView loadDataView;



    private String videoID="";
    private String videoType="";

    public CommentFragment(String videoID,String type) {
        this.videoID = videoID;
        this.videoType=type;
    }

    @Override
    protected int layoutId() {
        return R.layout.fragment_comment;
    }

    @Override
    protected void initView() {
        EventBus.getDefault().register(this);
        progressDialog=new SFProgressDialog(getActivity());

        commentDataList=new ArrayList<>();
        commentAdapter=new VideoCommentAdapter(commentDataList,getActivity());
        lvComment.setAdapter(commentAdapter);


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

        this.loadDataView= mLoadView;


        this.loadDataView.setErrorListner(v -> loadData(false));
    }

    @Override
    protected void initData() {
        loadData(false);
    }


    @OnClick(R.id.et_content_add)
    public void onViewClick(View view){

        switch (view.getId()){

            case R.id.et_content_add:
                CommentFrame.liveCommentEdit(getActivity(), rootView, new ContentResult());
                break;
        }
    }
    private class ContentResult implements CommentFrame.liveCommentResult {
        @Override
        public void onResult(boolean confirmed, String comment) {
//            comment = removeEnterKey(comment);
            if (progressDialog!=null){
                progressDialog.show();
            }
            gotoSendComment(comment);
        }
    }

    /**
     * 发送评论
     * @param comment 评论内容
     */
    private void gotoSendComment(String comment) {
        HashMap<String,String>map =new HashMap<>();
        map.put("userid", SPUtils.getString(getActivity(), GlobalConstants.userID,""));
        map.put("content", comment);
        map.put("videotype", videoType);
        map.put("seeding_id",videoID);

        commentPresenter.sendComment(map, new SendCommentIView() {
            @Override
            public void sendCommentSuccess(String msg) {
                ToastUtil.showShort(getActivity(),msg);
                loadData(false);
            }

            @Override
            public void sendCommentOnFailre(String msg) {
                ToastUtil.showShort(getActivity(),msg);
                if (progressDialog!=null){
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onNetworkFailure(String message) {
                ToastUtil.showShort(getActivity(),message);
                if (progressDialog!=null){
                    progressDialog.dismiss();
                }

            }
        });

    }


    private int pageNumber;

    /**
     * 获取评论列表
     * @param isLoadMore 是否加载更多
     */
    private void loadData(boolean isLoadMore) {
        loadDataView.changeStatusView(ViewStatus.START);
        if(isLoadMore){
            pageNumber++;
        }else {
            pageNumber=0;
        }

        HashMap<String,String>map =new HashMap<>();
        map.put("userid", SPUtils.getString(getActivity(), GlobalConstants.userID,""));
        map.put("seeding_id",videoID);
        map.put("page", String.valueOf(pageNumber));
        map.put("videotype", videoType);

        commentPresenter.getCommentList(map, new CommentListIView() {
            @Override
            public void getCommentListSuccess(ArrayList<CommentProttocol> commentProtocolList) {
                if (pageNumber==0){
                    commentDataList.clear();
                }
                if (refreshLayout!=null){
                    refreshLayout.finishRefresh();
                    refreshLayout.finishLoadmore();
                }

                if (progressDialog!=null&&progressDialog.isShowing()){
                    progressDialog.dismiss();
                }

                if (commentProtocolList!=null){
                    commentDataList.addAll(commentProtocolList);
                    commentAdapter.notifyDataSetChanged();
                    layoutEmpty.setVisibility(View.GONE);
                }else {
                    if (pageNumber==0){
                        layoutEmpty.setVisibility(View.VISIBLE);
                    }else {
                        layoutEmpty.setVisibility(View.GONE);
                    }

                }



                loadDataView.setFirstLoad();
                loadDataView.changeStatusView(ViewStatus.SUCCESS);

//                if (commentProtocolList.size()==0){
//                    layoutEmpty.setVisibility(View.VISIBLE);
//                }else {
//                    layoutEmpty.setVisibility(View.GONE);
//                }
            }

            @Override
            public void getCommentListOnFailure(String msg) {
                ToastUtil.showShort(getActivity(),msg);
                if (refreshLayout!=null){
                    refreshLayout.finishRefresh();
                    refreshLayout.finishLoadmore();
                }

                loadDataView.changeStatusView(ViewStatus.FAILURE);

            }

            @Override
            public void onNetworkFailure(String message) {
                ToastUtil.showShort(getActivity(),message);
                if (refreshLayout!=null){
                    refreshLayout.finishRefresh();
                    refreshLayout.finishLoadmore();
                }

                loadDataView.changeStatusView(ViewStatus.NOTNETWORK);
            }
        });

    }


    /**
     * 删除评论
     * @param commentId
     */
    private void deleteComment(String commentId){


        HashMap<String,String>map =new HashMap<>();
        map.put("userid", SPUtils.getString(getActivity(), GlobalConstants.userID,""));
        map.put("id",commentId);
        commentPresenter.deleteComment(map, new SendCommentIView() {
            @Override
            public void sendCommentSuccess(String msg) {
                if (progressDialog!=null){
                    progressDialog.dismiss();
                }
                ToastUtil.showShort(getActivity(),msg);
            }

            @Override
            public void sendCommentOnFailre(String msg) {
                if (progressDialog!=null){
                    progressDialog.dismiss();
                }
                ToastUtil.showShort(getActivity(),msg);
            }

            @Override
            public void onNetworkFailure(String message) {
                if (progressDialog!=null){
                    progressDialog.dismiss();
                }
                ToastUtil.showShort(getActivity(),message);
            }
        });
    }


    @Override
    protected void initPresenter() {
        commentPresenter=new CommentPresenter();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        commentPresenter.destroy();
        EventBus.getDefault().unregister(this);
    }


    //删除评论
    @Subscriber(mode = ThreadMode.MAIN,tag = GlobalConstants.EventBus.DELETE_VIDEO_comment)
    public void getChangeVideo(String msg){
        LogUtils.show("评论Id",msg);

        if (progressDialog!=null){
            progressDialog.show();
        }
        deleteComment(msg);
    }


}
