package com.xasfemr.meiyaya.activity;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.CoordinatorLayout;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.lzy.imagepicker.view.SystemBarTintManager;
import com.xasfemr.meiyaya.R;
import com.xasfemr.meiyaya.bean.DynamicFollowData;
import com.xasfemr.meiyaya.bean.LookUserData;
import com.xasfemr.meiyaya.fragment.BaseFragment;
import com.xasfemr.meiyaya.fragment.UserAboutMeFragment;
import com.xasfemr.meiyaya.fragment.UserPostFragment;
import com.xasfemr.meiyaya.global.GlobalConstants;
import com.xasfemr.meiyaya.main.BaseActivity;
import com.xasfemr.meiyaya.utils.SPUtils;
import com.xasfemr.meiyaya.view.AvatarScanHelper;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;

public class UserPagerActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "UserPagerActivity";

    private boolean isFollow = false;
    private String                lookUserId;
    private LookUserData.UserInfo mLookUserInfo;

    public static Handler mHandler;

    private Intent            mIntent;
    private Toolbar           toolbar;
    private CoordinatorLayout coordinatorLayout;
    private LinearLayout      llRoot;
    private LinearLayout      llLoading;
    private LinearLayout      llNetError;
    private TextView          tvNetError;
    private ImageView         ivUserBg;
    private ImageView         ivBack;
    private TextView          tvUserReport;
    private CircleImageView   civUserIcon;
    private TextView          tvUserNickname;
    private ImageView         ivUserGender;
    private ImageView         ivMemberCrown;
    private ImageView         ivAuthMark;
    private TextView          tvUserFollowNum;
    private TextView          tvUserFansNum;
    private ImageView         ivUserPagerEdit;
    private LinearLayout      llFollowPrivateMsg;
    private TextView          tvUserToFollow;
    private TextView          tvUserToPrivateMsg;
    private TextView          tvUserCourse;
    private TextView          tvUserDynamic;
    private TextView          tvUserAboutMe;
    private ViewPager         vpUserPager;
    private LinearLayout      llToolbarBg;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_pager);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        toolbar.setNavigationIcon(R.drawable.back);
        setSupportActionBar(toolbar);
        initHandler();

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
        llRoot = (LinearLayout) findViewById(R.id.ll_root);
        llLoading = (LinearLayout) findViewById(R.id.ll_loading);
        llNetError = (LinearLayout) findViewById(R.id.ll_net_error);
        tvNetError = (TextView) findViewById(R.id.tv_net_error);
        ivUserBg = (ImageView) findViewById(R.id.iv_user_bg);
        ivBack = (ImageView) findViewById(R.id.iv_top_back);
        tvUserReport = (TextView) findViewById(R.id.tv_user_report);
        civUserIcon = (CircleImageView) findViewById(R.id.civ_user_icon);
        tvUserNickname = (TextView) findViewById(R.id.tv_user_nickname);
        ivUserGender = (ImageView) findViewById(R.id.iv_user_gender);
        ivMemberCrown = (ImageView) findViewById(R.id.iv_member_crown);
        ivAuthMark = (ImageView) findViewById(R.id.iv_company_auth_mark);
        tvUserFollowNum = (TextView) findViewById(R.id.tv_user_follow_num);
        tvUserFansNum = (TextView) findViewById(R.id.tv_user_fans_num);
        ivUserPagerEdit = (ImageView) findViewById(R.id.iv_user_pager_edit);
        llFollowPrivateMsg = (LinearLayout) findViewById(R.id.ll_follow_privateMsg);
        tvUserToFollow = (TextView) findViewById(R.id.tv_user_to_follow);
        tvUserToPrivateMsg = (TextView) findViewById(R.id.tv_user_to_private_msg);
        tvUserCourse = (TextView) findViewById(R.id.tv_user_course);
        tvUserDynamic = (TextView) findViewById(R.id.tv_user_dynamic);
        tvUserAboutMe = (TextView) findViewById(R.id.tv_user_about_me);
        vpUserPager = (ViewPager) findViewById(R.id.vp_user_pager);
        llToolbarBg = (LinearLayout) findViewById(R.id.ll_toolbar_bg);


        ivBack.setOnClickListener(v -> finish());
        toolbar.setNavigationOnClickListener(v -> finish());
        tvUserReport.setOnClickListener(this);
        civUserIcon.setOnClickListener(this);
        tvUserFollowNum.setOnClickListener(this);
        tvUserFansNum.setOnClickListener(this);
        ivUserPagerEdit.setOnClickListener(this);
        tvUserToFollow.setOnClickListener(this);
        tvUserToPrivateMsg.setOnClickListener(this);
        tvUserCourse.setOnClickListener(this);
        tvUserDynamic.setOnClickListener(this);
        tvUserAboutMe.setOnClickListener(this);

        Intent intent = getIntent();
        lookUserId = intent.getStringExtra("LOOK_USER_ID");

        if (TextUtils.isEmpty(lookUserId) || TextUtils.equals(lookUserId, "0")) {
            Toast.makeText(this, "跳转出现问题", Toast.LENGTH_SHORT).show();
            finish();
        }

        String mUserId = SPUtils.getString(UserPagerActivity.this, GlobalConstants.userID, "");
        if (TextUtils.equals(lookUserId, mUserId)) { //查看的是自己的页面
            ivUserPagerEdit.setVisibility(View.VISIBLE);
            llFollowPrivateMsg.setVisibility(View.GONE);
            tvUserReport.setVisibility(View.GONE);
        } else {    //查看的是别人的页面
            ivUserPagerEdit.setVisibility(View.GONE);
            llFollowPrivateMsg.setVisibility(View.VISIBLE);
            tvUserReport.setVisibility(View.VISIBLE);
        }

        vpUserPager.setAdapter(new UserPagerAdapter(getFragmentManager()));
        vpUserPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:    //课程
                        tvUserCourse.setSelected(true);
                        tvUserDynamic.setSelected(false);
                        tvUserAboutMe.setSelected(false);
                        break;
                    /*case 1:   //动态
                        tvUserCourse.setSelected(false);
                        tvUserDynamic.setSelected(true);
                        tvUserAboutMe.setSelected(false);
                        break;*/
                    case 1:     //关于我  此处原本是case 2: 取消动态后改为case 1: 动态回归时需再次改为case 2:
                        tvUserCourse.setSelected(false);
                        tvUserDynamic.setSelected(false);
                        tvUserAboutMe.setSelected(true);
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        llNetError.setOnClickListener(v -> gotoGetUserInfo(mUserId, lookUserId));

        //初始化
        tvUserCourse.setSelected(true);
        tvUserDynamic.setSelected(false);
        tvUserAboutMe.setSelected(false);

        gotoGetUserInfo(mUserId, lookUserId);
    }

    private void gotoGetUserInfo(final String mUserId, final String lookUserId) {
        coordinatorLayout.setVisibility(View.GONE);
        llNetError.setVisibility(View.GONE);
        llLoading.setVisibility(View.VISIBLE);
        OkHttpUtils.get().url(GlobalConstants.URL_LOOK_USER_PAGER)
                .addParams("id", mUserId)       //客户端id用字段id提交
                .addParams("uid", lookUserId)   //被点击的头像的id用字段uid提交
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.i(TAG, "onError: ---网络异常,获取用户数据失败---");
                        Toast.makeText(UserPagerActivity.this, "网络异常,获取用户数据失败", Toast.LENGTH_SHORT).show();
                        coordinatorLayout.setVisibility(View.GONE);
                        llNetError.setVisibility(View.VISIBLE);
                        llLoading.setVisibility(View.GONE);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.i(TAG, "onResponse: 用户数据:response = ---" + response + "---");
                        try {
                            parserUserInfoJson(response, mUserId, lookUserId);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        coordinatorLayout.setVisibility(View.VISIBLE);
                        llNetError.setVisibility(View.GONE);
                        llLoading.setVisibility(View.GONE);
                    }
                });
    }

    private void parserUserInfoJson(String response, String mUserId, String lookUserId) {
        Gson gson = new Gson();
        LookUserData lookUserData = gson.fromJson(response, LookUserData.class);

        mLookUserInfo = lookUserData.data;

        Glide.with(UserPagerActivity.this).load(lookUserData.data.bgimg).into(ivUserBg);
        Glide.with(UserPagerActivity.this).load(lookUserData.data.images).into(civUserIcon);
        tvUserNickname.setText(lookUserData.data.username);

        //用户性别 sex：性别（0未知，1男，2女）
        if (lookUserData.data.sex == 1) {
            ivUserGender.setVisibility(View.VISIBLE);
            ivUserGender.setImageResource(R.drawable.gender_boy);
        } else if (lookUserData.data.sex == 2) {
            ivUserGender.setVisibility(View.VISIBLE);
            ivUserGender.setImageResource(R.drawable.gender_girl);
        } else {
            ivUserGender.setVisibility(View.GONE);
        }

        //是否会员 ustatus：会员状态（1为会员）
        ivMemberCrown.setImageResource(lookUserData.data.ustatus == 1 ? R.drawable.member_crown : R.drawable.member_no_crown);
        //是否认证 is_approve (0:未认证, 1:已认证)
        ivAuthMark.setVisibility(lookUserData.data.is_approve == 1 ? View.VISIBLE : View.GONE);

        if (TextUtils.equals(mUserId, lookUserId)) {
            tvUserFollowNum.setText("我关注的  " + lookUserData.data.follow);
            tvUserFansNum.setText("关注我的  " + lookUserData.data.fans);

            ivUserPagerEdit.setVisibility(View.VISIBLE);
            llFollowPrivateMsg.setVisibility(View.GONE);
        } else {
            tvUserFollowNum.setText("Ta关注的  " + lookUserData.data.follow);
            tvUserFansNum.setText("关注Ta的  " + lookUserData.data.fans);

            ivUserPagerEdit.setVisibility(View.GONE);
            llFollowPrivateMsg.setVisibility(View.VISIBLE);

            //attention：关注状态（0未关注，1已关注  -1 为自己 显示编辑）
            if (lookUserData.data.attention == 0) {  //未关注
                tvUserToFollow.setText("关注");
                tvUserToFollow.setBackgroundResource(R.drawable.rectangle_bg_red);
                isFollow = false;

            } else if (lookUserData.data.attention == 1) { //已关注
                tvUserToFollow.setText("已关注");
                tvUserToFollow.setBackgroundResource(R.drawable.rectangle_bg_white);
                isFollow = true;

            } else if (lookUserData.data.attention == -1) {  //自己的动态不用显示关注按钮,这里不可能走到这个分支
                ivUserPagerEdit.setVisibility(View.VISIBLE);
                llFollowPrivateMsg.setVisibility(View.GONE);
                isFollow = false;

            } else {   //默认
                tvUserToFollow.setText("关注");
                tvUserToFollow.setBackgroundResource(R.drawable.rectangle_bg_red);
                isFollow = false;
            }
        }
    }

    @Override
    public void onClick(View v) {
        //如果未登录,就直接跳到登录页面,并且结束方法;
        boolean isLoginState = SPUtils.getboolean(UserPagerActivity.this, GlobalConstants.isLoginState, false);
        if (!isLoginState) {
            Toast.makeText(this, "请先登录", Toast.LENGTH_SHORT).show();
            Intent intentLogin = new Intent(UserPagerActivity.this, LoginActivity.class);
            startActivity(intentLogin);
            return;
        }

        if (mIntent == null) {
            mIntent = new Intent();
        }
        String mUserId = SPUtils.getString(UserPagerActivity.this, GlobalConstants.userID, "");
        switch (v.getId()) {
            case R.id.tv_user_report:        //举报
                mIntent.setClass(UserPagerActivity.this, MyReportActivity.class);
                mIntent.putExtra("LOOK_USER_ID", lookUserId);
                startActivity(mIntent);

                break;
            case R.id.civ_user_icon:        //显示头像大图
                //打开预览
                new AvatarScanHelper(UserPagerActivity.this, mLookUserInfo.images).show();
                break;
            case R.id.tv_user_follow_num:   //关注人数
                if (TextUtils.equals(mUserId, lookUserId)) {  //只看自己关注的人
                    mIntent.setClass(UserPagerActivity.this, MyFriendsActivity.class);
                    mIntent.putExtra("LOOK_USER_ID", lookUserId);
                    mIntent.putExtra("FRIENDS", 0);
                    startActivity(mIntent);
                }
                break;
            case R.id.tv_user_fans_num:     //粉丝人数
                if (TextUtils.equals(mUserId, lookUserId)) {  //只看自己的粉丝
                    mIntent.setClass(UserPagerActivity.this, MyFriendsActivity.class);
                    mIntent.putExtra("LOOK_USER_ID", lookUserId);
                    mIntent.putExtra("FRIENDS", 1);
                    startActivity(mIntent);
                }
                break;
            case R.id.iv_user_pager_edit:   //编辑自己的主页
                mIntent.setClass(UserPagerActivity.this, UserPagerEditInfoActivity.class);
                mIntent.putExtra("LOOK_USER_INFO", mLookUserInfo);
                startActivityForResult(mIntent, 10);
                break;
            case R.id.tv_user_to_follow:    // 关注或取关别人
                tvUserToFollow.setEnabled(false);

                if (isFollow) {
                    //取消关注
                    tvUserToFollow.setText("关注");
                    tvUserToFollow.setBackgroundResource(R.drawable.rectangle_bg_red);
                    isFollow = false;
                    gotoCancelFollow();
                } else {
                    //去关注
                    tvUserToFollow.setText("已关注");
                    tvUserToFollow.setBackgroundResource(R.drawable.rectangle_bg_white);
                    isFollow = true;
                    gotoAddFollow();
                }
                break;
            case R.id.tv_user_to_private_msg: //向别人发送私信
                mIntent.setClass(UserPagerActivity.this, ChatActivity.class);
                //mLookUserInfo
                mIntent.putExtra("FRIEND_ID", lookUserId);
                mIntent.putExtra("FRIEND_NAME", mLookUserInfo.username);
                mIntent.putExtra("FRIEND_ICON", mLookUserInfo.images);
                startActivity(mIntent);
                break;
            case R.id.tv_user_course:         //用户的课程页
                vpUserPager.setCurrentItem(0);
                break;
            /*case R.id.tv_user_dynamic:        //用户的动态页
                vpUserPager.setCurrentItem(1);
                break;*/
            case R.id.tv_user_about_me:       //关于我
                vpUserPager.setCurrentItem(1);//2
                break;
            default:
                break;
        }
    }

    //添加关注
    private void gotoAddFollow() {
        String mUserId = SPUtils.getString(UserPagerActivity.this, GlobalConstants.userID, "");

        OkHttpUtils.get().url(GlobalConstants.URL_ADD_FOLLOW)
                .addParams("userid", mUserId)
                .addParams("uid", lookUserId)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.i(TAG, "onError: -----网络异常,关注失败-----");
                        Toast.makeText(UserPagerActivity.this, "网络异常,关注失败,请重试", Toast.LENGTH_SHORT).show();
                        //关注失败将所有改变的状态回归
                        tvUserToFollow.setText("关注");
                        tvUserToFollow.setBackgroundResource(R.drawable.rectangle_bg_red);
                        isFollow = false;
                        tvUserToFollow.setEnabled(true);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.i(TAG, "onResponse: -----关注成功-----response = " + response + "-----");

                        //关注成功,所有状态之前已经改变了,这里可以不再做处理,这里我只是强化一下关注的状态
                        tvUserToFollow.setText("已关注");
                        tvUserToFollow.setBackgroundResource(R.drawable.rectangle_bg_white);
                        isFollow = true;
                        tvUserToFollow.setEnabled(true);
                        try {
                            Gson gson = new Gson();
                            DynamicFollowData dynamicFollowData = gson.fromJson(response, DynamicFollowData.class);
                            if (dynamicFollowData != null) {
                                //弹出来自服务器的信息
                                Toast.makeText(UserPagerActivity.this, dynamicFollowData.message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    //取消关注
    private void gotoCancelFollow() {
        String mUserId = SPUtils.getString(UserPagerActivity.this, GlobalConstants.userID, "");
        OkHttpUtils.get().url(GlobalConstants.URL_CANCEL_FOLLOW)
                .addParams("userid", mUserId)
                .addParams("uid", lookUserId)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.i(TAG, "onError: -----网络异常,取消关注失败-----");
                        Toast.makeText(UserPagerActivity.this, "网络异常,取消关注失败,请重试", Toast.LENGTH_SHORT).show();
                        //取消关注失败将所有改变的状态回归
                        tvUserToFollow.setText("已关注");
                        tvUserToFollow.setBackgroundResource(R.drawable.rectangle_bg_white);
                        isFollow = true;
                        tvUserToFollow.setEnabled(true);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.i(TAG, "onResponse: -----取消关注成功-----response = " + response + "-----");
                        //取消关注成功,所有状态之前已经改变了,这里可以不再做处理,这里我只是强化一下取消关注的状态
                        tvUserToFollow.setText("关注");
                        tvUserToFollow.setBackgroundResource(R.drawable.rectangle_bg_red);
                        isFollow = false;
                        tvUserToFollow.setEnabled(true);

                        try {
                            Gson gson = new Gson();
                            DynamicFollowData dynamicFollowData = gson.fromJson(response, DynamicFollowData.class);
                            if (dynamicFollowData != null) {
                                //弹出来自服务器的信息
                                Toast.makeText(UserPagerActivity.this, dynamicFollowData.message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    private class UserPagerAdapter extends FragmentPagerAdapter {

        public UserPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Log.i(TAG, "getItem: position = " + position);

            BaseFragment fragment = null;
            switch (position) {
                case 0:
                    fragment = new UserPostFragment();
                    break;
                case 1:
                    fragment = new UserAboutMeFragment();
                    break;
                default:
                    break;
            }
            return fragment;

            /*//使用UserPagerFragmentFactory之后,fragment被缓存,改变访问的用户时Fragment的构造方法不会被再次调用,
            //所以此处没有采用FriendsFragmentFactory缓存
            BaseFragment baseFragment = UserPagerFragmentFactory.getFragment(position);
            return baseFragment;*/
        }

        @Override
        public int getCount() {
            return 2; //2指两个界面来回切换, 加上动态时需要置为3; 此处是魔术数字
        }
    }

    public String getLookUserId() {
        return lookUserId;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10 && resultCode == 20) {
            if (data != null) {
                boolean isEdit = data.getBooleanExtra("IS_EDIT", false);
                Log.i(TAG, "onActivityResult: isEdit = " + isEdit);
                if (isEdit) {
                    //更新UserPagerActivity数据
                    String mUserId = SPUtils.getString(UserPagerActivity.this, GlobalConstants.userID, "");
                    gotoGetUserInfo(mUserId, lookUserId);

                    //更新UserAboutMeFragment数据,"android:switcher:" + R.id.vp_user_pager + ":1"这个字符串表示的就是该fragment的tag，其中1是fragment在viewpager中的位置。
                    BaseFragment fragment = (BaseFragment) getFragmentManager().findFragmentByTag("android:switcher:" + R.id.vp_user_pager + ":1");
                    if (fragment != null && fragment.getView() != null) { //可能没有实例化
                        fragment.initData();//自定义方法更新数据
                    }
                }
            }
        } else if (requestCode == 30 && resultCode == 31) { //从动态评论页面删除动态时返回回来
            int deletePosition = data.getIntExtra("DELETE_POSITION", -1);
            if (onBackListener != null) {
                onBackListener.onBack(deletePosition);
            }
        }
    }

    private void initHandler() {
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                float alpha = ((float) msg.what) / 100f;
                Log.e("Behavior", "alpha : msg.what = " + msg.what + ", alpha = " + alpha);
                llToolbarBg.setAlpha(alpha);

                if (msg.what == 0) {
                    llToolbarBg.setVisibility(View.INVISIBLE);
                    toolbar.setNavigationIcon(R.drawable.back);
                } else {
                    llToolbarBg.setVisibility(View.VISIBLE);
                    toolbar.setNavigationIcon(R.drawable.back_black);
                    //toolbar.setTitle(tvUserNickname.getText());
                }
            }
        };
    }

    //观察者模式(回调):就是一个对象的引用传递
    //观察者:DynamicAdapter 被观察者:UserPagerActivity(返回事件的发生者)
    public interface OnBackListener{
        void onBack(int deletePosition);
    }

    private OnBackListener onBackListener;

    public void setOnBackListener(OnBackListener onBackListener){
        this.onBackListener = onBackListener;
    }

    /*--------------------以下为状态栏设置---------------------------*/

    /**
     * 设置状态栏黑色字体图标，
     * 适配4.4以上版本MIUIV、Flyme和6.0以上版本其他Android
     * @param activity
     * @param dark
     *         是否把状态栏字体及图标颜色设置为深色
     * @return 1:MIUUI 2:Flyme 3:android6.0
     */
    public void StatusBarLightMode(Activity activity, boolean dark) {
        if (MIUISetStatusBarLightMode(activity.getWindow(), dark)) {
            systemBarTint();
            if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)) {
                activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            systemBarTint();
            activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }

    public void systemBarTint() {
        //        BKConstant.isSystemBarTint = true;
      /*
        *已下处理状态栏的问题
                * **/
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            //window.setStatusBarColor(Color.parseColor("#ffffff"));
            window.setStatusBarColor(Color.parseColor("#00000000"));

            /*if (this instanceof MainActivity) {
                window.setStatusBarColor(Color.parseColor("#00000000"));
                return;
            }*/

            ViewGroup mContentView = (ViewGroup) findViewById(Window.ID_ANDROID_CONTENT);
            View mChildView = mContentView.getChildAt(0);
            if (mChildView != null) {
                //注意不是设置 ContentView 的 FitsSystemWindows, 而是设置 ContentView 的第一个子 View . 使其不为系统 View 预留空间.
                ViewCompat.setFitsSystemWindows(mChildView, true);
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
            // 创建状态栏的管理实例
            SystemBarTintManager tintManager = new SystemBarTintManager(this);
            // 激活状态栏设置
            tintManager.setStatusBarTintEnabled(true);
            //		    // 激活导航栏设置
            tintManager.setNavigationBarTintEnabled(true);
            // 设置一个颜色给系统栏  #ff4444红色

            //tintManager.setTintColor(Color.parseColor("#ffffff"));
            tintManager.setTintColor(Color.parseColor("#00000000"));

            /*if (this instanceof MainActivity) {
                tintManager.setTintColor(Color.parseColor("#00000000"));
                return;
            }*/

            ViewGroup mContentView = (ViewGroup) findViewById(Window.ID_ANDROID_CONTENT);
            View mChildView = mContentView.getChildAt(0);
            if (mChildView != null) {
                //注意不是设置 ContentView 的 FitsSystemWindows, 而是设置 ContentView 的第一个子 View . 使其不为系统 View 预留空间.
                ViewCompat.setFitsSystemWindows(mChildView, true);
            }
        }


    }

    private void setTranslucentStatus(boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }


    /**
     * 设置状态栏图标为深色和魅族特定的文字风格
     * 可以用来判断是否为Flyme用户
     * @param window
     *         需要设置的窗口
     * @param dark
     *         是否把状态栏字体及图标颜色设置为深色
     * @return boolean 成功执行返回true
     */
    public boolean FlymeSetStatusBarLightMode(Window window, boolean dark) {
        boolean result = false;
        if (window != null) {
            try {
                WindowManager.LayoutParams lp = window.getAttributes();
                Field darkFlag = WindowManager.LayoutParams.class
                        .getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON");
                Field meizuFlags = WindowManager.LayoutParams.class
                        .getDeclaredField("meizuFlags");
                darkFlag.setAccessible(true);
                meizuFlags.setAccessible(true);
                int bit = darkFlag.getInt(null);
                int value = meizuFlags.getInt(lp);
                if (dark) {
                    value |= bit;
                } else {
                    value &= ~bit;
                }
                meizuFlags.setInt(lp, value);
                window.setAttributes(lp);
                result = true;
            } catch (Exception e) {

            }
        }
        return result;
    }

    /**
     * 设置状态栏字体图标为深色，需要MIUIV6以上
     * @param window
     *         需要设置的窗口
     * @param dark
     *         是否把状态栏字体及图标颜色设置为深色
     * @return boolean 成功执行返回true
     */
    public boolean MIUISetStatusBarLightMode(Window window, boolean dark) {
        boolean result = false;
        if (window != null) {
            Class clazz = window.getClass();
            try {
                int darkModeFlag = 0;
                Class layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
                Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
                darkModeFlag = field.getInt(layoutParams);
                Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);
                if (dark) {
                    extraFlagField.invoke(window, darkModeFlag, darkModeFlag);//状态栏透明且黑色字体
                } else {
                    extraFlagField.invoke(window, 0, darkModeFlag);//清除黑色字体
                }
                result = true;
            } catch (Exception e) {

            }
        }
        return result;
    }

}
