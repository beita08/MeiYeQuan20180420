package com.xasfemr.meiyaya.adapter;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.xasfemr.meiyaya.R;
import com.xasfemr.meiyaya.activity.MeetingDetailsActivity;
import com.xasfemr.meiyaya.activity.UserPagerActivity;
import com.xasfemr.meiyaya.bean.UserPostInfoData;
import com.xasfemr.meiyaya.global.GlobalConstants;
import com.xasfemr.meiyaya.module.home.activity.InstrumentTransferDetailActivity;
import com.xasfemr.meiyaya.module.home.activity.RecruitmentDetailActivity;
import com.xasfemr.meiyaya.module.home.activity.RequestJobDetailActivity;
import com.xasfemr.meiyaya.utils.LocationUtils;
import com.xasfemr.meiyaya.utils.LogUtils;
import com.xasfemr.meiyaya.view.progress.SFProgressDialog;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.Call;

/**
 * Description:
 * Company    : shifeier
 * Author     : liuxb
 * Date       : 2018/3/9 0009 17:55
 */

public class PostInfoAdapter extends RecyclerView.Adapter<PostInfoAdapter.PostInfoHolder> {
    private static final String TAG = "PostInfoAdapter";

    private SFProgressDialog  sfProgressDialog;
    private UserPagerActivity userPagerActivity;
    private String            lookUserId;
    private String            mUserId;
    private double            longitude;
    private double            latitude;

    private ArrayList<UserPostInfoData.UserPostInfo> mPostInfoList;

    public PostInfoAdapter(UserPagerActivity userPagerActivity, String lookUserId, String mUserId, ArrayList<UserPostInfoData.UserPostInfo> postInfoList) {
        this.userPagerActivity = userPagerActivity;
        this.lookUserId = lookUserId;
        this.mUserId = mUserId;
        this.mPostInfoList = postInfoList;
    }

