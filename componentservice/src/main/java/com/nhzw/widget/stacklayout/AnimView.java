package com.nhzw.widget.stacklayout;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.nhzw.service.AppService;
import com.xinayida.lib.utils.DisplayUtils;

/**
 * Created by ww on 2017/9/21.
 */

public class AnimView extends ViewGroup {
    private static final boolean DEBUG_MODE = false;
    private float[] debugPoint;

    private static final int WIDTH = 40;
    private static final int HEIGHT = 40;
    private static final int MAX_STEP = 64;
    private static final int NUM_OF_POINT = 8;
    public static final int DURATION = 400;//ms
    private static final float FOLD_SCALE = 30f / 41;
    private Point offset;
    private Paint mPaint;
    private MeshCalculator mesh;
    private Matrix mMatrix;
    private int imgWidth;
    private int imgHeight;
    private Bitmap mBitmap;
    private int screenW;
    private int screenH;
    private float[] foldSrc = new float[NUM_OF_POINT];
    private float[] foldDst = new float[NUM_OF_POINT];

    private boolean slideUp;//向上翻页或者向下翻

    //    private Matrix mShadowGradientMatrix;
//    private LinearGradient mShadowGradientShader;
//    private Paint mShadowPaint;
    private Paint mBackPaint;

    public AnimView(Context context) {
        super(context);
        init();
    }

    public AnimView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AnimView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setFocusable(false);
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        screenW = outMetrics.widthPixels;
        screenH = outMetrics.heightPixels;
    }

    private void setup() {
        if (mBitmap == null) {
            return;
        }
        imgWidth = mBitmap.getWidth();//getDrawable().getIntrinsicWidth();
        imgHeight = mBitmap.getHeight();//getDrawable().getIntrinsicHeight();
        offset = new Point((getMeasuredWidth() - imgWidth) / 2, DisplayUtils.dip2px(AppService.instance.getAppContext(), 27) + DisplayUtils.getStatusHeight(AppService.instance.getAppContext()));
//        offset = new Point(0, 0);
        initMesh();
        initMatrix();
    }

    private void initMesh() {
        mPaint = new Paint();
        debugPoint = new float[2];
        mesh = new MeshCalculator(offset, WIDTH, HEIGHT, MAX_STEP);
        mesh.setBitmapSize(imgWidth, imgHeight);

        buildPaths(screenW - DisplayUtils.dip2px(AppService.instance.getAppContext(),16), 0);
        mesh.buildMeshes(imgWidth, imgHeight);
    }

    private void buildPaths(float endX, float endY) {
        debugPoint[0] = endX;
        debugPoint[1] = endY;
        mesh.buildPaths(endX, endY);
    }

    private void initMatrix() {
        mMatrix = new Matrix();
        foldSrc = new float[NUM_OF_POINT];
        foldDst = new float[NUM_OF_POINT];
        float foldY = imgHeight * FOLD_SCALE;
        foldSrc[0] = 0;
        foldSrc[1] = 0;
        foldSrc[2] = imgWidth;
        foldSrc[3] = 0;
        foldSrc[4] = 0;
        foldSrc[5] = foldY;
        foldSrc[6] = imgWidth;
        foldSrc[7] = foldY;
        mBackPaint = new Paint();
        mBackPaint.setARGB(255, 240, 240, 240);
//        mShadowPaint = new Paint();
//        mShadowPaint.setStyle(Paint.Style.FILL);
//        mShadowGradientShader = new LinearGradient(0, 0, 0, 1f,
//                Color.BLACK, Color.TRANSPARENT, Shader.TileMode.CLAMP);
//        mShadowPaint.setShader(mShadowGradientShader);
//
//        mShadowGradientMatrix = new Matrix();
//        mShadowGradientMatrix.setScale(1, imgHeight);
//        mShadowGradientShader.setLocalMatrix(mShadowGradientMatrix);
//        mShadowPaint.setAlpha((int) (0.9 * 255));
    }


    private void stepMatrix(float step) {
        double radian = step * Math.PI / MAX_STEP;
//            Log.d("Stefan", "step: " + step + " deltaX:" + deltaX + " deltaY:" + deltaY);
        float foldY = imgHeight * FOLD_SCALE;
        float leftY = imgHeight - foldY;
        float disY = (foldY - leftY) * step / MAX_STEP;
        float deltaX = (float) (foldY * Math.sin(radian) * Math.tan(Math.PI / 8));
        float deltaY = (float) ((foldY - disY) * Math.cos(radian));

        foldDst[0] = -deltaX;
        foldDst[1] = imgHeight - deltaY - leftY;
        foldDst[2] = imgWidth + deltaX;
        foldDst[3] = imgHeight - deltaY - leftY;
        foldDst[4] = 0;
        foldDst[5] = imgHeight - leftY;
        foldDst[6] = imgWidth;
        foldDst[7] = imgHeight - leftY;
//        mShadowPaint.setAlpha((int) (step * 255 / MAX_STEP));
        mMatrix.setPolyToPoly(foldSrc, 0, foldDst, 0, NUM_OF_POINT >> 1);

    }

    private ValueAnimator vanim = ValueAnimator.ofFloat(0, 4, MAX_STEP - 4, MAX_STEP);
    private boolean startAnim = false;

    public boolean startAnim(boolean slideup, AnimatorEndListener listener) {
        if (vanim.isStarted() || vanim.isRunning()) return false;
        startAnim = true;
        slideUp = slideup;
        vanim.setDuration(DURATION);
        vanim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if (mesh != null) {
                    if (slideUp) {
                        mesh.buildMeshes((float) animation.getAnimatedValue());
                    } else {
                        stepMatrix((float) animation.getAnimatedValue());
                    }
                    invalidate();
                }
            }
        });
        vanim.addListener(listener);
