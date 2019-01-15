package com.xasfemr.meiyaya.weight;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

/**
 * 带下拉下载的RecycleView
 */
public class BottomRecyclerView extends RecyclerView {

    private OnBottomListener onBottomListener;
    private boolean isReply = false;

    public BottomRecyclerView(Context context) {
        super(context);
    }

    public BottomRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BottomRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setOnBottomListener(final OnBottomListener onBottomListener) {
        this.addOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                /**
                 *
                 * 如果当前RecyclerView处于最底部，先判断是否已经响应了底部监听事件。如果还未响应则可以对其响应，
                 * 并将响应标识变量 "isReply" 设置为 "true"，防止连续多次响应。如果当前RecyclerView并未处于
                 * 最底部，则需将响应标识变量 "isReply" 设置为 "true"，以保证当再次滑动到页面地板时可以正常响应监听事件
                 *
                 */
                if (((LinearLayoutManager) getLayoutManager()).findLastVisibleItemPosition() == getLayoutManager().getItemCount() - 1) {
                    if (!isReply) {
                        if (getLayoutManager().getItemCount() >2) {
                            onBottomListener.OnBottom();
                            isReply = true;
                        } else {
                            isReply = true;
                        }

                    }
                } else {
                    isReply = false;
                }
            }
        });
        this.onBottomListener = onBottomListener;
    }

    public interface OnBottomListener {
        void OnBottom();
    }
}

