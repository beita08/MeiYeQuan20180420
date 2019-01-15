package com.xasfemr.meiyaya.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.xasfemr.meiyaya.R;
import com.xasfemr.meiyaya.bean.DynamicFollowData;
import com.xasfemr.meiyaya.bean.MeetingApplyData;
import com.xasfemr.meiyaya.bean.MeetingDetailsData;
import com.xasfemr.meiyaya.global.GlobalConstants;
import com.xasfemr.meiyaya.main.BaseActivity;
import com.xasfemr.meiyaya.utils.LogUtils;
import com.xasfemr.meiyaya.utils.SPUtils;
import com.xasfemr.meiyaya.utils.ToastUtil;
import com.xasfemr.meiyaya.utils.UiUtils;
import com.xasfemr.meiyaya.view.RecycleViewDivider;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;

public class MeetingDetailsActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "MeetingDetailsActivity";

    private boolean isFocus = false;
    private int             meetingApplyNum;
    private String          mUserId;
    private String          meetingId;
    private String          lookUserId;
    private ScrollView      svMeetingDetails;
    private LinearLayout    llLoading;
    private LinearLayout    llNetworkFailed;
    private Button          btnAgainLoad;
    private ImageView       ivCover;
    private CircleImageView civLogo;
    private TextView        tvCompany;
    private ImageView       ivAuthMark;
    private TextView        tvLinkman;
    private ImageView       ivFocus;
    private TextView        tvDeadline;
    private TextView        tvTime;
    private TextView        tvPlace;
    private TextView        tvTitle;
    private TextView        tvContent;
    private Button          btnApply;
    private LinearLayout    llApplyList;
    private TextView        tvApplyPeopleNum;
    private RecyclerView    rvApplyList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meeting_details);
        initTopBar();
        setTopTitleText("会议详情");
        mUserId = SPUtils.getString(this, GlobalConstants.userID, "");
        Intent intent = getIntent();
        meetingId = intent.getStringExtra("info_id");        //会议Id
        lookUserId = intent.getStringExtra("user_id");       //发送该会议的用户Id
        if (TextUtils.isEmpty(meetingId) || TextUtils.isEmpty(lookUserId)) {
            Toast.makeText(this, "跳转有误,请重试", Toast.LENGTH_SHORT).show();
            finish();
        }

        svMeetingDetails = (ScrollView) findViewById(R.id.sv_meeting_details);
        llLoading = (LinearLayout) findViewById(R.id.ll_loading);
        llNetworkFailed = (LinearLayout) findViewById(R.id.ll_network_failed);
        btnAgainLoad = (Button) findViewById(R.id.btn_again_load);
        ivCover = (ImageView) findViewById(R.id.iv_meeting_cover);
        civLogo = (CircleImageView) findViewById(R.id.civ_meeting_logo);
        tvCompany = (TextView) findViewById(R.id.tv_meeting_company);
        ivAuthMark = (ImageView) findViewById(R.id.iv_company_auth_mark);
        tvLinkman = (TextView) findViewById(R.id.tv_meeting_linkman);
        ivFocus = (ImageView) findViewById(R.id.iv_meeting_focus);
        tvDeadline = (TextView) findViewById(R.id.tv_meeting_deadline);
        tvTime = (TextView) findViewById(R.id.tv_meeting_time);
        tvPlace = (TextView) findViewById(R.id.tv_meeting_place);
        tvTitle = (TextView) findViewById(R.id.tv_meeting_title);
        tvContent = (TextView) findViewById(R.id.tv_meeting_content);
        btnApply = (Button) findViewById(R.id.btn_meeting_apply);
        llApplyList = (LinearLayout) findViewById(R.id.ll_meeting_apply_list);
        tvApplyPeopleNum = (TextView) findViewById(R.id.tv_meeting_apply_peopleNum);
        rvApplyList = (RecyclerView) findViewById(R.id.rv_meeting_apply_list);

        btnAgainLoad.setOnClickListener(this);
        ivFocus.setOnClickListener(this);
        btnApply.setOnClickListener(this);

        if (TextUtils.equals(mUserId, lookUserId)) {
            btnApply.setVisibility(View.GONE);
            llApplyList.setVisibility(View.VISIBLE);
        } else {
            btnApply.setVisibility(View.VISIBLE);
            llApplyList.setVisibility(View.GONE);
        }
        gotoGetMeetingDetails(meetingId);
        gotoGetApplyList(meetingId);
    }

    private void gotoGetApplyList(String meetingId) {
        OkHttpUtils.get().url(GlobalConstants.URL_MEETING_APPLY_LIST)
                .addParams("infoid", meetingId)
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                LogUtils.show(TAG, "---网络异常,会议报名列表获取失败,请重试---");
                Toast.makeText(MeetingDetailsActivity.this, "网络异常,会议报名列表获取失败,请重试", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(String response, int id) {
                LogUtils.show(TAG, "---会议报名列表获取成功:response = " + response + "---");
                try {
                    parserApplyListJson(response);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private void parserApplyListJson(String response) {
        Gson gson = new Gson();
        MeetingApplyData meetingApplyData = gson.fromJson(response, MeetingApplyData.class);
        if (meetingApplyData != null && meetingApplyData.data != null && meetingApplyData.data.list != null) {
            if (meetingApplyData.data.list.size() > 0) {
                meetingApplyNum = meetingApplyData.data.count;
                tvApplyPeopleNum.setText("截止目前：已报名" + meetingApplyData.data.count + "人");
                rvApplyList.setLayoutManager(new LinearLayoutManager(MeetingDetailsActivity.this));
                ApplyAdapter applyAdapter = new ApplyAdapter(meetingApplyData.data.list);
                rvApplyList.addItemDecoration(new RecycleViewDivider(MeetingDetailsActivity.this, LinearLayoutManager.HORIZONTAL, UiUtils.dp2px(MeetingDetailsActivity.this, 0.5), 0xFFF0EDF0));
                rvApplyList.setAdapter(applyAdapter);
            } else {
                tvApplyPeopleNum.setText("目前还没有人报名");
            }
        }
    }

    private void gotoGetMeetingDetails(String meetingId) {
        svMeetingDetails.setVisibility(View.GONE);
        llLoading.setVisibility(View.VISIBLE);
        llNetworkFailed.setVisibility(View.GONE);
        OkHttpUtils.get().url(GlobalConstants.URL_MEETING_DETAILS)
                .addParams("infoid", meetingId)
                .addParams("userid", mUserId)
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                LogUtils.show(TAG, "---网络异常,会议详情获取失败,请重试---");
                Toast.makeText(MeetingDetailsActivity.this, "网络异常,会议详情获取失败,请重试", Toast.LENGTH_SHORT).show();
                svMeetingDetails.setVisibility(View.GONE);
                llLoading.setVisibility(View.GONE);
                llNetworkFailed.setVisibility(View.VISIBLE);
            }

            @Override
            public void onResponse(String response, int id) {
                LogUtils.show(TAG, "---会议详情获取成功:response = " + response + "---");
                svMeetingDetails.setVisibility(View.VISIBLE);
                llLoading.setVisibility(View.GONE);
                llNetworkFailed.setVisibility(View.GONE);
                try {
                    parserMeetingDetailsJson(response);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(MeetingDetailsActivity.this, "会议数据解析异常,请重试", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void parserMeetingDetailsJson(String response) {
        Gson gson = new Gson();
        MeetingDetailsData meetingDetailsData = gson.fromJson(response, MeetingDetailsData.class);
        if (meetingDetailsData != null && meetingDetailsData.data != null) {
            Glide.with(MeetingDetailsActivity.this).load(meetingDetailsData.data.thumb).into(ivCover);
            Glide.with(MeetingDetailsActivity.this).load(meetingDetailsData.data.icon).into(civLogo);
            tvCompany.setText(meetingDetailsData.data.organizer);
            //是否认证 is_approve (0:未认证, 1:已认证)
            ivAuthMark.setVisibility(meetingDetailsData.data.is_approve == 1 ? View.VISIBLE : View.GONE);
            tvLinkman.setText(meetingDetailsData.data.linkman);
            //关注
            if (meetingDetailsData.data.attention == 0) {  //未关注
                ivFocus.setVisibility(View.VISIBLE);
                ivFocus.setImageResource(R.drawable.focus);
                isFocus = false;
            } else if (meetingDetailsData.data.attention == 1) { //已关注
                ivFocus.setVisibility(View.VISIBLE);
                ivFocus.setImageResource(R.drawable.focused);
                isFocus = true;
            } else if (meetingDetailsData.data.attention == -1) {  //自己的动态不用显示关注按钮
                ivFocus.setVisibility(View.GONE);
                isFocus = false;
            } else {
                ivFocus.setVisibility(View.VISIBLE);
                ivFocus.setImageResource(R.drawable.focus);
                isFocus = false;
            }
            tvDeadline.setText("报名截止时间：" + meetingDetailsData.data.enddate);
            tvTime.setText(tvTime.getText() + meetingDetailsData.data.meetingTime);
            tvPlace.setText(tvPlace.getText() + meetingDetailsData.data.meetingPlace);
            tvTitle.setText(meetingDetailsData.data.title);
            tvContent.setText(meetingDetailsData.data.content);
        } else {
            svMeetingDetails.setVisibility(View.GONE);
            llLoading.setVisibility(View.GONE);
            llNetworkFailed.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_again_load:
                gotoGetMeetingDetails(meetingId);
                gotoGetApplyList(meetingId);
                break;
            case R.id.iv_meeting_focus:
                ivFocus.setEnabled(false);
                if (isFocus) {
                    //取消关注
                    isFocus = false;
                    ivFocus.setImageResource(R.drawable.focus);
                    gotoCancelFollow();
                } else {
                    //添加关注
                    isFocus = true;
                    ivFocus.setImageResource(R.drawable.focused);
                    gotoAddFollow();
                }
                break;
            case R.id.btn_meeting_apply:
                //如果没有登录先去登录
                boolean isLoginState = SPUtils.getboolean(this, GlobalConstants.isLoginState, false);
                if (isLoginState) {
                    Intent intent = new Intent(this, MeetingApplyActivity.class);
                    intent.putExtra("meetingId", meetingId);
                    intent.putExtra("meetingApplyNum", meetingApplyNum);
                    startActivity(intent);
                } else {
                    ToastUtil.showShort(this, "请先登录");
                    startActivity(new Intent(this, LoginActivity.class));
                }
                break;
            default:
                break;
        }
    }


    //取消关注
    private void gotoCancelFollow() {
        OkHttpUtils.get().url(GlobalConstants.URL_CANCEL_FOLLOW)
                .addParams("userid", mUserId)
                .addParams("uid", lookUserId)
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                LogUtils.show(TAG, "onError: -----网络异常,取消关注失败-----");
                Toast.makeText(MeetingDetailsActivity.this, "网络异常,取消关注失败,请重试", Toast.LENGTH_SHORT).show();
                //取消关注失败将所有改变的状态回归
                isFocus = true;
                ivFocus.setImageResource(R.drawable.focused);
                ivFocus.setEnabled(true);
            }

            @Override
            public void onResponse(String response, int id) {
                LogUtils.show(TAG, "onResponse: -----取消关注成功-----response = " + response + "-----");
                //取消关注成功,所有状态之前已经改变了,这里可以不再做处理,这里我只是强化一下取消关注的状态
                isFocus = false;
                ivFocus.setImageResource(R.drawable.focus);
                ivFocus.setEnabled(true);
                try {
                    Gson gson = new Gson();
                    DynamicFollowData dynamicFollowData = gson.fromJson(response, DynamicFollowData.class);
                    if (dynamicFollowData != null) {
                        //弹出来自服务器的信息
                        Toast.makeText(MeetingDetailsActivity.this, dynamicFollowData.message, Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    //添加关注
    private void gotoAddFollow() {
        OkHttpUtils.get().url(GlobalConstants.URL_ADD_FOLLOW)
                .addParams("userid", mUserId)
                .addParams("uid", lookUserId)
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                LogUtils.show(TAG, "onError: -----网络异常,关注失败-----");
                Toast.makeText(MeetingDetailsActivity.this, "网络异常,关注失败,请重试", Toast.LENGTH_SHORT).show();
                //关注失败将所有改变的状态回归
                isFocus = false;
                ivFocus.setImageResource(R.drawable.focus);
                ivFocus.setEnabled(true);
            }

            @Override
            public void onResponse(String response, int id) {
                LogUtils.show(TAG, "onResponse: -----关注成功-----response = " + response + "-----");
                //关注成功,所有状态之前已经改变了,这里可以不再做处理,这里我只是强化一下关注的状态
                isFocus = true;
                ivFocus.setImageResource(R.drawable.focused);
                ivFocus.setEnabled(true);
                try {
                    Gson gson = new Gson();
                    DynamicFollowData dynamicFollowData = gson.fromJson(response, DynamicFollowData.class);
                    if (dynamicFollowData != null) {
                        //弹出来自服务器的信息
                        Toast.makeText(MeetingDetailsActivity.this, dynamicFollowData.message, Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.i(TAG, "onResponse: 解析关注数据出现异常!");
                }
            }
        });
    }


    private class ApplyAdapter extends RecyclerView.Adapter<ApplyHolder> {

        private ArrayList<MeetingApplyData.MeetingApplyList.ApplyInfo> mApplyInfo;

        public ApplyAdapter(ArrayList<MeetingApplyData.MeetingApplyList.ApplyInfo> applyInfo) {
            this.mApplyInfo = applyInfo;
        }

        @Override
        public ApplyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = View.inflate(MeetingDetailsActivity.this, R.layout.item_meeting_apply, null);
            ApplyHolder applyHolder = new ApplyHolder(view);
            return applyHolder;
        }

        @Override
        public void onBindViewHolder(ApplyHolder holder, int position) {
            Glide.with(MeetingDetailsActivity.this).load(mApplyInfo.get(position).icon).into(holder.civApplyIcon);
            holder.tvApplyName.setText(mApplyInfo.get(position).username);
            holder.tvApplyJob.setText(mApplyInfo.get(position).position);
            holder.tvApplyPhone.setText(mApplyInfo.get(position).phone);
            holder.tvApplyGender.setVisibility(View.GONE);
            holder.tvApplyPeopleNum.setText("人数：" + mApplyInfo.get(position).meetingnumber);
            holder.tvApplyCompany.setText("单位：" + mApplyInfo.get(position).companyname);

        }

        @Override
        public int getItemCount() {
            return mApplyInfo.size();
        }
    }

    static class ApplyHolder extends RecyclerView.ViewHolder {

        public CircleImageView civApplyIcon;
        public TextView        tvApplyName;
        public TextView        tvApplyJob;
        public TextView        tvApplyPhone;
        public TextView        tvApplyGender;
        public TextView        tvApplyPeopleNum;
        public TextView        tvApplyCompany;

        public ApplyHolder(View itemView) {
            super(itemView);
            civApplyIcon = (CircleImageView) itemView.findViewById(R.id.civ_apply_icon);
            tvApplyName = (TextView) itemView.findViewById(R.id.tv_apply_name);
            tvApplyJob = (TextView) itemView.findViewById(R.id.tv_apply_job);
            tvApplyPhone = (TextView) itemView.findViewById(R.id.tv_apply_phone);
            tvApplyGender = (TextView) itemView.findViewById(R.id.tv_apply_gender);
            tvApplyPeopleNum = (TextView) itemView.findViewById(R.id.tv_apply_peopleNum);
            tvApplyCompany = (TextView) itemView.findViewById(R.id.tv_apply_company);
        }
    }

}
