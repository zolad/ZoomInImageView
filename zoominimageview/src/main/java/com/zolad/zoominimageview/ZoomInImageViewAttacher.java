package com.zolad.zoominimageview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.Interpolator;
import android.widget.ImageView;

import com.zolad.zoominimageview.animation.AnimCompat;
import com.zolad.zoominimageview.animation.SpringInterpolator;
import com.zolad.zoominimageview.gestures.OnScaleAndMoveGestureListener;
import com.zolad.zoominimageview.gestures.ScaleAndMoveDetector;
import com.zolad.zoominimageview.window.WindowManagerUtil;

import java.lang.ref.WeakReference;

public class ZoomInImageViewAttacher implements View.OnTouchListener {

    private WindowManager mWindowManager;
    private WindowManager.LayoutParams mWindowLayoutParams;
    /**
     * drag imageview
     */
    private ImageView mZoomIV;
    /**
     * drag image Bitmap
     */
    private Bitmap mZoomBitmap;

    private int mOffsetToTop;


    private int mOffsetToLeft;


    /**
     * is zoomable
     */
    private boolean mZoomEnabled = true;


    private ScaleAndMoveDetector mScaleGestureDetector;

    private int ZOOM_DURATION = 1000;

    private View mWindowLayout;
    private Matrix mSuppMatrix = new Matrix();
    private Matrix mBaseMatrix = new Matrix();
    private Matrix mDrawMatrix = new Matrix();
    private Interpolator mAnimInterpolator = new SpringInterpolator(1f);


    private boolean hasInterruptParentNotToHandleTouchEvent = false;

    private WeakReference<ImageView> mImageView;


    public ZoomInImageViewAttacher() {

    }

    public void attachImageView(ImageView imageView) {

        if (imageView == null) {

            throw new NullPointerException("imageview is null");

        }


        mImageView = new WeakReference<ImageView>(imageView);


        imageView.setOnTouchListener(this);


        initGestureDectector();

    }


    public ZoomInImageViewAttacher(ImageView imageView) {


        attachImageView(imageView);


    }


    public synchronized void initGestureDectector() {


        mScaleGestureDetector = new ScaleAndMoveDetector(getImageView().getContext(), new OnScaleAndMoveGestureListener() {


            @Override
            public void onScaleAndMove(ScaleGestureDetector detector, float currentScale, float moveDistanceFromX, float moveDistanceFromY) {


                if (mZoomIV == null) {

                    mScaleGestureDetector.setStartScale(detector.getScaleFactor());
                    return;
                }


                mSuppMatrix.reset();


                float centerX = mOffsetToLeft + getImageView().getWidth() / 2.0f;
                float centerY = mOffsetToTop + getImageView().getHeight() / 2.0f;


                if (currentScale >= 1.0) {
                    mSuppMatrix.postScale(currentScale, currentScale, centerX, centerY);


                } else {
                    currentScale = 1;
                    mSuppMatrix.postScale(1, 1, centerX, centerY);

                }

                mSuppMatrix.postTranslate(moveDistanceFromX, moveDistanceFromY);


                if (mWindowLayout != null) {
                    if (currentScale >= 1.0) {

                        if (currentScale > 3.0) {
                            mWindowLayout.setBackgroundColor(Color.argb((int) 200, 0, 0, 0));
                        } else {
                            mWindowLayout.setBackgroundColor(Color.argb((int) (200 * (currentScale - 1.0) / 2.0f), 0, 0, 0));
                        }
                    } else {
                        mWindowLayout.setBackgroundColor(Color.argb(0, 0, 0, 0));
                    }

                }


                setImageViewMatrix(getDrawMatrix());


            }

            @Override
            public void onScaleBegin(ScaleGestureDetector detector) {


                if (mZoomBitmap == null) {

                    getImageView().setDrawingCacheEnabled(true);

                    mZoomBitmap = Bitmap.createBitmap(getImageView().getDrawingCache());

                    getImageView().destroyDrawingCache();

                }
                createZoomImage(mZoomBitmap, mOffsetToLeft, mOffsetToTop);

            }

            @Override
            public void onScaleEnd(ScaleGestureDetector detector, float moveDistanceFromX, float moveDistanceFromY) {


                onReleaseZoom(moveDistanceFromX, moveDistanceFromY);

            }


        });


    }

