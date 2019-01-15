package com.xasfemr.meiyaya.module.live.IView;

import com.xasfemr.meiyaya.base.IView.IView;
import com.xasfemr.meiyaya.module.live.protocol.LiveMsgProtocol;

/**
 * Created by Administrator on 2017/12/25.
 */

public interface LiveMsgView extends IView{

    void getLiveMsgSuccess(LiveMsgProtocol liveMsgProtocol);
    void getLiveMsgOnfaile(String msg);
}
