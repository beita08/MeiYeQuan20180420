package com.xasfemr.meiyaya.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

import com.xasfemr.meiyaya.R;

/**
 * Created by sen.luo on 2018/2/9.
 */

public class NoScrollListView extends ListView{

    public NoScrollListView(Context context) {
        super(context);
        this.setDivider(context.getResources().getDrawable(R.color.textcolor_f0eded));
        this.setDividerHeight(1);
    }

    public NoScrollListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setDivider(context.getResources().getDrawable(R.color.textcolor_f0eded));
        this.setDividerHeight(1);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}