    public void setZoomReleaseAnimInterpolator(Interpolator animInterpolator) {

        if (animInterpolator != null)
            this.mAnimInterpolator = animInterpolator;
    }

    public void setZoomReleaseAnimDuration(int duration) {

        if (duration > 0)
            this.ZOOM_DURATION = duration;
    }


    public void setZoomable(boolean zoomable) {
        mZoomEnabled = zoomable;

    }


    public void detach() {

        clean();

    }


    private void clean() {

        if (null != mImageView) {

            if (null != mImageView.get()) {

                mImageView.get().setOnTouchListener(null);
            }
            mImageView = null;
        }


        mWindowLayoutParams = null;
        mWindowLayout = null;
        mWindowManager = null;

        mScaleGestureDetector = null;

        recycleZoomBitmap();
    }

    /**
     * On  release zoom
     */
    private void onReleaseZoom(float disx, float disy) {

        if (getImageView() == null)
            return;

        if (mZoomIV != null) {
            float centerx = mOffsetToLeft + mZoomBitmap.getWidth() / 2.0f;
            float centery = mOffsetToTop + mZoomBitmap.getHeight() / 2.0f;
            mZoomIV.post(new AnimatedZoomRunnable(getScale(), 1,
                    centerx, centery, disx, disy));
        }
    }


    /**
     * remove zoom  layout
     */
    private void removeZoomImage() {

        hasInterruptParentNotToHandleTouchEvent = false;

        if (mZoomIV != null) {
            mZoomIV.setVisibility(View.INVISIBLE);

            if (mWindowManager != null && mWindowLayout != null) {
                WindowManagerUtil.removeViewSafety(mWindowManager, mWindowLayout);
            }


            mZoomIV = null;
            recycleZoomBitmap();
        }

    }


    private void recycleZoomBitmap() {

        if (null != mZoomBitmap && !mZoomBitmap.isRecycled()) {
            mZoomBitmap.recycle();
            mZoomBitmap = null;
        }

    }


