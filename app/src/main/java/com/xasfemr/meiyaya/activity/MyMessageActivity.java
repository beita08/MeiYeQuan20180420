package com.xasfemr.meiyaya.activity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import com.xasfemr.meiyaya.R;
import com.xasfemr.meiyaya.fragment.BaseFragment;
import com.xasfemr.meiyaya.fragment.factory.MessageFragmentFactory;
import com.xasfemr.meiyaya.main.BaseActivity;

public class MyMessageActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "MyMessageActivity";

    private TextView  tvMyMessagePrivate;
    private TextView  tvMyMessagePublic;
    private ViewPager vpMyMessage;
    //private RecyclerView rvMyMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_message);
        initTopBar();
        setTopTitleText("我的消息");

        tvMyMessagePrivate = (TextView) findViewById(R.id.tv_my_message_private);
        tvMyMessagePublic = (TextView) findViewById(R.id.tv_my_message_public);
        vpMyMessage = (ViewPager) findViewById(R.id.vp_my_message);

        tvMyMessagePrivate.setOnClickListener(this);
        tvMyMessagePublic.setOnClickListener(this);

        vpMyMessage.setAdapter(new MessageAdapter(getFragmentManager()));
        vpMyMessage.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                System.out.println("22222position:" + position);
                if (position == 0) {
                    tvMyMessagePrivate.setSelected(true);
                    tvMyMessagePublic.setSelected(false);
                } else if (position == 1) {
                    tvMyMessagePrivate.setSelected(false);
                    tvMyMessagePublic.setSelected(true);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        //初始化
        tvMyMessagePrivate.setSelected(true);
        tvMyMessagePublic.setSelected(false);
    }

    //点击顶部的"私信"和"通知"切换页面
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_my_message_private:
                vpMyMessage.setCurrentItem(0);
                break;
            case R.id.tv_my_message_public:
                vpMyMessage.setCurrentItem(1);
                break;
            default:
                break;
        }
    }

    private class MessageAdapter extends FragmentPagerAdapter {

        public MessageAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            BaseFragment baseFragment = MessageFragmentFactory.getFragment(position);
            return baseFragment;
        }

        @Override
        public int getCount() {
            return 1;
        }
    }
}
