package com.xasfemr.meiyaya.module.home.IView;

import com.xasfemr.meiyaya.base.IView.IView;
import com.xasfemr.meiyaya.module.home.protocol.MemberCourseHotListProtocol;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/11/29.
 */

public interface MemberHotIView extends IView{

    void getMemberHotListSuccess(MemberCourseHotListProtocol memberCourseHotListProtocols);

    void getMemberHotListFailure(String msg);
}
