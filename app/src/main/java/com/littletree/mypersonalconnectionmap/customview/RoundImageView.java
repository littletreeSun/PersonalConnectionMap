package com.littletree.mypersonalconnectionmap;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * @ProjectName: MyPersonalConnectionMap
 * @Package: com.littletree.mypersonalconnectionmap
 * @ClassName: RoundImageView
 * @Author: littletree
 * @CreateDate: 2022/7/19/019 16:53
 */
public class RoundImageView extends ImageView {

    public enum RoundMode {
        ROUND_VIEW, ROUND_DRAWABLE
    }

    private static final int DEFAULT_BORDER_WIDTH = 0;
    private static final int DEFAULT_BORDER_COLOR = Color.TRANSPARENT;
    private static final int DEFAULT_FILL_COLOR = Color.WHITE;

    private boolean roundDisable;
    private RoundMode roundMode = RoundMode.ROUND_DRAWABLE;
    private int borderColor = DEFAULT_BORDER_COLOR;
    private int borderWidth = DEFAULT_BORDER_WIDTH;
    private int fillColor = DEFAULT_FILL_COLOR;

    private Paint borderPaint;
    private Paint fillPaint;
    private Paint imagePaint;
    private Paint portPaint;

    private Rect bounds = new Rect();
    private float radius = 0;
    private float cx = 0;
    private float cy = 0;

    public RoundImageView(Context context) {
        super(context);
        initView();
    }

    public RoundImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public RoundImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        portPaint = new Paint();
        portPaint.setAntiAlias(true);

        borderPaint = new Paint();
        borderPaint.setAntiAlias(true);
        borderPaint.setColor(DEFAULT_BORDER_COLOR);
        borderPaint.setStrokeWidth(DEFAULT_BORDER_WIDTH);
        borderPaint.setStyle(Paint.Style.STROKE);

        fillPaint = new Paint();
        fillPaint.setAntiAlias(true);
        fillPaint.setColor(DEFAULT_FILL_COLOR);
        fillPaint.setStyle(Paint.Style.FILL);

        imagePaint = new Paint();
        imagePaint.setAntiAlias(true);
        imagePaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
    }

    public void setRoundMode(RoundMode roundMode) {
        if (roundMode == null) {
            throw new IllegalArgumentException("roundMode is null.");
        }

        if (this.roundMode != roundMode) {
            this.roundMode = roundMode;
            invalidate();
        }
    }

    public void setRoundDisable(boolean roundDisable) {
        if (this.roundDisable != roundDisable) {
            this.roundDisable = roundDisable;
            invalidate();
        }
    }

    public boolean isRoundDisable() {
        return roundDisable;
    }

    public void setBorderColor(int borderColor) {
        if (this.borderColor != borderColor) {
            this.borderColor = borderColor;
            borderPaint.setColor(borderColor);
            invalidate();
        }
    }

    public void setBorderWidth(int borderWidth) {
        if (this.borderWidth != borderWidth) {
            this.borderWidth = borderWidth;
            borderPaint.setStrokeWidth(borderWidth);
            invalidate();
        }
    }

    public void setFillColor(int fillColor) {
        if (this.fillColor != fillColor) {
            this.fillColor = fillColor;
            fillPaint.setColor(fillColor);
            invalidate();
        }
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {

        if (roundDisable) {
            super.onDraw(canvas);
            return;
        }

        if (getDrawable() == null && roundMode == RoundMode.ROUND_DRAWABLE) {
            super.onDraw(canvas);
            return;
        }

        computeRoundBounds();
        drawCircle(canvas);
        drawImage(canvas);

    }

    @SuppressLint("WrongCall")
    private void drawImage(Canvas canvas) {
        @SuppressLint("DrawAllocation") Bitmap src = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_4444);
        super.onDraw(new Canvas(src));

        Bitmap port = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_4444);
        Canvas portCanvas = new Canvas(port);

        int saveCount = portCanvas.getSaveCount();
        portCanvas.save();
        adjustCanvas(portCanvas);
        portCanvas.drawCircle(cx, cy, radius, portPaint);
        portCanvas.restoreToCount(saveCount);

        portCanvas.drawBitmap(src, 0, 0, imagePaint);
        src.recycle();

        canvas.drawBitmap(port, 0, 0, null);
        port.recycle();

    }

    private void drawCircle(Canvas canvas) {
        int saveCount = canvas.getSaveCount();
        canvas.save();

        adjustCanvas(canvas);

        canvas.drawCircle(cx, cy, radius, fillPaint);
        if (borderWidth > 0) {
            canvas.drawCircle(cx, cy, radius - borderWidth / 2f, borderPaint);
        }

        canvas.restoreToCount(saveCount);

    }

    private void computeRoundBounds() {
        switch (roundMode) {
            case ROUND_VIEW:
                bounds.left = getPaddingLeft();
                bounds.top = getPaddingTop();
                bounds.right = getWidth() - getPaddingRight();
                bounds.bottom = getHeight() - getPaddingBottom();
                break;
            case ROUND_DRAWABLE:
                getDrawable().copyBounds(bounds);
                break;
            default:
                throw new RuntimeException("unknown round mode:" + roundMode);
        }

        radius = Math.min(bounds.width(), bounds.height()) / 2f;
        cx = bounds.left + bounds.width() / 2f;
        cy = bounds.top + bounds.height() / 2f;
    }

    private void adjustCanvas(Canvas canvas) {
        if (roundMode == RoundMode.ROUND_DRAWABLE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                if (getCropToPadding()) {
                    final int scrollX = getScrollX();
                    final int scrollY = getScrollY();
                    canvas.clipRect(scrollX + getPaddingLeft(), scrollY + getPaddingTop(),
                            scrollX + getRight() - getLeft() - getPaddingRight(),
                            scrollY + getBottom() - getTop() - getPaddingBottom());
                }
            }

            canvas.translate(getPaddingLeft(), getPaddingTop());
            if (getImageMatrix() != null) {
                Matrix m = new Matrix(getImageMatrix());
                canvas.concat(m);
            }
        }
    }

}
