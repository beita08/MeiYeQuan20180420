package com.xasfemr.meiyaya.module.player;

import android.app.Fragment;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.netease.neliveplayer.sdk.constant.NEType;
import com.xasfemr.meiyaya.R;
import com.xasfemr.meiyaya.base.activity.MVPBaseActivity;
import com.xasfemr.meiyaya.global.GlobalConstants;
import com.xasfemr.meiyaya.module.college.fragment.CommentFragment;
import com.xasfemr.meiyaya.module.college.presenter.CollegePresenter;
import com.xasfemr.meiyaya.module.player.adapter.VideoContentPageAdapter;
import com.xasfemr.meiyaya.module.player.fragment.VideoContentFragment;
import com.xasfemr.meiyaya.module.player.protocol.VideoSummaryProtocol;
import com.xasfemr.meiyaya.receiver.NEPhoneCallStateObserver;
import com.xasfemr.meiyaya.receiver.NEScreenStateObserver;
import com.xasfemr.meiyaya.receiver.Observer;

import com.xasfemr.meiyaya.utils.LogUtils;
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
 * 视频播放页面，处理点播，回放
 * 此页面需传：视频路径，用户头像，观看次数，title，简介
 */

public class VideoPlayerActivity extends MVPBaseActivity implements MyMediaController.onClickIsFullScreenListener {
    public final static String TAG = "VideoPlayerActivity";

    private MyMediaController mMediaController;

    private String mVideoPath; //文件路径
    private String mDecodeType;//解码类型，硬解或软解
    private String mMediaType; //媒体类型
    private boolean mHardware = true;

    private boolean mEnableBackgroundPlay = true;
    private boolean mBackPressed;

    private NEScreenStateObserver mScreenStateObserver;
    private boolean               isScreenOff;
    private boolean               isBackground;


    @BindView(R.id.rl_top_bar)
    RelativeLayout rl_top_bar;//Toolbar 横屏时隐藏
    @BindView(R.id.layout_root)
    LinearLayout layout_root;
    @BindView(R.id.video_play_view)
    PlayerVideoView mVideoView;  //用于画面显示

    @BindView(R.id.tv_top_title)
    TextView tvTitle;
    @BindView(R.id.iv_top_back)
    ImageView ivBack;

    @BindView(R.id.layoutContent)
    LinearLayout layoutContent;


    @BindView(R.id.live_layout)
    LinearLayout live_layout;

    @BindView(R.id.tlVideoContent)
    TabLayout tlVideoContent;
    @BindView(R.id.vpVideoContent)
    ViewPager vpVideoContent;

    @BindView(R.id.iv_top_search)
    ImageView imgShare;

    private LoadDataView loadDataView;


//    private String userName="";
//    private String userId="";
//    private String icon ="";
    private String coursename="";
//    private String viewNums=""; //播放次数
//    private String des="";
    private SFProgressDialog progressDialog;
    private CollegePresenter collegePresenter;

    private final String[] mTitles = {"简介","评论"};
    private List<Fragment> listragment;
    private String videoId="";

    @Override
    protected int layoutId() {
        return R.layout.activity_video_player;
    }

    @Override
    protected ViewGroup loadDataViewLayout() {
        return null;
    }

    @Override
    protected void initView() {
        EventBus.getDefault().register(this);
        progressDialog=new SFProgressDialog(this);

        videoId=getIntent().getStringExtra("video_id");
        VideoSummaryProtocol protocol =new VideoSummaryProtocol();
        protocol.userID=getIntent().getStringExtra("user_id");
        protocol.userName=getIntent().getStringExtra("user_name");
        protocol.coursename=getIntent().getStringExtra("coursename");
        coursename=getIntent().getStringExtra("coursename");
        protocol.icon=getIntent().getStringExtra("icon");
        protocol.des=getIntent().getStringExtra("des");
        protocol.viewNums=getIntent().getStringExtra("view");


        tvTitle.setText(coursename);
        if (!TextUtils.isEmpty(getIntent().getStringExtra("videoPath"))){
            mVideoPath = getIntent().getStringExtra("videoPath");
        }


        NEPhoneCallStateObserver.getInstance().observeLocalPhoneObserver(localPhoneObserver, true);
        mScreenStateObserver = new NEScreenStateObserver(this);
        mScreenStateObserver.observeScreenStateObserver(screenStateObserver, true);

        ivBack.setOnClickListener(v -> finish());
        mMediaController = new MyMediaController(this);
        mMediaController.setClickIsFullScreenListener(this);

        mVideoView.setMediaController(mMediaController);
//        mVideoView.setBufferingIndicator(mBuffer);
        mVideoView.setBufferStrategy(NEType.NELPANTIJITTER); //0为直播极速模式，1为直播低延时模式，2为直播流畅模式, 3为点播抗抖动模式
        mVideoView.setMediaType(mMediaType);
        mVideoView.setHardwareDecoder(false); //是否开启硬件解码
        mVideoView.setEnableBackgroundPlay(mEnableBackgroundPlay);
        mVideoView.setVideoPath(mVideoPath);
        mVideoView.requestFocus();
        mVideoView.start();


        listragment = new ArrayList<>();
        for (int i = 0; i < mTitles.length; i++) {
            switch (i) {
                case 0:
                    listragment.add(new VideoContentFragment(protocol));
                    break;
                case 1:
                    listragment.add(new CommentFragment(videoId,"0")); //默认将第一条视频id传过去
                    break;
            }
        }

        vpVideoContent.setAdapter(new VideoContentPageAdapter(this.getFragmentManager(), listragment));
        tlVideoContent.setupWithViewPager(vpVideoContent);
        tlVideoContent.setTabMode(TabLayout.MODE_FIXED);


        imgShare.setVisibility(View.VISIBLE);
        imgShare.setImageDrawable(getResources().getDrawable(R.drawable.news_share));
        imgShare.setOnClickListener(v ->getShare() );

    }

