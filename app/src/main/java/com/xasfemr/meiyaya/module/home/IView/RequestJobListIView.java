package com.xasfemr.meiyaya.module.home.IView;

import com.xasfemr.meiyaya.base.IView.IView;
import com.xasfemr.meiyaya.module.home.protocol.RecruitmentListProtocol;
import com.xasfemr.meiyaya.module.home.protocol.RequestJobListProtocol;

import java.util.ArrayList;

/**
 * Created by sen.luo on 2018/3/1.
 */

public interface RequestJobListIView extends IView{

    void getRequestJobListSuccess(ArrayList<RequestJobListProtocol> requestJobListProtocols);
    void getRequestJobLsitOnFailure(String msg);
}
