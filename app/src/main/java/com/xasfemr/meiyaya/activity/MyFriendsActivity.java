package com.xasfemr.meiyaya.activity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.xasfemr.meiyaya.R;
import com.xasfemr.meiyaya.fragment.BaseFragment;
import com.xasfemr.meiyaya.fragment.MyFriendFansFragment;
import com.xasfemr.meiyaya.fragment.MyFriendFollowFragment;
import com.xasfemr.meiyaya.global.GlobalConstants;
import com.xasfemr.meiyaya.main.BaseActivity;
import com.xasfemr.meiyaya.utils.SPUtils;

/*
* 好友页面原本在'我的'模块里面,所以命名是MyFriendsActivity,遵从'我的'模块的命名
* */
public class MyFriendsActivity extends BaseActivity implements View.OnClickListener {

    private TextView  tvMyFriendFollow;
    private TextView  tvMyFriendFans;
    private ViewPager vpMyFriend;
    private String    lookUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_friends);
        initTopBar();
        setTopTitleText("我的好友");

        tvMyFriendFollow = (TextView) findViewById(R.id.tv_my_friend_follow);
        tvMyFriendFans = (TextView) findViewById(R.id.tv_my_friend_fans);
        vpMyFriend = (ViewPager) findViewById(R.id.vp_my_friend);


        tvMyFriendFollow.setOnClickListener(this);
        tvMyFriendFans.setOnClickListener(this);
        vpMyFriend.setAdapter(new FriendsAdapter(getFragmentManager()));
        vpMyFriend.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                System.out.println("22222position:" + position);
                if (position == 0) {
                    tvMyFriendFollow.setSelected(true);
                    tvMyFriendFans.setSelected(false);
                } else if (position == 1) {
                    tvMyFriendFollow.setSelected(false);
                    tvMyFriendFans.setSelected(true);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        //初始化,根据传入的参数进行初始化
        Intent intent = getIntent();
        int friends = intent.getIntExtra("FRIENDS", 0);
        lookUserId = intent.getStringExtra("LOOK_USER_ID");
        if (friends == 0) {
            tvMyFriendFollow.setSelected(true);
            tvMyFriendFans.setSelected(false);
            vpMyFriend.setCurrentItem(0);
        } else if (friends == 1) {
            tvMyFriendFollow.setSelected(false);
            tvMyFriendFans.setSelected(true);
            vpMyFriend.setCurrentItem(1);
        }

        String mUserId = SPUtils.getString(this, GlobalConstants.userID, "");
        if (TextUtils.equals(mUserId, lookUserId)) {
            setTopTitleText("我的好友");
            tvMyFriendFollow.setText("我关注的");
            tvMyFriendFans.setText("关注我的");
        } else {
            setTopTitleText("Ta的好友");
            tvMyFriendFollow.setText("Ta关注的");
            tvMyFriendFans.setText("关注Ta的");
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.tv_my_friend_follow:
                vpMyFriend.setCurrentItem(0);
                break;
            case R.id.tv_my_friend_fans:
                vpMyFriend.setCurrentItem(1);
                break;
            default:
                break;
        }
    }

    private class FriendsAdapter extends FragmentPagerAdapter {

        public FriendsAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            /*//使用FriendsFragmentFactory之后,fragment被缓存,改变访问的用户时Fragment的构造方法不会被再次调用,
            //这样就不能用构造方法来传递参数lookUserId了,所以此处没有采用FriendsFragmentFactory缓存
            BaseFragment baseFragment = FriendsFragmentFactory.getFragment(position);
            return baseFragment;*/
            BaseFragment fragment = null;
            switch (position) {
                case 0:
                    fragment = new MyFriendFollowFragment(lookUserId);
                    break;
                case 1:
                    fragment = new MyFriendFansFragment(lookUserId);
                    break;
                default:
                    break;
            }
            return fragment;
        }

        @Override
        public int getCount() {
            return 2;
        }
    }
}
