package com.xasfemr.meiyaya.module.college.activity;

import android.app.Fragment;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xasfemr.meiyaya.R;
import com.xasfemr.meiyaya.base.activity.MVPBaseActivity;
import com.xasfemr.meiyaya.global.GlobalConstants;
import com.xasfemr.meiyaya.module.college.adapter.ExcellentCoursePageAdapter;
import com.xasfemr.meiyaya.module.college.fragment.CommentFragment;
import com.xasfemr.meiyaya.module.college.fragment.DirectoryFragment;
import com.xasfemr.meiyaya.module.college.fragment.SummaryFragment;
import com.xasfemr.meiyaya.module.college.protocol.ExcellentListProtocol;
import com.xasfemr.meiyaya.module.player.MyMediaController;
import com.xasfemr.meiyaya.module.player.PlayerVideoView;
import com.xasfemr.meiyaya.receiver.NEPhoneCallStateObserver;
import com.xasfemr.meiyaya.receiver.NEScreenStateObserver;
import com.xasfemr.meiyaya.receiver.Observer;
import com.xasfemr.meiyaya.utils.LogUtils;
import com.xasfemr.meiyaya.utils.ToastUtil;
import com.xasfemr.meiyaya.view.LoadDataView;
import com.xasfemr.meiyaya.view.ShareCourseDialog;
import com.xasfemr.meiyaya.view.progress.SFProgressDialog;
import com.xasfemr.meiyaya.weight.SFDialog;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;
import org.simple.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * 精品课程详情
 * Created by sen.luo on 2017/12/15.
 */

public class ExcellentCourseActivity extends MVPBaseActivity implements MyMediaController.onClickIsFullScreenListener {
    private final String[] mTitles = {"简介", "目录","评论"};
    private List<Fragment>        listragment;
    private boolean               isScreenOff;
    private boolean               mBackPressed;
    private boolean               isBackground;
    private NEScreenStateObserver mScreenStateObserver;

    @BindView(R.id.tlExcellent)
    TabLayout tlExcellent;
    @BindView(R.id.vpExcellent)
    ViewPager vpExcellent;
    private ExcellentListProtocol excellentListProtocollist;
    @BindView(R.id.video_play_view)
    PlayerVideoView mVideoView;
    @BindView(R.id.layoutContent)
    LinearLayout    layoutContent;
    @BindView(R.id.live_layout)
    LinearLayout    layoutRoot;
    @BindView(R.id.rl_top_bar)
    RelativeLayout  rl_top_bar;//Toolbar 横屏时隐藏
    @BindView(R.id.iv_top_back)
    ImageView       ivBack;
    @BindView(R.id.tv_top_title)
    TextView        tvTitle;

    @BindView(R.id.iv_top_search)
    ImageView imgShare;

    private String videopath = "";

    private MyMediaController mMediaController;
    private SFProgressDialog sfProgressDialog;


    private String seedingId="";// 单节课程Id
    private String videoId=""; //整套课程Id


    @Override
    protected int layoutId() {
        return R.layout.activity_excellent_course;
    }

    @Override
    protected ViewGroup loadDataViewLayout() {
        return null;
    }

    @Override
    protected void initView() {
        EventBus.getDefault().register(this);
        sfProgressDialog=new SFProgressDialog(this);
        ivBack.setOnClickListener(v -> finish());
        NEPhoneCallStateObserver.getInstance().observeLocalPhoneObserver(localPhoneObserver, true);
        mScreenStateObserver = new NEScreenStateObserver(this);
        mScreenStateObserver.observeScreenStateObserver(screenStateObserver, true);

        excellentListProtocollist = (ExcellentListProtocol) getIntent().getSerializableExtra("protocol");
        if (excellentListProtocollist != null && excellentListProtocollist.list != null && excellentListProtocollist.list.size() > 0) {
            videopath = excellentListProtocollist.list.get(0).origurl; //默认播放列表一
            LogUtils.show("video", "origurl = " + excellentListProtocollist.list.get(0).origurl);
        }
        tvTitle.setText(excellentListProtocollist.title);

        listragment = new ArrayList<>();
        for (int i = 0; i < mTitles.length; i++) {
            switch (i) {
                case 0:
                    listragment.add(new SummaryFragment(excellentListProtocollist));
                    break;
                case 1:
                    listragment.add(new DirectoryFragment(excellentListProtocollist.list));
                    break;
                case 2:
                    listragment.add(new CommentFragment(excellentListProtocollist.id,"1")); //默认将第一条视频id传过去
                    break;
            }
        }

        vpExcellent.setAdapter(new ExcellentCoursePageAdapter(this.getFragmentManager(), listragment));
        tlExcellent.setupWithViewPager(vpExcellent);
        tlExcellent.setTabMode(TabLayout.MODE_FIXED);


        mMediaController = new MyMediaController(this);
        mMediaController.setClickIsFullScreenListener(this);

        mVideoView.setMediaController(mMediaController);
        //        mVideoView.setBufferingIndicator(mBuffer);
        mVideoView.setMediaType("localaudio");
        mVideoView.setHardwareDecoder(false);
        mVideoView.setEnableBackgroundPlay(true);
        mVideoView.requestFocus();



        imgShare.setVisibility(View.VISIBLE);
        imgShare.setImageDrawable(getResources().getDrawable(R.drawable.news_share));
        imgShare.setOnClickListener(v ->getShare() );

    }