    private void getShare(){
        ShareCourseDialog shareCourseDialog = new ShareCourseDialog(this,coursename,videoId, "");
        shareCourseDialog.show();
    }

    @Override
    protected void initData() {


    }

    //视频第一帧显示
    @Subscriber(mode = ThreadMode.MAIN,tag = GlobalConstants.EventBus.LIVE_GOLD_INIT)
    public void  onLiveOneStart(String nums){
        LogUtils.show("开始喽---",nums);
        if (nums.equals("0")){
            live_layout.setVisibility(View.GONE);
        }


    }





    @Override
    protected void getLoadView(LoadDataView loadView) {
//        this.loadDataView=loadView;
//        loadDataView.setErrorListner(v -> initData());


    }

    @Override
    protected void initPresenter() {
        collegePresenter=new CollegePresenter();
    }



    Observer<Integer> localPhoneObserver = new Observer<Integer>() {
        @Override
        public void onEvent(Integer phoneState) {
            if (phoneState == TelephonyManager.CALL_STATE_IDLE) {
                mVideoView.restorePlayWithCall();
            } else if (phoneState == TelephonyManager.CALL_STATE_RINGING) {
                mVideoView.stopPlayWithCall();
            } else {
                Log.i(TAG, "localPhoneObserver onEvent " + phoneState);
            }

        }
    };

    Observer<NEScreenStateObserver.ScreenStateEnum> screenStateObserver = new Observer<NEScreenStateObserver.ScreenStateEnum>() {
        @Override
        public void onEvent(NEScreenStateObserver.ScreenStateEnum screenState) {
            if (screenState == NEScreenStateObserver.ScreenStateEnum.SCREEN_ON) {
                Log.i(TAG, "onScreenOn ");
                if (isScreenOff) {
                    mVideoView.restorePlayWithForeground();
                }
                isScreenOff = false;
            } else if (screenState == NEScreenStateObserver.ScreenStateEnum.SCREEN_OFF) {
                Log.i(TAG, "onScreenOff ");
                isScreenOff = true;
                if (!isBackground) {
                    mVideoView.stopPlayWithBackground();
                }
            } else {
                Log.i(TAG, "onUserPresent ");
                //				isScreenOff = false;
            }

        }
    };

    @Subscriber(mode = ThreadMode.MAIN,tag = GlobalConstants.EventBus.PLAYER_COMPLETED_OR_ERROR)
    public void onPlayerCompleted(String status){
        if (status.equals("completed")){  //播放完成
            SFDialog.basicDialog(this, "提示", "播放完成，点击确认退出", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
        }else {//播放错误
            SFDialog.onlyConfirmDialog(this, "提示", "播放出现错误，请退出重试", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
        }
    }


    @Override
    public void onDestroy() {
        mVideoView.destroy();
        super.onDestroy();
        NEPhoneCallStateObserver.getInstance().observeLocalPhoneObserver(localPhoneObserver, false);
        mScreenStateObserver.observeScreenStateObserver(screenStateObserver, false);
        mScreenStateObserver = null;
        EventBus.getDefault().unregister(this);
        collegePresenter.destroy();
    }

    @Override
    protected void onStart() {
        Log.i(TAG, "NEVideoPlayerActivity onStart");
        super.onStart();
    }

    @Override
    protected void onResume() {
        Log.i(TAG, "NEVideoPlayerActivity onResume");

        super.onResume();
        if (!mBackPressed && !isScreenOff && isBackground) {
            //			mVideoView.restorePlayWithForeground();
            isBackground = false;
        }
    }

    @Override
    protected void onStop() {
        Log.i(TAG, "NEVideoPlayerActivity onStop");
        super.onStop();

        if (!mBackPressed && !isScreenOff) {
            mVideoView.stopPlayWithBackground();
            isBackground = true;

        }
        EventBus.getDefault().unregister(this);
    }


    @Override
    protected void onRestart() {
        Log.i(TAG, "NEVideoPlayerActivity onRestart");
        super.onRestart();
    }

    @Override
    public void setOnClickIsFullScreen() {
        if(this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            rl_top_bar.setVisibility(View.GONE);
            layoutContent.setVisibility(View.GONE);

        }else{
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            rl_top_bar.setVisibility(View.VISIBLE);
            layoutContent.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public void onBackPressed() {
        mBackPressed = true;

        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            rl_top_bar.setVisibility(View.VISIBLE);
            layoutContent.setVisibility(View.VISIBLE);
        }else {
            finish();
        }

//        super.onBackPressed();
    }




//    @Override
//    public void onConfigurationChanged(Configuration newConfig) {
//        if(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE){
//            layout_root.setVisibility(View.GONE);
//            rl_top_bar.setVisibility(View.GONE);
//
//        }else{
//            layout_root.setVisibility(View.VISIBLE);
//            rl_top_bar.setVisibility(View.VISIBLE);
//        }
//        super.onConfigurationChanged(newConfig);
//        mVideoView.refreshDrawableState();
//
//    }

}
