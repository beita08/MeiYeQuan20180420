package com.xasfemr.meiyaya.fragment;

import android.Manifest;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.xasfemr.meiyaya.R;
import com.xasfemr.meiyaya.activity.CompanyAuthActivity;
import com.xasfemr.meiyaya.activity.LiveCreateActivity;
import com.xasfemr.meiyaya.activity.LoginActivity;
import com.xasfemr.meiyaya.activity.MyCollectActivity;
import com.xasfemr.meiyaya.activity.MyCourseActivity;
import com.xasfemr.meiyaya.activity.MyFriendsActivity;
import com.xasfemr.meiyaya.activity.MyGoldActivity;
import com.xasfemr.meiyaya.activity.MyGradeActivity;
import com.xasfemr.meiyaya.activity.MyInvitationCodeActivity;
import com.xasfemr.meiyaya.activity.MyLearnActivity;
import com.xasfemr.meiyaya.activity.MyMemberActivity;
import com.xasfemr.meiyaya.activity.MyMessageActivity;
import com.xasfemr.meiyaya.activity.MySettingActivity;
import com.xasfemr.meiyaya.activity.RealNameAuthActivity2;
import com.xasfemr.meiyaya.activity.UserPagerActivity;
import com.xasfemr.meiyaya.base.fragment.MVPBaseFragment;
import com.xasfemr.meiyaya.bean.CurrentMeUser;
import com.xasfemr.meiyaya.bean.SiginStatus;
import com.xasfemr.meiyaya.global.GlobalConstants;
import com.xasfemr.meiyaya.main.MainActivity;
import com.xasfemr.meiyaya.utils.LogUtils;
import com.xasfemr.meiyaya.utils.MD5Utils;
import com.xasfemr.meiyaya.utils.SPUtils;
import com.xasfemr.meiyaya.utils.permission.MPermission;
import com.xasfemr.meiyaya.utils.permission.annotation.OnMPermissionDenied;
import com.xasfemr.meiyaya.utils.permission.annotation.OnMPermissionGranted;
import com.xasfemr.meiyaya.view.LoadDataView;
import com.xasfemr.meiyaya.weight.SFDialog;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import butterknife.BindView;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;

/**
 * A simple {@link Fragment} subclass.
 */
public class MeFragment extends MVPBaseFragment implements View.OnClickListener {
    private static final String TAG = "MeFragment";

    private MainActivity mainActivity;
    private Intent       mIntent;
    private int     userGrowth         = 0;     //用户成长值默认为0
    private String  userInvitationCode = "";    //用户的专属邀请码
    private boolean userSignin         = false; //用户是否签到
    private boolean isLecturer         = false; //用户是否是讲师
    private boolean isMember           = false; //用户是否是会员
    private int rate; //金币兑换比例
    private String memberOvertime = "";
    private String userName       = "";
    private String userIcon       = "";
    private int    goldNumber     = 0;

    @BindView(R.id.iv_me_user_bg)
    ImageView ivMeUserBg;

    @BindView(R.id.civ_me_user_icon)
    CircleImageView civMeUserIcon;

    @BindView(R.id.rl_me_logined)
    RelativeLayout rlMeLogined;

    @BindView(R.id.rl_me_unlogin)
    RelativeLayout rlMeUnlogin;

    @BindView(R.id.tv_me_login_regis)
    TextView tvMeLoginRegis;
    @BindView(R.id.tv_me_user_name)
    TextView tvMeUserName;

    @BindView(R.id.iv_me_user_gender)
    ImageView ivMeUserGender;

    @BindView(R.id.iv_me_user_mem)
    ImageView ivMeUserMember;

    @BindView(R.id.tv_me_user_grade)
    TextView tvMeUserGrade;

    @BindView(R.id.tv_me_user_signin)
    TextView tvMeUserSignin;

    @BindView(R.id.tv_my_course)
    TextView tvMyCourse;

    @BindView(R.id.tv_my_subscribe)
    TextView tvMySubscribe;

    @BindView(R.id.rl_me_open_course)
    RelativeLayout rlMeOpenCourse;

    @BindView(R.id.tv_me_oc_become_lecturer)
    TextView tvMeOCBecomeLect; //我要开课后面的'成为讲师'文字,控制其显示和隐藏;


    @BindView(R.id.rl_my_message)
    RelativeLayout rlMyMessage;

    @BindView(R.id.iv_my_msg_redDot)
    ImageView ivMyMsgRedDot;

    @BindView(R.id.rl_my_friends)
    RelativeLayout rlMyFriends;

    @BindView(R.id.rl_my_learn)
    RelativeLayout rlMyLearn;

