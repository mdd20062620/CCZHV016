package com.ruiguan.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import com.ruiguan.R;

import java.text.DecimalFormat;

/**
 * Author LYJ
 * Created on 2016/12/20.
 * Time 10:56
 */

public class DialPlateView extends View {
    private static final int DEFAULT_SIDE = 200;//默认宽度
    private static final int DEFAULT_NUM = 0;//默认值0
    private static final float DEFAULT_ARC_ROTATE = 0f;//旋转角度
    private static final float DEFAULT_DEGREES = 0f;//表针的默认旋转角度
    private static final float DEFAULT_ARC_DEGREES = 90f;//弧形的角度
    private static final float DEFAULT_SCALE_VALUE = 40f;//刻度数量
    private static final String DEFAULT_SHOW_DB = "0.000";//默认显示的值
    private boolean isInited;//是否为初始化过
    private float rotateValues;//旋转角度
    private int width;//控件的宽度
    private int height;//控件的高度
    private Bitmap dialBitmap;//表盘
    private Bitmap pointerBitmap;//指针
    private Rect drawBitmapRect;//绘制图片的区域
    private String showDB = DEFAULT_SHOW_DB;//显示分贝内容
    private Matrix matrix;//矩阵用于图像变换
    private Paint paint;//画笔对象
    private boolean drawControlLine;//是否绘制控制线
    public DialPlateView(Context context) {
        this(context, null);
    }
    public DialPlateView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();//初始化操作
    }
    /**
     * 初始化操作
     */
    private void init() {
        initBitmap();//初始化位图
        paint = new Paint();
        paint.setStrokeWidth(dp2px(4));//画笔粗度
        paint.setStyle(Paint.Style.STROKE);//画笔模式
        paint.setTextSize(dp2px(10));//字体大小
        paint.setAntiAlias(true);//抗锯齿
        paint.setDither(true);//防抖动
        paint.setTextAlign(Paint.Align.CENTER);//文字居中
        drawBitmapRect = new Rect();//初始化绘制区域
        rotateValues = DEFAULT_ARC_DEGREES / DEFAULT_SCALE_VALUE;//旋转角度
    }
    /**
     * 初始化位图
     */
    private void initBitmap() {
        //获取表盘位图
        dialBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.dial);
        //获取表针位图
        pointerBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.needle).copy(Bitmap.Config.ARGB_8888, true);
    }

    /**
     * 更新表针的显示效果
     *
     * @param values
     */
    public void upDataValues(float values) {
        DecimalFormat decimalFormat = new DecimalFormat(".###");
        showDB = decimalFormat.format(values);
        matrix.setRotate(-(DEFAULT_DEGREES - values * rotateValues), getWidth() / 2, getHeight() / 2);
        invalidate();
    }

    /**
     * 设置图像旋转角度
     *
     * @param degrees
     */
    private void setBitmapDegrees(float degrees) {
        matrix = new Matrix();
        int pointerWidth = width;
        int pointerHeight = height;
        matrix.setRotate(-degrees, pointerWidth / 2, pointerHeight / 2);
        pointerBitmap = Bitmap.createScaledBitmap(pointerBitmap, width, height, true);
    }

    /**
     * 设置宽高相同
     *
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int size = opinionSide(MeasureSpec.getMode(widthMeasureSpec),
                MeasureSpec.getSize(widthMeasureSpec));
        width = height = size;//获取控件的宽高度
        setMeasuredDimension(width, height);//设置控件的宽高度
        //设置绘制图片的区域
        drawBitmapRect.left = DEFAULT_NUM;
        drawBitmapRect.top = DEFAULT_NUM;
        drawBitmapRect.right = width;
        drawBitmapRect.bottom = height;
    }

    /**
     * 测量
     *
     * @param mode
     * @param size
     * @return
     */
    private int opinionSide(int mode, int size) {
        int result = DEFAULT_NUM;
        if (mode == MeasureSpec.EXACTLY) {
            result = size;
        } else {
            // 设置默认边长
            int defaultSize = dp2px(DEFAULT_SIDE);
            if (mode == MeasureSpec.AT_MOST) {
                result = Math.min(size, defaultSize);
            }
        }
        return result;
    }

    /**
     * 绘制
     *
     * @param canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (isInited == false) {
            //设置图片旋转角度
            setBitmapDegrees(DEFAULT_DEGREES);
            isInited = true;
        }
        //每次绘制前清空画布
        //绘制表盘图片
        canvas.drawBitmap(dialBitmap, null, drawBitmapRect, null);
        canvas.drawBitmap(pointerBitmap, matrix, null);
        if (drawControlLine) {
            RectF rectF = new RectF(dp2px(4), dp2px(4), width - dp2px(4), height - dp2px(4));
            paint.setColor(0xffff0000);
            paint.setStrokeWidth(dp2px(4));
            canvas.drawArc(rectF, DEFAULT_ARC_ROTATE, DEFAULT_ARC_DEGREES, false, paint);
            canvas.save();
            canvas.rotate(-DEFAULT_ARC_ROTATE, width / 2, height / 2);
            for (int i = 0; i <= DEFAULT_SCALE_VALUE; i++) {
                //绘制长刻度
                if (i % 10 == 0) {
                    canvas.drawLine(width / 2, 0, width / 2, height / 16, paint);
                } else {
                    //绘制短刻度
                    canvas.drawLine(width / 2, 0, width / 2, height / 32, paint);
                }
                canvas.rotate(rotateValues, width / 2, height / 2);
            }
            canvas.restore();
        }
        /**
         * 绘制文字
         */
        paint.setColor(0xffffffff);
        paint.setStrokeWidth(0);
        canvas.drawText(showDB + "%", width / 2, (float) height / 4f * 2.7f, paint);
    }

    /**
     * 设置绘制控制线
     * @param flag
     */
    public void setDrawControlLine(boolean flag){
        drawControlLine = flag;
    }
    /**
     * dp转px
     */
    private int dp2px(int value) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, value,
                getResources().getDisplayMetrics());
    }
}
