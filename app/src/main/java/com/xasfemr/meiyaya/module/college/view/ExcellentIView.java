package com.xasfemr.meiyaya.module.college.view;

import com.xasfemr.meiyaya.base.IView.IView;
import com.xasfemr.meiyaya.module.college.protocol.ExcellentListProtocol;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/11/30.
 */

public interface ExcellentIView extends IView{
    void getExcellentListSuccess(ArrayList<ExcellentListProtocol> excellentListProtocols);
    void getExcellentListOnFailure(String message);

}
