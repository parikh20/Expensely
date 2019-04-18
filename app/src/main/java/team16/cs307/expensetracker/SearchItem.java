package team16.cs307.expensetracker;

import com.google.firebase.firestore.IgnoreExtraProperties;

@IgnoreExtraProperties
public class SearchItem {


    public String name;
    public double amount;
    public String location;
    public boolean outlierMonthly;
    public boolean isOutlierWeekly;
    public int priority;
    //public String[] tags;
    public boolean repeating;
    public int time;




    public SearchItem(double amount,String location,String name,boolean outlierMonthly,boolean OutlierWeekly,int priority,boolean repeating,int time){
        this.name=name;
        this.amount=amount;
        this.location=location;
        this.outlierMonthly=outlierMonthly;
        this.isOutlierWeekly=isOutlierWeekly();
        this.priority=priority;
        this.repeating=repeating;
        //this.tags=tags;
        this.time=time;

    }

    public String getName() {
        return name;
    }
    public double getAmount() {
        return amount;
    }

    public String getLocation() {
        return location;
    }

    public boolean isOutlierMonthly() {
        return outlierMonthly;
    }

    public boolean isOutlierWeekly() {
        return isOutlierWeekly;
    }

    public int getPriority() {
        return priority;
    }



    public boolean isRepeating() {
        return repeating;
    }

    public int getTime() {
        return time;
    }
    public SearchItem(){

    }
}
