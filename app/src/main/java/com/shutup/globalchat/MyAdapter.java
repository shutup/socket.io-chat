package com.shutup.globalchat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.shutup.globalrandomchat.R;

import java.util.ArrayList;

/**
 * Created by shutup on 15-8-29.
 */
public class MyAdapter extends BaseAdapter implements Constants {

    private ArrayList<MessageInfo> msgs = new ArrayList<>();
    private LayoutInflater mInflater;

    public MyAdapter(Context context, ArrayList<MessageInfo> msgs) {
        mInflater = LayoutInflater.from(context);
        this.msgs = msgs;
    }

    @Override
    public int getCount() {
        return msgs.size();
    }

    @Override
    public Object getItem(int position) {
        return msgs.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MessageInfo messageInfo = msgs.get(position);
        View view = null;

        if (messageInfo.getType() == OTHER) {
            view = mInflater.inflate(R.layout.left_chat_cell, null);
            TextView tv = (TextView) view.findViewById(R.id.reply_textView);
            tv.setText(messageInfo.getMsg());
            ViewStub viewStub = (ViewStub) view.findViewById(R.id.time_view_stub);
            View v = viewStub.inflate();
            tv = (TextView) v.findViewById(R.id.msg_Time_TextView);
            tv.setText(messageInfo.getTime());

        } else if (messageInfo.getType() == ME) {
            view = mInflater.inflate(R.layout.right_chat_cell, null);
            TextView tv = (TextView) view.findViewById(R.id.reply_textView);
            tv.setText(messageInfo.getMsg());
            ViewStub viewStub = (ViewStub) view.findViewById(R.id.time_view_stub);
            View v = viewStub.inflate();
            tv = (TextView) v.findViewById(R.id.msg_Time_TextView);
            tv.setText(messageInfo.getTime());
        }
        return view;
    }
}
