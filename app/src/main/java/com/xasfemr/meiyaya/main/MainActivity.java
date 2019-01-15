package com.xasfemr.meiyaya.main;

import android.Manifest;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.xasfemr.meiyaya.BuildConfig;
import com.xasfemr.meiyaya.R;
import com.xasfemr.meiyaya.activity.CompanyAuthActivity;
import com.xasfemr.meiyaya.activity.LoginActivity;
import com.xasfemr.meiyaya.activity.MeetingReleaseActivity;
import com.xasfemr.meiyaya.activity.PostResumeActivity;
import com.xasfemr.meiyaya.base.activity.MVPBaseActivity;
import com.xasfemr.meiyaya.base.fragment.MVPBaseFragment;
import com.xasfemr.meiyaya.fragment.ClassificationFragment;
import com.xasfemr.meiyaya.fragment.MeFragment;
import com.xasfemr.meiyaya.fragment.MeiYeQuanFragment;
import com.xasfemr.meiyaya.global.GlobalConstants;
import com.xasfemr.meiyaya.module.home.activity.PostInstrumentActivity;
import com.xasfemr.meiyaya.module.home.activity.PostRecruitmentActivity;
import com.xasfemr.meiyaya.module.home.fragment.HomeNewFragment;
import com.xasfemr.meiyaya.utils.FileDownLoader;
import com.xasfemr.meiyaya.utils.LocationUtils;
import com.xasfemr.meiyaya.utils.LogUtils;
import com.xasfemr.meiyaya.utils.SPUtils;
import com.xasfemr.meiyaya.utils.ToastUtil;
import com.xasfemr.meiyaya.view.LoadDataView;
import com.xasfemr.meiyaya.view.homePop.PopMenu;
import com.xasfemr.meiyaya.view.homePop.PopMenuItem;
import com.xasfemr.meiyaya.view.homePop.PopMenuItemListener;
import com.xasfemr.meiyaya.weight.SFDialog;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;
import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;
import org.simple.eventbus.ThreadMode;

import java.io.File;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;

public class MainActivity extends MVPBaseActivity {
    private static final String TAG = "MainActivity";


    private              String unReadCount   = "";
    private static final int    TIME_INTERVAL = 2000;
    private long mBackPressed;

    private int    versionCode = 0;
    private String downloadUrl = "";

    @BindView(R.id.iv_me_redDot)
    ImageView ivMeRedDot;

    @BindView(R.id.imgHome)
    ImageView imgHome;

    @BindView(R.id.imgClassification)
    ImageView imgClassification;

    @BindView(R.id.imgDynamic)
    ImageView imgDynamic;

    @BindView(R.id.imgMine)
    ImageView imgMine;


    private FragmentTransaction fragmentTransaction;
    private int checkItem = -1;

    private Fragment homeFragment, classifyFragment, dynamicFragment, mineFragment, currentFragment;

    private PopMenu popMenu;
    private boolean isFirstPost = true;

    @Override
    protected int layoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected ViewGroup loadDataViewLayout() {
        return null;
    }

    @Override
    protected void initView() {
        EventBus.getDefault().register(this);

        initFragmentTransaction();
        findViewById(R.id.layoutHome).performClick();

//        SwipeBackHelper.getCurrentPage(this).setSwipeBackEnable(false);
        //getPerssiomm();
    }


