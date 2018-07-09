package com.example.zhonghm.opengllearnproject.window;

import android.os.Looper;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.WindowManager;


/**
 * A Util for WindowManager run safety
 */


public class WindowManagerUtil {


    public synchronized void removeViewSafety(@NonNull WindowManager windowManager, @NonNull View viewNeedRemove) {


        if (windowManager == null || viewNeedRemove == null)
            return;


        if (Looper.myLooper() != Looper.getMainLooper()) {
            // Current thread is not the UI/Main thread

            return;

        }


        // Check  is the view attaching
        if (viewNeedRemove.getWindowToken() == null) {

            try {
                windowManager.removeViewImmediate(viewNeedRemove);

            } catch (Exception e) {

            }

            return;
        }

        try {
            windowManager.removeViewImmediate(viewNeedRemove);


        } catch (Exception e) {

        }

    }


}
