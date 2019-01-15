package com.xasfemr.meiyaya.activity;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.google.gson.Gson;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.ui.ImageGridActivity;
import com.netease.LSMediaCapture.lsMediaCapture;
import com.netease.vcloud.video.effect.VideoEffect;
import com.netease.vcloud.video.render.NeteaseView;
import com.xasfemr.meiyaya.R;
import com.xasfemr.meiyaya.bean.CourseClassData;
import com.xasfemr.meiyaya.bean.LiveChannelUrlData;
import com.xasfemr.meiyaya.global.GlobalConstants;
import com.xasfemr.meiyaya.main.BaseActivity;
import com.xasfemr.meiyaya.neteasecloud.LiveStreamingActivity;
import com.xasfemr.meiyaya.utils.LogUtils;
import com.xasfemr.meiyaya.utils.SPUtils;
import com.xasfemr.meiyaya.utils.SelectDialog;
import com.xasfemr.meiyaya.utils.ToastUtil;
import com.xasfemr.meiyaya.view.progress.SFProgressDialog;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.File;
import java.io.Serializable;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

import static com.netease.LSMediaCapture.lsMediaCapture.FormatType.RTMP;
import static com.netease.LSMediaCapture.lsMediaCapture.StreamType.AV;
import static com.xasfemr.meiyaya.activity.DynamicAddActivity.REQUEST_CODE_SELECT;

