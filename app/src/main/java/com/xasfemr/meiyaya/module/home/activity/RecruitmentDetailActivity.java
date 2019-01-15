package com.xasfemr.meiyaya.module.home.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lzy.ninegrid.ImageInfo;
import com.xasfemr.meiyaya.R;
import com.xasfemr.meiyaya.activity.LoginActivity;
import com.xasfemr.meiyaya.activity.MyImagePreviewActivity;
import com.xasfemr.meiyaya.base.activity.MVPBaseActivity;
import com.xasfemr.meiyaya.global.GlobalConstants;
import com.xasfemr.meiyaya.module.home.IView.RecruitmentDetailIView;
import com.xasfemr.meiyaya.module.home.adapter.DetailImagesAdapter;
import com.xasfemr.meiyaya.module.home.presenter.PostPresenter;
import com.xasfemr.meiyaya.module.home.protocol.RecruimentDetailProtocol;
import com.xasfemr.meiyaya.module.mine.protocol.Shareprotocol;
import com.xasfemr.meiyaya.utils.SPUtils;
import com.xasfemr.meiyaya.utils.ToastUtil;
import com.xasfemr.meiyaya.utils.ViewStatus;
import com.xasfemr.meiyaya.view.BasicShareDialog;
import com.xasfemr.meiyaya.view.LoadDataView;
import com.xasfemr.meiyaya.view.NoScrollNoDividerListView;
import com.xasfemr.meiyaya.weight.SFDialog;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;

/**
 * 招聘详情
 * Created by sen.luo on 2018/3/5.
 */

public class RecruitmentDetailActivity extends MVPBaseActivity {

    @BindView(R.id.tv_top_title)
    TextView                  tvTitle;
    @BindView(R.id.iv_top_back)
    ImageView                 ivBack;
    @BindView(R.id.rootLayout)
    LinearLayout              rootLayout;
    @BindView(R.id.lvImages)
    NoScrollNoDividerListView lvImages; //图片列表
    @BindView(R.id.tvCompanyNme)
    TextView                  tvCompanyName;  //公司名
    @BindView(R.id.tvName)
    TextView                  tvName; //联系人
    @BindView(R.id.tvPhone)
    TextView                  tvPhone;
    @BindView(R.id.tvDetailContent)
    TextView                  tvDetailContent;  //详细说明
    @BindView(R.id.tvSalary)
    TextView                  tvSalary; //待遇
    @BindView(R.id.tvTitleDetail)
    TextView                  tvTitleDetail;
    @BindView(R.id.imgIcon)
    ImageView                 imgIcon;
    @BindView(R.id.tvAddress)
    TextView                  tvAddress;
    @BindView(R.id.tvScanNum)
    TextView                  tvScanNum;
    @BindView(R.id.imgApprove)
    ImageView                 imgApprove;
    @BindView(R.id.iv_top_search)
    ImageView                 ivShare;

    private LoadDataView loadDataView;
    private ArrayList<String>    imagesList;
    private ArrayList<ImageInfo> bigImageList;

    private RecruimentDetailProtocol recruimentDetailProtocol;
    private DetailImagesAdapter      imagesAdapter;

    private PostPresenter postPresenter;
    private Shareprotocol shareprotocol;
    private String infoId = "";

    @Override
    protected int layoutId() {
        return R.layout.activity_recruitment_detail;
    }

    @Override
    protected ViewGroup loadDataViewLayout() {
        return rootLayout;
    }

