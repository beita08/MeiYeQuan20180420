package com.xasfemr.meiyaya.module.live.IView;

import com.xasfemr.meiyaya.base.IView.IView;

/**
 * Created by Administrator on 2017/12/25.
 */

public interface LivePlayView extends IView{

    void getLiewPlaySendMSGSuccess(String msg);
    void getLiewPlaySendMSGOnfaile(String msg);
}
