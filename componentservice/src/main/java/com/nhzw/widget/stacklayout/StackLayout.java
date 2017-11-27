package com.nhzw.widget.stacklayout;

import android.animation.Animator;
import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.mcxtzhang.commonadapter.viewgroup.adapter.base.BaseAdapter;
import com.nhzw.utils.DoubleClickController;
import com.nhzw.widget.stacklayout.transformer.StackPageTransformer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 层叠卡片控件
 */
public class StackLayout extends FrameLayout {
    private int screenW;
    private int screenH;
    private GestureDetector detector;
    private boolean isPressed;
    private int pointID;

    public StackLayout(Context context) {
        super(context);
        init(context);
    }

    public StackLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public StackLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        screenW = outMetrics.widthPixels;
        screenH = outMetrics.heightPixels;
        detector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            final float FLING_MIN_DISTANCE = 20;
            final float FLING_MIN_VELOCITY = 200;

            @Override
            /*
             * 用户按下触摸屏、快速移动后松开即触发这个事件
             * e1：第1个ACTION_DOWN MotionEvent
             * e2：最后一个ACTION_MOVE MotionEvent
             * velocityX：X轴上的移动速度，像素/秒
             * velocityY：Y轴上的移动速度，像素/秒
             * 触发条件 ：
             * X轴的坐标位移大于FLING_MIN_DISTANCE，且移动速度大于FLING_MIN_VELOCITY个像素/秒
             */
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                float disx = e2.getX() - e1.getX();
                float disy = e2.getY() - e1.getY();
                Point point = new Point((int) e1.getX(), (int) e1.getY());
                if (isTouchPointInView(point) && Math.abs(disx) < Math.abs(disy)) {
                    if (disy > FLING_MIN_DISTANCE
                            && velocityY > FLING_MIN_VELOCITY) {//向下滑动
                        slide(false);
                        return true;
                    } else if (disy < -FLING_MIN_DISTANCE
                            && velocityY < -FLING_MIN_VELOCITY) {//向上滑动
                        slide(true);
                        return true;
                    }
                }
                return false;
            }

            @Override
            public void onShowPress(MotionEvent e) {
//                Log.d("Stefan", "onShowPress");
            }

            @Override
            public void onLongPress(MotionEvent e) {
                if (isTouchPointInView(new Point((int) e.getX(), (int) e.getY()))) {
                    isPressed = true;
                    int count = getChildCount();
                    if (count > 0) {
                        mViewDragHelper.captureChildView(getChildAt(count - 1), pointID);
                    }
                }
//                Log.d("Stefan", "onLongPress");
            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
//                Log.d("Stefan", "onSingleTapUp");
                Point p = new Point((int) e.getX(), (int) e.getY());
                if (isTouchPointInView(p) && onRemoveListener != null) {
                    onRemoveListener.onClick();
                }
                return true;
            }

            @Override
            public boolean onDown(MotionEvent e) {
                pointID = e.getPointerId(0);
//                Log.d("Stefan", "onDown");
                return true;
            }

        });
    }

    // ------ Adapter ------
    private BaseAdapter mAdapter;

    public void setAdapter(@NonNull BaseAdapter adapter) {
        mAdapter = adapter;
    }

    // ------ 事件分发 ------
    private ViewDragHelper mViewDragHelper = ViewDragHelper.create(this, new ViewDragHelper.Callback() {
        private View mParent = StackLayout.this;

        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            return false;
        }

        @Override
        public int getViewVerticalDragRange(View child) {
            return mParent.getHeight();
        }

        @Override
        public int getViewHorizontalDragRange(View child) {
            return mParent.getWidth();
        }

        @Override
        public int clampViewPositionVertical(View child, int top, int dy) {
            return top;
        }

        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            return left;
        }

        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            float moveX = Math.abs((float) top / mParent.getHeight());
            float moveY = Math.abs((float) left / mParent.getWidth());
