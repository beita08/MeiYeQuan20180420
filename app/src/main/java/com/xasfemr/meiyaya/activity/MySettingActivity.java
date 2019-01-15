package com.xasfemr.meiyaya.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xasfemr.meiyaya.R;
import com.xasfemr.meiyaya.global.GlobalConstants;
import com.xasfemr.meiyaya.main.BaseActivity;
import com.xasfemr.meiyaya.utils.LogUtils;
import com.xasfemr.meiyaya.utils.MD5Utils;
import com.xasfemr.meiyaya.utils.SPUtils;
import com.xasfemr.meiyaya.websocket.MyyWebSocketClient;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Call;

public class MySettingActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "MySettingActivity";

    private static final String MSG_SWITCH = "MSG_SWITCH";//消息开关,true表示开,false表示关
    private boolean msgSwitch;

    private Intent         mIntent;
    private RelativeLayout rlMyMessageSetting;
    private RelativeLayout rlMyModifyPassword;
    private RelativeLayout rlMyAdviceFeedback;
    private RelativeLayout rlMyAboutOurs;
    private TextView       tvMyLogout;
    private ImageView      ivMsgSwitch;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_setting);
        initTopBar();
        setTopTitleText("设置");

        rlMyMessageSetting = (RelativeLayout) findViewById(R.id.rl_my_message_setting);
        ivMsgSwitch = (ImageView) findViewById(R.id.iv_msg_switch);
        rlMyModifyPassword = (RelativeLayout) findViewById(R.id.rl_my_modify_password);
        rlMyAdviceFeedback = (RelativeLayout) findViewById(R.id.rl_my_advice_feedback);
        rlMyAboutOurs = (RelativeLayout) findViewById(R.id.rl_my_about_ours);
        tvMyLogout = (TextView) findViewById(R.id.tv_my_logout);

        ivMsgSwitch.setOnClickListener(this);
        rlMyModifyPassword.setOnClickListener(this);
        rlMyAdviceFeedback.setOnClickListener(this);
        rlMyAboutOurs.setOnClickListener(this);
        tvMyLogout.setOnClickListener(this);

        msgSwitch = SPUtils.getboolean(this, MSG_SWITCH, true);
        if (msgSwitch) {
            ivMsgSwitch.setImageResource(R.drawable.switch_open);
        } else {
            ivMsgSwitch.setImageResource(R.drawable.switch_off);
        }

        Button btntRegister = (Button) findViewById(R.id.btnt_register);
        Button btntLogin = (Button) findViewById(R.id.btnt_login);
        Button btntForgetPassword = (Button) findViewById(R.id.btnt_forget_password);
        Button btntSetNewPassword = (Button) findViewById(R.id.btnt_set_new_password);

        btntRegister.setOnClickListener(this);
        btntLogin.setOnClickListener(this);
        btntForgetPassword.setOnClickListener(this);
        btntSetNewPassword.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (mIntent == null) {
            mIntent = new Intent();
        }
        switch (v.getId()) {
            case R.id.iv_msg_switch:   //消息设置
                if (msgSwitch) {
                    msgSwitch = false;
                    ivMsgSwitch.setImageResource(R.drawable.switch_off);
                } else {
                    msgSwitch = true;
                    ivMsgSwitch.setImageResource(R.drawable.switch_open);
                }
                SPUtils.putboolean(this, MSG_SWITCH, msgSwitch);
                break;
            case R.id.rl_my_modify_password:   //修改密码
                mIntent.setClass(MySettingActivity.this, PasswordChangeActivity.class);
                startActivity(mIntent);
                break;
            case R.id.rl_my_advice_feedback:   //意见反馈
                mIntent.setClass(MySettingActivity.this, MySuggestionFeedbackActivity.class);
                startActivity(mIntent);
                break;
            case R.id.rl_my_about_ours:         //关于我们
                mIntent.setClass(MySettingActivity.this, MyAboutMeiyayaActivity.class);
                startActivity(mIntent);
                break;
            case R.id.tv_my_logout:             //退出
                userLogout();
                break;
            case R.id.btnt_register:            //注册
                mIntent.setClass(MySettingActivity.this, RegisterActivity.class);
                startActivity(mIntent);
                break;
            case R.id.btnt_login:                //登录
                mIntent.setClass(MySettingActivity.this, LoginActivity.class);
                startActivity(mIntent);
                break;
            case R.id.btnt_forget_password:       //忘记密码
                mIntent.setClass(MySettingActivity.this, ForgetPasswordActivity.class);
                startActivity(mIntent);
                break;
            case R.id.btnt_set_new_password:       //设置新密码
                mIntent.setClass(MySettingActivity.this, SetNewPasswordActivity.class);
                startActivity(mIntent);
                break;
            default:
                break;
        }
    }

    //退出登录,弹出对话框,目前使用系统自带的对话框
    private void userLogout() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("确定退出登录？");
        builder.setNegativeButton("取消", null);//啥也不干,系统对话框自动消失
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String phoneNumber = SPUtils.getString(MySettingActivity.this, GlobalConstants.phoneNumber, "");
                if (TextUtils.isEmpty(phoneNumber)) {
                    Toast.makeText(MySettingActivity.this, "当前没有登录用户!", Toast.LENGTH_SHORT).show();
                } else {
                    //访问网络退出登录
                    gotoLogout(phoneNumber);
                }
            }
        });
        builder.show();
    }

    //访问网络退出登录
    private void gotoLogout(String phoneNumber) {
        OkHttpUtils.post().url(GlobalConstants.URL_LOGOUT)
                .addParams("phoneNumber", phoneNumber)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Toast.makeText(MySettingActivity.this, "网络异常,请稍后重试", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.i(TAG, "onResponse: ---退出登录访问网络成功---response = " + response + " ---");

                        try {
                            JSONObject jo = new JSONObject(response);
                            int status = jo.getInt("status");
                            String message = jo.getString("message");

                            switch (status) {
                                case 202: //退出账号失败
                                    Toast.makeText(MySettingActivity.this, message + ",请重试", Toast.LENGTH_SHORT).show();
                                    System.out.println(message);
                                    break;
                                case 200: //退出账号成功
                                    Toast.makeText(MySettingActivity.this, message, Toast.LENGTH_SHORT).show();
                                    System.out.println(message);

                                    //退出成功删除本地用户数据
                                    SPUtils.remove(MySettingActivity.this, GlobalConstants.isLoginState);
                                    SPUtils.remove(MySettingActivity.this, GlobalConstants.phoneNumber);
                                    SPUtils.remove(MySettingActivity.this, GlobalConstants.password);
                                    SPUtils.remove(MySettingActivity.this, GlobalConstants.userID);
                                    SPUtils.remove(MySettingActivity.this, GlobalConstants.USER_NAME);
                                    SPUtils.remove(MySettingActivity.this, GlobalConstants.USER_HEAD_IMAGE);
                                    //删除"我的"信息的缓存
                                    SPUtils.remove(MySettingActivity.this, MD5Utils.getMd5(GlobalConstants.URL_USER_ME));

                                    //用户切换账号是断开WebSocket, 登录时重新连接
                                    LogUtils.show("WebSocket", "close... 断开WebSocket...");
                                    MyyWebSocketClient.getInstance(MySettingActivity.this).close();

                                    finish();
                                    break;
                                default:
                                    Toast.makeText(MySettingActivity.this, "退出账号出现未知异常!", Toast.LENGTH_SHORT).show();
                                    System.out.println("退出账号出现未知异常!");
                                    break;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(MySettingActivity.this, "解析数据异常", Toast.LENGTH_SHORT).show();
                        } /*catch (InterruptedException e) {
                            e.printStackTrace();
                            LogUtils.show("WebSocket", "---用户退出登录成功,但是断开WebSocket出现异常---");
                        }*/
                    }
                });
    }
}
