package com.xasfemr.meiyaya.fragment;


import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.xasfemr.meiyaya.R;
import com.xasfemr.meiyaya.activity.LoginActivity;
import com.xasfemr.meiyaya.activity.UserPagerActivity;
import com.xasfemr.meiyaya.bean.FriendsAllData;
import com.xasfemr.meiyaya.global.GlobalConstants;
import com.xasfemr.meiyaya.main.MainActivity;
import com.xasfemr.meiyaya.utils.LogUtils;
import com.xasfemr.meiyaya.utils.SPUtils;
import com.xasfemr.meiyaya.utils.ToastUtil;
import com.xasfemr.meiyaya.view.MeiYaYaHeader;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;

/**
 * A simple {@link Fragment} subclass.
 */
public class FindFriendsFragment extends BaseFragment {
    private static final String TAG = "FindFriendsFragment";

    private boolean isPullRefresh = false;

    private MainActivity       mainActivity;
    private ExpandableListView elvFriends;
    private FriendsAdapter     mFriendsAdapter;
    private RefreshLayout      refreshLayout;
    private LinearLayout       llNoLogin;
    private Button             btnLogin;
    private LinearLayout       llLoading;
    private LinearLayout       llEmptyData;
    private ImageView          ivEmptyData;
    private TextView           tvEmptyData;
    private LinearLayout       llNetworkFailed;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity) getActivity();
    }

    @Override
    public View initView() {
        View view = View.inflate(mainActivity, R.layout.fragment_find_friends, null);

        refreshLayout = (RefreshLayout) view.findViewById(R.id.refreshLayout);
        elvFriends = (ExpandableListView) view.findViewById(R.id.elv_friends);
        llNoLogin = (LinearLayout) view.findViewById(R.id.ll_no_login);
        btnLogin = (Button) view.findViewById(R.id.btn_login);
        llLoading = (LinearLayout) view.findViewById(R.id.ll_loading);
        llEmptyData = (LinearLayout) view.findViewById(R.id.ll_empty_data);
        ivEmptyData = (ImageView) view.findViewById(R.id.iv_empty_data);
        tvEmptyData = (TextView) view.findViewById(R.id.tv_empty_data);
        llNetworkFailed = (LinearLayout) view.findViewById(R.id.ll_network_failed);

        ivEmptyData.setImageResource(R.drawable.default_no_fans);
        tvEmptyData.setText("还没有好友哦~");
        elvFriends.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                Intent intent = new Intent(mainActivity, UserPagerActivity.class);
                switch (groupPosition) {
                    case 0:
                        intent.putExtra("LOOK_USER_ID", mFriendsAdapter.getAllFriends().attention.get(childPosition).uid);
                        break;
                    case 1:
                        intent.putExtra("LOOK_USER_ID", mFriendsAdapter.getAllFriends().fans.get(childPosition).userid);
                        break;
                    default:
                        break;
                }
                startActivity(intent);
                return false;
            }
        });
        btnLogin.setOnClickListener(v -> startActivity(new Intent(mainActivity, LoginActivity.class)));

        refreshLayout.setRefreshHeader(new MeiYaYaHeader(mainActivity));
        refreshLayout.setEnableLoadmore(false);
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                //下拉刷新
                isPullRefresh = true;
                gotoGetFriends();
            }
        });
        return view;
    }

    @Override
    public void initData() {

    }

    @Override
    public void onResume() {
        super.onResume();
        boolean isLoginState = SPUtils.getboolean(mainActivity, GlobalConstants.isLoginState, false);
        if (isLoginState) {
            elvFriends.setVisibility(View.VISIBLE);
            llNoLogin.setVisibility(View.GONE);
            isPullRefresh = false;
            gotoGetFriends();
        } else {
            elvFriends.setVisibility(View.GONE);
            llNoLogin.setVisibility(View.VISIBLE);
        }
    }

    private void gotoGetFriends() {
        String mUserId = SPUtils.getString(mainActivity, GlobalConstants.userID, "");
        if (TextUtils.isEmpty(mUserId)) {
            return;
        }

        if (!isPullRefresh) {
            llLoading.setVisibility(View.VISIBLE);
        }
        llEmptyData.setVisibility(View.GONE);
        llNetworkFailed.setVisibility(View.GONE);

        OkHttpUtils.get().url(GlobalConstants.URL_FRIENDS_ALL)
                .addParams("id", mUserId)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        LogUtils.show(TAG, "onError: ---网络出现异常,获取好友列表失败---");
                        ToastUtil.showShort(mainActivity, "网络出现异常,请重试");
                        refreshLayout.finishRefresh();
                        refreshLayout.finishLoadmore();
                        elvFriends.setVisibility(View.GONE);
                        llLoading.setVisibility(View.GONE);
                        llEmptyData.setVisibility(View.GONE);
                        llNetworkFailed.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        LogUtils.show(TAG, "---获取好友列表成功---response = [ " + response + " ]---");
                        refreshLayout.finishRefresh();
                        refreshLayout.finishLoadmore();
                        llLoading.setVisibility(View.GONE);
                        llNetworkFailed.setVisibility(View.GONE);

                        try {
                            parserFriendsJson(response);
                        } catch (Exception e) {
                            e.printStackTrace();
                            LogUtils.show(TAG, "onResponse: ---解析好友列表数据出现异常---");
                            elvFriends.setVisibility(View.GONE);
                            llNetworkFailed.setVisibility(View.VISIBLE);
                        }
                    }
                });
    }

    private void parserFriendsJson(String response) {
        Gson gson = new Gson();
        FriendsAllData friendsAllData = gson.fromJson(response, FriendsAllData.class);
        if (friendsAllData == null || friendsAllData.data == null) {
            //数据为空
            elvFriends.setVisibility(View.GONE);
            llEmptyData.setVisibility(View.VISIBLE);
        } else {    //(有关注的人) || (有粉丝) = 好友不为空
            if ((friendsAllData.data.attention != null && friendsAllData.data.attention.size() > 0) || (friendsAllData.data.fans != null && friendsAllData.data.fans.size() > 0)) {
                elvFriends.setVisibility(View.VISIBLE);
                llEmptyData.setVisibility(View.GONE);
                mFriendsAdapter = new FriendsAdapter(friendsAllData.data);
                elvFriends.setAdapter(mFriendsAdapter);
                elvFriends.expandGroup(1);
            } else {
                elvFriends.setVisibility(View.GONE);
                llEmptyData.setVisibility(View.VISIBLE);
            }
        }
    }

    private class FriendsAdapter extends BaseExpandableListAdapter {

        private FriendsAllData.AllFriends allFriends;

        public FriendsAdapter(FriendsAllData.AllFriends allFriends) {
            this.allFriends = allFriends;
        }

        public FriendsAllData.AllFriends getAllFriends() {
            return allFriends;
        }

        //返回组的个数
        @Override
        public int getGroupCount() {
            return 2;
        }

        //根据groupPosition返回某组孩子个数
        @Override
        public int getChildrenCount(int groupPosition) {
            int childrenCount = 0;
            switch (groupPosition) {
                case 0:
                    childrenCount = allFriends.attention.size();
                    break;
                case 1:
                    childrenCount = allFriends.fans.size();
                    break;
                default:
                    break;
            }
            return childrenCount;
        }

        //getItem:返回组对象
        @Override
        public String getGroup(int groupPosition) {
            String groupStr = "";
            switch (groupPosition) {
                case 0:
                    groupStr = "我关注的";
                    break;
                case 1:
                    groupStr = "关注我的";
                    break;
                default:
                    break;
            }
            return groupStr;
        }

        //返回孩子对象
        @Override
        public FriendsAllData.AllFriends.FriendInfo getChild(int groupPosition, int childPosition) {
            FriendsAllData.AllFriends.FriendInfo friendInfo;
            switch (groupPosition) {
                case 0:
                    friendInfo = allFriends.attention.get(childPosition);
                    break;
                case 1:
                    friendInfo = allFriends.fans.get(childPosition);
                    break;
                default:
                    friendInfo = null;
                    break;
            }
            return friendInfo;
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        //是否有固定id, 默认就可以,不需要改动
        @Override
        public boolean hasStableIds() {
            return false;
        }

        //getView:返回组的布局对象
        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            View view = View.inflate(mainActivity, R.layout.item_my_friends_title, null);
            TextView tvFriendsInfoTitle = (TextView) view.findViewById(R.id.tv_friends_info_title);
            tvFriendsInfoTitle.setText(getGroup(groupPosition));
            return view;
        }

        //返回子布局对象
        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

            View view = View.inflate(mainActivity, R.layout.item_my_friends, null);
            CircleImageView ivFriendUserIcon = (CircleImageView) view.findViewById(R.id.iv_friend_user_icon);
            TextView tvFriendUserName = (TextView) view.findViewById(R.id.tv_friend_user_name);

            FriendsAllData.AllFriends.FriendInfo friendInfo = getChild(groupPosition, childPosition);
            Glide.with(FindFriendsFragment.this).load(friendInfo.images1).into(ivFriendUserIcon);
            tvFriendUserName.setText(friendInfo.cat_name);
            return view;
        }

        //孩子是否可以点击
        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
    }
}
