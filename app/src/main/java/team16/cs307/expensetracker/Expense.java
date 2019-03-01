package team16.cs307.expensetracker;


//import java.time.Clock;
//import java.time.ZonedDateTime;
import java.lang.reflect.Array;
import java.util.ArrayList;
import org.threeten.bp.Instant;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.ZoneId;

/*Expense:
    An expense or purpose serves as a singular entry within a user's finances, one set of purchases or payment.
    An expense consists of:
        - name and location : identifying information for the user
        - Repeating Bit : True if this is a repeating expense, for classification
        - Date/Time : when this purchase or payment was made or is scheduled to be made
        - Amount : amount of purchase in total, in USD
        - Category : A string custom category for the user to define upon entering the expense, for classification
        - Priority : An integer labelling the priority of this payment for alerts and budgets
        - outlierMonthly : If true, the monthly budget will not factor this payment in when determining if you are over or under budget.  (To be used only on massively large one-time payments the user wishes to remain on a yearly scale
        - outlierWeekly : If true, the weekly budget will not factor this payment in when determining if the user is over budget
        Functions:
            - Get/Set for all
            - modifyCategories : allows the user to add/remove additional categories
note: For dealing with repeating purchases, we will generate 1 payment into the future, marking that as repeating as well
//         Then, upon loading the app and realizing a <time period> has gone by, we will generate the next time period's repeating purchases
//          Thus, we will always have 2 repeating purchases for each instance

 */
public class Expense {
    private String name;
    private String location;
    private boolean repeating;
    private long timeEpoch;
    private double amount;
    private ArrayList<String> tags;
    private int priority;
    private boolean outlierMonthly;
    private boolean outlierWeekly;


    //default constructor
    public Expense() {
        this.name = "";
        this.repeating = false;
        this.timeEpoch = LocalDateTime.now().atZone(ZoneId.systemDefault()).toEpochSecond();
        this.amount = 0;
        this.tags = new ArrayList<>();
        this.priority = 0;
        this.outlierMonthly = false;
        this.outlierWeekly = false;
    }
    //non-default Constructor
    /*public Expense (String name, String location, boolean repeating, LocalDateTime time, double amount, ArrayList<String> tags, int priority, boolean outlierMonthly, boolean outlierWeekly) {
        this.name = name;
        this.location = location;
        this.repeating = repeating;
        this.timeEpoch = time.atZone(ZoneId.systemDefault()).toEpochSecond();
        this. amount = amount;
        this.tags = tags;
        this.priority = priority;
        this.outlierMonthly = outlierMonthly;
        this.outlierWeekly = outlierWeekly;
    }*/
    public Expense (String name, String location, boolean repeating, long t, double amount, ArrayList<String> tags, int priority, boolean outlierMonthly, boolean outlierWeekly) {
        this.name = name;
        this.location = location;
        this.repeating = repeating;
        this.timeEpoch = t;
        this. amount = amount;
        this.tags = tags;
        this.priority = priority;
        this.outlierMonthly = outlierMonthly;
        this.outlierWeekly = outlierWeekly;
    }

    public Boolean getRepeating() {
        return repeating;
    }

    public void setRepeating(boolean repeating) {
        this.repeating = repeating;
    }

    public long getTime() {
        return timeEpoch;
    }

    public void setTime(long time) {
        this.timeEpoch = time;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public ArrayList<String> getTags() {
        return tags;
    }

    public void setTags(ArrayList<String> tags) {
        this.tags = tags;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public Boolean getOutlierMonthly() {
        return outlierMonthly;
    }

    public void setOutlierMonthly(boolean outlierMonthly) {
        this.outlierMonthly = outlierMonthly;
    }

    public Boolean getOutlierWeekly() {
        return outlierWeekly;
    }

    public void setOutlierWeekly(boolean outlierWeekly) {
        this.outlierWeekly = outlierWeekly;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    //modifyCategories : Takes in an array list of tags, adds those that are not currently in the tag list.
    public void modifyCategories(ArrayList<String> str) {
        for (String s : str) {
            if (!this.tags.contains(s)) {
                this.tags.add(s);
            }
        }
    }
    public void removeCategories(ArrayList<String> str) {
        for (String s : str) {
            if (this.tags.contains(s)) {
                this.tags.remove(s);
            }
        }
    }
}
