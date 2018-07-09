package com.example.zhonghm.opengllearnproject;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.Interpolator;
import android.widget.ImageView;

import com.example.zhonghm.opengllearnproject.animation.AnimCompat;
import com.example.zhonghm.opengllearnproject.animation.SpringInterpolator;
import com.zolad.zoominimageview.R;

/**
 * Created by zhonghm on 2018/6/27.
 */

public class ZoomInImageView extends ImageView {

    private WindowManager mWindowManager;
    /**
     * item镜像的布局参数
     */
    private WindowManager.LayoutParams mWindowLayoutParams;
    /**
     * 用于拖拽的镜像，这里直接用一个ImageView装载Bitmap
     */
    private ImageView mDragIV;
    /**
     * 选中的item的镜像Bitmap
     */
    private Bitmap mBitmap;

    /**
     * CanDragListView距离屏幕顶部的偏移量
     */
    private int mOffsetToTop;


    /**
     * CanDragListView距离屏幕左边的偏移量
     */
    private int mOffsetToLeft;


    /**
     * 是否可拖拽，默认为false
     */
    private boolean mZoomEnabled;

    /**
     * 图片缩放手势
     */
    private ScaleGestureDetector mScaleGesture;

    int ZOOM_DURATION = 1000;

    private View mWindowLayout;
    private Matrix mSuppMatrix = new Matrix();

    private Interpolator mAnimInterpolator = new SpringInterpolator(1f);

    private GestureDetector mGesture;


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


    public ZoomInImageView(Context context) {
        super(context);

    }

