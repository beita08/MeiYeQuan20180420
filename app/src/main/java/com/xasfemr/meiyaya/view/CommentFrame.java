package com.xasfemr.meiyaya.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.IBinder;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.xasfemr.meiyaya.R;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Description:
 * Company    : shifeier
 * Author     : liuxb
 * Date       : 2017/9/15 0015 14:34
 */

public class CommentFrame {
    public static Activity mContext;
    public static liveCommentResult liveCommentResult = null;
    public static View              commentView       = null;

    public static PopupWindow commentPopup = null;
    public static String      result       = "";

    private static EditText etContentAdd;
    private static TextView tvContentSend;

    public static void liveCommentEdit(final Activity context, final View view, final liveCommentResult commentResult) {

        mContext = context;
        liveCommentResult = commentResult;

        if (commentView == null) {  ;
            commentView = context.getLayoutInflater().inflate(R.layout.layout_input_frame, null);
        }
        if (commentPopup == null) {
            // 创建一个PopuWidow对象
            commentPopup = new PopupWindow(commentView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        }
        // 设置动画 commentPopup.setAnimationStyle(R.style.popWindow_animation_in2out);
        // 使其聚集 ，要想监听菜单里控件的事件就必须要调用此方法
        commentPopup.setFocusable(true);
        // 设置允许在外点击消失
        commentPopup.setOutsideTouchable(true);
        // 设置背景，这个是为了点击“返回Back”也能使其消失，并且并不会影响你的背景
        commentPopup.setBackgroundDrawable(new BitmapDrawable());
        //必须加这两行，不然不会显示在键盘上方
        commentPopup.setSoftInputMode(PopupWindow.INPUT_METHOD_NEEDED);
        commentPopup.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        // PopupWindow的显示及位置设置
        commentPopup.showAtLocation(view, Gravity.BOTTOM, 0, 0);

        etContentAdd = (EditText) commentView.findViewById(R.id.et_content_add);
        tvContentSend = (TextView) commentView.findViewById(R.id.tv_content_send);


        //这是布局中发送按钮的监听
        tvContentSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                result = etContentAdd.getText().toString().trim();
                if (liveCommentResult != null && result.length() != 0) {
                    //把数据传出去
                    liveCommentResult.onResult(true, result);
                    //关闭popup
                    //commentPopup.dismiss();
                    etContentAdd.setText("");
                }
            }
        });
        //设置popup关闭时要做的操作
        commentPopup.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                System.out.println("popupwindow消失了!");
                //Toast.makeText(context, "popupwindow消失了!", Toast.LENGTH_SHORT).show();
                hideSoftInput(context, etContentAdd.getWindowToken());
                etContentAdd.setText("");
            }
        });
        //显示软键盘
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                //此方法就不提供了，网上一大推
                showKeyboard(etContentAdd);
            }
        }, 200);
    }

    private static void hideSoftInput(Activity context, IBinder windowToken) {
        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            System.out.println("----------------隐藏软键盘---------");
            imm.hideSoftInputFromWindow(windowToken, 0);
            //imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
        }
    }

    private static void showKeyboard(final EditText etContent) {

        final InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            mContext.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    etContent.requestFocus();
                    imm.showSoftInput(etContent, 0);
                }
            });
        }
    }

    /**
     * 发送评论回调
     */
    public interface liveCommentResult {
        void onResult(boolean confirmed, String comment);
    }

}
