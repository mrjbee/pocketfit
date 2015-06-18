package team.monroe.org.pocketfit.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

public class HorProgressBarView extends View{
    private Paint linePaint;
    private Paint valuePaint;
    private float progress = 0.3f;

    public HorProgressBarView(Context context) {
        super(context);
        init(context);
    }

    public HorProgressBarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public HorProgressBarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public HorProgressBarView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        linePaint = new Paint();
        linePaint.setStrokeWidth(2);
        linePaint.setColor(Color.parseColor("#11606060"));

        valuePaint = new Paint();
        valuePaint.setColor(Color.parseColor("#fb3666"));
    }


    @Override
    protected void onDraw(Canvas canvas) {
        valuePaint.setStrokeWidth(getHeight());
        linePaint.setStrokeWidth(getHeight());
        canvas.drawLine(0,getHeight()/2,getWidth(),getHeight()/2, linePaint);
        canvas.drawLine(0,getHeight()/2,getWidth()*progress, getHeight()/2, valuePaint);
    }

    public float getProgress() {
        return progress;
    }

    public void setProgress(float progress) {
        this.progress = progress;
        this.invalidate();
    }
}
