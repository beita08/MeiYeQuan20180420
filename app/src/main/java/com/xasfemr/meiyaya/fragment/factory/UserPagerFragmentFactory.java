package com.xasfemr.meiyaya.fragment.factory;

import com.xasfemr.meiyaya.fragment.BaseFragment;
import com.xasfemr.meiyaya.fragment.UserAboutMeFragment;
import com.xasfemr.meiyaya.fragment.UserCourseFragment;

import java.util.HashMap;

/**
 * Description:
 * Company    : shifeier
 * Author     : liuxb
 * Date       : 2017/9/29 0021 11:53
 */

public class UserPagerFragmentFactory {

    private static HashMap<Integer, BaseFragment> sSavedFragment = new HashMap<>();

    public static BaseFragment getFragment(int position) {
        BaseFragment fragment = sSavedFragment.get(position);
        if (fragment == null) {
            switch (position) {
                case 0:
                    fragment = new UserCourseFragment();
                    break;
                /*case 1:    //Version1.0 取消动态
                    fragment = new UserDynamicFragment();
                    break;*/
                case 1:
                    fragment = new UserAboutMeFragment();
                    break;
                default:
                    break;
            }
            sSavedFragment.put(position, fragment);
        }
        return fragment;
    }
}