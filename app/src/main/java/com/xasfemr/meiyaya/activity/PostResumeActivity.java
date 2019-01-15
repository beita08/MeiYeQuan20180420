package com.xasfemr.meiyaya.activity;

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
import android.widget.Toast;

import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.ui.ImageGridActivity;
import com.lzy.imagepicker.ui.ImagePreviewDelActivity;
import com.xasfemr.meiyaya.R;
import com.xasfemr.meiyaya.base.activity.MVPBaseActivity;
import com.xasfemr.meiyaya.global.GlobalConstants;
import com.xasfemr.meiyaya.module.home.activity.RequestJobListActivity;
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
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;

/**
 * 发布简历
 */
public class PostResumeActivity extends MVPBaseActivity implements ImagePickerAdapter.OnRecyclerViewItemClickListener {
    private static final String TAG = "PostResumeActivity";

    private final String[] mAgeArr    = {"18岁", "19岁", "20岁", "21岁", "22岁", "23岁", "24岁", "25岁", "26岁", "27岁", "28岁", "29岁", "30岁", "31岁", "32岁", "33岁", "34岁", "35岁", "36岁", "37岁", "38岁", "39岁", "40岁", "41岁", "42岁", "43岁", "44岁", "45岁", "46岁", "47岁", "48岁", "49岁", "50岁"};
    private final String[] mProfArr   = {"美容机构代表/美容院院长", "美容院店长", "高级美容师", "美业技师/学徒", "整形医生/高级护理", "高级美容顾问/咨询师", "美业协会工作人员", "美业产品、仪器生产厂商/代理商、供应商", "美业产品销售人员", "美容治疗师", "美导（助教老师）", "美容行业教育培训人员", "美业仪器操作师", "美业其他从业人员", "其他行业"};
    private final String[] mSalaryArr = {"面议", "基础工资+提成", "3000元 以下", "3001 - 5000元", "5001 - 7000元", "7001 - 10000元", "10001 - 12000元", "12001 - 15000元", "15000元以上"};
    private final String[] mCareerArr = {"半年", "1年", "2年", "3年", "4年", "5年", "6年", "7年", "8年", "9年", "10年", "10年以上"};
    private final String[] mLimitArr  = {"忽略", "已认证"};
    private final String[] mGenderArr = {"男", "女"};

    private final ArrayList<String> mAgeList    = new ArrayList<>();
    private final ArrayList<String> mProfList   = new ArrayList<>();
    private final ArrayList<String> mSalaryList = new ArrayList<>();
    private final ArrayList<String> mCareerList = new ArrayList<>();
    private final ArrayList<String> mLimitList  = new ArrayList<>();
    private final ArrayList<String> mGenderList = new ArrayList<>();


    private SFProgressDialog     progressDialog;
    private Intent               mIntent;
    private ImagePickerAdapter   adapter;
    private ArrayList<ImageItem> selImageList; //当前选择的所有图片
    private int maxImgCount = 3;               //允许选择图片最大数

    private String longitude = "0.0";
    private String latitude  = "0.0";

    public static final int IMAGE_ITEM_ADD       = -1;
    public static final int REQUEST_CODE_SELECT  = 100;
    public static final int REQUEST_CODE_PREVIEW = 101;

    @BindView(R.id.tv_top_title)
    TextView tvTopTitle;
    @BindView(R.id.tv_resume_name)
    TextView tvResumeName;
    @BindView(R.id.tv_resume_job)
    TextView tvResumeJob;
    @BindView(R.id.tv_resume_salary)
    TextView tvResumeSalary;
    @BindView(R.id.tv_resume_gender)
    TextView tvResumeGender;
    @BindView(R.id.tv_resume_age)
    TextView tvResumeAge;
    @BindView(R.id.tv_resume_phone)
    TextView tvResumePhone;
    @BindView(R.id.tv_resume_career)
    TextView tvResumeCareer;
    @BindView(R.id.tv_resume_limit)
    TextView tvResumeLimit;
    @BindView(R.id.et_work_experience)
    EditText etWorkExperience;
    @BindView(R.id.tv_work_experience_limit)
    TextView tvWorkExperienceLimit;
    @BindView(R.id.et_self_summary)
    EditText etSelfSummary;
    @BindView(R.id.tv_self_summary_limit)
    TextView tvSelfSummaryLimit;
    @BindView(R.id.tv_resume_pic_limit)
    TextView tvResumePicLimit;

