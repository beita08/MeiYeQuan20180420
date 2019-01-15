package com.xasfemr.meiyaya.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.google.gson.Gson;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.xasfemr.meiyaya.R;
import com.xasfemr.meiyaya.bean.GoldNumberData;
import com.xasfemr.meiyaya.bean.OrderInfoData;
import com.xasfemr.meiyaya.bean.WechatPrepayInfo;
import com.xasfemr.meiyaya.global.GlobalConstants;
import com.xasfemr.meiyaya.main.BaseActivity;
import com.xasfemr.meiyaya.pay.AuthResult;
import com.xasfemr.meiyaya.pay.PayResult;
import com.xasfemr.meiyaya.utils.LogUtils;
import com.xasfemr.meiyaya.utils.SPUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.Map;

import okhttp3.Call;

public class MyGoldRechargeActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "MyGoldRechargeActivity";

    private static final int PAYMENT_WECHAT_PAY = 0; //支付类型-微信
    private static final int PAYMENT_ALI_PAY    = 1;    //支付类型-支付宝

    private int PAYMENT       = PAYMENT_WECHAT_PAY;//默认使用微信支付
    private int rechargeMoney = 68; //默认充值68元=680金币

    private static final int SDK_PAY_FLAG  = 1;
    private static final int SDK_AUTH_FLAG = 2;

    private TextView       tvUserName;
    private TextView       tvMyGlod;
    private TextView       tvGoldNum10;
    private TextView       tvGoldNum30;
    private TextView       tvGoldNum50;
    private TextView       tvGoldNum100;
    private TextView       tvGoldNum200;
    private TextView       tvGoldNum500;
    private RelativeLayout rlGoldWechatpay;
    private ImageView      ivGoldWechatpay;
    private RelativeLayout rlGoldAlipay;
    private ImageView      ivGoldAlipay;
    private TextView       tvGoldRecharge;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @SuppressWarnings("unused")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    @SuppressWarnings("unchecked")
                    PayResult payResult = new PayResult((Map<String, String>) msg.obj);
                    /**
                     对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
                     */
                    String resultInfo = payResult.getResult();// 同步返回需要验证的信息
                    String resultStatus = payResult.getResultStatus();
                    // 判断resultStatus 为9000则代表支付成功
                    if (TextUtils.equals(resultStatus, "9000")) {
                        // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
                        Toast.makeText(MyGoldRechargeActivity.this, "支付成功", Toast.LENGTH_SHORT).show();
                    } else {
                        // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
                        Toast.makeText(MyGoldRechargeActivity.this, "支付失败", Toast.LENGTH_SHORT).show();
                    }
                    tvGoldRecharge.setEnabled(true);
                    break;
                }
                case SDK_AUTH_FLAG: {
                    @SuppressWarnings("unchecked")
                    AuthResult authResult = new AuthResult((Map<String, String>) msg.obj, true);
                    String resultStatus = authResult.getResultStatus();

                    // 判断resultStatus 为“9000”且result_code
                    // 为“200”则代表授权成功，具体状态码代表含义可参考授权接口文档
                    if (TextUtils.equals(resultStatus, "9000") && TextUtils.equals(authResult.getResultCode(), "200")) {
                        // 获取alipay_open_id，调支付时作为参数extern_token 的value
                        // 传入，则支付账户为该授权账户
                        Toast.makeText(MyGoldRechargeActivity.this, "授权成功\n" + String.format("authCode:%s", authResult.getAuthCode()), Toast.LENGTH_SHORT).show();
                    } else {
                        // 其他状态值则为授权失败
                        Toast.makeText(MyGoldRechargeActivity.this, "授权失败" + String.format("authCode:%s", authResult.getAuthCode()), Toast.LENGTH_SHORT).show();
                    }
                    break;
                }
                default:
                    break;
            }
        }

        ;
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_gold_recharge);
        initTopBar();
        setTopTitleText("充值");

        tvUserName = (TextView) findViewById(R.id.tv_user_name);
        tvMyGlod = (TextView) findViewById(R.id.tv_my_glod);
        tvGoldNum10 = (TextView) findViewById(R.id.tv_gold_recharge_num10);
        tvGoldNum30 = (TextView) findViewById(R.id.tv_gold_recharge_num30);
        tvGoldNum50 = (TextView) findViewById(R.id.tv_gold_recharge_num50);
        tvGoldNum100 = (TextView) findViewById(R.id.tv_gold_recharge_num100);
        tvGoldNum200 = (TextView) findViewById(R.id.tv_gold_recharge_num200);
        tvGoldNum500 = (TextView) findViewById(R.id.tv_gold_recharge_num500);
        rlGoldWechatpay = (RelativeLayout) findViewById(R.id.rl_gold_payment_wechatpay);
        ivGoldWechatpay = (ImageView) findViewById(R.id.iv_gold_wechatpay);
        rlGoldAlipay = (RelativeLayout) findViewById(R.id.rl_gold_payment_alipay);
        ivGoldAlipay = (ImageView) findViewById(R.id.iv_gold_alipay);
        tvGoldRecharge = (TextView) findViewById(R.id.tv_gold_recharge);

        tvGoldNum10.setOnClickListener(this);
        tvGoldNum30.setOnClickListener(this);
        tvGoldNum50.setOnClickListener(this);
        tvGoldNum100.setOnClickListener(this);
        tvGoldNum200.setOnClickListener(this);
        tvGoldNum500.setOnClickListener(this);
        rlGoldWechatpay.setOnClickListener(this);
        rlGoldAlipay.setOnClickListener(this);
        tvGoldRecharge.setOnClickListener(this);

        String UserName = SPUtils.getString(this, GlobalConstants.USER_NAME, "");
        tvUserName.setText(UserName);

        //初始化,默认选中500
        tvGoldNum500.setSelected(true);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.tv_gold_recharge_num10:
                clearSelected();
                tvGoldNum10.setSelected(true);
                tvGoldRecharge.setText("立即支付  1元");
                rechargeMoney = 1;
                break;
            case R.id.tv_gold_recharge_num30:
                clearSelected();
                tvGoldNum30.setSelected(true);
                tvGoldRecharge.setText("立即支付  3元");
                rechargeMoney = 3;
                break;
            case R.id.tv_gold_recharge_num50:
                clearSelected();
                tvGoldNum50.setSelected(true);
                tvGoldRecharge.setText("立即支付  6元");
                rechargeMoney = 6;
                break;
            case R.id.tv_gold_recharge_num100:
                clearSelected();
                tvGoldNum100.setSelected(true);
                tvGoldRecharge.setText("立即支付  12元");
                rechargeMoney = 12;
                break;
            case R.id.tv_gold_recharge_num200:
                clearSelected();
                tvGoldNum200.setSelected(true);
                tvGoldRecharge.setText("立即支付  30元");
                rechargeMoney = 30;
                break;
            case R.id.tv_gold_recharge_num500:
                clearSelected();
                tvGoldNum500.setSelected(true);
                tvGoldRecharge.setText("立即支付  68元");
                rechargeMoney = 68;
                break;
            case R.id.rl_gold_payment_wechatpay: //微信支付
                ivGoldWechatpay.setImageResource(R.drawable.gold_pay_selected);
                ivGoldAlipay.setImageResource(R.drawable.gold_pay_not_selected);

                PAYMENT = PAYMENT_WECHAT_PAY;

                break;
            case R.id.rl_gold_payment_alipay:  //支付宝支付
                ivGoldWechatpay.setImageResource(R.drawable.gold_pay_not_selected);
                ivGoldAlipay.setImageResource(R.drawable.gold_pay_selected);

                PAYMENT = PAYMENT_ALI_PAY;
                break;
            case R.id.tv_gold_recharge:  //立即支付

                String userId = SPUtils.getString(MyGoldRechargeActivity.this, GlobalConstants.userID, "");

                if (PAYMENT == PAYMENT_WECHAT_PAY) { //微信支付

                    if (TextUtils.isEmpty(userId)) {
                        Toast.makeText(this, "获取用户信息失败,请重新登录", Toast.LENGTH_SHORT).show();
                    } else {
                        //获取微信预支付信息
                        getWechatPrepayInfo(userId);
                        tvGoldRecharge.setEnabled(false);
                    }

                } else if (PAYMENT == PAYMENT_ALI_PAY) {//支付宝支付

                    if (TextUtils.isEmpty(userId)) {
                        Toast.makeText(this, "获取用户信息失败,请重新登录", Toast.LENGTH_SHORT).show();
                    } else {
                        //获取支付宝订单信息OrderInfo
                        getAlipayOrderInfo(userId);
                        tvGoldRecharge.setEnabled(false);
                    }

                } else {
                    Toast.makeText(this, "请选择支付方式", Toast.LENGTH_SHORT).show();
                }

                break;
            default:
                break;

        }
    }

    private void clearSelected() {
        tvGoldNum10.setSelected(false);
        tvGoldNum30.setSelected(false);
        tvGoldNum50.setSelected(false);
        tvGoldNum100.setSelected(false);
        tvGoldNum200.setSelected(false);
        tvGoldNum500.setSelected(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        gotoGetGoldNumber();
    }

    private void gotoGetGoldNumber() {
        String mUserId = SPUtils.getString(this, GlobalConstants.userID, "");
        OkHttpUtils.get().url(GlobalConstants.URL_MY_GOLD_NUMBER)
                .addParams("userid", mUserId)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        LogUtils.show(TAG, "---获取金币数量失败---");
                        Toast.makeText(MyGoldRechargeActivity.this, "网络异常,获取金币数量失败", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        LogUtils.show(TAG, "---获取金币数量成功---response = " + response + " ---");
                        try {
                            parserGoldNumberJson(response);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    private void parserGoldNumberJson(String response) {
        Gson gson = new Gson();
        GoldNumberData goldNumberData = gson.fromJson(response, GoldNumberData.class);
        tvMyGlod.setText(goldNumberData.data.goldmoney + "金币");
        SPUtils.putInt(this, GlobalConstants.USER_GOLD_NUMBER, Integer.parseInt(goldNumberData.data.goldmoney));
    }

    /*------------------------------------微信支付开始--------------------------------------------*/
    //获取微信预支付信息
    private void getWechatPrepayInfo(String userId) {
        LogUtils.show("userId=", userId);

        OkHttpUtils.post().url(GlobalConstants.PAY_WECHATPAY)
                .addParams("userid", userId)
                .addParams("total_fee", rechargeMoney * 100 + "") //设置支付金额 单位:分 //rechargeMoney
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        e.printStackTrace();
                        LogUtils.show("---------------------", e.getMessage());
                        Toast.makeText(MyGoldRechargeActivity.this, "预支付信息获取失败", Toast.LENGTH_SHORT).show();
                        tvGoldRecharge.setEnabled(true);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        LogUtils.show("---------------------", response);
                        try {
                            Gson gson = new Gson();
                            WechatPrepayInfo wechatPrepayInfo = gson.fromJson(response, WechatPrepayInfo.class);

                            if (wechatPrepayInfo.data == null) {
                                Toast.makeText(MyGoldRechargeActivity.this, "预支付信息为空,请重试!", Toast.LENGTH_SHORT).show();
                                tvGoldRecharge.setEnabled(true);
                            } else {
                                gotoPayUseWechat(wechatPrepayInfo.data);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(MyGoldRechargeActivity.this, "预支付信息解析异常!", Toast.LENGTH_SHORT).show();
                            tvGoldRecharge.setEnabled(true);
                        }
                    }
                });
    }

    private void gotoPayUseWechat(final WechatPrepayInfo.PrepayInfo data) {

        //1.注册APPID // 将该app注册到微信
        //IWXAPI api = WXAPIFactory.createWXAPI(MyGoldRechargeActivity.this, null); //
        IWXAPI api = WXAPIFactory.createWXAPI(MyGoldRechargeActivity.this, GlobalConstants.APP_ID);
        api.registerApp(GlobalConstants.APP_ID); //美页圈APPID wx9b8926da4f98936f

        PayReq req = new PayReq();
        req.appId = data.appid;
        req.partnerId = data.partnerid;
        req.prepayId = data.prepayid;
        req.nonceStr = data.noncestr;
        req.timeStamp = "" + data.timestamp;
        req.packageValue = data.packages;
        req.sign = data.sign;
        req.extData = "app data"; // 可选项
        Toast.makeText(MyGoldRechargeActivity.this, "正在开启微信支付", Toast.LENGTH_SHORT).show();
        // 在支付之前，如果应用没有注册到微信，应该先调用IWXMsg.registerApp将应用注册到微信
        api.sendReq(req);
        tvGoldRecharge.setEnabled(true);
    }
    /*------------------------------------微信支付结束--------------------------------------------*/

    /*------------------------------------支付宝支付开始--------------------------------------------*/
    //获取支付宝订单信息OrderInfo
    private void getAlipayOrderInfo(String userId) {

        OkHttpUtils.post().url(GlobalConstants.PAY_ALIPAY)
                .addParams("userid", userId)
                .addParams("total_fee", rechargeMoney + "")  //设置付款金额 单位:元
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        e.printStackTrace();
                        Toast.makeText(MyGoldRechargeActivity.this, "订单信息获取失败", Toast.LENGTH_SHORT).show();
                        tvGoldRecharge.setEnabled(true);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        LogUtils.show("---------------------", response);
                        try {
                            Gson gson = new Gson();
                            OrderInfoData orderInfoData = gson.fromJson(response, OrderInfoData.class);
                            if (TextUtils.isEmpty(orderInfoData.data)) {
                                Toast.makeText(MyGoldRechargeActivity.this, "订单信息为空,请重试!", Toast.LENGTH_SHORT).show();
                                tvGoldRecharge.setEnabled(true);
                            } else {
                                gotoPayUseAlipay(orderInfoData.data);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(MyGoldRechargeActivity.this, "订单信息解析异常!", Toast.LENGTH_SHORT).show();
                            tvGoldRecharge.setEnabled(true);
                        }
                    }
                });
    }

    private void gotoPayUseAlipay(final String orderInfo) {
        LogUtils.show(TAG, "订单信息:orderInfo = " + orderInfo);

        Runnable payRunnable = new Runnable() {
            @Override
            public void run() {
                PayTask alipay = new PayTask(MyGoldRechargeActivity.this);
                Map<String, String> result = alipay.payV2(orderInfo, true);


                Message msg = new Message();
                msg.what = SDK_PAY_FLAG;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        };
        // 必须异步调用
        Thread payThread = new Thread(payRunnable);
        payThread.start();
        tvGoldRecharge.setEnabled(true);
    }
    /*------------------------------------支付宝支付结束--------------------------------------------*/


}