public class LiveCreateActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "LiveCreateActivity";

    private String[]   firstClassStr;
    private String[][] secondClassStr;
    private String[][] secondClassDesc;
    private ArrayList<String> classStrList = new ArrayList<>(); //用来判断courseId

    private String coverImgPath;

    private int firstPosition = 0;
    private PopupWindow firstPopupWindow;
    private PopupWindow secondPopupWindow;

    private NeteaseView videoView;
    private ImageView   ivReverseCamera;
    private ImageView   ivLiveClose;
    private ImageView   ivLiveAddCover;
    private EditText    etLiveTitle;
    private TextView    tvLiveTitleLimit;
    private TextView    tvLiveClassFirst;
    private TextView    tvLiveClassSecond;
    private EditText    etCourseIntroduce;
    private TextView    tvCourseIntroLimit;
    private TextView    tvOpenCourse;
    private ListView    firstListView;
    private ListView    secondListView;

    private lsMediaCapture mLSMediaCapture;

    private String liveTitle;
    private String courseIntroduce;
    private String liveClassSecond;

    private SFProgressDialog sfProgressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creat_live);

        videoView = (NeteaseView) findViewById(R.id.video_view);
        ivReverseCamera = (ImageView) findViewById(R.id.iv_live_reverse_camera);
        ivLiveClose = (ImageView) findViewById(R.id.iv_live_close);
        ivLiveAddCover = (ImageView) findViewById(R.id.iv_live_add_cover);
        etLiveTitle = (EditText) findViewById(R.id.et_live_title);
        tvLiveTitleLimit = (TextView) findViewById(R.id.tv_live_title_limit);
        tvLiveClassFirst = (TextView) findViewById(R.id.tv_live_class_first);//一级分类
        tvLiveClassSecond = (TextView) findViewById(R.id.tv_live_class_second);//二级分类
        etCourseIntroduce = (EditText) findViewById(R.id.et_live_course_introduce);
        tvCourseIntroLimit = (TextView) findViewById(R.id.tv_live_course_introduce_limit);
        tvOpenCourse = (TextView) findViewById(R.id.tv_live_open_course);

        ivReverseCamera.setOnClickListener(this);
        ivLiveClose.setOnClickListener(this);
        ivLiveAddCover.setOnClickListener(this);
        tvLiveClassFirst.setOnClickListener(this);
        tvLiveClassSecond.setOnClickListener(this);
        tvOpenCourse.setOnClickListener(this);
        sfProgressDialog = new SFProgressDialog(this);

        //从网络获取课程的分类
        gotoGetCourseClass();

        ////获取权限
        //getPermissions();
        callBackFrontCamera();

        //放在onCreate()方法中new对象,有且仅有一个对象被new出来,虽然用户不点击popupwindow会白白new一个对象,但是不会出现new多次对象的情况
        firstListView = new ListView(this);
        firstListView.setBackgroundResource(R.drawable.popup_class_bg);
        firstListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                firstPosition = position;
                tvLiveClassFirst.setText(firstClassStr[firstPosition]);
                tvLiveClassSecond.setText(secondClassStr[firstPosition][0]);
                firstPopupWindow.dismiss();
                etCourseIntroduce.setText(secondClassDesc[firstPosition][0]);
            }
        });

        secondListView = new ListView(this);
        secondListView.setBackgroundResource(R.drawable.popup_class_bg);
        secondListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                tvLiveClassSecond.setText(secondClassStr[firstPosition][position]);
                secondPopupWindow.dismiss();
                etCourseIntroduce.setText(secondClassDesc[firstPosition][position]);
            }
        });

        etLiveTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                tvLiveTitleLimit.setText(s.length() + "/15");
            }
        });

        etCourseIntroduce.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                tvCourseIntroLimit.setText(s.length() + "/100");
            }
        });
    }

    private void gotoGetCourseClass() {
        sfProgressDialog.show();
        OkHttpUtils.get().url(GlobalConstants.URL_COURSE_CLASS)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        sfProgressDialog.dismiss();
                        LogUtils.show(TAG, "---获取课程分类失败---");
                        ToastUtil.showShort(LiveCreateActivity.this, "课程分类获取失败,请检查网络");
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        sfProgressDialog.dismiss();
                        LogUtils.show(TAG, "---获取课程分类成功---response = " + response + " ===");
                        try {
                            parserCourseClassJson(response);
                        } catch (Exception e) {
                            e.printStackTrace();
                            LogUtils.show(TAG, "---解析课程分类出现异常---");
                        }
                    }
                });
    }

    private void parserCourseClassJson(String response) {
        Gson gson = new Gson();
        CourseClassData courseClassData = gson.fromJson(response, CourseClassData.class);

        if (courseClassData != null && courseClassData.data != null && courseClassData.data.size() > 0) {
            //一维数组初始化  courseClassData.data.size()是一维数组的长度
            firstClassStr = new String[courseClassData.data.size()];
            //一维数组赋值
            for (int i = 0; i < courseClassData.data.size(); i++) {
                firstClassStr[i] = courseClassData.data.get(i).cname;
            }

            classStrList.clear();
            classStrList.add("其他");

            //二维数组初始化 courseClassData.data.size()是二维数组第一维的长度 (这种赋值方式比较牛逼啊！第二维的长度可以在随后的代码中执行)
            secondClassStr = new String[courseClassData.data.size()][];
            secondClassDesc = new String[courseClassData.data.size()][];
            //遍历二维数组的第一维
            for (int i = 0; i < courseClassData.data.size(); i++) {
                //二维数组第二维的初始化
                if (courseClassData.data.get(i).list != null && courseClassData.data.get(i).list.size() > 0) {
                    secondClassStr[i] = new String[courseClassData.data.get(i).list.size()];
                    secondClassDesc[i] = new String[courseClassData.data.get(i).list.size()];
                    //赋值
                    for (int j = 0; j < courseClassData.data.get(i).list.size(); j++) {
                        secondClassStr[i][j] = courseClassData.data.get(i).list.get(j).cname;
                        secondClassDesc[i][j] = courseClassData.data.get(i).list.get(j).cname + "：" + courseClassData.data.get(i).list.get(j).desc;
                        classStrList.add(courseClassData.data.get(i).list.get(j).cname);
                    }
                }
            }

            if (secondClassDesc[0] != null && secondClassDesc[0].length > 0) {
                //初始化直播课程的描述，默认secondClassDesc[0][0]
                etCourseIntroduce.setText(secondClassDesc[0][0]);
            }
            /*------------------------------打日志验证---------------------------------*/
            LogUtils.show(TAG, "firstClassStr.length = " + firstClassStr.length);
            for (int i = 0; i < firstClassStr.length; i++) {
                LogUtils.show(TAG, "firstClassStr[" + i + "] = " + firstClassStr[i]);
            }

            LogUtils.show(TAG, "secondClassStr.length = " + secondClassStr.length);
            for (int i = 0; i < secondClassStr.length; i++) {
                LogUtils.show(TAG, "---secondClassStr[" + i + "].length = " + secondClassStr[i].length);
                for (int j = 0; j < secondClassStr[i].length; j++) {
                    LogUtils.show(TAG, "------secondClassStr[" + i + "][" + j + "] = " + secondClassStr[i][j]);
                }
            }

            LogUtils.show(TAG, "secondClassDesc.length = " + secondClassDesc.length);
            for (int i = 0; i < secondClassDesc.length; i++) {
                LogUtils.show(TAG, "--secondClassDesc[" + i + "].length = " + secondClassDesc[i].length);
                for (int j = 0; j < secondClassDesc[i].length; j++) {
                    LogUtils.show(TAG, "----secondClassDesc[" + i + "][" + j + "] = " + secondClassDesc[i][j]);
                }
            }
            LogUtils.show(TAG, "classStrList = " + classStrList.toString());
        }
    }

    //回调前置相机
    private void callBackFrontCamera() {
        lsMediaCapture.LsMediaCapturePara lsMediaCapturePara = new lsMediaCapture.LsMediaCapturePara();
        lsMediaCapturePara.setContext(this);
        mLSMediaCapture = new lsMediaCapture(lsMediaCapturePara);
        mLSMediaCapture.startVideoPreview(videoView, true, true, lsMediaCapture.VideoQuality.SUPER_HIGH, false);


        //if (mLSMediaCapture == null) {
        //    lsMediaCapture.LsMediaCapturePara lsMediaCapturePara = new lsMediaCapture.LsMediaCapturePara();
        //    lsMediaCapturePara.setContext(this); //设置SDK上下文（建议使用ApplicationContext）
        //    //lsMediaCapturePara.setMessageHandler(this); //设置SDK消息回调
        //    lsMediaCapturePara.setLogLevel(lsLogUtil.LogLevel.INFO); //日志级别
        //    lsMediaCapturePara.setUploadLog(false);//是否上传SDK日志
        //    mLSMediaCapture = new lsMediaCapture(lsMediaCapturePara);
        //}
        ////VodeoView, 是否前置摄像头, 是否滤镜, 清晰度 , 视频比例是否强制16:9
        //mLSMediaCapture.startVideoPreview(videoView, true, true, lsMediaCapture.VideoQuality.SUPER, false);
    }


    //切换前后摄像头
    private void switchCamera() {
        //if (mLSMediaCapture != null) {
        //    mLSMediaCapture.switchCamera();
        //}
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.iv_live_reverse_camera:  //相机反转
                //switchCamera();
                break;
            case R.id.iv_live_close:          // 关闭直播
                finish();
                break;
            case R.id.iv_live_add_cover:      // 上传直播封面
                addLiveCover();
                break;
            case R.id.tv_live_class_first:      //一级分类
                showFirstPopup();
                break;
            case R.id.tv_live_class_second:    //二级分类
                showSecondPopup();
                break;
            case R.id.tv_live_open_course:    //开课

                liveTitle = etLiveTitle.getText().toString().trim();
                courseIntroduce = etCourseIntroduce.getText().toString().trim();
                liveClassSecond = tvLiveClassSecond.getText().toString().trim();
                LogUtils.show(TAG, "onClick: liveClassSecond = " + liveClassSecond);

                if (TextUtils.isEmpty(liveTitle)) {
                    ToastUtil.showShort(this, "请输入标题");
                    return;
                }

                if (TextUtils.isEmpty(coverImgPath)) {
                    ToastUtil.showShort(this, "请选择课程封面");
                    return;
                }

                if (TextUtils.isEmpty(courseIntroduce)) {
                    ToastUtil.showShort(this, "请填写课程介绍");
                    return;
                }

                getLiveChannelFromServer();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == ImagePicker.RESULT_CODE_ITEMS) {
            //添加图片返回
            if (data != null && requestCode == REQUEST_CODE_SELECT) {
                ArrayList<ImageItem> images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
                LogUtils.show("images.size() = ", "" + images.size());
                if (images != null) {
                    //设置图片
                    ImageItem imageItem = images.get(0);
                    ImagePicker.getInstance().getImageLoader().displayImage(LiveCreateActivity.this, imageItem.path, ivLiveAddCover, 0, 0);
                    LogUtils.show(TAG, "" + imageItem.path); //  /storage/emulated/0/DCIM/Camera/IMG_20171101_173307.jpg
                    coverImgPath = imageItem.path;
                }
            }
        }
    }

    private void addLiveCover() {
        //设置ImagePicker为单选
        ImagePicker.getInstance().setMultiMode(true);
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
                        Intent intent = new Intent(LiveCreateActivity.this, ImageGridActivity.class);
                        intent.putExtra(ImageGridActivity.EXTRAS_TAKE_PICKERS, true); // 是否是直接打开相机
                        startActivityForResult(intent, REQUEST_CODE_SELECT);
                        break;
                    case 1:
                        //打开选择,本次允许选择的数量
                        ImagePicker.getInstance().setSelectLimit(1);
                        Intent intent1 = new Intent(LiveCreateActivity.this, ImageGridActivity.class);
                        /* 如果需要进入选择的时候显示已经选中的图片，详情请查看ImagePickerActivity*/
                        startActivityForResult(intent1, REQUEST_CODE_SELECT);
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


    private void showFirstPopup() {
        LogUtils.show(TAG, "firstClassStr = " + firstClassStr);
        if (firstClassStr == null || firstClassStr.length == 0) {
            //从网络获取课程的分类
            gotoGetCourseClass();
            return;
        }

        if (firstPopupWindow == null) {//用户可能多次点击,不必重复创建对象
            firstPopupWindow = new PopupWindow(firstListView, tvLiveClassFirst.getWidth(), ViewGroup.LayoutParams.WRAP_CONTENT, true);
            firstPopupWindow.setBackgroundDrawable(new ColorDrawable());
        }
        firstPopupWindow.showAsDropDown(tvLiveClassFirst);
        firstListView.setAdapter(new ClassAdapter(firstClassStr));
    }

    private void showSecondPopup() {
        if (secondClassStr == null || secondClassStr.length == 0 || secondClassStr[firstPosition] == null || secondClassStr[firstPosition].length == 0) {
            //从网络获取课程的分类
            gotoGetCourseClass();
            return;
        }

        if (secondPopupWindow == null) {
            secondPopupWindow = new PopupWindow(secondListView, tvLiveClassSecond.getWidth(), ViewGroup.LayoutParams.WRAP_CONTENT, true);
            secondPopupWindow.setBackgroundDrawable(new ColorDrawable());
        }
        secondPopupWindow.showAsDropDown(tvLiveClassSecond);
        secondListView.setAdapter(new ClassAdapter(secondClassStr[firstPosition]));
    }


    private class ClassAdapter extends BaseAdapter {

        private String[] mArray;

        //采用构造方法将要展示的数据的数组传进来,这样这个适配器就可以共用了
        public ClassAdapter(String[] strArray) {
            mArray = strArray;
        }

        @Override
        public int getCount() {
            return mArray.length;
        }

        @Override
        public String getItem(int position) {
            return mArray[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = View.inflate(LiveCreateActivity.this, R.layout.list_item_popupwindow, null);
            TextView tvClass = (TextView) view.findViewById(R.id.tv_class);
            tvClass.setText(getItem(position));
            return view;

            //显示不出来文字??????
            /*int dp10 = UiUtils.dp2px(LiveCreateActivity.this, 10);
            TextView textView = new TextView(LiveCreateActivity.this);
            textView.setText(mArray[position]);
            textView.setPadding(dp10,dp10,dp10,dp10);
            textView.setTextColor(0x3C3C3C);
            textView.setTextSize(12);
            return textView;*/
        }
    }

    //从服务器获取直播频道
    private void getLiveChannelFromServer() {
        sfProgressDialog.show();
        //获取用户可开课直播频道 并且 设置讲师开课状态
        File file = new File(coverImgPath);
        String userId = SPUtils.getString(LiveCreateActivity.this, GlobalConstants.userID, "");
        int courseId = classStrList.indexOf(liveClassSecond);
        LogUtils.show(TAG, "courseId = " + courseId);

        //选择"其他"时courseId = 0,此时访问网络总是走onError()方法,显示"网络错误，请稍后重试"
        OkHttpUtils.post().url(GlobalConstants.URL_GET_LIVE_CHANNEL)
                .addParams("userid", userId)
                .addParams("courseid", courseId + "")
                .addParams("title", liveTitle)
                .addParams("des", courseIntroduce)
                .addFile("cover", "cover.png", file)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        e.printStackTrace();
                        if (e instanceof SocketException) {
                            ToastUtil.showShort(LiveCreateActivity.this, "网络连接超时");
                        } else {
                            ToastUtil.showShort(LiveCreateActivity.this, "网络错误，请稍后重试");
                        }
                        sfProgressDialog.dismiss();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        sfProgressDialog.dismiss();
                        LogUtils.show(TAG, "response = " + response + " ----");
                        try {
                            parserLiveChannelJson(response);
                        } catch (Exception e) {
                            e.printStackTrace();
                            ToastUtil.showShort(LiveCreateActivity.this, "解析异常");
                        }
                    }
                });
    }

    private void parserLiveChannelJson(String response) {

        Gson gson = new Gson();
        LiveChannelUrlData liveChannelUrlData = gson.fromJson(response, LiveChannelUrlData.class);

        PublishParam publishParam = new PublishParam();
        publishParam.pushUrl = liveChannelUrlData.data.addr.pushUrl;
        publishParam.cid = liveChannelUrlData.data.cid;
        publishParam.courseIntroduce = courseIntroduce;
        Intent intent = new Intent(LiveCreateActivity.this, LiveStreamingActivity.class);
        intent.putExtra("data", publishParam);

        mLSMediaCapture.stopVideoPreview();
        mLSMediaCapture.destroyVideoPreview();
        mLSMediaCapture.uninitLsMediaCapture(true);
        mLSMediaCapture = null;
        startActivity(intent);
        this.finish();

    }

    public static class PublishParam implements Serializable {
        public String                    pushUrl    = null; //推流地址,
        public lsMediaCapture.StreamType streamType = AV;  // 推流类型, lxb:AV指音视频
        public lsMediaCapture.FormatType formatType = RTMP; // 推流格式, lxb:RTMP指推流
        public String recordPath; //文件录制地址，当formatType 为 MP4 或 RTMP_AND_MP4 时有效 (lxb:没有到)
        public lsMediaCapture.VideoQuality videoQuality = lsMediaCapture.VideoQuality.SUPER_HIGH; //清晰度
        public boolean                     isScale_16x9 = false; //是否强制16:9
        public boolean                     useFilter    = true; //是否使用滤镜
        public VideoEffect.FilterType      filterType   = VideoEffect.FilterType.nature; //滤镜类型
        public boolean                     frontCamera  = true; //是否默认前置摄像头
        public boolean                     watermark    = true; //是否添加水印
        public boolean                     qosEnable    = true;  //是否开启QOS
        public boolean                     graffitiOn   = false; //是否添加涂鸦
        public boolean                     uploadLog    = true; //是否上传SDK运行日志

        public String cid;//直播间ID
        public String courseIntroduce;//直播简介

    }


}
