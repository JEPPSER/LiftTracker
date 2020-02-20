package com.jesperbergstrom.lifttracker.view.graphs;

import com.jesperbergstrom.lifttracker.model.Date;

public class PlotPoint implements Comparable<PlotPoint> {

    public Date date;
    public double y;

    public PlotPoint(Date date, double y) {
        this.date = date;
        this.y = y;
    }

    @Override
    public int compareTo(PlotPoint plotPoint) {
        if (date.isBefore(plotPoint.date)) {
            return -1;
        } else if (date.toString().equals(plotPoint.date.toString())) {
            return 0;
        } else if (!date.isBefore(plotPoint.date)) {
            return 1;
        }
        return 0;
    }
}
