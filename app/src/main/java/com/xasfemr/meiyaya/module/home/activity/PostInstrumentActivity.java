package com.xasfemr.meiyaya.module.home.activity;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import com.xasfemr.meiyaya.weight.MyPopupWindow;
import com.xasfemr.meiyaya.weight.keyboard.KeyboardUtil;
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
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * 闲置发布
 * Created by sen.luo on 2018/2/26.
 */

public class PostInstrumentActivity extends MVPBaseActivity implements ImagePickerAdapter.OnRecyclerViewItemClickListener {

    @BindView(R.id.tv_top_title)
    TextView     tvTitle;
    @BindView(R.id.iv_top_back)
    ImageView    ivBack;
    @BindView(R.id.rvPhotoList)
    RecyclerView rvPhotoList;
    @BindView(R.id.tvJiaGe)
    TextView     tvJiaGe;
    @BindView(R.id.root_View)
    LinearLayout rootView;
    @BindView(R.id.edTitle)
    EditText     edTitle;
    @BindView(R.id.etContent)
    EditText     etContent;
    @BindView(R.id.tv_detail_limit)
    TextView     tvDetailLimit;
    @BindView(R.id.tvClassify)
    TextView     tvClassify;
    @BindView(R.id.etName)
    TextView     etName;
    @BindView(R.id.etPhone)
    TextView     etPhone;
    @BindView(R.id.etCompanyName)
    TextView     etCompanyName;


    private ArrayList<String> stringArrayList;
    private PostPresenter     postPresenter;

    private ImagePickerAdapter imagePickerAdapter;

    private SFProgressDialog progressDialog;

    private ArrayList<ImageItem> seletedImgList; //当前选择的所有图片
    private             int maxImgCount          = 9;               //允许选择图片最大数
    public static final int IMAGE_ITEM_ADD       = -1;
    public static final int REQUEST_CODE_SELECT  = 100;
    public static final int REQUEST_CODE_PREVIEW = 101;


    private String price    = ""; //价格
    private String oldprice = "";
    private String postage  = "";// 原价


    @Override
    protected int layoutId() {
        return R.layout.activity_post_instrument;
    }

    @Override
    protected ViewGroup loadDataViewLayout() {
        return null;
    }

