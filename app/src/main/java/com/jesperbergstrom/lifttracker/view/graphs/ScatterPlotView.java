package com.jesperbergstrom.lifttracker.view.graphs;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.View;

import com.jesperbergstrom.lifttracker.model.Date;

import java.util.ArrayList;
import java.util.Collections;

public class ScatterPlotView extends View {

    private Paint blue;
    private Paint red;
    private Paint black;
    private Paint white;
    private Paint gray;
    private Paint font;

    private final int PADDING = 150;
    private int width;

    private ArrayList<PlotPoint> data;
    private Axis xAxis;
    private Axis yAxis;
    private boolean drawLine = true;
    private boolean propDates = false;

    public ScatterPlotView(Context context, AttributeSet attrs) {
        super(context, attrs);
        blue = new Paint();
        blue.setColor(Color.BLUE);
        red = new Paint();
        red.setColor(Color.RED);
        black = new Paint();
        black.setColor(Color.BLACK);
        white = new Paint();
        white.setColor(Color.WHITE);
        white.setStrokeWidth(3);
        gray = new Paint();
        gray.setColor(Color.GRAY);
        font = new Paint();
        font.setColor(Color.WHITE);
        font.setTextSize(40);

        data = new ArrayList<PlotPoint>();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(widthMeasureSpec, widthMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        width = this.getWidth() - PADDING;

        canvas.drawRect(PADDING, PADDING, PADDING + 10, width, white);
        canvas.drawRect(PADDING, width, width, width + 10, white);

        if (xAxis != null && yAxis != null) {
            double xInc = (double) (width - PADDING) / xAxis.getNumOfTicks();
            double yInc = (double) (width - PADDING) / yAxis.getNumOfTicks();

            // Draw x ticks
            if (propDates) {
                canvas.drawRect(PADDING, width, PADDING + 10, width + 30, white);
                canvas.drawText(data.get(0).date.toString(), PADDING - 100, width + 80, font);
                canvas.drawRect(width, width - 20, width + 10, width + 30, white);
                canvas.drawText(data.get(data.size() - 1).date.toString(), width - 100, width + 80, font);
            } else {
                for (int i = 1; i < xAxis.getNumOfTicks() + 1; i++) {
                    float x = (float) (PADDING + i * xInc);
                    canvas.drawRect(x, PADDING, x + 1, width, gray);
                    canvas.drawRect(x - 4, width - 20, x + 6, width + 30, white);
                    if (i < xAxis.getNumOfTicks()) {
                        int index = (i - 1) * xAxis.getTickSkip();
                        canvas.drawText(data.get(index).date.toShortString(), x - 30, width + 100, font);
                    }
                }
            }

            // Draw y ticks
            for (int i = 1; i < yAxis.getNumOfTicks() + 1; i++) {
                float y = (float) (width - i * yInc);
                canvas.drawRect(PADDING, y, width, y + 1, gray);
                canvas.drawRect(PADDING - 20, y - 4, PADDING + 30, y + 6, white);
                canvas.drawText(String.valueOf((int) (yAxis.getStart() + i * yAxis.getTickSpacing())), 10, y, font);
            }

            double xScale = xInc / xAxis.getTickSpacing();
            double yScale = yInc / yAxis.getTickSpacing();

            Point prev = null;
            Date last = data.get(data.size() - 1).date;
            float dif = data.get(0).date.daysTo(last);
            float scale = (width - PADDING) / dif;

            for (PlotPoint p : data) {
                float pointX;
                if (propDates) {
                    pointX = width - p.date.daysTo(last) * scale;
                } else {
                    pointX = (float) (PADDING + ((double) data.indexOf(p) / xAxis.getTickSkip() - xAxis.getStart()) * xScale + xInc);
                }
                float pointY = (float) (width - (p.y - yAxis.getStart()) * yScale);

                if (prev == null) {
                    prev = new Point((int) pointX, (int) pointY);
                } else if (drawLine) {
                    canvas.drawLine(prev.x, prev.y, pointX, pointY, white);
                    prev = new Point((int) pointX, (int) pointY);
                }

                canvas.drawCircle(pointX, pointY, 10, white);
            }
        }
    }

    public void setDrawLine(boolean drawLine) {
        this.drawLine = drawLine;
    }

    public void setPropDates(boolean propDates) {
        this.propDates = propDates;
    }

    public void updatePlot() {
        Collections.sort(data);
        xAxis = new Axis(data, Axis.HORIZONTAL, false);
        yAxis = new Axis(data, Axis.VERTICAL, true);
    }

    public ArrayList<PlotPoint> getData() {
        return data;
    }
}
