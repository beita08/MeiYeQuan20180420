package com.xasfemr.meiyaya.module.mine.IView;

import com.xasfemr.meiyaya.base.IView.IView;

/**
 * Created by Administrator on 2017/12/9.
 */

public interface WithDrawView extends IView{

    void getWithDrawSuccess(String msg);
    void getWithDrawOnfailure(String message);


}
