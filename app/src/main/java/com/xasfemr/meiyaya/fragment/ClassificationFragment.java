package com.xasfemr.meiyaya.fragment;


import android.Manifest;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;

import com.xasfemr.meiyaya.R;
import com.xasfemr.meiyaya.activity.LiveCreateActivity;
import com.xasfemr.meiyaya.activity.LoginActivity;
import com.xasfemr.meiyaya.activity.RealNameAuthActivity2;
import com.xasfemr.meiyaya.base.fragment.MVPBaseFragment;
import com.xasfemr.meiyaya.fragment.factory.CollegeFragmentFactory;
import com.xasfemr.meiyaya.global.GlobalConstants;
import com.xasfemr.meiyaya.main.MainActivity;
import com.xasfemr.meiyaya.utils.LogUtils;
import com.xasfemr.meiyaya.utils.SPUtils;
import com.xasfemr.meiyaya.utils.ToastUtil;
import com.xasfemr.meiyaya.utils.permission.MPermission;
import com.xasfemr.meiyaya.utils.permission.annotation.OnMPermissionDenied;
import com.xasfemr.meiyaya.utils.permission.annotation.OnMPermissionGranted;
import com.xasfemr.meiyaya.view.LoadDataView;
import com.xasfemr.meiyaya.weight.SFDialog;

import butterknife.BindView;


/**
 * 原命名是分类模块,2017年9月4日将分类模块修改为商学院,后又修改为美课堂,所以模块命名为ClassificationFragment
 */
public class ClassificationFragment extends MVPBaseFragment {

    private final String[] mTitles = {"课程", "讲师", "在线", "资料", "活动"};
    private MainActivity mainActivity;
    private int mFragmentPosition = 0;

    @BindView(R.id.tabLayout_classification)
    TabLayout tabLayoutClassification;

    @BindView(R.id.viewPager_content)
    ViewPager viewPagerContent;

    @BindView(R.id.iv_find_add)
    ImageView ivFindAdd;

    @Override
    protected int layoutId() {
        return R.layout.fragment_classification;
    }

    /**
     * 填充布局初始化fragment中的view
     * @return 返回fragment所包装的view对象
     */
    @Override
    public void initView() {
        mainActivity = (MainActivity) getActivity();
        viewPagerContent.setOffscreenPageLimit(2);
        viewPagerContent.setAdapter(new CollegeFragmentAdapter(getChildFragmentManager()));
        tabLayoutClassification.setupWithViewPager(viewPagerContent);

        viewPagerContent.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                mFragmentPosition = position;
                switch (position) {
                    case 0: //设置课程+显示隐藏
                        ivFindAdd.setVisibility(View.INVISIBLE);
                        break;
                    case 1: //设置讲师+显示隐藏
                        ivFindAdd.setVisibility(View.INVISIBLE);
                        break;
                    case 2: //设置在线+显示隐藏
                        ivFindAdd.setVisibility(View.VISIBLE);
                        break;
                    case 3: //设置资料+显示隐藏
                        ivFindAdd.setVisibility(View.INVISIBLE);
                        break;
                    case 4: //设置活动+显示隐藏
                        ivFindAdd.setVisibility(View.INVISIBLE);
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
            //如果没有登录先去登录
            boolean isLoginState = SPUtils.getboolean(mainActivity, GlobalConstants.isLoginState, false);
            if (!isLoginState) {
                ToastUtil.showShort(mainActivity, "请先登录");
                startActivity(new Intent(mainActivity, LoginActivity.class));
                return;
            }
            switch (mFragmentPosition) {
                case 0://执行课程添加逻辑
                    ToastUtil.showShort(mainActivity, "课程添加");
                    break;
                case 1://执行讲师添加逻辑
                    ToastUtil.showShort(mainActivity, "讲师添加");
                    break;
                case 2://执行在线添加逻辑
                    //lstatus：讲师状态（1为讲师）
                    int lecturerLstatus = SPUtils.getInt(mainActivity, GlobalConstants.LECTURER_lSTATUS, 0);
                    if (lecturerLstatus == 1) { //是讲师直接开课
                        if (bWritePermission) {
                            startActivity(new Intent(getActivity(), LiveCreateActivity.class));
                        } else {
                            requestBasicPermission();
                        }
                    } else {          //不是讲师去实名认证
                        mainActivity.startActivity(new Intent(mainActivity, RealNameAuthActivity2.class));
                    }
                    break;
                case 3:  //执行资料添加逻辑
                    ToastUtil.showShort(mainActivity, "资料添加");
                    break;
                case 4:  //执行活动添加逻辑
                    ToastUtil.showShort(mainActivity, "活动添加");
                    break;
                default:
                    break;
            }
        });
    }

    @Override
    protected void getLoadView(LoadDataView mLoadView) {

    }

    //初始化fragment中的数据
    @Override
    public void initData() {

    }

    @Override
    protected void initPresenter() {

    }

    private class CollegeFragmentAdapter extends FragmentPagerAdapter {

        public CollegeFragmentAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            MVPBaseFragment baseFragment = CollegeFragmentFactory.getFragment(position);
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

    private boolean bWritePermission;
    private final int BASIC_PERMISSION_REQUEST_CODE = 110;

    @OnMPermissionGranted(BASIC_PERMISSION_REQUEST_CODE)
    public void onBasicPermissionSuccess() {
        LogUtils.show("权限获取成功", "---");
        bWritePermission = true;
        startActivity(new Intent(getActivity(), LiveCreateActivity.class));
    }

    @OnMPermissionDenied(BASIC_PERMISSION_REQUEST_CODE)
    public void onBasicPermissionFailed() {
        LogUtils.show("权限获取失败", "---");
        bWritePermission = false;

        SFDialog.basicDialog(getActivity(), "提示", "请授予必要权限", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                requestBasicPermission();
            }
        });
    }

    private final String[] BASIC_PERMISSIONS = new String[]{
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO
    };

    /**
     * 申请权限
     */
    private void requestBasicPermission() {
        //        MPermission.printMPermissionResult(true, getActivity(), BASIC_PERMISSIONS); //打印Logo
        MPermission.with(this)
                .setRequestCode(BASIC_PERMISSION_REQUEST_CODE)
                .permissions(BASIC_PERMISSIONS)
                .request();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        MPermission.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }
}
