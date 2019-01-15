package com.xasfemr.meiyaya.module.college.view;

import com.xasfemr.meiyaya.base.IView.IView;
import com.xasfemr.meiyaya.module.college.protocol.CourseProtocolList;
import com.xasfemr.meiyaya.module.college.protocol.PlaybackListProtocol;

/**
 * Created by Administrator on 2017/11/23.
 */

public interface PlaybackIView extends IView{

    void getPlaybackListSuccess(PlaybackListProtocol playbackListProtocol);

    void getPlaybackListFailure(String msg);

}
