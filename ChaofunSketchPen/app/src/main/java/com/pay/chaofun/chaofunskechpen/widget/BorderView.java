package com.pay.chaofun.chaofunskechpen.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.pay.chaofun.chaofunskechpen.utils.TouchEventUtil;


/**
 * create by chaofun 2016/7/16
 */
public class BorderView extends RelativeLayout {

    private static final float MAX_SCALE = 10.0F;
    private static final float MIN_SCALE = 1.0f;
    private static final float BORDER = 10f;
    private float[] mMatrixValus = new float[9];
    private float mBorderX, mBorderY;
    private float mOldDistance;
    private boolean mIsDrag = false;
    private RelativeLayout mShowView;
    private ImageView mImageView;
    private LineView mLineView;
    private Bitmap mCutoutImage = null;
    private PointF mOldPointer = null;
    private float initImageWidth;
    private float initImageHeight;

    @SuppressLint("ClickableViewAccessibility")
    public BorderView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public BorderView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BorderView(Context context) {
        this(context, null, 0);
    }

    public void setCutoutImage(Bitmap bitmap) {
        mCutoutImage = bitmap;
    }

    public void setImageView(Bitmap bitmap) {
        mCutoutImage = bitmap;
        if (mImageView == null)
            return;
        mImageView.setImageBitmap(mCutoutImage);
    }

    public void setPenType_1() {
        mLineView.setPenType_1();
    }

    public void setPenType_2() {
        mLineView.setPenType_2();
    }

    public void setPenType_3() {
        mLineView.setPenType_3();
    }

    public void setPenType_4() {
        mLineView.setPenType_4();
    }

    public void setPenType_5() {
        mLineView.setPenType_5();
    }

    public void setPenType_6() {
        mLineView.setPenType_6();
    }

    public void setPenType_7() {
        mLineView.setPenType_7();
    }

    public void setPenType_8() {
        mLineView.setPenType_8();
    }

    public void setEraserType() {
        mLineView.setEraserType();
    }


    public void undo() {
        mLineView.undo();
    }

    public void redo() {
        mLineView.redo();
    }

    public Bitmap getResultBitmap() {

        RectF clipRect = new RectF();
        clipRect.top = mImageView.getY();
        clipRect.left = mImageView.getX();
        clipRect.bottom = mImageView.getHeight();
        clipRect.right = mImageView.getWidth();

        PointF srcSize = new PointF();
        srcSize.x = mCutoutImage.getWidth();
        srcSize.y = mCutoutImage.getHeight();

        Bitmap bitmap = mLineView.getBCResutlImage(clipRect, srcSize);


        Bitmap resultBitmap=Bitmap.createBitmap(mCutoutImage.getWidth(),mCutoutImage.getHeight(), Bitmap.Config.ARGB_4444);
        Canvas canvas=new Canvas(resultBitmap);
        canvas.drawBitmap(mCutoutImage,0,0,null);
        canvas.drawBitmap(bitmap,0,0,null);

        return resultBitmap;
    }



    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                return mLineView.onTouchEvent(event);

            case MotionEvent.ACTION_POINTER_DOWN:
                mIsDrag = true;
                mOldDistance = TouchEventUtil.spacingOfTwoFinger(event);
                mOldPointer = TouchEventUtil.middleOfTwoFinger(event);
                break;

            case MotionEvent.ACTION_MOVE:
                if (!mIsDrag) return mLineView.onTouchEvent(event);
                if (event.getPointerCount() != 2) break;
                float newDistance = TouchEventUtil.spacingOfTwoFinger(event);
                float scaleFactor = newDistance / mOldDistance;
                scaleFactor = checkingScale(mShowView.getScaleX(), scaleFactor);
                mShowView.setScaleX(mShowView.getScaleX() * scaleFactor);
                mShowView.setScaleY(mShowView.getScaleY() * scaleFactor);
                mOldDistance = newDistance;

                PointF newPointer = TouchEventUtil.middleOfTwoFinger(event);
                mShowView.setX(mShowView.getX() + newPointer.x - mOldPointer.x);
                mShowView.setY(mShowView.getY() + newPointer.y - mOldPointer.y);
                mOldPointer = newPointer;
                checkingBorder();

