package com.xasfemr.meiyaya.activity;

import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.ui.ImageGridActivity;
import com.xasfemr.meiyaya.R;
import com.xasfemr.meiyaya.base.activity.MVPBaseActivity;
import com.xasfemr.meiyaya.global.GlobalConstants;
import com.xasfemr.meiyaya.utils.LogUtils;
import com.xasfemr.meiyaya.utils.SPUtils;
import com.xasfemr.meiyaya.utils.SelectDialog;
import com.xasfemr.meiyaya.view.LoadDataView;
import com.xasfemr.meiyaya.view.progress.SFProgressDialog;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;

import static com.xasfemr.meiyaya.R.id.btn_confirm_submit;
import static com.xasfemr.meiyaya.R.id.iv_top_back;
import static com.xasfemr.meiyaya.R.id.rl_business_license;
import static com.xasfemr.meiyaya.R.id.rl_company_CEO;
import static com.xasfemr.meiyaya.R.id.rl_company_address;
import static com.xasfemr.meiyaya.R.id.rl_company_contacts;
import static com.xasfemr.meiyaya.R.id.rl_company_logo;
import static com.xasfemr.meiyaya.R.id.rl_company_name;
import static com.xasfemr.meiyaya.R.id.rl_company_phone_number;

/*
* 企业认证
* */

public class CompanyAuthActivity extends MVPBaseActivity {
    private static final String TAG = "CompanyAuthActivity";
    private SFProgressDialog sfProgressDialog;

    private Intent mIntent;
    private String businessLicensePath;
    private String companyLogoPath;

    @BindView(R.id.tv_top_title)
    TextView        tvTitle;
    @BindView(R.id.tv_company_name)
    TextView        tvCompanyName;
    @BindView(R.id.tv_company_CEO)
    TextView        tvCompanyCEO;
    @BindView(R.id.iv_business_license)
    ImageView       ivBusinessLicense;
    @BindView(R.id.civ_company_logo)
    CircleImageView civCompanyLogo;
    @BindView(R.id.tv_company_contacts)
    TextView        tvCompanyContacts;
    @BindView(R.id.tv_company_phone_number)
    TextView        tvCompanyPhoneNumber;
    @BindView(R.id.tv_company_address)
    TextView        tvCompanyAddress;

    @Override
    protected int layoutId() {
        return R.layout.activity_company_auth;
    }

    @Override
    protected ViewGroup loadDataViewLayout() {
        return null;
    }

