package com.jesperbergstrom.lifttracker.view.graphs;

import java.util.ArrayList;

public class Axis {

    public static final int VERTICAL = 0;
    public static final int HORIZONTAL = 1;

    private double min;
    private double max;
    private double start;
    private double tickSpacing;
    private int numOfTicks;
    private int tickSkip;
    private int orientation;

    public Axis(ArrayList<PlotPoint> data, int orientation, boolean fromZero) {
        this.orientation = orientation;

        if (data.isEmpty()) {
            return;
        }

        if (orientation == VERTICAL) {
            min = data.get(0).y;
            max = data.get(0).y;
        } else if (orientation == HORIZONTAL) {
            min = 0;
            max = 0;
        }

        // Find min and max
        for (PlotPoint p : data) {
            if (orientation == VERTICAL) {
                if (p.y < min) {
                    min = p.y;
                }
                if (p.y > max) {
                    max = p.y;
                }
            } else if (orientation == HORIZONTAL) {
                min = 0;
                max = data.size() - 1;
            }
        }

        if (fromZero) {
            min = 0;
        }

        double dif = max - min;
        double it = 1.0;

        if (orientation == VERTICAL) {
            numOfTicks = getSignificantDigit(dif);
            if (numOfTicks < 5) {
                it = 0.5;
                numOfTicks *= 2;
            }
            numOfTicks += 2;
        } else if (orientation == HORIZONTAL) {
            numOfTicks = data.size();
            tickSkip = 1;
            while (numOfTicks > 5) {
                tickSkip *= 2;
                numOfTicks = numOfTicks / 2;
            }
            numOfTicks += 1;
        }

        double log = Math.floor(Math.log10(dif));
        tickSpacing = Math.pow(10, log);
        tickSpacing *= it;
        start = Math.floor(min / tickSpacing) * tickSpacing;
    }

    public int getTickSkip() {
        return tickSkip;
    }

    public double getStart() {
        return start;
    }

    public double getMin() {
        return min;
    }

    public double getMax() {
        return max;
    }

    public int getNumOfTicks() {
        return numOfTicks;
    }

    public double getTickSpacing() {
        return tickSpacing;
    }

    public int getOrientation() {
        return orientation;
    }

    private int getSignificantDigit(double number) {
        String str = String.valueOf(number);
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) != '0' && str.charAt(i) != '.') {
                return Integer.parseInt("" + str.charAt(i));
            }
        }
        return 1;
    }
}
