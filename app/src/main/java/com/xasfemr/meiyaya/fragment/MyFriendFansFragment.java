package com.xasfemr.meiyaya.fragment;


import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.xasfemr.meiyaya.activity.MyFriendsActivity;
import com.xasfemr.meiyaya.activity.UserPagerActivity;
import com.xasfemr.meiyaya.bean.FriendsListData;
import com.xasfemr.meiyaya.global.GlobalConstants;
import com.xasfemr.meiyaya.utils.LogUtils;
import com.xasfemr.meiyaya.view.MeiYaYaHeader;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;

import okhttp3.Call;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyFriendFansFragment extends BaseFragment {
    private static final String TAG = "MyFriendFansFragment";

    private ArrayList<FriendsListData.FriendInfo> mFriendsList = new ArrayList<>();

    private boolean isPullRefresh = false;
    private int               pageNo;
    private String            lookUserId;
    private MyFriendsActivity mFriendActivity;
    private RecyclerView      rvFriendFans;
    private RefreshLayout     refreshLayout;
    private LinearLayout      llFriendsHave;
    private LinearLayout      llFriendsNone;
    private LinearLayout      llLoading;
    private FriendsAdapter    mFriendsAdapter;

    public MyFriendFansFragment() {
    }

    public MyFriendFansFragment(String lookUserId) {
        LogUtils.show(TAG, "---MyFriendFansFragment---lookUserId = " + lookUserId);
        this.lookUserId = lookUserId;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFriendActivity = (MyFriendsActivity) getActivity();
    }

    @Override
    public View initView() {
        View view = View.inflate(mFriendActivity, R.layout.fragment_my_friend_fans, null);
        rvFriendFans = (RecyclerView) view.findViewById(R.id.rv_friend_fans);
        refreshLayout = (RefreshLayout) view.findViewById(R.id.refreshLayout);
        llFriendsHave = (LinearLayout) view.findViewById(R.id.ll_friends_have);
        llFriendsNone = (LinearLayout) view.findViewById(R.id.ll_friends_none);
        llLoading = (LinearLayout) view.findViewById(R.id.ll_loading);

        setRefreshLayout();
        return view;
    }

    private void setRefreshLayout() {
        refreshLayout.setRefreshHeader(new MeiYaYaHeader(mFriendActivity));
        refreshLayout.setRefreshFooter(new ClassicsFooter(mFriendActivity).setSpinnerStyle(SpinnerStyle.Scale));
        refreshLayout.setEnableLoadmore(false);
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                //下拉刷新
                pageNo = 0;
                isPullRefresh = true;
                mFriendsList.clear();
                gotoGetFansFriend();
            }
        });
        refreshLayout.setOnLoadmoreListener(new OnLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                pageNo++;
                gotoGetFansFriend();
            }
        });
    }

    @Override
    public void initData() {
        pageNo = 0;
        isPullRefresh = false;
        mFriendsList.clear();
        gotoGetFansFriend();
    }

    private void gotoGetFansFriend() {
        if (pageNo == 0 && !isPullRefresh) {
            llLoading.setVisibility(View.VISIBLE);
        }
        OkHttpUtils.get().url(GlobalConstants.URL_FRIEND_FANS)
                .addParams("id", lookUserId)
                .addParams("page", pageNo + "")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.i(TAG, "onError: ---网络出现异常,获取关注的人失败---");
                        Toast.makeText(mFriendActivity, "网络出现异常", Toast.LENGTH_SHORT).show();
                        refreshLayout.finishRefresh();
                        refreshLayout.finishLoadmore();
                        llLoading.setVisibility(View.GONE);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.i(TAG, "onResponse: ---获取关注的人成功---response = " + response + "---");
                        refreshLayout.finishRefresh();
                        refreshLayout.finishLoadmore();
                        llFriendsHave.setVisibility(View.VISIBLE);
                        llFriendsNone.setVisibility(View.GONE);
                        llLoading.setVisibility(View.GONE);

                        try {
                            parserFansFriendsJson(response);
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.i(TAG, "onResponse: ---解析关注的人数据出现异常---");
                        }
                    }
                });
    }

    private void parserFansFriendsJson(String response) {
        Gson gson = new Gson();
        FriendsListData friendsListData = gson.fromJson(response, FriendsListData.class);

        if (friendsListData == null || friendsListData.data == null || friendsListData.data.size() == 0) {  //没有好友
            if (pageNo == 0) {
                Toast.makeText(mFriendActivity, "还没有粉丝", Toast.LENGTH_SHORT).show();
                llFriendsHave.setVisibility(View.GONE);
                llFriendsNone.setVisibility(View.VISIBLE);
            } else {
                Toast.makeText(mFriendActivity, "没有更多粉丝了", Toast.LENGTH_SHORT).show();
            }
        } else {  //有好友
            mFriendsList.addAll(friendsListData.data);

            //RecyclerView加载数据
            if (pageNo == 0) { //page:每次加载15条
                rvFriendFans.setLayoutManager(new LinearLayoutManager(mFriendActivity));
                mFriendsAdapter = new FriendsAdapter();
                rvFriendFans.setAdapter(mFriendsAdapter);

            } else {
                mFriendsAdapter.notifyDataSetChanged();
            }
        }
    }

    private class FriendsAdapter extends RecyclerView.Adapter<FriendsHolder> {

        @Override
        public FriendsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = View.inflate(mFriendActivity, R.layout.item_my_friends, null);
            FriendsHolder friendsHolder = new FriendsHolder(view);
            return friendsHolder;
        }

        @Override
        public void onBindViewHolder(FriendsHolder holder, int position) {
            Glide.with(MyFriendFansFragment.this).load(mFriendsList.get(position).images1).into(holder.ivFriendUserIcon);
            holder.tvFriendUserName.setText(mFriendsList.get(position).cat_name);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mFriendActivity, UserPagerActivity.class);
                    intent.putExtra("LOOK_USER_ID", mFriendsList.get(position).userid);
                    mFriendActivity.startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return mFriendsList.size();
        }
    }


    static class FriendsHolder extends RecyclerView.ViewHolder {

        public ImageView ivFriendUserIcon;
        public TextView  tvFriendUserName;
        public ImageView ivFriendFollow;

        public FriendsHolder(View itemView) {
            super(itemView);
            ivFriendUserIcon = (ImageView) itemView.findViewById(R.id.iv_friend_user_icon);
            tvFriendUserName = (TextView) itemView.findViewById(R.id.tv_friend_user_name);
            ivFriendFollow = (ImageView) itemView.findViewById(R.id.iv_friend_follow);
        }
    }

}
