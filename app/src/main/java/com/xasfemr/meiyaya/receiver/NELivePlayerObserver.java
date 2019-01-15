package com.xasfemr.meiyaya.receiver;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by netease on 17/9/4.
 */

public class NELivePlayerObserver {

    private static class InstanceHolder {
        public final static NELivePlayerObserver instance = new NELivePlayerObserver();
    }

    public static NELivePlayerObserver getInstance() {
        return InstanceHolder.instance;
    }

    private List<Observer<Void>> observers = new ArrayList<>(1); // 与本地电话互斥的挂断监听

    public void onReceive() {
        ObserverUtils.notifyObservers(observers, null);
    }

    public void observeNELivePlayerObserver(Observer<Void> observer, boolean register) {
        ObserverUtils.registerObservers(this.observers, observer, register);
    }

}
