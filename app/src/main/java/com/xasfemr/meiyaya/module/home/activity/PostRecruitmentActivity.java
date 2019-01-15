package com.xasfemr.meiyaya.module.home.activity;

import android.content.Intent;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.TextView;

import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.ui.ImageGridActivity;
import com.lzy.imagepicker.ui.ImagePreviewDelActivity;
import com.xasfemr.meiyaya.R;
import com.xasfemr.meiyaya.activity.CompanyEditInfoActivity;
import com.xasfemr.meiyaya.base.activity.MVPBaseActivity;
import com.xasfemr.meiyaya.global.GlobalConstants;
import com.xasfemr.meiyaya.module.home.IView.PostFilterListIView;
import com.xasfemr.meiyaya.module.home.presenter.PostPresenter;
import com.xasfemr.meiyaya.module.home.protocol.PostProtocol;
import com.xasfemr.meiyaya.utils.ImagePickerAdapter;
import com.xasfemr.meiyaya.utils.LocationUtils;
import com.xasfemr.meiyaya.utils.LogUtils;
import com.xasfemr.meiyaya.utils.SPUtils;
import com.xasfemr.meiyaya.utils.SelectDialog;
import com.xasfemr.meiyaya.utils.ToastUtil;
import com.xasfemr.meiyaya.view.BottomListDialog;
import com.xasfemr.meiyaya.view.LoadDataView;
import com.xasfemr.meiyaya.view.progress.SFProgressDialog;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.builder.PostFormBuilder;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;

/**
 * 发布招聘
 * Created by sen.luo on 2018/2/24.
 */

public class PostRecruitmentActivity extends MVPBaseActivity implements ImagePickerAdapter.OnRecyclerViewItemClickListener {
    private static final String TAG = "PostRecruitActivity";

    @BindView(R.id.tv_top_title)
    TextView     tvTitle;
    @BindView(R.id.tv_recruit_company)
    TextView     tvRecruitCompany;
    @BindView(R.id.tv_recruit_job)
    TextView     tvRecruitJob;
    @BindView(R.id.tv_recruit_address)
    TextView     tvRecruitAddress;
    @BindView(R.id.tv_recruit_treatment)
    TextView     tvRecruitTreatment;
    @BindView(R.id.tv_recruit_contacts)
    TextView     tvRecruitContacts;
    @BindView(R.id.tv_recruit_phone)
    TextView     tvRecruitPhone;
    @BindView(R.id.etContent)
    EditText     etJobDetail;
    @BindView(R.id.tv_content_limit)
    TextView     tvContentLimit;
    @BindView(R.id.rvPhotoList)
    RecyclerView rvPhotoList;

    private PostPresenter        postPresenter;
    private SFProgressDialog     progressDialog;
    private Intent               mIntent;
    private ImagePickerAdapter   imagePickerAdapter;
    private ArrayList<ImageItem> seletedImgList; //当前选择的所有图片
    private             int maxImgCount          = 9;               //允许选择图片最大数
    public static final int IMAGE_ITEM_ADD       = -1;
    public static final int REQUEST_CODE_SELECT  = 100;
    public static final int REQUEST_CODE_PREVIEW = 101;

    private String longitude = "0.0";
    private String latitude  = "0.0";

    private ArrayList<String> stringArrayList;

    private ArrayList<String> recrumentArrayList;

    @Override
    protected int layoutId() {
        return R.layout.activity_post_recruitment;
    }

    @Override
    protected ViewGroup loadDataViewLayout() {
        return null;
    }

