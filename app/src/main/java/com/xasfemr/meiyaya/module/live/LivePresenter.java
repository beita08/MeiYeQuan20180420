package com.xasfemr.meiyaya.module.live;

import com.xasfemr.meiyaya.base.presenter.BasePresenter;
import com.xasfemr.meiyaya.base.protocol.BaseProtocol;
import com.xasfemr.meiyaya.http.SFSubscriber;
import com.xasfemr.meiyaya.module.live.IView.LiveMsgView;
import com.xasfemr.meiyaya.module.live.IView.LivePlayView;
import com.xasfemr.meiyaya.module.live.protocol.LiveMsgProtocol;

import java.util.HashMap;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Administrator on 2017/12/25.
 */

public class LivePresenter extends BasePresenter{


    public void sendMessage(HashMap<String,String> map, LivePlayView livePlayView){
        subscriber(SFHttp(api().liveSendMessage(map)).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
        .subscribe(new SFSubscriber<BaseProtocol<String>>() {
            @Override
            protected void onSuccess(BaseProtocol<String> result) {
                livePlayView.getLiewPlaySendMSGSuccess(result.data);
            }
            @Override
            protected void onFailure(String msg) {
                livePlayView.getLiewPlaySendMSGOnfaile(msg);
            }
            @Override
            protected void onNetworkFailure(String message) {
                livePlayView.onNetworkFailure(message);
            }
        }));
    }



    // 获取直播间所有信息
    public void getLiveMessage(HashMap<String,String> map, LiveMsgView liveMsgView){
        subscriber(SFHttp(api().getLiveMsg(map)).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
        .subscribe(new SFSubscriber<BaseProtocol<LiveMsgProtocol>>() {
            @Override
            protected void onSuccess(BaseProtocol<LiveMsgProtocol> result) {
                liveMsgView.getLiveMsgSuccess(result.data);
            }

            @Override
            protected void onFailure(String msg) {
                liveMsgView.getLiveMsgOnfaile(msg);
            }

            @Override
            protected void onNetworkFailure(String message) {
                liveMsgView.getLiveMsgOnfaile(message);
            }
        }));
    }

}
