package com.xasfemr.meiyaya.module.home.presenter;

import com.xasfemr.meiyaya.base.presenter.BasePresenter;
import com.xasfemr.meiyaya.base.protocol.BaseProtocol;
import com.xasfemr.meiyaya.http.SFSubscriber;
import com.xasfemr.meiyaya.module.college.protocol.CourseProtocolList;
import com.xasfemr.meiyaya.module.college.protocol.HomeInterceptionProtocol;
import com.xasfemr.meiyaya.module.college.protocol.HomeRecommendProtocol;
import com.xasfemr.meiyaya.module.college.protocol.HotCourseOrMemberProtocol;
import com.xasfemr.meiyaya.module.college.protocol.PlaybackListProtocol;
import com.xasfemr.meiyaya.module.college.view.CourseHotIView;
import com.xasfemr.meiyaya.module.college.view.CourseIView;
import com.xasfemr.meiyaya.module.college.view.HomeGetRecommendIView;
import com.xasfemr.meiyaya.module.college.view.PlaybackIView;
import com.xasfemr.meiyaya.module.home.IView.CourseListIView;
import com.xasfemr.meiyaya.module.home.IView.HomeGetBannerIView;
import com.xasfemr.meiyaya.module.home.IView.HomeGetNewsIView;
import com.xasfemr.meiyaya.module.home.protocol.CourseListProtocol;
import com.xasfemr.meiyaya.module.home.protocol.HomeBannerProtocol;
import com.xasfemr.meiyaya.module.home.protocol.HomeNewsprotocol;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Administrator on 2017/11/29.
 */

public class HomePresenter extends BasePresenter{



    //课程列表
    public void getCourseListData(CourseListIView courseListIView){

        subscriber(SFHttp(api().getCourseDataList()).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
        .subscribe(new SFSubscriber<BaseProtocol<ArrayList<CourseListProtocol>>>() {
            @Override
            protected void onSuccess(BaseProtocol<ArrayList<CourseListProtocol>> result) {

                courseListIView.getCourseListSuccess(result.data);

            }

            @Override
            protected void onFailure(String msg) {
                courseListIView.getCourseListFailure(msg);
            }

            @Override
            protected void onNetworkFailure(String message) {
                courseListIView.onNetworkFailure(message);

            }
        }));

    }


    //热门课程更多
    public void getHotCourseMoreLsit(HashMap<String,String> map,CourseHotIView courseHotIView){
        subscriber(SFHttp(api().getHotCourseMoreList(map)).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SFSubscriber<BaseProtocol<HotCourseOrMemberProtocol>>() {
                    @Override
                    protected void onSuccess(BaseProtocol<HotCourseOrMemberProtocol> result) {
                        if (result.data==null||result.data.list.size()==0||result.data.list==null){
                            courseHotIView.getCourseListFailure("暂无数据");
                        }else {
                            courseHotIView.getCourseListSuccess(result.data);
                        }

//                        courseHotIView.getCourseListSuccess(result.data);
                    }

                    @Override
                    protected void onFailure(String msg) {
                        courseHotIView.getCourseListFailure(msg);
                    }

                    @Override
                    protected void onNetworkFailure(String message) {
                        courseHotIView.onNetworkFailure(message);
                    }
                }));
    }

    //会员课程更多
    public void getMemberMoreList(HashMap<String,String> map, CourseHotIView courseHotIView){
        subscriber(SFHttp(api().getMemberMoreList(map)).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SFSubscriber<BaseProtocol<HotCourseOrMemberProtocol>>() {
                    @Override
                    protected void onSuccess(BaseProtocol<HotCourseOrMemberProtocol> result) {

                        if (result.data==null||result.data.list.size()==0||result.data.list==null){
                            courseHotIView.getCourseListFailure("暂无数据");
                        }else {
                            courseHotIView.getCourseListSuccess(result.data);
                        }

//                        courseHotIView.getCourseListSuccess(result.data);

                    }

                    @Override
                    protected void onFailure(String msg) {
                        courseHotIView.getCourseListFailure(msg);
                    }

                    @Override
                    protected void onNetworkFailure(String message) {
                        courseHotIView.onNetworkFailure(message);
                    }
                }));

    }



    //获取首页Banner
    public void getHomeBannerData(HomeGetBannerIView homeGetBannerIView){
        subscriber(SFHttp(api().getHomeBanner()).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
        .subscribe(new SFSubscriber<BaseProtocol<HomeBannerProtocol>>() {
            @Override
            protected void onSuccess(BaseProtocol<HomeBannerProtocol> result) {
                if (result.data==null ||result.data.banner.size()==0){
                    homeGetBannerIView.getBannerOnFailure("暂无数据");

                }else {
                    homeGetBannerIView.getBannerSuccess(result.data);
                }


            }

            @Override
            protected void onFailure(String msg) {
                homeGetBannerIView.getBannerOnFailure(msg);
            }

            @Override
            protected void onNetworkFailure(String message) {
                homeGetBannerIView.onNetworkFailure(message);
            }
        }));
    }


    //获取首页资讯

    public void getHomeNewsData(HomeGetNewsIView homeGetNewsIView){
        subscriber(SFHttp(api().getHomeNews()).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
        .subscribe(new SFSubscriber<BaseProtocol<ArrayList<HomeNewsprotocol>>>() {
            @Override
            protected void onSuccess(BaseProtocol<ArrayList<HomeNewsprotocol>> result) {
                if (result.data==null ||result.data.size()==0){
                    homeGetNewsIView.getNewsOnFailure("暂无资讯数据");

                }else {
                    homeGetNewsIView.getNewSuccess(result.data);
                }

            }

            @Override
            protected void onFailure(String msg) {
                homeGetNewsIView.getNewsOnFailure(msg);
            }

            @Override
            protected void onNetworkFailure(String message) {
                homeGetNewsIView.getNewsOnFailure(message);

            }
        }));
    }


    //获取首页推荐
    public void getHomeRecommendData(Map<String,String> map, HomeGetRecommendIView homeGetRecommendIView){
        subscriber(SFHttp(api().getHomeRecommendList(map)).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
        .subscribe(new SFSubscriber<BaseProtocol<ArrayList<HomeInterceptionProtocol>>>() {
            @Override
            protected void onSuccess(BaseProtocol<ArrayList<HomeInterceptionProtocol>> result) {

                if (result.data==null ||result.data.size()==0){
                    homeGetRecommendIView.getHomeRecommendonFailure("推荐列表获取失败，请刷新再试");
                }else {
                    homeGetRecommendIView.getHomeRecommendOnSuccess(result.data);
                }

            }

            @Override
            protected void onFailure(String msg) {
                homeGetRecommendIView.getHomeRecommendonFailure(msg);
            }

            @Override
            protected void onNetworkFailure(String message) {
                homeGetRecommendIView.onNetworkFailure(message);

            }
        }));
    }

}
