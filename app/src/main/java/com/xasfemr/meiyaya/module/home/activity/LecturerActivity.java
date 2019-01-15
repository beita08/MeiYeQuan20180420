package com.xasfemr.meiyaya.module.home.activity;

import android.graphics.Rect;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.xasfemr.meiyaya.R;
import com.xasfemr.meiyaya.base.activity.MVPBaseActivity;
import com.xasfemr.meiyaya.bean.MessageEvent;
import com.xasfemr.meiyaya.global.GlobalConstants;
import com.xasfemr.meiyaya.module.college.adapter.LectureAdapter;
import com.xasfemr.meiyaya.module.college.presenter.CollegePresenter;
import com.xasfemr.meiyaya.module.college.protocol.AttentionEventProtocol;
import com.xasfemr.meiyaya.module.college.protocol.LectureProtocol;
import com.xasfemr.meiyaya.module.college.view.AttentionIView;
import com.xasfemr.meiyaya.module.college.view.LectureIView;
import com.xasfemr.meiyaya.utils.LogUtils;
import com.xasfemr.meiyaya.utils.SPUtils;
import com.xasfemr.meiyaya.utils.ToastUtil;
import com.xasfemr.meiyaya.utils.UiUtils;
import com.xasfemr.meiyaya.utils.ViewStatus;
import com.xasfemr.meiyaya.view.LoadDataView;
import com.xasfemr.meiyaya.view.MeiYaYaHeader;
import com.xasfemr.meiyaya.view.progress.SFProgressDialog;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;
import org.simple.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;

/**
 * 首页--讲师
 */
public class LecturerActivity extends MVPBaseActivity {

    @BindView(R.id.rv_college_lecture)
    RecyclerView rvCollegeLecture;
    @BindView(R.id.refreshLayout)
    RefreshLayout refreshLayout;
    @BindView(R.id.tv_top_title)
    TextView tvTitle;
    @BindView(R.id.iv_top_back)
    ImageView ivBack;
    @BindView(R.id.layout_root)
    LinearLayout layoutRoot;

    private LoadDataView loadDataView;
    private CollegePresenter collegePresenter;
    private ArrayList<LectureProtocol> lectureProtocols;

    private SFProgressDialog progressDialog;

    private  LectureAdapter lectureAdapter;

    private int pageNumber=0;


    @Override
    protected int layoutId() {
        return R.layout.activity_lecturer;
    }

    @Override
    protected ViewGroup loadDataViewLayout() {
        return layoutRoot;
    }

