package com.xasfemr.meiyaya.neteasecloud;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.netease.LSMediaCapture.lsAudioCaptureCallback;
import com.netease.LSMediaCapture.lsLogUtil;
import com.netease.LSMediaCapture.lsMediaCapture;
import com.netease.LSMediaCapture.lsMessageHandler;
import com.netease.LSMediaCapture.video.VideoCallback;
import com.netease.vcloud.video.effect.VideoEffect;
import com.netease.vcloud.video.render.NeteaseView;
import com.xasfemr.meiyaya.R;
import com.xasfemr.meiyaya.activity.LiveCreateActivity;
import com.xasfemr.meiyaya.activity.LiveEndActivity;
import com.xasfemr.meiyaya.adapter.MessageAdapter;
import com.xasfemr.meiyaya.bean.ChatMessage;
import com.xasfemr.meiyaya.global.GlobalConstants;
import com.xasfemr.meiyaya.module.mine.protocol.Shareprotocol;
import com.xasfemr.meiyaya.neteasewidget.NetWorkInfoDialog;
import com.xasfemr.meiyaya.module.player.protocol.GiftMessageProtocol;
import com.xasfemr.meiyaya.module.player.protocol.GiftProtocol;
import com.xasfemr.meiyaya.receiver.RxBroadcastTool;
import com.xasfemr.meiyaya.utils.LogUtils;
import com.xasfemr.meiyaya.utils.NetUtils;
import com.xasfemr.meiyaya.utils.SPUtils;
import com.xasfemr.meiyaya.utils.ToastUtil;
import com.xasfemr.meiyaya.view.BasicShareDialog;
import com.xasfemr.meiyaya.view.SummaryDialog;
import com.xasfemr.meiyaya.view.progress.SFProgressDialog;
import com.xasfemr.meiyaya.websocket.LiveChatClient;
import com.xasfemr.meiyaya.weight.GiftItemView;
import com.xasfemr.meiyaya.weight.SFDialog;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;
import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;
import org.simple.eventbus.ThreadMode;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;

import static com.netease.LSMediaCapture.lsMediaCapture.StreamType.AUDIO;

/*lxb:2017年11月7日
* LiveStreamingActivity就是直播的页面,此类里面主要的代码还是是网易云直播的Demo里的代码,美页圈只是对界面进行了修改
* 对于用不到的网易的界面功能,我并没有删掉,防止后面会加上.
* 对于用不到的网易的界面功能,xml文件里我采用gone掉,在代码里//注释起来;
*/

/**
 * 开课直播
 */
//由于直播推流的URL地址较长，可以直接在代码中的mliveStreamingURL设置直播推流的URL
public class LiveStreamingActivity extends AppCompatActivity implements View.OnClickListener, lsMessageHandler {
    private static final String TAG = "LiveStreamingActivity";

    private boolean mBeautyOn = false;   //美颜的三个选项是否显示

    //Demo控件
    private View      filterLayout;
    private View      configLayout;
    private ImageView startPauseResumeBtn;
    private TextView  mFpsView;
    private final int    MSG_FPS              = 1000;
    private       String mliveStreamingURL    = null;
    private       String mMixAudioFilePath    = null;
    private       File   mMP3AppFileDirectory = null;
    //状态变量
    private boolean m_liveStreamingOn                = false;
    private boolean m_liveStreamingInitFinished      = false;
    private boolean m_tryToStopLivestreaming         = false;
    private boolean m_startVideoCamera               = false;
    private Intent  mIntentLiveStreamingStopFinished = new Intent("LiveStreamingStopFinished");
    //伴音相关
    private AudioManager mAudioManager;
    private Intent mNetInfoIntent                  = new Intent(NetWorkInfoDialog.NETINFO_ACTION);
    private long   mLastVideoProcessErrorAlertTime = 0;
    private long   mLastAudioProcessErrorAlertTime = 0;
    //视频截图相关变量
    private String mScreenShotFilePath             = "/sdcard/";//视频截图文件路径
    private String mScreenShotFileName             = "test.jpg";//视频截图文件名
    //视频缩放相关变量
    private int    mMaxZoomValue                   = 0;
    private int    mCurrentZoomValue               = 0;
    private float mCurrentDistance;
    private float mLastDistance = -1;
    //Demo广播相关变量
//    private MsgReceiver               msgReceiver;
//    private audioMixVolumeMsgReceiver audioMixVolumeMsgReceiver;
    private String                    tvSummary;

    /**
     * SDK 相关参数
     **/
    private boolean mUseFilter;
    private boolean        mNeedWater      = false;
    private boolean        mNeedGraffiti   = false;
    private lsMediaCapture mLSMediaCapture = null;
    private lsMediaCapture.LiveStreamingPara mLiveStreamingPara;

    private boolean mVideoCallback = false; //是否对相机采集的数据进行回调（用户在这里可以进行自定义滤镜等）
    private boolean mAudioCallback = false; //是否对麦克风采集的数据进行回调（用户在这里可以进行自定义降噪等）

    private Toast mToast;

    //lxb:自己定义的控件
    private RelativeLayout  rlRoot;
    private ImageView       ivLiveClose;
    private ImageView       ivLiveBriefIntro;
    private PopupWindow     popupWindow;
    private LinearLayout    llLiveBeauty;
    private ImageView       ivLiveBeauty;
    private CircleImageView civUserIcon;
    private TextView        tvUserName;
    private TextView        tvLookerNum;
    private TextView        tvLiveGold;
    private ImageView       ivLiveShare;
    private ListView        messageListView;
    private String          liveCid; //直播间Id
    private LinearLayout    layoutSendMessage;
    private LinearLayout    live_btn_layout;

    private ArrayList<ChatMessage>  chatMessages;
    private MessageAdapter          messageAdapter;
    private LiveChatClient          liveChatClient;
    private EditText                sendEditText;
    private GiftItemView            gift_item_first;
    private ArrayList<GiftProtocol> gifts;


