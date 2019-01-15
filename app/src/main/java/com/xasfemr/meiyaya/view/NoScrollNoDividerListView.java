package com.xasfemr.meiyaya.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

import com.xasfemr.meiyaya.R;

/**
 * Created by sen.luo on 2018/2/9.
 */

public class NoScrollNoDividerListView extends ListView{

    public NoScrollNoDividerListView(Context context) {
        super(context);

    }

    public NoScrollNoDividerListView(Context context, AttributeSet attrs) {
        super(context, attrs);

    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}
