package com.xasfemr.meiyaya.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.ui.ImageGridActivity;
import com.lzy.imagepicker.ui.ImagePreviewDelActivity;
import com.xasfemr.meiyaya.R;
import com.xasfemr.meiyaya.global.GlobalConstants;
import com.xasfemr.meiyaya.main.BaseActivity;
import com.xasfemr.meiyaya.utils.ImagePickerAdapter;
import com.xasfemr.meiyaya.utils.LogUtils;
import com.xasfemr.meiyaya.utils.SPUtils;
import com.xasfemr.meiyaya.utils.SelectDialog;
import com.xasfemr.meiyaya.view.progress.SFProgressDialog;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.builder.PostFormBuilder;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

public class MyReportActivity extends BaseActivity implements ImagePickerAdapter.OnRecyclerViewItemClickListener {
    private static final String TAG = "MyReportActivity";

    public static final int IMAGE_ITEM_ADD       = -1;
    public static final int REQUEST_CODE_SELECT  = 100;
    public static final int REQUEST_CODE_PREVIEW = 101;
    private ImagePickerAdapter   adapter;
    private ArrayList<ImageItem> selImageList; //当前选择的所有图片
    private int maxImgCount = 3;               //允许选择图片最大数

    private Intent mIntent;
    private String lookUserId;
    private String reportOption = "私信聊天";