    private SFProgressDialog sfProgressDialog;

    private String live_nums    = "";//结束直播时观看人数
    private String goldmoney    = "";//结束直播金币余额
    private String goldmoney_in = "";//结束直播时本厂收入

    private String  shareSummary = ""; //直播简介
    private String  shareTitle   = "";
    private String  shareImage   = "";
    private String  shareUrl     = "";
    private boolean ifAuto       = false; //是否手动停止直播
    private boolean ifNetOpen    = false; //网络是否重新连接
    private URI socketUri;

    //Glide.with(getContext()).load(gift.img).apply(new RequestOptions().placeholder(R.drawable.meiyaya_logo_round_gray)).into(avatar);


    private void showToast(final String text) {
        if (mToast == null) {
            mToast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG);
        }
        if (Thread.currentThread() != Looper.getMainLooper().getThread()) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mToast.setText(text);
                    mToast.show();
                }
            });
        } else {
            mToast.setText(text);
            mToast.show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ne_livestreaming);
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(Color.BLACK);
        }

        EventBus.getDefault().register(this);
        sfProgressDialog = new SFProgressDialog(this);
        //应用运行时，保持屏幕高亮，不锁屏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.screenBrightness = 0.7f;
        getWindow().setAttributes(params);



        //从直播设置页面获取推流URL和分辨率信息
        LiveCreateActivity.PublishParam publishParam = (LiveCreateActivity.PublishParam) getIntent().getSerializableExtra("data");

        mliveStreamingURL = publishParam.pushUrl;
        mUseFilter = publishParam.useFilter;
        mNeedWater = publishParam.watermark;
        mNeedGraffiti = publishParam.graffitiOn;
        liveCid = publishParam.cid;
        tvSummary = publishParam.courseIntroduce;//直播简介

        m_liveStreamingOn = false;
        m_tryToStopLivestreaming = false;

        //以下为SDK调用主要步骤，请用户参考使用
        //1、创建直播实例
        lsMediaCapture.LsMediaCapturePara lsMediaCapturePara = new lsMediaCapture.LsMediaCapturePara();
        lsMediaCapturePara.setContext(this); //设置SDK上下文（建议使用ApplicationContext）
        lsMediaCapturePara.setMessageHandler(this); //设置SDK消息回调
        lsMediaCapturePara.setLogLevel(lsLogUtil.LogLevel.INFO); //日志级别
        lsMediaCapturePara.setUploadLog(publishParam.uploadLog);//是否上传SDK日志
        mLSMediaCapture = new lsMediaCapture(lsMediaCapturePara);

        //2、设置直播参数
        mLiveStreamingPara = new lsMediaCapture.LiveStreamingPara();
        mLiveStreamingPara.setStreamType(publishParam.streamType); // 推流类型 AV、AUDIO、VIDEO
        mLiveStreamingPara.setFormatType(publishParam.formatType); // 推流格式 RTMP、MP4、RTMP_AND_MP4
        mLiveStreamingPara.setRecordPath(publishParam.recordPath);//formatType 为 MP4 或 RTMP_AND_MP4 时有效
        mLiveStreamingPara.setQosOn(publishParam.qosEnable);


        //3、 预览参数设置
        NeteaseView videoView = (NeteaseView) findViewById(R.id.videoview);
        if (publishParam.streamType != AUDIO) { //开启预览画面
            boolean frontCamera = publishParam.frontCamera; // 是否前置摄像头
            boolean mScale_16x9 = publishParam.isScale_16x9; //是否强制16:9
            lsMediaCapture.VideoQuality videoQuality = publishParam.videoQuality; //视频模板（SUPER_HIGH 1280*720、SUPER 960*540、HIGH 640*480、MEDIUM 480*360、LOW 352*288）
            mLSMediaCapture.startVideoPreview(videoView, frontCamera, mUseFilter, videoQuality, mScale_16x9);
        }

        m_startVideoCamera = true;
        if (mUseFilter) { //demo中默认设置为干净滤镜
            mLSMediaCapture.setBeautyLevel(5); //磨皮强度为5,共5档，0为关闭
            mLSMediaCapture.setFilterStrength(0.5f); //滤镜强度
            mLSMediaCapture.setFilterType(publishParam.filterType);
        }


        //********** 摄像头采集原始数据回调（非滤镜模式下开发者可以修改该数据，美颜增加滤镜等，推出的流便随之发生变化） *************//
        if (mVideoCallback) {
            mLSMediaCapture.setCaptureRawDataCB(new VideoCallback() {
                int i = 0;

                @Override
                public void onVideoCapture(byte[] data, int width, int height) {
                    // 这里将data直接修改，SDK根据修改后的data数据直接推流
                    if (i % 10 == 0) {
                        for (int j = 0; j < width * height / 2; j++) {
                            data[j] = 0;
                        }
                    }
                    i++;
                }
            });
        }


        //********** 麦克风采集原始数据回调（开发者可以修改该数据，进行降噪、回音消除等，推出的流便随之发生变化） *************//
        if (mAudioCallback) {
            mLSMediaCapture.setAudioRawDataCB(new lsAudioCaptureCallback() {
                int i = 0;

                @Override
                public void onAudioCapture(byte[] data, int len) {
                    // 这里将data直接修改，SDK根据修改后的data数据直接推流
                    if (i % 10 == 0) {
                        for (int j = 0; j < 1000; j++) {
                            data[j] = 0;
                        }
                    }
                    i++;
                }
            });
        }


        //5、控件的初始化
        buttonInit();


        //lxb:直接进来就开启直播,不用再点击开播按钮
        startLive();

        //开启聊天
        socketUri = URI.create(GlobalConstants.SOCKET_URL);

        //timingHandler.postDelayed(timingRunnable,TIME);

        chatMessages = new ArrayList<>();
        messageAdapter = new MessageAdapter(this);
        messageListView.setAdapter(messageAdapter);
        messageAdapter.setDatas(chatMessages);

      //注册网络变化监听
        RxBroadcastTool.initRegisterReceiverNetWork(this);

    }


    //开启一个定时任务，每隔一分钟通过Socket send一个无意义的消息,保持握手
    private int TIME = 30000;
    Handler timingHandler = new Handler();

    Runnable timingRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                timingHandler.postDelayed(this, TIME);
                if (liveChatClient != null && liveChatClient.isOpen()) {

                    liveChatClient.send("1");
                    //                    liveChatClient.sendPing();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }


        }
    };

    private void getShareData() {
        LogUtils.show("直播间分享url", GlobalConstants.GET_LIVE_SHARE_DATA + liveCid);
        OkHttpUtils
                .post()
                .url(GlobalConstants.GET_LIVE_SHARE_DATA + liveCid)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        e.printStackTrace();
                        LogUtils.show("直播间分享信息异常", e.getMessage());
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        LogUtils.show("直播间分享信息", response);

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (!jsonObject.getString("data").equals("error")) {
                                JSONObject js = new JSONObject(jsonObject.getString("data"));
                                if (js != null) {
                                    shareImage = js.getString("cover");
                                    shareUrl = js.getString("shareUrl");
                                    shareSummary = js.getString("des");
                                    shareTitle = js.getString("title");
                                }

                            } else {
                                ToastUtil.showShort(LiveStreamingActivity.this, "获取直播间分享信息有误");
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });

    }

    //lxb:初始化美页圈自己定义的控件
    private void initMyyView() {
        //
        //        //根部局
        //        rlRoot = (RelativeLayout) findViewById(R.id.rl_root);
        //        rlRoot.addOnLayoutChangeListener(this);

        sendEditText = (EditText) findViewById(R.id.send_edit);
        //用户头像
        civUserIcon = (CircleImageView) findViewById(R.id.civ_live_user_icon);
        Glide.with(this).load(SPUtils.getString(this, GlobalConstants.USER_HEAD_IMAGE, "")).apply(new RequestOptions().placeholder(R.drawable.meiyaya_logo_round_gray)).into(civUserIcon);

        //用户名
        tvUserName = (TextView) findViewById(R.id.tv_live_user_name);
        tvUserName.setText(SPUtils.getString(this, GlobalConstants.USER_NAME, ""));

        //        //直播间人数
        tvLookerNum = (TextView) findViewById(R.id.tv_live_looker_num);
        //金币数量
        tvLiveGold = (TextView) findViewById(R.id.tv_live_home_gold);
        //关闭直播
        ivLiveClose = (ImageView) findViewById(R.id.iv_live_close);
        //美颜的三个选项
        llLiveBeauty = (LinearLayout) findViewById(R.id.ll_live_beauty);
        //直播简介按钮
        ivLiveBriefIntro = (ImageView) findViewById(R.id.iv_live_room_brief_introduction);
        //直播间美颜开关
        ivLiveBeauty = (ImageView) findViewById(R.id.iv_live_room_beauty);
        //分享
        ivLiveShare = (ImageView) findViewById(R.id.iv_live_room_share);

        live_btn_layout = (LinearLayout) findViewById(R.id.live_btn_layout);

        //礼物
        gift_item_first = (GiftItemView) findViewById(R.id.gift_item_first);

        //聊天列表
        messageListView = (ListView) findViewById(R.id.list_message);
        layoutSendMessage = (LinearLayout) findViewById(R.id.layout_send_message);

        ivLiveClose.setOnClickListener(this);
        ivLiveBriefIntro.setOnClickListener(this);
        ivLiveBeauty.setOnClickListener(this);
        ivLiveShare.setOnClickListener(this);

        gifts = new ArrayList<>();
    }

    private void startLive() {
        if (!m_liveStreamingOn) {
            //8、初始化直播推流
            if (mThread != null) {
                showToast(SPUtils.getString(LiveStreamingActivity.this, GlobalConstants.USER_NAME, "") + "开课啦");
                return;
            }
            showToast("初始化中");
            mThread = new Thread() {
                public void run() {
                    //正常网络下initLiveStream 1、2s就可完成，当网络很差时initLiveStream可能会消耗5-10s，因此另起线程防止UI卡住
                    if (!startAV()) {
                        showToast("在线课程开启失败，正在退出当前界面，请重试");
                        LiveStreamingActivity.this.finish();
//                        mHandler.postDelayed(new Runnable() {
//                            @Override
//                            public void run() {LiveStreamingActivity.this.finish();}
//                        }, 5000);
                    }
                    mThread = null;
                }
            };
            mThread.start();
        }
    }



    //开始直播
    private boolean startAV() {
        //6、初始化直播
        m_liveStreamingInitFinished = mLSMediaCapture.initLiveStream(mLiveStreamingPara, mliveStreamingURL);
        if (mLSMediaCapture != null && m_liveStreamingInitFinished) {
            //7、开始直播
            mLSMediaCapture.startLiveStreaming();
            m_liveStreamingOn = true;

            if (mNeedWater) {
                //8、设置视频水印参数（可选）
                addWaterMark();
                //9、设置视频动态水印参数（可选）
                //取消动态水印
                //addDynamicWaterMark();
            }

            return true;
        }
        return m_liveStreamingInitFinished;
    }

    private void stopAV() {
        if (mLSMediaCapture != null) {
            mLSMediaCapture.stopLiveStreaming();
        }


    }

    /**
     * 关闭直播间时调用
     * @param
     */
    private void finishHourse() {

        if (!NetUtils.isConnected(this)) {
            ToastUtil.showShort(this, "无网络链接");
            return;
        }
        if (sfProgressDialog != null) {
            sfProgressDialog.show();
        }

        String userId = SPUtils.getString(this, GlobalConstants.userID, "");
        if (TextUtils.isEmpty(userId)) {
            ToastUtil.showShort(this, "获取用户信息失败,请重新登录");
            return;
        }
        LogUtils.show("结束直播访问链接：",GlobalConstants.URL_LIVE_OVER+SPUtils.getString(this, GlobalConstants.CLIENT_ID, "")+userId+liveCid);
        OkHttpUtils
                .post()
                .url(GlobalConstants.URL_LIVE_OVER)
                .addParams("client_id", SPUtils.getString(this, GlobalConstants.CLIENT_ID, ""))
                .addParams("from_uid", userId)
                .addParams("cid", liveCid)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        e.printStackTrace();
                        LogUtils.show("结束直播异常",e.getMessage());
                        ToastUtil.showShort(LiveStreamingActivity.this, "结束直播异常");
                        if (sfProgressDialog != null&&sfProgressDialog.isShowing()) {
                            sfProgressDialog.dismiss();
                        }
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        if (sfProgressDialog != null&&sfProgressDialog.isShowing()) {
                            sfProgressDialog.dismiss();
                        }
                        LogUtils.show("结束直播成功",response);

                        if (response != null) {
                            try {
                                JSONObject js = new JSONObject(response);

                                JSONObject data = new JSONObject(js.getString("data"));
                                LogUtils.show("主播结束直播返回参数", response);
                                live_nums = data.getString("view"); //观看人次
                                goldmoney = data.getString("goldmoney");//余额
                                goldmoney_in = data.getString("goldmoney_in");//本次直播收入

                                stopAV(); //停止直播
                                ifAuto = true;
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
    }


    @Override
    protected void onPause() {
        Log.i(TAG, "Activity onPause");
        if (mLSMediaCapture != null) {
            if (!m_tryToStopLivestreaming && m_liveStreamingOn) {
                if (mLiveStreamingPara.getStreamType() != AUDIO) {
                    //推最后一帧图像
                    mLSMediaCapture.backgroundVideoEncode();
                } else {
                    //推静音帧
                    mLSMediaCapture.backgroundAudioEncode();
                }
            }
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        Log.i(TAG, "Activity onResume");
        super.onResume();
        if (mLSMediaCapture != null && m_liveStreamingOn) {
            if (mLiveStreamingPara.getStreamType() != AUDIO) {
                //关闭推流固定图像，正常推流
                mLSMediaCapture.resumeVideoEncode();
            } else {
                //关闭推流静音帧
                mLSMediaCapture.resumeAudioEncode();
            }
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        //切换横竖屏，需要在manifest中设置 android:configChanges="orientation|keyboardHidden|screenSize"
        //防止Activity重新创建而断开推流
        if (mLSMediaCapture != null) {
            mLSMediaCapture.onConfigurationChanged();
        }
    }

    @Override
    protected void onDestroy() {
        Log.i(TAG, "activity onDestroy");
        EventBus.getDefault().unregister(this);
        //伴音相关Receiver取消注册
//        unregisterReceiver(msgReceiver);
//        unregisterReceiver(audioMixVolumeMsgReceiver);
        //停止直播调用相关API接口
        if (mLSMediaCapture != null && m_liveStreamingOn) {

            //停止直播，释放资源
            stopAV();

            //如果音视频或者单独视频直播，需要关闭视频预览
            if (m_startVideoCamera) {
                mLSMediaCapture.stopVideoPreview();
                mLSMediaCapture.destroyVideoPreview();
            }

            //反初始化推流实例，当它与stopLiveStreaming连续调用时，参数为false
            mLSMediaCapture.uninitLsMediaCapture(false);
            mLSMediaCapture = null;

            mIntentLiveStreamingStopFinished.putExtra("LiveStreamingStopFinished", 2);
            sendBroadcast(mIntentLiveStreamingStopFinished);
        } else if (mLSMediaCapture != null && m_startVideoCamera) {
            mLSMediaCapture.stopVideoPreview();
            mLSMediaCapture.destroyVideoPreview();

            //反初始化推流实例，当它不与stopLiveStreaming连续调用时，参数为true
            mLSMediaCapture.uninitLsMediaCapture(true);
            mLSMediaCapture = null;

            mIntentLiveStreamingStopFinished.putExtra("LiveStreamingStopFinished", 1);
            sendBroadcast(mIntentLiveStreamingStopFinished);
        } else if (!m_liveStreamingInitFinished) {
            mIntentLiveStreamingStopFinished.putExtra("LiveStreamingStopFinished", 1);
            sendBroadcast(mIntentLiveStreamingStopFinished);

            //反初始化推流实例，当它不与stopLiveStreaming连续调用时，参数为true
            mLSMediaCapture.uninitLsMediaCapture(true);
        }

        if (m_liveStreamingOn) {
            m_liveStreamingOn = false;
        }

        super.onDestroy();
    }


    private boolean mFlashOn = false;
    private Thread mThread;

    //按钮初始化
    public void buttonInit() {
        //lxb:初始化美页圈自己定义的控件
        initMyyView();

        //闪光灯
        final ImageView flashBtn = (ImageView) findViewById(R.id.live_flash);
        flashBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mLSMediaCapture != null) {
                    mFlashOn = !mFlashOn;
                    mLSMediaCapture.setCameraFlashPara(mFlashOn);
                    if (mFlashOn) {
                        flashBtn.setImageResource(R.drawable.flashstop);
                    } else {
                        flashBtn.setImageResource(R.drawable.flashstart);
                    }
                }
            }
        });



        //切换前后摄像头按钮初始化
        View switchBtn = findViewById(R.id.live_camera_btn);
        switchBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                switchCamera();
            }
        });

        View captureBtn = findViewById(R.id.live_capture_btn);
        captureBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                capture();
            }
        });






            SeekBar filterSeekBar = ((SeekBar) findViewById(R.id.live_filter_seekbar));
            filterSeekBar.setVisibility(View.VISIBLE);
            filterSeekBar.setProgress(50);
            filterSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (mLSMediaCapture != null) {
                        float param;
                        param = (float) progress / 100;
                        mLSMediaCapture.setFilterStrength(param);
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                }
            });

            SeekBar beautySeekBar = ((SeekBar) findViewById(R.id.live_beauty_seekbar));
            beautySeekBar.setVisibility(View.VISIBLE);
            beautySeekBar.setProgress(100);
            beautySeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (mLSMediaCapture != null) {
                        int param;
                        param = progress / 20;
                        mLSMediaCapture.setBeautyLevel(param);
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                }
            });

            SeekBar SeekbarExposure = (SeekBar) findViewById(R.id.live_Exposure_seekbar);
            SeekbarExposure.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (mLSMediaCapture != null) {
                        int max = mLSMediaCapture.getMaxExposureCompensation();
                        mLSMediaCapture.setExposureCompensation((progress - 50) * max / 50);
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                }
            });
        }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            /*-------------------------------美页圈自己定义的控件的点击事件开始-----------------------------------*/
            case R.id.iv_live_close: //停止直播
                if (m_liveStreamingOn) {

                    SFDialog.basicDialog(this, "提示", "确认退出美页圈课堂吗？", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            finishHourse(); //关闭直播

                        }
                    });


                } else {
                    showToast("在线课堂未开启");
                }
                break;
            case R.id.iv_live_room_brief_introduction: // 直播间简介

                SummaryDialog summaryDialog = new SummaryDialog(this, tvSummary);
                summaryDialog.show();


                break;
            case R.id.iv_live_room_beauty:     //美颜
                if (mBeautyOn) {
                    llLiveBeauty.setVisibility(View.GONE);
                    mBeautyOn = false;
                } else {
                    llLiveBeauty.setVisibility(View.VISIBLE);
                    mBeautyOn = true;
                }
                break;
            case R.id.iv_live_room_share:     //分享直播间
                Shareprotocol shareprotocol = new Shareprotocol();
                shareprotocol.shareMsg = shareSummary;
                shareprotocol.shareImage = shareImage;
                shareprotocol.shareUrl = shareUrl;
                shareprotocol.sharetitle = shareTitle;
                shareprotocol.shareStatus="2";
                shareprotocol.shareCid=liveCid;
                BasicShareDialog shareDialog = new BasicShareDialog(LiveStreamingActivity.this, shareprotocol);
                shareDialog.show();
                break;
            /*-------------------------------美页圈自己定义的控件的点击事件结束-----------------------------------*/
            default:
                break;
        }
    }



    //切换前后摄像头
    private void switchCamera() {
        if (mLSMediaCapture != null) {
            mLSMediaCapture.switchCamera();
        }
    }

    private void capture() {
        if (mLSMediaCapture != null) {
            mLSMediaCapture.enableScreenShot();
        }
    }

    int count = 0;

    private void changeFormat() {
        int index = count % 4;
        count++;
        boolean is16x9 = true;
        switch (index) {
            case 0:
                mLSMediaCapture.changeCaptureFormat(lsMediaCapture.VideoQuality.SUPER_HIGH, is16x9);
                break;
            case 1:
                mLSMediaCapture.changeCaptureFormat(lsMediaCapture.VideoQuality.SUPER, is16x9);
                break;
            case 2:
                mLSMediaCapture.changeCaptureFormat(lsMediaCapture.VideoQuality.HIGH, is16x9);
                break;
            case 3:
                mLSMediaCapture.changeCaptureFormat(lsMediaCapture.VideoQuality.MEDIUM, is16x9);
                break;
        }
    }




    //直播加水印
    private void addWaterMark() {
        if (mLSMediaCapture != null) {
            Bitmap water = BitmapFactory.decodeResource(getResources(), R.mipmap.water_make);
            int x = 40;
            int y = 150;
            //VideoEffect.Rect.rightTop水印起始位置,, x, y 水印平移距离
            mLSMediaCapture.setWaterMarkPara(water, VideoEffect.Rect.rightTop, x, y);
        }
    }





    //处理SDK抛上来的异常和事件，用户需要在这里监听各种消息，进行相应的处理。
    @Override
    public void handleMessage(int msg, Object object) {
        switch (msg) {
            case MSG_INIT_LIVESTREAMING_OUTFILE_ERROR://初始化直播出错
            case MSG_INIT_LIVESTREAMING_VIDEO_ERROR:
            case MSG_INIT_LIVESTREAMING_AUDIO_ERROR: {
                showToast("在线课程初始化出错");
                break;
            }
            case MSG_START_LIVESTREAMING_ERROR://开始直播出错
            {
                showToast("开启在线课程出错：" + object);
                break;
            }
            case MSG_STOP_LIVESTREAMING_ERROR://停止直播出错
            {
                if (m_liveStreamingOn) {
                    showToast("停止在线课程出错");
                }
                break;
            }
            case MSG_AUDIO_PROCESS_ERROR://音频处理出错
            {
                if (m_liveStreamingOn && System.currentTimeMillis() - mLastAudioProcessErrorAlertTime >= 10000) {
                    showToast("音频处理出错");
                    mLastAudioProcessErrorAlertTime = System.currentTimeMillis();
                }

                break;
            }
            case MSG_VIDEO_PROCESS_ERROR://视频处理出错
            {
                if (m_liveStreamingOn && System.currentTimeMillis() - mLastVideoProcessErrorAlertTime >= 10000) {
                    showToast("视频处理出错");
                    mLastVideoProcessErrorAlertTime = System.currentTimeMillis();
                }
                break;
            }
            case MSG_START_PREVIEW_ERROR://视频预览出错，可能是获取不到camera的使用权限

            {
                showToast("无法打开相机，可能没有相关的权限或者自定义分辨率不支持");
                new Handler().postDelayed(this:: finish,2000);

                Log.i(TAG, "test: in handleMessage, MSG_START_PREVIEW_ERROR");
                break;
            }
            case MSG_AUDIO_RECORD_ERROR://音频采集出错，获取不到麦克风的使用权限
            {
                showToast("无法开启；录音，可能没有相关的权限");
                new Handler().postDelayed(this:: finish,2000);

                break;
            }
            case MSG_RTMP_URL_ERROR://断网消息
            {
                LogUtils.show("网易SDK", "断网回调");
                if (sfProgressDialog == null) {
                    sfProgressDialog = new SFProgressDialog(this);

                }
                sfProgressDialog.show();
                m_tryToStopLivestreaming = true;
                mLSMediaCapture.stopLiveStreaming();
                break;
            }
            case MSG_URL_NOT_AUTH://直播URL非法，URL格式不符合视频云要求
            {
                showToast("在线课程地址出现错误");
                break;
            }
            case MSG_SEND_STATICS_LOG_ERROR://发送统计信息出错
            {
                //Log.i(TAG, "test: in handleMessage, MSG_SEND_STATICS_LOG_ERROR");
                break;
            }
            case MSG_SEND_HEARTBEAT_LOG_ERROR://发送心跳信息出错
            {
                //Log.i(TAG, "test: in handleMessage, MSG_SEND_HEARTBEAT_LOG_ERROR");
                break;
            }
            case MSG_AUDIO_SAMPLE_RATE_NOT_SUPPORT_ERROR://音频采集参数不支持
            {
                LogUtils.show(TAG, "test: in handleMessage, MSG_AUDIO_SAMPLE_RATE_NOT_SUPPORT_ERROR");
                break;
            }
            case MSG_AUDIO_PARAMETER_NOT_SUPPORT_BY_HARDWARE_ERROR://音频参数不支持
            {
                LogUtils.show(TAG, "test: in handleMessage, MSG_AUDIO_PARAMETER_NOT_SUPPORT_BY_HARDWARE_ERROR");
                break;
            }
            case MSG_NEW_AUDIORECORD_INSTANCE_ERROR://音频实例初始化出错
            {
                LogUtils.show(TAG, "test: in handleMessage, MSG_NEW_AUDIORECORD_INSTANCE_ERROR");
                break;
            }
            case MSG_AUDIO_START_RECORDING_ERROR://音频采集出错
            {
                LogUtils.show(TAG, "test: in handleMessage, MSG_AUDIO_START_RECORDING_ERROR");
                break;
            }
            case MSG_QOS_TO_STOP_LIVESTREAMING://网络QoS极差，视频码率档次降到最低
            {
                showToast("网络情况差，将降低分辨率");
                LogUtils.show(TAG, "test: in handleMessage, MSG_QOS_TO_STOP_LIVESTREAMING");
                break;
            }
            case MSG_HW_VIDEO_PACKET_ERROR://视频硬件编码出错反馈消息
            {
                break;
            }
            case MSG_WATERMARK_INIT_ERROR://视频水印操作初始化出错
            {
                break;
            }
            case MSG_WATERMARK_PIC_OUT_OF_VIDEO_ERROR://视频水印图像超出原始视频出错
            {
                //Log.i(TAG, "test: in handleMessage: MSG_WATERMARK_PIC_OUT_OF_VIDEO_ERROR");
                break;
            }
            case MSG_WATERMARK_PARA_ERROR://视频水印参数设置出错
            {
                //Log.i(TAG, "test: in handleMessage: MSG_WATERMARK_PARA_ERROR");
                break;
            }
            case MSG_CAMERA_PREVIEW_SIZE_NOT_SUPPORT_ERROR://camera采集分辨率不支持
            {
                //Log.i(TAG, "test: in handleMessage: MSG_CAMERA_PREVIEW_SIZE_NOT_SUPPORT_ERROR");
                break;
            }
            case MSG_CAMERA_NOT_SUPPORT_FLASH:
                showToast("不支持闪光灯");
                break;
            case MSG_START_PREVIEW_FINISHED://camera采集预览完成
            {
                LogUtils.show(TAG, "test: MSG_START_PREVIEW_FINISHED");
                break;
            }
            case MSG_START_LIVESTREAMING_FINISHED://开始直播完成
            {
                if (sfProgressDialog != null) {
                    sfProgressDialog.dismiss();
                }


                try {
                    liveChatClient = new LiveChatClient(this, socketUri);
                    liveChatClient.connectBlocking();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    LogUtils.show("socket连接异常", e.getMessage());

                }
                //                liveChatClient =new LiveChatClient(this,socketUri);

                LogUtils.show(TAG, "test: MSG_START_LIVESTREAMING_FINISHED");
                showToast(SPUtils.getString(LiveStreamingActivity.this, GlobalConstants.USER_NAME, "") + "开课啦");
                getShareData();
                m_liveStreamingOn = true;
                break;
            }
            case MSG_STOP_LIVESTREAMING_FINISHED://停止直播完成
            {
                try {
                    if (liveChatClient != null) {
                        liveChatClient.closeBlocking();
                    }
                } catch (InterruptedException e) {
                    LogUtils.show("关闭socket连接异常", e.getMessage());
                    e.printStackTrace();
                }
                LogUtils.show(TAG, "test: MSG_STOP_LIVESTREAMING_FINISHED");

                if (ifAuto) {
                    m_liveStreamingOn = false;
                    mIntentLiveStreamingStopFinished.putExtra("LiveStreamingStopFinished", 1);
                    sendBroadcast(mIntentLiveStreamingStopFinished);

                    //停止直播 访问接口拿到数据
                    showToast("已退出美页圈课堂");
                    Intent intent = new Intent(LiveStreamingActivity.this, LiveEndActivity.class);
                    intent.putExtra("nums", live_nums);
                    intent.putExtra("goldmoney", goldmoney);
                    intent.putExtra("goldmoney_in", goldmoney_in);
                    finish();
                    startActivity(intent);
                } else {
                    LogUtils.show("开播", "重新开始推流");
                    if (ifNetOpen) {
                        mLSMediaCapture.initLiveStream(mLiveStreamingPara, mliveStreamingURL);
                        mLSMediaCapture.startLiveStreaming();
                    }
                }

                break;
            }
            case MSG_STOP_VIDEO_CAPTURE_FINISHED: {
                LogUtils.show(TAG, "test: in handleMessage: MSG_STOP_VIDEO_CAPTURE_FINISHED");
                break;
            }
            case MSG_STOP_AUDIO_CAPTURE_FINISHED: {
                LogUtils.show(TAG, "test: in handleMessage: MSG_STOP_AUDIO_CAPTURE_FINISHED");
                break;
            }
            case MSG_SWITCH_CAMERA_FINISHED://切换摄像头完成
            {
                //                int cameraId = (Integer) object;//切换之后的camera id
                break;
            }
            case MSG_SEND_STATICS_LOG_FINISHED://发送统计信息完成
            {
                //Log.i(TAG, "test: in handleMessage, MSG_SEND_STATICS_LOG_FINISHED");
                break;
            }
            case MSG_SERVER_COMMAND_STOP_LIVESTREAMING://服务器下发停止直播的消息反馈，暂时不使用
            {
                //Log.i(TAG, "test: in handleMessage, MSG_SERVER_COMMAND_STOP_LIVESTREAMING");
                break;
            }
            case MSG_GET_STATICS_INFO://获取统计信息的反馈消息
            {


//                Message message = Message.obtain(mHandler, MSG_GET_STATICS_INFO);
//                Statistics statistics = (Statistics) object;
//
//                Bundle bundle = new Bundle();
//                bundle.putInt("FR", statistics.videoEncodeFrameRate);
//                bundle.putInt("VBR", statistics.videoRealSendBitRate);
//                bundle.putInt("ABR", statistics.audioRealSendBitRate);
//                bundle.putInt("TBR", statistics.totalRealSendBitRate);
//                bundle.putInt("networkLevel", statistics.networkLevel);
//                bundle.putString("resolution", statistics.videoEncodeWidth + " x " + statistics.videoEncodeHeight);
//                message.setData(bundle);
//                //				  Log.i(TAG, "test: audio : " + statistics.audioEncodeBitRate + "  video: " + statistics.videoEncodeBitRate + "  total: " + statistics.totalRealSendBitRate);
//
//                if (mHandler != null) {
//                    mHandler.sendMessage(message);
//                }
                break;
            }
            case MSG_BAD_NETWORK_DETECT://如果连续一段时间（10s）实际推流数据为0，会反馈这个错误消息
            {
                showToast("MSG_BAD_NETWORK_DETECT");
                //Log.i(TAG, "test: in handleMessage, MSG_BAD_NETWORK_DETECT");
                break;
            }
            case MSG_SCREENSHOT_FINISHED://视频截图完成后的消息反馈
            {
//                getScreenShotByteBuffer((Bitmap) object);

                break;
            }
            case MSG_SET_CAMERA_ID_ERROR://设置camera出错（对于只有一个摄像头的设备，如果调用了不存在的摄像头，会反馈这个错误消息）
            {
                //Log.i(TAG, "test: in handleMessage, MSG_SET_CAMERA_ID_ERROR");
                break;
            }
            case MSG_SET_GRAFFITI_ERROR://设置涂鸦出错消息反馈
            {
                //Log.i(TAG, "test: in handleMessage, MSG_SET_GRAFFITI_ERROR");
                break;
            }
            case MSG_MIX_AUDIO_FINISHED://伴音一首MP3歌曲结束后的反馈
            {
                //Log.i(TAG, "test: in handleMessage, MSG_MIX_AUDIO_FINISHED");
                break;
            }
            case MSG_URL_FORMAT_NOT_RIGHT://推流url格式不正确
            {
                //Log.i(TAG, "test: in handleMessage, MSG_URL_FORMAT_NOT_RIGHT");
                LogUtils.show("开播", "MSG_URL_FORMAT_NOT_RIGHT");
                break;
            }
            case MSG_URL_IS_EMPTY://推流url为空
            {
                LogUtils.show("开播", "MSG_URL_IS_EMPTY");
                //Log.i(TAG, "test: in handleMessage, MSG_URL_IS_EMPTY");
                break;
            }

            case MSG_SPEED_CALC_SUCCESS:
            case MSG_SPEED_CALC_FAIL:
//                Message message = Message.obtain(mHandler, msg);
//                message.obj = object;
//                mHandler.sendMessage(message);
                break;

            default:
                break;

        }
    }


    //获取截屏图像的数据
