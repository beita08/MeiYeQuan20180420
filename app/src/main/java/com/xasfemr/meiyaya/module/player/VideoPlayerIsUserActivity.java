package com.xasfemr.meiyaya.module.player;

import android.app.Fragment;
import android.content.DialogInterface;
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
import com.xasfemr.meiyaya.module.player.adapter.VideoContentPageAdapter;
import com.xasfemr.meiyaya.module.player.fragment.VideoContentFragment;
import com.xasfemr.meiyaya.module.player.protocol.VideoSummaryProtocol;
import com.xasfemr.meiyaya.receiver.NEPhoneCallStateObserver;
import com.xasfemr.meiyaya.receiver.NEScreenStateObserver;
import com.xasfemr.meiyaya.receiver.Observer;
import com.xasfemr.meiyaya.utils.LogUtils;
import com.xasfemr.meiyaya.utils.UIUtil;
import com.xasfemr.meiyaya.view.LoadDataView;
import com.xasfemr.meiyaya.view.ShareCourseDialog;
import com.xasfemr.meiyaya.weight.SFDialog;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;
import org.simple.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 *第三方视频录制专用播放器
 * 视频播放页面，处理点播，回放
 * 此页面需传：视频路径，用户头像，观看次数，title，简介
 */

public class VideoPlayerIsUserActivity extends MVPBaseActivity implements MyMediaController.onClickIsFullScreenListener {
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
//    @BindView(R.id.civ_user_icon)
//    ImageView ivUserIcon;
//    @BindView(R.id.tv_user_name)
//    TextView tvUserName;
//    @BindView(R.id.tv_view_nums)
//    TextView tvViewNums;
//    @BindView(R.id.tv_coursename)
//    TextView tvCoursename;
//    @BindView(R.id.tv_des)
//    TextView tvDes;
    @BindView(R.id.tv_top_title)
    TextView tvTitle;
    @BindView(R.id.iv_top_back)
    ImageView ivBack;
//    @BindView(R.id.ivAttention)
//    ImageView ivAttention;
    @BindView(R.id.layout_down)
    LinearLayout layout_down;
    @BindView(R.id.layout_video)
    LinearLayout layout_video;
    @BindView(R.id.live_layout)
    LinearLayout live_layout;

    @BindView(R.id.tlVideoContent)
    TabLayout tlVideoContent;
    @BindView(R.id.vpVideoContent)
    ViewPager vpVideoContent;

    @BindView(R.id.iv_top_search)
    ImageView imgShare;

    private final String[] mTitles = {"简介","评论"};
    private List<Fragment> listragment;
    private String videoId="";

    private String coursename="";


    private boolean isBig =false;

    @Override
    protected int layoutId() {
        return R.layout.activity_video_player_is_user;
    }

    @Override
    protected ViewGroup loadDataViewLayout() {
        return null;
    }

    @Override
    protected void initView() {
        EventBus.getDefault().register(this);

        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) layout_video.getLayoutParams();
        layoutParams.height= UIUtil.getScreenSize(this,true)/3;
        layout_video.setLayoutParams(layoutParams);


        NEPhoneCallStateObserver.getInstance().observeLocalPhoneObserver(localPhoneObserver, true);
        mScreenStateObserver = new NEScreenStateObserver(this);
        mScreenStateObserver.observeScreenStateObserver(screenStateObserver, true);


        VideoSummaryProtocol protocol =new VideoSummaryProtocol();
        protocol.userID=getIntent().getStringExtra("user_id");
        LogUtils.show("---讲师ID，",getIntent().getStringExtra("user_id"));
        protocol.userName=getIntent().getStringExtra("user_name");
        protocol.coursename=getIntent().getStringExtra("coursename");
        protocol.icon=getIntent().getStringExtra("icon");
        protocol.des=getIntent().getStringExtra("des");
        protocol.viewNums=getIntent().getStringExtra("view");
        coursename=getIntent().getStringExtra("coursename");
        videoId=getIntent().getStringExtra("video_id");


