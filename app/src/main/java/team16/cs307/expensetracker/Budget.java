package team16.cs307.expensetracker;

import java.util.ArrayList;

public class Budget {
    private double limitMonthly;
    private double limitYearly;
    private double limitWeekly;
    private ArrayList<Limit> customLimits;
    private String name;
    private int totalRating;
    private int numRatings;

    public Budget() {
        this.name = "";
        this.customLimits = new ArrayList<>();
        this.limitMonthly = 0;
        this.limitWeekly = 0;
        this.limitYearly = 0;
        this.totalRating = 0;
        this.numRatings = 0;
    }

    public Budget(String name, double limitWeekly, double limitMonthly,double limitYearly, ArrayList<Limit> customLimits) {
        this.name = name;
        this.limitMonthly = limitMonthly;
        this.limitYearly = limitYearly;
        this.limitWeekly = limitWeekly;
        this.customLimits = customLimits;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLimitMonthly() {
        return limitMonthly;
    }

    public void setLimitMonthly(double limitMonthly) {
        this.limitMonthly = limitMonthly;
    }

    public double getLimitYearly() {
        return limitYearly;
    }

    public void setLimitYearly(double limitYearly) {
        this.limitYearly = limitYearly;
    }

    public double getLimitWeekly() {
        return limitWeekly;
    }

    public void setLimitWeekly(double limitWeekly) {
        this.limitWeekly = limitWeekly;
    }

    public ArrayList<Limit> getCustomLimits() {
        return customLimits;
    }

    public void setCustomLimits(ArrayList<Limit> customLimits) {
        this.customLimits = customLimits;
    }

    public void addCustomLimit (Limit l) {
        this.customLimits.add(l);
    }


    public int getTotalRating() {
        return totalRating;
    }

    public void setTotalRating(int totalRating) {
        this.totalRating = totalRating;
    }

    public int getNumRatings() {
        return numRatings;
    }

    public void setNumRatings(int numRatings) {
        this.numRatings = numRatings;
    }

}
