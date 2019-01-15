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
import android.widget.TextView;
import android.widget.Toast;

import com.mob.MobSDK;
import com.xasfemr.meiyaya.R;
import com.xasfemr.meiyaya.global.GlobalConstants;
import com.xasfemr.meiyaya.main.BaseActivity;

import org.json.JSONException;
import org.json.JSONObject;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

public class ForgetPasswordActivity extends BaseActivity implements View.OnClickListener {

    private static final int MSG_TIME  = 0;
    private              int secondNum = 60;

    private EventHandler eventHandler;

    private String   mPhoneNumber;
    private EditText etPhoneNumber;
    private Button   btnClosePhoneNum;
    private EditText etVerificationCode;
    private Button   btnGetVerificationCode;
    private TextView tvNextStep;

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
        setContentView(R.layout.activity_forget_password);
        initTopBar();
        setTopTitleText("验证手机号");

        initViewAddListener();

        //在onCreate方法中注册短信SDK
        registerMobSMSSDK();
    }

    //初始化控件并添加监听事件
    private void initViewAddListener() {
        etPhoneNumber = (EditText) findViewById(R.id.et_phone_number);
        btnClosePhoneNum = (Button) findViewById(R.id.btn_close_phone_num);
        etVerificationCode = (EditText) findViewById(R.id.et_verification_code);
        btnGetVerificationCode = (Button) findViewById(R.id.btn_get_verification_code);
        tvNextStep = (TextView) findViewById(R.id.tv_next_step);

        btnClosePhoneNum.setOnClickListener(this);
        btnGetVerificationCode.setOnClickListener(this);
        tvNextStep.setOnClickListener(this);

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
    }

    private void registerMobSMSSDK() {

        // 如果选择通过代码配置，则不需要继承MobApplication，只要在使用SMSSDK之前，调用以下代码：
        // 通过代码注册你的AppKey和AppSecret
        MobSDK.init(ForgetPasswordActivity.this, "209ea0ff07300", "7ed454548d1aa66ec1c3c8a3daadbab3");

        // 如果希望在读取通信录的时候提示用户，可以添加下面的代码，并且必须在其他代码调用之前，否则不起作用；如果没这个需求，可以不加这行代码
        //SMSSDK.setAskPermisionOnReadContact(true);

        // 创建EventHandler对象
        // 处理你自己的逻辑
        eventHandler = new EventHandler() {
            //TODO 检查注册流程问题
            public void afterEvent(int event, int result, Object data) {
                if (data instanceof Throwable) {
                    Throwable throwable = (Throwable) data;
                    String msg = throwable.getMessage();//获取到异常信息
                    //System.out.println("msg = " + msg);
                    try {
                        JSONObject jo = new JSONObject(msg);
                        String detail = jo.getString("detail");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(ForgetPasswordActivity.this, detail, Toast.LENGTH_SHORT).show();
                            }
                        });
                    } catch (JSONException e) {
                    }
                } else {
                    if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                        // 获取到验证码的回调
                        System.out.println("1111111获取验证码成功!");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(ForgetPasswordActivity.this, "验证码已发送,请注意查收!", Toast.LENGTH_SHORT).show();
                            }
                        });

                    } else if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                        //提交验证码的操作的回调
                        System.out.println("22222验证成功!");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(ForgetPasswordActivity.this, "验证成功!", Toast.LENGTH_SHORT).show();
                            }
                        });

                        //跳转到设置新密码,并携带电话号码的数据
                        Intent intent = new Intent(ForgetPasswordActivity.this, SetNewPasswordActivity.class);
                        intent.putExtra("PhoneNumber", mPhoneNumber);
                        finish();
                        startActivity(intent);
                    }
                }
            }
        };
        // 注册监听器
        SMSSDK.registerEventHandler(eventHandler);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btn_close_phone_num://一键删除电话号码 btn_close_phone_num
                etPhoneNumber.setText("");
                break;
            case R.id.btn_get_verification_code://获取验证码按钮,即获取验证码
                mPhoneNumber = etPhoneNumber.getText().toString().trim();

                if (TextUtils.isEmpty(mPhoneNumber)) { //1.判断手机号是否为空
                    Toast.makeText(ForgetPasswordActivity.this, "请输入手机号", Toast.LENGTH_SHORT).show();

                } else if (!mPhoneNumber.matches(GlobalConstants.STR_PHONE_REGEX2)) { //2.用正则表达式判断手机号是否合格
                    Toast.makeText(ForgetPasswordActivity.this, "请输入正确的手机号", Toast.LENGTH_SHORT).show();

                } else {
                    //Toast.makeText(this, "验证码将在3秒后发送到您的手机", Toast.LENGTH_SHORT).show();
                    //获取验证码
                    SMSSDK.getVerificationCode("86", mPhoneNumber);
                    secondNum = 60;
                    mHandler.sendEmptyMessage(MSG_TIME);
                }

                break;
            case R.id.tv_next_step:
                mPhoneNumber = etPhoneNumber.getText().toString().trim();
                String verificationCode = etVerificationCode.getText().toString().trim();

                if (TextUtils.isEmpty(mPhoneNumber)) {
                    Toast.makeText(this, "手机号码不能为空", Toast.LENGTH_SHORT).show();

                } else if (!mPhoneNumber.matches(GlobalConstants.STR_PHONE_REGEX2)) { //2.用正则表达式判断手机号是否合格
                    Toast.makeText(ForgetPasswordActivity.this, "请输入正确的手机号", Toast.LENGTH_SHORT).show();

                } else if (TextUtils.isEmpty(verificationCode)) {
                    Toast.makeText(this, "验证码不能为空!", Toast.LENGTH_SHORT).show();

                } else {
                    //向SDK服务器提交验证码
                    SMSSDK.submitVerificationCode("86", mPhoneNumber, verificationCode);
                }

                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在onDestroy方法中注销短信SDK
        SMSSDK.unregisterEventHandler(eventHandler);
    }
}