//    public void getScreenShotByteBuffer(Bitmap bitmap) {
//        FileOutputStream outStream = null;
//        String screenShotFilePath = mScreenShotFilePath + mScreenShotFileName;
//        try {
//
//            outStream = new FileOutputStream(String.format(screenShotFilePath));
//            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream);
//            showToast("截图已保存到SD下的test.jpg");
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            if (outStream != null) {
//                try {
//                    outStream.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }


    //Demo层视频缩放和摄像头对焦操作相关方法
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //Log.i(TAG, "test: down!!!");
                //调用摄像头对焦操作相关API
                if (mLSMediaCapture != null) {
                    mLSMediaCapture.setCameraFocus();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                //Log.i(TAG, "test: move!!!");
                /**
                 * 首先判断按下手指的个数是不是大于两个。
                 * 如果大于两个则执行以下操作（即图片的缩放操作）。
                 */
                if (event.getPointerCount() >= 2) {

                    float offsetX = event.getX(0) - event.getX(1);
                    float offsetY = event.getY(0) - event.getY(1);
                    /**
                     * 原点和滑动后点的距离差
                     */
                    mCurrentDistance = (float) Math.sqrt(offsetX * offsetX + offsetY * offsetY);
                    if (mLastDistance < 0) {
                        mLastDistance = mCurrentDistance;
                    } else {
                        if (mLSMediaCapture != null) {
                            mMaxZoomValue = mLSMediaCapture.getCameraMaxZoomValue();
                            mCurrentZoomValue = mLSMediaCapture.getCameraZoomValue();
                        }

                        /**
                         * 如果当前滑动的距离（currentDistance）比最后一次记录的距离（lastDistance）相比大于5英寸（也可以为其他尺寸），
                         * 那么现实图片放大
                         */
                        if (mCurrentDistance - mLastDistance > 5) {
                            //Log.i(TAG, "test: 放大！！！");
                            mCurrentZoomValue += 2;
                            if (mCurrentZoomValue > mMaxZoomValue) {
                                mCurrentZoomValue = mMaxZoomValue;
                            }

                            if (mLSMediaCapture != null) {
                                mLSMediaCapture.setCameraZoomPara(mCurrentZoomValue);
                            }

                            mLastDistance = mCurrentDistance;
                            /**
                             * 如果最后的一次记录的距离（lastDistance）与当前的滑动距离（currentDistance）相比小于5英寸，
                             * 那么图片缩小。
                             */
                        } else if (mLastDistance - mCurrentDistance > 5) {
                            //Log.i(TAG, "test: 缩小！！！");
                            mCurrentZoomValue -= 2;
                            if (mCurrentZoomValue < 0) {
                                mCurrentZoomValue = 0;
                            }
                            if (mLSMediaCapture != null) {
                                mLSMediaCapture.setCameraZoomPara(mCurrentZoomValue);
                            }

                            mLastDistance = mCurrentDistance;
                        }
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                //Log.i(TAG, "test: up!!!");
                if (filterLayout != null) {
                    filterLayout.setVisibility(View.GONE);
                }

                if (configLayout != null) {
                    configLayout.setVisibility(View.GONE);
                }

                break;
            default:
                break;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        //        super.onBackPressed();
        //        m_tryToStopLivestreaming = true;
    }



    //WebScoket链接成功后将ClientId发给服务器
    @Subscriber(mode = ThreadMode.MAIN, tag = GlobalConstants.EventBus.PUT_CLIENT_ID)
    public void getClientId(String client_id) {
        pugClient(client_id);

        LogUtils.show("---开播client---", client_id);
    }

    //聊天消息回调
    @Subscriber(mode = ThreadMode.MAIN, tag = GlobalConstants.EventBus.CHAT_LIVE_ANCHOR)
    public void onMessageEvent(ChatMessage chatMessage) {

        if (chatMessage != null) {
            if (chatMessage.chat_code == 10) {
                tvLiveGold.setText(chatMessage.radio_goldmoney);
            }
            LogUtils.show("---测试消息----", chatMessage.message);
            chatMessages.add(chatMessage);
            messageAdapter.notifyDataSetChanged();
            if (messageAdapter.getCount() > 2) {
                messageListView.setSelection(messageAdapter.getCount() - 1);
            }
        }
    }


    //发送礼物回调
    @Subscriber(mode = ThreadMode.MAIN, tag = GlobalConstants.EventBus.CHAT_LIVE_SEND_GIFT_BACK)
    public void onLiveGiftCallBack(GiftMessageProtocol giftMessageProtocol) {
        if (giftMessageProtocol != null) {
            tvLiveGold.setText(giftMessageProtocol.radio_goldmoney); //主播余额
            GiftProtocol giftProtocol = new GiftProtocol();
            giftProtocol.name = giftMessageProtocol.from_name;
            giftProtocol.giftName = "送出" + giftMessageProtocol.goldmoney + "金币";
            giftProtocol.num = Integer.parseInt(giftMessageProtocol.goldmoney);
            giftProtocol.img = giftMessageProtocol.from_icon;

            if (!gifts.contains(giftProtocol)) {
                gifts.add(giftProtocol);
                gift_item_first.setGift(giftProtocol);
            }
            gift_item_first.addNum(1);
            //radio_goldmoney 主播金币余额


        }
    }


    //网络切换
    @Subscriber(mode = ThreadMode.MAIN, tag = GlobalConstants.EventBus.NETWORK_CHANGE_LISTENER)
    public void onNetWorkCHange(String status) {
        if (!status.equals("fail")) {
            ifNetOpen = true;
            LogUtils.show("开播", "EVENTBUS切换网络" + status);
            //                mLSMediaCapture.restartLiveStream();
        }

    }

    //直播间人数变化
    @Subscriber(mode = ThreadMode.MAIN, tag = GlobalConstants.EventBus.LIVE_HORSE_NUMS_CHANGE)
    public void onLiveNumsChange(String nums) {
        if (nums != null) {
            tvLookerNum.setText(nums);
        }
    }


    private void pugClient(String client) {
        String userId = SPUtils.getString(this, GlobalConstants.userID, "");
        if (TextUtils.isEmpty(userId)) {
            ToastUtil.showShort(this, "获取用户信息失败,请重新登录");
            return;
        }

        OkHttpUtils
                .post()
                .url(GlobalConstants.URL_GET_LIVE_CHAT)
                .addParams("client_id", client) //后台分配设备Id  //   .addParams("to_uid",livePalyerId) //主播Id
                .addParams("from_uid", userId) //用户Id
                .addParams("cid", liveCid)  //直播间Id
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        e.printStackTrace();
                        ToastUtil.showShort(LiveStreamingActivity.this, e.getMessage());

                    }

                    @Override
                    public void onResponse(String response, int id) {

                        //                        toastTip(response);
                    }
                });
    }


}
