package com.xasfemr.meiyaya.view;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xasfemr.meiyaya.R;
import com.xasfemr.meiyaya.activity.MyGoldRechargeActivity;
import com.xasfemr.meiyaya.global.GlobalConstants;
import com.xasfemr.meiyaya.neteasecloud.NELivePlayerActivity;
import com.xasfemr.meiyaya.utils.SPUtils;

import org.simple.eventbus.EventBus;

/**
 * Description:
 * Company    : shifeier
 * Author     : liuxb
 * Date       : 2017/11/9 0009 15:25
 * public class ShareDialog extends Dialog
 */

public class GoldSendDialog extends Dialog implements View.OnClickListener {

    private int sendGoldNum = 200; //默认发送200金币

    private NELivePlayerActivity mContext;

    private RelativeLayout rlGold200;
    private RelativeLayout rlGold100;
    private RelativeLayout rlGold80;
    private RelativeLayout rlGold50;
    private RelativeLayout rlGold25;
    private RelativeLayout rlGold10;
    private RelativeLayout rlGold5;
    private RelativeLayout rlGold1;
    private LinearLayout   llGoldRecharge;
    private ImageView      ivGoldSend;
    private TextView tv_user_gold;

    public GoldSendDialog(@NonNull Context context) {
        super(context, R.style.GoldSendDialogStyle);
        this.mContext = (NELivePlayerActivity) context;
        setContentView(R.layout.dialog_gold_send);

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

        rlGold200 = (RelativeLayout) findViewById(R.id.rl_gold_send_200);
        rlGold100 = (RelativeLayout) findViewById(R.id.rl_gold_send_100);
        rlGold80 = (RelativeLayout) findViewById(R.id.rl_gold_send_80);
        rlGold50 = (RelativeLayout) findViewById(R.id.rl_gold_send_50);
        rlGold25 = (RelativeLayout) findViewById(R.id.rl_gold_send_25);
        rlGold10 = (RelativeLayout) findViewById(R.id.rl_gold_send_10);
        rlGold5 = (RelativeLayout) findViewById(R.id.rl_gold_send_5);
        rlGold1 = (RelativeLayout) findViewById(R.id.rl_gold_send_1);
        llGoldRecharge = (LinearLayout) findViewById(R.id.ll_gold_recharge);
        ivGoldSend = (ImageView) findViewById(R.id.iv_gold_send);
        tv_user_gold= (TextView) findViewById(R.id.tv_user_gold);

        rlGold200.setOnClickListener(this);
        rlGold100.setOnClickListener(this);
        rlGold80.setOnClickListener(this);
        rlGold50.setOnClickListener(this);
        rlGold25.setOnClickListener(this);
        rlGold10.setOnClickListener(this);
        rlGold5.setOnClickListener(this);
        rlGold1.setOnClickListener(this);
        llGoldRecharge.setOnClickListener(this);
        ivGoldSend.setOnClickListener(this);

        //初始化选择200
        rlGold200.setSelected(true);

        tv_user_gold.setText(SPUtils.getString(mContext,GlobalConstants.USER_GOLD_NUMBER,""));
    }


    public void refrshGold(String gold){
        tv_user_gold.setText(gold);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.rl_gold_send_200:
                rlGold200.setSelected(true);
                rlGold100.setSelected(false);
                rlGold80.setSelected(false);
                rlGold50.setSelected(false);
                rlGold25.setSelected(false);
                rlGold10.setSelected(false);
                rlGold5.setSelected(false);
                rlGold1.setSelected(false);
                sendGoldNum = 200;

                break;
            case R.id.rl_gold_send_100:
                rlGold200.setSelected(false);
                rlGold100.setSelected(true);
                rlGold80.setSelected(false);
                rlGold50.setSelected(false);
                rlGold25.setSelected(false);
                rlGold10.setSelected(false);
                rlGold5.setSelected(false);
                rlGold1.setSelected(false);
                sendGoldNum = 100;

                break;
            case R.id.rl_gold_send_80:
                rlGold200.setSelected(false);
                rlGold100.setSelected(false);
                rlGold80.setSelected(true);
                rlGold50.setSelected(false);
                rlGold25.setSelected(false);
                rlGold10.setSelected(false);
                rlGold5.setSelected(false);
                rlGold1.setSelected(false);
                sendGoldNum = 80;

                break;
            case R.id.rl_gold_send_50:
                rlGold200.setSelected(false);
                rlGold100.setSelected(false);
                rlGold80.setSelected(false);
                rlGold50.setSelected(true);
                rlGold25.setSelected(false);
                rlGold10.setSelected(false);
                rlGold5.setSelected(false);
                rlGold1.setSelected(false);
                sendGoldNum = 50;

                break;
            case R.id.rl_gold_send_25:
                rlGold200.setSelected(false);
                rlGold100.setSelected(false);
                rlGold80.setSelected(false);
                rlGold50.setSelected(false);
                rlGold25.setSelected(true);
                rlGold10.setSelected(false);
                rlGold5.setSelected(false);
                rlGold1.setSelected(false);
                sendGoldNum = 25;

                break;
            case R.id.rl_gold_send_10:
                rlGold200.setSelected(false);
                rlGold100.setSelected(false);
                rlGold80.setSelected(false);
                rlGold50.setSelected(false);
                rlGold25.setSelected(false);
                rlGold10.setSelected(true);
                rlGold5.setSelected(false);
                rlGold1.setSelected(false);
                sendGoldNum = 10;

                break;
            case R.id.rl_gold_send_5:
                rlGold200.setSelected(false);
                rlGold100.setSelected(false);
                rlGold80.setSelected(false);
                rlGold50.setSelected(false);
                rlGold25.setSelected(false);
                rlGold10.setSelected(false);
                rlGold5.setSelected(true);
                rlGold1.setSelected(false);
                sendGoldNum = 5;

                break;
            case R.id.rl_gold_send_1:
                rlGold200.setSelected(false);
                rlGold100.setSelected(false);
                rlGold80.setSelected(false);
                rlGold50.setSelected(false);
                rlGold25.setSelected(false);
                rlGold10.setSelected(false);
                rlGold5.setSelected(false);
                rlGold1.setSelected(true);
                sendGoldNum = 1;

                break;
            case R.id.ll_gold_recharge:
                Intent intent = new Intent(mContext, MyGoldRechargeActivity.class);
                mContext.startActivity(intent);
                break;
            case R.id.iv_gold_send:
                EventBus.getDefault().post(sendGoldNum+"", GlobalConstants.EventBus.CHAT_LIVE_SEND_GIFT);
                break;

            default:
                break;
        }

    }
}