    /**
     * @param bitmap
     * @param mOffsetToLeft
     * @param mOffsetToTop
     */
    private synchronized void createZoomImage(Bitmap bitmap, int mOffsetToLeft, int mOffsetToTop) {


        if (getImageView() == null)
            return;

        if (mWindowLayoutParams == null || mWindowLayout == null) {
            mWindowLayoutParams = new WindowManager.LayoutParams();
            mWindowLayoutParams.format = PixelFormat.RGBA_8888; // 图片之外的其他地方透明


            mWindowLayoutParams.alpha = 1f; // 透明度  0.55
            mWindowLayoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
            mWindowLayoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;


            mWindowLayoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_PANEL;


            mWindowLayout = LayoutInflater.from(getImageView().getContext()).inflate(R.layout.layout_zoominimage, null);
        }

        mWindowLayout.setClickable(true);

        mZoomIV = (ImageView) mWindowLayout.findViewById(R.id.iv_zoominpic);
        mZoomIV.setVisibility(View.VISIBLE);
        mZoomIV.setImageBitmap(bitmap);


        mBaseMatrix.reset();
        mSuppMatrix.reset();

        mBaseMatrix.postTranslate(mOffsetToLeft,
                mOffsetToTop);

        mDrawMatrix.set(mBaseMatrix);

        mZoomIV.setScaleType(ImageView.ScaleType.MATRIX);

        setImageViewMatrix(mDrawMatrix);


        mWindowLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                if (mImageView.get() != null) {
                    getImageView().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            getImageView().setVisibility(View.INVISIBLE);// 隐藏该item
                        }
                    }, 300);

                }
            }
        });

        Log.e("ivd", "show drag image");

        if (mWindowManager == null) {
            mWindowManager = (WindowManager) getImageView().getContext().getSystemService(Context.WINDOW_SERVICE);
        }

        WindowManagerUtil.addViewSafety(mWindowManager, mWindowLayout, mWindowLayoutParams);

    }


    public Matrix getDrawMatrix() {
        mDrawMatrix.set(mBaseMatrix);
        mDrawMatrix.postConcat(mSuppMatrix);
        return mDrawMatrix;
    }

    private void setImageViewMatrix(Matrix matrix) {

        if (null != mZoomIV) {


            mZoomIV.setImageMatrix(matrix);


        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {  //


        if (mZoomEnabled) {

            if (event.getPointerCount() >= 2) {


                //if touch by more than two finger  ,handle by itself

                if (!hasInterruptParentNotToHandleTouchEvent) {
                    view.getParent().requestDisallowInterceptTouchEvent(true);
                    hasInterruptParentNotToHandleTouchEvent = true;
                }

            } else {

                hasInterruptParentNotToHandleTouchEvent = false;
                view.getParent().requestDisallowInterceptTouchEvent(false);


            }
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:


                mOffsetToTop = (int) (event.getRawY() - event.getY());
                mOffsetToLeft = (int) (event.getRawX() - event.getX());


                break;
            case MotionEvent.ACTION_MOVE:

                break;
            case MotionEvent.ACTION_UP:

                break;
            case MotionEvent.ACTION_CANCEL:

                break;
            case MotionEvent.ACTION_OUTSIDE:

                break;
            default:
                break;
        }


        boolean handled = false;


        if (mZoomEnabled && null != mScaleGestureDetector
                && mScaleGestureDetector.onTouchEvent(event)) {
            handled = true;
        }


        if (view.onTouchEvent(event)) {
            handled = true;
        }

        return handled;
    }


    public ImageView getImageView() {

        if (mImageView != null)
            return mImageView.get();
        else {

            clean();
            return null;
        }
    }


    public float getScale() {
        //FloatMath
        return (float) Math.sqrt((float) Math.pow(getValue(mSuppMatrix, Matrix.MSCALE_X), 2) + (float) Math.pow(getValue(mSuppMatrix, Matrix.MSKEW_Y), 2));
    }

    private final float[] mMatrixValues = new float[9];

    private float getValue(Matrix matrix, int whichValue) {
        matrix.getValues(mMatrixValues);
        return mMatrixValues[whichValue];
    }


    private class AnimatedZoomRunnable implements Runnable {

        private final float mFocalX, mFocalY;
        private final long mStartTime;
        private final float mZoomStartScale, mZoomEndScale;
        private final float mTranslateDistanceX, mTranslateDistanceY;


        public AnimatedZoomRunnable(final float currentZoom, final float targetZoom,
                                    final float focalX, final float focalY, final float translateDistanceX, final float translateDistanceY) {
            mFocalX = focalX;
            mFocalY = focalY;
            mStartTime = System.currentTimeMillis();
            mZoomStartScale = currentZoom;
            mZoomEndScale = targetZoom;
            mTranslateDistanceX = translateDistanceX;
            mTranslateDistanceY = translateDistanceY;

            mWindowLayout.setBackgroundColor(Color.argb(0, 0, 0, 0));

        }


        @Override
        public void run() {
            ImageView imageView = mZoomIV;
            if (imageView == null) {
                return;
            }
            float time = 1f * (System.currentTimeMillis() - mStartTime) / ZOOM_DURATION;
            float t = mAnimInterpolator.getInterpolation(time);
            float scales = mZoomStartScale + t * (mZoomEndScale - mZoomStartScale);

            mSuppMatrix.reset();


            mSuppMatrix.postScale(scales, scales, mFocalX, mFocalY);

            float x = mTranslateDistanceX + t * (0 - mTranslateDistanceX);
            float y = mTranslateDistanceY + t * (0 - mTranslateDistanceY);

            mSuppMatrix.postTranslate(x, y);

            setImageViewMatrix(getDrawMatrix());

            float stopTime = 1f;

            if (mAnimInterpolator instanceof SpringInterpolator) {

                stopTime = 0.8f;


            }


            if (time < stopTime) {
                AnimCompat.postOnAnimation(imageView, this);


            } else {

                getImageView().post(new Runnable() {
                    @Override
                    public void run() {
                        getImageView().setVisibility(View.VISIBLE);
                        removeZoomImage();
                    }
                });

            }


        }

    }


}