    @Override
    protected void initView() {
        tvTitle.setText("发布招聘");
        LocationUtils.initLocation(this);
        longitude = Double.toString(LocationUtils.longitude);
        latitude = Double.toString(LocationUtils.latitude);
        LogUtils.show(TAG, "[ longitude = " + longitude + ", latitude = " + latitude + " ]");

        stringArrayList=new ArrayList<>();
        seletedImgList = new ArrayList<>();
        imagePickerAdapter = new ImagePickerAdapter(this, seletedImgList, maxImgCount);
        imagePickerAdapter.setOnItemClickListener(this);

        rvPhotoList.setLayoutManager(new GridLayoutManager(this, 4));
        rvPhotoList.setHasFixedSize(true);
        rvPhotoList.setAdapter(imagePickerAdapter);

        etJobDetail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                tvContentLimit.setText(s.length() + "/300");
            }
        });



        recrumentArrayList=new ArrayList<>();

        recrumentArrayList.add("面议");
        recrumentArrayList.add("1000-2000元");
        recrumentArrayList.add("2001-3000元");
        recrumentArrayList.add("3001-4000元");
        recrumentArrayList.add("4001-5000元");
        recrumentArrayList.add("5001-7000元");
        recrumentArrayList.add("7000元以上");
    }

    @Override
    protected void initData() {

        HashMap<String,String> map =new HashMap<>();
        map.put("catId","5");
        postPresenter.postRecruitment(map, new PostFilterListIView() {
            @Override
            public void getFilterListSuccess(PostProtocol postProtocol) {
                stringArrayList.addAll(postProtocol.customList);


            }

            @Override
            public void getFilterListOnFailure(String msg) {
                ToastUtil.showShort(PostRecruitmentActivity.this,msg);
            }

            @Override
            public void onNetworkFailure(String message) {
                ToastUtil.showShort(PostRecruitmentActivity.this,message);

            }
        });
    }

    @Override
    protected void getLoadView(LoadDataView loadView) {
    }

    @Override
    protected void initPresenter() {
        postPresenter = new PostPresenter();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        postPresenter.destroy();
    }

    @OnClick({R.id.iv_top_back, R.id.ll_recruit_company, R.id.ll_recruit_job, R.id.ll_recruit_address, R.id.ll_recruit_treatment, R.id.ll_recruit_contacts, R.id.ll_recruit_phone, R.id.btn_confirm_submit})
    public void onViewClick(View v) {
        if (mIntent == null) {
            mIntent = new Intent();
        }
        switch (v.getId()) {
            case R.id.iv_top_back:
                finish();
                break;

            case R.id.ll_recruit_company:    //公司名称
                mIntent.setClass(this, CompanyEditInfoActivity.class);
                mIntent.putExtra("OLD_INFO", tvRecruitCompany.getText().toString().trim());
                mIntent.putExtra("EDIT_TYPE", 61);
                startActivityForResult(mIntent, 61);
                break;

            case R.id.ll_recruit_job:        //职位名称

                BottomListDialog ageDialog = new BottomListDialog(this, "", "请选择职位", stringArrayList);
                ageDialog.setOnItemClickListener(new BottomListDialog.OnItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        tvRecruitJob.setText(stringArrayList.get(position));
                    }
                });
                ageDialog.show();



//                mIntent.setClass(this, CompanyEditInfoActivity.class);
//                mIntent.putExtra("OLD_INFO", tvRecruitJob.getText().toString().trim());
//                mIntent.putExtra("EDIT_TYPE", 62);
//                startActivityForResult(mIntent, 62);
                break;

            case R.id.ll_recruit_address:    //工作地点
                mIntent.setClass(this, CompanyEditInfoActivity.class);
                mIntent.putExtra("OLD_INFO", tvRecruitAddress.getText().toString().trim());
                mIntent.putExtra("EDIT_TYPE", 63);
                startActivityForResult(mIntent, 63);
                break;

            case R.id.ll_recruit_treatment:  //待遇

                BottomListDialog treatmentDialog = new BottomListDialog(this, "", "请选择待遇", recrumentArrayList);
                treatmentDialog.setOnItemClickListener(new BottomListDialog.OnItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        tvRecruitTreatment.setText(recrumentArrayList.get(position));
                    }
                });
                treatmentDialog.show();


