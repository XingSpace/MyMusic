package com.xing.app.mymusicplayer.StaticData;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangxing on 16/4/26.
 * 本类所需的静态常量都在这
 */
public class StaticData {

    private static List<String> list_music;//获取到的音乐列表

    public static void setList_music(List<String> list){
        list_music = list;
    }

    public static List<String> getList_music(){
        return list_music;
    }

    public static int[] Location = {50,50};

    /**
     * 获取本地音乐时。。。数据不全面
     * @param context
     */
    public static void findMusicList(Context context){

        List<String> list = new ArrayList<String>();

        ContentResolver mResolver = context.getContentResolver();
        Cursor cursor = mResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,null,null,null,MediaStore.Audio.Media.DEFAULT_SORT_ORDER);

        int i = 0;

        if (cursor.getCount() > 0){
            cursor.moveToFirst();
            do {
                String s = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
                list.add(s);
                Log.d("musicName:", s + i++);
            }while (cursor.moveToNext());
        }

        setList_music(list);
    }

}
