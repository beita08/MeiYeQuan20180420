package com.xasfemr.meiyaya.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.pickerview.TimePickerView;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.ui.ImageGridActivity;
import com.xasfemr.meiyaya.R;
import com.xasfemr.meiyaya.global.GlobalConstants;
import com.xasfemr.meiyaya.main.BaseActivity;
import com.xasfemr.meiyaya.utils.LogUtils;
import com.xasfemr.meiyaya.utils.SPUtils;
import com.xasfemr.meiyaya.utils.SelectDialog;
import com.xasfemr.meiyaya.utils.ToastUtil;
import com.xasfemr.meiyaya.view.progress.SFProgressDialog;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import okhttp3.Call;

public class MeetingReleaseActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "MeetingReleaseActivity";
    private SFProgressDialog sfProgressDialog;

    private static final int MEETING_DEADLINE = 0;    //会议截止时间
    private static final int MEETING_TIME     = 1;    //会议时间

    private Intent mIntent;
    private String meetingCoverPath;
    private long   meetingDeadlineSeconds;
    private long   meetingTimeSeconds;


    private TextView  tvMeetingTitle;
    private ImageView ivMeetingCover;
    private TextView  tvMeetingDeadline;
    private TextView  tvMeetingOrganizer;
    private TextView  tvMeetingAddress;
    private TextView  tvMeetingTime;
    private TextView  tvMeetingContacts;
    private TextView  tvMeetingPhoneNum;
    private EditText  etMeetingContent;
    private TextView  tvMeetingContentLimit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meeting_release);
        initTopBar();
        setTopTitleText("发布会议");
        sfProgressDialog = new SFProgressDialog(this);

        tvMeetingTitle = (TextView) findViewById(R.id.tv_meeting_title);
        ivMeetingCover = (ImageView) findViewById(R.id.iv_meeting_cover);
        tvMeetingDeadline = (TextView) findViewById(R.id.tv_meeting_deadline);
        tvMeetingOrganizer = (TextView) findViewById(R.id.tv_meeting_organizer);
        tvMeetingAddress = (TextView) findViewById(R.id.tv_meeting_address);
        tvMeetingTime = (TextView) findViewById(R.id.tv_meeting_time);
        tvMeetingContacts = (TextView) findViewById(R.id.tv_meeting_contacts);
        tvMeetingPhoneNum = (TextView) findViewById(R.id.tv_meeting_phoneNum);
        etMeetingContent = (EditText) findViewById(R.id.et_meeting_content);
        tvMeetingContentLimit = (TextView) findViewById(R.id.tv_meeting_content_limit);
        findViewById(R.id.rl_meeting_title).setOnClickListener(this);
        findViewById(R.id.rl_meeting_cover).setOnClickListener(this);
        findViewById(R.id.rl_meeting_deadline).setOnClickListener(this);
        findViewById(R.id.rl_meeting_organizer).setOnClickListener(this);
        findViewById(R.id.rl_meeting_address).setOnClickListener(this);
        findViewById(R.id.rl_meeting_time).setOnClickListener(this);
        findViewById(R.id.rl_meeting_contacts).setOnClickListener(this);
        findViewById(R.id.rl_meeting_phoneNum).setOnClickListener(this);
        findViewById(R.id.btn_confirm_submit).setOnClickListener(this);

        etMeetingContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                tvMeetingContentLimit.setText(s.length() + "/500");
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (mIntent == null) {
            mIntent = new Intent();
        }
        switch (v.getId()) {
            case R.id.rl_meeting_title: //会议主题
                mIntent.setClass(this, CompanyEditInfoActivity.class);
                mIntent.putExtra("OLD_INFO", tvMeetingTitle.getText().toString().trim());
                mIntent.putExtra("EDIT_TYPE", 71);
                startActivityForResult(mIntent, 71);
                break;
            case R.id.rl_meeting_cover:     //会议封面
                addMeetingCover(72);
                break;
            case R.id.rl_meeting_deadline:  //报名截止时间
                selectTime(MEETING_DEADLINE);
                break;
            case R.id.rl_meeting_organizer:  //承办单位 74
                mIntent.setClass(this, CompanyEditInfoActivity.class);
                mIntent.putExtra("OLD_INFO", tvMeetingOrganizer.getText().toString().trim());
                mIntent.putExtra("EDIT_TYPE", 74);
                startActivityForResult(mIntent, 74);
                break;
            case R.id.rl_meeting_address:   //会议地址
                mIntent.setClass(this, CompanyEditInfoActivity.class);
                mIntent.putExtra("OLD_INFO", tvMeetingAddress.getText().toString().trim());
                mIntent.putExtra("EDIT_TYPE", 73);
                startActivityForResult(mIntent, 73);
                break;
            case R.id.rl_meeting_time:      //会议时间
                selectTime(MEETING_TIME);
                break;
            case R.id.rl_meeting_contacts:  //联系人 75
                mIntent.setClass(this, CompanyEditInfoActivity.class);
                mIntent.putExtra("OLD_INFO", tvMeetingContacts.getText().toString().trim());
                mIntent.putExtra("EDIT_TYPE", 75);
                startActivityForResult(mIntent, 75);
                break;
            case R.id.rl_meeting_phoneNum:  //电话号码 76
                mIntent.setClass(this, CompanyEditInfoActivity.class);
                mIntent.putExtra("OLD_INFO", tvMeetingPhoneNum.getText().toString().trim());
                mIntent.putExtra("EDIT_TYPE", 76);
                startActivityForResult(mIntent, 76);
                break;
            case R.id.btn_confirm_submit:   //确认发布
                checkMeetingInfo();
                break;
            default:
                break;
        }
    }

    private void addMeetingCover(final int requestCode) {
        //设置ImagePicker为单选
        ImagePicker.getInstance().setMultiMode(false);
        List<String> names = new ArrayList<>();
        names.add("拍照");
        names.add("相册");
        showDialog(new SelectDialog.SelectDialogListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0: // 直接调起相机
                        /**
                         * 0.4.7 目前直接调起相机不支持裁剪，如果开启裁剪后不会返回图片，请注意，后续版本会解决
                         * 但是当前直接依赖的版本已经解决，考虑到版本改动很少，所以这次没有上传到远程仓库
                         * 如果实在有所需要，请直接下载源码引用。
                         */
                        //打开选择,本次允许选择的数量
                        ImagePicker.getInstance().setSelectLimit(1);
                        Intent intent = new Intent(MeetingReleaseActivity.this, ImageGridActivity.class);
                        intent.putExtra(ImageGridActivity.EXTRAS_TAKE_PICKERS, true); // 是否是直接打开相机
                        startActivityForResult(intent, requestCode);
                        break;
                    case 1:
                        //打开选择,本次允许选择的数量
                        ImagePicker.getInstance().setSelectLimit(1);
                        Intent intent1 = new Intent(MeetingReleaseActivity.this, ImageGridActivity.class);
                        /* 如果需要进入选择的时候显示已经选中的图片，详情请查看ImagePickerActivity*/
                        //intent1.putExtra(ImageGridActivity.EXTRAS_IMAGES,images);
                        startActivityForResult(intent1, requestCode);
                        break;
                    default:
                        break;
                }
            }
        }, names);
    }

    private SelectDialog showDialog(SelectDialog.SelectDialogListener listener, List<String> names) {
        SelectDialog dialog = new SelectDialog(this, R.style.transparentFrameWindowStyle, listener, names);
        if (!this.isFinishing()) {
            dialog.show();
        }
        return dialog;
    }

    private void selectTime(int timeType) {
        Calendar selectedDate = Calendar.getInstance();
        Date curDate = new Date(System.currentTimeMillis()); //获取系统当前时间
        selectedDate.setTime(curDate);
        //selectedDate.set(1991, 0, 1);
        //时间选择器
        TimePickerView pvTime = new TimePickerView.Builder(this, new TimePickerView.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {//选中事件回调
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                String strData = formatter.format(date);
                long selectTime = date.getTime() / 1000;                //java的时间戳是毫秒级的
                long currentTime = System.currentTimeMillis() / 1000;   //除以1000变为秒级,与PHP同步

                //不能选择以前的时间
                LogUtils.show(TAG, " selectDay = " + (selectTime / 3600 / 24));
                LogUtils.show(TAG, "currentDay = " + (currentTime / 3600 / 24));
                if ((selectTime / 3600 / 24) < (currentTime / 3600 / 24)) {//
                    ToastUtil.showShort(MeetingReleaseActivity.this, "您不能随意穿越");
                    return;
                }
                switch (timeType) {
                    case MEETING_DEADLINE:
                        meetingDeadlineSeconds = selectTime;
                        if (meetingTimeSeconds != 0 && (meetingDeadlineSeconds / 3600 / 24) > (meetingTimeSeconds / 3600 / 24)) {
                            ToastUtil.showShort(MeetingReleaseActivity.this, "报名截止时间不能晚于会议时间");
                        } else {
                            tvMeetingDeadline.setText(strData);
                        }
                        break;
                    case MEETING_TIME:
                        meetingTimeSeconds = selectTime;
                        if (meetingDeadlineSeconds != 0 && (meetingTimeSeconds / 3600 / 24) < (meetingDeadlineSeconds / 3600 / 24)) {
                            ToastUtil.showShort(MeetingReleaseActivity.this, "会议时间不能早于报名截止时间");
                        } else {
                            tvMeetingTime.setText(strData);
                        }
                        break;
                    default:
                        break;
                }
            }
        }).setType(new boolean[]{true, true, true, false, false, false})// 默认全部显示
                .setCancelText("取消")//取消按钮文字
                .setSubmitText("确认")//确认按钮文字
                .setContentSize(18)//滚轮文字大小
                .setTitleSize(18)//标题文字大小
                .setTitleText(timeType == MEETING_DEADLINE ? "选择报名截止时间" : "选择会议时间")//标题文字          //
                .setOutSideCancelable(true)//点击屏幕，点在控件外部范围时，是否取消显示
                .isCyclic(false)//是否循环滚动
                .setTitleColor(0xFFFFFFFF)//标题文字颜色  EB4F6F
                .setSubmitColor(0xFFFFFFFF)//确定按钮文字颜色
                .setCancelColor(0xFFFFFFFF)//取消按钮文字颜色
                .setTitleBgColor(0xFFEB4F6F)//标题背景颜色 Night mode
                .setBgColor(0xFFFFFFFF)//滚轮背景颜色 Night mode
                .setDate(selectedDate)// 如果不设置的话，默认是系统时间*/
                //.setRangDate(startDate,endDate)//起始终止年月日设定
                .setLabel("年", "月", "日", "时", "分", "秒")//默认设置为年月日时分秒
                .isCenterLabel(false) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
                .isDialog(false)//是否显示为对话框样式
                .build();
        pvTime.show();
    }

    //,  ,
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 60 && data != null) {
            String editInfo = data.getStringExtra("EDIT_INFO");
            switch (requestCode) {
                case 71: //发布会议
                    tvMeetingTitle.setText(editInfo);
                    break;
                case 73: //会议地址
                    tvMeetingAddress.setText(editInfo);
                    break;
                case 74: //承办单位
                    tvMeetingOrganizer.setText(editInfo);
                    break;
                case 75: //联系人
                    tvMeetingContacts.setText(editInfo);
                    break;
                case 76: //电话号码
                    tvMeetingPhoneNum.setText(editInfo);
                    break;
                default:
                    break;
            }
        } else if (resultCode == ImagePicker.RESULT_CODE_ITEMS && data != null) {
            ArrayList<ImageItem> images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
            Log.i(TAG, "onActivityResult: images.size() = " + images.size());
            if (images != null && images.size() > 0) {
                ImageItem imageItem = images.get(0);
                switch (requestCode) {
                    case 72:  //会议封面
                        //设置图片
                        ImagePicker.getInstance().getImageLoader().displayImage(MeetingReleaseActivity.this, imageItem.path, ivMeetingCover, 0, 0);
                        meetingCoverPath = imageItem.path;
                        LogUtils.show(TAG, "onActivityResult: meetingCoverPath = " + meetingCoverPath + "---");
                        break;
                    default:
                        break;
                }
            }
        }
    }

    private void checkMeetingInfo() {
        String meetingTitle = tvMeetingTitle.getText().toString().trim();
        String meetingDeadline = tvMeetingDeadline.getText().toString().trim();
        String meetingOrganizer = tvMeetingOrganizer.getText().toString().trim();
        String meetingAddress = tvMeetingAddress.getText().toString().trim();
        String meetingTime = tvMeetingTime.getText().toString().trim();
        String meetingContacts = tvMeetingContacts.getText().toString().trim();
        String meetingPhoneNum = tvMeetingPhoneNum.getText().toString().trim();
        String meetingContent = etMeetingContent.getText().toString().trim();

        if (TextUtils.isEmpty(meetingTitle) || TextUtils.equals(meetingTitle, "请填写") || TextUtils.equals(meetingTitle, "请选择")) {
            ToastUtil.showShort(this, "会议主题不能为空");
            return;
        }
        if (TextUtils.isEmpty(meetingCoverPath)) {
            Toast.makeText(this, "会议封面不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(meetingDeadline) || TextUtils.equals(meetingDeadline, "请填写") || TextUtils.equals(meetingDeadline, "请选择")) {
            ToastUtil.showShort(this, "报名截止时间不能为空");
            return;
        }
        if (TextUtils.isEmpty(meetingOrganizer) || TextUtils.equals(meetingOrganizer, "请填写") || TextUtils.equals(meetingOrganizer, "请选择")) {
            ToastUtil.showShort(this, "承办单位不能为空");
            return;
        }
        if (meetingDeadlineSeconds == 0 || (meetingDeadlineSeconds / 3600 / 24) < (System.currentTimeMillis() / 1000 / 3600 / 24)) {
            ToastUtil.showShort(this, "报名截止时间有误,请重新选择");
            return;
        }
        if (TextUtils.isEmpty(meetingAddress) || TextUtils.equals(meetingAddress, "请填写") || TextUtils.equals(meetingAddress, "请选择")) {
            ToastUtil.showShort(this, "会议地点时间不能为空");
            return;
        }
        if (TextUtils.isEmpty(meetingTime) || TextUtils.equals(meetingTime, "请填写") || TextUtils.equals(meetingTime, "请选择")) {
            ToastUtil.showShort(this, "会议时间不能为空");
            return;
        }
        if (meetingTimeSeconds == 0 || (meetingTimeSeconds / 3600 / 24) < (System.currentTimeMillis() / 1000 / 3600 / 24)) {
            ToastUtil.showShort(this, "会议时间有误,请重新选择");
            return;
        }

        if (TextUtils.isEmpty(meetingContacts) || TextUtils.equals(meetingContacts, "请填写") || TextUtils.equals(meetingContacts, "请选择")) {
            ToastUtil.showShort(this, "联系人不能为空");
            return;
        }
        if (TextUtils.isEmpty(meetingPhoneNum) || TextUtils.equals(meetingPhoneNum, "请填写") || TextUtils.equals(meetingPhoneNum, "请选择")) {
            ToastUtil.showShort(this, "联系电话不能为空");
            return;
        }
        if (!meetingPhoneNum.matches(GlobalConstants.STR_PHONE_REGEX2)) { //用正则表达式判断手机号是否合格
            ToastUtil.showShort(this, "请输入正确的联系电话");
            return;
        }

        if (TextUtils.isEmpty(meetingContent) || TextUtils.equals(meetingContent, "请填写") || TextUtils.equals(meetingContent, "请选择")) {
            ToastUtil.showShort(this, "会议内容不能为空");
            return;
        }
        gotoSubmitMeetingInfo(meetingTitle, meetingDeadline, meetingOrganizer, meetingAddress, meetingTime, meetingContacts, meetingPhoneNum, meetingContent);
    }

    private void gotoSubmitMeetingInfo(String meetingTitle, String meetingDeadline, String meetingOrganizer, String meetingAddress, String meetingTime, String meetingContacts, String meetingPhoneNum, String meetingContent) {
        sfProgressDialog.show();
        String mUserId = SPUtils.getString(this, GlobalConstants.userID, "");
        OkHttpUtils.post().url(GlobalConstants.URL_INFO_ADD)
                .addParams("catId", "14")                                  //必要参数 catId 分类  //14 会议报名
                .addParams("areaId", "")                                   //必要参数 areaId城市（默认西安可不传递，其它城市需要此参数）
                .addParams("userid", mUserId)                                   //用户id   必须
                .addParams("title", meetingTitle)                               //会议主题 字符串
                .addFile("thumb", "meetingCover" + mUserId + ".png", new File(meetingCoverPath)) //会议封面
                .addParams("enddate", String.valueOf(meetingDeadlineSeconds))   //截止报名时间   时间戳
                .addParams("organizer", meetingOrganizer)                       //组织单位
                .addParams("meetingPlace", meetingAddress)                      //会议地点  字符串
                .addParams("meetingTime", meetingTime)                          //会议时间 字符串
                .addParams("linkman", meetingContacts)                          //联系人姓名   必须
                .addParams("phone", meetingPhoneNum)                            //联系电话   必须
                .addParams("content", meetingContent)                           //会议内容  字符串
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                sfProgressDialog.dismiss();
                LogUtils.show(TAG, "---网络异常,发布会议失败,请重试---");
                Toast.makeText(MeetingReleaseActivity.this, "网络异常,发布会议失败,请重试", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(String response, int id) {
                sfProgressDialog.dismiss();
                LogUtils.show(TAG, "---简历会议成功:response = " + response + "---");
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String data = jsonObject.getString("data");
                    String message = jsonObject.getString("message");
                    Toast.makeText(MeetingReleaseActivity.this, message, Toast.LENGTH_SHORT).show();
                    if (TextUtils.equals(data, "success")) {
                        //startActivity(new Intent(MeetingReleaseActivity.this, 会议列表.class));
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(MeetingReleaseActivity.this, "数据异常,请重试", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}