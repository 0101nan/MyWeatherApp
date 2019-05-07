package com.liyinan.myweather.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;
import androidx.annotation.Nullable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import com.liyinan.myweather.R;

public class WeatherPerHourView extends View {
    private static final int sFIRSTITEM=0;
    private static final int sMIDITEM=1;
    private static final int sLASTITEM=2;

    private int mItemType;
    private Paint mPaint;
    private TextPaint mTextPaint;
    private Path mPath;
    private Path mFillPath=new Path();
    private int mNextHeightY;
    private int mHeightY;
    private int mPreHeightY;
    private int mMaxNum=40;
    private int mMinNum=-20;
    private int mCaculatedNextHeightY;
    private int mCaculatedHeightY;
    private int mCaculatedPreHeightY;
    private int mWidth;
    private int mHeight;
    private String mHeightText;



    public WeatherPerHourView(Context context) {
        super(context);
        init();
    }

    public WeatherPerHourView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public WeatherPerHourView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
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
        caculateY(mMaxNum,mMinNum);

        canvas.drawCircle(mWidth/2,mCaculatedHeightY,4,mPaint);
        canvas.drawText(mHeightText,mWidth/2-mTextPaint.getTextSize()/2,mCaculatedHeightY-mTextPaint.getTextSize(),mTextPaint);
        LinearGradient linearGradient=new LinearGradient(
                mWidth/2,0,mWidth/2,mHeight,
                getResources().getColor(R.color.colorPrimary),getResources().getColor(R.color.colorTransparent),
                Shader.TileMode.MIRROR
        );
        switch (mItemType){
            case sFIRSTITEM:
                mPath.moveTo(mWidth/2,mCaculatedHeightY);
                mPath.lineTo(mWidth,(mCaculatedHeightY+mCaculatedNextHeightY)/2);
                canvas.drawPath(mPath,mPaint);
                /*
                mFillPath.reset();
                mFillPath.addPath(mPath);
                mFillPath.lineTo(mWidth,mHeight);
                mFillPath.lineTo(mWidth/2,mHeight);
                mFillPath.close();

                mPaint.setShader(linearGradient);
                //mPaint.setColor(getResources().getColor(R.color.colorPrimary));
                mPaint.setStyle(Paint.Style.FILL);
                canvas.drawPath(mFillPath,mPaint);
                */
                break;
            case sMIDITEM:
                mPath.moveTo(0,(mCaculatedPreHeightY+mCaculatedHeightY)/2);
                mPath.lineTo(mWidth/2,mCaculatedHeightY);
                mPath.moveTo(mWidth/2,mCaculatedHeightY);
                mPath.lineTo(mWidth,(mCaculatedHeightY+mCaculatedNextHeightY)/2);
                canvas.drawPath(mPath,mPaint);
                /*
                mFillPath.reset();
                mFillPath.addPath(mPath);
                mFillPath.lineTo(mWidth,mHeight);
                mFillPath.lineTo(0,mHeight);
                mFillPath.lineTo(0,(mCaculatedHeightY+mCaculatedPreHeightY)/2);
                mFillPath.close();

                mPaint.setShader(linearGradient);
                //mPaint.setColor(getResources().getColor(R.color.colorPrimary));
                mPaint.setStyle(Paint.Style.FILL);
                canvas.drawPath(mFillPath,mPaint);
                */
                break;
            case sLASTITEM:
                mPath.moveTo(0,(mCaculatedPreHeightY+mCaculatedHeightY)/2);
                mPath.lineTo(mWidth/2,mCaculatedHeightY);
                canvas.drawPath(mPath,mPaint);
                /*
                mFillPath.reset();
                mFillPath.addPath(mPath);
                mFillPath.lineTo(mWidth/2,mHeight);
                mFillPath.lineTo(0,mHeight);
                mFillPath.close();

                mPaint.setShader(linearGradient);
                //mPaint.setColor(getResources().getColor(R.color.colorPrimary));
                mPaint.setStyle(Paint.Style.FILL);
                canvas.drawPath(mFillPath,mPaint);
                */
                break;
            default:break;
        }
        //canvas.drawPath(mPath,mPaint);
    }

    public void draws(int preHeightY,int heightY,int itemType,boolean last,int max,int min){
        mMaxNum=max;
        mMinNum=min;
        this.mPreHeightY = preHeightY;
        this.mHeightY = heightY;
        this.mItemType  =itemType;
        invalidate();
    }

    public void draws(int preHeightY,int heightY,int nextHeighY,int itemType,int max,int min) {
        mMaxNum=max;
        mMinNum=min;
        this.mPreHeightY =preHeightY;
        this.mHeightY = heightY;
        this.mNextHeightY = nextHeighY;
        this.mItemType  =itemType;
        invalidate();
    }

    public void draws(int heightY,int nextHeighY, int itemType,int max,int min) {
        mMaxNum=max;
        mMinNum=min;
        this.mHeightY = heightY;
        this.mNextHeightY = nextHeighY;
        this.mItemType  =itemType;
        invalidate();
    }

    private void caculateY(int maxNum,int minNum) {
        float k=(2*(int)(mTextPaint.getTextSize()*2+0.5)-mHeight)/(maxNum-minNum);
        float b=(int)(mTextPaint.getTextSize()*2+0.5) -(2*(int)(mTextPaint.getTextSize()*2+0.5)-mHeight)/(maxNum-minNum)*maxNum;
        mCaculatedNextHeightY =(int)(k*mNextHeightY+b);
        mCaculatedHeightY = (int)(k*mHeightY+b);
        mCaculatedPreHeightY=(int)(k*mPreHeightY+b);
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setColor(getResources().getColor(R.color.orange));
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

    public void setText(int heightText){
        this.mHeightText = heightText+"℃";
    }
}
