package com.xinguan14.jdyp.MyVeiw;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.CycleInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;

import com.xinguan14.jdyp.R;


/**
 * Created by jsion on 16/7/14.
 */
public class ATLoginButton extends View {
    private static final float DE_W = 280.F;
    private static final float DE_H = 65.F;
    private static final long ANIMATION_TIME = 800;
    private int buttonColor;
    private int textColor;
    private int textSize;
    private int circlerLoadingColor;
    private int failedButtonColor;
    private int circleLoadingLineWidth;
    private String loginDesc;
    private String failDesc;
    private String mText;
    private Paint buttonPaint;
    private Paint textPaint;
    private Paint circleLoadingPaint;

    private int mHeight;
    private int mWidth;
    private int circleAndRoundSize;

    private RectF buttonRectF;
    private Point textPoint;
    private boolean isLoading;
    private RotateAnimation rotateAnimation;

    private int viewState;

    public ATLoginButton(Context context) {
        this(context, null);
    }

    public ATLoginButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ATLoginButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.ATLoginButton, defStyleAttr, R.style.def_button_style);
        int indexCount = typedArray.getIndexCount();
        for (int i = 0; i < indexCount; i++) {
            int attr = typedArray.getIndex(i);
            switch (attr) {
                case R.styleable.ATLoginButton_button_color:
                    buttonColor = typedArray.getColor(attr, getResources().getColor(R.color.colorAccent));
                    break;
                case R.styleable.ATLoginButton_text_color:
                    textColor = typedArray.getColor(attr, getResources().getColor(R.color.colorW));
                    break;
                case R.styleable.ATLoginButton_text_size:
                    textSize = typedArray.getDimensionPixelSize(attr, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
                    break;
                case R.styleable.ATLoginButton_login_text:
                    loginDesc = typedArray.getString(attr);
                    break;
                case R.styleable.ATLoginButton_failed_text:
                    failDesc = typedArray.getString(attr);
                    break;
                case R.styleable.ATLoginButton_circle_loading_color:
                    circlerLoadingColor = typedArray.getColor(attr, Color.GRAY);
                    break;
                case R.styleable.ATLoginButton_circle_loading_width:
                    circleLoadingLineWidth = typedArray.getDimensionPixelOffset(attr, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, getResources().getDisplayMetrics()));
                    break;
                case R.styleable.ATLoginButton_failed_button_color:
                    failedButtonColor = typedArray.getColor(attr, Color.GRAY);
                    break;
            }
        }
        typedArray.recycle();
        init();
    }

    private void init() {
        buttonPaint = creatPaint(buttonColor, 0, Paint.Style.FILL, circleLoadingLineWidth);
        circleLoadingPaint = creatPaint(circlerLoadingColor, 0, Paint.Style.STROKE, circleLoadingLineWidth);
        textPaint = creatPaint(textColor, textSize, Paint.Style.FILL, circleLoadingLineWidth);
    }

    private Paint creatPaint(int paintColor, int textSize, Paint.Style style, int lineWidth) {
        Paint paint = new Paint();
        paint.setColor(paintColor);
        paint.setAntiAlias(true);
        paint.setStrokeWidth(lineWidth);
        paint.setDither(true);
        paint.setTextSize(textSize);
        paint.setStyle(style);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeJoin(Paint.Join.ROUND);
        return paint;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        int widthSize;
        int heightSize;

        if (widthMode == MeasureSpec.AT_MOST || widthMode == MeasureSpec.UNSPECIFIED) {
            widthSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DE_W, getResources().getDisplayMetrics());
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(widthSize, MeasureSpec.EXACTLY);
        }

        if (heightMode == MeasureSpec.AT_MOST || heightMode == MeasureSpec.UNSPECIFIED) {
            heightSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DE_H, getResources().getDisplayMetrics());
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(heightSize, MeasureSpec.EXACTLY);
        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
        circleAndRoundSize = h / 2;
        textPoint = getTextPointInView(loginDesc);
        buttonRectF = new RectF(0, 0, mWidth, mHeight);
        mText = loginDesc;
        viewState = LoginViewState.NORMAL_STATE;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawButton(canvas);
        drawTextDesc(canvas, mText);
        if (isLoading) {
            drawCircleLoading(canvas);
        }
    }

    private void drawCircleLoading(Canvas canvas) {
        float circleSpacing = circleAndRoundSize / 4;
        float x = mHeight / 2;
        float y = mHeight / 2;
        canvas.translate(mWidth / 2, y);
        canvas.scale(1F, 1F);
        canvas.rotate(0);
        RectF rectF = new RectF(-x + circleSpacing, -y + circleSpacing, x - circleSpacing, y - circleSpacing);
        canvas.drawArc(rectF, -45, 270, false, circleLoadingPaint);
    }

    private void drawTextDesc(Canvas canvas, String textDesc) {
        canvas.drawText(textDesc, textPoint.x, textPoint.y, textPaint);
    }

    private void drawButton(Canvas canvas) {
        canvas.drawRoundRect(buttonRectF, circleAndRoundSize, circleAndRoundSize, buttonPaint);
    }

    private Point getTextPointInView(String textDesc) {
        Point point = new Point();
        int textW = (mWidth - (int) textPaint.measureText(textDesc)) / 2;
        Paint.FontMetrics fm = textPaint.getFontMetrics();
        int textH = (int) Math.ceil(fm.descent - fm.top);
        point.set(textW, (mHeight + textH) / 2);
        return point;
    }

    public void buttonLoginAction() {
        setClickable(false);
        buttonPaint.setColor(buttonColor);
        if (viewState != LoginViewState.NORMAL_STATE) {
            circleLoadingPaint.setColor(circlerLoadingColor);
        }
        ValueAnimator valueAnimator = getValA(0F, 1F);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                Float aFloat = Float.valueOf(valueAnimator.getAnimatedValue().toString());
                int left = (int) ((aFloat) * mWidth);
                int jL = mWidth / 2 - circleAndRoundSize;
                if (left >= jL) {
                    buttonRectF = new RectF(jL, 0, jL + mHeight, mHeight);
                    textPaint.setColor(Color.TRANSPARENT);
                    isLoading = true;
                    invalidate();
                    valueAnimator.cancel();
                    startLoading();
                    viewState = LoginViewState.LOADING_STATE;
                    return;
                }
                float right = (1 - aFloat) * mWidth;
                buttonRectF = new RectF(left, 0, right, mHeight);
                invalidate();
            }
        });
        valueAnimator.start();
    }

    public void buttonLoaginResultAciton(final boolean isSuccess) {
        viewState = isSuccess ? LoginViewState.SUCCESS_STATE : LoginViewState.FAILED_STATE;
        stopLoading();
        ValueAnimator valueAnimator = getValA(0F, 1F);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int jL = mWidth / 2 - circleAndRoundSize;
                Float aFloat = Float.valueOf(valueAnimator.getAnimatedValue().toString());
                int left = (int) ((1 - aFloat) * jL);
                float right = jL + mHeight + jL * aFloat;
                buttonRectF = new RectF(left, 0, right, mHeight);
                textPaint.setColor(textColor);
                if (isSuccess){
                    mText = loginDesc;
                    textPoint = getTextPointInView(mText);
                    buttonPaint.setColor(buttonColor);
                    invalidate();
                    if (aFloat.intValue() == 1) {
                        setClickable(true);
                        buttonRectF = new RectF(0, 0, mWidth, mHeight);
                        invalidate();
                        valueAnimator.cancel();
                    }
                }else {
                    mText = failDesc;
                    textPoint = getTextPointInView(mText);
                    buttonPaint.setColor(failedButtonColor);
                    invalidate();
                    if (aFloat.intValue() == 1) {
                        setClickable(true);
                        buttonRectF = new RectF(0, 0, mWidth, mHeight);
                        invalidate();
                        shakeFailed();
                        valueAnimator.cancel();
                    }
                }
            }
        });
        valueAnimator.start();
    }

    public void stopLoading() {
        isLoading = false;
        circleLoadingPaint.setColor(Color.TRANSPARENT);
        if (null != rotateAnimation) {
            rotateAnimation.cancel();
        }
    }
    private void shakeFailed() {
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(this, "translationX", 0, 10);
        objectAnimator.setDuration(500);
        objectAnimator.setInterpolator(new CycleInterpolator(10));
        objectAnimator.start();
    }

    private void startLoading() {
        rotateAnimation = new RotateAnimation(0F, 360F, mWidth / 2, mHeight / 2);
        rotateAnimation.setDuration(500);
        rotateAnimation.setRepeatCount(Animation.INFINITE);
        rotateAnimation.setInterpolator(new LinearInterpolator());
        startAnimation(rotateAnimation);
    }

    private static class LoginViewState {
        static final int NORMAL_STATE = 90;
        static final int LOADING_STATE = 91;
        static final int FAILED_STATE = 92;
        static final int SUCCESS_STATE = 93;
    }
    private ValueAnimator getValA(float start, float end) {
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(start, end);
        valueAnimator.setDuration(ANIMATION_TIME);
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.setRepeatCount(0);
        return valueAnimator;
    }
}
