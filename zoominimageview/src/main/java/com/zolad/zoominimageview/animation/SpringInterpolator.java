package com.example.zhonghm.opengllearnproject.animation;

import android.view.animation.Interpolator;

/**
 * A Spring Interpolator
 */

public class SpringInterpolator implements Interpolator {

    private float factor;

    public SpringInterpolator(float factor) {
        this.factor = factor;
    }

    @Override
    public float getInterpolation(float input) {

        return (float) (Math.pow(2, -10 * input) * Math.sin((input - factor / 4) * (2 * Math.PI) / factor) + 1);
    }





}