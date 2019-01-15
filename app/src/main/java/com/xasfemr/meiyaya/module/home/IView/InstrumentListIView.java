package com.xasfemr.meiyaya.module.home.IView;

import com.xasfemr.meiyaya.base.IView.IView;
import com.xasfemr.meiyaya.module.home.protocol.InstrumentListProtocol;
import com.xasfemr.meiyaya.module.home.protocol.RequestJobListProtocol;

import java.util.ArrayList;

/**
 * Created by sen.luo on 2018/3/1.
 */

public interface InstrumentListIView extends IView{

    void getInstrumentListSuccess(ArrayList<InstrumentListProtocol> instrumentListProtocols);
    void getInstrumentLsitOnFailure(String msg);
}
