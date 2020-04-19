package com.shinelw.library;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;

import androidx.core.content.ContextCompat;

//
//
//<com.shinelw.library.ColorArcProgressBar
//        android:layout_width="300dp"
//        android:layout_height="300dp"
//        android:layout_gravity="center_horizontal"
//        android:id="@+id/bar1"
//        app:is_need_content="true"
//        app:front_color1="@color/colorAccent"
//        app:max_value="100"
//        app:back_width="10dp"
//        app:front_width="10dp"
//
//
//        app:back_color="@android:color/darker_gray"
//        android:layout_marginBottom="150dp"
//        app:layout_constraintBottom_toBottomOf="parent"
//        app:layout_constraintStart_toStartOf="parent"/>


public class ColorArcProgressBar extends View {

    private int mWidth;
    private int mHeight;
    private int diameter = 500;  //直径
    private float centerX;  //圆心X坐标
    private float centerY;  //圆心Y坐标

    private Paint allArcPaint;
    private Paint smallerPaint;
    private Paint progressPaint;
    private Paint vTextPaint;
    private Paint hintPaint;
    private Paint degreePaint;
    private Paint curSpeedPaint;

    private RectF bgRect;

    private ValueAnimator progressAnimator;
    private PaintFlagsDrawFilter mDrawFilter;
    private SweepGradient sweepGradient;
    private Matrix rotateMatrix;

    private float startAngle = 180;
    private float sweepAngle = 180;
    private float currentAngle = 0;
    public float lastAngle;
    private int[] colors = new int[]{Color.GREEN, Color.YELLOW, Color.RED, Color.RED};
    private float maxValues = 60;
    private float curValues = 0;
    private float textSize = dipToPx(60);
    private float hintSize = dipToPx(15);
    private float curSpeedSize = dipToPx(13);
    private int aniSpeed = 1000;
    private float longdegree = dipToPx(0);
    private float shortdegree = dipToPx(0);
    private final int DEGREE_PROGRESS_DISTANCE = dipToPx(0);

    private String hintColor = "#676767";
    private String longDegreeColor = "#111111";

    private boolean isNeedTitle;
    private boolean isNeedUnit;
    private boolean isNeedDial;
    private boolean isNeedContent;

    private float bigArchWidth = dipToPx(26);
    private float smallArchWidth = dipToPx(16);
    private float progressArchWidth = dipToPx(6);
    private float k;

    public ColorArcProgressBar(Context context) {
        super(context, null);
        initView();
    }

