package com.pangbai.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;

public class CustomTextView extends androidx.appcompat.widget.AppCompatTextView {
    private static final int DEFAULT_COLOR = Color.RED;
    private static final int DEFAULT_CHAR_COUNT = 5;

    private Paint paint;
    private int color;
    private int charCount;

    public CustomTextView(Context context) {
        super(context);
        init();
    }

    public CustomTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        paint = new Paint();
        color = DEFAULT_COLOR;
        charCount = DEFAULT_CHAR_COUNT;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int viewWidth = getWidth();
        int viewHeight = getHeight();
        String text = getText().toString();
        int drawCount = Math.min(charCount, text.length());

        paint.setColor(color);
        paint.setTextSize(getTextSize());
        paint.setStyle(Paint.Style.FILL);

        float textWidth = paint.measureText(text);
        float startX = (viewWidth - textWidth) / 2f;
        float startY = (viewHeight + getTextSize()) / 2f;

        canvas.drawText(text, 0, drawCount, startX, startY, paint);
        paint.setColor(getCurrentTextColor());
        canvas.drawText(text, drawCount, text.length(), startX + paint.measureText(text, 0, drawCount), startY, paint);
        //test code
      //  this.color = Color.p;
        this.color=Color.WHITE;
        this.paint.setTypeface(Typeface.SERIF);
        this.charCount = text.indexOf("：")+1;
        invalidate();
        
    }

    public void setColor(int color) {
        this.color = color;
        invalidate();
    }

    public void setCharCount(int charCount) {
        this.charCount = charCount;
        invalidate();
    }
}