//            transformPage(Math.max(moveX, moveY), true);
        }

        /**
         * 手指释放的时候回调
         * @param releasedChild 释放的元素
         * @param xvel x方向的加速度 左负右正
         * @param yvel y方向加速度 上负下正
         */
        @Override
        public void onViewReleased(final View releasedChild, float xvel, float yvel) {
            isPressed = false;
//            Log.d("Stefan", "childPosition: " + releasedChild.getLeft() + "  " + releasedChild.getTop() + "  " + releasedChild.getRight() + " " + releasedChild.getBottom());
//            Log.d("Stefan", "onViewReleased xvel: " + xvel + " yvel:" + yvel);
            mViewDragHelper.smoothSlideViewTo(releasedChild, 0, 0);
            ViewCompat.postOnAnimation(releasedChild, new SettleRunnable(releasedChild));
        }
    });

    private class SettleRunnable implements Runnable {
        private final View mView;

        public SettleRunnable(View view) {
            mView = view;
        }

        @Override
        public void run() {
            if (mViewDragHelper != null) {
                if (mViewDragHelper.continueSettling(true)) {
                    ViewCompat.postOnAnimation(mView, this);    // 递归调用
                }
            }
        }
    }

    /**
     * 滑动动画
     *
     * @param up true:上，false:下
     */
    public void slide(final boolean up) {
        if (mAdapter == null || DoubleClickController.isDoubleClick("slide", AnimView.DURATION))
            return;
        final View releasedChild = getChildAt(getChildCount() - 1);
        if (releasedChild == null) {
            return;
        }
        if (onRemoveListener != null) {
            onRemoveListener.onStart(up);
        }
        ((AnimView) releasedChild).startAnim(up, new AnimView.AnimatorEndListener() {
            @Override
            public void onAnimationEnd(Animator animator) {
                if (mAdapter == null) return;
                ViewCompat.postInvalidateOnAnimation(releasedChild);
                mAdapter.getDatas().remove(mAdapter.getCount() - 1);
                removeView(releasedChild);
                if (onRemoveListener != null) {
                    onRemoveListener.onRemoved(up, mAdapter.getCount());
                }
            }
        });
        animatePage();
    }

    // 判断一个具体的触摸点是否在 view 上；
    public boolean isTouchPointInView(Point point) {
        if (point == null) {
            throw new NullPointerException();
        }
        if (getChildAt(getChildCount() - 1) == null) {
            return false;
        }
        ViewGroup vg = ((ViewGroup) getChildAt(getChildCount() - 1));
        View releasedChild = vg.getChildAt(0);
        Rect rect = new Rect();
        int location[] = new int[2];
        vg.getLocationInWindow(location);
        releasedChild.getGlobalVisibleRect(rect);
        rect.top -= location[1];
        rect.bottom -= location[1];
        return rect.contains(point.x, point.y);
//        return point.x >= rect.left && point.x <= rect.right && point.y >= rect.top && point.y <= rect.bottom;
    }

    private ViewDragHelper getViewDragHelper() {
        return mViewDragHelper;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        return getViewDragHelper().shouldInterceptTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isPressed) {
            return detector.onTouchEvent(event);
        } else {
            getViewDragHelper().processTouchEvent(event);
            return true;
        }
    }

    // ------ PageTransformer ------
    private List<PageTransformer> mPageTransformerList = new ArrayList<>();

    public void addPageTransformer(PageTransformer... pageTransformerList) {
        mPageTransformerList.addAll(Arrays.asList(pageTransformerList));
    }

    /**
     * 卡片滑动动画接口, 类似 {@link ViewPager.PageTransformer}
     */
    public static abstract class PageTransformer {
        protected static final int MAX_PAGE = 5;//页数

        /**
         * 直接设置位置
         *
         * @param page     各卡片的根布局
         * @param position 各卡片的位置
         * @param hideLast 隐藏最后一个
         */
        public abstract void transformPage(View page, float position, boolean hideLast);

        /**
         * 带动画效果移动
         *
         * @param page
         * @param position
         */
        public void animate(View page, int position) {

        }
    }

    public void animatePage() {
        if (mAdapter == null) return;
        List<PageTransformer> list = new ArrayList<>(mPageTransformerList); // 保护性复制, 防止污染原来的list
        if (list.isEmpty())
            list.add(new StackPageTransformer());   // default PageTransformer

        int itemCount = mAdapter.getCount();
        if (itemCount > 1) {
            for (int i = itemCount - 2; i >= 0; i--) {
                View page = getChildAt(i);
                for (PageTransformer pageTransformer : list) {
                    pageTransformer.animate(page, itemCount - 1 - i);
                }
            }
        }
    }

    public void transformPage() {
        if (mAdapter != null) {
            mAdapter.notifyDatasetChanged();
        }
        transformPage(0, false);
    }

    private void transformPage(float topPagePos, boolean hideLast) {
        if (mAdapter == null) return;
        List<PageTransformer> list = new ArrayList<>(mPageTransformerList); // 保护性复制, 防止污染原来的list
        if (list.isEmpty())
            list.add(new StackPageTransformer());   // default PageTransformer

        int itemCount = mAdapter.getCount();
        for (int i = 0; i < itemCount; i++) {
            View page = getChildAt(i);
            if (page == null)
                return;
            for (PageTransformer pageTransformer : list) {
                pageTransformer.transformPage(page, itemCount - 1 - i - Math.abs(topPagePos), hideLast);
            }
        }
    }

//    private boolean isFirstLayout = true;
//
//    @Override
//    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
//        super.onLayout(changed, left, top, right, bottom);
//        if (isFirstLayout) {
//            transformPage(0, false);
//            isFirstLayout = false;
//        }
//    }

    // ------ OnRemoveListener ------
    @NonNull
    private OnRemoveListener onRemoveListener;

    public void setOnRemoveListener(@NonNull OnRemoveListener onSwipeListener) {
        onRemoveListener = onSwipeListener;
    }

    /**
     * 滑动事件监听
     */
    public interface OnRemoveListener {

        /**
         * 已被移除屏幕时回调. 另外, 可以根据 itemLeft, 决定何时加载更多.
         *
         * @param isSwipeUp true: 往上滑动 false: 往下滑动
         * @param itemLeft  当前剩余的item个数 (栈中的 + 待显示的)
         */
        void onRemoved(boolean isSwipeUp, int itemLeft);

        /**
         * 动画开始回调
         *
         * @param isSwipeUp true: 往上滑动 false: 往下滑动
         */
        void onStart(boolean isSwipeUp);

        void onClick();
    }
}

