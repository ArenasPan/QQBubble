package com.example.qqbubble;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.annotation.PluralsRes;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by Administrator on 2017/11/16.
 *
 * @author pz
 *         可拖拽的TextView
 */

public class DragTextView extends AppCompatTextView {

    private int startX;
    private int startY;
    private int endX;
    private int endY;

    public DragTextView(Context context) {
        super(context);
    }

    public DragTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public DragTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                startX = (int) event.getRawX();
                startY = (int) event.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                int dx = (int) (event.getRawX() - startX);
                int dy = (int) (event.getRawY() - startY);
                int l = getLeft();
                int r = getRight();
                int t = getTop();
                int b = getBottom();
                break;
            case MotionEvent.ACTION_UP:
                break;
            default:
                break;

        }
        return true;
    }
}
