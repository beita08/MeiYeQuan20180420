package com.xasfemr.meiyaya.adapter;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.xasfemr.meiyaya.R;
import com.xasfemr.meiyaya.activity.UserPagerActivity;
import com.xasfemr.meiyaya.bean.UserVideoData;
import com.xasfemr.meiyaya.global.GlobalConstants;
import com.xasfemr.meiyaya.module.player.VideoPlayerIsUserActivity;
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
 * Date       : 2018/3/12 0012 9:35
 */

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoHolder> {
    private static final String TAG = "VideoAdapter";

    private SFProgressDialog  sfProgressDialog;
    private UserPagerActivity userPagerActivity;
    private String            lookUserId;
    private String            mUserId;

    private ArrayList<UserVideoData.VideoInfo> mVideoList;

    public VideoAdapter(UserPagerActivity userPagerActivity, String lookUserId, String mUserId, ArrayList<UserVideoData.VideoInfo> videoList) {
        this.userPagerActivity = userPagerActivity;
        this.lookUserId = lookUserId;
        this.mUserId = mUserId;
        this.mVideoList = videoList;
    }

    @Override
    public VideoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(userPagerActivity, R.layout.item_user_video, null);
        VideoHolder videoHolder = new VideoHolder(view);
        sfProgressDialog = new SFProgressDialog(userPagerActivity);
        return videoHolder;
    }

    @Override
    public void onBindViewHolder(VideoHolder holder, int position) {
        Glide.with(userPagerActivity).load(mVideoList.get(position).cover).into(holder.ivVideoCover);
        holder.tvVideoTitleTag.setText(mVideoList.get(position).coursename);
        holder.tvVideoNumber.setText(mVideoList.get(position).view);
        holder.tvVideoDesc.setText(mVideoList.get(position).title);
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.putExtra("videoPath", mVideoList.get(position).addr.rtmpPullUrl);
            intent.putExtra("media_type", "videoondemand");
            intent.putExtra("user_name", mVideoList.get(position).username);
            intent.putExtra("user_id", mVideoList.get(position).userid);
            intent.putExtra("coursename", mVideoList.get(position).coursename);
            intent.putExtra("des", mVideoList.get(position).des);
            intent.putExtra("view", mVideoList.get(position).view);
            intent.putExtra("icon", mVideoList.get(position).icon);
            intent.putExtra("video_id", mVideoList.get(position).id);
            //intent.putExtra("media_type", "videoondemand");
            //intent.putExtra("decode_type", "software");
            intent.setClass(userPagerActivity, VideoPlayerIsUserActivity.class);
            userPagerActivity.startActivity(intent);
        });

        if (TextUtils.equals(lookUserId, mUserId)) {  //只有自己的页面才能长按删除视频
            holder.itemView.setOnLongClickListener(v -> {
                showDeleteDialog(position);
                return true;
            });
        }
    }

    @Override
    public int getItemCount() {
        return mVideoList.size();
    }


    static class VideoHolder extends RecyclerView.ViewHolder {

        public RelativeLayout rlVideoCover;
        public ImageView      ivVideoCover;
        public TextView       tvVideoTitleTag;
        public TextView       tvVideoNumber;
        public TextView       tvVideoDesc;

        public VideoHolder(View itemView) {
            super(itemView);
            rlVideoCover = (RelativeLayout) itemView.findViewById(R.id.rl_video_cover);
            ivVideoCover = (ImageView) itemView.findViewById(R.id.iv_video_cover);
            tvVideoTitleTag = (TextView) itemView.findViewById(R.id.tv_video_title_tag);
            tvVideoNumber = (TextView) itemView.findViewById(R.id.tv_video_number);
            tvVideoDesc = (TextView) itemView.findViewById(R.id.tv_video_desc);
        }
    }

    private void showDeleteDialog(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(userPagerActivity);
        builder.setTitle("提示：");
        builder.setMessage("确定要删除“" + mVideoList.get(position).title + "”这个视频吗?");
        builder.setPositiveButton("取消", null);
        builder.setNegativeButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                gotoDeleteUserVideo(position);
            }
        });
        builder.show();
    }

    private void gotoDeleteUserVideo(int position) {
        sfProgressDialog.show();
        OkHttpUtils.get().url(GlobalConstants.URL_DELETE_MY_VIDEO)
                .addParams("vid", mVideoList.get(position).vid)
                .addParams("userid", mUserId)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        sfProgressDialog.dismiss();
                        LogUtils.show(TAG, "onError: ---网络异常,删除视频失败---");
                        Toast.makeText(userPagerActivity, "网络异常,删除视频失败,请重试", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        sfProgressDialog.dismiss();
                        LogUtils.show(TAG, "onResponse: ---删除视频访问网络成功---response = " + response + " ---");
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String data = jsonObject.getString("data");
                            String message = jsonObject.getString("message");
                            if (TextUtils.equals(data, "success")) {
                                Toast.makeText(userPagerActivity, message, Toast.LENGTH_SHORT).show();
                                mVideoList.remove(position);
                                VideoAdapter.this.notifyDataSetChanged();
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



