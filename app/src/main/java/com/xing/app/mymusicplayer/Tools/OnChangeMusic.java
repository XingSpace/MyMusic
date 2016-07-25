package com.xing.app.mymusicplayer.Tools;

import com.xing.app.mymusicplayer.StaticData.StaticData;

import java.util.List;
import java.util.Random;

/**
 * Created by wangxing on 16/6/29.
 */
public class OnChangeMusic {

    private List<String> music;

    private int NOW_MUSIC_INDEX = 0;//当前播放到哪一首歌曲的记录

    public OnChangeMusic(){
        //如果本地播放列表不为空。。。就将播放列表复制至此
        if (StaticData.getList_music()!=null){
            music = StaticData.getList_music();
        }
    }

    public void setNOW_MUSIC_INDEX(int i){
        NOW_MUSIC_INDEX = i;
    }

    public int getNOW_MUSIC_INDEX(){
        return NOW_MUSIC_INDEX;
    }

    public String getMusicOfrandom(){
        Random random = new Random();
        int i = random.nextInt(music.size());
        NOW_MUSIC_INDEX = i;
        return music.get(i);
    }

    public String getMusicOfIndex(int i){
        return music.get(i);
    }

    public String nextMusic(){

        if (NOW_MUSIC_INDEX == music.size()-1){
            NOW_MUSIC_INDEX = 0;
        }else {
            NOW_MUSIC_INDEX++;
        }

        return music.get(NOW_MUSIC_INDEX);
    }

    public String lastMusic(){
        if (NOW_MUSIC_INDEX == 0){
            NOW_MUSIC_INDEX = music.size()-1;
        }else {
            NOW_MUSIC_INDEX--;
        }
        return music.get(NOW_MUSIC_INDEX);
    }

}
