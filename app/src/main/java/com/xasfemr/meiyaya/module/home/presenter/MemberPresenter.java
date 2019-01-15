package com.xasfemr.meiyaya.module.home.presenter;

import com.xasfemr.meiyaya.base.presenter.BasePresenter;
import com.xasfemr.meiyaya.base.protocol.BaseProtocol;
import com.xasfemr.meiyaya.http.SFSubscriber;
import com.xasfemr.meiyaya.module.home.IView.MemberHotIView;
import com.xasfemr.meiyaya.module.home.protocol.MemberCourseHotListProtocol;

import java.util.ArrayList;
import java.util.HashMap;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Administrator on 2017/11/29.
 */

public class MemberPresenter extends BasePresenter{


    //课程--直播列表
    public void getMemberHotList(HashMap<String,String>map, MemberHotIView memberHotIView){
        subscriber(SFHttp(api().getMemberHotList(map)).subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new SFSubscriber<BaseProtocol<MemberCourseHotListProtocol>>() {
            @Override
            protected void onSuccess(BaseProtocol<MemberCourseHotListProtocol> result) {

                memberHotIView.getMemberHotListSuccess(result.data);
//                if (result.data.list.size()==0){
//                    memberHotIView.getMemberHotListFailure("暂无数据");
//                }else {
//                    memberHotIView.getMemberHotListSuccess(result.data);
//                }

            }

            @Override
            protected void onFailure(String msg) {
                memberHotIView.getMemberHotListFailure(msg);

            }

            @Override
            protected void onNetworkFailure(String message) {
                memberHotIView.onNetworkFailure(message);

            }
        }));
    }

    //课程--回放列表
    public void getMemberPlaybackList(HashMap<String,String>map, MemberHotIView memberHotIView){
        subscriber(SFHttp(api().getMemberPlaybackList(map)).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SFSubscriber<BaseProtocol<MemberCourseHotListProtocol>>() {
                    @Override
                    protected void onSuccess(BaseProtocol<MemberCourseHotListProtocol> result) {
                        memberHotIView.getMemberHotListSuccess(result.data);
//                        if (result.data.list.size()==0){
//                            memberHotIView.getMemberHotListFailure("暂无数据");
//                        }else {
//
//
//                        }
                    }

                    @Override
                    protected void onFailure(String msg) {
                        memberHotIView.getMemberHotListFailure(msg);

                    }

                    @Override
                    protected void onNetworkFailure(String message) {
                        memberHotIView.onNetworkFailure(message);

                    }
                }));
    }



    //会员--直播列表
    public void getMemberLiveList(HashMap<String,String> map,MemberHotIView memberHotIView){
        subscriber(SFHttp(api().getMemberLiveList(map)).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SFSubscriber<BaseProtocol<MemberCourseHotListProtocol>>() {
                    @Override
                    protected void onSuccess(BaseProtocol<MemberCourseHotListProtocol> result) {
                        memberHotIView.getMemberHotListSuccess(result.data);

                    }

                    @Override
                    protected void onFailure(String msg) {
                        memberHotIView.getMemberHotListFailure(msg);
                    }

                    @Override
                    protected void onNetworkFailure(String message) {
                        memberHotIView.onNetworkFailure(message);
                    }
                }));

    }

    //会员回放列表
    public void getMemberPlayback(HashMap<String,String> map,MemberHotIView memberHotIView){
        subscriber(SFHttp(api().getMemberbackList(map)).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SFSubscriber<BaseProtocol<MemberCourseHotListProtocol>>() {
                    @Override
                    protected void onSuccess(BaseProtocol<MemberCourseHotListProtocol> result) {
                        memberHotIView.getMemberHotListSuccess(result.data);
//                        if (result.data.list.size()==0){
//                            memberHotIView.getMemberHotListFailure("暂无数据");
//                        }else {
//
//                        }


                    }

                    @Override
                    protected void onFailure(String msg) {
                        memberHotIView.getMemberHotListFailure(msg);
                    }

                    @Override
                    protected void onNetworkFailure(String message) {
                        memberHotIView.onNetworkFailure(message);
                    }
                }));

    }
}
