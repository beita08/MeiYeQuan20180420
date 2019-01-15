package com.xasfemr.meiyaya.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
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

public class PasswordChangeActivity extends BaseActivity {
    private static final String TAG = "PasswordChangeActivity";

    private EditText etCurrentPassword;
    private EditText etNewPassword;
    private EditText etNewPasswordConfirm;
    private TextView btnConfirmPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_change);
        initTopBar();
        setTopTitleText("修改密码");

        etCurrentPassword = (EditText) findViewById(R.id.et_current_password);
        etNewPassword = (EditText) findViewById(R.id.et_new_password);
        etNewPasswordConfirm = (EditText) findViewById(R.id.et_new_password_confirm);
        btnConfirmPassword = (TextView) findViewById(R.id.btn_confirm_password);

        btnConfirmPassword.setOnClickListener(v -> changePassword());
    }

    private void changePassword() {
        String currentPassword = etCurrentPassword.getText().toString().trim();
        String newPassword = etNewPassword.getText().toString().trim();
        String newPasswordConfirm = etNewPasswordConfirm.getText().toString().trim();

        if (TextUtils.isEmpty(currentPassword)) {
            Toast.makeText(this, "请输入当前密码", Toast.LENGTH_SHORT).show();

        } else if (TextUtils.isEmpty(newPassword)) {
            Toast.makeText(this, "请输入新密码!", Toast.LENGTH_SHORT).show();

        } else if (TextUtils.isEmpty(newPasswordConfirm)) {
            Toast.makeText(this, "请确认新密码!", Toast.LENGTH_SHORT).show();

        } else if (!TextUtils.equals(newPassword, newPasswordConfirm)) { //两次输入的密码不一致
            Toast.makeText(this, "两次输入的新密码不一致!", Toast.LENGTH_SHORT).show();

        } else if (TextUtils.equals(currentPassword, newPassword)) {
            Toast.makeText(this, "新密码与当前密码相同", Toast.LENGTH_SHORT).show();

        } else if (newPassword.length() < 6 || newPassword.length() > 16) {
            Toast.makeText(this, "新密码长度必须再6至16位之间!", Toast.LENGTH_SHORT).show();

        } else {
            //先MD5加密
            String md5CurrentPassword = MD5Utils.getMd5(currentPassword);
            String md5NewPassword = MD5Utils.getMd5(newPassword);
            gotoChangePassword(md5CurrentPassword, md5NewPassword);
        }
    }

    private void gotoChangePassword(String md5CurrentPassword, String md5NewPassword) {
        btnConfirmPassword.setEnabled(false);
        String mUserId = SPUtils.getString(this, GlobalConstants.userID, "");
        String mPhoneNumber = SPUtils.getString(this, GlobalConstants.phoneNumber, "");

        OkHttpUtils.get().url(GlobalConstants.URL_CHANGE_PASSWORD)
                .addParams("phoneNumber", mPhoneNumber)
                .addParams("oldpassword", md5CurrentPassword)
                .addParams("password", md5NewPassword)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.i(TAG, "onError: ---网络异常,修改密码失败---");
                        btnConfirmPassword.setEnabled(true);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.i(TAG, "onResponse: ---修改密码访问服务器成功---response = " + response + "---");
                        btnConfirmPassword.setEnabled(true);

                        try {
                            JSONObject jo = new JSONObject(response);
                            int status = jo.getInt("status");
                            String message = jo.getString("message");

                            switch (status) {
                                case 201:  //新密码与当前密码相同
                                case 203:  //密码修改失败
                                case 204:  //当前密码不正确
                                case 205:   //用户不存在
                                    Toast.makeText(PasswordChangeActivity.this, message, Toast.LENGTH_SHORT).show();
                                    Log.i(TAG, "onResponse: message = " + message);
                                    break;
                                case 202:  //密码修改成功
                                    Toast.makeText(PasswordChangeActivity.this, message+"请重新登录", Toast.LENGTH_SHORT).show();
                                    Log.i(TAG, "onResponse: message = " + message);

                                    //密码修改成功,删除本地用户数据,让用户重新登录
                                    SPUtils.remove(PasswordChangeActivity.this, GlobalConstants.isLoginState);
                                    SPUtils.remove(PasswordChangeActivity.this, GlobalConstants.phoneNumber);
                                    SPUtils.remove(PasswordChangeActivity.this, GlobalConstants.password);
                                    SPUtils.remove(PasswordChangeActivity.this, GlobalConstants.userID);
                                    SPUtils.remove(PasswordChangeActivity.this, GlobalConstants.USER_NAME);
                                    SPUtils.remove(PasswordChangeActivity.this, GlobalConstants.USER_HEAD_IMAGE);

                                    //跳到登录页面
                                    startActivity(new Intent(PasswordChangeActivity.this, LoginActivity.class));
                                    finish();
                                    break;
                                default:
                                    Toast.makeText(PasswordChangeActivity.this, "修改密码出现未知异常!", Toast.LENGTH_SHORT).show();
                                    System.out.println("修改密码出现未知异常!");
                                    break;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(PasswordChangeActivity.this, "解析数据失败!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }
}
