package com.jesperbergstrom.lifttracker.model;

public class Set {

    private int reps;
    private float weight;

    public Set(float weight, int reps) {
        this.reps = reps;
        this.weight = weight;
    }

    public int getReps() {
        return reps;
    }

    public void setReps(int reps) {
        this.reps = reps;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }
}
