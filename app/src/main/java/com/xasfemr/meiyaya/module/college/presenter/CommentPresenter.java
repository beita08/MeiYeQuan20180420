package com.xasfemr.meiyaya.module.college.presenter;

import com.xasfemr.meiyaya.base.presenter.BasePresenter;
import com.xasfemr.meiyaya.base.protocol.BaseProtocol;
import com.xasfemr.meiyaya.http.SFSubscriber;
import com.xasfemr.meiyaya.module.college.protocol.CommentProttocol;
import com.xasfemr.meiyaya.module.college.view.CommentListIView;
import com.xasfemr.meiyaya.module.college.view.SendCommentIView;

import java.util.ArrayList;
import java.util.HashMap;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Administrator on 2018/1/23.
 */

public class CommentPresenter extends BasePresenter{

    //获取评论列表
    public void getCommentList(HashMap<String,String>map, CommentListIView commentListIView){

        subscriber(SFHttp(api().getCommentList(map)).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
        .subscribe(new SFSubscriber<BaseProtocol<ArrayList<CommentProttocol>>>() {
            @Override
            protected void onSuccess(BaseProtocol<ArrayList<CommentProttocol>> result) {
//                if (result.data==null){
//                    commentListIView.getCommentListOnFailure(result.message);
//                }else {
//                    commentListIView.getCommentListSuccess(result.data);
//                }

                commentListIView.getCommentListSuccess(result.data);
            }

            @Override
            protected void onFailure(String msg) {
                commentListIView.getCommentListOnFailure(msg);
            }

            @Override
            protected void onNetworkFailure(String message) {
                commentListIView.onNetworkFailure(message);
            }
        }));

    }


    //发表评论
    public void sendComment(HashMap<String,String>map, SendCommentIView sendCommentIView){
        subscriber(SFHttp(api().sendComment(map)).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
        .subscribe(new SFSubscriber<BaseProtocol<Object>>() {
            @Override
            protected void onSuccess(BaseProtocol<Object> result) {
                if (result.code==200){
                    sendCommentIView.sendCommentSuccess(result.message);

                }else {
                    sendCommentIView.sendCommentOnFailre(result.message);
                }
            }

            @Override
            protected void onFailure(String msg) {
                sendCommentIView.sendCommentOnFailre(msg);
            }

            @Override
            protected void onNetworkFailure(String message) {
                sendCommentIView.onNetworkFailure(message);
            }
        }));
    }

    //删除评论
    public void deleteComment(HashMap<String,String>map,SendCommentIView sendCommentIView){
        subscriber(SFHttp(api().deleteComment(map)).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
        .subscribe(new SFSubscriber<BaseProtocol<Object>>() {
            @Override
            protected void onSuccess(BaseProtocol<Object> result) {
                if (result.code==204){
                    sendCommentIView.sendCommentOnFailre(result.message);
                }else {
                    sendCommentIView.sendCommentSuccess(result.message);
                }
            }

            @Override
            protected void onFailure(String msg) {
                sendCommentIView.sendCommentOnFailre(msg);
            }

            @Override
            protected void onNetworkFailure(String message) {
                sendCommentIView.onNetworkFailure(message);
            }
        }));
    }

}
