package com.nhzw.widget.stacklayout;

/**
 * Created by ww on 2017/9/21.
 */

import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.Point;



public class MeshCalculator {

    private final int maxStep;//动画总共步数
    private float leftStep;
    private float rightStep;
    private Path leftPath;
    private Path rightPath;
    private PathMeasure leftPathMeasure;
    private PathMeasure rightPathMeasure;
    private Point offset;
    private final float[] vertices;
    private int horizontalSplit;
    private int verticalSplit;

    private int imgWidth = -1;
    private int imgHeight = -1;

    public MeshCalculator(Point offset, int width, int height, int max_step) {
        maxStep = max_step;
        this.offset = offset;
        horizontalSplit = width;
        verticalSplit = height;
        vertices = new float[(horizontalSplit + 1) * (verticalSplit + 1) * 2];
        leftPath = new Path();
        rightPath = new Path();
        leftPathMeasure = new PathMeasure();
        rightPathMeasure = new PathMeasure();
    }
    public int getWidth() {
        return horizontalSplit;
    }

    public int getHeight() {
        return verticalSplit;
    }

    public void setBitmapSize(int w, int h) {
        imgWidth = w;
        imgHeight = h;
    }

    public void buildPaths(float endX, float endY) {
        if (imgWidth <= 0 || imgHeight <= 0) {
            throw new IllegalArgumentException(
                    "Bitmap size must be > 0, did you call setBitmapSize(int, int) method?");
        }
        leftPathMeasure.setPath(leftPath, false);
        rightPathMeasure.setPath(rightPath, false);

        float w = imgWidth;
        float h = imgHeight;

        leftPath.reset();
        rightPath.reset();

        leftPath.moveTo(offset.x, offset.y + h);
        leftPath.lineTo(offset.x, offset.y);
        leftPath.quadTo(offset.x, (endY + offset.y) / 2, endX, endY);
        rightPath.moveTo(offset.x + w, offset.y + h);
        rightPath.lineTo(offset.x + w, offset.y);
        rightPath.quadTo(offset.x + w, (endY + offset.y) / 2, endX, endY);
        leftPathMeasure.setPath(leftPath, false);
        rightPathMeasure.setPath(rightPath, false);
        leftStep = leftPathMeasure.getLength() / maxStep;
        rightStep = rightPathMeasure.getLength() / maxStep;
    }

    public float[] getVertices() {
        return vertices;
    }

    public void buildMeshes(float w, float h) {
        int index = 0;
        for (int y = 0; y <= verticalSplit; y++) {
            float fy = y * h / verticalSplit;
            for (int x = 0; x <= horizontalSplit; x++) {
                float fx = x * w / horizontalSplit;
                vertices[index * 2] = fx + offset.x;
                vertices[index * 2 + 1] = fy + offset.y;
                index++;
            }
        }
    }

    public void buildMeshes(float timeIndex) {
        if (imgWidth <= 0 || imgHeight <= 0) {
            throw new IllegalArgumentException(
                    "Bitmap size must be > 0, did you call setBitmapSize(int, int) method?");
        }

        int index = 0;
        float[] pos1 = {0.0f, 0.0f};
        float[] pos2 = {0.0f, 0.0f};

        float firstPointDist = timeIndex * leftStep;
        float secondPointDist = timeIndex * rightStep;
        float height = imgHeight;

        leftPathMeasure.getPosTan(firstPointDist, pos1, null);
        leftPathMeasure.getPosTan(firstPointDist + height, pos2, null);

        float x1 = pos1[0];
        float x2 = pos2[0];
        float y1 = pos1[1];
        float y2 = pos2[1];
        float firstDist = (float) Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
        float firstSplitDist = firstDist / verticalSplit;

        rightPathMeasure.getPosTan(secondPointDist, pos1, null);
        rightPathMeasure.getPosTan(secondPointDist + height, pos2, null);
        x1 = pos1[0];
        x2 = pos2[0];
        y1 = pos1[1];
        y2 = pos2[1];

        float secondDist = (float) Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
        float secondSplitDist = secondDist / verticalSplit;

        for (int y = verticalSplit; y >= 0; y--) {
            leftPathMeasure.getPosTan(y * firstSplitDist + firstPointDist, pos1, null);
            rightPathMeasure.getPosTan(y * secondSplitDist + secondPointDist, pos2, null);

            float fx1 = pos1[0];
            float fx2 = pos2[0];
            float fy1 = pos1[1];
            float fy2 = pos2[1];

            float dy = fy2 - fy1;
            float dx = fx2 - fx1;

            for (int x = 0; x <= horizontalSplit; x++) {
                float fx = dx * x / horizontalSplit;
                float fy = dy * x / horizontalSplit;

                vertices[index * 2] = fx + fx1;
                vertices[index * 2 + 1] = fy + fy1;

                index += 1;
            }
        }
    }

    public Path[] getPaths() {
        return new Path[]{leftPath, rightPath};
    }
}