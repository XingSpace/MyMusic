package com.xing.app.mymusicplayer.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.xing.app.mymusicplayer.R;

import java.util.List;

/**
 * Created by wangxing on 16/4/26.
 */
public class BaseAdapterForMyList extends BaseAdapter {

    private Context context;
    private List<String> list;

    private int[] color = {R.color.item_color1,
            R.color.item_color2,
            R.color.item_color3,
            R.color.item_color4,
            R.color.item_color5};

    public BaseAdapterForMyList(Context context){
        this.context = context;
    }

    public void setList(List<String> list){
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView!=null){
            if (convertView.getTag() == null){
                convertView.setTag(R.id.mylist_item_text,position);
            }else if (convertView.getTag(R.id.mylist_item_text).equals(position)){
                return convertView;
            }
        }

        convertView = LayoutInflater.from(context).inflate(R.layout.mylist_item,parent,false);

        TextView textView = (TextView) convertView.findViewById(R.id.mylist_item_text);
        textView.setText(list.get(position));//这里设置歌曲名

        ImageView imageView = (ImageView) convertView.findViewById(R.id.mylist_item_image);

        int i = position%5;

        imageView.setBackgroundColor(context.getResources().getColor(color[i]));

        return convertView;
    }
}
