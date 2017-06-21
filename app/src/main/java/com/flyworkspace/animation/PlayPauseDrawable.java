package com.flyworkspace.animation;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;

/**
 * Created by jinpengfei on 17-4-10.
 */

public class PlayPauseDrawable extends Drawable {
    public static final int STATE_PLAYING = 0;
    public static final int STATE_PAUSE = 1;

    private final Paint mPaint = new Paint();
    private final Path mPath = new Path();

    private static final int COUNT = 16;

    private int current = COUNT;

    private int mCurrentStatus;

    private float[][] TOP;
    private float[][] BOTTOM;
    private float[][] LEFT;
    private float[][] RIGHT;

    private float[][] origin1;
    private float[][] origin2;
    private float[][] target1;
    private float[][] target2;

    private float[] circle;

    /**
     *
     * @param currentStatus : STATE_PLAYING, STATE_PAUSE
     */
    public PlayPauseDrawable(int currentStatus) {
        this.mCurrentStatus = currentStatus;
        mPaint.setStrokeJoin(Paint.Join.MITER);
        mPaint.setStrokeCap(Paint.Cap.BUTT);
        mPaint.setColor(Color.RED);
        mPaint.setAntiAlias(true);
        init();
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        int width = bounds.right - bounds.left;
        int height = bounds.bottom - bounds.top;
        float lineLength = Math.min(width, height);
        circle = new float[]{width / 2 , height / 2, lineLength / 2 - 1};
        lineLength = lineLength - 60;
        float lineXMax = 0.866f * lineLength;
        float midX = width/2f;
        float midY = height/2f;
        float startX = midX - lineXMax/2f;
        float endX = midX + lineXMax / 2f;
        float startY = midY - lineLength / 2;
        float endY = midY + lineLength / 2;
        float pauseLineWidth = lineXMax / 3f;
        TOP = new float[][]{
                {startX, startY}, {startX, midY}, {endX, midY}, {endX, midY}
        };
        BOTTOM = new float[][]{
                {startX, midY}, {startX, endY}, {endX, midY}, {endX, midY}
        };
        LEFT = new float[][]{
                {startX, startY}, {startX, endY}, {startX +pauseLineWidth, endY}, {startX + pauseLineWidth, startY}
        };
        RIGHT = new float[][]{
                {endX -pauseLineWidth, startY}, {endX -pauseLineWidth, endY}, {endX, endY}, {endX, startY}
        };

        this.setPath();
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        mPaint.setStyle(Paint.Style.FILL);
        mPath.rewind();
        getPath(mPath, origin1, target1);
        mPath.close();
        getPath(mPath, origin2, target2);
        mPath.close();
        canvas.save();
        canvas.drawPath(mPath, mPaint);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(10);
        canvas.drawCircle(circle[0], circle[1], circle[2], mPaint);
        canvas.restore();
    }

    private ObjectAnimator mArcArrowIn;

    private void init() {
        PropertyValuesHolder arcRotate = PropertyValuesHolder.ofInt("arcRotate", 0, COUNT);
        mArcArrowIn = ObjectAnimator.ofPropertyValuesHolder(this, arcRotate);
        mArcArrowIn.setDuration(200);
    }

    private void setArcRotate(int arrowRadius) {
        current = arrowRadius;//++;
        invalidateSelf();
    }

    private void getPath(Path path, float[][] origin, float[][] target) {
        float[][] cur = new float[4][2];
        for (int i = 0; i < target.length; i++) {
            for (int j = 0; j < target[i].length; j++) {
                cur[i][j] = origin[i][j] + (target[(i + 3) % 4][j] - origin[i][j]) * current / COUNT;
            }
        }
        for (int i = 0; i < cur.length; i++) {
            if (i == 0) {
                path.moveTo(cur[i][0], cur[i][1]);
            } else
                path.lineTo(cur[i][0], cur[i][1]);
        }
        path.close();
    }

    @Override
    public void setAlpha(int alpha) {
        if (alpha != mPaint.getAlpha()) {
            mPaint.setAlpha(alpha);
            invalidateSelf();
        }
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {
        mPaint.setColorFilter(colorFilter);
        invalidateSelf();
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    public void setPlay(boolean animation) {
        if (mCurrentStatus == STATE_PAUSE) {
            mCurrentStatus = STATE_PLAYING;
            current = animation ? 0 : COUNT;
            setPath();
            mArcArrowIn.start();
        }
    }

    public void setPause(boolean animation) {
        if (mCurrentStatus == STATE_PLAYING) {
            mCurrentStatus = STATE_PAUSE;
            current = animation ? 0 : COUNT;
            setPath();
            mArcArrowIn.start();
        }
    }

    /**
     * 0：playing ;   1: pause
     *
     * @return
     */
    public int getCurrentStatus() {
        return mCurrentStatus;
    }

    private void setPath() {
        if (mCurrentStatus == STATE_PLAYING) {
            origin1 = LEFT;
            target1 = TOP;
            origin2 = RIGHT;
            target2 = BOTTOM;
        } else {
            origin1 = TOP;
            target1 = RIGHT;
            origin2 = BOTTOM;
            target2 = LEFT;
        }
    }
}
