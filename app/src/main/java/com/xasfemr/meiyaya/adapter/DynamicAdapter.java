package com.xasfemr.meiyaya.adapter;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.lzy.ninegrid.ImageInfo;
import com.lzy.ninegrid.NineGridView;
import com.lzy.ninegrid.preview.NineGridViewClickAdapter;
import com.xasfemr.meiyaya.R;
import com.xasfemr.meiyaya.activity.DynamicCommentActivity;
import com.xasfemr.meiyaya.activity.LoginActivity;
import com.xasfemr.meiyaya.activity.UserPagerActivity;
import com.xasfemr.meiyaya.bean.DynamicData;
import com.xasfemr.meiyaya.global.GlobalConstants;
import com.xasfemr.meiyaya.utils.LogUtils;
import com.xasfemr.meiyaya.utils.SPUtils;
import com.xasfemr.meiyaya.view.ShareDynamicDialog;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;

/**
 * Description:
 * Company    : shifeier
 * Author     : liuxb
 * Date       : 2018/3/9 0009 15:48
 */
public class DynamicAdapter extends RecyclerView.Adapter<DynamicAdapter.DynamicHolder> {
    private static final String TAG = "DynamicAdapter";

    private UserPagerActivity userPagerActivity;
    private String            lookUserId;
    private String            mUserId;

    private ArrayList<DynamicData.DynamicItemInfo> mDynamicList;
    private ArrayList<Boolean> mLikeList = new ArrayList<>();

    public DynamicAdapter(UserPagerActivity userPagerActivity, String lookUserId, String mUserId, ArrayList<DynamicData.DynamicItemInfo> dynamicList) {
        this.userPagerActivity = userPagerActivity;
        this.lookUserId = lookUserId;
        this.mUserId = mUserId;
        this.mDynamicList = dynamicList;
        mLikeList.clear();
        for (int i = 0; i < mDynamicList.size(); i++) {
            mLikeList.add(i, false);
        }
    }

    @Override
    public DynamicHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(userPagerActivity, R.layout.item_dynamic, null);
        DynamicHolder dynamicHolder = new DynamicHolder(view, userPagerActivity);

        userPagerActivity.setOnBackListener(new UserPagerActivity.OnBackListener() {
            @Override
            public void onBack(int deletePosition) {
                mDynamicList.remove(deletePosition);
                DynamicAdapter.this.notifyDataSetChanged();
            }
        });

