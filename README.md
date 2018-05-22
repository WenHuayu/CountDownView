# 一个可以自定义样式的倒计时View

![image](https://github.com/WenHuayu/CountDownView/blob/master/img/show.gif)

#### 1.在项目文件中添加jitpack仓库,如果已经有了就不用了

	allprojects {
			repositories {
				...
				maven { url 'https://jitpack.io' }
			}
		}
	
#### 2.在模块中添加模块

	dependencies {
				implementation 'com.github.WenHuayu:CountDownView:1.0.0'
		}
	
#### 3.在xml中使用

    <com.why94.view.CountDownView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        app:cdv_number_background="@android:color/black"
        app:cdv_number_color="@android:color/white"
        app:cdv_number_height="16dp"
        app:cdv_number_round="3dp"
        app:cdv_number_size="12sp"
        app:cdv_number_width="16dp"
        app:cdv_unit_background="@android:color/transparent"
        app:cdv_unit_color="@android:color/black"
        app:cdv_unit_height="16dp"
        app:cdv_unit_round="3dp"
        app:cdv_unit_size="12sp"
        app:cdv_unit_width="16dp" />
		
#### 4.在代码中使用
        
	CountDownView view = findViewById(R.id.cdv);
	
        view.start(...);

## 自定义样式

创建一个绘制类继承CountDownView.PainterAdapter,在draw(...)方法内绘制自己想要的样式

然后通过CountDownView.setPainter()将绘制模块添加到View内

    /**
     * 自定义样式,心跳的数字,转圈的边框
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

#

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