    @Override
    protected int layoutId() {
        return R.layout.activity_post_resume;
    }

    @Override
    protected ViewGroup loadDataViewLayout() {
        return null;
    }

    @Override
    protected void initView() {
        tvTopTitle.setText("发布简历");
        progressDialog = new SFProgressDialog(this);
        LocationUtils.initLocation(this);
        longitude = Double.toString(LocationUtils.longitude);
        latitude = Double.toString(LocationUtils.latitude);
        LogUtils.show(TAG, "[ longitude = " + longitude + ", latitude = " + latitude + " ]");

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        selImageList = new ArrayList<>();
        adapter = new ImagePickerAdapter(this, selImageList, maxImgCount);
        adapter.setOnItemClickListener(this);

        recyclerView.setLayoutManager(new GridLayoutManager(this, 4));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);

        etWorkExperience.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                tvWorkExperienceLimit.setText(s.length() + "/500");
            }
        });

        etSelfSummary.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                tvSelfSummaryLimit.setText(s.length() + "/300");
            }
        });
    }

    @Override
    protected void initData() {
        for (int i = 0; i < mAgeArr.length; i++) {
            mAgeList.add(mAgeArr[i]);
        }
        for (int i = 0; i < mProfArr.length; i++) {
            mProfList.add(mProfArr[i]);
        }
        for (int i = 0; i < mSalaryArr.length; i++) {
            mSalaryList.add(mSalaryArr[i]);
        }
        for (int i = 0; i < mCareerArr.length; i++) {
            mCareerList.add(mCareerArr[i]);
        }
        for (int i = 0; i < mLimitArr.length; i++) {
            mLimitList.add(mLimitArr[i]);
        }
        for (int i = 0; i < mGenderArr.length; i++) {
            mGenderList.add(mGenderArr[i]);
        }
    }

    @Override
    protected void getLoadView(LoadDataView loadView) {

    }

    @Override
    protected void initPresenter() {

    }

    @OnClick({R.id.iv_top_back, R.id.rl_resume_name, R.id.rl_resume_job, R.id.rl_resume_salary, R.id.rl_resume_gender, R.id.rl_resume_age, R.id.rl_resume_phone, R.id.rl_resume_career, R.id.rl_resume_limit, R.id.btn_confirm_submit})
    public void onViewClick(View v) {
        if (mIntent == null) {
            mIntent = new Intent();
        }
        switch (v.getId()) {
            case R.id.iv_top_back:
                finish();
                break;
            case R.id.rl_resume_name:   //姓名
                mIntent.setClass(this, CompanyEditInfoActivity.class);
                mIntent.putExtra("OLD_INFO", tvResumeName.getText().toString().trim());
                mIntent.putExtra("EDIT_TYPE", 48);
                startActivityForResult(mIntent, 48);
                break;

            case R.id.rl_resume_job:   //求职职位
                String resumeJob = tvResumeJob.getText().toString().trim();
                BottomListDialog jobDialog = new BottomListDialog(this, resumeJob, "请选择求职职位", mProfList);
                jobDialog.setOnItemClickListener(new BottomListDialog.OnItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        tvResumeJob.setText(mProfArr[position]);
                    }
                });
                jobDialog.show();
                break;

            case R.id.rl_resume_salary:   //期望薪资
                String resumeSalary = tvResumeSalary.getText().toString().trim();
                BottomListDialog salaryDialog = new BottomListDialog(this, resumeSalary, "请选择期望薪资", mSalaryList);
                salaryDialog.setOnItemClickListener(new BottomListDialog.OnItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        tvResumeSalary.setText(mSalaryArr[position]);
                    }
                });
                salaryDialog.show();
                break;

            case R.id.rl_resume_gender://性别
                /*String resumeGender = tvResumeGender.getText().toString().trim();
                showGenderDialog(resumeGender);*/
                String resumeGender = tvResumeGender.getText().toString().trim();
                BottomListDialog genderDialog = new BottomListDialog(this, resumeGender, "请选择性别", mGenderList);
                genderDialog.setOnItemClickListener(new BottomListDialog.OnItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        tvResumeGender.setText(mGenderArr[position]);
                    }
                });
                genderDialog.show();
                break;
            case R.id.rl_resume_age:   //年龄
                String resumeAge = tvResumeAge.getText().toString().trim();
                BottomListDialog ageDialog = new BottomListDialog(this, resumeAge, "请选择年龄", mAgeList);
                ageDialog.setOnItemClickListener(new BottomListDialog.OnItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        tvResumeAge.setText(mAgeArr[position]);
                    }
                });
                ageDialog.show();
                break;
            case R.id.rl_resume_phone: //联系电话
                mIntent.setClass(this, CompanyEditInfoActivity.class);
                mIntent.putExtra("OLD_INFO", tvResumePhone.getText().toString().trim());
                mIntent.putExtra("EDIT_TYPE", 49);
                startActivityForResult(mIntent, 49);
                break;

            case R.id.rl_resume_career:  //从业时间
                String resumeCareer = tvResumeCareer.getText().toString().trim();
                BottomListDialog careerDialog = new BottomListDialog(this, resumeCareer, "请选择从业时间", mCareerList);
                careerDialog.setOnItemClickListener(new BottomListDialog.OnItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        tvResumeCareer.setText(mCareerArr[position]);
                    }
                });
                careerDialog.show();
                break;
            case R.id.rl_resume_limit:  //浏览权限
                String resumeLimit = tvResumeLimit.getText().toString().trim();
                BottomListDialog limitDialog = new BottomListDialog(this, resumeLimit, "请设置浏览权限", mLimitList);
                limitDialog.setOnItemClickListener(new BottomListDialog.OnItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        tvResumeLimit.setText(mLimitArr[position]);
                    }
                });
                limitDialog.show();
                break;
            case R.id.btn_confirm_submit://确认发布
                checkResumeInfo();
                break;
            default:
                break;
        }
    }

    ArrayList<ImageItem> images = null;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 60 && data != null) {
            String editInfo = data.getStringExtra("EDIT_INFO");
            switch (requestCode) {
                case 48:
                    tvResumeName.setText(editInfo);
                    break;
                case 49:
                    tvResumePhone.setText(editInfo);
                    break;
                default:
                    break;
            }
        }

        if (resultCode == ImagePicker.RESULT_CODE_ITEMS && requestCode == REQUEST_CODE_SELECT && data != null) {//添加图片返回
            images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
            if (images != null) {
                selImageList.addAll(images);
                adapter.setImages(selImageList);
                tvResumePicLimit.setText(selImageList.size() + "/3");
            }
        } else if (resultCode == ImagePicker.RESULT_CODE_BACK && requestCode == REQUEST_CODE_PREVIEW && data != null) {//预览图片返回
            images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_IMAGE_ITEMS);
            if (images != null) {
                selImageList.clear();
                selImageList.addAll(images);
                adapter.setImages(selImageList);
                tvResumePicLimit.setText(selImageList.size() + "/3");
            }
        }
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
                                ImagePicker.getInstance().setSelectLimit(maxImgCount - selImageList.size());
                                Intent intent = new Intent(PostResumeActivity.this, ImageGridActivity.class);
                                intent.putExtra(ImageGridActivity.EXTRAS_TAKE_PICKERS, true); // 是否是直接打开相机
                                startActivityForResult(intent, REQUEST_CODE_SELECT);
                                break;
                            case 1:
                                //打开选择,本次允许选择的数量
                                ImagePicker.getInstance().setSelectLimit(maxImgCount - selImageList.size());
                                Intent intent1 = new Intent(PostResumeActivity.this, ImageGridActivity.class);
                                // 如果需要进入选择的时候显示已经选中的图片，详情请查看ImagePickerActivity
                                //intent1.putExtra(ImageGridActivity.EXTRAS_IMAGES,images);
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
                intentPreview.putExtra(ImagePicker.EXTRA_IMAGE_ITEMS, (ArrayList<ImageItem>) adapter.getImages());
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


   /* private void showGenderDialog(String userGender) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = View.inflate(this, R.layout.dialog_gender, null);
        builder.setView(view);

        RelativeLayout rlGenderBoy = (RelativeLayout) view.findViewById(R.id.rl_gender_boy);
        ImageView ivGenderBoy = (ImageView) view.findViewById(R.id.iv_gender_boy);
        RelativeLayout rlGenderGril = (RelativeLayout) view.findViewById(R.id.rl_gender_gril);
        ImageView ivGenderGril = (ImageView) view.findViewById(R.id.iv_gender_gril);
        AlertDialog dialog = builder.create();

        //用户性别 sex：性别（0未知，1男，2女）
        if (TextUtils.equals(userGender, "男")) {
            ivGenderBoy.setVisibility(View.VISIBLE);
            ivGenderGril.setVisibility(View.GONE);
        } else if (TextUtils.equals(userGender, "女")) {
            ivGenderBoy.setVisibility(View.GONE);
            ivGenderGril.setVisibility(View.VISIBLE);
        } else {
            ivGenderBoy.setVisibility(View.GONE);
            ivGenderGril.setVisibility(View.GONE);
        }

        rlGenderBoy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ivGenderBoy.setVisibility(View.VISIBLE);
                ivGenderGril.setVisibility(View.GONE);
                tvResumeGender.setText("男");
                dialog.dismiss();
            }
        });
        rlGenderGril.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ivGenderBoy.setVisibility(View.GONE);
                ivGenderGril.setVisibility(View.VISIBLE);
                tvResumeGender.setText("女");
                dialog.dismiss();
            }
        });
        dialog.show();
    }*/

    private void checkResumeInfo() {
        String resumeName = tvResumeName.getText().toString().trim();
        String resumeJob = tvResumeJob.getText().toString().trim();
        String resumeSalary = tvResumeSalary.getText().toString().trim();
        String resumeGender = tvResumeGender.getText().toString().trim();
        String resumeAge = tvResumeAge.getText().toString().trim();
        String resumePhone = tvResumePhone.getText().toString().trim();
        String resumeCareer = tvResumeCareer.getText().toString().trim();
        String resumeLimit = tvResumeLimit.getText().toString().trim();
        String workExperience = etWorkExperience.getText().toString().trim();
        String selfSummary = etSelfSummary.getText().toString().trim();


        if (TextUtils.isEmpty(resumeName) || TextUtils.equals(resumeName, "请填写") || TextUtils.equals(resumeName, "请选择")) {
            ToastUtil.showShort(this, "姓名不能为空");
            return;
        }
        if (TextUtils.isEmpty(resumeJob) || TextUtils.equals(resumeJob, "请填写") || TextUtils.equals(resumeJob, "请选择")) {
            ToastUtil.showShort(this, "求职职位不能为空");
            return;
        }
        if (TextUtils.isEmpty(resumeSalary) || TextUtils.equals(resumeSalary, "请填写") || TextUtils.equals(resumeSalary, "请选择")) {
            ToastUtil.showShort(this, "期望薪资不能为空");
            return;
        }
        if (TextUtils.isEmpty(resumeGender) || TextUtils.equals(resumeGender, "请填写") || TextUtils.equals(resumeGender, "请选择")) {
            ToastUtil.showShort(this, "请选择性别");
            return;
        }
        if (TextUtils.isEmpty(resumeAge) || TextUtils.equals(resumeAge, "请填写") || TextUtils.equals(resumeAge, "请选择")) {
            ToastUtil.showShort(this, "年龄不能为空");
            return;
        }
        if (TextUtils.isEmpty(resumePhone) || TextUtils.equals(resumePhone, "请填写") || TextUtils.equals(resumePhone, "请选择")) {
            ToastUtil.showShort(this, "联系电话不能为空");
            return;
        }
        if (!resumePhone.matches(GlobalConstants.STR_PHONE_REGEX2)) { //用正则表达式判断手机号是否合格
            ToastUtil.showShort(this, "请输入正确的联系电话");
            return;
        }
        if (TextUtils.isEmpty(resumeCareer) || TextUtils.equals(resumeCareer, "请填写") || TextUtils.equals(resumeCareer, "请选择")) {
            ToastUtil.showShort(this, "从业时间不能为空");
            return;
        }
        if (TextUtils.isEmpty(resumeLimit) || TextUtils.equals(resumeLimit, "请填写") || TextUtils.equals(resumeLimit, "请选择")) {
            ToastUtil.showShort(this, "请选择浏览权限");
            return;
        }
        if (TextUtils.isEmpty(workExperience) || TextUtils.equals(workExperience, "请填写") || TextUtils.equals(workExperience, "请选择")) {
            ToastUtil.showShort(this, "请填写工作经验");
            return;
        }
        if (TextUtils.isEmpty(selfSummary) || TextUtils.equals(selfSummary, "请填写") || TextUtils.equals(selfSummary, "请选择")) {
            ToastUtil.showShort(this, "请填写自我评价");
            return;
        }
        if (selImageList == null || selImageList.size() == 0) {
            ToastUtil.showShort(this, "请至少选择一张简历图片");
            return;
        }
        gotoSubmitResume(resumeName, resumeJob, resumeSalary, resumeGender, resumeAge, resumePhone, resumeCareer, resumeLimit, workExperience, selfSummary);
    }

    private void gotoSubmitResume(String resumeName, String resumeJob, String resumeSalary, String resumeGender, String resumeAge, String resumePhone, String resumeCareer, String resumeLimit, String workExperience, String selfSummary) {
        progressDialog.show();
        String mUserId = SPUtils.getString(this, GlobalConstants.userID, "");

        PostFormBuilder postFormBuilder = OkHttpUtils.post().url(GlobalConstants.URL_INFO_ADD)
                .addParams("catId", "6")                        //必要参数 catId 分类
                .addParams("areaId", "")                        //必要参数 areaId城市（默认西安可不传递，其它城市需要此参数）
                .addParams("userid", mUserId)                   //用户id   必须
                .addParams("linkman", resumeName)               //联系人姓名   必须
                .addParams("position", resumeJob)               //求职职位
                .addParams("expect_salary", resumeSalary)       //期望薪资
                .addParams("sex", resumeGender)                 //性别
                .addParams("age", resumeAge)                    //年龄
                .addParams("phone", resumePhone)                //联系电话   必须
                .addParams("yearWork", resumeCareer)            //从业时间
                .addParams("workExperience", workExperience)    //工作经验
                .addParams("personalSummary", selfSummary)      //自我评价
                .addParams("longitude", longitude)              //经度
                .addParams("latitude", latitude);               //纬度

        LogUtils.show(TAG, "selImageList.size() = " + selImageList.size());
        for (int i = 0; i < selImageList.size(); i++) {            //图片列表上传 可选（picture0, picture1, ....., picture8最多可以传9张图）
            postFormBuilder.addFile("picture" + i, "picture" + i + ".png", new File(selImageList.get(i).path));
            LogUtils.show(TAG, "第" + i + "张图片" + selImageList.get(i).path);
        }

        postFormBuilder.build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                progressDialog.dismiss();
                LogUtils.show(TAG, "---网络异常,发布简历失败,请重试---");
                Toast.makeText(PostResumeActivity.this, "网络异常,发布简历失败,请重试", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(String response, int id) {
                progressDialog.dismiss();
                LogUtils.show(TAG, "---简历发布成功:response = " + response + "---");
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String data = jsonObject.getString("data");
                    String message = jsonObject.getString("message");
                    Toast.makeText(PostResumeActivity.this, message, Toast.LENGTH_SHORT).show();
                    if (TextUtils.equals(data, "success")) {
                        startActivity(new Intent(PostResumeActivity.this, RequestJobListActivity.class));
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(PostResumeActivity.this, "数据异常,请重试", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


}
