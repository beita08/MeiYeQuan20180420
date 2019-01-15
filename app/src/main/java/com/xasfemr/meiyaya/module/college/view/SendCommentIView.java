package com.xasfemr.meiyaya.module.college.view;

import com.xasfemr.meiyaya.base.IView.IView;

/**
 * Created by Administrator on 2018/1/24.
 */

public interface SendCommentIView extends IView{
    void sendCommentSuccess(String msg);
    void sendCommentOnFailre(String msg);
}
