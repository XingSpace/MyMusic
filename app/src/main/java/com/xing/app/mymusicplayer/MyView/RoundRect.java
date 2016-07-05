package com.xing.app.mymusicplayer.MyView;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import com.xing.app.mymusicplayer.StaticData.StaticData;

/**
 * Created by wangxing on 16/5/9.
 * 音量条的View。。。貌似也是需要改
 */
public class RoundRect extends View {

    private Rect rect;

    private Rect volumeRect;

    private int volumeSum;

    private VolumeControl.OnVolumeChange onVolumeChange;

    public RoundRect(Context context) {
        super(context);
        setup();
    }

    public RoundRect(Context context, AttributeSet attrs) {
        super(context, attrs);
        setup();
    }

    private void setup(){
        volumeSum = (int)(StaticData.Location[0]*6.5);
        rect = new Rect(0,StaticData.Location[0]/2, StaticData.Location[0]/5,(int)(StaticData.Location[0]*6.5));
        volumeRect = new Rect(0,volumeSum,StaticData.Location[0]/5,(int)(StaticData.Location[0]*6.5));
    }

    public void setOnVolumeChange(VolumeControl.OnVolumeChange onVolumeChange){
        this.onVolumeChange = onVolumeChange;
    }

    public void runOnVolumeChange(){
        if (onVolumeChange!=null){
            onVolumeChange.onVolumeChange(volumeSum,volumeRect);
        }
    }

    public void setVolumeRect(int i){
        volumeRect.set(volumeRect.left,i,volumeRect.right,volumeRect.bottom);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.FILL);//充满
        paint.setAntiAlias(true);// 设置画笔的锯齿效果
        canvas.drawRect(rect, paint);
        //
        paint.setColor(Color.BLUE);
        canvas.drawRect(volumeRect, paint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(StaticData.Location[0]/5,StaticData.Location[0]*7);
    }
}
