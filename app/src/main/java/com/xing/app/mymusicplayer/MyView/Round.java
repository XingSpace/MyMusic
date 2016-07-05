package com.xing.app.mymusicplayer.MyView;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.xing.app.mymusicplayer.StaticData.StaticData;

/**
 * Created by wangxing on 16/5/9.
 */
public class Round extends View {



    public Round(Context context) {
        super(context);
    }
    public Round(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.FILL);//充满
        paint.setAntiAlias(true);// 设置画笔的锯齿效果
        canvas.drawCircle((StaticData.Location[0]/2f),StaticData.Location[0]/2f,StaticData.Location[0]/2f,paint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(StaticData.Location[0],StaticData.Location[0]);
    }

}
