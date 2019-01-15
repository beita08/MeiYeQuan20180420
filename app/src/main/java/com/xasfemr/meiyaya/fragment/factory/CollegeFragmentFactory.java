package com.xasfemr.meiyaya.fragment.factory;

import com.xasfemr.meiyaya.base.fragment.MVPBaseFragment;
import com.xasfemr.meiyaya.module.college.fragment.CollegeCourseFragment;
import com.xasfemr.meiyaya.module.college.fragment.CollegeDataFragment;
import com.xasfemr.meiyaya.module.college.fragment.CollegeEventFragment;
import com.xasfemr.meiyaya.module.college.fragment.CollegeExcellentCourseFragment;
import com.xasfemr.meiyaya.module.college.fragment.CollegeLectureFragment;

import java.util.HashMap;

/**
 * Created by liuxb on 2017年9月11日.
 * 创建CollegeFragment对象，并且对Fragment的对象进行缓存
 */

public class CollegeFragmentFactory {

    private static HashMap<Integer, MVPBaseFragment> sSavedFragment = new HashMap<>();

    public static MVPBaseFragment getFragment(int position) {
        MVPBaseFragment fragment = sSavedFragment.get(position);
        if (fragment == null) {
            switch (position) {
                case 0:
                    fragment = new CollegeExcellentCourseFragment(); //精品课程
                    break;
                case 1:
                    fragment = new CollegeLectureFragment(); //讲师
                    break;
                case 2:
                    fragment = new CollegeCourseFragment(); //直播课程
                    break;
                case 3:
                    fragment = new CollegeDataFragment(); //资料
                    break;
                case 4:
                    fragment = new CollegeEventFragment(); //活动
                    break;
                default:
                    break;
            }

            sSavedFragment.put(position, fragment);
        }

        return fragment;
    }

}
