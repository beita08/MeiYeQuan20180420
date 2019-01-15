package com.xasfemr.meiyaya.module.home.IView;

import com.xasfemr.meiyaya.base.IView.IView;
import com.xasfemr.meiyaya.module.home.protocol.PostProtocol;

/**
 * Created by sen.luo on 2018/3/1.
 */

public interface PostFilterListIView extends IView{

    void getFilterListSuccess(PostProtocol postProtocol);
    void getFilterListOnFailure(String msg);
}