    private SFProgressDialog progressDialog;
    private RadioGroup       rgReportOption;
    private TextView         tvReportImageLimit;
    private EditText         etReportContent;
    private TextView         tvReportContentLimit;
    private TextView         tvReportSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_report);
        initTopBar();
        setTopTitleText("举报");
        mIntent = getIntent();
        lookUserId = mIntent.getStringExtra("LOOK_USER_ID");
        progressDialog = new SFProgressDialog(this);

        rgReportOption = (RadioGroup) findViewById(R.id.rg_report_option);
        tvReportImageLimit = (TextView) findViewById(R.id.tv_report_image_limit);
        etReportContent = (EditText) findViewById(R.id.et_report_content);
        tvReportContentLimit = (TextView) findViewById(R.id.tv_report_content_limit);
        tvReportSubmit = (TextView) findViewById(R.id.tv_report_submit);

        tvReportSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selImageList == null || selImageList.size() == 0) {
                    Toast.makeText(MyReportActivity.this, "请选择相关图片证明", Toast.LENGTH_SHORT).show();
                } else {
                    showConfrimDialog();
                }
            }
        });

        rgReportOption.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId) {
                    case R.id.rb_report1_live:
                        reportOption = "在线课程";
                        break;
                    case R.id.rb_report2_playback:
                        reportOption = "回放课程";
                        break;
                    case R.id.rb_report3_dynamic:
                        reportOption = "动态图文";
                        break;
                    case R.id.rb_report4_chat:
                        reportOption = "私信聊天";
                        break;
                    default:
                        break;
                }
                LogUtils.show(TAG, "reportOption = " + reportOption);
            }
        });

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        selImageList = new ArrayList<>();
        adapter = new ImagePickerAdapter(this, selImageList, maxImgCount);
        adapter.setOnItemClickListener(this);

        recyclerView.setLayoutManager(new GridLayoutManager(this, 4));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
    }

    //确认是否提交举报的dialog
    private void showConfrimDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(MyReportActivity.this);
        builder.setTitle("确认举报？");
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                tvReportSubmit.setEnabled(false);
                progressDialog.show();
                gotoSubmitReport();
            }
        });
        builder.setNegativeButton("取消",null);
        builder.show();
    }

    //选取照片还是直接照相的dialog
    private SelectDialog showDialog(SelectDialog.SelectDialogListener listener, List<String> names) {
        SelectDialog dialog = new SelectDialog(this, R.style.transparentFrameWindowStyle, listener, names);
        if (!this.isFinishing()) {
            dialog.show();
        }
        return dialog;
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
                                ImagePicker.getInstance().setSelectLimit(maxImgCount - selImageList.size());
                                Intent intent = new Intent(MyReportActivity.this, ImageGridActivity.class);
                                intent.putExtra(ImageGridActivity.EXTRAS_TAKE_PICKERS, true); // 是否是直接打开相机
                                startActivityForResult(intent, REQUEST_CODE_SELECT);
                                break;
                            case 1:
                                //打开选择,本次允许选择的数量
                                ImagePicker.getInstance().setSelectLimit(maxImgCount - selImageList.size());
                                Intent intent1 = new Intent(MyReportActivity.this, ImageGridActivity.class);
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
                intentPreview.putExtra(ImagePicker.EXTRA_IMAGE_ITEMS, (ArrayList<ImageItem>) adapter.getImages());
                intentPreview.putExtra(ImagePicker.EXTRA_SELECTED_IMAGE_POSITION, position);
                intentPreview.putExtra(ImagePicker.EXTRA_FROM_ITEMS, true);
                startActivityForResult(intentPreview, REQUEST_CODE_PREVIEW);
                break;
        }
    }

    ArrayList<ImageItem> images = null;
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == ImagePicker.RESULT_CODE_ITEMS) {
            //添加图片返回
            if (data != null && requestCode == REQUEST_CODE_SELECT) {
                images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
                if (images != null) {
                    selImageList.addAll(images);
                    adapter.setImages(selImageList);
                    tvReportImageLimit.setText(selImageList.size() + "/3");
                }
            }
        } else if (resultCode == ImagePicker.RESULT_CODE_BACK) {
            //预览图片返回
            if (data != null && requestCode == REQUEST_CODE_PREVIEW) {
                images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_IMAGE_ITEMS);
                if (images != null) {
                    selImageList.clear();
                    selImageList.addAll(images);
                    adapter.setImages(selImageList);
                    tvReportImageLimit.setText(selImageList.size() + "/3");
                }
            }
        }
    }

    private void gotoSubmitReport() {
        String mUserId = SPUtils.getString(MyReportActivity.this, GlobalConstants.userID, "");
        String reportContent = etReportContent.getText().toString().trim();
        reportContent = removeEnterKey(reportContent);

        PostFormBuilder postFormBuilder = OkHttpUtils.post().url(GlobalConstants.URL_REPORT_ILLEGAL)
                .addParams("rid", lookUserId)                       //rid被举报者id
                .addParams("uid", mUserId)                          //uid举报者id
                .addParams("content", reportOption);                //content举报内容

        if (!TextUtils.isEmpty(reportContent)) {
            postFormBuilder.addParams("replenish", reportContent);  //replenish补充说明（非必填）
        }

        System.out.println("selImageList.size() = " + selImageList.size());
        for (int i = 0; i < selImageList.size(); i++) {            //images截图（images0，images1 类同动态图片）
            postFormBuilder.addFile("images" + i, "images" + i + ".png", new File(selImageList.get(i).path));
            System.out.println("第" + i + "张图片" + selImageList.get(i).path);
        }

        postFormBuilder.build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                tvReportSubmit.setEnabled(true);
                progressDialog.dismiss();
                Toast.makeText(MyReportActivity.this, "网络异常,举报失败,请重试", Toast.LENGTH_SHORT).show();
                System.out.println("----举报失败----");
            }

            @Override
            public void onResponse(String response, int id) {
                tvReportSubmit.setEnabled(true);
                progressDialog.dismiss();
                System.out.println("---举报成功---");
                System.out.println("---" + response + "---");
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String message = jsonObject.getString("message");
                    Toast.makeText(MyReportActivity.this, message, Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                    System.out.println("----举报成功的json,出现解析异常!-----");
                    Toast.makeText(MyReportActivity.this, "举报成功,解析出现异常", Toast.LENGTH_SHORT).show();
                }
                finish();
            }
        });
    }

    //多个回车键换行连续的话,只保留一个
    private String removeEnterKey(String dynamicContent) {

        ArrayList<Integer> indexList = new ArrayList<>();

        for (int i = 1; i < dynamicContent.length(); i++) {
            if (dynamicContent.charAt(i) == '\n' && dynamicContent.charAt(i - 1) == '\n') {
                indexList.add(i);
            }
        }

        for (int i = 0; i < indexList.size(); i++) {
            Log.i(TAG, "gotoAddDynamic: index = " + indexList.get(i));
        }

        for (int i = indexList.size() - 1; i >= 0; i--) {
            dynamicContent = removeChar(indexList.get(i), dynamicContent);
        }
        return dynamicContent;
    }

    private String removeChar(int index, String Str) {
        Str = Str.substring(0, index) + Str.substring(index + 1, Str.length());//substring的取值范围是:[,)
        return Str;
    }
}
