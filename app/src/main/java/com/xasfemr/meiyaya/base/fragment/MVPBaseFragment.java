package com.xasfemr.meiyaya.base.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xasfemr.meiyaya.view.LoadDataView;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * MVPBaseFragment  封装loadView
 * Created by sen.luo on 2017/11/22.
 */

public abstract class MVPBaseFragment extends Fragment {

    private Unbinder mUnbinder;
    private LoadDataView mLoadView;

    private AlertDialog loginInvalidDialog = null;

    protected abstract int layoutId();

    protected abstract void initView();

    protected abstract void getLoadView(LoadDataView mLoadView);

    protected abstract void initData();

    protected abstract void initPresenter();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        try{
            if(null==mLoadView){
                View view = inflater.inflate(layoutId(), container, false);
                mUnbinder = ButterKnife.bind(this, view);
                mLoadView = new LoadDataView(getActivity(), view);
            }
        }catch (Exception e){
            if(null!=mLoadView){
                ((ViewGroup)mLoadView.getParent()).removeView(mLoadView);
                View view = inflater.inflate(layoutId(), container, false);
                mUnbinder = ButterKnife.bind(this, view);
                mLoadView = new LoadDataView(getActivity(), view);
            }
        }

        return mLoadView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        getLoadView(mLoadView);
        initPresenter();
        initData();
    }

    @Override
    public void onDestroyView() {
        if(null!=mLoadView){
            if(null!=((ViewGroup)mLoadView.getParent())){
                ((ViewGroup)mLoadView.getParent()).removeView(mLoadView);
            }
            mLoadView=null;
        }
        if(null!=mUnbinder){
            mUnbinder.unbind();
        }


        super.onDestroyView();
    }


    //MeFragment,DynamicFragment中重写了此方法
    public void updateRedDotByActivity(String unReadCount) {}
}
