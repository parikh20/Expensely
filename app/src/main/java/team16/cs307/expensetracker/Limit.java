package team16.cs307.expensetracker;
//Limit: A category-defined limit object consists of monthly, weekly, and yearly limit values, and an identifying category string
public class Limit {
    private String category;
    private double limitMonthly;
    private double limitWeekly;
    private double limitYearly;

    
    public Limit() {
        this.category = "";
        this.limitMonthly = 0;
        this.limitWeekly = 0;
        this.limitYearly = 0;

    }

    public Limit(String category, double limitMonthly, double limitWeekly, double limitYearly) {
        this.category = category;
        this.limitMonthly = limitMonthly;
        this.limitWeekly = limitWeekly;
        this.limitYearly = limitYearly;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getLimitMonthly() {
        return limitMonthly;
    }

    public void setLimitMonthly(double limitMonthly) {
        this.limitMonthly = limitMonthly;
    }

    public double getLimitWeekly() {
        return limitWeekly;
    }

    public void setLimitWeekly(double limitWeekly) {
        this.limitWeekly = limitWeekly;
    }

    public double getLimitYearly() {
        return limitYearly;
    }

    public void setLimitYearly(double limitYearly) {
        this.limitYearly = limitYearly;
    }
}