    @Override
    protected void initView() {
        tvTitle.setText("讲师");
        EventBus.getDefault().register(this);
        lectureProtocols=new ArrayList<>();
        progressDialog=new SFProgressDialog(this);

        lectureAdapter =new LectureAdapter(this,lectureProtocols);
        rvCollegeLecture.setLayoutManager(new GridLayoutManager(this, 3));
        rvCollegeLecture.addItemDecoration(new SpaceItemDecoration(UiUtils.dp2px(this, 10)));
        rvCollegeLecture.setAdapter(lectureAdapter);




        refreshLayout.setRefreshHeader(new MeiYaYaHeader(this));
        refreshLayout.setRefreshFooter(new ClassicsFooter(this).setSpinnerStyle(SpinnerStyle.Scale));
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                pageNumber=0;
                initData();
            }
        });

        refreshLayout.setOnLoadmoreListener(new OnLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                pageNumber++;
                initData();
            }
        });

        ivBack.setOnClickListener(v -> finish());

    }

    @Override
    protected void initData() {
        loadDataView.changeStatusView(ViewStatus.START);

        HashMap<String,String> map =new HashMap<>();
        map.put("id", SPUtils.getString(this, GlobalConstants.userID,""));
        map.put("page", pageNumber+"");

        collegePresenter.getLectureListData(map,new LectureIView() {
            @Override
            public void getLectureListSuccess(ArrayList<LectureProtocol> lectureProtocoList) {
                if (pageNumber==0){
                    lectureProtocols.clear();
                }
                if (refreshLayout!=null){
                    refreshLayout.finishRefresh();
                    refreshLayout.finishLoadmore();
                }
                lectureProtocols.addAll(lectureProtocoList);
                lectureAdapter.notifyDataSetChanged();

            setEmpty(lectureProtocoList.size()==0);
            }

            @Override
            public void getLectureListFailure(String msg) {
                loadDataView.changeStatusView(ViewStatus.FAILURE);
                if (refreshLayout!=null){
                    refreshLayout.finishRefresh();
                    refreshLayout.finishLoadmore();
                }
                ToastUtil.showShort(LecturerActivity.this,msg);



            }

            @Override
            public void onNetworkFailure(String message) {
                loadDataView.changeStatusView(ViewStatus.NOTNETWORK);
                if (refreshLayout!=null){
                    refreshLayout.finishRefresh();
                    refreshLayout.finishLoadmore();
                }
                ToastUtil.showShort(LecturerActivity.this,message);

            }
        });

    }

    @Override
    protected void getLoadView(LoadDataView loadView) {
        this.loadDataView=loadView;
        loadDataView.setErrorListner(v -> initData());

    }

    @Override
    protected void initPresenter() {
        collegePresenter=new CollegePresenter();

    }


    /**
     * 发起关注
     * @param userId
     */
    private void getAttention(String userId,int postion){

        if (TextUtils.isEmpty(SPUtils.getString(this,GlobalConstants.userID,""))){
            ToastUtil.showShort(this,"您未登陆，请先去登陆");
            return;
        }  if (SPUtils.getString(this,GlobalConstants.userID,"").equals(userId)){
            ToastUtil.showShort(this,"自己不能关注自己");
            return;
        }


        if (progressDialog==null){
          progressDialog=new SFProgressDialog(this);
        }

        progressDialog.show();
        HashMap map =new HashMap();
        map.put("userid",SPUtils.getString(this,GlobalConstants.userID,""));
        map.put("uid",userId);

        collegePresenter.getAttention(map, new AttentionIView() {
            @Override
            public void getAttentionSuccess(String message) {
                progressDialog.dismiss();
                ToastUtil.showShort(LecturerActivity.this,message);
                lectureProtocols.get(postion).attention="1";
                lectureAdapter.notifyDataSetChanged();

            }

            @Override
            public void getAttentionOnFailure(String message) {
                progressDialog.dismiss();
                ToastUtil.showShort(LecturerActivity.this,message);
            }

            @Override
            public void onNetworkFailure(String message) {
                progressDialog.dismiss();
                ToastUtil.showShort(LecturerActivity.this,message);
            }
        });

    }


    /**
     * 取消关注
     * @param userId
     */
    private void cancelAttention(String userId,int postion){


//
        progressDialog.show();
        HashMap map =new HashMap();
        map.put("userid",SPUtils.getString(this,GlobalConstants.userID,""));
        map.put("uid",userId);


        collegePresenter.cancelAttention(map, new AttentionIView() {
            @Override
            public void getAttentionSuccess(String message) {
                progressDialog.dismiss();
                ToastUtil.showShort(LecturerActivity.this,message);
                lectureProtocols.get(postion).attention="0";
                lectureAdapter.notifyDataSetChanged();
            }

            @Override
            public void getAttentionOnFailure(String message) {
                progressDialog.dismiss();
                ToastUtil.showShort(LecturerActivity.this,message);
            }

            @Override
            public void onNetworkFailure(String message) {
                progressDialog.dismiss();
                ToastUtil.showShort(LecturerActivity.this,message);
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        collegePresenter.destroy();
    }

    private class SpaceItemDecoration extends RecyclerView.ItemDecoration {

        private int mSpace;

        public SpaceItemDecoration(int mSpace) {
            this.mSpace = mSpace;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            outRect.top = mSpace;
            outRect.right = mSpace;

            if (parent.getChildAdapterPosition(view) % 3 == 0) {
                outRect.left = mSpace;
            }
        }
    }




    @Subscriber(mode = ThreadMode.MAIN, tag = GlobalConstants.EventBus.GET_ATTENTION)
    public void onGetAttention(AttentionEventProtocol attentionEventProtocol) { //发起关注

        if (progressDialog==null){
            progressDialog=new SFProgressDialog(this);
        }

        if (attentionEventProtocol.type==0){ //发起关注
            getAttention(attentionEventProtocol.userId,attentionEventProtocol.position);
        }else { //取消关注
            cancelAttention(attentionEventProtocol.userId,attentionEventProtocol.position);
        }



    }

    private void setEmpty(boolean isEmpty) {
        loadDataView.setFirstLoad();
        loadDataView.changeStatusView( isEmpty ? ViewStatus.EMPTY:ViewStatus.SUCCESS);
    }

//    @Subscriber(mode = ThreadMode.MAIN, tag = GlobalConstants.EventBus.CANCEL_ATTENTION)
//    public void onCancelAttention(String user_id) { //取消关注
//        cancelAttention(user_id);
//
//    }

}

/*-----原本该页面分为'金牌讲师'和'业界新秀'两个Fragment,后合二为一-----*/
/* //onCreate方法中的初始化
tvLecturerGood = (TextView) findViewById(R.id.tv_lecturer_good);
tvLecturerNew = (TextView) findViewById(R.id.tv_lecturer_new);
vpLecturer = (ViewPager) findViewById(R.id.vp_lecturer);

tvLecturerGood.setOnClickListener(this);
tvLecturerNew.setOnClickListener(this);

vpLecturer.setAdapter(new LecturerAdapter(getFragmentManager()));
vpLecturer.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        if (position == 0) {
            tvLecturerGood.setSelected(true);
            tvLecturerNew.setSelected(false);
        } else if (position == 1) {
            tvLecturerGood.setSelected(false);
            tvLecturerNew.setSelected(true);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }
});

//初始化
tvLecturerGood.setSelected(true);
tvLecturerNew.setSelected(false);*/

/* onClick方法
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_lecturer_good:
                vpLecturer.setCurrentItem(0);
                break;
            case R.id.tv_lecturer_new:
                vpLecturer.setCurrentItem(1);
                break;
            default:
                break;
        }
    }*/

/*private class LecturerAdapter extends FragmentPagerAdapter {

        public LecturerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public Fragment getItem(int position) {
            BaseFragment baseFragment = LecturerFragmentFactory.getFragment(position);
            return baseFragment;
        }

        @Override
        public int getCount() {
            return 2;
        }
    }*/