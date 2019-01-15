package com.xasfemr.meiyaya.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.xasfemr.meiyaya.R;
import com.xasfemr.meiyaya.bean.EditUserInfoData;
import com.xasfemr.meiyaya.global.GlobalConstants;
import com.xasfemr.meiyaya.main.BaseActivity;
import com.xasfemr.meiyaya.utils.SPUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import okhttp3.Call;

public class UserPagerEditNameActivity extends BaseActivity {
    private static final String TAG = "UserPagerEditName";

    private Intent    mIntent;
    private TextView  tvTopRight;
    private EditText  etNewNickname;
    private ImageView ivCloseNickname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_pager_edit_name);
        initTopBar();
        setTopTitleText("编辑昵称");

        tvTopRight = (TextView) findViewById(R.id.tv_top_right);
        etNewNickname = (EditText) findViewById(R.id.et_new_nickname);
        ivCloseNickname = (ImageView) findViewById(R.id.iv_close_nickname);

        mIntent = getIntent();
        String nickName = mIntent.getStringExtra("NickName");
        etNewNickname.setText(nickName);
        etNewNickname.setSelection(nickName.length());
        if (!TextUtils.isEmpty(nickName)) {
            ivCloseNickname.setVisibility(View.VISIBLE);
        }
        initView();
    }

    private void initView() {
        tvTopRight.setVisibility(View.VISIBLE);
        tvTopRight.setText("保存");
        tvTopRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newNickname = etNewNickname.getText().toString().trim();
                if (TextUtils.isEmpty(newNickname)) {
                    Toast.makeText(UserPagerEditNameActivity.this, "请填入您的昵称", Toast.LENGTH_SHORT).show();
                } else {
                    Log.i(TAG, "onClick: newNickname.length() = " + newNickname.length());
                    if (newNickname.length() > 15) {
                        Toast.makeText(UserPagerEditNameActivity.this, "您的昵称太长了", Toast.LENGTH_SHORT).show();
                    } else {
                        gotoSaveNickname(newNickname);
                    }
                }
            }
        });

        etNewNickname.addTextChangedListener(new TextWatcher() {
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
                    ivCloseNickname.setVisibility(View.GONE);
                } else {
                    ivCloseNickname.setVisibility(View.VISIBLE);
                }
            }
        });

        ivCloseNickname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etNewNickname.setText("");
            }
        });
    }

    private void gotoSaveNickname(final String newNickname) {
        String mUserId = SPUtils.getString(UserPagerEditNameActivity.this, GlobalConstants.userID, "");

        OkHttpUtils.post().url(GlobalConstants.URL_EDIT_MY_INFO)
                .addParams("id", mUserId)
                .addParams("username", newNickname)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.i(TAG, "onError: ---网络异常,编辑用户名失败---");
                        Toast.makeText(UserPagerEditNameActivity.this, "网络异常,编辑用户名失败", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.i(TAG, "onResponse: ---编辑用户名成功---response = " + response + "---");

                        try {
                            parserEditNicknameJson(response);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    private void parserEditNicknameJson(String response) {
        Gson gson = new Gson();
        EditUserInfoData editUserInfoData = gson.fromJson(response, EditUserInfoData.class);

        switch (editUserInfoData.status) {
            case 201:
            case 203:
            case 204:
            case 205:
                Toast.makeText(this, editUserInfoData.message, Toast.LENGTH_SHORT).show();
                break;

            case 202:
                Toast.makeText(this, editUserInfoData.message, Toast.LENGTH_SHORT).show();
                mIntent.putExtra("NewNickName", editUserInfoData.data.username);
                setResult(23, mIntent);
                finish();
                break;

            default:
                break;
        }
    }
}
