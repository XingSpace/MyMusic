package com.xing.app.mymusicplayer.Activity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;

import com.xing.app.mymusicplayer.Adapter.BaseAdapterForMyList;
import com.xing.app.mymusicplayer.MyView.VolumeControl;
import com.xing.app.mymusicplayer.R;
import com.xing.app.mymusicplayer.StaticData.StaticData;
import com.xing.app.mymusicplayer.Tools.DensityUtil;
import com.xing.app.mymusicplayer.Tools.MakeGuess;
import com.xing.app.mymusicplayer.Tools.OnChangeMusic;

import java.io.IOException;


/**
 * Created by wangxing on 16/4/24.
 *
 *
 *
 */
public class PlayerFace extends Activity implements OnClickListener{


    /** 记录当前播放键的属性，如果是false表示歌曲还没有开始播放或者已经暂停播放
     *                    如果是true表示歌曲正在播放 */
    private boolean isPlay = false;

    private BaseAdapterForMyList baseAdapterForMyList;
    private ListView listView;

    private ImageView Background;
    private ImageView image_mode,image_back,image_pause_play,image_next,image_volume;

    /** 保存选择播放模式的菜单 */
    private View mode_menu;
    private boolean isModemenuShow = false;

    /** 保存音量调节的菜单 */
    private View volume_menu;
    private boolean isVolumeShow = false;

    /** 保存当前的播放模式,默认为按列表循环
     *  0:为按列表循环
     *  1:为随机播放
     *  2:单曲循环*/
    private int mode_number = 0;

    private FrameLayout frameLayout;//背景实例

    /** 本数组保存三个不同的播放模式的图标
     *  0:为按列表循环
     *  1:为随机播放
     *  2:单曲循环*/
    private int[] mode_array = new int[]{R.drawable.selector_cycle
            ,R.drawable.selector_random
            ,R.drawable.selector_single};

    private MediaPlayer mediaPlayer;

    private OnChangeMusic onChangeMusic;

    private String NOW_PLAY_THE_MUSIC_INDEX;

    /*   -------------------------------------------------------------   */
    /*   ----------------------- 本宝宝是分割线 -------------------------   */
    /*   -------------------------------------------------------------   */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playerface);
        setup();
    }

    private void setup(){

        /** 用于高斯模糊背景。。。 */
        Background = (ImageView)findViewById(R.id.images);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.background);
        bitmap = MakeGuess.blurBitmap(bitmap, this);
        Background.setImageBitmap(bitmap);
        bitmap = null;

        /** 用于绑定控件 */
        image_mode = (ImageView) findViewById(R.id.mode);
        image_back = (ImageView) findViewById(R.id.back_music);
        image_pause_play = (ImageView) findViewById(R.id.pause_play);
        image_next = (ImageView) findViewById(R.id.next_music);
        image_volume = (ImageView) findViewById(R.id.volume);
        listView = (ListView) findViewById(R.id.list_music);