    @Override
    protected void initView() {
        sfProgressDialog = new SFProgressDialog(this);
        tvTitle.setText("企业认证");
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

    @OnClick({iv_top_back, rl_company_name, rl_company_CEO, rl_business_license, rl_company_logo, rl_company_contacts, rl_company_phone_number, rl_company_address, btn_confirm_submit})
    public void onViewClick(View v) {
        if (mIntent == null) {
            mIntent = new Intent();
        }
        switch (v.getId()) {
            case R.id.iv_top_back:
                finish();
                break;
            case R.id.rl_company_name:       // 公司名称
                mIntent.setClass(this, CompanyEditInfoActivity.class);
                mIntent.putExtra("OLD_INFO", tvCompanyName.getText().toString().trim());
                mIntent.putExtra("EDIT_TYPE", 51);
                startActivityForResult(mIntent, 51);
                break;
            case R.id.rl_company_CEO:        //公司法人
                mIntent.setClass(this, CompanyEditInfoActivity.class);
                mIntent.putExtra("OLD_INFO", tvCompanyCEO.getText().toString().trim());
                mIntent.putExtra("EDIT_TYPE", 52);
                startActivityForResult(mIntent, 52);
                break;
            case R.id.rl_business_license:   // 营业执照
                addCompanyPic(56);
                break;
            case R.id.rl_company_logo:       //公司logo
                addCompanyPic(57);
                break;
            case R.id.rl_company_contacts:     //联系人
                mIntent.setClass(this, CompanyEditInfoActivity.class);
                mIntent.putExtra("OLD_INFO", tvCompanyContacts.getText().toString().trim());
                mIntent.putExtra("EDIT_TYPE", 53);
                startActivityForResult(mIntent, 53);
                break;
            case R.id.rl_company_phone_number: //联系电话
                mIntent.setClass(this, CompanyEditInfoActivity.class);
                mIntent.putExtra("OLD_INFO", tvCompanyPhoneNumber.getText().toString().trim());
                mIntent.putExtra("EDIT_TYPE", 54);
                startActivityForResult(mIntent, 54);
                break;
            case R.id.rl_company_address:      //公司地址
                mIntent.setClass(this, CompanyEditInfoActivity.class);
                mIntent.putExtra("OLD_INFO", tvCompanyAddress.getText().toString().trim());
                mIntent.putExtra("EDIT_TYPE", 55);
                startActivityForResult(mIntent, 55);
                break;
            case R.id.btn_confirm_submit:      //确认提交
                checkCompanyInfo();
                break;
            default:
                break;
        }
    }

    private void addCompanyPic(final int requestCode) {
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
                        Intent intent = new Intent(CompanyAuthActivity.this, ImageGridActivity.class);
                        intent.putExtra(ImageGridActivity.EXTRAS_TAKE_PICKERS, true); // 是否是直接打开相机
                        startActivityForResult(intent, requestCode);
                        break;
                    case 1:
                        //打开选择,本次允许选择的数量
                        ImagePicker.getInstance().setSelectLimit(1);
                        Intent intent1 = new Intent(CompanyAuthActivity.this, ImageGridActivity.class);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 60 && data != null) {
            String editInfo = data.getStringExtra("EDIT_INFO");
            switch (requestCode) {
                case 51: //公司名称
                    tvCompanyName.setText(editInfo);
                    break;
                case 52: //公司法人
                    tvCompanyCEO.setText(editInfo);
                    break;
                case 53: //联系人
                    tvCompanyContacts.setText(editInfo);
                    break;
                case 54: //联系电话
                    tvCompanyPhoneNumber.setText(editInfo);
                    break;
                case 55:  //公司地址
                    tvCompanyAddress.setText(editInfo);
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
                    case 56:  //营业执照
                        //设置图片
                        ImagePicker.getInstance().getImageLoader().displayImage(CompanyAuthActivity.this, imageItem.path, ivBusinessLicense, 0, 0);
                        businessLicensePath = imageItem.path;
                        LogUtils.show(TAG, "onActivityResult: businessLicensePath = " + businessLicensePath + "---");
                        break;

                    case 57:  //公司logo
                        //设置图片
                        ImagePicker.getInstance().getImageLoader().displayImage(CompanyAuthActivity.this, imageItem.path, civCompanyLogo, 0, 0);
                        companyLogoPath = imageItem.path;
                        LogUtils.show(TAG, "onActivityResult: companyLogoPath = " + companyLogoPath + "---");
                        break;

                    default:
                        break;
                }
            }
        }
    }

    private void checkCompanyInfo() {
        String companyName = tvCompanyName.getText().toString().trim();
        String companyCEO = tvCompanyCEO.getText().toString().trim();
        String companyContacts = tvCompanyContacts.getText().toString().trim();
        String companyPhoneNumber = tvCompanyPhoneNumber.getText().toString().trim();
        String companyAddress = tvCompanyAddress.getText().toString().trim();

        if (TextUtils.isEmpty(companyName) || TextUtils.equals(companyName, "请填写")) {
            Toast.makeText(this, "公司名称不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(companyCEO) || TextUtils.equals(companyCEO, "请填写")) {
            Toast.makeText(this, "公司法人不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(businessLicensePath)) {
            Toast.makeText(this, "营业执照不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(companyLogoPath)) {
            Toast.makeText(this, "公司logo不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(companyContacts) || TextUtils.equals(companyContacts, "请填写")) {
            Toast.makeText(this, "联系人不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(companyPhoneNumber) || TextUtils.equals(companyPhoneNumber, "请填写")) {
            Toast.makeText(this, "联系电话不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!companyPhoneNumber.matches(GlobalConstants.STR_PHONE_REGEX2)) { //用正则表达式判断手机号是否合格
            Toast.makeText(this, "请输入正确的联系电话", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(companyAddress) || TextUtils.equals(companyAddress, "请填写")) {
            Toast.makeText(this, "公司地址不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        gotoSubmitApproveInfo(companyName, companyCEO, companyContacts, companyPhoneNumber, companyAddress);
    }


    private void gotoSubmitApproveInfo(String companyName, String companyCEO, String companyContacts, String companyPhoneNumber, String companyAddress) {
        sfProgressDialog.show();
        LogUtils.show(TAG, "companyName = " + companyName + ", companyCEO = " + companyCEO + ", companyContacts = " + companyContacts + ", " +
                "companyPhoneNumber = " + companyPhoneNumber + ", companyAddress = " + companyAddress + ", businessLicensePath = " + businessLicensePath + ", companyLogoPath = " + companyLogoPath);

        String mUserId = SPUtils.getString(this, GlobalConstants.userID, "");
        OkHttpUtils.post().url(GlobalConstants.URL_COMPANY_APPROVE)
                .addParams("userid", mUserId)
                .addParams("companyName", companyName)
                .addParams("comperson", companyCEO)
                .addParams("linkman", companyContacts)
                .addParams("link_phone", companyPhoneNumber)
                .addParams("address", companyAddress)
                .addFile("com_license", "BusinessLicense_" + mUserId + ".png", new File(businessLicensePath))
                .addFile("com_logo", "CompanyLogo_" + mUserId + ".png", new File(companyLogoPath))
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                sfProgressDialog.dismiss();
                LogUtils.show(TAG, "网络异常,上传公司认证信息失败");
                Toast.makeText(CompanyAuthActivity.this, "网络异常,上传公司认证信息失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(String response, int id) {
                sfProgressDialog.dismiss();
                LogUtils.show(TAG, "onResponse: ---上传公司认证信息成功---response = " + response + "---");
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String message = jsonObject.getString("message");
                    int code = jsonObject.getInt("code");
                    if (code == 200) {
                        Toast.makeText(CompanyAuthActivity.this, message, Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(CompanyAuthActivity.this, "上传失败,请重试", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(CompanyAuthActivity.this, "数据异常,请重试", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
