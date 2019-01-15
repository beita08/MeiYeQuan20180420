package com.xasfemr.meiyaya.neteasecloud;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.netease.neliveplayer.sdk.constant.NEType;
import com.xasfemr.meiyaya.R;
import com.xasfemr.meiyaya.media.NEMediaController;
import com.xasfemr.meiyaya.media.NEVideoView;
import com.xasfemr.meiyaya.receiver.NEPhoneCallStateObserver;
import com.xasfemr.meiyaya.receiver.NEScreenStateObserver;
import com.xasfemr.meiyaya.receiver.Observer;
import com.xasfemr.meiyaya.media.NEMediaController.OnHiddenListener;
import com.xasfemr.meiyaya.media.NEMediaController.OnShownListener;

import java.util.List;

//视频点播的播放器
public class NEVideoPlayerActivity extends Activity {
    public final static String TAG = "NEVideoPlayerActivity";
    public  NEVideoView       mVideoView;  //用于画面显示
    private View              mBuffer; //用于指示缓冲状态
    private NEMediaController mMediaController; //用于控制播放

    private String mVideoPath; //文件路径
    private String mDecodeType;//解码类型，硬解或软解
    private String mMediaType; //媒体类型
    private boolean mHardware = true;
    private ImageButton mPlayBack;
    private TextView    mFileName; //文件名称
    private String      mTitle;
    private Uri         mUri;

    /**
     * mEnableBackgroundPlay 控制退到后台或者锁屏时是否继续播放，开发者可根据实际情况灵活开发,我们的示例逻辑如下：
     * mEnableBackgroundPlay 为 false时，
     * 使用软件编码或者硬件解码，点播进入后台暂停，进入前台恢复播放，直播进入后台停止播放，进入前台重新拉流播放
     * mEnableBackgroundPlay 为 true时，
     * 使用软件编码，点播和直播进入后台不做处理，继续播放
     * 使用硬件解码，点播和直播进入后台统一停止播放，进入前台的话重新拉流播放
     */

    private boolean mEnableBackgroundPlay = true;
    private boolean mBackPressed;

    private RelativeLayout        mPlayToolbar;
    private NEScreenStateObserver mScreenStateObserver;
    private boolean               isScreenOff;
    private boolean               isBackground;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ne_video_player);
        NEPhoneCallStateObserver.getInstance().observeLocalPhoneObserver(localPhoneObserver, true);
        mScreenStateObserver = new NEScreenStateObserver(this);
        mScreenStateObserver.observeScreenStateObserver(screenStateObserver, true);

        //接收MainActivity传过来的参数
        mMediaType = getIntent().getStringExtra("media_type");
        mDecodeType = getIntent().getStringExtra("decode_type");
        mVideoPath = getIntent().getStringExtra("videoPath");

        Log.i(TAG, "playType = " + mMediaType);
        Log.i(TAG, "decodeType = " + mDecodeType);
        Log.i(TAG, "videoPath = " + mVideoPath);

        if (mMediaType.equals("localaudio")) { //本地音频文件采用软件解码
            mDecodeType = "software";
        }

        Intent intent = getIntent();
        String intentAction = intent.getAction();
        if (!TextUtils.isEmpty(intentAction) && intentAction.equals(Intent.ACTION_VIEW)) {
            mVideoPath = intent.getDataString();
            Log.i(TAG, "videoPath = " + mVideoPath);
        }

        if (mDecodeType.equals("hardware")) {
            mHardware = true;
        } else if (mDecodeType.equals("software")) {
            mHardware = false;
        }

        mPlayBack = (ImageButton) findViewById(R.id.player_exit);//退出播放
        mPlayBack.getBackground().setAlpha(0);
        mFileName = (TextView) findViewById(R.id.file_name);

        mUri = Uri.parse(mVideoPath);
        if (mUri != null) { //获取文件名，不包括地址
            List<String> paths = mUri.getPathSegments();
            String name = paths == null || paths.isEmpty() ? "null" : paths.get(paths.size() - 1);
            setFileName(name);
        }

        mPlayToolbar = (RelativeLayout) findViewById(R.id.play_toolbar);
        mPlayToolbar.setVisibility(View.INVISIBLE);

        mBuffer = findViewById(R.id.buffering_prompt);
        mMediaController = new NEMediaController(this);

        mVideoView = (NEVideoView) findViewById(R.id.video_view);

        if (mMediaType.equals("livestream")) {
            mVideoView.setBufferStrategy(NEType.NELPLOWDELAY); //直播低延时
        } else {
            mVideoView.setBufferStrategy(NEType.NELPANTIJITTER); //点播抗抖动
        }
        mVideoView.setMediaController(mMediaController);
        mVideoView.setBufferingIndicator(mBuffer);
        mVideoView.setMediaType(mMediaType);
        mVideoView.setHardwareDecoder(mHardware);
        mVideoView.setEnableBackgroundPlay(mEnableBackgroundPlay);
        mVideoView.setVideoPath(mVideoPath);
        mVideoView.requestFocus();
        mVideoView.start();

        mPlayBack.setOnClickListener(mOnClickEvent); //监听退出播放的事件响应
        mMediaController.setOnShownListener(mOnShowListener); //监听mediacontroller是否显示
        mMediaController.setOnHiddenListener(mOnHiddenListener); //监听mediacontroller是否隐藏
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

    OnClickListener mOnClickEvent = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.player_exit) {
                Log.i(TAG, "player_exit");
                mBackPressed = true;
                finish();
            }
        }
    };

    OnShownListener mOnShowListener = new OnShownListener() {
        @Override
        public void onShown() {
            mPlayToolbar.setVisibility(View.VISIBLE);
            mPlayToolbar.requestLayout();
            mVideoView.invalidate();
            mPlayToolbar.postInvalidate();
        }
    };

    OnHiddenListener mOnHiddenListener = new OnHiddenListener() {
        @Override
        public void onHidden() {
            mPlayToolbar.setVisibility(View.INVISIBLE);
        }
    };

    public void setFileName(String name) { //设置文件名并显示出来
        mTitle = name;
        if (mFileName != null) {
            mFileName.setText(mTitle);
            mFileName.setGravity(Gravity.CENTER);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        System.out.println("---NEVideoPlayerActivity---onConfigurationChanged---" + newConfig);
    }

    @Override
    public void onBackPressed() {
        Log.i(TAG, "onBackPressed");
        mBackPressed = true;
        finish();

        super.onBackPressed();
    }


    @Override
    protected void onPause() {
        Log.i(TAG, "NEVideoPlayerActivity onPause");

        super.onPause();
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "NEVideoPlayerActivity onDestroy");
        mMediaController.destroy();
        mVideoView.destroy();
        super.onDestroy();
        NEPhoneCallStateObserver.getInstance().observeLocalPhoneObserver(localPhoneObserver, false);
        mScreenStateObserver.observeScreenStateObserver(screenStateObserver, false);
        mScreenStateObserver = null;
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
    }


    @Override
    protected void onRestart() {
        Log.i(TAG, "NEVideoPlayerActivity onRestart");
        super.onRestart();
    }
}