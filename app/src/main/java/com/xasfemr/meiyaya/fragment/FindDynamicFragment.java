package com.xasfemr.meiyaya.fragment;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.xasfemr.meiyaya.activity.DynamicAddActivity;
import com.xasfemr.meiyaya.activity.DynamicCommentActivity;
import com.xasfemr.meiyaya.activity.LoginActivity;
import com.xasfemr.meiyaya.activity.UserPagerActivity;
import com.xasfemr.meiyaya.bean.DynamicData;
import com.xasfemr.meiyaya.bean.DynamicFollowData;
import com.xasfemr.meiyaya.global.GlobalConstants;
import com.xasfemr.meiyaya.main.MainActivity;
import com.xasfemr.meiyaya.utils.LogUtils;
import com.xasfemr.meiyaya.utils.SPUtils;
import com.xasfemr.meiyaya.view.MeiYaYaHeader;
import com.xasfemr.meiyaya.view.ShareDynamicDialog;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;

/**
 * A simple {@link Fragment} subclass.
 */
public class FindDynamicFragment extends BaseFragment implements View.OnClickListener {
    private static final String TAG = "FindDynamicFragment";

    private ArrayList<DynamicData.DynamicItemInfo> mDynamicList = new ArrayList<>();
    private ArrayList<Boolean>                     mLikeList    = new ArrayList<>();
    private ArrayList<Boolean>                     mFocusList   = new ArrayList<>();

    private MainActivity mainActivity;
    private boolean isPullRefresh = false;
    private int pageNo;

