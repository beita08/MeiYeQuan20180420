package com.xasfemr.meiyaya.module.mine.activity;

import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.xasfemr.meiyaya.R;
import com.xasfemr.meiyaya.base.activity.MVPBaseActivity;
import com.xasfemr.meiyaya.view.LoadDataView;

import butterknife.BindView;

/**
 * Created by Administrator on 2017/12/11.
 */

public class WithdrawSuccessActivity extends MVPBaseActivity{

    @BindView(R.id.tv_confirm)
    TextView tvConfirm;
    @BindView(R.id.tv_text)
    TextView tvText;
    @BindView(R.id.tv_top_title)
    TextView tvTitle;
    @BindView(R.id.iv_top_back)
    ImageView ivBack;

    @Override
    protected int layoutId() {
        return R.layout.activity_with_draw_success;
    }

    @Override
    protected ViewGroup loadDataViewLayout() {
        return null;
    }

    @Override
    protected void initView() {
        tvTitle.setText("提现申请成功");
        ivBack.setOnClickListener(v -> finish());
        tvConfirm.setOnClickListener(v -> finish());

        if (!getIntent().getStringExtra("text").equals("")){
            tvText.setText(getIntent().getStringExtra("text"));
        }


    }

    @Override
    protected void initData() {

    }

    @Override
    protected void getLoadView(LoadDataView loadView) {

    }

    @Override
    protected void initPresenter() {

    }
}
