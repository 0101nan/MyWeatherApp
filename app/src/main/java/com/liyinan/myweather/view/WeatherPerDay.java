package com.liyinan.myweather.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import com.liyinan.myweather.R;

public class WeatherPerDay extends View {
    private Paint mPaint;
    private TextPaint mTextPaint;
    private Path mPath;

    private int mNextHeightY;
    private int mNextLowY;
    private int mHeightY;
    private int mLowY;
    private int mPreHeightY;
    private int mPreLowY;

    private int mMaxNum=40;
    private int mMinNum=-20;

    private int mCaculatedNextHeightY;
    private int mCaculatedNextLowY;
    private int mCaculatedHeightY;
    private int mCaculatedLowY;
    private int mCaculatedPreHeightY;
    private int mCaculatedPreLowY;

    private int mWidth;
    private int mHeight;

    private String mHeightText;
    private String mLowText;

    private static final int sFIRSTITEM=0;
    private static final int sMIDITEM=1;
    private static final int sLASTITEM=2;
    private int mItemType;

    private static final String TAG="DiagramView";

    public WeatherPerDay(Context context) {
        super(context);
        init();
    }

    public WeatherPerDay(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public WeatherPerDay(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private int getMySize(int defaultSize, int measureSpec) {
        int mySize = defaultSize;

        int mode = MeasureSpec.getMode(measureSpec);
        int size = MeasureSpec.getSize(measureSpec);

        switch (mode) {
            case MeasureSpec.UNSPECIFIED: {//如果没有指定大小，就设置为默认大小
                mySize = defaultSize;
                break;
            }
            case MeasureSpec.AT_MOST: {//如果测量模式是最大取值为size
                //我们将大小取最大值,你也可以取其他值
                mySize = size;
                break;
            }
            case MeasureSpec.EXACTLY: {//如果是固定的大小，那就不要去改变它
                mySize = size;
                break;
            }
        }
        return mySize;
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec,heightMeasureSpec);
        mWidth=getMySize(60,widthMeasureSpec);
        mHeight=getMySize(100,heightMeasureSpec);
        setMeasuredDimension(mWidth, mHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        caculateY(mHeight,mMaxNum,mMinNum);
        mPaint.setColor(getResources().getColor(R.color.orange));
        canvas.drawCircle(mWidth/2,mCaculatedHeightY,4,mPaint);
        mPaint.setColor(getResources().getColor(R.color.gray));
        canvas.drawCircle(mWidth/2,mCaculatedLowY,4,mPaint);

        canvas.drawText(mHeightText,mWidth/2-mTextPaint.getTextSize()/2,mCaculatedHeightY-mTextPaint.getTextSize(),mTextPaint);
        canvas.drawText(mLowText,mWidth/2-mTextPaint.getTextSize()/2,mCaculatedLowY+mTextPaint.getTextSize()*3/2,mTextPaint);

        switch (mItemType){
            case sFIRSTITEM:
                mPath.reset();
                mPath.moveTo(mWidth/2,mCaculatedHeightY);
                mPath.lineTo(mWidth,(mCaculatedHeightY+mCaculatedNextHeightY)/2);
                mPaint.setColor(getResources().getColor(R.color.orange));
                canvas.drawPath(mPath,mPaint);
                mPath.reset();
                mPath.moveTo(mWidth/2,mCaculatedLowY);
                mPath.lineTo(mWidth,(mCaculatedLowY+mCaculatedNextLowY)/2);
                mPaint.setColor(getResources().getColor(R.color.gray));
                canvas.drawPath(mPath,mPaint);
                break;
            case sMIDITEM:
                mPath.reset();
                mPath.moveTo(0,(mCaculatedPreHeightY+mCaculatedHeightY)/2);
                mPath.lineTo(mWidth/2,mCaculatedHeightY);
                mPath.moveTo(mWidth/2,mCaculatedHeightY);
                mPath.lineTo(mWidth,(mCaculatedHeightY+mCaculatedNextHeightY)/2);
                mPaint.setColor(getResources().getColor(R.color.orange));
                canvas.drawPath(mPath,mPaint);
                mPath.reset();
                mPath.moveTo(0,(mCaculatedPreLowY+mCaculatedLowY)/2);
                mPath.lineTo(mWidth/2,mCaculatedLowY);
                                mPath.moveTo(mWidth/2,mCaculatedLowY);
                mPath.lineTo(mWidth,(mCaculatedLowY+mCaculatedNextLowY)/2);
                mPaint.setColor(getResources().getColor(R.color.gray));
                canvas.drawPath(mPath,mPaint);
                break;
            case sLASTITEM:
                mPath.reset();
                mPath.moveTo(0,(mCaculatedPreHeightY+mCaculatedHeightY)/2);
                mPath.lineTo(mWidth/2,mCaculatedHeightY);
                mPaint.setColor(getResources().getColor(R.color.orange));
                canvas.drawPath(mPath,mPaint);
                mPath.reset();
                mPath.moveTo(0,(mCaculatedPreLowY+mCaculatedLowY)/2);
                mPath.lineTo(mWidth/2,mCaculatedLowY);
                mPaint.setColor(getResources().getColor(R.color.gray));
                canvas.drawPath(mPath,mPaint);
                break;
                default:break;
        }
    }

    public void draws(int preHeightY,int preLowY,int heightY,int lowY,int itemType,boolean last,int max,int min){
        mMaxNum=max;
        mMinNum=min;
        this.mPreHeightY = preHeightY;
        this.mPreLowY = preLowY;
        this.mHeightY = heightY;
        this.mLowY =lowY;
        this.mItemType  =itemType;

        invalidate();
    }

    public void draws(int preHeightY,int preLowY,int heightY, int lowY, int nextHeighY, int nextLowY,int itemType,int max,int min) {
        mMaxNum=max;
        mMinNum=min;
        this.mPreHeightY =preHeightY;
        this.mPreLowY = preLowY;
        this.mHeightY = heightY;
        this.mLowY = lowY;
        this.mNextHeightY = nextHeighY;
        this.mNextLowY = nextLowY;
        this.mItemType  =itemType;


        invalidate();


    }

    public void draws(int heightY, int lowY, int nextHeighY, int nextLowY, int itemType,int max,int min) {
        mMaxNum=max;
        mMinNum=min;
        this.mHeightY = heightY;
        this.mLowY = lowY;
        this.mNextHeightY = nextHeighY;
        this.mNextLowY = nextLowY;
        this.mItemType  =itemType;

        invalidate();


    }

    private void caculateY(int height,int maxNum,int minNum) {
        float k=(2*(int)(mTextPaint.getTextSize()*2+0.5)-mHeight)/(maxNum-minNum);
        float b=(int)(mTextPaint.getTextSize()*2+0.5) -(2*(int)(mTextPaint.getTextSize()*2+0.5)-mHeight)/(maxNum-minNum)*maxNum;

        mCaculatedNextHeightY =(int)(k*mNextHeightY+b);
        mCaculatedNextLowY = (int)(k*mNextLowY+b);
        mCaculatedHeightY = (int)(k*mHeightY+b);
        mCaculatedLowY=(int)(k*mLowY+b);
        mCaculatedPreHeightY=(int)(k*mPreHeightY+b);
        mCaculatedPreLowY=(int)(k*mPreLowY+b);
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setColor(getResources().getColor(R.color.colorPrimary));
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(5);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeJoin(Paint.Join.BEVEL);
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);

        mPath = new Path();
        mItemType = 0;
        mTextPaint = new TextPaint();
        mTextPaint.setColor(getResources().getColor(R.color.colorDefaultBlack));
        mTextPaint.setAntiAlias(true);
        mTextPaint.setStrokeWidth(1);
        mTextPaint.setTextSize(30);
        mTextPaint.setStrokeCap(Paint.Cap.ROUND);

    }

    public void setText(int heightText,int lowText){
        this.mHeightText = heightText+"℃";
        this.mLowText = lowText+"℃";
    }
}
