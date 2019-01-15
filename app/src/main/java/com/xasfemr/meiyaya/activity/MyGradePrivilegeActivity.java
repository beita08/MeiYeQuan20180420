package com.xasfemr.meiyaya.activity;

import android.os.Bundle;
import android.webkit.WebView;

import com.xasfemr.meiyaya.R;
import com.xasfemr.meiyaya.global.GlobalConstants;
import com.xasfemr.meiyaya.main.BaseActivity;

public class MyGradePrivilegeActivity extends BaseActivity {

    private WebView webViewPrivilege;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_grade_privilege);
        initTopBar();
        setTopTitleText("等级规则");

        webViewPrivilege = (WebView) findViewById(R.id.web_view_privilege);
        webViewPrivilege.loadUrl(GlobalConstants.H5_GRADE_PRIVILEGE);
    }
}
