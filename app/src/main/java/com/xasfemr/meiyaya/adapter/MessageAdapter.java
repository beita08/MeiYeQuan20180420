package com.xasfemr.meiyaya.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xasfemr.meiyaya.R;
import com.xasfemr.meiyaya.bean.ChatMessage;

/**
 * Created by sen.luo on 2016/12/25.
 */

public class MessageAdapter extends BaseListAdapter<ChatMessage> {
    public MessageAdapter(Context ctx) {
        super(ctx);
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder ;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.item_live_chat_message, null, false);
            viewHolder.userName = (TextView) convertView.findViewById(R.id.user_name);
            viewHolder.message = (TextView) convertView.findViewById(R.id.chatMessage);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        ChatMessage chatMessage = datas.get(position);

        if (chatMessage.chat_code==10){  //进入直播间
            viewHolder.userName.setVisibility(View.GONE);
            viewHolder.message.setTextColor(ctx.getResources().getColor(R.color.dialog_click_color));

        }else if (chatMessage.chat_code==20){ //管理员说话
            viewHolder.userName.setVisibility(View.GONE);
            viewHolder.message.setTextColor(ctx.getResources().getColor(R.color.re_login_btn_def_color));

        }else if (chatMessage.chat_code==200){  //聊天文本
            viewHolder.userName.setVisibility(View.VISIBLE);
            viewHolder.message.setTextColor(ctx.getResources().getColor(R.color.white));
            viewHolder.userName.setText(chatMessage.user_name+" 说: ");
        }

        viewHolder.message.setText(chatMessage.message);
//        SpannableStringBuilder builder = new SpannableStringBuilder(viewHolder.message.getText().toString());
        //ForegroundColorSpan 为文字前景色，BackgroundColorSpan为文字背景色
//        ForegroundColorSpan yellowSpan  = new ForegroundColorSpan(Color.YELLOW);
//        ForegroundColorSpan whiteSpan = new ForegroundColorSpan(Color.WHITE);
//        builder.setSpan(yellowSpan, 0, chatMessage.name.length()+1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//        builder.setSpan(whiteSpan, chatMessage.name.length()+1, builder.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
//        viewHolder.message.setText(builder);
        return  convertView ;
    }
    public class ViewHolder{
        TextView message ;
        TextView userName ;
    }
}
