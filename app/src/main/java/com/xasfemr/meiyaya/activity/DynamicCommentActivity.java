package com.xasfemr.meiyaya.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.lzy.ninegrid.ImageInfo;
import com.lzy.ninegrid.NineGridView;
import com.lzy.ninegrid.preview.NineGridViewClickAdapter;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.xasfemr.meiyaya.R;
import com.xasfemr.meiyaya.bean.DynamicCommentData;
import com.xasfemr.meiyaya.bean.DynamicData;
import com.xasfemr.meiyaya.bean.DynamicFollowData;
import com.xasfemr.meiyaya.global.GlobalConstants;
import com.xasfemr.meiyaya.main.BaseActivity;
import com.xasfemr.meiyaya.utils.SPUtils;
import com.xasfemr.meiyaya.view.CommentFrame;
import com.xasfemr.meiyaya.view.MeiYaYaHeader;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;

import static com.xasfemr.meiyaya.global.GlobalConstants.LIVE_BASE_URL;

public class DynamicCommentActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "DynamicCommentActivity";

    private static final int TYPE_DYNAMIC_DETAIL  = 0;   //RecyclerView显示两种布局,第0种
    private static final int TYPE_DYNAMIC_COMMENT = 1;   //RecyclerView显示两种布局,第1种

    private boolean isPullRefresh = false;
    private int     showType      = GlobalConstants.INTENT_DYNAMIC_LOOK_DETAIL; //用户是查看动态详情还是进来评论,默认查看动态详情
    private Boolean isFollow;
    private int     pageNo;
    private Intent  mIntent;

    private int                         clickPosition;
    private int                         isMyDynamic;
    private DynamicData.DynamicItemInfo mDynamicInfo;
    private ArrayList<DynamicCommentData.CommentInfo> mCommentList = new ArrayList<>();

    private DynamicDetailAdapter mCommentAdapter;
    private RecyclerView         rvDynamicDetailComment;
    private TextView             tvAddCommentContent;
    private RelativeLayout       rlRoot;
    private RefreshLayout        refreshLayout;
    private LinearLayout         llLoading;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            CommentFrame.liveCommentEdit(DynamicCommentActivity.this, rlRoot, new ContentResult());
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dynamic_detail_comment);
        initTopBar();
        setTopTitleText("动态");
        pageNo = 0;

        rlRoot = (RelativeLayout) findViewById(R.id.rl_root);
        tvAddCommentContent = (TextView) findViewById(R.id.tv_add_comment_content);
        rvDynamicDetailComment = (RecyclerView) findViewById(R.id.rv_dynamic_detail_comment);
        llLoading = (LinearLayout) findViewById(R.id.ll_loading);

        mIntent = getIntent();
        clickPosition = mIntent.getIntExtra("CLICK_POSITION", -1);
        isMyDynamic = mIntent.getIntExtra("IS_MY_DYNAMIC", -1);
        mDynamicInfo = (DynamicData.DynamicItemInfo) mIntent.getSerializableExtra("DYNAMIC_INFO");
        int type = mIntent.getIntExtra("DYNAMIC", GlobalConstants.INTENT_DYNAMIC_LOOK_DETAIL);
        if (type == GlobalConstants.INTENT_DYNAMIC_ADD_COMMENT) {
            mHandler.sendEmptyMessageDelayed(1, 50);
            showType = GlobalConstants.INTENT_DYNAMIC_ADD_COMMENT;
        }

        tvAddCommentContent.setOnClickListener(this);

        setRefreshLayout();
        gotoGetDynamicCommentData();
    }


    private void setRefreshLayout() {
        refreshLayout = (RefreshLayout) findViewById(R.id.refreshLayout);
        refreshLayout.setRefreshHeader(new MeiYaYaHeader(this));
        refreshLayout.setRefreshFooter(new ClassicsFooter(this).setSpinnerStyle(SpinnerStyle.Scale));

        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                //下拉刷新
                pageNo = 0;
                isPullRefresh = true;
                mCommentList.clear();
                gotoGetDynamicCommentData();
            }
        });
        refreshLayout.setOnLoadmoreListener(new OnLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                //上拉加载更多
                pageNo++;
                gotoGetDynamicCommentData();
            }
        });
    }

    private void gotoGetDynamicCommentData() {
        if (pageNo == 0 && !isPullRefresh) {
            llLoading.setVisibility(View.VISIBLE);
        }
        OkHttpUtils.get().url(GlobalConstants.URL_DYNAMIC_COMMENT)
                .addParams("id", mDynamicInfo.id)
                .addParams("page", pageNo + "")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.i(TAG, "onError: -----网络异常,获取动态评论失败!-----");
                        Toast.makeText(DynamicCommentActivity.this, "获取评论失败", Toast.LENGTH_SHORT).show();

                        //网络异常没有获取到评论数据,还是要展示动态详情
                        if (pageNo == 0) { //第一次就获取数据失败
                            rvDynamicDetailComment.setLayoutManager(new LinearLayoutManager(DynamicCommentActivity.this));
                            rvDynamicDetailComment.setAdapter(new DynamicDetailAdapter());
                        } else {           //后面获取数据失败
                            mCommentAdapter.notifyDataSetChanged();
                        }
                        refreshLayout.finishRefresh();
                        refreshLayout.finishLoadmore();
                        llLoading.setVisibility(View.GONE);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.i(TAG, "onResponse: -----获取动态评论成功-----response = " + response + "-----");
                        try {
                            parserCommentJson(response);
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.i(TAG, "onResponse: -----动态评论解析出现异常-----");
                        }
                        refreshLayout.finishRefresh();
                        refreshLayout.finishLoadmore();
                        llLoading.setVisibility(View.GONE);
                    }
                });
    }

    private void parserCommentJson(String response) {
        Gson gson = new Gson();
        DynamicCommentData dynamicCommentData = gson.fromJson(response, DynamicCommentData.class);

        if (dynamicCommentData == null || dynamicCommentData.data == null || dynamicCommentData.data.size() == 0) {
            //没有评论
            if (pageNo == 0) {
                Toast.makeText(this, "此条动态还没有评论", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "没有更多评论了", Toast.LENGTH_SHORT).show();
            }
        } else {
            //有评论
            mCommentList.addAll(dynamicCommentData.data);
        }

        //RecyclerView加载数据
        if (mCommentList.size() <= 15 && pageNo == 0) {  //后台每页显示15条数据,这是第一页数据
            rvDynamicDetailComment.setLayoutManager(new LinearLayoutManager(DynamicCommentActivity.this));
            mCommentAdapter = new DynamicDetailAdapter();
            rvDynamicDetailComment.setAdapter(mCommentAdapter);
        } else {
            mCommentAdapter.notifyDataSetChanged();
        }

        //如果用户点击的是评论则让RecyclerView直接滑动到评论处
        if (showType == GlobalConstants.INTENT_DYNAMIC_ADD_COMMENT) {
            rvDynamicDetailComment.scrollToPosition(1);
            showType = GlobalConstants.INTENT_DYNAMIC_LOOK_DETAIL;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_add_comment_content:
                CommentFrame.liveCommentEdit(DynamicCommentActivity.this, rlRoot, new ContentResult());
                break;
            default:
                break;
        }
    }

    private class ContentResult implements CommentFrame.liveCommentResult {
        @Override
        public void onResult(boolean confirmed, String comment) {
            comment = removeEnterKey(comment);
            gotoSendComment(comment);
        }
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

    private void gotoSendComment(final String comment) {

        String userId = SPUtils.getString(DynamicCommentActivity.this, GlobalConstants.userID, "");
        OkHttpUtils.post().url(GlobalConstants.URL_COMMENT_ADD)
                .addParams("userid", userId)
                .addParams("id", mDynamicInfo.id)
                .addParams("content", comment)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.i(TAG, "onError: -----评论发表失败!-----");
                        Toast.makeText(DynamicCommentActivity.this, "评论失败,请重试", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.i(TAG, "onResponse: -----评论发表成功!-----");
                        Log.i(TAG, "onResponse: response = [" + response + "]");
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String message = jsonObject.getString("message");
                            //弹出服务器提示信息
                            if (!TextUtils.isEmpty(message)) {
                                Toast.makeText(DynamicCommentActivity.this, message, Toast.LENGTH_SHORT).show();
                                //评论发布成功,插入一条数据,并刷新列表
                                refreshCommentList(comment);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    //评论发布成功,插入一条数据,并刷新列表
    private void refreshCommentList(String comment) {

        String mUserName = SPUtils.getString(DynamicCommentActivity.this, GlobalConstants.USER_NAME, "我");
        String mUserIcon = SPUtils.getString(DynamicCommentActivity.this, GlobalConstants.USER_HEAD_IMAGE, LIVE_BASE_URL + "Public/Uploads/2017-09-25/defaultimg.png");

        DynamicCommentData.CommentInfo addCommentInfo = new DynamicCommentData.CommentInfo();

        addCommentInfo.content = comment;
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        String curTime = formatter.format(curDate);
        addCommentInfo.datetime = curTime;
        addCommentInfo.cat_name = mUserName;
        addCommentInfo.images1 = mUserIcon;

        mCommentList.add(0, addCommentInfo);

        mCommentAdapter.notifyDataSetChanged();
    }

    private class DynamicDetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        @Override
        public int getItemViewType(int position) {
            if (position == 0) {
                //动态详情
                return TYPE_DYNAMIC_DETAIL;
            } else {
                //动态详情评论
                return TYPE_DYNAMIC_COMMENT;
            }
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = null;
            RecyclerView.ViewHolder holder = null;
            switch (viewType) {
                case TYPE_DYNAMIC_DETAIL:
                    view = View.inflate(DynamicCommentActivity.this, R.layout.item_dynamic_detail, null);
                    holder = new DynamicDetailHolder(view);
                    break;
                case TYPE_DYNAMIC_COMMENT:
                    view = View.inflate(DynamicCommentActivity.this, R.layout.item_dynamic_detail_comment, null);
                    holder = new DynamicDetailCommentHolder(view);
                    break;
                default:
                    break;
            }
            return holder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

            int type = getItemViewType(position);
            switch (type) {
                case TYPE_DYNAMIC_DETAIL:
                    final DynamicDetailHolder dynamicDetailHolder = (DynamicDetailHolder) holder;

                    Glide.with(DynamicCommentActivity.this).load(mDynamicInfo.images1).into(dynamicDetailHolder.civDyUserIcon);
                    dynamicDetailHolder.tvDyUserName.setText(mDynamicInfo.cat_name);
                    dynamicDetailHolder.tvDySendTime.setText(mDynamicInfo.datetime);

                    if (TextUtils.isEmpty(mDynamicInfo.content)) {
                        dynamicDetailHolder.tvDyContent.setVisibility(View.GONE);
                    } else {
                        dynamicDetailHolder.tvDyContent.setVisibility(View.VISIBLE);
                        dynamicDetailHolder.tvDyContent.setText(mDynamicInfo.content);
                    }

                    if (isMyDynamic > 0) { //从个人主页发布板块的动态跳转过来
                        isFollow = false;
                        dynamicDetailHolder.ivDyFocus.setVisibility(View.GONE);
                        switch (isMyDynamic) {
                            case 1: //自己看自己的动态
                                dynamicDetailHolder.ivDyDelete.setVisibility(View.VISIBLE);
                                break;
                            case 2: //看别人的动态
                                dynamicDetailHolder.ivDyDelete.setVisibility(View.GONE);
                                break;
                            default:
                                break;
                        }
                    } else {      //从美页圈的动态跳转过来
                        //是否关注
                        if (mDynamicInfo.attention == 0) {  //未关注
                            dynamicDetailHolder.ivDyFocus.setVisibility(View.VISIBLE);
                            dynamicDetailHolder.ivDyDelete.setVisibility(View.GONE);
                            dynamicDetailHolder.ivDyFocus.setImageResource(R.drawable.focus);
                            isFollow = false;

                        } else if (mDynamicInfo.attention == 1) { //已关注
                            dynamicDetailHolder.ivDyFocus.setVisibility(View.VISIBLE);
                            dynamicDetailHolder.ivDyDelete.setVisibility(View.GONE);
                            dynamicDetailHolder.ivDyFocus.setImageResource(R.drawable.focused);
                            isFollow = true;

                        } else if (mDynamicInfo.attention == -1) {  //自己的动态不用显示关注按钮
                            dynamicDetailHolder.ivDyFocus.setVisibility(View.GONE);
                            dynamicDetailHolder.ivDyDelete.setVisibility(View.VISIBLE);
                            isFollow = false;

                        } else {   //默认
                            dynamicDetailHolder.ivDyFocus.setVisibility(View.VISIBLE);
                            dynamicDetailHolder.ivDyDelete.setVisibility(View.GONE);
                            dynamicDetailHolder.ivDyFocus.setImageResource(R.drawable.focus);
                            isFollow = false;
                        }
                    }
                    //图片九宫格展示
                    ArrayList<ImageInfo> imageList = new ArrayList<>();
                    ArrayList<String> imageUrlList = mDynamicInfo.picture;
                    if (imageUrlList != null) {
                        for (int i = 0; i < imageUrlList.size(); i++) {
                            ImageInfo imageInfo = new ImageInfo();
                            imageInfo.setThumbnailUrl(imageUrlList.get(i));
                            imageInfo.setBigImageUrl(imageUrlList.get(i));
                            imageList.add(imageInfo);
                        }
                    }
                    dynamicDetailHolder.nineGridView.setAdapter(new NineGridViewClickAdapter(DynamicCommentActivity.this, imageList));

                    //点击头像
                    dynamicDetailHolder.civDyUserIcon.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(DynamicCommentActivity.this, UserPagerActivity.class);
                            intent.putExtra("LOOK_USER_ID", mDynamicInfo.userid);
                            startActivity(intent);
                        }
                    });

                    //关注与取关
                    dynamicDetailHolder.ivDyFocus.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dynamicDetailHolder.ivDyFocus.setEnabled(false);
                            if (isFollow) {
                                //取消关注
                                isFollow = false;
                                mDynamicInfo.attention = 0;
                                dynamicDetailHolder.ivDyFocus.setImageResource(R.drawable.focus);
                                gotoCancelFollow(mDynamicInfo.userid, dynamicDetailHolder);
                            } else {
                                //添加关注
                                isFollow = true;
                                mDynamicInfo.attention = 1;
                                dynamicDetailHolder.ivDyFocus.setImageResource(R.drawable.focused);
                                gotoAddFollow(mDynamicInfo.userid, dynamicDetailHolder);
                            }
                        }
                    });

                    dynamicDetailHolder.ivDyDelete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(DynamicCommentActivity.this);
                            builder.setTitle("删除动态？");
                            builder.setMessage("确定删除这条动态吗？");
                            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            });
                            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //删除自己的动态
                                    gotoDeleteMyDynamic(mDynamicInfo.id);
                                }
                            });
                            builder.show();
                        }
                    });
                    break;
                case TYPE_DYNAMIC_COMMENT:
                    DynamicDetailCommentHolder dynamicDetailCommentHolder = (DynamicCommentActivity.DynamicDetailCommentHolder) holder;

                    if (mCommentList == null || mCommentList.size() == 0) {
                        //展示没有评论的界面情况
                    } else {
                        Glide.with(DynamicCommentActivity.this).load(mCommentList.get(position - 1).images1).into(dynamicDetailCommentHolder.civDyUserIcon);
                        dynamicDetailCommentHolder.tvDyUserName.setText(mCommentList.get(position - 1).cat_name);
                        dynamicDetailCommentHolder.tvDySendTime.setText(mCommentList.get(position - 1).datetime);
                        dynamicDetailCommentHolder.tvDyCommentContent.setText(mCommentList.get(position - 1).content);

                        dynamicDetailCommentHolder.civDyUserIcon.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(DynamicCommentActivity.this, UserPagerActivity.class);
                                intent.putExtra("LOOK_USER_ID", mCommentList.get(position - 1).userid);
                                startActivity(intent);
                            }
                        });

                    }
                    break;

                default:
                    break;
            }
        }

        @Override
        public int getItemCount() {
            if (mCommentList == null || mCommentList.size() == 0) {
                return 1;
            } else {
                return mCommentList.size() + 1; //条目数量等于评论数量+1(动态还占一个条目)
            }
        }
    }

    //删除自己的动态
    private void gotoDeleteMyDynamic(String did) {
        String userId = SPUtils.getString(DynamicCommentActivity.this, GlobalConstants.userID, "");

        OkHttpUtils.get().url(GlobalConstants.URL_DYNAMIC_DELETE)
                .addParams("id", did)
                .addParams("userid", userId)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.i(TAG, "onError: -----网络异常,动态删除失败-----");
                        Toast.makeText(DynamicCommentActivity.this, "网络异常,动态删除失败", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.i(TAG, "onResponse: -----动态删除成功-----");
                        Log.i(TAG, "onResponse: -----response = " + response + "-----");
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String message = jsonObject.getString("message");
                            //吐司弹出服务器返回内容
                            if (!TextUtils.isEmpty(message)) {
                                Toast.makeText(DynamicCommentActivity.this, message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.i(TAG, "onResponse: -----动态删除成功,但解析服务器信息出现异常-----");
                        }
                        mIntent.putExtra("DELETE_POSITION", clickPosition);
                        setResult(31, mIntent);
                        finish();
                    }
                });
    }

    //取消关注
    private void gotoCancelFollow(String uid, final DynamicDetailHolder holder) {
        String userId = SPUtils.getString(DynamicCommentActivity.this, GlobalConstants.userID, "");

        OkHttpUtils.get().url(GlobalConstants.URL_CANCEL_FOLLOW)
                .addParams("userid", userId)
                .addParams("uid", uid)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.i(TAG, "onError: -----网络异常,取消关注失败-----");
                        Toast.makeText(DynamicCommentActivity.this, "网络异常,取消关注失败,请重试", Toast.LENGTH_SHORT).show();

                        //取消关注失败将所有改变的状态回归
                        isFollow = true;
                        mDynamicInfo.attention = 1;
                        holder.ivDyFocus.setImageResource(R.drawable.focused);
                        holder.ivDyFocus.setEnabled(true);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.i(TAG, "onResponse: -----取消关注成功-----response = " + response + "-----");
                        //取消关注成功,所有状态之前已经改变了,这里可以不再做处理,这里我只是强化一下取消关注的状态
                        isFollow = false;
                        mDynamicInfo.attention = 0;
                        holder.ivDyFocus.setImageResource(R.drawable.focus);
                        holder.ivDyFocus.setEnabled(true);

                        try {
                            Gson gson = new Gson();
                            DynamicFollowData dynamicFollowData = gson.fromJson(response, DynamicFollowData.class);
                            if (dynamicFollowData != null) {
                                //弹出来自服务器的信息
                                Toast.makeText(DynamicCommentActivity.this, dynamicFollowData.message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    //添加关注
    private void gotoAddFollow(String uid, final DynamicDetailHolder holder) {
        String userId = SPUtils.getString(DynamicCommentActivity.this, GlobalConstants.userID, "");

        OkHttpUtils.get().url(GlobalConstants.URL_ADD_FOLLOW)
                .addParams("userid", userId)
                .addParams("uid", uid)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.i(TAG, "onError: -----网络异常,关注失败-----");
                        Toast.makeText(DynamicCommentActivity.this, "网络异常,关注失败,请重试", Toast.LENGTH_SHORT).show();
                        //关注失败将所有改变的状态回归
                        isFollow = false;
                        mDynamicInfo.attention = 0;
                        holder.ivDyFocus.setImageResource(R.drawable.focus);
                        holder.ivDyFocus.setEnabled(true);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.i(TAG, "onResponse: -----关注成功-----response = " + response + "-----");

                        //关注成功,所有状态之前已经改变了,这里可以不再做处理,这里我只是强化一下关注的状态
                        isFollow = true;
                        mDynamicInfo.attention = 1;
                        holder.ivDyFocus.setImageResource(R.drawable.focused);
                        holder.ivDyFocus.setEnabled(true);
                        try {
                            Gson gson = new Gson();
                            DynamicFollowData dynamicFollowData = gson.fromJson(response, DynamicFollowData.class);
                            if (dynamicFollowData != null) {
                                //弹出来自服务器的信息
                                Toast.makeText(DynamicCommentActivity.this, dynamicFollowData.message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    static class DynamicDetailHolder extends RecyclerView.ViewHolder {

        public View            itemView;
        public CircleImageView civDyUserIcon;
        public TextView        tvDyUserName;
        public TextView        tvDySendTime;
        public ImageView       ivDyFocus;
        public ImageView       ivDyDelete;//// TODO: 2017/11/18 0018  
        public TextView        tvDyContent;
        public NineGridView    nineGridView;


        public DynamicDetailHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            civDyUserIcon = (CircleImageView) itemView.findViewById(R.id.civ_dy_user_icon);//civ_dy_user_icon
            tvDyUserName = (TextView) itemView.findViewById(R.id.tv_dy_user_name);         //tv_dy_user_name
            tvDySendTime = (TextView) itemView.findViewById(R.id.tv_dy_send_time);          //tv_dy_send_time
            ivDyFocus = (ImageView) itemView.findViewById(R.id.iv_dy_focus);
            ivDyDelete = (ImageView) itemView.findViewById(R.id.iv_dy_delete);
            tvDyContent = (TextView) itemView.findViewById(R.id.tv_dy_content);             //tv_dy_content
            nineGridView = (NineGridView) itemView.findViewById(R.id.nineGridView);          //nineGridView
        }
    }

    static class DynamicDetailCommentHolder extends RecyclerView.ViewHolder {

        public View            itemView;
        public CircleImageView civDyUserIcon;
        public TextView        tvDyUserName;
        public TextView        tvDySendTime;
        public ImageView       ivDyDetailComment;
        public TextView        tvDyDetailLike;
        public TextView        tvDyCommentContent;
        //public LinearLayout    rlDyDetailCommentList;
        public TextView        tvCommentChildUserName;
        public TextView        tvCommentChildContent;
        public TextView        tvCommentChildNumber;

        public DynamicDetailCommentHolder(View itemView) {
            super(itemView);

            this.itemView = itemView;
            civDyUserIcon = (CircleImageView) itemView.findViewById(R.id.civ_dy_user_icon);// civ_dy_user_icon
            tvDyUserName = (TextView) itemView.findViewById(R.id.tv_dy_user_name);         //  tv_dy_user_name
            tvDySendTime = (TextView) itemView.findViewById(R.id.tv_dy_send_time);          //  tv_dy_send_time
            ivDyDetailComment = (ImageView) itemView.findViewById(R.id.iv_dy_detail_comment);
            tvDyDetailLike = (TextView) itemView.findViewById(R.id.tv_dy_detail_like);
            tvDyCommentContent = (TextView) itemView.findViewById(R.id.tv_dy_comment_content); // tv_dy_comment_content
            //rlDyDetailCommentList = (LinearLayout) itemView.findViewById(R.id.rl_dy_detail_comment_list);
            tvCommentChildUserName = (TextView) itemView.findViewById(R.id.tv_comment_child_user_name);
            tvCommentChildContent = (TextView) itemView.findViewById(R.id.tv_comment_child_content);
            tvCommentChildNumber = (TextView) itemView.findViewById(R.id.tv_comment_child_number);
        }
    }
}
