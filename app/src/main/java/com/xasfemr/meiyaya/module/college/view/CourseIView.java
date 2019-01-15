package com.xasfemr.meiyaya.module.college.view;

import com.xasfemr.meiyaya.base.IView.IView;
import com.xasfemr.meiyaya.module.college.protocol.CourseProtocolList;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/11/23.
 */

public interface CourseIView extends IView{

    void getCourseListSuccess(CourseProtocolList courseProtocolListList);

    void getCourseListFailure(String msg);

}
