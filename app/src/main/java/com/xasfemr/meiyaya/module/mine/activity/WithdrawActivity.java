package com.xasfemr.meiyaya.module.mine.activity;

import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xasfemr.meiyaya.R;
import com.xasfemr.meiyaya.base.activity.MVPBaseActivity;
import com.xasfemr.meiyaya.global.GlobalConstants;
import com.xasfemr.meiyaya.module.college.activity.WebViewActivity;
import com.xasfemr.meiyaya.module.mine.IView.WithDrawView;
import com.xasfemr.meiyaya.module.mine.presenter.MinePresenter;
import com.xasfemr.meiyaya.utils.CharUtils;
import com.xasfemr.meiyaya.utils.LogUtils;
import com.xasfemr.meiyaya.utils.MD5Utils;
import com.xasfemr.meiyaya.utils.SPUtils;
import com.xasfemr.meiyaya.utils.ToastUtil;
import com.xasfemr.meiyaya.view.LoadDataView;
import com.xasfemr.meiyaya.view.progress.SFProgressDialog;

import java.text.DecimalFormat;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 提现
 * Created by sen.luo on 2017/12/7.
 */

public class WithdrawActivity extends MVPBaseActivity{


    @BindView(R.id.layout_root)
    LinearLayout layoutRoot;
    @BindView(R.id.tv_top_title)
    TextView tvTitle;
    @BindView(R.id.iv_top_back)
    ImageView ivBack;
    @BindView(R.id.et_get_cash_money)
    EditText etGold;
    @BindView(R.id.tv_card_number)
    EditText etCardNumber;
    @BindView(R.id.et_password)
    EditText etPassword;

    @BindView(R.id.tv_get_cash_sum_money)
    TextView tvGold;
    @BindView(R.id.tv_uname)
    TextView tvUname;

    private LoadDataView loadDataView;
    private int rate;
    private String uname="";

    private SFProgressDialog progressDialog;
    private MinePresenter minePresenter;

    @Override
    protected int layoutId() {
        return R.layout.activity_with_draw;
    }

    @Override
    protected ViewGroup loadDataViewLayout() {
        return layoutRoot;
    }

    @Override
    protected void initView() {
        tvTitle.setText("提现");
        ivBack.setOnClickListener(v -> finish());
        progressDialog =new SFProgressDialog(this);
        rate=getIntent().getIntExtra("RATE",0);
        uname=getIntent().getStringExtra("UNAME");
        tvUname.setText(uname);

        DecimalFormat df=new DecimalFormat("0.00");

        etGold.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                tvGold.setText("提现金额：¥ ");
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                if (rate!=0&&Integer.parseInt( etGold.getText().toString())>rate&& !TextUtils.isEmpty( etGold.getText().toString())){
//                    tvGold.setText("提现金额：¥ "+ Integer.parseInt( etGold.getText().toString()) /rate);
//                    ToastUtil.showShort(WithdrawActivity.this,"可提现金币数量不足");
//                }

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (rate<=0){
                    return;
                }

                if (etGold.getText().toString().trim().equals("")){
                    return;
                }
                try {
                    tvGold.setText("提现金额：¥ "+ df.format((float)Integer.parseInt( etGold.getText().toString()) /rate));
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(WithdrawActivity.this, "提现金币只能是整数", Toast.LENGTH_SHORT).show();
                    etGold.setText("");
                }

                //if (rate!=0&&Integer.parseInt( etGold.getText().toString())>rate&& !TextUtils.isEmpty( etGold.getText().toString())){
                //
                //}else {
                //   ToastUtil.showShort(WithdrawActivity.this,"可提现金币数量不足");
                // }
            }
        });


        findViewById(R.id.tv_my_gold_about_gold).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(WithdrawActivity.this, WebViewActivity.class).
                        putExtra("title","关于提现").putExtra("url",GlobalConstants.URL_ABOUT_WITHDRAW).putExtra("news",false));
            }
        });

    }

    @Override
    protected void initData() {





    }

    private void getData() {

        if (TextUtils.isEmpty(SPUtils.getString(this, GlobalConstants.userID,""))){
            ToastUtil.showShort(this,"您未登录，请先去登录");
            return;
        }

        if (etGold.getText().toString().equals("")){
            ToastUtil.showShort(this,"提现金额不能为空");
            return;
        }
        if (Integer.parseInt( etGold.getText().toString())<100){
            ToastUtil.showShort(this,"金币必须大于100才可以提现");
            return;
        }

        if (etCardNumber.getText().toString().equals("")){

            ToastUtil.showShort(this,"卡号不能为空");
            return;
        }

        if (!CharUtils.checkBankCard(etCardNumber.getText().toString())){
            ToastUtil.showShort(this,"卡号格式不正确");
            return;
        }


        if (etPassword.getText().toString().equals("")){

            ToastUtil.showShort(this,"密码不能为空");
            return;
        }

        if (!MD5Utils.getMd5(etPassword.getText().toString()).equals(SPUtils.getString(this,GlobalConstants.password,""))){
            ToastUtil.showShort(this,"密码不正确");
            return;
        }


        progressDialog.show();
        HashMap<String,String> map =new HashMap<>();
        map.put("userid",SPUtils.getString(this, GlobalConstants.userID,""));
        map.put("cardholder",uname); //持卡人
        map.put("cardnumber",etCardNumber.getText().toString()); //卡号
        map.put("goldmoney",etGold.getText().toString()); //金币

        minePresenter.getWithDraw(map, new WithDrawView() {
            @Override
            public void getWithDrawSuccess(String msg) {
                progressDialog.dismiss();
                LogUtils.show("提现返回",msg);

                startActivity(new Intent(WithdrawActivity.this,WithdrawSuccessActivity.class).putExtra("text",msg));
                finish();

//                SFDialog.onlyConfirmDialog(WithdrawActivity.this, "提示", "提现发起成功，请注意您账户的资金变化", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        finish();
//                    }
//                });

            }

            @Override
            public void getWithDrawOnfailure(String message) {
                ToastUtil.showShort(WithdrawActivity.this,message);
                progressDialog.dismiss();
            }

            @Override
            public void onNetworkFailure(String message) {
                progressDialog.dismiss();
                ToastUtil.showShort(WithdrawActivity.this,message);

            }
        });
    }


    @OnClick({R.id.tv_confirm})
    public void onViewClick(View view){
        switch (view.getId()){
            case R.id.tv_confirm:
                getData();

                break;
        }
    }


    @Override
    protected void getLoadView(LoadDataView loadView) {
        this.loadDataView=loadView;
        loadDataView.setErrorListner(v -> initData());
    }

    @Override
    protected void initPresenter() {
        minePresenter=new MinePresenter();
    }
}
