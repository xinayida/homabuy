package com.nhzw.widget.stacklayout.transformer;

import android.view.View;

import com.nhzw.widget.stacklayout.StackLayout;

/**
 * 透明度渐变
 */
public final class AlphaTransformer extends StackLayout.PageTransformer {
    private float mMaxAlpha;
    private float alphaStep;

    public AlphaTransformer(float minAlpha, float maxAlpha) {
        mMaxAlpha = maxAlpha;
        alphaStep = (maxAlpha - minAlpha) / MAX_PAGE;
    }

    public AlphaTransformer() {
        this(0.2f, 1f);
    }

    @Override
    public void transformPage(View view, float position, boolean hideLast) {
        view.setAlpha(Math.min(1.0f, mMaxAlpha - alphaStep * position));
    }

    @Override
    public void animate(View view, int position) {
        if (position == MAX_PAGE) {
            view.setAlpha(0f);
        }
        float alpha = Math.min(1.0f, mMaxAlpha - alphaStep * (position - 1));
        view.animate().alpha(alpha).setStartDelay(position * 50).start();
    }
}