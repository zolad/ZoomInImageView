package com.zolad.zoominimageview.gestures;

import android.content.Context;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

public class ScaleAndMoveDetector  implements ScaleGestureDetector.OnScaleGestureListener{

    private float scaleStart = 1;
    private float LastX = 0;
    private float LastY = 0;
    private float moveDisFromX = 0f;
    private float moveDisFromY = 0f;
    private float mCurrentScale = 1f;

    private final ScaleGestureDetector mDetector;
    private OnScaleAndMoveGestureListener mDetectorListener;


    public ScaleAndMoveDetector(Context context,OnScaleAndMoveGestureListener detectorListener) {

        mDetector = new ScaleGestureDetector(context,this);

        mDetectorListener = detectorListener;
    }

    @Override
    public boolean onScale(ScaleGestureDetector detector) {


        float tempx = detector.getFocusX();
        float tempy = detector.getFocusY();
        moveDisFromX = tempx - LastX;
        moveDisFromY = tempy - LastY;
        if(mDetectorListener!=null)
            mDetectorListener.onScaleAndMove(detector,getCurrentScale(detector.getScaleFactor()),moveDisFromX,moveDisFromY);
        return false;
    }

    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector) {
        LastX = detector.getFocusX();
        LastY = detector.getFocusY();

        moveDisFromX = 0;
        moveDisFromY = 0;

        scaleStart = detector.getScaleFactor();

        if(mDetectorListener!=null)
            mDetectorListener.onScaleBegin(detector);
        return true;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector detector) {

        if(mDetectorListener!=null)
            mDetectorListener.onScaleEnd(detector,moveDisFromX,moveDisFromY);

    }

    public  void  setStartScale(float scaleFactor){

        scaleStart = scaleFactor;
    }


    private   float getCurrentScale(float scaleFactor){

        mCurrentScale = scaleFactor * (1f / scaleStart);
        return  mCurrentScale;


    }


    public boolean onTouchEvent(MotionEvent ev) {
       return mDetector.onTouchEvent(ev);
    }


}
