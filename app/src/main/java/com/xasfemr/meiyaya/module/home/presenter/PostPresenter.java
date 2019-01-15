package com.xasfemr.meiyaya.module.home.presenter;

import com.xasfemr.meiyaya.base.presenter.BasePresenter;
import com.xasfemr.meiyaya.base.protocol.BaseProtocol;
import com.xasfemr.meiyaya.http.SFSubscriber;
import com.xasfemr.meiyaya.module.home.IView.InstrumentDetailIView;
import com.xasfemr.meiyaya.module.home.IView.InstrumentListIView;
import com.xasfemr.meiyaya.module.home.IView.PostFilterListIView;
import com.xasfemr.meiyaya.module.home.IView.PutRecruitmentDetailIView;
import com.xasfemr.meiyaya.module.home.IView.RecruitmentDetailIView;
import com.xasfemr.meiyaya.module.home.IView.RecruitmentListIView;
import com.xasfemr.meiyaya.module.home.IView.RequestJobListIView;
import com.xasfemr.meiyaya.module.home.protocol.InstrumentDetailProtocol;
import com.xasfemr.meiyaya.module.home.protocol.InstrumentListProtocol;
import com.xasfemr.meiyaya.module.home.protocol.PostProtocol;
import com.xasfemr.meiyaya.module.home.protocol.RecruimentDetailProtocol;
import com.xasfemr.meiyaya.module.home.protocol.RecruitmentListProtocol;
import com.xasfemr.meiyaya.module.home.protocol.RequestJobListProtocol;

import java.util.ArrayList;
import java.util.HashMap;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by sen.luo on 2018/3/1.
 */

public class PostPresenter extends BasePresenter{


    //获取分类筛选列表信息
    public void postRecruitment(HashMap<String,String>map, PostFilterListIView postFilterListIView){

        subscriber(SFHttp(api().getFilterList(map)).subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new SFSubscriber<BaseProtocol<PostProtocol>>() {
            @Override
            protected void onSuccess(BaseProtocol<PostProtocol> result) {
                postFilterListIView.getFilterListSuccess(result.data);
            }

            @Override
            protected void onFailure(String msg) {
                postFilterListIView.getFilterListOnFailure(msg);
            }

            @Override
            protected void onNetworkFailure(String message) {
                postFilterListIView.onNetworkFailure(message);
            }
        }));
    }

    //获取招聘、求职、仪器列表
    public void postRecruitmentList(HashMap<String,String>map, RecruitmentListIView recruitmentListIView){
        subscriber(SFHttp(api().getRecruitmentList(map)).subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new SFSubscriber<BaseProtocol<ArrayList<RecruitmentListProtocol>>>() {
            @Override
            protected void onSuccess(BaseProtocol<ArrayList<RecruitmentListProtocol>> result) {

                recruitmentListIView.getRecruitmentListSuccess(result.data);
//                if (result.data==null){
//                    recruitmentListIView.getRecruimentLsitOnFailure("暂无数据");
//                }else {
//                    recruitmentListIView.getRecruitmentListSuccess(result.data);
//                }

            }

            @Override
            protected void onFailure(String msg) {
                recruitmentListIView.getRecruimentLsitOnFailure(msg);
            }

            @Override
            protected void onNetworkFailure(String message) {
                recruitmentListIView.onNetworkFailure(message);
            }
        }));
    }




    //获取招聘详情
    public void postRecruitmentDetail(HashMap<String,String>map, RecruitmentDetailIView recruitmentDetailIView){
        subscriber(SFHttp(api().getRecruimentDetail(map)).subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new SFSubscriber<BaseProtocol<RecruimentDetailProtocol>>() {
            @Override
            protected void onSuccess(BaseProtocol<RecruimentDetailProtocol> result) {
                recruitmentDetailIView.getRecruimentDetailOnSuccess(result.data);
            }

            @Override
            protected void onFailure(String msg) {
                recruitmentDetailIView.getRecruimentDetailsOnFailure(msg);
            }

            @Override
            protected void onNetworkFailure(String message) {
                recruitmentDetailIView.onNetworkFailure(message);

            }
        }));

    }


    //获取仪器转让详情
    public void postinstrumentDetail(HashMap<String,String>map, InstrumentDetailIView instrumentDetailIView){
        subscriber(SFHttp(api().getInstrumentDetail(map)).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SFSubscriber<BaseProtocol<InstrumentDetailProtocol>>() {
                    @Override
                    protected void onSuccess(BaseProtocol<InstrumentDetailProtocol> result) {
                        instrumentDetailIView.getInstrumentDetailOnSuccess(result.data);
                    }

                    @Override
                    protected void onFailure(String msg) {
                        instrumentDetailIView.getInstrumenDetailsOnFailure(msg);
                    }

                    @Override
                    protected void onNetworkFailure(String message) {
                        instrumentDetailIView.onNetworkFailure(message);

                    }
                }));

    }




    //获取求职列表
    public void postRequestJobList(HashMap<String,String>map, RequestJobListIView requestJobListIView){
        subscriber(SFHttp(api().getRequestJobList(map)).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SFSubscriber<BaseProtocol<ArrayList<RequestJobListProtocol>>>() {
                    @Override
                    protected void onSuccess(BaseProtocol<ArrayList<RequestJobListProtocol>> result) {
                        requestJobListIView.getRequestJobListSuccess(result.data);
//                        if (result.data==null){
//                            requestJobListIView.getRequestJobLsitOnFailure("暂无数据");
//                        }else {
//                            requestJobListIView.getRequestJobListSuccess(result.data);
//                        }

                    }

                    @Override
                    protected void onFailure(String msg) {
                        requestJobListIView.getRequestJobLsitOnFailure(msg);
                    }

                    @Override
                    protected void onNetworkFailure(String message) {
                        requestJobListIView.onNetworkFailure(message);
                    }
                }));
    }

    //获取仪器转让列表
    public void postInstrumentList(HashMap<String,String>map, InstrumentListIView instrumentListIView){
        subscriber(SFHttp(api().getInstrumentList(map)).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SFSubscriber<BaseProtocol<ArrayList<InstrumentListProtocol>>>() {
                    @Override
                    protected void onSuccess(BaseProtocol<ArrayList<InstrumentListProtocol>> result) {
                        instrumentListIView.getInstrumentListSuccess(result.data);
//                        if (result.data==null){
//                            instrumentListIView.getInstrumentLsitOnFailure("暂无数据");
//                        }else {
//                            instrumentListIView.getInstrumentListSuccess(result.data);
//                        }

                    }

                    @Override
                    protected void onFailure(String msg) {
                        instrumentListIView.getInstrumentLsitOnFailure(msg);
                    }

                    @Override
                    protected void onNetworkFailure(String message) {
                        instrumentListIView.onNetworkFailure(message);
                    }
                }));
    }

}
