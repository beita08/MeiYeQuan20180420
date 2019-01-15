package com.xasfemr.meiyaya.module.home.IView;

import com.xasfemr.meiyaya.base.IView.IView;
import com.xasfemr.meiyaya.module.home.protocol.RecruitmentListProtocol;

import java.util.ArrayList;

/**
 * Created by sen.luo on 2018/3/1.
 */

public interface RecruitmentListIView extends IView{

    void getRecruitmentListSuccess(ArrayList<RecruitmentListProtocol> protocolArrayList);
    void getRecruimentLsitOnFailure(String msg);
}
