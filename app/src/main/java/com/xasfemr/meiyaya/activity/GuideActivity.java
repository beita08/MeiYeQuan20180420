package com.xasfemr.meiyaya.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.xasfemr.meiyaya.R;
import com.xasfemr.meiyaya.main.BaseActivity;
import com.xasfemr.meiyaya.main.MainActivity;
import com.xasfemr.meiyaya.utils.SPUtils;

public class GuideActivity extends BaseActivity {

    private int[] mResIds = new int[]{R.drawable.guide_1, R.drawable.guide_2, R.drawable.guide_3};

    private ViewPager viewPager;
    private TextView  tvStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);

        viewPager = (ViewPager) findViewById(R.id.viewPager);
        tvStart = (TextView) findViewById(R.id.tv_start);

        viewPager.setAdapter(new GuideAdapter());
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            @Override
            public void onPageSelected(int position) {
                if(position == mResIds.length - 1) {
                    tvStart.setVisibility(View.VISIBLE);
                } else {
                    tvStart.setVisibility(View.GONE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {}
        });

        tvStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //1、打开主界面
                Intent intent = new Intent(GuideActivity.this, MainActivity.class);
                //2、将isFirstIn的值改为false
                SPUtils.putboolean(GuideActivity.this, "isFirstIn", false);
                //3、将当前界面结束
                finish();
                startActivity(intent);
            }
        });
    }


    private class GuideAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return mResIds.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            //1、创建View对象,设置相关的属性
            ImageView iv = new ImageView(GuideActivity.this);
            iv.setBackgroundResource(mResIds[position]);
            //2、将View对象添加到container中
            container.addView(iv);
            //3、将view对象返回
            return iv;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

    }
}