                break;

            case MotionEvent.ACTION_POINTER_UP:
                break;

            case MotionEvent.ACTION_UP:
                if (!mIsDrag) return mLineView.onTouchEvent(event);
                mShowView.getMatrix().getValues(mMatrixValus);
                mLineView.setScaleAndOffset(mShowView.getScaleX(), mMatrixValus[2], mMatrixValus[5]);
                mIsDrag = false;
                break;
        }
        return true;

    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        float bitmapWidth = (float) mCutoutImage.getWidth();
        float bitmapHeight = (float) mCutoutImage.getHeight();
        initImageWidth = 0;
        initImageHeight = 0;

        if (bitmapWidth > bitmapHeight) {
            initImageWidth = getWidth() - 2 * BORDER;
            initImageHeight = (bitmapHeight / bitmapWidth) * initImageWidth;
        } else {
            initImageHeight = getHeight() - 2 * BORDER;
            initImageWidth = (bitmapWidth / bitmapHeight) * initImageHeight;
        }

        mShowView = new RelativeLayout(getContext());
        RelativeLayout.LayoutParams showViewParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);

        mImageView = new ImageView(getContext());
        RelativeLayout.LayoutParams imageViewParams = new RelativeLayout.LayoutParams((int) initImageWidth, (int) initImageHeight);
        if (mCutoutImage != null) {
            mImageView.setImageBitmap(mCutoutImage);
        }
        imageViewParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        mLineView = new LineView(getContext());
        RelativeLayout.LayoutParams lineViewParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);



        mShowView.addView(mImageView, imageViewParams);
        mShowView.addView(mLineView, lineViewParams);

        addView(mShowView, showViewParams);

        mBorderX = (getWidth() - initImageWidth) / 2;
        mBorderY = (getHeight() - initImageHeight) / 2;
    }

    private float checkingScale(float scale, float scaleFactor) {
        if ((scale <= MAX_SCALE && scaleFactor > 1.0) || (scale >= MIN_SCALE && scaleFactor < 1.0)) {
            if (scale * scaleFactor < MIN_SCALE) {
                scaleFactor = MIN_SCALE / scale;
            }

            if (scale * scaleFactor > MAX_SCALE) {
                scaleFactor = MAX_SCALE / scale;
            }

        }

        return scaleFactor;
    }

    private void checkingBorder() {
        PointF offset = offsetBorder();
        mShowView.setX(mShowView.getX() + offset.x);
        mShowView.setY(mShowView.getY() + offset.y);
        if (mShowView.getScaleX() == 1) {
            mShowView.setX(0);
            mShowView.setY(0);
        }
    }

    private PointF offsetBorder() {
        PointF offset = new PointF(0, 0);
        if (mShowView.getScaleX() > 1) {
            mShowView.getMatrix().getValues(mMatrixValus);
            if (mMatrixValus[2] > -(mBorderX * (mShowView.getScaleX() - 1))) {
                offset.x = -(mMatrixValus[2] + mBorderX * (mShowView.getScaleX() - 1));
            }

            if (mMatrixValus[2] + mShowView.getWidth() * mShowView.getScaleX() - mBorderX * (mShowView.getScaleX() - 1) < getWidth()) {
                offset.x = getWidth() - (mMatrixValus[2] + mShowView.getWidth() * mShowView.getScaleX() - mBorderX * (mShowView.getScaleX() - 1));
            }

            if (mMatrixValus[5] > -(mBorderY * (mShowView.getScaleY() - 1))) {
                System.out.println("offsetY:" + mMatrixValus[5] + " borderY:" + mBorderY + " scale:" + getScaleY() + " scaleOffset:" + mBorderY * (getScaleY() - 1));
                offset.y = -(mMatrixValus[5] + mBorderY * (mShowView.getScaleY() - 1));
            }

            if (mMatrixValus[5] + mShowView.getHeight() * mShowView.getScaleY() - mBorderY * (mShowView.getScaleY() - 1) < getHeight()) {
                offset.y = getHeight() - (mMatrixValus[5] + mShowView.getHeight() * mShowView.getScaleY() - mBorderY * (mShowView.getScaleY() - 1));
            }
        }

        return offset;
    }
}
