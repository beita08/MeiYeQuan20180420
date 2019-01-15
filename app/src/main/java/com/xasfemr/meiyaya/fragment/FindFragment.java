package com.xasfemr.meiyaya.fragment;


import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.xasfemr.meiyaya.R;
import com.xasfemr.meiyaya.main.MainActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class FindFragment extends BaseFragment {

    private MainActivity mainActivity;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity) getActivity();
    }

    @Override
    public View initView() {
        View view = View.inflate(mainActivity, R.layout.fragment_find, null);

        return view;
    }

    @Override
    public void initData() {

    }


}