    private void getShare(){
        ShareCourseDialog shareCourseDialog = new ShareCourseDialog(this,
                excellentListProtocollist.title,excellentListProtocollist.list.get(0).id, excellentListProtocollist.id); //单节课程ID 整套课程ID
        shareCourseDialog.show();
    }


    @Override
    protected void initData() {
        if (!TextUtils.isEmpty(videopath)) {
            mVideoView.setVideoPath(videopath);
            mVideoView.start();
        } else {
            ToastUtil.showShort(this, "视频地址出错，暂时无法播放");
        }


    }

    @Override
    protected void getLoadView(LoadDataView loadView) {

    }

    @Override
    protected void initPresenter() {

    }

    @Override
    public void setOnClickIsFullScreen() {
        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            layoutContent.setVisibility(View.GONE);
            rl_top_bar.setVisibility(View.GONE);

        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            layoutContent.setVisibility(View.VISIBLE);
            rl_top_bar.setVisibility(View.VISIBLE);
        }
    }


    //视频第一帧显示
    @Subscriber(mode = ThreadMode.MAIN, tag = GlobalConstants.EventBus.LIVE_GOLD_INIT)
    public void onLiveOneStart(String nums) {
        LogUtils.show("开始喽---", nums);
        if (sfProgressDialog!=null&&sfProgressDialog.isShowing()){
            sfProgressDialog.dismiss();
        }

        if (nums.equals("0")) {
            layoutRoot.setVisibility(View.GONE);
        }


    }

    @Subscriber(mode = ThreadMode.MAIN, tag = GlobalConstants.EventBus.CHANGE_PLAYER_PATH)
    public void changePlayerPath(String path) {
        if (mVideoView != null) {
            if (sfProgressDialog!=null){
                sfProgressDialog.show();
            }

            LogUtils.show("切换播放源", path);
            mVideoView.changeUrl(path);
            //            mVideoView.setVideoPath(path);
            //            mVideoView.start();

        }
    }


    Observer<Integer> localPhoneObserver = new Observer<Integer>() {
        @Override
        public void onEvent(Integer phoneState) {
            if (phoneState == TelephonyManager.CALL_STATE_IDLE) {
                mVideoView.restorePlayWithCall();
            } else if (phoneState == TelephonyManager.CALL_STATE_RINGING) {
                mVideoView.stopPlayWithCall();
            } else {
                LogUtils.show("", "localPhoneObserver onEvent " + phoneState);
            }

        }
    };

    Observer<NEScreenStateObserver.ScreenStateEnum> screenStateObserver = new Observer<NEScreenStateObserver.ScreenStateEnum>() {
        @Override
        public void onEvent(NEScreenStateObserver.ScreenStateEnum screenState) {
            if (screenState == NEScreenStateObserver.ScreenStateEnum.SCREEN_ON) {
                if (isScreenOff) {
                    mVideoView.restorePlayWithForeground();
                }
                isScreenOff = false;
            } else if (screenState == NEScreenStateObserver.ScreenStateEnum.SCREEN_OFF) {
                isScreenOff = true;
                if (!isBackground) {
                    mVideoView.stopPlayWithBackground();
                }
            } else {
                //				isScreenOff = false;
            }

        }
    };

    @Subscriber(mode = ThreadMode.MAIN, tag = GlobalConstants.EventBus.PLAYER_COMPLETED_OR_ERROR)
    public void onPlayerCompleted(String status) {
        if (status.equals("completed")) {  //播放完成
            SFDialog.basicDialog(this, "提示", "播放完成，点击确认退出", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
        } else {//播放错误
            SFDialog.onlyConfirmDialog(this, "提示", "播放出现错误，请退出重试", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
        }
    }

    @Override
    protected void onResume() {

        super.onResume();
        if (!mBackPressed && !isScreenOff && isBackground) {
            //			mVideoView.restorePlayWithForeground();
            isBackground = false;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (!mBackPressed && !isScreenOff) {
            mVideoView.stopPlayWithBackground();
            isBackground = true;

        }
    }


    @Override
    public void onBackPressed() {
        mBackPressed = true;

        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            layoutContent.setVisibility(View.VISIBLE);
            rl_top_bar.setVisibility(View.VISIBLE);
        } else {
            finish();
        }

        //        super.onBackPressed();
    }

    @Override
    public void onDestroy() {
        mVideoView.destroy();
        super.onDestroy();
        NEPhoneCallStateObserver.getInstance().observeLocalPhoneObserver(localPhoneObserver, false);
        mScreenStateObserver.observeScreenStateObserver(screenStateObserver, false);
        mScreenStateObserver = null;
        EventBus.getDefault().unregister(this);
    }

}
