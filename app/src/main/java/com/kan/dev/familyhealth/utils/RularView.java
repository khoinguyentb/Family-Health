package com.kan.dev.familyhealth.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Path;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.Scroller;

import com.chingtech.rulerview.library.R.styleable;

public class RularView extends View {
    private int mMinVelocity;
    private Scroller mScroller;
    private VelocityTracker mVelocityTracker;
    private int mWidth;
    private int mHeight;
    private boolean showResult;
    private boolean showUnit;
    private boolean mAlphaEnable;
    private int mStrokeCap;
    private static final int ROUND = 1;
    private static final int BUTT = 0;
    private static final int SQUARE = 2;
    private static final float DEFAULT_VALUE = 50.0F;
    private static final float MAX_VALUE = 100.0F;
    private static final float MIN_VALUE = 0.0F;
    private static final float SPAN_VALUE = 0.1F;
    private static final int ITEM_MAX_HEIGHT = 36;
    private static final int ITEM_MAX_WIDTH = 3;
    private static final int ITEM_MIN_HEIGHT = 20;
    private static final int ITEM_MIN_WIDTH = 2;
    private static final int ITEM_MIDDLE_HEIGHT = 28;
    private static final int ITEM_MIDDLE_WIDTH = 2;
    private static final int INDICATOR_WIDTH = 3;
    private static final int INDICATOR_HEIGHT = 38;
    private static final int LINE = 1;
    private static final int TRIANGLE = 2;
    private static final int defaultItemSpacing = 6;
    private static final int textMarginTop = 8;
    private static final int scaleTextSize = 15;
    private static final int unitTextSize = 15;
    private static final int resultTextSize = 17;
    private int mIndicatorType;
    private int resultHeight;
    private float mValue;
    private float mMaxValue;
    private float mMinValue;
    private int mItemSpacing;
    private float mPerSpanValue;
    private int mMaxLineHeight;
    private int mMiddleLineHeight;
    private int mMinLineHeight;
    private int mMinLineWidth;
    private int mMaxLineWidth;
    private int mMiddleLineWidth;
    private int mTextMarginTop;
    private float mScaleTextHeight;
    private float mResultTextHeight;
    private float mUnitTextHeight;
    private int mIndcatorColor;
    private int mIndcatorWidth;
    private int mIndcatorHeight;
    private int mScaleTextColor;
    private int mScaleTextSize;
    private int mResultTextColor;
    private int mResultTextSize;
    private int mUnitTextColor;
    private int mUnitTextSize;
    private String unit;
    private int mMinLineColor;
    private int mMaxLineColor;
    private int mMiddleLineColor;
    private Paint mScaleTextPaint;
    private Paint mLinePaint;
    private Paint mIndicatorPaint;
    private Paint mResultTextPaint;
    private Paint mUnitTextPaint;
    private int mTotalLine;
    private int mMaxOffset;
    private float mOffset;
    private int mLastX;
    private int mMove;
    private OnChooseResulterListener mListener;

    public RularView(Context context) {
        this(context, (AttributeSet)null);
    }

