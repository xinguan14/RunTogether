package com.xinguan14.jdyp.guideView;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * Created by binIoter
 */
class MaskView extends ViewGroup {

  private static final String TAG = "MaskView";

  private final RectF mTargetRect = new RectF();
  private final RectF mFullingRect = new RectF();
  private final RectF mChildTmpRect = new RectF();
  private final Paint mFullingPaint = new Paint();
  private int mPadding = 0;
  private int mPaddingLeft = 0;
  private int mPaddingTop = 0;
  private int mPaddingRight = 0;
  private int mPaddingBottom = 0;

  private final Path mOutPath = new Path();

  private final Paint mTargetPaint = new Paint();
  private final Paint mPaint = new Paint();

  private boolean mCustomFullingRect;
  private boolean mOverlayTarget;
  private int mCorner = 0;
  private int mStyle = Component.ROUNDRECT;

  //构造快优先于构造方法执行
  {
    mPaint.setAntiAlias(true);
    mTargetPaint.setColor(0x00000000);
    mTargetPaint.setStrokeWidth(10);
    mTargetPaint.setAntiAlias(true);
  }

  public MaskView(Context context) {
    this(context, null, 0);
  }

  public MaskView(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public MaskView(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
    setLayoutParams(new ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT));
    setFocusable(true);//键盘是否能获得焦点
    setFocusableInTouchMode(true);//触摸是否能获得焦点
    requestFocus();
    mOutPath.setFillType(Path.FillType.EVEN_ODD);//设置填充模式，取path所在并不相交区域
    resetOutPath();
  }

