package com.example.edz.mydemo;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.ProgressBar;

public class Progressbar extends ProgressBar {

    private static final int DEFAULT_TEXT_COLOR=Color.BLUE;
    private static final int DEFAULT_TEXT_SIZE=10;//SP
    private static final int DEFAULT_COLOR_UNREACH=Color.RED;
    private static final int DEFAULT_HEIGHT_UNREACH=2;//DP
    private static final int DEFAULT_COLOR_REACH=Color.BLUE;
    private static final int DEFAULT_HEIGHT_REACH=2;//DP
    private static final int DEFAULT_TEXT_OFFSET=10;//DP
    private static final boolean DEFAULT_ISRADIUS=false;//是否绘制圆形进度条
    private static final int DEFAULT_RADIUS=50;//DP

    private int mTextColor=DEFAULT_TEXT_COLOR;
    private int mTextSize=sp2px(DEFAULT_TEXT_SIZE);
    private int mUnReachColor=DEFAULT_COLOR_UNREACH;
    private int mUnReachHeight=dp2px(DEFAULT_HEIGHT_UNREACH);
    private int mReachColor=DEFAULT_COLOR_REACH;
    private int mReachHeight=dp2px(DEFAULT_HEIGHT_REACH);
    private int mTextOffset=dp2px(DEFAULT_TEXT_OFFSET);
    private boolean isRadius=DEFAULT_ISRADIUS;
    private int mRadius=dp2px(DEFAULT_RADIUS);

    private Paint mPaint=new Paint();
    private int mRealWidth;
    private int mMaxPaintWidth;

    public Progressbar(Context context) {
        this(context,null);
    }

    public Progressbar(Context context, AttributeSet attrs) {
        this(context, attrs,0);

    }

    public Progressbar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        obtainStyledAttrs(attrs);
    }

    private void obtainStyledAttrs(AttributeSet attrs) {
       TypedArray ta=getContext().obtainStyledAttributes(attrs,R.styleable.Progressbar);

        mTextColor=ta.getColor(R.styleable.Progressbar_progress_text_color,mTextColor);
        mTextSize= (int) ta.getDimension(R.styleable.Progressbar_progress_text_size,mTextSize);
        mUnReachColor=ta.getColor(R.styleable.Progressbar_progress_unreach_color,mUnReachColor);
        mUnReachHeight= (int) ta.getDimension(R.styleable.Progressbar_progress_unreach_height,mUnReachHeight);
        mReachColor=ta.getColor(R.styleable.Progressbar_progress_reach_color,mReachColor);
        mReachHeight= (int) ta.getDimension(R.styleable.Progressbar_progress_reach_height,mReachHeight);
        mTextOffset= (int) ta.getDimension(R.styleable.Progressbar_progress_text_offset,mTextOffset);
        isRadius=ta.getBoolean(R.styleable.Progressbar_isradius,false);
        mRadius= (int) ta.getDimension(R.styleable.Progressbar_radius,mRadius);

        if (isRadius) {
            mReachHeight = (int) (mUnReachHeight * 2.5f);
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setAntiAlias(true);
            mPaint.setDither(true);
            mPaint.setStrokeCap(Paint.Cap.ROUND);
        }

        mPaint.setTextSize(mTextSize);
        ta.recycle();
    }

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (isRadius){
            mMaxPaintWidth=Math.max(mReachHeight,mUnReachHeight);
            int expact=mRadius*2+mMaxPaintWidth+getPaddingLeft()+getPaddingRight();
            int width=resolveSize(expact,widthMeasureSpec);
            int height=resolveSize(expact,heightMeasureSpec);
            int readWidth=Math.min(width,height);
            mRadius=(readWidth-getPaddingLeft()-getPaddingRight()-mMaxPaintWidth)/2;
            setMeasuredDimension(readWidth,readWidth);
        }else{
//        int widthMode=MeasureSpec.getMode(widthMeasureSpec);
        int widthVal=MeasureSpec.getSize(widthMeasureSpec);
        int height=measuerHeight(heightMeasureSpec);
        setMeasuredDimension(widthVal,height);
        mRealWidth=widthVal-getPaddingLeft()-getPaddingRight();
        }
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        if (isRadius){
           drawRadius(canvas);
       }
       else {
           drawLine(canvas);
       }
    }

    //绘制圆形进度条
    private void drawRadius(Canvas canvas){
        boolean noNeedUnReach=false;
        //ReachBar
        String text=getProgress()+"%";
        float textWidth=  mPaint.measureText(text);
        float textHeight= (mPaint.descent()+mPaint.ascent())/2;
        canvas.save();
        canvas.translate(getPaddingLeft()+mMaxPaintWidth/2,getPaddingTop());
        //unReachBar
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(mUnReachColor);
        mPaint.setStrokeWidth(mUnReachHeight);
        canvas.drawCircle(mRadius,mRadius,mRadius,mPaint);
        //ReachBar
        mPaint.setColor(mReachColor);
        mPaint.setStrokeWidth(mReachHeight);
        float sweepAngle=getProgress()*1.0f/getMax()*360;
        canvas.drawArc(new RectF(0,0,mRadius*2,mRadius*2),
                0,sweepAngle,false,mPaint);

        //Text
        mPaint.setColor(mTextColor);
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawText(text,mRadius-textWidth/2,mRadius-textHeight,mPaint);

        canvas.restore();
    }
    //绘制线性进度条
    private void drawLine(Canvas canvas){

        boolean noNeedUnReach=false;
        //ReachBar
        float radio=getProgress()*1.0f/getMax();
        String text=getProgress()+"%";
        float textWidth=  mPaint.measureText(text);
        float progressX=mRealWidth*radio;
        canvas.save();
        canvas.translate(getPaddingRight(),getHeight()/2);

        if (progressX+textWidth>mRealWidth)
        {
            progressX=mRealWidth-textWidth;
            noNeedUnReach=true;
        }

        float endX=progressX-mTextOffset/2;

        if (endX>0)
        {
            mPaint.setColor(mReachColor);
            mPaint.setStrokeWidth(mReachHeight);
            canvas.drawLine(0,0,endX,0,mPaint);
        }
        //Text
        mPaint.setColor(mTextColor);
        int y= (int) (-(mPaint.descent()+mPaint.ascent())/2);
        canvas.drawText(text,progressX,y,mPaint);

        //unReachBar
        if (!noNeedUnReach)
        {
            float start=progressX+mTextOffset/2+textWidth;
            mPaint.setColor(mUnReachColor);
            mPaint.setStrokeWidth(mUnReachHeight);
            canvas.drawLine(start,0,mRealWidth,0,mPaint);
        }
        canvas.restore();
    }

    private int measuerHeight(int heightMeasureSpec) {
        int result=0;
        int heightModel=MeasureSpec.getMode(heightMeasureSpec);
        int heightVal=MeasureSpec.getSize(heightMeasureSpec);

        if (heightModel==MeasureSpec.EXACTLY)
        {
            result=heightVal;
        }
        else
        {
            int textHeight= (int) (mPaint.descent()-mPaint.ascent());
            result=getPaddingTop()+
                    getPaddingBottom()+
                    Math.max(Math.max(mReachHeight,mUnReachHeight),Math.abs(textHeight));

            if (heightMeasureSpec==MeasureSpec.AT_MOST)
            {
                result=Math.min(result,heightVal);
            }
        }

        return result;
    }

    private int dp2px(int dpVal){
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dpVal,
                getResources().getDisplayMetrics());
    }
    private int sp2px(int spVal){
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP,
                spVal,
                getResources().getDisplayMetrics());
    }
}
