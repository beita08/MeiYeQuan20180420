package com.xasfemr.meiyaya.activity;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.xasfemr.meiyaya.R;
import com.xasfemr.meiyaya.global.GlobalConstants;
import com.xasfemr.meiyaya.main.BaseActivity;
import com.xasfemr.meiyaya.utils.LogUtils;
import com.xasfemr.meiyaya.utils.SPUtils;

import java.util.HashMap;
import java.util.Map;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.tencent.qzone.QZone;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;

public class MyInvitationCodeActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "MyInvitationCodeActivity";

    private StringBuffer strInvitationCode;
    private ImageView    ivMyUserIcon;
    private TextView     tvMyInvitationCode;
    private TextView     tvShareFriendsCircle;
    private TextView     tvShareWechat;
    private TextView     tvShareQQ;
    private TextView     tvShareWeibo;
    private TextView     tvShareQzone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_invitation_code);
        initTopBar();
        setTopTitleText("分享美页圈");

        ivMyUserIcon = (ImageView) findViewById(R.id.iv_my_user_icon);
        tvMyInvitationCode = (TextView) findViewById(R.id.tv_my_invitation_code);
        tvShareFriendsCircle = (TextView) findViewById(R.id.tv_share_friends_circle);
        tvShareWechat = (TextView) findViewById(R.id.tv_share_wechat);
        tvShareQQ = (TextView) findViewById(R.id.tv_share_qq);
        tvShareWeibo = (TextView) findViewById(R.id.tv_share_weibo);
        tvShareQzone = (TextView) findViewById(R.id.tv_share_qzone);

        tvShareFriendsCircle.setOnClickListener(this);
        tvShareWechat.setOnClickListener(this);
        tvShareQQ.setOnClickListener(this);
        tvShareWeibo.setOnClickListener(this);
        tvShareQzone.setOnClickListener(this);


        Intent intent = getIntent();
        String invitationCode = intent.getStringExtra("InvitationCode");
        String userName = intent.getStringExtra("USER_NAME");
        String userIcon = intent.getStringExtra("USER_ICON");
        Glide.with(MyInvitationCodeActivity.this).load(userIcon).into(ivMyUserIcon);

        strInvitationCode = new StringBuffer(invitationCode);
        for (int i = strInvitationCode.length(); i < 6; i++) {
            strInvitationCode.insert(0, '0');
        }
        tvMyInvitationCode.setText(strInvitationCode);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_share_friends_circle:  //朋友圈分享
                shareFriendsCircle();
                break;
            case R.id.tv_share_wechat: //微信分享
                shareWechat();
                break;
            case R.id.tv_share_qq:     //QQ分享
                shareQQ();
                break;
            case R.id.tv_share_weibo:  //微博分享
                shareWeibo();
                break;
            case R.id.tv_share_qzone:  //QQ空间分享
                shareQZone();
                break;
            default:
                break;
        }
    }

    /*
    1.初始化shareSDK,在分享的最开始需要进行初始化。
            ShareSDK.initSDK(this);
    2.设置分享内容（以新浪微博为例子）
            ShareParams sp = new ShareParams();
            sp.setText(“测试分享的文本”);
            sp.setImagePath(“/mnt/sdcard/测试分享的图片.jpg”);
    3.获取平台对象
            Platform weibo = ShareSDK.getPlatform(SinaWeibo.NAME);
    4.设置结果回调
            weibo.setPlatformActionListener(new PlatformActionListener() {}); // 设置分享事件回调
    5.执行分享
            weibo.share(sp);
    */

    private void shareFriendsCircle() {
        String mUserId = SPUtils.getString(MyInvitationCodeActivity.this, GlobalConstants.userID, "");

        Platform.ShareParams sp = new Platform.ShareParams();
        sp.setShareType(Platform.SHARE_WEBPAGE);
        sp.setTitle("美页圈APP：邀请码");
        //sp.setTitleUrl("http://www.xasfemr.com/"); // 标题的超链接
        sp.setText("我的专属邀请码：" + strInvitationCode);
        sp.setImageData(BitmapFactory.decodeResource(getResources(), R.mipmap.meiyaya_logo2_rectangle));
        sp.setUrl(GlobalConstants.H5_SHARE_MEIYAYA + "&id=" + mUserId);
        //sp.setImageUrl(GlobalConstants.YAYA_PIC);
        //sp.setSite("美页圈网站名称");
        //sp.setSiteUrl("http://www.xasfemr.com/");

        Platform wechatMoments = ShareSDK.getPlatform(WechatMoments.NAME);
        // 设置分享事件回调（注：回调放在不能保证在主线程调用，不可以在里面直接处理UI操作）
        wechatMoments.setPlatformActionListener(new MyyPlatformActionListener());
        // 执行图文分享
        wechatMoments.share(sp);
        Toast.makeText(this, "正在开启朋友圈", Toast.LENGTH_SHORT).show();
    }

    private void shareWechat() {
        String mUserId = SPUtils.getString(MyInvitationCodeActivity.this, GlobalConstants.userID, "");

        Platform.ShareParams sp = new Platform.ShareParams();
        sp.setShareType(Platform.SHARE_WEBPAGE);
        sp.setTitle("美页圈APP：邀请码");
        //sp.setTitleUrl("http://www.xasfemr.com/"); // 标题的超链接
        sp.setText("我的专属邀请码：" + strInvitationCode);
        sp.setImageData(BitmapFactory.decodeResource(getResources(), R.mipmap.meiyaya_logo2_rectangle));
        sp.setUrl(GlobalConstants.H5_SHARE_MEIYAYA + "&id=" + mUserId);
        //sp.setImageUrl(GlobalConstants.YAYA_PIC);
        //sp.setSite("美页圈网站名称");
        //sp.setSiteUrl("http://www.xasfemr.com/");

        Platform wechat = ShareSDK.getPlatform(Wechat.NAME);
        // 设置分享事件回调（注：回调放在不能保证在主线程调用，不可以在里面直接处理UI操作）
        wechat.setPlatformActionListener(new MyyPlatformActionListener());
        // 执行图文分享
        wechat.share(sp);
        Toast.makeText(this, "正在开启微信", Toast.LENGTH_SHORT).show();

    }

    //分享至QQ
    private void shareQQ() {
        String mUserId = SPUtils.getString(MyInvitationCodeActivity.this, GlobalConstants.userID, "");

        Platform.ShareParams sp = new Platform.ShareParams();
        sp.setTitle("美页圈APP：邀请码");
        sp.setText("我的专属邀请码：" + strInvitationCode);
        sp.setImageUrl(GlobalConstants.PIC_MEIYAYA_LOGO_RECTANGLE);
        sp.setTitleUrl(GlobalConstants.H5_SHARE_MEIYAYA + "&id=" + mUserId);

        Platform qq = ShareSDK.getPlatform(QQ.NAME);
        qq.setPlatformActionListener(new MyyPlatformActionListener());
        qq.share(sp);
        Toast.makeText(this, "正在开启QQ", Toast.LENGTH_SHORT).show();
    }

    //分享至微博
    private void shareWeibo() {
        String mUserId = SPUtils.getString(MyInvitationCodeActivity.this, GlobalConstants.userID, "");

        Platform.ShareParams sp = new Platform.ShareParams();
        sp.setText("美页圈APP：邀请码 \n我的专属邀请码：" + strInvitationCode + "\n" + GlobalConstants.H5_SHARE_MEIYAYA + "&id=" + mUserId);
        sp.setImageData(BitmapFactory.decodeResource(getResources(), R.mipmap.meiyaya_logo2_rectangle));
        //sp.setImagePath(“/mnt/sdcard/测试分享的图片.jpg”);
        //sp.setImageUrl(GlobalConstants.YAYA_PIC);
        //sp.setUrl("http://www.xasfemr.com/");

        Platform weibo = ShareSDK.getPlatform(SinaWeibo.NAME);
        weibo.setPlatformActionListener(new MyyPlatformActionListener()); // 设置分享事件回调
        // 执行图文分享
        weibo.share(sp);
        Toast.makeText(this, "正在开启微博", Toast.LENGTH_SHORT).show();
    }

    //分享至QQ空间
    private void shareQZone() {
        String mUserId = SPUtils.getString(MyInvitationCodeActivity.this, GlobalConstants.userID, "");

        Platform.ShareParams sp = new Platform.ShareParams();
        sp.setTitle("美页圈APP：邀请码");                     //title：最多200个字符
        sp.setTitleUrl(GlobalConstants.H5_SHARE_MEIYAYA + "&id=" + mUserId); // 标题的超链接
        sp.setText("我的专属邀请码：" + strInvitationCode);   //text：最多600个字符
        //sp.setImageData(BitmapFactory.decodeResource(getResources(), R.mipmap.meiyaya_logo2_rectangle));
        sp.setImageUrl(GlobalConstants.PIC_MEIYAYA_LOGO_RECTANGLE);
        sp.setSite("美页圈官网");
        sp.setSiteUrl("http://www.xasfemr.com/");

        Platform qzone = ShareSDK.getPlatform(QZone.NAME);
        // 设置分享事件回调（注：回调放在不能保证在主线程调用，不可以在里面直接处理UI操作）
        qzone.setPlatformActionListener(new MyyPlatformActionListener());
        // 执行图文分享
        qzone.share(sp);
        Toast.makeText(this, "正在开启QQ空间", Toast.LENGTH_SHORT).show();

    }

    private class MyyPlatformActionListener implements PlatformActionListener {
        @Override
        public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
            //Toast.makeText(MyInvitationCodeActivity.this, "成功分享至" + platform.getName(), Toast.LENGTH_SHORT).show();
            toastShareResult("成功分享至", platform.getName());

            //操作成功，在这里可以做后续的步骤
            //这里需要说明的一个参数就是HashMap<String, Object> hashMap
            //这个参数在你进行登录操作的时候里面会保存有用户的数据，例如用户名之类的。
            for (Map.Entry<String, Object> entry : hashMap.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                LogUtils.show(TAG, "key = " + key + "value = " + value.toString());
            }
        }

        @Override
        public void onError(Platform platform, int i, Throwable throwable) {
            //Toast.makeText(MyInvitationCodeActivity.this, "分享至" + platform.getName() + "出现错误", Toast.LENGTH_SHORT).show();
            toastShareResult("分享出现错误", platform.getName());
            //操作失败啦，打印提供的错误，方便调试
            throwable.printStackTrace();
        }

        @Override
        public void onCancel(Platform platform, int i) {
            //用户取消操作
            //Toast.makeText(MyInvitationCodeActivity.this, "已取消分享至" + platform.getName(), Toast.LENGTH_SHORT).show();
            toastShareResult("已取消分享至", platform.getName());
            //LogUtils.show(TAG, "++++++++ platform.getName() = " + platform.getName());
        }
    }

    //吐司分享结果 2017年12月14日19:29
    private void toastShareResult(String shareResult, String sharePlatform) {

        String platformName = "";
        switch (sharePlatform) {
            case "WechatMoments":
                platformName = "朋友圈";
                break;
            case "Wechat":
                platformName = "微信";
                break;
            case "QQ":
                platformName = "QQ";
                break;
            case "SinaWeibo":
                platformName = "微博";
                break;
            case "QZone":
                platformName = "QQ空间";
                break;
            default:
                break;
        }
        Toast.makeText(this, shareResult + platformName, Toast.LENGTH_SHORT).show();
    }
}
