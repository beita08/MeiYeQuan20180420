package com.xasfemr.meiyaya.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.ocr.sdk.OCR;
import com.baidu.ocr.sdk.OnResultListener;
import com.baidu.ocr.sdk.exception.OCRError;
import com.baidu.ocr.sdk.model.AccessToken;
import com.baidu.ocr.sdk.model.IDCardParams;
import com.baidu.ocr.sdk.model.IDCardResult;
import com.baidu.ocr.ui.camera.CameraActivity;
import com.bumptech.glide.Glide;
import com.xasfemr.meiyaya.R;
import com.xasfemr.meiyaya.global.GlobalConstants;
import com.xasfemr.meiyaya.main.BaseActivity;
import com.xasfemr.meiyaya.utils.FileUtil;
import com.xasfemr.meiyaya.utils.LogUtils;
import com.xasfemr.meiyaya.utils.SPUtils;
import com.xasfemr.meiyaya.view.progress.SFProgressDialog;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.builder.PostFormBuilder;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import okhttp3.Call;

public class RealNameAuthActivity2 extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "RealNameAuthActivity2";

    private static final int REQ_CODE_IDCARD_POSITIVE = 31;
    private static final int REQ_CODE_IDCARD_BACK     = 32;
    private static final int REQ_CODE_CERTIFICATE     = 33;

    private static final int REQUEST_CODE_CAMERA          = 102;
    private static final int REQUEST_CODE_DRIVING_LICENSE = 103;
    private static final int REQUEST_CODE_VEHICLE_LICENSE = 104;

    private String idCardPositivePath;
    private String idCardBackPath;
    private String idCardCertificatePath;

    private SFProgressDialog progressDialog;

    private EditText  etRealName;
    private EditText  etIdcardNumber;
    private ImageView ivIdcardPicPositive;
    private ImageView ivIdcardPicBack;
    private ImageView ivIdcardCertificate;
    private TextView  tvRealNameSubmit;

    private long i = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_real_name_auth);
        initTopBar();
        setTopTitleText("实名认证");
        progressDialog = new SFProgressDialog(this);
        // 初始化
        initAccessTokenWithAkSk();

        etRealName = (EditText) findViewById(R.id.et_real_name);
        etIdcardNumber = (EditText) findViewById(R.id.et_idcard_number);
        ivIdcardPicPositive = (ImageView) findViewById(R.id.iv_idcard_pic_positive);
        ivIdcardPicBack = (ImageView) findViewById(R.id.iv_idcard_pic_back);
        ivIdcardCertificate = (ImageView) findViewById(R.id.iv_idcard_certificate);
        tvRealNameSubmit = (TextView) findViewById(R.id.tv_real_name_submit);

        ivIdcardPicPositive.setOnClickListener(this);
        ivIdcardPicBack.setOnClickListener(this);
        ivIdcardCertificate.setOnClickListener(this);
        tvRealNameSubmit.setOnClickListener(this);
    }

    private void initAccessTokenWithAkSk() {
        OCR.getInstance().initAccessTokenWithAkSk(
                new OnResultListener<AccessToken>() {
                    @Override
                    public void onResult(AccessToken result) {
                        Log.d("MainActivity", "onResult: " + result.toString());
                    }

                    @Override
                    public void onError(OCRError error) {
                        error.printStackTrace();
                        Log.e("MainActivity", "onError: " + error.getMessage());
                    }
                }, getApplicationContext(),
                // 需要自己配置 https://console.bce.baidu.com  "oH6tqEsBX2PSW2OViQyd2yYA" (框架的)
                "8B0RGMag6WcAQdQGYRHuIYMc",
                // 需要自己配置 https://console.bce.baidu.com  "A36f7UrseglvtH9jGP5u7bU9uGxIjZ31" (框架的)
                "EzGUYOlowz6GZYGwwdH99F4fr56OUSPm");
    }

    @Override
    public void onClick(View v) {
        i = System.currentTimeMillis();
        Intent intent = new Intent(RealNameAuthActivity2.this, CameraActivity.class);
        switch (v.getId()) {
            case R.id.iv_idcard_pic_positive:   //身份证照片面
                intent.putExtra(CameraActivity.KEY_OUTPUT_FILE_PATH, FileUtil.getSaveFile(getApplication(), "front" + i).getAbsolutePath());
                intent.putExtra(CameraActivity.KEY_CONTENT_TYPE, CameraActivity.CONTENT_TYPE_ID_CARD_FRONT);
                startActivityForResult(intent, REQUEST_CODE_CAMERA);
                break;

            case R.id.iv_idcard_pic_back:       //身份证国徽面
                intent.putExtra(CameraActivity.KEY_OUTPUT_FILE_PATH, FileUtil.getSaveFile(getApplication(), "back" + i).getAbsolutePath());
                intent.putExtra(CameraActivity.KEY_CONTENT_TYPE, CameraActivity.CONTENT_TYPE_ID_CARD_BACK);
                startActivityForResult(intent, REQUEST_CODE_CAMERA);
                break;

            case R.id.iv_idcard_certificate:    //资格证等
                intent.putExtra(CameraActivity.KEY_OUTPUT_FILE_PATH, FileUtil.getSaveFile(getApplication(), "certificate" + i).getAbsolutePath());
                intent.putExtra(CameraActivity.KEY_CONTENT_TYPE, CameraActivity.CONTENT_TYPE_GENERAL);
                startActivityForResult(intent, REQUEST_CODE_VEHICLE_LICENSE);
                break;

            case R.id.tv_real_name_submit:      //提交
                submitAuthInfo();
                break;

            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) {
            return;
        }

        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_CAMERA:
                    String contentType = data.getStringExtra(CameraActivity.KEY_CONTENT_TYPE);
                    if (!TextUtils.isEmpty(contentType)) {
                        if (CameraActivity.CONTENT_TYPE_ID_CARD_FRONT.equals(contentType)) {
                            String filePath = FileUtil.getSaveFile(getApplicationContext(), "front" + i).getAbsolutePath();
                            recIDCard(IDCardParams.ID_CARD_SIDE_FRONT, filePath);

                        } else if (CameraActivity.CONTENT_TYPE_ID_CARD_BACK.equals(contentType)) {
                            String filePath = FileUtil.getSaveFile(getApplicationContext(), "back" + i).getAbsolutePath();
                            recIDCard(IDCardParams.ID_CARD_SIDE_BACK, filePath);
                        }
                    }

                    break;
                case REQUEST_CODE_VEHICLE_LICENSE:

                    String filePath = FileUtil.getSaveFile(getApplicationContext(), "certificate" + i).getAbsolutePath();
                    Glide.with(RealNameAuthActivity2.this).load(Uri.fromFile(new File(filePath))).into(ivIdcardCertificate);
                    idCardCertificatePath = filePath;
                    LogUtils.show(TAG, "idCardCertificatePath = " + idCardCertificatePath);
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 解析身份证图片
     * @param idCardSide
     *         身份证正反面
     * @param filePath
     *         图片路径
     */
    private void recIDCard(String idCardSide, String filePath) {
        LogUtils.show(TAG, "filePath = " + filePath);
        IDCardParams param = new IDCardParams();
        param.setImageFile(new File(filePath));
        // 设置身份证正反面
        param.setIdCardSide(idCardSide);
        // 设置方向检测
        param.setDetectDirection(true);
        // 设置图像参数压缩质量0-100, 越大图像质量越好但是请求时间越长。 不设置则默认值为20
        param.setImageQuality(40);
        OCR.getInstance().recognizeIDCard(param, new OnResultListener<IDCardResult>() {
            @Override
            public void onResult(IDCardResult result) {
                if (result != null) {
                    if (TextUtils.equals(idCardSide, IDCardParams.ID_CARD_SIDE_FRONT)) {
                        Glide.with(RealNameAuthActivity2.this).load(Uri.fromFile(new File(filePath))).into(ivIdcardPicPositive);
                        idCardPositivePath = filePath;

                        if (result.getName() != null) {
                            String name = result.getName().toString();
                            etRealName.setText(name);
                        }

                        if (result.getIdNumber() != null) {
                            String num = result.getIdNumber().toString();
                            etIdcardNumber.setText(num);
                        }
                    } else if (TextUtils.equals(idCardSide, IDCardParams.ID_CARD_SIDE_BACK)) {

                        Glide.with(RealNameAuthActivity2.this).load(Uri.fromFile(new File(filePath))).into(ivIdcardPicBack);
                        idCardBackPath = filePath;
                    }
                }
            }

            @Override
            public void onError(OCRError error) {
                Toast.makeText(RealNameAuthActivity2.this, "未识别出身份证请重试", Toast.LENGTH_SHORT).show();
                Log.d("MainActivity", "onError: " + error.getMessage());
            }
        });
    }

    private void submitAuthInfo() {
        String realName = etRealName.getText().toString().trim();
        String idcardNumber = etIdcardNumber.getText().toString().trim();

        if (TextUtils.isEmpty(realName)) {
            Toast.makeText(this, "请输入您的真实姓名", Toast.LENGTH_SHORT).show();

        } else if (TextUtils.isEmpty(idcardNumber)) {
            Toast.makeText(this, "请输入您的身份证号码", Toast.LENGTH_SHORT).show();

        } else if (TextUtils.isEmpty(idCardPositivePath)) {
            Toast.makeText(this, "请选择您的身份证照片面", Toast.LENGTH_SHORT).show();

        } else if (TextUtils.isEmpty(idCardBackPath)) {
            Toast.makeText(this, "请选择您的身份证国徽面", Toast.LENGTH_SHORT).show();

        } else {
            //访问网络
            gotoSubmitAuthInfo(realName, idcardNumber);
        }
    }

    //上传实名信息
    private void gotoSubmitAuthInfo(String realName, String idcardNumber) {
        progressDialog.show();

        String mUserId = SPUtils.getString(RealNameAuthActivity2.this, GlobalConstants.userID, "");
        File idCardPositiveFile = new File(idCardPositivePath);
        File idCardBackFile = new File(idCardBackPath);

        PostFormBuilder postFormBuilder = OkHttpUtils.post().url(GlobalConstants.URL_REAL_NAME_AUTH)
                .addParams("userid", mUserId)
                .addParams("uname", realName)
                .addParams("idnumber", idcardNumber)
                .addFile("backcard", "backcard.png", idCardPositiveFile)//头像面
                .addFile("fontcard", "fontcard.png", idCardBackFile);//国徽面

        if (!TextUtils.isEmpty(idCardCertificatePath)) {
            File idCardCertificateFile = new File(idCardCertificatePath);
            LogUtils.show(TAG, "idCardCertificateFile = " + idCardCertificateFile);
            postFormBuilder.addFile("certification", "certification.png", idCardCertificateFile);
        }

        postFormBuilder.build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                LogUtils.show(TAG, "---上传身份信息失败---");
                Toast.makeText(RealNameAuthActivity2.this, "网络异常,上传失败,请重试", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }

            @Override
            public void onResponse(String response, int id) {
                progressDialog.dismiss();

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    int status = jsonObject.getInt("status");
                    String message = jsonObject.getString("message");
                    switch (status) {
                        case 200:   //成功
                            Toast.makeText(RealNameAuthActivity2.this, message, Toast.LENGTH_SHORT).show();
                            finish();
                            break;
                        case 204:   //失败
                            Toast.makeText(RealNameAuthActivity2.this, message, Toast.LENGTH_SHORT).show();
                            break;
                        default:
                            break;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.i(TAG, "onResponse: ---解析身份信息返回的数据出现异常---");
                }
            }
        });
    }
}
