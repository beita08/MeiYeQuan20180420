package com.xasfemr.meiyaya.module.college.view;

import com.xasfemr.meiyaya.base.IView.IView;
import com.xasfemr.meiyaya.module.college.protocol.HomeInterceptionProtocol;
import com.xasfemr.meiyaya.module.college.protocol.HomeRecommendProtocol;

import java.util.ArrayList;

/**
 * Created by sen.luo on 2018/3/13.
 */

public interface HomeGetRecommendIView extends IView{

    void getHomeRecommendOnSuccess(ArrayList<HomeInterceptionProtocol> recommendProtocolArrayList);
    void getHomeRecommendonFailure(String msg);
}
