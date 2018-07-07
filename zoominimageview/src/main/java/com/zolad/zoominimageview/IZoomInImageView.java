package com.example.zhonghm.opengllearnproject;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.ImageView;

/**
 * Created by zhonghm on 2018/6/27.
 */

public class TestImageView extends ImageView {

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
     * 按下的点到所在item的上边缘的距离
     */
    private int mPoint2ItemTop;

    /**
     * 按下的点到所在item的左边缘的距离
     */
    private int mPoint2ItemLeft;

    /**
     * CanDragListView距离屏幕顶部的偏移量
     */
    private int mOffset2Top;
    /**
     * CanDragListView自动向下滚动的边界值
     */
    private int mDownScrollBorder;

    /**
     * CanDragListView自动向上滚动的边界值
     */
    private int mUpScrollBorder;
    /**
     * CanDragListView自动滚动的速度
     */
    private static final int speed = 20;

    /**
     * CanDragListView距离屏幕左边的偏移量
     */
    private int mOffset2Left;
    /**
     * 状态栏的高度
     */
    private int mStatusHeight;
    /**
     * 按下的系统时间
     */
    private long mActionDownTime = 0;
    /**
     * 移动的系统时间
     */
    private long mActionMoveTime = 0;
    /**
     * 默认长按事件时间是1000毫秒
     */
    private long mLongClickTime = 1000;
    /**
     * 是否可拖拽，默认为false
     */
    private boolean isDrag = false;
    /**
     * 按下是的x坐标
     */
    private int mDownX;
    /**
     * 按下是的y坐标
     */
    private int mDownY;


    public TestImageView(Context context) {
        super(context);
        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        mStatusHeight = getStatusHeight(context);
        //super.setScaleType(ScaleType.MATRIX);
    }

    public TestImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        mStatusHeight = getStatusHeight(context);
        //super.setScaleType(ScaleType.MATRIX);
    }


    public TestImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        mStatusHeight = getStatusHeight(context);
       // super.setScaleType(ScaleType.MATRIX);
    }
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        // if (getCurrentItem() != 0) {
        getParent().requestDisallowInterceptTouchEvent(true);// 用getParent去请求,
        // 不拦截
