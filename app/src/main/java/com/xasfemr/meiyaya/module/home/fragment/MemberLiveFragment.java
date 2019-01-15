package com.xasfemr.meiyaya.module.home.fragment;


import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.xasfemr.meiyaya.R;
import com.xasfemr.meiyaya.base.fragment.MVPBaseFragment;
import com.xasfemr.meiyaya.module.college.activity.PlaybackListActivity;
import com.xasfemr.meiyaya.module.home.IView.MemberHotIView;
import com.xasfemr.meiyaya.module.home.adapter.MemberCourseHotAdapter;
import com.xasfemr.meiyaya.module.home.presenter.MemberPresenter;
import com.xasfemr.meiyaya.module.home.protocol.MemberCourseHotListProtocol;
import com.xasfemr.meiyaya.module.home.protocol.MemberCourseHotProtocol;
import com.xasfemr.meiyaya.utils.ToastUtil;
import com.xasfemr.meiyaya.utils.ViewStatus;
import com.xasfemr.meiyaya.view.LoadDataView;
import com.xasfemr.meiyaya.view.MeiYaYaHeader;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;

/**
 * 会员课程--直播
 */
public class MemberLiveFragment extends MVPBaseFragment {

    @BindView(R.id.rv_member_hot)
    RecyclerView rvMemberHot;
    @BindView(R.id.refreshLayout)
    RefreshLayout refreshLayout;

    private String net_typeid="";
    private LoadDataView loadDataView;
    private ArrayList<MemberCourseHotProtocol> memberCourseHotProtocols;

    private MemberPresenter memberPresenter;
    private int pageNumber=0;
    private MemberCourseHotAdapter memberCourseHotAdapter;
//
//    public MemberLiveFragment(String net_typeid) {
//        this.net_typeid = net_typeid;
//    }


    @Override
    protected int layoutId() {
        return R.layout.fragment_member_course_hot;
    }

    @Override
    protected void initView() {

        memberCourseHotProtocols =new ArrayList<>();
        memberCourseHotAdapter=new MemberCourseHotAdapter(getActivity(), memberCourseHotProtocols);
        rvMemberHot.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        rvMemberHot.setAdapter(memberCourseHotAdapter);
        refreshLayout.setRefreshFooter(new ClassicsFooter(getActivity()).setSpinnerStyle(SpinnerStyle.Scale));
        refreshLayout.setRefreshHeader(new MeiYaYaHeader(getActivity()));
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                loadData(false);
            }
        });
        refreshLayout.setOnLoadmoreListener(new OnLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                loadData(true);
            }
        });
    }


    @Override
    protected void getLoadView(LoadDataView mLoadView) {
        this.loadDataView=mLoadView;
        loadDataView.setErrorListner(v -> loadData(false));

    }

    @Override
    public void initData() {
        loadData(false);

    }

    private void loadData(boolean isLoadMore) {
        if (isLoadMore){
            pageNumber++;
        }else {
            pageNumber=0;
        }

        loadDataView.changeStatusView(ViewStatus.START);


        HashMap<String,String> map =new HashMap<>();
        map.put("page", String.valueOf(pageNumber));

        memberPresenter.getMemberLiveList( map,new MemberHotIView() {
            @Override
            public void getMemberHotListSuccess(MemberCourseHotListProtocol memberCourseHotListProtocolist) {
                if (pageNumber==0){
                    memberCourseHotProtocols.clear();
                }

                if (refreshLayout!=null){
                    refreshLayout.finishRefresh();
                    refreshLayout.finishLoadmore();
                }

                memberCourseHotProtocols.addAll(memberCourseHotListProtocolist.list);
                memberCourseHotAdapter.notifyDataSetChanged();

                if (memberCourseHotListProtocolist.list.size()==0){
                    loadDataView.changeStatusView(ViewStatus.EMPTY);
                    if (pageNumber!=0){
                        ToastUtil.showShort(getActivity(),"暂无数据");
                    }

                }else {
                    loadDataView.changeStatusView(ViewStatus.SUCCESS);
                }

            }

            @Override
            public void getMemberHotListFailure(String msg) {
                if (refreshLayout!=null){
                    refreshLayout.finishRefresh();
                    refreshLayout.finishLoadmore();
                }

                ToastUtil.showShort(getActivity(),msg);
                loadDataView.changeStatusView(ViewStatus.FAILURE);
            }

            @Override
            public void onNetworkFailure(String message) {
                if (refreshLayout!=null){
                    refreshLayout.finishRefresh();
                    refreshLayout.finishLoadmore();
                }
                ToastUtil.showShort(getActivity(),message);
                loadDataView.changeStatusView(ViewStatus.NOTNETWORK);

            }
        });



    }

    @Override
    protected void initPresenter() {
        memberPresenter=new MemberPresenter();
    }

    private void setEmpty(boolean isEmpty) {
        loadDataView.setFirstLoad();
        loadDataView.changeStatusView( isEmpty ? ViewStatus.EMPTY:ViewStatus.SUCCESS);
    }


//    private class CourseAdapter extends RecyclerView.Adapter<CourseHolder> {
//
//        @Override
//        public CourseHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//            View view = View.inflate(getActivity(), R.layout.item_live_content_single, null);
//            CourseHolder courseHolder = new CourseHolder(view);
//
//            return courseHolder;
//        }
//
//        @Override
//        public void onBindViewHolder(CourseHolder holder, int position) {
//            holder.tvLiveTime.setText("01:30:12");
//
//            holder.ivUserIcon.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent intent = new Intent(getActivity(), UserPagerActivity.class);
//                    getActivity().startActivity(intent);
//                }
//            });
//
//            holder.ivLiveScreenshot.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    boolean isLoginState = SPUtils.getboolean(getActivity(), GlobalConstants.isLoginState, false);
//                    Intent intent = new Intent();
//                    if (isLoginState) {
//                        Toast.makeText(getActivity(), "跳到视频播放页面", Toast.LENGTH_SHORT).show();
//                    } else {
//                        intent.setClass(getActivity(), LoginActivity.class);
//                        getActivity().startActivity(intent);
//                    }
//                }
//            });
//        }
//
//        @Override
//        public int getItemCount() {
//            return 100;
//        }
//    }
//
//    static class CourseHolder extends RecyclerView.ViewHolder {
//
//        public ImageView ivUserIcon;
//        public TextView  tvUserName;
//        public TextView  tvPeopleNumber;
//        public TextView  tvUserDes;
//        public ImageView ivLiveScreenshot;
//        public TextView  tvLiveTitleTag;
//        public TextView  tvLiveTime;
//
//        public CourseHolder(View itemView) {
//            super(itemView);
//            ivUserIcon = (ImageView) itemView.findViewById(R.id.iv_user_icon);
//            tvUserName = (TextView) itemView.findViewById(R.id.tv_user_name);
//            tvPeopleNumber = (TextView) itemView.findViewById(R.id.tv_people_number);
//            tvUserDes = (TextView) itemView.findViewById(R.id.tv_user_des);
//            ivLiveScreenshot = (ImageView) itemView.findViewById(R.id.iv_live_screenshot);
//            tvLiveTitleTag = (TextView) itemView.findViewById(R.id.tv_live_title_tag);
//            tvLiveTime = (TextView) itemView.findViewById(R.id.tv_live_time);
//        }
//    }


}
