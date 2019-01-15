package com.xasfemr.meiyaya.module.college.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.xasfemr.meiyaya.R;
import com.xasfemr.meiyaya.activity.UserPagerActivity;
import com.xasfemr.meiyaya.base.fragment.MVPBaseFragment;
import com.xasfemr.meiyaya.global.GlobalConstants;
import com.xasfemr.meiyaya.module.college.presenter.CollegePresenter;
import com.xasfemr.meiyaya.module.college.protocol.ExcellentListProtocol;
import com.xasfemr.meiyaya.module.college.view.CollectIView;
import com.xasfemr.meiyaya.utils.SPUtils;
import com.xasfemr.meiyaya.utils.ToastUtil;
import com.xasfemr.meiyaya.view.LoadDataView;
import com.xasfemr.meiyaya.view.progress.SFProgressDialog;

import org.simple.eventbus.EventBus;

import java.util.HashMap;


/**
 * 精品课程---简介
 * Created by sen.luo on 2017/12/18.
 */

public class SummaryFragment extends Fragment{

    private ExcellentListProtocol excellentListProtocollist;

    private static SummaryFragment summaryFragment;
//    @BindView(R.id.tvCourseName)
    TextView tvCourseName;

//    @BindView(R.id.tvCourseNum)
    TextView tvCourseNum;

//    @BindView(R.id.tvStuday)
    TextView tvStusay;

//    @BindView(R.id.tvCouserSummary)
    TextView tvCourseSummary;

//    @BindView(R.id.tvLectureSummary)
    TextView tvLectureSummary;
//    @BindView(R.id.img_lecture)
    ImageView imglecture;
//    @BindView(R.id.tvLectureName)
    TextView tvLectureName;

    private CollegePresenter collegePresenter;
    private SFProgressDialog progressDialog;

    private String courseId="";

    private boolean isCollect=false;

//    public static  SummaryFragment getInstance(ExcellentListProtocol excellentListProtocol){
//        synchronized (SummaryFragment.class){
//            if (summaryFragment==null){
//                summaryFragment =new SummaryFragment(excellentListProtocol);
//            }
//        }
//
////        Bundle bundle =new Bundle();
////        bundle.putSerializable("protocol",excellentListProtocol);
////        summaryFragment.setArguments(bundle);
//        return summaryFragment;
//
//    }

    public SummaryFragment(ExcellentListProtocol excellentListProtocol){
        this.excellentListProtocollist=excellentListProtocol;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_summary,null);

        tvCourseName= (TextView) view.findViewById(R.id.tvCourseName);
        tvCourseNum= (TextView) view.findViewById(R.id.tvCourseNum);
        tvStusay= (TextView) view.findViewById(R.id.tvStuday);
        tvCourseSummary= (TextView) view.findViewById(R.id.tvCouserSummary);
        tvLectureSummary= (TextView) view.findViewById(R.id.tvLectureSummary);
        tvLectureName= (TextView) view.findViewById(R.id.tvLectureName);
        imglecture= (ImageView) view.findViewById(R.id.img_lecture);

        tvCourseName.setText(excellentListProtocollist.title);
        tvCourseNum.setText(excellentListProtocollist.view);
        tvLectureName.setText(excellentListProtocollist.lecturer_name);
        Glide.with(getActivity()).load(excellentListProtocollist.icon).apply(new RequestOptions().placeholder(R.drawable.meiyaya_logo_round_gray)).into(imglecture);
        tvCourseSummary.setText(excellentListProtocollist.des);
        tvLectureSummary.setText(excellentListProtocollist.lecturer);
        courseId=excellentListProtocollist.id;

        collegePresenter=new CollegePresenter();
        progressDialog=new SFProgressDialog(getActivity());

        if (excellentListProtocollist.iscollect){
            tvStusay.setBackground(getResources().getDrawable(R.drawable.shape_button_load_view));
            tvStusay.setText("已加入学习");
            tvStusay.setTextColor(getResources().getColor(R.color.globalRed));
            isCollect=true;

        }else {
            tvStusay.setBackground(getResources().getDrawable(R.drawable.shape_button_load));
            tvStusay.setText("加入学习");
            tvStusay.setTextColor(getResources().getColor(R.color.white));
            isCollect=false;

        }

        tvStusay.setOnClickListener(v -> JoinStuday());
        imglecture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(excellentListProtocollist.lecturer_id)){
                    startActivity(new Intent(getActivity(), UserPagerActivity.class).putExtra("LOOK_USER_ID",excellentListProtocollist.lecturer_id));
                }

            }
        });
        return view;
    }


    /**
     * 加入学习
     */
    private void JoinStuday() {
        if (progressDialog!=null){
            progressDialog.show();
        }


        HashMap<String,String > map =new HashMap<>();
        map.put("userid", SPUtils.getString(getActivity(), GlobalConstants.userID,""));
        map.put("id",courseId); //课程ID

        collegePresenter.collectCourse(map, new CollectIView() {
            @Override
            public void getCollectSuccess(String message) {

                if (progressDialog!=null){
                    progressDialog.dismiss();
                }

                if (!isCollect){
                    tvStusay.setBackground(getResources().getDrawable(R.drawable.shape_button_load_view));
                    tvStusay.setText("已加入学习");
                    tvStusay.setTextColor(getResources().getColor(R.color.globalRed));
                    isCollect=true;
                }else {
                    tvStusay.setBackground(getResources().getDrawable(R.drawable.shape_button_load));
                    tvStusay.setText("加入学习");
                    tvStusay.setTextColor(getResources().getColor(R.color.white));
                    isCollect=false;
                }

                EventBus.getDefault().post("1",GlobalConstants.EventBus.UPDATE_EXCELLENT_COURSE_LIST);

            }

            @Override
            public void getCollectOnFailure(String message) {
                ToastUtil.showShort(getActivity(),message);
                if (progressDialog!=null){
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onNetworkFailure(String message) {
                ToastUtil.showShort(getActivity(),message);
                if (progressDialog!=null){
                    progressDialog.dismiss();
                }
            }
        });


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        collegePresenter.destroy();
    }

    //    @Override
//    protected int layoutId() {
//        return R.layout.fragment_summary;
//    }

//    @Override
//    protected void initView() {
////        excellentListProtocollist= (ExcellentListProtocol) getArguments().getSerializable("protocol");
//
//
//
//
//    }
//
//    @Override
//    protected void getLoadView(LoadDataView mLoadView) {
//
//    }
//
//    @Override
//    protected void initData() {
//
//    }
//
//    @Override
//    protected void initPresenter() {
//
//    }
}
