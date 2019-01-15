package com.xasfemr.meiyaya.fragment.factory;

import com.xasfemr.meiyaya.fragment.BaseFragment;
import com.xasfemr.meiyaya.fragment.MyMessagePrivateFragment;
import com.xasfemr.meiyaya.fragment.MyMessagePublicFragment;

import java.util.HashMap;

/**
 * Description:
 * Company    : shifeier
 * Author     : liuxb
 * Date       : 2017/9/21 0021 11:53
 */

public class MessageFragmentFactory {

    private static HashMap<Integer, BaseFragment> sSavedFragment = new HashMap<>();

    public static BaseFragment getFragment(int position) {
        BaseFragment fragment = sSavedFragment.get(position);
        if (fragment == null) {
            switch (position) {
                case 0:
                    fragment = new MyMessagePrivateFragment();
                    break;
                case 1:
                    fragment = new MyMessagePublicFragment();
                    break;
                default:
                    break;
            }

            sSavedFragment.put(position, fragment);
        }
        return fragment;
    }
}
