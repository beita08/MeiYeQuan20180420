package com.xasfemr.meiyaya.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.xasfemr.meiyaya.R;
import com.xasfemr.meiyaya.bean.LoginData;
import com.xasfemr.meiyaya.global.GlobalConstants;
import com.xasfemr.meiyaya.main.BaseActivity;
import com.xasfemr.meiyaya.utils.LogUtils;
import com.xasfemr.meiyaya.utils.MD5Utils;
import com.xasfemr.meiyaya.utils.SPUtils;
import com.xasfemr.meiyaya.utils.ToastUtil;
import com.xasfemr.meiyaya.view.progress.SFProgressDialog;
import com.xasfemr.meiyaya.websocket.MyyWebSocketClient;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.java_websocket.WebSocket;

import cn.jpush.android.api.JPushInterface;
import okhttp3.Call;

public class LoginActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "LoginActivity";

    private Intent       mIntent;
    private EditText     etPhoneNumber;
    private Button       btnClosePhoneNumber;
    private EditText     etPassword;
    private Button       btnClosePassword;
    private Button       btnLogin;
    private LinearLayout llGotoRegister;
    private TextView     tvFrgetPassword;
    private Button       btnGotoQQ;
    private Button       btnGotoWechat;
    private Button       btnGotoWeibo;

    private SFProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initTopBar();
        setTopTitleText("登录");

        initViewAddListener();
    }

    private void initViewAddListener() {
        progressDialog = new SFProgressDialog(this);
        etPhoneNumber = (EditText) findViewById(R.id.et_phone_number);
        btnClosePhoneNumber = (Button) findViewById(R.id.btn_close_phone_number);
        etPassword = (EditText) findViewById(R.id.et_password);
        btnClosePassword = (Button) findViewById(R.id.btn_close_password);
        btnLogin = (Button) findViewById(R.id.btn_login);
        llGotoRegister = (LinearLayout) findViewById(R.id.ll_goto_register);
        tvFrgetPassword = (TextView) findViewById(R.id.tv_forget_password);
        btnGotoQQ = (Button) findViewById(R.id.btn_goto_qq);
        btnGotoWechat = (Button) findViewById(R.id.btn_goto_wechat);
        btnGotoWeibo = (Button) findViewById(R.id.btn_goto_weibo);

        btnClosePhoneNumber.setOnClickListener(this);
        btnClosePassword.setOnClickListener(this);
        btnLogin.setOnClickListener(this);
        llGotoRegister.setOnClickListener(this);
        tvFrgetPassword.setOnClickListener(this);
        btnGotoQQ.setOnClickListener(this);
        btnGotoWechat.setOnClickListener(this);
        btnGotoWeibo.setOnClickListener(this);

        etPhoneNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String phoneNum = s.toString().trim();
                if (TextUtils.isEmpty(phoneNum)) {
                    btnClosePhoneNumber.setVisibility(View.GONE);
                } else {
                    btnClosePhoneNumber.setVisibility(View.VISIBLE);
                }
            }
        });

        etPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String passW = s.toString().trim();
                if (TextUtils.isEmpty(passW)) {
                    btnClosePassword.setVisibility(View.GONE);
                } else {
                    btnClosePassword.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (mIntent == null) {
            mIntent = new Intent();
        }

        switch (v.getId()) {
            case R.id.btn_close_phone_number://一键删除电话
                etPhoneNumber.setText("");
                break;
            case R.id.btn_close_password: //一键删除密码
                etPassword.setText("");
                break;
            case R.id.btn_login:    //登录
                String phoneNumber = etPhoneNumber.getText().toString().trim();
                String password = etPassword.getText().toString().trim();

                if (TextUtils.isEmpty(phoneNumber)) {
                    ToastUtil.showShort(LoginActivity.this, "手机号不能为空");
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    ToastUtil.showShort(LoginActivity.this, "密码不能为空");
                    return;
                }
                if (!phoneNumber.matches(GlobalConstants.STR_PHONE_REGEX2)) {
                    ToastUtil.showShort(LoginActivity.this, "请输入正确的手机号");
                    return;
                }
                gotoLogin(phoneNumber, password);

                break;
            case R.id.ll_goto_register:  //去注册
                mIntent.setClass(LoginActivity.this, RegisterActivity.class);
                startActivity(mIntent);
                break;
            case R.id.tv_forget_password:  //忘记密码
                mIntent.setClass(LoginActivity.this, ForgetPasswordActivity.class);
                startActivity(mIntent);
                break;
            case R.id.btn_goto_qq: //第三方QQ登录

                break;
            case R.id.btn_goto_wechat://第三方微信登录

                break;
            case R.id.btn_goto_weibo: //第三方微博登录

                break;
            default:
                break;
        }
    }

    private void gotoLogin(final String phoneNumber, final String password) {
        progressDialog.show();
        LogUtils.show("登陆JPush RegistrationID", JPushInterface.getRegistrationID(this));
        String md5Password = MD5Utils.getMd5(password);
        OkHttpUtils
                .post()
                .url(GlobalConstants.URL_LOGIN)
                .addParams("phoneNumber", phoneNumber)
                .addParams("password", md5Password)
                .addParams("registrationID", JPushInterface.getRegistrationID(this))
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        LogUtils.show("--------登录访问异常------", e.getMessage());
                        e.printStackTrace();
                        ToastUtil.showShort(LoginActivity.this, "网络访问出现异常");
                        progressDialog.dismiss();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        progressDialog.dismiss();

                        LogUtils.show(TAG, "登录成功 response = " + response + " ---");
                        //System.out.println(response);
                        Gson gson = new Gson();
                        LoginData loginData = gson.fromJson(response, LoginData.class);

                        switch (loginData.status) {
                            case 200: //用户名未注册
                            case 201: //密码有误
                            case 203: //登录失败
                                Toast.makeText(LoginActivity.this, loginData.message, Toast.LENGTH_SHORT).show();
                                System.out.println(loginData.message);
                                break;
                            case 202:   //登录成功 // (修改密码,设置新密码,注册成功,之后都会跳到登录页面,这里是用户登录的唯一入口)
                                Toast.makeText(LoginActivity.this, loginData.message, Toast.LENGTH_SHORT).show();
                                System.out.println(loginData.message);

                                //本地保存必要的用户信息
                                SPUtils.putboolean(LoginActivity.this, GlobalConstants.isLoginState, true);
                                SPUtils.putString(LoginActivity.this, GlobalConstants.phoneNumber, loginData.data.phonenumber);
                                SPUtils.putString(LoginActivity.this, GlobalConstants.password, loginData.data.userpwd);
                                SPUtils.putString(LoginActivity.this, GlobalConstants.userID, loginData.data.id);
                                //将用户名和头像存至本地
                                SPUtils.putString(LoginActivity.this, GlobalConstants.USER_NAME, loginData.data.username);
                                SPUtils.putString(LoginActivity.this, GlobalConstants.IS_APPROVE, loginData.data.is_approve); //是否完成企业认证 5/3 加入
                                SPUtils.putString(LoginActivity.this, GlobalConstants.USER_HEAD_IMAGE, loginData.data.images);
                                SPUtils.putInt(LoginActivity.this, GlobalConstants.USER_GOLD_NUMBER, loginData.data.goldmoney);
                                SPUtils.putInt(LoginActivity.this, GlobalConstants.LECTURER_lSTATUS, loginData.data.lstatus);

                                //开启聊天的WebSocket
                                openMyyWebSocket();

                                //登录页面直接消失
                                finish();
                                break;
                            default:
                                ToastUtil.showShort(LoginActivity.this, "登录出现未知异常");
                                break;
                        }
                    }
                });
    }

    private void openMyyWebSocket() {
        //开启WebSocket客户端
        try {
            WebSocket.READYSTATE readyState = MyyWebSocketClient.getInstance(this).getReadyState();
            LogUtils.show("WebSocket", "getReadyState() = " + readyState);

            //|| readyState.equals(WebSocket.READYSTATE.CLOSED) || readyState.equals(WebSocket.READYSTATE.CLOSING)
            if (readyState.equals(WebSocket.READYSTATE.NOT_YET_CONNECTED)) {
                LogUtils.show("WebSocket", "---开启WebSocket客户端---");
                MyyWebSocketClient.getInstance(this).connect();
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.show("WebSocket", "...LoginActivity...开启WebSocket出现异常...");
        }
    }
}
