package com.xasfemr.meiyaya.adapter;

import android.graphics.Rect;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.xasfemr.meiyaya.R;
import com.xasfemr.meiyaya.bean.HomeIndustryData;
import com.xasfemr.meiyaya.bean.HomeVideoListData;
import com.xasfemr.meiyaya.main.MainActivity;

import java.util.ArrayList;

/**
 * Description:
 * Company    : shifeier
 * Author     : liuxb
 * Date       : 2018/1/24 0024 17:16
 */
public class HomeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "HomeAdapter";

    private static final int TYPE_TOP_BANNER = 0;   //顶部Banner类型
    private static final int TYPE_MORE_TITLE = 1;   //更多标题类型
    private static final int TYPE_LIVE_VIDEO = 2;   //直播内容类型
    private static final int TYPE_NEWS_INFO  = 3;   //新闻详情类型

    private int itemViewType = -1;

    private MainActivity                               mainActivity;
    private ArrayList<HomeIndustryData.IndustryInfo>   mIndustryInfoList;
    private ArrayList<HomeVideoListData.HomeVideoInfo> mHomeVideoList;
    private ArrayList<HomeVideoListData.HomeVideoInfo> mHotVideoList    = new ArrayList<>();
    private ArrayList<HomeVideoListData.HomeVideoInfo> mMemberVideoList = new ArrayList<>();

    public HomeAdapter(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    public void setHomeVideoList(ArrayList<HomeVideoListData.HomeVideoInfo> homeVideoList) {
        this.mHomeVideoList = homeVideoList;
        mHomeVideoList.add(0, new HomeVideoListData.HomeVideoInfo());
        mHomeVideoList.add(1, new HomeVideoListData.HomeVideoInfo());
        mHomeVideoList.add(4, new HomeVideoListData.HomeVideoInfo());

        /*for (int i = 0; i < homeVideoList.size(); i++) {
            if (TextUtils.equals(homeVideoList.get(i).type, "hot")) {
                mHotVideoList.add(homeVideoList.get(i));
            } else if (TextUtils.equals(homeVideoList.get(i).type, "member")) {
                mMemberVideoList.add(homeVideoList.get(i));
            }
        }*/
    }

    public void setIndustryInfoList(ArrayList<HomeIndustryData.IndustryInfo> industryInfoList) {
        this.mIndustryInfoList = industryInfoList;
    }


    @Override
    public int getItemViewType(int position) {
        switch (position) {
            case 0:
                itemViewType = TYPE_TOP_BANNER;
                break;
            case 1:
            case 4:
            case 7:
                itemViewType = TYPE_MORE_TITLE;
                break;
            case 2:
            case 3:
            case 5:
            case 6:
                itemViewType = TYPE_LIVE_VIDEO;
                break;
            case 8:
            case 9:
            case 10:
                itemViewType = TYPE_NEWS_INFO;
                break;
            default:
                break;
        }
        return itemViewType;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        RecyclerView.ViewHolder holder = null;
        switch (viewType) {
            case TYPE_TOP_BANNER:
                view = View.inflate(parent.getContext(), R.layout.item_live_pager, null);
                holder = new TopBannerHolder(view);
                break;
            case TYPE_MORE_TITLE:
                view = View.inflate(parent.getContext(), R.layout.item_industry_info_title, null);
                holder = new MoreTitleHolder(view);

                break;
            case TYPE_LIVE_VIDEO:
                view = View.inflate(parent.getContext(), R.layout.item_live_content_single_top10, null);
                holder = new LiveVideoHolder(view);

                break;
            case TYPE_NEWS_INFO:
                view = View.inflate(parent.getContext(), R.layout.item_industry_info, null);
                holder = new NewsInfoHolder(view);

                break;
            default:
                break;
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof TopBannerHolder) {
            TopBannerHolder topBannerHolder = (TopBannerHolder) holder;

        } else if (holder instanceof MoreTitleHolder) {
            MoreTitleHolder moreTitleHolder = (MoreTitleHolder) holder;
            switch (position) {
                case 1:
                    moreTitleHolder.tvInfoTitle.setText("热门在线");
                    break;
                case 4:
                    moreTitleHolder.tvInfoTitle.setText("会员在线");
                    break;
                case 7:
                    moreTitleHolder.tvInfoTitle.setText("美业资讯");
                    break;
                default:
                    break;
            }
        } else if (holder instanceof LiveVideoHolder) {
            LiveVideoHolder liveVideoHolder = (LiveVideoHolder) holder;
            if (mHomeVideoList != null && mHomeVideoList.size() > 0 && !TextUtils.isEmpty(mHomeVideoList.get(position).type)) {
                Glide.with(mainActivity).load(mHomeVideoList.get(position).icon).into(liveVideoHolder.ivUserIcon);
                liveVideoHolder.tvUserName.setText(mHomeVideoList.get(position).username);
                liveVideoHolder.tvPeopleNumber.setText(mHomeVideoList.get(position).view);
                liveVideoHolder.tvUserDes.setText(mHomeVideoList.get(position).title);
                Glide.with(mainActivity).load(mHomeVideoList.get(position).cover).into(liveVideoHolder.ivLiveScreenshot);
                liveVideoHolder.tvLiveTitleTag.setText(mHomeVideoList.get(position).coursename);

                if (mHomeVideoList.get(position).status == 1) {
                    liveVideoHolder.tvLiveTime.setText("开课中");
                    liveVideoHolder.tvLiveTime.setBackgroundResource(R.drawable.live_time_bg);
                } else if (mHomeVideoList.get(position).status == 2) {
                    liveVideoHolder.tvLiveTime.setText(mHomeVideoList.get(position).duration);
                    liveVideoHolder.tvLiveTime.setBackgroundResource(R.drawable.course_time_bg);
                } else {
                    liveVideoHolder.tvLiveTime.setText(mHomeVideoList.get(position).status1);
                    liveVideoHolder.tvLiveTime.setBackgroundResource(R.drawable.live_time_bg);
                }
            }
        } else if (holder instanceof NewsInfoHolder) {
            NewsInfoHolder newsInfoHolder = (NewsInfoHolder) holder;
            if (mIndustryInfoList != null && mIndustryInfoList.size() > 0) {
                //资讯的第0条是RecyclerView的第8个条目,所以取数据时-8;
                Glide.with(mainActivity).load(mIndustryInfoList.get(position - 8).images).into(newsInfoHolder.ivNewsImg);
                newsInfoHolder.tvNewsTitle.setText(mIndustryInfoList.get(position - 8).title);
                newsInfoHolder.tvNewsDes.setText(mIndustryInfoList.get(position - 8).digest);
                newsInfoHolder.tvNewsTime.setText(mIndustryInfoList.get(position - 8).time);
                newsInfoHolder.tvNewsScanNum.setText(mIndustryInfoList.get(position - 8).hits);
            }
        }
    }

    @Override
    public int getItemCount() {
        return 11;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            GridLayoutManager gridLayoutManager = (GridLayoutManager) layoutManager;
            gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    int itemViewType = getItemViewType(position);
                    switch (itemViewType) {
                        case TYPE_TOP_BANNER:
                        case TYPE_MORE_TITLE:
                        case TYPE_NEWS_INFO:
                            return 2;
                        case TYPE_LIVE_VIDEO:
                            return 1;
                        default:
                            return 2;
                    }
                }
            });
        }
    }


    public static class SpaceItemDecoration extends RecyclerView.ItemDecoration {
        private int mSpace;

        public SpaceItemDecoration(int mSpace) {
            this.mSpace = mSpace;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            if (parent.getChildAdapterPosition(view) == 2 || parent.getChildAdapterPosition(view) == 5) {
                outRect.left = mSpace;
            }
        }
    }


    static class TopBannerHolder extends RecyclerView.ViewHolder {

        public ViewPager    vpBanner;
        public LinearLayout llPointContainer;
        public TextView     tvClassCourse;
        public TextView     tvClassLecturer;
        public TextView     tvClassMember;
        public TextView     tvClassRankingList;

        public TopBannerHolder(View itemView) {
            super(itemView);

            vpBanner = (ViewPager) itemView.findViewById(R.id.vp_banner);
            llPointContainer = (LinearLayout) itemView.findViewById(R.id.ll_point_container);
            tvClassCourse = (TextView) itemView.findViewById(R.id.tv_class_course);
            tvClassLecturer = (TextView) itemView.findViewById(R.id.tv_class_lecturer);
            tvClassMember = (TextView) itemView.findViewById(R.id.tv_class_member);
            tvClassRankingList = (TextView) itemView.findViewById(R.id.tv_class_ranking_list);

        }
    }

    static class MoreTitleHolder extends RecyclerView.ViewHolder {

        public TextView tvInfoTitle;
        public TextView tvInfoMore;

        public MoreTitleHolder(View itemView) {
            super(itemView);

            tvInfoTitle = (TextView) itemView.findViewById(R.id.tv_industry_info_title);
            tvInfoMore = (TextView) itemView.findViewById(R.id.tv_industry_info_more);
        }
    }

    static class LiveVideoHolder extends RecyclerView.ViewHolder {

        public ImageView ivUserIcon;
        public TextView  tvUserName;
        public TextView  tvPeopleNumber;
        public TextView  tvUserDes;
        public ImageView ivLiveScreenshot;
        public TextView  tvLiveTitleTag;
        public TextView  tvLiveTime;

        public LiveVideoHolder(View itemView) {
            super(itemView);

            ivUserIcon = (ImageView) itemView.findViewById(R.id.iv_user_icon);
            tvUserName = (TextView) itemView.findViewById(R.id.tv_user_name);
            tvPeopleNumber = (TextView) itemView.findViewById(R.id.tv_people_number);
            tvUserDes = (TextView) itemView.findViewById(R.id.tv_user_des);
            ivLiveScreenshot = (ImageView) itemView.findViewById(R.id.iv_live_screenshot);
            tvLiveTitleTag = (TextView) itemView.findViewById(R.id.tv_live_title_tag);
            tvLiveTime = (TextView) itemView.findViewById(R.id.tv_live_time);
        }
    }

    static class NewsInfoHolder extends RecyclerView.ViewHolder {

        public ImageView ivNewsImg;
        public TextView  tvNewsTitle;
        public TextView  tvNewsDes;
        public TextView  tvNewsTime;
        public TextView  tvNewsScanNum;

        public NewsInfoHolder(View itemView) {
            super(itemView);

            ivNewsImg = (ImageView) itemView.findViewById(R.id.iv_news_img);
            tvNewsTitle = (TextView) itemView.findViewById(R.id.tv_news_title);
            tvNewsDes = (TextView) itemView.findViewById(R.id.tv_news_des);
            tvNewsTime = (TextView) itemView.findViewById(R.id.tv_news_time);
            tvNewsScanNum = (TextView) itemView.findViewById(R.id.tv_news_scan_num);
        }
    }


}


