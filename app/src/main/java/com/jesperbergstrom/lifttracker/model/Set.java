package com.jesperbergstrom.lifttracker.model;

public class Set {

    private int reps;
    private int weight;

    public Set(int weight, int reps) {
        this.reps = reps;
        this.weight = weight;
    }

    public int getReps() {
        return reps;
    }

    public void setReps(int reps) {
        this.reps = reps;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }
}
