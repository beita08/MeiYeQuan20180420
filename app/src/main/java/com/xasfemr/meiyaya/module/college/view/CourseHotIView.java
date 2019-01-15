package com.xasfemr.meiyaya.module.college.view;

import com.xasfemr.meiyaya.base.IView.IView;
import com.xasfemr.meiyaya.module.college.protocol.CourseProtocolList;
import com.xasfemr.meiyaya.module.college.protocol.HotCourseOrMemberProtocol;

/**
 * Created by Administrator on 2017/11/23.
 */

public interface CourseHotIView extends IView{

    void getCourseListSuccess(HotCourseOrMemberProtocol hotCourseOrMemberProtocols);

    void getCourseListFailure(String msg);

}
