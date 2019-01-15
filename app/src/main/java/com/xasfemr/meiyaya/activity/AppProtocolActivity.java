package com.xasfemr.meiyaya.activity;

import android.os.Bundle;
import android.webkit.WebView;

import com.xasfemr.meiyaya.R;
import com.xasfemr.meiyaya.global.GlobalConstants;
import com.xasfemr.meiyaya.main.BaseActivity;

public class AppProtocolActivity extends BaseActivity {

    private WebView webViewProtocol;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_protocol);
        initTopBar();
        setTopTitleText("App服务协议");

        webViewProtocol = (WebView) findViewById(R.id.web_view_protocol);
        webViewProtocol.loadUrl(GlobalConstants.H5_APP_PROTOCOL);
    }
}