//                mIntent.setClass(this, CompanyEditInfoActivity.class);
//                mIntent.putExtra("OLD_INFO", tvRecruitTreatment.getText().toString().trim());
//                mIntent.putExtra("EDIT_TYPE", 64);
//                startActivityForResult(mIntent, 64);
                break;

            case R.id.ll_recruit_contacts:   // 联系人
                mIntent.setClass(this, CompanyEditInfoActivity.class);
                mIntent.putExtra("OLD_INFO", tvRecruitContacts.getText().toString().trim());
                mIntent.putExtra("EDIT_TYPE", 65);
                startActivityForResult(mIntent, 65);
                break;

            case R.id.ll_recruit_phone:      //联系电话
                mIntent.setClass(this, CompanyEditInfoActivity.class);
                mIntent.putExtra("OLD_INFO", tvRecruitPhone.getText().toString().trim());
                mIntent.putExtra("EDIT_TYPE", 66);
                startActivityForResult(mIntent, 66);
                break;

            case R.id.btn_confirm_submit:    //确认发布
                checkRecruitmentInfo();
                break;

            default:
                break;
        }
    }

    private void checkRecruitmentInfo() {
        String recruitCompany = tvRecruitCompany.getText().toString().trim();
        String recruitJob = tvRecruitJob.getText().toString().trim();
        String recruitAddress = tvRecruitAddress.getText().toString().trim();
        String recruitTreatment = tvRecruitTreatment.getText().toString().trim();
        String recruitContacts = tvRecruitContacts.getText().toString().trim();
        String recruitPhone = tvRecruitPhone.getText().toString().trim();
        String jobDetail = etJobDetail.getText().toString().trim();


        if (TextUtils.isEmpty(recruitCompany) || TextUtils.equals(recruitCompany, "请填写") || TextUtils.equals(recruitCompany, "请选择")) {
            ToastUtil.showShort(this, "公司名称不能为空");
            return;
        }
        if (TextUtils.isEmpty(recruitJob) || TextUtils.equals(recruitJob, "请填写") || TextUtils.equals(recruitJob, "请选择")) {
            ToastUtil.showShort(this, "职位名称不能为空");
            return;
        }
        if (TextUtils.isEmpty(recruitAddress) || TextUtils.equals(recruitAddress, "请填写") || TextUtils.equals(recruitAddress, "请选择")) {
            ToastUtil.showShort(this, "工作地点不能为空");
            return;
        }
        if (TextUtils.isEmpty(recruitTreatment) || TextUtils.equals(recruitTreatment, "请填写") || TextUtils.equals(recruitTreatment, "请选择")) {
            ToastUtil.showShort(this, "待遇不能为空");
            return;
        }
        if (TextUtils.isEmpty(recruitContacts) || TextUtils.equals(recruitContacts, "请填写") || TextUtils.equals(recruitContacts, "请选择")) {
            ToastUtil.showShort(this, "联系人不能为空");
            return;
        }
        if (TextUtils.isEmpty(recruitPhone) || TextUtils.equals(recruitPhone, "请填写") || TextUtils.equals(recruitPhone, "请选择")) {
            ToastUtil.showShort(this, "联系电话不能为空");
            return;
        }
        if (!recruitPhone.matches(GlobalConstants.STR_PHONE_REGEX2)) { //用正则表达式判断手机号是否合格
            ToastUtil.showShort(this, "请输入正确的联系电话");
            return;
        }
        if (TextUtils.isEmpty(jobDetail) || TextUtils.equals(jobDetail, "请填写") || TextUtils.equals(jobDetail, "请选择")) {
            ToastUtil.showShort(this, "职位详情不能为空");
            return;
        }
        if (seletedImgList == null || seletedImgList.size() == 0) {
            ToastUtil.showShort(this, "请至少选择一张公司图片");
            return;
        }
        submitData(recruitCompany, recruitJob, recruitAddress, recruitTreatment, recruitContacts, recruitPhone, jobDetail);
    }

    /**
     * 提交数据
     * @param recruitCompany
     * @param recruitJob
     * @param recruitAddress
     * @param recruitTreatment
     * @param recruitContacts
     * @param recruitPhone
     * @param jobDetail
     */
    private void submitData(String recruitCompany, String recruitJob, String recruitAddress, String recruitTreatment, String recruitContacts, String recruitPhone, String jobDetail) {
        if (progressDialog == null) {
            progressDialog = new SFProgressDialog(this);
        }
        progressDialog.show();
        String mUserId = SPUtils.getString(this, GlobalConstants.userID, "");

        PostFormBuilder postFormBuilder = OkHttpUtils.post().url(GlobalConstants.URL_INFO_ADD)
                .addParams("catId", "5")                  //catId 分类 catId=5 招聘
                .addParams("areaId", "")                  //areaId 城市（默认西安可不传递，其它城市需要此参数）
                .addParams("userid", mUserId)
                .addParams("companyName", recruitCompany) //公司名
                .addParams("jobName", recruitJob)         //职位名称
                .addParams("workPlace", recruitAddress)   //工作地点
                .addParams("address", recruitAddress)     //地址
                .addParams("salary", recruitTreatment)    //待遇
                .addParams("linkman", recruitContacts)    //联系人
                .addParams("phone", recruitPhone)         //电话
                .addParams("content", jobDetail)          //内容:职位详情
                .addParams("description", jobDetail)      //描述简介:职位详情
                .addParams("longitude", longitude)        //经度
                .addParams("latitude", latitude);         //纬度

        for (int i = 0; i < seletedImgList.size(); i++) {      //图片列表上传 可选（picture0, picture1, ....., picture8最多可以传9张图）
            postFormBuilder.addFile("picture" + i, "picture" + i + ".png", new File(seletedImgList.get(i).path));
        }

        postFormBuilder.build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                progressDialog.dismiss();
                LogUtils.show(TAG, "---网络异常,发布招聘失败,请重试---");
                ToastUtil.showShort(PostRecruitmentActivity.this, "网络异常,发布招聘失败,请重试");
                LogUtils.show("Exception-----", e.getMessage() + "");
            }

            @Override
            public void onResponse(String response, int id) {
                progressDialog.dismiss();
                LogUtils.show(TAG, "---简历招聘成功:response = " + response + "---");
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getString("data") != null && jsonObject.getString("message") != null && jsonObject.getString("data").equals("success")) {
                        ToastUtil.showShort(PostRecruitmentActivity.this, jsonObject.getString("message"));
                        startActivity(new Intent(PostRecruitmentActivity.this, RecruitmentListActivity.class));
                        finish();
                    } else {
                        ToastUtil.showShort(PostRecruitmentActivity.this, jsonObject.getString("message"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    ToastUtil.showShort(PostRecruitmentActivity.this, "数据异常,请重试");
                }
            }
        });
    }

    @Override
    public void onItemClick(View view, int position) {
        //设置ImagePicker为单选
        ImagePicker.getInstance().setMultiMode(true);
        switch (position) {
            case IMAGE_ITEM_ADD:
                List<String> names = new ArrayList<>();
                names.add("拍照");
                names.add("相册");
                showDialog(new SelectDialog.SelectDialogListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        switch (position) {
                            case 0: // 直接调起相机
                                //打开选择,本次允许选择的数量
                                ImagePicker.getInstance().setSelectLimit(maxImgCount - seletedImgList.size());
                                Intent intent = new Intent(PostRecruitmentActivity.this, ImageGridActivity.class);
                                intent.putExtra(ImageGridActivity.EXTRAS_TAKE_PICKERS, true); // 是否是直接打开相机
                                startActivityForResult(intent, REQUEST_CODE_SELECT);
                                break;
                            case 1:
                                //打开选择,本次允许选择的数量
                                ImagePicker.getInstance().setSelectLimit(maxImgCount - seletedImgList.size());
                                Intent intent1 = new Intent(PostRecruitmentActivity.this, ImageGridActivity.class);
                                /* 如果需要进入选择的时候显示已经选中的图片，
                                 * 详情请查看ImagePickerActivity
                                 * */
                                //                                intent1.putExtra(ImageGridActivity.EXTRAS_IMAGES,images);
                                startActivityForResult(intent1, REQUEST_CODE_SELECT);
                                break;
                            default:
                                break;
                        }
                    }
                }, names);
                break;
            default:
                //打开预览
                Intent intentPreview = new Intent(this, ImagePreviewDelActivity.class);
                intentPreview.putExtra(ImagePicker.EXTRA_IMAGE_ITEMS, (ArrayList<ImageItem>) imagePickerAdapter.getImages());
                intentPreview.putExtra(ImagePicker.EXTRA_SELECTED_IMAGE_POSITION, position);
                intentPreview.putExtra(ImagePicker.EXTRA_FROM_ITEMS, true);
                startActivityForResult(intentPreview, REQUEST_CODE_PREVIEW);
                break;
        }
    }

    //选取照片还是直接照相的dialog
    private SelectDialog showDialog(SelectDialog.SelectDialogListener listener, List<String> names) {
        SelectDialog dialog = new SelectDialog(this, R.style.transparentFrameWindowStyle, listener, names);
        if (!this.isFinishing()) {
            dialog.show();
        }
        return dialog;
    }


    private ArrayList<ImageItem> images = null;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 60 && data != null) {
            String editInfo = data.getStringExtra("EDIT_INFO");
            switch (requestCode) {
                case 61:
                    tvRecruitCompany.setText(editInfo);
                    break;
                case 62:
                    tvRecruitJob.setText(editInfo);
                    break;
                case 63:
                    tvRecruitAddress.setText(editInfo);
                    break;
                case 64:
                    tvRecruitTreatment.setText(editInfo);
                    break;
                case 65:
                    tvRecruitContacts.setText(editInfo);
                    break;
                case 66:
                    tvRecruitPhone.setText(editInfo);
                    break;
                default:
                    break;
            }
        }

        if (resultCode == ImagePicker.RESULT_CODE_ITEMS && requestCode == REQUEST_CODE_SELECT && data != null) {//添加图片返回
            images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
            if (images != null) {
                seletedImgList.addAll(images);
                imagePickerAdapter.setImages(seletedImgList);
            }
        } else if (resultCode == ImagePicker.RESULT_CODE_BACK && requestCode == REQUEST_CODE_PREVIEW && data != null) {//预览图片返回
            images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_IMAGE_ITEMS);
            if (images != null) {
                seletedImgList.clear();
                seletedImgList.addAll(images);
                imagePickerAdapter.setImages(seletedImgList);
            }
        }
    }
}