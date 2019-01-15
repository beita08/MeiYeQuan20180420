package com.xasfemr.meiyaya.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.gson.Gson;
import com.xasfemr.meiyaya.R;
import com.xasfemr.meiyaya.activity.UserPagerActivity;
import com.xasfemr.meiyaya.adapter.DynamicAdapter;
import com.xasfemr.meiyaya.adapter.PostInfoAdapter;
import com.xasfemr.meiyaya.adapter.VideoAdapter;
import com.xasfemr.meiyaya.bean.DynamicData;
import com.xasfemr.meiyaya.bean.UserPostInfoData;
import com.xasfemr.meiyaya.bean.UserVideoData;
import com.xasfemr.meiyaya.global.GlobalConstants;
import com.xasfemr.meiyaya.utils.LogUtils;
import com.xasfemr.meiyaya.utils.SPUtils;
import com.xasfemr.meiyaya.utils.ToastUtil;
import com.xasfemr.meiyaya.utils.UiUtils;
import com.xasfemr.meiyaya.view.RecycleViewDivider;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import okhttp3.Call;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserPostFragment extends BaseFragment implements View.OnClickListener {
    private static final String TAG = "UserPostFragment";

    private UserPagerActivity userPagerActivity;
    private String            lookUserId;
    private String            mUserId;

    private boolean isPostInfoOpen = false; //默认发布信息是合并的
    private boolean isVideoOpen    = false; //默认视频是合并的
    private boolean isDynamicOpen  = true;  //默认动态是展开的

    private LinearLayout llPostInfo;
    private ImageView    ivPostInfoArrow;
    private RecyclerView rvPostInfo;
    private LinearLayout llVideo;
    private ImageView    ivVideoArrow;
    private RecyclerView rvVideo;
    private LinearLayout llDynamic;
    private ImageView    ivDynamicArrow;
    private RecyclerView rvDynamic;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userPagerActivity = (UserPagerActivity) getActivity();
        lookUserId = userPagerActivity.getLookUserId();
        mUserId = SPUtils.getString(userPagerActivity, GlobalConstants.userID, "");
    }

    @Override
    public View initView() {
        View view = View.inflate(userPagerActivity, R.layout.fragment_user_post, null);

        llPostInfo = (LinearLayout) view.findViewById(R.id.ll_user_post_info);
        ivPostInfoArrow = (ImageView) view.findViewById(R.id.iv_user_post_info_arrow);
        rvPostInfo = (RecyclerView) view.findViewById(R.id.rv_user_post_info);
        llVideo = (LinearLayout) view.findViewById(R.id.ll_user_video);
        ivVideoArrow = (ImageView) view.findViewById(R.id.iv_user_video_arrow);
        rvVideo = (RecyclerView) view.findViewById(R.id.rv_user_video);
        llDynamic = (LinearLayout) view.findViewById(R.id.ll_user_dynamic);
        ivDynamicArrow = (ImageView) view.findViewById(R.id.iv_user_dynamic_arrow);
        rvDynamic = (RecyclerView) view.findViewById(R.id.rv_user_dynamic);

        llPostInfo.setOnClickListener(this);
        llVideo.setOnClickListener(this);
        llDynamic.setOnClickListener(this);
        llPostInfo.setVisibility(View.GONE);
        llVideo.setVisibility(View.GONE);
        llDynamic.setVisibility(View.GONE);

        if (isPostInfoOpen) {
            rvPostInfo.setVisibility(View.VISIBLE);
        } else {
            rvPostInfo.setVisibility(View.GONE);
        }

        if (isVideoOpen) {
            rvVideo.setVisibility(View.VISIBLE);
        } else {
            rvVideo.setVisibility(View.GONE);
        }

        if (isDynamicOpen) {
            rvDynamic.setVisibility(View.VISIBLE);
        } else {
            rvDynamic.setVisibility(View.GONE);
        }
        return view;
    }

    @Override
    public void initData() {
        //这里为什么要同时调用三个接口,因为后台就是这么写接口的
        gotoGetUserPostInfo();
        gotoGetUserVideo();
        gotoGetUserDynamic();
    }

    private void gotoGetUserPostInfo() {
        //String mUserId = SPUtils.getString(userPagerActivity, GlobalConstants.userID, "");
        OkHttpUtils.get().url(GlobalConstants.URL_USER_POST_INFO)
                .addParams("userid", lookUserId)//userid动态所属用户的id
                .addParams("id", mUserId)       //id客户端的id
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        LogUtils.show(TAG, "onError: ---网络异常,获取用户发布信息失败---");
                        ToastUtil.showShort(userPagerActivity, "网络异常,获取用户发布信息失败");
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        LogUtils.show(TAG, "onResponse: 用户发布信息:response = ---" + response + "---");
                        try {
                            parserUserPostInfoJson(response);
                        } catch (Exception e) {
                            e.printStackTrace();
                            LogUtils.show(TAG, "onResponse: 用户发布信息解析异常");
                        }
                    }
                });
    }

    private void parserUserPostInfoJson(String response) {
        Gson gson = new Gson();
        UserPostInfoData userPostInfoData = gson.fromJson(response, UserPostInfoData.class);

        if (userPostInfoData == null || userPostInfoData.data == null || userPostInfoData.data.size() == 0) {
            //没有发布信息
            llPostInfo.setVisibility(View.GONE);
            rvPostInfo.setVisibility(View.GONE);
        } else {
            llPostInfo.setVisibility(View.VISIBLE);
            //rvPostInfo.setVisibility(View.VISIBLE);
            if (isPostInfoOpen) {
                rvPostInfo.setVisibility(View.VISIBLE);
            } else {
                rvPostInfo.setVisibility(View.GONE);
            }

            rvPostInfo.setLayoutManager(new LinearLayoutManager(userPagerActivity) {
                @Override
                public boolean canScrollVertically() {
                    return false;
                    //return super.canScrollVertically();
                }
            });
            rvPostInfo.addItemDecoration(new RecycleViewDivider(userPagerActivity, LinearLayoutManager.HORIZONTAL, UiUtils.dp2px(userPagerActivity, 0.5), 0xFFF0EDF0));
            rvPostInfo.setAdapter(new PostInfoAdapter(userPagerActivity, lookUserId, mUserId, userPostInfoData.data));
        }
    }


    private void gotoGetUserVideo() {
        OkHttpUtils.get().url(GlobalConstants.URL_USER_VIDEO)
                .addParams("userid", lookUserId)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        LogUtils.show(TAG, "onError: ---网络异常,获取用户课程视频失败---");
                        ToastUtil.showShort(userPagerActivity, "网络异常,获取用户课程视频失败");
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        LogUtils.show(TAG, "onResponse: 用户发布课程视频:response = ---" + response + "---");
                        try {
                            parserUserVideoJson(response);
                        } catch (Exception e) {
                            e.printStackTrace();
                            LogUtils.show(TAG, "onResponse: 用户发布课程视频解析异常");
                        }
                    }
                });
    }

    private void parserUserVideoJson(String response) {
        Gson gson = new Gson();
        UserVideoData userVideoData = gson.fromJson(response, UserVideoData.class);
        if (userVideoData == null || userVideoData.data == null || userVideoData.data.size() == 0) {
            llVideo.setVisibility(View.GONE);
            rvVideo.setVisibility(View.GONE);
        } else {
            llVideo.setVisibility(View.VISIBLE);
            if (isVideoOpen) {
                rvVideo.setVisibility(View.VISIBLE);
            } else {
                rvVideo.setVisibility(View.GONE);
            }

            rvVideo.setLayoutManager(new GridLayoutManager(userPagerActivity, 2) {
                @Override
                public boolean canScrollVertically() {
                    return false;
                    //return super.canScrollVertically();
                }

                @Override
                public boolean canScrollHorizontally() {
                    return super.canScrollHorizontally();
                }
            });
            rvVideo.setAdapter(new VideoAdapter(userPagerActivity, lookUserId, mUserId, userVideoData.data));
        }
    }


    private void gotoGetUserDynamic() {
        OkHttpUtils.get().url(GlobalConstants.URL_USER_DYNAMIC)
                .addParams("userid", lookUserId)//userid动态所属用户的id
                .addParams("id", mUserId)       //id客户端的id
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        LogUtils.show(TAG, "onError: ---网络异常,获取用户动态失败---");
                        ToastUtil.showShort(userPagerActivity, "网络异常,获取用户动态失败");
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        LogUtils.show(TAG, "onResponse: 用户动态:response = ---" + response + "---");
                        try {
                            parserUserDynamicJson(response);
                        } catch (Exception e) {
                            e.printStackTrace();
                            LogUtils.show(TAG, "onResponse: ---用户动态数据解析异常---");
                            llDynamic.setVisibility(View.GONE);
                            rvDynamic.setVisibility(View.GONE);
                        }
                    }
                });
    }

    private void parserUserDynamicJson(String response) {
        Gson gson = new Gson();
        DynamicData dynamicData = gson.fromJson(response, DynamicData.class);

        if (dynamicData == null || dynamicData.data == null || dynamicData.data.size() == 0) {
            //没有动态
            llDynamic.setVisibility(View.GONE);
            rvDynamic.setVisibility(View.GONE);
        } else {
            llDynamic.setVisibility(View.VISIBLE);
            if (isDynamicOpen) {
                rvDynamic.setVisibility(View.VISIBLE);
            } else {
                rvDynamic.setVisibility(View.GONE);
            }

            rvDynamic.setLayoutManager(new LinearLayoutManager(userPagerActivity) {
                @Override
                public boolean canScrollVertically() {
                    return false;//return super.canScrollVertically();
                }
            });
            rvDynamic.setAdapter(new DynamicAdapter(userPagerActivity, lookUserId, mUserId, dynamicData.data));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_user_post_info:
                if (isPostInfoOpen) {
                    rvPostInfo.setVisibility(View.GONE);
                    isPostInfoOpen = false;
                    ivPostInfoArrow.setImageResource(R.drawable.arrow_right);
                } else {
                    rvPostInfo.setVisibility(View.VISIBLE);
                    isPostInfoOpen = true;
                    ivPostInfoArrow.setImageResource(R.drawable.arrow_down);
                }
                break;

            case R.id.ll_user_video:
                if (isVideoOpen) {
                    rvVideo.setVisibility(View.GONE);
                    isVideoOpen = false;
                    ivVideoArrow.setImageResource(R.drawable.arrow_right);
                } else {
                    rvVideo.setVisibility(View.VISIBLE);
                    isVideoOpen = true;
                    ivVideoArrow.setImageResource(R.drawable.arrow_down);
                }
                break;

            case R.id.ll_user_dynamic:
                if (isDynamicOpen) {
                    rvDynamic.setVisibility(View.GONE);
                    isDynamicOpen = false;
                    ivDynamicArrow.setImageResource(R.drawable.arrow_right);
                } else {
                    rvDynamic.setVisibility(View.VISIBLE);
                    isDynamicOpen = true;
                    ivDynamicArrow.setImageResource(R.drawable.arrow_down);
                }
                break;

            default:
                break;
        }
    }
}