        tvTitle.setText(coursename);
        if (!TextUtils.isEmpty(getIntent().getStringExtra("videoPath"))){
            mVideoPath = getIntent().getStringExtra("videoPath");
        }



        ivBack.setOnClickListener(v -> finish());
        mMediaController = new MyMediaController(this);
        mMediaController.setClickIsFullScreenListener(this);

        mVideoView.setMediaController(mMediaController);
//        mVideoView.setBufferingIndicator(mBuffer);
        mVideoView.setMediaType("videoondemand");
        mVideoView.setHardwareDecoder(true); //是否开启硬件加速
        mVideoView.setBufferStrategy(NEType.NELPANTIJITTER); //点播抗抖动
        mVideoView.setEnableBackgroundPlay(mEnableBackgroundPlay);
        mVideoView.setVideoPath(mVideoPath);
        mVideoView.requestFocus();
        mVideoView.setVideoScalingMode(0);
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



    @Override
    protected void getLoadView(LoadDataView loadView) {


    }

    @Override
    protected void initPresenter() {
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






    @Override
    protected void onPause() {
        Log.i(TAG, "NEVideoPlayerActivity onPause");

        super.onPause();
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "NEVideoPlayerActivity onDestroy");
//        mMediaController.destroy();
        mVideoView.destroy();
        super.onDestroy();
        NEPhoneCallStateObserver.getInstance().observeLocalPhoneObserver(localPhoneObserver, false);
        mScreenStateObserver.observeScreenStateObserver(screenStateObserver, false);
        mScreenStateObserver = null;
        EventBus.getDefault().unregister(this);
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
//            			mVideoView.restorePlayWithForeground();
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
    }


    @Override
    protected void onRestart() {
        Log.i(TAG, "NEVideoPlayerActivity onRestart");
        super.onRestart();
    }



    @Override
    public void setOnClickIsFullScreen() {

        if (!isBig){
            mVideoView.setVideoScalingMode(3);
            rl_top_bar.setVisibility(View.GONE);
            layout_down.setVisibility(View.GONE);
            isBig=true;

            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) layout_video.getLayoutParams();
            layoutParams.height=layoutParams.MATCH_PARENT;

            layout_video.setLayoutParams(layoutParams);

        }else {
            isBig=false;
            mVideoView.setVideoScalingMode(0);
            rl_top_bar.setVisibility(View.VISIBLE);
            layout_down.setVisibility(View.VISIBLE);
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) layout_video.getLayoutParams();
            layoutParams.height= UIUtil.getScreenSize(this,true)/3;
            layout_video.setLayoutParams(layoutParams);



        }


//        if(this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
//            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//            rl_top_bar.setVisibility(View.GONE);
//
//        }else{
//            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//            rl_top_bar.setVisibility(View.VISIBLE);
//        }
    }


    @Override
    public void onBackPressed() {
        mBackPressed = true;

        if (isBig){
            mVideoView.setVideoScalingMode(0);
            rl_top_bar.setVisibility(View.VISIBLE);
            layout_down.setVisibility(View.VISIBLE);
            isBig=false;
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) layout_video.getLayoutParams();
            layoutParams.height= UIUtil.getScreenSize(this,true)/3;
            layout_video.setLayoutParams(layoutParams);
        }else {
            finish();
        }

//        super.onBackPressed();
    }




    //视频第一帧显示
    @Subscriber(mode = ThreadMode.MAIN,tag = GlobalConstants.EventBus.LIVE_GOLD_INIT)
    public void  onLiveOneStart(String nums){
        LogUtils.show("开始喽---",nums);
        if (nums.equals("0")){
            live_layout.setVisibility(View.GONE);
        }


    }


    @Subscriber(mode = ThreadMode.MAIN,tag = GlobalConstants.EventBus.PLAYER_COMPLETED_OR_ERROR)
    public void onPlayerCompleted(String status){
        if (status.equals("completed")){  //播放完成``
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




}
