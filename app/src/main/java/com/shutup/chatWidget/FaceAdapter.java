package com.shutup.chatWidget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.shutup.globalrandomchat.R;

import java.util.List;

/**
 * 表情面板中，展示表情的gridview的适配器
 */
public class FaceAdapter extends BaseAdapter {
    private Context context;
    private List<String> data;

    private LayoutInflater inflater;

    private int size = 0;

    public FaceAdapter(Context context, List<String> list) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.data = list;
        this.size = list.size();
    }

    @Override
    public int getCount() {
        return this.size;
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String emoji = data.get(position);
        ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.face_item, null);
            viewHolder.bigFace = (ImageView) convertView.findViewById(R.id.itemImage);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }


        int id = context.getResources().getIdentifier(emoji, "mipmap", context.getPackageName());
        viewHolder.bigFace.setImageResource(id);

        return convertView;
    }

    class ViewHolder {

        public ImageView bigFace;
    }
}