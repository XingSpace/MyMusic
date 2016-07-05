package com.xing.app.mymusicplayer.MyView;

import android.content.Context;
import android.graphics.Rect;
import android.media.AudioManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import com.xing.app.mymusicplayer.R;

/**
 * Created by wangxing on 16/5/7.
 * 音量控制器。。。绝对需要修改（那你为什么不改？（我懒啊嘿嘿。。。
 */
public class VolumeControl extends LinearLayout{

    private Context context;

    private Round round;

    private RoundRect roundRect;

    private float startY;

    private int[] location;//保存来自音量键的高宽，location[0]为宽，location[1]为高

    private float volumeUnit;

    private AudioManager mAudioManager;

    private int maxVolume;

    private float currentY;

    public VolumeControl(Context context,int[] location){
        super(context);
        this.context = context;
        this.location = location;
        setup();
    }

    public VolumeControl(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        setup();
    }

    private void setup(){
        setFocusable(true);

        mAudioManager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
        maxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);

        LayoutInflater inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.volume_control, this);

        round = (Round)findViewById(R.id.round);
        roundRect = (RoundRect)findViewById(R.id.roundrect);

        round.post(new Runnable() {
            @Override
            public void run() {
                roundRect.runOnVolumeChange();
            }
        });

        round.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startY = event.getY();
                        currentY = v.getY();
                        break;

                    case MotionEvent.ACTION_MOVE:

                        changeVolume(v,event);

                        break;
                }
                return true;
            }
        });

        roundRect.setOnVolumeChange(new OnVolumeChange() {
            @Override
            public void onVolumeChange(int sum, Rect rect) {
                roundRect.setVolumeRect((int) round.getY());
            }
        });

    }

    private synchronized void changeVolume(View v,MotionEvent event){
        //这是一段懵逼的代码。。。我自己都不知道怎么想到的。。。但是。。。他还是能解决初级问题的

        volumeUnit = (float) (roundRect.getHeight()-round.getHeight()) / maxVolume;

        int sum = (int) ((event.getY() - startY) / volumeUnit);

        float temp = (sum * volumeUnit) + v.getY();

        boolean top = temp > roundRect.getY();

        boolean bottom = temp < roundRect.getY() + roundRect.getHeight() - v.getHeight();

        Log.d("sumNumber",""+sum);

        if (top && bottom) {
            int volume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            if (temp > v.getY()){
                v.setY(temp);
                mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume - 1, 0);
                Log.d("volumeTAG", volume - 1 + "");
            }else if (temp < v.getY()){
                v.setY(temp);
                mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume + 1, 0);
                Log.d("volumeTAG Add", volume+1 + "");
            }
//            if (temp > currentY) {
//                if (temp != v.getY()) {
//                    v.setY(temp);
//                    mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume - 1, 0);
//                    Log.d("volumeTAG", volume-1 + "");
//                }
//            } else {
//                if (temp != v.getY()) {
//                    v.setY(temp);
//                    mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume + 1, 0);
//                    Log.d("volumeTAG Add", volume+1 + "");
//                }
//            }

        } else {
            if (!top) {
                v.setY(roundRect.getY());
                mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, maxVolume, 0);
            }
            if (!bottom) {
                v.setY(roundRect.getY() + roundRect.getHeight() - v.getHeight());
                mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
            }
        }
        roundRect.runOnVolumeChange();
    }

    public interface OnVolumeChange{
        public void onVolumeChange(int sum,Rect rect);
    }

}
