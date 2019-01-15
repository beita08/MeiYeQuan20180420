package com.xasfemr.meiyaya.activity;

import android.os.Bundle;
import android.webkit.WebView;

import com.xasfemr.meiyaya.R;
import com.xasfemr.meiyaya.global.GlobalConstants;
import com.xasfemr.meiyaya.main.BaseActivity;

public class MyAboutOursActivity extends BaseActivity {

    private WebView webViewAboutOurs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_about_ours);
        initTopBar();
        setTopTitleText("关于我们");

        webViewAboutOurs = (WebView) findViewById(R.id.web_view_about_ours);
        webViewAboutOurs.loadUrl(GlobalConstants.H5_ABOUT_OURS);
    }
}
