package com.jesperbergstrom.lifttracker.model;

import java.util.ArrayList;

public class Workout {

    private String date;
    private ArrayList<Set> sets;

    public Workout() {
        sets = new ArrayList<Set>();
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public ArrayList<Set> getSets() {
        return sets;
    }

    public void setSets(ArrayList<Set> sets) {
        this.sets = sets;
    }
}
