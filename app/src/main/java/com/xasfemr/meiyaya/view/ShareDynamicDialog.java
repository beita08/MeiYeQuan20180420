package com.xasfemr.meiyaya.view;

import android.app.Dialog;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.xasfemr.meiyaya.R;
import com.xasfemr.meiyaya.global.GlobalConstants;
import com.xasfemr.meiyaya.utils.LogUtils;
import com.xasfemr.meiyaya.utils.SPUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

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
import okhttp3.Call;

/**
 * Description:
 * Company    : shifeier
 * Author     : liuxb
 * Date       : 2017/9/19 0019 15:59
 */

public class ShareDynamicDialog extends Dialog implements View.OnClickListener {

    private static final String TAG = "ShareDynamicDialog";

    private Context  mContext;
    private String   did;
    private String   content;
    private TextView tvShareFriendsCircle;
    private TextView tvShareWechat;
    private TextView tvShareQQ;
    private TextView tvShareWeibo;
    private TextView tvShareQzone;

    public ShareDynamicDialog(@NonNull Context context, String did, String content) {
        super(context, R.style.ShareDialogStyle); //指定dialog的样式
        this.mContext = context;
        this.did = did;
        this.content = content;
        //设置布局
        setContentView(R.layout.dialog_share);

        //显示在屏幕正下方 原理: 修改dialog所在窗口Window的位置, dialog随窗口显示
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
        sp.setTitle("美页圈APP：分享动态");
        //sp.setTitleUrl("http://www.xasfemr.com/"); // 标题的超链接
        sp.setText(content);
        sp.setImageData(BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.meiyaya_logo2_rectangle));
        sp.setUrl(GlobalConstants.H5_SHARE_DYNAMIC + "&did=" + did);
        //sp.setImageUrl(GlobalConstants.YAYA_PIC);
        //sp.setSite("美页圈网站名称");
        //sp.setSiteUrl("http://www.xasfemr.com/");

