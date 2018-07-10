package com.zolad.zoominimageview.window;

import android.os.Build;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.WindowManager;


/**
 * A Util for WindowManager run safety
 */


public class WindowManagerUtil {


    public static synchronized void removeViewSafety(@NonNull WindowManager windowManager, @NonNull View viewNeedRemove) {


        if (windowManager == null || viewNeedRemove == null)
            return;


        if (Looper.myLooper() != Looper.getMainLooper()) {
            // Current thread is not the UI/Main thread

            return;

        }


        // Check  is the view attaching
        if (isAttachedToWindow(viewNeedRemove)) {

            try {
                windowManager.removeView(viewNeedRemove);

            } catch (Exception e) {

            }

            return;
        }

        try {
            windowManager.removeView(viewNeedRemove);


        } catch (Exception e) {

        }

    }


    public static synchronized void addViewSafety(@NonNull WindowManager windowManager, @NonNull View viewNeedAdd, @NonNull WindowManager.LayoutParams params) {


        if (windowManager == null || viewNeedAdd == null || params == null)
            return;


        if (Looper.myLooper() != Looper.getMainLooper()) {
            // Current thread is not the UI/Main thread

            return;

        }


        if (isAttachedToWindow(viewNeedAdd)) {

            try {
                windowManager.addView(viewNeedAdd, params);

            } catch (Exception e) {

            }


        }


    }

    /**
     * Check  view is  attach to window
     */
    public static boolean isAttachedToWindow(View view) {

        boolean isAlreadyAttach = false;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

            isAlreadyAttach = view.isAttachedToWindow();

        } else {

            isAlreadyAttach = (view.getWindowToken() == null);

        }

        return isAlreadyAttach;
    }


}
