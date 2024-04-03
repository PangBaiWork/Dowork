package com.pangbai.view;
import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.AttributeSet;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextPaint;
import android.util.AttributeSet;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.Layout;
import android.text.StaticLayout;


import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.AttributeSet;

import android.content.Context;
import android.graphics.Color;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.AttributeSet;
import androidx.appcompat.widget.AppCompatTextView;

import android.content.Context;
import android.graphics.Color;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import androidx.appcompat.widget.AppCompatTextView;

import android.content.Context;
import android.graphics.Color;
import android.text.SpannableStringBuilder;
import android.text.style.RelativeSizeSpan;
import android.util.AttributeSet;
import androidx.appcompat.widget.AppCompatTextView;

public class CustomTextView extends AppCompatTextView {

    public CustomTextView(Context context) {
        super(context);
    }

    public CustomTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        if (text != null) {
            String[] lines = text.toString().split("\n");
            SpannableStringBuilder builder = new SpannableStringBuilder();

            for (int i = 0; i < lines.length; i++) {
                int colonIndex = lines[i].indexOf(':');
                if (colonIndex >= 0 && colonIndex < lines[i].length() - 1) {
                    SpannableStringBuilder lineBuilder = new SpannableStringBuilder(lines[i]);
                    lineBuilder.setSpan(new RelativeSizeSpan(0.8f), colonIndex + 1, lines[i].length(), SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE);
                    builder.append(lineBuilder);
                } else {
                    builder.append(lines[i]);
                }

                // Avoid adding extra new line after the last line
                if (i < lines.length - 1) {
                    builder.append("\n");
                }
            }

            super.setText(builder, type);
            return;
        }
        super.setText(text, type);
    }
}
