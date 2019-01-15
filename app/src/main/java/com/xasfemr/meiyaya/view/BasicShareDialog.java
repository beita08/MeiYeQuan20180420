package com.xasfemr.meiyaya.view;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.xasfemr.meiyaya.R;
import com.xasfemr.meiyaya.global.GlobalConstants;
import com.xasfemr.meiyaya.module.college.activity.WebViewActivity;
import com.xasfemr.meiyaya.module.college.presenter.SharePresenter;
import com.xasfemr.meiyaya.module.college.view.ShareSuccessIView;
import com.xasfemr.meiyaya.module.mine.protocol.Shareprotocol;
import com.xasfemr.meiyaya.neteasecloud.NELivePlayerActivity;
import com.xasfemr.meiyaya.utils.LogUtils;
import com.xasfemr.meiyaya.utils.SPUtils;
import com.xasfemr.meiyaya.utils.ToastUtil;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;
import org.simple.eventbus.ThreadMode;

import java.util.HashMap;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.ShareContentCustomizeCallback;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.tencent.qzone.QZone;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;

/**
 * Description:
 * Company    : shifeier
 * Author     : liuxb
 * Date       : 2017/9/19 0019 15:59
 */

public class BasicShareDialog extends Dialog implements View.OnClickListener {
    private static final String TAG = "BasicShareDialog";

    private Context       mContext;
    private TextView      tvShareFriendsCircle;
    private TextView      tvShareWechat;
    private TextView      tvShareQQ;
    private TextView      tvShareWeibo;
    private TextView      tvShareQzone;
    private Shareprotocol shareprotocol;

    private SharePresenter sharePresenter;
    public BasicShareDialog(@NonNull Context context, Shareprotocol shareprotocol) {

        //指定dialog的样式
        super(context, R.style.ShareDialogStyle);
        this.mContext = context;
        this.shareprotocol = shareprotocol;
        //设置布局
        setContentView(R.layout.dialog_share);
        sharePresenter=new SharePresenter();

        //显示在屏幕正下方
        //原理: 修改dialog所在窗口Window的位置, dialog随窗口显示
        Window window = getWindow();//获取dialog所在的窗口对象
        WindowManager.LayoutParams attributes = window.getAttributes();//获取当前窗口的属性(布局参数)
        attributes.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;//靠下居中显示
        attributes.width = WindowManager.LayoutParams.MATCH_PARENT;     //宽度
        window.setAttributes(attributes);//重新设置布局参数

        initView();
    }

    private void initView() {
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
    }


