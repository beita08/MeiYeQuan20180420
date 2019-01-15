package com.xasfemr.meiyaya.fragment;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.xasfemr.meiyaya.R;
import com.xasfemr.meiyaya.activity.UserPagerActivity;
import com.xasfemr.meiyaya.bean.LookUserCourseData;
import com.xasfemr.meiyaya.global.GlobalConstants;
import com.xasfemr.meiyaya.module.player.VideoPlayerIsUserActivity;
import com.xasfemr.meiyaya.utils.LogUtils;
import com.xasfemr.meiyaya.utils.SPUtils;
import com.xasfemr.meiyaya.view.MeiYaYaHeader;
import com.xasfemr.meiyaya.view.progress.SFProgressDialog;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;

import static com.xasfemr.meiyaya.R.id.tv_course_time_duration;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserCourseFragment extends BaseFragment {
    private static final String TAG = "UserCourseFragment";

    private UserPagerActivity userPagerActivity;
    private String            lookUserId;
    private String            mUserId;
    private ArrayList<LookUserCourseData.DataBean.CourseVideoInfo> lookUserCourseList = new ArrayList<>();

    private boolean isPullRefresh = false;
    private int pageNo;

    private SFProgressDialog sfProgressDialog;
    private RefreshLayout    refreshLayout;
    private RecyclerView     rvUserCourse;
    private CourSubsAdapter  mCourSubsAdapter;
    private LinearLayout     llLoading;
    private LinearLayout     llNoCourse;
    private LinearLayout     llNetworkFailed;
    private Button           btnAgainLoad;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageNo = 0;
        userPagerActivity = (UserPagerActivity) getActivity();
        lookUserId = userPagerActivity.getLookUserId();
        mUserId = SPUtils.getString(userPagerActivity, GlobalConstants.userID, "");
        sfProgressDialog = new SFProgressDialog(userPagerActivity);
    }

    @Override
    public View initView() {
        View view = View.inflate(userPagerActivity, R.layout.fragment_user_course, null);
        rvUserCourse = (RecyclerView) view.findViewById(R.id.rv_user_course);
        refreshLayout = (RefreshLayout) view.findViewById(R.id.refreshLayout);
        llLoading = (LinearLayout) view.findViewById(R.id.ll_loading);
        llNoCourse = (LinearLayout) view.findViewById(R.id.ll_no_course);
        llNetworkFailed = (LinearLayout) view.findViewById(R.id.ll_network_failed);
        btnAgainLoad = (Button) view.findViewById(R.id.btn_again_load);

        btnAgainLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pageNo = 0;
                isPullRefresh = false;
                lookUserCourseList.clear();
                gotoGetUserCourse();
            }
        });
        setRefreshLayout();
        return view;
    }

    private void setRefreshLayout() {
        refreshLayout.setRefreshHeader(new MeiYaYaHeader(userPagerActivity));
        refreshLayout.setRefreshFooter(new ClassicsFooter(userPagerActivity).setSpinnerStyle(SpinnerStyle.Scale));

        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                //下拉刷新
                pageNo = 0;
                isPullRefresh = true;
                lookUserCourseList.clear();
                gotoGetUserCourse();
            }
        });
        refreshLayout.setOnLoadmoreListener(new OnLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                pageNo++;
                gotoGetUserCourse();
            }
        });

    }

    @Override
    public void initData() {
        pageNo = 0;
        isPullRefresh = false;
        lookUserCourseList.clear();
        gotoGetUserCourse();
    }

    private void gotoGetUserCourse() {
        if (pageNo == 0 && !isPullRefresh) {
            llLoading.setVisibility(View.VISIBLE);
        }
        llNetworkFailed.setVisibility(View.GONE);
        OkHttpUtils.get().url(GlobalConstants.URL_LOOK_USER_COURSE)
                .addParams("uid", lookUserId)
                .addParams("page", pageNo + "")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.i(TAG, "onError: ---网络异常,获取用户课程失败---");
                        Toast.makeText(userPagerActivity, "网络异常,获取用户课程失败", Toast.LENGTH_SHORT).show();
                        refreshLayout.finishRefresh();
                        refreshLayout.finishLoadmore();
                        llLoading.setVisibility(View.GONE);
                        llNetworkFailed.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.i(TAG, "onResponse: ---从服务器获取用户课程成功---");
                        Log.i(TAG, "onResponse: 用户课程:response = ---" + response + "---");
                        refreshLayout.finishRefresh();
                        refreshLayout.finishLoadmore();
                        llLoading.setVisibility(View.GONE);
                        llNetworkFailed.setVisibility(View.GONE);
                        try {
                            parserUserCourseJson(response);
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.i(TAG, "onResponse: .....解析用户课程出现异常.....");
                        }
                    }
                });
    }

    private void parserUserCourseJson(String response) {
        Gson gson = new Gson();
        LookUserCourseData lookUserCourseData = gson.fromJson(response, LookUserCourseData.class);
        Log.i(TAG, "parserUserCourseJson: ---用户课程数据解析成功---");
        if (lookUserCourseData == null || lookUserCourseData.data == null || lookUserCourseData.data.list == null) {
            Log.i(TAG, "parserUserCourseJson: ---没有数据或没有更多数据---");
            if (pageNo == 0) {
                Toast.makeText(userPagerActivity, "用户还没有开过课", Toast.LENGTH_SHORT).show();
                llNoCourse.setVisibility(View.VISIBLE);
                rvUserCourse.setVisibility(View.GONE);
            } else {
                Toast.makeText(userPagerActivity, "没有更多课程了", Toast.LENGTH_SHORT).show();
                llNoCourse.setVisibility(View.GONE);
                rvUserCourse.setVisibility(View.VISIBLE);
            }
        } else {
            lookUserCourseList.addAll(lookUserCourseData.data.list);
            Log.i(TAG, "parserUserCourseJson: ---课程数据添加成功---");
            llNoCourse.setVisibility(View.GONE);
            rvUserCourse.setVisibility(View.VISIBLE);
            //RecyclerView加载数据
            if (lookUserCourseList.size() <= 15 && pageNo == 0) { //后台每页显示15条数据,这是第一页数据
                rvUserCourse.setLayoutManager(new GridLayoutManager(userPagerActivity, 2));
                mCourSubsAdapter = new CourSubsAdapter();
                rvUserCourse.setAdapter(mCourSubsAdapter);
                Log.i(TAG, "parserUserCourseJson: ---RecyclerView展示数据---");
            } else {
                mCourSubsAdapter.notifyDataSetChanged();
                Log.i(TAG, "parserUserCourseJson: ---RecyclerView刷新数据---");
            }
        }
    }

    private class CourSubsAdapter extends RecyclerView.Adapter<CourSubsHolder> {

        @Override
        public CourSubsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = View.inflate(userPagerActivity, R.layout.item_course_subscribe_single, null);
            CourSubsHolder courSubsHolder = new CourSubsHolder(view);
            return courSubsHolder;
        }

        @Override
        public void onBindViewHolder(CourSubsHolder holder, int position) {
            holder.tvCourseTitle.setVisibility(View.VISIBLE);

            Glide.with(UserCourseFragment.this).load(lookUserCourseList.get(position).cover).into(holder.ivCourseScreenshot);
            holder.tvCourseTimeDuration.setText(lookUserCourseList.get(position).duration);
            holder.tvCoursePlayNumber.setText(lookUserCourseList.get(position).view);
            holder.tvCourseTitle.setText(lookUserCourseList.get(position).title);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LogUtils.show(TAG, "...OnClickListener...");

                    Intent intent = new Intent();
                    intent.putExtra("videoPath", lookUserCourseList.get(position).addr.rtmpPullUrl);
                    intent.putExtra("media_type", "videoondemand");
                    intent.putExtra("user_name", lookUserCourseList.get(position).username);
                    intent.putExtra("user_id", lookUserCourseList.get(position).userid);
                    intent.putExtra("coursename", lookUserCourseList.get(position).coursename);
                    intent.putExtra("des", lookUserCourseList.get(position).des);
                    intent.putExtra("view", lookUserCourseList.get(position).view);
                    intent.putExtra("icon", lookUserCourseList.get(position).icon);
                    intent.putExtra("video_id", lookUserCourseList.get(position).id);

                    //intent.putExtra("media_type", "videoondemand");
                    //intent.putExtra("decode_type", "software");
                    intent.setClass(getActivity(), VideoPlayerIsUserActivity.class);
                    userPagerActivity.startActivity(intent);
                }
            });

            if (TextUtils.equals(lookUserId, mUserId)) {  //只有自己的页面才能长按删除视频
                holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        showDeleteDialog(lookUserCourseList.get(position), position);
                        return true;
                    }
                });
            }
        }

        @Override
        public int getItemCount() {
            return lookUserCourseList.size();
        }
    }

    private void showDeleteDialog(LookUserCourseData.DataBean.CourseVideoInfo courseVideoInfo, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(userPagerActivity);
        builder.setTitle("提示：");
        builder.setMessage("确定要删除“" + courseVideoInfo.title + "”这个视频吗?");
        builder.setPositiveButton("取消", null);
        builder.setNegativeButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                gotoDeleteUserCourse(courseVideoInfo.vid, position);
            }
        });
        builder.show();
    }

    private void gotoDeleteUserCourse(String vid, int position) {
        sfProgressDialog.show();
        OkHttpUtils.get().url(GlobalConstants.URL_DELETE_MY_VIDEO)
                .addParams("vid", vid)
                .addParams("userid", mUserId)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        sfProgressDialog.dismiss();
                        LogUtils.show(TAG, "onError: ---网络异常,删除视频失败---");
                        Toast.makeText(userPagerActivity, "网络异常,删除视频失败,请重试", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        sfProgressDialog.dismiss();
                        LogUtils.show(TAG, "onResponse: ---删除视频访问网络成功---response = " + response + " ---");
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String data = jsonObject.getString("data");
                            String message = jsonObject.getString("message");
                            if (TextUtils.equals(data, "success")) {
                                Toast.makeText(userPagerActivity, message, Toast.LENGTH_SHORT).show();
                                lookUserCourseList.remove(position);
                                mCourSubsAdapter.notifyDataSetChanged();
                            } else {
                                Toast.makeText(userPagerActivity, message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    static class CourSubsHolder extends RecyclerView.ViewHolder {

        public RelativeLayout  rlCourseScreenshot;
        public ImageView       ivCourseScreenshot;
        public TextView        tvCourseTimeDuration;
        public TextView        tvCoursePlayNumber;
        public TextView        tvCourseTitle;
        public RelativeLayout  rlCourseInfo;
        public CircleImageView ivUserIcon;
        public TextView        tvUserName;
        public ImageView       ivUserCourSubs;
        public TextView        tvUserCourseDes;

        public CourSubsHolder(View itemView) {
            super(itemView);
            rlCourseScreenshot = (RelativeLayout) itemView.findViewById(R.id.rl_course_screenshot);
            ivCourseScreenshot = (ImageView) itemView.findViewById(R.id.iv_course_screenshot);
            tvCourseTimeDuration = (TextView) itemView.findViewById(tv_course_time_duration);
            tvCoursePlayNumber = (TextView) itemView.findViewById(R.id.tv_course_play_number);
            tvCourseTitle = (TextView) itemView.findViewById(R.id.tv_course_title);
            rlCourseInfo = (RelativeLayout) itemView.findViewById(R.id.rl_course_info);
            ivUserIcon = (CircleImageView) itemView.findViewById(R.id.iv_user_icon);
            tvUserName = (TextView) itemView.findViewById(R.id.tv_user_name);
            ivUserCourSubs = (ImageView) itemView.findViewById(R.id.iv_user_course_subscribe);
            tvUserCourseDes = (TextView) itemView.findViewById(R.id.tv_user_course_des);
        }
    }
}
