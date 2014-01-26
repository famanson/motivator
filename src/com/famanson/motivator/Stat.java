package com.famanson.motivator;

import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 * Created by famanson on 26/01/14.
 */
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class Stat {
    private long points;
    private double calorie;
    private double distanceMiles;
    private double fullBmrCalorie;
    private long seconds;
    private long steps;

    public long getPoints() {
        return points;
    }

    public void setPoints(long points) {
        this.points = points;
    }

    public double getCalorie() {
        return calorie;
    }

    public void setCalorie(double calorie) {
        this.calorie = calorie;
    }

    public double getDistanceMiles() {
        return distanceMiles;
    }

    public void setDistanceMiles(double distanceMiles) {
        this.distanceMiles = distanceMiles;
    }

    public double getFullBmrCalorie() {
        return fullBmrCalorie;
    }

    public void setFullBmrCalorie(double fullBmrCalorie) {
        this.fullBmrCalorie = fullBmrCalorie;
    }

    public long getSeconds() {
        return seconds;
    }

    public void setSeconds(long seconds) {
        this.seconds = seconds;
    }

    public long getSteps() {
        return steps;
    }

    public void setSteps(long steps) {
        this.steps = steps;
    }
}
