package com.xasfemr.meiyaya.module.college.view;

import com.xasfemr.meiyaya.base.IView.IView;
import com.xasfemr.meiyaya.module.college.protocol.AttentionProtocol;

/**
 * Created by Administrator on 2017/11/30.
 */

public interface  AttentionIView extends IView{
    void getAttentionSuccess(String message);
    void getAttentionOnFailure(String message);

}
