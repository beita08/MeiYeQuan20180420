package com.xasfemr.meiyaya.module.home.IView;

import com.xasfemr.meiyaya.base.IView.IView;
import com.xasfemr.meiyaya.module.home.protocol.InstrumentDetailProtocol;
import com.xasfemr.meiyaya.module.home.protocol.RecruimentDetailProtocol;

/**
 * Created by sen.luo on 2018/3/5.
 */

public interface InstrumentDetailIView extends IView{

    void getInstrumentDetailOnSuccess(InstrumentDetailProtocol instrumentDetailProtocol);

    void getInstrumenDetailsOnFailure(String msg);
}
