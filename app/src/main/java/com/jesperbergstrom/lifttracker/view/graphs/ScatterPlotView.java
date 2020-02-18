package com.jesperbergstrom.lifttracker.view.graphs;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class ScatterPlotView extends View {

    private Paint blue;
    private Paint red;

    public ScatterPlotView(Context context, AttributeSet attrs) {
        super(context, attrs);
        blue = new Paint();
        blue.setColor(Color.BLUE);
        red = new Paint();
        red.setColor(Color.RED);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawRect(0, 0, this.getWidth(), this.getHeight(), blue);
        canvas.drawCircle(this.getWidth() / 2f, this.getHeight() / 2f, 10, red);
    }
}
