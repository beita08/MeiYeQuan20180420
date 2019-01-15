package com.xasfemr.meiyaya.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
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
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.xasfemr.meiyaya.R;
import com.xasfemr.meiyaya.bean.OrderInfoData;
import com.xasfemr.meiyaya.bean.WechatPrepayInfo;
import com.xasfemr.meiyaya.global.GlobalConstants;
import com.xasfemr.meiyaya.main.BaseActivity;
import com.xasfemr.meiyaya.pay.AuthResult;
import com.xasfemr.meiyaya.pay.PayResult;
import com.xasfemr.meiyaya.utils.SPUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;

public class MyMemberRechargeActivity extends BaseActivity implements View.OnClickListener {

    private static final int PAYMENT_WECHAT_PAY = 0; //支付类型-微信
    private static final int PAYMENT_ALI_PAY    = 1; //支付类型-支付宝

    private int PAYMENT = PAYMENT_WECHAT_PAY;//默认使用微信支付

    private static final int SDK_PAY_FLAG  = 1;
    private static final int SDK_AUTH_FLAG = 2;

    private CircleImageView ivUserIcon;
    private TextView        tvUserName;
    private ImageView       ivMemberOpen;
    private RelativeLayout  rlMemWechatpay;
    private ImageView       ivMemWechatpay;
    private RelativeLayout  rlMemAlipay;
    private ImageView       ivMemAlipay;
    private TextView        tvMemRecharge;

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
                        Toast.makeText(MyMemberRechargeActivity.this, "支付成功", Toast.LENGTH_SHORT).show();
                    } else {
                        // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
                        Toast.makeText(MyMemberRechargeActivity.this, "支付失败", Toast.LENGTH_SHORT).show();
                    }
                    tvMemRecharge.setEnabled(true);
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
                        Toast.makeText(MyMemberRechargeActivity.this, "授权成功\n" + String.format("authCode:%s", authResult.getAuthCode()), Toast.LENGTH_SHORT).show();
                    } else {
                        // 其他状态值则为授权失败
                        Toast.makeText(MyMemberRechargeActivity.this, "授权失败" + String.format("authCode:%s", authResult.getAuthCode()), Toast.LENGTH_SHORT).show();

                    }
                    break;
                }
                default:
                    break;
            }
        };
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_member_recharge);
        initTopBar();
        setTopTitleText("会员充值");
        Intent intent = getIntent();
        String userName = intent.getStringExtra("USER_NAME");
        String userIcon = intent.getStringExtra("USER_ICON");

        ivUserIcon = (CircleImageView) findViewById(R.id.iv_me_user_icon);
        tvUserName = (TextView) findViewById(R.id.tv_me_user_name);
        ivMemberOpen = (ImageView) findViewById(R.id.iv_member_open);
        rlMemWechatpay = (RelativeLayout) findViewById(R.id.rl_mem_payment_wechatpay);
        ivMemWechatpay = (ImageView) findViewById(R.id.iv_mem_wechatpay);
        rlMemAlipay = (RelativeLayout) findViewById(R.id.rl_mem_payment_alipay);
        ivMemAlipay = (ImageView) findViewById(R.id.iv_mem_alipay);
        tvMemRecharge = (TextView) findViewById(R.id.tv_mem_recharge);

        Glide.with(MyMemberRechargeActivity.this).load(userIcon).into(ivUserIcon);
        tvUserName.setText(userName);

        rlMemWechatpay.setOnClickListener(this);
        rlMemAlipay.setOnClickListener(this);
        tvMemRecharge.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.rl_mem_payment_wechatpay:
                ivMemWechatpay.setImageResource(R.drawable.gold_pay_selected);
                ivMemAlipay.setImageResource(R.drawable.gold_pay_not_selected);

                PAYMENT = PAYMENT_WECHAT_PAY;
                break;
            case R.id.rl_mem_payment_alipay:
                ivMemWechatpay.setImageResource(R.drawable.gold_pay_not_selected);
                ivMemAlipay.setImageResource(R.drawable.gold_pay_selected);

                PAYMENT = PAYMENT_ALI_PAY;

                break;
            case R.id.tv_mem_recharge: //立即支付

                String userId = SPUtils.getString(MyMemberRechargeActivity.this, GlobalConstants.userID, "");

                if (PAYMENT == PAYMENT_WECHAT_PAY) { //微信支付

                    if (TextUtils.isEmpty(userId)) {
                        Toast.makeText(this, "获取用户信息失败,请重新登录", Toast.LENGTH_SHORT).show();
                    } else {
                        //获取微信预支付信息
                        getWechatPrepayInfo(userId);
                        tvMemRecharge.setEnabled(false);
                    }


                } else if (PAYMENT == PAYMENT_ALI_PAY) {//支付宝支付

                    if (TextUtils.isEmpty(userId)) {
                        Toast.makeText(this, "获取用户信息失败,请重新登录", Toast.LENGTH_SHORT).show();
                    } else {
                        //获取支付宝订单信息OrderInfo
                        getAlipayOrderInfo(userId);
                        tvMemRecharge.setEnabled(false);
                    }

                } else {
                    Toast.makeText(this, "请选择支付方式", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

    /*------------------------------------微信支付开始--------------------------------------------*/
    //获取微信预支付信息
    private void getWechatPrepayInfo(String userId) {
        System.out.println("userId=" + userId);

        OkHttpUtils
                .post()
                .url(GlobalConstants.PAY_WECHATPAY)
                .addParams("userid", userId)
                .addParams("total_fee", "1800") //支付金额:以分为单位
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        System.out.println("----------------预支付信息获取失败!----------------");
                        e.printStackTrace();
                        System.out.println("-----------------------------------------------");
                        Toast.makeText(MyMemberRechargeActivity.this, "预支付信息获取失败", Toast.LENGTH_SHORT).show();
                        tvMemRecharge.setEnabled(true);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        System.out.println("----------------预支付信息获取成功!----------------");
                        System.out.println("---------------------" + response + "--------------------------");
                        try {
                            Gson gson = new Gson();
                            WechatPrepayInfo wechatPrepayInfo = gson.fromJson(response, WechatPrepayInfo.class);

                            if (wechatPrepayInfo.data == null) {
                                Toast.makeText(MyMemberRechargeActivity.this, "预支付信息为空,请重试!", Toast.LENGTH_SHORT).show();
                                System.out.println("----------------预支付信息为空,请重试!----------------");
                                tvMemRecharge.setEnabled(true);
                            } else {
                                gotoPayUseWechat(wechatPrepayInfo.data);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(MyMemberRechargeActivity.this, "预支付信息解析异常!", Toast.LENGTH_SHORT).show();
                            System.out.println("----------------预支付信息解析异常!----------------");
                            tvMemRecharge.setEnabled(true);
                        }
                    }
                });
    }

    private void gotoPayUseWechat(final WechatPrepayInfo.PrepayInfo data) {

        //1.注册APPID // 将该app注册到微信
        IWXAPI api = WXAPIFactory.createWXAPI(MyMemberRechargeActivity.this, null);
        api.registerApp("wx9b8926da4f98936f"); //美页圈APPID wx9b8926da4f98936f

        PayReq req = new PayReq();
        req.appId = data.appid;
        req.partnerId = data.partnerid;
        req.prepayId = data.prepayid;
        req.nonceStr = data.noncestr;
        req.timeStamp = "" + data.timestamp;
        req.packageValue = data.packages;
        req.sign = data.sign;
        req.extData = "app data"; // 可选项
        Toast.makeText(MyMemberRechargeActivity.this, "正在开启微信支付", Toast.LENGTH_SHORT).show();
        // 在支付之前，如果应用没有注册到微信，应该先调用IWXMsg.registerApp将应用注册到微信
        api.sendReq(req);
        tvMemRecharge.setEnabled(true);
    }

    /*------------------------------------微信支付结束--------------------------------------------*/

    /*------------------------------------支付宝支付开始--------------------------------------------*/
    //获取支付宝订单信息OrderInfo
    private void getAlipayOrderInfo(String userId) {

        OkHttpUtils
                .post()
                .url(GlobalConstants.PAY_ALIPAY)
                .addParams("userid", userId)
                .addParams("total_fee", "18")  //支付金额:以元为单位
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        System.out.println("----------------订单信息获取失败!----------------");
                        e.printStackTrace();
                        System.out.println("-----------------------------------------------");
                        Toast.makeText(MyMemberRechargeActivity.this, "订单信息获取失败", Toast.LENGTH_SHORT).show();
                        tvMemRecharge.setEnabled(true);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        System.out.println("----------------订单信息获取成功!----------------");
                        System.out.println("---------------------" + response + "--------------------------");
                        try {
                            Gson gson = new Gson();
                            OrderInfoData orderInfoData = gson.fromJson(response, OrderInfoData.class);

                            if (TextUtils.isEmpty(orderInfoData.data)) {
                                Toast.makeText(MyMemberRechargeActivity.this, "订单信息为空,请重试!", Toast.LENGTH_SHORT).show();
                                System.out.println("----------------订单信息为空,请重试!----------------");
                                tvMemRecharge.setEnabled(true);
                            } else {
                                gotoPayUseAlipay(orderInfoData.data);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(MyMemberRechargeActivity.this, "订单信息解析异常!", Toast.LENGTH_SHORT).show();
                            System.out.println("----------------订单信息解析异常!----------------");
                            tvMemRecharge.setEnabled(true);
                        }
                    }
                });
    }

    private void gotoPayUseAlipay(final String orderInfo) {
        System.out.println("订单信息:orderInfo=" + orderInfo);

        Runnable payRunnable = new Runnable() {
            @Override
            public void run() {
                PayTask alipay = new PayTask(MyMemberRechargeActivity.this);
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
        tvMemRecharge.setEnabled(true);
    }
    /*------------------------------------支付宝支付结束--------------------------------------------*/


}
