package com.xasfemr.meiyaya.base.presenter;


import okhttp3.MediaType;
import okhttp3.RequestBody;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by lawson on 2017/6/30.
 */

public abstract class BasePresenter extends HttpPresenter {
    private CompositeSubscription  mSubscription = new CompositeSubscription();

    protected void subscriber(Subscription subscription){
        this.mSubscription.add(subscription);

    }

    public void destroy() {
        unSubscribe();
    }


    public void unSubscribe() {
        if (mSubscription != null && !mSubscription.isUnsubscribed()) {
            mSubscription.clear();
        }
    }


    protected RequestBody requestBody(String body){
       RequestBody mBody= RequestBody.create(MediaType.parse("application/json;charset=UTF-8"),body);
        return mBody;
    }


}
