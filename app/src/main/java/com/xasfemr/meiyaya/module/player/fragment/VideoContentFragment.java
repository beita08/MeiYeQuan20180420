package com.xasfemr.meiyaya.module.player.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.xasfemr.meiyaya.R;
import com.xasfemr.meiyaya.activity.UserPagerActivity;
import com.xasfemr.meiyaya.base.fragment.MVPBaseFragment;
import com.xasfemr.meiyaya.global.GlobalConstants;
import com.xasfemr.meiyaya.module.college.presenter.CollegePresenter;
import com.xasfemr.meiyaya.module.college.view.AttentionIView;
import com.xasfemr.meiyaya.module.player.protocol.VideoSummaryProtocol;
import com.xasfemr.meiyaya.utils.LogUtils;
import com.xasfemr.meiyaya.utils.SPUtils;
import com.xasfemr.meiyaya.utils.ToastUtil;
import com.xasfemr.meiyaya.view.LoadDataView;
import com.xasfemr.meiyaya.view.progress.SFProgressDialog;
import com.xasfemr.meiyaya.weight.SFDialog;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by sen.luo on 2018/1/29.
 */

public class VideoContentFragment extends MVPBaseFragment{

    private VideoSummaryProtocol videoSummaryProtocol;

    private CollegePresenter collegePresenter;
    @BindView(R.id.civ_user_icon)
    ImageView ivUserIcon;
    @BindView(R.id.tv_user_name)
    TextView tvUserName;
    @BindView(R.id.tv_view_nums)
    TextView tvViewNums;
    @BindView(R.id.tv_coursename)
    TextView tvCoursename;
    @BindView(R.id.tv_des)
    TextView tvDes;
    @BindView(R.id.ivAttention)
    ImageView ivAttention;

    private boolean follew=false; //是否关注

    private SFProgressDialog progressDialog;

    public VideoContentFragment(VideoSummaryProtocol videoSummaryProtocol) {
        this.videoSummaryProtocol = videoSummaryProtocol;
    }

    @Override
    protected int layoutId() {
        return R.layout.fagment_video_content;
    }

    @Override
    protected void initView() {
        progressDialog=new SFProgressDialog(getActivity());

        tvUserName.setText(videoSummaryProtocol.userName);
        Glide.with(this).load(videoSummaryProtocol.icon).apply(new RequestOptions().placeholder(R.drawable.meiyaya_logo_round_gray)).into(ivUserIcon);

        tvDes.setText(videoSummaryProtocol.des);
        tvCoursename.setText(videoSummaryProtocol.coursename);
        tvViewNums.setText("播放"+videoSummaryProtocol.viewNums);
    }

    @Override
    protected void getLoadView(LoadDataView mLoadView) {

    }

    @Override
    protected void initData() {
        getIsAttention();

    }

    /**
     * 是否关注
     */
    private void getIsAttention() {
        HashMap map =new HashMap();
        map.put("userid", SPUtils.getString(getActivity(), GlobalConstants.userID,""));
        map.put("uid",videoSummaryProtocol.userID);
        collegePresenter.ifAttention(map, new AttentionIView() {
            @Override
            public void getAttentionSuccess(String message) {
//                loadDataView.changeStatusView(ViewStatus.SUCCESS);
                if (message.equals("已关注")){
                    follew=true;
                    ivAttention.setImageDrawable(getResources().getDrawable(R.drawable.focused));

                }else {
                    follew=false;
                    ivAttention.setImageDrawable(getResources().getDrawable(R.drawable.live_follow1));
                }
            }

            @Override
            public void getAttentionOnFailure(String message) {
                ToastUtil.showShort(getActivity(),message);
//                loadDataView.changeStatusView(ViewStatus.FAILURE);
            }

            @Override
            public void onNetworkFailure(String message) {
                ToastUtil.showShort(getActivity(),message);
//                loadDataView.changeStatusView(ViewStatus.NOTNETWORK);
            }
        });
    }




    @OnClick({R.id.ivAttention,R.id.civ_user_icon})
    public void onViewClick(View view){

        switch (view.getId()){
            case R.id.ivAttention:

                if (follew){
                    SFDialog.basicDialog(getActivity(), "提示", "确定取消关注吗？", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            cancelAttention();
                        }
                    });

                }else {
                    getAttention();
                }

                break;

            case R.id.civ_user_icon: //跳转讲师用户页面 传UserId值
                if (!videoSummaryProtocol.userID.equals("0")){
                    startActivity(new Intent(getActivity(), UserPagerActivity.class).putExtra("LOOK_USER_ID",videoSummaryProtocol.userID));
                }

                LogUtils.show("讲师ID",videoSummaryProtocol.userID);

                break;
        }
    }

    /**
     * 发起关注
     */
    private void getAttention() {
        if (TextUtils.isEmpty(SPUtils.getString(getActivity(),GlobalConstants.userID,""))){
            ToastUtil.showShort(getActivity(),"您未登陆，请先去登陆");
            return;
        }

        if (SPUtils.getString(getActivity(),GlobalConstants.userID,"").equals(videoSummaryProtocol.userID)){
            ToastUtil.showShort(getActivity(),"自己不能关注自己");
            return;
        }


        if (progressDialog!=null){
            progressDialog.show();
        }
        HashMap map =new HashMap();
        map.put("userid",SPUtils.getString(getActivity(),GlobalConstants.userID,""));
        map.put("uid",videoSummaryProtocol.userID);

        collegePresenter.getAttention(map, new AttentionIView() {
            @Override
            public void getAttentionSuccess(String message) {
                if (progressDialog!=null){
                    progressDialog.dismiss();
                }
                ToastUtil.showShort(getActivity(),message);
                ivAttention.setImageDrawable(getResources().getDrawable(R.drawable.focused));
                follew=true;

            }

            @Override
            public void getAttentionOnFailure(String message) {
                if (progressDialog!=null){
                    progressDialog.dismiss();
                }
                ToastUtil.showShort(getActivity(),message);
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


    /**
     * 取消关注
     * @param
     */
    private void cancelAttention(){

//
        if (progressDialog!=null){
            progressDialog.show();
        }
        HashMap map =new HashMap();
        map.put("userid",SPUtils.getString(getActivity(),GlobalConstants.userID,""));
        map.put("uid",videoSummaryProtocol.userID);


        collegePresenter.cancelAttention(map, new AttentionIView() {
            @Override
            public void getAttentionSuccess(String message) {
                if (progressDialog!=null){
                    progressDialog.dismiss();
                }
                ToastUtil.showShort(getActivity(),message);
                ivAttention.setImageDrawable(getResources().getDrawable(R.drawable.live_follow1));
                follew=false;

            }

            @Override
            public void getAttentionOnFailure(String message) {
                if (progressDialog!=null){
                    progressDialog.dismiss();
                }
                ToastUtil.showShort(getActivity(),message);
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
    public void onDestroy() {
        super.onDestroy();
        collegePresenter.destroy();
    }

    @Override
    protected void initPresenter() {
        collegePresenter=new CollegePresenter();
    }
}
