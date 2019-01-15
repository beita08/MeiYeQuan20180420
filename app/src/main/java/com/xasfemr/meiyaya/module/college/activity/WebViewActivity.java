package com.xasfemr.meiyaya.module.college.activity;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.xasfemr.meiyaya.R;
import com.xasfemr.meiyaya.activity.LoginActivity;
import com.xasfemr.meiyaya.application.MyApplication;
import com.xasfemr.meiyaya.base.activity.MVPBaseActivity;
import com.xasfemr.meiyaya.global.GlobalConstants;
import com.xasfemr.meiyaya.module.college.presenter.SharePresenter;
import com.xasfemr.meiyaya.module.college.view.ShareSuccessIView;
import com.xasfemr.meiyaya.module.mine.protocol.Shareprotocol;
import com.xasfemr.meiyaya.utils.LogUtils;
import com.xasfemr.meiyaya.utils.NetUtils;
import com.xasfemr.meiyaya.utils.SPUtils;
import com.xasfemr.meiyaya.utils.ToastUtil;
import com.xasfemr.meiyaya.utils.ViewStatus;
import com.xasfemr.meiyaya.view.BasicShareDialog;
import com.xasfemr.meiyaya.view.LoadDataView;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;
import org.simple.eventbus.ThreadMode;

import java.util.HashMap;

import butterknife.BindView;

/**
 * 统一处理资讯、新闻、H5 页面
 * 此页面需传 URl、Title
 * 此页面无JS交互功能
 * Created by sen.luo on 2017/11/28.
 */

public class WebViewActivity extends MVPBaseActivity{

    @BindView(R.id.tv_top_title)
    TextView tvTitle;
    @BindView(R.id.iv_top_back)
    ImageView ivBack;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.layout_root)
    LinearLayout layoutRoot;
    @BindView(R.id.web_view)
    WebView webView;

    @BindView(R.id.iv_top_search)
    ImageView ivShare;

    private boolean isNews=false;

    private String urlPath;
    private String title;
    private String dev; //分享简介
    private String image; //分享图片
    private LoadDataView loadDataView;

    private Shareprotocol shareprotocol;
    private String urlId="";
    private String shareStatus=""; //分享状态    1 资讯分享    2 直播间分享
    private SharePresenter sharePresenter;

    @Override
    protected int layoutId() {
        return R.layout.activity_webview;
    }

    @Override
    protected ViewGroup loadDataViewLayout() {
        return layoutRoot;
    }

    @Override
    protected void initView() {
        EventBus.getDefault().register(this);

        ivBack.setOnClickListener(v -> finish());

        if (!TextUtils.isEmpty(getIntent().getStringExtra("url"))){
            urlPath=getIntent().getStringExtra("url");
        }

        if (!TextUtils.isEmpty(getIntent().getStringExtra("title"))){
            title=getIntent().getStringExtra("title");
        }

        if (!TextUtils.isEmpty(getIntent().getStringExtra("dev"))){
            dev=getIntent().getStringExtra("dev");
        }

        if (!TextUtils.isEmpty(getIntent().getStringExtra("image"))){
            image=getIntent().getStringExtra("image");
        }

        if (!TextUtils.isEmpty(getIntent().getStringExtra("url_id"))){
            urlId=getIntent().getStringExtra("url_id");
        }

        if (!TextUtils.isEmpty(getIntent().getStringExtra("share_status"))){
            shareStatus=getIntent().getStringExtra("share_status");
        }

        tvTitle.setText(title);
        isNews=getIntent().getBooleanExtra("news",false);

        if (isNews){
            ivShare.setVisibility(View.VISIBLE);
            ivShare.setImageDrawable(getResources().getDrawable(R.drawable.icon_gray_share));
        }else {
            ivShare.setVisibility(View.GONE);
        }

        ivShare.setOnClickListener(v -> getShare());
        shareprotocol=new Shareprotocol();

    }


    @Override
    protected void initData() {

        webView.setWebChromeClient(new BasicdWebChromeClient());
        webView.getSettings().setJavaScriptEnabled(true);

        if (webView!=null){
            if (!NetUtils.isConnected(MyApplication.getIns())) {
                loadDataView.changeStatusView(ViewStatus.NOTNETWORK);
                return;
            }
            if (isNews){
                webView.loadUrl(urlPath+"&id="+urlId);
                LogUtils.show("打开的url",urlPath+"&id="+urlId);
            }else {
                webView.loadUrl(urlPath);
                LogUtils.show("打开的url不拼参数",urlPath);
            }

            webView.clearHistory();
        }

        
        putHitsData();
        
    }

    private void putHitsData() {

    }

    @Override
    protected void getLoadView(LoadDataView loadView) {
        this.loadDataView=loadView;
        this.loadDataView.setErrorListner(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadDataView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        initData();
                    }
                }, 100);
            }
        });

    }

    @Override
    protected void initPresenter() {
        sharePresenter=new SharePresenter();
    }



    private class BasicdWebChromeClient extends WebChromeClient {

        @Override
        public void onReceivedTitle(WebView view, String showTitle) {
            super.onReceivedTitle(view, showTitle);
            if (TextUtils.isEmpty(title)) {
                tvTitle.setText(title);
            }
        }

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            if (null != progressBar) {
                if (newProgress == 100) {
                    progressBar.setVisibility(View.GONE);
                    loadDataView.changeStatusView(ViewStatus.SUCCESS);
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    progressBar.setProgress(newProgress);
                }
            }
            super.onProgressChanged(view, newProgress);
        }
    }


    /**
     * 分享
     */

    private void getShare() {

        if (TextUtils.isEmpty(SPUtils.getString(this, GlobalConstants.userID, ""))) {
            startActivity(new Intent(this, LoginActivity.class));
        } else {
            shareprotocol.sharetitle=title;
            shareprotocol.shareUrl=urlPath+"&id="+urlId+"&share=0";
            shareprotocol.shareImage=image;
            shareprotocol.shareMsg=dev;
            shareprotocol.shareCid=urlId;
            shareprotocol.shareStatus=shareStatus;
            BasicShareDialog shareDialog =new BasicShareDialog(this,shareprotocol);
            shareDialog.show();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        sharePresenter.destroy();
    }


    @Subscriber(mode = ThreadMode.MAIN,tag = GlobalConstants.EventBus.SHARE_NEWS)
    public void getShareStatus(String status){
        LogUtils.show("分享回调",status);

        if (sharePresenter!=null){

            HashMap<String,String >map= new HashMap<>();
            map.put("uid",SPUtils.getString(this,GlobalConstants.userID,""));
            map.put("information_id",urlId);
            sharePresenter.getNewsShare(map, new ShareSuccessIView() {
                @Override
                public void getShareSuccess(String message) {
                    ToastUtil.showShort(WebViewActivity.this,message);
                }

                @Override
                public void getShareOnfaile(String msg) {
                    ToastUtil.showShort(WebViewActivity.this,msg);
                }

                @Override
                public void onNetworkFailure(String message) {
                    ToastUtil.showShort(WebViewActivity.this,message);
                }
            });
        }
    }

}
