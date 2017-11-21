package com.example.qqbubble;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by Administrator on 2017/11/16.
 *
 * @author pz
 */

public class DragLayout extends RelativeLayout {

    private static final String TAG = "DragLayout";

    private ViewDragHelper mDragHelper;
    private TextView tvDot;
    private int dotOriX;
    private int dotOriY;
    private int dotWidth;
    private int dotHeight;

    private int finalX;
    private int finalY;
    private Paint mPaint;

    /**
     * 连接线条是否显示
     */
    private boolean showDragLine = true;
    /* 气泡爆炸的图片id数组 */
    private int[] mExplosionDrawables = {R.drawable.explosion_one, R.drawable.explosion_two
            , R.drawable.explosion_three, R.drawable.explosion_four, R.drawable.explosion_five};
    /**
     * 气泡爆炸的bitmap数组
     */
    private Bitmap[] mExplosionBitmaps;
    /* 气泡爆炸动画是否开始 */
    private boolean mIsExplosionAnimStart = false;
    /* 气泡爆炸当前进行到第几张 */
    private int mCurExplosionIndex;
    private Rect mExplosionRect;
    private Paint mExplosionPaint;

    public DragLayout(Context context) {
        super(context);
        init();
    }

    public DragLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DragLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        tvDot = findViewById(R.id.tv_drag);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        dotOriX = tvDot.getLeft();
        dotOriY = tvDot.getTop();
        dotWidth = tvDot.getRight() - tvDot.getLeft();
        dotHeight = tvDot.getBottom() - tvDot.getTop();
        finalX = tvDot.getLeft();
        finalY = tvDot.getTop();
        Log.i(TAG,"dotOriX = " + dotOriX + ", dotOriY = " + dotOriY);

    }

    @Override
    public void computeScroll() {
        if (mDragHelper.continueSettling(true)) {
            invalidate();
        }
    }

    private void init() {
        /**
         * @params ViewGroup forParent 必须是一个ViewGroup
         * @params float sensitivity 灵敏度
         * @params Callback cb 回调
         */
        mDragHelper = ViewDragHelper.create(this, 1.0f, new ViewDragCallback());
        mPaint = new Paint();
        mPaint.setColor(Color.parseColor("#f0605a"));
        mPaint.setStrokeWidth(10f);
        setWillNotDraw(false);

        //消失爆炸效果相关初始化
        mExplosionPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mExplosionPaint.setFilterBitmap(true);
        mExplosionRect = new Rect();
        mExplosionBitmaps = new Bitmap[mExplosionDrawables.length];
        for (int i = 0; i < mExplosionDrawables.length; i++) {
            //将气泡爆炸的drawable转为bitmap
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), mExplosionDrawables[i]);
            mExplosionBitmaps[i] = bitmap;
        }
    }

    public void reset() {
        tvDot.setVisibility(VISIBLE);
        tvDot.setX(dotOriX);
        tvDot.setY(dotOriY);
        tvDot.setText("1");
        tvDot.setBackgroundResource(R.drawable.red_circle_shape);
        showDragLine = true;
    }

    private class ViewDragCallback extends ViewDragHelper.Callback {
        /**
         * 尝试捕获子view，一定要返回true
         *
         * @param view      尝试捕获的view
         * @param pointerId 指示器id？
         *                  这里可以决定哪个子view可以拖动
         */
        @Override
        public boolean tryCaptureView(View view, int pointerId) {
            if (view.getId() == R.id.tv_drag) {
                return true;
            } else {
                return false;
            }
        }

        /**
         * 处理水平方向上的拖动
         *
         * @param child 被拖动到view
         * @param left  移动到达的x轴的距离
         * @param dx    建议的移动的x距离
         */
        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            // 两个if主要是为了让viewViewGroup里
            if (getPaddingLeft() > left) {
                return getPaddingLeft();
            }
            if (getWidth() - child.getWidth() < left) {
                return getWidth() - child.getWidth();
            }
            return left;
        }

        /**
         * 处理竖直方向上的拖动
         *
         * @param child 被拖动到view
         * @param top   移动到达的y轴的距离
         * @param dy    建议的移动的y距离
         */
        @Override
        public int clampViewPositionVertical(View child, int top, int dy) {
            // 两个if主要是为了让viewViewGroup里
            if (getPaddingTop() > top) {
                return getPaddingTop();
            }
            if (getHeight() - child.getHeight() < top) {
                return getHeight() - child.getHeight();
            }
            return top;
        }

        /**
         * 拖拽移动时触发的回调
         * @param changedView 触发该函数的View
         * @param left View左边界坐标
         * @param top View上边界坐标
         * @param dx 该次横向移动坐标
         * @param dy 该次纵向移动坐标
         */
        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            if (changedView.getId() == R.id.tv_drag) {
                Log.i(TAG,"left = " + left + ", top = " + top + "dx = " + dx + ", dy = " + dy);
                finalX = left;
                finalY = top;
                invalidate();
            }
            super.onViewPositionChanged(changedView, left, top, dx, dy);
        }

        /**
         * 当拖拽到状态改变时回调
         *
         * @params 新的状态
         */
        @Override
        public void onViewDragStateChanged(int state) {
            switch (state) {
                case ViewDragHelper.STATE_DRAGGING:  // 正在被拖动
                    break;
                case ViewDragHelper.STATE_IDLE:  // view没有被拖拽或者 正在进行fling/snap
                    break;
                case ViewDragHelper.STATE_SETTLING: // fling完毕后被放置到一个位置
                    break;
            }
            super.onViewDragStateChanged(state);
        }

        /**
         * 拖拽手指离开时回调
         * @param releasedChild 拖拽的控件
         * @param xvel 手指离开屏幕时拖拽控件的x方向的速度
         * @param yvel 手指离开屏幕时拖拽控件的y方向的速度
         */
        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            if (releasedChild == tvDot) {
                Log.i("getCenterDistance", String.valueOf(getCenterDistance()));
                if (getCenterDistance() < 300) { //连接线条未消失
                    postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            showDragLine = true;
                        }
                    }, 300);
                    mDragHelper.settleCapturedViewAt(dotOriX, dotOriY);
                    invalidate();
                } else {
                    showBomb();
                }
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (showDragLine) {
            double distance = getCenterDistance();
            int initRadius = (dotWidth / 2);
            int finalRadius;
            if (distance >= 300) {
                finalRadius = 10;
            } else {
                finalRadius = ((300 - distance) * initRadius / 300) < 10 ? 10 : (int) ((300 - distance) * initRadius / 300);
            }
            if (distance < 300) {
                drawOval(dotOriX + dotWidth / 2, dotOriY + dotHeight / 2, finalRadius, canvas);
                Path path = drawAdhesionBody(dotOriX + dotWidth / 2, dotOriY + dotHeight / 2, finalRadius, 90f,
                        finalX + dotWidth / 2, finalY + dotHeight / 2, dotHeight / 2, 45f);
                canvas.drawPath(path, mPaint);
            } else {
                showDragLine = false;
            }
        }

        if (mIsExplosionAnimStart && mCurExplosionIndex < mExplosionDrawables.length) {
            //设置气泡爆炸图片的位置
            mExplosionRect.set((int) (finalX), (int) (finalY)
                    , (int) (finalX+dotWidth), (int) (finalY+dotHeight));
            //根据当前进行到爆炸气泡的位置index来绘制爆炸气泡bitmap
            canvas.drawBitmap(mExplosionBitmaps[mCurExplosionIndex], null, mExplosionRect, mExplosionPaint);
        }
    }

    private double getCenterDistance() {
        return Math.sqrt(((finalX - dotOriX) * (finalX - dotOriX)) + ((finalY - dotOriY) * (finalY - dotOriY)));
    }

    private void drawOval(float centerX, float centerY, double finalRadius, Canvas canvas) {
//        int initRadius = (dotWidth / 2);
//        int finalRadius;
//        if (distance >= 500) {
//            finalRadius = 15;
//        } else {
//            finalRadius = ((500 - distance) * initRadius / 500) < 15 ? 15 : (int) ((500 - distance) * initRadius / 500);
//        }
        canvas.drawCircle(centerX, centerY, (float) finalRadius, mPaint);
    }


    /**
     * 画粘连体
     *
     * @param cx1     圆心x1
     * @param cy1     圆心y1
     * @param r1      圆半径r1
     * @param offset1 贝塞尔曲线偏移角度offset1
     * @param cx2     圆心x2
     * @param cy2     圆心y2
     * @param r2      圆半径r2
     * @param offset2 贝塞尔曲线偏移角度offset2
     * @return
     */
    public static Path drawAdhesionBody(float cx1, float cy1, float r1, float offset1, float
            cx2, float cy2, float r2, float offset2) {

    /* 求三角函数 */
        float degrees = (float) Math.toDegrees(Math.atan(Math.abs(cy2 - cy1) / Math.abs(cx2 - cx1)));

    /* 根据圆1与圆2的相对位置求四个点 */
        float differenceX = cx1 - cx2;
        float differenceY = cy1 - cy2;

    /* 两条贝塞尔曲线的四个端点 */
        float x1, y1, x2, y2, x3, y3, x4, y4;

    /* 圆1在圆2的下边 */
        if (differenceX == 0 && differenceY > 0) {
            x2 = cx2 - r2 * (float) Math.sin(Math.toRadians(offset2));
            y2 = cy2 + r2 * (float) Math.cos(Math.toRadians(offset2));
            x4 = cx2 + r2 * (float) Math.sin(Math.toRadians(offset2));
            y4 = cy2 + r2 * (float) Math.cos(Math.toRadians(offset2));
            x1 = cx1 - r1 * (float) Math.sin(Math.toRadians(offset1));
            y1 = cy1 - r1 * (float) Math.cos(Math.toRadians(offset1));
            x3 = cx1 + r1 * (float) Math.sin(Math.toRadians(offset1));
            y3 = cy1 - r1 * (float) Math.cos(Math.toRadians(offset1));
        }
    /* 圆1在圆2的上边 */
        else if (differenceX == 0 && differenceY < 0) {
            x2 = cx2 - r2 * (float) Math.sin(Math.toRadians(offset2));
            y2 = cy2 - r2 * (float) Math.cos(Math.toRadians(offset2));
            x4 = cx2 + r2 * (float) Math.sin(Math.toRadians(offset2));
            y4 = cy2 - r2 * (float) Math.cos(Math.toRadians(offset2));
            x1 = cx1 - r1 * (float) Math.sin(Math.toRadians(offset1));
            y1 = cy1 + r1 * (float) Math.cos(Math.toRadians(offset1));
            x3 = cx1 + r1 * (float) Math.sin(Math.toRadians(offset1));
            y3 = cy1 + r1 * (float) Math.cos(Math.toRadians(offset1));
        }
    /* 圆1在圆2的右边 */
        else if (differenceX > 0 && differenceY == 0) {
            x2 = cx2 + r2 * (float) Math.cos(Math.toRadians(offset2));
            y2 = cy2 + r2 * (float) Math.sin(Math.toRadians(offset2));
            x4 = cx2 + r2 * (float) Math.cos(Math.toRadians(offset2));
            y4 = cy2 - r2 * (float) Math.sin(Math.toRadians(offset2));
            x1 = cx1 - r1 * (float) Math.cos(Math.toRadians(offset1));
            y1 = cy1 + r1 * (float) Math.sin(Math.toRadians(offset1));
            x3 = cx1 - r1 * (float) Math.cos(Math.toRadians(offset1));
            y3 = cy1 - r1 * (float) Math.sin(Math.toRadians(offset1));
        }
    /* 圆1在圆2的左边 */
        else if (differenceX < 0 && differenceY == 0) {
            x2 = cx2 - r2 * (float) Math.cos(Math.toRadians(offset2));
            y2 = cy2 + r2 * (float) Math.sin(Math.toRadians(offset2));
            x4 = cx2 - r2 * (float) Math.cos(Math.toRadians(offset2));
            y4 = cy2 - r2 * (float) Math.sin(Math.toRadians(offset2));
            x1 = cx1 + r1 * (float) Math.cos(Math.toRadians(offset1));
            y1 = cy1 + r1 * (float) Math.sin(Math.toRadians(offset1));
            x3 = cx1 + r1 * (float) Math.cos(Math.toRadians(offset1));
            y3 = cy1 - r1 * (float) Math.sin(Math.toRadians(offset1));
        }
    /* 圆1在圆2的右下角 */
        else if (differenceX > 0 && differenceY > 0) {
            x2 = cx2 - r2 * (float) Math.cos(Math.toRadians(180 - offset2 - degrees));
            y2 = cy2 + r2 * (float) Math.sin(Math.toRadians(180 - offset2 - degrees));
            x4 = cx2 + r2 * (float) Math.cos(Math.toRadians(degrees - offset2));
            y4 = cy2 + r2 * (float) Math.sin(Math.toRadians(degrees - offset2));
            x1 = cx1 - r1 * (float) Math.cos(Math.toRadians(degrees - offset1));
            y1 = cy1 - r1 * (float) Math.sin(Math.toRadians(degrees - offset1));
            x3 = cx1 + r1 * (float) Math.cos(Math.toRadians(180 - offset1 - degrees));
            y3 = cy1 - r1 * (float) Math.sin(Math.toRadians(180 - offset1 - degrees));
        }
    /* 圆1在圆2的左上角 */
        else if (differenceX < 0 && differenceY < 0) {
            x2 = cx2 - r2 * (float) Math.cos(Math.toRadians(degrees - offset2));
            y2 = cy2 - r2 * (float) Math.sin(Math.toRadians(degrees - offset2));
            x4 = cx2 + r2 * (float) Math.cos(Math.toRadians(180 - offset2 - degrees));
            y4 = cy2 - r2 * (float) Math.sin(Math.toRadians(180 - offset2 - degrees));
            x1 = cx1 - r1 * (float) Math.cos(Math.toRadians(180 - offset1 - degrees));
            y1 = cy1 + r1 * (float) Math.sin(Math.toRadians(180 - offset1 - degrees));
            x3 = cx1 + r1 * (float) Math.cos(Math.toRadians(degrees - offset1));
            y3 = cy1 + r1 * (float) Math.sin(Math.toRadians(degrees - offset1));
        }
    /* 圆1在圆2的左下角 */
        else if (differenceX < 0 && differenceY > 0) {
            x2 = cx2 - r2 * (float) Math.cos(Math.toRadians(degrees - offset2));
            y2 = cy2 + r2 * (float) Math.sin(Math.toRadians(degrees - offset2));
            x4 = cx2 + r2 * (float) Math.cos(Math.toRadians(180 - offset2 - degrees));
            y4 = cy2 + r2 * (float) Math.sin(Math.toRadians(180 - offset2 - degrees));
            x1 = cx1 - r1 * (float) Math.cos(Math.toRadians(180 - offset1 - degrees));
            y1 = cy1 - r1 * (float) Math.sin(Math.toRadians(180 - offset1 - degrees));
            x3 = cx1 + r1 * (float) Math.cos(Math.toRadians(degrees - offset1));
            y3 = cy1 - r1 * (float) Math.sin(Math.toRadians(degrees - offset1));
        }
    /* 圆1在圆2的右上角 */
        else {
            x2 = cx2 - r2 * (float) Math.cos(Math.toRadians(180 - offset2 - degrees));
            y2 = cy2 - r2 * (float) Math.sin(Math.toRadians(180 - offset2 - degrees));
            x4 = cx2 + r2 * (float) Math.cos(Math.toRadians(degrees - offset2));
            y4 = cy2 - r2 * (float) Math.sin(Math.toRadians(degrees - offset2));
            x1 = cx1 - r1 * (float) Math.cos(Math.toRadians(degrees - offset1));
            y1 = cy1 + r1 * (float) Math.sin(Math.toRadians(degrees - offset1));
            x3 = cx1 + r1 * (float) Math.cos(Math.toRadians(180 - offset1 - degrees));
            y3 = cy1 + r1 * (float) Math.sin(Math.toRadians(180 - offset1 - degrees));
        }

    /* 贝塞尔曲线的控制点 */
        float anchorX1, anchorY1, anchorX2, anchorY2;

    /* 圆1大于圆2 */
        if (r1 > r2) {
            anchorX1 = (x2 + x3) / 2;
            anchorY1 = (y2 + y3) / 2;
            anchorX2 = (x1 + x4) / 2;
            anchorY2 = (y1 + y4) / 2;
        }
    /* 圆1小于或等于圆2 */
        else {
            anchorX1 = (x1 + x4) / 2;
            anchorY1 = (y1 + y4) / 2;
            anchorX2 = (x2 + x3) / 2;
            anchorY2 = (y2 + y3) / 2;
        }

    /* 画粘连体 */
        Path path = new Path();
        path.reset();
        path.moveTo(x1, y1);
        path.quadTo(anchorX1, anchorY1, x2, y2);
        path.lineTo(x4, y4);
        path.quadTo(anchorX2, anchorY2, x3, y3);
        path.lineTo(x1, y1);
        return path;
    }

    /**
     * 展示爆炸特效
     */
    private void showBomb() {
        tvDot.setVisibility(GONE);
        mIsExplosionAnimStart = true;
        //做一个int型属性动画，从0开始，到气泡爆炸图片数组个数结束
        ValueAnimator anim = ValueAnimator.ofInt(0, mExplosionDrawables.length);
        anim.setInterpolator(new LinearInterpolator());
        anim.setDuration(500);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                //拿到当前的值并重绘
                mCurExplosionIndex = (int) animation.getAnimatedValue();
                invalidate();
            }
        });
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                //动画结束后改变状态
                mIsExplosionAnimStart = false;
            }
        });
        anim.start();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_DOWN:
                mDragHelper.cancel(); // 相当于调用 processTouchEvent收到ACTION_CANCEL
                break;
        }
        /**
         * 检查是否可以拦截touch事件
         * 如果onInterceptTouchEvent可以return true 则这里return true
         */
        return mDragHelper.shouldInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        /**
         * 处理拦截到的事件
         * 这个方法会在返回前分发事件
         */
        mDragHelper.processTouchEvent(event);
        return true;
    }
}