        return dynamicHolder;
    }

    @Override
    public void onBindViewHolder(final DynamicHolder holder, int position) {

        Glide.with(userPagerActivity).load(mDynamicList.get(position).images1).into(holder.civDyUserIcon);
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

        holder.ivDyFocus.setVisibility(View.GONE);
        if (TextUtils.equals(lookUserId, mUserId)) { //自己页面
            holder.ivDyDelete.setVisibility(View.VISIBLE);
        } else {
            holder.ivDyDelete.setVisibility(View.GONE);
        }

        //1 表示客户端对这条动态已赞
        //0 表示未赞

        if (mDynamicList.get(position).zan == 0) {  //未赞
            Drawable fabulous = userPagerActivity.getResources().getDrawable(R.drawable.fabulous);
            fabulous.setBounds(0, 0, fabulous.getMinimumWidth(), fabulous.getMinimumWidth());
            holder.tvDyLike.setCompoundDrawables(fabulous, null, null, null);
            mLikeList.set(position, false);

        } else if (mDynamicList.get(position).zan == 1) {  //已赞
            Drawable fabulousSelected = userPagerActivity.getResources().getDrawable(R.drawable.fabulous_selected);
            fabulousSelected.setBounds(0, 0, fabulousSelected.getMinimumWidth(), fabulousSelected.getMinimumWidth());
            holder.tvDyLike.setCompoundDrawables(fabulousSelected, null, null, null);
            mLikeList.set(position, true);

        } else {
            Drawable fabulous = userPagerActivity.getResources().getDrawable(R.drawable.fabulous);
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
        holder.nineGridView.setAdapter(new NineGridViewClickAdapter(userPagerActivity, imageList));
        //绑定点击事件
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return mDynamicList.size();
    }


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


        public DynamicHolder(View itemView, UserPagerActivity userPagerActivity) {
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
            boolean isLoginState = SPUtils.getboolean(userPagerActivity, GlobalConstants.isLoginState, false);
            if (!isLoginState) {
                Toast.makeText(userPagerActivity, "请先登录", Toast.LENGTH_SHORT).show();
                Intent intentLogin = new Intent(userPagerActivity, LoginActivity.class);
                userPagerActivity.startActivity(intentLogin);
                return;
            }

            switch (v.getId()) {
                case R.id.iv_dy_focus:   //点击关注
                    break;

                case R.id.rl_item_dynamic_root:  //点击条目
                    if (mDynamicList != null) {
                        Intent intent = new Intent(userPagerActivity, DynamicCommentActivity.class);
                        intent.putExtra("CLICK_POSITION", clickPosition);
                        intent.putExtra("DYNAMIC_INFO", mDynamicList.get(clickPosition));
                        if (TextUtils.equals(lookUserId, mUserId)) {
                            intent.putExtra("IS_MY_DYNAMIC", 1);//自己看自己的动态
                        } else {
                            intent.putExtra("IS_MY_DYNAMIC", 2);//看别人的动态
                        }
                        userPagerActivity.startActivityForResult(intent, 30);
                    }
                    break;

                case R.id.tv_dy_comment:  //点击评论
                    Intent intent1 = new Intent(userPagerActivity, DynamicCommentActivity.class);
                    intent1.putExtra("CLICK_POSITION", clickPosition);
                    intent1.putExtra("DYNAMIC_INFO", mDynamicList.get(clickPosition));
                    intent1.putExtra("DYNAMIC", GlobalConstants.INTENT_DYNAMIC_ADD_COMMENT);
                    if (TextUtils.equals(lookUserId, mUserId)) {
                        intent1.putExtra("IS_MY_DYNAMIC", 1);//自己看自己的动态
                    } else {
                        intent1.putExtra("IS_MY_DYNAMIC", 2);//看别人的动态
                    }
                    userPagerActivity.startActivityForResult(intent1, 30);
                    break;

                case R.id.tv_dy_like:  //点赞
                    Log.i(TAG, "onClick: clickPosition = " + clickPosition);
                    tvDyLike.setEnabled(false);

                    String like = tvDyLike.getText().toString().trim();
                    int likeNum = Integer.parseInt(like);

                    if (mLikeList.get(clickPosition)) {
                        /* //我们目前不支持取消赞
                        mLikeList.set(clickPosition, false);
                        Drawable fabulous = getResources().getDrawable(R.drawable.fabulous);
                        fabulous.setBounds(0, 0, fabulous.getMinimumWidth(), fabulous.getMinimumWidth());
                        tvDyLike.setCompoundDrawables(fabulous, null, null, null);
                        likeNum = likeNum - 1;
                        tvDyLike.setText(likeNum + "");*/
                        Toast.makeText(userPagerActivity, "您已点赞", Toast.LENGTH_SHORT).show();
                    } else {
                        mLikeList.set(clickPosition, true);
                        Drawable fabulousSelected = userPagerActivity.getResources().getDrawable(R.drawable.fabulous_selected);
                        fabulousSelected.setBounds(0, 0, fabulousSelected.getMinimumWidth(), fabulousSelected.getMinimumWidth());
                        tvDyLike.setCompoundDrawables(fabulousSelected, null, null, null);
                        likeNum = likeNum + 1;
                        tvDyLike.setText(likeNum + "");

                        gotoAddLikesNum(clickPosition, likeNum, this);
                    }

                    break;

                case R.id.tv_dy_share:  //点击分享
                    ShareDynamicDialog shareDynamicDialog = new ShareDynamicDialog(userPagerActivity, mDynamicList.get(clickPosition).id, mDynamicList.get(clickPosition).content);
                    shareDynamicDialog.show();
                    break;

                case R.id.civ_dy_user_icon:  //点击用户头像

                    break;
                case R.id.iv_dy_delete:  //点击删除
                    AlertDialog.Builder builder = new AlertDialog.Builder(userPagerActivity);
                    builder.setTitle("删除动态？");
                    builder.setMessage("确定删除这条动态吗？");
                    builder.setNegativeButton("取消", null);
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
        String userId = SPUtils.getString(userPagerActivity, GlobalConstants.userID, "");

        OkHttpUtils.get().url(GlobalConstants.URL_DYNAMIC_DELETE)
                .addParams("id", mDynamicList.get(clickPosition).id)
                .addParams("userid", userId)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.i(TAG, "onError: -----网络异常,动态删除失败-----");
                        Toast.makeText(userPagerActivity, "网络异常,动态删除失败", Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(userPagerActivity, message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.i(TAG, "onResponse: -----动态删除成功,但解析服务器信息出现异常-----");
                        }
                        mDynamicList.remove(clickPosition);
                        DynamicAdapter.this.notifyDataSetChanged();
                    }
                });
    }

    //点赞
    private void gotoAddLikesNum(final int clickPosition, final int likeNum, final DynamicHolder holder) {

        String userId = SPUtils.getString(userPagerActivity, GlobalConstants.userID, "");
        String did = mDynamicList.get(clickPosition).id;
        OkHttpUtils.get().url(GlobalConstants.URL_DYNAMIC_LIKES)
                .addParams("cid", userId)
                .addParams("did", did)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        LogUtils.show(TAG, "onError: -----网络异常,点赞失败!-----");
                        Toast.makeText(userPagerActivity, "网络异常,点赞失败,请重试", Toast.LENGTH_SHORT).show();
                        //点赞失败将所有改变的状态回归
                        mLikeList.set(clickPosition, false);
                        Drawable fabulous = userPagerActivity.getResources().getDrawable(R.drawable.fabulous);
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
                        Drawable fabulousSelected = userPagerActivity.getResources().getDrawable(R.drawable.fabulous_selected);
                        fabulousSelected.setBounds(0, 0, fabulousSelected.getMinimumWidth(), fabulousSelected.getMinimumWidth());
                        holder.tvDyLike.setCompoundDrawables(fabulousSelected, null, null, null);
                        holder.tvDyLike.setText(likeNum + "");
                        holder.tvDyLike.setEnabled(true);
                    }
                });
    }


}