    public ZoomInImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);

    }


    public ZoomInImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

        setListenr();


    }

    float disx = 0f;
    float disy = 0f;
    float mCurrentScale = 1f;

    public void setListenr() {


        // setOnTouchListener(this);

        mScaleGesture = new ScaleGestureDetector(this.getContext(), new ScaleGestureDetector.OnScaleGestureListener() {


            private float scaleTemp = 1;
            private float LastX = 0;
            private float LastY = 0;


            @Override
            public boolean onScale(ScaleGestureDetector detector) {

                //    TestImageView.this.setVisibility(View.INVISIBLE);// 隐藏该item

                if (mDragIV == null) {

                    scaleTemp = detector.getScaleFactor();
                    return false;
                }
                mCurrentScale = detector.getScaleFactor() * (1f / scaleTemp);
                float tx = detector.getFocusX();
                float ty = detector.getFocusY();
                Log.e("ivd", "scale" + mCurrentScale + "position" + detector.getFocusX() + " xy " + detector.getFocusY() + "   move" +

                        (tx - LastX) + " xy" + (ty - LastY)
                );


                mSuppMatrix.reset();


                float cx = mOffsetToLeft + mBitmap.getWidth() / 2.0f;
                float cy = mOffsetToTop + mBitmap.getHeight() / 2.0f;
                if (mCurrentScale >= 1.0) {
                    mSuppMatrix.postScale(mCurrentScale, mCurrentScale, cx, cy);


                } else {
                    mCurrentScale = 1;
                    mSuppMatrix.postScale(1, 1, cx, cy);

                }
                disx = tx - LastX;
                disy = ty - LastY;
                mSuppMatrix.postTranslate(disx, disy);
                //  mSuppMatrix.postScale(scale, scale);
                mDrawMatrix.set(mBaseMatrix);
                mDrawMatrix.postConcat(mSuppMatrix);


                if (mWindowLayout != null) {
                    if (mCurrentScale >= 1.0) {

                        if (mCurrentScale > 3.0) {
                            mWindowLayout.setBackgroundColor(Color.argb((int) 200, 0, 0, 0));
                        } else {
                            mWindowLayout.setBackgroundColor(Color.argb((int) (200 * (mCurrentScale - 1.0) / 2.0f), 0, 0, 0));
                        }
                    } else {
                        mWindowLayout.setBackgroundColor(Color.argb(0, 0, 0, 0));
                    }

                }

                mDragIV.setImageMatrix(mDrawMatrix);


                return false;
            }

            @Override
            public boolean onScaleBegin(ScaleGestureDetector detector) {
                Log.e("ivd", "scale begin" + detector.getScaleFactor());
                LastX = detector.getFocusX();
                LastY = detector.getFocusY();

                disx = 0;
                disy = 0;
                //  if(detector.getScaleFactor()>1.0) {
                scaleTemp = detector.getScaleFactor();

                ZoomInImageView.this.setDrawingCacheEnabled(true);
                // 从缓存中获取bitmap
                mBitmap = Bitmap.createBitmap(ZoomInImageView.this.getDrawingCache());
                // 释放绘图缓存，避免出现重复的缓存对象
                ZoomInImageView.this.destroyDrawingCache();


                showIv();
                //   }
                return true;
            }

            @Override
            public void onScaleEnd(ScaleGestureDetector detector) {
                scaleTemp = detector.getScaleFactor();

                //  getParent().requestDisallowInterceptTouchEvent(false);
                onReleaseZoom();
                Log.e("ivd", "scale end");
            }


        });
        mGesture = new GestureDetector(this.getContext(), new GestureDetector.OnGestureListener() {


            @Override
            public boolean onDown(MotionEvent e) {
                return false;
            }

            @Override
            public void onShowPress(MotionEvent e) {

            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                performClick();
                return false;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                return false;
            }

            @Override
            public void onLongPress(MotionEvent e) {
                performLongClick();
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                return false;
            }
        });


    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {


        if (event.getPointerCount() >= 2) {

            //if touch by more than two finger  ,handle by itself
            getParent().requestDisallowInterceptTouchEvent(true);

        } else {
            getParent().requestDisallowInterceptTouchEvent(false);


        }

        return super.dispatchTouchEvent(event);
    }


    /**
     * On  release zoom
     */
    private void onReleaseZoom() {


        if (mDragIV != null) {
            float cx = mOffsetToLeft + mBitmap.getWidth() / 2.0f;
            float cy = mOffsetToTop + mBitmap.getHeight() / 2.0f;
            mDragIV.post(new AnimatedZoomRunnable(getScale(), 1,
                    cx, cy, disx, disy));
        }
    }


    /**
     * 移除镜像
     */
    private void removeDragImage() {
        if (mDragIV != null) {
            mWindowManager.removeView(mWindowLayout);
            mDragIV = null;
        }

    }

    Matrix mBaseMatrix = new Matrix();

    public void showIv() {

        //isDrag = true; // 设置可以拖拽
//            mVibrator.vibrate(100); // 震动100毫秒

        Log.i("CanDragListView", "**mLongClickRunnable**");
        // 根据我们按下的点显示item镜像
        createDragImage(mBitmap, mOffsetToLeft, mOffsetToTop);

    }


    /**
     * 创建拖动的镜像
     *
     * @param bitmap
     * @param downX  按下的点相对父控件的X坐标
     * @param downY  按下的点相对父控件的X坐标
     */
    private void createDragImage(Bitmap bitmap, int downX, int downY) {
        mWindowLayoutParams = new WindowManager.LayoutParams();
        mWindowLayoutParams.format = PixelFormat.RGBA_8888; // 图片之外的其他地方透明


        mWindowLayoutParams.alpha = 1f; // 透明度  0.55
        mWindowLayoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        mWindowLayoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;


        mWindowLayoutParams.type = WindowManager.LayoutParams.TYPE_TOAST;


        mWindowLayout = LayoutInflater.from(this.getContext()).inflate(R.layout., null);

        mWindowLayout.setClickable(true);

        mDragIV = (ImageView) mWindowLayout.findViewById(R.id.iv_pic);
        mDragIV.setImageBitmap(bitmap);



        mBaseMatrix.reset();
        Log.e("checksum", downX + " " +
                downY + "  " +
                mOffsetToTop + " " +
                mOffsetToLeft + " "
        );

        Log.e("checksum", (getWidth() - bitmap.getWidth()) / 2F + "  " +
                (getHeight() - bitmap.getHeight()) / 2F + "  " +
                getX() + " " +
                getY() + " " +
                getPivotX() + " " +
                getPivotY() + " " +
                getTranslationX() + " " +
                getTranslationY()
        );

        mSuppMatrix.reset();
        mBaseMatrix.postTranslate(mOffsetToLeft,
                mOffsetToTop - getStatusHeight(this.getContext()));

        mDrawMatrix.set(mBaseMatrix);

        mDragIV.setScaleType(ScaleType.MATRIX);

        mDragIV.setImageMatrix(mDrawMatrix);





        mWindowLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                if (ZoomInImageView.this != null) {
                    ZoomInImageView.this.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            ZoomInImageView.this.setVisibility(View.INVISIBLE);// 隐藏该item
                        }
                    }, 100);

                }
            }
        });


        mWindowManager.addView(mWindowLayout, mWindowLayoutParams);


    }

    Matrix mDrawMatrix = new Matrix();


    /**
     * 获取状态栏的高度
     *
     * @param context
     * @return
     */
    private static int getStatusHeight(Context context) {
        int statusHeight = 0;
        Rect localRect = new Rect();
        ((Activity) context).getWindow().getDecorView().getWindowVisibleDisplayFrame(localRect);
        statusHeight = localRect.top;
        if (0 == statusHeight) {
            Class<?> localClass;
            try {
                localClass = Class.forName("com.android.internal.R$dimen");
                Object localObject = localClass.newInstance();
                int i5 = Integer.parseInt(localClass.getField("status_bar_height").get(localObject).toString());
                statusHeight = context.getResources().getDimensionPixelSize(i5);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return statusHeight;
    }



    @Override
    public boolean onTouchEvent(MotionEvent event) {



        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:


                mOffsetToTop = (int) (event.getRawY() - event.getX());
                mOffsetToLeft = (int) (event.getRawX() - event.getY());


                break;
            case MotionEvent.ACTION_MOVE:

                break;
            case MotionEvent.ACTION_UP:

                break;
            case MotionEvent.ACTION_CANCEL:

                break;
            default:
                break;
        }
        mGesture.onTouchEvent(event);

        boolean res = false;


        res = mScaleGesture.onTouchEvent(event);
        return res;
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

    public void show() {

        setVisibility(View.VISIBLE);


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
            ImageView imageView = mDragIV;
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

            mDrawMatrix.set(mBaseMatrix);
            mDrawMatrix.postConcat(mSuppMatrix);

            mDragIV.setImageMatrix(mDrawMatrix);

            if (time < 0.8f) {
                AnimCompat.postOnAnimation(imageView, this);


            }

            if (time >= 0.8f) {

                show();
                removeDragImage();


            }

        }


    }


}
