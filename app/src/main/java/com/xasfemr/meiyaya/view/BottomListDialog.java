package com.xasfemr.meiyaya.view;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xasfemr.meiyaya.R;
import com.xasfemr.meiyaya.utils.UiUtils;

import java.util.List;

/**
 * Description:
 * Company    : shifeier
 * Author     : liuxb
 * Date       : 2018/3/15 0015 9:30
 */

public class BottomListDialog extends Dialog {

    private TextView     tvDialogTitle;
    private RecyclerView rvDialogList;

    /*
    * Context context: 一个Activity的 context
    * String oldInfo : 该条目原来的值
    * String title   : Dialog需要展示的标题
    * List<String> showList:Dialog需要展示的列表
    * */
    public BottomListDialog(@NonNull Context context, String oldInfo, String title, List<String> showList) {
        super(context, R.style.ShareDialogStyle);   //指定dialog的样式
        setContentView(R.layout.dialog_bottom_list);//给dialog设置布局

        //显示在屏幕正下方 原理: 修改dialog所在窗口Window的位置, dialog随窗口显示
        Window window = getWindow();//获取dialog所在的窗口对象
        WindowManager.LayoutParams attributes = window.getAttributes();//获取当前窗口的属性(布局参数)
        attributes.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;//靠下居中显示
        attributes.width = WindowManager.LayoutParams.MATCH_PARENT;     //宽度
        window.setAttributes(attributes);//重新设置布局参数

        tvDialogTitle = (TextView) findViewById(R.id.tv_dialog_title);
        tvDialogTitle.setText(title);
        rvDialogList = (RecyclerView) findViewById(R.id.rv_dialog_list);
        rvDialogList.setLayoutManager(new LinearLayoutManager(context));
        rvDialogList.setAdapter(new DialogAdapter(context, oldInfo, showList));

        int recyclerViewHeight;
        if (showList.size() >= 6) {
            recyclerViewHeight = 246;
        } else {
            recyclerViewHeight = 41 * showList.size();
        }
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, UiUtils.dp2px(context, recyclerViewHeight));
        rvDialogList.setLayoutParams(params);
    }

    private class DialogAdapter extends RecyclerView.Adapter<DialogHolder> {
        private Context  mContext;
        private String   mOldInfo;
        private List<String> mList;

        public DialogAdapter(Context context, String oldInfo, List<String> showList) {
            this.mContext = context;
            this.mOldInfo = oldInfo;
            this.mList = showList;
        }

        @Override
        public DialogHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = View.inflate(mContext, R.layout.item_bottom_list_dialog, null);
            DialogHolder dialogHolder = new DialogHolder(view);
            return dialogHolder;
        }

        @Override
        public void onBindViewHolder(DialogHolder holder, int position) {
            holder.tvDialogItem.setText(mList.get(position));
            if (TextUtils.equals(mOldInfo, mList.get(position))) {
                holder.ivDialogSelect.setVisibility(View.VISIBLE);
            } else {
                holder.ivDialogSelect.setVisibility(View.GONE);
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.ivDialogSelect.setVisibility(View.VISIBLE);
                    BottomListDialog.this.dismiss();
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(position);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }
    }

    static class DialogHolder extends RecyclerView.ViewHolder {

        public TextView  tvDialogItem;
        public ImageView ivDialogSelect;

        public DialogHolder(View itemView) {
            super(itemView);
            tvDialogItem = (TextView) itemView.findViewById(R.id.tv_dialog_item);
            ivDialogSelect = (ImageView) itemView.findViewById(R.id.iv_dialog_select);
        }
    }


    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(@Nullable OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

}