    @Override
    protected void initView() {
        tvTitle.setText("职位详情");
        ivBack.setOnClickListener(view -> finish());

        imagesList = new ArrayList<>();
        imagesAdapter = new DetailImagesAdapter(this, imagesList);
        lvImages.setAdapter(imagesAdapter);

        infoId = getIntent().getStringExtra("info_id");

        tvPhone.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        tvPhone.getPaint().setAntiAlias(true); //抗锯齿

        tvPhone.setOnClickListener(view -> {
            SFDialog.basicDialog(RecruitmentDetailActivity.this, "是否拨打电话？", tvPhone.getText().toString(), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + tvPhone.getText().toString()));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            });
        });

        lvImages.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(RecruitmentDetailActivity.this, MyImagePreviewActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable(MyImagePreviewActivity.IMAGE_INFO, (Serializable) bigImageList);
                bundle.putInt(MyImagePreviewActivity.CURRENT_ITEM, position);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        ivShare.setVisibility(View.VISIBLE);
        ivShare.setImageDrawable(getResources().getDrawable(R.drawable.icon_gray_share));

        ivShare.setOnClickListener(v -> getShare());
        shareprotocol = new Shareprotocol();
    }

    @Override
    protected void initData() {
        loadDataView.changeStatusView(ViewStatus.START);

        HashMap<String, String> map = new HashMap<>();
        map.put("infoid", infoId);

        postPresenter.postRecruitmentDetail(map, new RecruitmentDetailIView() {
            @Override
            public void getRecruimentDetailOnSuccess(RecruimentDetailProtocol recruimentDetail) {
                if (recruimentDetail != null) {
                    recruimentDetailProtocol = recruimentDetail;

                    tvCompanyName.setText(recruimentDetailProtocol.companyName);
                    tvName.setText(recruimentDetailProtocol.linkman);
                    tvPhone.setText(recruimentDetailProtocol.phone);
                    tvSalary.setText(recruimentDetailProtocol.salary);
                    tvDetailContent.setText(recruimentDetailProtocol.content);
                    tvTitleDetail.setText(recruimentDetailProtocol.title);
                    tvAddress.setText(recruimentDetailProtocol.workPlace);
                    tvScanNum.setText("浏览" + recruimentDetailProtocol.click);
                    Glide.with(RecruitmentDetailActivity.this).load(recruimentDetailProtocol.thumb).into(imgIcon);

                    if (!TextUtils.isEmpty(recruimentDetailProtocol.is_approve) && recruimentDetailProtocol.is_approve.equals("1")) {
                        imgApprove.setVisibility(View.VISIBLE);
                    } else {
                        imgApprove.setVisibility(View.GONE);
                    }

                    imagesList.addAll(recruimentDetailProtocol.imageList);
                    imagesAdapter.notifyDataSetChanged();

                    loadDataView.changeStatusView(ViewStatus.SUCCESS);

                    //点击图片看大图
                    bigImageList = new ArrayList<>();
                    ArrayList<String> imageUrlList = imagesList;
                    if (imageUrlList != null) {
                        for (int i = 0; i < imageUrlList.size(); i++) {
                            ImageInfo imageInfo = new ImageInfo();
                            imageInfo.setThumbnailUrl(imageUrlList.get(i));
                            imageInfo.setBigImageUrl(imageUrlList.get(i));
                            bigImageList.add(imageInfo);
                        }
                    }
                }
            }

            @Override
            public void getRecruimentDetailsOnFailure(String msg) {
                loadDataView.changeStatusView(ViewStatus.FAILURE);
                ToastUtil.showShort(RecruitmentDetailActivity.this, msg);
            }

            @Override
            public void onNetworkFailure(String message) {
                loadDataView.changeStatusView(ViewStatus.NOTNETWORK);
                ToastUtil.showShort(RecruitmentDetailActivity.this, message);
            }
        });
    }

    @Override
    protected void getLoadView(LoadDataView loadView) {
        this.loadDataView = loadView;
        this.loadDataView.setErrorListner(view -> initData());
    }

    @Override
    protected void initPresenter() {
        postPresenter = new PostPresenter();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        postPresenter.destroy();
    }

    /**
     * 分享
     */
    private void getShare() {
        if (TextUtils.isEmpty(SPUtils.getString(this, GlobalConstants.userID, ""))) {
            startActivity(new Intent(this, LoginActivity.class));
        } else {
            shareprotocol.sharetitle = recruimentDetailProtocol.title;
            shareprotocol.shareUrl = recruimentDetailProtocol.share_link;
            shareprotocol.shareImage = recruimentDetailProtocol.thumb;
            shareprotocol.shareMsg = recruimentDetailProtocol.content;
            //            shareprotocol.shareCid=urlId;
            //            shareprotocol.shareStatus=shareStatus;
            BasicShareDialog shareDialog = new BasicShareDialog(this, shareprotocol);
            shareDialog.show();
        }
    }
}
