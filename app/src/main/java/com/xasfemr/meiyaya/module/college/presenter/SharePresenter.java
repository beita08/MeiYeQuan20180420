package com.xasfemr.meiyaya.module.college.presenter;

import com.xasfemr.meiyaya.base.presenter.BasePresenter;
import com.xasfemr.meiyaya.base.protocol.BaseProtocol;
import com.xasfemr.meiyaya.http.SFSubscriber;
import com.xasfemr.meiyaya.module.college.view.ShareSuccessIView;
import com.xasfemr.meiyaya.utils.LogUtils;

import java.util.HashMap;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Administrator on 2018/1/5.
 */

public class SharePresenter extends BasePresenter{

    //分享资讯
    public void getNewsShare(HashMap<String,String>map, ShareSuccessIView shareSuccessIView){
        subscriber(SFHttp(api().getNewsShareSuccess(map)).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
        .subscribe(new SFSubscriber<BaseProtocol<Object>>() {
            @Override
            protected void onSuccess(BaseProtocol<Object> result) {
                if (result.status==200){
                    shareSuccessIView.getShareSuccess(result.message);

                }else {
                    shareSuccessIView.getShareOnfaile(result.message);
                }
            }

            @Override
            protected void onFailure(String msg) {
                shareSuccessIView.getShareOnfaile(msg);
            }

            @Override
            protected void onNetworkFailure(String message) {
                shareSuccessIView.onNetworkFailure(message);
            }
        }));
    }


    //分享直播间
    public void getLiveShare(HashMap<String,String>map, ShareSuccessIView shareSuccessIView){
        subscriber(SFHttp(api().getLiveShareSuccess(map)).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SFSubscriber<BaseProtocol<Object>>() {
                    @Override
                    protected void onSuccess(BaseProtocol<Object> result) {
                        if (result.status==200){
                            shareSuccessIView.getShareSuccess(result.message);

                        }else {
                            shareSuccessIView.getShareOnfaile(result.message);
                        }
                    }

                    @Override
                    protected void onFailure(String msg) {
                        shareSuccessIView.getShareOnfaile(msg);
                    }

                    @Override
                    protected void onNetworkFailure(String message) {
                        shareSuccessIView.onNetworkFailure(message);
                    }
                }));
    }

}
