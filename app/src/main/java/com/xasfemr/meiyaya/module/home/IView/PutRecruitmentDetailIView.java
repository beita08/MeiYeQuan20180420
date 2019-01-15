package com.xasfemr.meiyaya.module.home.IView;

import com.xasfemr.meiyaya.base.IView.IView;

/**
 * Created by sen.luo on 2018/3/5.
 */

public interface PutRecruitmentDetailIView extends IView{

    void getPutRecruitmentDetailOnSuccess(String message);
    void getPutRecruitmentDetailOnFailure(String message);
}
