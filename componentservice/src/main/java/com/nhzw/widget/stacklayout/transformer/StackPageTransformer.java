package com.nhzw.widget.stacklayout.transformer;

import android.view.View;
import android.view.animation.DecelerateInterpolator;

import com.nhzw.service.AppService;
import com.nhzw.widget.stacklayout.StackLayout;
import com.xinayida.lib.utils.DisplayUtils;

/**
 * 堆叠效果
 */
public final class StackPageTransformer extends StackLayout.PageTransformer {
    private float scaleStep; // 缩放步长
    private float disYStep;  // Y轴移动步长
    private float rate = 0.95f;

    /**
     * @param minScale 栈底: 最小页面缩放比
     * @param maxScale 栈顶: 最大页面缩放比
     */
    public StackPageTransformer(float minScale, float maxScale) {
        float mMinScale = minScale;
        float mMaxScale = maxScale;

        if (mMaxScale < mMinScale)
            throw new IllegalArgumentException("The Argument: maxScale must bigger than minScale !");
        scaleStep = (mMaxScale - mMinScale) / MAX_PAGE;
        disYStep = DisplayUtils.dip2px(AppService.instance.getAppContext(), 11);
    }

    public StackPageTransformer() {
        this(0.6f, 1f);
    }

    public final void transformPage(View view, float position, boolean hideLast) {
        if (Math.ceil(position) >= MAX_PAGE) {
            view.setVisibility(View.GONE);
        } else if (position >= 0) {
            view.setVisibility(View.VISIBLE);
        }
        View parent = (View) view.getParent();
        int pageHeight = parent.getMeasuredHeight();

        float scaleDis = position * scaleStep;
        float scale = 1 - scaleDis;
        float transY = -(disYStep - DisplayUtils.dip2px(AppService.instance.getAppContext(), position)) * position - scaleDis / 2 * pageHeight;
        view.animate().translationY(transY).scaleX(scale).scaleY(scale).setDuration(0).start();
    }

    @Override
    public void animate(View view, int position) {
        View parent = (View) view.getParent();
        int pageHeight = parent.getMeasuredHeight();
        if (position > MAX_PAGE) {
            view.setVisibility(View.GONE);
        } else if (position > 0) {
            view.setVisibility(View.VISIBLE);
        }
        float scaleDis = (position - 1) * scaleStep;
        float scale = 1 - scaleDis;
        float transY = -(disYStep - DisplayUtils.dip2px(AppService.instance.getAppContext(), position - 1)) * (position - 1) - scaleDis / 2 * pageHeight;
//            Log.d("Stefan", "index: " + index + " scale: " + scale + " transY: " + transY);
        view.animate().translationY(transY).scaleX(scale).scaleY(scale).setDuration(500).alpha(1f).setStartDelay(position * 50).setInterpolator(new DecelerateInterpolator(1.5f)).start();
    }
}