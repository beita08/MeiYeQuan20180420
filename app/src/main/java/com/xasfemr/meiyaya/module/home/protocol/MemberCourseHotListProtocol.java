package com.xasfemr.meiyaya.module.home.protocol;

import com.xasfemr.meiyaya.base.protocol.BaseProtocol;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/11/29.
 */

public class MemberCourseHotListProtocol extends BaseProtocol{

    public int pageSize;
    public int totalRecords;
    public int currentPage;
    public int pageNum;

    public ArrayList<MemberCourseHotProtocol>list;


}
