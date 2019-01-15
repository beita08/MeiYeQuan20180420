package com.xasfemr.meiyaya.neteasecloud;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.lljjcoder.citylist.Toast.ToastUtils;
import com.netease.neliveplayer.sdk.constant.NEType;
import com.xasfemr.meiyaya.R;
import com.xasfemr.meiyaya.activity.LoginActivity;
import com.xasfemr.meiyaya.adapter.MessageAdapter;
import com.xasfemr.meiyaya.bean.ChatMessage;
import com.xasfemr.meiyaya.global.GlobalConstants;
import com.xasfemr.meiyaya.media.NEMediaController.OnHiddenListener;
import com.xasfemr.meiyaya.media.NEMediaController.OnShownListener;
import com.xasfemr.meiyaya.media.NEVideoView;
import com.xasfemr.meiyaya.module.college.presenter.CollegePresenter;
import com.xasfemr.meiyaya.module.college.view.AttentionIView;
import com.xasfemr.meiyaya.module.live.IView.LiveMsgView;
import com.xasfemr.meiyaya.module.live.LivePresenter;
import com.xasfemr.meiyaya.module.live.protocol.LiveMsgProtocol;
import com.xasfemr.meiyaya.module.mine.protocol.Shareprotocol;
import com.xasfemr.meiyaya.module.player.protocol.GiftMessageProtocol;
import com.xasfemr.meiyaya.module.player.protocol.GiftProtocol;
import com.xasfemr.meiyaya.receiver.NEPhoneCallStateObserver;
import com.xasfemr.meiyaya.receiver.NEScreenStateObserver;
import com.xasfemr.meiyaya.receiver.Observer;
import com.xasfemr.meiyaya.utils.LogUtils;
import com.xasfemr.meiyaya.utils.NetUtils;
import com.xasfemr.meiyaya.utils.SPUtils;
import com.xasfemr.meiyaya.utils.ToastUtil;
import com.xasfemr.meiyaya.view.BasicShareDialog;
import com.xasfemr.meiyaya.view.GoldSendDialog;
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

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;

/**
 * 直播间
 */
public class NELivePlayerActivity extends AppCompatActivity implements OnClickListener, View.OnLayoutChangeListener, View.OnTouchListener {
    public final static String TAG = "NELivePlayerActivity";
    public  NEVideoView       mVideoView;  //用于画面显示
    private View              mBuffer; //用于指示缓冲状态
    private String mVideoPath; //文件路径

    private boolean mEnableBackgroundPlay = true;
    private boolean mBackPressed;

    private NEScreenStateObserver mScreenStateObserver;
    private boolean               isScreenOff;
    private boolean               isBackground;
    /*----------------------美页圈自己定义的控件开始---------------------------*/


    private CircleImageView civUserIcon;
    private TextView        tvUserName;
    private TextView        tvLookerNum;
    private ImageView       ivFollowLec;
    private TextView        tvLiveGold;
    private ImageView       ivLiveClose;
    private ImageView       ivLiveBriefIntro;
    private ImageView       ivLiveShare;
    private ImageView       ivLiveGoldSend;
    private ListView        messageListView;
    private RelativeLayout  giftLayout;
    private LinearLayout    sendMessageLayout;
    private EditText        sendEdit;
    private TextView        tvSendMsg;

    private GiftItemView gift_item_first;
    private ImageView    ivSummary;
    /*----------------------美页圈自己定义的控件结束---------------------------*/

    private ArrayList<ChatMessage> chatMessages;
    private MessageAdapter         messageAdapter;
    private LiveChatClient         liveChatClient;
    private String                 liveCid;
    private RelativeLayout         rootView;
    //屏幕高度
    private int screenHeight = 0;
    //软件盘弹起后所占高度阀值
    private int keyHeight    = 0;

    private SFProgressDialog sfProgressDialog;

    private ArrayList<GiftProtocol> gifts;

    private String livePalyerId = "";//主播ID
    private GoldSendDialog goldSendDialog;
    private String userIcon = "";
    private String userName = "";

    private LivePresenter livePresenter;
    private CollegePresenter collegePresenter;
    private boolean follew = false; //是否关注
    private SFProgressDialog progressDialog;