        Platform wechatMoments = ShareSDK.getPlatform(WechatMoments.NAME);
        // 设置分享事件回调（注：回调放在不能保证在主线程调用，不可以在里面直接处理UI操作）
        wechatMoments.setPlatformActionListener(new MyyPlatformActionListener());
        // 执行图文分享
        wechatMoments.share(sp);
        Toast.makeText(mContext, "正在开启朋友圈", Toast.LENGTH_SHORT).show();
    }

    private void shareWechat() {

        Platform.ShareParams sp = new Platform.ShareParams();
        sp.setShareType(Platform.SHARE_WEBPAGE);
        sp.setTitle("美页圈APP：分享动态");
        //sp.setTitleUrl("http://www.xasfemr.com/"); // 标题的超链接
        sp.setText(content);
        sp.setImageData(BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.meiyaya_logo2_rectangle));
        sp.setUrl(GlobalConstants.H5_SHARE_DYNAMIC + "&did=" + did);
        //sp.setImageUrl(GlobalConstants.YAYA_PIC);
        //sp.setSite("美页圈网站名称");
        //sp.setSiteUrl("http://www.xasfemr.com/");

        Platform wechat = ShareSDK.getPlatform(Wechat.NAME);
        // 设置分享事件回调（注：回调放在不能保证在主线程调用，不可以在里面直接处理UI操作）
        wechat.setPlatformActionListener(new MyyPlatformActionListener());
        // 执行图文分享
        wechat.share(sp);
        Toast.makeText(mContext, "正在开启微信", Toast.LENGTH_SHORT).show();

    }

    //分享至QQ
    private void shareQQ() {
        Platform.ShareParams sp = new Platform.ShareParams();
        sp.setTitle("美页圈APP 分享动态：");
        sp.setText(content);
        sp.setImageUrl(GlobalConstants.PIC_MEIYAYA_LOGO_RECTANGLE);
        //sp.setImageData(BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.meiyaya_logo2_rectangle));

        //sp.setUrl(GlobalConstants.H5_SHARE_DYNAMIC + "&did=" + did);
        sp.setTitleUrl(GlobalConstants.H5_SHARE_DYNAMIC + "&did=" + did);

        Platform qq = ShareSDK.getPlatform(QQ.NAME);
        qq.setPlatformActionListener(new MyyPlatformActionListener());
        qq.share(sp);
        Toast.makeText(mContext, "正在开启QQ", Toast.LENGTH_SHORT).show();
    }

    //分享至微博
    private void shareWeibo() {

        Platform.ShareParams sp = new Platform.ShareParams();
        sp.setText("美页圈APP 分享动态：\n" + content + "\n" + GlobalConstants.H5_SHARE_DYNAMIC + "&did=" + did);
        sp.setImageData(BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.meiyaya_logo2_rectangle));
        //sp.setImagePath(“/mnt/sdcard/测试分享的图片.jpg”);
        //sp.setImageUrl(GlobalConstants.YAYA_PIC);
        //sp.setUrl("http://www.xasfemr.com/");

        Platform weibo = ShareSDK.getPlatform(SinaWeibo.NAME);
        weibo.setPlatformActionListener(new MyyPlatformActionListener()); // 设置分享事件回调
        // 执行图文分享
        weibo.share(sp);
        Toast.makeText(mContext, "正在开启微博", Toast.LENGTH_SHORT).show();
    }

    //分享至QQ
    private void shareQZone() {
        Platform.ShareParams sp = new Platform.ShareParams();
        sp.setTitle("美页圈APP：分享动态");
        sp.setTitleUrl(GlobalConstants.H5_SHARE_DYNAMIC + "&did=" + did); // 标题的超链接
        sp.setText(content);
        //sp.setImageData(BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.meiyaya_logo2_rectangle));
        sp.setImageUrl(GlobalConstants.PIC_MEIYAYA_LOGO_RECTANGLE);
        sp.setSite("美页圈官网");
        sp.setSiteUrl("http://www.xasfemr.com/");

        Platform qzone = ShareSDK.getPlatform(QZone.NAME);
        // 设置分享事件回调（注：回调放在不能保证在主线程调用，不可以在里面直接处理UI操作）
        qzone.setPlatformActionListener(new MyyPlatformActionListener());
        // 执行图文分享
        qzone.share(sp);
        Toast.makeText(mContext, "正在开启QQ空间", Toast.LENGTH_SHORT).show();
    }


    private class MyyPlatformActionListener implements PlatformActionListener {
        @Override
        public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
            //Toast.makeText(mContext, "成功分享至" + platform.getName(), Toast.LENGTH_SHORT).show();
            toastShareResult("成功分享至", platform.getName());

            //操作成功，在这里可以做后续的步骤
            //这里需要说明的一个参数就是HashMap<String, Object> hashMap
            //这个参数在你进行登录操作的时候里面会保存有用户的数据，例如用户名之类的。
            for (Map.Entry<String, Object> entry : hashMap.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                //System.out.println("key" + key + "value" + value.toString());
                LogUtils.show(TAG, "key" + key + "value" + value.toString());
            }
            gotoAddShareNumber();
        }

        @Override
        public void onError(Platform platform, int i, Throwable throwable) {
            //Toast.makeText(mContext, "分享至" + platform.getName() + "出现错误", Toast.LENGTH_SHORT).show();
            toastShareResult("分享出现错误", platform.getName());
            //操作失败啦，打印提供的错误，方便调试
            throwable.printStackTrace();
        }

        @Override
        public void onCancel(Platform platform, int i) {
            //用户取消操作
            //Toast.makeText(mContext, "已取消分享至" + platform.getName(), Toast.LENGTH_SHORT).show();
            toastShareResult("已取消分享至", platform.getName());
        }
    }

    private void gotoAddShareNumber() {
        String userId = SPUtils.getString(mContext, GlobalConstants.userID, "");
        Log.i(TAG, "gotoAddShareNumber:" + userId + "分享了" + did);

        OkHttpUtils.get().url(GlobalConstants.URL_DYNAMIC_SHARE)
                .addParams("cid", userId)
                .addParams("did", did)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.i(TAG, "onError: 网络错误,上传分享数据失败!");
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.i(TAG, "onResponse: 分享数据上传成功!");
                    }
                });
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
}