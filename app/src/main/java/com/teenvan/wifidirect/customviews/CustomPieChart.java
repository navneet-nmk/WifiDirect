package com.teenvan.wifidirect.customviews;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.teenvan.wifidirect.R;

/**
 * Created by navneet on 04/02/16.
 */
public class CustomPieChart extends View {

    // Declaration of member variables
    private boolean mShowText;
    private int mTextPos;
    private Paint mTextPaint;
    private Paint mShadowPaint;
    private Paint mPiePaint;
    private int one = Color.BLACK, two= Color.GREEN, three = Color.MAGENTA;

    public CustomPieChart(Context context, AttributeSet attrs, int one, int two, int three) {
        super(context, attrs);

        this.one = one;
        this.two = two;
        this.three = three;

        init();
        // Get the attributes
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.PieChart,0,0);

        try {
            mShowText = a.getBoolean(R.styleable.PieChart_showTextValue, false);
            mTextPos = a.getInteger(R.styleable.PieChart_labelPosition, 0);
        } finally {
            // Typed array objects are a shared resource and must be recycled after use
            a.recycle();
        }
    }

    public boolean isShowText() {
        return mShowText;
    }

    public void setShowText(boolean showText) {
        mShowText = showText;
        invalidate();
        requestLayout();
    }

    public int getTextPos() {
        return mTextPos;
    }

    public void setTextPos(int textPos) {
        mTextPos = textPos;
        invalidate();
        requestLayout();
    }

    // Initialize the paint object
    private void init(){
        // Create the paint objects and assign values

        // Creating objects ahead of time is an important optimization

        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setColor(one);
        mTextPaint.setAntiAlias(true);
        mTextPaint.setStrokeWidth(5);
        mTextPaint.setStyle(Paint.Style.STROKE);
        mTextPaint.setStrokeJoin(Paint.Join.ROUND);
        mTextPaint.setStrokeCap(Paint.Cap.ROUND);

        mShadowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mShadowPaint.setColor(two);
        mShadowPaint.setAntiAlias(true);
        mShadowPaint.setStrokeWidth(5);
        mShadowPaint.setStrokeJoin(Paint.Join.ROUND);
        mShadowPaint.setStrokeCap(Paint.Cap.ROUND);

        mPiePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPiePaint.setColor(three);
        mPiePaint.setAntiAlias(true);
        mPiePaint.setStrokeWidth(5);
        mPiePaint.setStrokeJoin(Paint.Join.ROUND);
        mPiePaint.setStrokeCap(Paint.Cap.ROUND);

    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);


        canvas.drawRect(10,10,20,30,mTextPaint);
    }
}
