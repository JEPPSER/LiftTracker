package com.jesperbergstrom.lifttracker.model;

import java.util.ArrayList;

public class Workout {

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
}
