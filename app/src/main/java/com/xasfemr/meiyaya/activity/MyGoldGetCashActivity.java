package com.xasfemr.meiyaya.activity;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.xasfemr.meiyaya.R;
import com.xasfemr.meiyaya.main.BaseActivity;

// GetCash = 提现
public class MyGoldGetCashActivity extends BaseActivity {

    private ImageView ivGetCashIcon;
    private TextView  tvGetCashType;
    private TextView  tvGetCashTypeName;
    private EditText  etGetCashMoney;
    private TextView  tvGetCashSumMoney;
    private TextView  tvGetCashConfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_gold_get_cash);
        initTopBar();
        setTopTitleText("提现");

        ivGetCashIcon = (ImageView) findViewById(R.id.iv_get_cash_icon);
        tvGetCashType = (TextView) findViewById(R.id.tv_get_cash_type);
        tvGetCashTypeName = (TextView) findViewById(R.id.tv_get_cash_type_name);
        etGetCashMoney = (EditText) findViewById(R.id.et_get_cash_money);
        tvGetCashSumMoney = (TextView) findViewById(R.id.tv_get_cash_sum_money);
        tvGetCashConfirm = (TextView) findViewById(R.id.tv_get_cash_confirm);


    }
}
