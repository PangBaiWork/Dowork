package com.pangbai.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ListView;

public class CustomListView extends ListView {

    public CustomListView(Context context) {
        super(context);
    }

    public CustomListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // 使用AT_MOST模式来测量高度，使ListView能够在ScrollView中正确展开
        int expandSpec = MeasureSpec.makeMeasureSpec(MEASURED_SIZE_MASK, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        // 不处理滑动事件，直接返回false
        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        // 不拦截滑动事件，直接返回false
        return false;
    }


}