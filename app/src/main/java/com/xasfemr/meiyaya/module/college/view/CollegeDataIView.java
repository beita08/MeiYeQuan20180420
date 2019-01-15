package com.xasfemr.meiyaya.module.college.view;

import com.xasfemr.meiyaya.base.IView.IView;
import com.xasfemr.meiyaya.module.college.protocol.CollegeDataProtocol;
import com.xasfemr.meiyaya.module.college.protocol.LectureProtocol;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/11/23.
 */

public interface CollegeDataIView extends IView{

    void getCollegeDataListSuccess(ArrayList<CollegeDataProtocol> collegeDataProtocols);

    void getCollegeDataListFailure(String msg);

}