    private LinearLayout live_layout;

    private String shareSummary = ""; //直播简介
    private String shareTitle   = "";
    private String shareImage   = "";
    private String shareUrl     = "";
    private RelativeLayout layout_content; //隐藏聊天等

    private GestureDetector mGestureDetector; //监听屏幕左右滑动
    private static final int FLING_MIN_DISTANCE = 50;   //最小距离
    private static final int FLING_MIN_VELOCITY = 0;  //最小速度
    GestureDetector.SimpleOnGestureListener gestureListener = new GestureDetector.SimpleOnGestureListener() {
        /**
         * @param e1 表示手势起点的移动事件 可以得到移动的起始位置的坐标
         * @param e1 当前手势点的移动事件 可以得到移动结束时的位置坐标
         * @param velocityX 每秒x轴方向移动的像素
         * @param velocityY 每秒y轴方向移动的像素
         */
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            LogUtils.show("<--滑动测试-->", "开始滑动");
            float x = e1.getX() - e2.getX();
            float x2 = e2.getX() - e1.getX();
            if (x > FLING_MIN_DISTANCE && Math.abs(velocityX) > FLING_MIN_VELOCITY) {
                //                ToastUtil.showShort(NELivePlayerActivity.this,"向左滑动");
                if (layout_content != null) {

                    layout_content.setVisibility(View.VISIBLE);
                }


            } else if (x2 > FLING_MIN_DISTANCE && Math.abs(velocityX) > FLING_MIN_VELOCITY) {
                //                ToastUtil.showShort(NELivePlayerActivity.this,"向右滑动");
                if (layout_content != null) {
                    layout_content.setVisibility(View.GONE);
                }

            }

            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ne_live_player);
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(Color.BLACK);
        }

        EventBus.getDefault().register(this);
        collegePresenter = new CollegePresenter();
        livePresenter =new LivePresenter();
        progressDialog = new SFProgressDialog(this);
        NEPhoneCallStateObserver.getInstance().observeLocalPhoneObserver(localPhoneObserver, true);
        mScreenStateObserver = new NEScreenStateObserver(this);
        mScreenStateObserver.observeScreenStateObserver(screenStateObserver, true);

        mGestureDetector = new GestureDetector(this, gestureListener);

        //获取屏幕高度
        screenHeight = getWindowManager().getDefaultDisplay().getHeight();
        //阀值设置为屏幕高度的1/3
        keyHeight = screenHeight / 3;


        mVideoPath = getIntent().getStringExtra("videoPath");
        liveCid = getIntent().getStringExtra("cid");//直播间ID
        livePalyerId = getIntent().getStringExtra("user_id"); //主播ID
        userIcon = getIntent().getStringExtra("icon");
        userName = getIntent().getStringExtra("user_name");
        LogUtils.show("直播间Id", liveCid + "");


        Intent intent = getIntent();
        String intentAction = intent.getAction();
        if (!TextUtils.isEmpty(intentAction) && intentAction.equals(Intent.ACTION_VIEW)) {
            mVideoPath = intent.getDataString();
            Log.i(TAG, "videoPath = " + mVideoPath);
        }

        //lxb:初始化美页圈自己定义的控件
        initMyyView();


        mBuffer = findViewById(R.id.buffering_prompt);
        mVideoView = (NEVideoView) findViewById(R.id.video_view);
        mVideoView.setBufferStrategy(NEType.NELPLOWDELAY); //直播低延时
        mVideoView.setBufferingIndicator(mBuffer);
        mVideoView.setMediaType("livestream");
        mVideoView.setHardwareDecoder(true); //是否开启硬件加速
        mVideoView.setEnableBackgroundPlay(mEnableBackgroundPlay);
        mVideoView.setVideoPath(mVideoPath);

        mVideoView.setVideoScalingMode(3);

        mVideoView.requestFocus();
        mVideoView.start();


        getLiveData();

    }

    private void getLiveData() {
        HashMap<String,String>map =new HashMap<>();
        map.put("cid",liveCid);

        livePresenter.getLiveMessage(map, new LiveMsgView() {
            @Override
            public void getLiveMsgSuccess(LiveMsgProtocol liveMsgProtocol) {

                if (liveMsgProtocol!=null){
                    if (liveMsgProtocol.status==1){
                        getShareData();
                        getData();

                    }else {
                        SFDialog.onlyConfirmDialog(NELivePlayerActivity.this, "提示", "当前课程已经结束！", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                finish();
                            }
                        });

                    }
                }else {
                    ToastUtil.showShort(NELivePlayerActivity.this,"获取课程信息错误");
                }

            }

            @Override
            public void getLiveMsgOnfaile(String msg) {
                ToastUtil.showShort(NELivePlayerActivity.this,"获取课程信息错误");
            }

            @Override
            public void onNetworkFailure(String message) {
                ToastUtil.showShort(NELivePlayerActivity.this,message);
            }
        });
    }

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
                            JSONObject js = new JSONObject(jsonObject.getString("data"));
                            if (js != null) {
                                shareImage = js.getString("cover");
                                shareUrl = js.getString("shareUrl");
                                shareSummary = js.getString("des");
                                shareTitle = js.getString("title");
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });

    }

    private void getData() {
        //是否存在关注状态
        HashMap map = new HashMap();
        map.put("userid", SPUtils.getString(this, GlobalConstants.userID, ""));
        map.put("uid", livePalyerId);
        collegePresenter.ifAttention(map, new AttentionIView() {
            @Override
            public void getAttentionSuccess(String message) {
                if (message.equals("已关注")) {
                    follew = true;
                    ivFollowLec.setImageDrawable(getResources().getDrawable(R.drawable.live_follow1_selected));
                } else {
                    follew = false;
                    ivFollowLec.setImageDrawable(getResources().getDrawable(R.drawable.live_follow1));
                }
            }

            @Override
            public void getAttentionOnFailure(String message) {
                ToastUtil.showShort(NELivePlayerActivity.this, message);
            }

            @Override
            public void onNetworkFailure(String message) {
                ToastUtil.showShort(NELivePlayerActivity.this, message);

            }
        });
    }


    private void initMyyView() {
        rootView = (RelativeLayout) findViewById(R.id.root_View);
        rootView.addOnLayoutChangeListener(this);
        rootView.setOnTouchListener(this);
        rootView.setLongClickable(true);
        //        rootView.setVisibility(View.GONE);
        layout_content = (RelativeLayout) findViewById(R.id.layout_content);
        //用户头像
        civUserIcon = (CircleImageView) findViewById(R.id.civ_live_user_icon);
        Glide.with(this).load(userIcon).apply(new RequestOptions().placeholder(R.drawable.meiyaya_logo_round_gray)).into(civUserIcon);
        //用户名
        tvUserName = (TextView) findViewById(R.id.tv_live_user_name);
        tvUserName.setText(userName);
        //直播间人数
        tvLookerNum = (TextView) findViewById(R.id.tv_live_looker_num);
        //关注讲师
        ivFollowLec = (ImageView) findViewById(R.id.iv_live_follow_lec);
        //直播间金币数量
        tvLiveGold = (TextView) findViewById(R.id.tv_live_home_gold);
        //关闭直播
        ivLiveClose = (ImageView) findViewById(R.id.iv_live_close);
        //发送消息
        ivLiveBriefIntro = (ImageView) findViewById(R.id.iv_live_room_brief_introduction);
        //直播分享
        ivLiveShare = (ImageView) findViewById(R.id.iv_live_room_share);
        //直播打赏金币
        ivLiveGoldSend = (ImageView) findViewById(R.id.iv_live_room_gold_send);
        //聊天列表
        messageListView = (ListView) findViewById(R.id.list_message);

        //礼物设置布局
        giftLayout = (RelativeLayout) findViewById(R.id.gift_layout);
        //发送消息布局
        sendMessageLayout = (LinearLayout) findViewById(R.id.layout_send_message);


        //直播简介
        ivSummary = (ImageView) findViewById(R.id.ivSummary);

        //聊天输入框
        sendEdit = (EditText) findViewById(R.id.send_edit);

        //发送消息
        tvSendMsg = (TextView) findViewById(R.id.tv_send_msg);
        //
        live_layout = (LinearLayout) findViewById(R.id.live_layout);

        //礼物布局
        gift_item_first = (GiftItemView) findViewById(R.id.gift_item_first);

        gifts = new ArrayList<>();

        //开启聊天
        URI uri = URI.create(GlobalConstants.SOCKET_URL);
        liveChatClient = new LiveChatClient(this, uri);
        liveChatClient.connect();
        timingHandler.postDelayed(timingRunnable, TIME);

        chatMessages = new ArrayList<>();
        messageAdapter = new MessageAdapter(this);
        messageListView.setAdapter(messageAdapter);
        messageAdapter.setDatas(chatMessages);


        civUserIcon.setOnClickListener(this);
        ivFollowLec.setOnClickListener(this);
        ivLiveClose.setOnClickListener(this);
        ivLiveBriefIntro.setOnClickListener(this);
        ivLiveShare.setOnClickListener(this);
        ivLiveGoldSend.setOnClickListener(this);
        tvSendMsg.setOnClickListener(this);
        ivSummary.setOnClickListener(this);

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
                    //liveChatClient.sendPing();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    Observer<Integer> localPhoneObserver = new Observer<Integer>() {
        @Override
        public void onEvent(Integer phoneState) {
            if (phoneState == TelephonyManager.CALL_STATE_IDLE) {
                mVideoView.restorePlayWithCall();
            } else if (phoneState == TelephonyManager.CALL_STATE_RINGING) {
                mVideoView.stopPlayWithCall();
            } else {
                LogUtils.show(TAG, "localPhoneObserver onEvent " + phoneState);
            }
        }
    };

    Observer<NEScreenStateObserver.ScreenStateEnum> screenStateObserver = new Observer<NEScreenStateObserver.ScreenStateEnum>() {
        @Override
        public void onEvent(NEScreenStateObserver.ScreenStateEnum screenState) {
            if (screenState == NEScreenStateObserver.ScreenStateEnum.SCREEN_ON) {
                LogUtils.show(TAG, "onScreenOn ");
                if (isScreenOff) {
                    mVideoView.restorePlayWithForeground();
                }
                isScreenOff = false;
            } else if (screenState == NEScreenStateObserver.ScreenStateEnum.SCREEN_OFF) {
                LogUtils.show(TAG, "onScreenOff ");
                isScreenOff = true;
                if (!isBackground) {
                    mVideoView.stopPlayWithBackground();
                }
            } else {
                LogUtils.show(TAG, "onUserPresent ");
                //isScreenOff = false;
            }

        }
    };

    //    OnClickListener mOnClickEvent = new OnClickListener() {
    //        @Override
    //        public void onClick(View v) {
    //            if (v.getId() == R.id.player_exit) {
    //                Log.i(TAG, "player_exit");
    //                mBackPressed = true;
    //                finish();
    //            }
    //        }
    //    };

    OnShownListener mOnShowListener = new OnShownListener() {

        @Override
        public void onShown() {
           /* mPlayToolbar.setVisibility(View.VISIBLE);
            mPlayToolbar.requestLayout();
            mVideoView.invalidate();
            mPlayToolbar.postInvalidate();*/
        }
    };

    OnHiddenListener mOnHiddenListener = new OnHiddenListener() {

        @Override
        public void onHidden() {
            //mPlayToolbar.setVisibility(View.INVISIBLE);
        }
    };


    @Override
    public void onBackPressed() {
        Log.i(TAG, "onBackPressed");
        mBackPressed = true;
        //        finish();
        finishHourse();

    }


    @Override
    protected void onPause() {
        Log.i(TAG, "NEVideoPlayerActivity onPause");

        super.onPause();
    }

    @Subscriber(mode = ThreadMode.MAIN, tag = GlobalConstants.EventBus.LIVE_COMPLETED_OR_ERROR)
    public void onPlayerCompleted(String status) {
        if (status.equals("completed")) {  //播放完成
            SFDialog.onlyConfirmDialog(this, "提示", "在线课程已结束，点击确认退出", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mVideoView.destroy();
                    finish();
                }
            });

        } else {//播放错误

            SFDialog.onlyConfirmDialog(this, "提示", "网络出现错误，请退出重试", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mVideoView.destroy();
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
        livePresenter.destroy();
        liveChatClient.close();
        timingHandler.removeCallbacks(timingRunnable);


    }

    @Override
    protected void onStart() {
        super.onStart();
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

        if (goldSendDialog != null && goldSendDialog.isShowing()) {
            goldSendDialog.dismiss();
        }
    }


    @Override
    protected void onRestart() {
        super.onRestart();
    }

    /*----------------------美页圈自己的点击事件在这里处理---开始-------------------------*/
    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            //            case R.id.civ_live_user_icon://讲师头像
            //                Intent intent = new Intent(NELivePlayerActivity.this, LoginActivity.class);
            //                startActivity(intent);
            //                break;
            case R.id.iv_live_follow_lec://关注讲师
                if (follew) {
                    SFDialog.basicDialog(this, "提示", "确定取消关注吗？", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            cancelAttention(livePalyerId);
                        }
                    });

                } else {
                    getAttention();
                }
                break;
            case R.id.iv_live_close:     //关闭直播
                Log.i(TAG, "player_exit");
                mBackPressed = true;
                finishHourse();
                break;
            case R.id.iv_live_room_brief_introduction://发送消息布局
                //                showBriefIntroPopWindow();

                sendEdit.requestFocus();
                InputMethodManager inputManager =
                        (InputMethodManager) sendEdit.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.showSoftInput(sendEdit, 0);
                giftLayout.setVisibility(View.GONE);
                sendMessageLayout.setVisibility(View.VISIBLE);
                break;

            case R.id.tv_send_msg: //发送消息
                String userId = SPUtils.getString(this, GlobalConstants.userID, "");
                if (TextUtils.isEmpty(userId)) {
                    SFDialog.basicDialog(this, "提示", "请先登陆", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startActivity(new Intent(NELivePlayerActivity.this, LoginActivity.class));
                            finish();
                        }
                    });
                } else {
                    sendMessage(sendEdit.getText().toString().trim(), userId);
                }


                break;
            case R.id.iv_live_room_share:       //直播分享
                //                ShareDialog shareDialog = new ShareDialog(NELivePlayerActivity.this);
                //                shareDialog.show();
                Shareprotocol shareprotocol = new Shareprotocol();
                shareprotocol.shareMsg = shareSummary;
                shareprotocol.shareImage = shareImage;
                shareprotocol.shareUrl = shareUrl;
                shareprotocol.sharetitle = shareTitle;
                shareprotocol.shareStatus="2";
                shareprotocol.shareCid=liveCid;
                BasicShareDialog shareDialog = new BasicShareDialog(this, shareprotocol);
                shareDialog.show();

                break;
            case R.id.iv_live_room_gold_send:   //直播打赏金币
                if (goldSendDialog == null) {
                    goldSendDialog = new GoldSendDialog(NELivePlayerActivity.this);
                    goldSendDialog.show();
                } else {
                    goldSendDialog.show();
                }

                break;

            case R.id.ivSummary: //直播简介

                SummaryDialog summaryDialog = new SummaryDialog(this, shareSummary);
                summaryDialog.show();
                break;


            default:
                break;
        }
    }

    /**
     * 发起关注
     */
    private void getAttention() {
        if (TextUtils.isEmpty(SPUtils.getString(this, GlobalConstants.userID, ""))) {
            ToastUtil.showShort(this, "您未登陆，请先去登陆");
            return;
        }

        if (SPUtils.getString(this, GlobalConstants.userID, "").equals(livePalyerId)) {
            ToastUtil.showShort(this, "自己不能关注自己");
            return;
        }


        progressDialog.show();
        HashMap map = new HashMap();
        map.put("userid", SPUtils.getString(this, GlobalConstants.userID, ""));
        map.put("uid", livePalyerId);

        collegePresenter.getAttention(map, new AttentionIView() {
            @Override
            public void getAttentionSuccess(String message) {
                progressDialog.dismiss();
                ToastUtil.showShort(NELivePlayerActivity.this, message);
                ivFollowLec.setImageDrawable(getResources().getDrawable(R.drawable.live_follow1_selected));
                follew = true;

            }

            @Override
            public void getAttentionOnFailure(String message) {
                progressDialog.dismiss();
                ToastUtil.showShort(NELivePlayerActivity.this, message);
            }

            @Override
            public void onNetworkFailure(String message) {
                progressDialog.dismiss();
                ToastUtil.showShort(NELivePlayerActivity.this, message);
            }
        });

    }

    /**
     * 取消关注
     * @param userId
     */
    private void cancelAttention(String userId) {
        if (progressDialog != null) {
            progressDialog.show();
        }

        HashMap map = new HashMap();
        map.put("userid", SPUtils.getString(this, GlobalConstants.userID, ""));
        map.put("uid", userId);


        collegePresenter.cancelAttention(map, new AttentionIView() {
            @Override
            public void getAttentionSuccess(String message) {
                progressDialog.dismiss();
                ToastUtil.showShort(NELivePlayerActivity.this, message);
                ivFollowLec.setImageDrawable(getResources().getDrawable(R.drawable.live_follow1));
                follew = false;

            }

            @Override
            public void getAttentionOnFailure(String message) {
                if (progressDialog != null) {
                    progressDialog.dismiss();
                }
                ToastUtil.showShort(NELivePlayerActivity.this, message);
            }

            @Override
            public void onNetworkFailure(String message) {
                if (progressDialog != null) {
                    progressDialog.dismiss();
                }
                ToastUtil.showShort(NELivePlayerActivity.this, message);
            }
        });
    }


    /**
     * 发送消息
     */
    private void sendMessage(String msg, String user_id) {
        if (msg.equals("")) {
            toastTip("不可发送空消息");
            return;
        }
        if (sfProgressDialog == null) {
            sfProgressDialog = new SFProgressDialog(this);
        }
        sfProgressDialog.show();

        OkHttpUtils
                .post()
                .url(GlobalConstants.URL_SEND_LIVE_MSG)
                .addParams("msg", msg)
                .addParams("from_uid", user_id)
                .addParams("cid", liveCid)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        e.printStackTrace();
                        if (sfProgressDialog != null) {
                            sfProgressDialog.dismiss();
                        }
                        toastTip("发送消息失败，请稍后再发");
                        //                        if (e instanceof SocketTimeoutException){
                        //                            toastTip("发送消息失败，请稍后再发");
                        //                        }else {
                        //                            toastTip(e.toString());
                        //                        }


                    }

                    @Override
                    public void onResponse(String response, int id) {
                        if (sfProgressDialog != null) {
                            sfProgressDialog.dismiss();
                        }
                        LogUtils.show("发送消息---" +
                                "", response);
                        sendEdit.setText("");
                        //                        toastTip(response);
                    }
                });


    }


    /*----------------------美页圈自己的点击事件在这里处理---结束-------------------------*/
    // TODO: 2017/11/8 0008 处理点击事件,比较网易原生界面和MeiYaYa界面,综合一下;

    //    //显示简介的PopupWindow
    //    private void showBriefIntroPopWindow() {
    //
    //        if (popupWindow == null) {
    //            View view = View.inflate(NELivePlayerActivity.this, R.layout.layout_live_brief_intro, null);
    //            EditText etLiveIntro = (EditText) view.findViewById(R.id.et_live_course_introduce);
    //
    //            popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
    //            popupWindow.setBackgroundDrawable(new ColorDrawable());
    //        }
    //        popupWindow.showAsDropDown(ivLiveBriefIntro, 0, -600);
    //        //.showAtLocation(rlRoot, Gravity.CENTER, 0, 0);
    //    }

    private void toastTip(final String str) {
        ToastUtil.showShort(this, str);
    }


    /**
     * 关闭直播间时调用
     * @param
     */
    private void finishHourse() {

        if (!NetUtils.isConnected(this)) {
            toastTip("无网络链接");
            return;
        }

        if (sfProgressDialog == null) {
            sfProgressDialog = new SFProgressDialog(this);
        }
        sfProgressDialog.show();

        String userId = SPUtils.getString(this, GlobalConstants.userID, "");
        if (TextUtils.isEmpty(userId)) {
            toastTip("获取用户信息失败,请重新登录");
            return;
        }

        OkHttpUtils
                .post()
                .url(GlobalConstants.URL_CLOSE_LIVE_CHAT)
                .addParams("client_id", SPUtils.getString(this, GlobalConstants.CLIENT_ID, ""))
                .addParams("from_uid", userId)
                .addParams("cid", liveCid)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        e.printStackTrace();
                        toastTip("网络异常，请稍后再试");
                        sfProgressDialog.dismiss();

                    }

                    @Override
                    public void onResponse(String response, int id) {
                        sfProgressDialog.dismiss();
                        finish();
                    }
                });


    }


    @Override
    public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
        //只要控件将Activity向上推的高度超过了1/3屏幕高，就认为软键盘弹起

        if (oldBottom != 0 && bottom != 0 && (oldBottom - bottom > keyHeight)) {
            sendMessageLayout.setVisibility(View.VISIBLE);
            giftLayout.setVisibility(View.GONE);
            LogUtils.show("直播页面", "软键盘弹起");
            //            Toast.makeText(MainActivity.getActivity(), "监听到软键盘弹起...", Toast.LENGTH_SHORT).show();
        } else if (oldBottom != 0 && bottom != 0 && (bottom - oldBottom > keyHeight)) {
            sendMessageLayout.setVisibility(View.GONE);
            giftLayout.setVisibility(View.VISIBLE);
            LogUtils.show("直播页面", "软件盘关闭");
            //            Toast.makeText(MainActivity.getActivity(), "监听到软件盘关闭...", Toast.LENGTH_SHORT).show();
        }
    }


    //WebScoket链接成功后将ClientId发给服务器
    @Subscriber(mode = ThreadMode.MAIN, tag = GlobalConstants.EventBus.PUT_CLIENT_ID)
    public void getClientId(String client_id) {
        pugClient(client_id);
    }

    private void pugClient(String client) {
        String userId = SPUtils.getString(this, GlobalConstants.userID, "");
        if (TextUtils.isEmpty(userId)) {
            toastTip("获取用户信息失败,请重新登录");
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
                        toastTip("获取在线课程信息异常");
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        //toastTip(response);
                        //TODO Do something in here

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONObject js = new JSONObject(jsonObject.getString("data"));
                            if (js != null) {
                                if (js.has("from_goldmoney")) {
                                    SPUtils.putString(NELivePlayerActivity.this, GlobalConstants.USER_GOLD_NUMBER, js.getString("from_goldmoney")); //将用户余额保存至本地

                                } else {
                                    ToastUtil.showShort(NELivePlayerActivity.this, "获取用户余额失败，请重新进入");
                                }
                            } else {
                                ToastUtil.showShort(NELivePlayerActivity.this, "获取用户余额失败，请重新进入");
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });
    }


    //聊天消息回调
    @Subscriber(mode = ThreadMode.MAIN, tag = GlobalConstants.EventBus.CHAT_LIVE_ANCHOR)
    public void onMessageEvent(ChatMessage chatMessage) {

        if (chatMessage != null) {
            if (chatMessage.chat_code == 10) {
                tvLiveGold.setText(chatMessage.radio_goldmoney);
            }
            chatMessages.add(chatMessage);
            messageAdapter.notifyDataSetChanged();
            if (messageAdapter.getCount() > 2) {
                messageListView.setSelection(messageAdapter.getCount() - 1);
            }


        }

    }

    //直播结束：
    @Subscriber(mode = ThreadMode.MAIN, tag = GlobalConstants.EventBus.LIVE_END)
    public void onLiveEnd(int status) {
        if (status == 0) {
            SFDialog.onlyConfirmDialog(this, "提示", "在线课程已结束，点击确认退出", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
        }
    }


    //直播间人数变化
    @Subscriber(mode = ThreadMode.MAIN, tag = GlobalConstants.EventBus.LIVE_HORSE_NUMS_CHANGE)
    public void onLiveNumsChange(String nums) {
        if (nums != null) {
            tvLookerNum.setText(nums);
        }
    }


    //发送礼物回调
    @Subscriber(mode = ThreadMode.MAIN, tag = GlobalConstants.EventBus.CHAT_LIVE_SEND_GIFT_BACK)
    public void onLiveGiftCallBack(GiftMessageProtocol giftMessageProtocol) {
        if (giftMessageProtocol != null) {
            //            SPUtils.putString(this,GlobalConstants.USER_GOLD,giftMessageProtocol.from_goldmoney); //将用户余额保存至本地
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

            if (goldSendDialog != null) {
                LogUtils.show("用户剩余金币数量", giftMessageProtocol.from_goldmoney);

                if (giftMessageProtocol.from_uid.equals(SPUtils.getString(this, GlobalConstants.userID, ""))) {
                    goldSendDialog.refrshGold(giftMessageProtocol.from_goldmoney);
                }


            }


        }
    }


    //视频第一帧显示
    @Subscriber(mode = ThreadMode.MAIN, tag = GlobalConstants.EventBus.LIVE_GOLD_INIT)
    public void onLiveOneStart(String nums) {

        LogUtils.show("开始喽---", nums);
        if (nums.equals("0")) {
            live_layout.setVisibility(View.GONE);

            //            rootView.setVisibility(View.GONE);
        }


    }

    //发送礼物
    @Subscriber(mode = ThreadMode.MAIN, tag = GlobalConstants.EventBus.CHAT_LIVE_SEND_GIFT)
    public void onLiveEvent(String nums) {
        LogUtils.show("送礼物数量", nums);
        putGiftInfo(nums);
    }

    private void putGiftInfo(String nums) {
        if (sfProgressDialog == null) {
            sfProgressDialog = new SFProgressDialog(this);
        }

        sfProgressDialog.show();
        String userId = SPUtils.getString(this, GlobalConstants.userID, "");
        if (TextUtils.isEmpty(userId)) {
            toastTip("获取用户信息失败,请重新登录");
            return;
        }

        OkHttpUtils
                .post()
                .url(GlobalConstants.LIVE_CHAT_SEND_GIFT)
                .addParams("goldmoney", nums)
                .addParams("from_uid", userId)
                .addParams("to_uid", livePalyerId)
                .addParams("cid", liveCid)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        e.printStackTrace();
                        toastTip("发送礼物异常");
                        LogUtils.show("发送礼物异常", e.toString());
                        if (sfProgressDialog != null) {
                            sfProgressDialog.dismiss();
                        }


                    }

                    @Override
                    public void onResponse(String response, int id) {
                        if (sfProgressDialog != null) {
                            sfProgressDialog.dismiss();
                        }
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(response);
                            int code = jsonObject.getInt("data");
                            if (code == 500) {  //codewei 500  金币余额不足
                                ToastUtils.showShortToast(NELivePlayerActivity.this, jsonObject.getString("message"));
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                        //                        toastTip(response);
                    }
                });
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return mGestureDetector.onTouchEvent(event);
    }


//    @Subscriber(mode = ThreadMode.MAIN,tag = GlobalConstants.EventBus.SHARE_LIVE)
//    public void getShareStatus(String status){
//        LogUtils.show("分享回调",status);
//
//        if (sharePresenter!=null){
//
//            HashMap<String,String >map= new HashMap<>();
//            map.put("uid",SPUtils.getString(this,GlobalConstants.userID,""));
//            map.put("information_id",urlId);
//            sharePresenter.getNewsShare(map, new ShareSuccessIView() {
//                @Override
//                public void getShareSuccess(String message) {
//                    ToastUtil.showShort(NELivePlayerActivity.this,message);
//                }
//
//                @Override
//                public void getShareOnfaile(String msg) {
//                    ToastUtil.showShort(WebViewActivity.this,msg);
//                }
//
//                @Override
//                public void onNetworkFailure(String message) {
//                    ToastUtil.showShort(WebViewActivity.this,message);
//                }
//            });
//        }
//    }
}