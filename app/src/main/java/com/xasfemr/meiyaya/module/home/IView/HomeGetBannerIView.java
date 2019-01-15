package com.xasfemr.meiyaya.module.home.IView;

import com.xasfemr.meiyaya.base.IView.IView;
import com.xasfemr.meiyaya.module.home.protocol.HomeBannerProtocol;

import java.util.ArrayList;

/**
 * Created by sen.luo on 2018/2/7.
 */

public interface HomeGetBannerIView extends IView{

    void getBannerSuccess(HomeBannerProtocol homeBanner);
    void getBannerOnFailure(String message);
}
