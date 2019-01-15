package com.xasfemr.meiyaya.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.pickerview.TimePickerView;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.lljjcoder.citypickerview.widget.CityPicker;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.ui.ImageGridActivity;
import com.xasfemr.meiyaya.R;
import com.xasfemr.meiyaya.bean.EditUserInfoData;
import com.xasfemr.meiyaya.bean.LookUserData;
import com.xasfemr.meiyaya.global.GlobalConstants;
import com.xasfemr.meiyaya.main.BaseActivity;
import com.xasfemr.meiyaya.utils.SPUtils;
import com.xasfemr.meiyaya.utils.SelectDialog;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;

public class UserPagerEditInfoActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "UserPagerEditInfo";

    public static final int REQ_CODE_USER_ICON     = 11;
    public static final int REQ_CODE_USER_PAGER_BG = 12;

    private final String[] professionArr = {"美容机构代表/美容院院长", "美容院店长", "高级美容师", "美业技师/学徒", "整形医生/高级护理", "高级美容顾问/咨询师", "美业协会工作人员", "美业产品、仪器生产厂商/代理商、供应商", "美业产品销售人员", "美容治疗师", "美导（助教老师）", "美容行业教育培训人员", "美业仪器操作师", "美业其他从业人员", "其他行业"};
    private int[] selectProfArr;
    private int     intGender = 0;
    private boolean isEdit    = false; //用户是否编辑数据

    private Intent          mIntent;
    private Intent          editIntent;
    private ImageView       ivTopBack;
    private TextView        tvTopRight;
    private RelativeLayout  rlUserIcon;
    private CircleImageView civUserIcon;
    private RelativeLayout  rlUserPagerBg;
    private ImageView       ivUserPagerBg;
    private RelativeLayout  rlUserNickname;
    private TextView        tvUserNickname;
    private RelativeLayout  rlUserGender;
    private TextView        tvUserGender;
    private RelativeLayout  rlUserBirthday;
    private TextView        tvUserBirthday;
    private RelativeLayout  rlUserDistrict;
    private TextView        tvUserDistrict;
    private RelativeLayout  rlUserProfession;
    private TextView        tvUserProfession;
    private EditText        etUserIntroduce;
    private TextView        tvUserIntroduceLimit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_pager_edit_info);
        initTopBar();
        setTopTitleText("编辑资料");

        ivTopBack = (ImageView) findViewById(R.id.iv_top_back);
        tvTopRight = (TextView) findViewById(R.id.tv_top_right);
        rlUserIcon = (RelativeLayout) findViewById(R.id.rl_user_icon);
        civUserIcon = (CircleImageView) findViewById(R.id.civ_user_icon);
        rlUserPagerBg = (RelativeLayout) findViewById(R.id.rl_user_pager_bg);
        ivUserPagerBg = (ImageView) findViewById(R.id.iv_user_pager_bg);
        rlUserNickname = (RelativeLayout) findViewById(R.id.rl_user_nickname);
        tvUserNickname = (TextView) findViewById(R.id.tv_user_nickname);
        rlUserGender = (RelativeLayout) findViewById(R.id.rl_user_gender);
        tvUserGender = (TextView) findViewById(R.id.tv_user_gender);
        rlUserBirthday = (RelativeLayout) findViewById(R.id.rl_user_birthday);
        tvUserBirthday = (TextView) findViewById(R.id.tv_user_birthday);
        rlUserDistrict = (RelativeLayout) findViewById(R.id.rl_user_district);
        tvUserDistrict = (TextView) findViewById(R.id.tv_user_district);
        rlUserProfession = (RelativeLayout) findViewById(R.id.rl_user_profession);
        tvUserProfession = (TextView) findViewById(R.id.tv_user_profession);
        etUserIntroduce = (EditText) findViewById(R.id.et_user_introduce);
        tvUserIntroduceLimit = (TextView) findViewById(R.id.tv_user_introduce_limit);

        rlUserIcon.setOnClickListener(this);
        rlUserPagerBg.setOnClickListener(this);
        rlUserNickname.setOnClickListener(this);
        rlUserGender.setOnClickListener(this);
        rlUserBirthday.setOnClickListener(this);
        rlUserDistrict.setOnClickListener(this);
        rlUserProfession.setOnClickListener(this);

        setTopRight();

        editIntent = getIntent();
        LookUserData.UserInfo lookUserInfo = (LookUserData.UserInfo) editIntent.getSerializableExtra("LOOK_USER_INFO");


        Glide.with(UserPagerEditInfoActivity.this).load(lookUserInfo.images).into(civUserIcon);
        Glide.with(UserPagerEditInfoActivity.this).load(lookUserInfo.bgimg).into(ivUserPagerBg);
        tvUserNickname.setText(lookUserInfo.username);
        //用户性别 sex：性别（0未知，1男，2女）
        if (lookUserInfo.sex == 1) {
            tvUserGender.setText("男");
        } else if (lookUserInfo.sex == 2) {
            tvUserGender.setText("女");
        } else {
            tvUserGender.setText("");
        }
        tvUserBirthday.setText(lookUserInfo.birthday);
        tvUserDistrict.setText(lookUserInfo.region);
        tvUserProfession.setText(lookUserInfo.profession);
        etUserIntroduce.setText(lookUserInfo.signture);
        etUserIntroduce.setSelection(lookUserInfo.signture.length());
        tvUserIntroduceLimit.setText(lookUserInfo.signture.length() + "/100");
    }

    private void setTopRight() {
        tvTopRight.setVisibility(View.VISIBLE);
        tvTopRight.setText("保存");
        tvTopRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(UserPagerEditInfoActivity.this, "保存", Toast.LENGTH_SHORT).show();
                gotoSaveUserInfo();
            }
        });

        ivTopBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editIntent.putExtra("IS_EDIT", isEdit);
                setResult(20, editIntent);

                finish();
            }
        });

        etUserIntroduce.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                tvUserIntroduceLimit.setText(s.length() + "/100");
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (mIntent == null) {
            mIntent = new Intent();
        }
        switch (v.getId()) {
            case R.id.rl_user_icon:   //用户头像    11
                addUserIcon();
                break;
            case R.id.rl_user_pager_bg: //主页背景   12
                addUserPagerBg();
                break;
            case R.id.rl_user_nickname:  //昵称     13
                mIntent.setClass(UserPagerEditInfoActivity.this, UserPagerEditNameActivity.class);
                mIntent.putExtra("NickName", tvUserNickname.getText().toString());
                startActivityForResult(mIntent, 13);
                break;
            case R.id.rl_user_gender:  //性别     14
                String userGender = tvUserGender.getText().toString().trim();
                showGenderDialog(userGender);
                break;
            case R.id.rl_user_birthday:  //生日    15
                selectDate();
                break;
            case R.id.rl_user_district:  //地区    16
                selectAddress();
                break;
            case R.id.rl_user_profession:  //职业   17
                mIntent.setClass(UserPagerEditInfoActivity.this, UserPagerSelectProfActivity.class);
                mIntent.putExtra("PROFESSION", 0);
                startActivityForResult(mIntent, 17);
                break;
            default:
                break;
        }
    }

    private SelectDialog showDialog(SelectDialog.SelectDialogListener listener, List<String> names) {
        SelectDialog dialog = new SelectDialog(this, R.style.transparentFrameWindowStyle, listener, names);
        if (!this.isFinishing()) {
            dialog.show();
        }
        return dialog;
    }

    private void addUserIcon() {
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
                        Intent intent = new Intent(UserPagerEditInfoActivity.this, ImageGridActivity.class);
                        intent.putExtra(ImageGridActivity.EXTRAS_TAKE_PICKERS, true); // 是否是直接打开相机
                        startActivityForResult(intent, REQ_CODE_USER_ICON);
                        break;
                    case 1:
                        //打开选择,本次允许选择的数量
                        ImagePicker.getInstance().setSelectLimit(1);
                        Intent intent1 = new Intent(UserPagerEditInfoActivity.this, ImageGridActivity.class);
                        /* 如果需要进入选择的时候显示已经选中的图片，详情请查看ImagePickerActivity*/
                        //intent1.putExtra(ImageGridActivity.EXTRAS_IMAGES,images);
                        startActivityForResult(intent1, REQ_CODE_USER_ICON);
                        break;
                    default:
                        break;
                }
            }
        }, names);
    }

    private void addUserPagerBg() {

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
                        Intent intent = new Intent(UserPagerEditInfoActivity.this, ImageGridActivity.class);
                        intent.putExtra(ImageGridActivity.EXTRAS_TAKE_PICKERS, true); // 是否是直接打开相机
                        startActivityForResult(intent, REQ_CODE_USER_PAGER_BG);
                        break;
                    case 1:
                        //打开选择,本次允许选择的数量
                        ImagePicker.getInstance().setSelectLimit(1);
                        Intent intent1 = new Intent(UserPagerEditInfoActivity.this, ImageGridActivity.class);
                        /* 如果需要进入选择的时候显示已经选中的图片，详情请查看ImagePickerActivity*/
                        //intent1.putExtra(ImageGridActivity.EXTRAS_IMAGES,images);
                        startActivityForResult(intent1, REQ_CODE_USER_PAGER_BG);
                        break;
                    default:
                        break;
                }
            }
        }, names);

    }

    private void showGenderDialog(String userGender) {

        AlertDialog.Builder builder = new AlertDialog.Builder(UserPagerEditInfoActivity.this);
        View view = View.inflate(this, R.layout.dialog_gender, null);
        builder.setView(view);

        RelativeLayout rlGenderBoy = (RelativeLayout) view.findViewById(R.id.rl_gender_boy);
        ImageView ivGenderBoy = (ImageView) view.findViewById(R.id.iv_gender_boy);
        RelativeLayout rlGenderGril = (RelativeLayout) view.findViewById(R.id.rl_gender_gril);
        ImageView ivGenderGril = (ImageView) view.findViewById(R.id.iv_gender_gril);

        AlertDialog dialog = builder.create();

        //用户性别 sex：性别（0未知，1男，2女）
        if (TextUtils.equals(userGender, "男")) {
            intGender = 1;
            ivGenderBoy.setVisibility(View.VISIBLE);
            ivGenderGril.setVisibility(View.GONE);
        } else if (TextUtils.equals(userGender, "女")) {
            intGender = 2;
            ivGenderBoy.setVisibility(View.GONE);
            ivGenderGril.setVisibility(View.VISIBLE);
        } else {
            intGender = 0;
        }

        rlGenderBoy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ivGenderBoy.setVisibility(View.VISIBLE);
                ivGenderGril.setVisibility(View.GONE);
                tvUserGender.setText("男");
                intGender = 1;
                gotoSaveGender("1");
                dialog.dismiss();
            }
        });

        rlGenderGril.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ivGenderBoy.setVisibility(View.GONE);
                ivGenderGril.setVisibility(View.VISIBLE);
                tvUserGender.setText("女");
                intGender = 2;
                gotoSaveGender("2");
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void gotoSaveGender(String strGender) {
        String mUserId = SPUtils.getString(UserPagerEditInfoActivity.this, GlobalConstants.userID, "");

        OkHttpUtils.post().url(GlobalConstants.URL_EDIT_MY_INFO)
                .addParams("id", mUserId)
                .addParams("sex", strGender)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.i(TAG, "onError: ---网络异常,编辑性别失败---");
                        Toast.makeText(UserPagerEditInfoActivity.this, "网络异常,编辑性别失败", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.i(TAG, "onResponse: ---编辑性别访问服务器成功---response = " + response + "---");
                        try {
                            Gson gson = new Gson();
                            EditUserInfoData editUserInfoData = gson.fromJson(response, EditUserInfoData.class);
                            switch (editUserInfoData.status) {
                                case 201:
                                case 203:
                                case 204:
                                case 205:
                                    Toast.makeText(UserPagerEditInfoActivity.this, editUserInfoData.message, Toast.LENGTH_SHORT).show();
                                    break;

                                case 202:
                                    Toast.makeText(UserPagerEditInfoActivity.this, editUserInfoData.message, Toast.LENGTH_SHORT).show();
                                    if (editUserInfoData.data.sex == 1) {
                                        tvUserGender.setText("男");
                                    } else if (editUserInfoData.data.sex == 2) {
                                        tvUserGender.setText("女");
                                    } else {
                                        tvUserGender.setText("");
                                    }
                                    isEdit = true;
                                    break;

                                default:
                                    break;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    private void selectDate() {
        Calendar selectedDate = Calendar.getInstance();
        selectedDate.set(1991, 0, 1);
        //时间选择器
        TimePickerView pvTime = new TimePickerView.Builder(this, new TimePickerView.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {//选中事件回调
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                String strData = formatter.format(date);
                //tvUserBirthday.setText(strData);
                gotoSaveBirthday(strData);
            }
        }).setType(new boolean[]{true, true, true, false, false, false})// 默认全部显示
                .setCancelText("取消")//取消按钮文字
                .setSubmitText("确认")//确认按钮文字
                .setContentSize(16)//滚轮文字大小
                .setTitleSize(18)//标题文字大小
                .setTitleText("选择日期")//标题文字
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

    private void gotoSaveBirthday(String strBirthday) {
        String mUserId = SPUtils.getString(UserPagerEditInfoActivity.this, GlobalConstants.userID, "");

        OkHttpUtils.post().url(GlobalConstants.URL_EDIT_MY_INFO)
                .addParams("id", mUserId)
                .addParams("birthday", strBirthday)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.i(TAG, "onError: ---网络异常,编辑生日失败---");
                        Toast.makeText(UserPagerEditInfoActivity.this, "网络异常,编辑生日失败", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.i(TAG, "onResponse: ---编辑生日访问服务器成功---response = " + response + "---");
                        try {
                            Gson gson = new Gson();
                            EditUserInfoData editUserInfoData = gson.fromJson(response, EditUserInfoData.class);
                            switch (editUserInfoData.status) {
                                case 201:
                                case 203:
                                case 204:
                                case 205:
                                case 206:
                                    Toast.makeText(UserPagerEditInfoActivity.this, editUserInfoData.message, Toast.LENGTH_SHORT).show();
                                    break;

                                case 202:
                                    Toast.makeText(UserPagerEditInfoActivity.this, editUserInfoData.message, Toast.LENGTH_SHORT).show();
                                    tvUserBirthday.setText(editUserInfoData.data.birthday);
                                    isEdit = true;
                                    break;

                                default:
                                    break;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    private void selectAddress() {
        CityPicker cityPicker = new CityPicker.Builder(UserPagerEditInfoActivity.this)
                .textSize(15)
                .title("选择地址")
                .titleBackgroundColor("#EB4F6F")
                .titleTextColor("#FFFFFF")
                .confirTextColor("#FFFFFF")
                .cancelTextColor("#FFFFFF")
                .province("陕西省")
                .city("西安市")
                .district("雁塔区")
                .textColor(Color.parseColor("#000000"))
                .provinceCyclic(true)
                .cityCyclic(false)
                .districtCyclic(false)
                .visibleItemsCount(7)
                .itemPadding(10)
                .onlyShowProvinceAndCity(false)
                .build();
        cityPicker.show();
        //监听方法，获取选择结果
        cityPicker.setOnCityItemClickListener(new CityPicker.OnCityItemClickListener() {
            @Override
            public void onSelected(String... citySelected) {
                //省份
                String province = citySelected[0];
                //城市
                String city = citySelected[1];
                //区县（如果设定了两级联动，那么该项返回空）
                String district = citySelected[2];
                //邮编
                String code = citySelected[3];
                //为TextView赋值
                //tvUserDistrict.setText(province.trim() + "-" + city.trim() + "-" + district.trim());
                gotoSaveRegion(province.trim() + "-" + city.trim() + "-" + district.trim());
            }

            @Override
            public void onCancel() {

            }
        });
    }

    private void gotoSaveRegion(String region) {
        String mUserId = SPUtils.getString(UserPagerEditInfoActivity.this, GlobalConstants.userID, "");

        OkHttpUtils.post().url(GlobalConstants.URL_EDIT_MY_INFO)
                .addParams("id", mUserId)
                .addParams("region", region)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.i(TAG, "onError: ---网络异常,编辑地区失败---");
                        Toast.makeText(UserPagerEditInfoActivity.this, "网络异常,编辑地区失败", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.i(TAG, "onResponse: ---编辑地区访问服务器成功---response = " + response + "---");
                        try {
                            Gson gson = new Gson();
                            EditUserInfoData editUserInfoData = gson.fromJson(response, EditUserInfoData.class);
                            switch (editUserInfoData.status) {
                                case 201:
                                case 203:
                                case 204:
                                case 205:
                                    Toast.makeText(UserPagerEditInfoActivity.this, editUserInfoData.message, Toast.LENGTH_SHORT).show();
                                    break;

                                case 202:
                                    Toast.makeText(UserPagerEditInfoActivity.this, editUserInfoData.message, Toast.LENGTH_SHORT).show();
                                    tvUserDistrict.setText(editUserInfoData.data.region);
                                    isEdit = true;
                                    break;

                                default:
                                    break;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 13 && resultCode == 23) {                 //用户名
            String newNickName = data.getStringExtra("NewNickName");
            tvUserNickname.setText(newNickName);
            isEdit = true;

        } else if (requestCode == 17 && resultCode == 27) {          //职业
            int profSelected = data.getIntExtra("ProfSelected", 0);
            //tvUserProfession.setText(professionArr[profSelected]);
            gotoSaveProfession(professionArr[profSelected]);

        } else if (requestCode == REQ_CODE_USER_ICON && resultCode == ImagePicker.RESULT_CODE_ITEMS) {
            //添加图片返回
            if (data != null) {
                ArrayList<ImageItem> images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
                Log.i(TAG, "onActivityResult: images.size() = " + images.size());
                if (images != null) {
                    //设置图片
                    ImageItem imageItem = images.get(0);
                    //ImagePicker.getInstance().getImageLoader().displayImage(UserPagerEditInfoActivity.this, imageItem.path, civUserIcon, 0, 0);
                    Log.i(TAG, "onActivityResult: imageItem.path = " + imageItem.path + "---");
                    gotoSaveIcon(imageItem.path);
                }
            }
        } else if (requestCode == REQ_CODE_USER_PAGER_BG && resultCode == ImagePicker.RESULT_CODE_ITEMS) {
            //添加图片返回
            if (data != null) {
                ArrayList<ImageItem> images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
                Log.i(TAG, "onActivityResult: images.size() = " + images.size());
                if (images != null) {
                    //设置图片
                    ImageItem imageItem = images.get(0);
                    //ImagePicker.getInstance().getImageLoader().displayImage(UserPagerEditInfoActivity.this, imageItem.path, ivUserPagerBg, 0, 0);
                    Log.i(TAG, "onActivityResult: imageItem.path = " + imageItem.path + "---");
                    gotoSavePagerBg(imageItem.path);
                }
            }
        }
    }

    //保存职业
    private void gotoSaveProfession(String profession) {
        String mUserId = SPUtils.getString(UserPagerEditInfoActivity.this, GlobalConstants.userID, "");
        OkHttpUtils.post().url(GlobalConstants.URL_EDIT_MY_INFO)
                .addParams("id", mUserId)
                .addParams("profession", profession)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.i(TAG, "onError: ---网络异常,编辑职业失败---");
                        Toast.makeText(UserPagerEditInfoActivity.this, "网络异常,编辑职业失败,请重试", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.i(TAG, "onResponse: ---编辑职业访问服务器成功---response = " + response + "---");
                        try {
                            Gson gson = new Gson();
                            EditUserInfoData editUserInfoData = gson.fromJson(response, EditUserInfoData.class);
                            switch (editUserInfoData.status) {
                                case 201:
                                case 203:
                                case 204:
                                case 205:
                                    Toast.makeText(UserPagerEditInfoActivity.this, editUserInfoData.message, Toast.LENGTH_SHORT).show();
                                    break;

                                case 202:
                                    Toast.makeText(UserPagerEditInfoActivity.this, editUserInfoData.message, Toast.LENGTH_SHORT).show();
                                    //tvUserDistrict.setText(editUserInfoData.data.region);
                                    tvUserProfession.setText(editUserInfoData.data.profession);
                                    isEdit = true;
                                    break;

                                default:
                                    break;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    //保存主页背景
    private void gotoSavePagerBg(String pagerBgPath) {
        String mUserId = SPUtils.getString(UserPagerEditInfoActivity.this, GlobalConstants.userID, "");
        OkHttpUtils.post().url(GlobalConstants.URL_EDIT_MY_INFO)
                .addParams("id", mUserId)
                .addFile("bgimg", "pagerBg" + mUserId + ".png", new File(pagerBgPath))
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.i(TAG, "onError: ---网络异常,主页背景上传失败---");
                        Toast.makeText(UserPagerEditInfoActivity.this, "网络异常,主页背景上传失败,请重试", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.i(TAG, "onResponse: ---主页背景上传访问服务器成功---response = " + response + "---");
                        try {
                            Gson gson = new Gson();
                            EditUserInfoData editUserInfoData = gson.fromJson(response, EditUserInfoData.class);
                            switch (editUserInfoData.status) {
                                case 201:
                                case 203:
                                case 204:
                                case 205:
                                    Toast.makeText(UserPagerEditInfoActivity.this, editUserInfoData.message, Toast.LENGTH_SHORT).show();
                                    break;

                                case 202:
                                    Toast.makeText(UserPagerEditInfoActivity.this, editUserInfoData.message, Toast.LENGTH_SHORT).show();
                                    ImagePicker.getInstance().getImageLoader().displayImage(UserPagerEditInfoActivity.this, pagerBgPath, ivUserPagerBg, 0, 0);
                                    isEdit = true;
                                    break;

                                default:
                                    break;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    //保存用户头像
    private void gotoSaveIcon(String imagePath) {
        String mUserId = SPUtils.getString(UserPagerEditInfoActivity.this, GlobalConstants.userID, "");
        OkHttpUtils.post().url(GlobalConstants.URL_EDIT_MY_INFO)
                .addParams("id", mUserId)
                .addFile("images", "icon" + mUserId + ".png", new File(imagePath))
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.i(TAG, "onError: ---网络异常,头像上传失败---");
                        Toast.makeText(UserPagerEditInfoActivity.this, "网络异常,头像上传失败,请重试", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.i(TAG, "onResponse: ---头像上传访问服务器成功---response = " + response + "---");
                        try {
                            Gson gson = new Gson();
                            EditUserInfoData editUserInfoData = gson.fromJson(response, EditUserInfoData.class);
                            switch (editUserInfoData.status) {
                                case 201:
                                case 203:
                                case 204:
                                case 205:
                                    Toast.makeText(UserPagerEditInfoActivity.this, editUserInfoData.message, Toast.LENGTH_SHORT).show();
                                    break;

                                case 202:
                                    Toast.makeText(UserPagerEditInfoActivity.this, editUserInfoData.message, Toast.LENGTH_SHORT).show();
                                    ImagePicker.getInstance().getImageLoader().displayImage(UserPagerEditInfoActivity.this, imagePath, civUserIcon, 0, 0);
                                    isEdit = true;
                                    break;

                                default:
                                    break;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    //保存用户信息
    private void gotoSaveUserInfo() {
        String mUserId = SPUtils.getString(UserPagerEditInfoActivity.this, GlobalConstants.userID, "");
        String userIntroduce = etUserIntroduce.getText().toString().trim();

        OkHttpUtils.post().url(GlobalConstants.URL_EDIT_MY_INFO)
                .addParams("id", mUserId)
                .addParams("signture", userIntroduce)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.i(TAG, "onError: ---网络异常,个性签名编辑失败---");
                        Toast.makeText(UserPagerEditInfoActivity.this, "网络异常,用户信息编辑失败,请重试", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.i(TAG, "onResponse: ---编辑个性签名访问服务器成功---response = " + response + "---");
                        try {
                            Gson gson = new Gson();
                            EditUserInfoData editUserInfoData = gson.fromJson(response, EditUserInfoData.class);
                            switch (editUserInfoData.status) {
                                case 201:
                                    editIntent.putExtra("IS_EDIT", isEdit);
                                    setResult(20, editIntent);
                                    finish();
                                    break;

                                case 203:
                                case 204:
                                case 205:
                                case 206:
                                    Toast.makeText(UserPagerEditInfoActivity.this, editUserInfoData.message, Toast.LENGTH_SHORT).show();
                                    break;

                                case 202:
                                    Toast.makeText(UserPagerEditInfoActivity.this, editUserInfoData.message, Toast.LENGTH_SHORT).show();
                                    //tvUserDistrict.setText(editUserInfoData.data.region);
                                    etUserIntroduce.setText(editUserInfoData.data.signture);
                                    etUserIntroduce.setSelection(editUserInfoData.data.signture.length());
                                    //etUserIntroduce.setSelected(false);
                                    isEdit = true;

                                    editIntent.putExtra("IS_EDIT", isEdit);
                                    setResult(20, editIntent);
                                    finish();
                                    break;

                                default:
                                    Toast.makeText(UserPagerEditInfoActivity.this, "未知异常,请重试!", Toast.LENGTH_SHORT).show();
                                    break;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    @Override
    public void onBackPressed() {
        editIntent.putExtra("IS_EDIT", isEdit);
        setResult(20, editIntent);
        finish();
    }

}