//        baseAdapterForMyList = new BaseAdapterForMyList(this);
//        listView.setAdapter(baseAdapterForMyList);
        listView.setSelector(new ColorDrawable(Color.TRANSPARENT));

        /** 控件设置各类监听 */
        image_mode.setOnClickListener(this);
        image_back.setOnClickListener(this);
        image_pause_play.setOnClickListener(this);
        image_next.setOnClickListener(this);
        image_volume.setOnClickListener(this);

        //开启线程，用于后台获取到音乐播放的列表
        new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                StaticData.findMusicList(PlayerFace.this);//开启寻找音乐列表
                baseAdapterForMyList = new BaseAdapterForMyList(PlayerFace.this);
                baseAdapterForMyList.setList(StaticData.getList_music());//设置适配器数据
                onChangeMusic = new OnChangeMusic();//下面两行设置起始音乐
                NOW_PLAY_THE_MUSIC_INDEX = onChangeMusic.getMusicOfrandom();
                listView.setAdapter(baseAdapterForMyList);
            }
        }.sendMessage(new Message());

    }

    /**
     * 本方法基本原理在于先判断mediaPlayer是否已经存在。。。
     * 如果已存在就无需再浪费宝贵的CPU时间片去加载歌曲
     * @throws IOException
     */
    private void playMusic() throws IOException {
        if(mediaPlayer !=null){
            mediaPlayer.start();
        }else {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(NOW_PLAY_THE_MUSIC_INDEX);//设置文件路径 （本行待更改
            mediaPlayer.prepare();
            mediaPlayer.start();
        }

    }

    /**
     * 暂停歌曲
     */
    private void pauseMusic(){
        if (mediaPlayer == null){
            return;
        }
        mediaPlayer.pause();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.mode:
                //按哪种顺序播放,弹出选择菜单
                if (!isModemenuShow){
                    onModeClick();
                }else {
                    frameLayout.removeView(mode_menu);
                    isModemenuShow = false;
                }
                break;

            case R.id.back_music:
                //上一曲

                backMusic();

                break;

            case R.id.pause_play:
                //暂停或继续播放
                if (isPlay){
                    //音乐暂停播放
                    pauseMusic();
                    image_pause_play.setImageResource(R.drawable.selector_play);
                    isPlay = false;

                }else {
                    //音乐正在播放
                    try {
                        playMusic();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    image_pause_play.setImageResource(R.drawable.selector_pause);
                    isPlay = true;
                }

                break;

            case R.id.next_music:
                //下一曲

                nextMusic();

                break;

            case R.id.volume:
                //调节音量
                if(isVolumeShow){
                    frameLayout.removeView(volume_menu);
                    isVolumeShow = false;
                }else {
                    onVolumeClick();
                }
                break;

            case R.id.menu_cycle:
                //当在菜单中选择循环播放
                setImage_mode(0);
                frameLayout.removeView(mode_menu);
                break;

            case R.id.menu_random:
                //当在菜单中选择随机播放
                setImage_mode(1);
                frameLayout.removeView(mode_menu);
                break;

            case R.id.menu_single:
                //当在菜单中选择单曲循环
                setImage_mode(2);
                frameLayout.removeView(mode_menu);
                break;
        }

    }

    /**
     * 楼下两个方法未完待续。。。主要依靠isPlay这个常量来改
     */
    private void nextMusic(){
        pauseMusic();
        mediaPlayer = null;//重置
        NOW_PLAY_THE_MUSIC_INDEX = onChangeMusic.nextMusic();
        try {
            playMusic();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void backMusic(){
        pauseMusic();
        mediaPlayer = null;//重置
        NOW_PLAY_THE_MUSIC_INDEX = onChangeMusic.lastMusic();
        try {
            playMusic();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && isModemenuShow){
            frameLayout.removeView(mode_menu);
            isModemenuShow = false;
            return false;
        }
        if (keyCode == KeyEvent.KEYCODE_BACK && isVolumeShow){
            frameLayout.removeView(volume_menu);
            isVolumeShow = false;
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * @param mode image_mode的设置
     *  0:为按列表循环
     *  1:为随机播放
     *  2:单曲循环
     */
    private void setImage_mode(int mode){
        image_mode.setImageResource(mode_array[mode]);
        mode_number = mode;
    }

    /**
     * 当点击播放模式按钮时，用这个方法执行菜单弹出
     */
    private void onModeClick(){
        if (mode_menu == null){
            mode_menu = LayoutInflater.from(this).inflate(R.layout.mode_menu,null);
        }

        if (frameLayout == null){
            frameLayout = (FrameLayout)findViewById(R.id.main_back);
        }

        int[] location = new int[2];
        image_mode.getLocationOnScreen(location);

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT);
        params.leftMargin = location[0] - DensityUtil.dip2px(this,5);
        params.topMargin = location[1] - image_mode.getHeight()*5 - DensityUtil.dip2px(this, 15);

        Log.d("mode_menu:", location[0] + " " + location[1]);

        ImageView imageView = (ImageView) mode_menu.findViewById(R.id.menu_cycle);
        imageView.setOnClickListener(this);
        imageView = (ImageView) mode_menu.findViewById(R.id.menu_random);
        imageView.setOnClickListener(this);
        imageView = (ImageView) mode_menu.findViewById(R.id.menu_single);
        imageView.setOnClickListener(this);

        frameLayout.addView(mode_menu, params);
        isModemenuShow = true;
    }

    /**
     * 调节音量的菜单
     */
    private void onVolumeClick(){

        if (volume_menu == null){
            int[] location = new int[2];
            location[0] = image_volume.getWidth();
            location[1] = image_volume.getHeight();
            StaticData.Location =location;
            volume_menu = new VolumeControl(this,location);
        }

        if (frameLayout == null){
            frameLayout = (FrameLayout)findViewById(R.id.main_back);
        }

        int[] location = new int[2];
        image_volume.getLocationOnScreen(location);

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT);
        params.topMargin = location[1] - StaticData.Location[0]*8 - DensityUtil.dip2px(this,20);
        params.leftMargin = location[0] - DensityUtil.dip2px(this,5);

        frameLayout.addView(volume_menu, params);
        isVolumeShow = true;
    }

}
