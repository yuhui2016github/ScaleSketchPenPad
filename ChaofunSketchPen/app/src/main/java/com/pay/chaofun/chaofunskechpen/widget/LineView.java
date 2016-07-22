package com.pay.chaofun.chaofunskechpen.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

/**
 * @author chaofun
 */
@SuppressLint("ClickableViewAccessibility")
public class LineView extends View {

    private float mCurrentLineWidth = MarkPath.NORMAL_LINE_WIDTH;
    private boolean mIsDoubleTouch = false;

    private int mPathCount = 0;
    private ArrayList<MarkPath> mFinishedPaths;
    private MarkPath mCurrentPath = null;

    private Bitmap mBitmap = null;
    private Canvas mTempCanvas = null;
    private Paint mPaint = null;
    private float mScale = 1;
    private PointF mOffset = new PointF(0, 0);

    /**
     * 保存down下的坐标点
     */
    private PointF mCurrentPoint;

    /**
     * 当前的标记类型
     */
    private MarkPath.MarkType mCurrentType = MarkPath.MarkType.PEN_1;
    private int width;
    private int height;

    public LineView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mFinishedPaths = new ArrayList<MarkPath>();
        setBackgroundColor(Color.TRANSPARENT);
    }

    public LineView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LineView(Context context) {
        this(context, null, 0);
    }

    public void setScaleAndOffset(float scale, float dx, float dy) {
        mScale = scale;
        mCurrentLineWidth = MarkPath.NORMAL_LINE_WIDTH / mScale;
        mOffset.x = dx;
        mOffset.y = dy;
    }

    public void setPenType_1() {
        mCurrentType = MarkPath.MarkType.PEN_1;
    }

    public void setPenType_2() {
        mCurrentType = MarkPath.MarkType.PEN_2;
    }

    public void setPenType_3() {
        mCurrentType = MarkPath.MarkType.PEN_3;
    }

    public void setPenType_4() {
        mCurrentType = MarkPath.MarkType.PEN_4;
    }

    public void setPenType_5() {
        mCurrentType = MarkPath.MarkType.PEN_5;
    }

    public void setPenType_6() {
        mCurrentType = MarkPath.MarkType.PEN_6;
    }

    public void setPenType_7() {
        mCurrentType = MarkPath.MarkType.PEN_7;
    }

    public void setPenType_8() {
        mCurrentType = MarkPath.MarkType.PEN_8;
    }

    public void setEraserType() {
        mCurrentType = MarkPath.MarkType.ERASER;
    }


    /**
     * 撤销 上一个MarkPath 对象画的线
     */
    public void undo() {
        if (mPathCount > 0) {
            mPathCount--;
        }
        invalidate();
    }

    public void redo() {
        if (mFinishedPaths != null && mFinishedPaths.size() > 0) {
            if (mPathCount < mFinishedPaths.size()) {
                mPathCount++;
            }
        }
        invalidate();
    }


    public Bitmap getBCResutlImage(RectF clipRect, PointF srcSize) {
        Bitmap drawBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(drawBitmap);
        canvas.drawColor(Color.TRANSPARENT);
        for (int i = 0; i < mPathCount; i++) {
            mFinishedPaths.get(i).drawBCResultPath(canvas);
            ;
        }

        Bitmap clipBitmap = Bitmap.createBitmap(drawBitmap, (int) clipRect.left, (int) clipRect.top, (int) clipRect.right, (int) clipRect.bottom, null, false);
        Bitmap resultBitmap = Bitmap.createScaledBitmap(clipBitmap, (int) srcSize.x, (int) srcSize.y, true);
        drawBitmap.recycle();
        clipBitmap.recycle();
        return resultBitmap;
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mCurrentPoint = new PointF((event.getX() - mOffset.x) / mScale, (event.getY() - mOffset.y) / mScale);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mIsDoubleTouch = false;
                mCurrentPath = MarkPath.newMarkPath(mCurrentPoint);
                mCurrentPath.setCurrentMarkType(mCurrentType);
                mCurrentPath.setWidth(mCurrentLineWidth);
                invalidate();
                break;

            case MotionEvent.ACTION_POINTER_DOWN:
                mIsDoubleTouch = true;
                break;

            case MotionEvent.ACTION_MOVE:
                if (mCurrentPath == null || mIsDoubleTouch == true) break;
                mCurrentPath.addMarkPointToPath(mCurrentPoint);
                postInvalidateDelayed(40);
//			invalidate();
                break;

            case MotionEvent.ACTION_UP:
                if (mCurrentPath != null && mIsDoubleTouch != true) {
                    mCurrentPath.addMarkPointToPath(mCurrentPoint);
                    //如果是点击了撤销后，撤销的笔画移出栈，并将新的笔画压入栈
                    if (mPathCount < mFinishedPaths.size()) {
                        int oldSize = mFinishedPaths.size();
                        for (int i = oldSize; i > mPathCount; i--) {
                            mFinishedPaths.remove(i - 1);
                        }
                    }
                    mFinishedPaths.add(mCurrentPath);

                    mPathCount++;
                }

                mIsDoubleTouch = false;
                mCurrentPath = null;
                invalidate();
                break;
        }
        return true;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        mTempCanvas = new Canvas(mBitmap);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        //清空Bitmap画布
        mTempCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

        width = getWidth();
        height = getHeight();
        if (mFinishedPaths.size() >= 0) {
            for (int i = 0; i < mPathCount; i++) {
                mFinishedPaths.get(i).drawMarkPath(mTempCanvas);
            }
        }

        if (mCurrentPath != null) {
            mCurrentPath.drawMarkPath(mTempCanvas);
        }

        canvas.drawBitmap(mBitmap, 0, 0, mPaint);
    }


}