    @BindView(R.id.rl_my_member)
    RelativeLayout rlMyMember;

    @BindView(R.id.rl_my_gold)
    RelativeLayout rlMyGold;

    @BindView(R.id.rl_me_invitation_code)
    RelativeLayout rlMeInvitationCode;

    @BindView(R.id.rl_me_company_auth)
    RelativeLayout rlMeCompanyAuth;

    @BindView(R.id.rl_me_share_meiyaya)
    RelativeLayout rlMeShareMeiyaya;

    @BindView(R.id.rl_me_setting)
    RelativeLayout rlMeSetting;

    private String   uname   = "";
    private String[] myPerms = {Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO};

    /*@Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity) getActivity();
    }*/


    @Override
    protected int layoutId() {
        return R.layout.fragment_me;
    }

    //填充布局,初始化fragment中的view,@return 返回fragment所包装的view对象
    @Override
    public void initView() {
        mainActivity = (MainActivity) getActivity();
        /*View view = View.inflate(mainActivity, R.layout.fragment_me, null);
        ivMeUserBg = (ImageView) view.findViewById(R.id.iv_me_user_bg);
        civMeUserIcon = (CircleImageView) view.findViewById(R.id.civ_me_user_icon);
        rlMeLogined = (RelativeLayout) view.findViewById(R.id.rl_me_logined);
        rlMeUnlogin = (RelativeLayout) view.findViewById(R.id.rl_me_unlogin);
        tvMeLoginRegis = (TextView) view.findViewById(R.id.tv_me_login_regis);
        tvMeUserName = (TextView) view.findViewById(R.id.tv_me_user_name);
        ivMeUserGender = (ImageView) view.findViewById(R.id.iv_me_user_gender);
        ivMeUserMember = (ImageView) view.findViewById(R.id.iv_me_user_mem);
        tvMeUserGrade = (TextView) view.findViewById(R.id.tv_me_user_grade);
        tvMeUserSignin = (TextView) view.findViewById(R.id.tv_me_user_signin);
        tvMyCourse = (TextView) view.findViewById(R.id.tv_my_course);
        tvMySubscribe = (TextView) view.findViewById(R.id.tv_my_subscribe);
        rlMeOpenCourse = (RelativeLayout) view.findViewById(R.id.rl_me_open_course);
        tvMeOCBecomeLect = (TextView) view.findViewById(R.id.tv_me_oc_become_lecturer);
        rlMyMessage = (RelativeLayout) view.findViewById(R.id.rl_my_message);
        ivMyMsgRedDot = (ImageView) view.findViewById(R.id.iv_my_msg_redDot);
        rlMyFriends = (RelativeLayout) view.findViewById(R.id.rl_my_friends);
        rlMyLearn = (RelativeLayout) view.findViewById(R.id.rl_my_learn);
        rlMyMember = (RelativeLayout) view.findViewById(R.id.rl_my_member);
        rlMyGold = (RelativeLayout) view.findViewById(R.id.rl_my_gold);
        rlMeInvitationCode = (RelativeLayout) view.findViewById(R.id.rl_me_invitation_code);
        rlMeShareMeiyaya = (RelativeLayout) view.findViewById(R.id.rl_me_share_meiyaya);
        rlMeSetting = (RelativeLayout) view.findViewById(R.id.rl_me_setting);*/

        civMeUserIcon.setOnClickListener(this);
        tvMeLoginRegis.setOnClickListener(this);
        tvMeUserName.setOnClickListener(this);
        tvMeUserGrade.setOnClickListener(this);
        tvMeUserSignin.setOnClickListener(this);
        tvMyCourse.setOnClickListener(this);
        tvMySubscribe.setOnClickListener(this);
        rlMeOpenCourse.setOnClickListener(this);
        rlMyMessage.setOnClickListener(this);
        rlMyFriends.setOnClickListener(this);
        rlMyLearn.setOnClickListener(this);
        rlMyMember.setOnClickListener(this);
        rlMyGold.setOnClickListener(this);
        rlMeInvitationCode.setOnClickListener(this);
        rlMeCompanyAuth.setOnClickListener(this);
        rlMeShareMeiyaya.setOnClickListener(this);
        rlMeSetting.setOnClickListener(this);

        tvMeOCBecomeLect.setVisibility(View.VISIBLE);
        //return view;
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

    @Override
    public void onStart() {
        super.onStart();
        updateRedDot();
    }

    @Override
    public void onResume() {
        super.onResume();
        tvMeOCBecomeLect.setVisibility(View.VISIBLE);
        boolean isLoginState = SPUtils.getboolean(mainActivity, GlobalConstants.isLoginState, false);
        if (isLoginState) {
            rlMeLogined.setVisibility(View.VISIBLE);
            rlMeUnlogin.setVisibility(View.GONE);

            String phoneNumber = SPUtils.getString(mainActivity, GlobalConstants.phoneNumber, "");
            String password = SPUtils.getString(mainActivity, GlobalConstants.password, "");

            //获取缓存
            String cache = SPUtils.getString(mainActivity, MD5Utils.getMd5(GlobalConstants.URL_USER_ME), null);
            if (cache != null) {
                try {
                    parseJson(cache);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            //不管有没有缓存都应该要获取服务器最新的数据
            getDataFromServer(phoneNumber);

        } else {
            rlMeLogined.setVisibility(View.GONE);
            rlMeUnlogin.setVisibility(View.VISIBLE);

            Glide.with(MeFragment.this).clear(ivMeUserBg);
            Glide.with(MeFragment.this).clear(civMeUserIcon);
        }
    }

    private void getDataFromServer(String phoneNumber) {

        OkHttpUtils.get().url(GlobalConstants.URL_USER_ME)
                .addParams("phoneNumber", phoneNumber)  //13720549469
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Toast.makeText(mainActivity, "网络访问失败!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        //保存'我的'信息的缓存
                        SPUtils.putString(mainActivity, MD5Utils.getMd5(GlobalConstants.URL_USER_ME), response);
                        try {
                            parseJson(response);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    private void parseJson(String json) {
        Gson gson = new Gson();
        LogUtils.show("个人信息-- ", json);
        CurrentMeUser currentMeUser = gson.fromJson(json, CurrentMeUser.class);

        userName = currentMeUser.data.username;
        userIcon = currentMeUser.data.images;
        goldNumber = currentMeUser.data.goldmoney;
        rate = currentMeUser.data.rate; //金币兑换比例
        uname = currentMeUser.data.uname;//实名姓名

        //将用户名和头像存至本地
        SPUtils.putString(getActivity(), GlobalConstants.USER_NAME, currentMeUser.data.username);
        SPUtils.putString(getActivity(), GlobalConstants.USER_HEAD_IMAGE, currentMeUser.data.images);

        //将用户金额存至本地
        SPUtils.putInt(getActivity(), GlobalConstants.USER_GOLD_NUMBER, currentMeUser.data.goldmoney);

        //用户头像和主页背景
        Glide.with(MeFragment.this).load(currentMeUser.data.bgimg).into(ivMeUserBg);
        Glide.with(MeFragment.this).load(currentMeUser.data.images).into(civMeUserIcon);

        //用户名
        tvMeUserName.setText(currentMeUser.data.username);

        //用户性别
        if (currentMeUser.data.sex == 1) {
            ivMeUserGender.setVisibility(View.VISIBLE);
            ivMeUserGender.setImageResource(R.drawable.gender_boy);
        } else if (currentMeUser.data.sex == 2) {
            ivMeUserGender.setVisibility(View.VISIBLE);
            ivMeUserGender.setImageResource(R.drawable.gender_girl);
        } else {
            ivMeUserGender.setVisibility(View.GONE);
        }

        //是否会员
        isMember = currentMeUser.data.ustatus == 1;
        memberOvertime = currentMeUser.data.ustatus_overtime;
        ivMeUserMember.setImageResource(isMember ? R.drawable.member_crown : R.drawable.member_no_crown);

        //签到状态
        userSignin = currentMeUser.data.sign == 1; //sign : 1已签  0未签
        if (userSignin) {
            tvMeUserSignin.setBackgroundResource(R.drawable.me_day_signed);
        } else {
            tvMeUserSignin.setBackgroundResource(R.drawable.me_day_unsign);
        }

        //邀请码
        userInvitationCode = currentMeUser.data.invitation;
        //成长值,等级
        userGrowth = currentMeUser.data.growth;
        if (userGrowth >= 0 && userGrowth <= 100) {
            tvMeUserGrade.setText("LV.1");
        } else if (userGrowth >= 101 && userGrowth <= 300) {
            tvMeUserGrade.setText("LV.2");
        } else if (userGrowth >= 301 && userGrowth <= 500) {
            tvMeUserGrade.setText("LV.3");
        } else if (userGrowth >= 501 && userGrowth <= 1000) {
            tvMeUserGrade.setText("LV.4");
        } else if (userGrowth >= 1001 && userGrowth <= 2000) {
            tvMeUserGrade.setText("LV.5");
        } else if (userGrowth >= 2001 && userGrowth <= 3500) {
            tvMeUserGrade.setText("LV.6");
        } else if (userGrowth >= 3501 && userGrowth <= 5500) {
            tvMeUserGrade.setText("LV.7");
        } else if (userGrowth >= 5501 && userGrowth <= 8000) {
            tvMeUserGrade.setText("LV.8");
        } else if (userGrowth >= 8001 && userGrowth <= 11000) {
            tvMeUserGrade.setText("LV.9");
        } else if (userGrowth > 11000) {
            tvMeUserGrade.setText("LV.10");
        } else {
            LogUtils.show(TAG, "数据有误");
        }

        //是否是讲师
        //lstatus：讲师状态（1为讲师）
        isLecturer = currentMeUser.data.lstatus == 1;
        if (isLecturer) {
            tvMeOCBecomeLect.setVisibility(View.GONE);
        } else {
            tvMeOCBecomeLect.setVisibility(View.VISIBLE);
        }
    }

    //private boolean checkWritePermission() {
    //   if (bWritePermission) {
    //         return true;
    //     } else {
    //         requestBasicPermission();
    //         ToastUtil.showShort(getActivity(),"请授予相机和录音权限");
    //         return false;
    //     }
    //}
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

    @Override
    public void onClick(View v) {

        //如果未登录,就直接跳到登录页面,并且结束方法; //测试提交
        boolean isLoginState = SPUtils.getboolean(mainActivity, GlobalConstants.isLoginState, false);
        if (!isLoginState) {
            Intent intentLogin = new Intent(mainActivity, LoginActivity.class);
            mainActivity.startActivity(intentLogin);
            return;
        }

        String mUserId = SPUtils.getString(mainActivity, GlobalConstants.userID, "");
        if (mIntent == null) {
            mIntent = new Intent();
        }
        switch (v.getId()) {
            case R.id.civ_me_user_icon: //用户头像
                mIntent.setClass(mainActivity, UserPagerActivity.class);
                mIntent.putExtra("LOOK_USER_ID", mUserId);
                mainActivity.startActivity(mIntent);
                break;

            case R.id.tv_me_login_regis:    //登录注册
                mIntent.setClass(mainActivity, LoginActivity.class);
                mainActivity.startActivity(mIntent);
                break;

            case R.id.tv_me_user_name:    //用户名
                mIntent.setClass(mainActivity, UserPagerActivity.class);
                mainActivity.startActivity(mIntent);
                break;

            case R.id.tv_me_user_grade:    //用户等级
                mIntent.setClass(mainActivity, MyGradeActivity.class);
                mIntent.putExtra("USER_NAME", userName);
                mIntent.putExtra("USER_ICON", userIcon);
                mIntent.putExtra("GROWTH", userGrowth);
                mainActivity.startActivity(mIntent);
                break;

            case R.id.tv_me_user_signin:   //签到
                if (userSignin) {
                    Toast.makeText(mainActivity, "今日已签到", Toast.LENGTH_SHORT).show();
                } else {
                    tvMeUserSignin.setBackgroundResource(R.drawable.me_day_signed);
                    userSignin = true;
                    gotoSigin();
                }
                break;
            case R.id.tv_my_course:        //我的课程
                /*mIntent.setClass(mainActivity, MyCourseSubscribeActivity.class);
                mIntent.putExtra("COUR_OR_SUBS", 0);
                mainActivity.startActivity(mIntent);*/

                mIntent.setClass(mainActivity, MyCourseActivity.class);
                mainActivity.startActivity(mIntent);

                break;

            case R.id.tv_my_subscribe:     //我的订阅
                /*mIntent.setClass(mainActivity, MyCourseSubscribeActivity.class);
                mIntent.putExtra("COUR_OR_SUBS", 1);
                mainActivity.startActivity(mIntent);*/

                mIntent.setClass(mainActivity, MyCollectActivity.class);
                mainActivity.startActivity(mIntent);
                break;

            case R.id.rl_me_open_course:   //我要开课

                if (isLecturer) { //是讲师直接开课

                    if (bWritePermission) {
                        startActivity(new Intent(getActivity(), LiveCreateActivity.class));
                    } else {
                        requestBasicPermission();
                    }

                } else {          //不是讲师去实名认证
                    mIntent.setClass(mainActivity, RealNameAuthActivity2.class);
                    mainActivity.startActivity(mIntent);
                }

                break;

            case R.id.rl_my_message:       //我的消息
                mIntent.setClass(mainActivity, MyMessageActivity.class);
                mainActivity.startActivity(mIntent);
                break;

            case R.id.rl_my_friends://我的好友,(已经从'我的'模块移至'美业圈'模块,在布局文件中已经gone掉,不影响效果)
                mIntent.setClass(mainActivity, MyFriendsActivity.class);
                mIntent.putExtra("LOOK_USER_ID", mUserId);
                mainActivity.startActivity(mIntent);
                break;
            case R.id.rl_my_learn: //我的学习
                mIntent.setClass(mainActivity, MyLearnActivity.class);
                mainActivity.startActivity(mIntent);
                break;

            case R.id.rl_my_member:    //我的会员
                mIntent.setClass(mainActivity, MyMemberActivity.class);
                mIntent.putExtra("USER_NAME", userName);
                mIntent.putExtra("USER_ICON", userIcon);
                mIntent.putExtra("IS_MEMBER", isMember);
                mIntent.putExtra("MEMBER_OVERTIME", memberOvertime);
                mainActivity.startActivity(mIntent);
                break;

            case R.id.rl_my_gold:       //我的金币

                mIntent.setClass(mainActivity, MyGoldActivity.class);
                mIntent.putExtra("GOLD_NUMBER", goldNumber);
                mIntent.putExtra("IS_LECTURER", isLecturer);
                mIntent.putExtra("RATE", rate); //金币兑换比例
                mIntent.putExtra("UNAME", uname);
                mainActivity.startActivity(mIntent);
                break;

            case R.id.rl_me_invitation_code:  // 邀请码(已gone掉) 功能转至 分享美页圈
                mIntent.setClass(mainActivity, MyInvitationCodeActivity.class);
                mIntent.putExtra("InvitationCode", userInvitationCode);
                mainActivity.startActivity(mIntent);
                break;

            case R.id.rl_me_company_auth:  // 企业认证
                mIntent.setClass(mainActivity, CompanyAuthActivity.class);
                mainActivity.startActivity(mIntent);
                break;

            case R.id.rl_me_share_meiyaya:    // 分享美页圈
                /*ShareDialog shareDialog = new ShareDialog(mainActivity);
                shareDialog.show();*/
                mIntent.setClass(mainActivity, MyInvitationCodeActivity.class);
                mIntent.putExtra("USER_NAME", userName);
                mIntent.putExtra("USER_ICON", userIcon);
                mIntent.putExtra("InvitationCode", userInvitationCode);
                mainActivity.startActivity(mIntent);

                break;

            case R.id.rl_me_setting:          // 设置
                mIntent.setClass(mainActivity, MySettingActivity.class);
                mainActivity.startActivity(mIntent);
                break;

            default:
                break;
        }
    }

    //点击签到
    private void gotoSigin() {
        String userId = SPUtils.getString(mainActivity, GlobalConstants.userID, "");
        OkHttpUtils
                .get()
                .url(GlobalConstants.URL_USER_SIGNIN)
                .addParams("id", userId)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        e.printStackTrace();
                        userSignin = false;   //网络访问失败,签到失败
                        tvMeUserSignin.setBackgroundResource(R.drawable.me_day_unsign);
                        Toast.makeText(mainActivity, "网络出现异常,请重新签到", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        userSignin = true;   //网络访问成功,签到成功
                        try {
                            parseSiginJson(response);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    private void parseSiginJson(String json) {
        Gson gson = new Gson();
        SiginStatus siginStatus = gson.fromJson(json, SiginStatus.class);
        switch (siginStatus.status) {
            case 200:
                Toast.makeText(mainActivity, siginStatus.message, Toast.LENGTH_SHORT).show();
                break;
            case 202:
                Toast.makeText(mainActivity, siginStatus.message, Toast.LENGTH_SHORT).show();
                break;
            default:
                Toast.makeText(mainActivity, "签到成功,但获取信息出现异常!", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    public void updateRedDot() {
        String unReadCount = mainActivity.getUnReadCount();
        LogUtils.show(TAG, "unReadCount = " + unReadCount);
        Log.i("RedDot", "updateRedDot: unReadCount = 1111111 " + unReadCount);
        if (TextUtils.isEmpty(unReadCount) || TextUtils.equals(unReadCount, "null")) {
            ivMyMsgRedDot.setVisibility(View.INVISIBLE);
        } else {
            ivMyMsgRedDot.setVisibility(View.VISIBLE);
        }
    }

    //MainActivity中调用此方法
    @Override
    public void updateRedDotByActivity(String unReadCount) {
        super.updateRedDotByActivity(unReadCount);
        Log.i("RedDot", "updateRedDot: unReadCount = 2222222 " + unReadCount);
        if (TextUtils.isEmpty(unReadCount) || TextUtils.equals(unReadCount, "null")) {
            ivMyMsgRedDot.setVisibility(View.INVISIBLE);
        } else {
            ivMyMsgRedDot.setVisibility(View.VISIBLE);
        }
    }


}