//        } else {// 如果是第一个页面, 请求父控件拦截
//            getParent().requestDisallowInterceptTouchEvent(false);// 拦截
//        }
        return super.dispatchTouchEvent(ev);
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {


        Log.e("motion", event.getAction() + " " + event.getX() + event.getY());

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mActionDownTime = event.getDownTime();
                mDownX = (int) event.getX();
                mDownY = (int) event.getY();

//                // 根据按下的坐标获取item对应的position
//                mSelectedPosition = pointToPosition(mDownX, mDownY);
//                // 如果是无效的position，即值为-1
//                if (mSelectedPosition == AdapterView.INVALID_POSITION) {
//                    return super.onTouchEvent(event);
//                }
//                // 根据position获取对应的item
//                mItemView = getChildAt(mSelectedPosition - getFirstVisiblePosition());
//                // 使用Handler延迟mLongClickTime执行mLongClickRunnable
//                mHandler.post(mLongClickRunnable);//, mLongClickTime);
//


                //// TODO: 2018/6/27


                // 下面这几个距离大家可以参考我的博客上面的图来理解下
                mPoint2ItemTop = mDownY;//- this.getTop();
                mPoint2ItemLeft = mDownX;// - this.getLeft();

                mOffset2Top = (int) (event.getRawY() - mDownY);
                mOffset2Left = (int) (event.getRawX() - mDownX);

                // 获取CanDragListView自动向上滚动的偏移量，小于这个值，CanDragListView向下滚动
                mDownScrollBorder = getHeight() / 4;
                // 获取CanDragListView自动向下滚动的偏移量，大于这个值，CanDragListView向上滚动
                mUpScrollBorder = getHeight() * 3 / 4;

                // 将该item进行绘图缓存
                this.setDrawingCacheEnabled(true);
                // 从缓存中获取bitmap
                mBitmap = Bitmap.createBitmap(this.getDrawingCache());
                // 释放绘图缓存，避免出现重复的缓存对象
                this.destroyDrawingCache();
                showIv();

                // Log.i("CanDragListView", "****"+isDrag);
                break;
            case MotionEvent.ACTION_MOVE:
                // TODO
                if (isDrag) {
                    int moveX = (int) event.getX();
                    int moveY = (int) event.getY();
//                    if (!isOnTouchInItem(mItemView, moveX, moveY)) {
//                        mHandler.removeCallbacks(mLongClickRunnable);
//                    }
                    mDownX = moveX;
                    mDownY = moveY;
                    onDragItem(moveX, moveY);
                }
                break;
            case MotionEvent.ACTION_UP:
                onStopDrag();
//                mHandler.removeCallbacks(mLongClickRunnable);
//                mHandler.removeCallbacks(mScrollRunnable);

                isDrag = false;
                break;
            case MotionEvent.ACTION_CANCEL:
                onStopDrag();
//                mHandler.removeCallbacks(mLongClickRunnable);
//                mHandler.removeCallbacks(mScrollRunnable);

                isDrag = false;
                break;
            default:
                break;
        }

        //super.onTouchEvent(event)
        return true;
    }

    /**
     * 拖动item，在里面实现了item镜像的位置更新，item的相互交换以及ListView的自行滚动
     */
    private void onDragItem(int moveX, int moveY) {
//        if (mWindowLayoutParams != null && mDragIV != null) {
//            mWindowLayoutParams.x = moveX - mPoint2ItemLeft + mOffset2Left;
//            mWindowLayoutParams.y = moveY - mPoint2ItemTop + mOffset2Top - mStatusHeight;
//
//
            Matrix mDrawMatrix = new Matrix();
            mDrawMatrix.postTranslate( moveX,moveY);

            mDragIV.setImageMatrix(mDrawMatrix);
//
//         //   mDragIV.setImageAlpha(150);
//
//         //   mWindowManager.updateViewLayout(mDragIV, mWindowLayoutParams); // 更新镜像的位置
//        }
        //onSwapItem(moveX, moveY);
        // ListView自动滚动
        //mHandler.post(mScrollRunnable);

    }

    /**
     * 停止拖拽我们将之前隐藏的item显示出来，并将镜像移除
     */
    private void onStopDrag() {

        if (this != null) {
            this.setVisibility(View.VISIBLE);
        }
        // ((DragAdapter)this.getAdapter()).setItemHide(-1);
        removeDragImage();
    }


    /**
     * 移除镜像
     */
    private void removeDragImage() {
        if (mDragIV != null) {
            mWindowManager.removeView(mLayout);
            mDragIV = null;
        }

    }


    public void showIv() {

        isDrag = true; // 设置可以拖拽
//            mVibrator.vibrate(100); // 震动100毫秒

        Log.i("CanDragListView", "**mLongClickRunnable**");
        // 根据我们按下的点显示item镜像
        createDragImage(mBitmap, mDownX, mDownY);

    }
    View mLayout;

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

//        mWindowLayoutParams.gravity = Gravity.TOP | Gravity.LEFT;
//        mWindowLayoutParams.x = downX;// - mPoint2ItemLeft + mOffset2Left;
//        mWindowLayoutParams.y = downY;// - mPoint2ItemTop + mOffset2Top - mStatusHeight;
//        mWindowLayoutParams.x =0;
//        mWindowLayoutParams.y = 0;
        mWindowLayoutParams.alpha = 0.55f; // 透明度  0.55
        mWindowLayoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        mWindowLayoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;


        mWindowLayoutParams.type = WindowManager.LayoutParams.TYPE_TOAST;
        mWindowLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE

;
      //  mWindowLayoutParams.


