package com.xasfemr.meiyaya.module.home.IView;

import com.xasfemr.meiyaya.base.IView.IView;
import com.xasfemr.meiyaya.module.college.protocol.CollegeDataProtocol;
import com.xasfemr.meiyaya.module.home.protocol.CourseListProtocol;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/11/29.
 */

public interface CourseListIView extends IView{

    void getCourseListSuccess(ArrayList<CourseListProtocol> CourseListProtocolList);

    void getCourseListFailure(String msg);
}
