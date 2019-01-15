package com.xasfemr.meiyaya.module.college.presenter;

import com.xasfemr.meiyaya.base.presenter.BasePresenter;
import com.xasfemr.meiyaya.base.protocol.BaseProtocol;
import com.xasfemr.meiyaya.http.SFSubscriber;
import com.xasfemr.meiyaya.module.college.protocol.AttentionProtocol;
import com.xasfemr.meiyaya.module.college.protocol.CollegeDataProtocol;
import com.xasfemr.meiyaya.module.college.protocol.CollegeEventProtocol;
import com.xasfemr.meiyaya.module.college.protocol.CourseProtocolList;
import com.xasfemr.meiyaya.module.college.protocol.ExcellentListProtocol;
import com.xasfemr.meiyaya.module.college.protocol.LectureProtocol;
import com.xasfemr.meiyaya.module.college.protocol.PlaybackListProtocol;
import com.xasfemr.meiyaya.module.college.view.AttentionIView;
import com.xasfemr.meiyaya.module.college.view.CollectIView;
import com.xasfemr.meiyaya.module.college.view.CollegeDataIView;
import com.xasfemr.meiyaya.module.college.view.CollegeEventIView;
import com.xasfemr.meiyaya.module.college.view.CourseIView;
import com.xasfemr.meiyaya.module.college.view.ExcellentIView;
import com.xasfemr.meiyaya.module.college.view.LectureIView;
import com.xasfemr.meiyaya.module.college.view.PlaybackIView;
import com.xasfemr.meiyaya.utils.LogUtils;
import com.xasfemr.meiyaya.weight.SFDialog;


import java.util.ArrayList;
import java.util.HashMap;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Administrator on 2017/11/23.
 */

public class CollegePresenter extends BasePresenter{

    //直播列表
    public void getCollegeListData(HashMap<String,String> map,CourseIView courseIView){
        subscriber(SFHttp(api().getCourseList(map)).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
        .subscribe(new SFSubscriber<BaseProtocol<CourseProtocolList>>() {
            @Override
            protected void onSuccess(BaseProtocol<CourseProtocolList> result) {
                courseIView.getCourseListSuccess(result.data);
            }

            @Override
            protected void onFailure(String e) {
                courseIView.getCourseListFailure(e);
            }

            @Override
            protected void onNetworkFailure(String message) {
                courseIView.onNetworkFailure(message);
            }
        }));
    }