    @Override
    protected void initView() {
        tvTitle.setText("发布信息");
        ivBack.setOnClickListener(view -> finish());

        seletedImgList = new ArrayList<>();
        imagePickerAdapter = new ImagePickerAdapter(this, seletedImgList, maxImgCount);
        imagePickerAdapter.setOnItemClickListener(this);

        rvPhotoList.setLayoutManager(new GridLayoutManager(this, 4));
        rvPhotoList.setHasFixedSize(true);
        rvPhotoList.setAdapter(imagePickerAdapter);

        stringArrayList = new ArrayList<>();
        progressDialog = new SFProgressDialog(this);

        etContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                tvDetailLimit.setText(s.length() + "/300");
            }
        });
    }

    @Override
    protected void initData() {
        HashMap<String, String> map = new HashMap<>();
        map.put("catId", "3");
        postPresenter.postRecruitment(map, new PostFilterListIView() {
            @Override
            public void getFilterListSuccess(PostProtocol postProtocol) {
                stringArrayList.addAll(postProtocol.customList);
            }

            @Override
            public void getFilterListOnFailure(String msg) {
                ToastUtil.showShort(PostInstrumentActivity.this, msg);
            }

            @Override
            public void onNetworkFailure(String message) {
                ToastUtil.showShort(PostInstrumentActivity.this, message);

            }
        });
    }


    private EditText      etPrice;
    private EditText      et_orginal_price;
    private EditText      et_freight;
    private MyPopupWindow popupWindow;
    private CheckBox      cbMianYi, cbBaoYou;

    Intent mIntent =new Intent();

    @OnClick({R.id.layoutPrice, R.id.tvClassify, R.id.btRelease,R.id.etCompanyName,R.id.etName,R.id.etPhone})
    public void onViewClick(View view) {
        switch (view.getId()) {


            case R.id.layoutPrice:  //弹出数字键盘

                popupWindow = new MyPopupWindow(this, R.layout.layout_pop_window_keyboard);
                popupWindow.setBackgroundDrawable(new ColorDrawable(0x000000));
                View keyboardView = popupWindow.getView();
                etPrice = (EditText) keyboardView.findViewById(R.id.etPrice);
                et_orginal_price = (EditText) keyboardView.findViewById(R.id.et_orginal_price);
                et_freight = (EditText) keyboardView.findViewById(R.id.etFreight);
                cbMianYi = (CheckBox) keyboardView.findViewById(R.id.cbMianYi);
                cbBaoYou = (CheckBox) keyboardView.findViewById(R.id.cbBaoYou);


                final KeyboardUtil keyboardUtil = new KeyboardUtil(PostInstrumentActivity.this, keyboardView);

                keyboardUtil.attachTo(etPrice);
                etPrice.setFocusable(true);
                etPrice.setFocusableInTouchMode(true);
                etPrice.requestFocus();

                popupWindow.showAtLocation(rootView, Gravity.BOTTOM, 0, 0);
                keyboardUtil.setOnOkClick(new KeyboardUtil.OnOkClick() {
                    @Override
                    public void onOkClick() {
                        if (popupWindow != null && popupWindow.isShowing()) {
                            popupWindow.dismiss();
                        }

                        tvJiaGe.setText("价格:" + (cbMianYi.isChecked()? "面议":etPrice.getText().toString()) + ","
                                + "原价:" + et_orginal_price.getText().toString() + "," + "运费：" + (cbBaoYou.isChecked()? "包邮":et_freight.getText()).toString());


                        price = etPrice.getText().toString().trim();
                        oldprice = et_orginal_price.getText().toString().trim();
                        postage = et_freight.getText().toString().trim();

//                        if (!TextUtils.isEmpty(etPrice.getText().toString().trim())) {
//
//                            price = etPrice.getText().toString().trim();
//                            oldprice = et_orginal_price.getText().toString().trim();
//                            postage = et_freight.getText().toString().trim();
//
//                        }

                    }
                });

                keyboardUtil.setOnCancelClick(new KeyboardUtil.onCancelClick() {
                    @Override
                    public void onCancellClick() {
                        if (popupWindow != null && popupWindow.isShowing()) {
                            popupWindow.dismiss();
                        }
                    }
                });


                etPrice.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        keyboardUtil.attachTo(etPrice);
                        return false;
                    }
                });
                et_orginal_price.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        keyboardUtil.attachTo(et_orginal_price);
                        return false;
                    }
                });
                et_freight.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        keyboardUtil.attachTo(et_freight);
                        return false;
                    }
                });


                break;


            case R.id.tvClassify:  //选择分类

                BottomListDialog ageDialog = new BottomListDialog(this, "", "请选择分类", stringArrayList);
                ageDialog.setOnItemClickListener(new BottomListDialog.OnItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        tvClassify.setText(stringArrayList.get(position));
                    }
                });
                ageDialog.show();

                break;
            case R.id.btRelease: //发布

                if (validate()) {
                    postRelease();
                }

                break;


            case R.id.etCompanyName: //公司名称

                mIntent.setClass(this, CompanyEditInfoActivity.class);
                mIntent.putExtra("OLD_INFO", etCompanyName.getText().toString().trim());
                mIntent.putExtra("EDIT_TYPE", 61);
                startActivityForResult(mIntent, 61);
                break;


            case R.id.etName: //姓名

                mIntent.setClass(this, CompanyEditInfoActivity.class);
                mIntent.putExtra("OLD_INFO", etName.getText().toString().trim());
                mIntent.putExtra("EDIT_TYPE", 65);
                startActivityForResult(mIntent, 65);

                break;


            case R.id.etPhone:  //电话
                mIntent.setClass(this, CompanyEditInfoActivity.class);
                mIntent.putExtra("OLD_INFO", etPhone.getText().toString().trim());
                mIntent.putExtra("EDIT_TYPE", 66);
                startActivityForResult(mIntent, 66);

                break;
        }


    }




    private void postRelease() {

        if (progressDialog != null) {
            progressDialog.show();
        }


        if (cbBaoYou.isChecked()) {
            postage = "-1";
        }

        if (cbMianYi.isChecked()) {
            price = "-1";
        }


        LocationUtils.initLocation(this);
        String longitude = Double.toString(LocationUtils.longitude);
        String latitude = Double.toString(LocationUtils.latitude);

        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        PostFormBuilder postFormBuilder = OkHttpUtils.post().url(GlobalConstants.URL_INFO_ADD)
                .addParams("userid", SPUtils.getString(this, GlobalConstants.userID, ""))
                .addParams("catId", "3")
                .addParams("tile", edTitle.getText().toString().trim())
                .addParams("linkman", etName.getText().toString().trim())
                .addParams("phone", etPhone.getText().toString().trim())
                .addParams("classify", tvClassify.getText().toString().trim()) //分类
                .addParams("price", price) // 价格 -1 表示面议
                .addParams("oldprice", oldprice) //原价
                .addParams("postage", postage) //邮费 -1 包邮
                .addParams("company", etCompanyName.getText().toString().trim()) //公司名称
                .addParams("content", etContent.getText().toString().trim()) //内容
                .addParams("longitude", longitude) //经度
                .addParams("latitude", latitude); //维度


        LogUtils.show("提交数据", "");


        for (int i = 0; i < seletedImgList.size(); i++) {            //图片列表上传 可选（picture0, picture1, ....., picture8最多可以传9张图）
            postFormBuilder.addFile("picture" + i, "picture" + i + ".png", new File(seletedImgList.get(i).path));
            LogUtils.show("发布的图片", "第" + i + "张图片" + seletedImgList.get(i).path);
        }

        postFormBuilder.build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                if (progressDialog != null) {
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onResponse(String response, int id) {

                if (progressDialog != null) {
                    progressDialog.dismiss();
                }
                LogUtils.show("", "---仪器发布:response = " + response + "---");
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String data = jsonObject.getString("data");
                    String message = jsonObject.getString("message");
                    ToastUtil.showShort(PostInstrumentActivity.this, message);
                    if (TextUtils.equals(data, "success")) {
                        startActivity(new Intent(PostInstrumentActivity.this, InstrumentTransferListActivity.class));
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(PostInstrumentActivity.this, "数据异常,请重试", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


    private boolean validate() {

        if (edTitle.getText().toString().equals("")) {
            ToastUtil.showShort(this, "标题不能为空");
            return false;
        }

        if (etContent.getText().toString().equals("")) {
            ToastUtil.showShort(this, "内容不能为空");
            return false;
        }

        if (etCompanyName.getText().toString().equals("")) {
            ToastUtil.showShort(this, "公司名不能为空");
            return false;
        }

        if (etName.getText().toString().equals("")) {
            ToastUtil.showShort(this, "联系人不能为空");
            return false;
        }

        if (etPhone.getText().toString().equals("")) {
            ToastUtil.showShort(this, "电话不能为空");
            return false;
        }

        if (etContent.getText().toString().equals("")) {
            ToastUtil.showShort(this, "内容不能为空");
            return false;
        }

        if (etPrice.getText().toString().equals("") && !cbMianYi.isChecked()) {
            ToastUtil.showShort(this, "价格不能为空");
            return false;
        }

        if (et_orginal_price.getText().toString().equals("")) {
            ToastUtil.showShort(this, "原价不能为空");
            return false;
        }

        if (et_freight.getText().toString().equals("") && !cbBaoYou.isChecked()) {
            ToastUtil.showShort(this, "运费不能为空");
            return false;
        }

        if (seletedImgList.size() < 1) {
            ToastUtil.showShort(this, "请至少上传一张图片");
            return false;
        }


        return true;
    }


    @Override
    protected void getLoadView(LoadDataView loadView) {

    }

    @Override
    protected void initPresenter() {
        postPresenter = new PostPresenter();

    }

    @Override
    public void onBackPressed() {
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();

        } else {
            super.onBackPressed();
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
                                /**
                                 * 0.4.7 目前直接调起相机不支持裁剪，如果开启裁剪后不会返回图片，请注意，后续版本会解决
                                 * 但是当前直接依赖的版本已经解决，考虑到版本改动很少，所以这次没有上传到远程仓库
                                 * 如果实在有所需要，请直接下载源码引用。
                                 */
                                //打开选择,本次允许选择的数量
                                ImagePicker.getInstance().setSelectLimit(maxImgCount - seletedImgList.size());
                                Intent intent = new Intent(PostInstrumentActivity.this, ImageGridActivity.class);
                                intent.putExtra(ImageGridActivity.EXTRAS_TAKE_PICKERS, true); // 是否是直接打开相机
                                startActivityForResult(intent, REQUEST_CODE_SELECT);
                                break;
                            case 1:
                                //打开选择,本次允许选择的数量
                                ImagePicker.getInstance().setSelectLimit(maxImgCount - seletedImgList.size());
                                Intent intent1 = new Intent(PostInstrumentActivity.this, ImageGridActivity.class);
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
        if (resultCode == ImagePicker.RESULT_CODE_ITEMS) {
            //添加图片返回
            if (data != null && requestCode == REQUEST_CODE_SELECT) {
                images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
                if (images != null) {
                    seletedImgList.addAll(images);
                    imagePickerAdapter.setImages(seletedImgList);
                }
            }
        } else if (resultCode == ImagePicker.RESULT_CODE_BACK) {
            //预览图片返回
            if (data != null && requestCode == REQUEST_CODE_PREVIEW) {
                images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_IMAGE_ITEMS);
                if (images != null) {
                    seletedImgList.clear();
                    seletedImgList.addAll(images);
                    imagePickerAdapter.setImages(seletedImgList);
                }
            }
        }


        if (resultCode == 60 && data != null) {
            String editInfo = data.getStringExtra("EDIT_INFO");
            switch (requestCode) {
                case 61:
                    etCompanyName.setText(editInfo);
                    break;

                case 65:
                    etName.setText(editInfo);
                    break;

                case 66:
                    etPhone.setText(editInfo);
                    break;
            }
        }

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        postPresenter.destroy();
    }
}
