package com.xasfemr.meiyaya.activity;

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
import com.xasfemr.meiyaya.utils.LogUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.Call;

public class MySuggestionFeedbackActivity extends BaseActivity {
    private static final String TAG = "MySuggestionFeedback";

    private EditText etUserSuggestion;
    private TextView tvSuggestionLimit;
    private EditText etPhoneNumber;
    private Button   btnSubmitSuggestion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_suggestion_feedback);
        initTopBar();
        setTopTitleText("意见反馈");

        etUserSuggestion = (EditText) findViewById(R.id.et_user_suggestion);
        tvSuggestionLimit = (TextView) findViewById(R.id.tv_suggestion_limit);
        etPhoneNumber = (EditText) findViewById(R.id.et_phone_number);
        btnSubmitSuggestion = (Button) findViewById(R.id.btn_submit_suggestion);

        btnSubmitSuggestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitSuggestion();
            }
        });

        etUserSuggestion.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                tvSuggestionLimit.setText(s.length() + "/150");
            }
        });
    }

    private void submitSuggestion() {
        String userSuggestion = etUserSuggestion.getText().toString().trim();
        String phoneNumber = etPhoneNumber.getText().toString().trim();
        userSuggestion = removeEnterKey(userSuggestion);

        if (TextUtils.isEmpty(userSuggestion)) {
            Toast.makeText(this, "请输入您的反馈意见", Toast.LENGTH_SHORT).show();

        } else if (TextUtils.isEmpty(phoneNumber)) {
            Toast.makeText(this, "手机号不能为空", Toast.LENGTH_SHORT).show();

        } else if (!phoneNumber.matches(GlobalConstants.STR_PHONE_REGEX2)) {
            Toast.makeText(this, "请输入正确的手机号", Toast.LENGTH_SHORT).show();

        } else {
            gotoSubmitSuggestion(userSuggestion, phoneNumber);
        }
    }

    private void gotoSubmitSuggestion(String userSuggestion, String phoneNumber) {

        OkHttpUtils.post().url(GlobalConstants.URL_SUGGESTION_FEEDBACK)
                .addParams("phone", phoneNumber)
                .addParams("feed", userSuggestion)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        LogUtils.show(TAG, "---提交反馈,网络异常---");
                        Toast.makeText(MySuggestionFeedbackActivity.this, "网络出现异常,请重试", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        LogUtils.show(TAG, "---提交反馈网络访问成功---response = " + response + " ---");

                        try {
                            JSONObject jo = new JSONObject(response);
                            int status = jo.getInt("status");
                            String message = jo.getString("message");
                            switch (status) {
                                case 200: //提交成功
                                    Toast.makeText(MySuggestionFeedbackActivity.this, message, Toast.LENGTH_SHORT).show();
                                    finish();
                                    break;

                                case 204: //提交失败
                                    Toast.makeText(MySuggestionFeedbackActivity.this, message + ",我们会尽快处理", Toast.LENGTH_SHORT).show();
                                    break;

                                default:
                                    Toast.makeText(MySuggestionFeedbackActivity.this, "出现未知异常!", Toast.LENGTH_SHORT).show();
                                    break;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(MySuggestionFeedbackActivity.this, "解析数据异常", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    //多个回车键换行连续的话,只保留一个
    private String removeEnterKey(String dynamicContent) {
        ArrayList<Integer> indexList = new ArrayList<>();

        for (int i = 1; i < dynamicContent.length(); i++) {
            if (dynamicContent.charAt(i) == '\n' && dynamicContent.charAt(i - 1) == '\n') {
                indexList.add(i);
            }
        }

        for (int i = indexList.size() - 1; i >= 0; i--) {
            dynamicContent = removeChar(indexList.get(i), dynamicContent);
        }
        return dynamicContent;
    }

    private String removeChar(int index, String Str) {
        Str = Str.substring(0, index) + Str.substring(index + 1, Str.length());//substring的取值范围是:[,)
        return Str;
    }
}