    @Override
    public PostInfoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(userPagerActivity, R.layout.item_user_post_info, null);
        PostInfoHolder postInfoHolder = new PostInfoHolder(view);
        sfProgressDialog = new SFProgressDialog(userPagerActivity);
        LocationUtils.initLocation(userPagerActivity);
        longitude = LocationUtils.longitude;
        latitude = LocationUtils.latitude;
        return postInfoHolder;
    }

    @Override
    public void onBindViewHolder(PostInfoHolder holder, int position) {
        //图片
        Glide.with(userPagerActivity).load(mPostInfoList.get(position).thumb).into(holder.ivInfoPic);

        //标题
        holder.tvInfoTitle.setText(mPostInfoList.get(position).title);

        //是否认证 is_approve (0:未认证, 1:已认证)
        holder.ivAuthMark.setVisibility(mPostInfoList.get(position).is_approve == 1 ? View.VISIBLE : View.GONE);

        /*//距离
        if (longitude == 0.0 || latitude == 0.0 || mPostInfoList.get(position).longitude == 0.0 || mPostInfoList.get(position).latitude == 0.0) {
            holder.tvInfoDistance.setText("未获取到位置");
        } else {
            MapDistanceUtils mapDistanceUtils = MapDistanceUtils.getInstance();
            String shortDistance = mapDistanceUtils.getShortDistance(mPostInfoList.get(position).longitude, mPostInfoList.get(position).latitude, longitude, latitude);
            holder.tvInfoDistance.setText(shortDistance);
        }*/
        //距离(显示商圈),如果有商圈信息则显示,如果没有则隐藏该控件
        if (TextUtils.isEmpty(mPostInfoList.get(position).business)) {
            holder.tvInfoDistance.setVisibility(View.GONE);
            holder.viewDivider.setVisibility(View.GONE);
        } else {
            holder.tvInfoDistance.setVisibility(View.VISIBLE);
            holder.viewDivider.setVisibility(View.VISIBLE);
            holder.tvInfoDistance.setText(mPostInfoList.get(position).business);
        }

        //开关
        if (TextUtils.equals(lookUserId, mUserId)) {
            holder.ivInfoSwitch.setVisibility(View.VISIBLE);
            holder.ivInfoSwitch.setImageResource(mPostInfoList.get(position).is_start == 1 ? R.drawable.switch_on_small : R.drawable.switch_off_small);
        } else {
            holder.ivInfoSwitch.setVisibility(View.GONE);
        }

        //其他信息
        switch (mPostInfoList.get(position).catid) {
            case 2:     //catid=2 求购仪器(暂不使用)
                break;

            case 3:     //catid=3 转让仪器
                holder.tvInfoDesc.setText(mPostInfoList.get(position).content);
                holder.tvInfoAddress.setText(mPostInfoList.get(position).company);
                if (TextUtils.equals(mPostInfoList.get(position).price, "-1")) {
                    holder.tvInfoPrice.setText("价格面议");
                } else {
                    holder.tvInfoPrice.setText(mPostInfoList.get(position).price);
                }
                break;

            case 5:     //catid=5 招聘
                holder.tvInfoDesc.setText(mPostInfoList.get(position).content);
                holder.tvInfoAddress.setText(mPostInfoList.get(position).companyName);
                holder.tvInfoPrice.setText(mPostInfoList.get(position).salary);
                break;

            case 6:     //catid=6 求职
                holder.tvInfoDesc.setText(mPostInfoList.get(position).linkman + " " + mPostInfoList.get(position).sex + " " + mPostInfoList.get(position).age);
                holder.tvInfoAddress.setText(mPostInfoList.get(position).yearWork + "工作经验");
                holder.tvInfoPrice.setText(mPostInfoList.get(position).expect_salary);
                holder.tvInfoDistance.setVisibility(View.GONE);
                holder.viewDivider.setVisibility(View.GONE);
                break;

            case 8:     //catid=8 店铺转让(暂不使用)
                break;
            case 9:     //catid=9 店铺求租(暂不使用)
                break;
            case 14:    //catid=14 会议
                holder.tvInfoDesc.setText("会议时间：" + mPostInfoList.get(position).meetingTime);
                holder.tvInfoAddress.setText("报名截止：" + mPostInfoList.get(position).enddate);
                //holder.tvInfoPrice.setText(mPostInfoList.get(position).salary);
                break;
            default:
                break;
        }

        //开关操作
        holder.ivInfoSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogForSwitch(mPostInfoList.get(position).is_start == 1, mPostInfoList.get(position).id);
            }

            private void showDialogForSwitch(boolean isOpen, String postInfoId) {  //open是指目前的开关状态
                AlertDialog.Builder builder = new AlertDialog.Builder(userPagerActivity);
                if (isOpen) {
                    builder.setTitle("是否关闭开关？");
                    builder.setMessage("关闭开关后，其他用户将看不到你发布的这一条信息");
                    builder.setNegativeButton("取消", null);//啥也不干,系统对话框自动消失
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //关闭开关
                            gotoChangeSwitch(false, postInfoId, position);
                        }
                    });

                } else {
                    builder.setTitle("是否打开开关？");
                    builder.setMessage("打开开关后，其他用户将可以看到你发布的这一条信息");
                    builder.setNegativeButton("取消", null);//啥也不干,系统对话框自动消失
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //打开开关
                            gotoChangeSwitch(true, postInfoId, position);
                        }
                    });
                }
                builder.show();
            }
        });
        //点击跳转操作
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                switch (mPostInfoList.get(position).catid) {
                    case 2:     //catid=2 求购仪器(暂不使用)
                        break;
                    case 3:     //catid=3 转让仪器
                        userPagerActivity.startActivity(new Intent(userPagerActivity, InstrumentTransferDetailActivity.class).putExtra("info_id", mPostInfoList.get(position).id));
                        break;

                    case 5:     //catid=5 招聘
                        userPagerActivity.startActivity(new Intent(userPagerActivity, RecruitmentDetailActivity.class).putExtra("info_id", mPostInfoList.get(position).id));
                        LogUtils.show(TAG, "你点击了第" + position + "个条目, mPostInfoList.get(position).id = " + mPostInfoList.get(position).id);
                        break;

                    case 6:     //catid=6 求职
                        userPagerActivity.startActivity(new Intent(userPagerActivity, RequestJobDetailActivity.class).putExtra("info_id", mPostInfoList.get(position).id));
                        break;

                    case 8:     //catid=8 店铺转让(暂不使用)
                        break;
                    case 9:     //catid=9 店铺求租(暂不使用)
                        break;
                    case 14:    //catid=14 会议
                        Intent intent = new Intent(userPagerActivity, MeetingDetailsActivity.class);
                        intent.putExtra("info_id", mPostInfoList.get(position).id);
                        intent.putExtra("user_id", mPostInfoList.get(position).userid);
                        userPagerActivity.startActivity(intent);
                        break;
                    default:
                        break;
                }

            }
        });
        //长按删除操作
        if (TextUtils.equals(lookUserId, mUserId)) {  //只有自己的页面才能长按删除视频
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    showDeleteDialog(position);
                    return true;
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mPostInfoList.size();
    }

    static class PostInfoHolder extends RecyclerView.ViewHolder {

        public ImageView ivInfoPic;
        public TextView  tvInfoTitle;
        public ImageView ivAuthMark;
        public TextView  tvInfoDesc;
        public TextView  tvInfoDistance;
        public View      viewDivider;
        public TextView  tvInfoAddress;
        public ImageView ivInfoSwitch;
        public TextView  tvInfoPrice;

        public PostInfoHolder(View itemView) {
            super(itemView);
            ivInfoPic = (ImageView) itemView.findViewById(R.id.iv_post_info_pic);
            tvInfoTitle = (TextView) itemView.findViewById(R.id.tv_post_info_title);
            ivAuthMark = (ImageView) itemView.findViewById(R.id.iv_company_auth_mark);
            tvInfoDesc = (TextView) itemView.findViewById(R.id.tv_post_info_desc);
            tvInfoDistance = (TextView) itemView.findViewById(R.id.tv_post_info_distance);
            viewDivider = itemView.findViewById(R.id.view_divider);
            tvInfoAddress = (TextView) itemView.findViewById(R.id.tv_post_info_address);
            ivInfoSwitch = (ImageView) itemView.findViewById(R.id.iv_post_info_switch);
            tvInfoPrice = (TextView) itemView.findViewById(R.id.tv_post_info_price);
        }
    }

    private void gotoChangeSwitch(boolean open, String postInfoId, int position) {
        sfProgressDialog.show();
        String changeSwitchUrl = GlobalConstants.URL_CHANGE_INFO_SWITCH;
        if (open) {
            changeSwitchUrl = changeSwitchUrl + "&a=active";
        } else {
            changeSwitchUrl = changeSwitchUrl + "&a=close";
        }

        OkHttpUtils.get().url(changeSwitchUrl)
                .addParams("userid", mUserId)
                .addParams("id", postInfoId)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        sfProgressDialog.dismiss();
                        LogUtils.show(TAG, "onError: ---网络异常,改变开关失败---");
                        Toast.makeText(userPagerActivity, "网络异常,请重试", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        sfProgressDialog.dismiss();
                        LogUtils.show(TAG, "onResponse: 开关状态:response = ---" + response + "---");
                        try {
                            JSONObject jo = new JSONObject(response);
                            String message = jo.getString("message");
                            Toast.makeText(userPagerActivity, message, Toast.LENGTH_SHORT).show();
                            if (open) {
                                mPostInfoList.get(position).is_start = 1;
                            } else {
                                mPostInfoList.get(position).is_start = 0;
                            }
                            PostInfoAdapter.this.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(userPagerActivity, "数据异常,请重试", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void showDeleteDialog(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(userPagerActivity);
        builder.setTitle("提示：");
        switch (mPostInfoList.get(position).catid) {
            case 2:     //catid=2 求购仪器(暂不使用)
                break;
            case 3:     //catid=3 转让仪器
                builder.setMessage("确定要删除“" + mPostInfoList.get(position).classify + "”这条仪器转让信息吗?");
                break;
            case 5:     //catid=5 招聘
                builder.setMessage("确定要删除“" + mPostInfoList.get(position).jobName + "”这条招聘信息吗?");
                break;
            case 6:     //catid=6 求职
                builder.setMessage("确定要删除“" + mPostInfoList.get(position).position + "”这条求职信息吗?");
                break;
            case 8:     //catid=8 店铺转让(暂不使用)
                break;
            case 9:     //catid=9 店铺求租(暂不使用)
                break;
            case 14:     //catid=14 会议
                builder.setMessage("确定要删除“" + mPostInfoList.get(position).title + "”这条会议信息吗?");
                break;
            default:
                break;
        }
        builder.setPositiveButton("取消", null);
        builder.setNegativeButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                gotoDeleteUserPostInfo(position);
            }
        });
        builder.show();
    }

    private void gotoDeleteUserPostInfo(int position) {
        sfProgressDialog.show();
        String deletePostInfoUrl = GlobalConstants.URL_DELETE_POST_INFO;
        deletePostInfoUrl = deletePostInfoUrl + "&id=[\"" + mPostInfoList.get(position).id + "\"]";
        LogUtils.show(TAG, "deletePostInfoUrl = " + deletePostInfoUrl);

        OkHttpUtils.get().url(deletePostInfoUrl).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                sfProgressDialog.dismiss();
                LogUtils.show(TAG, "onError: ---网络异常,删除发布信息失败---");
                Toast.makeText(userPagerActivity, "网络异常,删除发布信息失败,请重试", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(String response, int id) {
                sfProgressDialog.dismiss();
                LogUtils.show(TAG, "onResponse: ---删除发布信息访问网络成功---response = " + response + " ---");
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String message = jsonObject.getString("message");
                    if (TextUtils.equals(message, "删除成功")) {
                        Toast.makeText(userPagerActivity, message, Toast.LENGTH_SHORT).show();
                        mPostInfoList.remove(position);
                        PostInfoAdapter.this.notifyDataSetChanged();
                    } else {
                        Toast.makeText(userPagerActivity, message, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

}
