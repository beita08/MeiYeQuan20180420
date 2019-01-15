package com.xasfemr.meiyaya.module.home.IView;

import com.xasfemr.meiyaya.base.IView.IView;
import com.xasfemr.meiyaya.module.home.protocol.HomeBannerProtocol;
import com.xasfemr.meiyaya.module.home.protocol.HomeNewsprotocol;

import java.util.ArrayList;

/**
 * Created by sen.luo on 2018/2/7.
 */

public interface HomeGetNewsIView extends IView{

    void getNewSuccess(ArrayList<HomeNewsprotocol> homeNewsList);
    void getNewsOnFailure(String message);
}
