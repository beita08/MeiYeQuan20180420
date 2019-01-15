package com.xasfemr.meiyaya.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.mob.MobSDK;
import com.xasfemr.meiyaya.R;
import com.xasfemr.meiyaya.bean.RegisterData;
import com.xasfemr.meiyaya.global.GlobalConstants;
import com.xasfemr.meiyaya.main.BaseActivity;
import com.xasfemr.meiyaya.utils.LogUtils;
import com.xasfemr.meiyaya.utils.MD5Utils;
import com.xasfemr.meiyaya.utils.SPUtils;
import com.xasfemr.meiyaya.utils.ToastUtil;
import com.xasfemr.meiyaya.view.progress.SFProgressDialog;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import okhttp3.Call;

public class RegisterActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "RegisterActivity";

    private static final int MSG_TIME  = 0;
    private              int secondNum = 60;

    private EventHandler eventHandler;
    private ImageView    ivLogo;
    private EditText     etPhoneNumber;
    private Button       btnClosePhoneNum;
    private EditText     etVerificationCode;
    private Button       btnGetVerificationCode;
    private EditText     etPassword;
    private Button       btnClosePassword;
    private TextView     tvAppProtocol;
    private Button       btnRegister;
    private LinearLayout llGotoLogin;
    private EditText     etInvitationCode;
    private Button       btnCloseInvitationCode;
    private String       mPhoneNumber;
    private String       mPassword;
    private String       invitationCode;

    private SFProgressDialog progressDialog;

    //发送验证码后Button变为秒数,并递减
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (secondNum > 0) {
                btnGetVerificationCode.setClickable(false);
                btnGetVerificationCode.setText(secondNum + " 秒");
                secondNum--;
                mHandler.sendEmptyMessageDelayed(MSG_TIME, 1000);
            } else {
                btnGetVerificationCode.setClickable(true);
                btnGetVerificationCode.setText("获取验证码");
                secondNum = 60;
                mHandler.removeMessages(MSG_TIME);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initTopBar();
        setTopTitleText("注册");

        //初始化控件并添加监听事件
        initViewAddListener();

        // 如果选择通过代码配置，则不需要继承MobApplication，只要在使用SMSSDK之前，调用以下代码：
        // 通过代码注册你的AppKey和AppSecret
        MobSDK.init(RegisterActivity.this, "209ea0ff07300", "7ed454548d1aa66ec1c3c8a3daadbab3");

        // 如果希望在读取通信录的时候提示用户，可以添加下面的代码，并且必须在其他代码调用之前，否则不起作用；如果没这个需求，可以不加这行代码
        //SMSSDK.setAskPermisionOnReadContact(true);

        // 创建EventHandler对象
        // 处理你自己的逻辑
        eventHandler = new EventHandler() {
            public void afterEvent(int event, int result, Object data) {
                if (data instanceof Throwable) {
                    Throwable throwable = (Throwable) data;
                    String msg = throwable.getMessage();
                    //System.out.println("msg = " + msg);
                    try {
                        JSONObject jo = new JSONObject(msg);
                        String detail = jo.getString("detail");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(RegisterActivity.this, detail, Toast.LENGTH_SHORT).show();
                            }
                        });
                    } catch (JSONException e) {
                    }
                } else {
                    if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                        // 获取到验证码的回调
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ToastUtil.showShort(RegisterActivity.this, "验证码已发送,请注意查收");

                            }
                        });

                    } else if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                        //提交验证码的操作的回调

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ToastUtil.showShort(RegisterActivity.this, "验证码验证成功!");
                                submitAccountToServer();
                            }
                        });
                    }
                }
            }
        };
        // 注册监听器
        SMSSDK.registerEventHandler(eventHandler);
    }


    //访问注册URL
    private void submitAccountToServer() {
        progressDialog.show();

        String md5PassWord = MD5Utils.getMd5(mPassword);
        System.out.println("md5PassWord== " + md5PassWord);
        OkHttpUtils
                .post()
                .url(GlobalConstants.URL_REGISTER)
                .addParams("phoneNumber", mPhoneNumber)
                .addParams("password", md5PassWord)
                .addParams("invitation", invitationCode)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        progressDialog.dismiss();
                        e.printStackTrace();
                        ToastUtil.showShort(RegisterActivity.this, "注册网络连接失败!");
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        LogUtils.show(TAG, "---注册服务网络成功---response = " + response + " ---");
                        progressDialog.dismiss();
                        Gson gson = new Gson();
                        RegisterData registerData = gson.fromJson(response, RegisterData.class);

                        switch (registerData.status) {
                            case 200://注册失败
                            case 201://该电话号码已经注册过
                                Toast.makeText(RegisterActivity.this, registerData.message, Toast.LENGTH_SHORT).show();
                                System.out.println(registerData.message);
                                break;

                            case 202:  //注册成功
                                Toast.makeText(RegisterActivity.this, registerData.message, Toast.LENGTH_SHORT).show();
                                System.out.println(registerData.message);

                                //注册成功,删除可能存在的旧本地用户数据,让用户重新登录
                                SPUtils.remove(RegisterActivity.this, GlobalConstants.isLoginState);
                                SPUtils.remove(RegisterActivity.this, GlobalConstants.phoneNumber);
                                SPUtils.remove(RegisterActivity.this, GlobalConstants.password);
                                SPUtils.remove(RegisterActivity.this, GlobalConstants.userID);
                                SPUtils.remove(RegisterActivity.this, GlobalConstants.USER_NAME);
                                SPUtils.remove(RegisterActivity.this, GlobalConstants.USER_HEAD_IMAGE);

                                //跳到登录页面
                                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                                finish();
                                break;

                            default:
                                ToastUtil.showShort(RegisterActivity.this, "注册出现未知异常!");
                                break;
                        }
                    }
                });
    }


    //初始化控件并添加监听事件
    private void initViewAddListener() {
        progressDialog = new SFProgressDialog(this);

        ivLogo = (ImageView) findViewById(R.id.iv_logo);
        etPhoneNumber = (EditText) findViewById(R.id.et_phone_number);
        btnClosePhoneNum = (Button) findViewById(R.id.btn_close_phone_num);
        etVerificationCode = (EditText) findViewById(R.id.et_verification_code);
        btnGetVerificationCode = (Button) findViewById(R.id.btn_get_verification_code);
        etPassword = (EditText) findViewById(R.id.et_password);
        btnClosePassword = (Button) findViewById(R.id.btn_close_password);
        tvAppProtocol = (TextView) findViewById(R.id.tv_app_protocol);
        btnRegister = (Button) findViewById(R.id.btn_register);
        llGotoLogin = (LinearLayout) findViewById(R.id.ll_goto_login);
        etInvitationCode = (EditText) findViewById(R.id.et_invitation_code);
        btnCloseInvitationCode = (Button) findViewById(R.id.btn_close_invitation_code);

        btnClosePhoneNum.setOnClickListener(this);
        btnGetVerificationCode.setOnClickListener(this);
        btnClosePassword.setOnClickListener(this);
        tvAppProtocol.setOnClickListener(this);
        btnRegister.setOnClickListener(this);
        llGotoLogin.setOnClickListener(this);
        btnCloseInvitationCode.setOnClickListener(this);

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
                    btnClosePhoneNum.setVisibility(View.GONE);
                } else {
                    btnClosePhoneNum.setVisibility(View.VISIBLE);
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

        etInvitationCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String inviCode = s.toString().trim();
                if (TextUtils.isEmpty(inviCode)) {
                    btnCloseInvitationCode.setVisibility(View.GONE);
                } else {
                    btnCloseInvitationCode.setVisibility(View.VISIBLE);
                }
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        //用户去查看验证码再回来时手机号码的回显
        if (!TextUtils.isEmpty(mPhoneNumber)) {
            etPhoneNumber.setText(mPhoneNumber);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SMSSDK.unregisterEventHandler(eventHandler);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btn_get_verification_code://获取验证码按钮,即获取验证码
                mPhoneNumber = etPhoneNumber.getText().toString().trim();

                if (TextUtils.isEmpty(mPhoneNumber)) { //1.判断手机号是否为空
                    ToastUtil.showShort(RegisterActivity.this, "请输入手机号");
                    return;
                }
                if (!mPhoneNumber.matches(GlobalConstants.STR_PHONE_REGEX2)) { //2.用正则表达式判断手机号是否合格
                    ToastUtil.showShort(RegisterActivity.this, "请输入正确的手机号");
                    return;
                }
                //Toast.makeText(this, "验证码将在3秒后发送到您的手机", Toast.LENGTH_SHORT).show();
                SMSSDK.getVerificationCode("86", mPhoneNumber);
                secondNum = 60;
                mHandler.sendEmptyMessage(MSG_TIME);

                break;
            case R.id.btn_register://注册按钮 ,即提交验证码

                mPhoneNumber = etPhoneNumber.getText().toString().trim();
                String verificationCode = etVerificationCode.getText().toString().trim(); //验证码
                mPassword = etPassword.getText().toString().trim();
                //邀请码
                invitationCode = etInvitationCode.getText().toString().trim();

                if (TextUtils.isEmpty(mPhoneNumber)) {
                    ToastUtil.showShort(RegisterActivity.this, "手机号码不能为空");
                    return;
                }
                if (!mPhoneNumber.matches(GlobalConstants.STR_PHONE_REGEX2)) { //2.用正则表达式判断手机号是否合格
                    ToastUtil.showShort(RegisterActivity.this, "请输入正确的手机号");
                    return;
                }
                if (TextUtils.isEmpty(verificationCode)) {
                    ToastUtil.showShort(RegisterActivity.this, "验证码不能为空");
                    return;
                }
                if (TextUtils.isEmpty(mPassword)) {
                    ToastUtil.showShort(RegisterActivity.this, "密码不能为空");
                    return;

                }
                if (mPassword.length() < 6 || mPassword.length() > 16) {
                    ToastUtil.showShort(RegisterActivity.this, "密码长度必须再6至16位之间!");
                    return;

                }
                //向SDK服务器提交验证码
                SMSSDK.submitVerificationCode("86", mPhoneNumber, verificationCode);
                break;
            case R.id.btn_close_phone_num://一键删除电话号码
                etPhoneNumber.setText("");
                break;
            case R.id.btn_close_password://一键删除密码
                etPassword.setText("");
                break;
            case R.id.tv_app_protocol://查看App服务协议
                Intent intent = new Intent(RegisterActivity.this, AppProtocolActivity.class);
                startActivity(intent);
                break;
            case R.id.ll_goto_login://已有账号去登录
                Intent loginIntent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(loginIntent);
                break;
            case R.id.btn_close_invitation_code://一键删除邀请码
                etInvitationCode.setText("");
                break;
            default:
                break;
        }

    }
}
