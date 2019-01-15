package com.xasfemr.meiyaya.fragment.factory;

import com.xasfemr.meiyaya.fragment.BaseFragment;
import com.xasfemr.meiyaya.fragment.FindDynamicFragment;
import com.xasfemr.meiyaya.fragment.FindFragment;
import com.xasfemr.meiyaya.fragment.FindFriendsFragment;

import java.util.HashMap;

/**
 * Description:
 * Company    : shifeier
 * Author     : liuxb
 * Date       : 2017/10/07 0021 11:53
 */

public class MeiYeQuanFragmentFactory {

    private static HashMap<Integer, BaseFragment> sSavedFragment = new HashMap<>();

    public static BaseFragment getFragment(int position) {
        BaseFragment fragment = sSavedFragment.get(position);
        if (fragment == null) {
            switch (position) {
                case 0:
                    fragment = new FindDynamicFragment();
                    break;
                case 1:
                    fragment = new FindFriendsFragment();
                    break;
                case 2:
                    fragment = new FindFragment();
                    break;
                default:
                    break;
            }

            sSavedFragment.put(position, fragment);
        }
        return fragment;
    }
}
