package com.pay.chaofun.chaofunskechpen.widget;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;

/**
 * 用于记录绘制路径
 *
 * @author chaofun
 */
public class MarkPath {

    public static final int[] m_penColors =
            {
                    Color.argb(128, 32, 79, 140),
                    Color.argb(128, 48, 115, 170),
                    Color.argb(128, 139, 26, 99),
                    Color.argb(128, 112, 101, 89),
                    Color.argb(128, 40, 36, 37),
                    Color.argb(128, 226, 226, 226),
                    Color.argb(128, 219, 88, 50),
                    Color.argb(128, 129, 184, 69)
            };

    public static final float[] m_PenStrock =
            {
                    12,
                    14,
                    16,
                    18,
                    20,
                    22,
                    24,
                    26

            };


    public static enum MarkType {
        PEN_1,
        PEN_2,
        PEN_3,
        PEN_4,
        PEN_5,
        PEN_6,
        PEN_7,
        PEN_8,
        ERASER
    }


    public static final float NORMAL_LINE_WIDTH = (float) 20.0f;


    private static enum LineType {
        MARK,
        BCRESULT,
    }

    private static final float ERASER_FACTOT = (float) 1.5;
    private static Paint sPaint = null;
    private static PorterDuffXfermode sClearMode = new PorterDuffXfermode(PorterDuff.Mode.CLEAR);

    private static final float TOUCH_TOLERANCE = 4.0f;

    private Path mPath;
    private float mCurrentWidth = NORMAL_LINE_WIDTH;
    private PointF mPrevPoint;
    private MarkType mCurrentMarkType = MarkType.PEN_1;

    private MarkPath() {
        mPath = new Path();
    }

    public static MarkPath newMarkPath(PointF point) {
        MarkPath newPath = new MarkPath();
        newPath.mPath.moveTo(point.x, point.y);
        newPath.mPrevPoint = point;

        return newPath;
    }

    /**
     * addMarkPointToPath 将坐标点添加到路径当中
     *
     * @param point， p2当前的点
     */
    public void addMarkPointToPath(PointF point) {
        float dx = Math.abs(point.x - mPrevPoint.x);
        float dy = Math.abs(point.y - mPrevPoint.y);

        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            mPath.quadTo(mPrevPoint.x, mPrevPoint.y, (point.x + mPrevPoint.x) / 2, (point.y + mPrevPoint.y) / 2);
        }
        mPrevPoint = point;
    }

    public void drawMarkPath(Canvas canvas) {
        resetPaint(LineType.MARK);
        canvas.drawPath(mPath, sPaint);
    }

    public void drawBCResultPath(Canvas canvas) {
        resetPaint(LineType.BCRESULT);
        canvas.drawPath(mPath, sPaint);
    }


    public MarkType getCurrentMarkType() {
        return mCurrentMarkType;
    }

    public void setCurrentMarkType(MarkType currentMarkType) {
        mCurrentMarkType = currentMarkType;
    }

    public  void setWidth(float width){
        mCurrentWidth=width;
    }

//	ERASER


    private void resetPaint(LineType lineType) {
        if (sPaint == null) {
            sPaint = new Paint();
            sPaint.setAntiAlias(true);
            sPaint.setDither(true);
            sPaint.setStyle(Paint.Style.STROKE);
            sPaint.setStrokeJoin(Paint.Join.ROUND);
            sPaint.setStrokeCap(Paint.Cap.ROUND);
        }


        switch (mCurrentMarkType) {
            case PEN_1:
                setNormalPaint();
                sPaint.setColor(m_penColors[1]);
                break;
            case PEN_2:
                setNormalPaint();
                sPaint.setColor(m_penColors[2]);
                break;
            case PEN_3:
                setNormalPaint();
                sPaint.setColor(m_penColors[3]);
                break;
            case PEN_4:
                setNormalPaint();
                sPaint.setColor(m_penColors[4]);
                break;
            case PEN_5:
                setNormalPaint();
                sPaint.setColor(m_penColors[5]);
                break;
            case PEN_6:
                setNormalPaint();
                sPaint.setColor(m_penColors[6]);
                break;
            case PEN_7:
                setNormalPaint();
                sPaint.setColor(m_penColors[7]);
                break;
            case PEN_8:
                setNormalPaint();
                sPaint.setColor(m_penColors[8]);
                break;
            case ERASER:
                sPaint.setAlpha(Color.TRANSPARENT);
                sPaint.setXfermode(sClearMode);
                sPaint.setStrokeWidth(mCurrentWidth * ERASER_FACTOT);
                break;

            default:
                break;
        }

    }

    private void setNormalPaint() {
        sPaint.setXfermode(null);
        sPaint.setAntiAlias(true);
        sPaint.setDither(true);
        sPaint.setStrokeWidth(mCurrentWidth);


    }
}
