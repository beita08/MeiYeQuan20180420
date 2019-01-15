package com.xasfemr.meiyaya.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
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

public class DynamicAddActivity extends BaseActivity implements View.OnClickListener, ImagePickerAdapter.OnRecyclerViewItemClickListener {
    private static final String TAG = "DynamicAddActivity";

    public static final int IMAGE_ITEM_ADD       = -1;
    public static final int REQUEST_CODE_SELECT  = 100;
    public static final int REQUEST_CODE_PREVIEW = 101;
    private ImagePickerAdapter   adapter;
    private ArrayList<ImageItem> selImageList; //当前选择的所有图片
    private int maxImgCount = 9;               //允许选择图片最大数

    private Intent mIntent;

    private SFProgressDialog sfProgressDialog;

    private TextView tvDynamicCancel;
    private TextView tvDynamicAdd;
    private EditText etDynamicShared;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dynamic_add);
        mIntent = getIntent();
        sfProgressDialog = new SFProgressDialog(this);

        initView();
    }

    private void initView() {
        tvDynamicCancel = (TextView) findViewById(R.id.tv_dynamic_cancel);
        tvDynamicAdd = (TextView) findViewById(R.id.tv_dynamic_add);
        etDynamicShared = (EditText) findViewById(R.id.et_dynamic_shared);//动态分享的内容
        tvDynamicAdd.setEnabled(true);

        tvDynamicCancel.setOnClickListener(this);
        tvDynamicAdd.setOnClickListener(this);


        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        selImageList = new ArrayList<>();
        adapter = new ImagePickerAdapter(this, selImageList, maxImgCount);
        adapter.setOnItemClickListener(this);

        recyclerView.setLayoutManager(new GridLayoutManager(this, 4));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.tv_dynamic_cancel: //回退
                finish();
                break;
            case R.id.tv_dynamic_add:    //发布动态
                LogUtils.show(TAG, "---点击了发布动态---");
                tvDynamicAdd.setEnabled(false);
                gotoAddDynamic();
                break;
            default:
                break;
        }
    }

    //访问网络发布动态
    private void gotoAddDynamic() {
        String dynamicContent = etDynamicShared.getText().toString().trim();
        String userId = SPUtils.getString(DynamicAddActivity.this, GlobalConstants.userID, "");

        LogUtils.show(TAG, "dynamicContent =" + dynamicContent + "---");
        LogUtils.show(TAG, "dynamicContent.length() = " + dynamicContent.length());

        //多个回车键换行连续的话,只保留一个
        dynamicContent = removeEnterKey(dynamicContent);

        LogUtils.show(TAG, "dynamicContent =" + dynamicContent + "---");
        LogUtils.show(TAG, "dynamicContent.length() = " + dynamicContent.length());

        //分享的动态内容或者分享图片不能同时为空
        if (TextUtils.isEmpty(dynamicContent) && (selImageList == null || selImageList.size() == 0)) {
            tvDynamicAdd.setEnabled(true);
            Log.i(TAG, "gotoAddDynamic: 动态内容或者图片为空");
            Toast.makeText(this, "请添加动态内容或者分享图片", Toast.LENGTH_SHORT).show();
            return;
        }

        sfProgressDialog.show();
        //userid：用户id   content：动态内容  picture：动态图片  videourl：视频地址
        PostFormBuilder postFormBuilder = OkHttpUtils.post().url(GlobalConstants.URL_DYNAMIC_ADD)
                .addParams("userid", userId)
                .addParams("content", dynamicContent);

        LogUtils.show(TAG, "selImageList.size() = " + selImageList.size());

        for (int i = 0; i < selImageList.size(); i++) {
            postFormBuilder.addFile("picture" + i, "picture" + i + ".png", new File(selImageList.get(i).path));
            LogUtils.show(TAG, "第" + i + "张图片" + selImageList.get(i).path);
        }

        postFormBuilder.build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        tvDynamicAdd.setEnabled(true);
                        sfProgressDialog.dismiss();
                        Toast.makeText(DynamicAddActivity.this, "动态发布失败,请重试", Toast.LENGTH_SHORT).show();
                        LogUtils.show(TAG, "-----------发布动态失败----------");
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        tvDynamicAdd.setEnabled(true);
                        sfProgressDialog.dismiss();
                        LogUtils.show(TAG, "----动态成功发布----" + response + "----");
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String message = jsonObject.getString("message");
                            Toast.makeText(DynamicAddActivity.this, message, Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            LogUtils.show(TAG, "-----发布动态json,出现解析异常!-----");
                            //Toast.makeText(DynamicAddActivity.this, "动态发送成功,解析出现异常", Toast.LENGTH_SHORT).show();
                        }
                        setResult(21, mIntent);
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
                                Intent intent = new Intent(DynamicAddActivity.this, ImageGridActivity.class);
                                intent.putExtra(ImageGridActivity.EXTRAS_TAKE_PICKERS, true); // 是否是直接打开相机
                                startActivityForResult(intent, REQUEST_CODE_SELECT);
                                break;
                            case 1:
                                //打开选择,本次允许选择的数量
                                ImagePicker.getInstance().setSelectLimit(maxImgCount - selImageList.size());
                                Intent intent1 = new Intent(DynamicAddActivity.this, ImageGridActivity.class);
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
                }
            }
        }
    }
}
