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
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

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

    /**
     * 音乐播放进度条
     * 2016-07-21 ps:差实现进度条
     */
    private SeekBar seekBar;
    private boolean isTouchSeekbar = false;
    private TextView nowTime;
    private TextView maxTime;
    private Handler handlerTime = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            // TODO Auto-generated method stub
            //要做的事情
            nowTime.setText(getMusicNowTime());
            seekBar.setProgress(mediaPlayer.getCurrentPosition());
            handlerTime.postDelayed(this, 1000);
        }
    };

//    handler.postDelayed(runnable, 2000);//每两秒执行一次runnable.

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
        nowTime = (TextView) findViewById(R.id.nowtime);
        maxTime = (TextView) findViewById(R.id.maxtime);
        seekBar = (SeekBar) findViewById(R.id.progress);
        image_mode = (ImageView) findViewById(R.id.mode);
        image_back = (ImageView) findViewById(R.id.back_music);
        image_pause_play = (ImageView) findViewById(R.id.pause_play);
        image_next = (ImageView) findViewById(R.id.next_music);
        image_volume = (ImageView) findViewById(R.id.volume);
        listView = (ListView) findViewById(R.id.list_music);

        listView.setSelector(new ColorDrawable(Color.TRANSPARENT));//背景透明



        /** 控件设置各类监听 */
        image_mode.setOnClickListener(this);
        image_back.setOnClickListener(this);
        image_pause_play.setOnClickListener(this);
        image_next.setOnClickListener(this);
        image_volume.setOnClickListener(this);
        seekBar.setOnSeekBarChangeListener(new SeekBarListener());

        /** 为list表写好监听，根据listview的position值与歌曲集合的下标相同设置歌曲 */
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                NOW_PLAY_THE_MUSIC_INDEX = onChangeMusic.getMusicOfIndex(position);//设置歌曲路径
                onChangeMusic.setNOW_MUSIC_INDEX(position);//同步当前要播放的歌曲到onChangeMusic
                if (mediaPlayer == null){
                    //如果没有初始化过播放器则先使用playMusic方法播放
                    try {
                        playMusic();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }else {
                    //否则先暂停歌曲播放
                    pauseMusic();
                    mediaPlayer.stop();//清空原有歌曲
                    try {
                        //reset方法重新设置播放器
                        mediaPlayer.reset();
                        mediaPlayer.setDataSource(NOW_PLAY_THE_MUSIC_INDEX);
                        mediaPlayer.prepareAsync();//准备加载歌曲，这里不需要调用start()方法，因为已经在playMusic方法中设置了对加载完毕的监听，一旦加载完毕会自动开始播放
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                //最后无论使用哪种播放方式，都可以确定歌曲处于播放状态，所以对isPlay属性和按键图标进行更换
                isPlay = true;
                image_pause_play.setImageResource(R.drawable.selector_pause);
                Log.d("OnItemClick","妖兽啦！！！我被调用啦");
            }
        });

        //开启线程，用于后台获取到音乐播放的列表
        final Handler handler  = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                listView.setAdapter(baseAdapterForMyList);
            }
        };

        new Thread(){
            @Override
            public void run() {
                super.run();
                StaticData.findMusicList(PlayerFace.this);//开启寻找音乐列表
                baseAdapterForMyList = new BaseAdapterForMyList(PlayerFace.this);
                baseAdapterForMyList.setList(StaticData.getMusic_name());//设置适配器数据
                onChangeMusic = new OnChangeMusic();//下面两行设置起始音乐
                NOW_PLAY_THE_MUSIC_INDEX = onChangeMusic.getMusicOfrandom();
                handler.sendMessage(new Message());
            }
        }.start();

    }

    private class SeekBarListener implements SeekBar.OnSeekBarChangeListener {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            //正在滑动seekbar的值

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            //开始触控
            isTouchSeekbar = true;
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            //停止之后
            int dest = seekBar.getProgress();

            mediaPlayer.seekTo(dest);
            isTouchSeekbar = false;
        }
    }

    /**
     * 本方法基本原理在于先判断mediaPlayer是否已经存在。。。
     * 如果已存在就无需再浪费宝贵的CPU时间片去加载歌曲
     * @throws IOException
     */
    private void playMusic() throws IOException {
        if(mediaPlayer !=null){
            mediaPlayer.start();
            handlerTime.postDelayed(runnable,1000);
        }else {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(NOW_PLAY_THE_MUSIC_INDEX);//设置文件路径 （本行待更改
            mediaPlayer.prepareAsync();

            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    /**
                     * getDuration()方法必须在onPrepared事件之后再执行（简言之就是必须等歌曲被完全加载）
                     * 否则获取到的数据是不准确的
                     */
                    maxTime.setText(getMusicMaxTime());//设置当前歌曲的最大音乐值
                    /**
                     * 2016-07-23 ps:让进度条跟着音乐一起动。。。还未完成
                     * 2016-07-24 ps:让进度条带着音乐动。。。还未完成
                     */
                    seekBar.setMax(mediaPlayer.getDuration());
                    seekBar.setProgress(0);
                    mp.start();
                    handlerTime.postDelayed(runnable,1000);//启动线程的方式，开始计时
                    isPlay = true;
                    image_pause_play.setImageResource(R.drawable.selector_pause);
                }
            });
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
        handlerTime.removeCallbacks(runnable);
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
                if (mediaPlayer == null){
                    return;
                }
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
                if (mediaPlayer == null){
                    return;
                }
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

    /**
     * 下一首歌
     */
    private void nextMusic(){
        pauseMusic();
        nowTime.setText("00:00");
        mediaPlayer.stop();
        NOW_PLAY_THE_MUSIC_INDEX = onChangeMusic.nextMusic();
        setImage_pause_playForChangeMusic();
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(NOW_PLAY_THE_MUSIC_INDEX);
            mediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 上一首歌
     */
    private void backMusic(){
        pauseMusic();
        nowTime.setText("00:00");
        mediaPlayer.stop();
        NOW_PLAY_THE_MUSIC_INDEX = onChangeMusic.lastMusic();
        setImage_pause_playForChangeMusic();
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(NOW_PLAY_THE_MUSIC_INDEX);
            mediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 专用于切歌键之后，处理播放键的图标问题
     * 2016.07.18 ps：这里会导致bug，单击播放列表再单击下一曲时，出现需要连续按两次暂停才能停止播放歌曲
     * 2016.07.10 ps:楼上已修复
     */
    private void setImage_pause_playForChangeMusic(){
        if (isPlay){
            image_pause_play.setImageResource(R.drawable.selector_play);
        }else {
            image_pause_play.setImageResource(R.drawable.selector_pause);
        }
        isPlay = !isPlay;
    }

    /**
     * 监听返回键来关闭弹出的控制窗口
     * @param keyCode
     * @param event
     * @return
     */
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

    /**
     * 2016-07-22 ps:时间还没弄好
     * @return 返回当前音乐的最大时间
     */
    private String getMusicMaxTime(){
        int musicTime = mediaPlayer.getDuration() / 1000;
        String m = "";
        int timeM = musicTime /60;
        int timeS = musicTime %60;
        if (timeM < 10){
            m += 0 +""+ timeM;
        }
        if (timeS < 10){
            m += ":"+0+""+timeS;
        }else {
            m += ":"+timeS;
        }
        return  m;
    }

    /**
     * @return 返回当前音乐的时间
     */
    private String getMusicNowTime(){
        int musicTime = mediaPlayer.getCurrentPosition() / 1000;
        String m = "";
        int timeM = musicTime /60;
        int timeS = musicTime %60;
        if (timeM < 10){
            m += 0 +""+ timeM;
        }
        if (timeS < 10){
            m += ":"+0+""+timeS;
        }else {
            m += ":"+timeS;
        }
        
        return m;
    }

}
