package com.htc.launcher.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Xfermode;
import android.util.AttributeSet;
import android.widget.ImageView;

public class CircleImageView extends ImageView {
    private int width;
    private int height;
    private Path path;
    private Path srcPath;
    private Paint paint;
    private RectF srcRectF;
    private Xfermode xfermode;

    public CircleImageView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        path = new Path();
        srcPath = new Path();
        paint = new Paint();
        xfermode = new PorterDuffXfermode(PorterDuff.Mode.DST_OUT);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.saveLayer(srcRectF, null, Canvas.ALL_SAVE_FLAG);
        super.onDraw(canvas);// ImageView自身的绘制流程，即绘制图片

        path.reset();
        paint.reset();
        path.addCircle(width / 2, height / 2, width / 2, Path.Direction.CW);//顺时针,这里是一个简单的园,无影响
        paint.setStyle(Paint.Style.FILL);//Paint 默认就是fill style,不设置也可以
        paint.setAntiAlias(true);//抗锯齿
        paint.setXfermode(xfermode);//设置混合模式 DST_OUT
        srcPath.addRect(srcRectF, Path.Direction.CCW);
        srcPath.op(path, Path.Op.DIFFERENCE);
        canvas.drawPath(srcPath, paint);
        paint.setXfermode(null);
        canvas.restore();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;
        srcRectF = new RectF(0, 0, w, h);
    }

    public CircleImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public CircleImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleImageView(Context context) {
        this(context, null);
    }
}