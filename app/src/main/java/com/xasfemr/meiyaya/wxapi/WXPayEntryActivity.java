package com.xasfemr.meiyaya.wxapi;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.xasfemr.meiyaya.R;
import com.xasfemr.meiyaya.global.GlobalConstants;
import com.xasfemr.meiyaya.main.BaseActivity;
import com.xasfemr.meiyaya.utils.LogUtils;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;
import org.simple.eventbus.ThreadMode;

public class WXPayEntryActivity extends BaseActivity implements IWXAPIEventHandler {

    private static final String TAG = "WXPayEntryActivity";

    private IWXAPI api;


    private TextView tv_top_title,tvPayResult;
    private ImageView iv_top_back,imgPayResult;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wxpay_entry);
        EventBus.getDefault().register(this);

        api = WXAPIFactory.createWXAPI(this, GlobalConstants.APP_ID);   ////美页圈APPID wx9b8926da4f98936f
        api.handleIntent(getIntent(), this);

        tv_top_title = (TextView) findViewById(R.id.tv_top_title);
        iv_top_back = (ImageView) findViewById(R.id.iv_top_back);
        imgPayResult = (ImageView) findViewById(R.id.iv_pay_result);
        tvPayResult = (TextView) findViewById(R.id.tv_pay_result);

        tv_top_title.setText("支付结果");
        iv_top_back.setOnClickListener(v -> finish());

        findViewById(R.id.tv_gold_recharge).setOnClickListener(v -> finish());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq req) {
    }

    //支付完成后，微信APP会返回到商户APP并回调onResp函数，开发者需要在该函数中接收通知，判断返回错误码,
    //如果支付成功则去后台查询支付结果再展示用户实际支付结果。注意一定不能以客户端返回作为用户支付的结果，应以服务器端的接收的支付通知或查询API返回的结果为准。
    @Override
    public void onResp(BaseResp resp) {
        LogUtils.show(TAG, "onPayFinish, errCode = " + resp.errCode);

        if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
            /*AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("提示");
            builder.setMessage(getString(R.string.pay_result_callback_msg, String.valueOf(resp.errCode)));
            builder.show();*/

            EventBus.getDefault().post(resp.errCode,GlobalConstants.EventBus.WXCALLBACK);
//            switch (resp.errCode) {
//                case 0:
//                    //Toast.makeText(this, "支付成功", Toast.LENGTH_SHORT).show();
//                    tvPayResult.setText("恭喜您，充值成功");
//                    imgPayResult.setImageDrawable(getResources().getDrawable(R.drawable.pay_complete));
//
//                    break;
//                case -1:
//                    tvPayResult.setText("支付失败");
//                    imgPayResult.setImageDrawable(getResources().getDrawable(R.drawable.pay_error));
//                    //Toast.makeText(this, "支付失败", Toast.LENGTH_SHORT).show();
//                    break;
//                case -2:
//                    tvPayResult.setText("取消支付");
//                    imgPayResult.setImageDrawable(getResources().getDrawable(R.drawable.pay_error));
//                    //Toast.makeText(this, "取消了支付", Toast.LENGTH_SHORT).show();
//                    break;
//                default:
//                    tvPayResult.setText("支付异常");
//                    imgPayResult.setImageDrawable(getResources().getDrawable(R.drawable.pay_error));
//                    //Toast.makeText(this, "出现未知异常", Toast.LENGTH_SHORT).show();
//                    break;
//            }
        }
    }




    @Subscriber(mode = ThreadMode.MAIN,tag = GlobalConstants.EventBus.WXCALLBACK)
    public void getPayResult(int code){
        switch (code){

            case 0:
                //Toast.makeText(this, "支付成功", Toast.LENGTH_SHORT).show();
                tvPayResult.setText("恭喜您，充值成功");
                imgPayResult.setImageDrawable(getResources().getDrawable(R.drawable.pay_complete));
                EventBus.getDefault().post("1",GlobalConstants.EventBus.UPDATE_USER_DATE);

                break;
            case -1:
                tvPayResult.setText("支付失败");
                imgPayResult.setImageDrawable(getResources().getDrawable(R.drawable.pay_error));
                //Toast.makeText(this, "支付失败", Toast.LENGTH_SHORT).show();
                break;
            case -2:
                tvPayResult.setText("取消支付");
                imgPayResult.setImageDrawable(getResources().getDrawable(R.drawable.pay_error));
                //Toast.makeText(this, "取消了支付", Toast.LENGTH_SHORT).show();
                break;
            default:
                tvPayResult.setText("支付异常");
                imgPayResult.setImageDrawable(getResources().getDrawable(R.drawable.pay_error));
                //Toast.makeText(this, "出现未知异常", Toast.LENGTH_SHORT).show();
                break;

        }


    }


    /*
    回调中errCode值列表：
    名称      描述          解决方案
    0       成功展示        成功页面
    -1      错误           可能的原因：签名错误、未注册APPID、项目设置APPID不正确、注册的APPID与设置的不匹配、其他异常等。
    -2      用户取消        无需处理。发生场景：用户不支付了，点击取消，返回APP。
    */

    /*SFDialog.onlyConfirmDialog(this, "提示", "出现未知异常", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });*/
}