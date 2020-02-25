package com.jesperbergstrom.lifttracker.model;

import java.util.ArrayList;

public class Workout implements Comparable<Workout> {

    private Date date;
    private ArrayList<Set> sets;
    private int displayIndex = -1;

    public Workout() {
        sets = new ArrayList<Set>();
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public ArrayList<Set> getSets() {
        return sets;
    }

    public void setSets(ArrayList<Set> sets) {
        this.sets = sets;
    }

    public int getDisplayIndex() { return displayIndex; }

    public void setDisplayIndex(int displayIndex) { this.displayIndex = displayIndex; }

    @Override
    public int compareTo(Workout workout) {
        if (date.isBefore(workout.getDate())) {
            return -1;
        } else if (date.toString().equals(workout.getDate().toString())) {
            return 0;
        } else if (!date.isBefore(workout.getDate())) {
            return 1;
        }
        return 0;
    }
}