    @Override
    public void onClick(View v) {

        this.dismiss();

        switch (v.getId()) {
            case R.id.tv_share_friends_circle:  //朋友圈
                shareFriendsCircle();
                break;
            case R.id.tv_share_wechat: //微信
                shareWechat();
                break;
            case R.id.tv_share_qq:     //QQ
                shareQQ();
                break;
            case R.id.tv_share_weibo:  //微博
                shareWeibo();
                break;
            case R.id.tv_share_qzone:  //QQ空间
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

        Platform.ShareParams sp = new Platform.ShareParams();
        sp.setShareType(Platform.SHARE_WEBPAGE);
        sp.setTitle(shareprotocol.sharetitle);
        //sp.setTitleUrl("http://www.xasfemr.com/"); // 标题的超链接
        sp.setText(shareprotocol.shareMsg);
        //sp.setImageData(BitmapFactory.decodeResource(mContext.getResources(),R.drawable.clhk));
        sp.setUrl(shareprotocol.shareUrl);
        sp.setImageUrl(shareprotocol.shareImage);
        //sp.setSite("美页圈网站名称");
        //sp.setSiteUrl("http://www.xasfemr.com/");

        Platform wechatMoments = ShareSDK.getPlatform(WechatMoments.NAME);
        // 设置分享事件回调（注：回调放在不能保证在主线程用，不可以在里面直接处理UI操作）
        wechatMoments.setPlatformActionListener(new MyyPlatformActionListener());
        // 执行图文分享
        wechatMoments.share(sp);
        ToastUtil.showShort(mContext, "正在开启朋友圈");
        //Toast.makeText(mContext, "wechatMoments", Toast.LENGTH_SHORT).show();
    }

    private void shareWechat() {

        Platform.ShareParams sp = new Platform.ShareParams();
        sp.setShareType(Platform.SHARE_WEBPAGE);
        sp.setTitle(shareprotocol.sharetitle);
        //sp.setTitleUrl("http://www.xasfemr.com/"); // 标题的超链接
        sp.setText(shareprotocol.shareMsg);
        //sp.setImageData(BitmapFactory.decodeResource(mContext.getResources(),R.drawable.clhk));
        sp.setUrl(shareprotocol.shareUrl);
        sp.setImageUrl(shareprotocol.shareImage);
        //sp.setImageUrl(GlobalConstants.YAYA_PIC);
        //sp.setSite("美页圈网站名称");
        //sp.setSiteUrl("http://www.xasfemr.com/");

        Platform wechat = ShareSDK.getPlatform(Wechat.NAME);
        // 设置分享事件回调（注：回调放在不能保证在主线程调用，不可以在里面直接处理UI操作）
        wechat.setPlatformActionListener(new MyyPlatformActionListener());
        // 执行图文分享
        wechat.share(sp);
        ToastUtil.showShort(mContext, "正在开启微信");
    }

    //分享至QQ
    private void shareQQ() {
        Platform.ShareParams sp = new Platform.ShareParams();
        sp.setTitle(shareprotocol.sharetitle);
        sp.setText(shareprotocol.shareMsg);
        sp.setImageUrl(shareprotocol.shareImage);
        //sp.setUrl(shareprotocol.shareUrl);
        sp.setTitleUrl(shareprotocol.shareUrl);

        Platform qq = ShareSDK.getPlatform(QQ.NAME);
        qq.setPlatformActionListener(new MyyPlatformActionListener());
        qq.share(sp);
        //Toast.makeText(mContext, "QQ", Toast.LENGTH_SHORT).show();
        ToastUtil.showShort(mContext, "正在开启QQ");
    }

    //分享至微博
    private void shareWeibo() {

        Platform.ShareParams sp = new Platform.ShareParams();
        sp.setText(shareprotocol.sharetitle + "\n" + shareprotocol.shareMsg + "\n" + shareprotocol.shareUrl);
        //sp.setImagePath(“/mnt/sdcard/测试分享的图片.jpg”);
        sp.setImageUrl(shareprotocol.shareImage);
        //sp.setUrl(shareprotocol.shareUrl);

        Platform weibo = ShareSDK.getPlatform(SinaWeibo.NAME);
        weibo.setPlatformActionListener(new MyyPlatformActionListener()); // 设置分享事件回调
        // 执行图文分享
        weibo.share(sp);
        ToastUtil.showShort(mContext, "正在开启微博");
    }

    //分享至QQ空间
    private void shareQZone() {
        Platform.ShareParams sp = new Platform.ShareParams();
        sp.setTitle(shareprotocol.sharetitle);
        sp.setTitleUrl(shareprotocol.shareUrl); // 标题的超链接
        sp.setText(shareprotocol.shareMsg);
        sp.setImageUrl(shareprotocol.shareImage);
        sp.setSite("美页圈网站名称");
        sp.setSiteUrl("http://www.xasfemr.com/");

        Platform qzone = ShareSDK.getPlatform(QZone.NAME);
        // 设置分享事件回调（注：回调放在不能保证在主线程调用，不可以在里面直接处理UI操作）
        qzone.setPlatformActionListener(new MyyPlatformActionListener());
        // 执行图文分享
        qzone.share(sp);
        ToastUtil.showShort(mContext, "正在开启QQ空间");
    }


    private class MyyPlatformActionListener implements PlatformActionListener {
        @Override
        public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
            //Toast.makeText(mContext, "成功分享至" + platform.getName(), Toast.LENGTH_SHORT).show();
//            toastShareResult("成功分享至", platform.getName());
            //操作成功，在这里可以做后续的步骤
            //这里需要说明的一个参数就是HashMap<String, Object> hashMap
            //这个参数在你进行登录操作的时候里面会保存有用户的数据，例如用户名之类的。
            //for (Map.Entry<String, Object> entry : hashMap.entrySet()) {
            //      String key = entry.getKey();
            //      Object value = entry.getValue();
            //      System.out.println("key" + key + "value" + value.toString());
            //}

            LogUtils.show("分享成功","shareStatus"+shareprotocol.shareStatus);

            if (shareprotocol.shareStatus.equals("1")){  //资讯分享
                getShareStatus();
//                EventBus.getDefault().post("1", GlobalConstants.EventBus.SHARE_NEWS);
            }

            if (shareprotocol.shareStatus.equals("2")){  //直播间分享
                getShareLive();
//                EventBus.getDefault().post("2", GlobalConstants.EventBus.SHARE_LIVE);
            }



        }

        @Override
        public void onError(Platform platform, int i, Throwable throwable) {
            toastShareResult("分享出现错误", platform.getName());
            //Toast.makeText(mContext, "分享错误" + platform.getName() + "出现错误", Toast.LENGTH_SHORT).show();
            //操作失败啦，打印提供的错误，方便调试
            throwable.printStackTrace();
        }

        @Override
        public void onCancel(Platform platform, int i) {
            //用户取消操作
            toastShareResult("已取消分享至", platform.getName());
            //Toast.makeText(mContext, "已取消分享至" + platform.getName(), Toast.LENGTH_SHORT).show();
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
        Toast.makeText(mContext, shareResult + platformName, Toast.LENGTH_SHORT).show();
    }



    public void getShareStatus(){

        if (sharePresenter!=null){
            HashMap<String,String >map= new HashMap<>();
            map.put("uid", SPUtils.getString(mContext,GlobalConstants.userID,""));
            map.put("information_id",shareprotocol.shareCid);
            sharePresenter.getNewsShare(map, new ShareSuccessIView() {
                @Override
                public void getShareSuccess(String message) {
                    ToastUtil.showShort(mContext,message);
                }

                @Override
                public void getShareOnfaile(String msg) {
                    ToastUtil.showShort(mContext,msg);
                }

                @Override
                public void onNetworkFailure(String message) {
                    ToastUtil.showShort(mContext,message);
                }
            });
        }
    }

    public void getShareLive(){

        if (sharePresenter!=null){

            HashMap<String,String >map= new HashMap<>();
            map.put("userid",SPUtils.getString(mContext,GlobalConstants.userID,""));
            map.put("cid",shareprotocol.shareCid);
            sharePresenter.getLiveShare(map, new ShareSuccessIView() {
                @Override
                public void getShareSuccess(String message) {
                    ToastUtil.showShort(mContext,message);
                }

                @Override
                public void getShareOnfaile(String msg) {
                    ToastUtil.showShort(mContext,msg);
                }

                @Override
                public void onNetworkFailure(String message) {
                    ToastUtil.showShort(mContext,message);
                }
            });
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        sharePresenter.destroy();
    }
}