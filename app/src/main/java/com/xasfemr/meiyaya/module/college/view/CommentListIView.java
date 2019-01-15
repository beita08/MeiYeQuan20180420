package com.xasfemr.meiyaya.module.college.view;

import com.xasfemr.meiyaya.base.IView.IView;
import com.xasfemr.meiyaya.module.college.protocol.CommentProttocol;

import java.util.ArrayList;

/**
 * Created by Administrator on 2018/1/23.
 */

public interface CommentListIView extends IView{

    void getCommentListSuccess(ArrayList<CommentProttocol> commentProttocolList);
    void getCommentListOnFailure(String msg);

}
