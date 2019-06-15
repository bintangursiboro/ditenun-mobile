package com.ditenun.appditenun.function.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import androidx.appcompat.widget.AppCompatSeekBar;
import android.util.AttributeSet;


public class ScrollBar extends AppCompatSeekBar {
    public ScrollBar(Context context) {
        super(context);
    }

    public ScrollBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public ScrollBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas c) {
        super.onDraw(c);
        int thumb_x = (int) (( (double)this.getProgress()/this.getMax() ) * (double)this.getWidth());
        float middle = (float) (this.getHeight());

        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setTextSize(20);
        c.drawText(""+this.getProgress(), thumb_x, middle, paint);
    }
}
