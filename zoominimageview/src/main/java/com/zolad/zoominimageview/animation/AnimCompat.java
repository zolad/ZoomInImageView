package com.example.zhonghm.opengllearnproject.animation;

import android.os.Build;
import android.view.View;

/**
 * A Compat tool for view.postOnAnimation
 */

public class AnimCompat {

    private static final int SIXTY_FPS_INTERVAL = 1000 / 60;

    public static void postOnAnimation(View view, Runnable runnable) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            view.postOnAnimation(runnable);
        } else {
            view.postDelayed(runnable, SIXTY_FPS_INTERVAL);

        }
    }




}