//        if (reverse) {
//            vanim.reverse();
//        } else {
        vanim.start();
//        }
        return true;
    }

    public static abstract class AnimatorEndListener implements Animator.AnimatorListener {
        @Override
        public void onAnimationStart(Animator animator) {

        }

        @Override
        public void onAnimationCancel(Animator animator) {

        }

        @Override
        public void onAnimationRepeat(Animator animator) {

        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        View child = getChildAt(0);
        measureChild(child, widthMeasureSpec, heightMeasureSpec);
//        setMeasuredDimension(child.getMeasuredWidth(),
//                child.getMeasuredHeight());
        setMeasuredDimension(screenW, screenH);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
//        Log.d("Stefan", "onLayout " + getTag(R.id.tag_first) + " " + l + " " + ' ' + t + ' ' + r + ' ' + b);
        if (mBitmap == null) {
            View child = getChildAt(0);
            mBitmap = Bitmap.createBitmap(child.getMeasuredWidth(), child.getMeasuredHeight(),
                    Bitmap.Config.ARGB_8888);
            mCanvas = new Canvas(mBitmap);
            isReady = false;
            setup();
            child.layout(offset.x, offset.y, offset.x + child.getMeasuredWidth(), offset.y + child.getMeasuredHeight());
        }
    }

    private Canvas mCanvas;
    private boolean isReady;

    @Override
    protected void dispatchDraw(Canvas canvas) {
        if (!startAnim) {
            super.dispatchDraw(canvas);
            return;
        }
        if (isReady) {
            if (slideUp) {
                canvas.save();
                canvas.drawBitmapMesh(mBitmap,
                        mesh.getWidth(),
                        mesh.getHeight(),
                        mesh.getVertices(),
                        0, null, 0, mPaint);

                // Draw the target point.
                if (DEBUG_MODE) {
                    mPaint.setColor(Color.RED);
                    mPaint.setStrokeWidth(2);
                    mPaint.setAntiAlias(true);
                    mPaint.setStyle(Paint.Style.FILL);
                    canvas.drawCircle(debugPoint[0], debugPoint[1], 5, mPaint);
                    // Draw the mesh vertices.
                    canvas.drawPoints(mesh.getVertices(), mPaint);
                    // Draw the paths
                    mPaint.setColor(Color.BLUE);
                    mPaint.setStyle(Paint.Style.STROKE);
                    Path[] paths = mesh.getPaths();
                    for (Path path : paths) {
                        canvas.drawPath(path, mPaint);
                    }
                }
                canvas.restore();
            } else {
                canvas.translate(offset.x, offset.y);
                canvas.save();
                canvas.clipRect(0, imgHeight * FOLD_SCALE, imgWidth, imgHeight);
                canvas.drawBitmap(mBitmap, 0, 0, null);
//                if (foldDst[5] > foldDst[1]) {
//                    canvas.drawRect(0, imgHeight * FOLD_SCALE, imgWidth, imgHeight, mShadowPaint);
//                }
                canvas.restore();
                canvas.save();
                canvas.clipRect(foldDst[0], Math.min(foldDst[1], foldDst[5]), foldDst[2], Math.max(foldDst[3], foldDst[7]));
                canvas.concat(mMatrix);
                if (foldDst[5] > foldDst[1]) {
                    canvas.drawBitmap(mBitmap, 0, 0, null);
//                    canvas.drawRect(0, 0, imgWidth, imgHeight * FOLD_SCALE, mShadowPaint);
                } else {
                    canvas.drawRect(0, 0, imgWidth, imgHeight * FOLD_SCALE, mBackPaint);
                }
                canvas.restore();
            }

        } else {
            mCanvas.translate(-offset.x, -offset.y);
            super.dispatchDraw(mCanvas);
            canvas.drawBitmap(mBitmap, offset.x, offset.y, null);
            isReady = true;
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (vanim != null && vanim.isRunning()) {
            vanim.cancel();
        }
        if (mBitmap != null) {
            mBitmap.recycle();
            mBitmap = null;
            mCanvas = null;
            mesh = null;
            mMatrix = null;
        }
    }
}
