package com.xasfemr.meiyaya.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.xasfemr.meiyaya.R;
import com.xasfemr.meiyaya.global.GlobalConstants;
import com.xasfemr.meiyaya.main.BaseActivity;
import com.xasfemr.meiyaya.utils.MD5Utils;
import com.xasfemr.meiyaya.utils.SPUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Call;

public class SetNewPasswordActivity extends BaseActivity implements View.OnClickListener {

    private String   mPhoneNumber;
    private EditText etNewPassword;
    private EditText etConfirmPassword;
    private Button   btnCloseNewPassword;
    private Button   btnCloseConfirmPassword;
    private TextView tvSetPasswordComplete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_new_password);
        initTopBar();
        setTopTitleText("设置新密码");

        Intent intent = getIntent();
        mPhoneNumber = intent.getStringExtra("PhoneNumber");
        System.out.println("---SetNewPasswordActivity------phoneNumber:" + mPhoneNumber);

        etNewPassword = (EditText) findViewById(R.id.et_new_password);
        etConfirmPassword = (EditText) findViewById(R.id.et_confirm_password);
        btnCloseNewPassword = (Button) findViewById(R.id.btn_close_new_password);
        btnCloseConfirmPassword = (Button) findViewById(R.id.btn_close_confirm_password);
        tvSetPasswordComplete = (TextView) findViewById(R.id.tv_set_password_complete);

        btnCloseNewPassword.setOnClickListener(this);
        btnCloseConfirmPassword.setOnClickListener(this);
        tvSetPasswordComplete.setOnClickListener(this);

        etNewPassword.addTextChangedListener(new TextWatcher() {
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
                    btnCloseNewPassword.setVisibility(View.GONE);
                } else {
                    btnCloseNewPassword.setVisibility(View.VISIBLE);
                }
            }
        });

        etConfirmPassword.addTextChangedListener(new TextWatcher() {
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
                    btnCloseConfirmPassword.setVisibility(View.GONE);
                } else {
                    btnCloseConfirmPassword.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_close_new_password:
                etNewPassword.setText("");
                break;
            case R.id.btn_close_confirm_password:
                etConfirmPassword.setText("");
                break;
            case R.id.tv_set_password_complete:
                String newPassword = etNewPassword.getText().toString().trim();
                String confirmPassword = etConfirmPassword.getText().toString().trim();

                if (TextUtils.isEmpty(newPassword)) {
                    Toast.makeText(this, "请输入新密码!", Toast.LENGTH_SHORT).show();

                } else if (TextUtils.isEmpty(confirmPassword)) {
                    Toast.makeText(this, "请确认新密码!", Toast.LENGTH_SHORT).show();

                } else if (!newPassword.equals(confirmPassword)) {  //两次输入的密码不一致
                    Toast.makeText(this, "两次输入的密码不一致!", Toast.LENGTH_SHORT).show();

                } else if (newPassword.length() < 6 || newPassword.length() > 16) {
                    Toast.makeText(this, "密码长度必须再6至16位之间!", Toast.LENGTH_SHORT).show();

                } else {
                    gotoSetNewPassword(newPassword);
                }
                break;
            default:
                break;
        }
    }

    private void gotoSetNewPassword(String newPassword) {

        String md5NewPassword = MD5Utils.getMd5(newPassword);

        OkHttpUtils
                .post()
                .url(GlobalConstants.URL_SET_NEW_PWD)
                .addParams("phoneNumber", mPhoneNumber)
                .addParams("password", md5NewPassword)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        System.out.println("--------------修改密码,网络出现异常---------------");
                        e.printStackTrace();
                        System.out.println("------------------------------------------------");
                        Toast.makeText(SetNewPasswordActivity.this, "网络出现异常,请重试!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        System.out.println("-------------修改密码网络连接成功-----------[" + response + "]-------------");

                        try {
                            JSONObject jo = new JSONObject(response);
                            int status = jo.getInt("status");
                            String message = jo.getString("message");

                            switch (status) {
                                case 201:  //新密码与当前密码相同
                                case 203:  //密码修改失败
                                case 204:  //新密码为空
                                case 205:   //用户不存在
                                    Toast.makeText(SetNewPasswordActivity.this, message, Toast.LENGTH_SHORT).show();
                                    System.out.println(message);
                                    break;
                                case 202:  //密码修改成功
                                    Toast.makeText(SetNewPasswordActivity.this, message, Toast.LENGTH_SHORT).show();
                                    System.out.println(message);

                                    //密码修改成功,删除本地用户数据,让用户重新登录
                                    SPUtils.remove(SetNewPasswordActivity.this, GlobalConstants.isLoginState);
                                    SPUtils.remove(SetNewPasswordActivity.this, GlobalConstants.phoneNumber);
                                    SPUtils.remove(SetNewPasswordActivity.this, GlobalConstants.password);
                                    SPUtils.remove(SetNewPasswordActivity.this, GlobalConstants.userID);
                                    SPUtils.remove(SetNewPasswordActivity.this, GlobalConstants.USER_NAME);
                                    SPUtils.remove(SetNewPasswordActivity.this, GlobalConstants.USER_HEAD_IMAGE);

                                    //跳到登录页面
                                    startActivity(new Intent(SetNewPasswordActivity.this, LoginActivity.class));
                                    finish();
                                    break;
                                default:
                                    Toast.makeText(SetNewPasswordActivity.this, "修改密码出现未知异常!", Toast.LENGTH_SHORT).show();
                                    System.out.println("修改密码出现未知异常!");
                                    break;
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(SetNewPasswordActivity.this, "解析数据失败!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });


    }
}