    //回放列表
    public void getPlaybackListData(HashMap<String,String> map,PlaybackIView playbackIView){
        subscriber(SFHttp(api().getPlaybackList(map)).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SFSubscriber<BaseProtocol<PlaybackListProtocol>>() {
                    @Override
                    protected void onSuccess(BaseProtocol<PlaybackListProtocol> result) {
                        playbackIView.getPlaybackListSuccess(result.data);
                    }

                    @Override
                    protected void onFailure(String e) {
                        playbackIView.getPlaybackListFailure(e);
                    }

                    @Override
                    protected void onNetworkFailure(String message) {
                        playbackIView.onNetworkFailure(message);
                    }
                }));
    }


    //讲师列表
    public void getLectureListData(HashMap<String,String>map, LectureIView lectureIView){
        subscriber(SFHttp(api().getLectureList(map)).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
        .subscribe(new SFSubscriber<BaseProtocol<ArrayList<LectureProtocol>>>() {
            @Override
            protected void onSuccess(BaseProtocol<ArrayList<LectureProtocol>> result) {
                if (result.data==null){
                    lectureIView.getLectureListFailure("暂无数据");
                }else {
                    lectureIView.getLectureListSuccess(result.data);
                }


            }

            @Override
            protected void onFailure(String msg) {
                lectureIView.getLectureListFailure(msg);
            }

            @Override
            protected void onNetworkFailure(String message) {
                lectureIView.onNetworkFailure(message);
            }
        }));
    }


    //资料列表
    public void getCollegeDataList(CollegeDataIView collegeDataIView){
        subscriber(SFHttp(api().getCollegeDataList()).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
        .subscribe(new SFSubscriber<BaseProtocol<ArrayList<CollegeDataProtocol>>>() {
            @Override
            protected void onSuccess(BaseProtocol<ArrayList<CollegeDataProtocol>> result) {
                collegeDataIView.getCollegeDataListSuccess(result.data);
            }

            @Override
            protected void onFailure(String msg) {
                collegeDataIView.getCollegeDataListFailure(msg);
            }

            @Override
            protected void onNetworkFailure(String message) {
                collegeDataIView.onNetworkFailure(message);
            }
        }));
    }



    //活动列表
    public void getCollegeEventList(CollegeEventIView collegeEventIView){
        subscriber(SFHttp(api().getCollegeEventList()).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
        .subscribe(new SFSubscriber<BaseProtocol<ArrayList<CollegeEventProtocol>>>() {
            @Override
            protected void onSuccess(BaseProtocol<ArrayList<CollegeEventProtocol>> result) {
                collegeEventIView.getCollegeEventListSuccess(result.data);
            }

            @Override
            protected void onFailure(String msg) {
                collegeEventIView.getCollegeEventListFailure(msg);

            }

            @Override
            protected void onNetworkFailure(String message) {
                collegeEventIView.onNetworkFailure(message);

            }
        }));
    }


    //发起关注
    public void getAttention(HashMap<String,String> map,AttentionIView attentionIView){
        subscriber(SFHttp(api().getAttention(map)).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
        .subscribe(new SFSubscriber<BaseProtocol<AttentionProtocol>>() {
            @Override
            protected void onSuccess(BaseProtocol<AttentionProtocol> result) {

                if (result.data!=null){
                    attentionIView.getAttentionSuccess(result.message);

                }else {
                    attentionIView.getAttentionOnFailure(result.message);
                }
            }

            @Override
            protected void onFailure(String msg) {
                attentionIView.getAttentionOnFailure(msg);
            }
            @Override
            protected void onNetworkFailure(String message) {
                attentionIView.onNetworkFailure(message);
            }

        }));
    }


    //取消关注
    public void cancelAttention(HashMap<String,String> map,AttentionIView attentionIView){
        subscriber(SFHttp(api().cancelAttention(map)).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
        .subscribe(new SFSubscriber<BaseProtocol<AttentionProtocol>>() {
            @Override
            protected void onSuccess(BaseProtocol<AttentionProtocol> result) {
                if (result.data!=null){
                    attentionIView.getAttentionSuccess(result.message);

                }else {
                    attentionIView.getAttentionOnFailure(result.message);
                }
            }

            @Override
            protected void onFailure(String msg) {
                attentionIView.getAttentionOnFailure(msg);
            }

            @Override
            protected void onNetworkFailure(String message) {
                attentionIView.onNetworkFailure(message);
            }
        }));
    }


    //是否存在关在状态
    public  void  ifAttention(HashMap<String,String>map,AttentionIView attentionIView){
        subscriber(SFHttp(api().ifAttention(map)).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SFSubscriber<BaseProtocol<AttentionProtocol>>() {
                    @Override
                    protected void onSuccess(BaseProtocol<AttentionProtocol> result) {
                        if (result.data!=null){
                            attentionIView.getAttentionSuccess(result.message);

                        }else {
                            attentionIView.getAttentionOnFailure(result.message);
                        }
                    }

                    @Override
                    protected void onFailure(String msg) {
                        attentionIView.getAttentionOnFailure(msg);
                    }

                    @Override
                    protected void onNetworkFailure(String message) {
                        attentionIView.onNetworkFailure(message);
                    }
                }));
    }



    //精品课程列表
    public void  getExcellentList(HashMap<String,String> map,ExcellentIView excellentIView){
        subscriber(SFHttp(api().getExcellentList(map)).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
        .subscribe(new SFSubscriber<BaseProtocol<ArrayList<ExcellentListProtocol>>>() {
            @Override
            protected void onSuccess(BaseProtocol<ArrayList<ExcellentListProtocol>> result) {
//                excellentIView.getExcellentListSuccess(result.data);
                if (result.data==null){
                    excellentIView.getExcellentListOnFailure("暂无数据");
                }else {
                    excellentIView.getExcellentListSuccess(result.data);
                }


            }

            @Override
            protected void onFailure(String msg) {
                excellentIView.getExcellentListOnFailure(msg);

            }

            @Override
            protected void onNetworkFailure(String message) {
                excellentIView.onNetworkFailure(message);
            }
        }));

    }

    //收藏课程/取消收藏
    public void collectCourse(HashMap<String,String> map, CollectIView collectIView){
        subscriber(SFHttp(api().getCollectData(map)).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
        .subscribe(new SFSubscriber<BaseProtocol<String>>() {
            @Override
            protected void onSuccess(BaseProtocol<String> result) {
                if (result.data!=null){
                    collectIView.getCollectSuccess(result.data);

                }else {
                    collectIView.getCollectOnFailure("出现错误");
                }
            }

            @Override
            protected void onFailure(String msg) {
                collectIView.getCollectOnFailure(msg);
            }

            @Override
            protected void onNetworkFailure(String message) {
                collectIView.onNetworkFailure(message);
            }
        }));
    }




}