    private void getPerssiomm() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= 23) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 11);
            }
        } else {
            LocationUtils.initLocation(this);
            LogUtils.show("经度：", Double.toString(LocationUtils.longitude));
            LogUtils.show("纬度：", Double.toString(LocationUtils.latitude));
        }
    }

    @Override
    protected void initData() {
        //检查版本更新
        getUpdate();
    }

    @Override
    protected void getLoadView(LoadDataView loadView) {

    }

    @Override
    protected void initPresenter() {
    }

    private void initFragmentTransaction() {
        if (fragmentTransaction == null) {
            fragmentTransaction = getFragmentManager().beginTransaction();
        }
    }

    @OnClick({R.id.layoutHome, R.id.layoutClassification, R.id.layoutDynamic, R.id.layoutMine, R.id.layoutRelease})
    public void onViewClick(View view) {


        switch (view.getId()) {
            case R.id.layoutHome:
                changeCheck(0);
                break;
            case R.id.layoutClassification:
                changeCheck(1);
                break;
            case R.id.layoutDynamic:
                changeCheck(2);
                break;
            case R.id.layoutMine:
                changeCheck(3);
                break;

            case R.id.layoutRelease: //发布
                if (popMenu == null) {
                    popMenu = new PopMenu.Builder().attachToActivity(MainActivity.this)
                            .addMenuItem(new PopMenuItem("发布招聘", getResources().getDrawable(R.drawable.tab_recruitment)))
                            .addMenuItem(new PopMenuItem("发布简历", getResources().getDrawable(R.drawable.tab_resume)))
                            .addMenuItem(new PopMenuItem("发布闲置", getResources().getDrawable(R.drawable.tab_idle)))
                            //.addMenuItem(new PopMenuItem("求购闲置", getResources().getDrawable(R.drawable.tab_buy_idle)))
                            .addMenuItem(new PopMenuItem("发布店铺", getResources().getDrawable(R.drawable.tab_release_shop)))
                            .addMenuItem(new PopMenuItem("求租店铺", getResources().getDrawable(R.drawable.tab_rent_shop)))
                            .addMenuItem(new PopMenuItem("发布会议", getResources().getDrawable(R.drawable.tab_release_meeting)))
                            .setOnItemClickListener(new PopMenuItemListener() {
                                @Override
                                public void onItemClick(PopMenu popMenu, int position) {

                                    //如果没有登录先去登录
                                    boolean isLoginState = SPUtils.getboolean(MainActivity.this, GlobalConstants.isLoginState, false);
                                    if (!isLoginState) {
                                        ToastUtil.showShort(MainActivity.this, "请先登录");
                                        startActivity(new Intent(MainActivity.this, LoginActivity.class));
                                        return;
                                    }

                                    if (isFirstPost) {
                                        isFirstPost = false;
                                        popMenu.setShowing(false);
                                        //如果没有进行企业认证弹窗提示用户去企业认证
                                        String isApprove = SPUtils.getString(MainActivity.this, GlobalConstants.IS_APPROVE, "0");//是否完成企业认证
                                        if (TextUtils.equals(isApprove, "0")) {//0未认证 1认证
                                            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                            builder.setTitle("企业认证");
                                            builder.setMessage("请先完成企业认证，增加企业的信誉度");
                                            builder.setNegativeButton("稍后认证", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    popMenu.setShowing(true);
                                                }
                                            });
                                            builder.setPositiveButton("现在认证", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    startActivity(new Intent(MainActivity.this, CompanyAuthActivity.class));
                                                    popMenu.setShowing(true);
                                                }
                                            });
                                            builder.show();
                                            return;
                                        }
                                    }
                                    popMenu.setShowing(true);

                                    switch (position) {
                                        case 0:
                                            startActivity(new Intent(MainActivity.this, PostRecruitmentActivity.class));
                                            break;
                                        case 1:
                                            startActivity(new Intent(MainActivity.this, PostResumeActivity.class));
                                            break;
                                        case 2:
                                            startActivity(new Intent(MainActivity.this, PostInstrumentActivity.class));
                                            break;
                                        case 3:
                                            ToastUtil.showShort(MainActivity.this, "发布店铺，即将上线");
                                            break;
                                        case 4:
                                            ToastUtil.showShort(MainActivity.this, "求租店铺，即将上线");
                                            break;
                                        case 5:
                                            startActivity(new Intent(MainActivity.this, MeetingReleaseActivity.class));
                                            break;
                                    }
                                }
                            })
                            .build();
                    popMenu.show();
                } else {
                    popMenu.show();
                }
                break;
        }
    }


    private void changeCheck(int selected) {

        if (checkItem != -1 && checkItem == selected) {
            return;
        }
        checkItem = selected;
        initFragmentTransaction();
        clearCheck();


        switch (selected) {
            case 0:
                imgHome.setSelected(true);
                if (homeFragment == null) {
                    homeFragment = new HomeNewFragment();
                }
                switchFragment(homeFragment);
                break;
            case 1:
                imgClassification.setSelected(true);

                if (classifyFragment == null) {
                    classifyFragment = new ClassificationFragment();
                }
                switchFragment(classifyFragment);
                break;
            case 2:
                imgDynamic.setSelected(true);
                if (dynamicFragment == null) {
                    //dynamicFragment = new DynamicFragment();
                    dynamicFragment = new MeiYeQuanFragment();
                }
                switchFragment(dynamicFragment);
                break;
            case 3:
                imgMine.setSelected(true);
                if (mineFragment == null) {
                    mineFragment = new MeFragment();
                }
                switchFragment(mineFragment);
                break;
        }
    }

    public void switchFragment(Fragment to) {
        initFragmentTransaction();
        if (currentFragment != to) {

            if (currentFragment == null) {
                fragmentTransaction.replace(R.id.fl_main_content, to).commitAllowingStateLoss();
                currentFragment = to;
                fragmentTransaction = null;
                return;
            }

            if (!to.isAdded()) {
                fragmentTransaction.hide(currentFragment).replace(R.id.fl_main_content, to).commitAllowingStateLoss();
            } else {
                fragmentTransaction.hide(currentFragment).show(to).commitAllowingStateLoss();
            }
        }
        currentFragment = to;
        fragmentTransaction = null;
    }

    private void clearCheck() {
        imgHome.setSelected(false);
        imgClassification.setSelected(false);
        imgDynamic.setSelected(false);
        imgMine.setSelected(false);
    }

    @Override
    protected void onStart() {
        super.onStart();
        //用户登录时更新小红点
        boolean isLoginState = SPUtils.getboolean(this, GlobalConstants.isLoginState, false);
        if (isLoginState) {
            updateRedDot("");
        } else {
            unReadCount = "";
            updateRedDotVisible();
        }
    }

    @Subscriber(mode = ThreadMode.MAIN, tag = GlobalConstants.EventBus.RECEIVE_MSG_UPDATA_DOT)
    private void updateRedDot(String s) {
        LogUtils.show("WebSocket", "MainActivity...updateRedDot");
        String mUserId = SPUtils.getString(this, GlobalConstants.userID, "");
        if (TextUtils.isEmpty(mUserId)) {
            return;
        }

        OkHttpUtils.get().url(GlobalConstants.URL_UNREAD_MSG_COUNT)
                .addParams("userid", mUserId)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        LogUtils.show(TAG, "---获取未读消息失败---");
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        LogUtils.show(TAG, "---获取未读消息成功---");
                        LogUtils.show(TAG, "response = " + response);
                        parserRedDotJson(response);
                    }
                });
    }

    private void parserRedDotJson(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            unReadCount = jsonObject.getString("data");
            LogUtils.show(TAG, "unReadCount = " + unReadCount);
            updateRedDotVisible();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void updateRedDotVisible() {

        Log.i("RedDot", "updateRedDot: unReadCount = 3333333 " + unReadCount);

        if (TextUtils.isEmpty(unReadCount) || TextUtils.equals(unReadCount, "null")) {//没有未读消息
            ivMeRedDot.setVisibility(View.INVISIBLE);
        } else {           //有未读消息
            ivMeRedDot.setVisibility(View.VISIBLE);
        }
        //网络获取数据有延迟,再次更新Fragment中的小红点
        //R.id.fl_main_content中显示的是哪个Fragment,返回的便是那个Fragment的对象;
        MVPBaseFragment fragment = (MVPBaseFragment) getFragmentManager().findFragmentById(R.id.fl_main_content);
        fragment.updateRedDotByActivity(unReadCount);

        //EventBus.getDefault().post("12",GlobalConstants.EventBus.UPDATE_MESSAGE_COUNT);

    }


    public String getUnReadCount() {
        return unReadCount;
    }

    @Override
    public void onBackPressed() {

        //先关闭菜单框
        if (popMenu != null && popMenu.isShowing()) {
            popMenu.hide();
            return;
        }

        if (mBackPressed + TIME_INTERVAL > System.currentTimeMillis()) {
            super.onBackPressed();
            return;
        } else {
            Toast.makeText(this, "再按一次返回键退出美页圈", Toast.LENGTH_SHORT).show();
        }
        mBackPressed = System.currentTimeMillis();
    }

    private void getUpdate() {

        LogUtils.show("升级接口信息", GlobalConstants.UPDATE_VERSION + BuildConfig.VERSION_CODE);
        OkHttpUtils
                .get()
                .url(GlobalConstants.UPDATE_VERSION)
                .addParams("version_code", BuildConfig.VERSION_CODE + "")
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                LogUtils.show("更新接口异常", e.getMessage());
            }

            @Override
            public void onResponse(String response, int id) {

                LogUtils.show("更新接口返回", response);

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getString("data") != null) {

                        JSONObject js = new JSONObject(jsonObject.getString("data"));
                        versionCode = js.getInt("version_code");
                        downloadUrl = js.getString("apk_url");
                        if (versionCode > BuildConfig.VERSION_CODE) {

                            SFDialog.onlyConfirmDialog(MainActivity.this, "提示：有新版本下载", js.getString("upgrade_point"), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    downloadUpadteFile(downloadUrl);
                                }
                            });


                        }


                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        });

    }

    private final static int    WRITE_EXTERNAL_STORAGE = 0x002;
    private              String Purl                   = "";

    private void downloadUpadteFile(final String url) {

        if (TextUtils.isEmpty(url)) {
            ToastUtil.showShort(MainActivity.this, "下载链接有误");
            return;
        }

        try {
            //适配6.0获取Android存储空间权限
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            WRITE_EXTERNAL_STORAGE);
                    Purl = url;
                } else {
                    // 在新线程中检查新版本
                    new DownloadApplicationTask().execute(new String[]{url});
                }
            } else {
                // 在新线程中检查新版本
                new DownloadApplicationTask().execute(new String[]{url});
            }
        } catch (Exception e) {

        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case WRITE_EXTERNAL_STORAGE://判断sd卡写入权限
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 在新线程中检查新版本
                    new DownloadApplicationTask().execute(new String[]{Purl});
                } else {
                    showMissingPermissionDialog();
                }
                break;
        }
    }


    private class DownloadApplicationTask extends AsyncTask<String, Void, Integer> {

        @Override
        protected void onPreExecute() {
            showDownLoadProgress();
            super.onPreExecute();
        }

        @Override
        protected Integer doInBackground(String... params) {
            return FileDownLoader.downloadFile(updateHandler, params[0]);
        }


        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            closeDownLoadProgress();

            File file = new File(Environment.getExternalStorageDirectory() + "/Download", "meiyaya.apk");
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            if (result == FileDownLoader.DOWN_FILE_SUCCESS) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    Uri photoURI = FileProvider.getUriForFile(MainActivity.this, getString(R.string.file_provider_path), file);
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    intent.setDataAndType(photoURI, "application/vnd.android.package-archive");
                } else {
                    intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
                }
            }
            startActivity(intent);
        }
    }


    private ProgressDialog downProgressDialog;
    private static final String PACKAGE_URL_SCHEME = "package:";

    private void showDownLoadProgress() {

        try {
            if (downProgressDialog == null) {
                downProgressDialog = new ProgressDialog(this);
            }
            if (!downProgressDialog.isShowing()) {
                downProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                downProgressDialog.setMessage("正在下载中....");
                downProgressDialog.setIcon(R.mipmap.meiyaya_logo2);
                downProgressDialog.setProgress(0);
                downProgressDialog.setMax(100);
                downProgressDialog.setIndeterminate(false);
                downProgressDialog.setCancelable(false);
                downProgressDialog.show();
            }
        } catch (Exception e) {
        }
    }


    private Handler updateHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case FileDownLoader.DOWN_FILE_ERROR:
                    ToastUtil.showShort(MainActivity.this, "下载错误");
                    break;
                case FileDownLoader.CLOSE_PROGRESSDIALOG:
                    closeDownLoadProgress();
                    break;
                case FileDownLoader.START_DOWNLOAD:
                    if (null != downProgressDialog) {
                        downProgressDialog.setProgress(0);
                        downProgressDialog.setMessage("下载中" + "\t" + msg.arg1 / 1024 + "Kb / " + msg.arg2 / 1024 + "Kb");
                    }
                    break;
                case FileDownLoader.UPDATE_DOWNLOAD_PROGRESS:
                    if (null != downProgressDialog) {
                        if (msg.arg2 != 0) {
                            downProgressDialog.setProgress(msg.arg1 * 100 / msg.arg2);
                        }
                        downProgressDialog.setMessage("下载中" + "\t" + msg.arg1 / 1024 + "Kb / " + msg.arg2 / 1024 + "Kb");
                    }
                    break;

            }
        }

    };

    public void closeDownLoadProgress() {
        try {
            if (downProgressDialog != null) {
                downProgressDialog.dismiss();
                downProgressDialog = null;
            }
        } catch (Exception e) {
        }
    }


    /**
     * 显示缺失权限提示
     */

    private void showMissingPermissionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setTitle("帮助");
        builder.setMessage("当前应用缺少下载所需存储权限\n请点击“设置”-“权限”-打开所需权限");

        // 拒绝, 退出应用
        builder.setNegativeButton("退出", (DialogInterface dialog, int which) -> dialog.dismiss());
        builder.setPositiveButton("设置", (DialogInterface dialog, int which) -> startAppSettings());
        builder.show();
    }

    /**
     * 启动应用的设置
     */

    private void startAppSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse(PACKAGE_URL_SCHEME + this.getPackageName()));
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