    public RularView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        this.setAttr(context, attrs, 0);
    }

    public RularView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.resultHeight = 0;
        this.mUnitTextHeight = 0.0F;
        this.mIndcatorColor = -11487866;
        this.mScaleTextColor = -2145246686;
        this.mResultTextColor = -11487866;
        this.mUnitTextColor = -10066330;
        this.mMinLineColor = -2145246686;
        this.mMaxLineColor = -2145246686;
        this.mMiddleLineColor = -2145246686;
        this.setAttr(context, attrs, defStyleAttr);
    }

    private void setAttr(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray a = this.getContext().getTheme().obtainStyledAttributes(attrs, styleable.RulerView, defStyleAttr, 0);
        this.mValue = a.getFloat(styleable.RulerView_rv_defaultValue, 50.0F);
        this.mMaxValue = a.getFloat(styleable.RulerView_rv_maxValue, 100.0F);
        this.mMinValue = a.getFloat(styleable.RulerView_rv_minValue, 0.0F);
        float precision = a.getFloat(styleable.RulerView_rv_spanValue, 0.1F);
        this.mPerSpanValue = precision * 10.0F;
        this.mItemSpacing = a.getDimensionPixelSize(styleable.RulerView_rv_itemSpacing, dp2px(6.0F));
        this.mMaxLineHeight = a.getDimensionPixelSize(styleable.RulerView_rv_maxLineHeight, dp2px(36.0F));
        this.mMinLineHeight = a.getDimensionPixelSize(styleable.RulerView_rv_minLineHeight, dp2px(20.0F));
        this.mMiddleLineHeight = a.getDimensionPixelSize(styleable.RulerView_rv_middleLineHeight, dp2px(28.0F));
        this.mMinLineWidth = a.getDimensionPixelSize(styleable.RulerView_rv_minLineWidth, dp2px(2.0F));
        this.mMiddleLineWidth = a.getDimensionPixelSize(styleable.RulerView_rv_middleLineWidth, dp2px(2.0F));
        this.mMaxLineWidth = a.getDimensionPixelSize(styleable.RulerView_rv_maxLineWidth, dp2px(3.0F));
        this.mMaxLineColor = a.getColor(styleable.RulerView_rv_maxLineColor, this.mMaxLineColor);
        this.mMiddleLineColor = a.getColor(styleable.RulerView_rv_middleLineColor, this.mMiddleLineColor);
        this.mMinLineColor = a.getColor(styleable.RulerView_rv_minLineColor, this.mMinLineColor);
        this.mIndcatorColor = a.getColor(styleable.RulerView_rv_indcatorColor, this.mIndcatorColor);
        this.mIndcatorWidth = a.getDimensionPixelSize(styleable.RulerView_rv_indcatorWidth, dp2px(3.0F));
        this.mIndcatorHeight = a.getDimensionPixelSize(styleable.RulerView_rv_indcatorHeight, dp2px(38.0F));
        this.mIndicatorType = a.getInt(styleable.RulerView_rv_indcatorType, 1);
        this.mScaleTextColor = a.getColor(styleable.RulerView_rv_scaleTextColor, this.mScaleTextColor);
        this.mScaleTextSize = a.getDimensionPixelSize(styleable.RulerView_rv_scaleTextSize, sp2px(15.0F));
        this.mTextMarginTop = a.getDimensionPixelSize(styleable.RulerView_rv_textMarginTop, dp2px(8.0F));
        this.mResultTextColor = a.getColor(styleable.RulerView_rv_resultTextColor, this.mResultTextColor);
        this.mResultTextSize = a.getDimensionPixelSize(styleable.RulerView_rv_resultTextSize, sp2px(17.0F));
        this.mUnitTextColor = a.getColor(styleable.RulerView_rv_unitTextColor, this.mUnitTextColor);
        this.mUnitTextSize = a.getDimensionPixelSize(styleable.RulerView_rv_unitTextSize, sp2px(15.0F));
        this.showUnit = a.getBoolean(styleable.RulerView_rv_showUnit, true);
        this.unit = a.getString(styleable.RulerView_rv_unit);
        if (TextUtils.isEmpty(this.unit)) {
            this.unit = "kg";
        }

        this.showResult = a.getBoolean(styleable.RulerView_rv_showResult, true);
        this.mAlphaEnable = a.getBoolean(styleable.RulerView_rv_alphaEnable, true);
        this.mStrokeCap = a.getInt(styleable.RulerView_rv_strokeCap, 0);
        this.init(context);
        a.recycle();
    }

    protected void init(Context context) {
        this.mScroller = new Scroller(context);
        this.mMinVelocity = ViewConfiguration.get(this.getContext()).getScaledMinimumFlingVelocity();
        this.mScaleTextPaint = new Paint(1);
        this.mScaleTextPaint.setTextSize((float)this.mScaleTextSize);
        this.mScaleTextPaint.setColor(this.mScaleTextColor);
        this.mScaleTextPaint.setAntiAlias(true);
        this.mScaleTextHeight = getFontHeight(this.mScaleTextPaint);
        this.mResultTextPaint = new Paint(1);
        this.mResultTextPaint.setTextSize((float)this.mResultTextSize);
        this.mResultTextPaint.setColor(this.mResultTextColor);
        this.mResultTextPaint.setAntiAlias(true);
        this.mResultTextPaint.setAlpha(255);
        this.mResultTextHeight = getFontHeight(this.mResultTextPaint);
        if (this.showUnit) {
            this.mUnitTextPaint = new Paint(1);
            this.mUnitTextPaint.setTextSize((float)this.mUnitTextSize);
            this.mUnitTextPaint.setColor(this.mUnitTextColor);
            this.mUnitTextPaint.setAntiAlias(true);
            this.mUnitTextPaint.setAlpha(232);
            this.mUnitTextHeight = getFontHeight(this.mUnitTextPaint);
        }

        this.mLinePaint = new Paint(1);
        if (this.mStrokeCap == 1) {
            this.mLinePaint.setStrokeCap(Cap.ROUND);
        } else if (this.mStrokeCap == 0) {
            this.mLinePaint.setStrokeCap(Cap.BUTT);
        } else if (this.mStrokeCap == 2) {
            this.mLinePaint.setStrokeCap(Cap.SQUARE);
        }

        this.mLinePaint.setAntiAlias(true);
        this.mIndicatorPaint = new Paint(1);
        this.mIndicatorPaint.setStrokeWidth((float)this.mIndcatorWidth);
        this.mIndicatorPaint.setAntiAlias(true);
        this.mIndicatorPaint.setColor(this.mIndcatorColor);
        if (this.mStrokeCap == 1) {
            this.mIndicatorPaint.setStrokeCap(Cap.ROUND);
        } else if (this.mStrokeCap == 0) {
            this.mIndicatorPaint.setStrokeCap(Cap.BUTT);
        } else if (this.mStrokeCap == 2) {
            this.mIndicatorPaint.setStrokeCap(Cap.SQUARE);
        }

        this.initViewParam(this.mValue, this.mMinValue, this.mMaxValue, this.mPerSpanValue);
    }

    @SuppressLint("WrongConstant")
    public void initViewParam(float defaultValue, float minValue, float maxValue, float perSpanValue) {
        this.mValue = defaultValue;
        this.mMaxValue = maxValue;
        this.mMinValue = minValue;
        this.mPerSpanValue = (float)((int)(perSpanValue * 10.0F));
        this.mTotalLine = (int)((float)((int)(this.mMaxValue * 10.0F - this.mMinValue * 10.0F)) / this.mPerSpanValue + 1.0F);
        this.mMaxOffset = -(this.mTotalLine - 1) * this.mItemSpacing;
        this.mOffset = (this.mMinValue - this.mValue) / this.mPerSpanValue * (float)this.mItemSpacing * 10.0F;
        this.invalidate();
        this.setVisibility(0);
    }

    public void setValue(float defaultValue) {
        this.mValue = defaultValue;
        this.invalidate();
        computeScroll();
    }

    public void setUnit(String unit) {
        this.unit = unit;
        invalidate();
    }

    public static float getFontHeight(Paint paint) {
        Paint.FontMetrics fm = paint.getFontMetrics();
        return fm.descent - fm.ascent;
    }

    public void setChooseValueChangeListener(OnChooseResulterListener listener) {
        this.mListener = listener;
    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (w > 0 && h > 0) {
            this.mWidth = w;
            this.mHeight = h;
        }

    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        this.resultHeight = this.showResult ? (int)(this.mResultTextHeight > this.mUnitTextHeight ? this.mResultTextHeight : this.mUnitTextHeight) + 20 : this.mIndcatorWidth / 2;
        this.drawScaleAndNum(canvas);
        this.drawIndicator(canvas);
        if (this.showResult) {
            if (this.mPerSpanValue == 1.0F) {
                this.drawResult(canvas, String.format(getPrecisionFormat(1), this.mValue));
            } else if (this.mPerSpanValue == 10.0F) {
                this.drawResult(canvas, String.format(getPrecisionFormat(0), this.mValue));
            }
        }

    }

    private void drawIndicator(Canvas canvas) {
        int srcPointX = this.mWidth / 2;
        if (this.mIndicatorType == 1) {
            canvas.drawLine((float)srcPointX, (float)this.resultHeight, (float)srcPointX, (float)(this.mIndcatorHeight + this.resultHeight), this.mIndicatorPaint);
        } else if (this.mIndicatorType == 2) {
            Path path = new Path();
            path.moveTo((float)(srcPointX - this.mItemSpacing), (float)this.resultHeight);
            path.lineTo((float)(srcPointX + this.mItemSpacing), (float)this.resultHeight);
            path.lineTo((float)srcPointX, (float)(this.mMinLineHeight + this.resultHeight));
            path.close();
            canvas.drawPath(path, this.mIndicatorPaint);
        }

    }

    private void drawScaleAndNum(Canvas canvas) {
        int srcPointX = this.mWidth / 2;

        for(int i = 0; i < this.mTotalLine; ++i) {
            float left = (float)srcPointX + this.mOffset + (float)(i * this.mItemSpacing);
            if (!(left < 0.0F) && !(left > (float)this.mWidth)) {
                float height;
                if (i % 10 == 0) {
                    height = (float)this.mMaxLineHeight;
                    this.mLinePaint.setColor(this.mMaxLineColor);
                    this.mLinePaint.setStrokeWidth((float)this.mMaxLineWidth);
                } else if (i % 5 == 0) {
                    height = (float)this.mMiddleLineHeight;
                    this.mLinePaint.setColor(this.mMiddleLineColor);
                    this.mLinePaint.setStrokeWidth((float)this.mMiddleLineWidth);
                } else {
                    height = (float)this.mMinLineHeight;
                    this.mLinePaint.setColor(this.mMinLineColor);
                    this.mLinePaint.setStrokeWidth((float)this.mMinLineWidth);
                }

                if (this.mAlphaEnable) {
                    float scale = 1.0F - Math.abs(left - (float)srcPointX) / (float)srcPointX;
                    int alpha = (int)(255.0F * scale * scale);
                    this.mLinePaint.setAlpha(alpha);
                    this.mScaleTextPaint.setAlpha(alpha);
                }

                canvas.drawLine(left, (float)this.resultHeight, left, height + (float)this.resultHeight, this.mLinePaint);
                if (i % 10 == 0) {
                    String value = String.valueOf((int)(this.mMinValue + (float)i * this.mPerSpanValue / 10.0F));
                    canvas.drawText(value, left - this.mScaleTextPaint.measureText(value) / 2.0F, (float)this.resultHeight + height + (float)this.mTextMarginTop + this.mScaleTextHeight, this.mScaleTextPaint);
                }
            }
        }

    }

    private void drawResult(Canvas canvas, String resultText) {
        int srcPointX = this.mWidth / 2;
        canvas.drawText(resultText, (float)srcPointX - this.mResultTextPaint.measureText(resultText) / 2.0F, this.mResultTextHeight > this.mUnitTextHeight ? this.mResultTextHeight : this.mUnitTextHeight, this.mResultTextPaint);
        if (this.showUnit) {
            canvas.drawText(this.unit, (float)srcPointX + this.mResultTextPaint.measureText(resultText) / 2.0F + 10.0F, this.mResultTextHeight > this.mUnitTextHeight ? this.mResultTextHeight : this.mUnitTextHeight, this.mUnitTextPaint);
        }

    }

    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        int xPosition = (int)event.getX();
        if (this.mVelocityTracker == null) {
            this.mVelocityTracker = VelocityTracker.obtain();
        }

        this.mVelocityTracker.addMovement(event);
        switch (action) {
            case 0:
                this.mScroller.forceFinished(true);
                this.mLastX = xPosition;
                this.mMove = 0;
                break;
            case 1:
            case 3:
                this.countMoveEnd();
                this.countVelocityTracker();
                return false;
            case 2:
                this.mMove = this.mLastX - xPosition;
                this.changeMoveAndValue();
        }

        this.mLastX = xPosition;
        return true;
    }

    private void countVelocityTracker() {
        this.mVelocityTracker.computeCurrentVelocity(1000);
        float xVelocity = this.mVelocityTracker.getXVelocity();
        if (Math.abs(xVelocity) > (float)this.mMinVelocity) {
            this.mScroller.fling(0, 0, (int)xVelocity, 0, Integer.MIN_VALUE, Integer.MAX_VALUE, 0, 0);
        }

    }

    private void countMoveEnd() {
        this.mOffset -= (float)this.mMove;
        if (this.mOffset <= (float)this.mMaxOffset) {
            this.mOffset = (float)this.mMaxOffset;
        } else if (this.mOffset >= 0.0F) {
            this.mOffset = 0.0F;
        }

        this.mLastX = 0;
        this.mMove = 0;
        this.mValue = this.mMinValue + (float)Math.round(Math.abs(this.mOffset) * 1.0F / (float)this.mItemSpacing) * this.mPerSpanValue / 10.0F;
        this.mOffset = (this.mMinValue - this.mValue) * 10.0F / this.mPerSpanValue * (float)this.mItemSpacing;
        this.notifyValueChange();
        this.postInvalidate();
    }

    private void changeMoveAndValue() {
        this.mOffset -= (float)this.mMove;
        if (this.mOffset <= (float)this.mMaxOffset) {
            this.mOffset = (float)this.mMaxOffset;
            this.mMove = 0;
            this.mScroller.forceFinished(true);
        } else if (this.mOffset >= 0.0F) {
            this.mOffset = 0.0F;
            this.mMove = 0;
            this.mScroller.forceFinished(true);
        }

        this.mValue = this.mMinValue + (float)Math.round(Math.abs(this.mOffset) * 1.0F / (float)this.mItemSpacing) * this.mPerSpanValue / 10.0F;
        this.notifyValueChange();
        this.postInvalidate();
    }

    private void notifyValueChange() {
        if (null != this.mListener) {
            this.mListener.onChooseValueChange(this.mValue);
        }

    }

    public void computeScroll() {
        super.computeScroll();
        if (this.mScroller.computeScrollOffset()) {
            if (this.mScroller.getCurrX() == this.mScroller.getFinalX()) {
                this.countMoveEnd();
            } else {
                int xPosition = this.mScroller.getCurrX();
                this.mMove = this.mLastX - xPosition;
                this.changeMoveAndValue();
                this.mLastX = xPosition;
            }
        }
    }

    public float getValue() {
        return mValue;
    }

    @SuppressLint("WrongConstant")
    public static int dp2px(float dpVal) {
        Resources r = Resources.getSystem();
        return (int)TypedValue.applyDimension(1, dpVal, r.getDisplayMetrics());
    }

    @SuppressLint("WrongConstant")
    public static int sp2px(float spVal) {
        Resources r = Resources.getSystem();
        return (int)TypedValue.applyDimension(2, spVal, r.getDisplayMetrics());
    }

    public static String getPrecisionFormat(int precision) {
        return "%." + precision + "f";
    }

    public interface OnChooseResulterListener {
        void onChooseValueChange(float var1);
    }
}
