package com.xasfemr.meiyaya.module.college.protocol;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.xasfemr.meiyaya.base.protocol.BaseProtocol;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/11/23.
 */

public class PlaybackListProtocol extends BaseProtocol {

    public  int pageSize;

//    @Expose
//    @SerializedName("list")
    public ArrayList<PlaybackProtocol> list;

}
