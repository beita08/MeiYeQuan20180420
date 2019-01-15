package com.xasfemr.meiyaya.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.lang.reflect.Field;

/**
 * Description: 所有fragment的基类
 * Company    : shifeier
 * Author     : liuxb
 * Date       : 2017/9/4 0004 10:49
 */

public abstract class BaseFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return initView();// 创建布局;
    }

    /**
     * 填充布局初始化fragment中的view
     * @return 返回fragment所包装的view对象
     */
    public abstract View initView();

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
    }

    /**
     * 初始化fragment中的数据
     */
    public abstract void initData();

    @Override
    public void onDetach() {
        super.onDetach();
        try {
            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    //MeFragment,DynamicFragment中重写了此方法
    public void updateRedDotByActivity(String unReadCount) {}
}
