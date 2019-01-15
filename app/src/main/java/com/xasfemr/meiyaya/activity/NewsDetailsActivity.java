package com.xasfemr.meiyaya.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.Toast;

import com.xasfemr.meiyaya.R;
import com.xasfemr.meiyaya.main.BaseActivity;

public class NewsDetailsActivity extends BaseActivity {
    private static final String TAG = "NewsDetailsActivity";

    private ImageView ivTopRight;
    private WebView   webViewNewsDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_details);
        initTopBar();
        setTopTitleText("资讯详情");
        initTopRightImg();

        Intent intent = getIntent();
        String newsDetailsUrl = intent.getStringExtra("NEWS_DETAILS_URL");
        Log.i(TAG, "onCreate: newsDetailsUrl = " + newsDetailsUrl + " ---");

        webViewNewsDetails = (WebView) findViewById(R.id.web_view_news_details);
        webViewNewsDetails.loadUrl(newsDetailsUrl);
    }

    private void initTopRightImg() {
        ivTopRight = (ImageView) findViewById(R.id.iv_top_search);
        ivTopRight.setVisibility(View.VISIBLE);
        ivTopRight.setImageResource(R.drawable.news_share);

        ivTopRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //分享资讯详情
                shareNewsDetail();
            }
        });
    }

    private void shareNewsDetail() {
        Toast.makeText(NewsDetailsActivity.this, "分享资讯详情", Toast.LENGTH_SHORT).show();

    }
}