    public ColorArcProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
        initCofig(context, attrs);
        initView();
    }

    public ColorArcProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initCofig(context, attrs);
        initView();
    }

    /**
     * 初始化布局配置
     *
     * @param context
     * @param attrs
     */
    private void initCofig(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ColorArcProgressBar);
        int color1 = ContextCompat.getColor(getContext(), R.color.low_risk);
        int color2 = ContextCompat.getColor(getContext(), R.color.medium_risk);
        int color3 = ContextCompat.getColor(getContext(), R.color.high_risk);
        colors = new int[]{color1, color2, color3, color3};

        sweepAngle = a.getInteger(R.styleable.ColorArcProgressBar_total_engle, 180);

        isNeedTitle = a.getBoolean(R.styleable.ColorArcProgressBar_is_need_title, false);
        isNeedContent = a.getBoolean(R.styleable.ColorArcProgressBar_is_need_content, false);
        isNeedUnit = a.getBoolean(R.styleable.ColorArcProgressBar_is_need_unit, false);
        isNeedDial = a.getBoolean(R.styleable.ColorArcProgressBar_is_need_dial, false);
        curValues = a.getFloat(R.styleable.ColorArcProgressBar_current_value, 0);
        maxValues = a.getFloat(R.styleable.ColorArcProgressBar_max_value, 60);
        setCurrentValues(curValues);
        setMaxValues(maxValues);
        a.recycle();

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = (int) (2 * longdegree + bigArchWidth + diameter + 2 * DEGREE_PROGRESS_DISTANCE);
        int height = (int) (2 * longdegree + bigArchWidth + diameter / 2 + 2 * DEGREE_PROGRESS_DISTANCE);
        setMeasuredDimension(width, height);
    }


    private void initView() {

        diameter = 3 * getScreenWidth() / 5;

        bgRect = new RectF();
        bgRect.top = longdegree + bigArchWidth / 2 + DEGREE_PROGRESS_DISTANCE;
        bgRect.left = longdegree + bigArchWidth / 2 + DEGREE_PROGRESS_DISTANCE;
        bgRect.right = diameter + (longdegree + bigArchWidth / 2 + DEGREE_PROGRESS_DISTANCE);
        bgRect.bottom = diameter + (longdegree + bigArchWidth / 2 + DEGREE_PROGRESS_DISTANCE);

        //圆心
        centerX = (2 * longdegree + bigArchWidth + diameter + 2 * DEGREE_PROGRESS_DISTANCE) / 2;
        centerY = (2 * longdegree + bigArchWidth + diameter + 2 * DEGREE_PROGRESS_DISTANCE) / 2;

        //外部刻度线
        degreePaint = new Paint();
        degreePaint.setColor(Color.parseColor(longDegreeColor));


        allArcPaint = new Paint();
        allArcPaint.setAntiAlias(true);
        allArcPaint.setStyle(Paint.Style.STROKE);
        allArcPaint.setStrokeWidth(bigArchWidth);
        allArcPaint.setColor(ContextCompat.getColor(getContext(), R.color.arch_border));
        allArcPaint.setStrokeCap(Paint.Cap.ROUND);

        smallerPaint = new Paint();
        smallerPaint.setAntiAlias(true);
        smallerPaint.setStyle(Paint.Style.STROKE);
        smallerPaint.setStrokeWidth(smallArchWidth);
        smallerPaint.setColor(ContextCompat.getColor(getContext(), R.color.ach_inside));
        smallerPaint.setStrokeCap(Paint.Cap.ROUND);


        //当前进度的弧形
        createProgressPaint();

        //内容显示文字
        vTextPaint = new Paint();
        vTextPaint.setTextSize(textSize);
        vTextPaint.setColor(Color.BLACK);
        vTextPaint.setTextAlign(Paint.Align.CENTER);

        //显示单位文字
        hintPaint = new Paint();
        hintPaint.setTextSize(hintSize);
        hintPaint.setColor(ContextCompat.getColor(getContext(), R.color.black_50));
        hintPaint.setTextAlign(Paint.Align.CENTER);

        //显示标题文字
        curSpeedPaint = new Paint();
        curSpeedPaint.setTextSize(curSpeedSize);
        curSpeedPaint.setColor(Color.parseColor(hintColor));
        curSpeedPaint.setTextAlign(Paint.Align.CENTER);

        mDrawFilter = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
        sweepGradient = new SweepGradient(centerX, centerY, colors, null);
        rotateMatrix = new Matrix();

    }

    private int progressColor = Color.GREEN;

    private void createProgressPaint() {
        progressPaint = new Paint();
        progressPaint.setAntiAlias(true);
        progressPaint.setStyle(Paint.Style.STROKE);
        progressPaint.setStrokeCap(Paint.Cap.ROUND);
        progressPaint.setStrokeWidth(progressArchWidth);
        progressPaint.setColor(progressColor);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //抗锯齿
        canvas.setDrawFilter(mDrawFilter);


        //整个弧
        canvas.drawArc(bgRect, startAngle, sweepAngle, false, allArcPaint);
        canvas.drawArc(bgRect, startAngle, sweepAngle, false, smallerPaint);

        rotateMatrix.setRotate(130, centerX, centerY);
        sweepGradient.setLocalMatrix(rotateMatrix);
//        progressPaint.setShader(sweepGradient);

        //当前进度
        canvas.drawArc(bgRect, startAngle, currentAngle, false, progressPaint);
        invalidate();

    }

    /**
     * 设置最大值
     *
     * @param maxValues
     */
    public void setMaxValues(float maxValues) {
        this.maxValues = maxValues;
        k = sweepAngle / maxValues;
    }

    /**
     * 设置当前值
     *
     * @param currentValues
     */
    int startColor = 0;
    int endColor = 0;

    public void setCurrentValues(float currentValues) {
        if (currentValues > maxValues) {
            currentValues = maxValues;
        }
        if (currentValues < 0) {
            currentValues = 0;
        }


        if (curValues > 70) {
            startColor = colors[2];
        } else if (curValues > 30) {
            startColor = colors[1];
        } else {
            startColor = colors[0];
        }

        if (currentValues > 70) {
            endColor = colors[2];
        } else if (currentValues > 30) {
            endColor = colors[1];
        } else {
            endColor = colors[0];
        }


        this.curValues = currentValues;
        lastAngle = currentAngle;

        setAnimation(lastAngle, currentValues * k, aniSpeed);
    }

    public void setValueNotAnimated(float currentValues) {

    }


    /**
     * 设置速度文字大小
     *
     * @param textSize
     */
    public void setTextSize(int textSize) {
        this.textSize = textSize;
    }

    /**
     * 设置单位文字大小
     *
     * @param hintSize
     */
    public void setHintSize(int hintSize) {
        this.hintSize = hintSize;
    }

    /**
     * 设置单位文字
     *
     * @param hintString
     */


    /**
     * 设置直径大小
     *
     * @param diameter
     */
    public void setDiameter(int diameter) {
        this.diameter = dipToPx(diameter);
    }

    /**
     * 设置标题
     *
     * @param title
     */

    /**
     * 设置是否显示标题
     *
     * @param isNeedTitle
     */
    private void setIsNeedTitle(boolean isNeedTitle) {
        this.isNeedTitle = isNeedTitle;
    }

    /**
     * 设置是否显示单位文字
     *
     * @param isNeedUnit
     */
    private void setIsNeedUnit(boolean isNeedUnit) {
        this.isNeedUnit = isNeedUnit;
    }

    /**
     * 设置是否显示外部刻度盘
     *
     * @param isNeedDial
     */
    private void setIsNeedDial(boolean isNeedDial) {
        this.isNeedDial = isNeedDial;
    }

    /**
     * 为进度设置动画
     *
     * @param last
     * @param current
     */


    private void setAnimation(float last, float current, final int length) {
        progressAnimator = ValueAnimator.ofFloat(last, current);


        progressAnimator.setDuration(length);
        progressAnimator.setTarget(currentAngle);
        progressAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                currentAngle = (float) animation.getAnimatedValue();
                curValues = currentAngle / k;

            }
        });
        progressAnimator.start();

        ValueAnimator colorAnimation =
                ValueAnimator.ofObject(new ArgbEvaluator(), startColor, endColor);
        colorAnimation.setDuration(length);
        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                progressColor = (int) animation.getAnimatedValue();
                if (progressPaint != null)
                    progressPaint.setColor(progressColor);
            }
        });
        colorAnimation.start();
    }

    /**
     * dip 转换成px
     *
     * @param dip
     * @return
     */
    private int dipToPx(float dip) {
        float density = getContext().getResources().getDisplayMetrics().density;
        return (int) (dip * density + 0.5f * (dip >= 0 ? 1 : -1));
    }

    /**
     * 得到屏幕宽度
     *
     * @return
     */
    private int getScreenWidth() {
        WindowManager windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.widthPixels;
    }
}
