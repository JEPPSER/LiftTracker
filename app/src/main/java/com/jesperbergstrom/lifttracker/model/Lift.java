package com.jesperbergstrom.lifttracker.model;

import java.util.ArrayList;

public class Lift {

    private String name;
    private ArrayList<Workout> workouts;

    public Lift(String name) {
        this.name = name;
        workouts = new ArrayList<Workout>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Workout> getWorkouts() {
        return workouts;
    }

    public void setWorkouts(ArrayList<Workout> workouts) {
        this.workouts = workouts;
    }
}