//        mNewWindowLayoutParams = new WindowManager.LayoutParams();
//        mNewWindowLayoutParams.format = PixelFormat.RGB_565; // 图片之外的其他地方透明
//        mNewWindowLayoutParams.gravity = Gravity.TOP | Gravity.LEFT;
//        mNewWindowLayoutParams.x = downX - mPoint2ItemLeft + mOffset2Left;
//        mNewWindowLayoutParams.y = downY - mPoint2ItemTop + mOffset2Top - mStatusHeight;
//        // mNewWindowLayoutParams.alpha = 0.55f; // 透明度
//        mNewWindowLayoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
//        mNewWindowLayoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
//        mNewWindowLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;

        mLayout =  LayoutInflater.from(this.getContext()).inflate(R.layout.layout_image, null);






        mDragIV = (ImageView) mLayout.findViewById(R.id.iv_pic);
        mDragIV.setImageBitmap(bitmap);
      Matrix mSuppMatrix = new Matrix();
        //  mSuppMatrix.postTranslate(downX - mPoint2ItemLeft + mOffset2Left, downY - mPoint2ItemTop + mOffset2Top - mStatusHeight);

     // mSuppMatrix.postRotate(90);

        Matrix mBaseMatrix = new Matrix();

        mBaseMatrix.reset();
        Log.e("checksum",downX+" "+
                downY+"  "+
                mPoint2ItemTop+" "+
                mPoint2ItemLeft+" "+
                mOffset2Top+" "+
                mOffset2Left+" "
        );

        Log.e("checksum",(getWidth() - bitmap.getWidth()) / 2F+"  "+
                (getHeight() - bitmap.getHeight()) / 2F + "  "+
                getX()+" "+
                getY()+" "+
                getPivotX() +" "+
                getPivotY()+" "+
                getTranslationX() +" "+
                getTranslationY()
        );


        mBaseMatrix.postTranslate(mOffset2Left,
                mOffset2Top-getStatusHeight(this.getContext()));
//        RectF mTempSrc = new RectF(0, 0, bitmap.getWidth(), bitmap.getHeight());
//        RectF mTempDst = new RectF(0, 0, getImageViewWidth(mDragIV), getImageViewHeight(mDragIV));
//        mBaseMatrix
//               .setRectToRect(mTempSrc,mTempDst, Matrix.ScaleToFit.FILL);

        Matrix  mDrawMatrix = new Matrix();
        mDrawMatrix.set(mBaseMatrix);
      //  mDrawMatrix.postConcat(mSuppMatrix);

        mDragIV.setScaleType(ScaleType.MATRIX);

        mDragIV.setImageMatrix(mDrawMatrix);


       // mDragIV.invalidate();


        mDragIV.getViewTreeObserver().addOnDrawListener(new ViewTreeObserver.OnDrawListener() {
            @Override
            public void onDraw() {
                // TODO Auto-generated method stub
                Log.e("motion222","drag2view draw finish");

                if (TestImageView.this != null) {
                    TestImageView.this.setVisibility(View.INVISIBLE);// 隐藏该item
                }
            }
        });

        mLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Log.e("motion222","drag2view draw finish22");
                if (TestImageView.this != null) {
                    TestImageView.this.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                          TestImageView.this.setVisibility(View.INVISIBLE);// 隐藏该item
                        }
                    },100);

                }
            }
        });

//        postDelayed(new Runnable() {
//            @Override
//            public void run() {
//
//            }
//        },500)
        mWindowManager.addView(mLayout, mWindowLayoutParams);

//        ViewTreeObserver observer = mDragIV.getViewTreeObserver();
//        if (null != observer)
//            observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//                @Override
//                public void onGlobalLayout() {
//
//                }
//            });




    }
    private int getImageViewHeight(ImageView imageView) {
        if (null == imageView)
            return 0;
        return imageView.getHeight() - imageView.getPaddingTop() - imageView.getPaddingBottom();
    }
    private int getImageViewWidth(ImageView imageView) {
        if (null == imageView)
            return 0;
        return imageView.getWidth() - imageView.getPaddingLeft() - imageView.getPaddingRight();
    }
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

}
