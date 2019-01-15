package com.xasfemr.meiyaya.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xasfemr.meiyaya.R;
import com.xasfemr.meiyaya.main.BaseActivity;
import com.xasfemr.meiyaya.utils.PackageUtils;

public class MyAboutMeiyayaActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "MyAboutMeiyayaActivity";

    private TextView       tvMeiyayaVersionName;
    private RelativeLayout rlMyCheckNewVersion;
    private RelativeLayout rlMyLikeOurs;
    private RelativeLayout rlMyAboutOurs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_about_meiyaya);
        initTopBar();
        setTopTitleText("关于美页圈");

        tvMeiyayaVersionName = (TextView) findViewById(R.id.tv_meiyaya_version_name);
        rlMyCheckNewVersion = (RelativeLayout) findViewById(R.id.rl_my_check_new_version);
        rlMyLikeOurs = (RelativeLayout) findViewById(R.id.rl_my_like_ours);
        rlMyAboutOurs = (RelativeLayout) findViewById(R.id.rl_my_about_ours);

        String versionName = PackageUtils.getVersionName(this);
        tvMeiyayaVersionName.setText("美页圈 V"+versionName);

        rlMyCheckNewVersion.setOnClickListener(this);
        rlMyLikeOurs.setOnClickListener(this);
        rlMyAboutOurs.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_my_check_new_version:  //检查新版本
                Toast.makeText(this, "当前已是最新版本", Toast.LENGTH_SHORT).show();
                break;
            case R.id.rl_my_like_ours:  //喜欢我们
                Toast.makeText(this, "喜欢我们", Toast.LENGTH_SHORT).show();
                break;
            case R.id.rl_my_about_ours:  //关于我们
                Intent intent = new Intent(MyAboutMeiyayaActivity.this, MyAboutOursActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }
}
