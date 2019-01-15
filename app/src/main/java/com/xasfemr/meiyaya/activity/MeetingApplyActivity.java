package com.xasfemr.meiyaya.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.xasfemr.meiyaya.R;
import com.xasfemr.meiyaya.global.GlobalConstants;
import com.xasfemr.meiyaya.main.BaseActivity;
import com.xasfemr.meiyaya.utils.LogUtils;
import com.xasfemr.meiyaya.utils.SPUtils;
import com.xasfemr.meiyaya.utils.ToastUtil;
import com.xasfemr.meiyaya.view.BottomListDialog;
import com.xasfemr.meiyaya.view.progress.SFProgressDialog;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.Call;

public class MeetingApplyActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "MeetingApplyActivity";
    private SFProgressDialog progressDialog;
    private Intent           mIntent;

    private final String[]          mProfArr    = {"美容机构代表/美容院院长", "美容院店长", "高级美容师", "美业技师/学徒", "整形医生/高级护理", "高级美容顾问/咨询师", "美业协会工作人员", "美业产品、仪器生产厂商/代理商、供应商", "美业产品销售人员", "美容治疗师", "美导（助教老师）", "美容行业教育培训人员", "美业仪器操作师", "美业其他从业人员", "其他行业"};
    private final String[]          mGenderArr  = {"男", "女"};
    private final ArrayList<String> mProfList   = new ArrayList<>();
    private final ArrayList<String> mGenderList = new ArrayList<>();

    private String meetingId;

    private TextView tvApplyPeopleNum;
    private TextView tvName;
    private TextView tvGender;
    private TextView tvPhone;
    private TextView tvJob;
    private TextView tvPeopleNum;
    private TextView tvCompany;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meeting_apply);
        initTopBar();
        setTopTitleText("会议报名");
        progressDialog = new SFProgressDialog(this);
        Intent intent = getIntent();
        meetingId = intent.getStringExtra("meetingId");
        int meetingApplyNum = intent.getIntExtra("meetingApplyNum", -1);

        tvApplyPeopleNum = (TextView) findViewById(R.id.tv_meeting_apply_peopleNum);
        tvName = (TextView) findViewById(R.id.tv_meeting_name);
        tvGender = (TextView) findViewById(R.id.tv_meeting_gender);
        tvPhone = (TextView) findViewById(R.id.tv_meeting_phone);
        tvJob = (TextView) findViewById(R.id.tv_meeting_job);
        tvPeopleNum = (TextView) findViewById(R.id.tv_meeting_peopleNum);
        tvCompany = (TextView) findViewById(R.id.tv_meeting_company);
        findViewById(R.id.rl_meeting_name).setOnClickListener(this);
        findViewById(R.id.rl_meeting_gender).setOnClickListener(this);
        findViewById(R.id.rl_meeting_phone).setOnClickListener(this);
        findViewById(R.id.rl_meeting_job).setOnClickListener(this);
        findViewById(R.id.rl_meeting_peopleNum).setOnClickListener(this);
        findViewById(R.id.rl_meeting_company).setOnClickListener(this);
        findViewById(R.id.btn_confirm_submit).setOnClickListener(this);

        if (meetingApplyNum > 0) {
            tvApplyPeopleNum.setText("截止目前：已报名" + meetingApplyNum + "人");
        } else {
            tvApplyPeopleNum.setText("未获取到数据");
        }

        for (int i = 0; i < mProfArr.length; i++) {
            mProfList.add(mProfArr[i]);
        }
        for (int i = 0; i < mGenderArr.length; i++) {
            mGenderList.add(mGenderArr[i]);
        }
    }

    @Override
    public void onClick(View v) {
        if (mIntent == null) {
            mIntent = new Intent();
        }
        switch (v.getId()) {
            case R.id.rl_meeting_name:
                mIntent.setClass(this, CompanyEditInfoActivity.class);
                mIntent.putExtra("OLD_INFO", tvName.getText().toString().trim());
                mIntent.putExtra("EDIT_TYPE", 81);
                startActivityForResult(mIntent, 81);
                break;

            case R.id.rl_meeting_gender:
                String meetingGender = tvGender.getText().toString().trim();
                BottomListDialog genderDialog = new BottomListDialog(this, meetingGender, "请选择性别", mGenderList);
                genderDialog.setOnItemClickListener(new BottomListDialog.OnItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        tvGender.setText(mGenderArr[position]);
                    }
                });
                genderDialog.show();
                break;

            case R.id.rl_meeting_phone:
                mIntent.setClass(this, CompanyEditInfoActivity.class);
                mIntent.putExtra("OLD_INFO", tvPhone.getText().toString().trim());
                mIntent.putExtra("EDIT_TYPE", 83);
                startActivityForResult(mIntent, 83);
                break;

            case R.id.rl_meeting_job:
                String meetingJob = tvJob.getText().toString().trim();
                BottomListDialog jobDialog = new BottomListDialog(this, meetingJob, "请选择求职职位", mProfList);
                jobDialog.setOnItemClickListener(new BottomListDialog.OnItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        tvJob.setText(mProfArr[position]);
                    }
                });
                jobDialog.show();
                break;

            case R.id.rl_meeting_peopleNum:
                mIntent.setClass(this, CompanyEditInfoActivity.class);
                mIntent.putExtra("OLD_INFO", tvPeopleNum.getText().toString().trim());
                mIntent.putExtra("EDIT_TYPE", 85);
                startActivityForResult(mIntent, 85);
                break;

            case R.id.rl_meeting_company:
                mIntent.setClass(this, CompanyEditInfoActivity.class);
                mIntent.putExtra("OLD_INFO", tvCompany.getText().toString().trim());
                mIntent.putExtra("EDIT_TYPE", 86);
                startActivityForResult(mIntent, 86);
                break;

            case R.id.btn_confirm_submit:
                boolean isLoginState = SPUtils.getboolean(this, GlobalConstants.isLoginState, false);
                if (!isLoginState) { //如果没有登录先去登录
                    ToastUtil.showShort(this, "请先登录");
                    startActivity(new Intent(this, LoginActivity.class));
                    return;
                } else {
                    checkMeetingApplyInfo();
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 60 && data != null) {
            String editInfo = data.getStringExtra("EDIT_INFO");
            switch (requestCode) {
                case 81:
                    tvName.setText(editInfo);
                    break;
                case 83:
                    tvPhone.setText(editInfo);
                    break;
                case 85:
                    tvPeopleNum.setText(editInfo);
                    break;
                case 86:
                    tvCompany.setText(editInfo);
                    break;
                default:
                    break;
            }
        }
    }

    private void checkMeetingApplyInfo() {
        String name = tvName.getText().toString().trim();
        String gender = tvGender.getText().toString().trim();
        String phone = tvPhone.getText().toString().trim();
        String job = tvJob.getText().toString().trim();
        String peopleNum = tvPeopleNum.getText().toString().trim();
        String company = tvCompany.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.equals(name, "请填写") || TextUtils.equals(name, "请选择")) {
            ToastUtil.showShort(this, "姓名不能为空");
            return;
        }
        if (TextUtils.isEmpty(gender) || TextUtils.equals(gender, "请填写") || TextUtils.equals(gender, "请选择")) {
            ToastUtil.showShort(this, "请选择性别");
            return;
        }
        if (TextUtils.isEmpty(phone) || TextUtils.equals(phone, "请填写") || TextUtils.equals(phone, "请选择")) {
            ToastUtil.showShort(this, "联系电话不能为空");
            return;
        }
        if (!phone.matches(GlobalConstants.STR_PHONE_REGEX2)) { //用正则表达式判断手机号是否合格
            ToastUtil.showShort(this, "请输入正确的联系电话");
            return;
        }
        if (TextUtils.isEmpty(job) || TextUtils.equals(job, "请填写") || TextUtils.equals(job, "请选择")) {
            ToastUtil.showShort(this, "请选择职位");
            return;
        }
        if (TextUtils.isEmpty(peopleNum) || TextUtils.equals(peopleNum, "请填写") || TextUtils.equals(peopleNum, "请选择")) {
            ToastUtil.showShort(this, "参会人数不能为空");
            return;
        }
        if (TextUtils.isEmpty(company) || TextUtils.equals(company, "请填写") || TextUtils.equals(company, "请选择")) {
            ToastUtil.showShort(this, "单位不能为空");
            return;
        }

        gotoSubmitApplyInfo(name, gender, phone, job, peopleNum, company);
    }

    private void gotoSubmitApplyInfo(String name, String gender, String phone, String job, String peopleNum, String company) {
        progressDialog.show();
        String mUserId = SPUtils.getString(this, GlobalConstants.userID, "");

        OkHttpUtils.post().url(GlobalConstants.URL_MEETING_APPLY)
                .addParams("infoid", meetingId)             //会议id   必须
                .addParams("userid", mUserId)           //用户id   必须
                .addParams("username", name)            //姓名		可选
                .addParams("sex", gender)               //
                .addParams("phone", phone)              //联系电话	可选
                .addParams("position", job)             //
                .addParams("meetingNumber", peopleNum)  //会议人数	可选
                .addParams("companyName", company)      //报名单位		可选
                .addParams("content", "")               //备注	可选
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                progressDialog.dismiss();
                LogUtils.show(TAG, "---网络异常,会议报名失败,请重试---");
                Toast.makeText(MeetingApplyActivity.this, "网络异常,会议报名失败,请重试", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(String response, int id) {
                progressDialog.dismiss();
                LogUtils.show(TAG, "---会议报名成功:response = " + response + "---");
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String data = jsonObject.getString("data");
                    String message = jsonObject.getString("message");
                    Toast.makeText(MeetingApplyActivity.this, message, Toast.LENGTH_SHORT).show();
                    if (TextUtils.equals(data, "success")) {
                        //startActivity(new Intent(MeetingApplyActivity.this, RequestJobListActivity.class));
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(MeetingApplyActivity.this, "数据异常,请重试", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