    private RecyclerView   rvDynamic;
    private RefreshLayout  refreshLayout;
    private DynamicAdapter mDyAdapter;
    private LinearLayout   llLoading;
    private LinearLayout   llNetworkFailed;
    private Button         btnAgainLoad;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity) getActivity();
    }

    @Override
    public View initView() {
        View view = View.inflate(mainActivity, R.layout.fragment_find_dynamic, null);

        refreshLayout = (RefreshLayout) view.findViewById(R.id.refreshLayout);
        rvDynamic = (RecyclerView) view.findViewById(R.id.rv_dynamic);
        llLoading = (LinearLayout) view.findViewById(R.id.ll_loading);
        llNetworkFailed = (LinearLayout) view.findViewById(R.id.ll_network_failed);
        btnAgainLoad = (Button) view.findViewById(R.id.btn_again_load);
        btnAgainLoad.setOnClickListener(this);

        setRefreshLayout();
        return view;
    }

    @Override
    public void initData() {
        pageNo = 0;
        isPullRefresh = false;
        mDynamicList.clear();
        gotoGetDynamicData();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            /*case R.id.iv_dynamic_add:
                boolean isLoginState = SPUtils.getboolean(DynamicActivity.this, GlobalConstants.isLoginState, false);
                Intent intent = new Intent();
                if (isLoginState) {
                    intent.setClass(DynamicActivity.this, DynamicAddActivity.class);
                } else {
                    intent.setClass(DynamicActivity.this, LoginActivity.class);
                }
                //startActivity(intent);
                //用获取返回值的方式开启Activity
                startActivityForResult(intent, 20);
                break;*/

            case R.id.btn_again_load:
                pageNo = 0;
                isPullRefresh = false;
                mDynamicList.clear();
                gotoGetDynamicData();
                break;

            default:
                break;
        }
    }

    private void setRefreshLayout() {
        //设置 Header 为 Material样式
        refreshLayout.setRefreshHeader(new MeiYaYaHeader(mainActivity));//new MaterialHeader(this).setShowBezierWave(true)
        //设置 Footer 为 球脉冲
        refreshLayout.setRefreshFooter(new ClassicsFooter(mainActivity).setSpinnerStyle(SpinnerStyle.Scale)); //new BallPulseFooter(this).setSpinnerStyle(SpinnerStyle.Scale)

        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                //refreshlayout.finishRefresh(2000);
                //下拉刷新
                pageNo = 0;
                isPullRefresh = true;
                mDynamicList.clear();
                gotoGetDynamicData();
            }
        });
        refreshLayout.setOnLoadmoreListener(new OnLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                //refreshlayout.finishLoadmore(2000);
                pageNo++;
                gotoGetDynamicData();
            }
        });
    }

    private void gotoGetDynamicData() {
        if (pageNo == 0 && !isPullRefresh) {
            llLoading.setVisibility(View.VISIBLE);
        }
        llNetworkFailed.setVisibility(View.GONE);
        String userId = SPUtils.getString(mainActivity, GlobalConstants.userID, "");
        OkHttpUtils.get().url(GlobalConstants.URL_DYNAMIC_GET)
                .addParams("id", userId)
                .addParams("page", pageNo + "")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.i(TAG, "onError: -----从服务器获取动态数据失败-----");
                        e.printStackTrace();
                        Log.i(TAG, "oError: -------------------------------");
                        Toast.makeText(mainActivity, "网络出现异常", Toast.LENGTH_SHORT).show();
                        refreshLayout.finishRefresh();
                        refreshLayout.finishLoadmore();
                        llLoading.setVisibility(View.GONE);
                        llNetworkFailed.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.i(TAG, "onResponse: -----从服务器获取动态数据成功-----");
                        refreshLayout.finishRefresh();
                        refreshLayout.finishLoadmore();
                        llLoading.setVisibility(View.GONE);
                        llNetworkFailed.setVisibility(View.GONE);

                        try {
                            parserDynamicJson(response);
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.i(TAG, "onResponse: -----动态数据解析异常-----");
                        }
                    }
                });
    }

    private void parserDynamicJson(String response) {

        Gson gson = new Gson();
        DynamicData dynamicData = gson.fromJson(response, DynamicData.class);

        if (dynamicData.data != null) {
            mDynamicList.addAll(dynamicData.data);

            mLikeList.clear();
            mFocusList.clear();
            for (int i = 0; i < mDynamicList.size(); i++) {
                mLikeList.add(i, false);
                mFocusList.add(i, false);
            }

            //RecyclerView加载数据
            if (mDynamicList.size() <= 15 && pageNo == 0) { //后台每页显示15条数据,这是第一页数据
                rvDynamic.setLayoutManager(new LinearLayoutManager(mainActivity));
                mDyAdapter = new DynamicAdapter();
                rvDynamic.setAdapter(mDyAdapter);
            } else {
                mDyAdapter.notifyDataSetChanged();
            }
        } else {
            if (pageNo == 0) {
                Toast.makeText(mainActivity, "还没有动态数据", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(mainActivity, "没有更多动态了", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //添加动态
    public void addDynamic() {
        boolean isLoginState = SPUtils.getboolean(mainActivity, GlobalConstants.isLoginState, false);
        Intent intent = new Intent();
        if (isLoginState) {
            intent.setClass(mainActivity, DynamicAddActivity.class);
        } else {
            intent.setClass(mainActivity, LoginActivity.class);
        }
        //用获取返回值的方式开启Activity
        startActivityForResult(intent, 20);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 20 && resultCode == 21) {   //从发布页面返回回来
            //从发布页面返回回来,刷新一次数据
            pageNo = 0;
            isPullRefresh = false;
            mDynamicList.clear();
            gotoGetDynamicData();

        } else if (requestCode == 30 && resultCode == 31) { //从动态评论页面删除动态时返回回来

            int deletePosition = data.getIntExtra("DELETE_POSITION", -1);
            mDynamicList.remove(deletePosition);
            mDyAdapter.notifyDataSetChanged();
        }
    }

    private class DynamicAdapter extends RecyclerView.Adapter<DynamicHolder> {

        public DynamicAdapter() {
        }

        @Override
        public DynamicHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = View.inflate(mainActivity, R.layout.item_dynamic, null);
            DynamicHolder dynamicHolder = new DynamicHolder(view);
            return dynamicHolder;
        }

        @Override
        public void onBindViewHolder(final DynamicHolder holder, int position) {
            System.out.println("---DynamicAdapter---onBindViewHolder---" + position + "---");

            Glide.with(FindDynamicFragment.this).load(mDynamicList.get(position).images1).into(holder.civDyUserIcon);
            //System.out.println("dyData.get(position).images1 = " + mDynamicList.get(position).images1);
            holder.tvDyUserName.setText(mDynamicList.get(position).cat_name);
            holder.tvDySendTime.setText(mDynamicList.get(position).datetime);

            if (TextUtils.isEmpty(mDynamicList.get(position).content)) {
                holder.tvDyContent.setVisibility(View.GONE);
            } else {
                holder.tvDyContent.setVisibility(View.VISIBLE);
                holder.tvDyContent.setText(mDynamicList.get(position).content);
            }

            holder.tvDyLike.setText(mDynamicList.get(position).likes);
            holder.tvDyComment.setText(mDynamicList.get(position).review);
            holder.tvDyShare.setText(mDynamicList.get(position).share);

            if (mDynamicList.get(position).attention == 0) {  //未关注
                holder.ivDyFocus.setVisibility(View.VISIBLE);
                holder.ivDyDelete.setVisibility(View.GONE);
                holder.ivDyFocus.setImageResource(R.drawable.focus);
                mFocusList.set(position, false);

            } else if (mDynamicList.get(position).attention == 1) { //已关注
                holder.ivDyFocus.setVisibility(View.VISIBLE);
                holder.ivDyDelete.setVisibility(View.GONE);
                holder.ivDyFocus.setImageResource(R.drawable.focused);
                mFocusList.set(position, true);

            } else if (mDynamicList.get(position).attention == -1) {  //自己的动态不用显示关注按钮
                holder.ivDyFocus.setVisibility(View.GONE);
                holder.ivDyDelete.setVisibility(View.VISIBLE);
                mFocusList.set(position, false);

            } else {
                holder.ivDyFocus.setVisibility(View.VISIBLE);
                holder.ivDyDelete.setVisibility(View.GONE);
                holder.ivDyFocus.setImageResource(R.drawable.focus);
                mFocusList.set(position, false);
            }

            /*
            1 表示客户端对这条动态已赞
            0 表示未赞
            */
            if (mDynamicList.get(position).zan == 0) {  //未赞
                Drawable fabulous = getResources().getDrawable(R.drawable.dynamic_praise_normal);
                fabulous.setBounds(0, 0, fabulous.getMinimumWidth(), fabulous.getMinimumWidth());
                holder.tvDyLike.setCompoundDrawables(fabulous, null, null, null);
                mLikeList.set(position, false);

            } else if (mDynamicList.get(position).zan == 1) {  //已赞
                Drawable fabulousSelected = getResources().getDrawable(R.drawable.dynamic_praise_selected);
                fabulousSelected.setBounds(0, 0, fabulousSelected.getMinimumWidth(), fabulousSelected.getMinimumWidth());
                holder.tvDyLike.setCompoundDrawables(fabulousSelected, null, null, null);
                mLikeList.set(position, true);

            } else {
                Drawable fabulous = getResources().getDrawable(R.drawable.dynamic_praise_normal);
                fabulous.setBounds(0, 0, fabulous.getMinimumWidth(), fabulous.getMinimumWidth());
                holder.tvDyLike.setCompoundDrawables(fabulous, null, null, null);
                mLikeList.set(position, false);
            }

            //图片九宫格展示
            ArrayList<ImageInfo> imageList = new ArrayList<>();
            ArrayList<String> imageUrlList = mDynamicList.get(position).picture;
            if (imageUrlList != null) {
                for (int i = 0; i < imageUrlList.size(); i++) {
                    ImageInfo imageInfo = new ImageInfo();
                    imageInfo.setThumbnailUrl(imageUrlList.get(i));
                    imageInfo.setBigImageUrl(imageUrlList.get(i));
                    imageList.add(imageInfo);
                }
            }
            holder.nineGridView.setAdapter(new NineGridViewClickAdapter(mainActivity, imageList));
            //绑定点击事件
            holder.bind(position);
        }

        @Override
        public int getItemCount() {
            return mDynamicList.size();
        }
    }

    //由于内部类有点击事件.此处没有加static修饰符
    class DynamicHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private int             clickPosition;
        public  View            itemView;
        public  CircleImageView civDyUserIcon;
        public  TextView        tvDyUserName;
        public  TextView        tvDySendTime;
        public  ImageView       ivDyFocus;
        public  ImageView       ivDyDelete;
        public  TextView        tvDyContent;
        public  NineGridView    nineGridView;
        public  TextView        tvDyLike;
        public  TextView        tvDyComment;
        public  TextView        tvDyShare;


        public DynamicHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            civDyUserIcon = (CircleImageView) itemView.findViewById(R.id.civ_dy_user_icon);
            tvDyUserName = (TextView) itemView.findViewById(R.id.tv_dy_user_name);
            tvDySendTime = (TextView) itemView.findViewById(R.id.tv_dy_send_time);
            ivDyFocus = (ImageView) itemView.findViewById(R.id.iv_dy_focus);
            ivDyDelete = (ImageView) itemView.findViewById(R.id.iv_dy_delete);
            tvDyContent = (TextView) itemView.findViewById(R.id.tv_dy_content);
            nineGridView = (NineGridView) itemView.findViewById(R.id.nineGridView);
            tvDyLike = (TextView) itemView.findViewById(R.id.tv_dy_like);
            tvDyComment = (TextView) itemView.findViewById(R.id.tv_dy_comment);
            tvDyShare = (TextView) itemView.findViewById(R.id.tv_dy_share);
        }

        public void bind(int position) {
            clickPosition = position;

            ivDyFocus.setOnClickListener(this);
            itemView.setOnClickListener(this);
            tvDyComment.setOnClickListener(this);
            tvDyLike.setOnClickListener(this);
            tvDyShare.setOnClickListener(this);
            civDyUserIcon.setOnClickListener(this);
            ivDyDelete.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            //如果未登录,就直接跳到登录页面,并且结束方法;
            boolean isLoginState = SPUtils.getboolean(mainActivity, GlobalConstants.isLoginState, false);
            if (!isLoginState) {
                Toast.makeText(mainActivity, "请先登录", Toast.LENGTH_SHORT).show();
                Intent intentLogin = new Intent(mainActivity, LoginActivity.class);
                startActivity(intentLogin);
                return;
            }

            switch (v.getId()) {
                case R.id.iv_dy_focus:   //点击关注
                    Log.i(TAG, "onClick: clickPosition = " + clickPosition);
                    ivDyFocus.setEnabled(false);
                    /*
                    1：表示已关注
                    0：表示未关注
                    -1：表示为自己的动态不用显示
                    */
                    if (mFocusList.get(clickPosition)) {
                        //取消关注
                        mFocusList.set(clickPosition, false);
                        // TODO: 2018/1/11 0011 java.lang.IndexOutOfBoundsException: Index: 2, Size: 0
                        mDynamicList.get(clickPosition).attention = 0;
                        ivDyFocus.setImageResource(R.drawable.focus);
                        gotoCancelFollow(clickPosition, this);

                    } else {
                        //添加关注
                        mFocusList.set(clickPosition, true);
                        mDynamicList.get(clickPosition).attention = 1;
                        ivDyFocus.setImageResource(R.drawable.focused);
                        gotoAddFollow(clickPosition, this);
                    }
                    break;

                case R.id.rl_item_dynamic_root:  //点击条目
                    if (mDynamicList != null) {
                        Intent intent = new Intent(mainActivity, DynamicCommentActivity.class);
                        intent.putExtra("CLICK_POSITION", clickPosition);
                        intent.putExtra("DYNAMIC_INFO", mDynamicList.get(clickPosition));
                        startActivityForResult(intent, 30);
                    }
                    break;

                case R.id.tv_dy_comment:  //点击评论
                    Intent intent1 = new Intent(mainActivity, DynamicCommentActivity.class);
                    intent1.putExtra("CLICK_POSITION", clickPosition);
                    intent1.putExtra("DYNAMIC_INFO", mDynamicList.get(clickPosition));
                    intent1.putExtra("DYNAMIC", GlobalConstants.INTENT_DYNAMIC_ADD_COMMENT);
                    startActivityForResult(intent1, 30);
                    break;

                case R.id.tv_dy_like:  //点赞
                    Log.i(TAG, "onClick: clickPosition = " + clickPosition);
                    tvDyLike.setEnabled(false);

                    String like = tvDyLike.getText().toString().trim();
                    int likeNum = Integer.parseInt(like);

                    if (mLikeList.get(clickPosition)) {
                        /* //我们目前不支持取消赞
                        mLikeList.set(clickPosition, false);
                        Drawable fabulous = getResources().getDrawable(R.drawable.dynamic_praise_normal);
                        fabulous.setBounds(0, 0, fabulous.getMinimumWidth(), fabulous.getMinimumWidth());
                        tvDyLike.setCompoundDrawables(fabulous, null, null, null);
                        likeNum = likeNum - 1;
                        tvDyLike.setText(likeNum + "");*/
                        Toast.makeText(mainActivity, "您已点赞", Toast.LENGTH_SHORT).show();
                    } else {
                        mLikeList.set(clickPosition, true);
                        Drawable fabulousSelected = getResources().getDrawable(R.drawable.dynamic_praise_selected);
                        fabulousSelected.setBounds(0, 0, fabulousSelected.getMinimumWidth(), fabulousSelected.getMinimumWidth());
                        tvDyLike.setCompoundDrawables(fabulousSelected, null, null, null);
                        likeNum = likeNum + 1;
                        tvDyLike.setText(likeNum + "");

                        gotoAddLikesNum(clickPosition, likeNum, this);
                    }

                    break;

                case R.id.tv_dy_share:  //点击分享
                    ShareDynamicDialog shareDynamicDialog = new ShareDynamicDialog(mainActivity, mDynamicList.get(clickPosition).id, mDynamicList.get(clickPosition).content);
                    shareDynamicDialog.show();
                    break;

                case R.id.civ_dy_user_icon:  //点击用户头像
                    Intent intent2 = new Intent(mainActivity, UserPagerActivity.class);
                    intent2.putExtra("LOOK_USER_ID", mDynamicList.get(clickPosition).userid);
                    startActivity(intent2);
                    break;
                case R.id.iv_dy_delete:  //点击删除
                    AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity);
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
                            gotoDeleteMyDynamic(clickPosition);
                        }
                    });
                    builder.show();
                    break;
                default:
                    break;
            }
        }
    }

    //删除自己的动态
    private void gotoDeleteMyDynamic(final int clickPosition) {
        String userId = SPUtils.getString(mainActivity, GlobalConstants.userID, "");

        OkHttpUtils.get().url(GlobalConstants.URL_DYNAMIC_DELETE)
                .addParams("id", mDynamicList.get(clickPosition).id)
                .addParams("userid", userId)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.i(TAG, "onError: -----网络异常,动态删除失败-----");
                        Toast.makeText(mainActivity, "网络异常,动态删除失败", Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(mainActivity, message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.i(TAG, "onResponse: -----动态删除成功,但解析服务器信息出现异常-----");
                        }
                        mDynamicList.remove(clickPosition);
                        mDyAdapter.notifyDataSetChanged();
                    }
                });
    }

    //取消关注
    private void gotoCancelFollow(final int clickPosition, final DynamicHolder holder) {

        String userId = SPUtils.getString(mainActivity, GlobalConstants.userID, "");
        String uid = mDynamicList.get(clickPosition).userid;

        OkHttpUtils.get().url(GlobalConstants.URL_CANCEL_FOLLOW)
                .addParams("userid", userId)
                .addParams("uid", uid)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.i(TAG, "onError: -----网络异常,取消关注失败-----");
                        Toast.makeText(mainActivity, "网络异常,取消关注失败,请重试", Toast.LENGTH_SHORT).show();

                        //取消关注失败将所有改变的状态回归
                        mFocusList.set(clickPosition, true);
                        mDynamicList.get(clickPosition).attention = 1;
                        holder.ivDyFocus.setImageResource(R.drawable.focused);
                        holder.ivDyFocus.setEnabled(true);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.i(TAG, "onResponse: -----取消关注成功-----response = " + response + "-----");
                        //取消关注成功,所有状态之前已经改变了,这里可以不再做处理,这里我只是强化一下取消关注的状态
                        mFocusList.set(clickPosition, false);
                        mDynamicList.get(clickPosition).attention = 0;
                        holder.ivDyFocus.setImageResource(R.drawable.focus);
                        holder.ivDyFocus.setEnabled(true);

                        try {
                            Gson gson = new Gson();
                            DynamicFollowData dynamicFollowData = gson.fromJson(response, DynamicFollowData.class);
                            if (dynamicFollowData != null) {
                                //弹出来自服务器的信息
                                Toast.makeText(mainActivity, dynamicFollowData.message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        mDyAdapter.notifyDataSetChanged();
                    }
                });
    }

    //添加关注
    private void gotoAddFollow(final int clickPosition, final DynamicHolder holder) {

        String userId = SPUtils.getString(mainActivity, GlobalConstants.userID, "");
        String uid = mDynamicList.get(clickPosition).userid;

        OkHttpUtils.get().url(GlobalConstants.URL_ADD_FOLLOW)
                .addParams("userid", userId)
                .addParams("uid", uid)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.i(TAG, "onError: -----网络异常,关注失败-----");
                        Toast.makeText(mainActivity, "网络异常,关注失败,请重试", Toast.LENGTH_SHORT).show();

                        //关注失败将所有改变的状态回归
                        mFocusList.set(clickPosition, false);
                        mDynamicList.get(clickPosition).attention = 0;
                        holder.ivDyFocus.setImageResource(R.drawable.focus);
                        holder.ivDyFocus.setEnabled(true);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.i(TAG, "onResponse: -----关注成功-----response = " + response + "-----");

                        //关注成功,所有状态之前已经改变了,这里可以不再做处理,这里我只是强化一下关注的状态
                        mFocusList.set(clickPosition, true);
                        mDynamicList.get(clickPosition).attention = 1;
                        holder.ivDyFocus.setImageResource(R.drawable.focused);
                        holder.ivDyFocus.setEnabled(true);
                        try {
                            Gson gson = new Gson();
                            DynamicFollowData dynamicFollowData = gson.fromJson(response, DynamicFollowData.class);
                            if (dynamicFollowData != null) {
                                //弹出来自服务器的信息
                                Toast.makeText(mainActivity, dynamicFollowData.message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.i(TAG, "onResponse: 解析关注数据出现异常!");
                        }
                        mDyAdapter.notifyDataSetChanged();
                    }
                });
    }

    //点赞
    private void gotoAddLikesNum(final int clickPosition, final int likeNum, final DynamicHolder holder) {

        String userId = SPUtils.getString(mainActivity, GlobalConstants.userID, "");
        String did = mDynamicList.get(clickPosition).id;
        OkHttpUtils.get().url(GlobalConstants.URL_DYNAMIC_LIKES)
                .addParams("cid", userId)
                .addParams("did", did)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        LogUtils.show(TAG, "onError: -----网络异常,点赞失败!-----");
                        Toast.makeText(mainActivity, "网络异常,点赞失败,请重试", Toast.LENGTH_SHORT).show();
                        //点赞失败将所有改变的状态回归
                        mLikeList.set(clickPosition, false);
                        Drawable fabulous = getResources().getDrawable(R.drawable.dynamic_praise_normal);
                        fabulous.setBounds(0, 0, fabulous.getMinimumWidth(), fabulous.getMinimumWidth());
                        holder.tvDyLike.setCompoundDrawables(fabulous, null, null, null);
                        int likeNumOld = likeNum - 1;
                        holder.tvDyLike.setText(likeNumOld + "");
                        holder.tvDyLike.setEnabled(true);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        LogUtils.show(TAG, "onResponse: -----点赞成功-----response = " + response + "-----");

                        //将本地点赞数据更新，防止上拉加载mDyAdapter.notifyDataSetChanged()后点赞数据又显示没点赞
                        mDynamicList.get(clickPosition).zan = 1;
                        mDynamicList.get(clickPosition).likes = String.valueOf(likeNum);

                        //点赞成功,所有状态之前已经改变了,这里可以不再做处理,这里我只是强化一下点赞的状态
                        mLikeList.set(clickPosition, true);
                        Drawable fabulousSelected = getResources().getDrawable(R.drawable.dynamic_praise_selected);
                        fabulousSelected.setBounds(0, 0, fabulousSelected.getMinimumWidth(), fabulousSelected.getMinimumWidth());
                        holder.tvDyLike.setCompoundDrawables(fabulousSelected, null, null, null);
                        holder.tvDyLike.setText(likeNum + "");
                        holder.tvDyLike.setEnabled(true);
                    }
                });
    }


}