  @Override protected void onDetachedFromWindow() {
    super.onDetachedFromWindow();
    try {
      clearFocus();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Override protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    final int w = MeasureSpec.getSize(widthMeasureSpec);
    final int h = MeasureSpec.getSize(heightMeasureSpec);
    setMeasuredDimension(w, h);
    if (!mCustomFullingRect) {
      mFullingRect.set(0, 0, w, h);
      resetOutPath();
    }

    final int count = getChildCount();
    View child;
    for (int i = 0; i < count; i++) {
      child = getChildAt(i);
      if (child != null) {
        LayoutParams lp = (LayoutParams) child.getLayoutParams();
        if (lp == null) {
          child.setLayoutParams(lp);
        }
        measureChild(child, w + MeasureSpec.AT_MOST, h + MeasureSpec.AT_MOST);
      }
    }
  }

  @Override protected void onLayout(boolean changed, int l, int t, int r, int b) {
    final int count = getChildCount();
    final float density = getResources().getDisplayMetrics().density;
    View child;
    for (int i = 0; i < count; i++) {
      child = getChildAt(i);
      if (child == null) {
        continue;
      }
      LayoutParams lp = (LayoutParams) child.getLayoutParams();
      if (lp == null) {
        continue;
      }
      switch (lp.targetAnchor) {
        case LayoutParams.ANCHOR_LEFT://左
          mChildTmpRect.right = mTargetRect.left;
          mChildTmpRect.left = mChildTmpRect.right - child.getMeasuredWidth();
          verticalChildPositionLayout(child, mChildTmpRect, lp.targetParentPosition);
          break;
        case LayoutParams.ANCHOR_TOP://上
          mChildTmpRect.bottom = mTargetRect.top;
          mChildTmpRect.top = mChildTmpRect.bottom - child.getMeasuredHeight();
          horizontalChildPositionLayout(child, mChildTmpRect, lp.targetParentPosition);
          break;
        case LayoutParams.ANCHOR_RIGHT://右
          mChildTmpRect.left = mTargetRect.right;
          mChildTmpRect.right = mChildTmpRect.left + child.getMeasuredWidth();
          verticalChildPositionLayout(child, mChildTmpRect, lp.targetParentPosition);
          break;
        case LayoutParams.ANCHOR_BOTTOM://下
          mChildTmpRect.top = mTargetRect.bottom;
          mChildTmpRect.bottom = mChildTmpRect.top + child.getMeasuredHeight();
          horizontalChildPositionLayout(child, mChildTmpRect, lp.targetParentPosition);
          break;
        case LayoutParams.ANCHOR_OVER://中心
          mChildTmpRect.left = ((int) mTargetRect.width() - child.getMeasuredWidth()) >> 1;
          mChildTmpRect.top = ((int) mTargetRect.height() - child.getMeasuredHeight()) >> 1;
          mChildTmpRect.right = ((int) mTargetRect.width() + child.getMeasuredWidth()) >> 1;
          mChildTmpRect.bottom = ((int) mTargetRect.height() + child.getMeasuredHeight()) >> 1;
          mChildTmpRect.offset(mTargetRect.left, mTargetRect.top);
          break;
      }
      //额外的xy偏移
      mChildTmpRect.offset((int) (density * lp.offsetX + 0.5f),
          (int) (density * lp.offsetY + 0.5f));
      child.layout((int) mChildTmpRect.left, (int) mChildTmpRect.top, (int) mChildTmpRect.right,
          (int) mChildTmpRect.bottom);
    }
  }

  private void horizontalChildPositionLayout(View child, RectF rect, int targetParentPosition) {
    switch (targetParentPosition) {
      case LayoutParams.PARENT_START:
        rect.left = mTargetRect.left;
        rect.right = rect.left + child.getMeasuredWidth();
        break;
      case LayoutParams.PARENT_CENTER:
        rect.left = (mTargetRect.width() - child.getMeasuredWidth()) / 2;
        rect.right = (mTargetRect.width() + child.getMeasuredWidth()) / 2;
        rect.offset(mTargetRect.left, 0);
        break;
      case LayoutParams.PARENT_END:
        rect.right = mTargetRect.right;
        rect.left = rect.right - child.getMeasuredWidth();
        break;
    }
  }

  private void verticalChildPositionLayout(View child, RectF rect, int targetParentPosition) {
    switch (targetParentPosition) {
      case LayoutParams.PARENT_START:
        rect.top = mTargetRect.top;
        rect.bottom = rect.top + child.getMeasuredHeight();
        break;
      case LayoutParams.PARENT_CENTER:
        rect.top = (mTargetRect.width() - child.getMeasuredHeight()) / 2;
        rect.bottom = (mTargetRect.width() + child.getMeasuredHeight()) / 2;
        rect.offset(0, mTargetRect.top);
        break;
      case LayoutParams.PARENT_END:
        rect.bottom = mTargetRect.bottom;
        rect.top = mTargetRect.bottom - child.getMeasuredHeight();
        break;
    }
  }

  private void resetOutPath() {
    mOutPath.reset();
    resetPadding();
    mOutPath.addRect(mTargetRect, Path.Direction.CW);
    mOutPath.addRect(mFullingRect, Path.Direction.CW);
  }

  /**
   * 设置padding
   */
  private void resetPadding() {
    if (mPadding != 0 && mPaddingLeft == 0) {
      mTargetRect.left -= mPadding;
    }
    if (mPadding != 0 && mPaddingTop == 0) {
      mTargetRect.top -= mPadding;
    }
    if (mPadding != 0 && mPaddingRight == 0) {
      mTargetRect.right += mPadding;
    }
    if (mPadding != 0 && mPaddingBottom == 0) {
      mTargetRect.bottom += mPadding;
    }
    if (mPaddingLeft != 0) {
      mTargetRect.left -= mPaddingLeft;
    }
    if (mPaddingTop != 0) {
      mTargetRect.top -= mPaddingTop;
    }
    if (mPaddingRight != 0) {
      mTargetRect.right += mPaddingRight;
    }
    if (mPaddingBottom != 0) {
      mTargetRect.bottom += mPaddingBottom;
    }
  }

  @Override protected LayoutParams generateDefaultLayoutParams() {
    return new LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
  }

  @Override protected void dispatchDraw(Canvas canvas) {
    final long drawingTime = getDrawingTime();
    canvas.save();

    if (!mOverlayTarget) {
      Path mPath = new Path();
      switch (mStyle) {
        case Component.ROUNDRECT:
          mPath.addRoundRect(mTargetRect, mCorner, mCorner, Path.Direction.CCW);
          break;
        case Component.CIRCLE:
          mPath.addCircle(mTargetRect.centerX(), mTargetRect.centerY(), mTargetRect.width() / 2,
              Path.Direction.CCW);
          break;
        default:
          mPath.addRoundRect(mTargetRect, mCorner, mCorner, Path.Direction.CCW);
          break;
      }
      canvas.clipPath(mPath, Region.Op.DIFFERENCE);
    }
    //画遮罩
    canvas.drawRect(mFullingRect, mFullingPaint);
    //抗锯齿
    canvas.setDrawFilter(
        new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
    canvas.restore();

    try {
      View child;
      for (int i = 0; i < getChildCount(); i++) {
        child = getChildAt(i);
        drawChild(canvas, child, drawingTime);
      }
    } catch (NullPointerException e) {

    }
  }

  public void setTargetRect(Rect rect) {
    mTargetRect.set(rect);
    resetOutPath();
    invalidate();
  }

  public void setFullingRect(Rect rect) {
    mFullingRect.set(rect);
    resetOutPath();
    mCustomFullingRect = true;
    invalidate();
  }

  public void setFullingAlpha(int alpha) {
    mFullingPaint.setAlpha(alpha);
    invalidate();
  }

  public void setFullingColor(int color) {
    mFullingPaint.setColor(color);
    invalidate();
  }

  public void setHighTargetCorner(int corner) {
    this.mCorner = corner;
  }

  public void setHighTargetGraphStyle(int style) {
    this.mStyle = style;
  }

  public void setOverlayTarget(boolean b) {
    mOverlayTarget = b;
  }

  public void setPadding(int padding) {
    this.mPadding = padding;
  }

  public void setPaddingLeft(int paddingLeft) {
    this.mPaddingLeft = paddingLeft;
  }

  public void setPaddingTop(int paddingTop) {
    this.mPaddingTop = paddingTop;
  }

  public void setPaddingRight(int paddingRight) {
    this.mPaddingRight = paddingRight;
  }

  public void setPaddingBottom(int paddingBottom) {
    this.mPaddingBottom = paddingBottom;
  }

  static class LayoutParams extends ViewGroup.LayoutParams {

    public static final int ANCHOR_LEFT = 0x01;
    public static final int ANCHOR_TOP = 0x02;
    public static final int ANCHOR_RIGHT = 0x03;
    public static final int ANCHOR_BOTTOM = 0x04;
    public static final int ANCHOR_OVER = 0x05;

    public static final int PARENT_START = 0x10;
    public static final int PARENT_CENTER = 0x20;
    public static final int PARENT_END = 0x30;

    public int targetAnchor = ANCHOR_BOTTOM;
    public int targetParentPosition = PARENT_CENTER;
    public int offsetX = 0;
    public int offsetY = 0;

    public LayoutParams(Context c, AttributeSet attrs) {
      super(c, attrs);
    }

    public LayoutParams(int width, int height) {
      super(width, height);
    }

    public LayoutParams(ViewGroup.LayoutParams source) {
      super(source);
    }
  }
}
