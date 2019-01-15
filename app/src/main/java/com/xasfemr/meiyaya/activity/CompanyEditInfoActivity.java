package com.xasfemr.meiyaya.activity;

import android.content.Intent;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.xasfemr.meiyaya.R;
import com.xasfemr.meiyaya.base.activity.MVPBaseActivity;
import com.xasfemr.meiyaya.utils.ToastUtil;
import com.xasfemr.meiyaya.view.LoadDataView;

import butterknife.BindView;
import butterknife.OnClick;

import static com.xasfemr.meiyaya.R.id.iv_delete_edit_info;
import static com.xasfemr.meiyaya.R.id.iv_top_back;
import static com.xasfemr.meiyaya.R.id.tv_top_right;

public class CompanyEditInfoActivity extends MVPBaseActivity {

    private Intent mIntent;
    private String infoType = "";

    @BindView(R.id.tv_top_title)
    TextView  tvTopTitle;
    @BindView(tv_top_right)
    TextView  tvTopRight;
    @BindView(R.id.et_edit_info)
    EditText  etEditInfo;
    @BindView(iv_delete_edit_info)
    ImageView ivDeleteEditInfo;

    @Override
    protected int layoutId() {
        return R.layout.activity_company_edit_info;
    }

    @Override
    protected ViewGroup loadDataViewLayout() {
        return null;
    }

    @Override
    protected void initView() {
        tvTopTitle.setText("");
        tvTopRight.setVisibility(View.VISIBLE);
        tvTopRight.setText("保存");

        etEditInfo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(s)) {
                    ivDeleteEditInfo.setVisibility(View.GONE);
                } else {
                    ivDeleteEditInfo.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    protected void initData() {
        mIntent = getIntent();
        String oldInfo = mIntent.getStringExtra("OLD_INFO");
        int editType = mIntent.getIntExtra("EDIT_TYPE", -1);
        switch (editType) {
            case 51:       //5系列:企业认证
                infoType = "公司名称";
                break;
            case 52:
                infoType = "公司法人";
                break;
            case 75:
            case 53:
                infoType = "联系人";
                break;
            case 76:
            case 54:
                infoType = "联系电话";
                etEditInfo.setFilters(new InputFilter[]{new InputFilter.LengthFilter(11)}); //电话号码最大长度11位
                etEditInfo.setInputType(InputType.TYPE_CLASS_NUMBER);
                break;
            case 55:
                infoType = "公司地址";
                break;
            case 48:       //4系列:发布简历
                infoType = "姓名";
                break;
            case 49:
                infoType = "联系电话";
                etEditInfo.setFilters(new InputFilter[]{new InputFilter.LengthFilter(11)}); //电话号码最大长度11位
                etEditInfo.setInputType(InputType.TYPE_CLASS_NUMBER);
                break;
            case 61:       //6系列:发布招聘
                infoType = "公司名称";
                break;
            case 62:
                infoType = "职位名称";
                break;
            case 63:
                infoType = "工作地点";
                break;
            case 64:
                infoType = "待遇";
                break;
            case 65:
                infoType = "联系人";
                break;
            case 66:
                infoType = "联系电话";
                etEditInfo.setFilters(new InputFilter[]{new InputFilter.LengthFilter(11)}); //电话号码最大长度11位
                etEditInfo.setInputType(InputType.TYPE_CLASS_NUMBER);
                break;
            case 71:
                infoType = "会议主题";
                break;
            case 73:
                infoType = "会议地址";
                break;
            case 74:       //承办单位
                infoType = "承办单位";
                break;
            case 81:       //8系列:会议报名
                infoType = "姓名";
                break;
            case 83:
                infoType = "联系电话";
                etEditInfo.setFilters(new InputFilter[]{new InputFilter.LengthFilter(11)}); //电话号码最大长度11位
                etEditInfo.setInputType(InputType.TYPE_CLASS_NUMBER);
                break;
            case 85:
                infoType = "参会人数";
                etEditInfo.setFilters(new InputFilter[]{new InputFilter.LengthFilter(5)}); //电话号码最大长度11位
                etEditInfo.setInputType(InputType.TYPE_CLASS_NUMBER);
                break;
            case 86:
                infoType = "单位";
                break;
            default:
                break;
        }
        tvTopTitle.setText(infoType);
        etEditInfo.setHint("请填写" + infoType);

        //已有填写信息的情况下,etEditInfo置为已填写信息
        if (TextUtils.equals(oldInfo, "请填写") || TextUtils.equals(oldInfo, "请选择") || TextUtils.isEmpty(oldInfo)) {
            etEditInfo.setText("");
        } else {
            etEditInfo.setText(oldInfo);
            ivDeleteEditInfo.setVisibility(View.VISIBLE);
            etEditInfo.setSelection(oldInfo.length());
        }
    }

    @Override
    protected void getLoadView(LoadDataView loadView) {

    }

    @Override
    protected void initPresenter() {

    }

    @OnClick({iv_top_back, tv_top_right, iv_delete_edit_info})
    public void onViewClick(View v) {
        switch (v.getId()) {
            case R.id.iv_top_back:   //返回
                finish();
                break;

            case R.id.tv_top_right:  //保存
                //ToastUtil.showShort(this, "保存");
                String editInfo = etEditInfo.getText().toString().trim();
                if (TextUtils.isEmpty(editInfo)) {
                    ToastUtil.showShort(this, infoType + "不能为空");
                } else {
                    mIntent.putExtra("EDIT_INFO", editInfo);
                    setResult(60, mIntent);
                    finish();
                }
                break;

            case R.id.iv_delete_edit_info:
                etEditInfo.setText("");
                break;

            default:
                break;
        }
    }
}
