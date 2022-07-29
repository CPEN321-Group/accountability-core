package com.cpen321group.accountability.reportpiechart;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class ReportPieChart extends View {
    private final Paint paint;
    private float center_X;
    private float center_Y;
    private float radiusDefault;
    private float radiusSelected;
    private ArrayList<PieEntry> pieEntries;
    private PieClickListener listener;

    public void setPieClickListener(PieClickListener listener) {
        this.listener = listener;
    }

    // Constructor
    public ReportPieChart(Context context) {
        super(context);
        paint = new Paint();
        paint.setTextSize((int) (context.getResources().getDisplayMetrics().scaledDensity * 15 + 0.5f));
        paint.setAntiAlias(true);
        pieEntries = new ArrayList<>();
    }

    public ReportPieChart(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
        paint.setTextSize((int) (context.getResources().getDisplayMetrics().scaledDensity * 15 + 0.5f));
        paint.setAntiAlias(true);
        pieEntries = new ArrayList<>();
    }

    public ReportPieChart(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        paint = new Paint();
        paint.setTextSize((int) (context.getResources().getDisplayMetrics().scaledDensity * 15 + 0.5f));
        paint.setAntiAlias(true);
        pieEntries = new ArrayList<>();
    }

    public void setRadiusDefault(float radiusDefault) {
        this.radiusDefault = radiusDefault;
    }

    public void setPieEntries(ArrayList<PieEntry> pieEntries) {
        this.pieEntries = pieEntries;
        invalidate();
    }

    public static int dp2px(Context context, float dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources()
                .getDisplayMetrics());
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                float touchX = event.getX();
                float touchY = event.getY();
                // Check if the touch point is inside the pie chart
                if (Math.pow(radiusDefault, 2) >= Math.pow(touchX - center_X, 2) + Math.pow(touchY - center_Y, 2)) {
                    float touchDegree;
                    float deltaTouchX = touchX - center_X;
                    float deltaTouchY = touchY - center_Y;
                    float degree = (float) Math.toDegrees(Math.atan(Math.abs(deltaTouchY) / Math.abs(deltaTouchX)));
                    // first quadrant
                    if (deltaTouchX >= 0 && deltaTouchY >= 0) {
                        touchDegree = degree;
                    }
                    // second quadrant
                    else if (deltaTouchX <= 0 && deltaTouchY >= 0) {
                        touchDegree = 180 - degree;
                    }
                    // third quadrant
                    else if (deltaTouchX <= 0 && deltaTouchY <= 0) {
                        touchDegree = 180 + degree;
                    }
                    // fourth quadrant
                    else {
                        touchDegree = 360 - degree;
                    }
                    // Traverse List<PieEntry> to determine the position in the pie chart
                    for (int i = 0; i < pieEntries.size(); i++) {
                        if (touchDegree < pieEntries.get(i).getDegreeEnd() && touchDegree >= pieEntries.get(i).getDegreeStart()) {
                            pieEntries.get(i).setSelected(true);
                            if (listener != null) {
                                listener.onItemClick(i);
                            }
                        } else {
                            pieEntries.get(i).setSelected(false);
                        }
                    }
                    // update canvas
                    invalidate();
                }
                break;
            default:
                // Do nothing
                break;
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // Sum up the value of all entries of chart.
        float sum = 0;
        for (int i = 0; i < pieEntries.size(); i++) {
            sum += pieEntries.get(i).getValue();

        }
        // initial the center coordinate position on canvas
        center_X = getPivotX();
        center_Y = getPivotY();
        // radius is non-negative, if it's not positive, retrieve size from canvas
        if (radiusSelected <= 0) {
            if ( getWidth() > getHeight()) {
                radiusSelected = getHeight() / 2;
            } else {
                radiusSelected = getWidth() / 2;
            }
        }
        // radius in selected status has radius larger than default status (5px)
        radiusDefault = radiusSelected - dp2px(getContext(), 5);

        float degreeStart = 0;
        // Traverse List<PieEntry> rendering pie chart on canvas
        for (int i = 0; i < pieEntries.size(); i++) {
            // Current entry degree
            float degree = sum <= 0 ? degree = 360 / pieEntries.size() : 360 * (pieEntries.get(i).getValue() / sum);
            // Set color for current entry
            paint.setColor(getResources().getColor(pieEntries.get(i).getColor()));
            // determine the radius by checking if the current portion has been selected
            float radiusCurrent = pieEntries.get(i).isSelected()? radiusSelected : radiusDefault;
            // Render portion on canvas
            canvas.drawArc(new RectF(center_X - radiusCurrent, center_Y - radiusCurrent, center_X + radiusCurrent, center_Y + radiusCurrent), degreeStart, degree, true, paint);
            // update degree value in the entry object
            pieEntries.get(i).setDegreeStart(degreeStart);
            pieEntries.get(i).setDegreeEnd(degreeStart + degree);
            degreeStart += degree;
        }
    }
}
