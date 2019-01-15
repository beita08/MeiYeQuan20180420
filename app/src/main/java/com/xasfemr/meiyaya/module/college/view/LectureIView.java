package com.xasfemr.meiyaya.module.college.view;

import com.xasfemr.meiyaya.base.IView.IView;
import com.xasfemr.meiyaya.module.college.protocol.LectureProtocol;
import com.xasfemr.meiyaya.module.college.protocol.PlaybackListProtocol;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/11/23.
 */

public interface LectureIView extends IView{

    void getLectureListSuccess(ArrayList<LectureProtocol> lectureProtocoList);

    void getLectureListFailure(String msg);

}
