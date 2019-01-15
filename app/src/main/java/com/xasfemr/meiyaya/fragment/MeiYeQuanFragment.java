package com.xasfemr.meiyaya.fragment;


import android.app.Fragment;
import android.app.FragmentManager;
import android.support.design.widget.TabLayout;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;

import com.xasfemr.meiyaya.R;
import com.xasfemr.meiyaya.base.fragment.MVPBaseFragment;
import com.xasfemr.meiyaya.fragment.factory.MeiYeQuanFragmentFactory;
import com.xasfemr.meiyaya.main.MainActivity;
import com.xasfemr.meiyaya.utils.ToastUtil;
import com.xasfemr.meiyaya.view.LoadDataView;

import butterknife.BindView;

/**
 * A simple {@link Fragment} subclass.
 */
public class MeiYeQuanFragment extends MVPBaseFragment {
    private static final String TAG = "MeiYeQuanFragment";

    private final String[] mTitles = {"动态", "好友", "发现"};
    private MainActivity mainActivity;
    private int mFragmentPosition = 0;

    @BindView(R.id.tabLayout_meiyequan)
    TabLayout tabLayoutMeiYeQuan;

    @BindView(R.id.viewPager_meiyequan)
    ViewPager viewPagerMeiYeQuan;

    @BindView(R.id.iv_find_add)
    ImageView ivFindAdd;


    @Override
    protected int layoutId() {
        return R.layout.fragment_mei_ye_quan;
    }

    @Override
    protected void initView() {
        mainActivity = (MainActivity) getActivity();
        //初始化显示添加按钮
        ivFindAdd.setVisibility(View.VISIBLE);
        //viewPagerMeiYeQuan.setOffscreenPageLimit(2);
        viewPagerMeiYeQuan.setAdapter(new MeiYeQuanFragmentAdapter(getChildFragmentManager()));
        tabLayoutMeiYeQuan.setupWithViewPager(viewPagerMeiYeQuan);

        viewPagerMeiYeQuan.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                mFragmentPosition = position;
                switch (position) {
                    case 0: //设置显示隐藏
                        ivFindAdd.setVisibility(View.VISIBLE);
                        break;
                    case 1: //设置显示隐藏
                        ivFindAdd.setVisibility(View.GONE);
                        break;
                    case 2: //设置显示隐藏
                        ivFindAdd.setVisibility(View.GONE);
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        ivFindAdd.setOnClickListener(v -> {
            switch (mFragmentPosition) {
                case 0: //执行动态添加逻辑
                    FindDynamicFragment findDynamicFragment = (FindDynamicFragment) MeiYeQuanFragmentFactory.getFragment(mFragmentPosition);
                    findDynamicFragment.addDynamic();
                    break;
                case 1: //执行好友添加逻辑
                    ToastUtil.showShort(mainActivity, "好友添加");
                    break;
                case 2:  //执行发现添加逻辑
                    ToastUtil.showShort(mainActivity, "发现添加");
                    break;
                default:
                    break;
            }
        });
    }

    @Override
    protected void getLoadView(LoadDataView mLoadView) {
    }

    @Override
    protected void initData() {
    }

    @Override
    protected void initPresenter() {
    }

    private class MeiYeQuanFragmentAdapter extends FragmentPagerAdapter {

        public MeiYeQuanFragmentAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            BaseFragment baseFragment = MeiYeQuanFragmentFactory.getFragment(position);
            return baseFragment;
        }

        @Override
        public int getCount() {
            return mTitles.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTitles[position];
        }
    }
}
