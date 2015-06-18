package team.monroe.org.pocketfit.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

public class FPCProgressBarView extends View{

    private Paint linePaint;
    private Paint valuePaint;

    private float proteinValue = 0.2f;
    private float fatsValue = 0.2f;
    private float carbsValue = 0.6f;
    private int mProteinColor;
    private int mFatsColor;
    private int mCurbsColor;

    public FPCProgressBarView(Context context) {
        super(context);
        init(context);
    }

    public FPCProgressBarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public FPCProgressBarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public FPCProgressBarView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        linePaint = new Paint();
        linePaint.setStrokeWidth(2);
        linePaint.setColor(Color.parseColor("#11606060"));

        valuePaint = new Paint();
        valuePaint.setColor(Color.parseColor("#fb3666"));

        mProteinColor = Color.parseColor("#fb3666");
        mFatsColor = Color.parseColor("#FFA32342");
        mCurbsColor = Color.parseColor("#ffff6d78");
    }


    @Override
    protected void onDraw(Canvas canvas) {
        valuePaint.setStrokeWidth(getHeight());
        linePaint.setStrokeWidth(getHeight());
        canvas.drawLine(0,getHeight()/2,getWidth(),getHeight()/2, linePaint);

        float startX = 0;
        float endX = 0 + getWidth()*proteinValue;
        valuePaint.setColor(mProteinColor);
        canvas.drawLine(startX,getHeight()/2,endX, getHeight()/2, valuePaint);

        startX = endX;
        endX += getWidth() * fatsValue;
        valuePaint.setColor(mFatsColor);
        canvas.drawLine(startX,getHeight()/2,endX, getHeight()/2, valuePaint);


        startX = endX;
        endX += getWidth() * carbsValue;
        valuePaint.setColor(mCurbsColor);
        canvas.drawLine(startX,getHeight()/2,endX, getHeight()/2, valuePaint);

    }

    public void setGram(float protein, float fat, float carbs) {
        float sum = protein+fat+carbs;
        if (sum == 0){
            carbsValue = 0;
            proteinValue = 0;
            fatsValue = 0;
        }

        this.carbsValue = carbs/sum;
        this.fatsValue = fat/sum;
        this.proteinValue = protein/sum;

        this.invalidate();
    }
}
