package com.xasfemr.meiyaya.module.mine.presenter;

import com.xasfemr.meiyaya.base.presenter.BasePresenter;
import com.xasfemr.meiyaya.base.protocol.BaseProtocol;
import com.xasfemr.meiyaya.http.SFSubscriber;
import com.xasfemr.meiyaya.module.mine.IView.WithDrawView;

import java.util.HashMap;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Administrator on 2017/12/9.
 */

public class MinePresenter extends BasePresenter{



    public void getWithDraw(HashMap<String,String> map, WithDrawView withDrawView){


        subscriber(SFHttp(api().getWithDraw(map)).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
        .subscribe(new SFSubscriber<BaseProtocol<String>>() {
            @Override
            protected void onSuccess(BaseProtocol<String> result) {

                if (result.data.equals("error")){
                    withDrawView.getWithDrawOnfailure(result.message);
                }else {
                    withDrawView.getWithDrawSuccess(result.data);
                }

            }

            @Override
            protected void onFailure(String msg) {
                withDrawView.getWithDrawOnfailure(msg);
            }

            @Override
            protected void onNetworkFailure(String message) {
                withDrawView.onNetworkFailure(message);
            }
        }));
    }
}
