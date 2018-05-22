package com.why94.timedownview;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.why94.view.CountDownView;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //模式样式
        cdv(R.id.cdv_1)
                .start(1, 1, 1, 1, CountDownView.Unit.MILLISECOND);

        //默认样式,修改显示区域
        cdv(R.id.cdv_2)
                .setShowRange(CountDownView.Unit.SECOND, CountDownView.Unit.HOUR)
                .start(1, 1, 1, 1, CountDownView.Unit.MILLISECOND);

        //默认样式,修改显示样式
        cdv(R.id.cdv_3)
                .setNumberBackground(0xFFFF9800)
                .setNumberRound(Integer.MAX_VALUE)
                .setUnitBackground(0xFFFF9800)
                .setUnitRound(Integer.MAX_VALUE)
                .setUnitColor(Color.WHITE)
                .setShowRange(CountDownView.Unit.SECOND, CountDownView.Unit.DAY)
                .start(1, 1, 1, 1, CountDownView.Unit.MILLISECOND);

        //修改部分绘制模块
        CountDownView cdv4 = cdv(R.id.cdv_4);
        cdv4.setPainter(
                cdv4.createDefaultNumberPainter(CountDownView.Unit.HOUR),
                new ColonPainter(false),
                cdv4.createDefaultNumberPainter(CountDownView.Unit.MINUTE),
                new ColonPainter(true),
                cdv4.createDefaultNumberPainter(CountDownView.Unit.SECOND)
        )
                .setNumberColor(Color.BLACK)
                .setNumberBackground(Color.TRANSPARENT)
                .setShowRange(CountDownView.Unit.MILLISECOND, CountDownView.Unit.HOUR)
                .start(1, 1, 1, 1, CountDownView.Unit.MILLISECOND);

        //自定义绘制模块
        cdv(R.id.cdv_5)
                .setPainter(new ProgressPainter())
                .setShowRange(CountDownView.Unit.MILLISECOND, CountDownView.Unit.SECOND)
                .start(999, CountDownView.Unit.MILLISECOND);
    }

    private CountDownView cdv(@IdRes int id) {
        return findViewById(id);
    }

    public static float density = Resources.getSystem().getDisplayMetrics().density;
    public static float scaledDensity = Resources.getSystem().getDisplayMetrics().scaledDensity;

    public static int dp2px(float v) {
        return (int) (density * v + 0.5);
    }

    public static int sp2px(float v) {
        return (int) (scaledDensity * v + 0.5);
    }

    /**
     * 自定义样式
     */
    private static class ProgressPainter extends CountDownView.PainterAdapter {

        private Paint progressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        static final int PAINTER_SIZE = dp2px(64);
        static final int PAINTER_WIDTH = dp2px(4);
        static final int TEXT_SIZE = sp2px(12);
        static final float TEXT_SIZE_SCALE_CHANGE = 0.2f;
        static final float TEXT_SCALE_CYCLE = 1000;//文字1秒一个收缩
        static final float PROGRESS_SPIN_CYCLE = 8000;//外环8秒一个循环
        static final int PROGRESS_START_COLOR = 0x002196F3;
        static final int PROGRESS_END_COLOR = 0x662196F3;
        static final int TEXT_COLOR = 0x9903A9F4;

        ProgressPainter() {
            super(true, PAINTER_SIZE, PAINTER_SIZE);
            progressPaint.setShader(new SweepGradient(PAINTER_SIZE / 2, PAINTER_SIZE / 2, PROGRESS_START_COLOR, PROGRESS_END_COLOR));
            progressPaint.setStrokeCap(Paint.Cap.ROUND);
            progressPaint.setStyle(Paint.Style.STROKE);
            progressPaint.setStrokeWidth(PAINTER_WIDTH / 2);
            textPaint.setTextAlign(Paint.Align.CENTER);
            textPaint.setTextSize(TEXT_SIZE);
            textPaint.setColor(TEXT_COLOR);
        }

        @Override
        public void draw(Canvas canvas, RectF area, CountDownView.Unit min, CountDownView.Unit max, long current) {
            float centerX = area.centerX();
            float centerY = area.centerY();
            //绘制圆环
            canvas.save();
            canvas.rotate(360 * (current % PROGRESS_SPIN_CYCLE / PROGRESS_SPIN_CYCLE), centerX, centerY);
            area.inset(PAINTER_WIDTH / 2, PAINTER_WIDTH / 2);
            canvas.drawArc(area, 5, 350, false, progressPaint);
            canvas.restore();
            String value = String.valueOf(CountDownView.Unit.SECOND.value(min, max, current)) + "秒";
            //绘制数字
            canvas.save();
            float scale = 1 + TEXT_SIZE_SCALE_CHANGE * 2 * Math.abs((current % TEXT_SCALE_CYCLE / TEXT_SCALE_CYCLE) - 0.5f);
            canvas.scale(scale, scale, centerX, centerY);
            canvas.drawText(value, centerX, centerY - (textPaint.descent() + textPaint.ascent()) / 2f, textPaint);
            canvas.restore();
        }
    }

    /**
     * 绘制冒号模块
     */
    private static class ColonPainter extends CountDownView.PainterAdapter {
        private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final char[] show = new char[]{':'};
        private boolean flashing;

        ColonPainter(boolean flashing) {
            super(true, 36, 18);
            this.flashing = flashing;
            this.paint.setTextSize(36);
            this.paint.setTextAlign(Paint.Align.CENTER);
        }

        @Override
        public void draw(Canvas canvas, RectF area, CountDownView.Unit min, CountDownView.Unit max, long current) {
            long value = CountDownView.Unit.MILLISECOND.value(min, max, current);
            if (!flashing || value > 50) {
                canvas.drawText(show, 0, 1, area.centerX(), area.centerY() - (paint.descent() + paint.ascent()) / 2f, paint);
            }
        }
    }
}